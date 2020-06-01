package edu.ncsu.csc.itrust.unit.action;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import edu.ncsu.csc.itrust.action.ViewMyMessagesAction;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.MessageDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

/**
 * ViewMyMessagesActionTest
 */
public class ViewMyMessagesActionTest extends TestCase {

	private ViewMyMessagesAction action;
	private ViewMyMessagesAction action2;
	private ViewMyMessagesAction evilAction;
	private DAOFactory factory;
	private DAOFactory evilFactory;
	private long mId = 2L;
	private long hcpId = 9000000000L;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();

		this.factory = TestDAOFactory.getTestInstance();
		this.evilFactory = EvilDAOFactory.getEvilInstance();
		this.action = new ViewMyMessagesAction(this.factory, this.mId);
		this.action2 = new ViewMyMessagesAction(this.factory, this.hcpId);
		this.evilAction = new ViewMyMessagesAction(this.evilFactory, this.mId);
	}

	/**
	 * testGetSortTypes
	 */
	public void testGetSortTypes() {
		List<String> sTypes = action.getSortTypes();
		assertEquals(sTypes.size(), 2);
	}

	/**
	 * testGetSortDirections
	 */
	public void testGetSortDirections() {
		List<String> sDirs = action.getSortDirections();
		assertEquals(sDirs.size(), 2);
	}
	
	/**
	 * testGetAllMyMessages
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetAllMyMessages() throws SQLException, DBException {
		List<MessageBean> mbList = action.getAllMyMessages();
		
		assertEquals(1, mbList.size());
		
		// Should send a message and recheck later.
	}

	/**
	 * testGetPatientName
	 * @throws ITrustException
	 */
	public void testGetPatientName() throws ITrustException {
		assertEquals("Andy Programmer", action.getName(this.mId));
	}
	
	/**
	 * testGetPersonnelName
	 * @throws ITrustException
	 */
	public void testGetPersonnelName() throws ITrustException {
		assertEquals("Kelly Doctor", action.getPersonnelName(this.hcpId));
	}
	
	/**
	 * testGetAllMyMessagesTimeAscending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesTimeDescending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMyMessagesTimeDescending();
		
		assertEquals(14, mbList.size());
		
		assertTrue(mbList.get(0).getSentDate().before(mbList.get(1).getSentDate()));
	}
	
	/**
	 * testGetAllMyMessagesTimeDescending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesTimeAscending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMyMessagesTimeAscending();
		
		assertEquals(14, mbList.size());
		
		assertTrue(mbList.get(0).getSentDate().after(mbList.get(1).getSentDate()));
	}
	
	/**
	 * testGetAllMyMessagesNameAscending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesNameAscending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMyMessagesNameAscending();
		List<MessageBean> mbList2 = action.getAllMyMessagesNameAscending();
		
		assertEquals(14, mbList.size());
		assertEquals(1, mbList2.size());
		
		try {
			assertTrue(action2.getName(mbList.get(0).getFrom()).compareTo(action2.getName(mbList.get(13).getFrom())) >= 0);
		} catch (ITrustException e) {
			//TODO
		}
	}
	
	/**
	 * testGetAllMyMessagesNameDescending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesNameDescending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMyMessagesNameDescending();
		List<MessageBean> mbList2 = action.getAllMyMessagesNameDescending();
		
		assertEquals(14, mbList.size());
		assertEquals(1, mbList2.size());
		
		try {
			assertTrue(action2.getName(mbList.get(13).getFrom()).compareTo(action2.getName(mbList.get(0).getFrom())) >= 0);
		} catch (ITrustException e) {
			//TODO
		}
	}
	
	/**
	 * testGetAllMySentMessages
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMySentMessages() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMySentMessages();
		
		assertEquals(2, mbList.size());
	}
	
	/**
	 * testGetAllMyMessagesFromTimeAscending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesFromTimeDescending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMySentMessagesTimeDescending();
		
		assertEquals(2, mbList.size());
		
		assertTrue(mbList.get(0).getSentDate().before(mbList.get(1).getSentDate()));
	}
	
	/**
	 * testGetAllMyMessagesFromTimeDescending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesFromTimeAscending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMySentMessagesTimeAscending();
		
		assertEquals(2, mbList.size());
		
		assertTrue(mbList.get(0).getSentDate().after(mbList.get(1).getSentDate()));
	}
	
	/**
	 * testGetAllMyMessagesFromNameAscending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesFromNameAscending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMySentMessagesNameAscending();
		List<MessageBean> mbList2 = action.getAllMySentMessagesNameAscending();
		
		assertEquals(2, mbList.size());
		assertEquals(3, mbList2.size());
		
		try {
			assertTrue(action2.getName(mbList.get(0).getFrom()).compareTo(action2.getName(mbList.get(1).getFrom())) >= 0);
		} catch (ITrustException e) {
			//TODO
		}
	}
	
	/**
	 * testGetAllMyMessagesFromNameDescending
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetAllMyMessagesFromNameDescending() throws DBException, SQLException {		
		
		List<MessageBean> mbList = action2.getAllMySentMessagesNameDescending();
		List<MessageBean> mbList2 = action.getAllMySentMessagesNameDescending();
		
		assertEquals(2, mbList.size());
		assertEquals(3, mbList2.size());
		
		try {
			assertTrue(action2.getName(mbList.get(1).getFrom()).compareTo(action2.getName(mbList.get(0).getFrom())) >= 0);
		} catch (ITrustException e) {
			//TODO
		}
	}
	
	/**
	 * testUpdateRead
	 * @throws ITrustException
	 * @throws SQLException
	 * @throws FormValidationException
	 */
	public void testUpdateRead() throws ITrustException, SQLException, FormValidationException {
		List<MessageBean> mbList = action.getAllMyMessages();
		assertEquals(0, mbList.get(0).getRead());
		action.setRead(mbList.get(0));
		mbList = action.getAllMyMessages();
		assertEquals(1, mbList.get(0).getRead());
	}
	
	/**
	 * testAddMessage
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testAddMessage() throws SQLException, DBException {
		MessageDAO test = new MessageDAO(factory);
		
		List<MessageBean> mbList = action.getAllMyMessages();
		
		test.addMessage(mbList.get(0));
		
		mbList = action.getAllMyMessages();
		
		assertEquals(2, mbList.size());
	}

	/**
	 * testFilterMessages
	 * @throws SQLException
	 * @throws ITrustException
	 * @throws ParseException
	 */
	public void testFilterMessages() throws SQLException, ITrustException, ParseException {
		List<MessageBean> mbList = action2.getAllMyMessages();
		
		List<MessageBean> res1 = action2.filterMessages(mbList, "Random Person,Appointment,Appointment,Lab,01/01/2010,01/31/2010");
		assertEquals(1, res1.size());

		List<MessageBean> res2 = action2.filterMessages(mbList, ",,,,,");
		assertEquals(14, res2.size());

		List<MessageBean> res3 = action2.filterMessages(mbList, ",Appointment,,,,");
		assertEquals(2, res3.size());

		List<MessageBean> res4 = action2.filterMessages(mbList, ",,Appointment,,,");
		assertEquals(5, res4.size());

		List<MessageBean> res5 = action2.filterMessages(mbList, ",,,Appointment,,");
		assertEquals(9, res5.size());

		List<MessageBean> res6 = action2.filterMessages(mbList, ",,,,01/21/2010,");
		assertEquals(7, res6.size());

		List<MessageBean> res7 = action2.filterMessages(mbList, ",,,,,01/21/2010");
		assertEquals(7, res7.size());
	}
	
	/**
	 * testGetUnreadCount
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetUnreadCount() throws DBException, SQLException {
		assertEquals(1, action.getUnreadCount());
		assertEquals(12, action2.getUnreadCount());
	}
	
	/**
	 * testLinkedToReferral
	 * @throws DBException
	 */
	public void testLinkedToReferral() throws DBException {
		assertEquals(0, action.linkedToReferral(1));
	}
	
	/**
	 * testGetCCdMessages
	 * @throws DBException
	 * @throws SQLException
	 */
	public void testGetCCdMessages() throws DBException, SQLException {
		assertEquals(0, action.getCCdMessages(1).size());
	}
	
	/**
	 * testThrowsExceptions
	 * @throws DBException
	 */
	public void testThrowsExceptions() throws DBException {
		List<MessageBean> resultList = null;
		try {
			resultList = evilAction.getAllMyMessages();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}
		
		resultList = null;
		try {
			resultList = evilAction.getAllMyMessagesNameAscending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}
		
		resultList = null;
		try {
			resultList = evilAction.getAllMySentMessages();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMyMessagesNameDescending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMyMessagesTimeAscending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMyMessagesTimeDescending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMySentMessagesTimeAscending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMySentMessagesTimeDescending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMySentMessagesNameAscending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}

		resultList = null;
		try {
			resultList = evilAction.getAllMySentMessagesNameDescending();
			fail();
		} catch (DBException e) {
			assertNull(resultList);
		} catch (SQLException e) {
			assertNull(resultList);
		}
	}

	public void testValidateAndStoreFilter() throws ParseException {
		String filter1 = "TestInvalidWords,,Snae,Snae,,";
		String result1 = action.validateAndCreateFilter(filter1);
		assertTrue(result1.startsWith("Error"));

		String filter2 = "TestInvalidDate,,,,10/10/1980,10/09/1980";
		String result2 = action.validateAndCreateFilter(filter2);
		assertTrue(result2.startsWith("Error"));

		String filter3 = "TestValidWords,,For,Man,,";
		String result3 = action.validateAndCreateFilter(filter3);
		assertTrue(result3.startsWith("Test"));

		String filter4 = "TestWordVariant1,,For,,,";
		String result4 = action.validateAndCreateFilter(filter4);
		assertTrue(result4.startsWith("Test"));

		String filter5 = "TestWordVariant2,,,Man,,";
		String result5 = action.validateAndCreateFilter(filter5);
		assertTrue(result5.startsWith("Test"));

		String filter6 = "TestWordVariant3,,Forman,man,,";
		String result6 = action.validateAndCreateFilter(filter6);
		assertTrue(result6.startsWith("Error"));

		String filter7 = "TestWordVariant4,,for,manfor,,";
		String result7 = action.validateAndCreateFilter(filter7);
		assertTrue(result7.startsWith("Error"));

		String filter8 = "TestDateVariant1,,,,,10/10/2010";
		String result8 = action.validateAndCreateFilter(filter8);
		assertTrue(result8.startsWith("Test"));

		String filter9 = "TestDateVariant2,,,,10/10/2010,";
		String result9 = action.validateAndCreateFilter(filter9);
		assertTrue(result9.startsWith("Test"));

		String filter10 = "TestDateVariant1,,,,09/09/2009,10/10/2010";
		String result10 = action.validateAndCreateFilter(filter10);
		assertTrue(result10.startsWith("Test"));

	}
}
