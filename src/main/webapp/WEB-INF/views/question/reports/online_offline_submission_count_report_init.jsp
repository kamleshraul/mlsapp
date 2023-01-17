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
				
				/**** Questions Online Offline Submission Count Report ****/
				$("#linkForReport").click(function(){	
					var reportURL = "question/report/online_offline_submission_count_report?session="+$('#session').val()
							+"&houseType="+$('#houseType').val()
							+"&questionType="+$('#questionType').val()
							+"&groupId="+$('#selectedGroupCount').val()
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
			});
		</script>		 
	</head>	
	<body>		
		<p id="error_p" style="display: none;">&nbsp;</p>
		<div class="fields clearfix">
		<p>
		<label class="small"><spring:message code="question.onlinesubmissioncountreport.forAllGroups" text="Groups"/>&nbsp;*</label>
		<select class="sSelect" name="selectedGroupCount" id="selectedGroupCount" style="width: 100px; height: 25px;">
		  <option value="0"><spring:message code="question.onlinesubmissioncountreport.forAllGroup" text="Select All"/></option>
			<c:forEach items="${groups}" var="i">
				<option value="${i.id}">
					<c:out value="${i.formatNumber()}"></c:out>
				</option>
			</c:forEach>
		</select>
		</p>
		
		<p>
			<label class="small"><spring:message code="question.onlinesubmissioncountreport.fromDate" text="From Date"/>&nbsp;*</label>
			<input id="fromDate" class="datemask sText" type="text" name="fromDate" value="${defaultFromDate}"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="question.onlinesubmissioncountreport.toDate" text="To Date"/>&nbsp;*</label>
			<input id="toDate" class="datemask sText" type="text" name="toDate" value="${defaultToDate}"/>
		</p>
			
		<c:if test="${isCurrentDateValidForSubmission == true}">
		<p>
			<label class="small"><spring:message code="question.onlinesubmissioncountreport.forToday" text="For Today?"/></label>
			<input id="forToday" class="sCheck" type="checkbox" name="forToday"/>
		</p>	
		</c:if>
		
		<p>
			<label class="small"><spring:message code="question.onlinesubmissioncountreport.criteria" text="Criteria"/></label>
			<select class="sSelect" id="criteria" name="criteria">
				<option value="memberwise"><spring:message code="question.onlinesubmissioncountreport.criteria.memberwise" text="Memberwise"/></option>
				<option value="datewise"><spring:message code="question.onlinesubmissioncountreport.criteria.datewise" text="Datewise"/></option>
			</select>
		</p>
			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport" style="font-size: 20px;">
					<spring:message code='question.onlinesubmissioncountreport.generateReport' text='Generate Report'/>
				</a>
			</p>
		</div>
		</div>
		<input type="hidden" id="session" value="${session}">
		<input type="hidden" id="questionType" value="${questionType}">
		<input type="hidden" id="houseType" value="${houseType}">
	</body>
</html>