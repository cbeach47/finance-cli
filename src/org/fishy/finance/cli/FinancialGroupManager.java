package org.fishy.finance.cli;

import java.util.List;
import java.util.UUID;

import org.fishy.finance.cli.dao.FinancialGroup;
import org.fishy.finance.cli.dao.GeneralDao;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.Utils;

public class FinancialGroupManager  extends GeneralManager{
	
	protected GeneralDao wizard(GeneralDao original) throws Exception {
		//If original if null, this is a 'create' wizard, else it's an 'edit' wizard
		if(original == null) {
			return new FinancialGroup(UUID.randomUUID().toString(), Utils.promptForString("Group name").toUpperCase());
		} else {
			FinancialGroup fg = (FinancialGroup) original;
			String newVal =  Utils.promptForStringGeneral("Choose a new group name or %s for original [%s]:", Utils.ESCAPE_CHAR, original.getShortName()).toUpperCase();
			if (!newVal.equals(Utils.ESCAPE_CHAR)) {
				fg.setName(newVal);
			} else {
				// No change
			}
			return fg;
		}
	}
	
	public List<GeneralDao> list() {
		return DbUtils.getGroups();
	}
	
	public String getNameLC() {
		return "financial group";
	}
	
	public String getNameUC() {
		return "Financial Group";
	}
	
	public String getNameUCP() {
		return "Financial Groups";
	}
}