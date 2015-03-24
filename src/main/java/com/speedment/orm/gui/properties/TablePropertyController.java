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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 * @param <V>
 */
public class TablePropertyController<V> implements Initializable {
	
	@FXML private HBox containerView;
	@FXML private Label labelView;
	
	private final TableProperty<V> property;
	
	public TablePropertyController(TableProperty<V> property) {
		this.property = property;
	}

	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		labelView.textProperty().bind(property.nameProperty());
		containerView.getChildren().add(property.getValueGraphic());
		property.getValueGraphic().maxWidth(Double.MAX_VALUE);
	}
}