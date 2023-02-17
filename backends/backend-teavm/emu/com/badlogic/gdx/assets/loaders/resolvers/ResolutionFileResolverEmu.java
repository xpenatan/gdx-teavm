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

package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(ResolutionFileResolver.class)
public class ResolutionFileResolverEmu implements FileHandleResolver {

    @Emulate(ResolutionFileResolver.Resolution.class)
    public static class ResolutionEmu {
        public final int portraitWidth;
        public final int portraitHeight;
        public final String folder;

        public ResolutionEmu(int portraitWidth, int portraitHeight, String folder) {
            this.portraitWidth = portraitWidth;
            this.portraitHeight = portraitHeight;
            this.folder = folder;
        }
    }

    public FileHandleResolver baseResolver;
    public ResolutionFileResolver.Resolution[] descriptors;

    public ResolutionFileResolverEmu(FileHandleResolver baseResolver, ResolutionFileResolver.Resolution... descriptors) {
        this.descriptors = descriptors;
        this.baseResolver = baseResolver;
    }

    @Override
    public FileHandle resolve(String fileName) {
        ResolutionFileResolver.Resolution bestDesc = choose(descriptors);
        FileHandle originalHandle = new TeaFileHandle(fileName);
        FileHandle handle = baseResolver.resolve(resolve(originalHandle, bestDesc.folder));
        if(!handle.exists()) handle = baseResolver.resolve(fileName);
        return handle;
    }

    protected String resolve(FileHandle originalHandle, String suffix) {
        return originalHandle.parent() + "/" + suffix + "/" + originalHandle.name();
    }

    static public ResolutionFileResolver.Resolution choose(ResolutionFileResolver.Resolution... descriptors) {
        int width = 0;
        if(Gdx.graphics.getWidth() > Gdx.graphics.getHeight()) {
            width = Gdx.graphics.getHeight();
        }
        else {
            width = Gdx.graphics.getWidth();
        }

        ResolutionFileResolver.Resolution bestDesc = null;
        // Find lowest.
        int best = Integer.MAX_VALUE;
        for(int i = 0, n = descriptors.length; i < n; i++) {
            if(descriptors[i].portraitWidth < best) {
                best = descriptors[i].portraitWidth;
                bestDesc = descriptors[i];
            }
        }
        // Find higher, but not over the screen res.
        best = Integer.MAX_VALUE;
        for(int i = 0, n = descriptors.length; i < n; i++) {
            if(descriptors[i].portraitWidth <= width) {
                best = descriptors[i].portraitWidth;
                bestDesc = descriptors[i];
            }
        }
        return bestDesc;
    }
}
