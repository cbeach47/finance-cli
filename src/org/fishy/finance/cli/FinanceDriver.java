package org.fishy.finance.cli;

import java.io.File;
import java.sql.SQLException;

import org.fishy.finance.cli.utils.Configs;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.FinanceState;
import org.fishy.finance.cli.utils.Utils;

public class FinanceDriver {
	
	public static void main(String[] args) {
		if ((args.length < 1) || (!new File(args[0]).exists())) {
			System.out.println("Please provide a configuration file.  Goodbye.");
			return;
		}
		Configs config = new Configs(args[0]);
		FinanceState state = new FinanceState();
		AccountManager am = new AccountManager();
		FinancialGroupManager fgm = new FinancialGroupManager();
		CategoryManager cm = new CategoryManager();
		ReportManager rm = new ReportManager();
		state.setManager(am);
		state.setManager(fgm);
		state.setManager(cm);
		state.setManager(rm);
		
		try {
			Utils.display("Welcome to a better way to track your finances.");
			Utils.newLine();
			DbUtils.initConnection(config);
			int option = -1;
			while (option != 0) {
				Utils.display("========================================");
				Utils.display("== Main Menu                          ==");
				Utils.display("========================================");
				Utils.display("Please choose an option:");
				Utils.display(" 0) Exit");
				Utils.display(" 1) Transaction Manager");
				Utils.display(" 2) Account Manager");
				Utils.display(" 3) Financial Group Manager");
				Utils.display(" 4) Category Manager");
				Utils.display(" 5) Report Manager");
				Utils.display(" 6) Toggle Debug Logging Current=(%s)", Utils.isDebug());
				option = Utils.promptForInt();
				switch (option) {
				case 0: {
					Utils.display("Goodbye.");
					break;
				}
				case 1: {
					TransactionManager.promptMainMenu(state);
					break;
				}
				case 2: {
					am.promptMainMenu(state);
					break;
				}
				case 3: {
					fgm.promptMainMenu(state);
					break;
				}
				case 4: {
					cm.promptMainMenu(state);
					break;
				}
				case 5: {
					rm.promptMainMenu(state);
					break;
				}
				case 6: {
					Utils.setDebug(!Utils.isDebug());
					break;
				}
				default: {
					Utils.display("Unknown option of [%d]", option);
					break;
				}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error talking to the DB.  MSG=" + e.getMessage());
			e.printStackTrace();
		}
	}
}
