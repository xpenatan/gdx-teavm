package com.github.xpenatan.gdx.backends.web.emu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Emulate{
    Class<?> value() default Object.class;
    String valueStr() default "";
    boolean replace() default false;
}