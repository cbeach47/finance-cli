package org.fishy.finance.cli;

import java.util.List;
import java.util.UUID;

import org.fishy.finance.cli.dao.Category;
import org.fishy.finance.cli.dao.GeneralDao;
import org.fishy.finance.cli.utils.DbUtils;
import org.fishy.finance.cli.utils.Utils;

public class CategoryManager extends GeneralManager{
	
	protected GeneralDao wizard(GeneralDao original) throws Exception {
		//If original if null, this is a 'create' wizard, else it's an 'edit' wizard
		if(original == null) {
			return new Category(UUID.randomUUID().toString(), Utils.promptForString("Category name").toUpperCase());
		} else {
			Category c = (Category) original;
			String newVal =  Utils.promptForStringGeneral("Choose a new category name or %s for original [%s]:", Utils.ESCAPE_CHAR, original.getShortName()).toUpperCase();
			if (!newVal.equals(Utils.ESCAPE_CHAR)) {
				c.setName(newVal);
			} else {
				// No change
			}
			return c;
		}	
	}
	
	public List<GeneralDao> list() {
		return DbUtils.getCategories();
	}
	
	public String getNameLC() {
		return "category";
	}
	
	public String getNameUC() {
		return "Category";
	}
	
	public String getNameUCP() {
		return "Categories";
	}
}