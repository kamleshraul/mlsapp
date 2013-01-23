<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.myTasks.list" text="List of My Tasks"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){				
			$('#list_tab').click(function(){
				showList();
			});	
					
			$(document).keydown(function (e){
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					process($('#key').val());
				}
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});

			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list');	
		});
				
		function showList() {
			showTabByIdAndUrl('list_tab', 'workflow/myTasks/list');								
		}
		
		function process(row) {
			var row = $('#key').val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("process");
			var form=$("#grid").jqGrid('getCell',row,'form');
			var urlpattern=$("#grid").jqGrid('getCell',row,'urlpattern');			
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process?form='+form+'&urlpattern='+urlpattern);			
		}			

		function rowDblClickHandler(row, iRow, iCol, e) {
			var row = $('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			var form=$("#grid").jqGrid('getCell',row,'form');
			var urlpattern=$("#grid").jqGrid('getCell',row,'urlpattern');			
			showTabByIdAndUrl('process_tab', 'workflow/myTasks/' + row + '/process?form='+form+'&urlpattern='+urlpattern);
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
				<a id="process_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>		
		</ul>
		<div class="tabContent clearfix"></div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
	</div> 
</body>
</html>