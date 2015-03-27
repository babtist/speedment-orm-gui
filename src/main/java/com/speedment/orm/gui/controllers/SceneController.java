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

import com.speedment.orm.config.model.External;
import com.speedment.orm.config.model.Project;
import com.speedment.orm.config.model.aspects.Child;
import com.speedment.orm.config.model.aspects.Node;
import com.speedment.orm.config.model.impl.utils.MethodsParser;
import com.speedment.orm.gui.MainApp;
import com.speedment.orm.gui.icons.Icons;
import com.speedment.orm.gui.icons.SilkIcons;
import com.speedment.orm.gui.properties.TableBooleanProperty;
import com.speedment.orm.gui.properties.TableClassProperty;
import com.speedment.orm.gui.properties.TableEnumProperty;
import com.speedment.orm.gui.properties.TableProperty;
import com.speedment.orm.gui.properties.TablePropertyRow;
import com.speedment.orm.gui.properties.TableStringProperty;
import com.speedment.orm.gui.util.FadeAnimation;
import com.speedment.util.java.JavaLanguage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static javafx.animation.Animation.INDEFINITE;
import static javafx.animation.Interpolator.EASE_BOTH;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import static javafx.util.Duration.ZERO;
import static javafx.util.Duration.millis;

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
	@FXML private StackPane arrowContainer;
	@FXML private Label arrow;
	
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
		
		animateArrow();
		
		mbNew.setGraphic(Icons.NEW_PROJECT.view());
		mbOpen.setGraphic(Icons.OPEN_PROJECT.view());
		mbSave.setGraphic(SilkIcons.DISK.view());
		mbSaveAs.setGraphic(SilkIcons.DISK_MULTIPLE.view());
		mbQuit.setGraphic(SilkIcons.DOOR_IN.view());
		mbGenerate.setGraphic(Icons.RUN_PROJECT.view());
		mbGitHub.setGraphic(SilkIcons.USER_COMMENT.view());
		mbAbout.setGraphic(SilkIcons.INFORMATION.view());
		
		buttonNew.setGraphic(Icons.NEW_PROJECT_24.view());
		buttonOpen.setGraphic(Icons.OPEN_PROJECT_24.view());
		buttonGenerate.setGraphic(Icons.RUN_PROJECT_24.view());
		
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
				
				if (item == null || empty) {
					setGraphic(null);
					setText(null);
				} else {
					setGraphic(iconFor(item));
					setText(item.getName());
				}
			}
		});

		treeHierarchy.getSelectionModel().setSelectionMode(MULTIPLE);
		treeHierarchy.getSelectionModel().getSelectedItems().addListener(change);
		treeHierarchy.setRoot(branch(project));
	}
	
	private ImageView iconFor(Node node) {
		final Icons icon = Icons.forNodeType(node.getInterfaceMainClass());
		
		if (icon == null) {
			throw new RuntimeException("Unknown node type '" + node.getInterfaceMainClass().getName() + "'.");
		} else {
			return icon.view();
		}
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
			final HBox row = new TablePropertyRow<>(p);
			propertiesContainer.getChildren().add(row);
		});
	}
	
	private Stream<TableProperty<?>> propertiesFor(List<Child<?>> nodes) {
		
		return nodes.stream().flatMap(node -> {
			System.out.println(node);
			return MethodsParser.streamOfExternal(node.getClass())
				.sorted((m0, m1) -> m0.getName().compareTo(m1.getName()))
				.map(m -> {
					final String javaName;
					if (m.getName().startsWith("is")) {
						javaName = m.getName().substring(2);
					} else {
						javaName = m.getName().substring(3);
					}
					
					final String propertyName = JavaLanguage.toHumanReadable(javaName);
					final External e = MethodsParser.getExternalFor(m, node.getClass());
					
					System.out.println(m);
					
					Class<?> type = m.getReturnType();
					Class<?> innerType = e.type();
					boolean optional = Optional.class.isAssignableFrom(type);

					if (Boolean.class.isAssignableFrom(innerType)) {
						return createBooleanProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, Boolean.class)
						);
					} else if (String.class.isAssignableFrom(innerType)) {
						return createStringProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, String.class)
						);
					} else if (Class.class.isAssignableFrom(innerType)) {
						return createClassProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, Class.class)
						);
					} else if (Enum.class.isAssignableFrom(innerType)) {
						return createEnumProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, Enum.class), 
							findSetter(node.getClass(), javaName, (Class<Enum>) innerType),
							(Enum) type.getEnumConstants()[0]
						);
					} else {
						throw new UnsupportedOperationException("Found method '" + m + "' marked as @External of unsupported type " + type.getName());
					}
				});
		}).distinct().map(tp -> (TableProperty<?>) tp);
		
//        return Stream.of(
//			createProperty("Name", nodes, TableStringProperty::new, n -> n.getName(), (n, v) -> n.setName(v)),
//			createProperty("Include in generation", nodes, TableBooleanProperty::new, n -> n.isEnabled(), (n, v) -> n.setEnabled(v))
//		);
	}
	
	private <V> Function<Child<?>, V> findGetter(Class<?> nodeClass, String javaName, boolean optional, Class<?> innerType) {
		final String methodName;
		
		if (Boolean.class.isAssignableFrom(innerType)) {
			methodName = "is" + javaName;
		} else {
			methodName = "get" + javaName;
		}

		try {
			final Method method = nodeClass.getMethod(methodName);
			return c -> {
				try {
					if (optional) {
						return ((Optional<V>) method.invoke(c)).orElse(null);
					} else {
						return (V) method.invoke(c);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new RuntimeException("Could not invoke method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
				}
			};
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException("Could not find @External method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
		}
	}
	
	private <V> BiConsumer<Child<?>, V> findSetter(Class<?> nodeClass, String javaName, Class<V> paramType) {
		final String methodName = "set" + javaName;
		
		try {
			final Method method = nodeClass.getMethod(methodName, paramType);
			return (c, v) -> {
				try {
					method.invoke(c, v);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new RuntimeException("Could not invoke method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
				}
			};
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException("Could not find @External method '" + methodName + "' with param '" + paramType + "' in class '" + nodeClass.getName() + "'.");
		}
	}
	
	private TableProperty<Boolean> createBooleanProperty(String label, List<Child<?>> nodes, Function<Child<?>, Boolean> selector, BiConsumer<Child<?>, Boolean> updater) {
		return createProperty(label, nodes, TableBooleanProperty::new, selector, updater);
	}
	
	private TableProperty<String> createStringProperty(String label, List<Child<?>> nodes, Function<Child<?>, String> selector, BiConsumer<Child<?>, String> updater) {
		return createProperty(label, nodes, TableStringProperty::new, selector, updater);
	}
	
	private TableProperty<Class> createClassProperty(String label, List<Child<?>> nodes, Function<Child<?>, Class> selector, BiConsumer<Child<?>, Class> updater) {
		return createProperty(label, nodes, TableClassProperty::new, selector, updater);
	}
	
	private <V extends Enum<V>> TableProperty<V> createEnumProperty(String label, List<Child<?>> nodes, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater, V defaultValue) {
		return createProperty(label, nodes, TableEnumProperty::new, selector, updater, defaultValue);
	}
	
	private <V> TableProperty<V> createProperty(String label, List<Child<?>> nodes, BiFunction<String, V, TableProperty<V>> initiator, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater) {
		return createProperty(label, nodes, initiator, selector, updater, null);
	}
	
	private <V> TableProperty<V> createProperty(String label, List<Child<?>> nodes, BiFunction<String, V, TableProperty<V>> initiator, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater, V defaultValue) {
		V option = getOption(nodes, selector);
		
		if (option == null) {
			option = defaultValue;
		}
		
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
	
	private void animateArrow() {
		final DropShadow glow = new DropShadow();
		glow.setBlurType(BlurType.TWO_PASS_BOX);
		glow.setColor(Color.rgb(0, 255, 255, 1.0));
		glow.setWidth(20);
		glow.setHeight(20);
		glow.setRadius(0.0);
		arrow.setEffect(glow);

		final KeyFrame kf0 = new KeyFrame(ZERO, 
			new KeyValue(arrow.translateXProperty(), 55, EASE_BOTH),
			new KeyValue(arrow.translateYProperty(), -15, EASE_BOTH),
			new KeyValue(glow.radiusProperty(), 32, EASE_BOTH)
		);
		
		final KeyFrame kf1 = new KeyFrame(millis(400), 
			new KeyValue(arrow.translateXProperty(), 45, EASE_BOTH),
			new KeyValue(arrow.translateYProperty(), 5, EASE_BOTH),
			new KeyValue(glow.radiusProperty(), 0, EASE_BOTH)
		);
		
		final Timeline tl = new Timeline(kf0, kf1);
		tl.setAutoReverse(true);
		tl.setCycleCount(INDEFINITE);
		tl.play();

		final EventHandler<MouseEvent> over = ev -> {
			FadeAnimation.fadeOut(arrow, e -> arrowContainer.getChildren().remove(arrow));
		};
		
		arrow.setOnMouseEntered(over);
	}
	
	public static void showIn(Stage stage, Project project) {
		final FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/Scene.fxml"));
		final SceneController control = new SceneController(stage, project);
		loader.setController(control);

		try {
			final Parent root = (Parent) loader.load();
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