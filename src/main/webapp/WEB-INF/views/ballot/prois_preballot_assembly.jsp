<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	</script>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${ballotVOs == null}">
	<spring:message code="question.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<div id="reportDiv">
<label class="small"><spring:message code="question.ballot.discussionDate" text="Answering Date"/>: ${answeringDate}</label>

<table class="strippedTable" border="1">
	<thead>
		<tr>
			<th><spring:message code="general.srnumber" text="Serial Number"/></th>
			<th><spring:message code="member.name" text="Member Name"/></th>
			<th><spring:message code="question.number" text="Notice Number"/></th>
			<th><spring:message code="question.subject" text="Subject"/></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="counter" value="1" />
		<c:forEach items="${ballotVOs}" var="ballotVO">
		<tr>
			<td>${counter}</td>
			<td>${ballotVO.memberName}</td>
			<td>${ballotVO.questionNumber}</td>
			<td>${ballotVO.questionSubject}</td>
		</tr>
		<c:set var="counter" value="${counter + 1}" />
		</c:forEach>
	</tbody>
</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>