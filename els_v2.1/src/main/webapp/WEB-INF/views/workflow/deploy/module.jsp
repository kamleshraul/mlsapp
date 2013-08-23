<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="workflow.deploy.list" text="List Of Deployments"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
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
				if(e.which==8 && e.ctrlKey){
					deleteRecord($('#key').val());
				}
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});

			showTabByIdAndUrl('list_tab', 'workflow/deploy/list');	
		});
				
		function showList() {
			showTabByIdAndUrl('list_tab', 'workflow/deploy/list');								
		}
		
		function newRecord() {
			showTabByIdAndUrl('details_tab', 'workflow/deploy/new');
			$("#key").val("");			
			$("#cancelFn").val("newRecord");
		}			
		
		function deleteRecord(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row, {
					buttons: {Ok:true, Cancel:false}, callback: function(v) {
			        if(v) {
				        $.delete_('workflow/deploy/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    	showList();
				        }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
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
				   <spring:message code="workflow.deploy.details" text="Details"></spring:message>
				</a>
			</li>		
		</ul>
		<div class="tabContent clearfix">
			<c:if test="${(error!='') && (error!=null)}">
				<h4 style="color: #FF0000;">${error}</h4>
			</c:if>
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div> 
</body>
</html>