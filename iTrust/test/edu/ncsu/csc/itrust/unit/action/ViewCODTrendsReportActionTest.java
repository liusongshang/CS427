package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.OfficeVisitDAO;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import edu.ncsu.csc.itrust.CauseOfDeathInfo;
import edu.ncsu.csc.itrust.CauseOfDeathRankings;
import edu.ncsu.csc.itrust.action.ViewCODTrendsReportAction;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.enums.Gender;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import org.mockito.internal.matchers.Null;

public class ViewCODTrendsReportActionTest extends TestCase {

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen = new TestDataGenerator();
	private ViewCODTrendsReportAction action;
	private List<OfficeVisitBean> ovAll;
	private List<OfficeVisitBean> ovLHCP;

	public ViewCODTrendsReportActionTest() throws DBException {
	}

	@Override
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.standardData();
		gen.patient_hcp_vists();

		action = new ViewCODTrendsReportAction(factory, 9000000000L);
		ovAll = action.getOvDAO().getAllOfficeVisits();
		ovLHCP = action.getOvDAO().getAllOfficeVisitsForLHCP(action.getLoggedInMID());
	}

	public void testGetRankedCausesOfDeathInfo() throws DBException {
		Map<String,Integer> causeOfDeathMap = new HashMap();
		causeOfDeathMap.put("84.50", 100); // Malaria	
		causeOfDeathMap.put("79.10", 200); // Echovirus
		causeOfDeathMap.put("70.10", 250); // Viral hepatitis A, infectious	
		causeOfDeathMap.put("72.00", 50);  // Mumps	
		causeOfDeathMap.put("79.30", 300); // Coxsackie

		List<CauseOfDeathInfo> rankedCausesOfDeathInfo = action.getRankedCausesOfDeathInfo(causeOfDeathMap, 2);
		assertEquals(rankedCausesOfDeathInfo.get(0).getIcdCode(), "79.30");	
		assertEquals(rankedCausesOfDeathInfo.get(1).getIcdCode(), "70.10");	
	}

	public void testRankCauseOfDeath() throws DBException {
		List<PatientBean> patients = new ArrayList();
		PatientBean patient3 = action.getPatientDAO().getPatient(3);
		PatientBean patient4 = action.getPatientDAO().getPatient(4);
		PatientBean patient5 = action.getPatientDAO().getPatient(5);
		PatientBean patient6 = action.getPatientDAO().getPatient(6);
		PatientBean patient7 = action.getPatientDAO().getPatient(7);
		PatientBean patient8 = action.getPatientDAO().getPatient(8);

		patient3.setDateOfDeathStr("2004-09-23");
		patient3.setCauseOfDeath("84.50"); // 84.50 - Malaria

		patient4.setDateOfDeathStr("2000-01-08");
		patient4.setCauseOfDeath("84.50"); // 84.50 - Malaria
		
		patient5.setDateOfDeathStr("2005-04-04");
		patient5.setCauseOfDeath("487.00"); // 487.00 - Influenza
		
		patient6.setDateOfDeathStr("2003-02-11");
		patient6.setCauseOfDeath("487.00"); // 487.00 - Influenza
		
		patient7.setDateOfDeathStr("2000-01-01");
		patient7.setCauseOfDeath("487.00"); // 487.00 - Influenza
		
		patient8.setCauseOfDeath(""); // 487.00 - Influenza
		
		patients.add(action.getPatientDAO().getPatient(2)); // DoD: 2005-03-10 ; COD: 250.10 - Diabetes with ketoacidosis
		patients.add(patient3);
		patients.add(patient4);
		patients.add(patient5);
		patients.add(patient6);
		patients.add(patient7);
		patients.add(patient7);
		patients.add(patient8);

		Map<String, Integer> rankedCauseOfDeathMap = action.rankCauseOfDeath(patients);
		assertEquals(rankedCauseOfDeathMap.get("250.10"), (Integer) 1);
		assertEquals(rankedCauseOfDeathMap.get("84.50"), (Integer) 2);
		assertEquals(rankedCauseOfDeathMap.get("487.00"), (Integer) 3);
	}

	public void testGetRankedCausesOfDeathLHCPMale() throws DBException, FormValidationException {
		String lowerDate = "01/01/2000";
		String upperDate = "01/01/2008";
		String gender = "Male";

		CauseOfDeathRankings top2CausesOfDeath = action.getRankedCausesOfDeathLHCP(lowerDate, upperDate, gender);

		assertEquals(top2CausesOfDeath.getCauseOfDeathLHCP(0).getIcdCode(), "250.10");	
		assertEquals(top2CausesOfDeath.getCauseOfDeathAll(0).getIcdCode(), "250.10");	
	}

	public void testGetRankedCausesOfDeathLHCPFemale() throws DBException, FormValidationException {
		String lowerDate = "01/01/2000";
		String upperDate = "01/01/2008";
		String gender = "Female";

		CauseOfDeathRankings top2CausesOfDeath = action.getRankedCausesOfDeathLHCP(lowerDate, upperDate, gender);

		assertEquals(top2CausesOfDeath.getCauseOfDeathLHCP().size(), 0);	
		assertEquals(top2CausesOfDeath.getCauseOfDeathAll().size(), 0);	
	}

	public void testGetRankedCausesOfDeathLHCPNoGender() throws DBException, FormValidationException {
		String lowerDate = "01/01/2000";
		String upperDate = "01/01/2008";
		String gender = "";

		CauseOfDeathRankings top2CausesOfDeath = action.getRankedCausesOfDeathLHCP(lowerDate, upperDate, gender);

		assertEquals(top2CausesOfDeath.getCauseOfDeathLHCP(0).getIcdCode(), "250.10");	
		assertEquals(top2CausesOfDeath.getCauseOfDeathAll(0).getIcdCode(), "250.10");	
	}

	public void testGetRankedCausesOfDeathLHCPException() throws DBException, FormValidationException {
		String lowerDate = "01/01/2008";
		String upperDate = "01/01/2000";
		String gender = "";

		try {
			CauseOfDeathRankings top2CausesOfDeath = action.getRankedCausesOfDeathLHCP(lowerDate, upperDate, gender);
		} catch (Exception e) {

		}
	}

	public void testGetPatientsByGender() throws DBException, ParseException {
		List<PatientBean> patientsAll = action.getPatientsFromOfficeVisitHistory(ovAll);
		List<PatientBean> patientsLHCP = action.getPatientsFromOfficeVisitHistory(ovLHCP);

		int patientsCount, femalePatientsCount, malePatientsCount, neutralPatientsCount;

		patientsCount = patientsAll.size();
		femalePatientsCount = action.getPatientsByGender(patientsAll, Gender.Female).size();
		malePatientsCount = action.getPatientsByGender(patientsAll, Gender.Male).size();
		neutralPatientsCount = action.getPatientsByGender(patientsAll, Gender.NotSpecified).size();
		assertEquals(patientsCount, (femalePatientsCount + malePatientsCount + neutralPatientsCount));

		patientsCount = patientsLHCP.size();
		femalePatientsCount = action.getPatientsByGender(patientsLHCP, Gender.Female).size();
		malePatientsCount = action.getPatientsByGender(patientsLHCP, Gender.Male).size();
		neutralPatientsCount = action.getPatientsByGender(patientsLHCP, Gender.NotSpecified).size();
		assertEquals(patientsCount, (femalePatientsCount + malePatientsCount + neutralPatientsCount));
	}

	public void testGetPatientsDiedInDateRange() throws DBException, ParseException {
		OfficeVisitDAO ovDAO = action.getOvDAO();
		List<OfficeVisitBean> ovLHCP = ovDAO.getAllOfficeVisitsForLHCP(9000000000L);
		List<PatientBean> patientsLHCP = action.getPatientsFromOfficeVisitHistory(ovLHCP);

		PatientBean patient3 = action.getPatientDAO().getPatient(3);
		patient3.setDateOfDeathStr("1990-08-23");
		patient3.setCauseOfDeath("487.00"); // 487.00 - Influenza
		patientsLHCP.add(patient3);
		
		PatientBean patient4 = action.getPatientDAO().getPatient(4);
		patient4.setDateOfDeathStr("2010-08-23");
		patient4.setCauseOfDeath("487.00"); // 487.00 - Influenza
		patientsLHCP.add(patient4);
		
		PatientBean patient5 = action.getPatientDAO().getPatient(5);
		patient5.setCauseOfDeath(""); // 487.00 - Influenza
		patientsLHCP.add(patient5);
		
		PatientBean patient6 = action.getPatientDAO().getPatient(6);
		patient6.setDateOfDeathStr("2004-07-23");
		patient6.setCauseOfDeath("487.00"); // 487.00 - Influenza
		patientsLHCP.add(patient6);
		
		PatientBean patient7 = action.getPatientDAO().getPatient(7);
		patient7.setDateOfDeathStr("2001-06-28");
		patient7.setCauseOfDeath("487.00"); // 487.00 - Influenza
		patientsLHCP.add(patient7);
		patientsLHCP.add(patient6);

		Date lower = new SimpleDateFormat("MM/dd/yyyy").parse("06/28/2000");
		Date upper = new SimpleDateFormat("MM/dd/yyyy").parse("07/23/2005");

		List<PatientBean> patientsDiedInDateRange = action.getPatientsDiedInDateRange(patientsLHCP, lower, upper);

		// get disjunction of ovLHCP and ovInRange
		patientsLHCP.removeAll(patientsDiedInDateRange);
		for (PatientBean patientNotInRange : patientsLHCP) {
			Date dateNotInRange = patientNotInRange.getDateOfDeath();
			assert(dateNotInRange == null || dateNotInRange.after(upper) || dateNotInRange.before(lower));
		}

		for (PatientBean patientInRange : patientsDiedInDateRange) {
			Date dateInRange = patientInRange.getDateOfDeath();
			assert((dateInRange.before(upper) && dateInRange.after(lower)) || dateInRange.equals(upper) || dateInRange.equals(lower));
		}
	}
}
