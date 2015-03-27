package com.speedment.orm.gui.icons;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Emil Forslund
 */
public enum Icons {
	NEW_PROJECT ("pics/newProject.png"),
	NEW_PROJECT_24 ("pics/newProject24.png"),
	OPEN_PROJECT ("pics/openProject.png"),
	OPEN_PROJECT_24 ("pics/openProject24.png"),
	RUN_PROJECT ("pics/runProject.png"),
	RUN_PROJECT_24 ("pics/runProject24.png"),
	COLUMN ("pics/dbentity/column.png"),
	DBMS ("pics/dbentity/dbms.png"),
	FOREIGN_KEY ("pics/dbentity/foreignkey.png"),
	FOREIGN_KEY_COLUMN ("pics/dbentity/foreignkeycolumn.png"),
	INDEX ("pics/dbentity/index.png"),
	INDEX_COLUMN ("pics/dbentity/indexcolumn.png"),
	MANAGER ("pics/dbentity/manager.png"),
	PRIMARY_KEY ("pics/dbentity/primarykey.png"),
	PRIMARY_KEY_COLUMN ("pics/dbentity/primarykeycolumn.png"),
	PROJECT ("pics/dbentity/project.png"),
	PROJECT_MANAGER ("pics/dbentity/projectmanager.png"),
	SCHEMA ("pics/dbentity/schema.png"),
	TABLE ("pics/dbentity/table.png"),
	ADD_DBMS_TRANS ("pics/dialog/add_dbms_trans.png"),
	OPEN_FILE ("pics/dialog/openFile.png"),
	QUESTION ("pics/dialog/question.png"),
	SPEEDMENT_LOGO ("pics/dialog/speedment_logo100.png"),
	WALKING_MAN ("pics/dialog/walking_man.gif"),
	WALKING_MAN_SMALL ("pics/dialog/walking_man_small.png");

	private final static String FOLDER = "/";
	private final String icon;
	
	private Icons(String icon) {
		this.icon = icon;
	}
	
	public String getFileName() {
		return icon;
	}
	
	public InputStream getFileInputStream() {
		final InputStream stream = getClass().getResourceAsStream(FOLDER + icon);
		
		if (stream == null) {
			throw new RuntimeException("Could not find icon: '" + FOLDER + icon + "'.");
		}
		
		return stream;
	}
	
	public Image load() {
		return new Image(getFileInputStream());
	}
	
	public ImageView view() {
		return new ImageView(load());
	}
}
