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
			<th style="width: 50px; text-align: center;"><spring:message code="general.srnumber" text="Serial Number"/></th>
			<th style="width: 250px; text-align: center;"><spring:message code="member.name" text="Member Name"/></th>
			<th style="width: 100px; text-align: center;"><spring:message code="question.number" text="Question Number"/></th>
			<th style="width: 350px; text-align: center;"><spring:message code="question.subject" text="Subject"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="counter">
		<tr>
			<td style="width: 50px; text-align: center;">${formater.formatNumberNoGrouping(counter.count, locale)}</td>
			<td style="width: 250px;">${ballotVO[0]}</td>
			<td style="width: 100px;">${ballotVO[1]}</td>
			<td style="width: 350px;">${ballotVO[2]}</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<br>
<div style="width: 100%; font-size: 14px; ">
	___________________ <spring:message code="question.ballot.council.hd.footer.roji" text="Dated"/> ${currentUser}
	<spring:message code="question.ballot.council.hd.footer.atloc" text="At Location"/>___________________ <spring:message code="question.ballot.council.hd.footer.at" text="At"/>
	<br><br>
	___________________________________________________________________ <spring:message code="question.ballot.council.hd.footer.presence" text="Presence"/>
</div>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>