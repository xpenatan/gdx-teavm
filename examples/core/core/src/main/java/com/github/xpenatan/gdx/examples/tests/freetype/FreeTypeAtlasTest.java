package com.github.xpenatan.gdx.examples.tests.freetype;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.EnumMap;

public class FreeTypeAtlasTest implements ApplicationListener {

    // Define font sizes here...
    static enum FontSize {
        Tiny(10), Small(12), Medium(16), Large(20), Huge(24), ReallyHuge(28), JustTooBig(64);

        public final int size;

        FontSize(int size) {
            this.size = size;
        }
    }

    // Define font styles here...
    static enum FontStyle {
        Regular("data/arial.ttf"), Italic("data/arial-italic.ttf");

        public final String path;

        FontStyle(String path) {
            this.path = path;
        }
    }

    OrthographicCamera camera;
    SpriteBatch batch;
    String text;
    PixmapPacker packer;
    FontMap<BitmapFont> fontMap;

    public static final int FONT_ATLAS_WIDTH = 1024;
    public static final int FONT_ATLAS_HEIGHT = 512;

    // whether to use integer coords for BitmapFont...
    private static final boolean INTEGER = false;

    // Our demo doesn't need any fancy characters.
    // Note: the set in FreeTypeFontGenerator.DEFAULT_CHARS is more extensive
    // Also note that this string must be contained of unique characters; no duplicates!
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz\n1234567890"
            + "\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*";

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();

        long start = System.currentTimeMillis();
        int glyphCount = createFonts();
        long time = System.currentTimeMillis() - start;
        text = glyphCount + " glyphs packed in " + packer.getPages().size + " page(s) in " + time + " ms";
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float x = 10;
        float y = Gdx.graphics.getHeight() - 10;

        int renderCalls = 0;

        // NOTE: Before production release on mobile, you should cache the array from values()
        // inside the Enum in order to reduce allocations in the render loop.
        for(FontStyle style : FontStyle.values()) {
            for(FontSize size : FontSize.values()) {
                BitmapFont fnt = getFont(style, size);

                fnt.draw(batch, style.name() + " " + size.size + "pt: The quick brown fox jumps over the lazy dog", x, y);
                y -= fnt.getLineHeight() + 10;
            }
            y -= 20;
        }

        BitmapFont font = getFont(FontStyle.Regular, FontSize.Medium);
        font.draw(batch, text, 10, font.getCapHeight() + 10);

        // draw all glyphs in background
        batch.setColor(1f, 1f, 1f, 0.15f);
        batch.draw(packer.getPages().first().getTexture(), 0, 0);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        packer.dispose();
        batch.dispose();
    }

    // Utility method to grab a font by style/size pair
    public BitmapFont getFont(FontStyle style, FontSize size) {
        return fontMap.get(style).get(size);
    }

    protected int createFonts() {
        // This test uses a less efficient but more convenient way to pack multiple generated fonts into a single
        // texture atlas.
        //
        // 1. Create a new PixmapPacker big enough to fit all your desired glyphs
        // 2. Create a new FreeTypeFontGenerator for each TTF file (i.e. font styles/families)
        // 3. For each size and style, call generator.generateFont() with the packer set on the parameter
        // 4. Generate the texture atlas using packer.generateTextureAtlas or packer.updateTextureAtlas.
        // 5. Dispose of the atlas upon application exit or when you are done using the fonts
        // //////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the pixmap packer
        packer = new PixmapPacker(FONT_ATLAS_WIDTH, FONT_ATLAS_HEIGHT, Format.RGBA8888, 2, false);

        fontMap = new FontMap<BitmapFont>();
        int fontCount = 0;

        // for each style...
        for(FontStyle style : FontStyle.values()) {
            // get the file for this style
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(style.path));

            // For each size...
            for(FontSize size : FontSize.values()) {
                // pack the glyphs into the atlas using the default chars
                FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                fontParameter.size = size.size;
                fontParameter.packer = packer;
                fontParameter.characters = CHARACTERS;
                BitmapFont bmFont = gen.generateFont(fontParameter);

                fontMap.get(style).put(size, bmFont);
                fontCount++;
            }

            // dispose of the generator once we're finished with this family
            gen.dispose();
        }

        // for the demo, show how many glyphs we loaded
        return fontCount * CHARACTERS.length();
    }

    // We use a nested EnumMap for fast access
    class FontMap<T> extends EnumMap<FontStyle, EnumMap<FontSize, T>> {

        public FontMap() {
            super(FontStyle.class);

            // create the enum map for each FontSize
            for(FontStyle style : FontStyle.values()) {
                put(style, new EnumMap<FontSize, T>(FontSize.class));
            }
        }
    }
}
