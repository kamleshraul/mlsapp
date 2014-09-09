<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="sectionorderseries.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#selectionDiv1").show();
			
			/**** grid params which is sent to load grid data being sent ****/	
			$("#gridURLParams").val('language='+$("#selectedLanguage").val());
			
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();
				newRecord();
			});
			$('#edit_record').click(function(){
				$("#selectionDiv1").hide();
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
		});
		
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {			
			$("#cancelFn").val("rowDblClickHandler");
			$("#selectionDiv1").hide();
			var rowid=$("#masterKey").val();			
			showTabByIdAndUrl('details_tab','sectionorderseries/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	

		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}	
			$("#masterKey").val(rowid);			
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
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	</div>
	<input type="hidden" id="currentPage" value="list">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
