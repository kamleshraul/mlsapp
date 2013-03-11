<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){				
			$('#list_tab').click(function(){
				showList();
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
				if(value!=""){		
					loadSubWorkflow(value);
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
				if(value!=""){				
					reloadMyTaskGrid();
				}
			});
					
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

			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list');	
		});
				
		function showList() {
			$("#selectionDiv").show();
			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list');								
		}
		
		function process(row) {
			var row = $('#key').val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("process");
			$("#selectionDiv").hide();
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process');			
		}			

		function rowDblClickHandler(row, iRow, iCol, e) {
			var row = $('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			$("#selectionDiv").hide();
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process');
		}		
		/**** reload grid ****/
		function reloadMyTaskGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&status="+$("#selectedStatus").val()
						+"&workflowSubType="+$("#selectedSubWorkflow").val()
						+"&assignee="+$("#assignee").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}
		
		 function loadSubWorkflow(deviceType){
			$.get('ref/status?deviceType='+ deviceType,function(data){
				$("#selectedSubWorkflow").empty();
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					selectedSubWorkflowText+="<option value='"+data[i].type+"'>"+data[i].name;
				}
				$("#selectedSubWorkflow").html(selectedSubWorkflowText);			
				}else{
					$("#selectedSubWorkflow").empty();
					var selectedSubWorkflowText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
					$("#selectedSubWorkflow").html(selectedSubWorkflowText);				
				}
			});
		} 
	</script>
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
			<hr>
			<a href="#" id="statusLabel" class="butSim">
				<spring:message code="mytask.status" text="Status"/>
			</a>
			<select id="selectedStatus" name="selectedStatus">
			<option value="PENDING"><spring:message code="mytask.pending" text="Pending"></spring:message></option>
			<option value="COMPLETED"><spring:message code="mytask.completed" text="Completed"></spring:message></option>
			</select> |								
			<a href="#" id="workflowLabel" class="butSim">
				<spring:message code="mytask.workflow" text="Workflow"/>
			</a>
			<select id="selectedSubWorkflow" name="selectedSubWorkflow">
			<c:forEach items="${workflowTypes}" var="i">
			<option value="${i.type}">${i.name}</option>
			</c:forEach>			
			</select> |		
			<hr>		
		</div>
		<div class="tabContent clearfix"></div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="assignee" value="${assignee}">		
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
		<input type="hidden" id="creationTime" name="creationTime" value="" />
	</div> 
</body>
</html>