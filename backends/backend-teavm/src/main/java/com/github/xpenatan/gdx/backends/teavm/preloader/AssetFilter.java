package com.github.xpenatan.gdx.backends.teavm.preloader;

/**
 * @author xpenatan
 */
public interface AssetFilter {
    /**
     * @param file        the file to filter
     * @param isDirectory whether the file is a directory
     * @return whether to include the file in the war/ folder or not.
     */
    public boolean accept(String file, boolean isDirectory);

    /**
     * @param file the file to get the bundle name for
     * @return the name of the bundle to which this file should be added
     */
    public String getBundleName(String file);

    /**
     * @param file the file to get the type for
     * @return the type of the file, one of {@link AssetType}
     */
    public static AssetType getType(String file) {
        String extension = extension(file).toLowerCase();
        if(isImage(extension)) return AssetType.Image;
        if(isAudio(extension)) return AssetType.Audio;
        if(isText(extension)) return AssetType.Text;
        return AssetType.Binary;
    }

    private static String extension(String file) {
        String name = file;
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    private static boolean isImage(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("bmp") || extension.equals("gif");
    }

    private static boolean isText(String extension) {
        return extension.equals("json") || extension.equals("xml") || extension.equals("txt") || extension.equals("glsl")
                || extension.equals("fnt") || extension.equals("pack") || extension.equals("obj") || extension.equals("atlas")
                || extension.equals("g3dj");
    }

    private static boolean isAudio(String extension) {
        return extension.equals("mp3") || extension.equals("ogg") || extension.equals("wav");
    }

}
