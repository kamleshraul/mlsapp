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
		/**** show Message list method is called by default.****/
		showMessageList();
		
		
		/**** displaying grid ****/
		$('#list_tab').click(function() {
			$("#selectionDiv1").show();
			showMessageList();
		});
		
		$("#selectedSessionYear").change(function(){
			reloadMessageGrid();
		});
		
		$("#selectedSessionType").change(function(){
			reloadMessageGrid();
		});

		$("#selectedDeviceType").change(function(){
			reloadMessageGrid();
		});

		$("#selectedHouseType").change(function(){
			reloadMessageGrid();
		});
	});
		
	/**** displaying grid ****/
	function showMessageList() {
		showTabByIdAndUrl('list_tab', 'pushmessage/list?houseType='
				+ $('#selectedHouseType').val() + '&deviceType='
				+ $("#selectedDeviceType").val() + '&sessionYear='
				+ $("#selectedSessionYear").val() + '&sessionType='
				+ $("#selectedSessionType").val() + "&ugparam="
				+ "&role=" + $("#srole").val() + "&usergroup="
				+ $("#currentusergroup").val() + "&usergroupType="
				+ $("#currentusergroupType").val());
	}

	/**** new question ****/
	function newMessage() {
		$("#cancelFn").val("newMessage");
		//since id of question has not been created so key is set to empty value
		$("#key").val("");
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&deviceType=" + $("#selectedDeviceType").val()
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="pushmessage/new?"+parameters;
		showTabByIdAndUrl('details_tab', resourceUrl);
	}
	
	function editMessage(row) {
		$("#cancelFn").val("editMessage");
		row = $('#key').val();
		if (row == null || row == '') {
			$.prompt($('#selectRowFirstMessage').val());
			return false;
		} else {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&deviceType=" + $("#selectedDeviceType").val()
			+ "&ugparam=" + $("#ugparam").val() + "&status="
			+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
			+ "&usergroup=" + $("#currentusergroup").val()
			+ "&usergroupType=" + $("#currentusergroupType").val();
			var resourceUrl="question/" + row + "/edit?"+parameters;
			showTabByIdAndUrl('details_tab',resourceUrl);
		}
	}
	
	/**** double clicking record in grid handler ****/
	function rowDblClickHandler(rowid, iRow, iCol, e) {
		$("#cancelFn").val("rowDblClickHandler");
		$('#key').val(rowid);
		var parameters = "houseType=" + $("#selectedHouseType").val()
		+ "&sessionYear=" + $("#selectedSessionYear").val()
		+ "&sessionType=" + $("#selectedSessionType").val()
		+ "&deviceType=" + $("#selectedDeviceType").val()
		+ "&ugparam=" + $("#ugparam").val() + "&status="
		+ $("#selectedStatus").val() + "&role=" + $("#srole").val()
		+ "&usergroup=" + $("#currentusergroup").val()
		+ "&usergroupType=" + $("#currentusergroupType").val();
		var resourceUrl="pushmessage/" + row + "/edit?"+parameters;
		showTabByIdAndUrl('details_tab', resourceUrl);
	}
	/**** delete question ****/
	function deleteMessage() {
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
	function reloadMessageGrid() {
		$("#gridURLParams").val(
				"houseType=" + $("#selectedHouseType").val() + "&sessionYear="
						+ $("#selectedSessionYear").val() + "&sessionType="
						+ $("#selectedSessionType").val() + "&deviceType="
						+ $("#selectedDeviceType").val() + "&ugparam="
						+ $("#ugparam").val() + "&status="
						+ $("#selectedStatus").val() + "&role="
						+ $("#srole").val() + "&usergroup="
						+ $("#currentusergroup").val() + "&usergroupType="
						+ $("#currentusergroupType").val()+"&subDepartment="
						+ $("#selectedSubDepartment").val());
		var oldURL = $("#grid").getGridParam("url");
		console.log(oldURL);
		var baseURL = oldURL.split("?")[0];
		newURL = baseURL + "?" + $("#gridURLParams").val();
		$("#grid").setGridParam({
			"url" : newURL
		});
		$("#grid").trigger("reloadGrid");
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
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">

			<a href="#" id="select_houseType" class="butSim"> <spring:message
					code="message.houseType" text="House Type" />
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
					code="message.sessionyear" text="Year" />
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
					code="message.sessionType" text="Session Type" />
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
			<a href="#" id="select_deviceType" class="butSim"> 
				<spring:message code="message.deviceType" text="Device Type" />
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType"
				style="width: 100px; height: 25px;">
				<c:forEach items="${deviceTypes}" var="i">
					<c:choose>
						<c:when test="${deviceType==i.id}">
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
				<c:forEach items="${deviceTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>
			</select>|
			<hr>
		</div>

		<div class="tabContent"></div>

		<input type="hidden" id="key" name="key"> <input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceTypeType}">
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
	</div>
</body>
</html>