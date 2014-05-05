<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {		
		var parameters = "houseType=" + $('#houseType').val()
						+"&houseTypeName=" + $('#houseTypeName').val()
						+"&sessionTypeName=" + $('#sessionTypeName').val()
						+"&sessionYear=" + $('#sessionYear').val()
						+"&sessionCountName=" + $('#sessionCountName').val()
						+"&questionSubmissionStartTime=" + $('#questionSubmissionStartTime').val()
						+"&questionSubmissionEndTime=" + $('#questionSubmissionEndTime').val()
						+"&questionSubmissionDate=" + $('#questionSubmissionDate').val()
						+"&dayTime=" + $('#dayTime').val()
						+"&questionTypeName=" + $('#questionTypeName').val();
		$.get('ballot/memberballot/questiondistribution/header?'+parameters, function(data) {
			$('#headerContent').empty();
			$('#headerContent').html(data);
		}, 'html');
		
		$('#totalquestions_pdf').click(function() {
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val()			 			 
			 +"&outputFormat=PDF";
			var resourceURL = 'ballot/memberballot/questiondistribution/report?'+ parameters;			
			$(this).attr('href', resourceURL);
		});
		
		$('#totalquestions_word').click(function() {
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val()
			 +"&outputFormat=WORD";
			var resourceURL = 'ballot/memberballot/questiondistribution/report?'+ parameters;			
			$(this).attr('href', resourceURL);
		});
	});
</script>
<style type="text/css">
th,td{
text-align: center;
}
</style>
<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=4" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:set value="0" var="admitted"></c:set>
<c:set value="0" var="unstarred"></c:set>
<c:set value="0" var="rejected"></c:set>
<c:set value="0" var="clarification"></c:set>
<c:set value="0" var="total"></c:set>

<c:set value="0" var="admittedpercent"></c:set>
<c:set value="0" var="unstarredpercent"></c:set>
<c:set value="0" var="rejectedpercent"></c:set>
<c:set value="0" var="clarificationpercent"></c:set>

<c:set value="0" var="totalMembers"></c:set>
<c:set value="0" var="size"></c:set>



<c:choose>
	<c:when test="${!(empty questionDistributions) }">
		<c:set value="${fn:length(questionDistributions) }" var="size"></c:set>
		
		<div id="exportDiv" style="margin-bottom: 20px;">
			<a id="totalquestions_pdf" href="#" style="text-decoration: none;">
			<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
			</a>
			&nbsp;
			<a id="totalquestions_word" href="#" style="text-decoration: none;">
				<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
			</a>
		</div>
		
		<div id="reportDiv">
			<p id="headerContent" style="font-weight: bold; margin-left:25px; margin-bottom: 10px; line-height: 200%; font-size: 16px;">
			</p>
			<table class="strippedTable" border="1">
			<thead>
			<tr height="30px;">
			<th><spring:message code="memberdistribution.sno" text="S.No"></spring:message></th>
			<th style="text-align: left;"><spring:message code="memberdistribution.member" text="Member"></spring:message></th>
			<th><spring:message code="memberdistribution.admitted" text="Admitted"></spring:message></th>
			<th><spring:message code="memberdistribution.unstarredadmitted" text="Unstarred Admitted"></spring:message></th>
			<th><spring:message code="memberdistribution.rejection" text="Rejection"></spring:message></th>
			<th><spring:message code="memberdistribution.clarificationneeded" text="Clarification From Member/Fact Finding"></spring:message></th>
			<th><spring:message code="memberdistribution.totalcount" text="Total Count"></spring:message></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${questionDistributions}"  var="i" varStatus="rowNumber">
			<c:set value="${totalMembers+1 }" var="totalMembers"></c:set>			
			<tr>
			<td>${i.sNo}</td>
			<td style="text-align: left;">${i.member}, <br/>
			<spring:message code="${houseType}.memberHouseTitle"/></td>
			
			<c:if test="${!(empty i.distributions ) }">
			
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${i.distributions}" var="j">
			<c:choose>
			<c:when test="${j.statusTypeType=='question_final_admission'}">
			<td>${j.count}</td>
			<c:set value="${count+1}" var="count"></c:set>
			<c:set value="${admitted+j.count}" var="admitted"></c:set>
			</c:when>
			<c:otherwise>
			</c:otherwise>
			</c:choose>
			</c:forEach>
			
			<c:if test="${count==0 }">
			<td>-</td>
			</c:if>
			
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${i.distributions}" var="j">
			<c:choose>
			<c:when test="${j.statusTypeType=='question_final_convertToUnstarredAndAdmit'}">
			<td>${j.count}</td>
			<c:set value="${count+1}" var="count"></c:set>
			<c:set value="${unstarred+j.count}" var="unstarred"></c:set>
			</c:when>
			<c:otherwise>
			</c:otherwise>
			</c:choose>
			</c:forEach>
			
			<c:if test="${count==0 }">
			<td>-</td>
			</c:if>
			
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${i.distributions}" var="j">
			<c:choose>
			<c:when test="${j.statusTypeType=='question_final_rejection'}">
			<td>${j.count}</td>
			<c:set value="${count+1}" var="count"></c:set>
			<c:set value="${rejected+j.count}" var="rejected"></c:set>
			</c:when>
			<c:otherwise>
			</c:otherwise>
			</c:choose>
			</c:forEach>
			
			<c:if test="${count==0 }">
			<td>-</td>
			</c:if>
			
			<c:set value="0" var="count"></c:set>
			<c:forEach items="${i.distributions}" var="j">
			<c:choose>
			<c:when test="${j.statusTypeType=='clarification'}">
			<td>${j.count}</td>
			<c:set value="${count+1}" var="count"></c:set>
			<c:set value="${clarification+j.count}" var="clarification"></c:set>
			</c:when>
			<c:otherwise>
			</c:otherwise>
			</c:choose>
			</c:forEach>
			
			<c:if test="${count==0 }">
			<td>-</td>
			</c:if>
			
			
			</c:if>
			<td>${i.totalCount }</td>
			<c:set value="${total+i.totalCount}" var="total"></c:set>
			</tr>
			<c:if test="${size==totalMembers }">
			<c:set value="${(admitted/total)*100}" var="admittedpercent"></c:set>
			<c:set value="${(unstarred/total)*100}" var="unstarredpercent"></c:set>
			<c:set value="${(rejected/total)*100}" var="rejectedpercent"></c:set>
			<c:set value="${(clarification/total)*100}" var="clarificationpercent"></c:set>
			<c:set value="${i.formatNumber(admitted,locale)}" var="admitted"></c:set>
			<c:set value="${i.formatNumber(unstarred,locale)}" var="unstarred"></c:set>
			<c:set value="${i.formatNumber(rejected,locale)}" var="rejected"></c:set>
			<c:set value="${i.formatNumber(clarification,locale)}" var="clarification"></c:set>
			<c:set value="${i.formatNumber(total,locale)}" var="total"></c:set>
			<c:set value="${i.formatDecimalNumber(admittedpercent,locale)}" var="admittedpercent"></c:set>
			<c:set value="${i.formatDecimalNumber(unstarredpercent,locale)}" var="unstarredpercent"></c:set>
			<c:set value="${i.formatDecimalNumber(rejectedpercent,locale)}" var="rejectedpercent"></c:set>
			<c:set value="${i.formatDecimalNumber(clarificationpercent,locale)}" var="clarificationpercent"></c:set>
			</c:if>
			</c:forEach>
			<tr>
			<td></td>
			<td style="font-weight: bold;"><spring:message code="memberdistribution.total" text="Total"></spring:message></td>
			<td style="font-weight: bold;">${admitted }</td>
			<td style="font-weight: bold;">${unstarred }</td>
			<td style="font-weight: bold;">${rejected }</td>
			<td style="font-weight: bold;">${clarification }</td>
			<td style="font-weight: bold;">${total }</td>
			</tr>
			<tr>
			<td></td>
			<td style="font-weight: bold;"><spring:message code="memberdistribution.percentage" text="Percentage"></spring:message></td>
			<td style="font-weight: bold;">${admittedpercent }%</td>
			<td style="font-weight: bold;">${unstarredpercent }%</td>
			<td style="font-weight: bold;">${rejectedpercent }%</td>
			<td style="font-weight: bold;">${clarificationpercent }%</td>
			<td></td>
			</tr>
			</tbody>
			</table>
		</div>
	</c:when>
	<c:otherwise>
		<h2><spring:message code="memberdistribution.noresultsfound" text="No Results Found"></spring:message></h2>
	</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="houseType" value="${houseType}"/>
<input type="hidden" id="houseTypeName" value="${houseTypeName}"/>
<input type="hidden" id="sessionTypeName" value="${sessionTypeName}"/>
<input type="hidden" id="sessionYear" value="${sessionYear}"/>
<input type="hidden" id="sessionCountName" value="${sessionCountName}"/>
<input type="hidden" id="questionSubmissionStartTime" value="${questionSubmissionStartTime}"/>
<input type="hidden" id="questionSubmissionEndTime" value="${questionSubmissionEndTime}"/>
<input type="hidden" id="questionSubmissionDate" value="${questionSubmissionDate}"/>
<input type="hidden" id="dayTime" value="${dayTime}"/>
<input type="hidden" id="questionTypeName" value="${questionTypeName}"/>
</body>
</html>