package edu.ncsu.csc.itrust.unit.bean;

import java.sql.Timestamp;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.TransactionAnonBean;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class TransactionAnonBeanTest extends TestCase {
  
  public void testValues() {
    TransactionAnonBean bean = new TransactionAnonBean();
    bean.setRoleOne(Role.PATIENT);
    bean.setRoleTwo(Role.HCP);
    bean.setTransactionType(TransactionType.USER_PREFERENCES_VIEW);
    bean.setTimeLogged(new Timestamp(1000));
    bean.setAddedInfo("Some Info");
    
    assertEquals(bean.getRoleOne(), Role.PATIENT);
    assertEquals(bean.getRoleTwo(), Role.HCP);
    assertEquals(bean.getTransactionType(), TransactionType.USER_PREFERENCES_VIEW);
    assertTrue(bean.getTimeLogged().equals(new Timestamp(1000)));
    assertTrue(bean.getAddedInfo().equals("Some Info"));
  }

  public void testRole() {

    TransactionAnonBean bean = new TransactionAnonBean();
    String a = null;
    String b = null;
    bean.setRoleOne(a);
    bean.setRoleTwo(b);
    assertEquals(bean.getRoleOne(), null);
    assertEquals(bean.getRoleTwo(), null);
    
    bean.setRoleOne("patient");
    bean.setRoleTwo("hcp");
    assertEquals(bean.getRoleOne(), Role.PATIENT);
    assertEquals(bean.getRoleTwo(), Role.HCP);
  }
}
