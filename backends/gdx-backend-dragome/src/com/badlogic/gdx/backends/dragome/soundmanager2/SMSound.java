/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome.soundmanager2;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.compiler.annotations.MethodAlias;

/** Ported from GWT backend
 * @author xpenatan */
public interface SMSound {
	
	public interface SMSoundCallback {
		@MethodAlias(local_alias= "onfinish")
		public void onfinish ();
	}
	
	/** Constants for play state. */
	public static final int STOPPED = 0;
	public static final int PLAYING = 1;

	/** Stops, unloads and destroys a sound, freeing resources etc. */
	public void destruct (); 

	/** The current location of the "play head" within the sound, specified in milliseconds (1 sec = 1000 msec).
	 * @return The current playing position of the sound. */
	public int getPosition (); 

	/** Seeks to a given position within a sound, specified by miliseconds (1000 msec = 1 second.) Affects position property.
	 * @param position the position to seek to. */
	@DelegateCode(eval = "this.node.setPosition($1)")
	public void setPosition (int position);

	/** Pauses the given sound. (Does not toggle.) Affects paused property (boolean.) */
	public void pause ();

	/** Starts playing the given sound. */
	@DelegateCode(eval = "this.node.play({ "
		+ "volume: $1.$$$volume,"
		+ "pan: $1.$$$pan,"
		+ "loops: $1.$$$loops,"
		+ "from: $1.$$$from,"
		+ "onfinish: function() { var callback = $1.$$$callback; if(callback != null){callback.onfinish();} }"
		+ "})")
	public void play (SMSoundOptions options);

	/** Starts playing the given sound. */
	public void play ();

	/** Resumes the currently-paused sound. Does not affect currently-playing sounds. */
	public void resume ();

	/** Stops playing the given sound. */
	public void stop ();

	/** Sets the volume of the given sound. Affects volume property. 
	 * @param volume the volume, accepted values: 0-100.*/
	@DelegateCode(eval = "this.node.setVolume($1)")
	public void setVolume (int volume);

	/** Gets the volume of the give sound.
	 * @return the volume as a value between 0-100. */
	public int getVolume ();

	/** Sets the stereo pan (left/right bias) of the given sound. Affects pan property.
	 * @param pan the panning amount, accepted values: -100 to 100 (L/R, 0 = center.) */
	@DelegateCode(eval = "this.node.setPan($1)")
	public void setPan (int pan);

	/** Gets the pan of the give sound.
	 * @return the pan as a value between -100-100. (L/R, 0 = center.)*/
	public int getPan ();

	/** Numeric value indicating the current playing state of the sound.
	 * 0 = stopped/uninitialised.
	 * 1 = playing or buffering sound (play has been called, waiting for data etc.).
	 * Note that a 1 may not always guarantee that sound is being heard, given buffering and autoPlay status.
	 * @return the current playing state. */
	public int getPlayState ();

	/** Boolean indicating pause status. True/False. */
	public boolean getPaused ();

	/** Number of times to loop the sound. */
	public int getLoops ();
}