<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proceeding.list" text="List Of Proceeding"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		
		$(document).ready(function(){
			
			$(".toolTip").hide();
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val('proceeding='+$('#key').val());
			/**** edit Proceeding ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editPart($('#key').val(),$('#internalKey').val());
			});
			$('#new_record').click(function(){
				newPart($('#key').val());										
			});
			
			
			/**** delete Proceeding ****/
			$("#delete_record").click(function() {
				deletePart();
			});		
			/****Searching roster****/
			$("#search").click(function() {
				searchRecord();
			});	
			
			$("#proceedingwiseReport").click(function() {
				proceedingwiseReport();
			});	
			
			
		});	
		function rowSelectHandler(rowid,status){			
			$('#internalKey').val(rowid);
			currentRowId=rowid;
			console.log(currentRowId);
			dataIds=$('#grid').jqGrid('getDataIDs');
			console.log(dataIds);
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();			
			$("#cancelFn").val("rowDblClickHandler");			
			$('#internalKey').val(rowid);
			showTabByIdAndUrl('part_tab', 'proceeding/part/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		
		
	</script>
</head>
<body>
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
			</a> |	
			<a href="#" id="test" class="butSim">
				<spring:message code="generic.test" text="test"/>
			</a> |	
			<a href="#" id="proceedingwiseReport" class="butSim">
				<spring:message code="proceeding.proceedingwiseReport" text="proceeding wise report"/>
			</a>					
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="internalKey" name="internalKey"/>
	</div>
</body>
</html>