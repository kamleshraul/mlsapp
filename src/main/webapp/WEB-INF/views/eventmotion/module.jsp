<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="eventmotion.list" text="List Of Event Motions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		/**** On Page Load ****/
		/* var currentDeviceType = $("#currentDeviceType").val();
		var currentHouseType = $("#currentHouseType").val(); */
		
		$("#new_record_saver").click(function(){
			newEventMotion();
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
			showEventMotionList();
		});
		/**** house type changes then reload grid****/
		$("#selectedHouseType").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadEventMotionGrid();	
			}
		});
		/**** session year changes then reload grid****/
		$("#selectedSessionYear").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadEventMotionGrid();
			}
		});
		/**** session type changes then reload grid****/
		$("#selectedSessionType").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadEventMotionGrid();	
			}
		});
		/**** question type changes then reload grid****/
		$("#selectedEventMotionType").change(function() {
				var value = $(this).val();
				var text = $("#deviceTypeMaster option[value='" + value + "']").text();
				
				if (value != "") {
					reloadEventMotionGrid();
				}

		});
		
		/**** Department change ****/
		$("#selectedSubDepartment").change(function(){
			reloadEventMotionGrid();
			
		});
		
		/**** status changes then reload grid****/
		$("#selectedStatus").change(function() {
			var value = $(this).val();
			if (value != "") {
				reloadEventMotionGrid();
			}
		});
		
		/**** Bulk Putup ****/
		$("#bulkputup_tab").click(function() {
			bulkPutup();
		});
		/**** Bulk Putup ****/
		$("#bulkputupassistant_tab").click(function() {
			bulkPutupAssistant();
		});
	
		/**** show eventmotion list method is called by default.****/
		showEventMotionList();
		
	});
	
	/**** displaying grid ****/
	function showEventMotionList() {
		$("#key").val('');
		
		showTabByIdAndUrl('list_tab', 'eventmotion/list?houseType='
				+ $('#selectedHouseType').val() + '&eventMotionType='
				+ $("#selectedEventMotionType").val() + '&sessionYear='
				+ $("#selectedSessionYear").val() + '&sessionType='
				+ $("#selectedSessionType").val() + "&ugparam="
				+ $("#ugparam").val() + "&status=" + $("#selectedStatus").val()
				+ "&role=" + $("#srole").val() + "&usergroup="
				+ $("#currentusergroup").val() + "&usergroupType="
				+ $("#currentusergroupType").val()
				+ "&subDepartment="+$("#selectedSubDepartment").val()
				+ "&status="+$("#selectedStatus").val());
		
		//make grid visible again with refreshed data
		//if gridDiv is hidden make it visible
		/* if($("#gridDataSaverDiv").css('display')=='none'){
			$("#gridDataSaverDiv").show();
		} */
	}

	function refreshList(){
		showEventMotionList();
	}
	/**** new motion ****/
	function newEventMotion() {
		$("#cancelFn").val("newEventMotion");
		$("#key").val("");
		$("#selectionDiv1").hide();
		showTabByIdAndUrl('details_tab', 'eventmotion/new?' + $("#gridURLParams_ForNew").val());
	}
	
	function editEventMotion(row) {
		$("#cancelFn").val("editEventtMotion");

		row = $('#key').val();
		if (row == null || row == '') {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else {			
			showTabByIdAndUrl('details_tab', 'eventmotion/' + row + '/edit?' + $("#gridURLParams").val());
		}
	}
	
	/**** double clicking record in grid handler ****/
	function rowDblClickHandler(rowid, iRow, iCol, e) {
		$("#cancelFn").val("rowDblClickHandler");
		$('#key').val(rowid);
		showTabByIdAndUrl('details_tab', ',eventmotion/' + rowid + '/edit?' + $("#gridURLParams").val());
	}
	/**** delete motion ****/
	function deleteEventMotion() {
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
							$.delete_('eventmotion/' + row+ '/delete',null,function(data, textStatus, XMLHttpRequest) {
								showCutMotionList();
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
	/**** reload grid ****/
	function reloadEventMotionGrid() {
				
		$("#gridURLParams").val(
				"houseType=" + $("#selectedHouseType").val() + "&sessionYear="
						+ $("#selectedSessionYear").val() + "&sessionType="
						+ $("#selectedSessionType").val() + "&eventMotionType="
						+ $("#selectedEventMotionType").val() + "&ugparam="
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
	}
	
	/**** Bulk putup(Member)****/
	function bulkPutup() {
		var parameters = $("#gridURLParams").val();
		if (parameters == undefined) {
			parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&motionType=" + $("#selectedEventMotionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val();

		}
		var resourceURL = 'eventmotion/bulksubmission?' + parameters + "&itemscount=" + $("#selectedItemsCount").val();
		
		showTabByIdAndUrl('bulkputup_tab', resourceURL);
	}
	/**** Bulk putup(Assistant)****/
	function bulkPutupAssistant() {
		var parameters = "houseType=" + $("#selectedHouseType").val()
					+ "&sessionYear=" + $("#selectedSessionYear").val()
					+ "&sessionType=" + $("#selectedSessionType").val()
					+ "&eventMotionType=" + $("#selectedEventMotionType").val()
					+ "&ugparam=" + $("#ugparam").val() + "&status="
					+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
					+ "&usergroup=" + $("#currentusergroup").val()
					+ "&usergroupType=" + $("#currentusergroupType").val()
					+ "&file=" + $("#selectedFileCount").val();

		var resourceURL = 'eventmotion/bulksubmission/assistant/int?' + parameters + "&itemscount=" + $("#selectedItemsCount").val();
	
		showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);
	}
	
	function loadSubDepartments(init){
		$.get('ref/getDepartment?userGroup='+$('#currentusergroup').val()
				+'&deviceType='+$("#selectedEventMotionType").val()
				+'&houseType='+$("#selectedHouseType").val(),function(data){
			
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
				reloadCutMotionGrid();
			}else if(init=='yes'){
				showCutMotionList();
			}
		});
	}
	
	function assignNumberAfterApproval(){
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&eventMotionType=" + $("#selectedEventMotionType").val()
		+ "&subDepartment=" + $("#selectedSubDepartment").val();

		var resourceURL = 'cutmotion/assignnumberafterapproval?' + parameters;
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get(resourceURL, function(data){
			
			if(data=='failure'){
				$.prompt("<p style='color: red;'>Error Occured</p>");
			}
			$.unblockUI();
		}).fail(function(){
			$.unblockUI();
		});

	}
	
	function showEventGenReport(){
		var url = "motion/report/eventmotion/genreport?"
		+ "reportout=evgenreport&report=EMOIS_GEN_REPORT"
		+ "&userName="+$("#authusername").val();
		
		var urlSession = "ref/sessionbyhousetype/"
			+ $("#selectedHouseType").val() + "/" 
			+ $("#selectedSessionYear").val() + "/"
			+ $("#selectedSessionType").val();
		$.get(urlSession, function(data){
			if(data){
				$("#selectionDiv").hide();
				$("#selectionDiv1").hide();
				url += '&sessionId=' + data.id + '&locale=' + $("#moduleLocale").val()+'&deviceTypeId='+$("#selectedEventMotionType").val();
				showTabByIdAndUrl('details_tab', url);
			}
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
			<security:authorize access="hasAnyRole('EMOIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<li><a id="bulkputup_tab" href="#" class="tab"> <spring:message code="generic.bulkputup" text="Bulk Putup" /></a></li>
			</security:authorize>
			<security:authorize access="hasAnyRole('EMOIS_ASSISTANT')">
				<li>
					<a id="bulkputupassistant_tab" href="#" class="tab"> <spring:message code="generic.bulkputup" text="Bulk Putup" /></a>
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
				<a href="#" id="select_eventMotionType" class="butSim"> <spring:message code="cutmotion.cutmotionType" text="Cut Motion Type" /></a>
			<select name="selectedEventMotionType" id="selectedEventMotionType" style="width: 100px; height: 25px;">
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
			<security:authorize access="hasAnyRole('EMOIS_TYPIST','EMOIS_ADMIN','EMOIS_ASSISTANT','EMOIS_UNDER_SECRETARY',
			'EMOIS_DEPUTY_SECRETARY','EMOIS_PRINCIPAL_SECRETARY','EMOIS_SPEAKER','EMOIS_JOINT_SECRETARY',
			'EMOIS_SECRETARY','EMOIS_OFFICER_ON_SPECIAL_DUTY','EMOIS_DEPUTY_SPEAKER','EMOIS_CHAIRMAN','EMOIS_DEPUTY_CHAIRMAN',
			'EMOIS_SECTION_OFFICER','EMOIS_UNDER_SECRETARY_COMMITTEE','EMOIS_ADDITIONAL_SECRETARY','EMOIS_LEADER_OF_OPPOSITION')">
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

			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','EMOIS_CLERK')">
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
					<a href="#" id="select_itemcount" class="butSim"> 
						<spring:message code="cutmotion.itemcount" text="No. of Motions(Bulk Putup)" />
					</a>
					<select name="selectedItemsCount" id="selectedItemsCount" style="width: 100px; height: 25px;">
						<option value="30">30</option>
						<option value="25">25</option>
						<option value="20">20</option>
						<option value="15">15</option>
						<option value="10">10</option>
						<option value="5">05</option>
					</select>|	
					</security:authorize>
			</security:authorize>
			<security:authorize access="hasAnyRole('EMOIS_TYPIST','EMOIS_CLERK','EMOIS_ASSISTANT',)">
				<a href="#" id="select_itemcount" class="butSim"> <spring:message code="cutmotion.itemcount" text="No. of Motions(Bulk Putup)" /></a>
				<select name="selectedItemsCount" id="selectedItemsCount"
					style="width: 100px; height: 25px;">
					<option value="100">100</option>
					<option value="75">75</option>
					<option value="50">50</option>
					<option value="25">25</option>
					<option value="10">10</option>
					<option value="5">05</option>
				</select>
				<c:if test="${highestFileNo==0}">|	
					<a href="#" id="select_filecount" class="butSim"> <spring:message code="cutmotion.filecount" text="Select File(Bulk Putup)" /></a>
					<select name="selectedFileCount" id="selectedFileCount" style="width: 100px; height: 25px;">
						<option value="-"><spring:message code='please.select' text='Please Select' /></option>
						<c:if test="${highestFileNo>0 }">
							<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
								<option value="${i}">${i}</option>
							</c:forEach>
						</c:if>
					</select>|	
				</c:if>
			</security:authorize>
			<hr>
		</div>		
			
		<div class="tabContent"></div>
		
		<input type="hidden" id="key" name="key" /> 
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }" /> 
		<input type="hidden" name="srole" id="srole" value="${role }" /> 
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}" /> 
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}" /> 
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${cutMotionTypeType}" /> 
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
	</div>
</body>
</html>