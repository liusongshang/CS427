<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust" %>
<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DateFormat"%>

<%@page import="edu.ncsu.csc.itrust.action.EditPHRAction"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.AllergyBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.FamilyMemberBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.HealthRecord"%>
<%@page import="edu.ncsu.csc.itrust.beans.MedicationBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.OfficeVisitBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.ProcedureBean"%>
<%@page import="edu.ncsu.csc.itrust.risk.RiskChecker"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Edit Personal Health Record";
%>

<%@include file="/header.jsp" %>
<itrust:patientNav thisTitle="Health Records" />
<%
PatientDAO c = new PatientDAO(prodDAO);
PersonnelDAO personnelDAO = new PersonnelDAO(prodDAO);
PersonnelBean personnelb = personnelDAO.getPersonnel(loggedInMID.longValue());
DateFormat df = DateFormat.getDateInstance();

String switchString = "";
if (request.getParameter("switch") != null) {
	switchString = request.getParameter("switch");
}

String relativeString = "";
if (request.getParameter("relative") != null) {
	relativeString = request.getParameter("relative");
}

String patientString = "";
if (request.getParameter("patient") != null) {
	patientString = request.getParameter("patient");
}

String pidString;
long pid = 0;

if (switchString.equals("true")) pidString = "";
else if (!relativeString.equals("")) {

	int relativeIndex = Integer.parseInt(relativeString);
	List<PatientBean> relatives = (List<PatientBean>) session.getAttribute("relatives");
	pid = relatives.get(relativeIndex).getMID();
	pidString = "" + pid;
	session.removeAttribute("relatives");
	session.setAttribute("pid", pidString);
}
else if (!patientString.equals("")) {

	int patientIndex = Integer.parseInt(patientString);
	List<PatientBean> patients = (List<PatientBean>) session.getAttribute("patients");
	pid = patients.get(patientIndex).getMID();
	pidString = "" + pid;
	session.removeAttribute("patients");
	session.setAttribute("pid", pidString);
}
else {
	if (session.getAttribute("pid") == null) {
		pid = 0;
		pidString = "";
	} else {
		pid = (long) Long.parseLong((String) session.getAttribute("pid"));
		pidString = ""+pid;
	}
}

if (pidString == null || 1 > pidString.length()) {
	response.sendRedirect("../getPatientID.jsp?forward=hcp-uap/editPHR.jsp");
	
   	return;
}
loggingAction.logEvent(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, loggedInMID.longValue(), pid, "");

//else {
//	session.removeAttribute("pid");
//}


EditPHRAction action = new EditPHRAction(prodDAO,loggedInMID.longValue(), pidString);
pid = action.getPid();
String confirm = "";
if(request.getParameter("addA") != null)
{
	try{
		confirm = action.updateAllergies(pid,request.getParameter("description"));
		loggingAction.logEvent(TransactionType.PATIENT_HEALTH_INFORMATION_EDIT, loggedInMID.longValue(), pid, "");
	} catch(Exception e)
	{
		confirm = e.getMessage();
	}
}

PatientBean patient = action.getPatient();
List<HealthRecord> records = action.getAllHealthRecords();
HealthRecord mostRecent = records.size() > 0 ? records.get(0) : null;
List<OfficeVisitBean> officeVisits = action.getAllOfficeVisits();
List<FamilyMemberBean> family = action.getFamily(); 
%>


<%@page import="edu.ncsu.csc.itrust.exception.NoHealthRecordsException"%><script type="text/javascript">
function showRisks(){
	document.getElementById("risks").style.display="inline";
	document.getElementById("riskButton").style.display="none";
}
</script>

<% if (!"".equals(confirm)) {%>
<span class="iTrustError"><%= StringEscapeUtils.escapeHtml("" + (confirm)) %></span><br />
<% } %>

<%         PatientBean p;
        if (request.getParameter("formIsFilled") != null && request.getParameter("formIsFilled").equals("true") &&
                        request.getParameter("understand") != null && request.getParameter("understand").equals("I UNDERSTAND")) {
                try {
              //          c.ActivatePrePatient(pid);
			action.activate();
                        loggingAction.logEvent(TransactionType.PRE_REGISTERED_ACTIVATE, loggedInMID.longValue(), Long.valueOf((String)session.getAttribute("pid")).longValue(), "");
                       // session.removeAttribute("pid");
		        
		 %>
        <br />
        <div align=center>
                <span class="iTrustMessage">Patient Successfully Activated</span>
        </div>
        <br />

<br />

<div align=center>
<a href="editPatient.jsp" style="text-decoration: none;">
                        <input type=button value="Edit" onClick="location='editPatient.jsp';">
                </a>
</div>

<br />

<%
                } catch (Exception e) {
%>
        <br />
        <div align=center>
                <span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
        </div>
        <br />
<%
                }
        } else {
                p = action.getPatient();

        if (request.getParameter("formIsFilled") != null && request.getParameter("formIsFilled").equals("true") &&
                        (request.getParameter("understand") == null || !request.getParameter("understand").equals("I UNDERSTAND"))) {
%>
                <br />
                <div align=center>
                        <span class="iTrustError">You must type "I UNDERSTAND" in the textbox.</span>
                </div>
                <br />
<%
        }

%>


<br />
<div align=center>
	<div style="margin-right: 10px;">
		<table class="fTable" align="center">
			<tr>
				<th colspan="2">Patient Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">Name:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getFullName())) %></td>
			</tr>
			<tr>
				<td  class="subHeaderVertical">Address:</td>
				<td > <%= StringEscapeUtils.escapeHtml("" + (patient.getStreetAddress1())) %><br />
				     <%="".equals(patient.getStreetAddress2()) ? "" : patient.getStreetAddress2() + "<br />"%>
				     <%= StringEscapeUtils.escapeHtml("" + (patient.getStreetAddress3())) %><br />									  
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getPhone())) %></td>
			</tr>
			<tr>
				<td class="subHeaderVertical" >Email:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getEmail())) %></td>
			</tr>
			<tr>
				<th colspan="2">Insurance Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical" >Provider Name:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getIcName())) %></td>
			</tr>
			<tr>
				<td  class="subHeaderVertical">Address:</td>
				<td > <%= StringEscapeUtils.escapeHtml("" + (patient.getIcAddress1())) %><br />
					<%="".equals(patient.getIcAddress2()) ? "" : patient.getIcAddress2() + "<br />"%>
					<%= StringEscapeUtils.escapeHtml("" + (patient.getIcAddress3())) %><br />							
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getIcPhone())) %></td>
			</tr>
	</div>
	<div style="margin-right: 10px;">
		<table class="fTable" align="center">
			<tr>
				<th colspan="2">Basic Health Records</th>
			</tr>
			<% if (null == mostRecent) { %>
			<tr><td colspan=2>No basic health records are on file for this patient</td></tr>
			<% } else {%>
			<tr>
				<td class="subHeaderVertical">Height:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getHeight())) %>in.</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Weight:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getWeight())) %>lbs.</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Smoker?:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getSmokingStatus()) + " - " + (mostRecent.getSmokingStatusDesc())) %></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Blood Pressure:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getBloodPressureN())) %>/<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getBloodPressureD())) %>mmHg</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Cholesterol:</td>
				<td >
				<table>
					<tr>
						<td style="text-align: right">HDL:</td>
						<td><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolHDL())) %> mg/dL</td>
					</tr>
					<tr>
						<td style="text-align: right">LDL:</td>
						<td><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolLDL())) %> mg/dL</td>
					</tr>
					<tr>
						<td style="text-align: right">Tri:</td>
						<td><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolTri())) %> mg/dL</td>
					</tr>
					<tr>
						<td style="text-align: right">Total:</td>
						<td>
							<span id="totalSpan" style="font-weight: bold;"><%= StringEscapeUtils.escapeHtml("" + (mostRecent.getTotalCholesterol())) %> mg/dL</span>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<% } //closing for "there is a most recent record for this patient" %>





<form id="activateForm" action=" viewprePHR.jsp" method="post">
<input type="hidden" name="formIsFilled" value="true"><br />
<table cellspacing=0 align=center cellpadding=0>
        <tr>
                <td valign=top>
            
		 <table class="fTable" align=center style="width: 350px;">
                        <tr>
                                <th colspan="4">Activate Patient</th>
                        </tr>
                        <tr>

                                <td class="subHeaderVertical">First Name:</td>
                                <td><%= StringEscapeUtils.escapeHtml("" + (p.getFirstName())) %></td>
                                <td class="subHeaderVertical">Last Name:</td>
                                <td><%= StringEscapeUtils.escapeHtml("" + (p.getLastName())) %></td>
                        </tr>
                        <tr>
                                <td colspan="4">Are you absolutely sure you want to activate this pre-register
                                patient?  This operation can only be undone by an administrator.  If
                                you are sure, type "I UNDERSTAND" into the box below and click the
                                button</td>
                        </tr>
                        <tr>
                                <td colspan="4"><div align="center"><input name="understand" type="text"></div></td>
                        </tr>
                </table>



		 </td>
        </tr>
</table>
<br />

         
<div align=center>
        <input type="submit" name="action"
                style="font-size: 16pt; font-weight: bold;" value="Activate"><br /><br />

</div>
<li><a href="/iTrust/auth/hcp-uap/deactivatePatient.jsp">Do you want to deactivate this patient?</a>
</form>
<% } %>
<br /><br /><br />
<%@include file="/footer.jsp" %>

