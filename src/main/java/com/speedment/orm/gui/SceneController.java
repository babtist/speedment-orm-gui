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

import com.speedment.orm.config.model.Dbms;
import com.speedment.orm.config.model.Schema;
import com.speedment.orm.config.model.aspects.Child;
import com.speedment.orm.config.model.parameters.StandardDbmsType;
import com.speedment.orm.gui.controllers.TableSettingsController;
import static com.speedment.orm.gui.util.FadeAnimation.fadeIn;
import static com.speedment.orm.gui.util.FadeAnimation.fadeOut;
import com.speedment.orm.gui.util.LayoutAnimation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class SceneController implements Initializable {

	@FXML
	private TreeView<String> panelTree;
	@FXML
	private VBox panelConnect;
	@FXML
	private StackPane rightPanel;
	@FXML
	private FlowPane settingsPane;
	@FXML
	private Button buttonConnect;
	@FXML
	private ChoiceBox<String> fieldDatabaseType;
	@FXML
	private TextField fieldHost;
	@FXML
	private TextField fieldPort;
	@FXML
	private TextField fieldUsername;
	@FXML
	private PasswordField fieldPassword;
	@FXML
	private TextField fieldDatabaseName;

	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Platform.get().get(DbmsComponent.class).

		// Load database types from Speedment.
		fieldDatabaseType.setItems(Stream.of(StandardDbmsType.values())
				.map(s -> s.getName())
				.collect(Collectors.toCollection(FXCollections::observableArrayList)));

		// When the database type changes, other fields should be entered automatically.
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

		// When the user click on "Connect"
		buttonConnect.setOnAction(l -> {
			System.out.println("Attempting to connect...");
			fadeOut(panelConnect, e -> rightPanel.getChildren().remove(panelConnect));

			final Dbms dbms = Dbms.newDbms();
			dbms.setIpAddress(fieldHost.textProperty().getValue());
			dbms.setPort(Integer.parseInt(fieldPort.textProperty().getValue()));
			dbms.setUsername(fieldUsername.textProperty().getValue());
			dbms.setPassword(fieldPassword.textProperty().getValue());
			dbms.setName(fieldDatabaseName.textProperty().getValue());
			dbms.setType(StandardDbmsType.valueOf(fieldDatabaseType.getValue().toUpperCase()));

			dbms.addNewSchema().setName("Schema 1");
			dbms.addNewSchema().setName("Schema 2");
			dbms.addNewSchema().setName("Schema 3");

			// TODO Check validity of input.
			// TODO Connect.
			populateTree(dbms);

			System.out.println("DBMS Name: " + dbms.getName());
		});
		
		// Animate settings panels.
		settingsPane.getChildren().addListener(new LayoutAnimation());
	}

	private void populateTree(Dbms dbms) {
		panelTree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
		panelTree.setRoot(branch(dbms));
//		panelTree.getSelectionModel().selectedItemProperty().addListener((obsrv, last, next) -> {
//			final TreeItem<String> selectedItem = (TreeItem<String>) next;
//			System.out.println("Selected Text : " + selectedItem.getValue());
//			// do what ever you want 
//		});
	}

	private CheckBoxTreeItem<String> branch(Child<?> node) {
		final CheckBoxTreeItem<String> branch = new CheckBoxTreeItem<>(node.getName());
		branch.setExpanded(true);
		branch.setSelected(true);
		
		if (node.is(Schema.class)) {
			createSettingsPanel((Schema) node, branch.selectedProperty());
		}

		node.asParent().ifPresent(p -> {
			p.stream().map(c -> branch(c)).forEach(
				c -> branch.getChildren().add(c)
			);
		});

		return branch;
	}

	private void createSettingsPanel(Schema table, BooleanProperty visiblitySwitch) {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TableSettings.fxml"));
		final TableSettingsController control = new TableSettingsController(table);
		loader.setController(control);
		System.out.println("Creating settings panel for " + table.getName());
		try {
			final GridPane node = (GridPane) loader.load();
			visiblitySwitch.addListener((o, oldValue, newValue) -> {
				if (newValue) {
					settingsPane.getChildren().add(fadeIn(node));
					System.out.println("Selected " + table.getName());
				} else {
					fadeOut(node, e -> settingsPane.getChildren().remove(node));
					System.out.println("Unselected " + table.getName());
				}
			});
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
