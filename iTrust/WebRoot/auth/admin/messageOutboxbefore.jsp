</* this file is mainly from the file  /auth/hcp-patient/messageOutbox.jsp */>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>

<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - View My Sent Messages";
session.setAttribute("outbox",true);
loggingAction.logEvent(TransactionType.OUTBOX_VIEW, loggedInMID.longValue(), 0L, "");

%>

<%@include file="/header.jsp" %>

<div align=center>
	<h2>My Sent Messages</h2>
	<%@include file="/auth/admin/messageoutbox.jsp" %>
</div>

<%@include file="/footer.jsp" %>

