<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {				
		});
	</script>
</head>

<body>
<c:choose>
<c:when test="${!(empty memberBallots)}">
<table class="uiTable">
	<tr>
	<th><spring:message code="memberballot.position" text="S.no"/></th>
	<th>	
	<spring:message code="memberballot.member" text="Members"/>
	</th>	
	</tr>
	<c:forEach items="${memberBallots}" var="i">
	<tr>
		<td>${i.position}</td>
		<td>${i.member.getFullname()}</td>		
	</tr>
	</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="memberballot.noballot" text="No Member Ballot Found"/>
</c:otherwise>
</c:choose>
</body>
</html>