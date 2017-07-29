package org.lodge.finance.cli.dao;

public class Category {
	private String uuid;
	private String name;
	
	public Category(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
