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

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.hisschemoller.epg.model.FileProxy;
import com.hisschemoller.epg.model.SequencerProxy;
import com.hisschemoller.epg.notification.SeqNotifications;

public class NewProjectCommand extends SimpleCommand
{
	/**
	 * Start new project.
	 */
	@Override public final void execute ( final INotification notification )
	{
		/** Clear all old data from the sequencer. */
		SequencerProxy sequencerProxy = ( SequencerProxy ) getFacade ( ).retrieveProxy ( SequencerProxy.NAME );
		sequencerProxy.clear ( );
		
		/** Clear file reference. */
		FileProxy fileProxy = ( FileProxy ) getFacade ( ).retrieveProxy ( FileProxy.NAME );
		fileProxy.setFile ( null );
		
		/** Reset tempo and resolution. */
		sendNotification ( SeqNotifications.UPDATE_TEMPO, 120 );
		
		/** No pattern selected. */
		sendNotification ( SeqNotifications.SELECT_PATTERN, null );
	}
}
