<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Appropriation Bill Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&appropriationBillMotionType="+$("#selectedAppropriationBillMotionType").val()
					+"&subDepartment="+$("#selectedSubDepartment").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					);
			
			/**** new appropriationbillmotion ****/
			$('#new_record').click(function(){
				newAppropriationBillMotion();
			});
			/**** edit appropriationbillmotion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editAppropriationBillMotion();
			});
			/**** delete appropriationbillmotion ****/
			$("#delete_record").click(function() {
				deleteAppropriationBillMotion();
			});		
			/****Searching AppropriationBillMotion****/
			$("#search").click(function() {
				searchRecord();
			});
						
			$("#refreshList").click(function() {
				refreshList();
			});
			
			/****Member's AppropriationBillMotions View ****/
			$("#member_appropriationbillmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberAppropriationBillMotionsView();
			});
			
			/****Member's Replied AppropriationBillMotions View ****/
			$("#member_replied_appropriationbillmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberRepliedAppropriationBillMotionsView();
			});
			
			/****Member's Replied Supported AppropriationBillMotions View ****/
			$("#member_replied_supported_appropriationbillmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberRepliedSupportedAppropriationBillMotionsView();
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
			
			$("#selectedAppropriationBillMotionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&appropriationBillMotionType="+$(this).val()
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
			showTabByIdAndUrl('details_tab', 'appropriationbillmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ABMOIS_TYPIST')">			
				<a href="#" id="new_record" class="butSim">
					<spring:message code="generic.new" text="New"/>
				</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
				<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ABMOIS_TYPIST')">
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="generic.delete" text="Delete"/>
				</a> |
			</security:authorize>
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<hr>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE')">
				<a href="#" id="member_appropriationbillmotions_view" class="butSim" style="display: none;">
					<spring:message code="appropriationbillmotion.member_appropriationbillmotions_view" text="Member's Appropriation Bill Motions View"/>
				</a> |
			</security:authorize>
			<security:authorize access="!hasAnyRole('ABMOIS_TYPIST','MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="appropriationbillmotion.generateCurrentStatusReport" text="Current Status Report"/>
				</a> |
				<a href="#" id="departmentwise_submitted_counts_report" class="butSim" style="display: none;">
					<spring:message code="appropriationbillmotion.departmentwise_submitted_counts_report" text="Departmentwise Submitted Counts"/>
				</a> |
				<a href="#" id="departmentwise_admitted_counts_report" class="butSim" style="display: none;">
					<spring:message code="appropriationbillmotion.departmentwise_admitted_counts_report" text="Departmentwise Admitted Counts"/>
				</a> |
			</security:authorize>
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