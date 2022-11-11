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

package com.github.xpenatan.gdx.backends.dragome;

import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;

/**
 * @author xpenatan
 */
public class DragomeWindow {

    public static int getInnerWidth() {
        return ScriptHelper.evalInt("window.innerWidth", null);
    }

    public static int getInnerHeight() {
        return ScriptHelper.evalInt("window.innerHeight", null);
    }

    public static void onResize(Runnable run) {
        ScriptHelper.put("run", run, null);
        ScriptHelper.evalNoResult("window.addEventListener('resize', function(){run.$run$void();})", null);
    }

    public static void requestAnimationFrame(Runnable run, Element element) {
        ScriptHelper.put("run", run, null);
        ScriptHelper.put("element", element, null);
        ScriptHelper.evalNoResult("requestAnimationFrame(function(time) {run.$run$void();}, element)", null);
    }

    public static void requestAnimationFrame(Runnable run) {
        ScriptHelper.put("run", run, null);
        ScriptHelper.evalNoResult("requestAnimationFrame(function() {run.$run$void();})", null);
    }
}
