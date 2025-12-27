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
package emu.com.badlogic.gdx.controllers.teavm;

import com.badlogic.gdx.controllers.ControllerMapping;

public class WebMapping extends ControllerMapping {
    private static WebMapping instance;

    WebMapping() {
        super(0, 1, 2, 3,
                0, 1, 2, 3, 8, 9,
                4, 6, 5, 7, 10, 11,
                12, 13, 14, 15);
    }

    static WebMapping getInstance() {
        if (instance == null)
            instance = new WebMapping();

        return instance;
    }
}
