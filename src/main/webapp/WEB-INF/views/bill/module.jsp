<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="bill.list" text="List Of Bills"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
				
			
			if($('#currentDeviceType').val()=='bills_nonofficial') {
				$('#ballotTab').show();
			}
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				showBillList();
			});	
			showBillList();	
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadBillGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadBillGrid();							
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadBillGrid();						
				}			
			});
			/**** device type changes then reload grid****/			
			$("#selectedDeviceType").change(function(){
				var value = $(this).val();
				var deviceType=$("#deviceTypeMaster option[value='"+value+"']").text();
				
				if(deviceType=='bills_government'){
					$("#provide_date").show();
				}else{
					$("#provide_date").hide();
				}
				
				if(value != ""){
					checkBallotTabForSelectedDeviceType();
					showBillList();
				}
				
			});
			
			$("#selectedStatus").change(function(){
				var width = $("#grid").jqGrid('getGridParam', 'width');
				var value=$(this).val();
				var statusType=$("#masterStatuses option[value='"+value+"']").text();
				
				if(value!=""){				
					reloadBillGrid();
				}
				//$("#grid").getGridParam("colModel")[9].name
				if(statusType!='bill_processed_underConsideration'){
					$("#grid").jqGrid('hideCol', 'recommendationStatus.name');
				}else{					
					$("#grid").jqGrid('showCol', 'recommendationStatus.name');
				}
				$("#grid").jqGrid('setGridWidth', width, true);
			});
			$("#selectedTranslationStatus").change(function(){
				var value=$(this).val();				
				if(value!=""){				
				reloadBillGrid();
				}
			});			
			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutup();
			});
			
			/**** Bulk Putup Assistant****/
			/* $("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			}); */
			/**** Ballot Tab ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
			});
			/**** Patrak Bhag Two ****/
			/* $("#patrakbhag2_tab").click(function(){
				$("#selectionDiv1").hide();
				viewPatrakBhag2();
			}); */
		});
		
		/* function loadStatus(deviceType){
			$.get('ref/status/'+ deviceType,function(data){
				$("#selectedStatus").empty();
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					selectedStatusText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#selectedStatus").html(selectedStatusText);			
				}else{
					$("#selectedStatus").empty();
					var selectedStatusText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
					$("#selectedStatus").html(selectedStatusText);				
				}
			});
		} */

		/**** displaying grid ****/					
		function showBillList() {	
			showTabByIdAndUrl('list_tab','bill/list?houseType='+$("#selectedHouseType").val()
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
		}
		
		/**** show/hide ballot tab as per selected device type****/
		function checkBallotTabForSelectedDeviceType() {
			$.get('ref/getTypeOfSelectedDeviceType?deviceTypeId='+$("#selectedDeviceType").val(), function(data) {
				if(data!=null && data!=undefined && data!='') {
					if(data=='bills_nonofficial') {
						$('#ballotTab').show();
					} else {
						$('#ballotTab').hide();
					}
				}
			});
		}
		
		function newBill() {
			$("#cancelFn").val("newBill");
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','bill/new?'+$("#gridURLParams").val());
		}
			
		function editBill(row) {
			$("#cancelFn").val("editBill");
			row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','bill/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
				
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'bill/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
			
		function deleteBill() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('bill/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showBillList();
				        });
			        }
				}});
			}
		}
		/**** reload grid ****/
		function reloadBillGrid(){
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
			var oldURL=$("#grid").getGridParam("url");
			console.log("oldURL: "+ oldURL);
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");											
		}
		/**** Chart Tab ****/
 		/* function viewChart() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			var resourceURL = 'chart/init?' + parameters;
			showTabByIdAndUrl('chart_tab', resourceURL);
		} */
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val();
			}
			var parameters = parameters + "&category=bill";
			var resourceURL = 'ballot/init?' + parameters;
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}
		
		
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +'&translationStatus='+$("#selectedTranslationStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'bill/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}
		
		/**** Bulk putup(Assistant)****/
		/* function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='bill/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		} */		
		/**** Member Ballot Tab ****/
		/* function viewMemberBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				var device=$("#currentDeviceType").val();
				if(device=='bills_nonofficial'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&ugparam="+$("#ugparam").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val()
					 +"&usergroup="+$("#currentusergroup").val()
					 +"&usergroupType="+$("#currentusergroupType").val();				
				}
			}
			parameters+= "&round=1";
			var resourceURL = 'ballot/memberballot/init?' + parameters;
			showTabByIdAndUrl('memberballot_tab', resourceURL);
		} */
		
		/****Provide introduction date ****/
		function provideDate(){
			var rURL = 'bill/providedate?houseType=' + $("#selectedHouseType option:selected").val()+"&sessionType="+$("#selectedSessionType option:selected").val()+"&sessionYear="+$("#selectedSessionYear option:selected").val()+"&usergrouptype="+$("#currentusergroupType").val();
			//alert(rURL);
			showTabByIdAndUrl('details_tab',rURL);
		}
		
		
		/****To generate Patrak Bhag Don****/
		function generatePatrakBhagDon(pURL){
			showTabByIdAndUrl('details_tab', pURL);
		}
		
		/**** To Manage Print Requisition ****/
		function managePrintRequisition() {			
			var selectedBillId = $("#grid").jqGrid ('getGridParam', 'selarrrow').toString();
			if(selectedBillId.split(",").length!=1) {
				selectedBillId = "";
			}			
			$.get('printrequisition/bill?billId='+selectedBillId+ "&houseTypeType="+ $("#selectedHouseType").val()
	    			+ "&billYear="+ $("#selectedSessionYear").val(), function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
		}
		/**** To Send Green Copy For Endorsement ****/
		function sendGreenCopyForEndorsement() {			
			var selectedBillId = $("#grid").jqGrid ('getGridParam', 'selarrrow').toString();
			if(selectedBillId.split(",").length!=1) {
				selectedBillId = "";
			}		
			$.get('printrequisition/bill/sendForEndorsement?'+$("#gridURLParams").val()+'&billId='+selectedBillId, function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
		}
		/**** To Transmit Endorsement Copies ****/
		function transmitEndorsementCopies() {			
			var selectedBillId = $("#grid").jqGrid ('getGridParam', 'selarrrow').toString();
			if(selectedBillId.split(",").length!=1) {
				selectedBillId = "";
			}		
			$.get('printrequisition/bill/transmitEndorsementCopies?'+$("#gridURLParams").val()+'&billId='+selectedBillId, function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
		}
		/**** To Transmit Press Copies ****/
		function transmitPressCopies() {			
			var selectedBillId = $("#grid").jqGrid ('getGridParam', 'selarrrow').toString();
			if(selectedBillId.split(",").length!=1) {
				selectedBillId = "";
			}		
			$.get('printrequisition/bill/transmitPressCopies?'+$("#gridURLParams").val()+'&billId='+selectedBillId, function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
		}
		/**** To Lay Letter for Bill Passed by First House ****/
		function layLetter() {			
			var selectedBillId = $("#grid").jqGrid ('getGridParam', 'selarrrow').toString();
			if(selectedBillId.split(",").length!=1) {
				selectedBillId = "";
			}		
			$.get('layingletter/bill/layLetterWhenPassedByFirstHouse?'+$("#gridURLParams").val()+'&billId='+selectedBillId, function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
		}
	</script>
</head>
<body>
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details">
				   </spring:message>
				</a>
			</li>
			<%-- <c:if test="${deviceTypeType == 'bills_nonofficial'}">
			<security:authorize access="hasAnyRole('BIS_ASSISTANT', 'BIS_UNDERSECRETARY',
				'BIS_DEPUTY_SECRETARY', 'BIS_PRINCIPAL_SECRETARY', 'BIS_JOINT_SECRETARY',
				'BIS_SECRETARY', 'BIS_OFFICER_ON_SPECIAL_DUTY', 'BIS_SECTION_OFFICER', 
				'BIS_UNDER_SECRETARY_COMMITTEE','SUPER_ADMIN')">
			<li>
				<a id="chart_tab" href="#" class="tab">
				   <spring:message code="bill.chart" text="Chart"></spring:message>
				</a>
			</li>
			</security:authorize>
			</c:if> --%>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<%-- <security:authorize access="hasAnyRole('BIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize> --%>			
			<security:authorize access="hasAnyRole('BIS_ASSISTANT', 'BIS_UNDERSECRETARY',
				'BIS_DEPUTY_SECRETARY', 'BIS_PRINCIPAL_SECRETARY', 'BIS_SPEAKER', 'BIS_JOINT_SECRETARY',
				'BIS_SECRETARY', 'BIS_OFFICER_ON_SPECIAL_DUTY', 'BIS_DEPUTY_SPEAKER', 'BIS_CHAIRMAN',
				'BIS_DEPUTY_CHAIRMAN', 'BIS_SECTION_OFFICER', 'BIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<li id="ballotTab" style="display:none;">
					<a id="ballot_tab" href="#" class="tab">
				   		<spring:message code="bill.ballot" text="Ballot"></spring:message>
					</a>
					</li>
			</security:authorize>
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="bill.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:97px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseType==i.type}">
			<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>					
			</c:when>
			<c:otherwise>
			<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |					
			<a href="#" id="select_session_year" class="butSim">
				<spring:message code="bill.sessionyear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:97px;height: 25px;">				
			<c:forEach var="i" items="${years}">
			<c:choose>
			<c:when test="${i==sessionYear }">
			<option value="${i}" selected="selected"><c:out value="${i}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i}" ><c:out value="${i}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach> 
			</select> |						
			<a href="#" id="select_sessionType" class="butSim">
				<spring:message code="bill.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:97px;height: 25px;">				
			<c:forEach items="${sessionTypes}" var="i">
			<c:choose>
			<c:when test="${sessionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>			
			</c:otherwise>
			</c:choose>			
			</c:forEach> 
			</select> |				
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="bill.deviceType" text="Bill Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:97px;height: 25px;">			
			<c:forEach items="${deviceTypes}" var="i">
			<c:choose>
			<c:when test="${deviceType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>
			</select> |			
			<a href="#" id="select_status" class="butSim">
				<spring:message code="bill.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:97px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach>
			</select>
			<hr>			
			
			<%-- <c:if test="${usergroupType=='assistant'}">
			<security:authorize access="hasRole('BIS_ASSISTANT')">
				<a href="#" id="select_translationStatus" class="butSim">
					<spring:message code="bill.translationStatus" text="Translation Status"/>
				</a>
				<select name="selectedTranslationStatus" id="selectedTranslationStatus" style="width:250px;height: 25px;">			
					<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${translationStatuses}" var="i">
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach>
				</select> |	
			</security:authorize>
			</c:if> --%>
		</div>		
				
		<div class="tabContent"></div>
		<div style="display: none;">
			<select id="masterStatuses">
				<c:forEach items="${status}" var="s">
					<option value="${s.id}">${s.type}</option>
				</c:forEach>
			</select>
		</div>
		<input type="hidden" id="key" name="key">		
		
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam}">
		<input type="hidden" name="srole" id="srole" value="${role}">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">
		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">	
		<%--<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>">			
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>">			
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>">			
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>">			
		
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		<input type="hidden" id="chartAnsweringDate" name="chartAnsweringDate" value="-"> --%>
		</div> 		
</body>
</html>