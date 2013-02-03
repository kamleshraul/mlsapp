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
	.true{
	border-bottom: 3px solid;
	font-size: 14px;
	}
	.false{
	border-bottom: 8px double;
	font-size: 14px;
	}
	.round1{
	border-bottom-color:green ;
	}
	.round2{
	border-bottom-color:blue ;
	}
	.round3{
	border-bottom-color: red;
	}
	.round4{
	border-bottom-color: black;
	}
	.round5{
	border-bottom-color: lime;
	}
	</style>
</head>

<body>
<c:choose>
<c:when test="${empty ballots}">
	<spring:message code="meberballot.finalballot.failed" text="Ballot Cannot Be Created"/>
</c:when>
<c:otherwise>
<label class="small"><spring:message code="question.ballot.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
<table class="uiTable">
	<tr>
	<th><spring:message code="memberballot.s.no" text="S.No."/></th>
	<th><spring:message code="memberballot.member" text="Member"/></th>
	<th><spring:message code="memberballot.round1" text="Round 1"/></th>
	<th><spring:message code="memberballot.position1" text="Position"/></th>
	<th><spring:message code="memberballot.round2" text="Round 2"/></th>
	<th><spring:message code="memberballot.position2" text="Position"/></th>
	<th><spring:message code="memberballot.round3" text="Round 3"/></th>
	<th><spring:message code="memberballot.position3" text="Position"/></th>	
	</tr>
	<c:forEach items="${ballots}" var="ballot">
	<tr>
		<td>${ballot.ballotSno}</td>
		<td>${ballot.member}</td>
		<c:if test="${!(empty ballot.questions) }">				
		<c:forEach items="${ballot.questions}" var="question">
			<td><span class="${question.questionAttendance} round${question.questionRound}">${question.questionNumber}</span></td>
			<td><span class="${question.questionAttendance} round${question.questionRound}">${question.questionPosition}</span></td>			
		</c:forEach>
		</c:if>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>
</body>

</html>