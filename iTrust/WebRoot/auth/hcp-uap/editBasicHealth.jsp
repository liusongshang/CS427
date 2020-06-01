<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.action.EditHealthHistoryAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.HealthRecord"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@page import="edu.ncsu.csc.itrust.beans.forms.HealthRecordForm"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Edit Basic Health Record";
%>

<%@include file="/header.jsp" %>
<itrust:patientNav thisTitle="Basic Health History" />
<%
/* Require a Patient ID first */
String pidString = (String)session.getAttribute("pid");
if (pidString == null || 1 > pidString.length()) {
	response.sendRedirect("/iTrust/auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp");
   	return;
}
//else {
//	session.removeAttribute("pid");
//}
EditHealthHistoryAction action = new EditHealthHistoryAction(prodDAO,loggedInMID.longValue(), pidString);
long pid = action.getPid();
String patientName = action.getPatientName();
Calendar currentDate = Calendar.getInstance();
currentDate.setTime(currentDate.getTime());
int ageinmonths = action.getPatientAgeInMonths(currentDate);
action.setPatientAge(action.getPatientAgeInMonths(currentDate));
String confirm = "";
if ("true".equals(request.getParameter("formIsFilled"))) {
	try { 
		confirm = action.addHealthRecord(pid, new BeanBuilder<HealthRecordForm>().build(request.getParameterMap(), new HealthRecordForm()));
		loggingAction.logEvent(TransactionType.PATIENT_HEALTH_INFORMATION_EDIT, loggedInMID.longValue(), pid, "");
		loggingAction.logEvent(TransactionType.EDIT_CURRENT_HEALTH_INFORMATION, loggedInMID.longValue(), pid, "");
	} catch(FormValidationException e){
%>
		<div align=center>
			<span class="iTrustError"><%e.printHTML(pageContext.getOut());%></span>
		</div>
		<br />
<%
	}
}
List<HealthRecord> records = action.getAllHealthRecords(pid);
HealthRecord mostRecent = (records.size() > 0) ? records.get(0) : new HealthRecord(); //for the default values
%>

<script type="text/javascript">
function showAddRow(){
	document.getElementById("addRow").style.display="inline";
	document.getElementById("addRowButton").style.display="none";
	document.forms[0].height.focus();
}

function showEditHeadCircumference(){
	document.getElementById("showHeadCircumference").style.display="inline";
	document.forms[0].height.focus();
}
</script>

<%
if (!"".equals(confirm)) {
%>
	<div align=center>
		<span class="iTrustMessage"><%= StringEscapeUtils.escapeHtml("" + (confirm)) %></span>
	</div>
<%
} else {
	loggingAction.logEvent(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, loggedInMID.longValue(), pid, "");
}
%>

</div>
<br />
<div id="addRow" style="display: inline;" align=center>
<form action="editBasicHealth.jsp" id="editHealth" name="editHealth" method="post">
<input type="hidden" name="formIsFilled" value="true">
<table class="fTable" align="center">
	<tr>
		<th colspan="2" style="background-color:silver;">Record Information</th>
	</tr>	
	<tr>
		<td class="subHeader">Height (in.):</td>
		<td ><input name="height"
			value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getHeight())) %>" style="width: 70px" type="text"
			maxlength="5"></td>
	</tr>
	<tr>
		<td class="subHeader">Weight (lbs.):</td>
		<td ><input name="weight"
			value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getWeight())) %>" style="width: 70px" type="text"
			maxlength="5"></td>
	</tr>
	<tr>
		<td class="subHeader">Smoker?: [Was last <%= StringEscapeUtils.escapeHtml("" + (mostRecent.getSmokingStatus())) %>]</td>
		<td >
		
      <select name="isSmoker" id="isSmoker" style="font-size:10px;">
                <%
                  String[] possibleSmokingOptions = new String[] {
                      " -- Please Select a Smoking Status -- ",
                      "1 - Current every day smoker",
                      "2 - Current some day smoker",
                      "3 - Former smoker",
                      "4 - Never smoker",
                      "5 - Smoker, current status unknown",
                      "9 - Unknown if ever smoked"
                  };
                  int[] possibleSmokingValues = new int[] {-1, 1, 2, 3, 4, 5, 9};
                  String smokingMenu = "";
                  int mostRecentSmokingValue = mostRecent.getSmokingStatus();
                  for (int index = 0; index < possibleSmokingValues.length; index++) {
                    smokingMenu += "<option value=\"" + possibleSmokingValues[index] + "\"";
                    if(mostRecentSmokingValue == possibleSmokingValues[index]) {
                      smokingMenu += " selected=\"selected\"";
                    }
                    smokingMenu += ">" + possibleSmokingOptions[index] + "</option>\n";
                  }
                %>
                <%= smokingMenu %>
        </select>
		</td>
	</tr>
	<tr>
		<td class="subHeader">Blood Pressure (mmHg):</td>
		<td >
			<input name="bloodPressureN" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getBloodPressureN())) %>" style="width: 70px" maxlength="3" type="text" />
			/ <input name="bloodPressureD" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getBloodPressureD())) %>" style="width: 70px" maxlength="3" type="text" />
		</td>
	</tr>
	<tr>
		<td class="subHeader">Cholesterol (mg/dL):</td>
		<td >
		<table>
			<tr>
				<td style="text-align: right">HDL:</td>
				<td><input name="cholesterolHDL" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolHDL())) %>" 
				style="width: 70px" maxlength="3" type="text"></td>
			</tr>
			<tr>
				<td style="text-align: right">LDL:</td>
				<td>
					<input name="cholesterolLDL" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolLDL())) %>" style="width: 70px" maxlength="3" type="text">
				</td>
			</tr>
			<tr>
				<td style="text-align: right">Tri:</td>
				<td>
					<input name="cholesterolTri" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getCholesterolTri())) %>" style="width: 70px" maxlength="3" type="text">
			    </td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="subHeader">Header Circumference</td>
		<td>
			<input name="headCircumference" value="<%= StringEscapeUtils.escapeHtml("" + (mostRecent.getHeadCircumference()))%>" style="width: 70px" maxlength="3" type="text" />
		</td>
	</tr>
	<tr>
		<td class="subHeader">Edit Reason:</td>
		<td ><input name="editReason"
					placeholder="Reason for editing" style="width: 300px" type="text"
					maxlength="1022"></td>
	</tr>
</table>
<br />
<input type="submit" value="Edit Record">
</form>
</div>

<br />
<br />
<br />

<%@include file="/footer.jsp" %>
