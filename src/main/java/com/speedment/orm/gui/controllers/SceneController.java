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

import com.speedment.orm.config.model.Project;
import com.speedment.orm.config.model.aspects.Child;
import com.speedment.orm.gui.MainApp;
import com.speedment.orm.gui.properties.TableBooleanProperty;
import com.speedment.orm.gui.properties.TableProperty;
import com.speedment.orm.gui.properties.TablePropertyController;
import com.speedment.orm.gui.properties.TableStringProperty;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

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
	@FXML private VBox propertiesContainer;
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
	
	private final Stage stage;
	private final Project project;
	
	public SceneController(Stage stage, Project project) {
		this.stage = stage;
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
		
		// New project.
		final EventHandler<ActionEvent> newProject = ev -> {
			System.out.println("Creating new project.");
			final Stage newStage = new Stage();
			ProjectPromptController.showIn(newStage);
		};
		
		buttonNew.setOnAction(newProject);
		mbNew.setOnAction(newProject);
		
		// Open project.
		final EventHandler<ActionEvent> openProject = ev -> {
			System.out.println("Load project");
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Groovy File");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Groovy files", "groovy"));
			fileChooser.showOpenDialog(stage);
		};
		
		buttonOpen.setOnAction(openProject);
		mbOpen.setOnAction(openProject);
	}

	private void populateTree(Project project) {
		final ListChangeListener<? super TreeItem<Child<?>>> change = l -> {

			System.out.println("Selection changed to: " + 
				l.getList().stream()
					.map(i -> i.getValue().getName())
					.collect(Collectors.joining(", ", "(", ")"))
			);
			
			populatePropertyTable(
				propertiesFor(
					l.getList().stream()
					.map(i -> i.getValue())
					.collect(Collectors.toList())
				)
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

	private void populatePropertyTable(Stream<TableProperty<?>> properties) {
		propertiesContainer.getChildren().clear();
		properties.forEach(p -> {
			final HBox row = new TablePropertyController(p);
			propertiesContainer.getChildren().add(row);
		});
	}
	
	private Stream<TableProperty<?>> propertiesFor(List<Child<?>> nodes) {
        return Stream.of(
			createProperty("Name", nodes, TableStringProperty::new, n -> n.getName(), (n, v) -> n.setName(v)),
			createProperty("Include in generation", nodes, TableBooleanProperty::new, n -> n.isEnabled(), (n, v) -> n.setEnabled(v))
		);
	}
	
	private <V> TableProperty<V> createProperty(String label, List<Child<?>> nodes, BiFunction<String, V, TableProperty<V>> initiator, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater) {
		final V option = getOption(nodes, selector);
		final TableProperty<V> property = initiator.apply(label, option);
		
		property.valueProperty().addListener((ob, o, newValue) -> {
			System.out.println("Value changed in setting to: " + newValue);
			treeHierarchy.getSelectionModel().getSelectedItems().stream()
                .map(i -> i.getValue())
                .forEach(c -> updater.accept(c, newValue));
		});
		
		return property;
	}
	
	private <V> V getOption(List<Child<?>> nodes, Function<Child<?>, V> selector) {
		final List<V> variants = nodes.stream().map(n -> selector.apply(n)).distinct().collect(Collectors.toList());
		if (variants.size() == 1) {
			return variants.get(0);
		} else {
			return null;
		}
	}
	
	public static void showIn(Stage stage, Project project) {
		final FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/Scene.fxml"));
		final SceneController control = new SceneController(stage, project);
		loader.setController(control);

		try {
			final BorderPane root = (BorderPane) loader.load();
			final Scene scene = new Scene(root);

			stage.hide();
			stage.setTitle("Speedment ORM");
			stage.setMaximized(true);
			stage.setScene(scene);
			stage.show();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}