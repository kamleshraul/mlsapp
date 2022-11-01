<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&cutMotionType="+$("#selectedCutMotionType").val()
					+"&subDepartment="+$("#selectedSubDepartment").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					);
			
			/**** new cutmotion ****/
			$('#new_record').click(function(){
				newCutMotion();
			});
			/**** edit cutmotion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editCutMotion();
			});
			/**** delete cutmotion ****/
			$("#delete_record").click(function() {
				deleteCutMotion();
			});		
			/****Searching CutMotion****/
			$("#search").click(function() {
				searchRecord();
			});
						
			$("#refreshList").click(function() {
				refreshList();
			});
			
			/****Member's CutMotions View ****/
			$("#member_cutmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberCutMotionsView();
			});
			
			/****Member's Replied CutMotions View ****/
			$("#member_replied_cutmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberRepliedCutMotionsView();
			});
			
			/****Member's Replied Supported CutMotions View ****/
			$("#member_replied_supported_cutmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberRepliedSupportedCutMotionsView();
			});
			
			/**** Departmentwise Submitted Counts ****/
			$("#departmentwise_submitted_counts_report").click(function() {
				$("#selectionDiv1").hide();
				generateDepartmentwiseSubmittedCountsReport();
			});
			
			/**** Departmentwise Admitted Counts ****/
			$("#departmentwise_admitted_counts_report").click(function() {
				$("#selectionDiv1").hide();
				generateDepartmentwiseAdmittedCountsReport();
			});
			
			/**** Departmentwise Pending-for-Reply Counts ****/
			$("#departmentwisePendingForReplyCountsReport").click(function() {
				$("#selectionDiv1").hide();
				generateDepartmentwisePendingForReplyCountsReport();
			});
			
			$("#generateCurrentStatusReport").click(function(){
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			//---ADDED BY VIKAS------------------
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedCutMotionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&cutMotionType="+$(this).val()
						+"&subDepartment="+$("#selectedSubdepartment").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());
			});
			
			$("#assign_number").click(function(){
				assignNumberAfterApproval();
			});
			
			$("#generateYaadiReport").click(function(){				
				generateYaadiReport();
			});
			
			/**** Generate Reminder Letter ****/			
			$("#generateReminderLetter").click(function(){
				$(this).attr('href','#');
				$.prompt("Do you really want to send reminder letter to department now?",{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	generateReminderLetter(true);
	    	        } else {
	    	        	generateReminderLetter(false);
	    	        }
				}});								
			});
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();		
			$("#cancelFn").val("rowDblClickHandler");
			$("#selectionDiv1").hide();	
			showTabByIdAndUrl('details_tab', 'cutmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','CMOIS_CLERK','CMOIS_TYPIST')">			
				<a href="#" id="new_record" class="butSim">
					<spring:message code="generic.new" text="New"/>
				</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
				<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','CMOIS_CLERK')">			
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="generic.delete" text="Delete"/>
				</a> |
			</security:authorize>			
			<security:authorize access="hasAnyRole('CMOIS_ASSISTANT')">			
				<a href="#" id="assign_number" class="butSim">
					<spring:message code="generic.assign_number" text="Assign Number"/>
				</a> |
			</security:authorize>
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<a href="#" id="refreshList" class="butSim">
				<spring:message code="generic.refresh" text="Refresh"/>
			</a> |	
			<hr>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE')">
				<a href="#" id="member_cutmotions_view" class="butSim">
					<spring:message code="cutmotion.member_cutmotions_view" text="Member's CutMotions View"/>
				</a> |
				<hr/>
				<a href="#" id="member_replied_cutmotions_view" class="butSim">
					<spring:message code="cutmotion.member_replied_cutmotions_view" text="Member's Replied CutMotions View"/>
				</a> |
				<a href="#" id="member_replied_supported_cutmotions_view" class="butSim">
					<spring:message code="cutmotion.member_replied_supported_cutmotions_view" text="Replied Supported CutMotions View"/>
				</a> |
			</security:authorize>
			<security:authorize access="!hasAnyRole('CMOIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="cutmotion.generateCurrentStatusReport" text="Current Status Report"/>
				</a> |
				<a href="#" id="departmentwise_submitted_counts_report" class="butSim">
					<spring:message code="cutmotion.departmentwise_submitted_counts_report" text="Departmentwise Submitted Counts"/>
				</a> |
				<a href="#" id="departmentwise_admitted_counts_report" class="butSim">
					<spring:message code="cutmotion.departmentwise_admitted_counts_report" text="Departmentwise Admitted Counts"/>
				</a> |
				<a href="#" id="generateYaadiReport" class="butSim">
					<spring:message code="cutmotion.generateYaadiReport" text="Yaadi Report"/>
				</a> |
				<hr/>
				<a href="#" id="generateReminderLetter" class="butSim">
					<spring:message code="generic.mytask.device.ReminderLetter" text="Reminder Letter"/>
				</a> |
				<a href="#" id="departmentwisePendingForReplyCountsReport" class="butSim">
					<spring:message code="cutmotion.departmentwise_pending_for_reply_counts_report" text="Departmentwise Pending-for-Reply Counts"/>
				</a> |
			</security:authorize>	
			<%-- <security:authorize access="hasAnyRole('CMOIS_ASSISTANT','CMOIS_SECTION_OFFICER')">				
				<a href="#" id="generateYaadiReport" class="butSim">
					<spring:message code="cutmotion.generateYaadiReport" text="Generate Yaadi Report"/>
				</a> |
			</security:authorize> --%>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>