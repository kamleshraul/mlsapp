<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="bill.list" text="List Of Bills"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$('.datemask').focus(function(){		
				if($(this).val()==""){
					$(".datemask").mask("99/99/9999");
				}
			});
			
			$("#selectionDiv1").show();					
			/**** grid params which is sent to load grid data being sent ****/	
			$("#gridURLParams").val('houseType='+$("#selectedHouseType").val()
					+'&introducingHouseType='+$("#selectedHouseType").val()
					+'&deviceType='+$("#selectedDeviceType").val()
					+'&sessionYear='+$("#selectedSessionYear").val()
					+'&sessionType='+$("#selectedSessionType").val()
					+'&ugparam='+$("#ugparam").val()
					+'&status='+$("#selectedStatus").val()
					//+'&translationStatus='+$("#selectedTranslationStatus").val()
					+'&role='+$("#srole").val()
					+'&usergroup='+$("#currentusergroup").val()
					+'&usergroupType='+$("#currentusergroupType").val());
			/**** new bill ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newBill();
			});
			/**** edit bill ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editBill();
			});
			/**** delete bill ****/
			$("#delete_record").click(function() {
				deleteBill();
			});		
			/****Searching Bill****/
			$("#search").click(function() {
				searchRecord();
			});
			
			$("#createpatrakbhagdon").click(function(){
				/**** url parameters for Patrak Bhag 2 report ****/		
				if($("#patrakbahgdonDate").val() !=''){
					
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 + "&outputFormat=PDF"
					 + "&patrakbahgdonDate=" + $("#patrakbahgdonDate").val();	
					var reportURL = 'bill/createbillpatrakbhagdon?' + parameters_report;
					$("#selectionDiv1").hide();	
					generatePatrakBhagDon(reportURL);
					
					//$(this).attr('href', reportURL);				
				}else{
					$.prompt($("#patrakDateMissingMsg").val());
				}
			});
			/**** Patrak Bhag 2 Report ****/
			$("#generate_patrakbhag2").click(function(){
				if($("#ptb2").css('display')=='none'){
					$("#ptb2").css('display', 'inline-block');
				}else{
					$("#ptb2").css('display', 'none');
				}
			});
			$("#provide_date").click(function(){	
				$("#selectionDiv").hide();
				provideDate();
			});
			
			
			var value = $("#selectedDeviceType").val();
			var deviceType=$("#deviceTypeMaster option[value='"+value+"']").text();
			
			if($("#currentDeviceType").val()!==''){
				
				if(deviceType=='bills_government' || deviceType=='bills_government'){
					$("#provide_date").show();
				}else{
					$("#provide_date").hide();
				}
			}
			/**** Manage Print Requisition ****/			
			$("#managePrintRequisition").click(function(){			
				managePrintRequisition();
			});			
			/**** Send Green Copy For Endorsement ****/			
			$("#sendGreenCopyForEndorsement").click(function(){			
				sendGreenCopyForEndorsement();
			});
			/**** Transmit Endorsement Copies ****/			
			$("#transmitEndorsementCopies").click(function(){	
				transmitEndorsementCopies();
			});
			/**** Transmit Press Copies ****/			
			$("#transmitPressCopies").click(function(){	
				transmitPressCopies();
			});
			/**** Lay Letter for Bill Passed by First House ****/	
			$("#layLetter").click(function(){	
				layLetter();
			});
		});		
			
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'bill/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}	
					
	</script>
	<style type="text/css">
		#goBtn{
			padding: 2px; 
			border: 1px solid #004D80; 
			background-color: #B4D6ED; 
			border-radius: 5px;
			height: 12px;
		}
		#goBtn:hover{
			padding: 2px; 
			border: 1px solid #004D80; 
			background-color: #6BB5E8; 
			border-radius: 5px;
			height: 12px;
		}
	</style>
</head>
<body>
	<div>
	<h1>${deviceTypeType}</h1>
	<div class="commandbar">
		<div class="commandbarContent" style="height: 36px;">	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','BIS_CLERK')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','BIS_CLERK')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |			
			<a href="#" id="submitBill" class="butSim">
				<spring:message code="generic.submitbill" text="submit"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<security:authorize access="hasAnyRole('BIS_ASSISTANT','BIS_SECTION_OFFICER')">			
				<a href="#" id="generate_patrakbhag2" class="butSim">
					<spring:message code="bill.patrakBhag2" text="Patrak Bhag 2"/>
				</a>
				 <div style="display: none;" id="ptb2">
				 	<input type="text" class="datemask" id="patrakbahgdonDate" style="width: 100px;" />
				 	<a href="#" id="createpatrakbhagdon" style="text-decoration: none;"><span id="goBtn"><spring:message code="bill.create.patrakbhagdon" text="Go" ></spring:message></span></a>
				 </div> |
			</security:authorize>
			<%-- <security:authorize access="hasAnyRole('BILL_DEPARTMENT_USER')"> --%>			
				<a href="#" id="provide_date" class="butSim">
					<spring:message code="generic.giveintroductiondate" text="Provide Introduction Date"/>
				</a> 
			<%-- </security:authorize> --%>
			<security:authorize access="hasAnyRole('BIS_ASSISTANT','BIS_SECTION_OFFICER')">
				<a href="#" id="managePrintRequisition" class="butSim">
					<spring:message code="bill.managePrintRequisition" text="Manage Print Requisition"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('BIS_ASSISTANT')">
			<a href="#" id="sendGreenCopyForEndorsement" class="butSim">
				<spring:message code="bill.sendGreenCopyForEndorsement" text="Send Green Copy For Endorsement"/>
			</a>
			</security:authorize>
			<security:authorize access="hasAnyRole('BIS_ASSISTANT')">
			<a href="#" id="transmitEndorsementCopies" class="butSim">
				<spring:message code="bill.transmitEndorsementCopies" text="Transmit Endorsement Copies"/>
			</a> | 
			</security:authorize>
			<security:authorize access="hasAnyRole('BIS_ASSISTANT')">
			<a href="#" id="transmitPressCopies" class="butSim">
				<spring:message code="bill.transmitPressCopies" text="Transmit Press Copies"/>
			</a> | 
			</security:authorize>
			<security:authorize access="hasAnyRole('BIS_ASSISTANT')">
			<a href="#" id="layLetter" class="butSim">
				<spring:message code="bill.layLetter" text="Lay letter for bill passed by first house"/>
			</a>
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="patrakDateMissingMsg" value="<spring:message code='bill.patrakbhagdon.circulationdatemissing' text='Distribution Date Missing'></spring:message>" />
	</div>
</body>
</html>