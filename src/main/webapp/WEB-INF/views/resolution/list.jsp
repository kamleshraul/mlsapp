<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="resolution.list" text="List Of Resolutions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$("#selectionDiv1").show();					
			/**** grid params which is sent to load grid data being sent ****/	
			var deviceType = "";
			$.ajax({url: 'ref/getTypeOfSelectedDeviceType?deviceTypeId='+ $("#selectedDeviceType").val(), async: false, success : function(data){	
				deviceType = data;
			}}).done(function(){
				if(deviceType == 'resolutions_government' && $("#currentusergroupType").val()!='member') {					
					$("#gridURLParams").val("deviceType="+$("#selectedDeviceType").val()
							    +"&sessionYear="+$("#selectedSessionYear").val()
								+"&sessionType="+$('#selectedSessionType').val()								
								+"&ugparam="+$('#ugparam').val()
								+"&status="+$('#selectedStatus').val()
								+"&role="+$('#srole').val()
								+"&usergroup="+$('#currentusergroup').val()
								+"&usergroupType="+$('#currentusergroupType').val()
								+'&subDepartment='+$("#selectedSubdepartment").val()
								);
					//console.log("govt params: " + $("#gridURLParams").val());	
					$('#karyavali_report').hide();
					$('#outputFormat').hide();
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
					//console.log("nonofficial params in else: " + $("#gridURLParams").val());
					if(deviceType == 'resolutions_nonofficial') {
						$('#karyavali_report').show();
						$('#outputFormat').show();
					} else {
						$('#karyavali_report').hide();
						$('#outputFormat').hide();
					}
				}
				$('#deviceTypeSelected').val(deviceType);
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			
			/*******For Enabling the new Resolution link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());	
			
			/**** new resolution ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newResolution();
			});
			/**** edit resolution ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editResolution();
			});
			/**** delete resolution ****/
			$("#delete_record").click(function() {
				deleteResolution();
			});		
			/****Searching Resolution****/
			$("#search").click(function() {
				searchRecord();
			});
			/****Member's Resolutions View ****/
			$("#member_resolutions_view").click(function() {
				$("#selectionDiv1").hide();
				memberResolutionsView();
			});
			
			/**** Generate Online Offline Submission Count Report ****/
			$("#online_offline_submission_count_report").click(function(){
				generateOnlineOfflineSubmissionCountReport();
			});
			
			$("#karyavali_report").click(function(){
				/**** url parameters for karyavali report ****/				
				var parameters_report = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 + "&outputFormat=" + $("#outputFormat").val();				
				var reportURL = 'resolution/generatekaryavalireport?' + parameters_report;
				$(this).attr('href', reportURL);		
				
				//check output format set or not
				if($("#outputFormat").val() == "") {
					$.prompt("Please Select Output Format first!!!!");
					return false;
				}				
			});	
			
			
			
			
			/***Current Status Report***/
			$("#generateCurrentStatusReport").click(function(){
				var selectedResolutionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedResolutionId.length>=1){
					showCurrentStatusReport('multiple',selectedResolutionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			/**** Generate Intimation Letter ****/			
			$("#generateIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateIntimationLetter();				
			});
			
			$("#temporaryKaryavaliReport").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters_report = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				var resourceURL='resolution/report/temporaryKaryavali?'+parameters_report;
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:500,height:300});
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	
			});
			
			/*$("#generateResolutionSummaryReport").click(function(){
				generateResolutionSummaryReport();
			});*/
			
			$("#generateResolutionSummaryReport").click(function(){
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
				
				var selectedResolutionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedResolutionId.length>=1){
					generateResolutionSummaryReport(selectedResolutionId);
				}else{
					generateResolutionSummaryReport('');
				}				
			});
			
			$("#sumRepDiv").hide();
			
			$("#registerReport").click(function(){
				generateResolutionRegister();
			});
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'resolution/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_CLERK')">
				<hr>
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="resolution.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='resolution.intimationletter.member' text='member' /></option>
						<option value="department"><spring:message code='resolution.intimationletter.department' text='department' /></option>
				</select> | 
			</security:authorize>
			
			<security:authorize access="!hasAnyRole('ROIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateResolutionSummaryReport" class="butSim link">
					<spring:message code="resolution.summaryReport" text="Resolution Summary Report"/>
				</a>
				<div id="sumRepDiv" style="display: inline;">
					<input type="text" class="sText datetimemask" id="sumRepFromDate" style="display: inline;">
					&nbsp; &nbsp;<input type="text" class="sText datetimemask" id="sumRepToDate" style="display: inline;">
					<div id="goSumRep" style="display: inline; border: 2px solid black; width: 10px; height: 10px;">Go</div>
				 </div> |
				 <a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="resolution.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
			</security:authorize>
			
			<%-- <security:authorize access="hasAnyRole('ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_CLERK')">
				| <a href="#" id="online_offline_submission_count_report" class="butSim link">
					<spring:message code="resolution.online_offline_submission_count_report" text="Online-Offline Submission Count Report"/>
				</a>
			</security:authorize> --%>
			
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_CLERK')">
			<hr>
			<a href="#" id="karyavali_report" class="butSim" target="_blank">
				<spring:message code="resolution.karyavali_report" text="Karyavali Report"/>
			</a>
			<c:if test="${not empty outputFormats}">				
				<select id="outputFormat" name="outputFormat">
					<option value="" selected="selected">Please Select Output Format</option>
					<c:forEach items="${outputFormats}" var="i">
						<option value="${i.value}">${i.name}</option>
					</c:forEach>
				</select>				
			</c:if>		 | 	
			</security:authorize>	
			
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT','ROIS_SECTION_OFFICER','ROIS_CLERK')">
				<a href="#" id="temporaryKaryavaliReport" class="butSim">
					<spring:message code="resolution.temporayKaryavaliReport" text="Temporary Karyavali Report"/>
				</a>
				<c:if test="${not empty outputFormats}">				
					<select id="outputFormat" name="outputFormat">
						<option value="" selected="selected">Please Select Output Format</option>
						<c:forEach items="${outputFormats}" var="i">
							<option value="${i.value}">${i.name}</option>
						</c:forEach>
					</select>				
				</c:if>	|
				
				<a href="#" id="registerReport" class="butSim">
					<spring:message code="resolution.register" text="Register"/>
				</a>		
			</security:authorize>	
							
			<hr>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |			
			<a href="#" id="submitResolution" class="butSim">
				<spring:message code="generic.submitresolution" text="submit"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="member_resolutions_view" class="butSim">
					<spring:message code="resolution.member_resolutions_view" text="Member's Resolutions View"/>
				</a> |
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="deviceTypeSelected">.
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>