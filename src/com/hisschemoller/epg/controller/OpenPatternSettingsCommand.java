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

package com.hisschemoller.epg.controller;

import java.util.UUID;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.hisschemoller.epg.model.SequencerProxy;
import com.hisschemoller.epg.model.data.PatternVO;
import com.hisschemoller.epg.notification.SeqNotifications;

public class OpenPatternSettingsCommand extends SimpleCommand
{
	@Override public final void execute ( INotification notification )
	{
		UUID patternID = (UUID) notification.getBody ( );
		SequencerProxy sequencerProxy = (SequencerProxy) getFacade ( ).retrieveProxy ( SequencerProxy.NAME );
		PatternVO patternVO = sequencerProxy.getPatternByID ( patternID );
		
		sendNotification ( SeqNotifications.PATTERN_SETTINGS_OPENED, patternVO );
		sendNotification ( SeqNotifications.SELECT_PATTERN, patternVO );
	}
}
