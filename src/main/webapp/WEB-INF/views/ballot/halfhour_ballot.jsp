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
<div id="reportDiv" style="width: 750px;">
	<div style="width: 100%; font-weight: bold; text-align: center; font-size: 22px; ">
		<c:choose>
			<c:when test="${houseType=='lowerhouse'}">
				<spring:message code="generic.maharashtra.lowerhouse" text="Maharashtra Assembly"/>
			</c:when>
			<c:when test="${houseType=='upperhouse'}">
				<spring:message code="generic.maharashtra.upperhouse" text="Maharashtra Council"/>
			</c:when>
		</c:choose>
	</div>
	<div style="width: 100%; font-weight: bold; text-align: center; ">
		<spring:message code="question.ballot.hdq.assembly.ballot" text="${deviceName}"/>
	</div>
	<br>
	<div style="width: 100%;font-size: 16px; font-weight: bold; text-align: center; width:">
		<spring:message code="question.ballot.discussionDate" text="Discussion Date"/> : ${answeringDate}
	</div>
<table class="strippedTable" border="1">
	<thead>
		<tr>
			<th><spring:message code="general.srnumber" text="Serial Number"/></th>
			<th><spring:message code="member.name" text="Member Name"/></th>
			<th><spring:message code="question.number" text="Question Number"/></th>
			<th><spring:message code="question.subject" text="Subject"/></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="counter" value="1" />
		<c:forEach items="${ballotVOs}" var="ballotVO">
		<tr>
			<td>${counter}</td>
			<td>${ballotVO[0]}</td>
			<td>${ballotVO[1]}</td>
			<td>${ballotVO[2]}</td>
		</tr>
		<c:set var="counter" value="${counter + 1}" />
		</c:forEach>
	</tbody>
</table>
<br>
<div style="width: 100%; font-size: 14px; ">
		________________<spring:message code="hd.ballot.roj" text="Dated" />
	</div>
</div>

</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>