package com.speedment.orm.gui.util;

import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author Emil Forslund
 */
public class FadeAnimation {
	
	public final static double SLOW = 1000, NORMAL = 500, FAST = 200;
	
	public static <T extends Node> T fadeOut(T node) {
		return fadeOut(node, null);
	}
	
	public static <T extends Node> T fadeIn(T node) {
		return fadeIn(node, null);
	}
	
	public static <T extends Node> T fadeOut(T node, EventHandler<ActionEvent> onFinished) {
		return fade(node, 1.0, 0.0, FAST, onFinished);
	}
	
	public static <T extends Node> T fadeIn(T node, EventHandler<ActionEvent> onFinished) {
		return fade(node, 0.0, 1.0, FAST, onFinished);
	}
	
	private static <T extends Node> T fade(T node, double from, double to, double duration, EventHandler<ActionEvent> onFinished) {
		node.setOpacity(from);
		final FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
		ft.setFromValue(from);
		ft.setToValue(to);
		ft.play();
		ft.setOnFinished(onFinished);
		return node;
	}
}
