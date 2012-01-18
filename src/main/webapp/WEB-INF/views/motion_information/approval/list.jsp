<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
	<title>प्रस्ताव सादर करणे</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
		<a href="motion_approval/new" id="start_process">Start Process
			</a> |
			<a href="mytask/new" id="myTask">My Task
			</a> |
			<a href="#" id="print_record"> Group Task
			</a> |
			<a href="#" id="citations_record">
				Track
			</a>  
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
