package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.EditPatientAction;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.List;

/**
 * ActivateDeactivateTest
 */
public class ActivateDeactivateTest extends TestCase{

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private PatientDAO patientDAO;
	private TestDataGenerator gen = new TestDataGenerator();
	private EditPatientAction action;
	
	@Override
	protected void setUp() throws Exception{
		gen.clearAllTables();
		gen.patient92();
		patientDAO = factory.getPatientDAO();
		action = new EditPatientAction(factory, 900000000, "92");
	}
	
	/**
	 * testGetPatient
	 * @throws Exception
	 */
	public void testGetPatient() throws Exception {
		PatientBean p = action.getPatient();
		assertEquals("Bo Zeng", p.getFullName());
	}

	/**
	 * testActivatePreRegisteredPatient
	 * @throws Exception
	 */
	public void testActivatePreRegisteredPatient() throws Exception {
		action.activate();
		assertEquals("patient", patientDAO.getRole(92L, "patient"));
	}

	/**
	 * testDeactivatePreRegisteredPatient
	 * @throws Exception
	 */
	public void testDeactivatePreRegisteredPatient() throws Exception {
		action.deactivate();
		assertEquals("hidden", patientDAO.getRole(92L, "hidden"));
	}

}
