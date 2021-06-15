package com.github.xpenatan.gdx.backend.web.soundmanager;

public interface SoundManagerWrapper {
	public SMSoundWrapper createSound (String url);
	public void setup(String url, SoundManagerCallbackWrapper callback);
}
