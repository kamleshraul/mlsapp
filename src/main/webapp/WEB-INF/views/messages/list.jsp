<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
	<div>
	<div class="commandbar">
				<div class="commandbarContent">
					<a href="messages/new" id="new_record"><img alt="" src="images/45.png" >New</a> | 
					<a href="messages" id="delete_record"><img alt="" src="images/41.png">Delete</a> | 
				</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
