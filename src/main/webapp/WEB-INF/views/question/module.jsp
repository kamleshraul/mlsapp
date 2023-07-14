<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.list"
		text="List Of Questions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	var processMode = $('#processMode').val();
	$(document).ready(function() {
		
		//Shubham A
		$('#TestReport').click(function(){
			TestRevisionReport();
			});	
		
		$('#statuswise_questionsCount_report').click(function(){
			
			 statuswiseQuestionCountReport(); 
			
			});	
					
		
		
		//blink notice message per second interval
		var f = document.getElementById('noticeMessageDiv');
		if(f!=null && f!=undefined) {
			setInterval(function() {
		        f.style.visibility = (f.style.visibility == 'hidden' ? '' : 'hidden');
		    }, 500);
		}			    
	    
		if($("#selectedGroup").val()!='' && $("#selectedGroup").val()!=null){
			loadChartAnsweringDateByGroup($("#selectedGroup").val());
		}
		
		/**** On Page Load ****/
		var currentDeviceType = $("#currentDeviceType").val();
		var currentHouseType = $("#currentHouseType").val();
		
		if (currentDeviceType == 'questions_halfhourdiscussion_from_question') {
			$('#bulkputup_tab').hide();
		}
		
		if(currentDeviceType == 'questions_unstarred' || currentDeviceType == 'questions_starred') {
			if(currentDeviceType == 'questions_unstarred'){
				$('#originalDeviceTypeSpan').show();
			}
			$('#answerReceivedStatusSpan').show();
		} else {
			$('#originalDeviceTypeSpan').hide();
			$('#answerReceivedStatusSpan').hide();
		}
	
		/**** Fro chart_tab to show or hide ****/
		if (currentDeviceType == 'questions_starred') {
			$("#chart_tab").show();
		} else {
			$("#chart_tab").hide();
		}
		
		$('#bulktimeout_tab').hide();

		/**** For ballot or member ballot tab to be visible ****/
		if (currentDeviceType == 'questions_starred'
				&& $('#processMode').val()  == 'upperhouse') {
			$("#memberballot_tab").show();
			$("#ballot_tab").hide();
		} else if ((currentDeviceType == 'questions_starred' && $('#processMode').val()  == 'lowerhouse')
				|| currentDeviceType == 'questions_halfhourdiscussion_from_question') {
			$("#memberballot_tab").hide();
			$("#ballot_tab").show();
		} else {
			$("#memberballot_tab").hide();
			$("#ballot_tab").hide();
		}	
		
		/**** For yaadi details tab to be visible ****/		
		if($("#currentusergroupType").val()=='clerk' || $("#currentusergroupType").val()=='assistant') {
			if(currentDeviceType == 'questions_unstarred') {
				$("#yaadi_details_tab").show();
			} else {
				$("#yaadi_details_tab").hide();
			}
		}	
		if(currentDeviceType == 'questions_starred') {
			if(($("#currentusergroupType").val()=='section_officer')
					|| $("#currentusergroupType").val()=='deputy_secretary'
					|| $("#currentusergroupType").val()=='joint_secretary'
					|| $("#currentusergroupType").val()=='joint_secretary2'
					|| $("#currentusergroupType").val()=='principal_secretary'
							
			) {
				//console.log("inside func");
				$("#yaadi_details_tab").show();
			} 
			else if(($("#currentusergroupType").val()=='clerk' && $("#generalClerkDisplay").val() !='YES') && $("#selectedHouseType").val() == 'lowerhouse')
			{  
			$("#yaadi_details_tab").show();
			}
			else if($("#selectedHouseType").val() == 'upperhouse'
				&& $("#currentusergroupType").val()=='under_secretary'){
				$("#yaadi_details_tab").show();
			} else {
				$("#yaadi_details_tab").hide();
			}
		} else {
			$("#yaadi_details_tab").show();
		}
		
		$('#selectedStatus option').each(function() {
			var selectedStatusType = $("#filterStatusMaster option[value='"+ $(this).attr('value') + "']").text();			
			//console.log("selectedStatusType: " + selectedStatusType);
		    if(!$(this).is(':last-child') && selectedStatusType.indexOf("processed_lapsed") != -1) {
		    	$(this).insertAfter($('#selectedStatus option:last-child'));	
		    	var topSelectedOption = $('#selectedStatus option:first-child');
		    	//console.log("selectedStatus Value: " + $(topSelectedOption).attr('value'));
				$('#selectedStatus').val($(topSelectedOption).attr('value'));
				//return false;
		    }
		});	
		
		/*Tooltip*/
		$(".toolTip").hide();
		/**** here we are trying to add date mask in grid search when field names ends with date ****/
		$(".sf .field").change(function() {
			var field = $(this).val();
			if (field.indexOf("Date") != -1) {
				$(".sf .data").mask("99/99/9999");
			}
		});
		/**** displaying grid ****/
		$('#list_tab').click(function() {
			$("#selectionDiv1").show();
			showQuestionList();
		});
		/**** house type changes then reload grid****/
		$("#selectedHouseType").change(function() {
			var value = $(this).val();
			if (value != "") {
				if($("#currentusergroupType").val()=='member') {
					updateVisibilityForMemberQuestionsViewLinks();
				}
				if($("#currentusergroupType").val()!='member' && $("#currentusergroupType").val()!='typist'){
					loadGroupsFromSessions();
				}
				loadProcessingMode();
				if($("#selectedGroup").val()!=''){
					loadChartAnsweringDateByGroup($("#selectedGroup").val());
				}
			}
			loadSession();
			reloadQuestionGrid();
		});
		/**** session year changes then reload grid****/
		$("#selectedSessionYear").change(function() {
			var value = $(this).val();
			/* $('#questionDepartment').hide();
			$('#subDepartment').val(""); */
			if (value != "") {
				if($("#currentusergroupType").val()=='member') {
					updateVisibilityForMemberQuestionsViewLinks();
				}
				if($("#currentusergroupType").val()!='member' && $("#currentusergroupType").val()!='typist'){
					loadGroupsFromSessions();
				}
				loadProcessingMode();
			}
			loadSession();
			reloadQuestionGrid();
		});
		/**** session type changes then reload grid****/
		$("#selectedSessionType").change(function() {
			var value = $(this).val();
			/* $('#questionDepartment').hide();
			$('#subDepartment').val(""); */
			if (value != "") {
				if($("#currentusergroupType").val()=='member') {
					updateVisibilityForMemberQuestionsViewLinks();
				}
				if($("#currentusergroupType").val()!='member' && $("#currentusergroupType").val()!='typist'){
					loadGroupsFromSessions();
				}
				loadProcessingMode();
			}
			
			if($("#selectedGroup").val()!='' && $("#selectedGroup").val()!=null){
				loadChartAnsweringDateByGroup($("#selectedGroup").val());
			}
			loadSession();
			reloadQuestionGrid();
		});
		/**** question type changes then reload grid****/
		$("#selectedQuestionType").change(function() {
			var value = $(this).val();
			var text = $("#deviceTypeMaster option[value='"+ value + "']").text();
			loadStatusByDeviceType();
			if(text == 'questions_unstarred') {
				$('#originalDeviceTypeSpan').show();
				loadOriginalDeviceTypesForGivenDeviceType(value);
				$('#selectedAnswerReceivedStatus').val("-");
				$('#answerReceivedStatusSpan').show();
			} else {
				$('#originalDeviceTypeSpan').hide();
				$("#selectedOriginalDeviceType").empty();
				var originalDeviceTypeText = "<option value='0' selected='selected'>---"+$("#pleaseSelect").val()+"---</option>";
				$("#selectedOriginalDeviceType").html(originalDeviceTypeText);
				$('#selectedAnswerReceivedStatus').val("-");
				$('#answerReceivedStatusSpan').hide();
			}
			if(text.indexOf("questions_halfhourdiscussion_")==-1){
				$("#hdReportsDiv").hide();
			}else{
				$("#hdReportsDiv").show();
			}
			
			if (text == 'questions_halfhourdiscussion_from_question') {
				$('#bulkputup_tab').hide();
			} else {
				$('#bulkputup_tab').show();
			}
			
			if (text == 'questions_starred') {
				$("#chart_tab").show();
				$("#currentDeviceType").val("questions_starred");
			} else {
				$("#chart_tab").hide();
			}
			
			if (text == 'questions_starred' && $('#processMode').val() == 'upperhouse') {
				$("#memberballot_tab").show();
				$("#ballot_tab").hide();
			} else if ((text == 'questions_starred' && $('#processMode').val()  == 'lowerhouse')
					|| text == 'questions_halfhourdiscussion_from_question'
					|| text == 'questions_halfhourdiscussion_standalone') {
				$("#memberballot_tab").hide();
				$("#ballot_tab").show();
			} else {
				$("#memberballot_tab").hide();
				$("#ballot_tab").hide();
			}
			
			/**** For yaadi details tab to be visible ****/						
			if($("#currentusergroupType").val()=='clerk' || $("#currentusergroupType").val()=='assistant') {
				if(text == 'questions_unstarred') {
					$("#yaadi_details_tab").show();
				} else {
					$("#yaadi_details_tab").hide();
				}
			}
			if(text == 'questions_starred') {
				if(($("#currentusergroupType").val()=='section_officer')
						|| $("#currentusergroupType").val()=='deputy_secretary'
						|| $("#currentusergroupType").val()=='joint_secretary2'
						|| $("#currentusergroupType").val()=='principal_secretary'
						) {
					$("#yaadi_details_tab").show();
				}
				else if(($("#currentusergroupType").val()=='clerk' && $("#generalClerkDisplay").val() !='YES') && $("#selectedHouseType").val() == 'lowerhouse')
					{  
					$("#yaadi_details_tab").show();
					}
				else if($("#selectedHouseType").val() == 'upperhouse'
						&& $("#currentusergroupType").val()=='under_secretary'){
					$("#yaadi_details_tab").show();
				} else {
					$("#yaadi_details_tab").hide();
				}
			} else {
				$("#yaadi_details_tab").show();
			}
			
			if(text == 'questions_starred' || text == 'questions_unstarred') {
				$("#admitted_departmentwise_report_span").show();
			} else {
				$("#admitted_departmentwise_report_span").hide();
			}
			
			/**** show/hide member_starred_suchi_view span as per selected devicetype ****/
			if($('#currentusergroupType').val()=='member' && text == 'questions_stared') {
				$('#member_starred_suchi_view_span').show();
				$('#suchiAnsweringDate').css('display', 'inline-block'); 
				populateSessionAnsweringDatesForMemberSuchiView();				
			} else {
				$('#suchiAnsweringDate').css('display', 'none'); 
				$("#member_starred_suchi_view_span").hide();
			}
			
			/**** show/hide determine_ordering_for_submission as per selected devicetype ****/
			if($('#currentusergroupType').val()=='member' && text == 'questions_starred') {
				$('#determine_ordering_for_submission_span').show();				
			} else {
				$('#determine_ordering_for_submission_span').hide();
			}
			
			/**** show/hide memberballotchoice as per selected devicetype ****/
			if($('#currentusergroupType').val()=='member' 
					&& text == 'questions_starred'
					&& $('#processMode').val()  == 'upperhouse') {
				$("#memberballotchoice_span").show();
			} else {
				$("#memberballotchoice_span").hide();
			}
			
			if (value != "") {
				reloadQuestionGrid();
			}
		});
		/**** original question type changes then reload grid****/
		$("#selectedOriginalDeviceType").change(function() {
			reloadQuestionGrid();
		});
		/**** answer received status changes then reload grid****/
		$("#selectedAnswerReceivedStatus").change(function() {
		
			showConvertStarredQsn();
			reloadQuestionGrid();
		});
		/**** status changes then reload grid****/
		$("#selectedStatus").change(function() {
			showConvertStarredQsn();
			$('#selectedAnswerReceivedStatus').val("-");
			var value = $(this).val();
			if (value != "") {
				var deviceTypeType = $("#deviceTypeMaster option[value='"+ $('#selectedQuestionType').val() + "']").text();
				var filterStatusType = $("#filterStatusMaster option[value='"+ value + "']").text();
				if(deviceTypeType == 'questions_starred'
						&& (filterStatusType=='question_final_clarificationNeededFromDepartment'
							|| filterStatusType=='question_final_clarificationNeededFromMember'
							|| filterStatusType=='question_final_clarificationNeededFromMemberAndDepartment')
				) {
					$('#bulktimeout_tab').show();
				} else {
					$('#bulktimeout_tab').hide();
				}
				
				//reloadQuestionGrid();
				showQuestionList();
			} else {
				$('#bulktimeout_tab').hide();
			}
			$("#generateIntimationLetter").attr("href","");
			
		});
		
		
		$('#selectedModuleAsweringDate').change(function(){
			
			showConvertStarredQsn();
			reloadQuestionGrid();
			
		});
		
       
		
		/**** group changes then reload grid ****/
		$("#selectedGroup").change(function() {
			var value = $(this).val();
			if (value != "") {
				$("#ugparam").val(value);
				loadSubDepartmentsFromGroup(value, 'no');	
				loadChartAnsweringDateByGroup(value);
				//loadChartAnsweringDateByGroup($("#selectedGroup").val());
			}
		});		
		
		/**** clubbing status changes then reload grid ****/
		$("#selectedClubbingStatus").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadQuestionGrid();
				showConvertStarredQsn();
			}		
			
		});
		
		/**** Chart Tab ****/
		$('#chart_tab').click(function() {
			$("#selectionDiv1").hide();
			viewChart();
		});
		
		/**** Ballot Tab ****/
		$('#ballot_tab').click(function() {
			$("#selectionDiv1").hide();
			viewBallot();
		});
		
		/**** Rotation Order Tab ****/
		$('#rotationorder_tab').click(function() {
			$("#selectionDiv1").hide();
			viewRotationOrder();
		});
		
		/**** Member Ballot Tab ****/
		$('#memberballot_tab').click(function() {
			$("#selectionDiv1").hide();
			viewMemberBallot();
		});
		
		/**** Bulk Putup ****/
		$("#bulkputup_tab").click(function() {
			$("#selectionDiv1").hide();
			bulkPutup();
		});
		
		/**** Bulk Putup ****/
		$("#bulkputupassistant_tab").click(function() {
			$("#selectionDiv1").hide();
			bulkPutupAssistant();
		});
		
		/**** Bulk Timeout ****/
		$("#bulktimeout_tab").click(function() {
			$("#selectionDiv1").hide();
			bulkTimeout();
		});
		
		/**** Yaadi Details Tab ****/
		$("#yaadi_details_tab").click(function() {
			$("#selectionDiv1").hide();
			yaadiDetails();
		});
		
		/**** Yaadi Update ****/
		$("#yaadiupdate_tab").click(function(event, isHighSecurityValidationRequired) {
			//isHighSecurityValidationRequired = false;
			if(isHighSecurityValidationRequired!=false) {
				validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
				return false;
			}
			$("#selectionDiv1").hide();
			yaadiUpdate();
		});	
		
		/**** Search Tab ****/
		$('#search_tab').click(function() {
			$("#selectionDiv1").hide();
			searchInt();
		});
		
		/**** status changes then reload grid****/
		$("#selectedSubDepartment").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadQuestionGrid();
			}
		});
		
		/**** show question list method is called by default.****/
		showQuestionList();
		/**** Toggle Reports Div ****/
		$("#reports_link").click(function(e){
			$("#assistantReportDiv").toggle("slow");
		});
		
		if($("#allowedGroups").val()!=''){
			loadSubDepartmentsFromGroup($("#selectedGroup").val(),'yes');
			loadChartAnsweringDateByGroup($("#selectedGroup").val());
		}
		
		$("#generateMemberDraftReport").click(function(){
			var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedQuestionId.length<1) {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else if(selectedQuestionId.length>1) {
				$.prompt("Please select only one question!");
				return false;
			} else {			
				showMemberDraftReport(selectedQuestionId);
			}
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
		
		/**** Generate Partywise Questions Count Report ****/
		$("#partywise_questions_count_report").click(function(){
			$("#selectionDiv1").hide();
			generatePartywiseQuestionsCountReport();
		});
		
		/**** Generate Extended Grid Report ****/
		$("#extended_grid_report").click(function(){
			$("#selectionDiv1").hide();
			generateExtendedGridReport();
		});
		
		/**** Statistical Counts Report ****/
		$("#statistical_counts_report").click(function(){
			$("#selectionDiv1").hide();
			generateStatisticalCountsReport();
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
		
		/**** Questions Groupwise Counts Report for BAC ****/
		$("#groupwise_counts_report_for_bac").click(function(){				
			$(this).attr('href','#');
			groupwiseCountsReportForBAC();
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
		
		$("#showVivranReport").click(function(){
			$("#selectionDiv1").hide();
			showVivranReport();
		});
		
		/**** Generate Member's Admitted Questions Report ****/
		$("#memberwise_admitted_questions_report").click(function(){
			$("#selectionDiv1").hide();
			memberwiseAdmittedQuestionsReport();
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
		
		$("#showHDBallotChoiceOptionReport").click(function(){
			$("#selectionDiv1").hide();
			showBallotChoiceOptionReport();
		});
		
		
		
		/* Shubham */
		$("#QuestionNumberRange").hide();
		
		$("#QuestionNumberRangeDivViewer").click(function(){
			loadQuestionNumberRange()
	    		$("#QuestionNumberRange").toggle();
	    	});
		
		var selectedDeviceType = $("#deviceTypeMaster option[value='" 
		                                                     + $("#selectedQuestionType").val() + "']").text();
		if(selectedDeviceType.indexOf("questions_halfhourdiscussion_")==-1){
			$("#hdReportsDiv").hide();
		}else{
			$("#hdReportsDiv").show();
		}
	});
	
	function showCurrentStatusReport(val, qId){
		$("#selectionDiv1").hide();
		var device = $("#deviceTypeMaster option[value='"
		                                         +$("#selectedQuestionType").val()+"']").text().split("_")[0];
		showTabByIdAndUrl('details_tab', 
				"question/report/currentstatusreport?device="+ device +"&reportType="+val+"&qId="+qId);
	}
	
	function showMemberDraftReport(qId){
		$("#selectionDiv1").hide();
		var device = $("#deviceTypeMaster option[value='"
		                                         +$("#selectedQuestionType").val()+"']").text().split("_")[0];
		showTabByIdAndUrl('details_tab', 
				"question/report/memberdraftreport?device="+device+"&qId="+qId);
	}
	
	function loadProcessingMode(){
		params = "houseType=" + $('#selectedHouseType').val()
		+ '&sessionYear=' + $("#selectedSessionYear").val()
		+ '&sessionType=' + $("#selectedSessionType").val();
		$.get("ref/processingMode?"+params,function(data){
			if(data!=''){
				$('#processMode').val(data);
			}
		}).success(function(){
			/* if ($('#processMode').val()  == 'upperhouse') {
				$('#memberballot_tab').show();					
			} else {
				$('#memberballot_tab').hide();					
			} */
			var selectedDeviceType = $("#deviceTypeMaster option[value='"+ $("#selectedQuestionType").val() + "']").text();
			if (selectedDeviceType == 'questions_starred'
					&& $('#processMode').val()  == 'upperhouse') {
				$("#memberballot_tab").show();
				$("#ballot_tab").hide();				
			} else if ((selectedDeviceType == 'questions_starred' && $('#processMode').val()  == 'lowerhouse')
					|| selectedDeviceType == 'questions_halfhourdiscussion_from_question') {
				$("#memberballot_tab").hide();
				$("#ballot_tab").show();
			} else {
				$("#memberballot_tab").hide();
				$("#ballot_tab").hide();
			}
			/**** show/hide memberballotchoice as per selected devicetype ****/
			if($('#currentusergroupType').val()=='member' 
					&& selectedDeviceType == 'questions_starred'
					&& $('#processMode').val()  == 'upperhouse') {
				$("#memberballotchoice_span").show();
			} else {
				$("#memberballotchoice_span").hide();
			}
		});
	}
	
	/*function getProcessMode() {
		processMode = $('#processMode').val();
		console.log(pMode);
		return pMode;
	}
	
	function setProcessMode(pMode) {
		$('#processMode').val(pMode);
	}*/
	
	function showConvertStarredQsn()
	{
		
		if($("#selectedModuleAsweringDate").val() != 0 && $("#selectedStatus").val() == 33 && $("#selectedAnswerReceivedStatus").val() == "answerReceived" && $("#selectedClubbingStatus").val() == "parent")
		{
		$("#convertStarredToUnStarred").show();
		}else{
			$("#convertStarredToUnStarred").hide();
		}
		
	}
	
	function showAdmissionReport(){
		params = "houseType=" + $('#selectedHouseType').val()
		+ '&sessionYear=' + $("#selectedSessionYear").val()
		+ '&sessionType=' + $("#selectedSessionType").val()
		+ '&deviceType=' + $("#selectedQuestionType").val()
		+ '&groupId=' + $("#selectedGroup").val()
		+ '&subDepartment=' + $("#selectedSubDepartment").val()
		+ '&answeringDate=' + $("#selectedModuleAsweringDate").val()
		+ '&clubbingStatus=' +$("#selectedClubbingStatus").val();
		
		
		showTabByIdAndUrl('details_tab', "question/report/admissionreport?"+ params);
	}
	
	function loadGroupsFromSessions() {
		if ($("#selectedGroup").length > 0) {
			params = "houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&allowedgroups=' + $("#allowedGroups").val();
			$.get(
					'ref/allowedgroups?' + params,
					function(data) {
						if (data.length > 0) {
							var text = "";
							for ( var i = 0; i < data.length; i++) {
								text += "<option value='"+data[i].id+"'>"
										+ data[i].name + "</option>";
							}
							$("#selectedGroup").empty();
							$("#selectedGroup").html(text);
							$("#ugparam").val(data[0].id);
							loadSubDepartmentsFromGroup(data[0].id, 'no');
						} else {
							$("#selectedGroup").empty();
						}
					}).fail(
					function() {
						if ($("#ErrorMsg").val() != '') {
							$("#error_p").html($("#ErrorMsg").val()).css({
								'color' : 'red',
								'display' : 'block'
							});
						} else {
							$("#error_p").html(
									"Error occured contact for support.").css({
								'color' : 'red',
								'display' : 'block'
							});
						}
						scrollTop();
					});
		}
		reloadQuestionGrid();
	}
	
	function updateVisibilityForMemberQuestionsViewLinks() {
		//reset visibility related flags and links
		$('#member_questions_view_status_flag').val("");
		$('#member_admitted_questions_view_flag').val("");
		$('#member_admitted_questions_view_span').hide();
		$('#member_rejected_questions_view_flag').val("");
		$('#member_rejected_questions_view_span').hide();
		$('#member_unstarred_questions_view_flag').val("");	
		$('#member_unstarred_questions_view_span').hide();
		
		params = "houseType=" + $('#selectedHouseType').val()
		+ '&sessionYear=' + $("#selectedSessionYear").val()
		+ '&sessionType=' + $("#selectedSessionType").val()
		$.get('ref/loadVisibilityFlagsForMemberQuestionsView?' + params, function(data) {
			if (data.length > 0) {
				var text = "";
				for ( var i = 0; i < data.length; i++) {
					text += "<option value='"+data[i].id+"'>"
							+ data[i].name + "</option>";
					if(data[i].name=='member_questions_view_status_flag') {
						if(data[i].value=='status_visible') {
							$('#member_questions_view_status_flag').val('status_visible');
						}
						
					} else if(data[i].name=='member_admitted_questions_view_flag') {
						if(data[i].value=='admitted_visible') {
							$('#member_admitted_questions_view_flag').val('admitted_visible');
							$('#member_admitted_questions_view_span').show();
						}
						
					} else if(data[i].name=='member_rejected_questions_view_flag') {
						if(data[i].value=='rejected_visible') {
							$('#member_rejected_questions_view_flag').val('rejected_visible');
							$('#member_rejected_questions_view_span').show();
						}
						
					} else if(data[i].name=='member_unstarred_questions_view_flag') {
						if(data[i].value=='unstarred_visible') {
							$('#member_unstarred_questions_view_flag').val('unstarred_visible');
							$('#member_unstarred_questions_view_span').show();
						}						
					}
				}
			}
		}).fail(function() {
			//reset visibility related flags and links
			$('#member_questions_view_status_flag').val("");
			$('#member_admitted_questions_view_flag').val("");
			$('#member_admitted_questions_view_span').hide();
			$('#member_rejected_questions_view_flag').val("");
			$('#member_rejected_questions_view_span').hide();
			$('#member_unstarred_questions_view_flag').val("");	
			$('#member_unstarred_questions_view_span').hide();
			
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
	
	/**** Load Session ****/
	function loadSession(){
		$.get("ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),
			function(data){
				if(data){
					$("#loadedSession").val(data.id);
					//console.log("loaded session id: " + $('#loadedSession').val());
					//loadMembers();
					//loadParties();
				}
			});
	}

	/**** displaying grid ****/
	function showQuestionList() {
		showTabByIdAndUrl('list_tab', 'question/list?houseType='+ $('#selectedHouseType').val()
				+ '&questionType='+ $("#selectedQuestionType").val()
				+ "&originalDeviceType=" + $("#selectedOriginalDeviceType").val()
				+ '&sessionYear='+ $("#selectedSessionYear").val()
				+ '&sessionType='+ $("#selectedSessionType").val()
				+ "&ugparam="+ $("#ugparam").val()
				+ "&status=" + $("#selectedStatus").val()
				+ "&clubbingStatus=" + $("#selectedClubbingStatus").val()
				+ "&answerReceivedStatus=" + $("#selectedAnswerReceivedStatus").val()
				+ "&role=" + $("#srole").val() + "&usergroup="
				+ $("#currentusergroup").val() + "&usergroupType="
				+ $("#currentusergroupType").val()+"&subdepartment="+(($("#selectedSubDepartment").val()==undefined)?'0':$("#selectedSubDepartment").val()));
		loadSession();
	}
	
	function determineOrderingForSubmission() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&createdBy=" + $("#ugparam").val()
		+ "&locale="+$("#moduleLocale").val();
		showTabByIdAndUrl('details_tab','question/determine_ordering_for_submission?'+parameters);
	}
	
	function fillMemberBallotChoices() {
		var parameters = "session=" + $("#loadedSession").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&role_filling_questionchoices=" + $("#srole").val()
		+ "&locale="+$("#moduleLocale").val();
		//showTabByIdAndUrl('details_tab','ballot/memberballot/listchoices?'+parameters, $('#questionChoiceSubmissionWindowClosedMsg').val());
		showTabByIdAndUrl('details_tab','ballot/memberballot/listchoices?'+parameters);
	}
	
	function memberQuestionsView(status_filter) {
		var viewMode = "";
		if(status_filter=='rejected' && $('#member_questions_view_status_flag').val()=='status_visible') { //in case to show statuses for lowerhouse
			viewMode = "_with_status";
		} else if(status_filter=='admitted' && $('#member_admitted_questions_view_flag').val()=='admitted_visible') { //in case to show statuses for lowerhouse
			viewMode = "_with_status";
		} else if(status_filter=='unstarred' /*&& $('#member_questions_view_status_flag').val()=='status_visible'*/) { //in case to show statuses for lowerhouse
			viewMode = "_with_status";
		}
		//$.get('/ref/status_visibility_for_member_in_session?')
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&statusFilter=" + status_filter
		+ "&createdBy=" + $("#ugparam").val()
		+"&locale="+$("#moduleLocale").val()
		+ "&report=MEMBER_QUESTIONS_VIEW"
		+ "&reportout=member_questions_view"+viewMode;
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		showTabByIdAndUrl('details_tab','question/report/generalreport?'+parameters);
		setTimeout(function(){
			$.unblockUI();
		},2000);
	}
	
	function memberQuestionsDetailView() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&createdBy=" + $("#ugparam").val()
		+"&locale="+$("#moduleLocale").val()
		+ "&report=MEMBER_QUESTIONS_DETAIL_VIEW"
		+ "&reportout=member_questions_detail_view";
		showTabByIdAndUrl('details_tab','question/report/generalreport?'+parameters);
	}
	
	function populateSessionAnsweringDatesForMemberSuchiView() {
		//populate answering dates for member suchi view
		if($('#suchiAnsweringDate > option').length == 1 && $('#suchiAnsweringDate').val()=='0') {			
			params = "houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val();
			$.get(
				'ref/answering_dates_for_member_suchi_view?' + params,
				function(data) {
					if (data.length > 0) {
						var currentDate = new Date().toJSON().split('T')[0];
						var text = "";
						for ( var i = 0; i < data.length; i++) {																		
							if(i==0) {
								if(data[i].value==currentDate) {
									text += "<option value='"+data[i].id+"' selected='selected'>"+ data[i].name + "</option>";
								} else {
									text += "<option value='"+data[i].id+"'>"+ data[i].name + "</option>";
								}
							} else {
								text += "<option value='"+data[i].id+"'>"+ data[i].name + "</option>";
							}
						}
						selectText = $("#suchiAnsweringDate").html();
						$("#suchiAnsweringDate").empty();								
						$("#suchiAnsweringDate").html(selectText+text);
					} else {
						selectText = $("#suchiAnsweringDate option[value='0']").html();
						$("#suchiAnsweringDate").empty();
						$("#suchiAnsweringDate").html(selectText);
					}
			}).fail(
				function() {
					if ($("#ErrorMsg").val() != '') {
						$("#error_p").html($("#ErrorMsg").val()).css({
							'color' : 'red',
							'display' : 'block'
						});
					} else {
						$("#error_p").html(
								"Error occured contact for support.").css({
							'color' : 'red',
							'display' : 'block'
						});
					}
					scrollTop();
			});
		}
	}
	
	function memberStarredSuchiView() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&answeringDate=" + $("#suchiAnsweringDate").val()
		+"&locale="+$("#moduleLocale").val();
		showTabByIdAndUrl('details_tab','question/report/member_starred_suchi_view?'+parameters);
	}

	/**** new question ****/
	function newQuestion() {
		$("#cancelFn").val("newQuestion");
		//since id of question has not been created so key is set to empty value
		$("#key").val("");
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="question/new?"+parameters;
		showTabByIdAndUrl('details_tab', resourceUrl);
	}
	
	function editQuestion(row) {
		$("#cancelFn").val("editQuestion");
		row = $('#key').val();
		if (row == null || row == '') {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&questionType=" + $("#selectedQuestionType").val()
			+ "&ugparam=" + $("#ugparam").val() 
			+ "&status=" + $("#selectedStatus").val()
			+ "&role=" + $("#srole").val()
			+ "&usergroup=" + $("#currentusergroup").val()
			+ "&usergroupType=" + $("#currentusergroupType").val();
			var resourceUrl="question/" + row + "/edit?"+parameters;
			showTabByIdAndUrl('details_tab',resourceUrl);
		}
	}
	function showDemo() {
		showTabByIdAndUrl('details_tab', 'test/showempsproject');
	}
	/**** double clicking record in grid handler ****/
	function rowDblClickHandler(rowid, iRow, iCol, e) {
		$("#cancelFn").val("rowDblClickHandler");
		$('#key').val(rowid);
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&ugparam=" + $("#ugparam").val() 
		+ "&status=" + $("#selectedStatus").val() 
		+ "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="question/" + row + "/edit?"+parameters;
		showTabByIdAndUrl('details_tab', resourceUrl);
	}
	/**** delete question ****/
	function deleteQuestion() {
		var row = $("#key").val();
		if (row == null || row == '') {
			$.prompt($('#selectRowFirstMessage').val());
			return;
		} else {
			deviceNumber = $("#grid").jqGrid ('getCell', row, 'number');
		    if(deviceNumber!='-') {
		    	$.prompt($('#submittedParliamentaryDevicesCannotBeDeletedPrompt').val());
				return;
		    } else {
				$.prompt($('#confirmDeleteMessage').val() + row,
						{buttons : {Ok : true,Cancel : false},
						callback : function(v) {
							if (v) {
								$.delete_('question/' + row+ '/delete',null,
										function(data,textStatus,XMLHttpRequest) {
									showQuestionList();
								}).fail(function() {
									if ($("#ErrorMsg").val() != '') {
										$("#error_p").html($("#ErrorMsg").val()).
										css({'color' : 'red','display' : 'block'});
									} else {
										$("#error_p").html("Error occured contact for support.").
										css({'color' : 'red','display' : 'block'});
									}
									scrollTop();
								});
							}
						}
				});		    	
		    }
		}
	}
	/**** reload grid ****/
	function reloadQuestionGrid() {
		$("#gridURLParams").val(
				"houseType=" + $("#selectedHouseType").val() 
				+ "&sessionYear=" + $("#selectedSessionYear").val() 
				+ "&sessionType=" + $("#selectedSessionType").val() 
				+ "&questionType=" + $("#selectedQuestionType").val()
				+ "&deviceType=" + $("#selectedQuestionType").val()
				+ "&originalDeviceType=" + $("#selectedOriginalDeviceType").val()
				+ "&ugparam=" + $("#ugparam").val() 
				+ "&status=" + $("#selectedStatus").val() 
				+ "&clubbingStatus=" + $("#selectedClubbingStatus").val()
				+ "&answerReceivedStatus=" + $("#selectedAnswerReceivedStatus").val()
				+ "&role=" + $("#srole").val() 
				+ "&usergroup=" + $("#currentusergroup").val() 
				+ "&usergroupType=" + $("#currentusergroupType").val()
				+"&subDepartment=" + (($("#selectedSubDepartment").val()==undefined)?'0':$("#selectedSubDepartment").val())
				+"&answeringDate="+$("#selectedModuleAsweringDate").val()
				);
		var oldURL = $("#grid").getGridParam("url");
		var baseURL = "";
		
		if(oldURL){
			baseURL = oldURL.split("?")[0];
		}
		newURL = baseURL + "?" + $("#gridURLParams").val();
		$("#grid").setGridParam({
			"url" : newURL
		});
		$("#grid").trigger("reloadGrid");
	}
	
	
	/*******-- Shubham   ---**************/
	function loadQuestionNumberRange(){
		
		$.get('ref/getQuestionNumberRange?'
				+ 'sessionId='+$('#loadedSession').val()
				+'&deviceType='+$('#currentDeviceType').val()
				
				,function(data) {
					$("#qsnRange").empty();
					var text = " ("+data.formattedOrder + - +data.formattedNumber +")";
					if(text != ' (undefinedNaN)'){
						$("#qsnRange").html(text);
					}
			
		})
	}

	/**** Chart Tab ****/
	function viewChart() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&answeringDate=" + $("#selectedModuleAsweringDate").val();
		}
		parameters = parameters + "&group=" + $("#selectedGroup").val();
		var resourceURL = 'chart/init?' + parameters;
		showTabByIdAndUrl('chart_tab', resourceURL);
	}
	/**** Ballot Tab ****/
	function viewBallot() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();
		}
		parameters = parameters + "&group=" + $("#selectedGroup").val()
				+ "&category=question";
		var resourceURL = 'ballot/init?' + parameters;
		showTabByIdAndUrl('ballot_tab', resourceURL);
	}
	/**** Rotation Order Tab ****/
	function viewRotationOrder() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();
		}
		parameters = parameters + "&group=" + $("#selectedGroup").val();
		var resourceURL = 'rotationorder/init?' + parameters;
		showTabByIdAndUrl('rotationorder_tab', resourceURL);
	}
	/**** Member Ballot Tab ****/
	function viewMemberBallot() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();
		}
		parameters += "&group=" + $("#selectedGroup").val() + "&round=1";
		var resourceURL = 'ballot/memberballot/init?' + parameters;
		showTabByIdAndUrl('memberballot_tab', resourceURL);
	}
	/**** Bulk putup(Member)****/
	function bulkPutup() {
		var parameters = $("#gridURLParams").val();
	
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&deviceType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();

		}
		var resourceURL = 'question/bulksubmission?' + parameters
				+ "&itemscount=" + $("#selectedItemsCount").val();
	
		showTabByIdAndUrl('bulkputup_tab', resourceURL);
	}
	/**** Bulk putup(Assistant)****/
	function bulkPutupAssistant() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&deviceType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&group=" + $("#ugparam").val()
					+ "&department="+$("#selectedSubDepartment").val();
		var resourceURL = 'question/bulksubmission/assistant/int?' + parameters
				+ "&itemscount=" + $("#selectedItemsCount").val();
		showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);
	}
	
	/**** Bulk Timeout ****/
	function bulkTimeout() {
		var selectedQuestionIds = $("#grid").jqGrid ('getGridParam', 'selarrrow');
		var parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&group=" + $("#ugparam").val()
					+ "&department="+$("#selectedSubDepartment").val()
					+ "&questionIds="+selectedQuestionIds;
		var resourceURL = 'question/bulktimeout/init?' + parameters
				+ "&itemscount=" + $("#selectedItemsCount").val();
		showTabByIdAndUrl('bulktimeout_tab', resourceURL);
	}
	
	/**** Yaadi Details ****/
	function yaadiDetails() {
		var parameters =  "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&deviceType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&group=" + $("#ugparam").val()
					+ "&answeringDate=" + $("#selectedModuleAsweringDate").val()
					+ "&category=question";
		

		var resourceURL = 'yaadi_details/init?' + parameters;
		showTabByIdAndUrl('yaadi_details_tab', resourceURL);
	}	
	
	/**** Bulk yaadiupdate(Assistant)****/
	function yaadiUpdate() {
		var parameters =  "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() 
					+ "&status=" + $("#selectedStatus").val() 
					+ "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&group=" + $("#ugparam").val();
		

		var resourceURL = 'question/yaaditodiscussupdate/assistant/init?' + parameters;
		showTabByIdAndUrl('yaadiupdate_tab', resourceURL);
	}
	/**** To Generate Intimation Letter ****/
	function generateIntimationLetter() {		 
		if($("#intimationLetterFilter").val()=='previewReminderReportForAnswer') { //for reminder letter report only
			generateReminderLetter(false);
		} else if($("#intimationLetterFilter").val()=='reminderToDepartmentForAnswer') { //for reminder letter generation to be saved as entry
			$.prompt("Do you really want to send reminder letter to department now?",{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					generateReminderLetter(true);
    	        }
			}});			
		} else {
			var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedQuestionId.length<1) {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else if(selectedQuestionId.length>1) {
				$.prompt("Please select only one question!");
				return false;
			} else {			
				$('#generateIntimationLetter').attr('href', 
						'question/report/generateIntimationLetter?'
								+'questionId=' + selectedQuestionId
								+ '&intimationLetterFilter=' + $("#intimationLetterFilter").val());
			}
		}		 		
	}
	/**** To Generate Reminder Letter ****/
	function generateReminderLetter(isRequiredToSend) {
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		if(devicetype.indexOf('questions_unstarred')==0){
			if($("#selectedSubDepartment").val()==undefined 
					|| $("#selectedSubDepartment").val()=='' 
					|| $("#selectedSubDepartment").val()=='0') {
				$.prompt('Please select a department for reminder letter of concerned unstarred questions!');
				return false;
			}
			var selectedQuestionIds = '';
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get('ref/device/findDevicesForReminderOfReply?'
					+ 'houseType=' + $('#selectedHouseType').val()
					+ '&sessionYear=' + $('#selectedSessionYear').val()
					+ '&sessionType=' + $('#selectedSessionType').val()
					+ '&deviceType=' + $('#selectedQuestionType').val()
					+ '&department=' + $('#selectedSubDepartment').val(),function(data){
				$.unblockUI();
				selectedQuestionIds = data;
			}).done(function(){
				if(selectedQuestionIds!=undefined && selectedQuestionIds.length>=1) {
					var outputFormat = 'WORD';
					if(isRequiredToSend==false) {
						outputFormat = 'PDF';
					}
					if($('#currentusergroupType').val()=='department' || $('#currentusergroupType').val()=='department_deskofficer') {
						outputFormat = 'PDF';
					}
					form_submit(
							'question/report/generateReminderLetter', 
							{
								questionIds: selectedQuestionIds,
								houseType: $('#selectedHouseType').val(),  
								//sessionYear: $('#selectedSessionYear').val(),  
								//sessionType: $('#selectedSessionType').val(), 
								usergroupType: $("#currentusergroupType").val(),
								locale: $('#moduleLocale').val(), 
								reportQuery: 'QIS_REMINDER_LETTER', 
								outputFormat: outputFormat,
								isDepartmentLogin: $("#isDepartmentLogin").val(),
								isRequiredToSend: isRequiredToSend
							}, 
							'GET'
					);
				} else {
					$.prompt('No unstarred questions found to be reminded for reply currently!');
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
		} else {
			var selectedQuestionIds = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedQuestionIds.length<1) {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else {
				//$('#generateIntimationLetter').attr('href', 'question/report/generateReminderLetter?'+'questionIds='+selectedQuestionIds);
				//console.log("selectedQuestionIds: " + selectedQuestionIds);
				form_submit('question/report/generateReminderLetter', {questionIds: selectedQuestionIds, houseType: $('#selectedHouseType').val(), locale: $('#moduleLocale').val(), reportQuery: 'QIS_REMINDER_LETTER', outputFormat: 'WORD', isDepartmentLogin: $("#isDepartmentLogin").val(), isRequiredToSend: isRequiredToSend}, 'GET');
			}
		}		
	}
	/**** To Generate Clubbed Intimation Letter ****/
	function generateClubbedIntimationLetter() {			
		var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
		if(selectedQuestionId.length<1) {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else if(selectedQuestionId.length>1) {
			$.prompt("Please select only one question!");
			return false;
		} else {	
			$.get('question/report/generateClubbedIntimationLetter/getClubbedQuestions?'
					+ 'questionId='+selectedQuestionId,function(data) {
				$.fancybox.open(data,{autoSize:false,width:400,height:300});
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
	}
	/**** To Generate Unstarred Yaadi Report ****/
	/* function generateUnstarredYaadiReport() {			
		var parameters = "houseType=" + $("#selectedHouseType").val()
					   + "&sessionYear=" + $("#selectedSessionYear").val()
					   + "&sessionType=" + $("#selectedSessionType").val()
					   + "&ugparam=" + $("#ugparam").val()
					   + "&role=" + $("#srole").val()
				 	   + "&usergroup=" + $("#currentusergroup").val()
					   + "&usergroupType=" + $("#currentusergroupType").val();
		$.get('question/report/generateUnstarredYaadiReport/getUnstarredYaadiNumberAndDate?'
				+ parameters, function(data) {
			$.fancybox.open(data,{autoSize:false,width:400,height:270});
		},'html').fail(function(){				
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});		
	} */
	function generateUnstarredYaadiReport() {	
		$("#selectionDiv1").hide();
		var parameters = "houseType=" + $("#selectedHouseType").val()
					   + "&sessionYear=" + $("#selectedSessionYear").val()
					   + "&sessionType=" + $("#selectedSessionType").val()
					   + '&deviceType=' + $("#selectedQuestionType").val()
					   + "&ugparam=" + $("#ugparam").val()
					   + "&role=" + $("#srole").val()
				 	   + "&usergroup=" + $("#currentusergroup").val()
					   + "&usergroupType=" + $("#currentusergroupType").val();
		showTabByIdAndUrl('details_tab', 'question/report/generateUnstarredYaadiReport/init?'+parameters);				
	}
	/**** To Generate Unstarred Suchi Report ****/
	function generateUnstarredSuchiReport() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		   + "&sessionYear=" + $("#selectedSessionYear").val()
		   + "&sessionType=" + $("#selectedSessionType").val()
		   + "&ugparam=" + $("#ugparam").val()
		   + "&role=" + $("#srole").val()
	 	   + "&usergroup=" + $("#currentusergroup").val()
		   + "&usergroupType=" + $("#currentusergroupType").val();
		$.get('question/report/generateUnstarredSuchiReport/getUnstarredYaadiNumberAndDateForSuchi?'
				+ parameters, function(data) {
			$.fancybox.open(data,{autoSize:false,width:400,height:270});
		},'html').fail(function(){				
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	function memberwiseQuestionsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()
		 + "&group=" + $("#selectedGroup").val()
		 + "&status=" + $("#selectedStatus").val()
		 + "&role=" + $("#srole").val() 
		 + "&answeringDate=" + $("#selectedModuleAsweringDate").val()
		 + "&category=question";	
		var resourceURL = 'question/report/memberwisequestions?'+ parameters;	
	 	showTabByIdAndUrl('details_tab', resourceURL);
	}
	function memberwiseAdmittedQuestionsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()
		 + "&group=" + $("#selectedGroup").val()
		 + "&status=" + $("#selectedStatus").val()
		 + "&role=" + $("#srole").val() 
		 + "&answeringDate=" + $("#selectedModuleAsweringDate").val()
		 + "&category=question";	
		var resourceURL = 'question/report/memberwise_admitted_questions?'+ parameters;
	 	showTabByIdAndUrl('details_tab', resourceURL);
	}	
	function generateOnlineOfflineSubmissionCountReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()		 
		 + "&role=" + $("#srole").val()
		 + "&session=" + $("#loadedSession").val();		 	
		var resourceURL = 'question/report/online_offline_submission_count_report/init?'+ parameters;
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
	
	function generateOnlineOfflineAnswersReceivedCountReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()		 
		 + "&role=" + $("#srole").val();		 	
		var resourceURL = 'question/report/online_offline_answered_count_report/init?'+ parameters;
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
	
	function generatePartywiseQuestionsCountReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()
		 + "&role=" + $("#srole").val(); 
		var resourceURL = 'question/report/partywise_questions_count_report/init?'+ parameters;
	 	showTabByIdAndUrl('details_tab', resourceURL);
	}
	
	function generateExtendedGridReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()
		 + "&role=" + $("#srole").val()
		 + "&usergroup=" + $("#currentusergroup").val()
		 + "&usergroupType=" + $("#currentusergroupType").val(); 
		var resourceURL = 'question/report/extended_grid_report/init?'+ parameters;
	 	showTabByIdAndUrl('details_tab', resourceURL);
	}
	
	function generateStatisticalCountsReport(){
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=" + $("#selectedQuestionType").val()
		 + "&role=" + $("#srole").val()
		 + "&usergroup=" + $("#currentusergroup").val()
		 + "&usergroupType=" + $("#currentusergroupType").val()
		 + "&reportQuery=" + devicetype.toUpperCase()+"_STATISTICAL_COUNTS_QUERY"
		 + "&reportFileName=qis_statistical_counts_report"
		 + "&locale=" + $("#moduleLocale").val();
		var resourceURL = 'question/report/statistical_counts_report?'+ parameters;
	 	showTabByIdAndUrl('details_tab', resourceURL);
	}
	
	function groupBulleteinReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&group=" + $("#selectedGroup").val();		 
		$('#group_bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	
	function bulleteinReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val();	
		$('#bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	
	function ahwalBulleteinReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&isAhwalBulletein=yes";	
		$('#bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	
	function ahwalStarredUnstarredReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val();
		$('#ahwal_starredUnstarred_report').attr('href',
				'question/report/ahwalStarredUnstarredReport?'+ parameters);
	}
	
	function ahwalShortNoticeStatsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val();
		$('#ahwal_shortnotice_stats_report').attr('href', 
				'question/report/ahwalShortNoticeStatsReport?'+ parameters);
	}
	
	function groupwiseCountsReportForBAC(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&group=" + $("#selectedGroup").val()
		 + "&is_BAC_PostBallot=no"; //modify this value as yes if BAC is post ballot
		$('#groupwise_counts_report_for_bac').attr('href', 'question/report/bac_groupwise_counts_report?'+ parameters);
	}
	
	function starredDepartmentwiseStatsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=starred";
		$('#starred_departmentwise_stats_report').attr('href',
				'question/report/departmentwiseStatsReport?'+ parameters);
	}
	
	function unstarredDepartmentwiseStatsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=unstarred";		 
		$('#unstarred_departmentwise_stats_report').attr('href',
				'question/report/departmentwiseStatsReport?'+ parameters);
	}
	
	/**** To Generate Unstarred Across Session Departmentwise Questions Report ****/
	function unstarredAcrossSessionDepartmentwiseQuestionsReport() {			
		var parameters = "houseType=" + $("#selectedHouseType").val()
					   + "&sessionYear=" + $("#selectedSessionYear").val()
					   + "&sessionType=" + $("#selectedSessionType").val()
					   + "&ugparam=" + $("#ugparam").val()
					   + "&role=" + $("#srole").val()
				 	   + "&usergroup=" + $("#currentusergroup").val()
					   + "&usergroupType=" + $("#currentusergroupType").val();
		$.get('question/report/unstarredacrosssessiondepartmentwise/sessionsinvolved?'
				+ parameters, function(data) {
			$.fancybox.open(data,{autoSize:false,width:400,height:270});
		},'html').fail(function(){				
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});		
	}
	
	function departmentwiseQuestionsReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&deviceType=" + $("#selectedQuestionType").val()
		 + "&subDepartment=" + $("#selectedSubDepartment").val()
		 + "&answeringDate=" //answering date can be selected later from dropdown for filtering result
		 + "&group=" + $("#selectedGroup").val()
		 + "&status=" + $("#selectedStatus").val()
		 + "&locale=" + $("#moduleLocale").val()
		 + "&role=" + $("#srole").val()
		 + "&report=" + "QIS_DEPARTMENTWISE_QUESTIONS_" + $("#selectedHouseType").val().toUpperCase()
		 + "&reportout=" + "departmentwisequestions";	
		var resourceURL = 'question/report/departmentwisequestions?'+ parameters;			
		showTabByIdAndUrl('details_tab', resourceURL);
	}
	
	function departmentwiseAdmittedQuestionsReport(){
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		var parameters = {
				houseType				: $("#selectedHouseType").val(),
				sessionYear				: $('#selectedSessionYear').val(), 
				sessionType				: $("#selectedSessionType").val(), 
				group					: $("#selectedGroup").val(),
				subDepartment			: $("#selectedSubDepartment").val(),
				originalDeviceType		: $("#selectedOriginalDeviceType").val(),
				deviceType				: $("#selectedQuestionType").val(),
				answerReceivedStatus	: $('#selectedAnswerReceivedStatus').val(), 
				locale					: $("#moduleLocale").val(), 
				role					: $("#srole").val(),
				reportQuery				: "QIS_ADMITTED_DEPARTMENTWISE_QUESTIONS"/* + "_" + $("#selectedHouseType").val().toUpperCase()*/,
				xsltFileName			: 'template_departmentwise_admitted_questions'/* + '_' + $("#selectedHouseType").val()*/,
				outputFormat			: 'WORD',
				reportFileName			: devicetype + "_departmentwise_admitted_questions"/* + "_" + $("#selectedHouseType").val()*/
		}
		form_submit('question/report/departmentwise_admitted_questions', parameters, 'GET');
	}
	
	function departmentwiseUnstarredAnsweredQuestionsReport() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&group=" + $("#selectedGroup").val()	
		 + "&subDepartment=" + $("#selectedSubDepartment").val()
		 + "&originalDeviceType=" + $("#selectedOriginalDeviceType").val()
		 + "&answerReceivedStatus=" + $("#selectedAnswerReceivedStatus").val()
		 + "&locale=" + $("#moduleLocale").val()
		 + "&role=" + $("#srole").val();		 	
		var resourceURL = 'question/report/departmentwise_unstarred_admitted_questions/init?'+ parameters;
		var fancyHeight = 200;
		if($("#selectedAnswerReceivedStatus").val()=='answerReceived') {
			fancyHeight = 250;
		}
		$.get(resourceURL,function(data) {
			$.fancybox.open(data,{autoSize:false,width:400,height:fancyHeight});
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
	
	function ahwalHDQConditionReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()	
		 + "&questionType=questions_halfhourdiscussion_from_question";
		$('#ahwal_hdq_condition_report').attr('href', 'question/report/ahwalHDConditionReport?'+ parameters);
	}
	
	function ahwalRulesSuspensionReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()	
		 + "&questionType=motions_rules_suspension";
		$('#ahwal_rules_suspension_report').attr('href', 'question/report/ahwalRulesSuspensionReport?'+ parameters);
	}
	
	function questionsUnstarredDepartmentwiseYaadi(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()	
		 + "&questionType=questions_unstarred";
		$('#questions_unstarred_departmentwise_yaadi_report').attr('href', 'question/report/questionUnstarredDepartmentwiseYaadi?'+ parameters);
	}
	
	function ahwalHDSConditionReport(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		 + "&sessionYear=" + $("#selectedSessionYear").val()
		 + "&sessionType=" + $("#selectedSessionType").val()
		 + "&questionType=questions_halfhourdiscussion_standalone";
		$('#ahwal_hds_condition_report').attr('href', 'question/report/ahwalHDConditionReport?'+ parameters);
	}
	
	function sankshiptAhwalReport() {
		showTabByIdAndUrl('details_tab', 'question/report/sankshiptAhwal?selectedHouseType='
				+$('#selectedHouseType').val());
	}
	
	function loadSubDepartmentsFromGroup(group, init){
		if($("#currentusergroupType").val()!='typist'){
			$.get('ref/getDepartment?group='+ group 
					+'&userGroup=' + $('#currentusergroup').val()
					+'&deviceType=' + $("#selectedQuestionType").val()
					+'&houseType=' + $("#selectedHouseType").val()
					+'&usergroupType='+ $('#currentusergroupType').val(),function(data){
				
				var subDepartmentText="<option value='0'>---"+$("#pleaseSelect").val()+"---</option>";
				$('#selectedSubDepartment').empty();
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
						
					}
					$("#selectedSubDepartment").html(subDepartmentText);
				}else{
					$("#selectedSubDepartment").html(subDepartmentText);
				}
			}).done(function(){
				if(init=='no'){
					reloadQuestionGrid();
				}else if(init=='yes'){
					showQuestionList();
				}
			});
		}else{
			if(init=='no'){
				reloadQuestionGrid();
			}else if(init=='yes'){
				showQuestionList();
			} 
		}
	}
	
	function statReport(){
		var url = "question/report/statreport?sessionYear=" + $("#selectedSessionYear").val()
				+ "&sessionType=" + $("#selectedSessionType").val()
				+ "&houseType=" + $("#selectedHouseType").val()
				+ "&deviceType=" + $("#selectedQuestionType").val()
				+ "&submissionBatch=" + $("#submissionBatchForStatReport").val();
		
			if($("#selectedHouseType").val() == 'lowerhouse'){
				var items = new Array();
				items.push('under_secretary');
				items.push('under_secretary_committee');
				items.push('principal_secretary');
				items.push('speaker');
				url += "&userGroups=" + items;
			}else if($("#selectedHouseType").val() == 'upperhouse'){
				var items = new Array();
				items.push('under_secretary');
				items.push('under_secretary_committee');
				items.push('deputy_secretary');
				items.push('secretary');
				items.push('principal_secretary');
				items.push('chairman');
				url += "&userGroups=" + items;
			}
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		showTabByIdAndUrl('details_tab', url);
		$.unblockUI();
	}
	
	function sendMessage() {		
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&deviceType=" + $("#selectedQuestionType").val()
		+ "&ugparam=" + $("#ugparam").val() 
		+ "&status=" + $("#selectedStatus").val() 
		+ "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&device=Question"
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="pushmessage/new?"+parameters;
		showTabByIdAndUrl('details_tab', resourceUrl);
	}
	
	function showHDDaywisereport(){
		var param = "sessionYear="+$("#selectedSessionYear").val()
		+ "&sessionType="+$("#selectedSessionType").val()
		+ "&houseType="+$("#selectedHouseType").val()
		+ "&deviceType="+$("#selectedQuestionType").val()
		+ "&subdate="+$("#hdDaysForReport").val()
		+ "&groupId=0&subDepartment=0";
		showTabByIdAndUrl('details_tab', 'question/report/halfhourdaysubmitreportdatefilter?'+ param);
	}
	
	//---------------------- #  Edited By Shubham A #-------------------------
	
	function TestRevisionReport(){
/* 		var param = "question/report/generalreport?sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&houseType=" + $("#selectedHouseType").val()
		+ "&deviceType=" + $("#selectedQuestionType").val()
	    + "&status=" + $("#selectedStatus").val() 
		+ "&groupId="+$("#selectedGroup").val()
		+ "&reportout=RevisionReport"
		+ "&report=LAST_REVISION_REPORT"
		+ "&locale=mr_IN";
		console.log(param);
	    showTabByIdAndUrl('details_tab',param) */
	    
		 var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","question/report/generalreport?"
							+"sessionId="+data.id
							+ "&deviceType=" + $("#selectedQuestionType").val()
						    + "&status=" + $("#selectedStatus").val() 
							+ "&groupId="+$("#selectedGroup").val()
							+ "&locale=mr_IN"
							+ "&reportout=RevisionReport"
							+ "&report=LAST_REVISION_REPORT");
				}
			}); 
	}
	
	//---------------------------------------------------------------------------------
	
	function showHDStatAndAdmissionreport(){
		var param = "sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&houseType=" + $("#selectedHouseType").val()
		+ "&deviceType=" + $("#selectedQuestionType").val()
		+ "&days=" + $("#hdDaysForReport").val()
		+ "&groupId=0&subDepartment=0";
		showTabByIdAndUrl('details_tab', 'question/report/hdstatandadmissionreport?'+ param);
	}
	
	function showHDGeneralreport(){
		var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
		+ "/" + $("#selectedSessionYear").val()
		+ "/" + $("#selectedSessionType").val();
		$.get(url,function(data){
			if(data){
				showTabByIdAndUrl('details_tab','question/report/generalreport?'
						+'sessionId='+data.id
						+"&houseType="+$("#selectedHouseType").val() 
						+"&deviceTypeId="+$("#selectedQuestionType").val()
						+"&locale="+$("#moduleLocale").val()
						+"&report=HD_CONDITION_REPORT&reportout=hdconditionreport");
			}
		});
	}
	
	function showBallotChoiceOptionReport(){
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		if(devicetype == 'questions_halfhourdiscussion_from_question'){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab',
							'question/report/generalreport?'
							+ "sessionId=" + data.id
							+ "&houseType="+$("#selectedHouseType").val() 
							+ "&deviceTypeId=" + $("#selectedQuestionType").val()
							+ "&locale=" + $("#moduleLocale").val()
							+ "&report=HDQ_UNIQUE_SUBJECT_MEMBER_REPORT&reportout=hdqballotchoiceoptionreport");
				}
			});
		}
	}
	
	function deptSessionreport(){
		var prevSession = "";
		var currentSession = "";
		
		$.get("ref/currentandprevioussession?houseType=" + $("#selectedHouseType").val()
				+ "&sessionYear=" + $("#selectedSessionYear").val()
				+ "&sessionType=" + $("#selectedSessionType").val()
				+ "&locale=" + $("#moduleLocale").val(), function(data){
			
			if(data){
				currentSession = data[0].id;
				prevSession = data[1].id;
				
				var url = "question/report/dept_sessionwise_report?houseType="+$("#selectedHouseType").val()
				+ "&sessionYear=" + $("#selectedSessionYear").val()
				+ "&sessionType=" + $("#selectedSessionType").val()
				+ "&deviceTypeId=" + $("#selectedQuestionType").val()
				+ "&locale=" + $("#moduleLocale").val()
				+ "&reportout=deptsessionwisereport"
				+ "&currentSession=" + currentSession
				+ "&prevSession=" + prevSession;
				
				var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
				if(devicetype=='questions_starred'){
					url += "&report=QIS_STARRED_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_unstarred'){
					url += "&report=QIS_UNSTARRED_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_shortnotice'){
					url += "&report=QIS_SN_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_halfhourdiscussion_from_question'){
					url += "&report=QIS_HD_DEPARTMENT_SESSION_REPORT";
				}
			
				showTabByIdAndUrl('details_tab', url);
			}
		});
	}
	
	function showVivranReport(){
		var url = "question/report/generalreport?houseType="+$("#selectedHouseType").val()
				+ "&groupYear=" + $("#selectedSessionYear").val()
				+ "&sessionTypeId=" + $("#selectedSessionType").val()
				+ "&deviceTypeId=" + $("#selectedQuestionType").val()
				+ "&locale=" + $("#moduleLocale").val()
				+ "&reportout=vivranreport&report=QIS_STARRED_VIVRAN_REPORT"
				+ "&userName="+$("#authusername").val();
		var urlSession = "ref/sessionbyhousetype/"
		+ $("#selectedHouseType").val() + "/" 
		+ $("#selectedSessionYear").val() + "/"
		+ $("#selectedSessionType").val();
		$.get(urlSession,function(data){
			if(data){
				url += '&sessionId=' + data.id + '&locale=' + $("#moduleLocale").val();
				showTabByIdAndUrl('details_tab', url);
			}
		});
	}
	
	function editQ(id){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&questionType=" + $("#selectedQuestionType").val()
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="question/" + id + "/edit?"+parameters;
		 $.get(resourceUrl,function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:700});
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.")
					.css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			
		/* $.get('question/viewquestion?qid='+id,function(data){
			$.fancybox.open(data,{autoSize: false, width: 800, height:700});				
		},'html'); */
	}
	
	/**** Search Facility ****/
	function searchInt(id){
		//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="searchfacility=yes&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val()+
			        "&houseType="+$("#selectedHouseType").val()+
			        "&sessionType="+$("#selectedSessionType").val()+
			        "&sessionYear="+$("#selectedSessionYear").val()+
			        "&questionType="+$("#selectedQuestionType").val();		
		showTabByIdAndUrl('search_tab','clubentity/init?'+params);
	}
	
	function loadStatusByDeviceType(){
		$.get('ref/loadStatusByDeviceType?deviceType=' + $('#selectedQuestionType').val()
				+ "&currentusergroupType=" + $('#currentusergroupType').val(),function(data){
			if(data.length>0){
				$("#selectedStatus").empty();
				var statusText = "<option value='0'>---"+$("#pleaseSelect").val()+"---</option>";
				for(var i=0;i<data.length;i++){
					statusText = statusText + "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$("#selectedStatus").html(statusText);
				
				$("#filterStatusMaster").empty();
				var filterStatusText = "";
				for(var i=0;i<data.length;i++){
					filterStatusText = filterStatusText + "<option value='" + data[i].id + "'>" + data[i].type + "</option>";
				}
				$("#filterStatusMaster").html(filterStatusText);
				
				$('#selectedStatus option').each(function() {
					var selectedStatusType = $("#filterStatusMaster option[value='"+ $(this).attr('value') + "']").text();			
					//console.log("selectedStatusType: " + selectedStatusType);
					if(!$(this).is(':last-child') && selectedStatusType.indexOf("processed_lapsed") != -1) {
				    	$(this).insertAfter($('#selectedStatus option:last-child'));	
				    	var topSelectedOption = $('#selectedStatus option:first-child');
				    	//console.log("selectedStatus Value: " + $(topSelectedOption).attr('value'));
						$('#selectedStatus').val($(topSelectedOption).attr('value'));
						//return false;
				    }
				});
			}
		}).fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.")
				.css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	function loadOriginalDeviceTypesForGivenDeviceType(deviceType) {
		var parameters = "deviceType="+deviceType;
		$.ajax({
			url: 'ref/loadOriginalDeviceTypesForGivenDeviceType',
			data: parameters, 
			type: 'GET',
	        async: false,
			success: function(data) {
				if(data.length>0){
					$("#selectedOriginalDeviceType").empty();
					var originalDeviceTypeText = "<option value='0' selected='selected'>---"+$("#pleaseSelect").val()+"---</option>";
					for(var i=0;i<data.length;i++){
						originalDeviceTypeText = originalDeviceTypeText + "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#selectedOriginalDeviceType").html(originalDeviceTypeText);
				} else {
					$("#selectedOriginalDeviceType").empty();
					var originalDeviceTypeText = "<option value='0' selected='selected'>---"+$("#pleaseSelect").val()+"---</option>";
					$("#selectedOriginalDeviceType").html(originalDeviceTypeText);
				}
			}
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}					
		});
	}
	
	function showStarredAdmitUnstarredReport(qId){
		$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val() +
				'/' + $("#selectedSessionYear").val() + 
				'/' + $("#selectedSessionType").val(),function(data){
			
			if(data){
				
				var url = "question/report/generalreport?sessionId=" + data.id
				+ "&deviceTypeId=" + $("#selectedQuestionType").val()
				+ "&originalDeviceTypeId=" + $("#selectedOriginalDeviceType").val()
				+ "&latestAssemblyHouseFormationDate=" + $("#latestAssemblyHouseFormationDate").val()
				+ "&locale=" + $("#moduleLocale").val()
				+ "&houseType=" + $("#selectedHouseType").val()
				+ "&statusId=" + $("#selectedStatus").val() 
				+ "&groupId=" + $("#selectedGroup").val()
				+ "&reportout=starred_admit_unstarred_report_mod"
				+ "&departmentId=" +$("#selectedSubDepartment").val()
				+ "&answeringDate="+$("#selectedModuleAsweringDate").val()
				+ "&clubbingStatus="+$("#selectedClubbingStatus").val()
				+ "&qId="+qId
				+ "&fromDate=" + ($("#sumRepFromDate").val()==''?'0':$("#sumRepFromDate").val())
				+ "&toDate=" + ($("#sumRepToDate").val()==''?'0':$("#sumRepToDate").val())
				+ "&report=STARRED_ADMIT_CONVERT_TO_UNSTARRED_REPORT";
							
				if(qId.length>0) {
					url += "&havingIN=TRUE";
				} else {
					url += "&havingIN=FALSE";
				}
				
				showTabByIdAndUrl('details_tab', url);
			}
		});
	}
	
	function loadChartAnsweringDateByGroup(value){
		$.get('ref/groupchartansweringdate?group='+value,function(data){
			var text="<option value='0'>"+$('#pleaseSelect').val()+"</option>";
			if(data.length>0){
				$('#selectedModuleAsweringDate').empty();
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";	
				}
				$('#selectedModuleAsweringDate').html(text);
			}
		});
	}
	
	
	function statuswiseQuestionCountReport(){
		 var allowedGroups = '';
		 var allowedGroupsText = '';
		
		$("#selectedGroup option").each(function()
				{
					allowedGroups = allowedGroups +  $(this).val() +',';
		});
		
		$("#selectedGroup option").each(function()
				{
			allowedGroupsText = allowedGroupsText + $(this).context.label +',';
			
		});
		
		allowedGroups =allowedGroups.substring(0,allowedGroups.length-1);
		allowedGroupsText =allowedGroupsText.substring(0,allowedGroupsText.length-1);
			
		
		 var url = "question/report/statuswiseQuestionCountReport?sessionId=" + $("#loadedSession").val()
		+ "&deviceType=" + $("#selectedQuestionType").val()
		+ "&allowedGroups=" + allowedGroups
		+ "&allowedGroupsText=" + allowedGroupsText; 
		
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		showTabByIdAndUrl('details_tab', url);
		$.unblockUI(); 
	}
	
	
	
	
	
</script>
<link rel="stylesheet" href="//use.fontawesome.com/releases/v5.0.7/css/all.css">
<style>
#QuestionNumberRangeDivViewer{
			
			width: 50px;
			height: 50px;
			z-index: 5000;
			bottom: 50px;
			right: 50px;			
			position: fixed;
			cursor: pointer;
		}

</style>
</head>
<body>
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li><a id="list_tab" class="selected tab" href="#"> <spring:message
						code="generic.list" text="List"></spring:message>
			</a></li>
			<li><a id="details_tab" href="#" class="tab"> <spring:message
						code="generic.details" text="Details">
					</spring:message>
			</a></li>
			<security:authorize
				access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<li><a id="bulkputup_tab" href="#" class="tab"> <spring:message
							code="generic.bulkputup" text="Bulk Putup">
						</spring:message>
				</a></li>
			</security:authorize>
			<security:authorize
				access="hasAnyRole('QIS_CLERK')">
				<li><a id="bulkputupassistant_tab" href="#" class="tab"> <spring:message
							code="generic.bulkputup" text="Bulk Putup">
						</spring:message>
				</a></li>
			</security:authorize>			
			<%-- <security:authorize
				access="hasAnyRole('QIS_SECTION_OFFICER')">
				<li><a id="yaadiupdate_tab" href="#" class="tab"> <spring:message
							code="generic.yaadiupdate" text="Yaadi Questions Update">
						</spring:message>
				</a></li>
			</security:authorize> --%>
			<c:if test="${questionTypeType == 'questions_starred'}">
				<security:authorize
					access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
						'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
						'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
						'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
						'SUPER_ADMIN','QIS_ADMIN','QIS_ADDITIONAL_SECRETARY')">
					<li><a id="rotationorder_tab" href="#" class="tab"> <spring:message
								code="question.rotationorder" text="Rotation Order"></spring:message>
					</a></li>
				</security:authorize>
			</c:if>

			<security:authorize
				access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_CHAIRMAN', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_SECTION_OFFICER', 
				'QIS_UNDER_SECRETARY_COMMITTEE','SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY')">
				<li><a id="chart_tab" href="#" class="tab"> <spring:message
							code="question.chart" text="Chart"></spring:message>
				</a></li>
			</security:authorize>

			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY','QIS_SPEAKER', 'QIS_JOINT_SECRETARY','QIS_PRINCIPAL_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY')">
				<c:if test="${houseType=='upperhouse'}">
					<li><a id="memberballot_tab" href="#" class="tab"> <spring:message
								code="question.memberballot" text="Member Ballot"></spring:message>
					</a></li>
				</c:if>
				<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY')">
					<li><a id="memberballot_tab" href="#" class="tab"
						style="display: none;"> <spring:message
								code="question.memberballot" text="Member Ballot"></spring:message>
					</a></li>
				</security:authorize>
			</security:authorize>
			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY','QIS_CLERK',
				'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY')">
				<li><a id="ballot_tab" href="#" class="tab"> <spring:message
							code="question.ballot" text="Ballot"></spring:message>
				</a></li>

			</security:authorize>
			<%-- <security:authorize
				access="hasAnyRole('QIS_DEPUTY_SECRETARY', 'QIS_JOINT_SECRETARY', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY')"> --%>
			<%-- <security:authorize access="hasAnyRole('QIS_DEPUTY_SECRETARY', 'QIS_JOINT_SECRETARY')">
			<li>
				<a id="yaadi_details_tab" href="#" class="tab"> 
					<spring:message code="generic.yaadi_details_tab" text="Yaadi Details"></spring:message>
				</a>
			</li>
			</security:authorize> --%>
			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY','QIS_CLERK',
				'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', '',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY')">			
				<li>
					<a id="yaadi_details_tab" href="#" class="tab">
						<spring:message code="generic.yaadi_details_tab" text="Yaadi Details"></spring:message>
					</a>
				</li>
			</security:authorize>
			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','QIS_ADDITIONAL_SECRETARY','QIS_CLERK','QIS_TYPIST')">
				<li>
					<a id="search_tab" href="#" class="tab"><spring:message code="question.searchT" text="Search"></spring:message></a>
				</li>

			</security:authorize>
			<security:authorize	access="hasAnyRole('QIS_CLERK', 'QIS_ASSISTANT')">
				<li>
					<a id="bulktimeout_tab" href="#" class="tab">
						<spring:message code="generic.bulktimeout" text="Bulk Timeout"></spring:message>
					</a>
				</li>
			</security:authorize>
		</ul>
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE', 'MEMBER_UPPERHOUSE')">
		<div id="noticeMessageDiv" style="display: none;">
			<label style="color: green;font-size: 14px;font-weight: bold;"><spring:message code="notification_messsage.qis.member.submission_window_extension" text="Submission Window is now extended upto 5.30pm!"/></label>
		</div>
		</security:authorize>
		
		<div class="commandbarContent" style="margin-top: 10px;"
			id="selectionDiv1">

			<a href="#" id="select_houseType" class="butSim"> <spring:message
					code="question.houseType" text="House Type" />
			</a> <select name="selectedHouseType" id="selectedHouseType"
				style="width: 100px; height: 25px;">
				<c:forEach items="${houseTypes}" var="i">
					<c:choose>
						<c:when test="${houseType==i.type}">
							<option value="${i.type}" selected="selected">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.type}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> | <a href="#" id="select_session_year" class="butSim"> <spring:message
					code="question.sessionyear" text="Year" />
			</a> <select name="selectedSessionYear" id="selectedSessionYear"
				style="width: 100px; height: 25px;">
				<c:forEach var="i" items="${years}">
					<c:choose>
						<c:when test="${i==sessionYear }">
							<option value="${i}" selected="selected">
								<c:out value="${i}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">
								<c:out value="${i}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> | <a href="#" id="select_sessionType" class="butSim"> <spring:message
					code="question.sessionType" text="Session Type" />
			</a> <select name="selectedSessionType" id="selectedSessionType"
				style="width: 100px; height: 25px;">
				<c:forEach items="${sessionTypes}" var="i">
					<c:choose>
						<c:when test="${sessionType==i.id}">
							<option value="${i.id}" selected="selected">
								<c:out value="${i.sessionType}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">
								<c:out value="${i.sessionType}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> |

			<a href="#" id="select_questionType" class="butSim"> <spring:message
					code="question.questionType" text="Question Type" />
			</a>
				
			<select name="selectedQuestionType" id="selectedQuestionType"
				style="width: 100px; height: 25px;">
				<c:forEach items="${questionTypes}" var="i">
					<c:choose>
						<c:when test="${questionType==i.id}">
							<option value="${i.id}" selected="selected">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> <select id="deviceTypeMaster" style="display: none;">
				<c:forEach items="${questionTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>
			</select>|
			
			<span id="originalDeviceTypeSpan" style="display: none;">
			<a href="#" id="select_originalDeviceType" class="butSim"> <spring:message
					code="question.originalDeviceType" text="Original Question Type" />
			</a>				
			<select name="selectedOriginalDeviceType" id="selectedOriginalDeviceType" style="width: 100px; height: 25px;">
				<option value="0" selected="selected"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${originalDeviceTypes}" var="i">
					<option value="${i.id}">
						<c:out value="${i.name}"></c:out>
					</option>
				</c:forEach>
			</select> 
			<select id="originalDeviceTypeMaster" style="display: none;">
				<c:forEach items="${originalDeviceTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>
			</select>|
			</span>
			
			<security:authorize
				access="hasAnyRole('QIS_ADMIN','QIS_ASSISTANT','QIS_UNDER_SECRETARY',
			'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER','QIS_JOINT_SECRETARY',
			'QIS_SECRETARY','QIS_OFFICER_ON_SPECIAL_DUTY','QIS_DEPUTY_SPEAKER','QIS_CHAIRMAN','QIS_DEPUTY_CHAIRMAN',
			'QIS_SECTION_OFFICER','QIS_UNDER_SECRETARY_COMMITTEE','QIS_ADDITIONAL_SECRETARY','QIS_CLERK')">
				<hr>
				<div style="display: inline; ">
					<a href="#" id="select_group" class="butSim"> <spring:message
							code="question.group" text="Group" />
					</a>
					<select name="selectedGroup" id="selectedGroup"
						style="width: 100px; height: 25px;">
						<c:forEach items="${groups}" var="i">
							<option value="${i.id}">
								<c:out value="${i.formatNumber()}"></c:out>
							</option>
						</c:forEach>
					</select>
				</div> |
				<div id='answeringDateDiv' style='display: inline-block;' >
					<a href="#" id="workflowLabel" class="butSim" >
						<spring:message code="mytask.chartAnsweringDate" text="Answering Date"/>
					</a>
					<select id="selectedModuleAsweringDate" name="selectedModuleAsweringDate" style="width: 115px; height: 25px;">
						<option value="0"><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message></option>
					</select>|
				</div>	
						
				<a href="#" id="select_status" class="butSim"> <spring:message
						code="question.status" text="Status" />
				</a>
				<select name="selectedStatus" id="selectedStatus"
					style="width: 250px; height: 25px;">
					<option value="0">
							<spring:message code='please.select' text='Please Select'/>
					</option>
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select> |					
				<span id="answerReceivedStatusSpan" style="display: none;">
					<a href="#" id="select_answerReceivedStatus" class="butSim"> <spring:message
							code="question.answerReceivedStatus" text="Answer Received Status" />
					</a>				
					<select name="selectedAnswerReceivedStatus" id="selectedAnswerReceivedStatus" style="width: 100px; height: 25px;">
						<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"/></option>
						<option value="answerReceived"><spring:message code="question.answerReceivedStatus.answerReceived" text="Answer Received"/></option>
						<option value="answerNotReceived"><spring:message code="question.answerReceivedStatus.answerNotReceived" text="Answer Not Received"/></option>						
					</select> |
				</span>
				<hr>
				<div id='questionDepartment' style="display:inline;">
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
					</select> |
				</div>	 
			</security:authorize>
			
			<security:authorize
				access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST','HDS_TYPIST')">
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
				
				<select name="selectedGroup" id="selectedGroup" style="width: 100px; height: 25px; display: none;">
				</select>
	
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
					<a href="#" id="select_itemcount" class="butSim"> <spring:message
							code="motion.itemcount" text="No. of Questions(Bulk Putup)" />
					</a>
					<select name="selectedItemsCount" id="selectedItemsCount"
						style="width: 100px; height: 25px;">
						<!-- <option value="30">30</option>
						<option value="25">25</option>
						<option value="20">20</option>
						<option value="15">15</option> -->
						<option value="10">10</option>
						<option value="5">5</option>
					</select>|	
					</security:authorize>
			</security:authorize>
			
			<select id="filterStatusMaster" style="display:none;">
				<c:forEach items="${status}" var="i">
					<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
				</c:forEach>
			</select>
			
			<security:authorize	access="hasAnyRole('QIS_CLERK', 'QIS_ASSISTANT', 'QIS_SECTION_OFFICER', 'QIS_DEPUTY_SECRETARY', 'QIS_JOINT_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_CHAIRMAN')">
				<a href="#" id="select_clubbingStatus" class="butSim"> 
					<spring:message	code="generic.clubbingStatus" text="Clubbing Status"/>
				</a>
				<select name="selectedClubbingStatus" id="selectedClubbingStatus" style="height: 25px;">
					<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
					<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
					<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
				</select>
			</security:authorize>
			
			<security:authorize	access="!hasAnyRole('QIS_CLERK', 'QIS_ASSISTANT')">
				<select hidden="true" name="selectedClubbingStatus" id="selectedClubbingStatus" style="height: 25px;">
					<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
					<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
					<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
				</select>
			</security:authorize>			
			
			<security:authorize	access="hasAnyRole('QIS_ASSISTANT','QIS_CLERK')">		
				<a href="#" id="select_itemcount" class="butSim"> <spring:message
						code="question.itemcount" text="No. of Questions(Bulk Putup)" />
				</a>
				<select name="selectedItemsCount" id="selectedItemsCount"
					style="width: 100px; height: 25px;">
					<option value="100">100</option>
					<option value="75">75</option>
					<option value="50">50</option>
					<option value="25">25</option>
					<option value="10">10</option>
					<option value="5">05</option>
				</select>
			</security:authorize>
			
			<security:authorize access="!hasAnyRole('QIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
					<spring:message code="question.reports" text="Reports"/>
				</a>
				<div id="assistantReportDiv" style="display: none; border: 1px solid green; border-radius: 6px; margin: 10px 0px 10px 0px; padding: 5px;">
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
						<hr>
					</security:authorize>
					
					<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','QIS_CLERK','HDS_CLERK','HDS_ASSISTANT','QIS_CHAIRMAN')">
						<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER','QIS_CHAIRMAN')">
							<a href="#" id="memberwise_questions_report" class="butSim link">
								<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
							</a> |		
							<a href="#" id="online_offline_submission_count_report" class="butSim link">
								<spring:message code="question.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
							</a> |
							<a href="#" id="partywise_questions_count_report" class="butSim link">
								<spring:message code="question.partywise_questions_count_report" text="Partywise Questions Count Report"/>
							</a> |
							<a href="#" id="extended_grid_report" class="butSim link">
								<spring:message code="question.extended_grid_report" text="Extended Grid Report"/>
							</a> |
							<hr> 
						</security:authorize>
						
						<security:authorize access="hasAnyRole('QIS_CLERK')">
							<a href="#" id="online_offline_submission_count_report" class="butSim link">
								<spring:message code="question.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
							</a> |
							<a href="#" id="extended_grid_report" class="butSim link">
								<spring:message code="question.extended_grid_report" text="Extended Grid Report"/>
							</a> |
						</security:authorize>
						
						<a href="#" id="group_bulletein_report" class="butSim link">
							<spring:message code="question.group_bulletein_report" text="Group Bulletein Report"/>
						</a> |
						<a href="#" id="bulletein_report" class="butSim link">
							<spring:message code="question.bulletein_report" text="Bulletein Report"/>
						</a> |
						
						<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER','QIS_CHAIRMAN')">
							<a href="#" id="groupwise_counts_report_for_bac" class="butSim link">
								<spring:message code="question.bulletein_report" text="Groupwise BAC Report"/>
							</a> |
						</security:authorize>						
						<a href="#" id="ahwal_report" class="butSim link">
							<spring:message code="question.ahwal_report" text="Sankshipt Ahwal Report"/>
						</a> |
						<a href="#" id="statuswise_questionsCount_report" class="butSim link">
							<spring:message code="question.statuswiseQuestionCount" text="Status Wise Question Count Report"/>
						</a> |
						<hr>
						<a href="#" id="departmentwise_report" class="butSim link">
							<spring:message code="question.departmentwise_report" text="Department's Questions Report"/>
						</a> |
						<%-- <a href="#" id="statistical_counts_report" class="butSim link">
							<spring:message code="question.statistical_counts_report" text="Statistical Counts Report"/>
						</a> | --%>
						<a href="#" id="memberwise_admitted_questions_report" class="butSim link">
							<spring:message code="question.memberwisereport.admitted" text="Member's Admitted Questions Report"/>
						</a> |						
						<a href="#" id="generateMemberDraftReport" class="butSim">
							<spring:message code="question.generateMemberDraftReport" text="Generate Member Draft Report"/>
						</a> 
						<!-- Edited By Shubham Amande  -->
						|
					    <a href="#" id="TestReport" class="butSim" >
							<spring:message code="question.LastRevisionReport" text="LastRevisionReport"/>
						</a>
						 |
						<!----------------------------------------------------------->
						<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER','QIS_CHAIRMAN')">
							<a href="#" id="showVivranReport" class="butSim">
								<spring:message code="question.vivranReport" text="Vivran Report"/>
							</a> |
						</security:authorize>
					</security:authorize>
					<security:authorize access="hasAnyRole('QIS_UNDER_SECRETARY', 'QIS_DEPUTY_SECRETARY', 'QIS_JOINT_SECRETARY', 'QIS_PRINCIPAL_SECRETARY')">
						<a href="#" id="memberwise_questions_report" class="butSim link">
							<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
						</a> |	
						<a href="#" id="departmentwise_report" class="butSim link">
							<spring:message code="question.departmentwise_report" text="Department's Questions Report"/>
						</a> |
						<a href="#" id="online_offline_submission_count_report" class="butSim link">
							<spring:message code="question.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
						</a>	
					</security:authorize>
				</div>
			</security:authorize>
			<hr>
		</div>

		<div class="tabContent"></div>

		<security:authorize access="!hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
		<c:if test="${houseType=='upperhouse'}">
		<c:if test="${questionTypeType=='questions_starred'}">
			<div id="QuestionNumberRangeDivViewer" title="View Question Number Range Viewer">
			<b><img src="./resources/images/Crystal_Project_Documentinfo.png"  width="45" height="45"></b>
			</div>
			
			<div id="QuestionNumberRange"
			style="position: fixed; z-index: 999; background: scroll; right: 25px; bottom: 70px; width: 300px; overflow: auto; height: 480px;">
			<%-- <c:forEach items="${departmentwiseCounts}" var="i"> --%>
				<div
					style="color: black; border: 1px solid black; background: #80dfff; width: 200px; height: 30px; padding: 2px; font-weight: bold; vertical-align: middle; display: inline-block; font-size: 12px;">
					<spring:message code='generic.QuestionNumberRange' text='Start And End Question Number '></spring:message></div>
				<div id="qsnRange"
					style="color: black; border: 1px solid black; background: #80dfff; width: 80px; height: 30px; padding: 2px; font-weight: bold; vertical-align: middle; display: inline-block; text-align: center; font-size: 12px;">
					<%-- ${startNumber} - ${lastNumber} --%>
				</div>
				<div style="height: 1px;"></div>
			<%-- </c:forEach> --%>
		</div>
		</c:if>
		</c:if>
			</security:authorize>
		<input type="hidden" id="key" name="key"> 
		<input type="hidden" id="loadedSession" value="" />
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden"  id="generalClerkDisplay" value="${GENERAL_CLERK_YAADI_TAB_OFF}">
		<input type="hidden" name="srole" id="srole" value="${role}">
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${questionTypeType}">
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">
		<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>">
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>">
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>">
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>">
		<input type="hidden" id="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="chartAnsweringDate" name="chartAnsweringDate" value="-">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>" />
		<%-- <input type="hidden" id="questionChoiceSubmissionWindowClosedMsg" value="<spring:message code='memberballotchoice.SubmissionWindowClosedMsg' text='Submission window for filling questions choices is closed.'/>"/> --%>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" id="processMode" value="${processMode}" />
		<input type="hidden" id="member_questions_view_status_flag" value="${member_questions_view_status_flag}" />
		<input type="hidden" id="member_admitted_questions_view_flag" value="${member_admitted_questions_view_flag}" />
		<input type="hidden" id="member_rejected_questions_view_flag" value="${member_rejected_questions_view_flag}" />
		<%-- <input type="hidden" id="member_unstarred_questions_view_flag" value="${member_unstarred_questions_view_flag}" /> --%>
		<input type="hidden" id="member_unstarred_questions_view_flag" value="unstarred_visible" />
	</div>
</body>
</html>