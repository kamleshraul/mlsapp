<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="constituency.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#new_record').click(function(){
				//console.log("yamete")
				var id = 'details_tab';
				var url = 'committee/adminCommitteeCreation/new';
				showTabByIdAndUrl(id, url);
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				//deleteRecord($('#key').val());
				$.prompt('Not Allowed  to delete Committee ')
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#test_report_link").click(function() {
				//testReport();
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
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a><%--  |
			<a href="#" id="test_report_link" class="butSim">
				<spring:message code="generic.testreport" text="Test Report"/>
			</a> --%>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
