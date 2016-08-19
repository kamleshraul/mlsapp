<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var answeredCount = $('table.strippedTable td.answer:not(:empty)').length;
			$.ajax({url: 'ref/formatNumbersInGivenText', data: "numberedText=" + answeredCount, 
				type: 'GET',
				async: false,
		        success: function(data) {
		        	$('#answeredCount').empty();
					$('#answeredCount').html(data);
				}
			});		
			
			var answerPendingCount = $('table.strippedTable td.answer:empty').length;
			$.ajax({url: 'ref/formatNumbersInGivenText', data: "numberedText=" + answerPendingCount, 
				type: 'GET',
				async: false,
		        success: function(data) {
		        	$('#answerPendingCount').empty();
					$('#answerPendingCount').html(data);
				}
			});
			
			$('#answeringDate').change(function() {
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedQuestionType").val()
				 +"&subDepartment="+$("#selectedSubDepartment").val()
				 +"&answeringDate="+$("#answeringDate").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&locale="+$("#moduleLocale").val()
				 +"&role="+$("#srole").val()
				 +"&report="+"QIS_DEPARTMENTWISE_QUESTIONS_"+$("#selectedHouseType").val().toUpperCase()
				 +"&reportout="+"departmentwisequestions";
				var resourceURL = 'question/report/departmentwisequestions?'+ parameters;			
				showTabByIdAndUrl('details_tab', resourceURL);
			});
			
			$('#departmentwisequestions__pdf').click(function() {
				$(this).attr('href', '#');
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedQuestionType").val()
				 +"&subDepartment="+$("#selectedSubDepartment").val()
				 +"&answeringDate="+$("#answeringDate").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&totalCount="+$("#totalCount").html()
				 +"&answeredCount="+$("#answeredCount").html()
				 +"&answerPendingCount="+$("#answerPendingCount").html()
				 +"&locale="+$("#moduleLocale").val()
				 +"&report="+"QIS_DEPARTMENTWISE_QUESTIONS_"+$("#selectedHouseType").val().toUpperCase()
				 +"&reportxsl="+"departmentwisequestions"
				 +"&reportout="+"departmentwisequestions"
				 +"&outputFormat="+"PDF";	
				var resourceURL = 'question/report/departmentwisequestions/export?'+ parameters;
				$(this).attr('href', resourceURL);
			});
			$('#departmentwisequestions__word').click(function() {
				$(this).attr('href', '#');
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedQuestionType").val()
				 +"&subDepartment="+$("#selectedSubDepartment").val()
				 +"&answeringDate="+$("#answeringDate").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&locale="+$("#moduleLocale").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&totalCount="+$("#totalCount").html()
				 +"&answeredCount="+$("#answeredCount").html()
				 +"&answerPendingCount="+$("#answerPendingCount").html()
				 +"&report="+"QIS_DEPARTMENTWISE_QUESTIONS_"+$("#selectedHouseType").val().toUpperCase()
				 +"&reportxsl="+"departmentwisequestions"
				 +"&reportout="+"departmentwisequestions"
				 +"&outputFormat="+"WORD";	
				var resourceURL = 'question/report/departmentwisequestions/export?'+ parameters;
				$(this).attr('href', resourceURL);
			});
			
			if($("#selectedSubDepartment").val()=='0'){
				$("#deptName").hide();
			}
		});
	</script>
	<style type="text/css" media="print">
		th{			
			font-size: 15px !important;
			vertical-align: top !important;
		}		
		#topHeader{
			font-size: 20px !important;
			font-weight: bold;
		}
	</style>
	<style type="text/css">
		.strippedTable {
			width: 875px !important;
		}
		th{
			border-left: 0px;border-right: 0px;
			vertical-align: top !important;
		}
		td{			
			vertical-align: top;
			border: 0px;
		}
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:if test="${selectedHouseType=='lowerhouse' and selectedDeviceType==starredDeviceType and selectedStatus==admittedStatus}">
	<c:if test="${not empty selectedAnsweringDate or (empty selectedAnsweringDate and not empty report)}">
		<div style="margin-bottom: 20px;">
			<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
			<select name="answeringDate" id="answeringDate" style="width: 100px; height: 25px;">
				<option value=""><spring:message code="please.select" /></option>
				<c:forEach items="${answeringDates}" var="i">
					<c:choose>
						<c:when test="${i[0]==selectedAnsweringDate}">
							<option value="${i[0]}" selected="selected">${i[1]}</option>
						</c:when>
						<c:otherwise>
							<option value="${i[0]}">${i[1]}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</div>
	</c:if>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="question.report.notCreated" text="Report not Created"/>
</c:when>

<c:when test="${empty report}">
	<div><spring:message code="question.report.empty" text="No entry found."/></div>
</c:when>

<c:otherwise>
<div>
	<a id="departmentwisequestions__pdf" class="exportLink" href="#" style="text-decoration: none;">
		<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
	</a>
	&nbsp;
	<a id="departmentwisequestions__word" class="exportLink" href="#" style="text-decoration: none;">
		<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
	</a>
</div>
<div id="reportDiv" >
	<div style="max-width: 800px; width: 800px; margin-left: 25px;">
		<h3 id="topHeader" style="text-align: center; color: black; text-decoration: underline; font-family: 'Times New Roman';">
			${report[0][2]} ${localisedContent[1]}
		</h3>
		<div style="font-size: 15px;">
			${localisedContent[2]} :- &nbsp;&nbsp;&nbsp;&nbsp;<b>${report[0][3]}</b>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			${localisedContent[3]} :- &nbsp;&nbsp;&nbsp;&nbsp;<b>${report[0][4]}</b>
		</div>
		<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
			${report[0][12]} - ${report[0][14]}
		</div>
		<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
			${localisedContent[6]} - ${report[0][6]}
		</div>
		<div style="margin-top: 15px;font-size: 18px;font-weight: bold;text-decoration: underline;" id="deptName">
			${localisedContent[5]} - ${report[0][5]}
		</div>
		<c:if test="${selectedHouseType=='lowerhouse' and report[0][11]=='questions_starred' and report[0][13]=='question_final_admission' and not empty selectedAnsweringDate}">
			<div style="text-align: center;margin-top: 15px;font-size: 18px;font-weight: bold;text-decoration: underline;">
				${report[0][17]}&nbsp;${localisedContent[12]}
			</div>
		</c:if>
	</div>
	<br />
	<c:set var="columns" value="0" />	
	<table class="strippedTable" style="margin-left: 25px; font-size: 15px; border-top: 0px;border-right: 0px;border-bottom: 0px;border-left: 0px;">
		<thead>
			<tr>
				<th style="text-align: center;">${localisedContent[0]}</th>
				<%-- <th style="text-align: center;">${localisedContent[6]}</th> --%>
				<th style="text-align: center;">${localisedContent[7]}</th>
				<th>${localisedContent[8]}</th>
				<c:choose>
					<c:when test="${report[0][11]=='questions_starred' and report[0][13]=='question_final_admission'}">
					 	<th>${localisedContent[10]}</th>
						<th>${localisedContent[9]}</th>
						<th>${localisedContent[11]}</th>
					</c:when>
					<c:when test="${report[0][11]=='questions_unstarred' and report[0][13]=='question_unstarred_final_admission'}">
					 	<th>${localisedContent[9]}</th>
					 	<th>${localisedContent[12]}</th>						
						<th>${localisedContent[11]}</th>
						<th>${localisedContent[10]}</th>
					</c:when>	
					<c:otherwise><th>${localisedContent[9]}</th></c:otherwise>
				</c:choose>		
				<th style="display: none; width: 0px;">Answer for count</th>		
			</tr>
			<!-- <tr>
				<th style="font-size: 8px;" colspan="4">&nbsp;</th>
			</tr> -->
		</thead>
		<tbody>
			<c:forEach items="${report}" var="r" varStatus="counter">
				<tr style="border-top: 0px;border-bottom: 0px;">
					<td width="5%">${formater.formatNumbersInGivenText(counter.count, locale)}</td>
					<%-- <td width="5%">${r[6]}</td> --%>
					<td width="10%" style="text-align: center;">
						${r[7]}												
						<c:if test="${not empty clubbedNumbers}">	
							<br/>											
							${clubbedNumbers[counter.count-1]}				
						</c:if>
					</td>
					<td width="20%">${r[9]}</td>
					<c:choose>
						<c:when test="${report[0][11]=='questions_starred' and report[0][13]=='question_final_admission'}">
						 	<td width="15%">${r[15]}</td>
							<td width="40%">${r[10]}</td>
							<td width="10%">${r[16]}</td>
						</c:when>
						<c:when test="${report[0][11]=='questions_unstarred' and report[0][13]=='question_unstarred_final_admission'}">
						 	<td width="40%">${r[10]}</td>
						 	<td width="10%">${r[21]}</td>
						 	<td width="5%">${r[16]}</td>							
							<td width="10%">${r[15]}</td>
						</c:when>	
						<c:otherwise><td width="65%">${r[10]}</td></c:otherwise>
					</c:choose>		
					<td class="answer" style="display: none;" width="0%">${r[22]}</td>				
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<c:if test="${report[0][11]=='questions_unstarred' and report[0][13]=='question_unstarred_final_admission'}">
		<div style="max-width: 800px; width: 800px; margin-left: 25px; margin-top: 20px;">
			<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
				<spring:message code="qis.departmentwisequestions.totalcount" text="Total Questions"/> - <label id="totalCount">${formater.formatNumbersInGivenText(fn:length(report), locale)}</label>
			</div>
			<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
				<spring:message code="qis.departmentwisequestions.answeredcount" text="Answered Questions"/> - <label id="answeredCount">Answered Count</label>
			</div>	
			<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
				<spring:message code="qis.departmentwisequestions.answerpendingcount" text="Answer Pending Questions"/> - <label id="answerPendingCount">Answer Pending Count</label>
			</div>
		</div>
	</c:if>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="selectedHouseType" value="${selectedHouseType}"/>
<input type="hidden" id="selectedSessionType" value="${selectedSessionType}"/>
<input type="hidden" id="selectedSessionYear" value="${selectedSessionYear}"/>
<input type="hidden" id="selectedSubDepartment" value="${selectedSubDepartment}"/>
<input type="hidden" id="locale" value="${locale}"/>
</body>

</html>