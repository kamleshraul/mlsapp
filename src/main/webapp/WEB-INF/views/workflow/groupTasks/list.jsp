<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.groupTasks.list" text="List of Group Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#claim_record').click(function(){
				claim($('#key').val());
			});
			
			$("#search").click(function() {
				searchRecord();
			});
		});

		function claim(row) {
			var row = $('#key').val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('groupTasks/' + row + '/claim', function(data){}, 'html');
		}
		
		// Double clicking the row should not result in any request
		// to the Server.
		function rowDblClickHandler(rowid, iRow, iCol, e) {}

	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="claim_record" class="butSim">
				<spring:message code="workflow.groupTasks.claim" text="Claim"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a>  
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURL" value="/els/grid/groupTasks/data/">
	</div>
</body>
</html>