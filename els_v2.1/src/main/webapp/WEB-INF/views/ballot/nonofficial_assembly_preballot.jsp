<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	</script>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=3" media="print" />
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div id="reportDiv">
		<c:choose>
			<c:when test="${ballotVOs == null}">
				<spring:message code="resolution.ballot.notCreated" text="Ballot is not Created"/>
			</c:when>
			
			<c:when test="${empty ballotVOs}">
				<spring:message code="resolution.ballot.noEntries" text="There are no entries in the Ballot"/>
			</c:when>
			
			<c:otherwise>
				<label class="small"><spring:message code="resolution.ballot.discussionDate" text="Discussion Date"/>: ${answeringDate}</label>
				
				<table class="strippedTable" border="1">
					<tr>
					<th><spring:message code="general.srnumber" text="Serial Number"/></th>
					<th><spring:message code="member.name" text="Member Name"/></th>
					<th><spring:message code="resolution.number" text="Resolution Number"/></th>
					<th><spring:message code="Resolution.subject" text="Subject"/></th>
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
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>