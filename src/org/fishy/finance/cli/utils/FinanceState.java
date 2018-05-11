package org.fishy.finance.cli.utils;

import org.fishy.finance.cli.AccountManager;
import org.fishy.finance.cli.CategoryManager;
import org.fishy.finance.cli.FinancialGroupManager;
import org.fishy.finance.cli.ReportManager;
import org.fishy.finance.cli.dao.Transaction;

public class FinanceState {
	private Transaction currentTransaction;
	private int currentDetailOrder;
	private AccountManager accountManager;
	private FinancialGroupManager financialGroupManager;
	private CategoryManager categoryManager;
	private ReportManager reportManager;
	
	public Transaction getCurrentTransaction() {
		return currentTransaction;
	}
	public String displayCurrentTransactionId() {
		return currentTransaction == null ? "Not set" : currentTransaction.getId();
	}
	public void setCurrentTransaction(Transaction currentTransaction) {
		this.currentTransaction = currentTransaction;
	}
	public int getCurrentDetailOrder() {
		return currentDetailOrder;
	}
	public void setCurrentDetailOrder(int currentDetailOrder) {
		this.currentDetailOrder = currentDetailOrder;
	}
	public AccountManager getAccountManager() {
		return accountManager;
	}
	public void setManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	public FinancialGroupManager getFinancialGroupManager() {
		return financialGroupManager;
	}
	public void setManager(FinancialGroupManager financialGroupManager) {
		this.financialGroupManager = financialGroupManager;
	}
	public CategoryManager getCategoryManager() {
		return categoryManager;
	}
	public void setManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
	public ReportManager getReportManager() {
		return reportManager;
	}
	public void setManager(ReportManager reportManager) {
		this.reportManager = reportManager;
	}

}
