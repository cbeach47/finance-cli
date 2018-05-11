package org.fishy.finance.cli.dao;

public class Category extends GeneralDao {
	public Category(String uuid, String name) {
		super(uuid, name);
	}

	@Override
	public String getShortName() {
		return name;
	}
}
