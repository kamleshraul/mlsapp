<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.list" text="List Of Members"/></title>
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
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('personal_tab', 'member/personal/'+rowid+'/edit');
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}
	</script>
</head>
<body>
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
			</a> <!-- |
			<a href="#" id="send_for_approval" class="butSim">
				<spring:message code="generic.sendForApproval" text="Send For Approval"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<a href="#" id="view" class="butSim">
				<spring:message code="generic.view" text="View"/>
			</a> |
			<a href="#" id="publish" class="butSim">
				<spring:message code="generic.publish" text="Publish"/>
			</a>  -->
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>