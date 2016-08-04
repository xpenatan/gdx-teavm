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

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;

/** Ported from GWT backend
 * @author xpenatan */
public class SMSound {

	public interface SMSoundCallback {
		@MethodAlias(local_alias= "onfinish")
		public void onfinish ();
	}

	/** Constants for play state. */
	public static final int STOPPED = 0;
	public static final int PLAYING = 1;

	/** Stops, unloads and destroys a sound, freeing resources etc. */
	public void destruct () {
		ScriptHelper.evalNoResult("this.node.destruct()", this);
	}

	/** The current location of the "play head" within the sound, specified in milliseconds (1 sec = 1000 msec).
	 * @return The current playing position of the sound. */
	public int getPosition () {
		return ScriptHelper.evalInt("this.node.position", this);
	}

	/** Seeks to a given position within a sound, specified by miliseconds (1000 msec = 1 second.) Affects position property.
	 * @param position the position to seek to. */
	public void setPosition (int position) {
		ScriptHelper.put("$1", position, this);
		ScriptHelper.evalNoResult("this.node.setPosition($1)", this);
	}

	/** Pauses the given sound. (Does not toggle.) Affects paused property (boolean.) */
	public void pause () {
		ScriptHelper.evalNoResult("this.node.pause()", this);
	}

	/** Starts playing the given sound. */
	public void play (SMSoundOptions options) {
		
		ScriptHelper.put("callback", options.callback, this);
		ScriptHelper.put("volume", options.volume, this);
		ScriptHelper.put("pan", options.pan, this);
		ScriptHelper.put("loops", options.loops, this);
		ScriptHelper.put("from", options.from, this);
		ScriptHelper.put("volume", options.volume, this);
		ScriptHelper.put("volume", options.volume, this);
		
		Object eval = ScriptHelper.eval("{ "
		+ "volume: volume,"
		+ "pan: pan,"
		+ "loops: loops,"
		+ "from: from,"
		+ "onfinish: function() { if(callback != null){callback.onfinish();} }"
		+ "}", this);
		ScriptHelper.put("$1", eval, this);
		ScriptHelper.evalNoResult("this.node.play($1)", this);
	}

	/** Starts playing the given sound. */
	public void play () {
		ScriptHelper.evalNoResult("this.node.play()", this);
	}

	/** Resumes the currently-paused sound. Does not affect currently-playing sounds. */
	public void resume () {
		ScriptHelper.evalNoResult("this.node.resume()", this);
	}

	/** Stops playing the given sound. */
	public void stop () {
		ScriptHelper.evalNoResult("this.node.stop()", this);
	}

	/** Sets the volume of the given sound. Affects volume property. 
	 * @param volume the volume, accepted values: 0-100.*/
	public void setVolume (int volume) {
		ScriptHelper.put("$1", volume, this);
		ScriptHelper.evalNoResult("this.node.setVolume($1)", this);
	}

	/** Gets the volume of the give sound.
	 * @return the volume as a value between 0-100. */
	public int getVolume () {
		return ScriptHelper.evalInt("this.node.volume", this);
	}

	/** Sets the stereo pan (left/right bias) of the given sound. Affects pan property.
	 * @param pan the panning amount, accepted values: -100 to 100 (L/R, 0 = center.) */
	public void setPan (int pan) {
		ScriptHelper.put("$1", pan, this);
		ScriptHelper.evalNoResult("this.node.setPan($1)", this);
	}

	/** Gets the pan of the give sound.
	 * @return the pan as a value between -100-100. (L/R, 0 = center.)*/
	public int getPan () {
		return ScriptHelper.evalInt("this.node.pan", this);
	}

	/** Numeric value indicating the current playing state of the sound.
	 * 0 = stopped/uninitialised.
	 * 1 = playing or buffering sound (play has been called, waiting for data etc.).
	 * Note that a 1 may not always guarantee that sound is being heard, given buffering and autoPlay status.
	 * @return the current playing state. */
	public int getPlayState () {
		return ScriptHelper.evalInt("this.node.playState", this);
	}

	/** Boolean indicating pause status. True/False. */
	public boolean getPaused () {
		return ScriptHelper.evalBoolean("this.node.paused", this);
	}

	/** Number of times to loop the sound. */
	public int getLoops () {
		return ScriptHelper.evalInt("this.node.loops", this);
	}
}