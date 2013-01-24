<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflowconfig.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				listWorkflowConfig();
			});	
			/**** Details ****/		
			$('#details_tab').click(function(){
				$("#selectionDiv1").show();								
				editWorkflowConfig();
			});			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){					
					reloadWorkflowConfigGrid();					
				}	
			});	
			/**** device type changes then reload grid****/			
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				if(value!=""){				
				reloadWorkflowConfigGrid();
				}
			});	
			/**** listWorkflowConfig method is called by default.****/
			listWorkflowConfig();	
		});

		/**** displaying grid ****/					
		function listWorkflowConfig() {
				showTabByIdAndUrl('list_tab','workflowconfig/list?houseType='+$('#selectedHouseType').val()
						+'&deviceType='+$("#selectedDeviceType").val());
		}
		/**** new question ****/
		function newWorkflowConfig() {
			$("#cancelFn").val("newWorkflowConfig");
			var params=$("#gridURLParams").val();
			if(params==undefined){
				params="houseType="+$('#selectedHouseType').val()
				+"&deviceType="+$("#selectedDeviceType").val();
			}
			//since id of question has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','workflowconfig/new?'+params);
		}
		/**** edit question ****/		
		function editWorkflowConfig() {
			$("#cancelFn").val("editWorkflowConfig");						
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
				var params=$("#gridURLParams").val();
				if(params==undefined){
					params="houseType="+$('#selectedHouseType').val()
					+"&deviceType="+$("#selectedDeviceType").val();
				}
			showTabByIdAndUrl('details_tab','workflowconfig/'+row+'/edit?'+params);
			}			
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			var params=$("#gridURLParams").val();
			if(params==undefined){
				params="houseType="+$('#selectedHouseType').val()
				+"&deviceType="+$("#selectedDeviceType").val();
			}
			$('#editDeleteLinks').show();
			$('#list_record').hide();
			$("#selectionDiv1").show();
			showTabByIdAndUrl('details_tab', 'workflowconfig/'+rowid+'/edit?'+params);
		}	
		/**** delete question ****/	
		function deleteWorkflowConfig() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('workflowconfig/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
							$.prompt(data);
						    showQuestionList();
				        });
			        }
				}});
			}
		}
		/**** reload grid ****/
		function reloadWorkflowConfigGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+
						"&deviceType="+$("#selectedDeviceType").val());
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
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
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="workflowconfig.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseType==i.type}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |						
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="workflowconfig.deviceType" text="Device Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:100px;height: 25px;">			
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
			</select> |								
			<hr>						
		</div>				
		
		<div class="tabContent">
		</div>
			
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		</div> 		
</body>
</html>