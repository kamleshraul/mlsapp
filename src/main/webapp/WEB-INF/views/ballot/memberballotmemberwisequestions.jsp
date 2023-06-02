<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
		$(document).ready(function() {
			$('#memberwisequestions_pdf').click(function() {
				var parameters = "session="+$("#session").val()
				 +"&questionType="+$("#questionType").val()
				 +"&member="+$("#member").val()				 
				 +"&outputFormat=PDF";
				var resourceURL = 'ballot/memberballot/member/questionsreport?'+ parameters;			
				$(this).attr('href', resourceURL);
			});
			
			$('#memberwisequestions_word').click(function() {
				var session = $("#session").val();
				var questionType = $("#questionType").val();
				var member = $("#member").val();
				if($('#viewer').val()=='member') {
					session = $("#loadedSession").val();
					questionType = $("#selectedQuestionType").val();
					member = $("#loggedInMemberId").val();
				}
				var parameters = "session="+session
				 +"&questionType="+questionType
				 +"&member="+member
				 +"&outputFormat=WORD";
				var resourceURL = 'ballot/memberballot/member/questionsreport?'+ parameters;			
				$(this).attr('href', resourceURL);
			});
		});
	</script>
<link rel="stylesheet" href="resources/css/printerfriendly.css" media="print" type="text/css">
<style type="text/css">
.underlined{
text-decoration: underline;
}
#reportDiv{
width: 600px;
margin-left: 30px;
}

.centerdiv{
	text-align: center;
	width: 800px;
}
</style>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>

<c:when test="${!(empty report) }">
<div id="exportDiv" style="margin-bottom: 20px;">
	<a id="memberwisequestions_pdf" href="#" style="text-decoration: none;">
	<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
	</a>
	&nbsp;
	<a id="memberwisequestions_word" href="#" style="text-decoration: none;">
		<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
	</a>
</div>
<div id="reportDiv">		
		<c:if test="${!(empty report.memberBallotMemberWiseCountVOs)}">
			<c:set value="0" var="count"></c:set>
			<c:set value="0" var="index"></c:set>
			
			<c:set value="${fn:length(report.memberBallotMemberWiseCountVOs) }" var="size"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseCountVOs }" var="i">
			<c:choose>
			<c:when test="${i.statusTypeType=='question_recommend_clarificationNeededFromMember'
			 ||i.statusTypeType=='question_final_clarificationNeededFromMember'
			 ||i.statusTypeType=='question_recommend_clarificationNeededFromDepartment'
			 ||i.statusTypeType=='question_final_clarificationNeededFromDepartment'
			 ||i.statusTypeType=='question_recommend_clarificationNeededFromGovt'
			 ||i.statusTypeType=='question_final_clarificationNeededFromGovt'
			 ||i.statusTypeType=='question_recommend_clarificationNeededFromMemberAndDepartment'
			 ||i.statusTypeType=='question_final_clarificationNeededFromMemberAndDepartment'}">
			<c:set value="${count+i.count }" var="count"></c:set>
			</c:when>
			<c:otherwise>
			<c:choose>
				<c:when test="${i.currentDeviceType=='questions_starred' && (i.statusTypeType=='question_recommend_admission' || i.statusTypeType=='question_final_admission')}">
					<strong><spring:message code="memberballotmemberwisequestions.starredAdmit" text="Starred Admit"></spring:message>-${i.count}</strong><br>
				</c:when>
				<c:when test="${i.currentDeviceType=='questions_starred' && (i.statusTypeType=='question_recommend_rejection' || i.statusTypeType=='question_final_rejection')}">
					<strong><spring:message code="memberballotmemberwisequestions.starredReject" text="Starred Reject"></spring:message>-${i.count}</strong><br>
				</c:when>
				<c:when test="${(i.statusTypeType=='question_recommend_convertToUnstarredAndAdmit') || i.statusTypeType=='question_unstarred_final_admission'}">
					<strong><spring:message code="memberballotmemberwisequestions.unstarredAndAdmit" text="Unstarred Admit"></spring:message>-${i.count}</strong><br>
				</c:when>
			</c:choose>
			
			</c:otherwise>
			</c:choose>
			<c:set value="${index+1 }" var="index"></c:set>
			<c:if test="${index==size }">			
			<c:set value="${i.formatNumber(count,locale) }" var="count"></c:set>
			</c:if>
			</c:forEach>			
			<c:if test="${count>0 }">
			<strong><spring:message code="memberballotmemberwisewuestions.clrificationneeded" text="Clarification From Member/Fact Finding From Department"></spring:message>-${count}</strong>
			</c:if>
			<hr style="width: 800px;">
			
			<c:if test="${!(empty groups)}">
			<c:forEach items="${groups }" var="i">
			
			<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs) }">
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${j.groupNumber==i.number }">
			<c:set value="${count+1 }" var="count"></c:set>
			</c:if>
			</c:forEach>
			</c:if>
			
			<c:if test="${count>0 }">
				<div class="centerdiv">
					<h4><spring:message code="memberballotmemberwisewuestions.group" text="Group"></spring:message>-${i.formatNumber()}</h4>
					<h2 class="underlined">${report.member }</h2>
					<br />
				</div>
			<table class="strippedTable" border="1">
			<tr>
			<th><spring:message code="memberballotmemberwisewuestions.ministries" text="Ministries"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.anseringDates" text="Answering Dates"></spring:message></th>
			</tr>
			<tr>
			<td>
			<c:choose>
			<c:when test="${i.findMinistryDisplayNamesByPriority()!=null}">
			<c:forEach items="${i.findMinistryDisplayNamesByPriority()}" var="j">
			<c:if test="${!(empty j) }">
			${j}<br>
			</c:if>
			</c:forEach>
			</c:when>
			<c:otherwise>
			-
			</c:otherwise>
			</c:choose>
			</td>
			<td>
			<c:choose>
			<c:when test="${i.findQuestionDateByGroup()!=null}">
			<c:forEach items="${i.findQuestionDateByGroup()}" var="j">
			<c:if test="${!(empty j) }">
			${j.name}<br>
			</c:if>
			</c:forEach>
			</c:when>
			<c:otherwise>
			-
			</c:otherwise>
			</c:choose>
			</td>
			</tr>
			</table>
			
			
			
			<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs) }">
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${j.currentDeviceType=='questions_starred' && (j.statusTypeType=='question_recommend_admission' || j.statusTypeType=='question_final_admission') && j.groupNumber==i.number }">
			<c:set value="${count+1 }" var="count"></c:set>
			<c:set var="admitted" value="${j.statusType}"></c:set>
			</c:if>
			</c:forEach>
			<c:if test="${count>0 }"><br />
			<%-- <h2 class="underlined">${admitted}</h2> --%>
			<h2 class="underlined"><spring:message code="memberballotmemberwisequestions.starredAdmit" text="Starred Admit"/></h2>
			<br />
			<table class="strippedTable" border="1">
			<thead>
				<tr>
					<th><spring:message code="memberballotmemberwisewuestions.sno" text="S.No"></spring:message></th>
					<th><spring:message code="memberballotmemberwisewuestions.number" text="Question Number"></spring:message></th>
					<th><spring:message code="memberballotmemberwisewuestions.subject" text="Subject"></spring:message></th>
					<th><spring:message code="memberballotmemberwisewuestions.clubbingInformation" text="Clubbing Information"></spring:message></th>
				</tr>
			</thead>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${j.currentDeviceType=='questions_starred' && (j.statusTypeType=='question_recommend_admission' || j.statusTypeType=='question_final_admission') && j.groupNumber==i.number }">
			<tr class="page-break">
			<td>${j.sno}</td>
			<td>${j.questionNumber}</td>
			<td>${j.questionSubject}</td>
			<td>${j.clubbingInformation}</td>
			</tr>
			</c:if>
			</c:forEach>
			</table>
			</c:if>
			</c:if>
			
			<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs) }">
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_convertToUnstarredAndAdmit' || j.statusTypeType=='question_unstarred_final_admission')
				&& j.groupNumber==i.number }">
			<c:set value="${count+1 }" var="count"></c:set>
			<c:set var="unstarred" value="${j.statusType}"></c:set>
			</c:if>
			</c:forEach>
			<c:if test="${count>0 }">
			<%-- <h2 class="underlined">${unstarred}</h2> --%>
			<h2 class="underlined"><spring:message code="memberballotmemberwisequestions.unstarredAndAdmit" text="Unstarred Admit"></spring:message></h2>
			<table class="strippedTable" border="1">
			<tr>
			<th><spring:message code="memberballotmemberwisewuestions.sno" text="S.No"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.number" text="Question Number"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.subject" text="Subject"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.clubbingInformation" text="Clubbing Information"></spring:message></th>
			</tr>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_convertToUnstarredAndAdmit' || j.statusTypeType=='question_unstarred_final_admission')
				&& j.groupNumber==i.number }">
			<tr>
			<td>${j.sno}</td>
			<td>${j.questionNumber}</td>
			<td>${j.questionSubject}</td>
			<td>${j.clubbingInformation}</td>
			</tr>
			</c:if>
			</c:forEach>
			</table>
			</c:if>
			</c:if>
			
			<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs) }">
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_rejection' || j.statusTypeType=='question_final_rejection') && j.groupNumber==i.number }">
			<c:set value="${count+1 }" var="count"></c:set>
			<c:set var="rejected" value="${j.statusType}"></c:set>
			</c:if>
			</c:forEach>
			<c:if test="${count>0 }">
			<%-- <h2 class="underlined">${rejected}</h2> --%>
			<h2 class="underlined"><spring:message code="memberballotmemberwisequestions.starredReject" text="Starred Rejected"/></h2>
			<table class="strippedTable" border="1">
			<tr style="background-color: #A2C6E4;">
			<th><spring:message code="memberballotmemberwisewuestions.sno" text="S.No"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.number" text="Question Number"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.subject" text="Subject"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.reason" text="Reason"></spring:message></th>
			<%-- <th><spring:message code="memberballotmemberwisewuestions.clubbingInformation" text="Clubbing Information"></spring:message></th> --%>
			</tr>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_rejection' || j.statusTypeType=='question_final_rejection') && j.groupNumber==i.number }">
			<tr>
			<td>${j.sno}</td>
			<td>${j.questionNumber}</td>
			<td>${j.questionSubject}</td>
			<td>${j.questionReason}</td>
			<%-- <td>${j.clubbingInformation}</td> --%>
			</tr>
			</c:if>
			</c:forEach>
			</table>
			</c:if>
			</c:if>
			
			<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs) }">
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_clarificationNeededFromMember'
			||j.statusTypeType=='question_final_clarificationNeededFromMember'
			||j.statusTypeType=='question_recommend_clarificationNeededFromMemberAndDepartment'
			||j.statusTypeType=='question_final_clarificationNeededFromMemberAndDepartment'
			||j.statusTypeType=='question_recommend_clarificationNeededFromDepartment'
			||j.statusTypeType=='question_final_clarificationNeededFromDepartment'
			||j.statusTypeType=='question_recommend_clarificationNeededFromGovt'
			||j.statusTypeType=='question_final_clarificationNeededFromGovt') && j.groupNumber==i.number }">
			<c:set value="${count+1 }" var="count"></c:set>
			<c:set var="clarification" value="${j.statusType}"></c:set>
			</c:if>
			</c:forEach>
			<c:if test="${count>0 }">
			<h2 class="underlined"><spring:message code="memberballotmemberwisewuestions.clrificationneeded" text="Clarification From Member/Fact Finding From Department"></spring:message></h2>
			<table class="strippedTable" border="1">
			<tr>
			<th><spring:message code="memberballotmemberwisewuestions.sno" text="S.No"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.number" text="Question Number"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.subject" text="Subject"></spring:message></th>
			<th><spring:message code="memberballotmemberwisewuestions.status" text="Status"></spring:message></th>
			</tr>
			<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }" var="j">
			<c:if test="${(j.statusTypeType=='question_recommend_clarificationNeededFromMember'
			||j.statusTypeType=='question_final_clarificationNeededFromMember'
			||j.statusTypeType=='question_recommend_clarificationNeededFromMemberAndDepartment'
			||j.statusTypeType=='question_final_clarificationNeededFromMemberAndDepartment'
			||j.statusTypeType=='question_recommend_clarificationNeededFromDepartment'
			||j.statusTypeType=='question_final_clarificationNeededFromDepartment'
			||j.statusTypeType=='question_recommend_clarificationNeededFromGovt'
			||j.statusTypeType=='question_final_clarificationNeededFromGovt') && j.groupNumber==i.number }">
			<tr>
			<td>${j.sno}</td>
			<td>${j.questionNumber}</td>
			<td>${j.questionSubject}</td>
			<td>${j.statusType}</td>
			</tr>
			</c:if>
			</c:forEach>
			</table>
			</c:if>
			</c:if>
			</c:if>
			
			</c:forEach>
		</c:if>
		<!--<div class="page-break"></div>
	--></c:if>
</div>
</c:when>

<c:otherwise>
<h2><spring:message code="memberballotmemberwisewuestions.noquestionsfound" text="No Questions Found"></spring:message></h2>
</c:otherwise>

</c:choose>
<input type="hidden" id="viewer" value="${viewer}">
<input type="hidden" id="loggedInMemberId" value="${loggedInMemberId}">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>