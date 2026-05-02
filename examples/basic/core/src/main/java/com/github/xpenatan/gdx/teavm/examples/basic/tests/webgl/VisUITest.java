package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class VisUITest extends ApplicationAdapter {

    private Stage stage;

    @Override
    public void create() {
        VisUI.load(VisUI.SkinScale.X1);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        boolean classpathSkinExists = Gdx.files.classpath("com/kotcrab/vis/ui/skin/x1/uiskin.json").exists();

        VisTable root = new VisTable(true);
        root.setFillParent(true);
        root.defaults().growX().pad(8f);

        VisWindow window = new VisWindow("VisUI Widgets");
        window.defaults().left().growX().pad(4f);

        VisLabel classpathLabel = new VisLabel("VisUI classpath skin exists: " + classpathSkinExists);
        VisCheckBox checkBox = new VisCheckBox("Enable slider", true);
        VisSlider slider = new VisSlider(0f, 100f, 1f, false);
        slider.setValue(50f);
        VisLabel sliderValueLabel = new VisLabel("Slider value: 50");

        VisTextField textField = new VisTextField("Type text here");
        VisSelectBox<String> selectBox = new VisSelectBox<>();
        selectBox.setItems("Option A", "Option B", "Option C", "Option D");

        VisList<String> list = new VisList<>();
        list.setItems("Red", "Green", "Blue", "Yellow", "Purple", "Orange");
        VisScrollPane listScroll = new VisScrollPane(list);
        listScroll.setFadeScrollBars(false);
        listScroll.setScrollingDisabled(true, false);

        VisTextButton actionButton = new VisTextButton("Log selection");

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                sliderValueLabel.setText("Slider value: " + (int)slider.getValue());
            }
        });

        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                slider.setDisabled(!checkBox.isChecked());
            }
        });

        actionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.log("VisUITest", "Text='" + textField.getText() + "', Select='" + selectBox.getSelected() + "', List='" + list.getSelected() + "'");
            }
        });

        window.add(classpathLabel).row();
        window.add(new VisTextButton("VisUI button")).row();
        window.add(checkBox).row();
        window.add(slider).row();
        window.add(sliderValueLabel).row();
        window.add(textField).row();
        window.add(selectBox).row();
        window.add(listScroll).height(120f).row();
        window.add(actionButton).row();
        window.pack();

        root.add(window).growX();

        stage.addActor(root);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        if(VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }
}

