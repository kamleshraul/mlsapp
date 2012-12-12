<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#new_record').click(function(){
				newRecord();				
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#rotation_order_report").click(function() {
				viewRotationOrderReport($('#key').val());
			});
			$("#rotation_order_publish").click(function() {
				publishRotationOrderReport();
			});
		});
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'session/'+rowid+'/edit');
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
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
			<a href="#" id="rotation_order_report" class="butSim">
				<spring:message code="session.rotationOrderReport" text="Rotation order report"/>
			</a> |
			<a href="#" id="rotation_order_publish" class="butSim">
				<spring:message code="generic.rotationOrderpublish" text="Publish Rotation Order Report"/>
			</a>
			
			<p>&nbsp;</p>
			
		
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="urlPattern" name="urlPattern" value="session">
	<input type="hidden" name="gridURLParams" id="gridURLParams" value="houseId=${houseId}" >
	<input type="hidden" name="houseId" id="houseId" value="${houseId}">
</div>
</body>
</html>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#new_record').click(function(){
				newRecord();				
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#rotation_order_report").click(function() {
				viewRotationOrderReport($('#key').val());
			});
			$("#rotation_order_publish").click(function() {
				publishRotationOrderReport();
			});
		});
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'session/'+rowid+'/edit');
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
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
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a><%-- |
			 <a href="#" id="rotation_order_report" class="butSim">
				<spring:message code="session.rotationOrderReport" text="Rotation order report"/>
			</a> |
			<a href="#" id="rotation_order_publish" class="butSim">
				<spring:message code="generic.rotationOrderpublish" text="Publish Rotation Order Report"/>
			</a> --%>
			
			<p>&nbsp;</p>
			
		
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="urlPattern" name="urlPattern" value="session">
	<input type="hidden" name="gridURLParams" id="gridURLParams" value="houseId=${houseId}" >
	<input type="hidden" name="houseId" id="houseId" value="${houseId}">
</div>
</body>
</html>
