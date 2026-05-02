package com.github.xpenatan.gdx.teavm.backends.psp.natives;

import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("PSPTextureApi.h")
public class PSPTextureApi {

    @Import(name = "load_texture")
    public static native Address load_texture(String filename, int vram);

}