package com.Julian.game.web;

import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLDocument;

/**
 * Loops a folder of background tracks via a single browser &lt;audio&gt;
 * element, replacing the desktop build's javax.sound.sampled MusicPlayer
 * (which doesn't exist in a TeaVM/wasmGC target). Ported behavior: cycle
 * through songNames in order, wrapping back to the start.
 *
 * Not yet wired to a call site or a real tracklist - see WebMain/Game once
 * real audio assets are supplied. Browsers also block audio.play() before a
 * user gesture (click/keydown/touchstart), so the eventual call site needs to
 * be gated behind one of those.
 */
public final class WebMusicPlayer {

	private final String folderPath;
	private final String[] songNames;
	private final HTMLAudioElement audio;
	private int songIndex = 0;

	public WebMusicPlayer(String folderPath, String[] songNames) {
		this.folderPath = folderPath;
		this.songNames = songNames;
		this.audio = (HTMLAudioElement) HTMLDocument.current().createElement("audio");
	}

	public void playNextSong() {
		if (songNames.length == 0) {
			return;
		}
		audio.setSrc(folderPath + songNames[songIndex]);
		audio.play();
		songIndex = (songIndex + 1) % songNames.length;
	}
}
