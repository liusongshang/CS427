package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.beans.OperationalProfile;
import edu.ncsu.csc.itrust.beans.TransactionAnonBean;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.beans.loaders.OperationalProfileLoader;
import edu.ncsu.csc.itrust.beans.loaders.TransactionAnonBeanLoader;
import edu.ncsu.csc.itrust.beans.loaders.TransactionBeanLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;

/**
 * Used for the logging mechanism.
 *
 * DAO stands for Database Access Object. All DAOs are intended to be reflections of the database, that is,
 * one DAO per table in the database (most of the time). For more complex sets of queries, extra DAOs are
 * added. DAOs can assume that all data has been validated and is correct.
 *
 * DAOs should never have setters or any other parameter to the constructor than a factory. All DAOs should be
 * accessed by DAOFactory (@see {@link DAOFactory}) and every DAO should have a factory - for obtaining JDBC
 * connections and/or accessing other DAOs.
 *
 *
 *
 */
public class TransactionDAO {
	private DAOFactory factory;
	private TransactionAnonBeanLoader anonLoader = new TransactionAnonBeanLoader();
	private TransactionBeanLoader loader = new TransactionBeanLoader();
	private OperationalProfileLoader operationalProfileLoader = new OperationalProfileLoader();

	/**
	 * The typical constructor.
	 * @param factory The {@link DAOFactory} associated with this DAO, which is used for obtaining SQL connections, etc.
	 */
	public TransactionDAO(DAOFactory factory) {
		this.factory = factory;
	}

	/**
	 * Returns the whole transaction log
	 *
	 * @return
	 * @throws DBException
	 */
	public List<TransactionBean> getAllTransactions() throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("SELECT * FROM transactionlog ORDER BY timeLogged DESC");
			ResultSet rs = ps.executeQuery();
			List<TransactionBean> loadlist = loader.loadList(rs);
			rs.close();
			ps.close();
			return loadlist;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 * Returns all unique transaction type names
	 *
	 * @return List<TransactionType>
	 * @throws DBException
	 */
	public List<TransactionType> getAllTransactionTypes() throws DBException{
		List<TransactionBean> transactions = getAllTransactions();
		List<TransactionType> allTransactionTypes = new ArrayList();

		for (TransactionBean t : transactions) {
			if (!allTransactionTypes.contains(t.getTransactionType())) {
				allTransactionTypes.add(t.getTransactionType());
			}
		}

		return allTransactionTypes;
	}

	/**
	 * Log a transaction, with all of the info. The meaning of secondaryMID and addedInfo changes depending on
	 * the transaction type.
	 *
	 * @param type The {@link TransactionType} enum representing the type this transaction is.
	 * @param loggedInMID The MID of the user who is logged in.
	 * @param secondaryMID Typically, the MID of the user who is being acted upon.
	 * @param addedInfo A note about a subtransaction, or specifics of this transaction (for posterity).
	 * @throws DBException
	 */
	public void logTransaction(TransactionType type, long loggedInMID, long secondaryMID, String addedInfo)
			throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("INSERT INTO transactionlog(loggedInMID, secondaryMID, "
					+ "transactionCode, addedInfo) VALUES(?,?,?,?)");
			ps.setLong(1, loggedInMID);
			ps.setLong(2, secondaryMID);
			ps.setInt(3, type.getCode());
			ps.setString(4, addedInfo);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 * Return a list of all transactions in which an HCP accessed the given patient's record
	 *
	 * @param patientID The MID of the patient in question.
	 * @return A java.util.List of transactions.
	 * @throws DBException
	 */
	public List<TransactionBean> getAllRecordAccesses(long patientID, long dlhcpID, boolean getByRole) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = factory.getConnection();
			ps = conn
					.prepareStatement("SELECT * FROM transactionlog WHERE secondaryMID=? AND transactionCode "
							+ "IN(" + TransactionType.patientViewableStr + ") AND loggedInMID!=? ORDER BY timeLogged DESC");
			ps.setLong(1, patientID);
			ps.setLong(2, dlhcpID);
			ResultSet rs = ps.executeQuery();
			List<TransactionBean> tbList = loader.loadList(rs);

			tbList = addAndSortRoles(tbList, patientID, getByRole);

			rs.close();
			ps.close();
			return tbList;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 * The Most Thorough Fetch 
	 * @param mid MID of the logged in user
	 * @param dlhcpID MID of the user's DLHCP
	 * @param start Index to start pulling entries from
	 * @param range Number of entries to retrieve
	 * @return List of <range> TransactionBeans affecting the user starting from the <start>th entry
	 * @throws DBException
	 */
	public List<TransactionBean> getTransactionsAffecting(long mid, long dlhcpID, java.util.Date start, int range) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = factory.getConnection();
			ps = conn
					.prepareStatement("SELECT * FROM transactionlog WHERE ((timeLogged <= ?) " +
							"AND  (secondaryMID=? AND transactionCode " +
							"IN (" +
							TransactionType.patientViewableStr+ ")) " +
							"OR (loggedInMID=? AND transactionCode=?) ) " +
							"AND NOT (loggedInMID=? AND transactionCode IN (" + //exclude if DLHCP as specified in UC43
							TransactionType.dlhcpHiddenStr + ")) " +
							"ORDER BY timeLogged DESC LIMIT 0,?");
			ps.setString(2, mid + "");
			ps.setString(3, mid + "");
			ps.setInt(4, TransactionType.LOGIN_SUCCESS.getCode());
			ps.setTimestamp(1, new Timestamp(start.getTime()));
			ps.setLong(5, dlhcpID);
			ps.setInt(6, range);
			ResultSet rs = ps.executeQuery();
			List<TransactionBean> tbList = loader.loadList(rs);
			rs.close();
			ps.close();
			return tbList;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 * Return a list of all transactions in which an HCP accessed the given patient's record, within the dates
	 *
	 * @param patientID The MID of the patient in question.
	 * @param lower The starting date as a java.util.Date
	 * @param upper The ending date as a java.util.Date
	 * @return A java.util.List of transactions.
	 * @throws DBException
	 */
	public List<TransactionBean> getRecordAccesses(long patientID, long dlhcpID, java.util.Date lower, java.util.Date upper, boolean getByRole) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = factory.getConnection();
			ps = conn
					.prepareStatement("SELECT * FROM transactionlog WHERE secondaryMID=? AND transactionCode IN ("
							+ TransactionType.patientViewableStr
							+ ") "
							+ "AND timeLogged >= ? AND timeLogged <= ? "
							+ "AND loggedInMID!=? "
							+ "ORDER BY timeLogged DESC");
			ps.setLong(1, patientID);
			ps.setTimestamp(2, new Timestamp(lower.getTime()));
			// add 1 day's worth to include the upper
			ps.setTimestamp(3, new Timestamp(upper.getTime() + 1000L * 60L * 60 * 24L));
			ps.setLong(4, dlhcpID);
			ResultSet rs = ps.executeQuery();
			List<TransactionBean> tbList = loader.loadList(rs);

			tbList = addAndSortRoles(tbList, patientID, getByRole);
			rs.close();
			ps.close();
			return tbList;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 * Returns the operation profile
	 *
	 * @return The OperationalProfile as a bean.
	 * @throws DBException
	 */
	public OperationalProfile getOperationalProfile() throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("SELECT TransactionCode, count(transactionID) as TotalCount, "
					+ "count(if(loggedInMID<9000000000, transactionID, null)) as PatientCount, "
					+ "count(if(loggedInMID>=9000000000, transactionID, null)) as PersonnelCount "
					+ "FROM transactionlog GROUP BY transactionCode ORDER BY transactionCode ASC");
			ResultSet rs = ps.executeQuery();
			OperationalProfile result = operationalProfileLoader.loadSingle(rs);
			rs.close();
			ps.close();
			return result;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

	/**
	 *
	 * @param tbList
	 * @param patientID
	 * @param sortByRole
	 * @return
	 * @throws DBException
	 */
	private List<TransactionBean> addAndSortRoles(List<TransactionBean> tbList, long patientID, boolean sortByRole) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = factory.getConnection();

			for(TransactionBean t : tbList) {
				ps = conn
						.prepareStatement("SELECT Role FROM users WHERE MID=?");
				ps.setLong(1, t.getLoggedInMID());
				ResultSet rs = ps.executeQuery();
				String role = "";
				if(rs.next())
					role = rs.getString("Role");
				if(role.equals("er"))
					role = "Emergency Responder";
				else if(role.equals("uap"))
					role = "UAP";
				else if(role.equals("hcp")) {
					role = "LHCP";
					ps.close();
					ps = conn
							.prepareStatement("SELECT PatientID FROM declaredhcp WHERE HCPID=?");
					ps.setLong(1, t.getLoggedInMID());
					ResultSet rs2 = ps.executeQuery();
					while(rs2.next()) {
						if (rs2.getLong("PatientID") == patientID){
							role = "DLHCP";
							break;
						}
					}
					rs2.close();
				}
				else if(role.equals("patient")){
					role = "Patient";
					ps.close();
					ps = conn
							.prepareStatement("SELECT representeeMID FROM representatives WHERE representerMID=?");
					ps.setLong(1, t.getLoggedInMID());
					ResultSet rs2 = ps.executeQuery();
					while(rs2.next()) {
						if (rs2.getLong("representeeMID") == patientID){
							role = "Personal Health Representative";
							break;
						}
					}
					rs2.close();
				}

				t.setRole(role);
				rs.close();
				ps.close();
			}

			if(sortByRole){
				TransactionBean[] array = new TransactionBean[tbList.size()];
				array[0] = tbList.get(0);
				TransactionBean t;
				for(int i = 1; i < tbList.size(); i++) {
					t = tbList.get(i);
					String role = t.getRole();
					int j = 0;
					while(array[j] != null && role.compareToIgnoreCase(array[j].getRole()) >= 0)
						j++;
					for(int k = i; k > j; k--) {
						array[k] = array[k-1];
					}
					array[j] = t;
				}
				int size = tbList.size();
				for(int i = 0; i < size; i++)
					tbList.set(i, array[i]);
			}

			return tbList;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

  /**
   * Append AND conditionals to a mysql query
   * @param firstCondition Whether this would be the first conditional to append
   * @param append Whether to append this statement
   * @param query Pointer to query string. query[0] should be the query string and can be modified by this method.
   * @param text The condition to append. This should have a trailing space.
   * @return firstCondition, whether or not the next item will be the first condition.
   */
  private boolean appendConditional(boolean firstCondition, boolean append, String [] query, String text) {
    String addition = "";
		if (append) {
			if (firstCondition) {
				firstCondition = false;
				addition += "WHERE ";
			}
			else {
				addition += "AND ";
			}
			addition += text;
      query[0] = query[0] + addition;
		}
    return firstCondition;
  }

	/**
	 * Get transactions by logged in role, affected role, date range, transaction name(s)
	 * Specifically for UC39 View
	 * @param loggedInRole Role of the logged in user field as a string
	 * @param secondaryRole Role of the secondaryMID user as a string
	 * @param transactionType
	 * @param startDate Start of date range
	 * @param endDate End of date range
	 * @return List of TransactionAnonBeans satisfying the given criteria
	 * @throws DBException
	 */
	public List<TransactionAnonBean> getTransactionsAnon(String loggedInRole, String secondaryRole, TransactionType transactionType, java.util.Date startDate, java.util.Date endDate) throws DBException {
		Connection conn = null;
		PreparedStatement ps = null;
		String q =
				"SELECT timeLogged, users1.Role roleOne, users2.Role roleTwo, transactionCode, addedInfo " +
						"FROM transactionlog " +
						"LEFT JOIN users users1 " +
						"ON transactionlog.loggedInMID=users1.MID " +
						"LEFT JOIN users users2 " +
						"ON transactionlog.secondaryMID=users2.MID ";
    String [] query = new String[] {q};
		boolean firstCondition = true;
    firstCondition = appendConditional(firstCondition, (loggedInRole != null), query, "users1.Role = ? ");
    firstCondition = appendConditional(firstCondition, (secondaryRole != null), query, "users2.Role = ? ");
    firstCondition = appendConditional(firstCondition, (transactionType != null), query, "transactionCode = ? ");
    firstCondition = appendConditional(firstCondition, (startDate != null), query, "transactionlog.timeLogged > ? ");
    firstCondition = appendConditional(firstCondition, (endDate != null), query, "transactionlog.timeLogged < ? ");
		query[0] += "ORDER BY timeLogged DESC;";
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement(query[0]);
			int counter = 1;
			if (loggedInRole != null) {
				ps.setString(counter++, loggedInRole);
			}
			if (secondaryRole != null) {
				ps.setString(counter++, secondaryRole);
			}
			if (transactionType != null) {
				ps.setInt(counter++, transactionType.getCode());
			}
			if (startDate != null) {
				ps.setTimestamp(counter++, new Timestamp(startDate.getTime()));
			}
			if (endDate != null) {
				ps.setTimestamp(counter++, new Timestamp(endDate.getTime()));
			}
			ResultSet rs = ps.executeQuery();
			List<TransactionAnonBean> tlbList = anonLoader.loadList(rs);
			rs.close();
			ps.close();
			return tlbList;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
	}

  /**
   * Get a list of distinct transaction types in String format
   * @return list of transaction types in the transactionlog table sorted by code
   */
  public List<String> getUsedTransactionTypes() throws DBException{
		Connection conn = null;
		PreparedStatement ps = null;

    String query = "SELECT DISTINCT transactionCode FROM transactionlog;";

    try {
			conn = factory.getConnection();
			ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
      List<String> list = new ArrayList<String>();
      while (rs.next()) {
        int code = rs.getInt("transactionCode");
        TransactionType tt = TransactionType.parse(code);
        list.add(String.format("%04d", tt.getCode()) + ": " + tt.getDescription());
      }
			rs.close();
			ps.close();

      Collections.sort(list);
			return list;
		} catch (SQLException e) {

			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
  }

}
