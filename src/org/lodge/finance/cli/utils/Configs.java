package org.lodge.finance.cli.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
	public static final String DB_URL = "db.url";
	public static final String DB_NAME = "db.name";
	public static final String DB_USERNAME = "db.username";
	public static final String DB_PASSWORD = "db.password";
	private Properties store;
	
	public Configs(String path) {
		try (InputStream input = new FileInputStream(path)) {
			System.out.printf("Using [%s] as the configuration file.\n", path);
			store = new Properties();
			store.load(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public String get(String key) {
		if(store.containsKey(key)) {
			return store.getProperty(key);
		} else {
			return String.format("PROP_NOT_FOUND: [%s]", key);
		}
	}
}
