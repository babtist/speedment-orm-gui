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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
//	@FXML private TableView<Setting> tableSettings;
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
//	@FXML private TableColumn<Setting, String> settingsColumnKey;
//	@FXML private TableColumn<Setting, Setting.Value> settingsColumnValue;
	
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
		
//		settingsColumnKey.setCellValueFactory(
//			new PropertyValueFactory<>("key")
//		);
//		settingsColumnValue.setCellValueFactory(
//			new PropertyValueFactory<>("value")
//		);
//        
//        settingsColumnValue.setCellFactory(s -> new TableCell<Setting, Setting.Value>() {
//
//            final CheckBox checkBox = new CheckBox() {{
//                setAllowIndeterminate(true);
//                selectedProperty().addListener((ob, o, n) -> {
//                    if (shouldUpdate()) {
//                        onCheckboxChange(n);
//                    }
//                });
//            }};
//            
//            protected boolean shouldUpdate() {
//                return (checkBox.equals(getGraphic()));
//            }
//            
//            protected void onCheckboxChange(Boolean b) {
//                System.out.println("Checkbox was set to " + b);
//                System.out.println("Settings old value " + itemProperty().get().name());
//                itemProperty().setValue(b ? Setting.Value.TRUE : Setting.Value.FALSE);
//                System.out.println("Settings new value " + itemProperty().get().name());
//            }
//
//            @Override
//            protected void updateItem(Setting.Value value, boolean empty) {
//                super.updateItem(value, empty);
//                
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    System.out.println("Updating checkbox to: " + value.name());
//                    checkBox.setIndeterminate(Setting.Value.INDETERMINATE == value);
//                    checkBox.setSelected(Setting.Value.TRUE == value);
//                    setGraphic(checkBox);
//                }
//            }
//        });

		//settingsColumnValue.setCellFactory(CheckBoxTableCell.forTableColumn(settingsColumnValue));
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
			
//			tableSettings.getItems().clear();
//			tableSettings.getItems().addAll(
//				settingsFor(
//                    l.getList().stream()
//					.map(i -> i.getValue())
//                ).collect(Collectors.toList())
//			);
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
    
//    private Setting mergedSettings(Stream<Child<?>> nodes, String property, Function<Child<?>, Boolean> value) {
//        final List<Boolean> variants = nodes.map(s -> value.apply(s)).distinct().collect(Collectors.toList());
//        System.out.println("Merged variants to a list of " + variants.size());
//        if (variants.size() == 1) {
//            return new Setting(property, variants.get(0) ? Setting.Value.TRUE : Setting.Value.FALSE);
//        } else {
//            return new Setting(property, Setting.Value.INDETERMINATE);
//        }
//    }

	private void populatePropertyTable(Stream<TableProperty<?>> properties) {
		propertiesContainer.getChildren().clear();
		properties.forEach(p -> {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TableProperty.fxml"));
			final TablePropertyController control = new TablePropertyController(p);
			loader.setController(control);
			
			try {
				final HBox row = (HBox) loader.load();
				propertiesContainer.getChildren().add(row);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
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
	
//	private Stream<Setting> settingsFor(Stream<Child<?>> nodes) {
//		final Setting setting = mergedSettings(nodes, "Generate", c -> c.isEnabled());
//
//        setting.valueProperty().addListener((ov, o, newValue) -> {
//            System.out.println("Value changed in setting to: " + newValue); // KÖRS ALDRIG??
//            treeHierarchy.getSelectionModel().getSelectedItems().stream()
//                .filter(n -> newValue != Setting.Value.INDETERMINATE)
//                .map(i -> i.getValue())
//                .forEach(c -> c.setEnabled(newValue == Setting.Value.TRUE));
//        });
//
//        return Stream.of(setting);
//	}
//	
//	public static class Setting {
//		
//		private final StringProperty key;
//		private final ObjectProperty<Value> value;
//
//        public static enum Value {TRUE, FALSE, INDETERMINATE}
//		
//		private Setting(String key, Value value) {
//			this.key   = new SimpleStringProperty(key);
//			this.value = new SimpleObjectProperty<>(value);
//		}
//        
//		public StringProperty keyProperty() {
//			return key;
//		}
//		
//		public ObjectProperty<Value> valueProperty() {
//			return value;
//		}
//		
//		public void setKey(String key) {
//			this.key.setValue(key);
//		}
//		
//		public void setValue(Value value) {
//			this.value.setValue(value);
//		}
//		
//		public String getKey() {
//			return key.get();
//		}
//		
//		public Value getValue() {
//			return value.get();
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hashCode(getKey());
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			return Optional.ofNullable(obj)
//				.filter(o -> o instanceof Setting)
//				.map(o -> (Setting) o)
//				.filter(o -> getKey().equals(o.getKey()))
//				.isPresent();
//		}
//	}
}