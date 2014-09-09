<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="sectionorder.list" text="List of Section Orders"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			$('#listSpacer').show();
			
			$('#gridURLParams').val("series="+$('#masterKey').val());
			
			$('#new_record').click(function(){
				newSectionOrderRecord();
			});
			$('#edit_record').click(function(){
				editSectionOrderRecord($('#internalKey').val());
			});
			$("#delete_record").click(function() {
				deleteSectionOrderRecord($('#internalKey').val());
			});
			$("#list_record").click(function() {
				listSectionOrderRecord($('#internalKey').val());
			});
			$("#search").click(function() {
				searchRecord();
			});					
		});
		function listSectionOrderRecord(){
			$('#gridURLParams').val("series="+$('#masterKey').val());
			showTabByIdAndUrl('sectionorder_tab','sectionorder/list?'+$('#gridURLParams').val());
		}
		/**** reload grid ****/
		function reloadSectionOrderGrid(){
			$('#gridURLParams').val("series="+$('#masterKey').val());
			var oldURL=$("#grid").getGridParam("url");
			console.log("oldURL: "+ oldURL);
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");											
		}
		function newSectionOrderRecord(bill){
			$.get('sectionorder/new?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				$("#cancelFn").val("newSectionOrderRecord");
				$('#newLink').hide();
				$('#editDeleteLinks').hide();
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
		function editSectionOrderRecord(row) {	
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('sectionorder/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				$("#cancelFn").val("editSectionOrderRecord");	
				$('#newLink').hide();
				$('#editDeleteLinks').hide();
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
			var row=$('#internalKey').val();			
			$.get('sectionorder/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				$("#cancelFn").val("rowDblClickHandler");
				$('#newLink').hide();
				$('#editDeleteLinks').hide();
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
		function deleteSectionOrderRecord(row) {
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('sectionorder/'+row+'/delete?'+$('#gridURLParams').val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listSectionOrderRecord();
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
				<spring:message code="sectionorder.new" text="New"/>
			</a> |
			<span id="editDeleteLinks"> 
				<a href="#" id="edit_record" class="butSim">
				<spring:message code="sectionorder.edit" text="Edit"/>
				</a> |
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="sectionorder.delete" text="Delete"/>
				</a> |
				<a href="#" id="search" class="butSim">
					<spring:message code="sectionorder.search" text="Search"/>
				</a>
			</span>  
			<a href="#" id="list_record" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>			
			<p id="listSpacer">&nbsp;</p>
		</div>
	</div>
		
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="internalKey" name="internalKey">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
