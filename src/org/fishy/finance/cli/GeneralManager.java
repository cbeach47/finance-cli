package org.fishy.finance.cli;

import java.util.List;

import org.fishy.finance.cli.dao.GeneralDao;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.FinanceState;
import org.fishy.finance.cli.utils.Utils;

public abstract class GeneralManager {
	public abstract String getNameLC();
	public abstract String getNameUC();
	public abstract String getNameUCP();
	
	protected abstract GeneralDao wizard(GeneralDao original) throws Exception;
	protected abstract List<GeneralDao> list();
	
	public void promptMainMenu(FinanceState state) {
		int option = -1;
		while (option != 0) {
			Utils.display("====================================");
			Utils.display("== %s Manager Menu           ", getNameUC());
			Utils.display("====================================");
			Utils.display("Please choose an option:");
			Utils.display(" 0) Exit");
			Utils.display(" 1) Add %s", getNameUC());
			Utils.display(" 2) Edit %s", getNameUC());
			Utils.display(" 3) View %s", getNameUCP());
			option = Utils.promptForInt();
			switch (option) {
			case 0: {
				Utils.display("Leaving %s Manager.", getNameUC());
				Utils.newLine();
				break;
			}
			case 1: {
				addFlow();
				break;
			}
			case 2: {
				editFlow();
				break;
			}
			case 3: {
				Utils.displayList(list());
				Utils.newLine();
				break;
			}
			default: {
				Utils.display("Unknown option of [%d]", option);
				Utils.newLine();
				break;
			}
			}
		}
	}
	
	private void addFlow() {
		Utils.display("\n--%s Add Wizard--", getNameUC());
		
		try {
			GeneralDao dao = wizard(null);
			if (dao != null) {
				Utils.display("Saving %s:  %s", getNameLC(), dao);
				if (DbUtils.save(dao))
					Utils.display("%s saved [%s]", getNameUC(), dao);
				else
					Utils.display("Error saving %s [%s]", getNameLC(), dao);
			}
		} catch (Exception e) {
			Utils.display("Failure to correctly save the %s due to: %s", getNameLC(), e.getMessage());
		}
	}
	
	private void editFlow() {
		Utils.display("\n--%s Edit Wizard--", getNameUC());
		
		try {
			GeneralDao orig = chooseEntityFlow();
			if(orig == null) throw new Exception("Unable to select the "+ getNameLC() + " to edit.");
			GeneralDao dao = wizard(orig);
			if (dao != null) {
				Utils.display("Edit wizard complete!  Please review the staged changes. ");
				Utils.display(dao.toString());
				String s = Utils.promptForStringGeneral("Do you want to save the changes?  (Y/n):");
				if(s != null && (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("Y"))) {
					if(DbUtils.update(dao)) {
						Utils.display("%s updated.", getNameUC());
					} else {
						Utils.display("%s failed to update.", getNameUC());	
					}
				} else {
					Utils.display("Confirmed - cancelled edit of %s.", getNameLC());
				}
			}
		} catch (Exception e) {
			Utils.display("Failure to correctly update the %s due to: %s", getNameLC(), e.getMessage());
		}
	}
	
	public GeneralDao chooseEntityFlow() {
		Utils.display("Choose an available %s:", getNameLC());
		List<GeneralDao> daos = list();
		for (int i = 0; i < daos.size(); i++) {
			Utils.display("[%s] - %s", "" + i, daos.get(i));
		}
		GeneralDao dao = daos.get(Utils.promptForInt());
		Utils.display("Confirmed choice [%s].", dao.getShortName());
		return dao;
	}
}
