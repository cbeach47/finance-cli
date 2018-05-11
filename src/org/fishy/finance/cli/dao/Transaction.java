package org.fishy.finance.cli.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.fishy.finance.cli.utils.Utils;

public class Transaction {
	public enum ReceiptType {
		NA,
		PAPER,
		EMAIL
	}
	public enum TransType {
		DEBIT,
		CREDIT,
		TRANSFER
	}
	
	private String id;
	private Date date;
	private BigDecimal amount;
	private Account account;
	private TransType type;
	private String location;
	private ReceiptType receiptType;
	private String comments;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public String getType() {
		return type.toString();
	}
	public void setType(String type) {
		this.type = TransType.valueOf(type);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getReceiptType() {
		return receiptType.toString();
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = ReceiptType.valueOf(receiptType);
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String toString() {
		return String.format("Transaction:  id=[%s], date=[%s], "
				+ "amount=[%s], account=[%s], "
				+ "type=[%s], location=[%s], "
				+ "receiptType=[%s], comments=[%s]", id, Utils.DATE_FORMAT.format(date), amount, account, type, location, type, receiptType, comments);
	}
	
	public String toCsv() {
		return String.format("Transaction,%s,%s,%s,%s,%s,%s,%s,%s]", id, Utils.DATE_FORMAT.format(date), amount, account, type, location, type, location, receiptType, comments);
	}
	
	public static String getCsvHeader() {
		return String.format("Entity,ID,Date,Amount,Account,Type,Location,Receipt Type,Comments");
	}
	
	public TransType getTypeAsEnum() {
		return type;
	}
	
}

