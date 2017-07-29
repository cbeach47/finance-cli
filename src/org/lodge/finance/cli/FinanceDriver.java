package org.lodge.finance.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.lodge.finance.cli.dao.Account;
import org.lodge.finance.cli.dao.Category;
import org.lodge.finance.cli.dao.Detail;
import org.lodge.finance.cli.dao.FinancialGroup;
import org.lodge.finance.cli.dao.Transaction;
import org.lodge.finance.cli.utils.Configs;
import org.lodge.finance.cli.utils.DbUtils;

public class FinanceDriver {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
	
	public static void main(String[] args) {
		if((args.length < 1) || (!new File(args[0]).exists())) {
			System.out.println("Please provide a configuration file.  Goodbye.");
			return;
		}
		Configs config = new Configs(args[0]);
		String currentTransId = "ERROR NOT SET";
		int detailCounter = -1;
		try {
			outfln("Welcome to the Lodge Financial Engine.");
			DbUtils.initConnection(config);
			int option = -1;
			while(option != 0) {
				outfln("========================================");
				outfln("Please choose an option:");
				outfln("0) Exit");
				outfln("1) Add Transaction");
				outfln("2) Add Transaction Details");
				outfln("3) Add Account");
				outfln("4) Add Financial Group");
				outfln("5) Add Category");
				outfln("6) Set Current Transaction ID");
				outfln("7) Set Current Detail Order");
				outfln("8) Check Detail Amounts Against Current Transaction");
				option = promptForInt();
				switch(option) {
				case 0: {
					outfln("Goodbye.");
					break;
				}
				case 1: {
					Transaction t = promptTransaction();
					if(t != null) {
						outfln("Saving transaction:  %s", t);
						if(DbUtils.save(t)) outfln("Transaction saved [%s]", t.getId());
						else outfln("Error saving transaction [%s]", t.getId());
						detailCounter = 0;
						currentTransId = t.getId();
					}
					break;
				}
				case 2: {
					Detail d = promptDetail(currentTransId, detailCounter);
					if(d != null) {
						outfln("Saving detail:  %s", d);
						if(DbUtils.save(d)) {
							outfln("Detail saved [%s]", d.getUniqueId());
							detailCounter++;
						}
						else outfln("Error saving detail [%s]", d.getUniqueId());
					}
					break;
				}
				case 3: {
					Account a = promptAccount();
					if(a != null) {
						outfln("Saving account:  %s", a);
						if(DbUtils.save(a)) outfln("Account saved [%s]", a);
						else outfln("Error saving account [%s]", a);
					}
					break;
				}
				case 4: {
					FinancialGroup g = promptGroup();
					if(g != null) {
						outfln("Saving financial group %s", g);
						if(DbUtils.save(g)) outfln("Financial Group saved [%s]", g);
						else outfln("Error saving financial group [%s]", g);
					}
					break;
				}
				case 5: {
					Category c = promptCategory();
					if(c != null) {
						outfln("Saving category %s", c);
						if(DbUtils.save(c)) outfln("Category saved [%s]", c);
						else outfln("Error saving category [%s]", c);
					}
					break;
				}
				case 6: {
					currentTransId = promptForString("Trans ID");
					break;
				}
				case 7: {
					detailCounter = promptForInt();
					break;
				}
				case 8: {
					Transaction t = DbUtils.getTransaction(currentTransId);
					if(t == null) {
						outfln("Oops!  No transaction found with id=[%s]", currentTransId);
					}
					List<Detail> dList = DbUtils.getDetailsNoTrans(currentTransId);
					//if(t.getTypeAsEnum() == Transaction.TransType.DEBIT) {
						BigDecimal total = t.getAmount();
						for(Detail d : dList) {
							outfln("Adding the amount [%s] from detail [%s]", d.getAmount(), d.getUniqueId());
							total = total.add(d.getAmount());
						}
						if(total.compareTo(BigDecimal.ZERO) == 0) {
							outfln("Entire amount of transaction detailed out!");
						} else {
							outfln("There is still $%s left in the transaction to detail.", total.doubleValue());
						}
					//}
					break;
				}
				default: {
					outfln("Unknown option of [%d]", option);
					break;
				}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error talking to the DB.  MSG="+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
	private static Transaction promptTransaction() {
		outfln("\n--Transaction Add Wizard--");
		try {
			Transaction t = new Transaction();
			t.setDate(DATE_FORMAT.parse(promptForString("Date (yyyy-mm-dd)")));
			t.setId(DbUtils.getNextTransactionId(YEAR_FORMAT.format(t.getDate())));
			System.out.printf("Using the generated transaction ID of [%s]\n", t.getId());
			if(t.getId().length() != 10) {
				throw new Exception("Error generating unique transaction ID.");
			}t.setAmount(new BigDecimal(promptForString("Amount (no dollar sign)")));
			t.setAccount(promptAccounts());
			t.setType(promptTransType());
			t.setLocation(promptForString("Location"));
			t.setReceiptType(promptReceiptType());
			t.setComments(promptForString("Comments"));
			return t;
		} catch (Exception e) {
			System.out.println("Failure to correctly build the transaction due to:  " + e.getMessage());
			return null;
		}
	}
	
	private static Account promptAccount() {
		outfln("\n--Account Add Wizard--");
		try {
			return new Account(UUID.randomUUID().toString(), promptForString("Account name").toUpperCase());
		} catch (Exception e) {
			System.out.println("Failure to correctly build the account due to:  " + e.getMessage());
			return null;
		}
	}
	
	private static FinancialGroup promptGroup() {
		outfln("\n--Financial Group Add Wizard--");
		try {
			return new FinancialGroup(UUID.randomUUID().toString(), promptForString("Group name").toUpperCase());
		} catch (Exception e) {
			System.out.println("Failure to correctly build the group due to:  " + e.getMessage());
			return null;
		}
	}

	
	private static Category promptCategory() {
		outfln("\n--Category Add Wizard--");
		try {
			return new Category(UUID.randomUUID().toString(), promptForString("Category name").toUpperCase());
		} catch (Exception e) {
			System.out.println("Failure to correctly build the category due to:  " + e.getMessage());
			return null;
		}
	}

	private static Detail promptDetail(String transId, int order) {
		Detail d = new Detail();
		d.setTransactionId(transId);
		d.setOrder(order);
		outfln("\n--Detail Add Wizard [%s]--", d.getUniqueId());
		try {
			d.setMonthIntended(promptForString("Month Intended (YYYY-MM)"));
			if(d.getMonthIntended().length() != 7) {
				throw new Exception("Month Intended must be 7 characters in length!");
			}
			d.setCategory(promptCategories());
			d.setGroup(promptGroups());
			d.setAmount(new BigDecimal(promptForString("Amount (no dollar sign)")));
			d.setComments(promptForString("Comments"));
			return d;
		} catch (Exception e) {
			System.out.println("Failure to correctly build the detail due to:  " + e.getMessage());
			return null;
		}
	}
	
	private static Account promptAccounts() {
		outfln("Choose an available account:");
		List<Account> accounts = DbUtils.getAccounts();
		for(int i = 0; i < accounts.size(); i++) {
			outfln("[%s] - %s", ""+i, accounts.get(i));
		}
		Account a = accounts.get(promptForInt());
		outfln("Confirmed [%s].", a.getName());
		return a;
	}
	
	private static FinancialGroup promptGroups() {
		outfln("Choose an available group:");
		List<FinancialGroup> groups = DbUtils.getGroups();
		for(int i = 0; i < groups.size(); i++) {
			outfln("[%s] - %s", ""+i, groups.get(i));
		}
		FinancialGroup g = groups.get(promptForInt());
		outfln("Confirmed [%s].", g.getName());
		return g;
	}
	
	private static Category promptCategories() {
		outfln("Choose an available category:");
		List<Category> cats = DbUtils.getCategories();
		for(int i = 0; i < cats.size(); i++) {
			outfln("[%s] - %s", ""+i, cats.get(i));
		}
		Category c = cats.get(promptForInt());
		outfln("Confirmed [%s].", c.getName());
		return c;
	}
	
	private static String promptTransType() {
		outfln("Choose an available type:");
		String[] types = {"DEBIT", "CREDIT", "TRANSFER"};
		for(int i = 0; i < types.length; i++) {
			outfln("[%s] - %s", ""+i, types[i]);
		}
		String ret = types[promptForInt()];
		outfln("Confirmed [%s].", ret);
		return ret;
	}
	
	private static String promptReceiptType() {
		outfln("Choose an available receipt type:");
		String[] types = {"NA", "PAPER", "EMAIL"};
		for(int i = 0; i < types.length; i++) {
			outfln("[%s] - %s", ""+i, types[i]);
		}
		return types[promptForInt()];
	}
	
	private static int promptForInt() {
		try {
			System.out.print("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try{
			    return Integer.parseInt(br.readLine());
			}catch(NumberFormatException nfe){
			    System.err.println("Sorry, not a number!");
			}
		} catch (IOException e) {
			System.err.println("Error getting input...");
		}
		return -1;
	}
	
	private static String promptForString(String s) {
		String ret = "";
		outfln("Enter value for %s:", s);
		try {
			System.out.print("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ret = br.readLine();
		} catch (IOException e) {
			System.err.println("Error getting input...");
		}
		outfln("Confirmed for [%s]", ret);
		return ret;
	}
	
	private static void outfln(String s, Object... args) {
		System.out.println(String.format(s, args));
	}
	
}
