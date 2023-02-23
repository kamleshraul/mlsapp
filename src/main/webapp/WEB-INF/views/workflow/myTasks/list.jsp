<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			var houseTypeList = $("#houseTypeMaster option[value='"+$("#selectedHouseType").val()+"']").text();
			var houseTypeType = $("#houseTypeMaster option[value='"+$("#selectedHouseType").val()+"']").text();
			
			if($("#deviceTypeType").val() == 'motions_calling_attention'){
				displayNewAdvanceCopyForMotion();
				//$("#advanceCopyDiv").css("display","inline");
			}else{
				$("#advanceCopyDiv").hide();
			}
			$('.datetimemask').mask("99-99-9999,99:99:99");			
			if($("#deviceTypeType").val() != 'motions_adjournment' 
					|| $("#deviceTypeType").val() != 'motions_rules_suspension'
					|| $("#deviceTypeType").val() != 'notices_specialmention'
				    || !(houseTypeType == 'upperhouse' && $("#deviceTypeType").val() == 'proprietypoint')){
				$("#selectedAdjourningDate").val("");				
			}
			if($("#deviceTypeType").val() == 'motions_adjournment'){
				$("#intimationLetterFilter option[class=adjournmentmotion]").show();
				$("#intimationLetterFilter option[class=question]").hide();
				$("#intimationLetterFilter option[class=unstarred_question]").hide();
			} else if($("#deviceTypeType").val() == 'questions_unstarred'){
				$("#intimationLetterFilter option[class=unstarred_question]").show();				
				$("#intimationLetterFilter option[class=question]").show();		
				$("#intimationLetterFilter option[class=adjournmentmotion]").hide();
			} else {
				$("#intimationLetterFilter option[class=adjournmentmotion]").hide();
				$("#intimationLetterFilter option[class=unstarred_question]").hide();
				$("#intimationLetterFilter option[class=question]").show();
			}
			if($("#deviceTypeType").val() == 'motions_cutmotion_budgetary' || $("#deviceTypeType").val() == 'motions_cutmotion_supplementary'){
				if($("#currentusergroupType").val()=='department' 
					|| $("#currentusergroupType").val()=='department_deskofficer'
						|| $("#currentusergroupType").val()=='member'){
					$("#yaadiReportSpan").show();
				} else {
					$("#yaadiReportSpan").hide();
				}				
			} else {
				$("#yaadiReportSpan").hide();
			}
			if($("#deviceTypeType").val() == 'questions_unstarred'
					|| $("#deviceTypeType").val() == 'motions_cutmotion_budgetary'
					|| $("#deviceTypeType").val() == 'motions_cutmotion_supplementary'){
				if($("#currentusergroupType").val()=='section_officer'
					|| $("#currentusergroupType").val()=='department'
					||$("#currentusergroupType").val()=='department_deskofficer' ){
					$("#reminderLetterSpan").show();
				} else {
					$("#reminderLetterSpan").hide();
				}				
			} else {
				$("#reminderLetterSpan").hide();
			}
			var selectedAdjourningDate = convertToDbFormat($('#selectedAdjourningDate').val());
			/**** Initially we want to get only those tasks which belongs to current user and of selected status ****/
			if($('#isAdjourningDateSelected').is(':checked')){
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
			      }
				else{
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
								+"&adjourningDate="+""
								+"&replyReceivedStatus="+$("#selectedReplyStatus").val()
								);
					}
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
			/**** Generate Reminder Letter ****/			
			$("#generateReminderLetter").click(function(){
				$(this).attr('href','#');
				if($("#currentusergroupType").val()=='department' 
					||$("#currentusergroupType").val()=='department_deskofficer' ){
					generateReminderLetter(false);
				} else {					
					$.prompt("Do you really want to send reminder letter to department now?",{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	generateReminderLetter(true);
		    	        } else {
		    	        	generateReminderLetter(false);
		    	        }
					}});
				}								
			});
			/**** Generate Yaadi Report ****/
			$("#generateYaadiReport").click(function(){				
				generateYaadiReport();
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
				
				var selectedWorkflowDetailsId = $('#grid').jqGrid('getGridParam', 'selarrrow'); 
				//$("#grid").jqGrid ('getGridParam', 'selarrrow');
			
			if(selectedWorkflowDetailsId != null && selectedWorkflowDetailsId.length >= 1){
				questionSummaryReport(selectedWorkflowDetailsId);
				
			}else{
				questionSummaryReport('');
			}
			
				
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
		
		function SupportMember(){				
			$.get("ref/sessionbyhousetypeworkflow" + "/" + $("#selectedHouseType").val()
					+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),function(data){
			if(data){
				$.get("ref/sessionbydevicetypeworkflow" + "/" +$("#selectedDeviceType").val(),function(data1){
                    if(data1){
                        
							var queParams = encodeURI('devicescommonreport/report/generalreport?'
							+'sessionId='+data
							+'&originaldevicetypeId='+$("#selectedDeviceType").val()
							    +'&housetypeName='+$("#selectedHouseType").val()
								    +'&decisionStatus='+$("#selectedSupportStatus").val()
									+'&locale='+$("#moduleLocale").val()
								    +'&report=WORKFLOW_QUESTION_COMMON_REPORT'
									+'&reportout=devicesSupportMemberCommonReport');
							
							var moParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_MOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var admoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_ADMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var cumoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_CUMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var smoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_SMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var dmoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_DMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var rmoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_RMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
							
							var ppmoParams = encodeURI('devicescommonreport/report/generalreport?'
									+'sessionId='+data
									+'&originaldevicetypeId='+$("#selectedDeviceType").val()
									    +'&housetypeName='+$("#selectedHouseType").val()
										    +'&decisionStatus='+$("#selectedSupportStatus").val()
											+'&locale='+$("#moduleLocale").val()
											+'&report=WORKFLOW_PPMOTION_COMMON_REPORT'
											+'&reportout=devicesSupportMemberCommonReport');
														
							if(data1 == 4 || data1 == 5 || data1 == 7 || data1 == 49) {
					   			showTabByIdAndUrl('details_tab',queParams);
							}
							else if(data1 == 101){
								showTabByIdAndUrl('details_tab',moParams);
							}
							else if(data1 == 2552){
								showTabByIdAndUrl('details_tab',admoParams);
							}
							else if(data1 == 104 || data1 == 105){
								showTabByIdAndUrl('details_tab',cumoParams);
							}		
							else if(data1 == 2550){
								showTabByIdAndUrl('details_tab',smoParams);
							}	
							else if(data1 == 2601 || data1 == 2602){
								showTabByIdAndUrl('details_tab',dmoParams);
							}
							else if(data1 == 2700){
								showTabByIdAndUrl('details_tab',rmoParams);
							}
							else if(data1 == 106){
								showTabByIdAndUrl('details_tab',ppmoParams);
							}
							else {
							   	alert("Currently Report not available for this device");
							}
                     
                    }
				});					
	     	}
			});
			
		}
	
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
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','HDS_SECTION_OFFICER','QIS_DEPARTMENT_USER','ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_DEPARTMENT_USER','AMOIS_SECTION_OFFICER','CMOIS_SECTION_OFFICER','SMIS_ASSISTANT','SMIS_SECTION_OFFICER','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option class="all" value="-">-</option>
					 	<option class="question" value="member"><spring:message code='question.intimationletter.member' text='member' /></option>
						<option class="question" value="department"><spring:message code='question.intimationletter.department' text='department' /></option>
						<option class="question" value="prestatus"><spring:message code='question.intimationletter.prestatus' text='pre-status' /></option>
						<option class="question" value="discussiondate"><spring:message code='question.intimationletter.discussiondate' text='discussion date' /></option>
						<option class="question" value="groupChangedAfterBallot"><spring:message code='question.intimationletter.groupChangedAfterBallot' text='group changed after ballot' /></option>
						<option class="question" value="answeringDateForwarded"><spring:message code='question.intimationletter.answeringDateForwarded' text='answering date forwarded' /></option>
						<option class="unstarred_question" value="reminderToDepartmentForReply"><spring:message code='intimationletter.reminderToDepartmentForReply' text='reminder for reply' /></option>
						<option class="adjournmentmotion" value="reminder1ToDepartmentForReply"><spring:message code='intimationletter.reminder1ToDepartmentForReply' text='reminder 1 for reply' /></option>
						<option class="adjournmentmotion" value="reminder2ToDepartmentForReply"><spring:message code='intimationletter.reminder2ToDepartmentForReply' text='reminder 2 for reply' /></option>
				</select>				
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_UPPERHOUSE','MEMBER_LOWERHOUSE')">
			<select id="selectedSupportStatus" name="selectedSupportStatus">
				<option value="all"><spring:message code="supportReport.all" text="All"></spring:message></option>
				<option value="supportingmember_pending"><spring:message code="supportReport.pending" text="Pending"></spring:message></option>
				<option value="supportingmember_approved"><spring:message code="supportReport.approved" text="Approved"></spring:message></option>
				<option value="supportingmember_rejected"><spring:message code="supportReport.rejected" text="Rejected"></spring:message></option>
				<option value="supportingmember_timeout"><spring:message code="supportReport.timeout" text="Timeout"></spring:message></option>
				<%-- <option value="supportingmember_notsend"><spring:message code="supportReport.notsend" text="Supporting Member Not Send"></spring:message></option> --%>
			</select>	
			<a href="javascript:void(0);" id="support_member_report" onclick="SupportMember();" class="butSim" >
		     <spring:message code="workflow.supportingmember.report" text="Supporting Member Report"/>
			</a>
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
			 <security:authorize access="hasAnyRole('ROIS_PRINCIPALSECRETARY','ROIS_UNDERSECRETARY','ROIS_UNDER_SECRETARY_COMMITTEE','ROIS_SECRETARY','ROIS_DEPUTYSECRETARY','ROIS_CHAIRMAN','ROIS_SPEAKER','ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_JOINT_SECRETARY')">
				 <a href="javascript:void(0);" id="resolutionWorkflowSummaryReport" class="butSim">
					<spring:message code="resolution.summaryReport" text="Resolution Summary Report"/>
				 </a>
				 <div id="resolutionSumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;width:115px">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;width:115px">
					<div id="goResolutionRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div>
			 </security:authorize>
			 <security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE')">
			 	<span id="yaadiReportSpan" style="display: none;">
			 	 <a href="#" id="generateYaadiReport" class="butSim">
					<spring:message code="generic.mytask.device.YaadiReport" text="Yaadi Report"/>
				 </a> |
				 </span>
			 </security:authorize>
			 <security:authorize access="hasAnyRole('QIS_DEPARTMENT_USER', 'CMOIS_SECTION_OFFICER', 'CMOIS_DEPARTMENT_USER')">
			 	 |
			 	 <span id="reminderLetterSpan" style="display: none;">
			 	 <a href="#" id="generateReminderLetter" class="butSim">
					<spring:message code="generic.mytask.device.ReminderLetter" text="Reminder Letter"/>
				 </a> |
				 </span>			 	
			 </security:authorize>
			 <security:authorize access="hasAnyRole('QIS_DEPARTMENT_USER')">
			 	 <span id="yaadiReportSpan" style="display: none;">
			 	 <a href="#" id="generateYaadiReport" class="butSim">
					<spring:message code="generic.mytask.device.YaadiReport" text="Yaadi Report"/>
				 </a> |
				 </span>
				 <a href="javascript:void(0);" id="departmentStatementReport" class="butSim">
					<spring:message code="resolution.departmentStatementReport" text="Department Statement"/>
				 </a>|
				 <a href="javascript:void(0);" id="deviceSupplement" class="butSim">
					<spring:message code="device.deviceSupplement" text="Device Supplement"/>
				 </a>|
				 <div id="supplementaryNotificationDiv" style="display:none;" title="<spring:message code="device.deviceSupplementCount" text="Device Supplement Count"/>">
					
				 </div>|
			 </security:authorize>
			
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
