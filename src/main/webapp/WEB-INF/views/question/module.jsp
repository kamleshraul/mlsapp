<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//here we are trying to add date mask in grid search when field names
			//ends with Date
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});			
			$('#list_tab').click(function(){
				showList();
			});	
			$('#details_tab').click(function(){
				details($('#key').val());
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
			//houseType is passed so as to appropriately populate select assembly/council select box
			//showTabByIdAndUrl('list_tab','member/list?houseType='+$('#houseType').val());	
			showList();	
		});
				
		function showList() {
			//houseType is passed so as to appropriately populate select assembly/council select box			
			showTabByIdAndUrl('list_tab','question/list?houseType='+$('#houseType').val()+'&questionType='+$("#questionType").val());								
		}	
		function newRecord() {
			//here house parameter will be used to add house member role association i.e default role and so need to be present in new.jsp/edit.jsp
			//also housetype is needed to load proper background image
			showTabByIdAndUrl('details_tab','question/new?'+$("#gridURLParams").val());
			$("#key").val("");			
			$("#cancelFn").val("newRecord");
		}
		function editRecord(row) {			
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("editRecord");
			showTabByIdAndUrl('details_tab','question/'+row+'/edit?'+$("#gridURLParams").val());			
		}	

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
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
				        $.delete_('question/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
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
				   <spring:message code="generic.details" text="Details">
				   </spring:message>
				</a>
			</li>	
		</ul>
		<div class="tabContent clearfix">
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" name="houseType" id="houseType" value="${houseType}">
		<input type="hidden" name="house" id="house" value="${house}">	
		<input type="hidden" name="sessionYear" id="sessionYear" value="${sessionYear}">		
		<input type="hidden" name="sessionType" id="sessionType" value="${sessionType}">		
 		<input type="hidden" name="questionType" id="questionType" value="${questionType}">
		</div> 
</body>
</html>