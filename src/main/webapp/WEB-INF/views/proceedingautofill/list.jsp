<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="proceedingautofill.list" text="List of Proceeding Autofill"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		editProceedingAutofill(rowId);
	}
	
	function setGridURLParams() {
		var username = "username=" + $('#currentUsername').val();
		var gridURLParamsVal = username;
		$('#gridURLParams').val(gridURLParamsVal);
	}
	
	$('document').ready(function(){
		
		setGridURLParams();
		// New Record link is clicked
		$('#new_record').click(function(){
			newProceedingAutofill();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			var rowId = $("#key").val();
			editProceedingAutofill(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteProceedingAutofill(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchProceedingAutofill();
		});
	});
	</script>
</head>
<body>
<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="prashnavali.new" text="New"/>
			</a> |
			
			<a href="#" id="edit_record" class="butSim">
				<spring:message code="prashnavali.edit" text="Edit"/>
			</a> |
			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="prashnavali.delete" text="Delete"/>
			</a> |
			
			<a href="#" id="search" class="butSim">
				<spring:message code="prashnavali.search" text="Search"/>
			</a> |
			
			<p>&nbsp;</p>
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
</div>
</body>
</html>