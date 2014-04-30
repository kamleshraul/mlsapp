<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){ 
			/**** Initially we want to get only those tasks which belongs to current user and of selected status ****/
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&deviceType="+$("#selectedDeviceType").val()
						+"&module="+$("#selectedModule").val()
						+"&status="+$("#selectedStatus").val()
						+"&workflowSubType="+$("#selectedSubWorkflow").val()
						+"&assignee="+$("#assignee").val()
						);
			$('#process').click(function(){
				process($('#key').val());
			});			
			$("#search").click(function() {
				searchRecord();
			});
			$("#provide_date").click(function(){	
				$("#selectionDiv").hide();
				provideDate();
			});
			
			$("#generateCurrentStatusReport").click(function(){
				showCurrentStatusReport();
			});
		});
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
			<a href="#" id="process_record" class="butSim">
				<spring:message code="generic.details" text="Process"/>
			</a>  |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a>  
			<security:authorize access="hasAnyRole('BILL_DEPARTMENT_USER')">			
			| <a href="#" id="provide_date" class="butSim">
				<spring:message code="generic.giveintroductiondate" text="Provide Introduction Date"/>
			</a>
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY','QIS_UNDER_SECRETARY','QIS_UNDER_SECRETARY_COMMITTEE','QIS_SECTION_OFFICER')">
				|
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
			 </security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	<script type="text/javascript">
		$('#grid').jqGrid('setSelection',$("#persistentGridRowId").val());
	</script>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	</div>
</body>
</html>
