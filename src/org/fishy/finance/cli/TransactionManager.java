package org.fishy.finance.cli;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.fishy.finance.cli.dao.Account;
import org.fishy.finance.cli.dao.Category;
import org.fishy.finance.cli.dao.Detail;
import org.fishy.finance.cli.dao.FinancialGroup;
import org.fishy.finance.cli.dao.Transaction;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.FinanceState;
import org.fishy.finance.cli.utils.Utils;

public class TransactionManager {
	
	public static void promptMainMenu(FinanceState state) {
		try {
			int option = -1;
			while (option != 0) {
				Utils.display("========================================");
				Utils.display("== Transaction Manager Menu           ==");
				Utils.display("========================================");
				Utils.display("Please choose an option:");
				Utils.display(" 0) Exit");
				Utils.display(" 1) Add Transaction");
				Utils.display(" 2) Add Transaction Detail");
				Utils.display(" 3) Edit Transaction");
				Utils.display(" 4) Edit Transaction Detail");
				Utils.display(" 5) View Transaction / Details");
				Utils.display(" 6) Set Current Transaction ID (%s)", state.displayCurrentTransactionId());
				Utils.display(" 7) Set Current Detail Order (%s)", state.getCurrentDetailOrder());
				Utils.display(" 8) Check Detail Amounts Against Current Transaction");
				option = Utils.promptForInt();
				switch (option) {
				case 0: {
					Utils.display("Leaving Transaction Manager.");
					break;
				}
				case 1: {
					transactionWizardFlow(state, false);
					break;
				}
				case 2: {
					if(confirmTransactionSet(state)) detailWizardFlow(state, false);
					break;
				}
				case 3: {
					if(confirmTransactionSet(state)) transactionWizardFlow(state, true);
					break;
				}
				case 4: {
					if(confirmTransactionSet(state)) detailWizardFlow(state, true);
					break;
				}
				case 5: {
					if(confirmTransactionSet(state)) viewFlow(state.getCurrentTransaction());
					break;
				}
				case 6: {
					String newTransactionString = Utils.promptForString("Transaction ID");
					Transaction newTransaction = DbUtils.getTransaction(newTransactionString);
					if(newTransaction == null) {
						Utils.display("Unable to find transaction with ID=[%s]", newTransactionString);
					} else {
						state.setCurrentTransaction(newTransaction);
					}
					break;
				}
				case 7: {
					state.setCurrentDetailOrder(Utils.promptForInt());
					break;
				}
				case 8: {
					if(confirmTransactionSet(state)) checkTransactionZeroedOut(state);
					break;
				}
				default: {
					Utils.display("Unknown option of [%d]", option);
					break;
				}
				}
				Utils.newLine();
			}
		} catch (Exception e) {
			Utils.display(e);
		}
	}
	
	private static void checkTransactionZeroedOut(FinanceState state) {
		List<Detail> dList = DbUtils.getDetailsNoTrans(state.getCurrentTransaction().getId());
		// if(t.getTypeAsEnum() == Transaction.TransType.DEBIT) {
		BigDecimal total = state.getCurrentTransaction().getAmount();
		for (Detail d : dList) {
			Utils.display("Adding the amount [%s] from detail [%s]", d.getAmount(), d.getUniqueId());
			total = total.add(d.getAmount());
		}
		if (total.compareTo(BigDecimal.ZERO) == 0) {
			Utils.display("Entire amount of transaction detailed out!");
		} else {
			Utils.display("There is still $%s left in the transaction to detail.", total.doubleValue());
		}
		// }
	}
	
	private static boolean confirmTransactionSet(FinanceState state) {
		if(state.getCurrentTransaction() == null) {
			Utils.display("No current transaction set");
			return false;
		}
		
		return true;
	}

	private static void transactionWizardFlow(FinanceState state, boolean editMode) {
		Utils.display("\n--Transaction %s Wizard--", editMode ? "Edit" : "Add");
		
		try {
			Transaction t = editMode ? state.getCurrentTransaction() : new Transaction();

			// Date
			Date priorYear = t.getDate();
			if (editMode) {
				Utils.display("Choose a new date or %s for original [%s]:", Utils.ESCAPE_CHAR, t.getDate());
			}
			String newVal = Utils.promptForString("Date (yyyy-mm-dd)");
			if (newVal.length() != 10) {
				throw new Exception("Date must be 10 characters in length!");
			}
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				t.setDate(t.getDate());
			} else {
				t.setDate(Utils.DATE_FORMAT.parse(newVal));
			}
			Utils.newLine();

			// ID (automatic)
			if (editMode) {
				if (Utils.YEAR_FORMAT.format(t.getDate()).equals(Utils.YEAR_FORMAT.format(priorYear))) {
					Utils.display("Keeping the original transaction ID: %s", t.getId());
				} else {
					throw new Exception(
							"The year of the transaction changed, which would cause the transaction ID to change!  This is not supported.");
				}
			} else {
				t.setId(DbUtils.getNextTransactionId(Utils.YEAR_FORMAT.format(t.getDate())));
				Utils.display("Using the generated transaction ID of [%s]\n", t.getId());
				if (t.getId().length() != 10) {
					throw new Exception("Error generating unique transaction ID.");
				}
			}
			Utils.newLine();

			// Amount
			if (editMode) {
				Utils.display("Choose a new amount or %s for original [%s]", Utils.ESCAPE_CHAR, t.getAmount());
			} 
			newVal = Utils.promptForString("Amount (no dollar sign)");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				t.setAmount(t.getAmount());
			} else {
				try {
					t.setAmount(new BigDecimal(newVal));
				} catch (Exception e) {
					throw new Exception(String.format("Value [%s] is not a number!", newVal));
				}
			}
			Utils.newLine();

			// Account
			if (editMode) {
				Utils.display("Choose an account - original (%s)", t.getAccount().getName());
			} 
			Account a = (Account)Utils.promptList(state.getAccountManager().list(), "account");
			if(a == null) throw new Exception("Account not chosen.");
			t.setAccount(a);
			Utils.newLine();
			
			// Type
			if (editMode) {
				Utils.display("Choose a type - original (%s)", t.getType());
			} 
			String type = Utils.promptStringArray(Utils.TRANSACTION_TYPES, "type");
			if(type == null) throw new Exception("Type not chosen.");
			t.setType(type);
			Utils.newLine();
			
			
			// Location
			if (editMode) {
				Utils.display("Choose a new location or %s for original [%s]", Utils.ESCAPE_CHAR, t.getLocation());
			} 
			newVal = Utils.promptForString("Location");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				t.setLocation(t.getLocation());
			} else {
				t.setLocation(newVal);
			}
			Utils.newLine();
			
			// Receipt Type
			if (editMode) {
				Utils.display("Choose a receipt type - original (%s)", t.getReceiptType());
			} 
			String rType = Utils.promptStringArray(Utils.RECEIPT_TYPES, "receipt type");
			if(rType == null) throw new Exception("Receipt type not chosen.");
			t.setReceiptType(rType);
			Utils.newLine();

			// Comments
			if (editMode) {
				Utils.display("Choose new comments or %s for original [%s]", Utils.ESCAPE_CHAR, t.getComments());
			} 
			newVal = Utils.promptForString("Comments");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				t.setComments(t.getComments());
			} else {
				t.setComments(newVal);
			}
			Utils.newLine();
						
			if(editMode) {
				// Display transaction / details
				Utils.display("Edit wizard complete!  Please review the staged transaction changes. ");
				viewFlow(t);
				Utils.display("Do you want to save the changes?  (Y/n)");
				String s = Utils.promptForString("");
				if(s != null && (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("Y"))) {
					if(DbUtils.update(t)) {
						Utils.display("Transaction updated.");
					} else {
						Utils.display("Transaction failed to update.");	
					}
				} else {
					Utils.display("Confirmed - cancelled edit of transaction.");
					state.setCurrentTransaction(DbUtils.getTransaction(t.getId()));
					state.setCurrentDetailOrder(0);
				}
			} else {
				Utils.display("Saving transaction:  %s", t);
				if (DbUtils.save(t))
					Utils.display("Transaction saved [%s]", t.getId());
				else
					Utils.display("Error saving transaction [%s]", t.getId());
				state.setCurrentTransaction(t);
				state.setCurrentDetailOrder(0);
			}
			
		} catch (Exception e) {
			System.out.println("Failure to correctly complete the transaction wizard due to:  " + e.getMessage());
			Utils.display(e);
		}
	}

	private static void viewFlow(Transaction t) {
		List<Detail> details = DbUtils.getDetailsNoTrans(t.getId());
	
		Utils.display("Detailing transaction [%s]:", t.getId());
		Utils.display("- Date: %s", Utils.DATE_FORMAT.format(t.getDate()));
		Utils.display("- Amount ($): %s", t.getAmount());
		Utils.display("- Type: %s", t.getType());
		Utils.display("- Account: %s", t.getAccount().getName());
		Utils.display("- Location: %s", t.getLocation());
		Utils.display("- Receipt Type: %s", t.getReceiptType());
		Utils.display("- Comments: %s", t.getComments());
		Utils.display("- # of details: %s", details.size());
		for (Detail d : details) {
			displayDetail(d);
		}
		Utils.display("----\n");
	}
	
	private static void displayDetail(Detail d) {
		Utils.display("- Detail [%s]", d.getUniqueId());
		Utils.display("--- Amount($) [%s]", d.getAmount());
		Utils.display("--- Category [%s]", d.getCategory().getName());
		Utils.display("--- Financial Group [%s]", d.getGroup().getName());
		Utils.display("--- Month Intended [%s]", d.getMonthIntended());
		Utils.display("--- Comments [%s]", d.getComments());
	}

	private static void detailWizardFlow(FinanceState state, boolean editMode) {
		Utils.display("\n--Detail %s Wizard--", editMode ? "Edit" : "Add");
		
		try {
			Detail d;
			if(!editMode) {
				d = new Detail();
				d.setTransactionId(state.getCurrentTransaction().getId());
				d.setOrder(state.getCurrentDetailOrder());
			} else {
				d = DbUtils.getDetailNoTrans(state.getCurrentTransaction().getId(), state.getCurrentDetailOrder());
				if(d == null) throw new Exception("Unable to retrieve detail to edit.");
			}

			Utils.display("\n--Detail Add Wizard [%s]--", d.getUniqueId());
		
			// Month Intended
			if (editMode) {
				Utils.display("Choose a new Month Intended or %s for original [%s]", Utils.ESCAPE_CHAR, d.getMonthIntended());
			} 
			String newVal = Utils.promptForString("Month Intended (YYYY-MM)");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				d.setMonthIntended(d.getMonthIntended());
			} else {
				d.setMonthIntended(newVal);
			}
			if (d.getMonthIntended().length() != 7) {
				throw new Exception("Month Intended must be 7 characters in length!");
			}
			Utils.newLine();
			
			// Category
			if (editMode) {
				Utils.display("Choose a category - original (%s)", d.getCategory().getName());
			} 
			Category c = (Category)Utils.promptList(state.getCategoryManager().list(), "category");
			if(c == null) throw new Exception("Category not chosen.");
			d.setCategory(c);
			Utils.newLine();

			// Financial Group
			if (editMode) {
				Utils.display("Choose a financial group - original (%s)", d.getGroup().getName());
			} 
			FinancialGroup fg = (FinancialGroup)Utils.promptList(state.getFinancialGroupManager().list(), "financial group");
			if(fg == null) throw new Exception("Financial Group not chosen.");
			d.setGroup(fg);
			Utils.newLine();

			// Amount
			if (editMode) {
				Utils.display("Choose a new Amount or %s for original [%s]", Utils.ESCAPE_CHAR, d.getAmount());
			} 
			newVal = Utils.promptForString("Amount (no dollar sign)");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				d.setAmount(d.getAmount());
			} else {
				d.setAmount(new BigDecimal(newVal));
			}
			Utils.newLine();
						
			// Comments
			if (editMode) {
				Utils.display("Choose new Comments or %s for original [%s]", Utils.ESCAPE_CHAR, d.getComments());
			} 
			newVal = Utils.promptForString("Comments");
			if (editMode && newVal.equals(Utils.ESCAPE_CHAR)) {
				d.setComments(d.getComments());
			} else {
				d.setComments(newVal);
			}
			Utils.newLine();
						
			
			if(editMode) {
				// Display transaction / details
				Utils.display("Edit wizard complete!  Please review the staged detail changes. ");
				displayDetail(d);
				Utils.display("Do you want to save the changes?  (Y/n)");
				String s = Utils.promptForString("");
				if(s != null && (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("Y"))) {
					if(DbUtils.update(d)) {
						Utils.display("Detail updated.");
					} else {
						Utils.display("Detail failed to update.");	
					}
				} else {
					Utils.display("Confirmed - cancelled edit of detail.");
				}
			} else {
				Utils.display("Saving detail:  %s", d);
				if (DbUtils.save(d)) {
					Utils.display("Detail saved [%s]", d.getUniqueId());
					state.setCurrentDetailOrder(state.getCurrentDetailOrder()+1);
				}
				else
					Utils.display("Error saving detail [%s]", d.getUniqueId());
			}
		} catch (Exception e) {
			System.out.println("Failure to correctly build the detail due to:  " + e.getMessage());
			Utils.display(e);
		}
	}
}