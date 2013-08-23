<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$('.questionNumber').click(function() {
				var id = $(this).attr("id");
				editQuestion(id);
			});
		});
	</script>
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
<label class="small"><spring:message code="question.ballot.answeringDate" text="Answering Date"/>: ${answeringDate}</label>

<table class="uiTable" border="1">
	<tr>
	<th><spring:message code="member.name" text="Member Name"/></th>
	<th><spring:message code="question1" text="Question 1"/></th>
	<th><spring:message code="question2" text="Question 2"/></th>
	<th><spring:message code="question3" text="Question 3"/></th>
	</tr>
	<c:forEach items="${ballotVOs}" var="ballotVO">
	<tr>
		<td>${ballotVO.memberName}</td>
		<c:forEach items="${ballotVO.questionSequenceVOs}" var="questionSequenceVO">
			<td align="center">
				${questionSequenceVO.number}
			</td>
		</c:forEach>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>