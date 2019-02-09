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

package com.hisschemoller.epg.model.data;

import java.util.UUID;

public class SettingsVO
{
	public UUID patternID;

	public int quantization;

	/** Pattern settings */
	public int steps;
	public int fills;
	public int rotation;

	/** MIDI Out settings */
	public int midiOutChannel;
	public int midiOutPitch;
	public int midiOutVelocity;
	public int noteLength;

	/** MIDI In settings */
	public boolean midiInTriggerEnabled;
	public int midiInChannel;
	public int midiInPitch;

	/** OSC settings */
	public String oscOutAddress;
	
	/** Other settings */
	public boolean mute;
	public boolean solo;
	public String name;
}