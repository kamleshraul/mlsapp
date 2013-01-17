<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	</script>
</head>

<body>
<c:choose>
<c:when test="${ballotVOs == null}">
	<spring:message code="question.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<label class="small"><spring:message code="question.ballot.answeringDate" text="Answering Date"/>: ${answeringDate}</label>

<table border="1">
	<tr>
	<th><spring:message code="member.name" text="Member Name"/></th>
	</tr>
	<c:forEach items="${ballotVOs}" var="ballotVO">
	<tr>
		<td>${ballotVO.memberName}</td>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>
</body>

</html>