<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$(".datemask").mask("99-99-9999");
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&questionType="+$("#selectedQuestionType").val()
					+"&originalDeviceType=" + $("#selectedOriginalDeviceType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&clubbingStatus="+$("#selectedClubbingStatus").val()
					+"&answerReceivedStatus=" + $("#selectedAnswerReceivedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&subDepartment="+$('#selectedSubDepartment').val()
					+"&answeringDate="+$("#selectedModuleAsweringDate").val()
					);
			/**** show/hide unstarred yaadi link as per selected devicetype ****/
			var currentDeviceType = 
				$("#deviceTypeMaster option[value='"+ $("#selectedQuestionType").val() + "']").text();
			if(currentDeviceType == 'questions_unstarred') {
				$('#unstarred_admitted_departmentwise_report_span').show();
			} else {
				$("#unstarred_admitted_departmentwise_report_span").hide();
			}			
			$("#member_statistics").click(function(){
				memberStatistics();
			});
			/**** new question ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newQuestion();
			});
			
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editQuestion();
			});
			
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteQuestion();
			});	
			
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
			/****Member's Questions View ****/
			$("#member_questions_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsView();
			});
			
			/****Member's Questions Detail View ****/
			$("#member_questions_detail_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsDetailView();
			});
			
			$("#statreport").click(function(){
				statReport();
			});
			
			//---ADDED BY VIKAS------------------
			
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedQuestionType").change(function(){
				$("#gridURLParams").val("houseType=" + $("#selectedHouseType").val()
						+ "&sessionYear=" + $("#selectedSessionYear").val()
						+ "&sessionType=" + $("#selectedSessionType").val()
						+ "&questionType=" + $(this).val()
						+ "&originalDeviceType=" + $("#selectedOriginalDeviceType").val()
						+ "&ugparam=" + $("#ugparam").val()
						+ "&status=" + $("#selectedStatus").val()
						+ "&clubbingStatus=" + $("#selectedClubbingStatus").val()
						+ "&answerReceivedStatus=" + $("#selectedAnswerReceivedStatus").val()
						+ "&role=" + $("#srole").val()
						+ "&usergroup=" + $("#currentusergroup").val()
						+ "&usergroupType=" + $("#currentusergroupType").val()
						+"&answeringDate="+$("#selectedModuleAsweringDate").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());				
			});
						
			/**** Generate Intimation Letter ****/			
			$("#generateIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateIntimationLetter();				
			});
			
			/**** Generate Clubbed Intimation Letter ****/			
			$("#generateClubbedIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateClubbedIntimationLetter();				
			});
			
			/**** Generate Unstarred Yaadi Report ****/			
			$("#unstarred_yaadi_report").click(function(){
				$(this).attr('href','#');
				generateUnstarredYaadiReport();
			});
			
			/**** Generate Unstarred Suchi Report ****/			
			$("#unstarred_suchi_report").click(function(){
				$(this).attr('href','#');
				generateUnstarredSuchiReport();
			});
			
			/**** Generate Member's Questions Report ****/
			$("#memberwise_questions_report").click(function(){
				$("#selectionDiv1").hide();
				memberwiseQuestionsReport();
			});	
			
			/**** Generate Online Offline Submission Count Report ****/
			$("#online_offline_submission_count_report").click(function(){
				generateOnlineOfflineSubmissionCountReport();
			});
			
			/**** Questions Bulletein Report ****/
			$("#group_bulletein_report").click(function(){				
				$(this).attr('href','#');
				groupBulleteinReport();
			});
			
			/**** Questions Bulletein Report ****/
			$("#bulletein_report").click(function(){				
				$(this).attr('href','#');
				bulleteinReport();
			});
			
			/**** Departmentwise Questions Report ****/
			$("#departmentwise_report").click(function(){				
				$("#selectionDiv1").hide();
				departmentwiseQuestionsReport();
			});			
			
			/**** Unstarred Admitted Questions Departmentwise Report ****/
			$("#unstarred_admitted_departmentwise_report").click(function(){
				departmentwiseUnstarredAdmittedQuestionsReport();
			});
			
			/**** Sankshipt Ahwal Report ****/
			$("#ahwal_report").click(function(){				
				$("#selectionDiv1").hide();
				sankshiptAhwalReport();
			});
			
			//------stats reports as html-----------------------starts----------------
			$("#generateCurrentStatusReport").click(function(){
				var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedQuestionId.length>=1){
					showCurrentStatusReport('multiple',selectedQuestionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			$("#generateAdmissionReport").click(function(){
				$("#selectionDiv1").hide();
				showAdmissionReport();
			});
			
			$("#hdDays").hide();
			
			$("#showHDDayWiseReport").click(function(){
				$("#hdDays").toggle();
			});
			
			$("#showhddaywisereport").click(function(){
				$("#selectionDiv1").hide();
				showHDDaywisereport();
			});
			
			$("#showHDStatAndAdmissionReport").click(function(){
				$("#selectionDiv1").hide();
				showHDStatAndAdmissionreport();
			});
			
			$("#showHDGeneralReport").click(function(){
				$("#selectionDiv1").hide();
				showHDGeneralreport();
			});
			
			$("#showDeptSessionReport").click(function(){
				$("#selectionDiv1").hide();
				deptSessionreport();
			});
			
			$("#showVivranReport").click(function(){
				$("#selectionDiv1").hide();
				showVivranReport();
			});
			
			$("#showHDBallotChoiceOptionReport").click(function(){
				$("#selectionDiv1").hide();
				showBallotChoiceOptionReport();
			});
			
			var selectedDeviceType = $("#deviceTypeMaster option[value='" 
			                                                     + $("#selectedQuestionType").val() + "']").text();
			if(selectedDeviceType.indexOf("questions_halfhourdiscussion_")==-1){
				$("#hdReportsDiv").hide();
			}else{
				$("#hdReportsDiv").show();
			}
			
			$("#starredAdmitUnstarred").click(function(){
				showStarredAdmitUnstarredReport();
			});
			//------stats reports as html-----------------------ends----------------
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}			
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST','HDS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="question.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="question.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST','HDS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="question.delete" text="Delete"/>
			</a> |			
			<a href="#" id="submitQuestion" class="butSim" style="display: none;">
				<spring:message code="generic.submitquestion" text="submit"/>
			</a> 
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="question.search" text="Search"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="member_questions_view" class="butSim">
					<spring:message code="question.member_questions_view" text="Member's Questions View"/>
				</a> |
				<a href="#" id="member_questions_detail_view" class="butSim">
					<spring:message code="question.member_questions_detail_view" text="Member's Questions Detail View"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<a href="#" id="statreport" class="butSim">
					<spring:message code="question.statreport" text="Generate Statistics Report"/>
				</a> |
			</security:authorize>
			 <security:authorize access="!hasAnyRole('QIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
				<a href="#" id="starredAdmitUnstarred" class="butSim link">
					<spring:message code="question.starredAdmitUnstarred" text="Question Summary Report"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_CLERK')">	 
				<a href="#" id="generateAdmissionReport" class="butSim">
					<spring:message code="question.generateAdmissionReport" text="Generate Admission Report"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_TYPIST', 'QIS_CLERK', 'QIS_ASSISTANT')">
				<div id="hdReportsDiv" style="display: inline;">
					<hr>
					<a href="#" id="showHDDayWiseReport" class="butSim">
						<spring:message code="question.hdDayWiseReport" text="HD Day wise Report"/>
					</a> <div id="hdDays" style="display: inline; width: 200px;">
						<input type="text" value="0" id="hdDaysForReport" class="sText datemask" style="width: 60px; border: 1px solid black; border-radius: 2px;"/>
						<a href="javascript:void(0);" id="showhddaywisereport" >Go</a>
					</div>|
					<a href="#" id="showHDStatAndAdmissionReport" class="butSim">
						<spring:message code="question.hdStatAndAdmissionReport" text="HD Stat and Admission Report"/>
					</a> |
					<a href="#" id="showHDGeneralReport" class="butSim">
						<spring:message code="question.hdGeneralReport" text="HD General Report"/>
					</a> |
					<a href="#" id="showHDBallotChoiceOptionReport" class="butSim">
						<spring:message code="question.BallotChoiceOptionReport" text="HD Ballot Choice Option Report"/>
					</a> |
				</div>
			</security:authorize>	
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER','QIS_CLERK')">
				<a href="#" id="showDeptSessionReport" class="butSim">
						<spring:message code="question.deptSessionReport" text="Department-Session-wise Report"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<a href="#" id="showVivranReport" class="butSim">
					<spring:message code="question.vivranReport" text="Vivran Report"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','QIS_CLERK','HDS_CLERK','HDS_ASSISTANT')">
				<hr>
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='question.intimationletter.member' text='member' /></option>
						<option value="department"><spring:message code='question.intimationletter.department' text='department' /></option>
						<option value="prestatus"><spring:message code='question.intimationletter.prestatus' text='pre-status' /></option>
						<option value="discussiondate"><spring:message code='question.intimationletter.discussiondate' text='discussion date' /></option>
						<option value="groupchanged"><spring:message code='question.intimationletter.groupChanged' text='group changed' /></option>
						<option value="groupChangedAfterBallot"><spring:message code='question.intimationletter.groupChangedAfterBallot' text='group changed after ballot' /></option>
						<option value="answeringDateForwarded"><spring:message code='question.intimationletter.answeringDateForwarded' text='answering date forwarded' /></option>
						<option value="reminderToDepartmentForAnswer"><spring:message code='question.intimationletter.reminderToDepartmentForAnswer' text='reminder for answer' /></option>
				</select> | 
				<a href="#" id="generateClubbedIntimationLetter" class="butSim">
					<spring:message code="question.generateClubbedIntimationLetter" text="Generate Clubbed Intimation Letter"/>
				</a> | 						
				<span id="unstarredYaadiSpan" style="display: none;">
				<a href="#" id="unstarred_yaadi_report" class="butSim link">
					<spring:message code="question.unstarred_yaadi_report" text="Unstarred Yaadi Report"/>
				</a> |
				<a href="#" id="unstarred_suchi_report" class="butSim link">
					<spring:message code="question.unstarred_suchi_report" text="Unstarred Suchi Report"/>
				</a> |
				</span>
				<span id="unstarred_admitted_departmentwise_report_span">
				<a href="#" id="unstarred_admitted_departmentwise_report" class="butSim link">
					<spring:message code="question.unstarred_admitted_departmentwise_report" text="Unstarred Admitted Departmentwise Report"/>
				</a> |
				</span>
				<hr> 
				<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<a href="#" id="memberwise_questions_report" class="butSim link">
					<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
				</a> |		
				<a href="#" id="online_offline_submission_count_report" class="butSim link">
					<spring:message code="question.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
				</a> |
				<hr> 
				</security:authorize>
				<security:authorize access="hasAnyRole('QIS_CLERK')">
				<a href="#" id="online_offline_submission_count_report" class="butSim link">
					<spring:message code="question.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
				</a> |
				</security:authorize>
				<a href="#" id="group_bulletein_report" class="butSim link">
					<spring:message code="question.group_bulletein_report" text="Group Bulletein Report"/>
				</a> |
				<a href="#" id="bulletein_report" class="butSim link">
					<spring:message code="question.bulletein_report" text="Bulletein Report"/>
				</a> |
				<a href="#" id="departmentwise_report" class="butSim link">
					<spring:message code="question.departmentwise_report" text="Department's Questions Report"/>
				</a> |
				<a href="#" id="ahwal_report" class="butSim link">
					<spring:message code="question.ahwal_report" text="Sankshipt Ahwal Report"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_DEPUTY_SECRETARY')">
				<a href="#" id="memberwise_questions_report" class="butSim link">
					<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
				</a> |		
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>