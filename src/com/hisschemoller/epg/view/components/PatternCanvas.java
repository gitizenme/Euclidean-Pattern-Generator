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

package com.hisschemoller.epg.view.components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Timer;

import org.swixml.XScrollPane;

import com.hisschemoller.epg.model.data.EPGEnums;
import com.hisschemoller.epg.model.data.PatternVO;
import com.hisschemoller.epg.notification.note.PatternPositionNote;
import com.hisschemoller.epg.notification.note.PatternSequenceNote;
import com.hisschemoller.epg.util.EPGSwingEngine;
import com.hisschemoller.epg.view.events.IViewEventListener;
import com.hisschemoller.epg.view.events.ViewEvent;

public class PatternCanvas implements MouseListener, MouseMotionListener, ActionListener
{
	public static final long serialVersionUID = -1L;
	private Vector < IViewEventListener > _viewEventListeners = new Vector < IViewEventListener > ( );
	private Vector < Pattern > _patterns = new Vector < Pattern > ( );
	private Pattern _patternUnderMouse;
	private PatternVO _patternChangesVO;
	private JPanel _panel;
	private JViewport _viewport;
	private Timer _timer;
	private Point _dragOffset = new Point ( 0, 0 );
	private Point _dragScrollOffset = new Point ( 0, 0 );

	public PatternCanvas ( )
	{
		this ( null );
	}

	public PatternCanvas ( EPGSwingEngine swingEngine )
	{
		_panel = ( JPanel ) swingEngine.find ( "CANVAS_PANEL" );
		_panel.setLayout ( null );
		_panel.addMouseListener ( this );
		_panel.addMouseMotionListener ( this );

		_viewport = ( ( XScrollPane ) swingEngine.find ( "CANVAS_SCROLLPANE" ) ).getViewport ( );

		_timer = new Timer ( 35, this );
		_timer.start ( );
	}

	public void addPattern ( PatternVO patternVO, Boolean isAnimated )
	{
		Pattern pattern = new Pattern ( patternVO, isAnimated );
		_panel.add ( pattern );
		_patterns.add ( pattern );
		_panel.repaint ( pattern.getBounds ( ) );
		validatePanel ( );
	}

	public void updatePattern ( PatternVO patternVO, Pattern.Operation operation )
	{
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			if ( pattern.getID ( ) == patternVO.id )
			{
				pattern.updatePattern ( patternVO, operation );
				if ( operation == Pattern.Operation.LOCATION )
				{
					validatePanel ( );
				}
				break;
			}
		}
	}

	public void deletePattern ( PatternVO patternVO )
	{
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			if ( pattern.getID ( ) == patternVO.id )
			{
				_panel.remove ( pattern );
				pattern.dispose ( );
				_patterns.remove ( n );
				_panel.repaint ( pattern.getBounds ( ) );
				validatePanel ( );
				break;
			}
		}
	}

	public void selectPattern ( PatternVO patternVO )
	{
		if ( patternVO != null )
		{
			int n = _patterns.size ( );
			while ( --n > -1 )
			{
				Pattern pattern = _patterns.get ( n );
				pattern.select ( pattern.getID ( ) == patternVO.id );
			}
		}
	}

	/**
	 * Update pointer position on all patterns.
	 */
	public void updatePatternPositions ( PatternPositionNote [ ] positionNotes )
	{
		int m = positionNotes.length;
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			while ( --m > -1 )
			{
				if ( pattern.getID ( ) == positionNotes[ m ].patternID )
				{
					pattern.updatePosition ( positionNotes[ m ].position );
					break;
				}
			}
		}
	}

	/**
	 * Show the MIDI note that is played.
	 */
	public void updatePatternSequence ( PatternSequenceNote note )
	{
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			if ( pattern.getID ( ) == note.patternID )
			{
				pattern.updateSequence ( note );
			}
		}
	}

	public void updatePlayback ( EPGEnums.Playback playback )
	{
		if ( playback == EPGEnums.Playback.STOP )
		{
			// updateTempo ( 0 );
		}
	}

	public Point getMouseClickPosition ( )
	{
		return new Point ( _panel.getMousePosition ( ).x - Pattern.PANEL_SIZE / 2, _panel.getMousePosition ( ).y - Pattern.PANEL_SIZE / 2 );
	}

	public PatternVO getPatternChangesVO ( )
	{
		return _patternChangesVO;
	}

	public UUID getPatternUnderMouseID ( )
	{
		return _patternUnderMouse.getID ( );
	}

	public synchronized void addViewEventListener ( IViewEventListener listener )
	{
		_viewEventListeners.addElement ( listener );
	}

	public synchronized void removeViewEventListener ( IViewEventListener listener )
	{
		_viewEventListeners.removeElement ( listener );
	}

	/**
	 * Called by _timer to update all patterns.
	 */
	public void actionPerformed ( ActionEvent event )
	{
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			_patterns.get ( n ).updateDraw ( );
			_panel.repaint ( _patterns.get ( n ).getBounds ( ) );
		}
	}

	public void mouseReleased ( MouseEvent event )
	{
		if ( _patternUnderMouse != null )
		{
			_patternChangesVO = new PatternVO ( );
			_patternChangesVO.id = _patternUnderMouse.getID ( );
			_patternChangesVO.viewX = _patternUnderMouse.getX ( );
			_patternChangesVO.viewY = _patternUnderMouse.getY ( );

			dispatchViewEvent ( ViewEvent.PATTERN_LOCATION_CHANGE );
		}
	}

	public void mouseEntered ( MouseEvent event )
	{
	}

	public void mouseExited ( MouseEvent event )
	{
		_patternUnderMouse = null;
	}

	public void mousePressed ( MouseEvent event )
	{
		/** Check if a pattern is clicked. */
		_patternUnderMouse = null;
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			int w = event.getX ( ) - pattern.getX ( ) - Pattern.PANEL_SIZE / 2;
			int h = event.getY ( ) - pattern.getY ( ) - Pattern.PANEL_SIZE / 2;
			double distance = Math.sqrt ( w * w + h * h );

			if ( distance < 20 )
			{
				_dragOffset.x = w;
				_dragOffset.y = h;
				_patternUnderMouse = pattern;
				dispatchViewEvent ( ViewEvent.PATTERN_CENTER_PRESS );
				break;
			}
		}

		/** Mouse press on background, not on any pattern center. */
		if ( _patternUnderMouse == null )
		{
			_dragScrollOffset = event.getLocationOnScreen ( );
		}
	}

	public void mouseClicked ( MouseEvent event )
	{
		if ( _panel.contains ( event.getPoint ( ) ) && event.getClickCount ( ) == 2 )
		{
			if ( _patternUnderMouse == null )
			{
				dispatchViewEvent ( ViewEvent.PANEL_CLICK );
			}
		}
	}

	public void mouseDragged ( MouseEvent event )
	{
		if ( _patternUnderMouse != null )
		{
			_patternUnderMouse.setLocation ( ( event.getX ( ) - Pattern.PANEL_SIZE / 2 ) - _dragOffset.x, ( event.getY ( ) - Pattern.PANEL_SIZE / 2 ) - _dragOffset.y );
		}
		else
		{
			/** Scroll panel by dragging the background. */
			Point distance = new Point ( _dragScrollOffset.x - event.getLocationOnScreen ( ).x, _dragScrollOffset.y - event.getLocationOnScreen ( ).y );
			Point newPosition = _viewport.getViewPosition ( );
			newPosition.translate ( distance.x, distance.y );
			_dragScrollOffset = event.getLocationOnScreen ( );
			_panel.scrollRectToVisible ( new Rectangle ( newPosition, _viewport.getSize ( ) ) );
		}
	}

	public void mouseMoved ( MouseEvent event )
	{
	}

	protected void dispatchViewEvent ( int id )
	{
		ViewEvent viewEvent = new ViewEvent ( this, id );
		for ( int i = 0; i < _viewEventListeners.size ( ); i++ )
		{
			( ( IViewEventListener ) _viewEventListeners.elementAt ( i ) ).viewEventHandler ( viewEvent );
		}
	}

	private void validatePanel ( )
	{
		Dimension actualSize = new Dimension ( 100, 100 );
		int n = _patterns.size ( );
		while ( --n > -1 )
		{
			Pattern pattern = _patterns.get ( n );
			actualSize.width = Math.max ( actualSize.width, pattern.getX ( ) + pattern.getWidth ( ) );
			actualSize.height = Math.max ( actualSize.height, pattern.getY ( ) + pattern.getHeight ( ) );
		}
		_panel.setPreferredSize ( actualSize );
		_panel.revalidate ( );
	}
}
