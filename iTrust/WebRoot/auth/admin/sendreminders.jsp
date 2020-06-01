<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="java.net.URLEncoder" %>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.util.ArrayList" %>
<%@page import="edu.ncsu.csc.itrust.*"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@ page import="java.text.ParseException" %>

<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.action.SendReminderAction"%>


<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Send Reminders";
%>

<%@include file="/header.jsp" %>
    <h2>Send reminders</h2>


<form id="mainForm" action="sendreminders.jsp" method="post">
<input type="hidden" name="formIsFilled" value="true"><br />
<table cellspacing=0 align=center cellpadding=0>
	<tr>
		<td valign=top>
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan="4">Send Reminders</th>
			</tr>		
			
			<tr>
				<td colspan="4">Fill the blank with number of days to remind in advance</td>
			</tr>
			<tr>
				<td colspan="4"><div align="center"><input name="days" type="text"></div></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<br />
<div align=center>
	<input type="submit" name="action"
		style="font-size: 16pt; font-weight: bold;" value="Send Appointment Reminders"><br /><br />
</div>
</form>


			

 <%
    String days = request.getParameter("days");
    if (days != null){
        int number_days = Integer.parseInt(days);
        SendReminderAction action = new SendReminderAction(prodDAO,9000000009L);
        try{
            action.sendReminder(number_days,9000000009L);
            %> Reminders sent successfully <%
        } catch (Exception e){
            %>
			<div align=center>
				<span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
			</div>
            <%
        }
    }
%>

<%@include file="/footer.jsp"%>

