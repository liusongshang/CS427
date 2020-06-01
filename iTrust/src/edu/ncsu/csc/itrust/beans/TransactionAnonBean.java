package edu.ncsu.csc.itrust.beans;

import java.sql.Timestamp;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * A bean for storing anonymous data about a transaction that occurred within iTrust.
 * 
 * A bean's purpose is to store data. Period. Little or no functionality is to be added to a bean 
 * (with the exception of minor formatting such as concatenating phone numbers together). 
 * A bean must only have Getters and Setters (Eclipse Hint: Use Source > Generate Getters and Setters
 * to create these easily)
 */
public class TransactionAnonBean {
	private Role roleOne;
	private Role roleTwo;
	private TransactionType transactionType;
	private Timestamp timeLogged;
	private String addedInfo;

	public TransactionAnonBean() {
	}

	public String getAddedInfo() {
		return addedInfo;
	}

	public void setAddedInfo(String addedInfo) {
		this.addedInfo = addedInfo;
	}

	public Role getRoleOne() {
		return roleOne;
	}

	public void setRoleOne(Role roleOne) {
		this.roleOne = roleOne;
	}

  public void setRoleOne(String roleOne) {
    if (roleOne == null) {
      this.roleOne = null;
      return;
    }
    this.roleOne = Role.parse(roleOne);
  }

	public Role getRoleTwo() {
		return roleTwo;
	}

  public void setRoleTwo(String roleTwo) {
    if (roleTwo == null) {
      this.roleTwo = null;
      return;
    }
    this.roleTwo = Role.parse(roleTwo);
  }

	public void setRoleTwo(Role roleTwo) {
		this.roleTwo = roleTwo;
	}

	public Timestamp getTimeLogged() {
		return (Timestamp) timeLogged.clone();
	}

	public void setTimeLogged(Timestamp timeLogged) {
		this.timeLogged = (Timestamp) timeLogged.clone();
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType tranactionType) {
		this.transactionType = tranactionType;
	}
}
