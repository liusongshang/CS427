package edu.ncsu.csc.itrust.action;

import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.ncsu.csc.itrust.beans.HealthRecord;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.HealthRecordsDAO;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.validate.AddPatientValidator;

public class PreRegisterAction {
    private PatientDAO patientDAO;
    private AuthDAO authDAO;
    private HealthRecordsDAO healthRecordsDAO;

    private boolean failedAttempt;
    private boolean validEmail;
    private boolean newEmail;
    private boolean passwordMatch;
    private boolean validPhone;
    private boolean validIcPhone;
    private PatientBean patientBean;
    private HealthRecord healthRecord;

    private long preRegisteredMID;

    public PreRegisterAction(DAOFactory factory) {
        this.patientDAO = factory.getPatientDAO();
        this.authDAO = factory.getAuthDAO();
        this.healthRecordsDAO = factory.getHealthRecordsDAO();

        failedAttempt = false;
        validEmail = true;
        validPhone = true;
        validIcPhone = true;
        newEmail = true;
        passwordMatch = true;
    }

    /**
     * Check if the database already has the email to be registered.
     *
     * @param email The email to check.
     */
    private boolean checkNewEmail(String email) throws DBException {
        ArrayList<String> allEmails = patientDAO.getAllEmails();
        return !allEmails.contains(email);
    }

    private void recordValidEmail() {
        validEmail = true;
    }

    private void recordInvalidEmail() {
        validEmail = false;
    }

    private void recordNewEmail() {
        newEmail = true;
    }

    private void recordExistingEmail() {
        newEmail = false;
    }

    private void recordValidPhone() {
        validPhone = true;
    }

    private void recordInvalidPhone() {
        validPhone = false;
    }

    private void recordValidIcPhone() {
        validIcPhone = true;
    }

    private void recordInvalidIcPhone() {
        validIcPhone = false;
    }

    private void recordPasswordMatch() {
        passwordMatch = true;
    }

    private void recordPasswordMismatch() {
        passwordMatch = false;
    }

    public boolean previousAttemptFailed() {
        return failedAttempt;
    }

    public boolean isValidEmail() {
        return validEmail;
    }

    public boolean isNewEmail() {
        return newEmail;
    }

    public boolean isValidPhone() {
        return validPhone;
    }

    public boolean isValidIcPhone() {
        return validIcPhone;
    }

    public boolean doPasswordsMatch() {
        return passwordMatch;
    }

    public PatientBean getPatientBean() {
        return patientBean;
    }

    public HealthRecord getHealthRecord() {
        return healthRecord;
    }

    public long getMID() {
        return preRegisteredMID;
    }

    /**
     * Check if the format of the email is valid using regex.
     *
     * @param emailAddress The email to check.
     */
    private boolean isValidEmailFormat(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(emailAddress).matches();
    }

    /**
     * Check if the format of the phone number is valid using regex.
     *
     * @param phoneNumber The email to check.
     */
    private boolean isValidPhoneFormat(String phoneNumber) {
        String emailRegex = "(?:\\d{3}-){2}\\d{4}";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(phoneNumber).matches();
    }

    /**
     * Verify that a patient can be preregistered with the given input.
     *
     * @param patientBean  Information about the patient, as well as login.
     * @param healthRecord For height, weight, and smoker status.
     */
    private boolean verifyInput(PatientBean patientBean, HealthRecord healthRecord) throws DBException {
        boolean isCorrectInput = true;

        // If the email is invalid or already exists,
        // the user is prompted with an error message.
        // (Search both registered and pre-registered emails.)

        if (isValidEmailFormat(patientBean.getEmail())) {
            recordValidEmail();
            boolean isNewEmail = checkNewEmail(patientBean.getEmail());
            if (!isNewEmail) {
                // Email already exists in the database
                recordExistingEmail();
                isCorrectInput = false;
            } else {
                recordNewEmail();
            }
        } else {
            // Email format is wrong
            recordInvalidEmail();
            isCorrectInput = false;
        }

        // Handle phone matching
        if (patientBean.getPhone().equals("") || isValidPhoneFormat(patientBean.getPhone())) {
            recordValidPhone();
        } else {
            recordInvalidPhone();
            isCorrectInput = false;
        }
        if (patientBean.getIcPhone().equals("") || isValidPhoneFormat(patientBean.getIcPhone())) {
            recordValidIcPhone();
        } else {
            recordInvalidIcPhone();
            isCorrectInput = false;
        }

        // Handle password matching
        if (!patientBean.getPassword().equals(patientBean.getConfirmPassword())) {
            recordPasswordMismatch();
            isCorrectInput = false;
        } else {
            recordPasswordMatch();
        }

        if (isCorrectInput) {
            failedAttempt = false;
        }

        return isCorrectInput;
    }

    /**
     * PreRegister the user by adding the given information to the auth, patients, personalhealthinformation tables.
     *
     * @param patientBean  Information about the patient, as well as login.
     * @param healthRecord For height, weight, and smoker status.
     */
    public boolean preRegister(PatientBean patientBean, HealthRecord healthRecord) throws FormValidationException, ITrustException, DBException {
        new AddPatientValidator().validate(patientBean);
        long newMID = patientDAO.addEmptyPatient();
        patientBean.setMID(newMID);
        String pwd = authDAO.addUser(newMID, Role.PREREGISTERED, patientBean.getPassword());
        patientBean.setPassword(pwd);
        long adminMID = 9000000001L;
        patientDAO.editPatient(patientBean, adminMID);
        // TODO: Possible create our own access to personalhealthinformation
        // instead of using HealthRecord
        healthRecord.setPatientID(newMID);
        healthRecord.setPersonnelID(adminMID);
        healthRecord.setOfficeVisitDateStr("01/01/1970");
        healthRecordsDAO.add(healthRecord);
        preRegisteredMID = newMID;
        return true;
    }

    /**
     * Attempt to preregister a patient with the given information.
     *
     * @param patientBean  Information about the patient, as well as login.
     * @param healthRecord For height, weight, and smoker status.
     */
    public boolean attemptPreRegistration(PatientBean patientBean, HealthRecord healthRecord) throws FormValidationException, ITrustException, DBException {
        this.patientBean = patientBean;
        this.healthRecord = healthRecord;

        failedAttempt = true;
        boolean isCorrectInput = verifyInput(patientBean, healthRecord);
        if (isCorrectInput) {
            return preRegister(patientBean, healthRecord);
        } else {
            return false;
        }
    }
}
