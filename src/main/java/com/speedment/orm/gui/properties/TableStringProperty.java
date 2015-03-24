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

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 * @author Emil Forslund
 */
public class TableStringProperty extends TableProperty<String> {
	
	private final TextField textfield;

	public TableStringProperty(String name, String value) {
		super (name);
		textfield = new TextField();
		
		if (value == null) {
			textfield.setText("");
		} else {
			textfield.setText(value);
		}
	}

	@Override
	public Property<String> valueProperty() {
		return textfield.textProperty();
	}

	@Override
	public Node getValueGraphic() {
		return textfield;
	}
}