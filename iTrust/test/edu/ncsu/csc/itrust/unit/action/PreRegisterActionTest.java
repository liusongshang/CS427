/**
 * Tests for PreRegisterAction
 */

package edu.ncsu.csc.itrust.unit.action;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.action.PreRegisterAction;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.HealthRecord;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.HealthRecordsDAO;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class PreRegisterActionTest extends TestCase {
  private DAOFactory factory= TestDAOFactory.getTestInstance();
  private TestDataGenerator gen;
  private PreRegisterAction action;

/**
 * Sets up defaults
 */
  @Override
  protected void setUp() throws Exception {
    gen = new TestDataGenerator();
		gen.clearAllTables();

    gen.transactionLog();
    gen.patient11();
    action = new PreRegisterAction(factory);
  }

  /**
   * Tests preregistering a new patient successfully
   */
  public void testPreregisterPatient() throws FormValidationException, ITrustException {
    AuthDAO authDAO = factory.getAuthDAO();

    PatientBean p = new PatientBean();
    p.setFirstName("Hello");
    p.setLastName("World");
    p.setPassword("helloworld");
    p.setConfirmPassword("helloworld");
    p.setEmail("hello@world.com");

    HealthRecord h = new HealthRecord();

    boolean result = action.attemptPreRegistration(p, h);

    assertTrue(result);

    long mid = action.getPatientBean().getMID();
    assertEquals(authDAO.getUserRole(mid), Role.PREREGISTERED); 

    assertTrue(action.isValidEmail()); 
    assertTrue(action.isNewEmail()); 
    assertTrue(action.doPasswordsMatch()); 
    assertFalse(action.previousAttemptFailed());
  }

  /**
   * Tests preregistering a patient with bad input
   */
  public void testPreregisterPatientBad() throws FormValidationException, ITrustException {
    PatientBean p = new PatientBean();
    p.setFirstName("");
    p.setLastName("World");
    p.setPassword("helloworld");
    p.setConfirmPassword("hello");
    p.setEmail("5");
    p.setPhone("33");
    p.setIcPhone("44");

    HealthRecord h = new HealthRecord();
    boolean result = action.attemptPreRegistration(p, h);

    assertFalse(result);

    p.setEmail("h@gmail.com");
    p.setConfirmPassword("helloworld");
    p.setFirstName("h");
    p.setPhone("");
    p.setIcPhone("");

    h = new HealthRecord();
    result = action.attemptPreRegistration(p, h);

    assertTrue(result);

    h = new HealthRecord();
    result = action.attemptPreRegistration(p, h);

    assertFalse(result);    
  }

  /**
   * Tests failure at invalid email format
   */
  public void testPreregisterPatientInvalidEmail() throws FormValidationException , ITrustException {
    AuthDAO authDAO = factory.getAuthDAO();

    PatientBean p = new PatientBean();
    p.setFirstName("Helloa");
    p.setLastName("Worlda");
    p.setPassword("helloworld");
    p.setConfirmPassword("helloworld");
    p.setEmail("helloworld.com");

    HealthRecord h = new HealthRecord();

    boolean result = action.attemptPreRegistration(p, h);

    assertFalse(result);

    assertFalse(action.isValidEmail()); 
    assertTrue(action.isNewEmail()); 
    assertTrue(action.doPasswordsMatch()); 
    assertTrue(action.previousAttemptFailed());
  }

  /**
   * Tests failure at already used email
   */
  public void testPreregisterPatientUsedEmail() throws FormValidationException, ITrustException {
    AuthDAO authDAO = factory.getAuthDAO();

    PatientBean p = new PatientBean();
    p.setFirstName("Helloaa");
    p.setLastName("Worldaa");
    p.setPassword("helloworld");
    p.setConfirmPassword("helloworld");
    p.setEmail("e@f.com");

    HealthRecord h = new HealthRecord();

    boolean result = action.attemptPreRegistration(p, h);

    assertFalse(result);

    assertTrue(action.isValidEmail()); 
    assertFalse(action.isNewEmail()); 
    assertTrue(action.doPasswordsMatch()); 
    assertTrue(action.previousAttemptFailed());
  }

  /**
   * Tests failure at mismatched passwords
   */
  public void testPreregisterPatientPasswordMismatch() throws FormValidationException, ITrustException {
    AuthDAO authDAO = factory.getAuthDAO();

    PatientBean p = new PatientBean();
    p.setFirstName("Helloaaa");
    p.setLastName("Worldaaa");
    p.setPassword("helloworld");
    p.setConfirmPassword("helloworld1");
    p.setEmail("hello@world.comaaa");

    HealthRecord h = new HealthRecord();

    boolean result = action.attemptPreRegistration(p, h);

    assertFalse(result);

    assertTrue(action.isValidEmail()); 
    assertTrue(action.isNewEmail()); 
    assertFalse(action.doPasswordsMatch()); 
    assertTrue(action.previousAttemptFailed());
  }
}
