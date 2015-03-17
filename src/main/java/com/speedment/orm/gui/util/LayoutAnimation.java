package com.speedment.orm.gui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author Emil Forslund
 */
public class LayoutAnimation implements ChangeListener, ListChangeListener<Node> {

	private final Map<Node, Transition> nodesInTransition;

	public LayoutAnimation() {
		this.nodesInTransition = new HashMap<>();
	}

	public void observe(ObservableList<Node> nodes) {
		nodes.forEach(n -> {
			this.observe(n);
		});
		nodes.addListener(this);
	}

	public void unobserve(ObservableList<Node> nodes) {
		nodes.removeListener(this);
	}

	public void observe(Node n) {
		n.layoutXProperty().addListener(this);
		n.layoutYProperty().addListener(this);
	}

	public void unobserve(Node n) {
		n.layoutXProperty().removeListener(this);
		n.layoutYProperty().removeListener(this);
	}

	@Override
	public void changed(ObservableValue ov, Object oldValue, Object newValue) {
		final Double oldValueDouble = (Double) oldValue;
		final Double newValueDouble = (Double) newValue;
		final Double changeValueDouble = newValueDouble - oldValueDouble;
		final DoubleProperty doubleProperty = (DoubleProperty) ov;

		final Node node = (Node) doubleProperty.getBean();
		final TranslateTransition t;
		
		if (nodesInTransition.get(node) == null) {
			t = new TranslateTransition(Duration.millis(150), node);
		} else {
			t = (TranslateTransition) nodesInTransition.get(node);
		}

		if ("layoutX".equals(doubleProperty.getName())) {
			Double orig = node.getTranslateX();
			if (Double.compare(t.getFromX(), Double.NaN) == 0) {
				t.setFromX(orig - changeValueDouble);
				t.setToX(orig);
			}
		}
		
		if ("layoutY".equals(doubleProperty.getName())) {
			Double orig = node.getTranslateY();
			if (Double.compare(t.getFromY(), Double.NaN) == 0) {
				t.setFromY(orig - changeValueDouble);
				t.setToY(orig);
			}
		}
		
		t.play();
	}

	@Override
	public void onChanged(ListChangeListener.Change change) {
		while (change.next()) {
			if (change.wasAdded()) {
				for (Node node : (List<Node>) change.getAddedSubList()) {
					this.observe(node);
				}
			} else if (change.wasRemoved()) {
				for (Node node : (List<Node>) change.getRemoved()) {
					this.unobserve(node);
				}
			}
		}
	}
}
