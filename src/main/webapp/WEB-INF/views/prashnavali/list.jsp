<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="prashnavali.list" text="List of Prashnavali Tours"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		viewPrashnavali(rowId);
	}
	
	function setGridURLParams() {
		var committeeTypeId = "committeeType=" + $('#committeeTypeFilter').val();
		var committeeNameId = "committeeName=" + $('#committeeNameFilter').val();
		var gridURLParamsVal = committeeTypeId + '&' + committeeNameId;
		$('#gridURLParams').val(gridURLParamsVal);
	}
	
	$('document').ready(function(){
		
		setGridURLParams();
		// New Record link is clicked
		$('#new_record').click(function(){
			newPrashnavali();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			var rowId = $("#key").val();
			editPrashnavali(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deletePrashnavali(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchPrashnavali();
		});

		// View Record link is clicked
		$('#view_record').click(function(){
			var rowId = $("#key").val();
			viewPrashnavali(rowId);
		});
	});
	</script>
</head>
<body>
<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<security:authorize access="hasAnyRole('CIS_ASSISTANT','CIS_MAIN_ASSISTANT')">
				<a href="#" id="new_record" class="butSim">
					<spring:message code="prashnavali.new" text="New"/>
				</a> |
				
				<a href="#" id="edit_record" class="butSim">
					<spring:message code="prashnavali.edit" text="Edit"/>
				</a> |
				
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="prashnavali.delete" text="Delete"/>
				</a> |
			</security:authorize>
			
			<a href="#" id="search" class="butSim">
				<spring:message code="prashnavali.search" text="Search"/>
			</a> |
			
			<!-- This link is only for viewing purpose -->
			<a href="#" id="view_record" class="butSim">
				<spring:message code="prashnavali.view" text="View"/>
			</a> |
			<p>&nbsp;</p>
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="allOption" name="allOption" value="<spring:message code='generic.allOption' text='---- All ----'></spring:message>">
</div>
</body>
</html>