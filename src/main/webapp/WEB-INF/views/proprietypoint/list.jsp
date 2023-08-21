<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proprietypoint.list" text="List Of Propriety Points"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();
			
			var selectedProprietyPointDate = "";
			/* if($('#selectedHouseType').val()=='upperhouse') {
				if($("#isProprietyPointDateSelected").is(":checked")) {
					selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
				}
			} */
			if($("#isProprietyPointDateSelected").is(":checked")) {
				selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
			}
			
			/**** grid params which is sent to load grid data being sent ****/				
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&deviceType="+$("#selectedDeviceType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&proprietyPointDate="+selectedProprietyPointDate
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			/*******For Enabling the new Propriety Point link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());
			
			/**** report links display as per housetype ****/
			if($('#selectedHouseType').val()=='lowerhouse') {
				$('.lowerhouse_report').show();
				$('.bothhouse_report').show();
				$('.upperhouse_report').hide();
				
			} else if($('#selectedHouseType').val()=='upperhouse') {
				$('.lowerhouse_report').hide();
				$('.upperhouse_report').show();
				$('.bothhouse_report').show();
			}
			
			/**** new proprietypoint ****/
			$('#new_record').click(function(){				
				$("#selectionDiv1").hide();	
				newProprietyPoint();
			});
			/**** edit proprietypoint ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editProprietyPoint();
			});
			/**** delete proprietypoint ****/
			$("#delete_record").click(function() {
				deleteProprietyPoint();
			});		
			/****Searching proprietypoint****/
			$("#search").click(function() {
				searchRecord();
			});	
			/****Member's Propriety Points View ****/
			$("#member_proprietypoints_view").click(function() {
				$("#selectionDiv1").hide();
				memberProprietyPointsView();
			});
			/**** Current Status Report Generation ****/
			$("#prois_current_status_report").click(function() {
				/* $(this).attr('href','#');
				generateCurrentStatusReport(); */
				var selectedProprietyPointId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				console.log('deviceIds: ' + selectedProprietyPointId);
				if(selectedProprietyPointId.length>=1){
					showCurrentStatusReport('multiple',selectedProprietyPointId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			/**** To Be Admitted Report Generation ****/
			$("#prois_tobeadmitted_report").click(function() {
				$(this).attr('href','#');
				generateToBeAdmittedReport();
			});
			/**** To Be Rejected Report Generation ****/
			$("#prois_toberejected_report").click(function() {
				$(this).attr('href','#');
				generateToBeRejectedReport();
			});
			/**** Submitted Devices Report Generation ****/
			$("#prois_submitted_report").click(function() {
				$(this).attr('href','#');
				generateSubmittedDevicesReport();
			});
			/**** Admitted Devices Report Generation ****/
			$("#prois_admitted_report").click(function() {
				$(this).attr('href','#');
				generateAdmittedDevicesReport();
			});
			/**** Rejected Devices Report Generation ****/
			$("#prois_rejected_report").click(function() {
				$(this).attr('href','#');
				generateRejectedDevicesReport();
			});

			/**** Register of Admitted Devices Report Generation ****/
			$("#prois_register_report").click(function() {
				$(this).attr('href','#');
				//$.prompt("Work in progress for register report..");
				generateRegisterReport();
			});
			/**** Admitted Devices for Reporting Branch Report Generation ****/
			$("#prois_admitted_reportingbranch").click(function() {
				$(this).attr('href','#');
				//$.prompt("Work in progress for reporting branch report..");
				generateAdmittedDevicesReportForReporting();
			});
			
			$('#admissionNumberChange').click(function(){
				showTabByIdAndUrl('details_tab', 'proprietypoint/bulkview/admissionnumber?houseType='+$('#currentHouseType').val());
			});
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'proprietypoint/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="proprietypoint.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="proprietypoint.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="proprietypoint.delete" text="Delete"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="proprietypoint.search" text="Search"/>
			</a> |	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_proprietypoints_view" class="butSim">
					<spring:message code="proprietypoint.member_proprietypoints_view" text="Member's Propriety Points View"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('PROIS_CLERK', 'PROIS_ASSISTANT', 'PROIS_SECTION_OFFICER')">
				<a href="#" id="prois_current_status_report" class="butSim">
					<spring:message code="prois.current_status_report" text="Current Status Report"/>
				</a> |
				<a href="#" id="prois_tobeadmitted_report" class="butSim lowerhouse_report">
					<spring:message code="prois.tobeadmitted_report" text="Bulk Admission Approval Report"/>
				</a> |
				<a href="#" id="prois_submitted_report" class="butSim bothhouse_report">
					<spring:message code="prois.submitted_report" text="Submitted Notices Report"/>
				</a> |
				<a href="#" id="prois_admitted_report" class="butSim upperhouse_report">
					<spring:message code="prois.admitted_report" text="Admitted Notices Report"/>
				</a> |
				<a href="#" id="prois_rejected_report" class="butSim upperhouse_report">
					<spring:message code="prois.rejected_report" text="Rejected Notices Report"/>
				</a> |
				<a href="#" id="prois_register_report" class="butSim upperhouse_report">
					<spring:message code="prois.register_report" text="Register Report"/>
				</a> |
				<a href="#" id="prois_admitted_reportingbranch" class="butSim bothhouse_report">
					<spring:message code="prois.admitted_report" text="Admitted Notices Report For Reporters"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('PROIS_CLERK','PROIS_ASSISTANT')">
				<hr/>
				<a href="#" id="admissionNumberChange" class="butSim">
					<spring:message code="generic.admissionNumberChange" text="Change admission Number Change"/>
				</a> |
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>