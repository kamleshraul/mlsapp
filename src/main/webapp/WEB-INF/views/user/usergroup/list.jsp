<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="user.usergroup.list" text="List of User Groups"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#gridURLParams').val("credential="+$('#credential').val()+"&user="+$("#user").val());
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newUserGroupRecord();
			});
			$('#edit_record').click(function(){
				editUserGroupRecord();
			});
			$("#delete_record").click(function() {
				deleteUserGroupRecord();
			});
			$("#list_record").click(function() {
				listUserGroupRecord();
			});
			$("#search").click(function() {
				searchRecord();
			});			
		});
		function listUserGroupRecord(){	
			showTabByIdAndUrl('groups_tab','user/usergroup/list?'+$('#gridURLParams').val());	
		}
		function newUserGroupRecord(){
				$('#editDeleteLinks').hide();
				$("#cancelFn").val("newUserGroupRecord");
				$.get('user/usergroup/new?'+$('#gridURLParams').val(), function(data){					
					$('#grid_container').html(data);
					scrollTop();					
			});
				
		}
		function editUserGroupRecord() {
			$('#editDeleteLinks').hide();
			var row=$('#internalKey').val();
			$("#cancelFn").val("editUserGroupRecord");									
			if(row==null||row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('user/usergroup/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				scrollTop();								
		});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$('#editDeleteLinks').hide();
			$("#internalKey").val(rowid);			
			var row=$('#internalKey').val();
			$("#cancelFn").val("rowDblClickHandler");						
			$.get('user/usergroup/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				scrollTop();							
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}						
		}
		function deleteUserGroupRecord() {
			var row=$('#internalKey').val();
			if(row==null||row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('user/usergroup/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listUserGroupRecord();
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
				<spring:message code="usergroup.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="usergroup.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="usergroup.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="usergroup.search" text="Search"/>
			</a> |
			</span>			
			<p>&nbsp;</p>
		</div>
	</div>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">	
	<input type="hidden" id="credential" name="credential" value="${credential}">
	<input type="hidden" id="user" name="user" value="${user}">	
	</div>
</body>
</html>
