<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour.list" text="List of Committee Tours"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		viewCommitteeTour(rowId);
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
			newCommitteeTour();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			var rowId = $("#key").val();
			editCommitteeTour(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteCommitteeTour(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchCommitteeTours();
		});

		// View Record link is clicked
		$('#view_record').click(function(){
			var rowId = $("#key").val();
			viewCommitteeTour(rowId);
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
					<spring:message code="committeetour.new" text="New"/>
				</a> |
				
				<a href="#" id="edit_record" class="butSim">
					<spring:message code="committeetour.edit" text="Edit"/>
				</a> |
				
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="committeetour.delete" text="Delete"/>
				</a> |
			</security:authorize>
			
			<a href="#" id="search" class="butSim">
				<spring:message code="committeetour.search" text="Search"/>
			</a> |
			
			<!-- This link is only for viewing purpose -->
			<a href="#" id="view_record" class="butSim">
				<spring:message code="committeetour.view" text="View"/>
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