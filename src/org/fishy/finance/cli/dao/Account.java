package org.fishy.finance.cli.dao;

public class Account extends GeneralDao {
	public Account(String uuid, String name) {
		super(uuid, name);
	}

	@Override
	public String getShortName() {
		return name;
	}
}
