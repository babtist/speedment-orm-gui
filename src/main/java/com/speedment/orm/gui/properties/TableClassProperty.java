/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.orm.gui.properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 * @author Emil Forslund
 */
public class TableClassProperty extends TableProperty<Class> {
	
	private final TextField textfield;
	private final ObjectProperty<Class> property;

	public TableClassProperty(String name, Class value) {
		super (name);
		textfield = new TextField();
		
		if (value == null) {
			textfield.setText("...");
		} else {
			textfield.setText(value.getName());
		}
		
		property = new SimpleObjectProperty<>(value);
		property.bind(new ObjectBinding<Class>() {
			@Override
			protected Class computeValue() {
				if ("...".equals(textfield.getText())) {
					return null;
				} else {
					try {
						return Class.forName(textfield.getText());
					} catch (ClassNotFoundException ex) {
						return null;
					}
				}
			}
		});
	}

	@Override
	public Property<Class> valueProperty() {
		return property;
	}

	@Override
	public Node getValueGraphic() {
		return textfield;
	}
}