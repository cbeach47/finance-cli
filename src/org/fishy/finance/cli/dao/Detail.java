package org.fishy.finance.cli.dao;

import java.math.BigDecimal;

public class Detail {
	private String transactionId;
	private int order;
	private String monthIntended;
	private BigDecimal amount;
	private FinancialGroup group;
	private Category category;
	private String comments;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getMonthIntended() {
		return monthIntended;
	}
	public void setMonthIntended(String monthIntended) {
		this.monthIntended = monthIntended;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public FinancialGroup getGroup() {
		return group;
	}
	public void setGroup(FinancialGroup group) {
		this.group = group;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getUniqueId() {
		return String.format("%s-%d", transactionId, order);
	}
	
	public String toString() {
		return String.format("TransDetail: TransId-Order=[%s,%d], monthIntended=[%s], "
				+ "amount=[%s], Group=[%s], Category=[%s], comments=[%s]", transactionId, order, monthIntended,
				amount.toString(), group.toString(), category.toString(), comments);
	}
}
