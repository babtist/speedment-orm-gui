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

import java.util.Objects;
import java.util.Optional;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 *
 * @author Emil Forslund
 * @param <V> The type of the value column.
 */
public abstract class TableProperty<V> {
	
	private final StringProperty name;
	
	public TableProperty(String name) {
		this.name  = new SimpleStringProperty(name);
	}
	
	public StringProperty nameProperty() {
		return name;
	}

	public abstract Property<V> valueProperty();
	public abstract Node getValueGraphic();

	@Override
	public int hashCode() {
		return Objects.hashCode(this.name);
	}

	@Override
	public boolean equals(Object obj) {
		return Optional.ofNullable(obj)
			.filter(o -> TableProperty.class.isAssignableFrom(o.getClass()))
			.map(o -> (TableProperty) o)
			.filter(tp -> name.equals(tp.name))
			.isPresent();
	}
	
	
}