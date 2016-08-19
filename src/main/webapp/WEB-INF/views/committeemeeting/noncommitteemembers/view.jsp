<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="noncommitteemember" text="noncommitteemember"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	</script>
</head>
<body>
<div class="fields clearfix">
<form>
	<h2><spring:message code="generic.view.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${id}]
	</h2>
	
	<p>
		<label class="small"><spring:message code="department.name" text="Department Name" /></label>
		<input type="text" id="departmentName" name="departmentName" value="${departmentName}" class="sText" readonly="readonly"/>
	</p>
	
	
	
	<!-- Table displaying members -->
	<c:if test="${not empty noncommiteememberinformations}">
		<label class="small"><spring:message code="noncommitteemember.member" text="noncommitteemember"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="noncommitteemember.member" text="Member"/></th>
			
			</tr>
			<c:set var="qCount" value="1"></c:set>
			<c:forEach items="${noncommiteememberinformations}" var="q">
				<tr>
					<td>${q.name}</td>
					
				</tr>
				<c:set var="qCount" value="${qCount + 1}"></c:set>
			</c:forEach>
		</table>
	</c:if>	
	
</form>
</div>
</body>
</html>