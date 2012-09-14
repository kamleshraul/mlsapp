<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var urlPattern=$('#urlPattern').val();
		$(document).ready(function(){
			$('#list_tab').click(function(){
				showList();
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
			showTabByIdAndUrl('list_tab','session/list');	
		});	
		function showList() {
			showTabByIdAndUrl('list_tab','session/list');
		}	
		function newRecord() {
			showTabByIdAndUrl('details_tab','session/new')	;
		}
		function editRecord(row) {
			if(this.id =='edit_record' && row==null){
				alert("Please select the desired row to edit");
				return false;
			}
			showTabByIdAndUrl('details_tab','session/'+row+'/edit');
		}

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('details_tab', 'session/'+rowid+'/edit');
		}
		
		function deleteRecord(row) {
			if(row==null){
				$.prompt("Please select the desired row to delete");		
			}
			else{
				$.prompt('Are you sure you want to delete the record with Id: '+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('session/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				            showList();
				        });
			        }
				}});
			}
		}
		
		function viewRotationOrderReport(row){
			if(this.id =='rotation_order_report' && row==null){
				alert("Please select the desired row to view Rotation Order ");
				return false;
			}
			showTabByIdAndUrl('details_tab','session/'+row+'/viewRotationOrder');
		}
			
	</script>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.module.list" text="List"></spring:message>
				</a>
			</li>
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.module.details" text="Details"></spring:message>
				</a>
			</li>			
		</ul>
		<div class="tabContent clearfix">
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
	</div> 
</body>
</html>