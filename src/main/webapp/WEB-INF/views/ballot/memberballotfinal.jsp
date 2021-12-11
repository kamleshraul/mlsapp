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
	font-size: 16px;
	}
	.false{
	border-bottom: 8px double;
	font-size: 16px;
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
	border-bottom-color: #8B6914;
	}
	td,th{
	font-size: 16px;
	}
	</style>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
	<c:when test="${empty ballots}">
		<spring:message code="meberballot.finalballot.failed" text="Ballot Cannot Be Created"/>
	</c:when>
	<c:otherwise>
		<div id="reportDiv">
			<label class="small"><spring:message code="question.ballot.answeringDate" text="Answering Date"/>: ${displayAnsweringDate}</label>
			<table class="uiTable">
				<thead>
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
				</thead>
				<tbody>
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
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>