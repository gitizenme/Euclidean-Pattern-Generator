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

package com.hisschemoller.epg.controller.preferences;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.hisschemoller.epg.model.MidiProxy;
import com.hisschemoller.epg.model.PreferencesProxy;
import com.hisschemoller.epg.model.data.EPGEnums.ClockSourceType;
import com.hisschemoller.epg.notification.SeqNotifications;
import com.hisschemoller.epg.util.EPGPreferences;

public class SyncToMidiClockEnabledCommand extends SimpleCommand
{
	/**
	 * Sync to external MIDI clock checkbox in preferences changed.
	 */
	@Override public final void execute ( final INotification notification )
	{
		boolean isEnabled = ( Boolean ) notification.getBody ( );

		MidiProxy midiProxy = ( MidiProxy ) getFacade ( ).retrieveProxy ( MidiProxy.NAME );
		PreferencesProxy preferencesProxy = ( PreferencesProxy ) getFacade ( ).retrieveProxy ( PreferencesProxy.NAME );
		preferencesProxy.setIsSyncToMidiInClockEnabled ( isEnabled );

		EPGPreferences.putBoolean ( EPGPreferences.SYNC_TO_MIDI_IN_CLOCK, isEnabled );

		ClockSourceType type = ( isEnabled && midiProxy.getMidiInEnabled ( ) ) ? ClockSourceType.MIDI_CLOCK_IN : ClockSourceType.INTERNAL;
		sendNotification ( SeqNotifications.SYNC_TO_MIDI_IN_ENABLED_UPDATED, isEnabled );
		sendNotification ( SeqNotifications.UPDATE_CLOCK_SOURCE, type );
	}
}
