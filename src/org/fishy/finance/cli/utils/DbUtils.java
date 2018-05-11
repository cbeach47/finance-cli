package org.fishy.finance.cli.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.fishy.finance.cli.dao.Account;
import org.fishy.finance.cli.dao.Category;
import org.fishy.finance.cli.dao.Detail;
import org.fishy.finance.cli.dao.FinancialGroup;
import org.fishy.finance.cli.dao.GeneralDao;
import org.fishy.finance.cli.dao.Transaction;

public class DbUtils {
	private static Connection conn = null;

	public static void initConnection(Configs c) throws SQLException {
		String url = String.format("jdbc:postgresql://%s/%s?user=%s&password=%s&ssl=false", 
				c.get(Configs.DB_URL),
				c.get(Configs.DB_NAME),
				c.get(Configs.DB_USERNAME),
				c.get(Configs.DB_PASSWORD));
		conn = DriverManager.getConnection(url);
		System.out.printf("Connected to the [%s] database.\n", c.get(Configs.DB_NAME));
	}
	
	public static boolean save(GeneralDao dao) throws Exception {
		if(dao instanceof Account) return save((Account) dao);
		else if(dao instanceof FinancialGroup) return save((FinancialGroup) dao);
		else if(dao instanceof Category) return save((Category) dao);
		else throw new Exception("Unknown type of DAO to save: " + dao.getClass().getName());
	}

	public static boolean update(GeneralDao dao) throws Exception {
		if(dao instanceof Account) return update((Account) dao);
		else if(dao instanceof FinancialGroup) return update((FinancialGroup) dao);
		else if(dao instanceof Category) return update((Category) dao);
		else throw new Exception("Unknown type of DAO to save: " + dao.getClass().getName());
	}

	public static boolean update(Account a) {
		String sql = "update ACCOUNT set NAME = ? where UUID = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, a.getName());
			st.setString(2, a.getUuid());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error updating the account ["+a+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean update(FinancialGroup fg) {
		String sql = "update FINANCIAL_GROUP set NAME = ? where UUID = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, fg.getName());
			st.setString(2, fg.getUuid());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error updating the financial group ["+fg+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean update(Category c) {
		String sql = "update CATEGORY set NAME = ? where UUID = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, c.getName());
			st.setString(2, c.getUuid());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error updating the category ["+c+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean save(Account a) {
		String sql = "insert into ACCOUNT (UUID, NAME) values (?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, a.getUuid());
			st.setString(2, a.getName());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the account ["+a+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	

	public static boolean save(FinancialGroup g) {
		String sql = "insert into financial_group (UUID, NAME) values (?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, g.getUuid());
			st.setString(2, g.getName());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the financial group ["+g+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	

	public static boolean save(Category c) {
		String sql = "insert into category (UUID, NAME) values (?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, c.getUuid());
			st.setString(2, c.getName());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the category ["+c+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean save(Transaction t) {
		String sql = "insert into TRANSACTION (id, date, amount, type, account_id, location, receipt, comments) "
				+ "values (?,?,?,?,?,?,?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, t.getId());
			st.setDate(2, new Date(t.getDate().getTime()));
			st.setDouble(3, t.getAmount().doubleValue());
			st.setString(4, t.getType());
			st.setString(5, t.getAccount().getUuid());
			st.setString(6, t.getLocation());
			st.setString(7, t.getReceiptType());
			st.setString(8, t.getComments());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the transaction ["+t+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean save(Detail d) {
		String sql = "insert into detail (trans_id, detail_order, month_intended, "
				+ "amount, financial_group_id, category_id, comments) "
				+ "values (?,?,?,?,?,?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, d.getTransactionId());
			st.setInt(2, d.getOrder());
			st.setString(3, d.getMonthIntended());
			st.setDouble(4, d.getAmount().doubleValue());
			st.setString(5, d.getGroup().getUuid());
			st.setString(6, d.getCategory().getUuid());
			st.setString(7, d.getComments());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the detail ["+d+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean update(Transaction t) {
		String sql = "update TRANSACTION set date = ?, amount = ?, type = ?, account_id = ?, location = ?, receipt = ?, comments = ? "
				+ "where id = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setDate(1, new Date(t.getDate().getTime()));
			st.setDouble(2, t.getAmount().doubleValue());
			st.setString(3, t.getType());
			st.setString(4, t.getAccount().getUuid());
			st.setString(5, t.getLocation());
			st.setString(6, t.getReceiptType());
			st.setString(7, t.getComments());
			st.setString(8, t.getId());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error updating the transaction ["+t+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	public static boolean update(Detail d) {
		String sql = "update DETAIL set month_intended = ?, amount = ?, financial_group_id = ?, category_id = ?, comments = ? "
				+ "where trans_id = ? and detail_order = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, d.getMonthIntended());
			st.setDouble(2, d.getAmount().doubleValue());
			st.setString(3, d.getGroup().getUuid());
			st.setString(4, d.getCategory().getUuid());
			st.setString(5, d.getComments());
			st.setString(6, d.getTransactionId());
			st.setInt(7, d.getOrder());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error updating the detail ["+d+"].  MSG="+e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
	
	
	public static List<GeneralDao> getAccounts() {
		List<GeneralDao> toRet = new ArrayList<GeneralDao>();
		String sql = "select uuid, name from ACCOUNT order by name";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				toRet.add(new Account(rs.getString("uuid"), rs.getString("name")));
			}
			rs.close();
			return toRet;
		} catch (SQLException e) {
			System.err.println("Error retrieving accounts.  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GeneralDao> getGroups() {
		List<GeneralDao> toRet = new ArrayList<GeneralDao>();
		String sql = "select uuid, name from financial_group order by name";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				toRet.add(new FinancialGroup(rs.getString("uuid"), rs.getString("name")));
			}
			rs.close();
			return toRet;
		} catch (SQLException e) {
			System.err.println("Error retrieving financial groups.  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GeneralDao> getCategories() {
		List<GeneralDao> toRet = new ArrayList<GeneralDao>();
		String sql = "select uuid, name from Category order by name";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				toRet.add(new Category(rs.getString("uuid"), rs.getString("name")));
			}
			rs.close();
			return toRet;
		} catch (SQLException e) {
			System.err.println("Error retrieving categories.  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Transaction> getTransactionsByYear(String year) {
		String sql = "select t.date, t.amount, t.type, a.uuid, a.name, t.location, t.receipt, t.comments, t.id "
				+ "from transaction t "
				+ "left outer join account a on a.uuid = t.account_id "
				+ "where t.id like ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, year+"%");
			List<Transaction> tList = new ArrayList<>();
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				tList.add(inflateTransaction(rs));
			}
			rs.close();
			return tList;
		} catch (SQLException e) {
			System.err.println("Error retrieving transaction by year=["+year+"].  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;	
	}
	
	private static Transaction inflateTransaction(ResultSet rs) throws SQLException {
		Transaction t = new Transaction();
		t.setDate(rs.getDate(1));
		t.setAmount(BigDecimal.valueOf(rs.getDouble(2)));
		t.setType(rs.getString(3));
		t.setAccount(new Account(rs.getString(4), rs.getString(5)));
		t.setLocation(rs.getString(6));
		t.setReceiptType(rs.getString(7));
		t.setComments(rs.getString(8));
		t.setId(rs.getString(9));
		return t;
	}

	public static Transaction getTransaction(String currentTransId) {
		String sql = "select t.date, t.amount, t.type, a.uuid, a.name, t.location, t.receipt, t.comments, t.id "
				+ "from transaction t "
				+ "left outer join account a on a.uuid = t.account_id "
				+ "where t.id = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, currentTransId);
			Transaction t = null;
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				t = inflateTransaction(rs);
			}
			rs.close();
			return t;
		} catch (SQLException e) {
			System.err.println("Error retrieving transaction=["+currentTransId+"].  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static List<Detail> getDetailsNoTrans(String currentTransId) {
		List<Detail> toRet = new ArrayList<Detail>();
		String sql = "select d.detail_order, d.month_intended, d.amount, "
				+ "fg.uuid, fg.name, cat.uuid, cat.name, d.comments "
				+ "from detail d "
				+ "left outer join financial_group fg on fg.uuid = d.financial_group_id "
				+ "left outer join category cat on cat.uuid = d.category_id "
				+ "where d.trans_id = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, currentTransId);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Detail d = new Detail();
				d.setTransactionId(currentTransId);
				d.setOrder(rs.getInt(1));
				d.setMonthIntended(rs.getString(2));
				d.setAmount(BigDecimal.valueOf(rs.getDouble(3)));
				d.setGroup(new FinancialGroup(rs.getString(4), rs.getString(5)));
				d.setCategory(new Category(rs.getString(6), rs.getString(7)));
				d.setComments(rs.getString(8));
				toRet.add(d);
			}
			rs.close();
			return toRet;
		} catch (SQLException e) {
			System.err.println("Error retrieving details associated to transaction ["+currentTransId+"].  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static Detail getDetailNoTrans(String currentTransId, int order) {
		Detail d = null;
		String sql = "select d.detail_order, d.month_intended, d.amount, "
				+ "fg.uuid, fg.name, cat.uuid, cat.name, d.comments "
				+ "from detail d "
				+ "left outer join financial_group fg on fg.uuid = d.financial_group_id "
				+ "left outer join category cat on cat.uuid = d.category_id "
				+ "where d.trans_id = ? and d.detail_order = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, currentTransId);
			st.setInt(2, order);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				d = new Detail();
				d.setTransactionId(currentTransId);
				d.setOrder(rs.getInt(1));
				d.setMonthIntended(rs.getString(2));
				d.setAmount(BigDecimal.valueOf(rs.getDouble(3)));
				d.setGroup(new FinancialGroup(rs.getString(4), rs.getString(5)));
				d.setCategory(new Category(rs.getString(6), rs.getString(7)));
				d.setComments(rs.getString(8));
			}
			rs.close();
			return d;
		} catch (SQLException e) {
			System.err.println("Error retrieving detail associated to transaction ["+currentTransId+"] at order ["+order+"].  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static String getNextTransactionId(String year) {
		String sql = "select id from transaction where id like ? order by id desc limit 1";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, year+"-%");
			String prev = year+"-00000";
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				prev = rs.getString(1);
			}
			rs.close();
			
			//Now need to bump up the ID.
			String[] parts = prev.split("-");
			int counter = Integer.parseInt(parts[1])+1;
			
			return parts[0]+"-"+Utils.pad(counter+"",'0',5);
		} catch (SQLException e) {
			System.err.println("Error getting next Transaction ID year=["+year+"].  MSG="+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
