<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="appropriationbillmotion.list" text="List Of Appropriation Bill Motions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	/**** Load Session ****/
	function loadSession(){
		$.get("ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),
			function(data){
				if(data){
					$("#loadedSession").val(data.id);
					//alert($("#loadedSession").val());
				}
			});
	}
	$(document).ready(function() {
		/**** On Page Load ****/
		/* var currentDeviceType = $("#currentDeviceType").val();
		var currentHouseType = $("#currentHouseType").val(); */
		
		$("#new_record_saver").click(function(){
			newAppropriationBillMotion();
		});
		
		/*Tooltip*/
		$(".toolTip").hide();
		
		$("#bulkputup_tab").hide();
		
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
			showAppropriationBillMotionList();
		});
		/**** house type changes then reload grid****/
		$("#selectedHouseType").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadAppropriationBillMotionGrid();	
			}
		});
		/**** session year changes then reload grid****/
		$("#selectedSessionYear").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadAppropriationBillMotionGrid();
			}
		});
		/**** session type changes then reload grid****/
		$("#selectedSessionType").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadAppropriationBillMotionGrid();	
			}
		});
		/**** question type changes then reload grid****/
		$("#selectedAppropriationBillMotionType").change(function() {
				var value = $(this).val();
				var text = $("#deviceTypeMaster option[value='" + value + "']").text();
				
				if (value != "") {
					reloadAppropriationBillMotionGrid();
				}

		});
		
		/**** Department change ****/
		$("#selectedSubDepartment").change(function(){
			reloadAppropriationBillMotionGrid();
			
		});
		
		/**** status changes then reload grid****/
		$("#selectedStatus").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadAppropriationBillMotionGrid();
			}
		});
		
		/**** Bulk Putup ****/
		$("#bulkputup_tab").click(function() {
			bulkPutup();
		});
		/**** Bulk Putup ****/
		$("#bulkputupassistant_tab").click(function() {
			$("#selectionDiv1").hide();
			bulkPutupAssistant();
		});	
		
		/**** Search Tab ****/
		$('#search_tab').click(function() {
			$("#selectionDiv1").hide();
			searchInt();
		});
	
		/**** show appropriationbillmotions list method is called by default.****/
		showAppropriationBillMotionList();
		
		/**** Load subdepartment filter ****/
		loadSubDepartments('yes');
	});
	
	/**** displaying grid ****/
	function showAppropriationBillMotionList() {
		$("#key").val('');
		
		showTabByIdAndUrl('list_tab', 'appropriationbillmotion/list?houseType='
				+ $('#selectedHouseType').val() + '&appropriationBillMotionType='
				+ $("#selectedAppropriationBillMotionType").val() + '&sessionYear='
				+ $("#selectedSessionYear").val() + '&sessionType='
				+ $("#selectedSessionType").val() + "&ugparam="
				+ $("#ugparam").val() + "&status=" + $("#selectedStatus").val()
				+ "&role=" + $("#srole").val() + "&usergroup="
				+ $("#currentusergroup").val() + "&usergroupType="
				+ $("#currentusergroupType").val()
				+ "&subDepartment="+$("#selectedSubDepartment").val()
				+ "&status="+$("#selectedStatus").val());
		
		loadSession();
		
		//make grid visible again with refreshed data
		//if gridDiv is hidden make it visible
		/* if($("#gridDataSaverDiv").css('display')=='none'){
			$("#gridDataSaverDiv").show();
		} */
	}

	function refreshList(){
		showAppropriationBillMotionList();
	}
	
	function memberAppropriationBillMotionsView() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&appropriationBillMotionType=" + $("#selectedAppropriationBillMotionType").val()
		+ "&createdBy=" + $("#ugparam").val()
		+ "&memberId=" + $("#loggedInMemberId").val()
		+"&locale="+$("#moduleLocale").val()
		+ "&report=MEMBER_APPROPRIATIONBILLMOTIONS_VIEW"
		+ "&reportout=member_appropriationbillmotions_view";
		showTabByIdAndUrl('details_tab','appropriationbillmotion/report/genreport?'+parameters);
	}
	
	function memberRepliedAppropriationBillMotionsView() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&appropriationBillMotionType=" + $("#selectedAppropriationBillMotionType").val()
		+ "&createdBy=" + $("#ugparam").val()
		+"&locale="+$("#moduleLocale").val()
		+ "&report=MEMBER_REPLIED_APPROPRIATIONBILLMOTIONS_VIEW"
		+ "&reportout=member_replied_appropriationbillmotions_view";
		showTabByIdAndUrl('details_tab','appropriationbillmotion/report/genreport?'+parameters);
	}
	
	function memberRepliedSupportedAppropriationBillMotionsView() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&appropriationBillMotionType=" + $("#selectedAppropriationBillMotionType").val()
		+ "&supportedBy=" + $("#ugparam").val()
		+"&locale="+$("#moduleLocale").val()
		+ "&report=MEMBER_REPLIED_SUPPORTED_APPROPRIATIONBILLMOTIONS_VIEW"
		+ "&reportout=member_replied_supported_appropriationbillmotions_view";
		showTabByIdAndUrl('details_tab','appropriationbillmotion/report/genreport?'+parameters);
	}
	
	function generateDepartmentwiseSubmittedCountsReport() {
		var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
		+ "/" + $("#selectedSessionYear").val()
		+ "/" + $("#selectedSessionType").val();
		
		$.get(url,function(data){
			if(data){				
				showTabByIdAndUrl("details_tab","appropriationbillmotion/report/genreport?"
						+"sessionId="+data.id
						+"&deviceTypeId="+$("#selectedAppropriationBillMotionType").val()
						+"&locale="+$("#moduleLocale").val()
						+"&report=ABMOIS_DEPARTMENTWISE_SUBMITTED_COUNTS"
						+"&reportout=departmentwise_submitted_counts_report");
			}
		});
	}
	
	function generateDepartmentwiseAdmittedCountsReport() {
		var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
		+ "/" + $("#selectedSessionYear").val()
		+ "/" + $("#selectedSessionType").val();
		
		$.get(url,function(data){
			if(data){				
				showTabByIdAndUrl("details_tab","appropriationbillmotion/report/genreport?"
						+"sessionId="+data.id
						+"&deviceTypeId="+$("#selectedAppropriationBillMotionType").val()
						+"&locale="+$("#moduleLocale").val()
						+"&report=ABMOIS_DEPARTMENTWISE_ADMITTED_COUNTS"
						+"&reportout=departmentwise_admitted_counts_report");
			}
		});
	}
	
	/**** new question ****/
	function newAppropriationBillMotion() {
		$("#cancelFn").val("newAppropriationBillMotion");
		$("#key").val("");
		$("#selectionDiv1").hide();
		showTabByIdAndUrl('details_tab', 'appropriationbillmotion/new?' + $("#gridURLParams_ForNew").val());
	}
	
	function editAppropriationBillMotion(row) {
		$("#cancelFn").val("editAppropriationBillMotion");

		row = $('#key').val();
		if (row == null || row == '') {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else {			
			showTabByIdAndUrl('details_tab', 'appropriationbillmotion/' + row + '/edit?' + $("#gridURLParams").val());
		}
	}
	
	/**** double clicking record in grid handler ****/
	function rowDblClickHandler(rowid, iRow, iCol, e) {
		$("#cancelFn").val("rowDblClickHandler");
		$('#key').val(rowid);
		showTabByIdAndUrl('details_tab', ',appropriationbillmotion/' + rowid + '/edit?' + $("#gridURLParams").val());
	}
	
	/**** delete appropriationbillmotion ****/
	function deleteAppropriationBillMotion() {
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
				$.prompt($('#confirmDeleteMessage').val() + row, {buttons : {Ok : true,Cancel : false},
					callback : function(v) {
						if (v) {
							$.delete_('appropriationbillmotion/' + row+ '/delete',null,function(data, textStatus, XMLHttpRequest) {
								showAppropriationBillMotionList();
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
	}
	
	/**** Search Facility ****/
	function searchInt(id){
		//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="searchfacility=yes&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val()+
			        "&houseType="+$("#selectedHouseType").val()+
			        "&sessionType="+$("#selectedSessionType").val()+
			        "&sessionYear="+$("#selectedSessionYear").val()+
			        "&deviceType="+$("#selectedAppropriationBillMotionType").val();
		showTabByIdAndUrl('search_tab','clubentity/init?'+params);
	}
	
	/**** reload grid ****/
	function reloadAppropriationBillMotionGrid() {				
		$("#gridURLParams").val(
				"houseType=" + $("#selectedHouseType").val() + "&sessionYear="
						+ $("#selectedSessionYear").val() + "&sessionType="
						+ $("#selectedSessionType").val() + "&appropriationBillMotionType="
						+ $("#selectedAppropriationBillMotionType").val() + "&ugparam="
						+ $("#ugparam").val() + "&status="
						+ $("#selectedStatus").val() + "&role="
						+ $("#srole").val() + "&usergroup="
						+ $("#currentusergroup").val() + "&usergroupType="
						+ $("#currentusergroupType").val() +"&subDepartment="
						+ $("#selectedSubDepartment").val() + "&status="
						+ $("#selectedStatus").val());
		var oldURL = $("#grid").getGridParam("url");
		var baseURL = oldURL.split("?")[0];
		newURL = baseURL + "?" + $("#gridURLParams").val();
		
		$("#grid").setGridParam({"url" : newURL});
		$("#grid").trigger("reloadGrid");
		
		loadSession();
	}
	
	/**** Bulk putup(Member)****/
	function bulkPutup() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&motionType=" + $("#selectedAppropriationBillMotionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();

		}
		var resourceURL = 'appropriationbillmotion/bulksubmission?' + parameters + "&itemscount=" + $("#selectedItemsCount").val();
		
		showTabByIdAndUrl('bulkputup_tab', resourceURL);
	}
	/**** Bulk putup(Assistant)****/
	function bulkPutupAssistant() {
		
		var parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&appropriationBillMotionType=" + $("#selectedAppropriationBillMotionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&file=" + $("#selectedFileCount").val() + "&group="
					+ $("#ugparam").val();

		var resourceURL = 'appropriationbillmotion/bulksubmission/assistant/int?' + parameters + "&itemscount=" + $("#selectedItemsCount").val();
	
		showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);
	}
	
	function loadSubDepartments(init){
		
		var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
				
		$.get(url, function(data){
			$.get('ref/getDepartment?session=' + data.id + '&group=0&userGroup='+$('#currentusergroup').val()
					+'&deviceType='+$("#selectedAppropriationBillMotionType").val()+'&houseType='+$("#selectedHouseType").val()
					+'&usergroupType='+$("#currentusergroupType").val(),function(data){
				
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
					showAppropriationBillMotionList();
				}
			});
		});
	}
	
	function assignNumberAfterApproval(){
		if($("#selectedSubDepartment").val()==undefined || $("#selectedSubDepartment").val()=='') {
			$.prompt("Please select department for assigning the numbers after approval!");
			return false;
		}
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&appropriationBillMotionType=" + $("#selectedAppropriationBillMotionType").val()
		+ "&subDepartment=" + $("#selectedSubDepartment").val();

		var resourceURL = 'appropriationbillmotion/assignnumberafterapproval?' + parameters;
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get(resourceURL, function(data){
			$.unblockUI();
			if(data=='failure'){
				$.prompt("<p style='color: red;'>Error Occured</p>");
			} else {
				$.prompt("Internal Numbers assigned successfully!");
			}			
		}).fail(function(){
			$.unblockUI();
		});

	}
	
	function showCurrentStatusReport(val, moId){
		$("#selectionDiv1").hide();
		var device = $("#deviceTypeMaster option[value='"+$("#selectedAppropriationBillMotionType").val()+"']").text().split("_")[0];
		showTabByIdAndUrl('details_tab', "appropriationbillmotion/report/currentstatusreport?device="+ device +"&reportType="+val+"&moId="+moId);
	}
	
	function generateYaadiReport(){
		if($("#selectedSubDepartment").val()==undefined || $("#selectedSubDepartment").val()=='') {
			$.prompt("Please select department for the yaadi report!");
			return false;
		}
		var parameters = {
				houseType				: $("#selectedHouseType").val(),
				sessionYear				: $('#selectedSessionYear').val(), 
				sessionType				: $("#selectedSessionType").val(), 
				sessionId				: $("#loadedSession").val(),
				subDepartment			: $("#selectedSubDepartment").val(),
				appropriationBillMotionType			: $("#selectedAppropriationBillMotionType").val(),
				locale					: $("#moduleLocale").val(),
				reportQuery				: "ABMOIS_YAADI_REPORT"/* + "_" + $("#selectedHouseType").val().toUpperCase()*/,
				xsltFileName			: 'abmois_yaadi_report_template'/* + '_' + $("#selectedHouseType").val()*/,
				outputFormat			: 'WORD',
				reportFileName			: "abmois_yaadi_report"/* + "_" + $("#selectedAppropriationBillMotionType").val()*/
		}
		form_submit('appropriationbillmotion/report/yaadi_report', parameters, 'GET');
	}
	
	/**** To Generate Reminder Letter ****/
	function generateReminderLetter(isRequiredToSend) {
		//var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedAppropriationBillMotionType").val() + "']").text();
		if($("#selectedSubDepartment").val()==undefined 
				|| $("#selectedSubDepartment").val()=='' 
				|| $("#selectedSubDepartment").val()=='0') {
			$.prompt('Please select a department for reminder letter of concerned unstarred questions!');
			return false;
		}
		var selectedAppropriationBillMotionIds = '';
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get('ref/device/findDevicesForReminderOfReply?'
				+ 'houseType=' + $('#selectedHouseType').val()
				+ '&sessionYear=' + $('#selectedSessionYear').val()
				+ '&sessionType=' + $('#selectedSessionType').val()
				+ '&deviceType=' + $('#selectedAppropriationBillMotionType').val()
				+ '&department=' + $('#selectedSubDepartment').val(),function(data){
			$.unblockUI();
			selectedAppropriationBillMotionIds = data;
		}).done(function(){
			if(selectedAppropriationBillMotionIds!=undefined && selectedAppropriationBillMotionIds.length>=1) {
				var outputFormat = 'WORD';
				if($('#currentusergroupType').val()=='department' || $('#currentusergroupType').val()=='department_deskofficer') {
					outputFormat = 'PDF';
				}
				form_submit(
						'appropriationbillmotion/report/generateReminderLetter', 
						{
							appropriationbillmotionIds: selectedAppropriationBillMotionIds,
							houseType: $('#selectedHouseType').val(),  
							//sessionYear: $('#selectedSessionYear').val(),  
							//sessionType: $('#selectedSessionType').val(), 
							usergroupType: $("#currentusergroupType").val(),
							locale: $('#moduleLocale').val(), 
							reportQuery: 'ABMOIS_REMINDER_LETTER', 
							outputFormat: outputFormat,
							isDepartmentLogin: $("#isDepartmentLogin").val(),
							isRequiredToSend: isRequiredToSend
						}, 
						'GET'
				);
			} else {
				$.prompt('No appropriationbillmotions found to be reminded for reply currently!');
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
</script>
<style type="text/css">
	.butSim:link{
		padding: 5px !important;
	}
</style>
</head>
<body>
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li><a id="list_tab" class="selected tab" href="#"> <spring:message code="generic.list" text="List"/></a></li>
			<li><a id="details_tab" href="#" class="tab"> <spring:message code="generic.details" text="Details" /></a></li>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<li><a id="bulkputup_tab" href="#" class="tab"> <spring:message code="generic.bulkputup" text="Bulk Putup" /></a></li>
			</security:authorize>
			<security:authorize access="hasAnyRole('ABMOIS_ASSISTANT')">
				<li>
					<a id="bulkputupassistant_tab" href="#" class="tab"> <spring:message code="generic.bulkputup" text="Bulk Putup" /></a>
				</li>
			</security:authorize>		
			<security:authorize access="!hasAnyRole('ABMOIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<li>
					<a id="search_tab" href="#" class="tab"><spring:message code="question.searchT" text="Search"></spring:message></a>
				</li>
			</security:authorize>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">

			<a href="#" id="select_houseType" class="butSim"> <spring:message code="generic.houseType" text="House Type" /></a> 
			<select name="selectedHouseType" id="selectedHouseType" style="width: 100px; height: 25px;">
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
			</select> | 
			<a href="#" id="select_session_year" class="butSim"> <spring:message code="generic.year" text="Year" /></a> 
			<select name="selectedSessionYear" id="selectedSessionYear" style="width: 100px; height: 25px;">
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
			</select> | 
			<a href="#" id="select_sessionType" class="butSim"> <spring:message code="generic.sessionType" text="Session Type" /></a> 
			<select name="selectedSessionType" id="selectedSessionType" style="width: 100px; height: 25px;">
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
				<a href="#" id="select_appropriationBillMotionType" class="butSim"> <spring:message code="appropriationbillmotion.appropriationbillmotionType" text="Cut Motion Type" /></a>
			<select name="selectedAppropriationBillMotionType" id="selectedAppropriationBillMotionType" style="width: 100px; height: 25px;">
				<c:forEach items="${motionTypes}" var="i">
					<c:choose>
						<c:when test="${motionType==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}" /></option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display: none;">
				<c:forEach items="${motionTypes}" var="i">
					<option value="${i.id }">${i.type }</option>
				</c:forEach>
			</select>|
			<security:authorize access="hasAnyRole('ABMOIS_TYPIST','ABMOIS_ADMIN','ABMOIS_ASSISTANT','ABMOIS_UNDER_SECRETARY',
			'ABMOIS_DEPUTY_SECRETARY','ABMOIS_PRINCIPAL_SECRETARY','ABMOIS_SPEAKER','ABMOIS_JOINT_SECRETARY',
			'ABMOIS_SECRETARY','ABMOIS_OFFICER_ON_SPECIAL_DUTY','ABMOIS_DEPUTY_SPEAKER','ABMOIS_CHAIRMAN','ABMOIS_DEPUTY_CHAIRMAN',
			'ABMOIS_SECTION_OFFICER','ABMOIS_UNDER_SECRETARY_COMMITTEE','ABMOIS_ADDITIONAL_SECRETARY','ABMOIS_LEADER_OF_OPPOSITION')">
				<hr>
				<a href="#" id="select_status" class="butSim"> <spring:message code="generic.status" text="Status" /></a>
				<select name="selectedStatus" id="selectedStatus" style="width: 250px; height: 25px;">
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select> |			 
			</security:authorize>

			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ABMOIS_CLERK')">
				<a href="#" id="select_status" class="butSim"> <spring:message code="generic.status" text="Status" /></a>
				<select name="selectedStatus" id="selectedStatus" style="width: 100px; height: 25px;">
					<c:forEach items="${status}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select>
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
					<hr>
					<%-- <a href="#" id="select_itemcount" class="butSim"> 
						<spring:message code="appropriationbillmotion.itemcount" text="No. of Motions(Bulk Putup)" />
					</a>
					<select name="selectedItemsCount" id="selectedItemsCount" style="width: 100px; height: 25px;">
						<option value="30">30</option>
						<option value="25">25</option>
						<option value="20">20</option>
						<option value="15">15</option>
						<option value="10">10</option>
						<option value="5">05</option>
					</select>|	 --%>
					</security:authorize>
			</security:authorize>
			<security:authorize access="hasAnyRole('ABMOIS_ASSISTANT')">
				<a href="#" id="select_itemcount" class="butSim"> <spring:message code="appropriationbillmotion.itemcount" text="No. of Motions(Bulk Putup)" /></a>
				<select name="selectedItemsCount" id="selectedItemsCount"
					style="width: 100px; height: 25px;">
					<option value="100">100</option>
					<option value="75">75</option>
					<option value="50">50</option>
					<option value="25">25</option>
					<option value="10">10</option>
					<option value="5">05</option>
				</select>|	
				<a href="#" id="select_filecount" class="butSim"> <spring:message code="appropriationbillmotion.filecount" text="Select File(Bulk Putup)" /></a>
				<select name="selectedFileCount" id="selectedFileCount" style="width: 100px; height: 25px;">
					<option value="-"><spring:message code='please.select' text='Please Select' /></option>
					<c:if test="${highestFileNo>0 }">
						<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
							<option value="${i}">${i}</option>
						</c:forEach>
					</c:if>
				</select>|	
				<hr>				
			</security:authorize>
			<div id='appropriationBillMotionDepartment' style="display:inline;">
				<a href="#" id="select_department" class="butSim"> <spring:message
						code="appropriationbillmotion.department" text="Department" />
				</a>
				<select name="selectedSubDepartment" id="selectedSubDepartment"
					style="width: 200px; height: 25px;">
					<option value="0" selected="selected"><spring:message code="please.select"></spring:message></option>
					<c:forEach items="${subDepartments}" var="i">
						<option value="${i.id}">
							<c:out value="${i.name}"></c:out>
						</option>
					</c:forEach>
				</select>|
			</div>
			<hr>
		</div>		
			
		<div class="tabContent"></div>
		
		<input type="hidden" id="key" name="key" /> 
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }" /> 
		<input type="hidden" name="srole" id="srole" value="${role }" /> 
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}" /> 
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}" /> 
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${appropriationBillMotionTypeType}" /> 
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}" />

		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" />
		<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>" />
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>" />
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>" />
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>" />
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled" /> 
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled" /> 
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }" /> 
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="chartAnsweringDate" name="chartAnsweringDate" value="-"> 
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>" />
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" id="loadedSession" value="" />
	</div>
</body>
</html>