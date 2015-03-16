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
package com.speedment.orm.gui;

import com.speedment.orm.config.model.parameters.StandardDbmsType;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class SceneController implements Initializable {
    
    @FXML private TreeView<?> panelTree;
    @FXML private VBox panelConnect;
    @FXML private Button buttonConnect;
    @FXML private ChoiceBox<String> fieldDatabaseType;
    @FXML private TextField fieldHost;
    @FXML private TextField fieldPort;
    @FXML private TextField fieldUsername;
    @FXML private PasswordField fieldPassword;
    @FXML private TextField fieldDatabaseName;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		//Platform.get().get(DbmsComponent.class).

        fieldDatabaseType.setItems(Stream.of(StandardDbmsType.values())
			.map(s -> s.getName())
			.collect(Collectors.toCollection(FXCollections::observableArrayList)));
		
		fieldDatabaseType.getSelectionModel().selectedItemProperty().addListener((observable, old, next) -> {
			System.out.println("Selecting");
			if (!observable.getValue().isEmpty()) {
				System.out.println("Observable: '" + observable + "', Old: '" + old + "', New: '" + next + "'.");
				final StandardDbmsType item = StandardDbmsType.valueOf(next.toUpperCase());

				if (fieldHost.textProperty().getValue().isEmpty()) {
					System.out.println("Setting host.");
					fieldHost.textProperty().setValue("127.0.0.1");
				}

				System.out.println("Setting port.");
				fieldPort.textProperty().setValue("" + item.getDefaultPort());
			}
		});
        
        buttonConnect.onActionProperty().addListener(l -> {
            System.out.println("Attempting to connect...");
        });
    }
}