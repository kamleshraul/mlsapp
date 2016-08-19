<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeesubject.list" text="List of Committee Subjects"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		$("#selectionDiv1").hide();
		editCommitteeSubject(rowId);
	}

	/* 
	 * The key-value parameters that will travel as a query string part of the url 
	 */
	function setGridURLParams() {
		var committeeTypeId = "committeeType=" + $('#committeeTypeFilter').val();
		var committeeNameId = "committeeName=" + $('#committeeNameFilter').val();
		var gridURLParamsVal = committeeTypeId + '&' + committeeNameId;
		$('#gridURLParams').val(gridURLParamsVal);
	}	
	
	$('document').ready(function(){
		$('#selectionDiv1').show();

		setGridURLParams();
		
		// New Record link is clicked
		$('#new_record').click(function(){
			$('#selectionDiv1').hide();	
			newCommitteeSubject();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			$('#selectionDiv1').hide();
			var rowId = $("#key").val();
			editCommitteeSubject(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteCommitteeSubject(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchCommitteeSubject();
		});

	});
	</script>
</head>
<body>
<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<security:authorize access="hasAnyRole('CIS_ASSISTANT')">
				<a href="#" id="new_record" class="butSim">
					<spring:message code="committee.new" text="New"/>
				</a> |
				
				<a href="#" id="edit_record" class="butSim">
					<spring:message code="committee.edit" text="Edit"/>
				</a> |
				
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="committee.delete" text="Delete"/>
				</a> |
			</security:authorize>
			
			<a href="#" id="search" class="butSim">
				<spring:message code="committee.search" text="Search"/>
			</a> |
			
			<!-- This link is only for viewing purpose -->
			<a href="#" id="view_record" class="butSim">
				<spring:message code="committee.view" text="View"/>
			</a> |
		
				
			
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="allOption" name="allOption" value="<spring:message code='generic.allOption' text='---- All ----'></spring:message>">
</div>
</body>
</html>