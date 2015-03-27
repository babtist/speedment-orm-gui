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

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public class TableEnumProperty<T extends Enum<T>> extends TableProperty<T> {
	
	private final ChoiceBox<T> choice;

	public TableEnumProperty(String name, T value) {
		super (name);
		
		choice = new ChoiceBox(
			Stream.of(value.getClass().getEnumConstants())
				.map(e -> (T) e)
				.collect(Collectors.toCollection(
					FXCollections::observableArrayList
				))
		);
		
		choice.setValue(value);
		
		choice.setConverter(new StringConverter<T>() {
			@Override
			public String toString(T object) {
				final String str = object.name().replace("_", " ").toLowerCase();
				if (str.length() < 2) {
					return str.toUpperCase();
				} else {
					return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
				}
			}

			@Override
			public T fromString(String string) {
				return Stream.of(value.getClass().getEnumConstants())
					.map(e -> (T) e)
					.filter(e -> e.name().replace("_", " ").equalsIgnoreCase(string))
					.findAny().orElse(null);
			}
		});
	}

	@Override
	public Property<T> valueProperty() {
		return choice.valueProperty();
	}

	@Override
	public Node getValueGraphic() {
		return choice;
	}
}