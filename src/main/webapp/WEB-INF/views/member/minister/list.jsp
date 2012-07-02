<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.minister.list" text="List of Ministers"/></title>
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
				newMinisterRecord($('#key').val());
			});
			$('#edit_record').click(function(){
				editMinisterRecord($('#internalKey').val(),$('#key').val());
			});
			$("#delete_record").click(function() {
				deleteMinisterRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listMinisterRecord($('#internalKey').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
		});
		function listMinisterRecord(){
			showTabByIdAndUrl('minister_tab','member/minister/list?'+$('#gridURLParams').val());	
		}
		function newMinisterRecord(member){
			var member=$('#key').val();
			$("#cancelFn").val("newMinisterRecord");
			$('#editDeleteLinks').hide();			
			$.get('member/minister/new?'+$('#gridURLParams').val(), function(data){					
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();					
			});
		}
		function editMinisterRecord(row,member) {	
			var row=$('#internalKey').val();
			var member=$('#key').val();
			$("#cancelFn").val("editMinisterRecord");			
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('member/minister/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				scrollTop();									
			});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var member=$('#key').val();
			var rowid=$('#internalKey').val();
			$("#cancelFn").val("rowDblClickHandler");
			$.get('member/minister/'+rowid+'/edit?'+$('#gridURLParams').val(), function(data){
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
		function deleteMinisterRecord(row) {
			var member=$('#key').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/minister/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listMinisterRecord();
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
				<spring:message code="memberministry.new" text="New"/>
			</a><span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="memberministry.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="memberministry.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="memberministry.search" text="Search"/>
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
