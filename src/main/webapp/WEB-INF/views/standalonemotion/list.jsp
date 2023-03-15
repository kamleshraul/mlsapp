<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="standalonemotion.list" text="List Of StandaloneMotions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$(".datemask").mask("99-99-9999");
			//$("#onlineOfflineCountReportDate").mask("9999-99-99");
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&questionType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&subDepartment="+$('#selectedSubDepartment').val()
					);
			/**** show/hide unstarred yaadi link as per selected devicetype ****/
			var currentDeviceType = $("#deviceTypeMaster option[value='"+ $("#selectedQuestionType").val() + "']").text();
			/**** new question ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newStandaloneMotion();
			});
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editStandaloneMotion();
			});
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteStandaloneMotion();
			});		
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
			if($('#member_admitted_standalonemotions_view_flag').val()=="admitted_visible") {
				$('#member_admitted_standalonemotions_view_span').show();
			}
			if($('#member_rejected_standalonemotions_view_flag').val()=="rejected_visible") {
				$('#member_rejected_standalonemotions_view_span').show();
			}
			if($('#member_unstarred_standalonemotions_view_flag').val()=="unstarred_visible") {
				$('#member_unstarred_standalonemotions_view_span').show();
			}
			
			/****Member's StandaloneMotions View ****/
			$("#member_standalonemotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberStandaloneMotionsView("all");
			});
			
			/****Member's Rejected StandaloneMotions View ****/
			$("#member_rejected_standalonemotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberStandaloneMotionsView("rejected");
			});
			
			/****Member's Admitted StandaloneMotions View ****/
			$("#member_admitted_standalonemotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberStandaloneMotionsView("admitted");
			});
						
			$("#statreport").click(function(){
				statReport();
			});
			
			//---ADDED BY VIKAS------------------
			
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedQuestionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&questionType="+$(this).val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());				
				var standAlone = $("#deviceTypeMaster option[value='"+$(this).val()+"']").text();
				if(standAlone=='motions_standalonemotion_halfhourdiscussion'){
					$("#new_record").html("<spring:message code='question.newStandAlone' text='New'/>");
				}
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
						
			/**** Generate Member's Standalone Motions Report ****/
			$("#memberwise_standalonemotions_report").click(function(){
				$("#selectionDiv1").hide();
				memberwiseStandaloneMotionReport();
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
			
			$("#send_message").click(function(){
				sendMessage();
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
			
			var selectedDeviceType = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
			if(selectedDeviceType.indexOf("motions_standalonemotion_halfhourdiscussion")==-1){
				$("#hdReportsDiv").css({'display':'none'});
			}else{
				$("#hdReportsDiv").css({'display':'inline'});
			}
			//------stats reports as html-----------------------ends----------------
			
			/* $("#onlineOfflineCountReport").click(function(){
				if($("#onlineOfflineCountReportDiv").css('display')!='none'){
					$("#onlineOfflineCountReportDiv").css({'display':'none'});
				}else{
					$("#onlineOfflineCountReportDiv").css({'display':'inline'});
				}
			}); */
			
			/* $("#goOnlineOfflineCountReport").click(function(){
				if($("#onlineOfflineCountReportDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					showCountMotion();
					$("#onlineOfflineCountReportDiv").css({'display': 'none'});
				}
			}); */
			
			/**** Generate Online Offline Submission Count Report ****/
			$("#online_offline_submission_count_report").click(function(){
				generateOnlineOfflineSubmissionCountReport();
			});
			
			$("#standaloneMotionSummary").click(function(e){
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
				
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedMotionId.length>=1){
					showStandaloneMotionSummaryReport(selectedMotionId);
				}else{
					showStandaloneMotionSummaryReport('');
				}				
			});
			
			$("#sumRepDiv").hide();
			//------stats reports as html-----------------------ends----------------

			
		});

		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'standalonemotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="question.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="question.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMOIS_TYPIST')">			
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
			<%-- <security:authorize access="!hasAnyRole('SMOIS_TYPIST','SMOIS_CLERK')">	 
				<a href="#" id="send_message" class="butSim">
					<spring:message code="question.sendMessage" text="Send Message"/>
				</a> |
			</security:authorize> --%>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_standalonemotions_view" class="butSim">
					<spring:message code="standalonemotion.member_standalonemotions_view" text="Member's Standalone Motions View"/>
				</a> |
				<span id="member_admitted_standalonemotions_view_span" style="display: none;">
				<a href="#" id="member_admitted_standalonemotions_view" class="butSim">
					<spring:message code="standalonemotion.member_admitted_standalonemotions_view" text="Member's Admitted Questions Detail View"/>
				</a> |
				</span>
				<span id="member_rejected_standalonemotions_view_span" style="display: none;">
				<a href="#" id="member_rejected_standalonemotions_view" class="butSim">
					<spring:message code="standalonemotion.member_rejected_standalonemotions_view" text="Member's Rejected Questions Detail View"/>
				</a> |		
				</span>				
			</security:authorize>
			<security:authorize access="hasAnyRole('SMOIS_SECTION_OFFICER')">
				<a href="#" id="statreport" class="butSim">
					<spring:message code="question.statreport" text="Generate Statistics Report"/>
				</a> |
			</security:authorize>
			 <security:authorize access="!hasAnyRole('SMOIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
				<a href="#" id="standaloneMotionSummary" class="butSim link">
					<spring:message code="standalone.standaloneMOtionSummary" text="Standalone Motion Summary Report"/>
				</a> 
				<div id="sumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;">
					<div id="goSumRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('SMOIS_PRINCIPAL_SECRETARY')">	 
				<a href="#" id="generateAdmissionReport" class="butSim">
					<spring:message code="question.generateAdmissionReport" text="Generate Admission Report"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('SMOIS_TYPIST', 'SMOIS_CLERK', 'SMOIS_ASSISTANT')">
				<div id="hdReportsDiv" style="margin-top: 10px;">
					<a href="#" id="showHDDayWiseReport" class="butSim">
						<spring:message code="question.hdDayWiseReport" text="HD Day wise Report"/>
					</a> <div id="hdDays" style="display: inline; width: 200px;">
						<input type="text" value="" class="sText datemask" id="hdDaysForReport" style="width: 60px; border: 1px solid black; border-radius: 2px;"/>
						<a href="javascript:void(0);" id="showhddaywisereport" >Go</a>
					</div>|
					<a href="#" id="showHDStatAndAdmissionReport" class="butSim">
						<spring:message code="question.hdStatAndAdmissionReport" text="HD Stat and Admission Report"/>
					</a> |
					<a href="#" id="showHDGeneralReport" class="butSim">
						<spring:message code="question.hdGeneralReport" text="HD General Report"/>
					</a> |
					<a href="#" id="online_offline_submission_count_report" class="butSim link">
						<spring:message code="smois.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
					</a> |
					<%-- <div style="display: inline;">
						<a href="javascript:void(0);" id="onlineOfflineCountReport" class="butSim">
							<spring:message code="motion.onlineoffline" text="Online Offline Count"/>
						</a> 
						<div style="display: none; width: 200px;" id="onlineOfflineCountReportDiv">
							<input id="onlineOfflineCountReportDate" class="sText datemask"/> <a class="butSim" href="javascript:void(0);" id="goOnlineOfflineCountReport"><spring:message code="goselmotion.go" text="Go" /></a>
							<!-- &nbsp;
							<a href="javascript:void(0);" id="formSelMotion" style="margin: 0px 0px 0px -10px;">
								<img width="20px" height="20px" src="./resources/images/word_icon.png"  title="<spring:message code='motion.selmotion.formation' text='Dated Motions'/>" />
							</a>-->						
						</div>
					</div>|	 --%>
					<%-- <a href="#" id="showHDBallotChoiceOptionReport" class="butSim">
						<spring:message code="question.BallotChoiceOptionReport" text="HD Ballot Choice Option Report"/>
					</a> | --%>
				</div>
			</security:authorize>	
			<security:authorize access="hasAnyRole('SMOIS_SECTION_OFFICER')">
				<%-- <a href="#" id="showDeptSessionReport" class="butSim">
						<spring:message code="question.deptSessionReport" text="Department-Session-wise Report"/>
				</a> | --%>
				<a href="#" id="showHDGeneralReport" class="butSim">
					<spring:message code="question.hdGeneralReport" text="HD General Report"/>
				</a> |
				<a href="#" id="showVivranReport" class="butSim">
						<spring:message code="question.vivranReport" text="Vivran Report"/>
				</a> |
			</security:authorize>		
			<security:authorize access="hasAnyRole('SMOIS_ASSISTANT','SMOIS_CLERK')">
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
				</select> | 
				<a href="#" id="generateClubbedIntimationLetter" class="butSim">
					<spring:message code="question.generateClubbedIntimationLetter" text="Generate Clubbed Intimation Letter"/>
				</a> | 						
				<!-- <hr>  -->
				<a href="#" id="memberwise_standalonemotions_report" class="butSim link">
					<spring:message code="standalonemotion.memberwisereport" text="Member's Standalone Motions Report"/>
				</a> |			
				<!-- <a href="#" id="group_bulletein_report" class="butSim link">
					<spring:message code="question.group_bulletein_report" text="Group Bulletein Report"/>
				</a> |
				<a href="#" id="bulletein_report" class="butSim link">
					<spring:message code="question.bulletein_report" text="Bulletein Report"/>
				</a> |
				<a href="#" id="departmentwise_report" class="butSim link">
					<spring:message code="question.departmentwise_report" text="Department's Questions Report"/>
				</a> |  -->
				<a href="#" id="ahwal_report" class="butSim link">
					<spring:message code="question.ahwal_report" text="Sankshipt Ahwal Report"/>
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