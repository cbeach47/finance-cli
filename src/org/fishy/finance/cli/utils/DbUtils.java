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

	public static boolean save(Account a) {
		String sql = "insert into ACCOUNT (UUID, NAME) values (?,?)";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, a.getUuid());
			st.setString(2, a.getName());
			int ret = st.executeUpdate();
			return ret == 1;
		} catch (SQLException e) {
			System.err.println("Error saving the account ["+a+"].  MSG="+e.getMessage());
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return false;
	}
	
	public static List<Account> getAccounts() {
		List<Account> toRet = new ArrayList<Account>();
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
	
	public static List<FinancialGroup> getGroups() {
		List<FinancialGroup> toRet = new ArrayList<FinancialGroup>();
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
	
	public static List<Category> getCategories() {
		List<Category> toRet = new ArrayList<Category>();
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

	public static Transaction getTransaction(String currentTransId) {
		String sql = "select t.date, t.amount, t.type, a.uuid, a.name, t.location, t.receipt, t.comments "
				+ "from transaction t "
				+ "left outer join account a on a.uuid = t.account_id "
				+ "where t.id = ?";
		 
		try (PreparedStatement st = conn.prepareStatement(sql)){
			st.setString(1, currentTransId);
			Transaction t = null;
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				t = new Transaction();
				t.setId(currentTransId);
				t.setDate(rs.getDate(1));
				t.setAmount(BigDecimal.valueOf(rs.getDouble(2)));
				t.setType(rs.getString(3));
				t.setAccount(new Account(rs.getString(4), rs.getString(5)));
				t.setLocation(rs.getString(6));
				t.setReceiptType(rs.getString(7));
				t.setComments(rs.getString(8));
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
