package edu.ncsu.csc.itrust.unit.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import edu.ncsu.csc.itrust.action.*;
import junit.framework.TestCase;

@SuppressWarnings("unused")
public class ViewDiagnosisStatisticsActionTest extends TestCase {

	private TestDataGenerator gen = new TestDataGenerator();
	private ViewDiagnosisStatisticsAction action;
	
	private int thisYear = Calendar.getInstance().get(Calendar.YEAR);

	
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.standardData();
		gen.patient_hcp_vists();
		gen.hcp_diagnosis_data();
		
		action = new ViewDiagnosisStatisticsAction(TestDAOFactory.getTestInstance());
	}
	
	public void testGetDiagnosisCodes() throws Exception {
		List<DiagnosisBean> db = action.getDiagnosisCodes();
		assertEquals(19, db.size());
	}
	
	public void testGetDiagnosisStatisticsValid() throws Exception {
		DiagnosisStatisticsBean dsBean = action.getDiagnosisStatistics("06/28/2011", "09/28/2011", "487.00", "27606-1234");
		assertEquals(2, dsBean.getZipStats());
		assertEquals(5, dsBean.getRegionStats());
	}
	
	public void testGetDiagnosisStatisticsValidNull() throws Exception {
		DiagnosisStatisticsBean dsBean0 = action.getDiagnosisStatistics(null, "09/28/2011", "487.00", "27606");
		assertEquals(null, dsBean0);

		DiagnosisStatisticsBean dsBean1 = action.getDiagnosisStatistics("06/28/2011", null, "487.00", "27606");
		assertEquals(null, dsBean1);

		DiagnosisStatisticsBean dsBean2 = action.getDiagnosisStatistics("06/28/2011", "09/28/2011", null, "27606-1234");
		assertEquals(null, dsBean2);
	}
	
	public void testGetDiagnosisStatisticsInvalidDate() throws Exception {
		try {
			action.getDiagnosisStatistics("06-28/2011", "09/28/2011", "487.00", "27606");
			fail("Should have failed but didn't");
		} catch (FormValidationException e) {
			assertEquals(1, e.getErrorList().size());
			assertEquals("Invalid date format entered. Please enter dates in MM/dd/yyyy and try again.", e.getErrorList().get(0));
		}
	}
	
	
	public void testGetDiagnosisStatisticsReversedDates() throws Exception {
		try {
			action.getDiagnosisStatistics("09/28/2011", "06/28/2011", "487.00", "27606");
			fail("Should have failed but didn't");
		} catch (FormValidationException e) {
			assertEquals(1, e.getErrorList().size());
			assertEquals("Start date must be before end date. Please try again.", e.getErrorList().get(0));
		}
	}
	
	public void testGetDiagnosisStatisticsInvalidZip() throws Exception {
		try {
			action.getDiagnosisStatistics("06/28/2011", "09/28/2011", "487.00", "2766");
			fail("Should have failed but didn't");
		} catch (FormValidationException e) {
			assertEquals(1, e.getErrorList().size());
			assertEquals("Zip Code must be 5 digits. Please try again.", e.getErrorList().get(0));
		}
	}
	
	public void testGetDiagnosisStatisticsInvalidICDCode() throws Exception {
		try {
			action.getDiagnosisStatistics("06/28/2011", "09/28/2011", "11114.00", "27606");
			fail("Should have failed but didn't");
		} catch (FormValidationException e) {
			assertEquals(1, e.getErrorList().size());
			assertEquals("ICDCode must be valid diagnosis code. Please try again.", e.getErrorList().get(0));
		}
	}
	
	public void testIsMalariaEpidemic() throws Exception {
		gen.malaria_epidemic();
		assertTrue(action.isMalariaEpidemic("11/02/" + thisYear, "27606", "110"));
		assertFalse(action.isMalariaEpidemic("11/16/" + thisYear, "27606", "110"));
	}
	
	public void testIsFluEpidemic() throws Exception {
		gen.influenza_epidemic();
		assertFalse(action.isFluEpidemic("10/02/" + thisYear, "27606"));
		assertFalse(action.isFluEpidemic("10/25/" + thisYear, "27606"));
		assertTrue(action.isFluEpidemic("11/02/" + thisYear, "27606"));
		assertTrue(action.isFluEpidemic("11/09/" + thisYear, "27606"));
		assertFalse(action.isFluEpidemic("11/16/" + thisYear, "27606"));
	}
	
	public void testGetEpidemicStatisticsInvalidThreshold(){
		try{
			ArrayList<DiagnosisStatisticsBean> dsList = action.getEpidemicStatistics("11/02/" + thisYear, "84.50", "27606", "");
			fail("FormValidationException should have been thrown.");
		}catch(FormValidationException e){
			//This should be thrown
		} catch (DBException e) {
			fail("DB Exception thrown");
		}
	}
	
	public void testGetEpidemicStatistics() {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11/02/2012", "487.00", 
							"00601", "5");
			assertEquals(2, dsList.size());
		} catch (FormValidationException e) {
			fail("FormValidationException");
		} catch (DBException d) {
			fail("DBException thrown");
		}
	}

	public void testGetEpidemicStatisticsMalaria() {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11/02/2012", "84.50", 
							"00601", "5");
			assertEquals(2, dsList.size());
		} catch (FormValidationException e) {
			fail("FormValidationException");
		} catch (DBException d) {
			fail("DBException thrown");
		}
	}

	public void testGetEpidemicStatisticsInvalidLowerDate() {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11-02--2012", "487.00", 
							"00601", "5");
			fail("FormValidationException should have been thrown");
		} catch (FormValidationException e) {
			// this should be thrown
		} catch (DBException d) {
			fail("DBException thrown");
		}
	}

	public void testGetEpidemicStatisticsInvalidZipcode() {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11/02/2012", "487.00", 
							"006010000", "5");
			fail("FormValidationException should have been thrown");
		} catch (FormValidationException e) {
			// this should be thrown
		} catch (DBException d) {
			fail("DBException thrown");
		}
	}

	public void testGetEpidemicStatisticsNullStartDate() throws Exception {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics(null, "487.00", 
							"00601", "5");
			assertEquals(null, dsList);
		} catch (Exception e) {
			fail("Exception");
		} 	
	}

	public void testGetEpidemicStatisticsNullICDCode() throws Exception {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11/02/2012", null, 
							"00601", "5");
			assertEquals(null, dsList);
		} catch (Exception e) {
			fail("Exception");
		} 	
	}

	public void testGetEpidemicStatisticsInvalidICDCode() throws Exception {
		try {
			ArrayList<DiagnosisStatisticsBean> dsList = 
					action.getEpidemicStatistics("11/02/2012", "7.00", 
							"00601", "5");
		} catch (Exception e) {
		} 	
	}
}
