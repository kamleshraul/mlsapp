<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="sectionorderseries.list" text="List of Section Order Series"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#list_tab').click(function(){
				showList();
			});	
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newRecord();
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					editRecord($('#key').val());
				}
				if(e.which==8 && e.ctrlKey){
					deleteRecord($('#key').val());
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			
			showTabByIdAndUrl('list_tab','sectionorderseries/list?language='+$("#selectedLanguage").val());	
			
			$("#details_tab").click(function(){
				$("#selectionDiv1").hide();
				var rowid=$("#masterKey").val();			
				showTabByIdAndUrl('details_tab','sectionorderseries/'+rowid+'/edit?'+$("#gridURLParams").val());
			});
			
			$('#selectedLanguage').change(function() {
				var value=$(this).val();
				if(value!=""){
					reloadGrid();
					$('#masterKey').val("");
				}
			});
			
			$('#sectionorder_tab').click(function(){
				var isSeriesAutonomous = "";
				if($('#currentPage').val()=='list') {
					isSeriesAutonomous = $("#grid").getCell($('#masterKey').val(), 'isAutonomous');
				} else if($('#currentPage').val()=='new') {
					return false;
				} else if($('#currentPage').val()=='edit') {
					isSeriesAutonomous = $('#isAutonomous').val();
				}
				if(isSeriesAutonomous=='true') {
					$.prompt("This ordering series is autonomous.. So no need to add section orders to it.");
					return false;
				}
				listSectionOrderDetails($('#masterKey').val());
			});
		});	
		function showList() {	
			$("#masterKey").val("");
			$("#key").val("");
			showTabByIdAndUrl('list_tab','sectionorderseries/list?language='+$("#selectedLanguage").val());
		}	
		function newRecord() {
			$("#cancelFn").val("newRecord");
			$("#masterKey").val("");
			showTabByIdAndUrl('details_tab','sectionorderseries/new?'+$("#gridURLParams").val());
		}
		function editRecord(row) {
			var row=$("#masterKey").val();
			if(this.id =='edit_record' && row==null){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("editRecord");
			showTabByIdAndUrl('details_tab','sectionorderseries/'+row+'/edit?'+$("#gridURLParams").val());
		}	

		function rowDblClickHandler(rowid, iRow, iCol, e) {			
			$("#cancelFn").val("rowDblClickHandler");
			$("#selectionDiv1").hide();
			var rowid=$("#masterKey").val();			
			showTabByIdAndUrl('details_tab','sectionorderseries/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	

		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}	
			$("#masterKey").val(rowid);			
		}	
		
		function deleteRecord(row) {
			var row=$("#masterKey").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('sectionorderseries/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				            showList();
				        }).fail(function(){
							$.unblockUI();
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
		
		function listSectionOrderDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());
				return;
			}
			else{
				$("#selectionDiv1").hide();
				showTabByIdAndUrl('sectionorder_tab','sectionorder/list?series='+$('#masterKey').val());
			}
		}
		
		/**** reload grid ****/
		function reloadGrid() {
			$("#gridURLParams").val('language='+$("#selectedLanguage").val());
			var oldURL=$("#grid").getGridParam("url");
			console.log("oldURL: "+ oldURL);
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");											
		}			
	</script>
</head>
<body>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>
			<li>
				<a id="sectionorder_tab" href="#" class="tab">
				   <spring:message code="sectionorderseries.sectionorder" text="Section Orders"></spring:message>
				</a>
			</li>			
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">
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
			</select>
			<hr>
		</div>
		<div class="tabContent clearfix">
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="masterKey" name="masterKey">
		
	</div> 
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>