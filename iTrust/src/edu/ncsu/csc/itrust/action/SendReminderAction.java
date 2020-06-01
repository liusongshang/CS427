package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.ncsu.csc.itrust.EmailUtil;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.MessageDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.validate.EMailValidator;
import edu.ncsu.csc.itrust.validate.MessageValidator;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import java.text.*;
import java.util.*;

/**
 * Class for sendreminders.jsp.  
 */

public class SendReminderAction {
    private ApptDAO apptdao;
    private SendMessageAction message;
	private PersonnelDAO personneldao;



	/**
	 * Sets up defaults
	 * @param factory The DAOFactory used to create the DAOs used in this action.
	 * @param loggedInMID The MID of the user sending the message.
	 */
	public SendReminderAction(DAOFactory factory, long loggedInMID) {
		this.apptdao = factory.getApptDAO();
        this.message = new SendMessageAction(factory, loggedInMID);
        this.personneldao = factory.getPersonnelDAO();
	}
	
	/**
	 * Sends a reminder
	 * 
	 * @param days days in advance
	 * @throws ITrustException
	 * @throws SQLException
	 */
	public void sendReminder(int days,long loggedInMID) throws ITrustException, SQLException, FormValidationException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date dateend = new Date();
        String today = dateFormat.format(date);
        long onedaytime=24 * 60 * 60 * 1000;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        dateend = c.getTime();
        String enddate = dateFormat.format(dateend);
        try {
            List<ApptBean> beans = apptdao.getApptDays(today, enddate);
            for (int i = 0; i < beans.size(); i++) {
                ApptBean appt_bean = beans.get(i);
                Date appt_day = new Date(appt_bean.getDate().getTime());
                // get the person's beans and set the subject and body of message
                MessageBean message_bean = new MessageBean();
                message_bean.setFrom(loggedInMID);
                message_bean.setTo(appt_bean.getPatient());
                message_bean.setBody("You have an appointment on " + appt_bean.getDate() + " with Dr. "
                        + personneldao.getPersonnel(appt_bean.getHcp()).getFullName());

                String subject = "Reminder: upcoming appointment in " + ((appt_day.getTime() - date.getTime()) / onedaytime + 1) + " day";
                if (((appt_day.getTime() - date.getTime()) / onedaytime ) > 0) {
                    subject += "s";
                }
                message_bean.setSubject(subject);
                message_bean.setRead(0);
                // send the message
                message.sendMessage(message_bean);
            }
        } catch (Exception e) {
            throw e;
        }

    	}
}
		
	

