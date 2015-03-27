package com.speedment.orm.gui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Singleton.
 * 
 * @author Emil Forslund
 */
public final class Settings {

	private final static File SETTINGS_FILE = new File("settings.properties");
	private final static URL SYNC_URL;
	private final static boolean SYNC = false;
	
	static {
		final String url = "https://www.speedment.org/community/";
		try {
			SYNC_URL = new URL(url);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Sync URL is mailformed: '" + url + "'.");
		}
	}
	
	private final Properties props;
	
	private Settings() {
		props = new Properties();
		
		if (!SETTINGS_FILE.exists()) {
			try {
				SETTINGS_FILE.createNewFile();
			} catch (IOException ex) {
				throw new RuntimeException(
					"Could not create file '" + filename() + "'."
				);
			}
		}

		try (final InputStream is = new FileInputStream(SETTINGS_FILE)) {
			props.load(is);
		} catch (Exception e) {
			throw new RuntimeException(
				"Could not find file '" + filename() + "'."
			);
		}
	}
	
	public boolean has(String key) {
		return props.containsKey(key);
	}
	
	public <T> void set(String key, T value) {
		props.setProperty(key, value.toString());
		storeChanges();
	}
	
	public String get(String key) {
		return props.getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	
	public Boolean get(String key, Boolean defaultValue) {
		return Boolean.parseBoolean(props.getProperty(key, Boolean.toString(defaultValue)));
	}
	
	public Integer get(String key, Integer defaultValue) {
		return Integer.parseInt(props.getProperty(key, Integer.toString(defaultValue)));
	}
	
	private String encode() {
		return props.entrySet().stream()
			.map(e -> {
				try {
					return URLEncoder.encode(e.getKey().toString(), "UTF-8") + "=" + 
						   URLEncoder.encode(e.getValue().toString(), "UTF-8");
				} catch (UnsupportedEncodingException ex) {
					throw new RuntimeException("Encoding 'UTF-8' is not supported.");
				}
			}).collect(Collectors.joining("&"));
	}
	
	private void storeChanges() {
		try (final OutputStream out = new FileOutputStream(SETTINGS_FILE, false)) {
			props.store(out, "Speedment ORM Settings");
		} catch (IOException ex) {
			throw new RuntimeException(
				"Could not save file '" + filename() + "'."
			);
		}
		
		if (SYNC) {
			try {
				final HttpURLConnection con = (HttpURLConnection) SYNC_URL.openConnection();
				con.setRequestMethod("POST");
				
				try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
					wr.writeBytes(encode());
					wr.flush();
				}
			} catch (IOException ex) {
				throw new RuntimeException(
					"Could not sync to url '" + SYNC_URL.toString() + "'."
				);
			}
		}
	}
	
	private static String filename() {
		return SETTINGS_FILE.getAbsolutePath();
	}
	
	private static class Holder {
		private final static Settings INSTANCE = new Settings();
	}
	
	public static Settings inst() {
		return Holder.INSTANCE;
	}
}