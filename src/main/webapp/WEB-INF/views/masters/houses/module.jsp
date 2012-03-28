<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="${urlPattern}.list" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var urlPattern=$('#urlPattern').val();
	$(document).ready(function(){
		 houseId=$("#grid").jqGrid('getGridParam','selrow');
		alert($('#urlPattern').val());
			$('#list_tab').click(function(){
				showList();
			});
			
			$('#details_tab').click(function(){
				var row = $("#grid").jqGrid('getGridParam','selrow');
				if(row == null){
					newRecord();
				}
				else{
					editRecord();
				}
			});
			$('#session_tab').click(function(){
				var row = $("#grid").jqGrid('getGridParam','selrow');
				showTabByIdAndUrl('session_tab','masters_sessions/'+ row +'/list');
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
			showTabByIdAndUrl('list_tab',urlPattern +'/list');
		});
		
		function showList() {
			showTabByIdAndUrl('list_tab',urlPattern+'/list');
			

		}

		function newRecord() {
			showTabByIdAndUrl('details_tab',urlPattern+'/new')	;
		}

		function editRecord() {
			var row = $("#grid").jqGrid('getGridParam','selrow');
			if(this.id =='edit_record' && row==null){
				alert("Please select the desired row to edit");
				return false;
			}
			showTabByIdAndUrl('details_tab',urlPattern+'/'+row+'/edit');
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
				        $.delete_(urlPattern+'/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				            $('#grid').trigger("reloadGrid");
				        });
			        }
				}});
				

				//$("#grid").jqGrid('delGridRow',row,{reloadAfterSubmit:true, mtype:'DELETE', url:url+'/'+row+'/delete',modal:true});
			}
		}

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('details_tab', urlPattern+'/'+rowid+'/edit');
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
				<a id="session_tab" href="#" class="tab">
					<spring:message code="${urlPattern}.module.session" text="Sessions"></spring:message>
				</a>
			</li>
		</ul>
		<div class="tabContent clearfix">
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" name="gridURLParams" id="gridURLParams" value="houseType=${type}" >
		
</body>
</html>