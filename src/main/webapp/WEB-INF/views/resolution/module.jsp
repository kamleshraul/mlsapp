<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="resolution.list" text="List Of Resolutions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
		
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				showResolutionList();
			});	
			$('#chart_tab').click(function(){
				$("#selectionDiv1").hide();								
				viewChart();
			});			
			showResolutionList();	
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					loadSession();
					loadStatus();
					reloadResolutionGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					loadSession();
					loadStatus();
					reloadResolutionGrid();							
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){		
					loadSession();
					loadStatus();
					reloadResolutionGrid();						
				}			
			});
			/**** device type changes then reload grid****/			
			$("#selectedDeviceType").change(function(){
				var value = $(this).val();
				if(value != ""){
					loadStatus();
					showResolutionList();
				}
			});
			
			$("#selectedStatus").change(function(){
				var value=$(this).val();				
				if(value!=""){				
				reloadResolutionGrid();
				}
			});
			
			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutup();
			});
			
			/**** Bulk Putup Assistant****/
			$("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			});
			
			/**** Ballot Tab ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
			});	
			
			/**** Member Ballot Tab ****/
			$('#memberballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewMemberBallot();
			});
			
			/**** Patrak Bhag Two ****/
			$("#patrakbhag2_tab").click(function(){
				$("#selectionDiv1").hide();
				viewPatrakBhag2();
			});
			
			$("#selectedSubdepartment").change(function(){
				var value = $(this).val();
				if(value != ""){
					//loadStatus($(this).val());
					showResolutionList();
				}
			});
			
			/**** Search ****/
			$('#search_tab').click(function() {
				$("#selectionDiv1").hide();
				searchInt();
			});
			
			/**** Toggle Reports Div ****/
			$("#reports_link").click(function(e){
				$("#assistantReportDiv").toggle("slow");
			});
			$("#members").change(function(){
				var val = $(this).val();
				if(val!="" && val!='-'){
					memberWiseReport($(this).val());
				}
			});
			$("#department_report").click(function(e){
				var dept = $("#selectedSubDepartment").val();
				if(dept!="" && dept!='0'){
					departmentWiseReport(dept);	
				}
			});
		});
		
		/**** Load Session ****/
		function loadSession(){
			$.get("ref/sessionbyhousetype/" + $("#selectedHouseType").val()
				+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),
				function(data){
					if(data){
						$("#loadedSession").val(data.id);
						loadMembers();
						//loadParties();
					}
				});
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
		
		 function loadStatus(){
			var params = "deviceType=" + $("#selectedDeviceType").val() +
						"&houseType=" + $("#selectedHouseType").val() +
						"&sessionType=" + $("#selectedSessionType").val() +
						"&sessionYear=" + $("#selectedSessionYear").val() +
						"&usergroupType="+$("#currentusergroupType").val();
						
			$.get('ref/requiredStatus?'+ params,function(data){
				$("#selectedStatus").empty();
				if(data.length>0){
				var selectedStatusText="<option value='0' selected='selected'>----"+$("#pleaseSelect").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					selectedStatusText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#selectedStatus").html(selectedStatusText);			
				}else{
					$("#selectedStatus").empty();
					var selectedStatusText="<option value='0' selected='selected'>----"+$("#pleaseSelect").val()+"----</option>";				
					$("#selectedStatus").html(selectedStatusText);				
				}
			});
		} 

		/**** displaying grid ****/					
		function showResolutionList() {
			showTabByIdAndUrl('list_tab','resolution/list?houseType='+$("#selectedHouseType").val()
					+'&deviceType='+$("#selectedDeviceType").val()
					+'&sessionYear='+$("#selectedSessionYear").val()
					+'&sessionType='+$("#selectedSessionType").val()
					+'&ugparam='+$("#ugparam").val()
					+'&status='+$("#selectedStatus").val()
					+'&role='+$("#srole").val()
					+'&usergroup='+$("#currentusergroup").val()
					+'&usergroupType='+$("#currentusergroupType").val()
					+'&subDepartment='+$("#selectedSubdepartment").val()
				);	
			loadSession();
		}
		
		function memberResolutionsView() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&resolutionType=" + $("#selectedDeviceType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_RESOLUTIONS_VIEW"
			+ "&reportout=member_resolutions_view";
			showTabByIdAndUrl('details_tab','resolution/report/generalreport?'+parameters);
		}
		
		function newResolution() {
			$("#cancelFn").val("newResolution");
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','resolution/new?'+$("#gridURLParams").val());
		}
			
		function editResolution(row) {
			$("#cancelFn").val("editResolution");
			row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','resolution/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
				
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'resolution/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
			
		function deleteResolution() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('resolution/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showResolutionList();
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
		/**** reload grid ****/
		function reloadResolutionGrid(){
			var deviceType = "";
			$.ajax({url: 'ref/getTypeOfSelectedDeviceType?deviceTypeId='+ $("#selectedDeviceType").val(), async: false, success : function(data){	
				deviceType = data;				
			}}).done(function(){
				if(deviceType == 'resolutions_government'&& $("#currentusergroupType").val()!='member' ) {					
					$("#gridURLParams").val("deviceType="+$("#selectedDeviceType").val()
							    +"&sessionYear="+$("#selectedSessionYear").val()
								+"&sessionType="+$("#selectedSessionType").val()								
								+"&ugparam="+$("#ugparam").val()
								+"&status="+$("#selectedStatus").val()
								+"&role="+$("#srole").val()
								+"&usergroup="+$("#currentusergroup").val()
								+"&usergroupType="+$("#currentusergroupType").val()
								+'&subDepartment='+$("#selectedSubdepartment").val()
								);
					console.log("reloaded govt params: " + $("#gridURLParams").val());
				} else {
					
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
							    +"&deviceType="+$("#selectedDeviceType").val()
								+"&sessionYear="+$("#selectedSessionYear").val()
								+"&sessionType="+$("#selectedSessionType").val()								
								+"&ugparam="+$("#ugparam").val()
								+"&status="+$("#selectedStatus").val()
								+"&role="+$("#srole").val()
								+"&usergroup="+$("#currentusergroup").val()
								+"&usergroupType="+$("#currentusergroupType").val()
								+'&subDepartment='+$("#selectedSubdepartment").val()
								);
					console.log("reloaded nonofficial params: " + $("#gridURLParams").val());
				}				
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];				
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});											
		}
		/**** Chart Tab ****/
 		function viewChart() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			var resourceURL = 'chart/init?' + parameters;
			showTabByIdAndUrl('chart_tab', resourceURL);
		}
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val();
			}
			var parameters = parameters + "&category=resolution";
			var resourceURL = 'ballot/init?' + parameters;
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}
		
		
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'resolution/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}
		
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='resolution/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}
		
		/**** To Generate Online-Offline Submission Count Report ****/
		function generateOnlineOfflineSubmissionCountReport(){
			var parameters = "houseType=" + $("#selectedHouseType").val()
			 + "&sessionYear=" + $("#selectedSessionYear").val()
			 + "&sessionType=" + $("#selectedSessionType").val()
			 + "&deviceType=" + $("#selectedDeviceType").val()
			 + "&role=" + $("#srole").val();		 	
			var resourceURL = 'resolution/report/online_offline_submission_count_report/init?'+ parameters;
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
		
		/**** To Generate Intimation Letter ****/
		function generateIntimationLetter() {			
			var selectedResolutionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedResolutionId.length<1) {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else if(selectedResolutionId.length>1) {
				$.prompt("Please select only one question!");
				return false;
			} else {			
				$('#generateIntimationLetter').attr('href', 
						'resolution/report/generateIntimationLetter?'
								+'resolutionId=' + selectedResolutionId
								+ '&intimationLetterFilter=' + $("#intimationLetterFilter").val());
			}		
		}
		
		function showCurrentStatusReport(val,rId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"
			                                         +$("#selectedDeviceType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', 
					"resolution/report/currentstatusreport?device="+ device +"&reportType="+val+"&rId="+rId);
		}
		
		function generateResolutionSummaryReport(roId){
			$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val() +
					'/' + $("#selectedSessionYear").val() + 
					'/' + $("#selectedSessionType").val(),function(data){
				
				if(data){
					
					var url = "resolution/report/generalreport?sessionId=" + data.id
					+ "&deviceTypeId=" + $("#selectedDeviceType").val()
					+ "&locale=" + $("#moduleLocale").val()
					+ "&statusId=" + $("#selectedStatus").val() 
					+ "&subdepartmentId=" + $("#selectedSubdepartment").val()
					+ "&reportout=resolution_summaryreport"
					+ "&roId="+roId
					+ "&fromDate=" + ($("#sumRepFromDate").val()==''?'0':$("#sumRepFromDate").val())
					+ "&toDate=" + ($("#sumRepToDate").val()==''?'0':$("#sumRepToDate").val())
					+ "&report=RESOLUTION_SUMMARY_REPORT";
					
					if(roId.length>0) {
						url += "&havingIN=TRUE";
					} else {
						url += "&havingIN=FALSE";
					}
					
					showTabByIdAndUrl('details_tab', url);
				}
			});
		}
		
		function generateResolutionRegister(){
			$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val() +
					'/' + $("#selectedSessionYear").val() + 
					'/' + $("#selectedSessionType").val(),function(data){
				
				if(data){
					
					var url = "resolution/report/generalreport?sessionId=" + data.id
					+ "&deviceTypeId=" + $("#selectedDeviceType").val()
					+ "&locale=" + $("#moduleLocale").val()
					+ "&reportout=resolution_registerreport"
					+ "&report=RESOLUTION_REGISTER_REPORT";
					
					showTabByIdAndUrl('details_tab', url);
				}
			});
		}
		
		/**** Search Facility ****/
		function searchInt(){
			//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="searchfacility=yes&usergroup="+$("#currentusergroup").val()
				        +"&usergroupType="+$("#currentusergroupType").val()+
				        "&houseType="+$("#selectedHouseType").val()+
				        "&sessionType="+$("#selectedSessionType").val()+
				        "&sessionYear="+$("#selectedSessionYear").val()+
				        "&deviceType="+$("#selectedDeviceType").val();		
			showTabByIdAndUrl('search_tab','devicesearch/init?'+params);
		}
		
		/**** Memberwise Devices Report Generation ****/
		function memberWiseReport(memberId){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","resolution/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&memberId="+memberId 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=ROIS_MEMBER_WISE_REPORT_"+$('#selectedHouseType').val().toUpperCase()+"&reportout=resolutionMemberReport");
				}
			});
		}
		/**** Departmentwise Devices Report Generation ****/
		function departmentWiseReport(dept){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","adjournmentmotion/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&subd="+dept 
							+"&locale="+$("#moduleLocale").val()
							+"&report=ROIS_DEPARTMENT_WISE_REPORT&reportout=resolutionDepartmentReport");
				}
			});
		}

	</script>
</head>
<body>
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
			<c:if test="${deviceTypeType == 'resolutions_nonofficial'}">
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT', 'ROIS_UNDERSECRETARY',
				'ROIS_DEPUTY_SECRETARY', 'ROIS_PRINCIPALSECRETARY', 'ROIS_JOINT_SECRETARY',
				'ROIS_SECRETARY', 'ROIS_OFFICER_ON_SPECIAL_DUTY','ROIS_SPEAKER','ROIS_CHAIRMAN', 'ROIS_SECTION_OFFICER', 
				'ROIS_UNDER_SECRETARY_COMMITTEE','SUPER_ADMIN','ROIS_CLERK')">
			<li>
				<a id="chart_tab" href="#" class="tab">
				   <spring:message code="resolution.chart" text="Chart"></spring:message>
				</a>
			</li>
			</security:authorize>
			</c:if>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			
			
			
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT', 'ROIS_UNDERSECRETARY',
				'ROIS_DEPUTYSECRETARY', 'ROIS_PRINCIPALSECRETARY', 'ROIS_SPEAKER', 'ROIS_JOINT_SECRETARY',
				'ROIS_SECRETARY', 'ROIS_OFFICER_ON_SPECIAL_DUTY', 'ROIS_DEPUTY_SPEAKER', 'ROIS_CHAIRMAN',
				'ROIS_DEPUTY_CHAIRMAN', 'ROIS_SECTION_OFFICER', 'ROIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<li>
					<a id="ballot_tab" href="#" class="tab">
				   		<spring:message code="resolution.ballot" text="Ballot"></spring:message>
					</a>
					</li>
			</security:authorize>
			
			<security:authorize
				access="!hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_TYPIST')">
				<li><a id="search_tab" href="#" class="tab"> <spring:message
							code="generic.resolution.search" text="Search">
						</spring:message>
				</a></li>
			</security:authorize>
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="resolution.houseType" text="House Type"/>
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
				<spring:message code="resolution.sessionyear" text="Year"/>
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
				<spring:message code="resolution.sessionType" text="Session Type"/>
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
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="resolution.deviceType" text="Resolution Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:100px;height: 25px;">			
				<c:forEach items="${deviceTypes}" var="i">
					<c:choose>
						<c:when test="${deviceType==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>
			</select>|			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_CLERK','ROIS_ASSISTANT','ROIS_UNDERSECRETARY',
			'ROIS_DEPUTYSECRETARY','ROIS_PRINCIPALSECRETARY','ROIS_SPEAKER','ROIS_JOINT_SECRETARY',
			'ROIS_SECRETARY','ROIS_OFFICER_ON_SPECIAL_DUTY','ROIS_DEPUTY_SPEAKER','ROIS_CHAIRMAN','ROIS_DEPUTY_CHAIRMAN',
			'ROIS_SECTION_OFFICER','ROIS_UNDERSECRETARY_COMMITTEE','ROIS_TYPIST')">
			<hr>
			<a href="#" id="select_status" class="butSim">
				<spring:message code="resolution.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:250px;height: 25px;">			
				<option value="0">--<spring:message code="please.select" text="Please Select" />--</option>
				<c:forEach items="${status}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
				</c:forEach>
			</select> |			 
			</security:authorize>
			
			<security:authorize access="hasAnyRole('ROIS_CLERK','ROIS_ASSISTANT','ROIS_UNDERSECRETARY',
			'ROIS_DEPUTYSECRETARY','ROIS_PRINCIPALSECRETARY','ROIS_SPEAKER','ROIS_JOINT_SECRETARY',
			'ROIS_SECRETARY','ROIS_OFFICER_ON_SPECIAL_DUTY','ROIS_DEPUTY_SPEAKER','ROIS_CHAIRMAN','ROIS_DEPUTY_CHAIRMAN',
			'ROIS_SECTION_OFFICER','ROIS_UNDERSECRETARY_COMMITTEE')">
			<a href="#" id="select_subdepartment" class="butSim">
				<spring:message code="resolution.subdepartment" text="Department"/>
			</a>
			<select name="selectedSubdepartment" id="selectedSubdepartment" style="width:250px;height: 25px;">			
				<option value="0">--<spring:message code="please.select" text="Please Select" />--</option>
				<c:forEach items="${subdepartments}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
				</c:forEach>
			</select> |			 
			</security:authorize>
				
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="resolution.itemcount" text="No. of Resolution(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
				<option value="30">30</option>
				<option value="25">25</option>
				<option value="20">20</option>
				<option value="15">15</option>
				<option value="10">10</option>
				<option value="5">05</option>		
			</select>|	
			</security:authorize>
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT')">
			<hr>
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="resolution.itemcount" text="No. of Resolution(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
				<option value="30">30</option>
				<option value="25">25</option>
				<option value="20">20</option>
				<option value="15">15</option>
				<option value="10">10</option>
				<option value="5">05</option>		
			</select>|	
			<a href="#" id="select_filecount" class="butSim">
				<spring:message code="resolution.filecount" text="Select File(Bulk Putup)"/>
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
			
			<security:authorize access="hasAnyRole('ROIS_CLERK', 'ROIS_ASSISTANT', 'ROIS_SECTION_OFFICER', 'ROIS_PRINCIPAL_SECRETARY')">					
				<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
					<spring:message code="adjournmentmotion.reports" text="Reports"/>
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
					<%-- <a href="javascript:void(0);" id="party_report" class="butSim" >
						<spring:message code="generic.partyWiseReport" text="Party-wise Report"/>
					</a>						
					<select id="parties" class="sSelect" style="display: inline; width:100px;">
					</select>|<br> --%>
					<hr>
				</div>
			</security:authorize>
		</div>		
				
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">		
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam}">
		<input type="hidden" name="srole" id="srole" value="${role}">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">
		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<input type="hidden" id="loadedSession" value="" />
		<%--<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>">			
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>">			
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>">			
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>">			
		
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }"> 
		<input type="hidden" id="chartAnsweringDate" name="chartAnsweringDate" value="-"> --%>
		</div> 		
</body>
</html>