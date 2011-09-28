<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="state.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
	<div>
	<div class="commandbar">
				<div class="commandbarContent">
					<a href="states/new" id="new_record"><spring:message code="generic.new"/></a> | 
					<a href="states" id="delete_record"><spring:message code="generic.delete"/></a> | 
				</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
