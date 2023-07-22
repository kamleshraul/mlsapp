<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="rulessuspensionmotion_${houseType}.list" text="List Of Rules Suspension Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();		
			var selectedRuleSuspensionDate = "";
			if($("#isRuleSuspensionDateSelected").is(":checked")) {
				selectedRuleSuspensionDate = convertToDbFormat($('#selectedRuleSuspensionDate').val());
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
					+"&ruleSuspensionDate="+selectedRuleSuspensionDate);
			/*******For Enabling the new Rules Suspension Motion link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());
			/**** new motion ****/
			$('#new_record').click(function(){				
				$("#selectionDiv1").hide();	
				newRulesSuspensionMotion();
			});
			/**** edit motion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editRulesSuspensionMotion();
			});
			/**** delete motion ****/
			$("#delete_record").click(function() {
				deleteRulesSuspensionMotion();
			});		
			/****Searching motion****/
			$("#search").click(function() {
				searchRecord();
			});	
			/****Member's Rules Suspension Motions View ****/
			$("#member_rulessuspensionmotions_view").click(function() {
				$("#selectionDiv1").hide();
				membersRulesSuspensionMotionsView();
			});
			/**** Current Status Report Generation ****/
			$("#rsmois_current_status_report").click(function() {
				/* $(this).attr('href','#');
				generateCurrentStatusReport(); */
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				//alert(selectedMotionId);
				if(selectedMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			/**** Bhag 1 Report Generation ****/
			$("#rsmois_bhag_1_report").click(function() {
				$(this).attr('href','#');
				generateBhag1Report();
			});
			/**** Bhag 2 Report Generation ****/
			$("#rsmois_bhag_2_report").click(function() {
				$(this).attr('href','#');
				generateBhag2Report();
			});
			/**** Statement Report Generation ****/
			$("#rsmois_statement_report").click(function() {
				$(this).attr('href','#');
				generateStatementReport();
			});
			/**** Submitted Motions Report Generation ****/
			$("#rsmois_submitted_report").click(function() {
				$(this).attr('href','#');
				generateSubmittedMotionsReport();
			});
			/**** Admitted Motions Report Generation ****/
			$("#rsmois_admitted_report").click(function() {
				$(this).attr('href','#');
				generateAdmittedMotionsReport();
			});
			/**** Rejected Motions Report Generation ****/
			$("#rsmois_rejected_report").click(function() {
				$(this).attr('href','#');
				generateRejectedMotionsReport();
			});
			/**** Register Report Generation ****/
			$("#rsmois_register_report").click(function() {
				$(this).attr('href','#');
				generateRegisterReport();
			});
			$("#rsmois_decision_report").click(function(){
				$(this).attr('href','#');
				generateDecisionReport($('#key').val());
			});
			$("#rsmois_notice_statement").click(function(){
				$(this).attr('href','#');
				generateNoticeStatement($('#key').val());
			});
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'rulessuspensionmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','RSMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="rulessuspensionmotion.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="motion.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','RSMOIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="motion.delete" text="Delete"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="motion.search" text="Search"/>
			</a> |	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_rulessuspensionmotions_view" class="butSim">
					<spring:message code="question.member_rulessuspensionmotions_view" text="Member's Rules Suspension Motions View"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('RSMOIS_CLERK', 'RSMOIS_ASSISTANT', 'RSMOIS_SECTION_OFFICER','RSMOIS_DEPUTY_SECRETARY','RSMOIS_SECRETARY')">
			<a href="#" id="rsmois_current_status_report" class="butSim">
				<spring:message code="rsmois.current_status_report" text="Current Status Report"/>
			</a> |		
			<c:choose>
				<c:when test="${houseType=='lowerhouse'}">
					<%-- <a href="#" id="rsmois_bhag_1_report" class="butSim">
						<spring:message code="rsmois.bhag_1_report" text="Notice Report"/>
					</a> |
					<a href="#" id="rsmois_bhag_2_report" class="butSim">
						<spring:message code="rsmois.bhag_2_report" text="Bhag 2 Report"/>
					</a> |
					<a href="#" id="rsmois_statement_report" class="butSim">
						<spring:message code="rsmois.statement_report" text="Statement Report"/>
					</a> | --%>
					<a href="#" id="rsmois_register_report" class="butSim">
						<spring:message code="rsmois.register_report" text="Register Report"/>
					</a> |
				</c:when>
				<c:when test="${houseType=='upperhouse'}">
					<a href="#" id="rsmois_submitted_report" class="butSim">
						<spring:message code="rsmois.submitted_report" text="Rule Suspension Motions Report"/>
					</a> |
					<%-- <a href="#" id="rsmois_admitted_report" class="butSim">
						<spring:message code="rsmois.admitted_report" text="Admitted Motions Report"/>
					</a> |
					<a href="#" id="rsmois_rejected_report" class="butSim">
						<spring:message code="rsmois.rejected_report" text="Rejected Motions Report"/>
					</a> |
					<a href="#" id="rsmois_register_report" class="butSim">
						<spring:message code="rsmois.rejected_report" text="Register Report"/>
					</a> | --%>
					<a href="#" id="rsmois_notice_statement" class="butSim">
						<spring:message code="rsmois.notice_statement" text="Notice Statement"/>
					</a> |
					<security:authorize access="hasAnyRole('RSMOIS_SECTION_OFFICER','RSMOIS_DEPUTY_SECRETARY','RSMOIS_SECRETARY')">
						<a href="#" id="rsmois_decision_report" class="butSim">
							<spring:message code="rsmois.decision_report" text="Decision Report"/>
						</a> |
					</security:authorize>
				</c:when>
			</c:choose>				
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