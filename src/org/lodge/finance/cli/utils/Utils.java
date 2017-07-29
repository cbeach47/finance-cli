package org.lodge.finance.cli.utils;

public class Utils {
	public static String pad(String org, char padding, int desiredLength) {
		StringBuilder toRet = new StringBuilder();
		toRet.append(org);
		while(toRet.length() < desiredLength) {
			toRet.insert(0, padding);
		}
		return toRet.toString();
	}
}
