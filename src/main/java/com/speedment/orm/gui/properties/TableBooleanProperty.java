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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 *
 * @author Emil Forslund
 */
public class TableBooleanProperty extends TableProperty<Boolean> {
	
	private final CheckBox checkbox;

	public TableBooleanProperty(String name, Boolean value) {
		super (name);
		checkbox = new CheckBox();
		checkbox.setAlignment(Pos.CENTER);
		
		if (value == null) {
			checkbox.setIndeterminate(true);
		} else {
			checkbox.setIndeterminate(false);
			checkbox.setSelected(value);
		}
	}

	@Override
	public Property<Boolean> valueProperty() {
		return checkbox.selectedProperty();
	}

	@Override
	public Node getValueGraphic() {
		return checkbox;
	}
}