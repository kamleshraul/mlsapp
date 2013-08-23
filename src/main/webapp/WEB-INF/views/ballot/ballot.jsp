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
	<style type="text/css">
		.memberName{
			min-width: 170px;
			max-width: 250px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.questionNumber{
			width: 50px;
			max-width: 105px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}	
		.round{
			width: 50px;
			max-width: 105px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.td{
			text-align: center;
			padding: 5px;
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
<c:when test="${ballotVOs == null}">
	<spring:message code="question.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<label class="small"><spring:message code="question.ballot.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
<div id="reportDiv" >
<table class="strippedTable" border="1">
	<tr>
		<th><spring:message code="general.srnumber" text="Serial Number"/></th>
		<th><spring:message code="member.name" text="Member Name"/></th>
		<th><spring:message code="question1" text="Question 1"/></th>
		<th><spring:message code="round1" text="Round 1"/></th>
		<th><spring:message code="question2" text="Question 2"/></th>
		<th><spring:message code="round2" text="Round 2"/></th>
		<th><spring:message code="question3" text="Question 3"/></th>
		<th><spring:message code="round3" text="Round 3"/></th>
	</tr>
	
	<c:set var="counter" value="1" />
	<c:set var="memberName" value="" />
	<c:set var="mCounter" value="1" />
	<c:set var="maxCount" value="3" />
	<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="ctr">
		<c:choose>
			<c:when test="${memberName != ballotVO[0]}">
				<c:if test="${memberName!=''}">
					<c:if test="${mCounter <= maxCount}">
						<c:forEach begin="${mCounter}" end="${maxCount}" step="1" varStatus="ctr">
							<td class="td">-</td>
							<td class="td">-</td>						
						</c:forEach>
					</c:if>
					
					</tr>
					<c:set var="mCounter" value="1" />
				</c:if>
				
				<tr>
					<td class="td">${counter}</td>
					<td class="memberName">${ballotVO[0]}</td>
					<td class="td">${ballotVO[1]}</td>
					<td class="td">${ballotVO[2]}</td>
					<c:set var="counter" value="${counter + 1}" />
					<c:set var="mCounter" value="${mCounter + 1}" />
			</c:when>
			<c:otherwise>
				<td class="td">${ballotVO[1]}</td>
				<td class="td">${ballotVO[2]}</td>
				<c:set var="mCounter" value="${mCounter + 1}" />
			</c:otherwise>
		</c:choose>
		<c:set var="memberName" value="${ballotVO[0]}" />   
	</c:forEach>
	<c:if test="${mCounter <= maxCount}">
		<c:forEach begin="${mCounter}" end="${maxCount}" step="1">
			<td class="td">-</td>
			<td class="td">-</td>						
		</c:forEach>
	</c:if>
</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>