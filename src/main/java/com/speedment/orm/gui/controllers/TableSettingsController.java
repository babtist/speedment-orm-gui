package com.speedment.orm.gui.controllers;

import com.speedment.orm.config.model.Schema;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class TableSettingsController implements Initializable {
	
	private final Schema table;
	
	@FXML private Label headerText;
	
	public TableSettingsController(Schema table) {
		this.table = table;
	}
	
	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		headerText.setText(table.getName());
	}
}