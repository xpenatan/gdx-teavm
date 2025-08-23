package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class AudioTest extends ApplicationAdapter {

    private static final String[] FILENAMES = {"shotgun.ogg", "shotgun-8bit.wav", "shotgun-32float.wav", "shotgun-64float.wav",
            "quadraphonic.ogg", "quadraphonic.wav", "bubblepop.ogg", "bubblepop-stereo-left-only.wav"};

    Music music;
    float songDuration;
    float currentPosition;

    Sound sound;
    long soundId = 0;

    SpriteBatch batch;

    Stage stage;
    Label label;
    Slider slider;
    boolean sliderUpdating = false;
    SelectBox<Song> musicBox;
    TextButton btLoop;


    enum Song {
        MP3, OGG, WAV, PCM8, MP3_CLOCK
    }

    float time;

    @Override
    public void create() {

        batch = new SpriteBatch();

        stage = new Stage(new ExtendViewport(600, 480));
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        stage.setDebugAll(false);
        setupMusic(skin);
        setupSound(skin);

        Gdx.input.setInputProcessor(stage);
    }

    private void setupSound(Skin skin) {
        final SelectBox<String> soundSelector = new SelectBox<String>(skin);
        soundSelector.setItems(FILENAMES);
        setSound(soundSelector.getSelected());

        TextButton play = new TextButton("Play", skin);
        TextButton pause = new TextButton("Pause", skin);
        TextButton resume = new TextButton("Resume", skin);
        TextButton stop = new TextButton("Stop", skin);
        TextButton loop = new TextButton("Loop", skin);
        final Slider pitch = new Slider(0.1f, 4, 0.1f, false, skin);
        pitch.setValue(1);
        final Label pitchValue = new Label("1.0", skin);
        final Slider volume = new Slider(0.1f, 1, 0.1f, false, skin);
        volume.setValue(1);
        final Label volumeValue = new Label("1.0", skin);
        Table table = new Table();
        final Slider pan = new Slider(-1f, 1f, 0.1f, false, skin);
        pan.setValue(0);
        final Label panValue = new Label("0.0", skin);
        table.setFillParent(true);

        table.align(Align.center | Align.top);
        table.add(soundSelector);
        table.row();

        Table buttonTable = new Table();
        buttonTable.add(play);
        buttonTable.add(pause);
        buttonTable.add(resume);
        buttonTable.add(loop);
        buttonTable.add(stop);

        table.add(buttonTable);
        table.row();

        Table controllTable = new Table();
        controllTable.add(new Label("Pitch", skin));
        controllTable.add(pitch);
        controllTable.add(pitchValue);
        controllTable.row();
        controllTable.add(new Label("Volume", skin));
        controllTable.add(volume);
        controllTable.add(volumeValue);
        controllTable.row();
        controllTable.add(new Label("Pan", skin));
        controllTable.add(pan);
        controllTable.add(panValue);
        table.add(controllTable);

        soundSelector.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                setSound(soundSelector.getSelected());
            }
        });

        play.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                soundId = sound.play(volume.getValue());
                sound.setPitch(soundId, pitch.getValue());
                sound.setPan(soundId, pan.getValue(), volume.getValue());
            }
        });

        resume.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                sound.resume();
            }
        });

        pause.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                sound.pause();
            }
        });

        loop.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                soundId = sound.loop(volume.getValue());
                sound.setPitch(soundId, pitch.getValue());
                sound.setPan(soundId, pan.getValue(), volume.getValue());
            }
        });
        stop.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                sound.stop();
                soundId = 0;
            }
        });
        pitch.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                sound.setPitch(soundId, pitch.getValue());
                pitchValue.setText("" + pitch.getValue());
            }
        });
        volume.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                sound.setVolume(soundId, volume.getValue());
                volumeValue.setText("" + volume.getValue());
            }
        });
        pan.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                sound.setPan(soundId, pan.getValue(), volume.getValue());
                panValue.setText("" + pan.getValue());
            }
        });

        stage.addActor(table);
    }

    protected void setSound (String fileName) {
        if (sound != null) sound.dispose();
        sound = Gdx.audio.newSound(Gdx.files.internal("data/audio/").child(fileName));
    }

    private void setupMusic(Skin skin) {
        Table sliderTable = new Table();
        label = new Label("", skin);
        slider = new Slider(0, 100, 0.1f, false, skin);
        sliderTable.add(slider).expand();
        sliderTable.add(label).left().width(60f);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!sliderUpdating && slider.isDragging()) music.setPosition((slider.getValue() / 100f) * songDuration);
            }
        });

        musicBox = new SelectBox<Song>(skin);
        musicBox.setItems(Song.values());
        musicBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setSong(musicBox.getSelected());
            }
        });

        btLoop = new TextButton("loop", skin, "toggle");
        btLoop.setChecked(true);
        btLoop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(music != null) music.setLooping(btLoop.isChecked());
            }
        });

        // Build buttons
        Table controlsTable = new Table();
        controlsTable.setSize(200f, 80f);
        Button playButton = new ImageButton(getDrawable("data/audio/player_play.png"));
        Button pauseButton = new ImageButton(getDrawable("data/audio/player_pause.png"));
        Button stopButton = new ImageButton(getDrawable("data/audio/player_stop.png"));
        float buttonSize = 64f;
        controlsTable.add(playButton).size(buttonSize);
        controlsTable.add(pauseButton).size(buttonSize);
        controlsTable.add(stopButton).size(buttonSize);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                music.play();
                time = 0;
            }
        });
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                music.pause();
            }
        });
        stopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                music.stop();
            }
        });

        Table footerTable = new Table();
        footerTable.setSize(500f, 120f);
        footerTable.add(controlsTable);
        footerTable.add(sliderTable).width(250f);

        setSong(musicBox.getSelected());

        Table table = new Table(skin);
        table.add(musicBox);
        table.add(btLoop);
        table.setFillParent(true);
        stage.addActor(table);
        stage.addActor(footerTable);
    }

    void setSong(Song song) {
        if(music != null) {
            music.dispose();
        }
        switch(song) {
            default:
            case MP3_CLOCK:
                music = Gdx.audio.newMusic(Gdx.files.internal("data/audio/60bpm.mp3"));
                songDuration = 5 * 60 + 4;
                break;
            case MP3:
                music = Gdx.audio.newMusic(Gdx.files.internal("data/audio/8.12.mp3"));
                songDuration = 183;
                break;
            case OGG:
                music = Gdx.audio.newMusic(Gdx.files.internal("data/audio/8.12.ogg"));
                songDuration = 183;
                break;
            case WAV:
                music = Gdx.audio.newMusic(Gdx.files.internal("data/audio/8.12.loop.wav"));
                songDuration = 4;
                break;
            case PCM8:
                music = Gdx.audio.newMusic(Gdx.files.internal("data/audio/8.12.loop-8bit.wav"));
                songDuration = 4;
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void resume() {
        System.out.println(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        currentPosition = music.getPosition();
        label.setText((int)currentPosition / 60 + ":" + (int)currentPosition % 60);

        sliderUpdating = true;
        slider.setValue((currentPosition / songDuration) * 100f);
        sliderUpdating = false;
        stage.act();
        stage.draw();

// if(music.isPlaying()){
// time += Gdx.graphics.getDeltaTime();
// System.out.println("realtime: " + time + " music time: " + currentPosition);
// }
    }

    @Override
    public void dispose() {
        batch.dispose();
        music.dispose();
    }

    private Drawable getDrawable(String path) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }
}
