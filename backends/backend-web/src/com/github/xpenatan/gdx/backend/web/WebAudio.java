package com.github.xpenatan.gdx.backend.web;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerWrapper;

public class WebAudio implements Audio {

	private SoundManagerWrapper soundManager;

	public WebAudio(SoundManagerWrapper soundManager) {
		this.soundManager = soundManager;
	}

	@Override
	public AudioDevice newAudioDevice (int samplingRate, boolean isMono) {
		throw new GdxRuntimeException("AudioDevice not supported by Web backend");
	}

	@Override
	public AudioRecorder newAudioRecorder (int samplingRate, boolean isMono) {
		throw new GdxRuntimeException("AudioRecorder not supported by Web backend");
	}

	@Override
	public Sound newSound(FileHandle fileHandle) {
		return new WebSound(soundManager, fileHandle);
	}

	@Override
	public Music newMusic(FileHandle file) {
		return new WebMusic(soundManager, file);
	}

}
