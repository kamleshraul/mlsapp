<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.list"
		text="List Of Questions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		/**** On Page Load ****/
		var currentDeviceType = $("#currentDeviceType").val();
		var currentHouseType = $("#currentHouseType").val();
		/**** Fro chart_tab to show or hide ****/
		if (currentDeviceType == 'questions_starred' || (currentDeviceType=='questions_halfhourdiscussion_standalone' && currentHouseType=='lowerhouse')) {
			$("#chart_tab").show();
		} else {
			$("#chart_tab").hide();
		}

		/**** For ballot or member ballot tab to be visible ****/
		if (currentDeviceType == 'questions_starred'
				&& currentHouseType == 'upperhouse') {
			$("#memberballot_tab").show();
			$("#ballot_tab").hide();
		} else if ((currentDeviceType == 'questions_starred' && currentHouseType == 'lowerhouse')
				|| currentDeviceType == 'questions_halfhourdiscussion_from_question'
				|| currentDeviceType == 'questions_halfhourdiscussion_standalone') {
			$("#memberballot_tab").hide();
			$("#ballot_tab").show();
		} else {
			$("#memberballot_tab").hide();
			$("#ballot_tab").hide();
		}		
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
				loadGroupsFromSessions();
				if (value == 'upperhouse') {
					$('#memberballot_tab').show();					
				} else {
					$('#memberballot_tab').hide();					
				}
			}
		});
		/**** session year changes then reload grid****/
		$("#selectedSessionYear").change(function() {
			var value = $(this).val();
			/* $('#questionDepartment').hide();
			$('#subDepartment').val(""); */
			if (value != "") {
				loadGroupsFromSessions();
			}
		});
		/**** session type changes then reload grid****/
		$("#selectedSessionType").change(function() {
			var value = $(this).val();
			/* $('#questionDepartment').hide();
			$('#subDepartment').val(""); */
			if (value != "") {
				loadGroupsFromSessions();
			}
		});
		/**** question type changes then reload grid****/
		$("#selectedQuestionType").change(function() {
							var value = $(this).val();
							var text = $("#deviceTypeMaster option[value='"+ value + "']").text();
							
							if(text.indexOf("questions_halfhourdiscussion_")==-1){
								$("#hdReportsDiv").hide();
							}else{
								$("#hdReportsDiv").show();
							}
							
							if (text == 'questions_starred') {
								$("#chart_tab").show();
							} else {
								$("#chart_tab").hide();
							}
							if (text == 'questions_starred' && currentHouseType == 'upperhouse') {
								$("#memberballot_tab").show();
								$("#ballot_tab").hide();
							} else if ((text == 'questions_starred' && currentHouseType == 'lowerhouse')
									|| text == 'questions_halfhourdiscussion_from_question'
									|| text == 'questions_halfhourdiscussion_standalone') {
								$("#memberballot_tab").hide();
								$("#ballot_tab").show();
							} else {
								$("#memberballot_tab").hide();
								$("#ballot_tab").hide();
							}
							if(text == 'questions_unstarred') {
								$("#unstarredYaadiSpan").show();
							} else {
								$("#unstarredYaadiSpan").hide();
							}
							if (value != "") {
								reloadQuestionGrid();
							}

						});
		/**** status changes then reload grid****/
		$("#selectedStatus").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadQuestionGrid();
			}
			$("#generateIntimationLetter").attr("href","");
			
		});
		/**** group changes then reload grid ****/
		$("#selectedGroup").change(function() {
			var value = $(this).val();
			if (value != "") {
				$("#ugparam").val(value);
				loadSubDepartmentsFromGroup(value, 'no');								
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
		
		/**** Yaadi Update ****/
		$("#yaadiupdate_tab").click(function() {
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
		
		loadSubDepartmentsFromGroup($("#selectedGroup").val(),'yes');
	});
	
	function showCurrentStatusReport(val, qId){
		$("#selectionDiv1").hide();
		var device = $("#deviceTypeMaster option[value='"+$("#selectedQuestionType").val()+"']").text().split("_")[0];
		showTabByIdAndUrl('details_tab', "question/report/currentstatusreport?device="+ device +"&reportType="+val+"&qId="+qId);
	}
	function showAdmissionReport(){
		params = "houseType=" + $('#selectedHouseType').val()
		+ '&sessionYear=' + $("#selectedSessionYear").val()
		+ '&sessionType=' + $("#selectedSessionType").val()
		+ '&deviceType=' + $("#selectedQuestionType").val()
		+ '&groupId=' + $("#selectedGroup").val()
		+ '&subDepartment=' + $("#selectedSubDepartment").val();
		
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

	/**** displaying grid ****/
	function showQuestionList() {
		showTabByIdAndUrl('list_tab', 'question/list?houseType='
				+ $('#selectedHouseType').val() + '&questionType='
				+ $("#selectedQuestionType").val() + '&sessionYear='
				+ $("#selectedSessionYear").val() + '&sessionType='
				+ $("#selectedSessionType").val() + "&ugparam="
				+ $("#ugparam").val() + "&status=" + $("#selectedStatus").val()
				+ "&role=" + $("#srole").val() + "&usergroup="
				+ $("#currentusergroup").val() + "&usergroupType="
				+ $("#currentusergroupType").val()+"&subdepartment="
				+ $("#selectedSubDepartment").val());
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
			+ "&ugparam=" + $("#ugparam").val() + "&status="
			+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
			$.prompt($('#confirmDeleteMessage').val() + row,
					{buttons : {Ok : true,Cancel : false},
					callback : function(v) {
						if (v) {
							$.delete_('question/' + row+ '/delete',null,function(data,textStatus,XMLHttpRequest) {
								showQuestionList();
							}).fail(function() {
								if ($("#ErrorMsg").val() != '') {
									$("#error_p").html($("#ErrorMsg").val()).css({'color' : 'red','display' : 'block'});
								} else {
									$("#error_p").html("Error occured contact for support.").css({'color' : 'red','display' : 'block'});
								}
								scrollTop();
							});
						}
					}
			});
		}
	}
	/**** reload grid ****/
	function reloadQuestionGrid() {
		$("#gridURLParams").val(
				"houseType=" + $("#selectedHouseType").val() + "&sessionYear="
						+ $("#selectedSessionYear").val() + "&sessionType="
						+ $("#selectedSessionType").val() + "&questionType="
						+ $("#selectedQuestionType").val() + "&ugparam="
						+ $("#ugparam").val() + "&status="
						+ $("#selectedStatus").val() + "&role="
						+ $("#srole").val() + "&usergroup="
						+ $("#currentusergroup").val() + "&usergroupType="
						+ $("#currentusergroupType").val()+"&subDepartment="
						+ $("#selectedSubDepartment").val());
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
	/**** Chart Tab ****/
	function viewChart() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();
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
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();

		}
		var resourceURL = 'question/bulksubmission?' + parameters
				+ "&itemscount=" + $("#selectedItemsCount").val();
		showTabByIdAndUrl('bulkputup_tab', resourceURL);
	}
	/**** Bulk putup(Assistant)****/
	function bulkPutupAssistant() {
		var parameters = null;
		if ($('#currentDeviceType').val() == "questions_halfhourdiscussion_standalone"
				&& $("#selectedHouseType").val() == "lowerhouse") {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&file=" + $("#selectedFileCount").val();
			;
		} else {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&file=" + $("#selectedFileCount").val() + "&group="
					+ $("#ugparam").val();
		}

		var resourceURL = 'question/bulksubmission/assistant/int?' + parameters
				+ "&itemscount=" + $("#selectedItemsCount").val();
		showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);
	}
	
	
	/**** Bulk yaadiupdate(Assistant)****/
	function yaadiUpdate() {
		var parameters = null;
		if ($('#currentDeviceType').val() == "questions_halfhourdiscussion_standalone"
				&& $("#selectedHouseType").val() == "lowerhouse") {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&file=" + $("#selectedFileCount").val();
			;
		} else {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&questionType=" + $("#selectedQuestionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&group=" + $("#ugparam").val();
		}

		var resourceURL = 'question/yaaditodiscussupdate/assistant/init?' + parameters;
		showTabByIdAndUrl('yaadiupdate_tab', resourceURL);
	}
	/**** To Generate Intimation Letter ****/
	function generateIntimationLetter() {			
		var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
		if(selectedQuestionId.length<1) {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else if(selectedQuestionId.length>1) {
			$.prompt("Please select only one question!");
			return false;
		} else {			
			$('#generateIntimationLetter').attr('href', 'question/report/generateIntimationLetter?questionId='+selectedQuestionId+'&intimationLetterFilter='+$("#intimationLetterFilter").val());
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
			$.get('question/report/generateClubbedIntimationLetter/getClubbedQuestions?questionId='+selectedQuestionId,function(data) {
				$.fancybox.open(data,{autoSize:false,width:400,height:300});
			},'html').fail(function(){				
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		}		
	}
	/**** To Generate Unstarred Yaadi Report ****/
	function generateUnstarredYaadiReport() {			
		var parameters = "houseType=" + $("#selectedHouseType").val()
					   + "&sessionYear=" + $("#selectedSessionYear").val()
					   + "&sessionType=" + $("#selectedSessionType").val()
					   + "&ugparam=" + $("#ugparam").val()
					   + "&role=" + $("#srole").val()
				 	   + "&usergroup=" + $("#currentusergroup").val()
					   + "&usergroupType=" + $("#currentusergroupType").val();
		$.get('question/report/generateUnstarredYaadiReport/getUnstarredYaadiNumberAndDate?'+parameters, function(data) {
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
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&questionType="+$("#selectedQuestionType").val()
		 +"&group="+$("#selectedGroup").val()
		 +"&status="+$("#selectedStatus").val()
		 +"&role="+$("#srole").val() 
		 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=question";	
		var resourceURL = 'question/report/memberwisequestions?'+ parameters;	
		//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		showTabByIdAndUrl('details_tab', resourceURL);
		/* $.get(resourceURL,function(data){
			$("#ballotResultDiv").html(data);
			$.unblockUI();				
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
			scrollTop();
		}); */
	}
	function groupBulleteinReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&group="+$("#selectedGroup").val();		 
		$('#group_bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	function bulleteinReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val();	
		$('#bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	function ahwalBulleteinReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&isAhwalBulletein=yes";	
		$('#bulletein_report').attr('href', 'question/report/bulleteinreport?'+ parameters);
	}
	function ahwalStarredUnstarredReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val();
		$('#ahwal_starredUnstarred_report').attr('href', 'question/report/ahwalStarredUnstarredReport?'+ parameters);
	}
	function ahwalShortNoticeStatsReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val();
		$('#ahwal_shortnotice_stats_report').attr('href', 'question/report/ahwalShortNoticeStatsReport?'+ parameters);
	}
	function starredDepartmentwiseStatsReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&questionType=starred";
		$('#starred_departmentwise_stats_report').attr('href', 'question/report/departmentwiseStatsReport?'+ parameters);
	}
	function unstarredDepartmentwiseStatsReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&questionType=unstarred";		 
		$('#unstarred_departmentwise_stats_report').attr('href', 'question/report/departmentwiseStatsReport?'+ parameters);
	}
	function departmentwiseQuestionsReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&deviceType="+$("#selectedQuestionType").val()
		 +"&subDepartment="+$("#selectedSubDepartment").val()
		 +"&answeringDate=" //answering date can be selected later from dropdown for filtering result
		 +"&group="+$("#selectedGroup").val()
		 +"&status="+$("#selectedStatus").val()
		 +"&locale="+$("#moduleLocale").val()
		 +"&role="+$("#srole").val()
		 +"&report="+"QIS_DEPARTMENTWISE_QUESTIONS_"+$("#selectedHouseType").val().toUpperCase()
		 +"&reportout="+"departmentwisequestions";	
		var resourceURL = 'question/report/departmentwisequestions?'+ parameters;			
		showTabByIdAndUrl('details_tab', resourceURL);
	}
	function ahwalHDQConditionReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()	
		 +"&questionType=questions_halfhourdiscussion_from_question";
		$('#ahwal_hdq_condition_report').attr('href', 'question/report/ahwalHDConditionReport?'+ parameters);
	}
	function ahwalHDSConditionReport(){
		var parameters = "houseType="+$("#selectedHouseType").val()
		 +"&sessionYear="+$("#selectedSessionYear").val()
		 +"&sessionType="+$("#selectedSessionType").val()
		 +"&questionType=questions_halfhourdiscussion_standalone";
		$('#ahwal_hds_condition_report').attr('href', 'question/report/ahwalHDConditionReport?'+ parameters);
	}
	function sankshiptAhwalReport() {
		showTabByIdAndUrl('details_tab', 'question/report/sankshiptAhwal?selectedHouseType='+$('#selectedHouseType').val());
	}
	function loadSubDepartmentsFromGroup(group, init){
		$.get('ref/getDepartment?group='+group+'&userGroup='+$('#currentusergroup').val()
				+'&deviceType='+$("#selectedQuestionType").val()+'&houseType='+$("#selectedHouseType").val(),function(data){
			
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
				reloadQuestionGrid();
			}else if(init=='yes'){
				showQuestionList();
			}
		});
	}	
	function statReport(){
		var url = "question/report/statreport?sessionYear="+$("#selectedSessionYear").val()
				+ "&sessionType="+$("#selectedSessionType").val()
				+ "&houseType="+$("#selectedHouseType").val()
				+ "&deviceType="+$("#selectedQuestionType").val();
		
			if($("#selectedHouseType").val()=='lowerhouse'){
				var items = new Array();
				items.push('under_secretary');
				items.push('under_secretary_committee');
				items.push('principal_secretary');
				items.push('speaker');
				url += "&userGroups="+items;
			}else if($("#selectedHouseType").val()=='upperhouse'){
				var items = new Array();
				items.push('under_secretary');
				items.push('under_secretary_committee');
				items.push('principal_secretary');
				items.push('chairman');
				url += "&userGroups=" + items;
			}
		showTabByIdAndUrl('details_tab', url);
	}
	
	function sendMessage() {		
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&deviceType=" + $("#selectedQuestionType").val()
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
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
		+ "&days="+$("#hdDaysForReport").val()
		+ "&groupId=0&subDepartment=0";
		showTabByIdAndUrl('details_tab', 'question/report/halfhourdaysubmitreport?'+ param);
	}
	
	function showHDStatAndAdmissionreport(){
		var param = "sessionYear="+$("#selectedSessionYear").val()
		+ "&sessionType="+$("#selectedSessionType").val()
		+ "&houseType="+$("#selectedHouseType").val()
		+ "&deviceType="+$("#selectedQuestionType").val()
		+ "&days="+$("#hdDaysForReport").val()
		+ "&groupId=0&subDepartment=0";
		showTabByIdAndUrl('details_tab', 'question/report/hdstatandadmissionreport?'+ param);
	}
	
	function showHDGeneralreport(){
		var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
		
		$.get(url,function(data){
			if(data){
				showTabByIdAndUrl('details_tab','question/report/generalreport?sessionId='+data.id+"&deviceTypeId="+$("#selectedQuestionType").val()+"&locale="+$("#moduleLocale").val()+"&report=HD_CONDITION_REPORT&reportout=hdconditionreport");
			}
		});
	}
	
	function showBallotChoiceOptionReport(){
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		if(devicetype=='questions_halfhourdiscussion_from_question' || devicetype=='questions_halfhourdiscussion_standalone'){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab','question/report/generalreport?sessionId='+data.id+"&deviceTypeId="+$("#selectedQuestionType").val()+"&locale="+$("#moduleLocale").val()+"&report=HDQ_UNIQUE_SUBJECT_MEMBER_REPORT&reportout=hdqballotchoiceoptionreport");
				}
			});
		}
	}
	
	function deptSessionreport(){
		var url = "question/report/generalreport?houseType="+$("#selectedHouseType").val()+
				"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+
				"&deviceTypeId="+$("#selectedQuestionType").val()+"&locale="+$("#moduleLocale").val()+
				"&reportout=deptsessionwisereport";
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		if(devicetype=='questions_starred'){
			url += "&report=QIS_STARRED_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_unstarred'){
			url += "&report=QIS_UNSTARRED_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_shortnotice'){
			url += "&report=QIS_SN_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_halfhourdiscussion_from_question' || devicetype=='questions_halfhourdiscussion_standalone'){
			url += "&report=QIS_HD_DEPARTMENT_SESSION_REPORT";
		}
		
		
		showTabByIdAndUrl('details_tab', url);
	}
	
	function showVivranReport(){
		var url = "question/report/generalreport?houseType="+$("#selectedHouseType").val()+
				"&groupYear="+$("#selectedSessionYear").val()+"&sessionTypeId="+$("#selectedSessionType").val()+
				"&deviceTypeId="+$("#selectedQuestionType").val()+"&locale="+$("#moduleLocale").val()+
				"&reportout=vivranreport&report=QIS_STARRED_VIVRAN_REPORT"+
				"&userName="+$("#authusername").val();
		var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
		/* if(devicetype=='questions_starred'){
			url += "&report=QIS_STARRED_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_unstarred'){
			url += "&report=QIS_UNSTARRED_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_shortnotice'){
			url += "&report=QIS_SN_DEPARTMENT_SESSION_REPORT";
		}else if(devicetype=='questions_halfhourdiscussion_from_question' || devicetype=='questions_halfhourdiscussion_standalone'){
			url += "&report=QIS_HD_DEPARTMENT_SESSION_REPORT";
		} */
		
		var urlSession = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
		$.get(urlSession,function(data){
			if(data){
				url += '&sessionId='+data.id+'&locale='+$("#moduleLocale").val();
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
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
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
			        "&sessionYear="+$("#selectedSessionYear").val();		
		/* $.get('clubentity/init?'+params,function(data){
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			if(data){
				$.unblockUI();
			}
			$("#clubbingResultDiv").html(data);
			$("#clubbingResultDiv").show();
			$("#referencingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToQuestionDiv").show();			
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		}); */
		showTabByIdAndUrl('search_tab','clubentity/init?'+params);
	}
</script>
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
				access="hasAnyRole('QIS_ASSISTANT','HDS_ASSISTANT')">
				<li><a id="bulkputupassistant_tab" href="#" class="tab"> <spring:message
							code="generic.bulkputup" text="Bulk Putup">
						</spring:message>
				</a></li>
			</security:authorize>
			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT','HDS_ASSISTANT')">
				<li><a id="yaadiupdate_tab" href="#" class="tab"> <spring:message
							code="generic.yaadiupdate" text="Yaadi Questions Update">
						</spring:message>
				</a></li>
			</security:authorize>
			<c:if test="${questionTypeType == 'questions_starred'}">
				<security:authorize
					access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
						'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
						'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
						'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
						'SUPER_ADMIN','HDS_ASSISTANT','QIS_ADMIN','QIS_ADDITIONAL_SECRETARY')">
					<li><a id="rotationorder_tab" href="#" class="tab"> <spring:message
								code="question.rotationorder" text="Rotation Order"></spring:message>
					</a></li>
				</security:authorize>
			</c:if>

			<security:authorize
				access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_SECTION_OFFICER', 
				'QIS_UNDER_SECRETARY_COMMITTEE','SUPER_ADMIN','HDS_ASSISTANT','QIS_ADDITIONAL_SECRETARY')">
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
				access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN','HDS_ASSISTANT','QIS_ADDITIONAL_SECRETARY')">
				<li><a id="ballot_tab" href="#" class="tab"> <spring:message
							code="question.ballot" text="Ballot"></spring:message>
				</a></li>

			</security:authorize>
				<li>
					<a id="search_tab" href="#" class="tab"><spring:message code="question.searchT" text="Search"></spring:message></a>
				</li>
		</ul>
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

			<c:choose>
				<c:when
					test="${questionTypeType=='questions_halfhourdiscussion_standalone'}">
					<a href="#" id="devicetypeLabel" class="butSim"> <spring:message
							code="mytask.deviceType" text="Device Type" />
					</a>
				</c:when>
				<c:otherwise>
					<a href="#" id="select_questionType" class="butSim"> <spring:message
							code="question.questionType" text="Question Type" />
					</a>
				</c:otherwise>
			</c:choose>
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

			<security:authorize
				access="hasAnyRole('QIS_ADMIN','QIS_ASSISTANT','QIS_UNDER_SECRETARY',
			'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER','QIS_JOINT_SECRETARY',
			'QIS_SECRETARY','QIS_OFFICER_ON_SPECIAL_DUTY','QIS_DEPUTY_SPEAKER','QIS_CHAIRMAN','QIS_DEPUTY_CHAIRMAN',
			'QIS_SECTION_OFFICER','QIS_UNDER_SECRETARY_COMMITTEE','RIS_ASSISTANT','HDS_ASSISTANT','HDS_UNDER_SECRETARY',
			'HDS_DEPUTY_SECRETARY','HDS_PRINCIPAL_SECRETARY','HDS_SPEAKER','HDS_JOINT_SECRETARY',
			'HDS_SECRETARY','HDS_OFFICER_ON_SPECIAL_DUTY','HDS_DEPUTY_SPEAKER','HDS_CHAIRMAN','HDS_DEPUTY_CHAIRMAN',
			'HDS_SECTION_OFFICER','HDS_UNDER_SECRETARY_COMMITTEE','QIS_ADDITIONAL_SECRETARY','QIS_CLERK','HDS_CLERK')">
				<hr>
				<c:choose>
					<c:when test="${not(questionTypeType=='questions_halfhourdiscussion_standalone' and houseType=='lowerhouse')}">
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
						</div>
					</c:when>
					<c:otherwise>
						<div style="display: none; ">
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
						</div>
					</c:otherwise>
				</c:choose>|
				<a href="#" id="select_status" class="butSim"> <spring:message
						code="question.status" text="Status" />
				</a>
				<select name="selectedStatus" id="selectedStatus"
					style="width: 250px; height: 25px;">
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select> |	
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
					</select>|
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
				<c:if
					test="${not (questionTypeType!='questions_halfhourdiscussion_standalone' and houseType=='lowerhouse') }">|
					<select name="selectedGroup" id="selectedGroup"
						style="width: 100px; height: 25px; display: none;">
					</select>
				</c:if>
				<security:authorize
					access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
					<hr>
					<a href="#" id="select_itemcount" class="butSim"> <spring:message
							code="motion.itemcount" text="No. of Questions(Bulk Putup)" />
					</a>
					<select name="selectedItemsCount" id="selectedItemsCount"
						style="width: 100px; height: 25px;">
						<option value="30">30</option>
						<option value="25">25</option>
						<option value="20">20</option>
						<option value="15">15</option>
						<option value="10">10</option>
						<option value="5">05</option>
					</select>|	
					</security:authorize>
			</security:authorize>
			<security:authorize
				access="hasAnyRole('QIS_ASSISTANT','HDS_ASSISTANT')">
				<hr>
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
				</select>|	
				<a href="#" id="select_filecount" class="butSim"> <spring:message
						code="question.filecount" text="Select File(Bulk Putup)" />
				</a>
				<select name="selectedFileCount" id="selectedFileCount"
					style="width: 100px; height: 25px;">
					<option value="-">
						<spring:message code='please.select' text='Please Select' />
					</option>
					<c:if test="${highestFileNo>0 }">
						<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
							<option value="${i}">${i}</option>
						</c:forEach>
					</c:if>
				</select>|	
			</security:authorize>
			<hr>
		</div>

		<div class="tabContent"></div>

		<input type="hidden" id="key" name="key"> <input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">
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
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
	</div>
</body>
</html>