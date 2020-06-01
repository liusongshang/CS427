<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewCODTrendsReportAction"%>
<%@page import="edu.ncsu.csc.itrust.CauseOfDeathRankings"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>

<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - View Cause of Death Statistics";
%>

    <%@include file="/header.jsp" %>

<%
    //log the page view
    loggingAction.logEvent(TransactionType.VIEW_COD_TRENDS, loggedInMID.longValue(), 0, "");

    String view = request.getParameter("viewSelect");
    String genderView = request.getParameter("genderViewSelect");

    ViewCODTrendsReportAction cods = new ViewCODTrendsReportAction(prodDAO, loggedInMID);
    CauseOfDeathRankings AllCODs = null;
    CauseOfDeathRankings FemaleCODs = null;
    CauseOfDeathRankings MaleCODs = null;

    //get form data
    String startDate = request.getParameter("startDate");
    String endDate = request.getParameter("endDate");

    if (startDate == null)
        startDate = "";
    if (endDate == null)
        endDate = "";

    boolean yearError = false;
%>

<br />
<form action="viewCODStatistics.jsp" method="post" id="formMain">
    <input type="hidden" name="viewSelect" value="showTrends" />
    <table class="fTable" align="center" id="CODStatisticsSelectionTable">
        <tr>
            <th colspan="4">Cause of Death Statistics</th>
        </tr>
        <tr class="subHeader">
            <td>Start Date:</td>
            <td>
                <input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (startDate)) %>" size="10">
                <input type=button id="startDatePicker" value="Select Date" onclick="displayDatePicker('startDate');">
            </td>
            <td>End Date:</td>
            <td>
                <input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (endDate)) %>" size="10">
                <input type=button id="endDatePicker" value="Select Date" onclick="displayDatePicker('endDate');">
            </td>
        </tr>
        <tr>
            <td colspan="4" style="text-align: center;"><input type="submit" id="select_cod" value="View Statistics"></td>
        </tr>
    </table>

</form>

<br />

<% if (view != null && !view.equalsIgnoreCase("")) {
    if (view.equalsIgnoreCase("showTrends")) {
        // view statistics button has been pressed

        //try to get the statistics
	try {
        	AllCODs = cods.getRankedCausesOfDeathLHCP(startDate, endDate, "");
        	FemaleCODs = cods.getRankedCausesOfDeathLHCP(startDate, endDate, "Female");
        	MaleCODs = cods.getRankedCausesOfDeathLHCP(startDate, endDate, "Male");
	} catch (Exception e) { 
	%>
		<center><h2> Dates provided were incorrectly formatted or invalid. Please re-try search with valid input. </h2></center> 	
	<%
		yearError = true;
	}
%>

<%
if (yearError == false) {
%>
<center><h2>2 Most Common Causes of Death</h2></center>

<br />

<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">Your Patients (All Genders)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (AllCODs.getCauseOfDeathLHCP(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(AllCODs.getCauseOfDeathLHCP(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(AllCODs.getCauseOfDeathLHCP(i).getCauseOfDeath()) %></td>
            <td><%= AllCODs.getCauseOfDeathAll(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
        } %>
    </table>
</div>
<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">All Patients (All Genders)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (AllCODs.getCauseOfDeathAll(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(AllCODs.getCauseOfDeathAll(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(AllCODs.getCauseOfDeathAll(i).getCauseOfDeath()) %></td>
            <td><%= AllCODs.getCauseOfDeathAll(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
        } %>
    </table>
</div>

<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">Your Patients (Female Only)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (FemaleCODs.getCauseOfDeathLHCP(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(FemaleCODs.getCauseOfDeathLHCP(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(FemaleCODs.getCauseOfDeathLHCP(i).getCauseOfDeath()) %></td>
            <td><%= FemaleCODs.getCauseOfDeathLHCP(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
        } %>
    </table>
</div>
<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">All Patients (Female Only)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (FemaleCODs.getCauseOfDeathAll(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(FemaleCODs.getCauseOfDeathAll(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(FemaleCODs.getCauseOfDeathAll(i).getCauseOfDeath()) %></td>
            <td><%= FemaleCODs.getCauseOfDeathAll(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
        } %>
    </table>
</div>

<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">Your Patients (Male Only)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (MaleCODs.getCauseOfDeathAll(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(MaleCODs.getCauseOfDeathLHCP(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(MaleCODs.getCauseOfDeathLHCP(i).getCauseOfDeath()) %></td>
            <td><%= MaleCODs.getCauseOfDeathLHCP(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
        } %>
    </table>
</div>
<div class="col-sm-6" style="padding-bottom: 30px;">
    <table class="fTable" style="width:100%" align="center">
        <tr>
            <th colspan="3">All Patients (Male Only)</th>
        </tr>
        <tr class="subHeader" style="text-align:center;">
            <th>Diagnosis Code</th>
            <th>Diagnosis Name</th>
            <th>Quantity of Deaths</th>
        </tr>
        <% for (int i = 0; i < 2; i++) {
            if (MaleCODs.getCauseOfDeathAll(i) != null) { %>
        <tr style="text-align:center;">
            <td><%= StringEscapeUtils.escapeHtml(MaleCODs.getCauseOfDeathAll(i).getIcdCode()) %></td>
            <td><%= StringEscapeUtils.escapeHtml(MaleCODs.getCauseOfDeathAll(i).getCauseOfDeath()) %></td>
            <td><%= MaleCODs.getCauseOfDeathAll(i).getQuantityOfDeaths() %></td>
        </tr>
        <% } else { %>
        <tr style = "text-align:center;">
            <td> N/A </td>
            <td> N/A </td>
            <td> 0 </td>
        </tr>
        <% }
            } %>
    </table>
</div>

<%
		}
        }
} %>

<%@include file="/footer.jsp" %>
