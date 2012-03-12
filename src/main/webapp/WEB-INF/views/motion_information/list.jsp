<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="mois.list.title" text="List of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#new_record').click(function(){
				newRecord();
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
		});
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="add_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="view_record" class="butSim">
			<spring:message code="generic.edit" text="View"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="print_record" class="butSim">
				<spring:message code="generic.print" text="Print"/>
			</a> |
			<a href="#" id="search_record" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<a href="#" id="report_record" class="butSim">
				<spring:message code="generic.report" text="Report"/>
			</a> |
			<a href="#" id="statistics_record" class="butSim">
				<spring:message code="generic.statistics" text="Statistics"/>
			</a> 
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
