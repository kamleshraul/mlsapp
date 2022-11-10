<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.party.list" text="List of Parties"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			if($("#house").val()!=""){
				$('#gridURLParams').val("member="+$('#key').val()+"&houseType="+$("#houseType").val()+"&house="+$("#house").val());
			}else{
					$('#gridURLParams').val("member="+$('#key').val()+"&houseType="+$("#houseType").val());				
			}
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newPartyRecord($('#key').val());										
			});
			$('#edit_record').click(function(){
				editPartyRecord($('#internalKey').val(),$('#key').val());
			});
			$("#delete_record").click(function() {
				deletePartyRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listPartyRecord($('#internalKey').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
		});
		function listPartyRecord(){
			showTabByIdAndUrl('party_tab','member/party/list?'+$('#gridURLParams').val());	
		}
		function newPartyRecord(member){
				$('#editDeleteLinks').hide();
				var member=$('#key').val();
				$("#cancelFn").val("newPartyRecord");
				$.get('member/party/new?'+$('#gridURLParams').val(), function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();
					scrollTop();					
			});
				
		}
		function editPartyRecord(row,member) {
			$('#editDeleteLinks').hide();
			var row=$('#internalKey').val();
			var member=$('#key').val();
			$("#cancelFn").val("editPartyRecord");									
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('member/party/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();								
		});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$('#editDeleteLinks').hide();			
			var member=$('#key').val();
			var rowid=$('#internalKey').val();
			$("#cancelFn").val("rowDblClickHandler");						
			$.get('member/party/'+rowid+'/edit?'+$('#gridURLParams').val(), function(data){
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
		function deletePartyRecord(row) {
			var member=$('#key').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/party/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listPartyRecord();
				        });
			        }
				}});
			}
		}		
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="memberparty.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="memberparty.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="memberparty.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="memberparty.search" text="Search"/>
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
</body>
</html>
