<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="bill.section.list" text="List of Sections"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_record').hide();
			$('#listSpacer').show();
			$('#gridURLParams').val("billId="+$('#key').val()+"&language="+$('#selectedLanguage').val());
			
			$('#new_record').click(function(){
				newSectionRecord();
			});
			$('#edit_record').click(function(){
				editSectionRecord($('#internalKey').val());
			});
			/* $("#delete_record").click(function() {
				deleteMinisterRecord($('#internalKey').val());
			}); */
			$("#list_record").click(function() {
				listSectionRecord($('#internalKey').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#testReport").click(function() {
				$(this).attr('href', 'bill/section/reportURL?outputFormat=PDF');
			});
			
			/* Language Change Handled for all pages here.. Wrapper Function for retaining value before change*/
			(function() {
				var currentSelectedLanguage="";
				$("#selectedLanguage").focus(function() {					
					currentSelectedLanguage = this.value;					
				}).change(function() {
					if($('#currentPage').val()=="list") {
						reloadSectionGrid();
					} else if($('#currentPage').val()=="new") {
						$.get('ref/section/findSeriesByLanguage?language='+$(this).val(), function(data) {
							$('#orderingSeries').empty();
							var orderingSeriesHtml = "<option value=''>"+$('#pleaseSelectMsg').val()+"</option>";
							if(data.length>0) {								
								for(var i=0; i<data.length; i++) {
									orderingSeriesHtml += "<option value='"+data[i].id+"'>"+data[i].name+"</option>";
								}
								$('#orderingSeries').html(orderingSeriesHtml);
							}					
						}).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
					} else if($('#currentPage').val()=="edit") {
						$.get('ref/bill/findsectionbyhierarchyorder?billId='+$('#key').val()
								+'&language='+$(this).val()+'&hierarchyOrder='
								+$('#hierarchyOrder').val(), function(data) {
							if(data!=undefined && data!="") {
								$.prompt("Do you really want to open section with selected language? Changes unsaved will be lost", {
									buttons: {Ok:true, Cancel:false}, callback: function(v) {
									if(!v) {
										$("#selectedLanguage").val(currentSelectedLanguage);									
									} else {
										currentSelectedLanguage=$("#selectedLanguage").val();
										editSectionRecord(data);
									}									
								}});								
							} else {
								$.prompt("Do you really want to change language of this section?", {
									buttons: {Ok:true, Cancel:false}, callback: function(v) {
									if(!v) {
										$("#selectedLanguage").val(currentSelectedLanguage);									
									} else {
										currentSelectedLanguage=$("#selectedLanguage").val();
									}									
								}});
							}
							
						}).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
					}					
				});
			})();			
		});
		function listSectionRecord(){
			$('#gridURLParams').val("billId="+$('#key').val()+"&language="+$('#selectedLanguage').val());
			showTabByIdAndUrl('section_tab','bill/section/list?'+$('#gridURLParams').val());
		}
		/**** reload grid ****/
		function reloadSectionGrid(){
			$('#gridURLParams').val("billId="+$('#key').val()+"&language="+$('#selectedLanguage').val());
			var oldURL=$("#grid").getGridParam("url");
			console.log("oldURL: "+ oldURL);
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");											
		}
		function newSectionRecord(bill){
			$.get('bill/section/new?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				$("#cancelFn").val("newSectionRecord");
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
		function editSectionRecord(row) {	
			if(row==""){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$.get('bill/section/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
				$('#grid_container').html(data);
				$('#list_record').show();
				$("#cancelFn").val("editSectionRecord");	
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
			$.get('bill/section/'+row+'/edit?'+$('#gridURLParams').val(), function(data){
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
		/* function deleteMinisterRecord(row) {
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
		} */		
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
			<a href="#" id="select_language" class="butSim">
				<spring:message code="section.language" text="Language"/>
			</a>
			<select name="selectedLanguage" id="selectedLanguage" style="width:97px;height: 25px;">			
			<c:forEach items="${languages}" var="i">
			<c:choose>
			<c:when test="${i.type==selectedLanguage}">
			<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>					
			</c:when>
			<c:otherwise>
			<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |	
			<hr>
			<span id="newLink">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="section.new" text="New"/>
			</a> |
			</span>
			<span id="editDeleteLinks"> 
				<a href="#" id="edit_record" class="butSim">
				<spring:message code="section.edit" text="Edit"/>
				</a> |
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="section.delete" text="Delete"/>
				</a> |
				<a href="#" id="search" class="butSim">
					<spring:message code="section.search" text="Search"/>
				</a> |
				<a href="#" id="testReport" class="butSim">
					<spring:message code="section.report" text="View Report"/>
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
	<input id="currentPage" name="currentPage" value="list" type="hidden">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
