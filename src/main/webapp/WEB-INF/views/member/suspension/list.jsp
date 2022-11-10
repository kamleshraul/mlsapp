<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.suspension.list" text="List of Suspensions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
				if($("#house").val()!=""){
			$('#gridURLParams').val("member="+$('#key').val()+"&houseType="+$("#houseType").val()+"&house="+$("#house").val());
			}else{
				$('#gridURLParams').val("member="+$('#key').val()+"&houseType="+$("#houseType").val());				
			}
			//		
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newSuspensionRecord($('#key').val());
				$('#editDeleteLinks').hide();						
			});
			$('#edit_record').click(function(){
				editSuspensionRecord($('#internalKey').val(),$('#key').val());
			});
			$("#delete_record").click(function() {
				deleteSuspensionRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listSuspensionRecord();
			});
			$("#search").click(function() {
				searchRecord();
			});
		});
		function listSuspensionRecord(){
			showTabByIdAndUrl('suspension_tab','member/suspension/list?'+$('#gridURLParams').val());	
		}
		function newSuspensionRecord(member){
			var member=$('#key').val();
			$("#cancelFn").val("newSuspensionRecord");
			$.get('member/suspension/new?'+$('#gridURLParams').val(), function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();
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
		function editSuspensionRecord(row,member) {	
			var row=$('#internalKey').val();
			var member=$('#key').val();
			$("#cancelFn").val("editSuspensionRecord");			
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('member/suspension/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
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
			var member=$('#key').val();
			var rowid=$('#internalKey').val();
			$("#cancelFn").val("rowDblClickHandler");
			$.get('member/suspension/'+rowid+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
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
		function deleteSuspensionRecord(row) {
			var member=$('#key').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/suspension/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listSuspensionRecord();
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
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="membersuspension.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="membersuspension.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="membersuspension.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="membersuspension.search" text="Search"/>
			</a> 
			</span> | 
			<a href="#" id="list_record" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>			
			<p>&nbsp;</p>
		</div>
	</div>
		
	<h3 style="color:black;"><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}		
	</h3>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">		
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
