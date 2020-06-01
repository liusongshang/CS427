package edu.ncsu.csc.itrust.unit.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.DateUtil;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.enums.BloodType;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import edu.ncsu.csc.itrust.unit.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;

public class PatientBeanTest extends TestCase {
	private Date today;

	@Override
	protected void setUp() throws Exception {
		today = new Date();
	}

	public void testAgeZero() throws Exception {
		PatientBean baby = new PatientBean();
		baby.setDateOfBirthStr(new SimpleDateFormat("MM/dd/yyyy").format(today));
		assertEquals(0, baby.getAge());
	}

	public void testAge10() throws Exception {
		PatientBean kid = new PatientBean();
		kid.setDateOfBirthStr(DateUtil.yearsAgo(10));
		assertEquals(10, kid.getAge());
	}

	public void testBean() {
		PatientBean p = new PatientBean();
		p.setBloodType(BloodType.ABNeg);
		p.setDateOfBirthStr("bad date");
		p.setCity("Raleigh");
		p.setState("NC");
		p.setZip("27613-1234");
		p.setIcCity("Raleigh");
		p.setIcState("NC");
		p.setIcZip("27613-1234");
		p.setSecurityQuestion("Question");
		p.setSecurityAnswer("Answer");
		p.setPassword("password");
		p.setConfirmPassword("confirm");
		assertEquals(BloodType.ABNeg, p.getBloodType());
		assertNull(p.getDateOfBirth());
		assertEquals(-1, p.getAge());
		assertEquals("Raleigh, NC 27613-1234", p.getIcAddress3());
		assertEquals("Raleigh, NC 27613-1234", p.getStreetAddress3());
		assertEquals("Question", p.getSecurityQuestion());
		assertEquals("Answer", p.getSecurityAnswer());
		assertEquals("password", p.getPassword());
		assertEquals("confirm", p.getConfirmPassword());
	}

	public void testAgeInDays() throws ParseException {
		PatientBean pb = new PatientBean();
		long DAYS_IN_MS = 1000 * 60 * 60 * 24;
		Date birth = new Date(System.currentTimeMillis() - (33333 * DAYS_IN_MS));
		pb.setDateOfBirthStr(new SimpleDateFormat("MM/dd/yyyy").format(birth));
		long age = pb.getAgeInDays();

		assertEquals(33333, age);
	}
	
	public void testAgeInWeeks() throws ParseException {
		PatientBean pb = new PatientBean();
		long WEEKS_IN_MS = 7 * 1000 * 60 * 60 * 24;
		Date birth = new Date(System.currentTimeMillis() - (8888 * WEEKS_IN_MS));
		pb.setDateOfBirthStr(new SimpleDateFormat("MM/dd/yyyy").format(birth));
		long age = pb.getAgeInWeeks();

		assertEquals(8888, age);
	}

	public void testMessageFilter() {
		PatientBean pb = new PatientBean();
		assertEquals("", pb.getMessageFilter());

		String filter = "Test,,,,,";
		pb.setMessageFilter(filter);
		assertEquals("Test,,,,,", pb.getMessageFilter());
	}

	public void testEquals() {
		PatientBean a = new PatientBean();
		a.setMID(4444);

		PatientBean b = new PatientBean();
		b.setMID(6666);

		assertEquals(2222, a.equals(b));
	}

	public void testHashCode() {
		PatientBean pb = new PatientBean();
		assertEquals(42, pb.hashCode());
	}
	
	
}
