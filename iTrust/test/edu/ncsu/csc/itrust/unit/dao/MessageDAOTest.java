package edu.ncsu.csc.itrust.unit.dao;

import junit.framework.TestCase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.itrust.action.ViewMyMessagesAction;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.MessageDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;

/**
 * MessageDAOTest
 */
@SuppressWarnings("unused")
public class MessageDAOTest extends TestCase{
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private MessageDAO mDAO;
	
	
	/**
	 * setUp
	 */
	public void setUp() throws Exception, SQLException, DBException{
		super.setUp();

		TestDataGenerator gen = new TestDataGenerator();
                gen.clearAllTables();
                gen.standardData();

		this.factory = TestDAOFactory.getTestInstance();

		this.mDAO = new MessageDAO(factory);
	}

	/**
	 * testGetMessagesNameAscending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesNameAcending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesNameAscending(9000000000L);
		assertEquals(14, msgs.size());
		assertEquals(1, msgs.get(0).getFrom());
		assertEquals("Old Medicine", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesNameDescending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesNameDecending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesNameDescending(9000000000L);
		assertEquals(14, msgs.size());
		assertEquals(5, msgs.get(0).getFrom());
		assertEquals("Remote Monitoring Question", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesTimeAscending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesTimeAcending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesTimeAscending(9000000000L);
		assertEquals(14, msgs.size());
		assertEquals(1, msgs.get(0).getFrom());
		assertEquals("Old Medicine", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesTimeDescending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesTimeDecending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesTimeDescending(9000000000L);
		assertEquals(14, msgs.size());
		assertEquals(2, msgs.get(0).getFrom());
		assertEquals("Scratchy Throat", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesFromNameAscending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesFromNameAcending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesFromNameAscending(1L);
		assertEquals(11, msgs.size());
		assertEquals(9000000000L, msgs.get(0).getTo());
		assertEquals("Old Medicine", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesFromNameDescending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesFromNameDecending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesFromNameDescending(1L);
		assertEquals(11, msgs.size());
		assertEquals(9000000003L, msgs.get(0).getTo());
		assertEquals("Appointment Reschedule", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesFromTimeAscending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesFromTimeAcending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesFromTimeAscending(1L);
		assertEquals(11, msgs.size());
		assertEquals(9000000000L, msgs.get(0).getTo());
		assertEquals("Old Medicine", msgs.get(0).getSubject());
	}

	/**
	 * testGetMessagesFromTimeDescending
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetMessagesFromTimeDecending() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesFromTimeDescending(1L);
		assertEquals(11, msgs.size());
		assertEquals(9000000000L, msgs.get(0).getTo());
		assertEquals("Appointment", msgs.get(0).getSubject());
	}

	public void testGetMessageFromTimeDescendingException() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getMessagesFromTimeDescending(-1L);
	}

	/**
	 * testGetCCdMessages
	 * @throws SQLException
	 * @throws DBException
	 */
	public void testGetCCdMessages() throws SQLException, DBException {
		List<MessageBean> msgs = mDAO.getCCdMessages(1L);
		assertEquals(0, msgs.size());
	}
}
