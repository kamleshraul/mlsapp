<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.workflowconfig.list" text="List of Workflow Settings"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//initially list is hidden
			$('#list_record').hide();
			//session and housetype will be passed all the time 
			$('#gridURLParams').val("session="+$('#key').val()+"&houseType="+$("#houseType").val());
			//initially edit,delete and search links will be visible
			$('#editDeleteLinks').show();
			//new record		
			$('#new_record').click(function(){
				newWorkflowConfigRecord();										
			});
			$('#edit_record').click(function(){
				editWorkflowConfigRecord();
			});
			$("#delete_record").click(function() {
				deleteWorkflowConfigRecord();
			});
			$("#list_record").click(function() {
				listWorkflowConfigRecord();
			});
			$("#search").click(function() {
				searchWorkflowConfigRecord();
			});
		});
		function listWorkflowConfigRecord(){
			showTabByIdAndUrl('workflowconfig_tab','session/workflowconfig/list?'+$('#gridURLParams').val());	
		}
		function newWorkflowConfigRecord(){
				$('#editDeleteLinks').hide();
				$("#cancelFn").val("newWorkflowConfigRecord");
				$.get('session/workflowconfig/new?'+$('#gridURLParams').val(), function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();
					scrollTop();					
			});
				
		}
		function editWorkflowConfigRecord() {
			$('#editDeleteLinks').hide();
			var row=$('#internalKey').val();
			$("#cancelFn").val("editWorkflowConfigRecord");									
			if(row==null||row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('session/workflowconfig/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();								
		});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$('#editDeleteLinks').hide();			
			var rowid=$('#internalKey').val();
			$("#cancelFn").val("rowDblClickHandler");						
			$.get('session/workflowconfig/'+rowid+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();							
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}						
		}
		function deleteWorkflowConfigRecord() {
			var row=$('#internalKey').val();
			if(row==null||row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('session/workflowconfig/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listWorkflowConfigRecord();
				        });
			        }
				}});
			}
		}		
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="session.workflowconfig.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="session.workflowconfig.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="session.workflowconfig.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="session.workflowconfig.search" text="Search"/>
			</a> 
			</span> | 
			<a href="#" id="list_record" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>			
			<p>&nbsp;</p>
		</div>
	</div>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">	
	</div>
</body>
</html>
