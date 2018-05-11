package org.fishy.finance.cli;

import java.util.List;

import org.fishy.finance.cli.dao.Transaction;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.FinanceState;
import org.fishy.finance.cli.utils.Utils;

public class ReportManager {
	
	public void promptMainMenu(FinanceState state) {
		int option = -1;
		while (option != 0) {
			Utils.display("========================================");
			Utils.display("== Report Manager Menu                ==");
			Utils.display("========================================");
			Utils.display("Please choose an option:");
			Utils.display(" 0) Exit");
			Utils.display(" 1) Display all 2018 Transactions");
			option = Utils.promptForInt();
			switch (option) {
			case 0: {
				Utils.display("Leaving Report Manager.");
				break;
			}
			case 1: {
				displayAllTransactions("2018");
				break;
			}
			default: {
				Utils.display("Unknown option of [%d]", option);
				break;
			}
			}
			Utils.newLine();
		}
	}
	
	private void displayAllTransactions(String year) {
		List<Transaction> tList = DbUtils.getTransactionsByYear(year);
		if(tList == null) {
			Utils.display("Unable to display all transactions");
			return;
		}
		Utils.display(Transaction.getCsvHeader());
		for(Transaction t : tList) {
			Utils.display(t.toCsv());
		}
	}
}