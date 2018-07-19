<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="adjournmentmotion_${houseType}.list" text="List Of Adjournment Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();		
			var selectedAdjourningDate = "";
			if($("#isAdjourningDateSelected").is(":checked")) {
				selectedAdjourningDate = convertToDbFormat($('#selectedAdjourningDate').val());
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
					+"&adjourningDate="+selectedAdjourningDate
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			/*******For Enabling the new Adjournment Motion link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());
			/**** new motion ****/
			$('#new_record').click(function(){				
				$("#selectionDiv1").hide();	
				newAdjournmentMotion();
			});
			/**** edit motion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editAdjournmentMotion();
			});
			/**** delete motion ****/
			$("#delete_record").click(function() {
				deleteAdjournmentMotion();
			});		
			/****Searching motion****/
			$("#search").click(function() {
				searchRecord();
			});	
			/****Member's Adjournment Motions View ****/
			$("#member_adjournmentmotions_view").click(function() {
				$("#selectionDiv1").hide();
				memberAdjournmentMotionsView();
			});
			/**** Current Status Report Generation ****/
			$("#amois_current_status_report").click(function() {
				/* $(this).attr('href','#');
				generateCurrentStatusReport(); */
				var selectedMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			/**** Bhag 1 Report Generation ****/
			$("#amois_bhag_1_report").click(function() {
				$(this).attr('href','#');
				generateBhag1Report();
			});
			/**** Bhag 2 Report Generation ****/
			$("#amois_bhag_2_report").click(function() {
				$(this).attr('href','#');
				generateBhag2Report();
			});
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'adjournmentmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','AMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="adjournmentmotion.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="motion.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','AMOIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="motion.delete" text="Delete"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="motion.search" text="Search"/>
			</a> |	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<hr/>
				<a href="#" id="member_adjournmentmotions_view" class="butSim">
					<spring:message code="question.member_adjournmentmotions_view" text="Member's Adjournment Motions View"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('AMOIS_CLERK', 'AMOIS_ASSISTANT', 'AMOIS_SECTION_OFFICER')">
			<a href="#" id="amois_current_status_report" class="butSim">
				<spring:message code="amois.current_status_report" text="Current Status Report"/>
			</a> |
			<a href="#" id="amois_bhag_1_report" class="butSim">
				<spring:message code="amois.bhag_1_report" text="Bhag 1 Report"/>
			</a> |
			<a href="#" id="amois_bhag_2_report" class="butSim">
				<spring:message code="amois.bhag_2_report" text="Bhag 2 Report"/>
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