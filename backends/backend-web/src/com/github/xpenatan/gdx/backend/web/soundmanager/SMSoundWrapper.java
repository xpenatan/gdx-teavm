package com.github.xpenatan.gdx.backend.web.soundmanager;

/**
 * @author xpenatan
 */
public interface SMSoundWrapper {

	/** Constants for play state. */
	public static final int STOPPED = 0;
	public static final int PLAYING = 1;

	public int getPosition();

	public void destruct();

	public void setPosition(int position);

	public void pause();

	public void play(SMSoundOptions options);

	public void play();

	public void resume();

	public void stop();

	public void setVolume(int volume);

	public int getVolume();

	public void setPan(int pan);

	public int getPan();

	public int getPlayState();

	public boolean getPaused();

	public int getLoops();
}
