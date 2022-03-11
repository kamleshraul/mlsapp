<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="billamendmentmotion.list" text="List Of Bill Amendment Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();				
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
				showBillAmendmentMotionList();
			});			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadBillAmendmentMotionGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadBillAmendmentMotionGrid();								
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadBillAmendmentMotionGrid();							
				}			
			});
			/**** billamendmentmotion type changes ****/		
			$("#selectedMotionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadBillAmendmentMotionGrid();							
				}				
			});	
			/**** status changes then reload grid****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadBillAmendmentMotionGrid();
					$("#selectedFileCount").val("-");
				}
			});	
			/**** bill year changes then reload grid****/			
			$("#selectedBillYear").change(function(){
				findBillsForGivenCombinationOfYearAndHouseType();
				reloadBillAmendmentMotionGrid();	
			});
			/**** bill house type changes then reload grid****/			
			$("#selectedBillHouseType").change(function(){
				findBillsForGivenCombinationOfYearAndHouseType();
				reloadBillAmendmentMotionGrid();	
			});
			/**** bill number changes then reload grid****/
			$("#selectedBillNumber").change(function(){
				var value=$(this).val();					
				if(value=="0" && $('#selectedBillNumber option').size()>1){
					var billIds = "";
					$("#selectedBillNumber option").each(function() {
						if($(this).val()!="0") {
							billIds += $(this).val() + ",";
						}
					});					
					if(billIds == "") {
						billIds = "0";
					} else {
						billIds = billIds.substring(0, billIds.length-1);
					}					
					$('#selectedBillIds').val(billIds);					
				} else {
					$('#selectedBillIds').val(value);
				}
				reloadBillAmendmentMotionGrid();
			});
			/**** Ballot ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
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
			/**** show billamendmentmotion list method is called by default.****/
			showBillAmendmentMotionList();				
		});
		/**** displaying grid ****/					
		function showBillAmendmentMotionList() {
				if($('#selectedBillNumber option').size()<=1) {
					findBillsForGivenCombinationOfYearAndHouseType();
				}						
				showTabByIdAndUrl('list_tab','billamendmentmotion/list?houseType='+$('#selectedHouseType').val()
						+'&motionType='+$("#selectedMotionType").val()+'&sessionYear='
						+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+
						"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()	
						+"&bills="+$('#selectedBillIds').val()
						);
		}
		/**** new billamendmentmotion ****/
		function newBillAmendmentMotion() {		
			$("#cancelFn").val("newBillAmendmentMotion");
			//since id of billamendmentmotion has not been created so key is set to empty value
			$("#key").val("");	
			var amendedBillId = $('#selectedBillNumber').val();			
			showTabByIdAndUrl('details_tab','billamendmentmotion/new?'
					+$("#gridURLParams").val()+'&amendedBillId='+amendedBillId);
		}
		/**** edit billamendmentmotion ****/		
		function editBillAmendmentMotion(row) {
			$("#cancelFn").val("editBillAmendmentMotion");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','billamendmentmotion/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);			
			showTabByIdAndUrl('details_tab', 'billamendmentmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete billamendmentmotion ****/	
		function deleteBillAmendmentMotion() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				deviceNumber = $("#grid").jqGrid ('getCell', row, 'number');
			    if(deviceNumber!='-') {
			    	$.prompt($('#submittedParliamentaryDevicesCannotBeDeletedPrompt').val());
					return;
			    } else {
					$.prompt($('#confirmDeleteMessage').val()+ row,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
					        $.delete_('billamendmentmotion/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					        	showBillAmendmentMotionList();
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
		}
		/**** reload grid ****/
		function reloadBillAmendmentMotionGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+
						"&motionType="+$("#selectedMotionType").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						+"&bills="+$('#selectedBillIds').val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'ballot/billamendmentmotion/init?' + parameters +"&deviceType="+$("#selectedMotionType").val();
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'billamendmentmotion/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}	
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='billamendmentmotion/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}	
		/**** Find bills for given combination of bill year & introducing housetype ****/
		function findBillsForGivenCombinationOfYearAndHouseType() {
			$('#selectedBillIds').val("");
			var billIds = "";
			var parameters = "billYear="+$('#selectedBillYear').val()
					+"&billHouseType="+$('#selectedBillHouseType').val();
			$.ajax({url: 'ref/findBillsForGivenCombinationOfYearAndHouseType', data: parameters, 
				type: 'GET',
		        async: false,
				success: function(data) {
					$('#selectedBillNumber').empty();
					var selectedBillNumberHtml = "";
					selectedBillNumberHtml += "<option value='0'>"+$('#pleaseSelect').val()+"</option>";
					if(data.length>0) {					
						for(var i=0; i<data.length; i++) {
							selectedBillNumberHtml += "<option value='"+data[i].id+"'>"+data[i].formattedNumber+"</option>";
							billIds += data[i].id;
							if(i!=data.length-1) {
								billIds += ",";
							}
						}		
						if($('#selectedBillYear').val()!=undefined && $('#selectedBillYear').val()!="" && $('#selectedBillYear').val()!="0"
							&& $('#selectedBillHouseType').val()!=undefined && $('#selectedBillHouseType').val()!="") {
							$('#selectedBillNumberSpan').show();
						} else {
							$('#selectedBillNumberSpan').hide();
						}					
					} else {	
						billIds = "0";
						$('#selectedBillNumberSpan').hide();
					}		
					$('#selectedBillNumber').html(selectedBillNumberHtml);					
				}				
			});
			if(billIds=="") {
				billIds = "0";
			}
			$('#selectedBillIds').val(billIds);								
		}
	</script>
</head>
<body>
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
			<security:authorize access="hasAnyRole('BAMOIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('BAMOIS_ASSISTANT', 'BAMOIS_UNDER_SECRETARY',
				'BAMOIS_DEPUTY_SECRETARY', 'BAMOIS_PRINCIPAL_SECRETARY', 'BAMOIS_SPEAKER', 'BAMOIS_JOINT_SECRETARY',
				'BAMOIS_SECRETARY', 'BAMOIS_OFFICER_ON_SPECIAL_DUTY', 'BAMOIS_DEPUTY_SPEAKER', 'BAMOIS_CHAIRMAN',
				'BAMOIS_DEPUTY_CHAIRMAN', 'BAMOIS_SECTION_OFFICER', 'BAMOIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<c:if test="${houseType=='lowerhouse'}">
					<li>
					<a id="ballot_tab" href="#" class="tab">
				   		<spring:message code="billamendmentmotion.memberballot" text="Ballot"></spring:message>
					</a>
					</li>			
					</c:if>				
			</security:authorize>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="billamendmentmotion.houseType" text="House Type"/>
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
				<spring:message code="billamendmentmotion.sessionyear" text="Year"/>
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
				<spring:message code="billamendmentmotion.sessionType" text="Session Type"/>
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
						
			<a href="#" id="select_motionType" class="butSim">
				<spring:message code="billamendmentmotion.motionType" text="Motion Type"/>
			</a>
			<select name="selectedMotionType" id="selectedMotionType" style="width:99px;height: 25px;">			
			<c:forEach items="${motionTypes}" var="i">
			<c:choose>
			<c:when test="${motionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${motionTypes }" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>			
			</select>|		
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','BAMOIS_TYPIST')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="billamendmentmotion.status" text="Status"/>
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
			
			<hr>
			
			<a href="#" id="select_bill_year" class="butSim">
				<spring:message code="billamendmentmotion.billyear" text="Bill Year"/>
			</a>
			<select name="selectedBillYear" id="selectedBillYear" style="width:99px;height: 25px;">		
				<option value="0" ><spring:message code="please.select" text="Please Select"/></option>		
				<c:forEach var="i" items="${years}">			
					<option value="${i}" ><c:out value="${i}"></c:out></option>
				</c:forEach> 
			</select> |	
			
			<a href="#" id="select_bill_houseType" class="butSim">
				<spring:message code="billamendmentmotion.billhousetype" text="Bill House Type"/>
			</a>
			<select name="selectedBillHouseType" id="selectedBillHouseType" style="width:99px;height: 25px;">		
				<option value="" ><spring:message code="please.select" text="Please Select"/></option>		
				<c:forEach var="i" items="${billHouseTypes}">			
					<option value="${i.type}" ><c:out value="${i.name}"></c:out></option>
				</c:forEach> 
			</select> |	
			
			<span id="selectedBillNumberSpan" style="display: none;">
			<a href="#" id="select_bill_number" class="butSim">
				<spring:message code="billamendmentmotion.billNumber" text="Bill Number"/>
			</a>
			<select name="selectedBillNumber" id="selectedBillNumber" style="width:99px;height: 25px;">		
				<option value="0" ><spring:message code="please.select" text="Please Select"/></option>						
			</select> |	
			</span>
			
			<input type="hidden" id="selectedBillIds" name="selectedBillIds"/>
			
			<hr>
					
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
					
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="billamendmentmotion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:99px;height: 25px;">			
			<option value="30">30</option>
			<option value="25">25</option>
			<option value="20">20</option>
			<option value="15">15</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			</security:authorize>	
			<security:authorize access="hasAnyRole('BAMOIS_ASSISTANT')">						
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="billamendmentmotion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:99px;height: 25px;">			
			<option value="100">100</option>
			<option value="75">75</option>
			<option value="50">50</option>
			<option value="25">25</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			<a href="#" id="select_filecount" class="butSim">
				<spring:message code="billamendmentmotion.filecount" text="Select File(Bulk Putup)"/>
			</a>
			<select name="selectedFileCount" id="selectedFileCount" style="width:99px;height: 25px;">			
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
			<c:if test="${highestFileNo>0 }">
			<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
			<option value="${i}">${i}</option>
			</c:forEach>
			</c:if>						
			</select>|	
			<hr>
			</security:authorize>											
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">				
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${motionTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		</div> 	
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
</body>
</html>