<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflowconfig.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			onPageLoad();
				
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
						if($("#selectedModule option[value='']").html==null){
							prependOptionToSelectedModule();
						}else{
							$("#selectedModule").val('');
						}
						reloadWorkflowConfigGrid();
					}

		    	});	
			
				$('#selectedModule').change(function(){
					var value = $(this).val();
					if(value!=""){
						if($("#selectedDeviceType option[value='']").html()==null){
							prependOptionToSelectedDeviceType();
						}else{
							$("#selectedDeviceType").val('');
						}
						
					}
					reloadWorkflowConfigGrid();
				});
				
				$('#isLockedCheck').change(function(){
					var isLocked = $(this).is(':checked');
					//console.log("isLocked: " + isLocked);
					if(isLocked){
						$("#lockedConfig").val('YES');
					} else {
						$("#lockedConfig").val('');
					}
					//console.log("lockedConfig: " + $("#lockedConfig").val());
					reloadWorkflowConfigGrid();
				});
			/**** listWorkflowConfig method is called by default.****/
			listWorkflowConfig();	
		});

		/**** displaying grid ****/					
		function listWorkflowConfig() {
				showTabByIdAndUrl('list_tab','workflowconfig/list?houseType='+$('#selectedHouseType').val()
						+'&deviceType='+$("#selectedDeviceType").val()
						+'&module='+$("selectedModule").val());
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
				        $.delete_('workflowconfig/wfc/'+ row + '/delete', null, function(data, textStatus, XMLHttpRequest) {
							$.prompt(data);
							reloadWorkflowConfigGrid();
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
		/**** reload grid ****/
		function reloadWorkflowConfigGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+
						"&deviceType="+$("#selectedDeviceType").val()+
						"&module="+$("#selectedModule").val()+
						"&lockedConfig="+$("#lockedConfig").val());
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}

		function onPageLoad() {
			prependOptionToSelectedDeviceType();
			prependOptionToSelectedModule();
			$('#isLockedCheck').attr('checked', 'checked');
		}

		function prependOptionToSelectedDeviceType() {
			var optionValue = $('#pleaseSelectOption').val();
			var option = "<option value='0' selected>" + optionValue + "</option>";
			$('#selectedDeviceType').prepend(option);
		}	
		
		function prependOptionToSelectedModule() {
			var optionValue = $('#pleaseSelectOption').val();
			var option = "<option value='' selected>" + optionValue + "</option>";
			$('#selectedModule').prepend(option);
		}	
		
		function prependOptionToSelectedLockedStatus() {
			var optionValue = $('#pleaseSelectOption').val();
			var option = "<option value=''>" + optionValue + "</option>";
			$('#selectedLockedStatus').prepend(option);
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
			<option value="0" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
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
			<a id="moduletypeLabel" class="butSim" href="#"> Module </a>
				<select name="selectedModule" id="selectedModule" style="width:100px;height: 25px;">			
					<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
					<option value="REPORTING"><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
					<option value="EDITING"><spring:message code="mytask.editing" text="Editing"></spring:message></option>				
				</select> 	|
			<a id="isLockedCheckLabel" class="butSim" href="#" style="padding-right: 3px;">Locked</a>
			<input type="checkbox" id="isLockedCheck" class="sCheck" value="true">
			<input type="hidden" id="lockedConfig" value="YES" />
			<hr>	
		</div>				
		
		<div class="tabContent">
			<c:if test="${(error!='') && (error!=null)}">
				<h4 style="color: #FF0000;">${error}</h4>
			</c:if>
		</div>
			
		<input type="hidden" id="key" value="" />
 		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
				<input type="hidden" id="allOption" name="allOption" value="<spring:message code='generic.allOption' text='---- All ----'></spring:message>">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="pleaseSelectOption" name="pleaseSelectOption" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
		</div> 		
</body>
</html>