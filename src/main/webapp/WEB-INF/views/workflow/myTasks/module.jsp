<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">		
		
		$(document).ready(function(){
			onPageLoad();
			
			$("#next_task").click(function(){
				nextTask();
			});
			
			$('#list_tab').click(function(){
				$("#nextTaskDiv").hide();
				showList();
			});	
			/**** Bulk Approval ****/
			$("#bulkapproval_tab").click(function(){
				bulkApproval();					
			});
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadMyTaskGrid();
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadMyTaskGrid();
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadMyTaskGrid();
				}			
			});
			/**** device type changes then reload grid****/			
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				var device = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text();
				if(device=='questions_shortnotice'){
					$("#shortNoticeAnswerDateDiv").show();
				}else{
					$("#shortNoticeAnswerDateDiv").hide();
				}
				if(value!=""){
					if($("#selectedModule option[value='']").html==null){
						prependOptionToSelectedModule();
					}else{
						$("#selectedModule").val('');
					}
					loadSubWorkflowByDeviceType(value);
					 $.get('ref/getTypeOfSelectedDeviceTypeFromName?deviceType='+ value,function(data){
						$('#deviceTypeType').val(data);
					}).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					}); 
				}
				var houseType = $("#houseTypeMaster option[value='"+$("#selectedHouseType").val()+"']").text();
				if((device.indexOf('questions_halfhourdiscussion_standalone')==0 && houseType=='lowerhouse') 
						|| device.indexOf('motions_')==0
						|| device.indexOf('resolutions_')==0
						|| device.indexOf('bills_')==0){
					$("#groupDiv").hide();
				}else{
					// The Group Div is to be hidden for department user
					if($('#currentusergroupType').val()!='department'){
						$("#groupDiv").show();
					}
				}
				
			});	
			/**** status changes then reload grid****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadMyTaskGrid();
				}
			});
			/**** workflow changes then reload grid****/			
			$("#selectedSubWorkflow").change(function(){
				var value=$(this).val();
				if(value!=null){				
					 if(($('#currentusergroupType').val()=="assistant"||$('#currentusergroupType').val()=="section_officer")&& value.contains("final")&&$('#currenthousetype').val()=='lowerhouse'){
						$('#bulkapproval_tab').hide();
						$('#selectedItemsCount').hide();
						$('#selectedFileCount').hide();
					}else{
						$('#bulkapproval_tab').show();
						$('#selectedItemsCount').show();
						$('#selectedFileCount').show();
					} 
					var deviceTypeForGrid = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text();
					/* if(deviceTypeForGrid=='motions_billamendment') {
						
					} else {
						reloadMyTaskGrid();
					} */	
					// Department users are allowed to search by number,subject and member ,
					//so new mytask grid is defined in db for department users.
					showTabByIdAndUrl('list_tab', 'workflow/myTasks/list?deviceTypeForGrid='+deviceTypeForGrid+'&currentusergroupType='+$('#currentusergroupType').val());
				}
			});

			/**** module changes then reload grid****/
			$('#selectedModule').change(function(){
				var value = $(this).val();
				if(value!=""){
					if($("#selectedDeviceType option[value='']").html()==null){
						prependOptionToSelectedDeviceType();
					}else{
						$("#selectedDeviceType").val('');
					}
					loadSubWorkflowByModule(value);
				}
				/*if(value=='EDITING'){
					$("#editingLinkDiv").show();
				}else{
					$("#editingLinkDiv").hide();
				}*/
			});
			
			$('#selectedGroup').change(function(){
				var value=$(this).val();
				if(value!=""){
					loadChartAnsweringDateByGroup(value);
					$('#answeringDateDiv').css('display','inline-block');
					
				}
				reloadMyTaskGrid();
			});
			
			$('#selectedAnsweringDate').change(function(){
				reloadMyTaskGrid();
				
			});
			/**** Keyboard Events ****/	
			$(document).keydown(function (e){
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					process($('#key').val());
				}
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			var deviceTypeForGrid = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text();
			// Department users are allowed to search by number,subject and member ,
			//so new mytask grid is defined in db for department users.
			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list?deviceTypeForGrid='+deviceTypeForGrid+'&currentusergroupType='+$('#currentusergroupType').val());
			pendingNewTasks();
			if($("#getNewTasks").val() != undefined && $("#getNewTasks").val() != ''){
				setInterval(function(){pendingNewTasks();}, 900000);
			}
			$("#notificationDiv").hide();
			$("#newTasksDiv").hide();	
			$("#notificationDiv").hide();
			$("#newMessageDiv").hide();
			
			$("#newMessageDivViewer").click(function(){
				
				if($("#newMessageDiv").css('display')=='none'){
					$(this).empty();
					$(this).html("<b>&#9658;</b>");
					pendingNewMessages();
				}else{
					$(this).empty();
					$(this).html("<b>&#9668;</b>");
				}
				$("#newMessageDiv").toggle();
			});
			//Some filters are not required for department screen. this filters are hidden in departmentutility function
			departmentUtility();
						
		});
				
		//to get the new pending tasks
		function pendingNewTasks(){
			if($("#getNewTasks").val() != undefined && $("#getNewTasks").val() != ''){
				var url = "ref/newpendingtasks?sessionYear=" + $("#selectedSessionYear").val() +
							"&sessionType=" + $("#selectedSessionType").val() + 
							"&houseType=" + $("#selectedHouseType").val() +
							"&status=PENDING";
				$.get(url, function(data){
					if(data){
						$("#notificationDiv").html(data.value);
						$("#notificationDiv").show();
					}
				});
			}			  
		}
		
		//to get the new pending messages
		function pendingNewMessages(){
			
			var url = "ref/newpendingmessages?sessionYear=" + $("#selectedSessionYear").val() +
						"&sessionType=" + $("#selectedSessionType").val() + 
						"&houseType=" + $("#selectedHouseType").val();
			$.get(url, function(data){
				$("#newMessageDiv").empty();
				if(data.length > 0){
					var text = "<table style='margin: 10px; padding: 2px;'>";
					for(var i = 0; i < data.length; i++){
						text += "<tr><td style='text-align: center; margin: 2px; padding: 2px;'><a hrfe='javascript:void(0);' id='message" + data[i][0] + "' onclick='updateMessage("+ data[i][0] +")' title='" + data[i][0] + "'>" + data[i][1]+"</a></td>"
								+ "<td style='text-align: center; margin: 2px; padding: 2px;'>" + data[i][2] + "</td><td style='text-align: center; margin: 2px; padding: 2px;'>" + data[i][3] + "</td></tr>";						
					}
					text += "</table>";
					$("#newMessageDiv").html(text);
				}
			});
		}
		
		function updateMessage(val){
			if($("#message"+val).attr('title') != 'SUCCESS'){
				$.post("pushmessage/"+ val+"/updateasread?read=yes", function(data){
					if(data){
						$("#message"+val).attr('title',data);
					}
				});
			}
		}
		
		function showList() {
			$("#selectionDiv").show();
			var deviceTypeForGrid = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text();
			// Department users are allowed to search by number,subject and member ,
			//so new mytask grid is defined in db for department users.
			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list?deviceTypeForGrid='+deviceTypeForGrid+'&currentusergroupType='+$('#currentusergroupType').val());
		}
		
		function process(row) {
			var row = $('#key').val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("process");
			$("#selectionDiv").hide();
			if($("#selectedModule").val()=='EDITING'){
				showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process'+'?action=edited');
			}else{
				showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process');
			}
		}		
		
		function showEditCopyOfMember(sentParams){
			var params='?houseType='+$("#selectedHouseType").val()
					+ '&sessionType='+$("#selectedSessionType").val()
					+ '&sessionYear='+$("#selectedSessionYear").val()
					+ '&userGroup='+$("#currentusergroup").val()
					+ '&userGroupType='+$("#currentusergroupType").val()
					+ '&selectedSubWorkflow='+$("#selectedSubWorkflow").val()
					+ '&action=edit';
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + $("#key").val() + '/process'+params+sentParams);
		}
		function showEditCopy(){
			var params='?houseType='+$("#selectedHouseType").val()
			+ '&sessionType='+$("#selectedSessionType").val()
			+ '&sessionYear='+$("#selectedSessionYear").val()
			+ '&userGroup='+$("#currentusergroup").val()
			+ '&userGroupType='+$("#currentusergroupType").val()
			+ '&selectedSubWorkflow='+$("#selectedSubWorkflow").val()
			+ '&action=edit';
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + $("#key").val() + '/process'+params);
		}
		function showEditedCopy(){
			var params='?houseType='+$("#selectedHouseType").val()
			+ '&sessionType='+$("#selectedSessionType").val()
			+ '&sessionYear='+$("#selectedSessionYear").val()
			+ '&userGroup='+$("#currentusergroup").val()
			+ '&userGroupType='+$("#currentusergroupType").val()
			+ '&selectedSubWorkflow='+$("#selectedSubWorkflow").val()
			+ '&action=edited';
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + $("#key").val() + '/process'+params);
		}

		function rowDblClickHandler(row, iRow, iCol, e) {
			/**** To maintain the grid ids to allow nextTask to be show next ***/
			$("#currentRowId").val(row);
			$("#persistentGridRowId").val(row);
			$("#allRowIds").val($('#grid').jqGrid('getDataIDs'));
			$("#nextTaskDiv").show();
			
			var row = $('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			$("#selectionDiv").hide();
			if($("#selectedModule").val()=='EDITING'){
				var params = "?action=edited"
						+"&houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&userGroup="+$("#currentusergroup").val()
						+"&userGroupType="+$("#currentusergroupType").val()
						+"&selectedSubWorkflow="+ $("#selectedSubWorkflow").val();
				
				showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process'+params);
			}else{
				showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process');
			}
		}		
		
		function showUneditedCopy(){
			
		}
		/**** reload grid ****/
		function reloadMyTaskGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&module="+$("#selectedModule").val()
						+"&status="+$("#selectedStatus").val()
						+"&workflowSubType="+$("#selectedSubWorkflow").val()
						+"&assignee="+$("#assignee").val()
						+"&group="+$("#selectedGroup").val()
						+"&answeringDate="+$("#selectedAnsweringDate").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");	
				
		}	
		
		
		function loadSubWorkflowByDeviceType(deviceType){
			$.get('ref/status?deviceType='+ deviceType,function(data){
				$("#selectedSubWorkflow").empty();
				var selectedSubWorkflowText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						/* if(data[i].value=='question_final_admission'){
							selectedSubWorkflowText+="<option value='"+data[i].value+"' selected='selected'>"+data[i].name;
						}else{
							selectedSubWorkflowText+="<option value='"+data[i].value+"'>"+data[i].name;
						} */
						selectedSubWorkflowText+="<option value='"+data[i].value+"'>"+data[i].name;
					}
				}else{
					selectedSubWorkflowText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				}
				$("#selectedSubWorkflow").html(selectedSubWorkflowText);
			}).done(function(){
				$('#selectedSubWorkflow').trigger('change');
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		} 

		function loadSubWorkflowByModule(module){
			$.get('ref/workflowTypes?module='+ module,function(data){
				$("#selectedSubWorkflow").empty();
				var selectedSubWorkflowText="";
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						selectedSubWorkflowText+="<option value='"+data[i].type+"'>"+data[i].name;
					}
				}else{
					selectedSubWorkflowText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				}
				$("#selectedSubWorkflow").html(selectedSubWorkflowText);
			}).done(function(){
				$('#selectedSubWorkflow').trigger('change');
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		} 
		
		
		/**** Bulk Approval ****/
		function bulkApproval(){
			var resourceURL="";
			if($('#deviceTypeType').val().contains("resolutions_")){
				resourceURL="workflow/resolution/bulkapproval/init";					
			}else if($('#deviceTypeType').val().contains("motions_")){
				if($('#deviceTypeType').val().indexOf("motions_cutmotion_")==0){
					resourceURL="workflow/cutmotion/bulkapproval/init";	
				}else{
					resourceURL="workflow/cutmotion/bulkapproval/init";
				}				
			}else if($('#deviceTypeType').val().contains("questions_")){
				resourceURL="workflow/question/bulkapproval/init";
			}
			$("#selectionDiv").hide();
			var file=$("#selectedFileCount").val();	
			$.post(resourceURL,{houseType:$("#selectedHouseType").val(),
				sessionYear:$("#selectedSessionYear").val(),
				sessionType:$("#selectedSessionType").val(),
				deviceType:$("#selectedDeviceType").val(),
				status:$("#selectedStatus").val(),
				workflowSubType:$("#selectedSubWorkflow").val(),
				itemsCount:$("#selectedItemsCount").val(),
				file:file,
				group:$('#selectedGroup').val(),
				answeringDate:$('#selectedAnsweringDate').val()
				},function(data){
				$('a').removeClass('selected');
				$('#bulkputup_tab').addClass('selected');
				$('.tabContent').html(data);
				scrollTop();
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		
		/**** To enable the next task link ****/
		function nextTask(){
			var currentrowid = $("#currentRowId").val(); 
			var allIds = $("#allRowIds").val();
			var allNextIds = allIds.substring(allIds.indexOf(currentrowid)+currentrowid.length+1);
			if(allNextIds.length>0){
				$(".tabContent").hide();
				//filler to neutralize the previous content i.e. previous question id 
				showTabByIdAndUrl('list_tab', 'ref/dummypage');
				var nextRowId = allNextIds.split(',')[0];
			
				//console.log(allNextIds+"\n"+nextRowId+"\n"+isValidRow(allIds, nextRowId));
					
				$("#currentRowId").val(nextRowId);
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + nextRowId + '/process');
				if(isValidRow(allIds, nextRowId)){
					setTimeout(function(){
						$.unblockUI();
					}, 800);
					$(".tabContent").show();
				}else{
					$(".tabContent").show();
					$.unblockUI();
				}
			}
		}
		
		function isValidRow(ids, row){
			return (ids.contains(row));
		}

		function onPageLoad() {
			prependOptionToSelectedModule();
		}

		function prependOptionToSelectedDeviceType() {
			var optionValue = $('#pleaseSelectOption').val();
			var option = "<option value='' selected>" + optionValue + "</option>";
			$('#selectedDeviceType').prepend(option);
		}

		function prependOptionToSelectedModule() {
			var optionValue = $('#pleaseSelectOption').val();
			var option = "<option value='' selected>" + optionValue + "</option>";
			$('#selectedModule').prepend(option);
		}
		
		/****Provide introduction date ****/
		function provideDate(){
			showTabByIdAndUrl('details_tab','bill/providedate?houseType=' + $("#selectedHouseType").val()+"&sessionType="+$("#selectedSessionType").val()+"&sessionYear="+$("#selectedSessionYear").val());
		}
		
		/**** To Generate Intimation Letter ****/
		function generateIntimationLetter() {			
			var workflowId = $("#grid").jqGrid ('getGridParam', 'selrow');
			if(workflowId==undefined || workflowId=='') {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else {			
				$('#generateIntimationLetter').attr('href', 'question/report/generateIntimationLetter?workflowId='+workflowId+'&intimationLetterFilter='+$("#intimationLetterFilter").val());
			}
		}
		
		function showCurrentStatusReport(val, wfdId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', "question/report/currentstatusreport?device="+device+"&reportType="+val+"&wfdId="+wfdId);
		}
		
		function loadChartAnsweringDateByGroup(value){
			$.get('ref/getChartAnsweringDateByGroup?group='+$("#selectedGroup").val()
					+ '&houseType='+$("#selectedHouseType").val()
					+ '&sessionType='+$("#selectedSessionType").val()
					+ '&sessionYear='+$("#selectedSessionYear").val(),function(data){
				var text="<option value=''>"+$('#pleaseSelectOption').val()+"</option>";
				if(data.length>0){
					$('#selectedAnsweringDate').empty();
					for(var i=0;i<data.length;i++){
						text=text+"<option value='"+data[i].value+"'>"+data[i].name+"</option>";	
					}
					$('#selectedAnsweringDate').html(text);
				}
			});
		}
		
		function showShortNoticeAnswerDateReport(wfId){
			$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab','question/report/shortnoticeanswerdatereport?wfdId='+ wfId);
		}		
		
		function departmentUtility(){
			if($('#currentusergroupType').val()=='department'){
				$('#moduleFilter').css('display','none');
				$('#groupDiv').css('display','none');
			}
		}
	</script>
	
	<style type="text/css">
		#nextTaskDiv{
			background: #A6F7BE scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			/*border-radius: 10px;*/
			text-align: center;
			border: 1px solid black;
			z-index: 5000;
			bottom: 5px;
			right: 90px;			
			position: fixed;
			cursor: pointer;
		}
		#notificationDiv{
			background: #FCCD32 scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			/*border-radius: 10px;*/
			text-align: center;
			border: 1px solid black;
			z-index: 5000;
			bottom: 5px;
			right: 5px;			
			position: fixed;
			cursor: pointer;
		}
		#newTasksDiv{
			background: #FCCD32 scroll no-repeat;
			max-width: 400px;
			width: 350px;
			max-height: 600px;
			height: 80px;
			/*border-radius: 10px;*/
			padding-left 5px;
			border: 1px solid black;
			z-index: 5000;
			bottom: 25px;
			right: 5px;			
			position: fixed;
			overflow: auto;
		}
		
		.newMessageDivNormal{
			background: #D4F4FF scroll no-repeat;
			width: 80px;
			border: 1px solid black;
			z-index: 4000;
			bottom: 35px;
			right: 5px;			
			position: fixed;
			cursor: pointer;
			text-align: center
		}
		
		.newMessageDivBig{
			background: #D4F4FF scroll no-repeat;
			width: 300px;
			height: 250px;
			border: 1px solid black;
			z-index: 4000;
			bottom: 25px;
			right: 25px;			
			position: fixed;
			cursor: pointer;
		}
		#newMessageDivViewer{
			background: #0A469A scroll no-repeat;
			width: 15px;
			height: 15px;
			border: 1px solid black;
			z-index: 5000;
			bottom: 25px;
			right: 5px;			
			position: fixed;
			cursor: pointer;
		}
	</style>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<li>
				<a id="process_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>
			<c:if test="${usergroupType!='department'}">
				<li>
					<a id="bulkapproval_tab" href="#" class="tab">
					   <spring:message code="generic.bulkputup" text="Bulk Putup"></spring:message>
					</a>
				</li>
			</c:if>
					
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv">	
			<a href="#" id="houseTypeLabel" class="butSim">
				<spring:message code="mytask.housetype" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseType==i.type}">
			<option value="${i.name}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |					
			<a href="#" id="sessionYearlabel" class="butSim">
				<spring:message code="mytask.sessionYear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:100px;height: 25px;">				
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
			<a href="#" id="sessiontypeLabel" class="butSim">
				<spring:message code="mytask.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">				
			<c:forEach items="${sessionTypes}" var="i">
			<c:choose>
			<c:when test="${sessionType==i.id}">
			<option value="${i.sessionType}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i.sessionType}"><c:out value="${i.sessionType}"></c:out></option>			
			</c:otherwise>
			</c:choose>			
			</c:forEach> 
			</select> |				
			<a href="#" id="devicetypeLabel" class="butSim">
				<spring:message code="mytask.deviceType" text="Device Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:100px;height: 25px;">			
			<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
			</c:forEach>
			</select> |
			<div id='moduleFilter' style='display:inline;'>
			<a href="#" id="moduletypeLabel" class="butSim">
				<spring:message code="mytask.module" text="Module"/>
			</a>
			<select name="selectedModule" id="selectedModule" style="width:100px;height: 25px;">			
				<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
				<option value="REPORTING"><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
				<option value="EDITING"><spring:message code="mytask.editing" text="Editing"></spring:message></option>				
			</select> |
			</div>	
			<a href="#" id="statusLabel" class="butSim">
				<spring:message code="mytask.status" text="Status"/>
			</a>
			<select id="selectedStatus" name="selectedStatus">
			<option value="PENDING"><spring:message code="mytask.pending" text="Pending"></spring:message></option>
			<option value="COMPLETED"><spring:message code="mytask.completed" text="Completed"></spring:message></option>
			<option value="TIMEOUT"><spring:message code="mytask.timeout" text="Timeout"></spring:message></option>
			</select> |	
			<hr>
			<div id="groupDiv" style='display: inline;'>
				<a href="#" id="workflowLabel" class="butSim">
					<spring:message code="mytask.group" text="Group"/>
				</a>
				<select id="selectedGroup" name="selectedGroup" class="sSelect">
				<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message></option>			
					<c:forEach items="${groups}" var="i">
						<option value="${i.name}">${i.name}</option>
					</c:forEach>			
			</select>|
			</div>
			<div id='answeringDateDiv' style='display: none;' >
			<a href="#" id="workflowLabel" class="butSim" >
				<spring:message code="mytask.chartAnsweringDate" text="Answering Date"/>
			</a>
			<select id="selectedAnsweringDate" name="selectedAnsweringDate" class="sSelect">
				<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message></option>
			</select>|
			</div>										
			<a href="#" id="workflowLabel" class="butSim">
				<spring:message code="mytask.workflow" text="Workflow"/>
			</a>
			<select id="selectedSubWorkflow" name="selectedSubWorkflow">
			<option value=""><spring:message code='please.select' text='Please Select'/></option>			
			<c:forEach items="${workflowTypes}" var="i">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>			
			</select>| 
			<div id="subWFMasterDiv" style="display: none;">
				<select id="subWFMaster">
					<c:forEach items="${workflowTypes}" var="i">
						<option value="${i.type}">${i.id}</option>
					</c:forEach>
				</select>
			</div> 
			<hr>
			<c:if test="${usergroupType!='department'}">
				<a href="#" id="select_itemcount" class="butSim">
					<spring:message code="device.itemcount" text="No. of Devices(Bulk Putup)"/>
				</a>
				<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
					<option value="100">100</option>
					<option value="75">75</option>
					<option value="50">50</option>
					<option value="25">25</option>
					<option value="10">10</option>
					<option value="5">05</option>		
				</select>|	
				<a href="#" id="select_filecount" class="butSim">
					<spring:message code="motion.filecount" text="Select File(Bulk Putup)"/>
				</a>
				<select name="selectedFileCount" id="selectedFileCount" style="width:100px;height: 25px;">			
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>		
				</select>|
			</c:if>		
		</div>
		<div id="nextTaskDiv" style="display: none;">
			<a href="#" id="next_task" class="butSim" style="text-decoration: none;">	
				<spring:message code="generic.next_task" text="Next"/>
			</a>
		</div>
		<div class="tabContent clearfix">
			<c:if test="${(error!='') && (error!=null)}">
				<h4 style="color: #FF0000;">${error}</h4>
			</c:if>
		</div>		
		
		<div id="newTasksDiv">
			Content
		</div>
		
		<div id="notificationDiv">
			V
		</div>
		
		<div id="newMessageDivViewer">
			<b>&#9668;</b>
		</div>
		
		<div id="newMessageDiv" class="newMessageDivBig">
			Message
		</div>			
		
		<div style="display: none;">
			<select id="deviceTypeMaster">			
				<c:forEach items="${deviceTypes}" var="i">
					<option value="${i.name}"><c:out value="${i.type}"></c:out></option>			
				</c:forEach>
			</select>
		</div>
		
		<div style="display: none;">
			<select id="houseTypeMaster">			
				<c:forEach items="${houseTypes}" var="i">
					<option value="${i.name}"><c:out value="${i.type}"></c:out></option>			
				</c:forEach>
			</select>
		</div>
		
		<input type="hidden" id="getNewTasks" value="yes" />
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">
		<input type="hidden" name="currenthousetype" id="currenthousetype" value="${houseType}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="assignee" value="${assignee}">	
		<input type="hidden" id="deviceTypeType" value="${selectedDeviceType}"/>	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
		<input type="hidden" id="creationTime" name="creationTime" value="" />		
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="currentRowId" value="" />
		<input type="hidden" id="allRowIds" value="" />
		<input type="hidden" id="persistentGridRowId" value="" />
		<input type="hidden" id="pleaseSelectOption" name="pleaseSelectOption" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
	</div> 
</body>
</html>