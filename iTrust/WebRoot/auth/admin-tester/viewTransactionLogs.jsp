<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust" %>
<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="edu.ncsu.csc.itrust.beans.TransactionAnonBean" %>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean" %>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.TransactionDAO" %>
<%@page import="edu.ncsu.csc.itrust.enums.Role" %>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="edu.ncsu.csc.itrust.enums.TransactionType" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO" %>


<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - View Transaction Logs";
%>

<%@include file="/header.jsp" %>

<%
    //log the page view
    loggingAction.logEvent(TransactionType.VIEW_TRANSACTION_LOGS, loggedInMID.longValue(), 0, "");

    TransactionDAO taDAO = new TransactionDAO(prodDAO);
    List<String> transactions = taDAO.getUsedTransactionTypes();
    List<String> allRoles = Role.getAllRoles();
    List<TransactionType> allTransactionTypes = taDAO.getAllTransactionTypes();

    String view = request.getParameter("viewSelect");

    //get form data
    String loggedInRoleStr = request.getParameter("loggedInRole");
    String secondaryRoleStr = request.getParameter("secondaryRole");
    String startDateStr = request.getParameter("startDate");
    String endDateStr = request.getParameter("endDate");
    String searchAllDatesStr = request.getParameter("searchAllDates");
    String transactionTypeStr = request.getParameter("transactionTypeCode");

    // null checks
    if (loggedInRoleStr == null) {
        loggedInRoleStr = "";
    }
    if (secondaryRoleStr == null) {
        secondaryRoleStr = "";
    }
    if (startDateStr == null) {
        startDateStr = "";
    }
    if (endDateStr == null) {
        endDateStr = "";
    }
    if (searchAllDatesStr == null) {
        searchAllDatesStr = "";
    }
    if (transactionTypeStr == null) {
        transactionTypeStr = "-1"; // set to -1 to search for all transaction types (default)
    }

    // Filter role strings to work when all roles are specified
    String loggedInRole = null;
    if (!loggedInRoleStr.equals("") && !loggedInRoleStr.equals("all")) {
        loggedInRole = loggedInRoleStr;
    }
    String secondaryRole = null;
    if (!secondaryRoleStr.equals("") && !secondaryRoleStr.equals("all")) {
        secondaryRole = secondaryRoleStr;
    }

    // Convert date strings to date objects, adjust values
    Date startDate = null;
    Date endDate = null;
    if (!searchAllDatesStr.equals("true") && (!startDateStr.equals("") || !endDateStr.equals(""))) {
        searchAllDatesStr = ""; // set this to anything other than "true" to get the checkbox to not be checked
        if (!startDateStr.equals("")) {
            startDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDateStr);
        }
        if (!endDateStr.equals("")) {
            endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDateStr);
        }
    } else {
        // assume that if no date info has been input, search over all dates
        startDateStr = "";
        endDateStr = "";
        searchAllDatesStr = "true";
    }

    // Convert transaction codes into TransactionType objects
    TransactionType transactionType = null;
    int transactionTypeCode = -1;
    if (!transactionTypeStr.equals("-1")) {
        transactionTypeCode = Integer.parseInt(transactionTypeStr);
        transactionType = TransactionType.parse(transactionTypeCode);
    }

    // Get dataset
    List<TransactionAnonBean> anonTransactions = taDAO.getTransactionsAnon(loggedInRole, secondaryRole, transactionType, startDate, endDate);

%>
<br />
<form action="viewTransactionLogs.jsp" method="post" id="formMain">
    <table class="fTable" align="center" id="TransactionsSelectionsTable">
        <tr>
            <th colspan="5">View Transaction Logs</th>
        </tr>
        <tr>
            <td>Logged-In Role:</td>
            <td>
                <select name="loggedInRole" style="font-size:10" >
                    <% if (loggedInRoleStr.equals("all")) { %>
                    <option selected="selected" value="all">-- All Roles --</option>
                    <% } else { %>
                    <option value="all">-- All Roles --</option>
                    <% }
                        String currRole;
                        for (String r : allRoles) {
                            currRole = StringEscapeUtils.escapeHtml("" + r);
                            if (loggedInRoleStr.equals(currRole)) { %>
                    <option selected="selected" value="<%=currRole%>"><%=currRole%></option>
                    <% } else { %>
                    <option value="<%=currRole%>"><%=currRole%></option>
                    <% }
                    } %>
                </select>
            </td>
            <td>Secondary User Role:</td>
            <td colspan="2">
                <select name="secondaryRole" style="font-size:10" >
                    <% if (secondaryRoleStr.equals("all")) { %>
                    <option selected="selected" value="all">-- All Roles --</option>
                    <% } else { %>
                    <option value="all">-- All Roles --</option>
                    <% }
                        for (String r : allRoles) {
                            currRole = StringEscapeUtils.escapeHtml("" + r);
                            if (secondaryRoleStr.equals(currRole)) { %>
                    <option selected="selected" value="<%=currRole%>"><%=currRole%></option>
                    <% } else { %>
                    <option value="<%=currRole%>"><%=currRole%></option>
                    <% }
                    } %>
                </select>
            </td>
        </tr>
        <tr>
            <td>Start Date:</td>
            <td>
                <input name="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (startDateStr)) %>" size="10">
                <input type=button value="Select Date" onclick="displayDatePicker('startDate');">
            </td>
            <td>End Date:</td>
            <td>
                <input name="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (endDateStr)) %>" size="10">
                <input type=button value="Select Date" onclick="displayDatePicker('endDate');">
            </td>
            <td>
                <% if (searchAllDatesStr.equals("true")) { %>
                <input type="checkbox" checked="checked" name="searchAllDates" value="true">Search over all dates<br>
                <% } else { %>
                <input type="checkbox" name="searchAllDates" value="true">Search over all dates<br>
                <% } %>
            </td>
        </tr>
        <tr>
            <td>Transaction Type Name:</td>
            <td colspan="4">
                <select name="transactionTypeCode" style="font-size:10" >
                    <% if (transactionTypeCode == -1) { %>
                    <option selected="selected" value="-1">-- All Transactions --</option>
                    <% } else { %>
                    <option value="-1">-- All Transactions --</option>
                    <% }
                        int transactionCode;
                        for (String transaction : transactions) {
                            transactionCode = Integer.parseInt(transaction.substring(0, 4));
                            if (transactionTypeCode == transactionCode) { %>
                    <option selected="selected" value="<%=transactionCode%>"><%=transaction%></option>
                    <% } else { %>
                    <option value="<%=transactionCode%>"><%=transaction%></option>
                    <% }
                    } %>
                </select>
            </td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: center;"><input type="submit" id="view" name="viewSelect" value="View"></td>
            <td colspan="3" style="text-align: center;"><input type="submit" id="summarize" name="viewSelect" value="Summarize"></td>
        </tr>
    </table>
</form>
<br />

<% if (view != null && !view.equalsIgnoreCase("")) {
    if (view.equalsIgnoreCase("View")) { %>
        <table class="fTable" align="center" id="TransactionsViewTable">
            <tr>
                <th>Logged-In User Role</th>
                <th>Secondary User Role</th>
                <th>Transaction Type Name</th>
                <th>Additional Information</th>
                <th>Time Stamp</th>
            </tr>
            <% for (TransactionAnonBean ta : anonTransactions) { %>
                <tr>
                    <td><%=ta.getRoleOne()%></td>
                    <td><%=ta.getRoleTwo()%></td>
                    <td><%=ta.getTransactionType().getDescription()%></td>
                    <td><%=ta.getAddedInfo()%></td>
                    <td><%=ta.getTimeLogged()%></td>
                </tr>
            <% } %>
        </table>
    <% } else if (view.equalsIgnoreCase("Summarize")) {
        %>

        <%--Display Bar Charts--%>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <div align="center">
            <div id="chart_first" style="width: 800px"></div>
            <div id="chart_second" style="width: 800px"></div>
            <div id="chart_third" style="height: 300px;width: 800px"></div>
            <div id="chart_fourth" style="height: 500px;width: 800px"></div>
        </div>


        <script>
            google.charts.load('current', {packages: ['corechart', 'bar']});
            google.charts.setOnLoadCallback(drawFirstChart);
            google.charts.setOnLoadCallback(drawSecondChart);
            google.charts.setOnLoadCallback(drawThirdChart);
            google.charts.setOnLoadCallback(drawFourthChart);

            function drawFirstChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Logged-in User Role');
                data.addColumn('number', '# Transaction Logs');

                // Retrieve info for rendering graphs from anonTransactions
                var info = [];
                <%  SortedMap<String, Integer> mpFirst = new TreeMap<String, Integer>();
                for (TransactionAnonBean trans : anonTransactions) {
                    String key = trans.getRoleOne().toString();
                    if (mpFirst.containsKey(key)) mpFirst.put(key, mpFirst.get(key) + 1);
                    else mpFirst.put(key, 1);
                }
                %>
                <% for (SortedMap.Entry<String, Integer> entry : mpFirst.entrySet()) {%>
                info.push(["<%=entry.getKey()%>", <%=entry.getValue()%>]);
                <%}%>
                data.addRows(info);

                // Set up graph options
                var options = {
                    title: 'Logged-in User Roles v.s. Transaction Logs',
                    colors: ['#B8352D'],
                    legend: 'none',
                    hAxis: {title: 'Logged-in User Roles'},
                    vAxis: {title: '# Transaction Logs'}
                };
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_first'));
                chart.draw(data, options);
            }

            function drawSecondChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Secondary User Role');
                data.addColumn('number', '# Transaction Logs');

                // Retrieve info for rendering graphs from anonTransactions
                var info = [];
                <%  SortedMap<String, Integer> mpSecond = new TreeMap<String, Integer>();
                for (TransactionAnonBean trans : anonTransactions) {
                    // Ignore situation where we do not have second role
                    if (trans.getRoleTwo() == null) continue;
                    String key = trans.getRoleTwo().toString();
                    if (mpSecond.containsKey(key)) mpSecond.put(key, mpSecond.get(key) + 1);
                    else mpSecond.put(key, 1);
                }
                %>
                <% for (SortedMap.Entry<String, Integer> entry : mpSecond.entrySet()) {%>
                info.push(["<%=entry.getKey()%>", <%=entry.getValue()%>]);
                <%}%>
                data.addRows(info);

                // Set up graph options
                var options = {
                    title: 'Secondary Roles v.s. Transaction Logs',
                    colors: ['#B8352D'],
                    legend: 'none',
                    hAxis: {title: 'Secondary User Roles'},
                    vAxis: {title: '# Transaction Logs'}
                };
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_second'));
                chart.draw(data, options);
            }

            function drawThirdChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Date');
                data.addColumn('number', '# Transaction Logs');

                // Retrieve info for rendering graphs from anonTransactions
                var info = [];
                <%  SortedMap<String, Integer> mpThird = new TreeMap<String, Integer>();
                for (TransactionAnonBean trans : anonTransactions) {
                    int year = trans.getTimeLogged().getYear() + 1900;
                    int month = trans.getTimeLogged().getMonth() + 1;
                    String key = "" + year + "/" + (month < 10 ? "0" : "") + month;
                    if (mpThird.containsKey(key)) mpThird.put(key, mpThird.get(key) + 1);
                    else mpThird.put(key, 1);
                }
                %>
                <% for (SortedMap.Entry<String, Integer> entry : mpThird.entrySet()) {%>
                info.push(["<%=entry.getKey()%>", <%=entry.getValue()%>]);
                <%}%>
                data.addRows(info);

                // Set up graph options
                var options = {
                    title: 'Date v.s. Transaction Logs',
                    colors: ['#B8352D'],
                    legend: 'none',
                    chartArea: {top: 30, height: '50%'},
                    hAxis: {
                        title: 'Date',
                        viewWindowMode: 'pretty', slantedText: true
                    },
                    vAxis: {title: '# Transaction Logs'}
                };
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_third'));
                chart.draw(data, options);
            }

            function drawFourthChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Transaction Types');
                data.addColumn('number', '# Transaction Logs');

                // Retrieve info for rendering graphs from anonTransactions
                var info = [];
                <%  SortedMap<String, Integer> mpFourth = new TreeMap<String, Integer>();
                for (TransactionAnonBean trans : anonTransactions) {
                    String key = trans.getTransactionType().toString();
                    if (mpFourth.containsKey(key)) mpFourth.put(key, mpFourth.get(key) + 1);
                    else mpFourth.put(key, 1);
                }
                %>
                <% for (SortedMap.Entry<String, Integer> entry : mpFourth.entrySet()) {%>
                info.push(["<%=entry.getKey()%>", <%=entry.getValue()%>]);
                <%}%>
                data.addRows(info);

                // Set up graph options
                var options = {
                    title: 'Transaction Types v.s. Transaction Logs',
                    colors: ['#B8352D'],
                    legend: 'none',
                    chartArea: {top: 50, height: '40%'},
                    hAxis: {
                        title: 'Transaction Types',
                        viewWindowMode: 'pretty', slantedText: true
                    },
                    vAxis: {title: '# Transaction Logs'}
                };
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_fourth'));
                chart.draw(data, options);
            }

        </script>
        <br />
    <% }
} %>

<%@include file="/footer.jsp" %>
