<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				$('#forToday').click(function() {
					if($(this).is(':checked')) {
						$('#fromDate').attr('disabled', 'disabled');
						$('#toDate').attr('disabled', 'disabled');
					} else {
						$('#fromDate').removeAttr('disabled');
						$('#toDate').removeAttr('disabled');
					}
				});
				
				$('#includeYaadiQuestions').click(function() {
					if($(this).is(':checked')) {
						$('#includeConfirmedAnswers').attr('checked', 'checked');
						$('#includeConfirmedAnswers').attr('disabled', 'disabled');
					} else {
						$('#includeConfirmedAnswers').removeAttr('checked');
						$('#includeConfirmedAnswers').removeAttr('disabled');
					}
				});
				
				/**** Report Generation (Old) ****/
				$("#linkForReportOld").click(function(){	
					var reportURL = "question/report/online_offline_answered_count_report?session="+$('#session').val()
							+"&houseType="+$('#houseType').val()
							+"&questionType="+$('#questionType').val()
							+"&criteria="+$('#criteria').val();
							
					if($('#forToday').is(':checked')) {
						$('#forToday').val("true");
						reportURL += "&forToday="+$('#forToday').val();
						
					} else {						
						if($('#fromDate').val()==undefined || $('#fromDate').val()=='') {
							alert("Please Enter From Date");
							return false;
						}
						if($('#toDate').val()==undefined || $('#toDate').val()=='') {
							alert("Please Enter To Date");
							return false;
						}
						$('#forToday').val("false");
						reportURL += "&forToday="+$('#forToday').val()+"&fromDate="+$('#fromDate').val()+"&toDate="+$('#toDate').val();
					}
					
					$(this).attr('href', reportURL);					
				});		
				
				/**** Report Generation ****/
				$('#linkForReport').click(function() {
					var criteria = "departmentwise";					
					if($('#selectedAnswerReceivedStatus').val()=='answerReceived') {
						criteria = $('#criteria').val();
					}
					
					var forToday = "false";
					if($('#forToday').is(':checked')) {
						forToday = "true";
						
					} else {						
						if($('#fromDate').val()==undefined || $('#fromDate').val()=='') {
							alert("Please Enter From Date");
							return false;
						}
						if($('#toDate').val()==undefined || $('#toDate').val()=='') {
							alert("Please Enter To Date");
							return false;
						}
					}
					
					var includeYaadiQuestions = "no";
					if($('#includeYaadiQuestions').is(':checked')) {
						includeYaadiQuestions = "yes";
						
					}
					
					var includeConfirmedAnswers = "no";
					if($('#includeConfirmedAnswers').is(':checked')) {
						includeConfirmedAnswers = "yes";
						
					}
					
					/* var answeredStatus = "";
					if($('#selectedAnswerReceivedStatus').val()=='answerReceived') {
						answeredStatus = "_answered"
					} else if($('#selectedAnswerReceivedStatus').val()=='answerReceived') {
						answeredStatus = "_unanswered"
					} */ 
					
					var parameters = {
							houseType				: $("#selectedHouseType").val(),
							sessionYear				: $('#selectedSessionYear').val(), 
							sessionType				: $("#selectedSessionType").val(), 
							group					: $("#selectedGroup").val(),
							subDepartment			: $("#selectedSubDepartment").val(),
							originalDeviceType		: $("#selectedOriginalDeviceType").val(),
							answerReceivedStatus	: $('#selectedAnswerReceivedStatus').val(), 
							criteria				: criteria,		
							fromDate				: $('#fromDate').val(),
							toDate					: $('#toDate').val(),
							forToday				: forToday,	
							includeYaadiQuestions	: includeYaadiQuestions,	
							includeConfirmedAnswers	: includeConfirmedAnswers,
							locale					: $("#moduleLocale").val(), 
							role					: $("#srole").val(),
							reportQuery				: "QIS_UNSTARRED_ANSWERED_DEPARTMENTWISE_QUESTIONS"/* + "_" + $("#selectedHouseType").val().toUpperCase()*/,
							xsltFileName			: 'template_departmentwise_unstarred_answered_questions'/* + '_' + $("#selectedHouseType").val()*/,
							outputFormat			: 'WORD',
							reportFileName			: "departmentwise_unstarred_answered_questions"/* + "_" + $("#selectedHouseType").val()*/
					}
					
					form_submit('question/report/departmentwise_unstarred_answered_questions', parameters, 'GET');
				});
			});
		</script>		 
	</head>	
	<body>		
		<p id="error_p" style="display: none;">&nbsp;</p>
		<div class="fields clearfix">
		<p>
			<label class="small"><spring:message code="generic.fromDate" text="From Date"/>&nbsp;*</label>
			<input id="fromDate" class="datemask sText" type="text" name="fromDate"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="generic.toDate" text="To Date"/>&nbsp;*</label>
			<input id="toDate" class="datemask sText" type="text" name="toDate"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="generic.forToday" text="For Today?"/></label>
			<input id="forToday" class="sCheck" type="checkbox" name="forToday"/>
		</p>
		
		<c:if test="${answerReceivedStatus=='answerReceived'}">
		<p>
			<label class="small"><spring:message code="question.onlineansweredcountreport.includeYaadiQuestions" text="Include Yaadi Questions?"/></label>
			<input id="includeYaadiQuestions" class="sCheck" type="checkbox" name="includeYaadiQuestions"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="question.onlineansweredcountreport.includeConfirmedAnswers" text="Include Answers In Confirmation Questions??"/></label>
			<input id="includeConfirmedAnswers" class="sCheck" type="checkbox" name="includeConfirmedAnswers"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="generic.criteria" text="Criteria"/></label>
			<select class="sSelect" id="criteria" name="criteria">
				<option value="departmentwise"><spring:message code="generic.criteria.departmentwise" text="Departmentwise"/></option>
				<option value="datewise"><spring:message code="generic.criteria.datewise" text="Datewise"/></option>
			</select>
		</p>
		</c:if>
			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport" style="font-size: 20px;">
					<spring:message code='generic.generateReport' text='Generate Report'/>
				</a>
			</p>
		</div>
		</div>
	</body>
</html>