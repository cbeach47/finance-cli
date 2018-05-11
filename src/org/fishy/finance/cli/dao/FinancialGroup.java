package org.fishy.finance.cli.dao;

public class FinancialGroup extends GeneralDao {
	public FinancialGroup(String uuid, String name) {
		super(uuid, name);
	}

	@Override
	public String getShortName() {
		return name;
	}
}
