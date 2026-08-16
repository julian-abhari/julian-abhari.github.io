package com.Julian.game;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.HTMLDocument;

public class InputHandler {

	public InputHandler() {
		HTMLDocument document = HTMLDocument.current();
		document.addEventListener("keydown", (EventListener<KeyboardEvent>) this::onKeyDown);
		document.addEventListener("keyup", (EventListener<KeyboardEvent>) this::onKeyUp);
	}

	public class Key {
		public boolean pressed = false;

		public boolean isPressed() {
			return pressed;
		}

		public void toggle(boolean isPressed) {
			pressed = isPressed;
		}
	}

	//-----Movement Keys-----
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	// Interact with item or self
	public Key D = new Key();

	private void onKeyDown(KeyboardEvent event) {
		if (toggleKey(event.getKey(), true)) {
			event.preventDefault();
		}
	}

	private void onKeyUp(KeyboardEvent event) {
		if (toggleKey(event.getKey(), false)) {
			event.preventDefault();
		}
	}

	public boolean toggleKey(String key, boolean isPressed) {
		//-----Movement Keys-----
		if (key.equals("ArrowUp")) {
			up.toggle(isPressed);
			return true;
		}
		if (key.equals("ArrowDown")) {
			down.toggle(isPressed);
			return true;
		}
		if (key.equals("ArrowLeft")) {
			left.toggle(isPressed);
			return true;
		}
		if (key.equals("ArrowRight")) {
			right.toggle(isPressed);
			return true;
		}
		// Interact with item or self Key
		if (key.equalsIgnoreCase("d")) {
			D.toggle(isPressed);
			return true;
		}
		return false;
	}

}
