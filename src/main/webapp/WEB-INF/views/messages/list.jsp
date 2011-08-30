<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>List of Message Resources</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
	<div>
	<div class="commandbar">
				<div class="commandbarContent">
					<a href="messages/new" id="new_record">New</a> | 
					<a href="messages" id="delete_record">Delete</a> | 
				</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
