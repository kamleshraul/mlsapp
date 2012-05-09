<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.party.list" text="List of Parties"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			$('#gridURLParams').val("member="+$('#key').val());		
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newPartyRecord($('#key').val());
				$('#editDeleteLinks').hide();						
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
			showTabByIdAndUrl('party_tab','member/party/list');	
		}
		function newPartyRecord(member){
				$.get('member/party/new?member='+member, function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();					
			});
		}
		function editPartyRecord(row,member) {			
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('member/party/'+row+'/edit?member='+member, function(data){
				$('#grid_container').html(data);
				$('#list_record').show();					
		});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var member=$('#key').val();
			$.get('member/party/'+rowid+'/edit?member='+member, function(data){
				$('#grid_container').html(data);
				$('#list_record').show();					
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
				        $.delete_('member/party/'+row+'/delete?member='+member, null, function(data, textStatus, XMLHttpRequest) {
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
				<spring:message code="generic.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 
			</span> | 
			<a href="#" id="list_record" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>			
			<p>&nbsp;</p>
		</div>
	</div>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">	
	</div>
</body>
</html>
