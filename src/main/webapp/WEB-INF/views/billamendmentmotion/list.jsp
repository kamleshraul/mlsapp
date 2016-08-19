<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
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
					+"&bills="+$('#selectedBillIds').val()
					);
			/**** new motion ****/
			$('#new_record').click(function(){
				
				if($('#selectedBillYear').val()==undefined 
						|| $('#selectedBillYear').val()=="" || $('#selectedBillYear').val()=="0") {
					$.prompt($('#emptyBillYearPromptMsg').val());
					return false;
				} else if($('#selectedBillHouseType').val()==undefined || $('#selectedBillHouseType').val()=="") {
					$.prompt($('#emptyBillHouseTypePromptMsg').val());
					return false;
				} else if($('#selectedBillNumber').val()==undefined 
						|| $('#selectedBillNumber').val()=="" || $('#selectedBillNumber').val()=="0") {
					$.prompt($('#emptyBillNumberPromptMsg').val());
					return false;
				}
				$("#selectionDiv1").hide();	
				newBillAmendmentMotion();
			});
			/**** edit motion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editBillAmendmentMotion();
			});
			/**** delete motion ****/
			$("#delete_record").click(function() {
				deleteBillAmendmentMotion();
			});		
			/****Searching motion****/
			$("#search").click(function() {
				searchRecord();
			});					
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'billamendmentmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','BAMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="motion.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="motion.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','BAMOIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="motion.delete" text="Delete"/>
			</a> |			
			<%-- <a href="#" id="submitMotion" class="butSim">
				<spring:message code="generic.submitmotion" text="submit"/>
			</a> | --%>
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="motion.search" text="Search"/>
			</a> |				
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillYearPromptMsg" value="<spring:message code='billamendmentmotion.emptyBillYearPromptMsg' text='Please select bill year for the amended bill'/>" type="hidden">
	<input id="emptyBillHouseTypePromptMsg" value="<spring:message code='billamendmentmotion.emptyBillHouseTypePromptMsg' text='Please select bill housetype for the amended bill'/>" type="hidden">
	<input id="emptyBillNumberPromptMsg" value="<spring:message code='billamendmentmotion.emptyBillNumberPromptMsg' text='Please select bill number for the amended bill'/>" type="hidden">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>