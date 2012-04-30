<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.ministry.list" text="List of Ministries"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			$('#gridURLParams').val("member="+$('#key').val());		
			$('#editDeleteLinks').show();		
			$('#new_record').click(function(){
				newMinistryRecord($('#key').val());
				$('#editDeleteLinks').hide();						
			});
			$('#edit_record').click(function(){
				editMinistryRecord($('#internalKey').val(),$('#key').val());
			});
			$("#delete_record").click(function() {
				deleteMinistryRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listMinistryRecord($('#internalKey').val());
			});
		});
		function listMinistryRecord(){
			showTabByIdAndUrl('house_tab','member/ministry/list');	
		}
		function newMinistryRecord(member){
				$.get('member/ministry/new?member='+member, function(data){					
					$('#grid_container').html(data);
					$('#list_record').show();					
			});
		}
		function editMinistryRecord(row,member) {			
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('member/ministry/'+row+'/edit?member='+member, function(data){
				$('#grid_container').html(data);
				$('#list_record').show();					
		});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var member=$('#key').val();
			$.get('member/ministry/'+rowid+'/edit?member='+member, function(data){
				$('#grid_container').html(data);
				$('#list_record').show();					
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}						
		}
		function deleteMinistryRecord(row) {
			var member=$('#key').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/ministry/'+row+'/delete?member='+member, null, function(data, textStatus, XMLHttpRequest) {
				        	listMinistryRecord();
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
			</a></span> |
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
