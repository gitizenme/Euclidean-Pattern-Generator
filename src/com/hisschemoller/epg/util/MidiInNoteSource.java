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

package com.hisschemoller.epg.util;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class MidiInNoteSource implements Receiver
{
	private ISequenceable _sequencer;
	private Transmitter _transmitter;

	public MidiInNoteSource ( ISequenceable sequencer, Transmitter transmitter )
	{
		_sequencer = sequencer;
		_transmitter = transmitter;
		_transmitter.setReceiver ( this );
	}

	public void dispose ( )
	{
		close ( );
	}

	public void close ( )
	{
		_transmitter.setReceiver ( null );
	}

	public void send ( MidiMessage message, long timeStamp )
	{
		if ( message instanceof ShortMessage )
		{
			ShortMessage shortMessage = ( ShortMessage ) message;
			switch ( shortMessage.getCommand ( ) )
			{
			case 0x80:
				_sequencer.onNoteOff ( shortMessage );
				break;

			case 0x90:
				_sequencer.onNoteOn ( shortMessage );
				break;
			}
		}
	}
}
