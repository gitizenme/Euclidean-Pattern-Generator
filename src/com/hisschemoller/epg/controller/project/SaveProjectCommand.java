/**
 * Copyright 2011 Wouter Hisschemoller
 * 
 * This file is part of Euclidean Pattern Generator.
 * 
 * Euclidean Pattern Generator is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * Euclidean Pattern Generator is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Euclidean Pattern Generator.  If not, 
 * see <http://www.gnu.org/licenses/>.
 */

package com.hisschemoller.epg.controller.project;

import java.io.File;
import java.io.StringWriter;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hisschemoller.epg.model.FileProxy;
import com.hisschemoller.epg.model.SequencerProxy;
import com.hisschemoller.epg.model.WindowProxy;
import com.hisschemoller.epg.model.data.PatternVO;
import com.hisschemoller.epg.notification.SeqNotifications;

public class SaveProjectCommand extends SimpleCommand
{
	/**
	 * Save a project as an XML file.
	 */
	@Override public final void execute ( final INotification notification )
	{
		Document document = createXML ( );
		printXML ( document );
		saveFile ( document, notification.getName ( ) );
	}

	private Document createXML ( )
	{
		try
		{
			/** Create an empty XML document. */
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder ( );
			Document document = docBuilder.newDocument ( );

			SequencerProxy sequencerProxy = ( SequencerProxy ) getFacade ( ).retrieveProxy ( SequencerProxy.NAME );

			/** Create the root element and add it to the document. */
			Element projectNode = document.createElement ( "project" );
			projectNode.setAttribute ( "tempo", Float.toString ( sequencerProxy.getBPM ( ) ) );
			document.appendChild ( projectNode );

			Element patternsNode = document.createElement ( "patterns" );
			projectNode.appendChild ( patternsNode );
			Vector<PatternVO> patterns = sequencerProxy.getPatterns ( );

			for ( int j = 0; j < patterns.size ( ); j++ )
			{
				/** Add a pattern. */
				PatternVO patternVO = patterns.get ( j );
				Element patternNode = document.createElement ( "pattern" );
				patternNode.setAttribute ( "id", patternVO.id.toString ( ) );
				patternsNode.appendChild ( patternNode );

				/** Add events element. */
				Element events = document.createElement ( "events" );
				events.setAttribute ( "notes", Integer.toString ( patternVO.fills ) );
				events.setAttribute ( "steps", Integer.toString ( patternVO.steps ) );
				events.setAttribute ( "rotation", Integer.toString ( patternVO.rotation ) );
				patternNode.appendChild ( events );

                /** Add osc element. */
                Element osc = document.createElement ( "osc_out" );
                osc.setAttribute ( "address", patternVO.oscOutAddress );
                patternNode.appendChild ( osc );

				/** Add midi out element. */
				Element midiOut = document.createElement ( "midi_out" );
				midiOut.setAttribute ( "channel", Integer.toString ( patternVO.midiOutChannel ) );
				midiOut.setAttribute ( "pitch", Integer.toString ( patternVO.midiOutPitch ) );
				midiOut.setAttribute ( "velocity", Integer.toString ( patternVO.midiOutVelocity ) );
				patternNode.appendChild ( midiOut );

				/** Add midi in element. */
				Element midiIn = document.createElement ( "midi_in" );
				midiIn.setAttribute ( "trigger_enabled", Boolean.toString ( patternVO.triggerMidiInEnabled ) );
				midiIn.setAttribute ( "channel", Integer.toString ( patternVO.triggerMidiInChannel ) );
				midiIn.setAttribute ( "pitch", Integer.toString ( patternVO.triggerMidiInPitch ) );
				patternNode.appendChild ( midiIn );

				/** Add settings element. */
				Element settings = document.createElement ( "settings" );
				settings.setAttribute ( "notelength", Integer.toString ( patternVO.noteLength ) );
				settings.setAttribute ( "quantization", Integer.toString ( patternVO.quantization ) );
				settings.setAttribute ( "solo", Boolean.toString ( patternVO.solo ) );
				settings.setAttribute ( "mute", Boolean.toString ( patternVO.mute ) );
				patternNode.appendChild ( settings );

				/** Add location element. */
				Element location = document.createElement ( "location" );
				location.setAttribute ( "x", Integer.toString ( patternVO.viewX ) );
				location.setAttribute ( "y", Integer.toString ( patternVO.viewY ) );
				patternNode.appendChild ( location );
				
				/** Add name element. */
				Element name = document.createElement ( "name" );
				CDATASection cdataName = document.createCDATASection ( patternVO.name );
				name.appendChild ( cdataName );
				patternNode.appendChild ( name );
			}

			return document;
		}
		catch ( ParserConfigurationException exception )
		{
			showMessage ( "SaveProjectCommand.execute() ParserConfigurationException: " + exception.getMessage ( ) );
		}

		return null;
	}

	private void printXML ( Document document )
	{
		try
		{
			/** Set up a transformer. */
			TransformerFactory transFactory = TransformerFactory.newInstance ( );
			Transformer transformer = transFactory.newTransformer ( );
			transformer.setOutputProperty ( OutputKeys.OMIT_XML_DECLARATION, "yes" );
			transformer.setOutputProperty ( OutputKeys.INDENT, "yes" );

			/** Create string from XML tree. */
			StringWriter stringWriter = new StringWriter ( );
			StreamResult result = new StreamResult ( stringWriter );
			DOMSource source = new DOMSource ( document );
			transformer.transform ( source, result );
			String xmlString = stringWriter.toString ( );

			/** Print XML. */
			System.out.println ( "Here's the xml:\n\n" + xmlString );
		}
		catch ( Exception exception )
		{
			showMessage ( "SaveProjectCommand.printXML() Exception: " + exception.getMessage ( ) );
		}
	}

	private void saveFile ( Document document, String saveType )
	{
		FileProxy fileProxy = ( FileProxy ) getFacade ( ).retrieveProxy ( FileProxy.NAME );

		if ( saveType == SeqNotifications.SAVE_PROJECT_AS || ( saveType == SeqNotifications.SAVE_PROJECT && fileProxy.getFile ( ) == null ) )
		{
			WindowProxy windowProxy = ( WindowProxy ) getFacade ( ).retrieveProxy ( WindowProxy.NAME );
			JFileChooser fileChooser = fileProxy.getFileChooser ( );
			fileChooser.setDialogTitle ( "Save Project File" );
			fileChooser.setSelectedFile ( new File ( "EPG-Project.xml" ) );
			int returnVal = fileChooser.showSaveDialog ( windowProxy.getMainFrame ( ) );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				fileProxy.setFile ( fileChooser.getSelectedFile ( ) );
			}
			else
			{
				return;
			}
		}

		System.out.println ( "SaveProjectCommand.saveFile() file.getAbsolutePath: " + fileProxy.getFile ( ).getAbsolutePath ( ) );

		// Prepare the DOM document for writing.
		Source source = new DOMSource ( document );

		// Prepare the output file.
		Result result = new StreamResult ( fileProxy.getFile ( ) );

		try
		{
			/** Write the DOM document to the file. */
			Transformer transformer = TransformerFactory.newInstance ( ).newTransformer ( );
			transformer.setOutputProperty ( OutputKeys.INDENT, "yes" );
			transformer.transform ( source, result );
		}
		catch ( TransformerConfigurationException exception )
		{
			System.out.println ( "SaveProjectCommand.saveFile() TransformerConfigurationException: " + exception.getMessage ( ) );
		}
		catch ( TransformerException exception )
		{
			System.out.println ( "SaveProjectCommand.saveFile() TransformerException: " + exception.getMessage ( ) );
		}
	}

	private void showMessage ( String message )
	{
		System.out.println ( message );
	}
}
