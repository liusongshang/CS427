package edu.ncsu.csc.itrust.action;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import edu.ncsu.csc.itrust.CauseOfDeathInfo;
import edu.ncsu.csc.itrust.CauseOfDeathRankings;
import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.ICDCodesDAO;
import edu.ncsu.csc.itrust.dao.mysql.OfficeVisitDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.enums.Gender;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;


/**
 * UC20
 *
 * Handles LHCP viewing cause-of-death trends reports (top 2 most common causes-of-death)
 *  based on a specified time period and gender for their patients which fit the criteria 
 *  and all patients which fit the criteria
 *
 */
public class ViewCODTrendsReportAction {
	private OfficeVisitDAO ovDAO;
	private PatientDAO paDAO;
	private ICDCodesDAO icdDAO;
	long loggedInMID;

	/**
	 * Sets up defaults
	 *
	 * @param factory The DAOfactory used to create the DAOs used in this action.
	 * @param loggedInMID MID for the logged in HCP.
	 *
	 * Referencing action/LabProcHCPAction.java
	 */

	public ViewCODTrendsReportAction(DAOFactory factory, long loggedInMID) {
		//super(factory, loggedInMID);
		ovDAO = factory.getOfficeVisitDAO();
		paDAO = factory.getPatientDAO();
		icdDAO = factory.getICDCodesDAO();
		this.loggedInMID = loggedInMID;
	}

	public OfficeVisitDAO getOvDAO() {
		return this.ovDAO; 
	}

	public PatientDAO getPatientDAO() {
		return this.paDAO;
	}

	public ICDCodesDAO getIcdDAO() {
		return this.icdDAO;
	}

	public long getLoggedInMID() {
		return this.loggedInMID;
	}


	/**
	 *	Given a list of patients, find patients who died within a specific date range.
	 *	Return a list of unique patients (aka, a set) that
	 *	died within the given date range (inclusive).
	 *
	 *  Note that, because PatientBean does not have a "hashCode" to do equality check,
	 *  we cannot directly use "Set<PatientBean> patientsDiedInDateRange" and
	 *  "if patientsDiedInDateRange.contains(patient)"
	 *  Instead, a "Set<Long> patientsMIDSet" is used.
	 *
	 *
	 *	@param patients
	 *	@param lower
	 *	@param upper
	 *	@return (List<PatientBean>)
	 *
	 */
	public List<PatientBean> getPatientsDiedInDateRange(List<PatientBean> patients, Date lower, Date upper) throws DBException {
		List<PatientBean> patientsDiedInDateRange = new ArrayList<>();
		Set<Long> patientsMIDSet = new HashSet<>();

		for (PatientBean patient : patients) {
			long patientMID = patient.getMID();
			if (patientsMIDSet.contains(patientMID)) {
				continue;
			} else {
				patientsMIDSet.add(patientMID);
			}

			Date patientDateOfDeath = patient.getDateOfDeath();
			if (patientDateOfDeath != null) { 
				//boolean inRange = patientDateOfDeath.before(upper) && patientDateOfDeath.after(lower);
				//Boolean onLowerEnd = patientDateOfDeath.equals(lower); 
				//Boolean onUpperEnd = patientDateOfDeath.equals(upper);
				if (patientDateOfDeath.before(upper)) {
					if (patientDateOfDeath.after(lower)) {
						patientsDiedInDateRange.add(patient);
					}
				}	
			}
		}

		return patientsDiedInDateRange;
	}

	/**
	 *	Given a list of office visit history, return a set of patients invovled.
	 *
	 *  Return a list of unique patients (aka, a set)
	 *
	 *	@param officeVisitHistory
	 *	@return (List<PatientBean>)
	 *
	 */
	public List<PatientBean> getPatientsFromOfficeVisitHistory(List<OfficeVisitBean> officeVisitHistory) throws DBException {
		List<PatientBean> patients = new ArrayList<>();
		Set<Long> patientsIDSet = new HashSet<>();
		for(OfficeVisitBean ov : officeVisitHistory) {
			long patientID = ov.getPatientID();
			if (!patientsIDSet.contains(patientID)) {
				PatientBean patient = paDAO.getPatient(patientID);
				patients.add(patient);
				patientsIDSet.add(patientID);
			}
		}
		return patients;
	}


	/**
	 *  Given a list of patients, return a list of patients whose genders match the specified gender.
	 *
	 *  @param patients
	 *  @param gender
	 *	@return (List<PatientBean>)
	 *
	 */
	public List<PatientBean> getPatientsByGender(List<PatientBean> patients, Gender gender) throws DBException {
		List<PatientBean> patientsByGender = new ArrayList<>();

		// Check gender of each patient who showed up in the provided list of officeVisitHistory
		for(PatientBean patient : patients) {
			if (patient.getGender().equals(gender)){
				patientsByGender.add(patient);
			}
		}

		return patientsByGender;
	}



	/**
	 * Given a list of patients, log their causes of death in a Map (key: causeOfDeath, value: killCount),
	 * to keep track of the quantity of deaths caused by each unique cause of death, and return the map.
	 *
	 * @param allPatients
	 * @return Map (key: causeOfDeath, value: count)
	 * @throws DBException
	 */
	public Map<String, Integer> rankCauseOfDeath(List<PatientBean> allPatients) throws DBException{
		// count all causes of death for the given set of patients
		Map<String, Integer> causeOfDeathMap = new HashMap<>();
		Set<Long> patientsMIDSet = new HashSet<>();
		for (PatientBean patient : allPatients){
			long patientMID = patient.getMID();
			if (patientsMIDSet.contains(patientMID)) {
				continue;
			} else {
				patientsMIDSet.add(patientMID);
			}

			String causeOfDeath = patient.getCauseOfDeath();
			if (causeOfDeath.equals("")){
				continue;
			}
			if (causeOfDeathMap.containsKey(causeOfDeath)){
				Integer newCount = causeOfDeathMap.get(causeOfDeath) + 1;
				causeOfDeathMap.put(causeOfDeath, newCount);
			}
			else {
				causeOfDeathMap.put(causeOfDeath, 1);
			}
		}

		return causeOfDeathMap;
	}

	/**
	 *	Given a cause of death map (key: causeOfDeath, value: killCount),
	 *	search for the top causes of death based on quantity
	 *	of deaths per cause.
	 *
	 *  rankingRange is an integer that specifies how many top causes of death should be found
	 *  (i.e. rankingRange = 10 means finding the top 10 causes of death)
	 *
	 *  Return a list of causeOfDeathInfo, a wrapper class that stores the name, code and killCount
	 *  of a cause of death
	 *
	 *	@param codMap
	 *	@param rankingRange
	 *	@return (List<CauseOfDeathInfo>)
	 *
	 */
	public List<CauseOfDeathInfo> getRankedCausesOfDeathInfo(Map<String,Integer> codMap, Integer rankingRange) throws DBException {
		List<CauseOfDeathInfo> topCausesOfDeath = new ArrayList();
		// topCausesOfDeathEntryList is a sorted (by value) list of codMap entries
		List<Map.Entry<String, Integer>> topCausesOfDeathEntryList = codMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toList());

		Collections.reverse(topCausesOfDeathEntryList);

		// Don't attempt to get info from an index that doesn't exist
		if (rankingRange > topCausesOfDeathEntryList.size()) {
			rankingRange = topCausesOfDeathEntryList.size();
		}

		for (int rank = 0; rank < rankingRange; rank ++){
			Map.Entry<String, Integer> cod = topCausesOfDeathEntryList.get(rank);
			DiagnosisBean codBean = icdDAO.getICDCode(cod.getKey());
			//String codStr = "place holder";
			CauseOfDeathInfo topCauseOfDeath = new CauseOfDeathInfo(cod.getKey(), codBean.getDescription(), cod.getValue());
			//CauseOfDeathInfo topCauseOfDeath = new CauseOfDeathInfo(cod.getKey(), codStr, cod.getValue());
			topCausesOfDeath.add(topCauseOfDeath);
		}

		return topCausesOfDeath;
	}


	/**
	 * Main function that uses all the functions implemented above
	 *
	 * First, select patients based on date range and gender
	 * Then, find the top causes of death in the given patient lists
	 *
	 * @param lowerDate
	 * @param upperDate
	 * @param genderStr
	 * @return
	 * @throws DBException
	 * @throws FormValidationException
	 */

	public CauseOfDeathRankings getRankedCausesOfDeathLHCP(String lowerDate, String upperDate, String genderStr)
			throws DBException,FormValidationException 
	{
		// check the validity of the year given, otherwise ask user to retry

		List<OfficeVisitBean> ovLHCP = ovDAO.getAllOfficeVisitsForLHCP(loggedInMID);
        List<OfficeVisitBean> ovAll = ovDAO.getAllOfficeVisits();

        Date lower;
		Date upper;

		try {
			lower = new SimpleDateFormat("MM/dd/yyyy").parse(lowerDate);
			upper = new SimpleDateFormat("MM/dd/yyyy").parse(upperDate);

			if (lower.after(upper)) {
				throw new FormValidationException("Start date must be before end date!");
			}
		} catch (ParseException e) {
			throw new FormValidationException("Enter dates in MM/dd/yyyy format");
		}

		List<PatientBean> patientsLHCP, patientsLHCPByGender, patientsAll, patientsAllByGender;

		if (genderStr != "") {
			// get patients of all gender first, then retrive patients of selected gender
			Gender gender = Gender.parse(genderStr);
			patientsLHCP = getPatientsFromOfficeVisitHistory(ovLHCP);
			patientsLHCPByGender = getPatientsByGender(patientsLHCP, gender);
			patientsAll = paDAO.getAllPatients();
			patientsAllByGender = getPatientsByGender(patientsAll, gender);
		} else {
			// get patients of all gender, since no gender is specified
			patientsLHCPByGender = getPatientsFromOfficeVisitHistory(ovLHCP);
			patientsAllByGender = getPatientsFromOfficeVisitHistory(ovAll);
		}

		List<PatientBean> patientsDiedInDateRangeLHCP = getPatientsDiedInDateRange(patientsLHCPByGender, lower, upper);
		List<PatientBean> patientsDiedInDateRangeAll = getPatientsDiedInDateRange(patientsAllByGender, lower, upper);

		Map<String,Integer> rankedCausesOfDeathLHCP = rankCauseOfDeath(patientsDiedInDateRangeLHCP);
		Map<String,Integer> rankedCausesOfDeathAll = rankCauseOfDeath(patientsDiedInDateRangeAll);

		List<CauseOfDeathInfo> top2CausesOfDeathLHCP = getRankedCausesOfDeathInfo(rankedCausesOfDeathLHCP, 2);
		List<CauseOfDeathInfo> top2CausesOfDeathAll = getRankedCausesOfDeathInfo(rankedCausesOfDeathAll, 2);

		CauseOfDeathRankings top2CausesOfDeath = new CauseOfDeathRankings(top2CausesOfDeathLHCP, top2CausesOfDeathAll);

		return top2CausesOfDeath;
	}




}












