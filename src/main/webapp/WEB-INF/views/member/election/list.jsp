<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.election.list" text="List of Elections"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			$('#gridURLParams').val("member="+$('#key').val()+"&houseType="+$("#houseType").val()+"&house="+$("#house").val());		
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newElectionRecord();
			});
			$('#edit_record').click(function(){
				editElectionRecord($('#internalKey').val(),$('#key').val());
			});
			$("#delete_record").click(function() {
				deleteElectionRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listElectionRecord($('#internalKey').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
		});
		function listElectionRecord(){
			showTabByIdAndUrl('election_tab','member/election/list?'+$("#gridURLParams").val());	
		}
		function newElectionRecord(member){				
				$("#cancelFn").val("newElectionRecord");
			    $('#editDeleteLinks').hide();			
				$.get('member/election/new?'+$("#gridURLParams").val(), function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();
					scrollTop();										
			});
		}
		function editElectionRecord(row,member) {	
			var row=$('#internalKey').val();
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}				
			$("#cancelFn").val("editElectionRecord");		
			$.get('member/election/'+row+'/edit?'+$("#gridURLParams").val(),
					 function(data){
					$('#grid_container').html(data);
					$('#list_record').show();	
					scrollTop();															
				});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#internalKey').val();			
			$("#cancelFn").val("rowDblClickHandler");
			$.get('member/election/'+rowid+'/edit?'+$("#gridURLParams").val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();						
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}
		}
		function deleteElectionRecord(row) {
			var member=$('#key').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/election/'+row+'/delete?'+$("#gridURLParams").val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listElectionRecord();
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
				<spring:message code="electionresult.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="electionresult.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="electionresult.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="electionresult.search" text="Search"/>
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
	<input type="hidden" id="houseType" name="houseType" value="${houseType}">			
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
