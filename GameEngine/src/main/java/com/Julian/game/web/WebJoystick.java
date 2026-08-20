package com.Julian.game.web;

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.Touch;
import org.teavm.jso.dom.events.TouchEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.TextRectangle;

import com.Julian.game.InputHandler;

/**
 * A virtual on-screen joystick for mobile/touch input.
 *
 * Tapping anywhere on the visible canvas spawns a joystick centered at the tap
 * position; dragging the thumb away from that origin moves the player in that
 * direction (both axes independently, so diagonal movement works). Lifting the
 * finger stops movement and removes the joystick.
 *
 * This drives player movement by toggling the exact same {@link InputHandler.Key}
 * instances ({@code up}/{@code down}/{@code left}/{@code right}) that keyboard
 * input already uses, so {@code Player.tick()} doesn't need to know or care
 * whether the movement came from a keyboard or a touchscreen. {@code input.D}
 * (the desktop interact key) is intentionally left untouched here.
 */
public final class WebJoystick {

	private static final double MAX_RADIUS = 40;
	private static final double DEADZONE = 11;

	private static final String BASE_FILL_STYLE = "rgba(255,255,255,0.25)";
	private static final String THUMB_FILL_STYLE = "rgba(255,255,255,0.5)";
	private static final double BASE_RADIUS = MAX_RADIUS;
	private static final double THUMB_RADIUS = 18;

	private final HTMLCanvasElement canvas;
	private final InputHandler input;

	private boolean active = false;
	private int activeTouchIdentifier = -1;
	private double originX;
	private double originY;
	private double thumbX;
	private double thumbY;

	public WebJoystick(HTMLCanvasElement canvas, InputHandler input) {
		this.canvas = canvas;
		this.input = input;

		canvas.addEventListener("touchstart", (EventListener<TouchEvent>) this::onTouchStart);
		canvas.addEventListener("touchmove", (EventListener<TouchEvent>) this::onTouchMove);
		canvas.addEventListener("touchend", (EventListener<TouchEvent>) this::onTouchEnd);
		canvas.addEventListener("touchcancel", (EventListener<TouchEvent>) this::onTouchEnd);
	}

	private void onTouchStart(TouchEvent event) {
		event.preventDefault();
		if (active) {
			// A joystick is already active from another finger; ignore additional touches.
			return;
		}
		JSArrayReader<Touch> changedTouches = event.getChangedTouches();
		if (changedTouches.getLength() == 0) {
			return;
		}
		Touch touch = changedTouches.get(0);
		double[] local = toCanvasLocal(touch);

		active = true;
		activeTouchIdentifier = touch.getIdentifier();
		originX = local[0];
		originY = local[1];
		thumbX = originX;
		thumbY = originY;
	}

	private void onTouchMove(TouchEvent event) {
		if (!active) {
			return;
		}
		Touch touch = findActiveTouch(event.getChangedTouches());
		if (touch == null) {
			touch = findActiveTouch(event.getTouches());
		}
		if (touch == null) {
			return;
		}
		event.preventDefault();

		double[] local = toCanvasLocal(touch);
		double deltaX = local[0] - originX;
		double deltaY = local[1] - originY;

		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if (distance > MAX_RADIUS && distance > 0) {
			double scale = MAX_RADIUS / distance;
			deltaX *= scale;
			deltaY *= scale;
		}

		thumbX = originX + deltaX;
		thumbY = originY + deltaY;

		input.right.toggle(deltaX > DEADZONE);
		input.left.toggle(deltaX < -DEADZONE);
		input.down.toggle(deltaY > DEADZONE);
		input.up.toggle(deltaY < -DEADZONE);
	}

	private void onTouchEnd(TouchEvent event) {
		if (!active) {
			return;
		}
		Touch touch = findActiveTouch(event.getChangedTouches());
		if (touch == null) {
			// The touch that ended isn't the one driving the joystick; ignore it.
			return;
		}
		event.preventDefault();
		reset();
	}

	private void reset() {
		active = false;
		activeTouchIdentifier = -1;
		input.up.toggle(false);
		input.down.toggle(false);
		input.left.toggle(false);
		input.right.toggle(false);
	}

	private Touch findActiveTouch(JSArrayReader<Touch> touches) {
		for (int i = 0; i < touches.getLength(); i += 1) {
			Touch touch = touches.get(i);
			if (touch.getIdentifier() == activeTouchIdentifier) {
				return touch;
			}
		}
		return null;
	}

	/** Converts a touch's client (viewport) coordinates into canvas-local pixel coordinates. */
	private double[] toCanvasLocal(Touch touch) {
		TextRectangle rect = canvas.getBoundingClientRect();
		double scaleX = rect.getWidth() > 0 ? (double) canvas.getWidth() / rect.getWidth() : 1;
		double scaleY = rect.getHeight() > 0 ? (double) canvas.getHeight() / rect.getHeight() : 1;
		double localX = (touch.getClientX() - rect.getLeft()) * scaleX;
		double localY = (touch.getClientY() - rect.getTop()) * scaleY;
		return new double[] { localX, localY };
	}

	/**
	 * Draws the joystick (base + thumb) onto the given context, if a touch is
	 * currently active. Intended to be called once per animation frame, after the
	 * game frame itself has been blitted, so the joystick renders on top.
	 */
	public void render(CanvasRenderingContext2D ctx) {
		if (!active) {
			return;
		}

		ctx.beginPath();
		ctx.arc(originX, originY, BASE_RADIUS, 0, Math.PI * 2);
		ctx.setFillStyle(BASE_FILL_STYLE);
		ctx.fill();

		ctx.beginPath();
		ctx.arc(thumbX, thumbY, THUMB_RADIUS, 0, Math.PI * 2);
		ctx.setFillStyle(THUMB_FILL_STYLE);
		ctx.fill();
	}
}
