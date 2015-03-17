package com.speedment.orm.gui.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public class FilePickerController implements Initializable {
	
	@FXML private ComboBox picker;

	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("Set showing.");
		
		picker.setOnShown(l -> {
			System.out.println("Showing");
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select generation target.");
			fileChooser.setInitialDirectory(
				new File(System.getProperty("user.home"))
			);
			
			final File file = fileChooser.showSaveDialog(picker.getScene().getWindow());
			
			if (file != null) {
				picker.setValue(file);
			}
			
			picker.hide();
		});
	}
}