package com.Julian.game.web;

import org.teavm.jso.JSBody;

/**
 * Thin bridge from Java into the React side's {@code window.__portfolioGame}
 * object (wired up by {@code src/components/GameCanvas.tsx} right before it
 * boots the wasm module). Defensively no-ops if that global (or the expected
 * method on it) isn't present, so this never throws even if called before the
 * React side has finished setting things up.
 */
public final class WebBridge {

	private WebBridge() {
	}

	@JSBody(params = { "isNear" }, script = "if (window.__portfolioGame && window.__portfolioGame.setNearInteractive) "
			+ "{ window.__portfolioGame.setNearInteractive(isNear); }")
	public static native void setNearInteractive(boolean isNear);
}
