<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".datemask").mask("9999-99-99");
			var memberArray = [];			
			
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();		
			
			$("#bulkputup_tab").show();
			
			$("#reminderLetterFilterDiv").hide();
			
			/*Tooltip*/
			$(".toolTip").hide();					
			/**** here we are trying to add date mask in grid search when field names ends with date ****/
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});
			
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();		
				$("#assistantReportDiv").hide();
				showMotionList();
			});			
			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					if($("#currentusergroupType").val()=='member') {
						updateVisibilityForMemberMotionsViewLinks();
					}
					reloadMotionGrid();	
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){	
					if($("#currentusergroupType").val()=='member') {
						updateVisibilityForMemberMotionsViewLinks();
					}	
					reloadMotionGrid();
				}			
			});
			
			$("#selectedSubDepartment").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadMotionGrid();
				}
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){		
					if($("#currentusergroupType").val()=='member') {
						updateVisibilityForMemberMotionsViewLinks();
					}	
					reloadMotionGrid();
				}			
			});
			/**** motion type changes then reload grid****/			
			$("#selectedMotionType").change(function(){
				var value = $(this).val();
				var text = $("#deviceTypeMaster option[value='"+value+"']").text();				
				if(value != ""){				
					reloadMotionGrid();
				}				
			});	
			/**** status changes then reload grid****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					//reloadMotionGrid();
					showMotionList();
					$("#selectedFileCount").val("-");
				}
			});	
			/**** Ballot ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
			});	
			
			/**** motion_general_statistics_report ****/
			$('#motion_general_statistics_report').click(function(){
				GeneralStatisticsReport();
			});	

			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				/** keep it disabled for members till submission date validations are added or submission is allowed **/
				/* if(currentDeviceType=='motions_calling_attention'
						&& $("#currentusergroupType").val()=='member') {
					$.prompt("Submission is not started for this session yet.. Please contact branch for more details!");
					return false;
				} else { */
					$("#selectionDiv1").hide();
					bulkPutup();
				//}				
			});	
			/**** Bulk Putup ****/
			$("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			});	
			
			/**** Search ****/
			$('#search_tab').click(function() {
				$("#selectionDiv1").hide();
				searchInt();
			});
			
			/**** for deciding upon department filter or just loading grid only ****/
			if($("#usersAllowedForDepartmentFilter").val().indexOf($("#currentusergroupType").val())>=0){
				/****Load departments and load the motion list user is not typist****/
				loadSubDepartmentsFromGroup('0', 'yes');
			}else{
				showMotionList();
			}
			
			/**** Status Update ****/
			$("#statusupdate_tab").click(function() {
				$("#selectionDiv1").hide();
				statusUpdate();
			});
			
			$("#reports_link").click(function(e){
				$("#assistantReportDiv").toggle("slow");
			});
			
			$("#status_report").click(function(e){
				statusWiseReport();
			});
			
			$("#department_report").click(function(e){
				var dept = $("#selectedSubDepartment").val();
				departmentWiseReport(dept);	
			});
			
			/**** clubbing status changes then reload grid ****/
			$("#selectedClubbingStatus").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadMotionGrid();
				}			
			});
			
			//$("select[name='"+controlName+"']").hide();
			
			/* $(".autosuggest").autocomplete({						
				source: memberArray,
				select:function(event,ui){	
					$('#members').val("");
					$('#members option').each(function(){						
						if($(this).text()==ui.item.value) {							
							$(this).attr('selected', 'selected');
							generateMemberWiseReport();
						}
					});			
				}	
			});	 */
			
			$("#members").change(function(){
				var val = $(this).val();
				if(val!="" && val!='-'){
				 	memberWiseReport($(this).val()); 
				
				}
			});
			
			/* Motion Discussion */
			$("#motion_discussion_report").click(function(){		
				motionDiscussionReport();
			});
			
			$("#reminderLetterReport").click(function(){
				if($("#reminderLetterFilterDiv").css('display')=='none'){
					$("#reminderLetterFilterDiv").show();
					$("#reminderLetterFilter").val('preview');
				}else if($("#reminderLetterFilterDiv").css('display')=='inline'){
					$("#reminderLetterFilterDiv").hide();
				}
			});
			
			$("#goRLRep").click(function(e){
				$("#reminderLetterFilterDiv").hide();				
				reminderLetterReport();				
			});
			
		/* 	$("#member_report").click(function(){
				
				var val = $('#members').val();
				if(val!="" && val!='-'){
					memberWiseReport(val); 
				
				}
			}); */
			
			//$("select[name='"+controlName+"']").hide();
			
			/* $(".autosuggest").autocomplete({						
				source: memberArray,
				select:function(event,ui){	
					$('#members').val("");
					$('#members option').each(function(){						
						if($(this).text()==ui.item.value) {							
							$(this).attr('selected', 'selected');
							generateMemberWiseReport();
						}
					});			
				}	
			});	 */
			
			$("#parties").change(function(){
				var val = $(this).val();
				if(val!="" && val!='-'){
					partyWiseReport($(this).val());
				}
			});
			
			/* Edited By Shubham A  */
			$("#ActiveMinistries").change(function(){
				var val = $("#ActiveMinistries").val();
			
					ActiveMinistryReport($(this).val());
				
			});
			
			
			/**** Submission Time Window ****/
			$("#session_time_window").click(function(event, isHighSecurityValidationRequired){			
				/* if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				} */
				$(this).attr('href','#');
				setsessionTimeWindow();
			});
			 
			
			/*  */
			
			$("#entry_register").click(function(e){
				registerReport();
			});			
			
			
			$("#admission_report").click(function(){
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showAdmissionReport(selectedId[0]);
				}
			});
			
			$("#preAdmissionIntimationReport").click(function(){
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showPreIntimationAdmissionReport(selectedId[0]);
				}
			});
			
			
			$("#jodPatra").click(function(){
				if($("#jodPatraDiv").css('display')!='none'){
					$("#jodPatraDiv").css({'display':'none'});
				}else{
					$("#jodPatraDiv").css({'display':'inline'});
				}
			});
			
			$("#motionDiscStatus").click(function(){
				if($("#discussionStatusDiv").css('display')!='none'){
					$("#discussionStatusDiv").css({'display':'none'});
				}else{
					$("#discussionStatusDiv").css({'display':'inline'});
				}
			});
			
			$("#selMotion").click(function(){
				if($("#selMotionDiv").css('display')!='none'){
					$("#selMotionDiv").css({'display':'none'});
				}else{
					$("#selMotionDiv").css({'display':'inline'});
				}
			});
			
			/* $("#onlineOfflineCountReport").click(function(){
				if($("#onlineOfflineCountReportDiv").css('display')!='none'){
					$("#onlineOfflineCountReportDiv").css({'display':'none'});
				}else{
					$("#onlineOfflineCountReportDiv").css({'display':'inline'});
				}
			}); */
			
			$("#goJodPatra").click(function(){
				if($("#jodPatraDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					showJodPatra();
					$("#jodPatraDiv").css({'display': 'none'});
				}
			});
			
			$("#goDiscStatus").click(function(){
				if($("#discStatusDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					showDiscussionStatus();
					$("#discussionStatusDiv").css({'display': 'none'});
				}
			});
			
			$("#goSelMotion").click(function(){
				if($("#selMotionDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					showSelMotion();
					$("#selMotionDiv").css({'display': 'none'});
				}
			});
			
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
			
			$("#formJodPatra").click(function(){
				if($("#jodPatraDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					formJodPatra();
					$("#jodPatraDiv").css({'display': 'none'});
				}
			});
			
			$("#formDiscStatus").click(function(){
				if($("#discStatusDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					formDiscussionStatus();
					$("#discussionStatusDiv").css({'display': 'none'});
				}
			});
			
			//------stats reports as html-----------------------starts----------------
			$("#generateCurrentStatusReport").click(function(){
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			$("#advanceStatusReport").click(function(){
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedMotionId.length>=1){
					showAdvanceStatusReport(selectedMotionId);
				}
			});
			
			$("#intimationletter").click(function(){
				
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showIntimationLetter(selectedId[0]);
				}
			});
			
			
			$("#discussionStatsReport").click(function(){
				if($("#discussionStatsReportDiv").css('display')!='none'){
					$("#discussionStatsReportDiv").css({'display':'none'});
				}else{
					$("#discussionStatsReportDiv").css({'display':'inline'});
				}
			});
			
			$("#goDiscussionStatsReport").click(function(){
				if($("#discussionStatsReportDate").val()=='-'){
					$.prompt("Date not selected.");
				}else{
					showDiscussionStatistics();
					$("#discussionStatsReportDiv").css({'display': 'none'});
				}
			});
			
			$("#generateVivranReport").click(function(){
				generateVivranReport();
			});
			
			$("#memberMotionStatisticReport").click(function(){
				generateMemberMotionStatisticalReport();
			});
			
			$("#motion_order_of_the_day").click(function(){
				getSessionDates();
			});
		});
		
		function updateVisibilityForMemberMotionsViewLinks() {
			//reset visibility related flags and links
			$('#member_motions_view_status_flag').val("");
			$('#member_admitted_motions_view_flag').val("");
			$('#member_admitted_motions_view_span').hide();
			$('#member_rejected_motions_view_flag').val("");
			$('#member_rejected_motions_view_span').hide();
			
			params = "houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val();
			
			$.get('ref/loadVisibilityFlagsForMemberMotionsView?' + params, function(data) {
				if (data.length > 0) {
					var text = "";
					for ( var i = 0; i < data.length; i++) {
						text += "<option value='"+data[i].id+"'>"
								+ data[i].name + "</option>";
						if(data[i].name=='member_motions_view_status_flag') {
							if(data[i].value=='status_visible') {
								$('#member_motions_view_status_flag').val('status_visible');
							}
							
						} else if(data[i].name=='member_admitted_motions_view_flag') {
							if(data[i].value=='admitted_visible') {
								$('#member_admitted_motions_view_flag').val('admitted_visible');
								$('#member_admitted_motions_view_span').show();
							}
							
						} else if(data[i].name=='member_rejected_motions_view_flag') {
							if(data[i].value=='rejected_visible') {
								$('#member_rejected_motions_view_flag').val('rejected_visible');
								$('#member_rejected_motions_view_span').show();
							}
							
						}
					}
				}
			}).fail(function() {
				//reset visibility related flags and links
				$('#member_motions_view_status_flag').val("");
				$('#member_admitted_motions_view_flag').val("");
				$('#member_admitted_motions_view_span').hide();
				$('#member_rejected_motions_view_flag').val("");
				$('#member_rejected_motions_view_span').hide();
				
				if ($("#ErrorMsg").val() != '') {
					$("#error_p").html($("#ErrorMsg").val()).css({
						'color' : 'red',
						'display' : 'block'
					});
				} else {
					$("#error_p").html("Error occured contact for support.").css({
						'color' : 'red',
						'display' : 'block'
					});
				}
				scrollTop();
			});
		}
		
		function loadSession(){
			$.get("ref/sessionbyhousetype/" + $("#selectedHouseType").val()
				+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),
				function(data){
					if(data){
						$("#loadedSession").val(data.id);
						loadMembers();
						loadParties();
						loadActiveMinistry();
						showJodPatraDate(data.id);
					}
				});
		}
		
		function showCurrentStatusReport(val, qId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"
			                                         +$("#selectedMotionType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', 
					"motion/report/currentstatusreport?device="+ device +"&reportType="+val+"&moId="+qId);
		}
		
		function showIntimationLetter(id){
			
			
					$("#intimationletter").attr('href',
							'motion/report/fopgenreport?motionId=' + id 
									+ '&locale=' + $("#moduleLocale").val()
									+ '&outputFormat=' + $("#defaultReportFormat").val()
									+ '&reportQuery=MOTION_INTIMATION_LETTER'
									+ '&templateName=motion_intimation_report'
									+ '&houseType=' + $("#selectedHouseType").val()
									+ '&reportName=intimationLetter');
				
		}
		
		function reminderLetterReport() {
			if($("#reminderLetterFilter").val()=='preview') { //for reminder letter report only
				generateReminderLetter(false);
			} else if($("#reminderLetterFilter").val()=='generate') { //for reminder letter generation to be saved as entry
				$.prompt("Do you really want to send reminder letter to department now?",{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						generateReminderLetter(true);
	    	        }
				}});			
			}
		}
		
		function GeneralStatisticsReport() { //includes all submitted motions
			var parameters = "motionType=" + $("#selectedMotionType").val()
			+ "&locale="+$("#moduleLocale").val()
			+ "&report=CALLING_ATTENTION_MOTION_GENERAL_STATISTICS_REPORT"
			+ "&reportout=motionGeneralStatisticsReport";
		
			var urlSession = "ref/sessionbyhousetype/"
				+ $("#selectedHouseType").val() + "/" 
				+ $("#selectedSessionYear").val() + "/"
				+ $("#selectedSessionType").val();
			$.get(urlSession,function(data){
					if(data){
						parameters += '&sessionId=' + data.id;
						showTabByIdAndUrl('details_tab','motion/report/motion/genreport?'+parameters);
						
					}
				});
		}
		
		
		/**** To Generate Reminder Letter ****/
		function generateReminderLetter(isRequiredToSend) {
			//var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedMotionType").val() + "']").text();
			if($("#selectedSubDepartment").val()==undefined 
					|| $("#selectedSubDepartment").val()=='' 
					|| $("#selectedSubDepartment").val()=='0') {
				$.prompt('Please select a department for reminder letter of concerned motions!');
				return false;
			}
			var selectedMotionIds = '';
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get('ref/device/findDevicesForReminderOfReply?'
					+ 'houseType=' + $('#selectedHouseType').val()
					+ '&sessionYear=' + $('#selectedSessionYear').val()
					+ '&sessionType=' + $('#selectedSessionType').val()
					+ '&deviceType=' + $('#selectedMotionType').val()
					+ '&department=' + $('#selectedSubDepartment').val(),function(data){
				$.unblockUI();
				selectedMotionIds = data;
			}).done(function(){
				if(selectedMotionIds!=undefined && selectedMotionIds.length>=1) {
					var outputFormat = 'WORD';
					if(isRequiredToSend==false) {
						outputFormat = 'PDF';
					}
					if($('#currentusergroupType').val()=='department' || $('#currentusergroupType').val()=='department_deskofficer') {
						outputFormat = 'PDF';
					}
					form_submit(
							'motion/report/generateReminderLetter', 
							{
								motionIds: selectedMotionIds,
								houseType: $('#selectedHouseType').val(),  
								//sessionYear: $('#selectedSessionYear').val(),  
								//sessionType: $('#selectedSessionType').val(), 
								usergroupType: $("#currentusergroupType").val(),
								locale: $('#moduleLocale').val(), 
								reportQuery: 'MOIS_REMINDER_LETTER', 
								outputFormat: outputFormat,
								isDepartmentLogin: $("#isDepartmentLogin").val(),
								isRequiredToSend: isRequiredToSend
							}, 
							'GET'
					);
				} else {
					$.prompt('No calling attention motions found to be reminded for reply currently!');
					return false;
				}					
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		
		function showAdvanceStatusReport(qId){
			$("#selectionDiv1").hide();
			var reportQuery = "MOTION_ADVANCE_REPORT";
			if($("#selectedHouseType").val()=='lowerhouse'){
				reportQuery+="_LOWERHOUSE";
			}else{
				reportQuery+="_UPPERHOUSE";
			}
			showTabByIdAndUrl('details_tab', 
					"motion/report/motion/advancereport?report="+reportQuery+"&reportout=advanestatusreport&inParam="+qId);
		}
		
		function loadMembers(){
			memberArray = [];
			$.get('ref/alleligiblemembers?session='+$("#loadedSession").val(), function(data){
				if(data.length>0){
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					for(var i = 0; i < data.length; i++){
						memberArray.push(data[i].name);
						text+="<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#members").empty();
					$("#members").html(text);
				}
			});
		}
  

		function loadActiveMinistry(){
		
			
			memberArray = [];
			$.get('ref/ministry/'+$('#selectedHouseType').val()+'/'+$('#selectedSessionYear').val()+'/'+$('#selectedSessionType').val(), function(data){
				if(data.length>0){
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					for(var i = 0; i < data.length; i++){
						memberArray.push(data[i].name);
						text+="<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#ActiveMinistries").empty();
					$("#ActiveMinistries").html(text);
				}
			});
		}
		
		
		
		function loadParties(){
			memberArray = [];
			$.get('ref/allparties/'+$("#loadedSession").val(), function(data){
				if(data.length>0){
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					for(var i = 0; i < data.length; i++){
						memberArray.push(data[i].name);
						text+="<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#parties").empty();
					$("#parties").html(text);
				}
			});
		}
		
		/**** displaying grid ****/					
		function showMotionList() {
				showTabByIdAndUrl('list_tab','motion/list?houseType='+$('#selectedHouseType').val()
						+'&motionType='+$("#selectedMotionType").val()+'&sessionYear='
						+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+
						"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$('#srole').val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						+"&subDepartment="+$("#selectedSubDepartment").val()
						+"&clubbingStatus=" + $("#selectedClubbingStatus").val()
						);
				
				loadSession();
		}
		
		function determineOrderingForSubmission() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&motionType=" + $("#selectedMotionType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+ "&locale="+$("#moduleLocale").val();
			showTabByIdAndUrl('details_tab','motion/determine_ordering_for_submission?'+parameters);
		}
		
		function memberMotionsView(displayContent) { //includes all submitted motions
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&motionType=" + $("#selectedMotionType").val()
			+ "&statusFilter=all"
			+ "&createdBy=" + $("#ugparam").val()
			+ "&memberId=" + $("#loggedInMemberId").val()
			+ "&displayContent=" + displayContent 
			+ "&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_MOTIONS_VIEW"
			+ "&reportout=member_motions_view";
			showTabByIdAndUrl('details_tab','motion/report/motion/genreport?'+parameters);
		}
		
		function memberMotionsViewForStatus(status_filter) { //includes submitted motions having given status (admitted / rejected)
			var viewMode = "";
			if(status_filter=='rejected' && $('#member_motions_view_status_flag').val()=='status_visible') { //in case to show statuses for lowerhouse
				viewMode = "_with_status";
			} else if(status_filter=='admitted' && $('#member_admitted_motions_view_flag').val()=='admitted_visible') { //in case to show statuses for lowerhouse
				viewMode = "_with_status";
			}
			//$.get('/ref/status_visibility_for_member_in_session?')
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&motionType=" + $("#selectedMotionType").val()
			+ "&statusFilter=" + status_filter
			+ "&createdBy=" + $("#ugparam").val()
			+ "&memberId=" + $("#loggedInMemberId").val()
			+ "&displayContent=subject"
			+ "&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_MOTIONS_VIEW"
			+ "&reportout=member_motions_view"+viewMode;
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			showTabByIdAndUrl('details_tab','motion/report/motion/genreport?'+parameters);
			setTimeout(function(){
				$.unblockUI();
			},2000);
		}
		
		/**** new motion ****/
		function newMotion() {
			$("#new_record").show();
			$("#cancelFn").val("newMotion");
			//since id of motion has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','motion/new?'+(($("#gridURLParams").val()==undefined)? $("#gridURLParams_ForNew").val():$("#gridURLParams").val()));
		}
		/**** edit motion ****/		
		function editMotion(row) {
			$("#cancelFn").val("editMotion");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','motion/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'motion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete motion ****/	
		function deleteMotion() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				deviceNumber = $("#grid").jqGrid ('getCell', row, 'number');
			    if(deviceNumber!='-') {
			    	$.prompt($('#submittedParliamentaryDevicesCannotBeDeletedPrompt').val());
					return;
			    } else {
					$.prompt($('#confirmDeleteMessage').val()+ row,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
					        $.delete_('motion/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					        	showMotionList();
					        }).fail(function(){
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
								}
								scrollTop();
							});
				        }
					}});			    	
			    }
			}
		}
		function updateDecisionForMotions() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&deviceType=" + $("#selectedMotionType").val()
			+ "&locale="+$("#moduleLocale").val();
			
			showTabByIdAndUrl('details_tab','poster_activities/update_decision/init?'+parameters);
		}
		/**** reload grid ****/
		function reloadMotionGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+
						"&motionType="+$("#selectedMotionType").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						+"&subDepartment="+$("#selectedSubDepartment").val()
						+"&clubbingStatus=" + $("#selectedClubbingStatus").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				
				if(oldURL!=undefined){
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");
				}else{
					showMotionList();
				}
				
				loadSession();
		}
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'ballot/motion/init?' + parameters +"&deviceType="+$("#selectedMotionType").val();
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'motion/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}	
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='motion/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}	
		
		function showDiscussionSelection(){
			var parameters = "houseType="+$("#selectedHouseType").val()
			 +"&sessionYear="+$("#selectedSessionYear").val()
			 +"&sessionType="+$("#selectedSessionType").val()
			 +"&motionType="+$("#selectedMotionType").val()
			 +"&ugparam="+$("#ugparam").val()
			 +"&status="+$("#selectedStatus").val()
			 +"&role="+$("#srole").val()
			 +"&usergroup="+$("#currentusergroup").val()
			 +"&usergroupType="+$("#currentusergroupType").val();
				
			 var resource = 'motion/discussionselection';
			 var resourceURL = resource+"?"+parameters;	
			 $("#selectionDiv").hide();
			 $("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', resourceURL);
		}
		
		function showJodPatraDate(session){
			$.get('ref/sessiondates/' + session,function(data){
				if(data){
					var text = "<option value='-'>" + $("#pleaseSelect").val() + "</option>";
					for(var i = 0; i < data.length; i++){
						text+="<option value='" + data[i].value + "'>" + data[i].name +"</option>";
					}
					$("#jodPatraDate").empty();
					$("#jodPatraDate").html(text);
					
					$("#discStatusDate").empty();
					$("#discStatusDate").html(text);
					
					$("#selMotionDate").empty();
					$("#selMotionDate").html(text);
					//$("#jodPatraDiv").css({'display':'inline'});
				}
			});	
		}
		
		function loadSubDepartmentsFromGroup(group, init){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$.get(url, function(data){
				$.get('ref/getDepartment?session=' + data.id + '&group=0&userGroup='+$('#currentusergroup').val()
						+'&deviceType='+$("#selectedMotionType").val()+'&houseType='+$("#selectedHouseType").val()
						+'&usergroupType='+$('#currentusergroupType').val(),function(data){
					
					var subDepartmentText="<option value='0'>---"+$("#pleaseSelect").val()+"---</option>";
					$('#selectedSubDepartment').empty();
					if(data.length>0){
						for(var i=0;i<data.length;i++){
							subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
							
						}
						$("#selectedSubDepartment").html(subDepartmentText);
					}
				}).done(function(){
					if(init=='no'){
						reloadMotionGrid();
					}else if(init=='yes'){
						showMotionList();
					}
				});
			});
		}
		
		function showJodPatra(){
			var houseType = $("#selectedHouseType").val().trim().toUpperCase();
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$("#selectionDiv").hide();
			$("#selectionDiv1").hide();
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab','motion/report/motion/jodpatra?sessionId='+data.id+"&deviceTypeId="+$("#selectedMotionType").val()+"&statusId=" + $("#selectedStatus").val() + "&locale="+$('#moduleLocale').val()+"&report=MOTION_JODPATRA_REPORT_"+houseType+"&reportout=motionjodpatra&discussionDate="+$("#jodPatraDate").val());
				}
			});
		}
		
		function showDiscussionStatus(){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$("#selectionDiv").hide();
			$("#selectionDiv1").hide();
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab','motion/report/motion/genreport?sessionId='+data.id + "&locale="+$('#moduleLocale').val()+"&report=MOTION_DISCUSSION_STATUS_REPORT&reportout=motiondiscussionstatusreport&discussionDate="+$("#discStatusDate").val()+"&houseType="+$("#selectedHouseType").val());
				}
			});
		}
		
		function showSelMotion(){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$("#selectionDiv").hide();
			$("#selectionDiv1").hide();
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab','motion/report/motion/genreport?sessionId='+data.id + "&locale="+$('#moduleLocale').val()+"&report=MOTION_DATE_SELECTED&reportout=selmotionreport&discussionDate="+$("#selMotionDate").val()+"&houseType="+$("#selectedHouseType").val());
				}
			});
		}
		
		/* function showCountMotion(){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&submissionDate="+$("#onlineOfflineCountReportDate").val()
							+"&locale="+$("#moduleLocale").val()
							+"&report=MOIS_ONLINE_OFFLINE_COUNT&reportout=onoffcountreport");
				}
			});
		} */
		
		function generateOnlineOfflineSubmissionCountReport(){
			var parameters = "houseType=" + $("#selectedHouseType").val()
			 + "&sessionYear=" + $("#selectedSessionYear").val()
			 + "&sessionType=" + $("#selectedSessionType").val()
			 + "&deviceType=" + $("#selectedMotionType").val()		 
			 + "&role=" + $("#srole").val();		 	
			var resourceURL = 'motion/report/online_offline_submission_count_report/init?'+ parameters;
			$.get(resourceURL,function(data) {
				$.fancybox.open(data,{autoSize:false,width:400,height:200});
			},'html').fail(function(){				
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").
					css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		
		function formJodPatra(){
			var houseType = $("#selectedHouseType").val().trim().toUpperCase();
			$("#formJodPatra").attr('href','motion/report/motion/jodpatraformation?sessionId='+$("#loadedSession").val()+"&deviceTypeId="+$("#selectedMotionType").val()+"&statusId=" + $("#selectedStatus").val() + "&locale="+$('#moduleLocale').val()+"&report=MOTION_JODPATRA_REPORT_" + houseType +"&reportout=motionjodpatra&discussionDate="+$("#jodPatraDate").val()+"&reportFormat=" + $("#defaultReportFormat").val()+"&currUser="+$("#ugparam").val());			
		}
		
		function formDiscussionStatus(){
			$("#formDiscStatus").attr('href','motion/report/motion/discussionstatusformation?sessionId='+$("#loadedSession").val()+ "&locale="+$('#moduleLocale').val()+"&report=MOTION_DISCUSSION_STATUS_REPORT&reportout=discussionstatus&discussionDate="+$("#discStatusDate").val()+"&reportFormat=" + $("#defaultReportFormat").val()+"&houseType="+$("#selectedHouseType").val());			
		}
		
		function showAdmissionReport(id){
			$("#admission_report").attr('href','motion/report/commonadmissionreport?motionId=' + id + '&locale=' + $("#moduleLocale").val() + '&outputFormat=' + $("#defaultReportFormat").val());
		}
		
		function showPreIntimationAdmissionReport(id){
			$("#preAdmissionIntimationReport").attr('href','motion/report/commonadmissionreport?motionId=' + id + '&locale=' + $("#moduleLocale").val() + '&outputFormat=' + $("#defaultReportFormat").val() + '&isAdvanceCopy=yes&copyType=advanceCopy');
		}
		
		/**** Bulk statusupdate(Assistant)****/
		function statusUpdate() {
			var parameters =  "houseType=" + $("#selectedHouseType").val()
						+ "&sessionYear=" + $("#selectedSessionYear").val()
						+ "&sessionType=" + $("#selectedSessionType").val()
						+ "&motionType=" + $("#selectedMotionType").val()
						+ "&ugparam=" + $("#ugparam").val() 
						+ "&status=" + $("#selectedStatus").val() 
						+ "&role=" + $("#srole").val()
						+ "&usergroup=" + $("#currentusergroup").val()
						+ "&usergroupType=" + $("#currentusergroupType").val()
			

			var resourceURL = 'motion/statusupdate/assistant/init?' + parameters;
			showTabByIdAndUrl('statusupdate_tab', resourceURL);
		}
		
		function statusWiseReport(){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&statusId="+selectedStatus
							+"&statusType="+statusType
							+"&locale="+$("#moduleLocale").val()
							+"&report=MOIS_STATUS_BASED_REPORT&reportout=motionStatusReport");
				}
			});
		}
		
		
		function memberWiseReport(memberId){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					var report = 'MOIS_MEMBER_WISE_REPORT';
					/* <security:authorize access="(hasAnyRole('MOIS_CHAIRMAN'))">
					var report = 'MOIS_MEMBER_WISE_REPORT';
					</security:authorize> */
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&memberId="+memberId 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report="+report
							+"&role="+$('#srole').val()
							+"&reportout=motionMemberReport");
				}
			});
		}
		
		function departmentWiseReport(dept){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&subd="+dept 
							+"&statusId="+$("#selectedStatus").val()
							+"&ClubStatus="+$("#selectedClubbingStatus").val()
							+"&locale="+$("#moduleLocale").val()
							+"&report=MOIS_DEPARTMENT_WISE_REPORT&reportout=motionDepartmentReport");
				}
			});
		}
		
		function partyWiseReport(party){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					
					showTabByIdAndUrl("details_tab","motion/report/selectPartyWisePage?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&partyId="+party 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+$("#selectedStatus").val()
							//+"&report=MOIS_PARTY_WISE_REPORT&reportout=motionPartyReport"
							);
					
					/*showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&partyId="+party 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+$("#selectedStatus").val()
							+"&report=MOIS_PARTY_WISE_REPORT&reportout=motionPartyReport"
							);*/
				}
			});
		}
		/* Edited by SHubham A */
		function ActiveMinistryReport(ActiveMinistry){
			 var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			var statusCheck = $("#selectedStatus").val()
			
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&housetype="+$("#selectedHouseType").val()
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&ActiveMinistries="+ActiveMinistry 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+$("#selectedStatus").val()
							+"&ClubStatus="+$("#selectedClubbingStatus").val()
							+"&report=MOIS_MINISTRY_REPORT&reportout=motionMinistryReport");
				}
			}); 
		}
		
		//Set Submission Time Window 
		function setsessionTimeWindow() {
			
			
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&motionType="+$("#selectedMotionType").val()
				//+"&adjourningDate="+$("#selectedAdjourningDate").val()
				//+"&formattedAdjourningDate="+$("#selectedAdjourningDate").text()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val();
				var resourceURL='motion/submissionwindow?'+parameters;
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:360,height:270});
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
					
		}
		
		/**** -------------------- ****/
		
		function  motionDiscussionReport(){
			 var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
				+ "/" + $("#selectedSessionYear").val()
				+ "/" + $("#selectedSessionType").val();
			 $.get(url,function(data){
					if(data){
						
						showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
								+"sessionId="+data.id
								+"&statusId="+$("#selectedStatus").val()
								+"&locale="+$("#moduleLocale").val()							
								+"&report=MOIS_DISCUSSION_DATE_REPORT&reportout=motionDiscussionReport");
					}
				}); 
		}
		/* ------ */
		
		function registerReport(){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&locale="+$("#moduleLocale").val()
							+"&report=MOIS_ENTRY_REGISTER&reportout=registerReport");
				}
			});
		}		
		
		
		/**** Search Facility ****/
		function searchInt(id){
			//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="searchfacility=yes&usergroup="+$("#currentusergroup").val()
				        +"&usergroupType="+$("#currentusergroupType").val()+
				        "&houseType="+$("#selectedHouseType").val()+
				        "&sessionType="+$("#selectedSessionType").val()+
				        "&sessionYear="+$("#selectedSessionYear").val()+
				        "&deviceType="+$("#selectedMotionType").val();		
			showTabByIdAndUrl('search_tab','devicesearch/init?'+params);
		}
		
		function showDiscussionStatistics(){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","motion/report/motion/genreport?"
							+"sessionId="+data.id
							+"&discussionDate="+$("#discussionStatsReportDate").val()
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&locale="+$("#moduleLocale").val()
							+"&report=MOIS_DISCUSSION_STATISTICS_DATEWISE&reportout=motionDiscussionStatsReport");
				}
			});
		}
		
		
		function generateVivranReport(){
			$("#generateVivranReport").attr('href','motion/report/motion/vivranreport?sessionId='+$("#loadedSession").val()
					+"&deviceTypeId="+$("#selectedMotionType").val()
					+"&statusId=" + $("#selectedStatus").val() 
					+"&locale="+$('#moduleLocale').val()+"&report=MOIS_VIVRAN_REPORT" 
					+"&reportout=vivranreport&discussionDate="+$("#jodPatraDate").val()
					+"&reportFormat=" + $("#defaultReportFormat").val()
					+"&currUser="+$("#ugparam").val());			
		}
		
		function generateMemberMotionStatisticalReport(){
			showTabByIdAndUrl("details_tab","motion/report/motion/genreport?sessionId="+$("#loadedSession").val()
					+"&locale="+$('#moduleLocale').val()+"&report=MOIS_MEMBERWISE_MOTION_STATISTICAL_REPORT" 
					+"&reportout=memberMotionStatisticReport");			
		}
		
		function getSessionDates(){
			/* showTabByIdAndUrl("details_tab","motion/report/sessiondates/orderoftheday?sessionId="+$("#loadedSession").val());			
			 */
			 $.get("motion/report/sessiondates/orderoftheday?sessionId="+$("#loadedSession").val()+"&ugparam="+$("#ugparam").val(),function(data){
				$.fancybox.open(data);
			 }).fail(function(){
    			if($("#ErrorMsg").val()!=''){
    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
    			}else{
    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    			}
    			scrollTop();
    		});
		}
	</script>
</head>
<body>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details">
				   </spring:message>
				</a>
			</li>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT', 'MOIS_UNDER_SECRETARY',
				'MOIS_DEPUTY_SECRETARY', 'MOIS_PRINCIPAL_SECRETARY', 'MOIS_SPEAKER', 'MOIS_JOINT_SECRETARY',
				'MOIS_SECRETARY', 'MOIS_OFFICER_ON_SPECIAL_DUTY', 'MOIS_DEPUTY_SPEAKER', 'MOIS_CHAIRMAN',
				'MOIS_DEPUTY_CHAIRMAN', 'MOIS_SECTION_OFFICER', 'MOIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<c:if test="${houseType=='lowerhouse'}">
					<li>
						<a id="ballot_tab" href="#" class="tab">
					   		<spring:message code="motion.memberballot" text="Ballot"></spring:message>
						</a>
					</li>			
					</c:if>				
			</security:authorize>
			
			<security:authorize
				access="hasAnyRole('MOIS_SECTION_OFFICER', 'MOIS_ASSISTANT','MOIS_CLERK')">
				<li><a id="statusupdate_tab" href="#" class="tab"> <spring:message
							code="generic.motion.statusupdate" text="Update Motion">
						</spring:message>
				</a></li>
			</security:authorize>
			
			<security:authorize
				access="!hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">
				<li><a id="search_tab" href="#" class="tab"> <spring:message
							code="generic.motion.search" text="Search">
						</spring:message>
				</a></li>
			</security:authorize>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
				<c:forEach items="${houseTypes}" var="i">
					<c:choose>
						<c:when test="${houseType==i.type}">
							<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> |	
							
			<a href="#" id="select_session_year" class="butSim">
				<spring:message code="question.sessionyear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:100px;height: 25px;">				
				<c:forEach var="i" items="${years}">
					<c:choose>
						<c:when test="${i==sessionYear }">
							<option value="${i}" selected="selected"><c:out value="${i}"></c:out></option>				
						</c:when>
						<c:otherwise>
							<option value="${i}" ><c:out value="${i}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach> 
			</select> |	
								
			<a href="#" id="select_sessionType" class="butSim">
				<spring:message code="question.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">				
				<c:forEach items="${sessionTypes}" var="i">
					<c:choose>
						<c:when test="${sessionType==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>			
						</c:otherwise>
					</c:choose>			
				</c:forEach> 
			</select> |	
						
			<a href="#" id="select_motionType" class="butSim">
				<spring:message code="question.questionType" text="Motion Type"/>
			</a>
			<select name="selectedMotionType" id="selectedMotionType" style="width:100px;height: 25px;">			
				<c:forEach items="${motionTypes}" var="i">
					<c:choose>
						<c:when test="${motionType==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
				<c:forEach items="${motionTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>			
			</select>|		
			
			
			<security:authorize access="!(hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST'))">
				<hr>					
				<a href="#" id="select_status" class="butSim"> <spring:message
						code="question.status" text="Status" />
				</a>
				<select name="selectedStatus" id="selectedStatus"
					style="width: 250px; height: 25px;">
					<option value="0" selected="selected">--<spring:message code="please.select" text="Please Select"/>--</option>
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select>
				<select id="statusMaster" style="display: none;">
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.type}"></c:out>
						</option>
					</c:forEach>
				</select>|	
				
				
				<div id='motionDepartment' style="display:inline;">
					<a href="#" id="select_department" class="butSim"> <spring:message
							code="question.department" text="Group" />
					</a>
					<select name="selectedSubDepartment" id="selectedSubDepartment"
						style="width: 250px; height: 25px;">
						<option value="0" selected="selected"><spring:message code="please.select"></spring:message></option>
						<c:forEach items="${subDepartments}" var="i">
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:forEach>
					</select>|
				</div>	 
			</security:authorize>
			
			<security:authorize access="hasAnyRole('MOIS_SECTION_OFFICER')">
			<a href="#" id="session_time_window" class="butSim">
				<spring:message code="session.session_time_window" text="Session Time Window"/>
			</a> |
			</security:authorize>	
			
			
			<security:authorize
				access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">
				<a href="#" id="select_status" class="butSim"> <spring:message
						code="question.status" text="Status" />
				</a>
				<select name="selectedStatus" id="selectedStatus"
					style="width: 100px; height: 25px;">
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select>
			</security:authorize>
					
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">	
			<hr>		
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="motion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
			<!-- <option value="30">30</option>
			<option value="25">25</option>
			<option value="20">20</option> -->
			<!-- <option value="15">15</option> -->
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			</security:authorize>	
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
				<hr>			
				<a href="#" id="select_itemcount" class="butSim">
					<spring:message code="motion.itemcount" text="No. of Motions(Bulk Putup)"/>
				</a>
				<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
					<option value="100">100</option>
					<option value="75">75</option>
					<option value="50">50</option>
					<option value="25">25</option>
					<option value="10">10</option>
					<option value="5">05</option>		
				</select>|	
				<a href="javascript:void(0);" id="select_filecount" class="butSim">
					<spring:message code="motion.filecount" text="Select File(Bulk Putup)"/>
				</a>
				<select name="selectedFileCount" id="selectedFileCount" style="width:100px;height: 25px;">			
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
					<c:if test="${highestFileNo>0 }">
						<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
							<option value="${i}">${i}</option>
						</c:forEach>
					</c:if>						
				</select>
			</security:authorize>
			
			<security:authorize	access="hasAnyRole('MOIS_CLERK', 'MOIS_ASSISTANT', 'MOIS_DEPUTY_SECRETARY' ,'MOIS_PRINCIPAL_SECRETARY', 'MOIS_SPEAKER', 'MOIS_CHAIRMAN')">
				<a href="#" id="select_clubbingStatus" class="butSim"> 
					<spring:message	code="generic.clubbingStatus" text="Clubbing Status"/>
				</a>
				<select name="selectedClubbingStatus" id="selectedClubbingStatus" style="height: 25px;">
					<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
					<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
					<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
				</select>
			</security:authorize>
			
			<security:authorize	access="!hasAnyRole('MOIS_CLERK', 'MOIS_ASSISTANT')">
				<select hidden="true" name="selectedClubbingStatus" id="selectedClubbingStatus" style="height: 25px;">
					<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
					<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
					<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
				</select>
			</security:authorize>	
			
				<security:authorize access="hasAnyRole('MOIS_CLERK','MOIS_ASSISTANT','MOIS_SECTION_OFFICER','MOIS_SPEAKER','MOIS_CHAIRMAN')">
					<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
						<spring:message code="motion.reports" text="Reports"/>
					</a>
					<div id="assistantReportDiv" style="display: none; border: 1px solid green; border-radius: 6px; margin: 10px 0px 10px 0px; padding: 5px;">
						<a href="javascript:void(0);" id="status_report" class="butSim">
							<spring:message code="motion.statusReport" text="Status-wise Report"/>
						</a>|
						
						<!-- <input type="text" id="forMemberId" value="" class="sText autosuggest" style="width: 100px;"/>
						<input type="hidden" id="memberId" value="" /> -->
						<a href="javascript:void(0);" id="member_report" class="butSim" >
							<spring:message code="generic.memberWiseReport" text="Member-wise Report"/>
						</a>						
						<select id="members" class="sSelect" style="display: inline; width:100px;">
						</select>|
						
						<a href="javascript:void(0);" id="department_report" class="butSim" >
							<spring:message code="generic.departmentWiseReport" text="Department-wise Report"/>
						</a>|
						
					
						<a href="javascript:void(0);" id="party_report" class="butSim" >
							<spring:message code="generic.partyWiseReport" text="Party-wise Report"/>
						</a>						
						<select id="parties" class="sSelect" style="display: inline; width:100px;">
						</select>|
						
						<br>
						<!-- Edited By Shubham A  -->
						<a href="javascript:void(0);" id="ministry_report" class="butSim" >
							<spring:message code="generic.MinistryWiseReport" text="Ministry-wise Report"/>
						</a>						
						<select id="ActiveMinistries" class="sSelect" style="display: inline; width:100px;">
					</select>|
						<a href="javascript:void(0);" id="motion_discussion_report" class="butSim" >
							<spring:message code="motion.discussionReport" text="Discussion Motion Report"/>
						</a>|
						<a href="javascript:void(0);" id="reminderLetterReport" class="butSim" >
							<spring:message code="generic.reminder_letter_report" text="Reminder Letter Report"/>
						</a>
						<div id="reminderLetterFilterDiv" style="display: inline;">
							<select id="reminderLetterFilter" class="sSelect" style="display: inline; width:100px;">
								<option value="preview"><spring:message code="reminder_letter.preview" text="Preview"/></option>
								<option value="generate"><spring:message code="reminder_letter.generate" text="Send to Department"/></option>
							</select>
							<div id="goRLRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
						</div>|
						<a href="javascript:void(0);" id="motion_general_statistics_report" class="butSim" >
							<spring:message code="motion.GeneralStatisticsReport" text=" Motion General Statistics Report"/>
						</a>|
						<!--  -->
						<hr>
						<a href="javascript:void(0);" id="entry_register" class="butSim" >
							<spring:message code="generic.register" text="Register"/>
						</a>|
						
						<c:if test="${houseType=='upperhouse'}">
							<a href="javascript:void(0);" id="preAdmissionIntimationReport" class="butSim" >
								<spring:message code="generic.preAdmissionIntimationLetter" text="Pre Admission Intimation Letter"/>
							</a>|
						</c:if>
						
						<a href="javascript:void(0);" id="admission_report" class="butSim" >
							<c:choose>
								<c:when test="${houseType=='lowerhouse'}">
									<spring:message code="generic.admissionLetter" text="Admission Letter"/>
								</c:when>
								<c:when test="${houseType=='upperhouse'}">
									<spring:message code="generic.nivedanTarikh" text="Nivedan Tarikh"/>
								</c:when>
							</c:choose>
						</a>
						
						<div style="display: inline;">
							<a href="javascript:void(0);" id="jodPatra" class="butSim">
								<spring:message code="motion.jodpatra" text="Remained Motions"/>
							</a> 
							<div style="display: none; width: 200px;" id="jodPatraDiv">
								<select id="jodPatraDate" class="sSelect">
									<option value="-"><spring:message code='please.select' text="Please Select"></spring:message></option>
								</select> <a class="butSim" href="javascript:void(0);" id="goJodPatra"><spring:message code="jodpatra.go" text="Go" /></a>
								&nbsp;
								<a href="javascript:void(0);" id="formJodPatra" style="margin: 0px 0px 0px -10px;">
									<img width="20px" height="20px" src="./resources/images/word_icon.png"  title="<spring:message code='motion.jodpatra.formation' text='Jod Patra'/>" />
								</a>						
							</div>
						</div>|
						<a href="javascript:void(0);" id="generateCurrentStatusReport" class="butSim">
							<spring:message code="motion.generateCurrentStatusReport" text="Current Status Report"/>
						</a> |
						<a href="javascript:void(0);" id="advanceStatusReport" class="butSim">
							<spring:message code="motion.advanceStatusReport" text="Bulk Report"/>
						</a> |
						<a href="javascript:void(0);" id="generateVivranReport" class="butSim">
							<spring:message code="motion.generateVivranReport" text="Vivran Report"/>
						</a> |
						<a href="javascript:void(0);" id="memberMotionStatisticReport" class="butSim">
							<spring:message code="motion.memberMotionStatisticReport" text="Member Motion Statistical Report"/>
						</a>|
						<hr>
						<div style="display: inline;">
							<a href="javascript:void(0);" id="motionDiscStatus" class="butSim">
								<spring:message code="motion.chartreport" text="Discussion Status"/>
							</a> 
							<div style="display: none; width: 200px;" id="discussionStatusDiv">
								<select id="discStatusDate" class="sSelect">
									<option value="-"><spring:message code='please.select' text="Please Select"></spring:message></option>
								</select> <a class="butSim" href="javascript:void(0);" id="goDiscStatus"><spring:message code="godistatus.go" text="Go" /></a>
								&nbsp;
								<a href="javascript:void(0);" id="formDiscStatus" style="margin: 0px 0px 0px -10px;">
									<img width="20px" height="20px" src="./resources/images/word_icon.png"  title="<spring:message code='motion.discstatus.formation' text='Discussion Status'/>" />
								</a>						
							</div>
						</div>|		
						<div style="display: inline;">
							<a href="javascript:void(0);" id="selMotion" class="butSim">
								<spring:message code="motion.date.discuss" text="Selected Motions"/>
							</a> 
							<div style="display: none; width: 200px;" id="selMotionDiv">
								<select id="selMotionDate" class="sSelect">
									<option value="-"><spring:message code='please.select' text="Please Select"></spring:message></option>
								</select> <a class="butSim" href="javascript:void(0);" id="goSelMotion"><spring:message code="goselmotion.go" text="Go" /></a>
								<!-- &nbsp;
								<a href="javascript:void(0);" id="formSelMotion" style="margin: 0px 0px 0px -10px;">
									<img width="20px" height="20px" src="./resources/images/word_icon.png"  title="<spring:message code='motion.selmotion.formation' text='Dated Motions'/>" />
								</a>-->						
							</div>
						</div>|
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
						<a href="#" id="online_offline_submission_count_report" class="butSim link">
							<spring:message code="smois.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
						</a> |
						<a href="javascript:void(0);" id="intimationletter" class="butSim">
							<spring:message code="motion.intimationletter" text="Intimation Letter"/>
						</a>
						<div style="display: inline;">
							|<a href="javascript:void(0);" id="discussionStatsReport" class="butSim">
								<spring:message code="motion.discussionStats" text="Discussion Statistics"/>
							</a> 
							<div style="display: none; width: 200px;" id="discussionStatsReportDiv">
								<input id="discussionStatsReportDate" class="sText datemask"/> <a class="butSim" href="javascript:void(0);" id="goDiscussionStatsReport"><spring:message code="goselmotion.go" text="Go" /></a>
							</div>
						</div>|	
						
					</div>	
				</security:authorize>
				<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
				    <c:if test="${houseType=='lowerhouse'}">
				         <a href="#" id="motion_order_of_the_day" class="butSim link">
							<spring:message code="mois.order_of_the_day" text="Order of the Day Notices"/>
						</a> |
				     </c:if>
				</security:authorize>
				<security:authorize access="hasAnyRole('MOIS_UNDER_SECRETARY', 'MOIS_DEPUTY_SECRETARY', 'MOIS_SECRETARY', 'MOIS_PRINCIPAL_SECRETARY')">
					
					<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
						<spring:message code="motion.reports" text="Reports"/>
					</a>
					<div id="assistantReportDiv" style="display: none; border: 1px solid green; border-radius: 6px; margin: 10px 0px 10px 0px; padding: 5px;">
						<a href="javascript:void(0);" id="member_report" class="butSim" >
							<spring:message code="generic.memberWiseReport" text="Member-wise Report"/>
						</a>						
						<select id="members" class="sSelect" style="display: inline; width:100px;">
						</select>|
						<a href="javascript:void(0);" id="department_report" class="butSim" >
							<spring:message code="generic.departmentWiseReport" text="Department-wise Report"/>
						</a>|
						<a href="javascript:void(0);" id="party_report" class="butSim" >
							<spring:message code="generic.partyWiseReport" text="Party-wise Report"/>
						</a>						
						<select id="parties" class="sSelect" style="display: inline; width:100px;">
						</select>|<br>
						<hr>
					</div>
				</security:authorize>						
			<hr>							
		</div>		
		
		<div class="tabContent">
		</div>
		
		<!-- <select id="members" >
			
		</select> -->
		<input type="hidden" id="key" name="key">				
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${motionTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		<input type="hidden" id="usersAllowedForDepartmentFilter" value="${usersAllowedForDepartmentFilter}" />		
		<input type="hidden" id="member_motions_view_status_flag" value="${member_motions_view_status_flag}" />
		<input type="hidden" id="member_admitted_motions_view_flag" value="${member_admitted_motions_view_flag}" />
		<input type="hidden" id="member_rejected_motions_view_flag" value="${member_rejected_motions_view_flag}" />
		</div> 	
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />	
		<input type="hidden" id="loadedSession" value="" />
		<input type="hidden" id="defaultReportFormat" value="<spring:message code='motion.report.defaultFormat' text='PDF' />" />
</body>
</html>