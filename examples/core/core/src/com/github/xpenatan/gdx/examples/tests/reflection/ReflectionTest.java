package com.github.xpenatan.gdx.examples.tests.reflection;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.github.xpenatan.gdx.examples.tests.reflection.models.ReflectionModel;
import com.github.xpenatan.gdx.examples.tests.reflection.models.ReflectionModelItem;

public class ReflectionTest implements ApplicationListener {
    // TODO convert to junittest

    @Override
    public void create() {
        FileHandle file = Gdx.files.internal("reflection/reflectionClass.json");
        Json json = new Json();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue parse = jsonReader.parse(file);
            ReflectionModel data = json.readValue(ReflectionModel.class, null, parse);
            ReflectionModelItem testModel = data.buffers.get(0);
            System.out.println("byteLength: " + testModel.byteLength);
            System.out.println("uri: " + testModel.uri);
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
