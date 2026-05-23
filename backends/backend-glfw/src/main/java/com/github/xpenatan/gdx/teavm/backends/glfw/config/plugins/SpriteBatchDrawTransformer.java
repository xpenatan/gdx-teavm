package com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions.GdxTeaVMSpriteBatchSubstitution;
import com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions.GdxTeaVMSpriteSubstitution;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;
import org.teavm.model.ReferenceCache;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

public class SpriteBatchDrawTransformer implements ClassHolderTransformer {

    private static final String SPRITE_BATCH_CLASS = SpriteBatch.class.getName();
    private static final String SPRITE_BATCH_SUBSTITUTION_CLASS = GdxTeaVMSpriteBatchSubstitution.class.getName();
    private static final String SPRITE_CLASS = Sprite.class.getName();
    private static final String SPRITE_SUBSTITUTION_CLASS = GdxTeaVMSpriteSubstitution.class.getName();
    private static final MethodDescriptor DRAW_TEXTURE_ARRAY = new MethodReference(
            SpriteBatch.class, "draw", Texture.class, float[].class, int.class, int.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor GET_VERTICES = new MethodReference(
            Sprite.class, "getVertices", float[].class)
            .getDescriptor();

    private final ReferenceCache referenceCache = new ReferenceCache();

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if (SPRITE_BATCH_CLASS.equals(cls.getName())) {
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_TEXTURE_ARRAY);
        }
        else if (SPRITE_CLASS.equals(cls.getName())) {
            replaceMethod(cls, context, SPRITE_SUBSTITUTION_CLASS, SPRITE_CLASS, GET_VERTICES);
        }
    }

    private void replaceMethod(ClassHolder cls, ClassHolderTransformerContext context, String substitutionClass,
            String targetClass, MethodDescriptor descriptor) {
        ClassReader substitution = context.getHierarchy().getClassSource().get(substitutionClass);
        if (substitution == null) {
            throw new IllegalStateException("Missing TeaVM substitution class: " + substitutionClass);
        }

        MethodReader substitutionMethod = substitution.getMethod(descriptor);
        if (substitutionMethod == null) {
            throw new IllegalStateException("Missing substitution method: " + descriptor);
        }

        MethodHolder originalMethod = cls.getMethod(descriptor);
        if (originalMethod != null) {
            cls.removeMethod(originalMethod);
        }

        MethodHolder methodCopy = ModelUtils.copyMethod(substitutionMethod);
        ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache,
                name -> substitutionClass.equals(name) ? targetClass : name);
        cls.addMethod(renamer.rename(methodCopy));
    }
}
