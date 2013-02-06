<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {				
		});
	</script>
	<style type="text/css">
	.round1{
	color:green ;
	}
	.round2{
	color:blue ;
	}
	.round3{
	color: red;
	}
	.round4{
	color: black;
	}
	.round5{
	color: #F26522;
	}
	</style>
</head>

<body>
<c:choose>
<c:when test="${!(empty selectedItems)}">
<table class="uiTable">
	<tr>
	<th><spring:message code="preballot.position" text="S.no"/></th>
	<th>
	<c:choose>
	<c:when test="${attendance=='true' }">
	<spring:message code="preballot.presentmember" text="Present Members"/>
	</c:when>
	<c:otherwise>
	<spring:message code="preballot.absentmember" text="Absent Members"/>	
	</c:otherwise>
	</c:choose>
	</th>	
	</tr>
	<c:forEach items="${selectedItems}" var="i">
	<tr>
		<td class="round${round }">${i.position}</td>
		<td class="round${round }">${i.member.getFullname()}</td>		
	</tr>
	</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="preballot.nomember" text="No list found"/>
</c:otherwise>
</c:choose>
</body>
</html>