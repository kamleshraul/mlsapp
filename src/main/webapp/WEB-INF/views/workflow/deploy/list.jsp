<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.deploy.list" text="List of Deployments"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#new_record').click(function(){
				newRecord();
			});
			
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			
			$("#search").click(function() {
				searchRecord();
			});
		});

		// Double clicking the row should not result in any request
		// to the Server.
		function rowDblClickHandler(rowid, iRow, iCol, e) {}
		
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
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<a href="#" id="preview" class="butSim">
				<spring:message code="generic.preview" text="Preview"/>
			</a> 
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURL" value="/els/grid/deployment/data/">
	</div>
</body>
</html>
