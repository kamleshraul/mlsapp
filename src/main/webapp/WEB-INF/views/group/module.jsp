<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="group.list" /></title>
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
			$('#rotationOrder_tab').click(function(){
				assignRotationOrder($('#key').val());
			});
			
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newRecord();
				}
				if(e.which==83 && e.ctrlKey){
					console.log('save');
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					editRecord();
				}
				if(e.which==8 && e.ctrlKey){
					deleteRecord();
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			showTabByIdAndUrl('list_tab','group/list');
		});
		
		function showList() {
			showTabByIdAndUrl('list_tab','group/list');
			

		}

		function newRecord(copy) {
			if(copy==null||copy==undefined){
			showTabByIdAndUrl('details_tab','group/new')	;
			}else{
				showTabByIdAndUrl('details_tab','group/new?copy='+$("#key").val())	;
			}
		}

		function editRecord() {
			var row = $("#grid").jqGrid('getGridParam','selrow');
			if(this.id =='edit_record' && row==null){				
				$.prompt("Please select the desired row to edit");
				return false;							
			} 
			else{
				if(row!=$('#key').val()) {
					row = $('#key').val();
				}								
				showTabByIdAndUrl('details_tab','group/'+row+'/edit');
			}
		}

		function deleteRecord() {
			var row = $("#grid").jqGrid('getGridParam','selrow'); 
			if(row==null){
				$.prompt("Please select the desired row to delete");		
			}
			else{
				$.prompt('Are you sure you want to delete the record with Id: '+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('group/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				            $('#grid').trigger("reloadGrid");
				        });
			        }
				}});				
			}
		}

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('details_tab', 'group/'+rowid+'/edit');
		}
		
		function assignRotationOrder(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());	
				return;
			}
			else{
				showTabByIdAndUrl('rotationOrder_tab','group/rotationorder/'+row+'/edit');
				}
		}
		
	</script>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li class="tab1">
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>
			<li class="tab2">
				<a id="details_tab" href="#" class="tab">
					<spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>
			<li class="tab3">
				<a id="rotationOrder_tab" href="#" class="tab">
					<spring:message code="group.module.rotationOrder" text="Rotation Order"></spring:message>
				</a>
			</li>
		</ul>
		<div class="tabContent clearfix">
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='group.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
	</div>		
</body>
</html>