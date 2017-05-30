<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="cutmotiondate.list" text="List Of Cut Motion Date"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			onPageLoad();
				
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				listCutMotionDate();
			});	
			/**** Details ****/		
			$('#details_tab').click(function(){
				$("#selectionDiv1").hide();								
				editCutMotionDate();
			});			
			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){					
					reloadCutMotionDateGrid();					
				}	
			});	
			
			/**** house type changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){					
					reloadCutMotionDateGrid();					
				}	
			});
			
			/**** house type changes then reload grid****/			
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){					
					reloadCutMotionDateGrid();					
				}	
			});
			
			/**** device type changes then reload grid****/			
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadCutMotionDateGrid();
				}
			});	
			
			$("#selectedStatus").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadCutMotionDateGrid();
				}
			});
			
			/**** listWorkflowConfig method is called by default.****/
			listCutMotionDate();	
		});

		/**** displaying grid ****/					
		function listCutMotionDate() {
				showTabByIdAndUrl('list_tab','cutmotiondate/list?houseType='+$('#selectedHouseType').val()+
						'&deviceType='+$("#selectedDeviceType").val()+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+"&usergroup="+$("#userGroup").val()+
						"&usergroupType="+$("#userGroupType").val()+ "&role="+$("#role").val()+
						"&status="+$("#selectedStatus").val());
		}
		/**** new question ****/
		function newCutMotionDate() {
			$("#cancelFn").val("newCutMotionDate");
			var params=$("#gridURLParams").val();
			if(params==undefined){
				params="houseType="+$('#selectedHouseType').val()
				+"&deviceType="+$("#selectedDeviceType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()+
				"&sessionType="+$("#selectedSessionType").val()+
				"&usergroup="+$("#userGroup").val()+
				"&usergroupType="+$("#userGroupType").val()+
				"&role="+$("#role").val();
			}
			//since id of question has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','cutmotiondate/new?'+params);
		}
		/**** edit question ****/		
		function editCutMotionDate() {
			$("#cancelFn").val("editCutMotionDate");						
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
				var params=$("#gridURLParams").val();
				if(params==undefined){
					params="houseType="+$('#selectedHouseType').val()
					+"&deviceType="+$("#selectedDeviceType").val()+
					"&sessionYear="+$("#selectedSessionYear").val()+
					"&sessionType="+$("#selectedSessionType").val()+
					"&usergroup="+$("#userGroup").val()+
					"&usergroupType="+$("#userGroupType").val()+
					"&role="+$("#role").val();
				}
				showTabByIdAndUrl('details_tab','cutmotiondate/'+row+'/edit?'+params);
			}			
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			var params=$("#gridURLParams").val();
			if(params==undefined){
				params="houseType="+$('#selectedHouseType').val()
				+"&deviceType="+$("#selectedDeviceType").val()+
				"&sessionYear="+$("#selectedSessionYear").val()+
				"&sessionType="+$("#selectedSessionType").val()+
				"&usergroup="+$("#userGroup").val()+
				"&usergroupType="+$("#userGroupType").val()+
				"&role="+$("#role").val();
			}
			$('#editDeleteLinks').hide();
			$('#list_record').hide();
			$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'cutmotiondate/'+rowid+'/edit?'+params);
		}	
		/**** delete cutmotion date ****/	
		function deleteCutMotionDate() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('cutmotiondate/cmd/'+ row + '/delete', null, function(data, textStatus, XMLHttpRequest) {
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
		function reloadCutMotionDateGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+
						"&deviceType="+$("#selectedDeviceType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+
						"&usergroup="+$("#userGroup").val()+
						"&usergroupType="+$("#userGroupType").val()+
						"&role="+$("#role").val()+
						"&status="+$("#selectedStatus").val());
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}

		function onPageLoad() {
			
		}

		function prependOptionToSelectedDeviceType() {
			var optionValue = $('#allOption').val();
			var option = "<option value='0' selected>" + optionValue + "</option>";
			$('#selectedDeviceType').prepend(option);
		}	
		
		function showCurrentStatusReport(){
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
				$("#selectionDiv1").hide();
				var device = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text().split("_")[0];
				showTabByIdAndUrl('details_tab', "cutmotiondate/report/currentstatusreport?device="+ device +"&reportType=single&cutMotionDateId="+row);
			}			
		}
		
		function showCutmotionDatePatrakReport(){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$.get(url,function(data){
				if(data){
					//showTabByIdAndUrl('details_tab','motion/report/cutmotion/genreport?sessionId='+data.id+"&deviceTypeId="+$("#selectedDeviceType").val()+"&statusId=" + $("#selectedStatus").val() + "&locale="+$("#moduleLocale").val()+"&reportQuery=CUTMOTIONDATE_PATRAKBHAG2_REPORT&reportFileName=cutmotiondate_patrakbhag2_report");
					form_submit(
							'cutmotiondate/report/patrakbhag2', 
							{
								sessionId: data.id, 
								deviceTypeId: $('#selectedDeviceType').val(), 
								reportQuery: 'CUTMOTIONDATE_PATRAKBHAG2_REPORT', 
								xsltFileName: 'template_cutmotiondate_patrakbhag2',
								reportFileName: 'cutmotiondate_patrakbhag2',
								outputFormat: 'WORD'
							}, 
							'GET'
					);
				}
			});
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
				<spring:message code="cutmotion.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
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
				<spring:message code="cutmotion.sessionyear" text="Year" />
			</a> 
			<select name="selectedSessionYear" id="selectedSessionYear"
				style="width: 100px; height: 25px;">
				<c:forEach var="i" items="${years}">
					<c:choose>
						<c:when test="${i==sessionYear }">
							<option value="${i}" selected="selected">
								<c:out value="${i}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">
								<c:out value="${i}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> | 
			<a href="#" id="select_sessionType" class="butSim"> 
				<spring:message code="cutmotion.sessionType" text="Session Type" /> 
			</a> 
			<select name="selectedSessionType" id="selectedSessionType" style="width: 100px; height: 25px;">
				<c:forEach items="${sessionTypes}" var="i">
					<c:choose>
						<c:when test="${sessionType==i.id}">
							<option value="${i.id}" selected="selected">
								<c:out value="${i.sessionType}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">
								<c:out value="${i.sessionType}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> |
								
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="cutmotion.deviceType" text="Device Type"/>
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
			</select>	
			<select id="deviceTypeMaster" style="display: none;">
				<c:forEach items="${deviceTypes}" var="i">
					<option value="${i.id }">${i.type }</option>
				</c:forEach>
			</select> |		
			<hr>		
			<a href="#" id="select_status" class="butSim"> <spring:message code="generic.status" text="Status" /></a>
			<select name="selectedStatus" id="selectedStatus" style="width: 190px; height: 25px;">
				<c:forEach items="${status}" var="i">
					<option value="${i.id}">
						<c:out value="${i.name}"></c:out>
					</option>
				</c:forEach>
			</select> |	
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
		<input type="hidden" id="userGroup" value="${usergroup}" />
		<input type="hidden" id="userGroupType" value="${usergroupType}" />
		<input type="hidden" id="role" value="${role}" />
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		</div> 		
</body>
</html>