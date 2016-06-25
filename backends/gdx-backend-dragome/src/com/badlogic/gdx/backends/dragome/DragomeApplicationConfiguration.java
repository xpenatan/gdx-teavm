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

package com.badlogic.gdx.backends.dragome;

/** @author xpenatan */
public class DragomeApplicationConfiguration {
	/** whether to use a stencil buffer **/
	public boolean stencil = false;
	/** whether to enable antialiasing **/
	public boolean antialiasing = false;
	/** whether to include an alpha channel in the color buffer to combine the color buffer with the rest of the webpage
	 * effectively allows transparent backgrounds in Dragome, at a performance cost. **/
	public boolean alpha = false;
	/** whether to use premultipliedalpha, may have performance impact  **/
	public boolean premultipliedAlpha = false;
	/** preserve the back buffer, needed if you fetch a screenshot via canvas#toDataUrl, may have performance impact **/
	public boolean preserveDrawingBuffer = false;
	/** whether to use debugging mode for OpenGL calls. Errors will result in a RuntimeException being thrown. */
	public boolean useDebugGL = false;
	/** whether SoundManager2 should prefer to use flash instead of html5 audio (it should fall back if not available) */
	public boolean preferFlash = false;
}
