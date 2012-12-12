<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="role.list" text="List of Roles"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var urlPattern=$('#urlPattern').val();
		$(document).ready(function(){
			$('#list_tab').click(function(){
				showList();
			});	
			$('#details_tab').click(function(){
				var row = $("#grid").jqGrid('getGridParam','selrow');
				if(row == null){
					if($('#key').val()!=""){							
						editRecord();
					}else{
						newRecord();
					};					
				}
				else{					
					editRecord();
				}
			});
			$('#users_tab').click(function(){
				var row = $("#grid").jqGrid('getGridParam','selrow');
				if(row==null){
					if($('#key').val()!=""){
						if(row!=$('#key').val()) {
						row = $('#key').val();
					}
					}
					else{
						$.prompt("Please select the desired row to Assign Users");
						return false;	
					}
				}
				showTabByIdAndUrl('users_tab','role/user'+'?roleId='+row);
			});			
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newRecord();
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					editRecord($('#key').val());
				}
				if(e.which==8 && e.ctrlKey){
					deleteRecord($('#key').val());
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			showTabByIdAndUrl('list_tab','role/list');	
		});	
		function showList() {
			showTabByIdAndUrl('list_tab','role/list');
		}	
		
		function editRecord(row) {
			
			var row = $("#grid").jqGrid('getGridParam','selrow');
			if(this.id =='edit_record' && row==null){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			} 
			else{
				if(row!=$('#key').val()) {
					row = $('#key').val();
				}
				showTabByIdAndUrl('details_tab','role/'+row+'/edit');
			}
		}
			
				
		function newRecord() {
			showTabByIdAndUrl('details_tab','role/new');
			$("#key").val("");			
			$("#cancelFn").val("newRecord");
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('details_tab', 'role/'+rowid+'/edit');
		}
		
		function deleteRecord(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('role/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showList();
				        });
			        }
				}});
			}
		}	
		
			
	</script>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>	
			<li>
				<a id="users_tab" href="#" class="tab">
				   <spring:message code="role.users" text="Users"></spring:message>
				</a>
			</li>				
		</ul>
		<div class="tabContent clearfix">
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
	</div> 
</body>
</html>