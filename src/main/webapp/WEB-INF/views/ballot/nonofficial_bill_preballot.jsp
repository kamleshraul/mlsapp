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
<c:when test="${isBallotAllowedToCreate==false}">
	<spring:message code="bill.ballot.noEntries" text="There are pending bills of previous discussion dates.. So preballot can not be created now."/>
</c:when>

<c:when test="${ballotVOs == null}">
	<spring:message code="bill.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="bill.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<label class="small"><spring:message code="bill.ballot.discussionDate" text="Discussion Date"/>: ${answeringDate}</label>

<table class="strippedTable" border="1">
	<tr>
	<th><spring:message code="general.srnumber" text="Serial Number"/></th>
	<th><spring:message code="member.name" text="Member Name"/></th>
	<th><spring:message code="bill.number" text="bill Number"/></th>
	<th><spring:message code="bill.title" text="Title"/></th>
	</tr>
	<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="counter">
	<tr>
		<td>${counter.count}</td>
		<td>${ballotVO.memberName}</td>
		<td>${ballotVO.questionNumber}</td>
		<td>${ballotVO.questionSubject}</td>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>
</body>

</html>