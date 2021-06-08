<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="memberballotchoice.revisions" text="Member Ballot Choice Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		.uiTable{width:780px;}
		td{min-width:100px; max-width:350px;min-height:30px;}
		th{min-width:100px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty revisions) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="memberballotchoice.revision.questionNumber" text="Question Number"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.memberballotround" text="Round"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.choice" text="Choice"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.answeringDate" text="Answering Date"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.isAutoFilled" text="Is Auto Filled?"></spring:message></th>
					<th><spring:message code="memberballotchoice.revision.reasonForChoiceUpdate" text="Reason For Choice Update"></spring:message></th>
				</tr>
				
				<c:set var="currentQuestionNumber" value="${revisions[0].questionNumber}" />
				<c:forEach items="${revisions}" var="i" varStatus="revisionCounter">
					<tr>
					<c:choose>
						<c:when test="${revisionCounter.count==1}">
							<td rowspan="${i.revisionsCount}">${i.questionNumber}</td>
						</c:when>
						<c:when test="${i.questionNumber ne currentQuestionNumber}">
							<td rowspan="${i.revisionsCount}">${i.questionNumber}</td>
							<c:set var="currentQuestionNumber" value="${i.questionNumber}" />
						</c:when>
					</c:choose>
						<td>${i.editedAs}<br>${i.editedBY}<br>${i.editedOn}</td>
						<td>${i.round}</td>
						<td>${i.choice}</td>
						<td>${i.answeringDate}</td>
						<td>${i.isAutoFilled}</td>
						<td>${i.reasonForChoiceUpdate}</td>
					</tr>							
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="memberballotchoice.revision.noentries" text="No Member Ballot Choice Revisions Found for Given Member"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>