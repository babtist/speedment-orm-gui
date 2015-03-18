package com.speedment.orm.gui.controllers;

import com.speedment.orm.config.model.Column;
import com.speedment.orm.config.model.Dbms;
import com.speedment.orm.config.model.Project;
import com.speedment.orm.config.model.Schema;
import com.speedment.orm.config.model.Table;
import com.speedment.orm.config.model.parameters.FieldStorageType;
import com.speedment.orm.config.model.parameters.StandardDbmsType;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class ProjectPromptController implements Initializable {

	@FXML private Button buttonOpen;
	@FXML private TextField fieldHost;
	@FXML private TextField fieldPort;
	@FXML private ChoiceBox<String> fieldType;
	@FXML private TextField fieldName;
	@FXML private TextField fieldUser;
	@FXML private PasswordField fieldPass;
	@FXML private Button buttonConnect;
	
	private final Stage stage;
	
	public ProjectPromptController(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		fieldType.setItems(
			Stream.of(StandardDbmsType.values())
				.map(s -> s.getName())
				.collect(Collectors.toCollection(FXCollections::observableArrayList))
		);
		
		fieldType.getSelectionModel().selectedItemProperty().addListener((observable, old, next) -> {
			if (!observable.getValue().isEmpty()) {
				final StandardDbmsType item = StandardDbmsType.valueOf(next.toUpperCase());

				if (fieldHost.textProperty().getValue().isEmpty()) {
					fieldHost.textProperty().setValue("127.0.0.1");
				}
				
				if (fieldUser.textProperty().getValue().isEmpty()) {
					fieldUser.textProperty().setValue("root");
				}

				fieldPort.textProperty().setValue("" + item.getDefaultPort());
			}
		});
		
		fieldHost.textProperty().addListener((ov, o, n) -> toggleConnectButton());
		fieldPort.textProperty().addListener((ov, o, n) -> toggleConnectButton());
		fieldType.selectionModelProperty().addListener((ov, o, n) -> toggleConnectButton());
		fieldName.textProperty().addListener((ov, o, n) -> toggleConnectButton());
		fieldUser.textProperty().addListener((ov, o, n) -> toggleConnectButton());
		
		buttonConnect.setOnAction(ev -> {
			Project project = Project.newProject();
			project.setName("Hello Speedment");
			project.setPacketName("org.speedment.test");
			
			Dbms dbms = Dbms.newDbms();
			dbms.setIpAddress(fieldHost.getText());
			dbms.setPort(Integer.parseInt(fieldPort.getText()));
			dbms.setName(fieldName.getText());
			dbms.setUsername(fieldUser.getText());
			dbms.setPassword(fieldPass.getText());
			dbms.setType(fieldType.getSelectionModel().getSelectedItem());
			project.add(dbms);
			
			Schema schema = Schema.newSchema();
			schema.setName("SiSP");
			dbms.add(schema);
			
			Table tableA = Table.newTable();
			tableA.setName("User");
			schema.add(tableA);
			
			Column columnA1 = Column.newColumn();
			columnA1.setName("id");
			tableA.add(columnA1);
			
			Column columnA2 = Column.newColumn();
			columnA2.setName("firstname");
			tableA.add(columnA2);
			
			Column columnA3 = Column.newColumn();
			columnA3.setName("lastname");
			tableA.add(columnA3);
			
			Table tableB = Table.newTable();
			tableB.setName("Topic");
			schema.add(tableB);
			
			Column columnB1 = Column.newColumn();
			columnB1.setName("id");
			tableB.add(columnB1);
			
			Column columnB2 = Column.newColumn();
			columnB2.setName("header");
			tableB.add(columnB2);
			
			Column columnB3 = Column.newColumn();
			columnB3.setName("content");
			tableB.add(columnB3);
			
			Column columnB4 = Column.newColumn();
			columnB4.setName("uploaded");
			tableB.add(columnB4);
			

			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
			final SceneController control = new SceneController(project);
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
		});
	}
	
	private void toggleConnectButton() {
		buttonConnect.setDisable(
			fieldHost.textProperty().getValue().isEmpty() ||
			fieldPort.textProperty().getValue().isEmpty() ||
			fieldType.getSelectionModel().isEmpty() ||
			fieldName.textProperty().getValue().isEmpty() ||
			fieldUser.textProperty().getValue().isEmpty()
		);
	}
}