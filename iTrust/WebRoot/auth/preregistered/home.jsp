<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Pre-registered Patient Home";
loggingAction.logEvent(TransactionType.HOME_VIEW, loggedInMID, 0, "");
%>

<%@include file="/header.jsp" %>

<div style="text-align: center; height: 300px;">
	<h2>Welcome <%= StringEscapeUtils.escapeHtml("" + (userName )) %>!</h2>
	<div style="width: 50%; text-align:left;">You have been successfully
		pre-registered. You will need a health care provider (HCP) to
		activate your account in order to use the iTrust medical system.
	</div>
</div>

<%@include file="/footer.jsp" %>
