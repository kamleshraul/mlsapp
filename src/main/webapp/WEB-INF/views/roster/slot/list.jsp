<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.slot.list" text="List of Slots"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#gridURLParams').val("roster="+$('#key').val());		
			$('#new_record').click(function(){
				$("#sendReminderNotification").hide();
				newSlot();
			});
			$('#edit_record').click(function(){
				$("#sendReminderNotification").hide();
				editSlot();
			});
			$("#delete_record").click(function() {
				deleteSlot();
			});			
			$("#search").click(function() {
				searchRecord();
			});
			$("#sendReminderNotification").click(function() {
				sendReminderNotification();
			});
		});			
		function newSlot(){				
				$("#cancelFn").val("newSlot");
				$.get('roster/slot/new?'+$("#gridURLParams").val(), function(data){					
					$('#grid_container').html(data);
					scrollTop();										
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		function editSlot() {	
			var row=$('#internalKey').val();
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}				
			$("#cancelFn").val("editSlot");		
			$.get('roster/slot/'+row+'/edit?'+$("#gridURLParams").val(),
					 function(data){
					$('#grid_container').html(data);
					scrollTop();															
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#internalKey').val();			
			$("#cancelFn").val("rowDblClickHandler");
			$.get('roster/slot/'+rowid+'/edit?'+$("#gridURLParams").val(), function(data){
				$('#grid_container').html(data);
				scrollTop();						
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}
		}
		function deleteSlot() {
			var slot=$('#internalKey').val();
			if(slot ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ slot,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('roster/slot/'+slot+'/delete?'+$("#gridURLParams").val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listSlot();
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
		function sendReminderNotification() {
			var selectedSlotId=$('#internalKey').val();
			if(selectedSlotId==null||selectedSlotId==""){								
				$.prompt("Please select a slot for sending the reminder");
				return false;
			}else{
				var slotCompleted = $('#grid').jqGrid('getCell',selectedSlotId,'completed');
				if(slotCompleted) {
					$.prompt("This slot is already completed!");
					return false;
				} else {
					$('.commandbarContent').hide();
					var reporterUsername = $('#grid').jqGrid('getCell',selectedSlotId,'reporter.user.credential.username');
					$.get('roster/slot/sendReminderNotification?slotId='+selectedSlotId+'&reporterUsername='+reporterUsername, function(data){
						$('#grid_container').html(data);
						scrollTop();															
					}).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						$('.commandbarContent').show();
						scrollTop();
					});
				}				
			}
		}
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="roster.slot.new" text="New"/>
			</a>|
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="roster.slot.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="roster.slot.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="roster.slot.search" text="Search"/>
			</a> | 			
			<security:authorize access="hasAnyRole('RIS_CHIEF_REPORTER')">
				<a href="#" id="sendReminderNotification" class="butSim">
					<spring:message code="roster.slot.sendReminderNotification" text="Send Reminder"/>
				</a> |
			</security:authorize>			
			<p>&nbsp;</p>
		</div>
	</div>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>
