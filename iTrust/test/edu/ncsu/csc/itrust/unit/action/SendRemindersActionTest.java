package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.action.SendReminderAction;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.FakeEmailDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.lang.System;

public class SendRemindersActionTest extends TestCase {
    private ApptDAO apptDAO;
    private SendReminderAction remindersAction;
    private FakeEmailDAO emailDAO;

    long patientMID = 42L;
    long doctorMID = 9000000000L;

    private TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();

        remindersAction = new SendReminderAction(TestDAOFactory.getTestInstance(),9000000009L);
        emailDAO = TestDAOFactory.getTestInstance().getFakeEmailDAO();
        apptDAO = TestDAOFactory.getTestInstance().getApptDAO();

        ApptBean appt = new ApptBean();
        appt.setDate(new Timestamp(new Date().getTime()));
        appt.setApptType("Ultrasound");
        appt.setHcp(doctorMID);
        appt.setPatient(patientMID);

        apptDAO.scheduleAppt(appt);
    }

    public void testSendReminders() throws Exception {
        List<Email> initialEmails = emailDAO.getAllEmails();
        remindersAction.sendReminder(5,9000000009L);
        List<Email> finalEmails = emailDAO.getAllEmails();
        System.out.print(initialEmails);
        System.out.print(finalEmails);
        assertEquals(4, initialEmails.size());
        assertEquals(1, finalEmails.size() - initialEmails.size());

        remindersAction.sendReminder(60,9000000009L);
    }

}
