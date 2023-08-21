<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>SpecialMentionNotice</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();		
			var selectedSpecialMentionNoticeDate = "";
			if($("#isSpecialMentionNoticeDateSelected").is(":checked")) {
				selectedSpecialMentionNoticeDate = convertToDbFormat($('#selectedSpecialMentionNoticeDate').val());
			}
			/**** grid params which is sent to load grid data being sent ****/				
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&motionType="+$("#selectedMotionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&specialMentionNoticeDate="+selectedSpecialMentionNoticeDate
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			/*******For Enabling the new Adjournment Motion link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());
			/**** new motion ****/
			$('#new_record').click(function(){				
				$("#selectionDiv1").hide();	
				newSpecialMentionNotice();
			});
			/**** edit motion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editSpecialMentionNotice();
			});
			/**** delete motion ****/
			$("#delete_record").click(function() {
				deleteSpecialMentionNotice();
			});		
			/****Searching motion****/
			$("#search").click(function() {
				searchRecord();
			});	
			/****Member's Adjournment Motions View ****/
			$("#member_specialmentionnotices_view").click(function() {
				$("#selectionDiv1").hide();
				memberSpecialMentionNoticesView();
			});
			/**** Current Status Report Generation ****/
			$("#smis_current_status_report").click(function() {
				/* $(this).attr('href','#');
				generateCurrentStatusReport(); */
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			/**** Submitted Motions Report Generation ****/
			$("#smis_submitted_report").click(function() {
				$(this).attr('href','#');
				generateSubmittedMotionsReport();
			});
			/**** Admitted Motions Report Generation ****/
			$("#smis_admitted_report").click(function() {
				$(this).attr('href','#');
				generateAdmittedMotionsReport();
			});
			
			/**** Admitted Motions Report Generation ****/
			$("#smis_admitted_reportingbranch").click(function() {
				$(this).attr('href','#');
				generateAdmittedMotionsReportForReporting();
			});
			/**** Rejected Motions Report Generation ****/
			$("#smis_rejected_report").click(function() {
				$(this).attr('href','#');
				generateRejectedMotionsReport();
			});
			/**** Register Report Generation ****/
			$("#smis_register_report").click(function() {
				$(this).attr('href','#');
				generateRegisterReport();
			});

			$('#admissionNumberChange').click(function(){
				showTabByIdAndUrl('details_tab', 'specialmentionnotice/bulkview/admissionnumber?houseType='+$('#currentHouseType').val());
			});
			
		});
		
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'specialmentionnotice/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="adjournmentmotion.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="motion.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="motion.delete" text="Delete"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="motion.search" text="Search"/>
			</a> |	
		 	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_specialmentionnotices_view" class="butSim">
					<spring:message code="question.member_specialmentionnotice_view" text="Member's Special Mention Notices View"/>
				</a> |
			</security:authorize>	 
			<security:authorize access="hasAnyRole('SMIS_CLERK', 'SMIS_ASSISTANT', 'SMIS_SECTION_OFFICER')">
			<a href="#" id="smis_current_status_report" class="butSim">
				<spring:message code="smis.current_status_report" text="Current Status Report"/>
			</a> |		
			<c:choose>
				<%-- <c:when test="${houseType=='lowerhouse'}">
					<a href="#" id="amois_bhag_1_report" class="butSim">
						<spring:message code="amois.bhag_1_report" text="Bhag 1 Report"/>
					</a> |
					<a href="#" id="amois_bhag_2_report" class="butSim">
						<spring:message code="amois.bhag_2_report" text="Bhag 2 Report"/>
					</a> |
					<a href="#" id="amois_statement_report" class="butSim">
						<spring:message code="amois.statement_report" text="Statement Report"/>
					</a> |
				</c:when> --%>
				<c:when test="${houseType=='upperhouse'}">
					<a href="#" id="smis_submitted_report" class="butSim">
						<spring:message code="smis.submitted_report" text="Submitted Notices Report"/>
					</a> |
					<a href="#" id="smis_admitted_report" class="butSim">
						<spring:message code="smis.admitted_report" text="Admitted Notices Report"/>
					</a> |
					<a href="#" id="smis_rejected_report" class="butSim">
						<spring:message code="smis.rejected_report" text="Rejected Notices Report"/>
					</a> |
					<a href="#" id="smis_register_report" class="butSim">
						<spring:message code="smis.rejected_report" text="Register Report"/>
					</a> |
					<a href="#" id="smis_admitted_reportingbranch" class="butSim">
						<spring:message code="smis.admitted_report" text="Admitted Notices Report For Reporters"/>
					</a> |
				</c:when>
			</c:choose>				
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_specialmentionnotices_view" class="butSim">
					<spring:message code="question.member_specialmentionnotice_view" text="Member's Special Mention Notices View"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('SMIS_CLERK','SMIS_ASSISTANT')">
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