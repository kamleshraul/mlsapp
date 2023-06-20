<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
		
			$("#convertStarredToUnStarred").hide();
			
			$(".datemask").mask("99-99-9999");
			$('.datetimemask').mask("99-99-9999,99:99:99");	
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&questionType="+$("#selectedQuestionType").val()
					+"&deviceType=" + $("#selectedQuestionType").val()
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
			if(currentDeviceType == 'questions_starred' || currentDeviceType == 'questions_unstarred') {
				$('#admitted_departmentwise_report_span').show();
			} else {
				$("#admitted_departmentwise_report_span").hide();
			}	
			if($('#member_admitted_questions_view_flag').val()=="admitted_visible") {
				$('#member_admitted_questions_view_span').show();
			}
			if($('#member_rejected_questions_view_flag').val()=="rejected_visible") {
				$('#member_rejected_questions_view_span').show();
			}
			if($('#member_unstarred_questions_view_flag').val()=="unstarred_visible") {
				$('#member_unstarred_questions_view_span').show();
			}
			/**** show/hide member_starred_suchi_view span as per selected devicetype ****/
			if($('#currentusergroupType').val()=='member' && currentDeviceType == 'questions_stared') {
				$('#member_starred_suchi_view_span').show();
				$('#suchiAnsweringDate').css('display', 'inline-block'); 
				populateSessionAnsweringDatesForMemberSuchiView();		
			} else {
				$('#suchiAnsweringDate').css('display', 'none');
				$("#member_starred_suchi_view_span").hide();
			}
			if($('#currentusergroupType').val()=='member' && currentDeviceType == 'questions_starred') {
				$("#determine_ordering_for_submission_span").show();
			} else {
				$("#determine_ordering_for_submission_span").hide();
			}
			if($('#currentusergroupType').val()=='member' 
					&& currentDeviceType == 'questions_starred'
					&& $('#processMode').val()  == 'upperhouse') {
				$("#memberballotchoice_span").show();
			} else {
				$("#memberballotchoice_span").hide();
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
			
			/****Determine Ordering of Questions for Submission ****/
			$("#determine_ordering_for_submission").click(function() {
				$("#selectionDiv1").hide();
				determineOrderingForSubmission();
			});
			
			/****Provide questions choices for first batch choices post member ballot ****/
			$("#memberballotchoice").click(function() {
				if($('#questionChoiceSubmissionWindowClosedMsg').val()!=undefined
						&& $('#questionChoiceSubmissionWindowClosedMsg').val()!='') {
					
					$.prompt($('#questionChoiceSubmissionWindowClosedMsg').val());
					
				} else {
					$("#selectionDiv1").hide();
					fillMemberBallotChoices();
				}
			});
			
			$('#member_firstbatch_questions_report').click(function() {
				$("#selectionDiv1").hide();
				//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
				var parameters="member=0&session="+$("#loadedSession").val()
							  +"&questionType="+$("#selectedQuestionType").val();
				var resource='ballot/memberballot/member/questions?'+parameters;
				showTabByIdAndUrl('details_tab',resource);
				/* setTimeout(function(){
					$.unblockUI();
				},2000); */
			});
			
			/****Member's Questions View ****/
			$("#member_questions_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsView("all");
			});
			
			/****Member's Questions Detail View ****/
			$("#member_questions_detail_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsDetailView();
			});
			
			/****Member's Rejected Questions View ****/
			$("#member_rejected_questions_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsView("rejected");
			});
			
			/****Member's Admitted Questions View ****/
			$("#member_admitted_questions_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsView("admitted");
			});
			
			/****Member's Unstarred Questions View ****/
			$("#member_unstarred_questions_view").click(function() {
				$("#selectionDiv1").hide();
				memberQuestionsView("unstarred");
			});
			
			/****Member's Starred Questions Suchi View ****/
			$("#member_starred_suchi_view").click(function() {
				if($('#suchiAnsweringDate').css('display')!='none' && $('#suchiAnsweringDate').val()=='0') {
					$.prompt($('#suchiAnsweringDateSelectionPromptMsg').val());
				} else if($('#suchiAnsweringDate').css('display')=='none' && $('#suchiAnsweringDate').val()=='0') {
					$('#suchiAnsweringDate').css('display', 'inline-block');
				} else if($('#suchiAnsweringDate').val()!='0') {	
					$("#selectionDiv1").hide();
					memberStarredSuchiView();
				}
			});
			
			$("#statreport").click(function(){
				if($('#processMode').val()=="lowerhouse") {
					statReport();
				} 
				else if($('#processMode').val()=="upperhouse") {
					if($("#statRepDiv").css('display')=='none'){
						$("#statRepDiv").show();
						$("#submissionBatchForStatReport").val('-');
					}else if($("#statRepDiv").css('display')=='inline'){
						$("#statRepDiv").hide();
					}
				}
			});
			
			$("#goStatRep").click(function(e){
				$("#statRepDiv").hide();				
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
			
			/**** Unstarred Admitted Questions Departmentwise Report ****/
			$("#admitted_departmentwise_report").click(function(){
				var currentDeviceType = 
					$("#deviceTypeMaster option[value='"+ $("#selectedQuestionType").val() + "']").text();
				if(currentDeviceType == 'questions_unstarred'
						&& $('#selectedAnswerReceivedStatus').val()=='answerReceived') {
					departmentwiseUnstarredAnsweredQuestionsReport();
				} else {
					departmentwiseAdmittedQuestionsReport();
				}				
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
			
			$("#showDeptSessionReport").click(function(){
				$("#selectionDiv1").hide();
				deptSessionreport();
			});
			
			/* $("#starredAdmitUnstarred").click(function(){
				var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedQuestionId.length>=1){
					showStarredAdmitUnstarredReport(selectedQuestionId);
				}else{
					showStarredAdmitUnstarredReport('');
				}				
			}); */
			
			$("#starredAdmitUnstarred").click(function(e){
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
				
				var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedQuestionId.length>=1){
					showStarredAdmitUnstarredReport(selectedQuestionId);
				}else{
					showStarredAdmitUnstarredReport('');
				}				
			});
			
			$("#statRepDiv").hide();
			$("#sumRepDiv").hide();
			//------stats reports as html-----------------------ends----------------
			
			
			
			/*---Added By Shubham A---------*/
			$("#convertStarredToUnStarred").click(function(){
				
				
				
				var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedQuestionId.length == 0)
				{
				$.prompt($('#selectQsnMsg').val());
				} 
				else{
				
				
				var items =new Array();
				var myArray = new Array();
				for(var i = 0 ; i<selectedQuestionId.length;i++ ){
					var BallotStatus = $("#grid").jqGrid ('getCell', selectedQuestionId[i], 'ballotStatus.type');
				
				if(BallotStatus == "question_processed_balloted")
					{
					var Qnumber = $("#grid").jqGrid ('getCell', selectedQuestionId[i], 'number');
						if(Qnumber != "")
						{
						if(Qnumber.indexOf("<") != -1)
							{
							 myArray = Qnumber.split("<");
							 items.push(myArray[0]);
							}
							else{
							items.push(Qnumber);
							}
						}
					
					} 	
				}
				
				if(items.length > 0)
					{
					var numbers ="";
						for( var i=0 ;i< items.length;i++)
							{
								if( items[i] != ""){
								numbers  = numbers + items[i]+",";}
							}
						if(numbers.charAt(numbers.length-1) == ",")
							{
							numbers = numbers.substring(0, numbers.length-1);
							}
					
					$.prompt($('#submissionMsg').val()+numbers);
					}
				else{
					
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post('question/questionStatusUpdate',
				        	{selectedQuestionId:selectedQuestionId,
							deviceType : '5'
						 	},
		    	            function(data){
		       						
		    					$.unblockUI();
		    					if(data != 'Failed'){
		    					$.prompt($('#UpdatedMsg').val()+data)
		    					}
		    					else{
		    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				    				
		    					}
		    	            }
		    	            ); 
					
				}}
				
			});
			/*---------------------------------*/
			
			
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
				<span id="determine_ordering_for_submission_span">
				<a href="#" id="determine_ordering_for_submission" class="butSim">
					<spring:message code="question.determine_ordering_for_submission" text="Determine Ordering for Submission"/>
				</a> |
				</span>
				<span id="memberballotchoice_span">
				<a href="#" id="memberballotchoice" class="butSim link">
					<spring:message code="memberballot.memberballotchoice" text="Question Choices"/>
				</a> |
				<a href="#" id="member_firstbatch_questions_report" class="butSim link" style="display: none;">
					<spring:message code="member_firstbatch_questions_report" text="First Batch Questions Report"/> |
				</a>
				</span>
				<hr/>
				<a href="#" id="member_questions_view" class="butSim">
					<spring:message code="question.member_questions_view" text="Member's Questions View"/>
				</a> |
				<a href="#" id="member_questions_detail_view" class="butSim">
					<spring:message code="question.member_questions_detail_view" text="Member's Questions Detail View"/>
				</a> |				
				<span id="member_starred_suchi_view_span" style="display: none;">
				<a href="#" id="member_starred_suchi_view" class="butSim">
					<spring:message code="question.member_starred_suchi_view" text="Starred Questions Suchi"/>
				</a>
				<select id="suchiAnsweringDate" name="suchiAnsweringDate" style="width: 115px; height: 25px;display: none;">
					<option value="0"><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message></option>
				</select> |
				<%-- <a class="butSim" href="javascript:void(0);" id="goSuchiView"><spring:message code="suchi_view.go" text="Go" /></a> | --%>
				</span>	
				<hr/>				
				<span id="member_admitted_questions_view_span" style="display: none;">
				<a href="#" id="member_admitted_questions_view" class="butSim">
					<spring:message code="question.member_admitted_questions_view" text="Member's Admitted Questions Detail View"/>
				</a> |
				</span>
				<span id="member_rejected_questions_view_span" style="display: none;">
				<a href="#" id="member_rejected_questions_view" class="butSim">
					<spring:message code="question.member_rejected_questions_view" text="Member's Rejected Questions Detail View"/>
				</a> |		
				</span>
				<span id="member_unstarred_questions_view_span" style="display: none;">
				<a href="#" id="member_unstarred_questions_view" class="butSim">
					<c:choose>
					<c:when test="${member_unstarred_questions_view_flag=='unstarred_visible'}">
						<spring:message code="question.member_unstarred_questions_view" text="Member's Unstarred Questions Detail View"/>
					</c:when>
					<c:otherwise>
						<spring:message code="question.member_reconsidered_unstarred_questions_view" text="Member's Reconsidered Unstarred Questions Detail View"/>
					</c:otherwise>
					</c:choose>
				</a> |	
				</span>		
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER', 'QIS_CHAIRMAN','QIS_DEPUTY_SECRETARY')">
				<a href="#" id="statreport" class="butSim">
					<spring:message code="question.statreport" text="Generate Statistics Report"/>
				</a>
				<div id="statRepDiv" style="display: inline;">
					<select name="submissionBatch" id="submissionBatchForStatReport" style="height: 25px;">
						<option value="batch-1" selected="selected"><spring:message code="question.submission_batch1" text="Batch 1"/></option>
						<option value="batch-2"><spring:message code="question.submission_batch2" text="Batch 2"/></option>
						<option value="both"><spring:message code="question.submission_batch.all" text="All"/></option>
					</select>
					<div id="goStatRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				</div>
			</security:authorize> |
			 <security:authorize access="!hasAnyRole('QIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
				<%-- <a href="#" id="starredAdmitUnstarred" class="butSim link">
					<spring:message code="question.starredAdmitUnstarred" text="Question Summary Report"/>
				</a> | --%>
				<a href="#" id="starredAdmitUnstarred" class="butSim link">
					<spring:message code="question.starredAdmitUnstarred" text="Question Summary Report"/>
				</a>
				 <div id="sumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;">
					<div id="goSumRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_CLERK')">	 
				<a href="#" id="generateAdmissionReport" class="butSim">
					<spring:message code="question.generateAdmissionReport" text="Generate Admission Report"/>
				</a> |
			</security:authorize>				
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER','QIS_CHAIRMAN','QIS_CLERK')">
				<a href="#" id="showDeptSessionReport" class="butSim">
						<spring:message code="question.deptSessionReport" text="Department-Session-wise Report"/>
				</a> |
			</security:authorize>	
			<br>
			<a href="#" id="convertStarredToUnStarred" class="butSim">
			<spring:message code="question.convertStarredToUnStarred" text="convert Starred To UnStarred"/>
			</a> 
			<hr>
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','QIS_CLERK','HDS_CLERK','QIS_CHAIRMAN','HDS_ASSISTANT')">
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
						<option value="previewReminderReportForAnswer"><spring:message code='question.intimationletter.previewReminderReportForAnswer' text='preview of reminder report' /></option>
						<option value="reminderToDepartmentForAnswer"><spring:message code='question.intimationletter.reminderToDepartmentForAnswer' text='reminder generation for answer' /></option>
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
				<span id="admitted_departmentwise_report_span">
				<a href="#" id="admitted_departmentwise_report" class="butSim link">
					<spring:message code="question.admitted_departmentwise_report" text="Admitted Departmentwise Report"/>
				</a> |
				</span>				
			</security:authorize>	
					
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input id="submissionMsg" value="<spring:message code='' text='Please Unselect the following questions :-'></spring:message>" type="hidden">
	<input id="selectQsnMsg" value="<spring:message code='' text='Please Select  questions :-'></spring:message>" type="hidden">
	<input id="UpdatedMsg" value="<spring:message code='' text=' following questions are converted to Unstarred :-'></spring:message>" type="hidden">
	<input id="suchiAnsweringDateSelectionPromptMsg" value="<spring:message code='question.suchiAnsweringDateSelectionPromptMsg' text='Please select the suchi answering date!'/>" type="hidden">	
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>