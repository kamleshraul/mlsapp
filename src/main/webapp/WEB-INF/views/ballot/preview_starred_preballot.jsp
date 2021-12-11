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
			width: 300px;			
			text-align: left;
			padding: 5px;	
			font-weight: bold !important;
		}
		.td{
			text-align: center;
			padding: 5px;
		}
	</style>
	<style type="text/css" media="print">
		table.strippedTable {width: 780px;}
		.memberName, .td, th, p#header, table#footer {font-size: 20px !important;}
		.memberName {font-weight: bold;}
		table#footer {width: 757px !important;}
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
	<spring:message code="question.ballot.notCreated" text=" No Entries"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Pre Ballot"/>
</c:when>

<c:otherwise>
<div id="reportDiv">
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h2 style="text-decoration: underline; font-family: 'Times New Roman';"><spring:message code="generic.preballot.previewList" text="PREVIEW OF PREBALLOT" /></h2>
	</div>
	
	<p id="header" style="margin: 0px 0px 10px 25px; font-size: 15px;">
		<label>
			<spring:message code="question.group" text="Group"/> <span style="font-weight: bold;">${groupNo}, </span>
			<spring:message code="generic.date" text="Answering Date"/> <span style="text-decoration: underline; font-weight: bold;">${displayAnsweringDate}</span>
			<spring:message code="question.preballot.starred.lowerhouse.headercontent" text="Pre-Ballot Of the Day"></spring:message>
		</label>
	</p>
	
	<c:set var="noOfRounds" value="${noOfRounds}"/>
	
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr>
				<th><spring:message code="question.preballot.serialNo" text="Sr. No."/></th>
				<th class="memberName"><spring:message code="question.preballot.member.name" text="Member Name"/></th>
				<c:forEach begin="${questionCount+1}" end="${noOfRounds}" varStatus="cnt">
					<th class="td"><spring:message code="qis.preballot.question${cnt.count}" text="Question ${cnt.count}"/></th>
				</c:forEach>		
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${ballotVOs}" var="ballotVO">
			<c:set var="questionCount" value="${ballotVO.questionSequenceVOs.size()}"/>
			<tr>
				<td>${ballotVO.serialNo}</td>
				<td class="memberName">${ballotVO.memberName}</td>
				<c:forEach items="${ballotVO.questionSequenceVOs}" var="questionSequenceVO">
					<td class="td">
						${questionSequenceVO.formattedNumber}
					</td>
				</c:forEach>
				<c:if test="${questionCount<noOfRounds}">
					<c:forEach begin="${questionCount+1}" end="${noOfRounds}">
						<td class="td">-</td>					
					</c:forEach>
				</c:if>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<table id="footer" style="max-width: 800px; width: 800px; margin-left: 25px; margin-top: 20px;">
		<tr>
			<td colspan="${noOfRounds+1}">
				<p>
					<span style="font-weight: bold;">
						<spring:message code="question.preballot.member.total" text="Total Members" />:&nbsp;&nbsp;&nbsp;&nbsp;${totalMembers}
						<br /><br />
						<spring:message code="question.preballot.question.total" text="Total Questions" />:&nbsp;&nbsp;&nbsp;&nbsp;${totalNoOfQuestions}
					</span><br /><br />
				</p>
			</td>		
		</tr>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>