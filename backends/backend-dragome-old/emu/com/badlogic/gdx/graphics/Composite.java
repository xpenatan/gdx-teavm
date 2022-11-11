package com.badlogic.gdx.graphics;

public class Composite {

    public static String COPY = "copy";

    /**
     * B atop A. Same as source-atop but using the destination image instead of
     * the source image and vice versa.
     */
    public static String DESTINATION_ATOP = "destination-atop";

    /**
     * B in A. Same as source-in but using the destination image instead of the
     * source image and vice versa.
     */
    public static String DESTINATION_IN = "destination-in";

    /**
     * B out A. Same as source-out but using the destination image instead of the
     * source image and vice versa.
     */
    public static String DESTINATION_OUT = "destination-out";

    /**
     * B over A. Same as source-over but using the destination image instead of
     * the source image and vice versa.
     */
    public static String DESTINATION_OVER = "destination-over";

    /**
     * A plus B. Display the sum of the source image and destination image, with
     * color values approaching 1 as a limit.
     */
    public static String LIGHTER = "lighter";

    /**
     * A atop B. Display the source image wherever both images are opaque. Display
     * the destination image wherever the destination image is opaque but the
     * source image is transparent. Display transparency elsewhere.
     */
    public static String SOURCE_ATOP = "source-atop";

    /**
     * A in B. Display the source image wherever both the source image and
     * destination image are opaque. Display transparency elsewhere.
     */
    public static String SOURCE_IN = "source-in";

    /**
     * A out B. Display the source image wherever the source image is opaque and
     * the destination image is transparent. Display transparency elsewhere.
     */
    public static String SOURCE_OUT = "source-out";

    /**
     * A over B. Display the source image wherever the source image is opaque.
     * Display the destination image elsewhere.
     */
    public static String SOURCE_OVER = "source-over";

    /**
     * A xor B. Exclusive OR of the source image and destination image.
     */
    public static String XOR = "xor";
}
