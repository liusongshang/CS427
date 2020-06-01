<%--
  Handle the preregister form submission
--%>

<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ResourceBundle" %>

<%@page import="edu.ncsu.csc.itrust.beans.HealthRecord" %>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean" %>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType" %>

<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - Pre-register";
%>

<%

    String preFirstName = request.getParameter("pre_firstname");
    String preLastName = request.getParameter("pre_lastname");
    String preEmail = request.getParameter("pre_email");
    String prePassword = request.getParameter("pre_password");
    String preVerify = request.getParameter("pre_verify");
    String preAddress1 = request.getParameter("pre_address_1");
    String preAddress2 = request.getParameter("pre_address_2");
    String preCity = request.getParameter("pre_city");
    String preState = request.getParameter("pre_state");
    String preZip = request.getParameter("pre_zip");
    String prePhone = request.getParameter("pre_phone");
    String preHeightStr = request.getParameter("pre_height");
    String preWeightStr = request.getParameter("pre_weight");
    String preSmokerStr = request.getParameter("pre_smoker");
    String insuranceName = request.getParameter("insurance_name");
    String insuranceAddress1 = request.getParameter("insurance_address_1");
    String insuranceAddress2 = request.getParameter("insurance_address_2");
    String insuranceCity = request.getParameter("insurance_city");
    String insuranceState = request.getParameter("insurance_state");
    String insuranceZip = request.getParameter("insurance_zip");
    String insurancePhone = request.getParameter("insurance_phone");

    double preHeight = 0.0;
    if (preHeightStr != null && !preHeightStr.isEmpty()) {
        preHeight = Double.parseDouble(preHeightStr);
    }

    double preWeight = 0.0;
    if (preWeightStr != null && !preWeightStr.isEmpty()) {
        preWeight = Double.parseDouble(preWeightStr);
    }

    int preSmoker = 9;
    if (preSmokerStr != null) {
        preSmoker = 5;
    }

    // PatientBean
    PatientBean patientBean = new PatientBean();
    patientBean.setFirstName(preFirstName);
    patientBean.setLastName(preLastName);
    patientBean.setEmail(preEmail);
    patientBean.setPassword(prePassword);
    patientBean.setConfirmPassword(preVerify);

    if (preAddress1 != null) {
        patientBean.setStreetAddress1(preAddress1);
    }
    if (preAddress2 != null) {
        patientBean.setStreetAddress2(preAddress2);
    }
    if (preCity != null) {
        patientBean.setCity(preCity);
    }
    if (preState != null && !preState.isEmpty()) {
        patientBean.setState(preState);
    }
    if (preZip != null) {
        patientBean.setZip(preZip);
    }
    if (prePhone != null) {
        patientBean.setPhone(prePhone);
    }
    if (insuranceName != null) {
        patientBean.setIcName(insuranceName);
    }
    if (insuranceAddress1 != null) {
        patientBean.setIcAddress1(insuranceAddress1);
    }
    if (insuranceAddress2 != null) {
        patientBean.setIcAddress2(insuranceAddress2);
    }
    if (insuranceCity != null) {
        patientBean.setIcCity(insuranceCity);
    }
    if (insuranceState != null && !insuranceState.isEmpty()) {
        patientBean.setIcState(insuranceState);
    }
    if (insuranceZip != null) {
        patientBean.setIcZip(insuranceZip);
    }
    if (insurancePhone != null) {
        patientBean.setIcPhone(insurancePhone);
    }

    // HealthRecord bean
    HealthRecord healthRecord = new HealthRecord();
    healthRecord.setHeight(preHeight);
    healthRecord.setWeight(preWeight);
    healthRecord.setSmoker(preSmoker);

    boolean isCorrectInput = preRegisterAction.attemptPreRegistration(patientBean, healthRecord);
    if (!isCorrectInput) {
        pageContext.forward("/login.jsp");
    }

    long newMIDLong = preRegisterAction.getPatientBean().getMID();
    String newMID = "" + newMIDLong;

%>
<%@include file="/header.jsp" %>

<div align=center>
    <h1>Pre-Registration Successful!</h1>
    <div style="width: 50%; text-align:left;">You have been successfully
        pre-registered. Your MID (username) is <strong id="pre_new_mid"><%=newMID%>
        </strong>.
        You can log in with your MID and password and edit some personal
        information, but you will need a health care provider (HCP) to
        activate your account in order to use the iTrust medical system.
    </div>
</div>

<%
    loggingAction.logEvent(TransactionType.PATIENT_PREREGISTER, newMIDLong, 0, "");
%>

<%@include file="/footer.jsp" %>
