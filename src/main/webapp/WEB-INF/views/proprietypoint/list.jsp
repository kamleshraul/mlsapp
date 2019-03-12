<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proprietypoint.list" text="List Of Propriety Points"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();		
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
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			/*******For Enabling the new Propriety Point link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());
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
			/**** Bhag 1 Report Generation ****/
			$("#prois_bhag_1_report").click(function() {
				$(this).attr('href','#');
				generateBhag1Report();
			});
			/**** Bhag 2 Report Generation ****/
			$("#prois_bhag_2_report").click(function() {
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
			<%-- <a href="#" id="prois_bhag_1_report" class="butSim">
				<spring:message code="prois.bhag_1_report" text="Bhag 1 Report"/>
			</a> |
			<a href="#" id="prois_bhag_2_report" class="butSim">
				<spring:message code="prois.bhag_2_report" text="Bhag 2 Report"/>
			</a> | --%>
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