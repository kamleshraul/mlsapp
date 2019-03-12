<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proprietypoint.list" text="List Of Propriety Points"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();
			$("#bulkputup_tab").hide();
			/*Tooltip*/
			$(".toolTip").hide();					
			/**** here we are trying to add date mask in grid search when field names ends with date ****/
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				showProprietyPointList();				
			});				
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadProprietyPointGrid();								
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadProprietyPointGrid();						
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadProprietyPointGrid();										
				}			
			});
			/**** proprietypoint type changes ****/		
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadProprietyPointGrid();							
				}				
			});	
			/**** status changes then reload grid ****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadProprietyPointGrid();
					$("#selectedFileCount").val("-");
				}
			});
			/**** sub-department changes then reload grid ****/
			$("#selectedSubDepartment").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadProprietyPointGrid();
					$("#selectedFileCount").val("-");
				}
			});
			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutup();
			});	
			/**** Bulk Putup Assistant ****/
			$("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			});		
			/**** show proprietypoint list method is called by default.****/
			showProprietyPointList();			
		});
		/**** displaying grid ****/					
		function showProprietyPointList() {
			showTabByIdAndUrl('list_tab','proprietypoint/list?houseType='+$('#selectedHouseType').val()
					+'&deviceType='+$("#selectedDeviceType").val()
					+'&sessionYear='+$("#selectedSessionYear").val()
					+'&sessionType='+$("#selectedSessionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()				
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
		}	
		function memberProprietyPointsView() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&deviceType=" + $("#selectedDeviceType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_PROPRIETYPOINTS_VIEW"
			+ "&reportout=member_proprietypoints_view";
			showTabByIdAndUrl('details_tab','proprietypoint/report/generalreport?'+parameters);
		}
		/**** new proprietypoint ****/
		function newProprietyPoint() {		
			$("#cancelFn").val("newProprietyPoint");
			//since id of proprietypoint has not been created so key is set to empty value
			$("#key").val("");	
			showTabByIdAndUrl('details_tab','proprietypoint/new?'+$("#gridURLParams").val());
			$("#selectionDiv1").hide();	
		}
		/**** edit proprietypoint ****/		
		function editProprietyPoint(row) {
			$("#cancelFn").val("editProprietyPoint");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','proprietypoint/'+row+'/edit?'+$("#gridURLParams").val());
				$("#selectionDiv1").hide();	
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);			
			showTabByIdAndUrl('details_tab', 'proprietypoint/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete proprietypoint ****/	
		function deleteProprietyPoint() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('proprietypoint/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				        	showProprietyPointList();
				        }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
			        }
				}});
			}
		}
		function convertToDbFormat(date){
			if(date!="") {
				var splitResult=date.split("/");
				if(splitResult.length==3){
					return splitResult[2]+"-"+splitResult[1]+"-"+splitResult[0];
				}else{
					return "Invalid Date";
				}
			} else {
				return "";
			}		
		}
		/**** reload grid ****/
		function reloadProprietyPointGrid(){
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()+
					"&sessionType="+$("#selectedSessionType").val()+
					"&deviceType="+$("#selectedDeviceType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()		
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			console.log($("#gridURLParams").val());
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");
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
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'proprietypoint/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}	
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
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
				 var resource='proprietypoint/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}
		/**** Current Status Report Generation ****/
		function showCurrentStatusReport(val, deviceId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', "proprietypoint/report/currentstatusreport?device="+ device +"&reportType="+val+"&deviceId="+deviceId);
		}
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('PROIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>			
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="proprietypoint.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:99px;height: 25px;">			
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
				<spring:message code="proprietypoint.sessionyear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:99px;height: 25px;">				
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
				<spring:message code="proprietypoint.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:99px;height: 25px;">				
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
				<spring:message code="proprietypoint.deviceType" text="Device Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width: 100px; height: 25px;">
				<c:forEach items="${deviceTypes}" var="i">
					<c:choose>
						<c:when test="${deviceType==i.id}">
							<option value="${i.id}" selected="selected">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display: none;">
				<c:forEach items="${deviceTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>
			</select>|		
			
			<security:authorize
				access="hasAnyRole('PROIS_ADMIN','PROIS_ASSISTANT','PROIS_UNDER_SECRETARY',
			'PROIS_DEPUTY_SECRETARY','PROIS_PRINCIPAL_SECRETARY','PROIS_SPEAKER','PROIS_JOINT_SECRETARY',
			'PROIS_SECRETARY','PROIS_OFFICER_ON_SPECIAL_DUTY','PROIS_DEPUTY_SPEAKER','PROIS_CHAIRMAN','PROIS_DEPUTY_CHAIRMAN',
			'PROIS_SECTION_OFFICER','PROIS_UNDER_SECRETARY_COMMITTEE','PROIS_ADDITIONAL_SECRETARY','PROIS_CLERK')">
			
			<hr>
			<a href="#" id="select_status" class="butSim">
				<spring:message code="proprietypoint.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:250px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<c:choose>
			<c:when test="${selectedStatusId==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>	
			</c:forEach>
			</select> |
			
			<div id='proprietypointDepartment' style="display:inline;">
			<a href="#" id="select_department" class="butSim"> <spring:message
					code="proprietypoint.department" text="Department" />
			</a>
			<select name="selectedSubDepartment" id="selectedSubDepartment"
				style="width: 250px; height: 25px;">
				<option value="0" selected="selected"><spring:message code="please.select"></spring:message></option>
				<c:forEach items="${subDepartments}" var="i">
					<option value="${i.id}">
						<c:out value="${i.name}"></c:out>
					</option>
				</c:forEach>
			</select>|
			</div>
			</security:authorize>
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_TYPIST')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="proprietypoint.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:99px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<c:choose>
			<c:when test="${selectedStatusId==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>	
			</c:forEach>
			</select> |
			</security:authorize>		
				
			<%-- <security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_ASSISTANT')">	
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="proprietypoint.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:99px;height: 25px;">			
			<option value="30">30</option>
			<option value="25">25</option>
			<option value="20">20</option>
			<option value="15">15</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			</security:authorize> --%>				
			
			<hr>										
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">				
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		</div> 
		<input type="hidden" id="allSelectedMsg" value="<spring:message code='generic.allOption' text='---- All ----'/>"/>
		<input type="hidden" id="yes" value="<spring:message code='generic.yes' text='Yes'/>"/>
		<input type="hidden" id="no" value="<spring:message code='generic.no' text='No'/>"/>
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
</body>
</html>