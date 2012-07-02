<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var houseId=$('#houseId').val();
	
		$(document).ready(function(){
			$("#list_session").hide();
			$('#key').val("");
			$('#new_record').click(function(){
				newSession();				
			});
			$('#edit_record').click(function(){
				editSession($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteSession($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			//$("#list_session").click(function() {
			//	showTabByIdAndUrl('session_tab','session/list'+'?houseId='+houseId);
			//});
		});
		
	</script>
	<script>
	function newSession()
	{			 		 
		$("#cancelFn").val(newSession);
		$('#edit_records').hide();
		showTabByIdAndUrl('session_tab','session/new'+'?houseId=' +houseId);
	}
	
	function editSession(row) {
		if(this.id =='edit_session' && row==null){
			alert("Please select the desired row to edit");
			return false;
		}
		$("#cancelFn").val(newSession);				  
		 showTabByIdAndUrl('session_tab','session/'+row+'/edit');
	}

	function deleteSession(row) {
		if(row==null){
			$.prompt("Please select the desired row to delete");		
		}
		else{
			$.prompt('Are you sure you want to delete the record with Id: '+ row,{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
			        $.delete_('session'+'/'+row +'/delete', null, function(data, textStatus, XMLHttpRequest) {
			            $('#grid').trigger("reloadGrid");
			        });
		        }
			}});
			

			//$("#grid").jqGrid('delGridRow',row,{reloadAfterSubmit:true, mtype:'DELETE', url:url+'/'+row+'/delete',modal:true});
		}
	}
	/* function rowDblClickHandler(rowid, iRow, iCol, e) {
		   $.get('session'+'/'+rowid+'/edit', function(data){
			$('#grid_container').html(data);
			$('#list_session').show();	
		}); 
		
	showTabByIdAndUrl('session_tab', 'session'+'/'+rowid+'/edit');
	}*/

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
			</a> 
			|
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 
			<p>&nbsp;</p><%--   |
			<a href="#" id="list_session" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>  --%>
			
		
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="urlPattern" name="urlPattern" value="session">
	<input type="hidden" name="gridURLParams" id="gridURLParams" value="houseId=${houseId}" >
	<input type="hidden" name="houseId" id="houseId" value="${houseId}">
</div>
</body>
</html>
