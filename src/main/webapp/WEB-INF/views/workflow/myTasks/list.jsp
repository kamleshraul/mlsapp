<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			if($("#deviceTypeType").val() == 'motions_calling_attention'){
				//displayNewAdvanceCopyForMotion();
				$("#advanceCopyDiv").css("display","inline");
			}else{
				$("#advanceCopyDiv").hide();
			}
			$('.datetimemask').mask("99-99-9999,99:99:99");			
			if($("#deviceTypeType").val() != 'motions_adjournment'){
				$("#selectedAdjourningDate").val("");				
			}
			var selectedAdjourningDate = convertToDbFormat($('#selectedAdjourningDate').val());
			/**** Initially we want to get only those tasks which belongs to current user and of selected status ****/
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&module="+$("#selectedModule").val()
						+"&status="+$("#selectedStatus").val()
						+"&workflowSubType="+$("#selectedSubWorkflow").val()
						+"&assignee="+$("#assignee").val()
						+"&group="+(($("#selectedGroup").val()==undefined)?"":$("#selectedGroup").val())
						+"&answeringDate="+$("#selectedAnsweringDate").val()
						+"&subdepartment="+$("#selectedDepartment").val()
						+"&adjourningDate="+selectedAdjourningDate
						+"&replyReceivedStatus="+$("#selectedReplyStatus").val()
						);
			$('#process_record').click(function(){
				process($('#key').val());
			});			
			$("#search").click(function() {
				searchRecord();
			});
			$("#provide_date").click(function(){	
				$("#selectionDiv").hide();
				provideDate();
			});
			/**** Generate Intimation Letter ****/			
			$("#generateIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateIntimationLetter();				
			});
			
			$("#generateCurrentStatusReport").click(function(){
				var selectedWorkflowDetailsId = $('#grid').jqGrid('getGridParam', 'selrow'); 
					//$("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedWorkflowDetailsId != null && selectedWorkflowDetailsId.length >= 1){
					showCurrentStatusReport('multiple',selectedWorkflowDetailsId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			$("#generateShortNoticeAnswerDateReport").click(function(){
				var selectedWorkflowDetailsId = $('#grid').jqGrid('getGridParam', 'selrow'); 
				if(selectedWorkflowDetailsId==undefined || selectedWorkflowDetailsId=='') {
					$.prompt($('#selectRowFirstMessage').val());
					return false;
				} else {
					showShortNoticeAnswerDateReport(selectedWorkflowDetailsId);
				}
			});
		
			if($("#deviceTypeType").val()=='questions_shortnotice'){
				$("#shortNoticeAnswerDateDiv").show();
			}else{
				$("#shortNoticeAnswerDateDiv").hide();
			}
			
			$('#groupChangeLink').click(function(){
				var selectedWorkflowDetailsId = $('#grid').jqGrid('getGridParam', 'selrow'); 
				$.get('ref/testchart?wfid='+selectedWorkflowDetailsId,function(data){
					$.prompt(data);
				});
			});
			
			$("#questionSummaryReport").click(function(e){
				if($("#sumRepDiv").css('display')=='none'){
					$("#sumRepDiv").show();
					$("#sumRepFromDate").val('');
					$("#sumRepToDate").val('');
				}else if($("#sumRepDiv").css('display')=='inline'){
					$("#sumRepDiv").hide();
				}
			});
			
			$("#goSumRep").click(function(e){
				$("#sumRepDiv").hide();
				questionSummaryReport();
			});
			pendingNewSupplementaryClubbingTasks();
			$("#resolutionWorkflowSummaryReport").click(function(e){
				if($("#resolutionSumRepDiv").css('display')=='none'){
					$("#resolutionSumRepDiv").show();
					$("#sumRepFromDate").val('');
					$("#sumRepToDate").val('');
				}else if($("#resolutionSumRepDiv").css('display')=='inline'){
					$("#resolutionSumRepDiv").hide();
				}
			});
			
			$("#goResolutionRep").click(function(e){
				$("#resolutionSumRepDiv").hide();
				generateResolutionWorkflowSummaryReport();
			});
			
			$("#resolutionSumRepDiv").hide();
			$("#sumRepDiv").hide();
			
			$(".sentBackTasksReport").click(function(e){
				sentBackTasksReport();
			});
			
			$("#departmentStatementReport").click(function(e){
				generateDepartmentStatementReport();
			});
			
			$("#deviceSupplement").click(function(e){
				showSupplementaryWorkflow();
			});
			
			$("#advanceCopy").click(function(e){
				showAdvanceMotionCopy();
			});
			/* if($("#currentusergroupType").val()=='department' 
					||$("#currentusergroupType").val()=='department_deskofficer' ){
				$("#intimationLetterFilter").css("display","none");
			} */
		});
	</script>
	<style type="text/css">
		#goSumRep:hover{
			cursor: pointer;
		}
		
		#goSumRep{
			cursor: default;
		}
		#supplementaryNotificationDiv, #advanceCopyNotificationDiv{
			background: #FF0000 scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			border-radius: 15px;
			text-align: center;
			border: 1px solid black;
			cursor: pointer;
		}
	</style>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<hr>
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','HDS_SECTION_OFFICER','QIS_DEPARTMENT_USER','ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_DEPARTMENT_USER')">
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='question.intimationletter.member' text='member' /></option>
						<option value="department"><spring:message code='question.intimationletter.department' text='department' /></option>
						<option value="prestatus"><spring:message code='question.intimationletter.prestatus' text='pre-status' /></option>
						<option value="discussiondate"><spring:message code='question.intimationletter.discussiondate' text='discussion date' /></option>
						<option value="groupChangedAfterBallot"><spring:message code='question.intimationletter.groupChangedAfterBallot' text='group changed after ballot' /></option>
						<option value="answeringDateForwarded"><spring:message code='question.intimationletter.answeringDateForwarded' text='answering date forwarded' /></option>
				</select>				
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_UNDER_SECRETARY','QIS_UNDER_SECRETARY_COMMITTEE','QIS_SECRETARY','QIS_DEPUTY_SECRETARY','QIS_JOINT_SECRETARY','QIS_CHAIRMAN','QIS_SPEAKER','QIS_SECTION_OFFICER','ROIS_DEPUTYSECRETARY', 'AMOIS_SECTION_OFFICER', 'AMOIS_DEPUTY_SECRETARY')">
				|
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
				<div id="shortNoticeAnswerDateDiv" style="display: inline;">
					<a href="#" id="generateShortNoticeAnswerDateReport" class="butSim">
						<spring:message code="question.generateShortNoticeAnswerDateReport" text="Generate Shortnotice Answer Date Report"/>
					</a> |
				</div>
			 </security:authorize>
			 <security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_UNDER_SECRETARY','QIS_UNDER_SECRETARY_COMMITTEE','QIS_SECRETARY','QIS_DEPUTY_SECRETARY','QIS_CHAIRMAN','QIS_SPEAKER','QIS_ASSISTANT','QIS_SECTION_OFFICER','QIS_JOINT_SECRETARY','ROIS_DEPUTYSECRETARY')">
				 <a href="javascript:void(0);" id="questionSummaryReport" class="butSim">
					<spring:message code="question.summaryReport" text="Question Summary Report"/>
				 </a>
				 <div id="sumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;">
					<div id="goSumRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div> |
				 <a href="javascript:void(0);" class="sentBackTasksReport" class="butSim">
					<spring:message code="generic.report.sentBackTasksReport" text="Sent Back Tasks Report"/>
				 </a>
			 </security:authorize>
			 <security:authorize access="hasAnyRole('ROIS_PRINCIPAL_SECRETARY','ROIS_UNDER_SECRETARY','ROIS_UNDER_SECRETARY_COMMITTEE','ROIS_SECRETARY','ROIS_DEPUTY_SECRETARY','ROIS_CHAIRMAN','ROIS_SPEAKER','ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_JOINT_SECRETARY')">
				 <a href="javascript:void(0);" id="resolutionWorkflowSummaryReport" class="butSim">
					<spring:message code="resolution.summaryReport" text="Resolution Summary Report"/>
				 </a>
				 <div id="resolutionSumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;width:115px">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;width:115px">
					<div id="goResolutionRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div>
			 </security:authorize>
			 <security:authorize access="hasAnyRole('QIS_DEPARTMENT_USER')">
				 <a href="javascript:void(0);" id="departmentStatementReport" class="butSim">
					<spring:message code="resolution.departmentStatementReport" text="Department Statement"/>
				 </a>|
				 <a href="javascript:void(0);" id="deviceSupplement" class="butSim">
					<spring:message code="device.deviceSupplement" text="Device Supplement"/>
				 </a>
				 <div id="supplementaryNotificationDiv" style="display:none;" title="<spring:message code="device.deviceSupplementCount" text="Device Supplement Count"/>">
					
				 </div>
			 </security:authorize>|
			
			<div id="advanceCopyDiv" style="display:none;">
				<a href="javascript:void(0);" id="advanceCopy" class="butSim">
					<spring:message code="device.advanceCopy" text="Advance Copy"/>
				</a>
				<div id="advanceCopyNotificationDiv" style="display:none;" title="<spring:message code="device.advanceCopyCount" text="Advance Copy Count"/>">
					
				</div>
			</div>
			
			 <br>
			 <hr>
			 <a href="#" id="process_record" class="butSim">
				<spring:message code="generic.details" text="Process"/>
			</a>  |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a>  |
			<security:authorize access="hasAnyRole('BILL_DEPARTMENT_USER')">			
			 <a href="#" id="provide_date" class="butSim">
				<spring:message code="generic.giveintroductiondate" text="Provide Introduction Date"/>
			</a> |
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<script type="text/javascript">
		$('#grid').jqGrid('setSelection',$("#persistentGridRowId").val());
	</script>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	</div>
</body>
</html>
