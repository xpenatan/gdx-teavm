/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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

package com.badlogic.gdx.controllers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.controllers.android.AndroidControllers;

public class Controllers {

    private static final String TAG = "Controllers";
    static final ObjectMap<Application, ControllerManager> managers = new ObjectMap<>();

    static public Array<Controller> getControllers() {
        initialize();
        return getManager().getControllers();
    }

    public static Controller getCurrent() {
        initialize();
        return getManager().getCurrentController();
    }

    static public void addListener(ControllerListener listener) {
        initialize();
        getManager().addListener(listener);
    }

    static public void removeListener(ControllerListener listener) {
        initialize();
        getManager().removeListener(listener);
    }

    static public void clearListeners() {
        initialize();
        getManager().clearListeners();
    }

    static public Array<ControllerListener> getListeners() {
        initialize();
        return getManager().getListeners();
    }

    static private ControllerManager getManager() {
        return managers.get(Gdx.app);
    }

    static private void initialize() {
        if(managers.containsKey(Gdx.app)) {
            return;
        }

        ControllerManager manager = AndroidControllers.getInstance();
        managers.put(Gdx.app, manager);

        final Application app = Gdx.app;
        Gdx.app.addLifecycleListener(new LifecycleListener() {
            @Override
            public void resume() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void dispose() {
                managers.remove(app);
                Gdx.app.log(TAG, "removed manager for application, " + managers.size + " managers active");
            }
        });
        Gdx.app.log(TAG, "added manager for application, " + managers.size + " managers active");
    }
}
