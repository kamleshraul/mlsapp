<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="user.list"/></title>
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
			$("#search").click(function() {
				searchRecord();
			});
		});
		function rowSelectHandler(rowid,status){
			//on row select key will be set
			if($('#key')){
				$('#key').val(rowid);					
			}		 
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			//here when we are clicking a particular row then we will first set the key and then load the edit
			//page. 
			$("#key").val(rowid);
			showTabByIdAndUrl('details_tab', 'user/'+rowid+'/edit');
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
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="question.search" text="Search"/>
			</a>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
	</div>
</body>
</html>
