<%@ include file="/common/taglibs.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<html>
<head>
	<title><spring:message code="masters.grids.list" text="List of Grids"/></title>
</head>
<body>
	<div>
	<div class="commandbar">
				<div class="commandbarContent">
					<a href="grid/new" id="new_record"><spring:message code="generic.new" text="New"/></a> | 
					<a href="grid" id="delete_record"><spring:message code="generic.delete" text="Delete"/></a> | 
				</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
