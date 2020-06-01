package edu.ncsu.csc.itrust.beans.loaders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.ncsu.csc.itrust.beans.TransactionAnonBean;
import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * A loader for TransactionAnonBeans.
 * 
 * Loads in information to/from beans using ResultSets and PreparedStatements. Use the superclass to enforce consistency. 
 * For details on the paradigm for a loader (and what its methods do), see {@link BeanLoader}
 */
public class TransactionAnonBeanLoader implements BeanLoader<TransactionAnonBean> {

	public List<TransactionAnonBean> loadList(ResultSet rs) throws SQLException {
		List<TransactionAnonBean> list = new ArrayList<TransactionAnonBean>();
		while (rs.next()) {
			list.add(loadSingle(rs));
		}
		return list;
	}

	public PreparedStatement loadParameters(PreparedStatement ps, TransactionAnonBean bean) throws SQLException {
		throw new IllegalStateException("unimplemented!");
	}

	public TransactionAnonBean loadSingle(ResultSet rs) throws SQLException {
		TransactionAnonBean t = new TransactionAnonBean();
		t.setAddedInfo(rs.getString("addedInfo"));
		t.setRoleOne(rs.getString("roleOne"));
		t.setRoleTwo(rs.getString("roleTwo"));
		t.setTimeLogged(rs.getTimestamp("timeLogged"));
		t.setTransactionType(TransactionType.parse(rs.getInt("transactionCode")));
		return t;
	}

}
