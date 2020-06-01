<%@page import="java.sql.SQLException"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.exception.DBException"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPersonnelAction"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPatientAction"%>
<%@ page import="edu.ncsu.csc.itrust.action.EditRepresentativesAction" %>
<%@ page import="edu.ncsu.csc.itrust.exception.ITrustException" %>

<script src="/iTrust/DataTables/media/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery.fn.dataTableExt.oSort['lname-asc'] = function (x, y) {
		var a = x.split(" ");
		var b = y.split(" ");
		return ((a[1] < b[1]) ? -1 : ((a[1] > b[1]) ? 1 : 0));
	};

	jQuery.fn.dataTableExt.oSort['lname-desc'] = function (x, y) {
		var a = x.split(" ");
		var b = y.split(" ");
		return ((a[1] < b[1]) ? 1 : ((a[1] > b[1]) ? -1 : 0));
	};
</script>
<script type="text/javascript">
	$(document).ready(function () {
		$("#mailbox").dataTable({
			"aaColumns": [[2, 'dsc']],
			"aoColumns": [{"sType": "lname"}, null, null, {"bSortable": false}],
			"sPaginationType": "full_numbers"
		});
	});
</script>
<style type="text/css" title="currentStyle">
	@import "/iTrust/DataTables/media/css/demo_table.css";
</style>

<%
	// Filter code mostly comes from auth/patient/messageInbox.jsp

boolean outbox=(Boolean)session.getAttribute("outbox");
boolean isHCP=(Boolean)session.getAttribute("isHCP");

String pageName="messageInbox.jsp";
if(outbox){
	pageName="messageOutbox.jsp";
}
	

DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

ViewMyMessagesAction viewMyMessageAction = new ViewMyMessagesAction(prodDAO, loggedInMID);

EditPatientAction editPatientAction = null;
EditPersonnelAction editPersonnelAction = null;
boolean isPatient, isPersonnel;
isPatient = isPersonnel = true;
try {
	editPatientAction = new EditPatientAction(prodDAO, loggedInMID, loggedInMID.toString());
} catch (ITrustException e){
	isPatient = false;
}
try {
	editPersonnelAction = new EditPersonnelAction(prodDAO, loggedInMID, loggedInMID.toString());
} catch (ITrustException e){
	isPersonnel = false;
}

String sortBy = request.getParameter("sortBy");
String sortDirection = request.getParameter("sortDirection");
	
if (sortBy == null) {
	sortBy = "";
}
if (sortDirection == null) {
	sortDirection = "";
}

List<MessageBean> messages = null;

try{
	if (sortBy.equalsIgnoreCase("recipient/sender")) {
		if (sortDirection.equalsIgnoreCase("ascending")) {
			messages = outbox? viewMyMessageAction.getAllMySentMessagesNameAscending(): viewMyMessageAction.getAllMyMessagesNameAscending();
		} else if (sortDirection.equalsIgnoreCase("descending")) {
			messages = outbox? viewMyMessageAction.getAllMySentMessagesNameDescending(): viewMyMessageAction.getAllMyMessagesNameDescending();
		}
	} else if (sortBy.equalsIgnoreCase("timestamp")) {
		if (sortDirection.equalsIgnoreCase("descending")) {
			messages = outbox? viewMyMessageAction.getAllMySentMessagesTimeAscending(): viewMyMessageAction.getAllMyMessagesTimeAscending();
		} else if (sortDirection.equalsIgnoreCase("ascending")) {
			messages = outbox? viewMyMessageAction.getAllMySentMessagesTimeDescending(): viewMyMessageAction.getAllMyMessagesTimeDescending();
		}
	}

	if (messages == null) {
		messages = outbox? viewMyMessageAction.getAllMySentMessages(): viewMyMessageAction.getAllMyMessages();
	}
} catch(SQLException | DBException se) {
	out.println(se);
}

	//Edit Filter backend
	boolean editing = false;
	String headerMessage = "";
	String[] fields = new String[6];
	if(request.getParameter("edit") != null && request.getParameter("edit").equals("true")) {
		editing = true;

		int i;
		for(i=0; i<6; i++) {
			fields[i] = "";
		}

		if(request.getParameter("cancel") != null)
			response.sendRedirect(pageName);
		else if(request.getParameter("test") != null || request.getParameter("save") != null) {
			boolean error = false;
			String nf = "";
			nf += request.getParameter("sender").replace(",","")+",";
			nf += request.getParameter("subject").replace(",","")+",";
			nf += request.getParameter("hasWords").replace(",","")+",";
			nf += request.getParameter("notWords").replace(",","")+",";
			nf += request.getParameter("startDate").replace(",","")+",";
			nf += request.getParameter("endDate");

			//Validate Filter
			nf = viewMyMessageAction.validateAndCreateFilter(nf); // TODO (done) UC30 S7 Validate filter
			if(nf.startsWith("Error")) {
				error = true;
				headerMessage = nf;
			}

			if(!error) {
				if(request.getParameter("test") != null) {
					response.sendRedirect(pageName+"?edit=true&testFilter="+nf);
				} else if(request.getParameter("save") != null) {
					if (editPatientAction != null) {
						editPatientAction.editMessageFilter(nf);
					}

					if (editPersonnelAction != null) {
						editPersonnelAction.editMessageFilter(nf, loggedInMID);
					}
					/*
					FIXME (done)
						UC30 S7
						store / update filter using editMessageFilter();
					*/
					response.sendRedirect(pageName+"?filter=true");
				}
			}
		}

		if(request.getParameter("testFilter") != null) {
			String filter = request.getParameter("testFilter");
			String[] f = filter.split(",", -1);
			for(i=0; i<6; i++) {
				try {
					fields[i] = f[i];
				} catch(ArrayIndexOutOfBoundsException e) {
					//do nothing
				}
			}
		} else {
			String filter = "";
			if (isPatient) {
				filter = editPatientAction.getAuthDAO().getMessageFilter(loggedInMID);
			}
			if (isPersonnel) {
				filter = editPersonnelAction.getAuthDAO().getMessageFilter(loggedInMID);
			}
			/*
			FIXME (done)
				UC30 S7
			 	get filter from either editPatientAction or editPersonnelAction;
			 	Consider moving this function to a general "user bean"
			*/
			if(!filter.equals("")) {
				String[] f = filter.split(",", -1);
				for(i=0; i<6; i++) {
					try {
						fields[i] = f[i];
					} catch(ArrayIndexOutOfBoundsException e) {
						//do nothing
					}
				}
			}
		}
	}

	//Filters Messages
	boolean is_filtered = false;
	if((request.getParameter("filter") != null && request.getParameter("filter").equals("true")) || request.getParameter("testFilter") != null) {
		String filter = "";
		if(request.getParameter("testFilter") != null) {
			filter = request.getParameter("testFilter");
		} else {
			if (isPatient) {
				filter = editPatientAction.getAuthDAO().getMessageFilter(loggedInMID);
			}

			if (isPersonnel) {
				filter = editPersonnelAction.getAuthDAO().getMessageFilter(loggedInMID);
			}
		}
		if(!filter.equals("") && !filter.equals(",,,,,")) {
			List<MessageBean> filtered = viewMyMessageAction.filterMessages(messages, filter);
			messages = filtered;
			is_filtered = true;
		}
	}


	session.setAttribute("messages", messages);



if (sortBy != null 
    && !sortBy.equalsIgnoreCase("") 
    && sortDirection != null 
    && !sortDirection.equalsIgnoreCase("")) 
{
%>

	<form method="post" action="<%=pageName%><%= StringEscapeUtils.escapeHtml("" + (is_filtered?"?filter=true":"" )) %>" id="formSelectFlow">
	<%-- <form viewMyMessageAction="<%=pageName%>" method="post" id="formSelectFlow"> --%>
		<table>
			<tr>
				<td>
        			<select name="sortBy" style="font-size:10" >
		<%
			for(String sType : viewMyMessageAction.getSortTypes()) {
				if (sType.equalsIgnoreCase(sortBy)) {
		%>
			<option selected="selected" value="<%=sType%>"><%= StringEscapeUtils.escapeHtml("" + sType) %></option>
		<% } else { %>
			<option value="<%=sType%>"><%= StringEscapeUtils.escapeHtml("" + sType) %></option>
		<% }
	} %>
        			</select>
				</td>

				<td>
        			<select name="sortDirection" style="font-size:10" >

		<%
			for(String sDir :  viewMyMessageAction.getSortDirections()) {
				if (sDir.equalsIgnoreCase(sortDirection)) {
		%>
			<option selected="selected" value="<%=sDir%>"><%= StringEscapeUtils.escapeHtml("" + sDir) %></option>
		<% } else { %>
			<option value="<%=sDir%>"><%= StringEscapeUtils.escapeHtml("" + sDir) %></option>
		<% }

	} %>
        			</select>
				</td>
				<td>
        			<input type="submit" id="select_View" value="Sort">
				</td>
			</tr>
			<tr>
				<td><a href=<%=pageName+"?edit=true"%>>Edit Filter</a></td>
				<td><a href=<%=pageName+"?filter=true"%>>Apply Filter</a></td>
			</tr>
		</table>
	</form>


<%
} else {
%>
	<form method="post" action="<%=pageName%><%= StringEscapeUtils.escapeHtml("" + (is_filtered?"?filter=true":"" )) %>" id="formSelectFlow">
	<%-- <form viewMyMessageAction="<%=pageName%>" method="post" id="formSelectFlow"> --%>
		<table>
			<tr>
				<td>
					<select name="sortBy" style="font-size:10" >
							<option value="">Sort By</option>
							<option value="timestamp"> Timestamp </option>
							<option value="recipient/sender"> Recipient/Sender </option>
					</select>
				</td>
				<td>
					<select name="sortDirection" style="font-size:10" >
							<option value="">By Order Of</option>
							<option value="ascending"> Ascending </option>
							<option value="descending"> Descending </option>
					</select>
				</td>
				<td>
					<input type="submit" id="select_View" value="Sort">
				</td>
			</tr>
			<tr>
				<td><a href=<%=pageName+"?edit=true"%>>Edit Filter</a></td>
				<td><a href=<%=pageName+"?filter=true"%>>Apply Filter</a></td>
			</tr>
		</table>
	</form>

<%
	if(editing) {
%>
<div class="filterEdit">
	<div align="center">
		<span style="font-size: 13pt; font-weight: bold;">Edit Message Filter</span>
		<%= headerMessage.equals("") ? "" : "<br /><span class=\"iTrustMessage\">"+headerMessage+"</span><br /><br />" %>
		<form method="post" action=<%=pageName+"?edit=true"%>>
			<table>
				<tr style="text-align: right;">
					<td>
						<label for="sender">Sender: </label>
						<input type="text" name="sender" id="sender" value="<%= StringEscapeUtils.escapeHtml("" + (fields[0] )) %>" />
					</td>
					<td style="padding-left: 10px; padding-right: 10px;">
						<label for="hasWords">Has the words: </label>
						<input type="text" name="hasWords" id="hasWords" value="<%= StringEscapeUtils.escapeHtml("" + (fields[2] )) %>" />
					</td>
					<td>
						<label for="startDate">Start Date: </label>
						<input type="text" name="startDate" id="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (fields[4] )) %>" />
						<input type="button" value="Select Date" onclick="displayDatePicker('startDate');" />
					</td>
				</tr>
				<tr style="text-align: right;">
					<td>
						<label for="subject">Subject: </label>
						<input type="text" name="subject" id="subject" value="<%= StringEscapeUtils.escapeHtml("" + (fields[1] )) %>" />
					</td>
					<td style="padding-left: 10px; padding-right: 10px;">
						<label for="notWords">Does not have the words: </label>
						<input type="text" name="notWords" id="notWords" value="<%= StringEscapeUtils.escapeHtml("" + (fields[3] )) %>" />
					</td>
					<td>
						<label for="endDate">End Date: </label>
						<input type="text" name="endDate" id="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (fields[5] )) %>" />
						<input type="button" value="Select Date" onclick="displayDatePicker('endDate');" />
					</td>
				</tr>
				<tr style="text-align: center;">
					<td colspan="3">
						<input type="submit" name="test" value="Test Filter" />
						<input type="submit" name="save" value="Save" />
						<input type="submit" name="cancel" value="Cancel" />
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<br />
<%
	}

%>





<% 
}
		// Messages have been filtered before being displayed
	if(messages != null && messages.size() > 0) { %>

<table id="mailbox" class="display fTable">
	<thead>		
		<tr>
			<th><%= outbox?"Receiver":"Sender" %></th>
			<th>Subject</th>
			<th><%= outbox?"Sent":"Received" %></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
	<% 
	int index=-1;
	for(MessageBean message : messages) {
		String style = "";
		if(message.getRead() == 0 && !outbox) {
			style = "style=\"font-weight: bold;\"";
		}

		if(!outbox || message.getOriginalMessageId()==0){
			index ++; 
			String primaryName = viewMyMessageAction.getName(outbox?message.getTo():message.getFrom());
			List<MessageBean> ccs = viewMyMessageAction.getCCdMessages(message.getMessageId());
			String ccNames = "";
			int ccCount = 0;
			for(MessageBean cc:ccs){
				ccCount++;
				long ccMID = cc.getTo();
				ccNames += viewMyMessageAction.getPersonnelName(ccMID) + ", ";
			}
			ccNames = ccNames.length() > 0 ? ccNames.substring(0, ccNames.length()-2) : ccNames;
			String toString = primaryName;
			if(ccCount>0){
				String ccNameParts[] = ccNames.split(",");
				toString = toString + " (CC'd: ";
				for(int i = 0; i < ccNameParts.length-1; i++) {
					toString += ccNameParts[i] + ", ";
				}
				toString += ccNameParts[ccNameParts.length - 1] + ")";
			}			
			%>					
				<tr <%=style%>>
					<td><%= StringEscapeUtils.escapeHtml("" + ( toString)) %></td>
					<td><%= StringEscapeUtils.escapeHtml("" + ( message.getSubject() )) %></td>
					<td><%= StringEscapeUtils.escapeHtml("" + ( dateFormat.format(message.getSentDate()) )) %></td>
					<td><a href="<%= outbox?"viewMessageOutbox.jsp?msg=" + StringEscapeUtils.escapeHtml("" + ( index )):"viewMessageInbox.jsp?msg=" + StringEscapeUtils.escapeHtml("" + ( index )) %>">Read</a></td>
				</tr>			
			<%
		}
		
	}	
	%>
	</tbody>
</table>
<%} else { %>
	<div>
		<i>You have no messages</i>
	</div>
<%} %>
