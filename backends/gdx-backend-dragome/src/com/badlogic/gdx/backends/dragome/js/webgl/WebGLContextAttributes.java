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

package com.badlogic.gdx.backends.dragome.js.webgl;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;

/** @author xpenatan */
public interface WebGLContextAttributes {
	
	@DelegateCode(ignore = true)
	public static WebGLContextAttributes create() {
		WebGLContextAttributes attr = null;
		attr = ScriptHelper.evalCasting("{premultipliedAlpha:false}", WebGLContextAttributes.class, null);
		return attr;
	}
	
	public void set_alpha (boolean alpha);
	public void set_depth (boolean depth);
	public void set_stencil (boolean stencil);
	public void set_antialias (boolean antialias);
	public void set_premultipliedAlpha (boolean premultipliedAlpha);
	public void set_preserveDrawingBuffer (boolean preserveDrawingBuffer);
	public void set_failIfMajorPerformanceCaveat (boolean perfCaveat);
}
