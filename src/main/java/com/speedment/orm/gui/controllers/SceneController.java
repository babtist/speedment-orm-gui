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
package com.speedment.orm.gui.controllers;

import com.speedment.orm.config.model.Column;
import com.speedment.orm.config.model.Project;
import com.speedment.orm.config.model.aspects.Child;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class SceneController implements Initializable {

	@FXML private Button buttonNew;
	@FXML private Button buttonOpen;
	@FXML private Button buttonGenerate;
	@FXML private ImageView logo;
	@FXML private TreeView<Child<?>> treeHierarchy;
	@FXML private TableView<String> tableProjectSettings;
	@FXML private TableView<Setting> tableSettings;
	@FXML private TextArea output;
	@FXML private Menu menuFile;
	@FXML private MenuItem mbNew;
	@FXML private MenuItem mbOpen;
	@FXML private MenuItem mbSave;
	@FXML private MenuItem mbSaveAs;
	@FXML private MenuItem mbQuit;
	@FXML private Menu menuEdit;
	@FXML private MenuItem mbGenerate;
	@FXML private Menu menuHelp;
	@FXML private MenuItem mbGitHub;
	@FXML private MenuItem mbAbout;
	@FXML private TableColumn settingsColumnKey;
	@FXML private TableColumn settingsColumnValue;
	
	private final Project project;
	
	public SceneController(Project project) {
		this.project = project;
	}

	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		populateTree(project);
		
		settingsColumnKey.setCellValueFactory(
			new PropertyValueFactory<>("key")
		);
		settingsColumnValue.setCellValueFactory(
			new PropertyValueFactory<>("value")
		);
		settingsColumnValue.setCellFactory(TextFieldTableCell.<Setting>forTableColumn());
		
		//Platform.get().get(DbmsComponent.class).

		// Load database types from Speedment.
//		fieldDatabaseType.setItems(Stream.of(StandardDbmsType.values())
//				.map(s -> s.getName())
//				.collect(Collectors.toCollection(FXCollections::observableArrayList)));

		// When the database type changes, other fields should be entered automatically.
//		fieldDatabaseType.getSelectionModel().selectedItemProperty().addListener((observable, old, next) -> {
//			System.out.println("Selecting");
//			if (!observable.getValue().isEmpty()) {
//				System.out.println("Observable: '" + observable + "', Old: '" + old + "', New: '" + next + "'.");
//				final StandardDbmsType item = StandardDbmsType.valueOf(next.toUpperCase());
//
//				if (fieldHost.textProperty().getValue().isEmpty()) {
//					System.out.println("Setting host.");
//					fieldHost.textProperty().setValue("127.0.0.1");
//				}
//
//				System.out.println("Setting port.");
//				fieldPort.textProperty().setValue("" + item.getDefaultPort());
//			}
//		});

		// When the user click on "Connect"
//		buttonConnect.setOnAction(l -> {
//			System.out.println("Attempting to connect...");
//			fadeOut(panelConnect, e -> rightPanel.getChildren().remove(panelConnect));
//
//			final Dbms dbms = Dbms.newDbms();
//			dbms.setIpAddress(fieldHost.textProperty().getValue());
//			dbms.setPort(Integer.parseInt(fieldPort.textProperty().getValue()));
//			dbms.setUsername(fieldUsername.textProperty().getValue());
//			dbms.setPassword(fieldPassword.textProperty().getValue());
//			dbms.setName(fieldDatabaseName.textProperty().getValue());
//			dbms.setType(StandardDbmsType.valueOf(fieldDatabaseType.getValue().toUpperCase()));
//
//			dbms.addNewSchema().setName("Schema 1");
//			dbms.addNewSchema().setName("Schema 2");
//			dbms.addNewSchema().setName("Schema 3");
//
//			// TODO Check validity of input.
//			// TODO Connect.
//			populateTree(dbms);
//
//			System.out.println("DBMS Name: " + dbms.getName());
//		});
		
		// Animate settings panels.
		//settingsPane.getChildren().addListener(new LayoutAnimation());
	}

	private void populateTree(Project project) {
		final ListChangeListener<? super TreeItem<Child<?>>> change = l -> {
			System.out.println("Selection changed to: " + 
				l.getList().stream()
					.map(i -> i.getValue().getName())
					.collect(Collectors.joining(", ", "(", ")"))
			);
			
			tableSettings.getItems().clear();
			tableSettings.getItems().addAll(
				l.getList().stream()
					.map(i -> i.getValue())
					.flatMap(i -> settingsFor(i))
					
					.distinct()
					.collect(Collectors.toList())
			);
		};
		
		treeHierarchy.setCellFactory(v -> new TreeCell<Child<?>>() {
			@Override
			protected void updateItem(Child<?> item, boolean empty) {
				super.updateItem(item, empty);
				
				Optional.ofNullable(item)
					.filter(i -> !empty)
					.ifPresent(i -> setText(i.getName()));
			}
		});

		treeHierarchy.getSelectionModel().setSelectionMode(MULTIPLE);
		treeHierarchy.getSelectionModel().getSelectedItems().addListener(change);
		treeHierarchy.setRoot(branch(project));
	}

	private TreeItem<Child<?>> branch(Child<?> node) {
		final TreeItem<Child<?>> branch = new TreeItem<>(node);
		branch.setExpanded(true);

		node.asParent().ifPresent(p -> {
			p.stream().map(c -> branch(c)).forEach(
				c -> branch.getChildren().add(c)
			);
		});

		return branch;
	}
	
	private Stream<Setting> settingsFor(Child<?> node) {
		if (node.is(Column.class)) {
			final Column column = (Column) node;
			final Setting setting = new Setting("Generate", "true");

			setting.valueProperty().addListener((ov, o, newValue) -> {
				System.out.println("Value changed in setting to: " + newValue);
				treeHierarchy.getSelectionModel().getSelectedItems().stream()
					.map(i -> i.getValue())
					.filter(n -> n.is(Column.class))
					.map(n -> (Column) n)
					.forEach(c -> c.setEnabled("true".equals(newValue)));
			});

			return Stream.of(setting);
		} else {
			return Stream.empty();
		}
	}
	
	public static class Setting {
		
		private final StringProperty key;
		private final StringProperty value;
		
		private Setting(String key, String value) {
			this.key   = new SimpleStringProperty(key);
			this.value = new SimpleStringProperty(value);
		}
		
		public StringProperty keyProperty() {
			return key;
		}
		
		public StringProperty valueProperty() {
			return value;
		}
		
		public void setKey(String key) {
			this.key.setValue(key);
		}
		
		public void setValue(String value) {
			this.value.setValue(value);
		}
		
		public String getKey() {
			return key.get();
		}
		
		public String getValue() {
			return value.get();
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(getKey());
		}

		@Override
		public boolean equals(Object obj) {
			return Optional.ofNullable(obj)
				.filter(o -> o instanceof Setting)
				.map(o -> (Setting) o)
				.filter(o -> getKey().equals(o.getKey()))
				.isPresent();
		}
	}
}
