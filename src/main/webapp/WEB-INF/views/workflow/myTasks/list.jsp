<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){ 
			/**** Initially we want to get only those tasks which belongs to current user and of selected status ****/
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&module="+$("#selectedModule").val()
						+"&status="+$("#selectedStatus").val()
						+"&workflowSubType="+$("#selectedSubWorkflow").val()
						+"&assignee="+$("#assignee").val()
						+"&group="+$("#selectedGroup").val()
						+"&answeringDate="+$("#selectedAnsweringDate").val()
						);
			$('#process').click(function(){
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
		});
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="process_record" class="butSim">
				<spring:message code="generic.details" text="Process"/>
			</a>  |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a>  
			<security:authorize access="hasAnyRole('BILL_DEPARTMENT_USER')">			
			| <a href="#" id="provide_date" class="butSim">
				<spring:message code="generic.giveintroductiondate" text="Provide Introduction Date"/>
			</a>
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','HDS_SECTION_OFFICER')">
			|	<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='question.intimationletter.member' text='member' /></option>
						<option value="department"><spring:message code='question.intimationletter.department' text='department' /></option>
						<option value="prestatus"><spring:message code='question.intimationletter.prestatus' text='pre-status' /></option>
						<option value="discussiondate"><spring:message code='question.intimationletter.discussiondate' text='discussion date' /></option>
				</select>				
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_UNDER_SECRETARY','QIS_UNDER_SECRETARY_COMMITTEE')">
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
