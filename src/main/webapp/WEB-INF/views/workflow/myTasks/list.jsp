<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#process').click(function(){
				process($('#key').val());
			});
			
			$("#search").click(function() {
				searchRecord();
			});
		});
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="process_record" class="butSim">
				<spring:message code="generic.details" text="Process"/>
			</a>  |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<%--value="els/grid/myTasks/data" changed to value="grid/myTasks/data" context changed --%>
	<input type="hidden" id="gridURL" value="grid/myTasks/data/">
	</div>
</body>
</html>
