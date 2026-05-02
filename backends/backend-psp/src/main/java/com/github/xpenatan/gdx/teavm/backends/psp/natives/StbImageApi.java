package com.github.xpenatan.gdx.teavm.backends.psp.natives;

import org.teavm.interop.Address;
import org.teavm.interop.Import;

public class StbImageApi {

    public static final int STBI_default = 0; // only used for desired_channels
    public static final int STBI_grey       = 1;
    public static final int STBI_grey_alpha = 2;
    public static final int STBI_rgb        = 3;
    public static final int STBI_rgb_alpha  = 4;

    @Import(name = "stbi_set_flip_vertically_on_load")
    public static native void stbi_set_flip_vertically_on_load(int flag_true_if_should_flip);

    @Import(name = "stbi_load")
    public static native Address stbi_load(String filename, Address x, Address y, Address comp, int req_comp);

    @Import(name = "stbi_image_free")
    public static native void stbi_image_free(Address retval_from_stbi_load);

}