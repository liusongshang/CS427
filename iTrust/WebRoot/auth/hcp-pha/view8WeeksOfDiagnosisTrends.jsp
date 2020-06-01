<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.action.ViewDiagnosisStatisticsAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.DiagnosisBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.DiagnosisStatisticsBean"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="java.util.*" %>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<% 
	//log the page view
	loggingAction.logEvent(TransactionType.DIAGNOSIS_TRENDS_VIEW, loggedInMID.longValue(), 0, "");
	
	Gson gsonObj = new Gson();
	Map<Object,Object> map = null;
	List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

	ViewDiagnosisStatisticsAction diagnoses = new ViewDiagnosisStatisticsAction(prodDAO);
	DiagnosisStatisticsBean dsBean = null;

	//get form data
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	
	String zipCode = request.getParameter("zipCode");
	if (zipCode == null)
		zipCode = "";
	
	String icdCode = request.getParameter("icdCode");
	
	//try to get the statistics. If there's an error, print it. If null is returned, it's the first page load
	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}
	
	if (startDate == null)
		startDate = "";
	if (endDate == null)
		endDate = "";
	if (icdCode == null)
		icdCode = "";

%>
<br />
<form action="viewDiagnosisStatistics.jsp" method="post" id="formMain">
<input type="hidden" name="viewSelect" value="trends" />
<table class="fTable" align="center" id="diagnosisStatisticsSelectionTable">
	<tr>
		<th colspan="4">Diagnosis Statistics</th>
	</tr>
	<tr class="subHeader">
		<td>Diagnosis:</td>
		<td>
			<select name="icdCode" style="font-size:10" >
			<option value="">-- None Selected --</option>
			<%for(DiagnosisBean diag : diagnoses.getDiagnosisCodes()) { %>
				<%if (diag.getICDCode().equals(icdCode)) { %>
					<option selected="selected" value="<%=diag.getICDCode()%>"><%= StringEscapeUtils.escapeHtml("" + (diag.getICDCode())) %>
					- <%= StringEscapeUtils.escapeHtml("" + (diag.getDescription())) %></option>
				<% } else { %>
					<option value="<%=diag.getICDCode()%>"><%= StringEscapeUtils.escapeHtml("" + (diag.getICDCode())) %>
					- <%= StringEscapeUtils.escapeHtml("" + (diag.getDescription())) %></option>
				<% } %>
			<%}%>
			</select>
		</td>
		<td>Zip Code:</td>
		<td ><input name="zipCode" value="<%= StringEscapeUtils.escapeHtml(zipCode) %>" /></td>
	</tr>
	<tr class="subHeader">
		<td>Start Date:</td>
		<td>
			<input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (startDate)) %>" size="10">
			<input type=button value="Select Date" onclick="displayDatePicker('startDate');">
		</td>
		<td>End Date:</td>
		<td>
			<input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (endDate)) %>" size="10">
			<input type=button value="Select Date" onclick="displayDatePicker('endDate');">
		</td>
	</tr>
	<tr>
		<td colspan="4" style="text-align: center;"><input type="submit" id="select_diagnosis" value="View Statistics"></td>
	</tr>
</table>	

</form>

<br />

<% if (dsBean != null) { 
	Date start = new SimpleDateFormat("MM/dd/yyyy").parse(startDate);
	Date end = new SimpleDateFormat("MM/dd/yyyy").parse(endDate);
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


	long zipCount = dsBean.getZipStats();
	long regionCount = dsBean.getRegionStats();
	long stateCount = dsBean.getStateStats();
	long allCount = dsBean.getOverallStats();

	String zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	String regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	String stateDate = "State (" + startDate + " - " + endDate + ")"; 
	String allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	String dataPoints = gsonObj.toJson(list);

%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	Calendar c = Calendar.getInstance();
	c.setTime(start);
	int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();

	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
	c = Calendar.getInstance();
	c.setTime(start);
	i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	c.add(Calendar.DATE, -i - 7);
	start = c.getTime();
	startDate = dateFormat.format(start);
	c.add(Calendar.DATE, 6);
	end = c.getTime();
	endDate = dateFormat.format(end);

	try{
		dsBean = diagnoses.getDiagnosisStatistics(startDate, endDate, icdCode, zipCode);
	} catch(FormValidationException e){
		e.printHTML(pageContext.getOut());
	}


	zipCount = dsBean.getZipStats();
	regionCount = dsBean.getRegionStats();
	stateCount = dsBean.getStateStats();
	allCount = dsBean.getOverallStats();
	
	zipDate = "Zipcode (" + startDate + " - " + endDate + ")"; 
	regionDate = "Region (" + startDate + " - " + endDate + ")"; 
	stateDate = "State (" + startDate + " - " + endDate + ")"; 
	allDate = "Overall (" + startDate + " - " + endDate + ")"; 
	
	map = new HashMap<Object,Object>(); map.put("label", (String) zipDate); map.put("y", (Long) zipCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) regionDate); map.put("y", (Long) regionCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) stateDate); map.put("y", (Long) stateCount); list.add(map);
	map = new HashMap<Object,Object>(); map.put("label", (String) allDate); map.put("y", (Long) allCount); list.add(map);
 
	dataPoints = gsonObj.toJson(list);
%>

<table class="fTable" align="center" id="diagnosisStatisticsTable">
<tr>
	<th>Diagnosis code</th>
	<th>Complete Zip</th>
	<th>Cases in Zipcode</th>
	<th>Cases in Region</th>
	<th>Cases in State</th>
	<th>Cases Overall</th>
	<th>Start Date</th>
	<th>End Date</th>
</tr>
<tr style="text-align:center;">
	<td><%= icdCode %></td>
	<td><%= zipCode %></td>
	<td><%= dsBean.getZipStats() %></td>
	<td><%= dsBean.getRegionStats() %></td>
	<td><%= dsBean.getStateStats() %></td>
	<td><%= dsBean.getOverallStats() %></td>
	<td><%= startDate %></td>
	<td><%= endDate %></td>
</tr>

</table>

<br />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() {

var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Diagnosis Trends"
	},
	subtitles: [{
		text: "Zipcode, Region, State, and Overall Trends"
	}],
	axisY: {
		title: "Number of Diagnoses",
		labelFormatter: addSymbols
	},
	data: [{
		type: "bar",
		indexLabel: "{y}",
		indexLabelFontColor: "#444",
		indexLabelPlacement: "inside",
		dataPoints: <%out.print(dataPoints);%>
	}]
});
chart.render();

function addSymbols(e) {
	var suffixes = ["", "K", "M", "B"];

	var order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
	if(order > suffixes.length - 1)
	order = suffixes.length - 1;

	var suffix = suffixes[order];
	return CanvasJS.formatNumber(e.value / Math.pow(1000, order)) + suffix;
}

}
</script>
</head>
<body>
<div id="chartContainer" style="height: 100%; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>

<%
} 
%>
<br />
<br />
