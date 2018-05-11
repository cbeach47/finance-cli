package org.fishy.finance.cli.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

import org.fishy.finance.cli.dao.GeneralDao;

public class Utils {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
	public static final String ESCAPE_CHAR = "~";
	public static final int ESCAPE_INDEX = -1;
	
	public static final String[] TRANSACTION_TYPES = { "DEBIT", "CREDIT", "TRANSFER" };
	public static final String[] RECEIPT_TYPES = { "NA", "PAPER", "EMAIL" };
	
	private static boolean debug = false;
	
	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Utils.debug = debug;
	}

	public static String pad(String org, char padding, int desiredLength) {
		StringBuilder toRet = new StringBuilder();
		toRet.append(org);
		while(toRet.length() < desiredLength) {
			toRet.insert(0, padding);
		}
		return toRet.toString();
	}
	
	public static void displayList(List<GeneralDao> list) {
		for (int i = 0; i < list.size(); i++) {
			Utils.display("[%s] - %s", "" + i, list.get(i));
		}
	}
	
	public static GeneralDao promptList(List<GeneralDao> list, String noun) {
		Utils.display("Choose a %s:", noun);
		displayList(list);
		int index = Utils.promptForInt();
		if(index == Utils.ESCAPE_INDEX) {
			return null;
		} else {
			GeneralDao ret = list.get(index);
			Utils.display("Confirmed [%s].", ret.getShortName());
			return ret;
		}
	}
	
	public static String promptStringArray(String[] list, String noun) {
		Utils.display("Choose a %s:", noun);
		for (int i = 0; i < list.length; i++) {
			Utils.display("[%s] - %s", "" + i, list[i]);
		}
		int index = Utils.promptForInt();
		if(index == Utils.ESCAPE_INDEX) {
			return null;
		} else {
			String ret = list[index];
			Utils.display("Confirmed [%s].", ret);
			return ret;
		}
	}

	public static int promptForInt() {
		return promptForInt(false);
	}
	/**
	 * 
	 * @param escape
	 * @return the value or -1 for the escape char.
	 */
	public static int promptForInt(boolean escape) {
		try {
			System.out.print("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				String val = br.readLine();
				if(escape && val.equals(ESCAPE_CHAR)) {
						return ESCAPE_INDEX;
				} else {
					return Integer.parseInt(val);
				}
			} catch (NumberFormatException nfe) {
				display("Sorry, not a number!");
			}
		} catch (IOException e) {
			display("Error getting input...");
		}
		return ESCAPE_INDEX;
	}
	
	public static String readPrompt() {
		try {
			display("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		} catch (IOException e) {
			System.err.println("Error getting input...");
		}
		return null;
	}

	public static String promptForStringGeneral(String s, Object... args) {
		String ret = "";
		display(s, args);
		try {
			//display("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ret = br.readLine();
		} catch (IOException e) {
			System.err.println("Error getting input...");
		}
		display("Confirmed for [%s]", ret);
		return ret;
	}
	
	public static String promptForString(String s) {
		String ret = "";
		display("Enter value for %s:", s);
		try {
			//display("Enter choice: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ret = br.readLine();
		} catch (IOException e) {
			System.err.println("Error getting input...");
		}
		display("Confirmed for [%s]", ret);
		return ret;
	}

	public static void display(String s, Object... args) {
		System.out.println(String.format(s, args));
	}
	
	public static void display(Exception e) {
		if(debug) {
			e.printStackTrace();
		}
	}
	
	public static void newLine() {
		System.out.println();
	}
}
