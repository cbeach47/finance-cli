package org.fishy.finance.cli.dao;

public abstract class GeneralDao {
	protected String uuid;
	protected String name;
	
	public abstract String getShortName();
	
	public GeneralDao(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	

}
