package org.fishy.finance.cli;

import java.util.List;
import java.util.UUID;

import org.fishy.finance.cli.dao.Account;
import org.fishy.finance.cli.dao.GeneralDao;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.Utils;

public class AccountManager extends GeneralManager{
	
	protected GeneralDao wizard(GeneralDao original) throws Exception {
		//If original if null, this is a 'create' wizard, else it's an 'edit' wizard
		if(original == null) {
			return new Account(UUID.randomUUID().toString(), Utils.promptForString("Account name").toUpperCase());
		} else {
			Account a = (Account) original;
			String newVal =  Utils.promptForStringGeneral("Choose a new account name or %s for original [%s]:", Utils.ESCAPE_CHAR, original.getShortName()).toUpperCase();
			if (!newVal.equals(Utils.ESCAPE_CHAR)) {
				a.setName(newVal);
			} else {
				// No change
			}
			return a;
		}	
	}
	
	public List<GeneralDao> list() {
		return DbUtils.getAccounts();
	}
	
	public String getNameLC() {
		return "account";
	}
	
	public String getNameUC() {
		return "Account";
	}
	
	public String getNameUCP() {
		return "Accounts";
	}
}