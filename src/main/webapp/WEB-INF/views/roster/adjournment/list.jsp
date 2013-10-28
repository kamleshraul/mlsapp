<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.adjurnment.list" text="List of Adjurnments"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#gridURLParams').val("roster="+$('#key').val());		
			$('#new_record').click(function(){
				newAdjournment();
			});
			$('#edit_record').click(function(){
				editAdjournment();
			});
			$("#delete_record").click(function() {
				deleteAdjournment();
			});			
			$("#search").click(function() {
				searchRecord();
			});
		});			
		function newAdjournment(){				
				$("#cancelFn").val("newAdjurnment");
				$.get('roster/adjournment/new?'+$("#gridURLParams").val(), function(data){					
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
		function editAdjournment() {	
			var row=$('#internalKey').val();
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}				
			$("#cancelFn").val("editAdjurnment");		
			$.get('roster/adjournment/'+row+'/edit?'+$("#gridURLParams").val(),
					 function(data){
					$('#grid_container').html(data);
					scrollTop();															
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val());
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
				});		
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#internalKey').val();			
			$("#cancelFn").val("rowDblClickHandler");
			$.get('roster/adjournment/'+rowid+'/edit?'+$("#gridURLParams").val(), function(data){
				$('#grid_container').html(data);
				scrollTop();						
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
		});
		}
		function rowSelectHandler(rowid,status){			
			if($('#internalKey')){
				$('#internalKey').val(rowid);
			}
		}
		function deleteAdjournment(row) {
			var slot=$('#internalKey').val();
			if(row ==""){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('roster/adjournment/'+row+'/delete?'+$("#gridURLParams").val(), null, function(data, textStatus, XMLHttpRequest) {
				        	listAdjournment();
				        }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val());
							}else{
								$("#error_p").html("Error occured contact for support.");
							}
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
				<spring:message code="roster.adjurnment.new" text="New"/>
			</a>|
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="roster.adjurnment.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="roster.adjurnment.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="roster.adjurnment.search" text="Search"/>
			</a> | 						
			<p>&nbsp;</p>
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
