<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="user.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#editDeleteLinks").show();
			$('#new_record').click(function(){
				newRecord();
				$("#editDeleteLinks").hide();
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
				$("#editDeleteLinks").hide();
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
			$("#editDeleteLinks").show();
			showTabByIdAndUrl('details_tab', 'user/'+rowid+'/edit');
		}
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
			<span id="editDeleteLinks"> 			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="question.search" text="Search"/>
			</a> |
			</span>  
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
