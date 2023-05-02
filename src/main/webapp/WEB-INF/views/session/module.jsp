<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var urlPattern=$('#urlPattern').val();
		$(document).ready(function(){
			
			$("#details_tab").click(function(){
				var rowid = $('#key').val();
				showTabByIdAndUrl('details_tab', 'session/'+rowid+'/edit');
			});
			
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
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'session/'+rowid+'/edit');
		}

		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}						
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
		
		function viewRotationOrderReport(row){
			if(this.id =='rotation_order_report' && row==null){
				$.prompt("Please select the desired row to view Rotation Order ");
				return false;
			}
			showTabByIdAndUrl('details_tab','session/'+row+'/viewRotationOrder');
		}

		//session_devicetype_config_tab
		$('#session_devicetype_config_tab').click(function(){
			var row = $("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('session_devicetype_config_tab','session/'+row +'/devicetypeconfig');
			}
		});
		
		$('#session_devicetype_config_tab').click(function(){
		
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('session_devicetype_config_tab','session/'+row +'/devicetypeconfig');
			}
		});
		
		$('#question_discussed').click(function(){
			
			
			
			showTabByIdAndUrl('question_discussed','session/getsessiondates/'+$("#key").val());
			
			
		});
		
		
		//Set Question Discussed Window 
		function setQuestionDiscussedWindow() {
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="SessionId="+$("#key").val();
			
			var resourceURL='session/getsessiondates?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:360,height:270});
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
					
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
			<li>
				<a id="session_devicetype_config_tab" href="#" class="tab">
				   <spring:message code="session.module.sessiondevicetypeconfig" text="Session Config"></spring:message>
				</a>
			</li>
			<li>
				<a id="question_discussed" href="#" class="tab">
				   <spring:message code="session.module.question_discussed" text="Question discussed"></spring:message>
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
		<input type="hidden" name="houseType" id="houseType" value="${housetype}">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div> 
</body>
</html>