<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee.list" text="List of Committees"/>
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
		viewCommittee(rowId);
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
			newCommittee();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			$('#selectionDiv1').hide();
			var rowId = $("#key").val();
			editCommittee(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteCommittee(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchCommittees();
		});

		// View Record link is clicked
		$('#view_record').click(function(){
			var rowId = $("#key").val();
			viewCommittee(rowId);
		});

		// Request to Parliamentary Affairs Minister for additon of Members to Committee(s)
		$('#request_to_parliamentary_minister').click(function(){
			$('#selectionDiv1').hide();
			requestToParliamentaryMinister();
		});

		// Request to Leader of Opposition for additon of Members to Committee(s)
		$('#request_to_leader_of_opposition').click(function(){
			$('#selectionDiv1').hide();
			requestToLeaderOfOpposition();
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
			
			<security:authorize access="hasAnyRole('CIS_ASSISTANT')">
				<hr>
				<a href="#" id="request_to_parliamentary_minister" class="butSim">
					<spring:message code="committee.requestToParliamentaryMinister" text="Request to Parliamentary Minister"/>
				</a> |
				
				<a href="#" id="request_to_leader_of_opposition" class="butSim">
					<spring:message code="committee.requestToLeaderOfOpposition" text="Request to Leader of Opposition"/>
				</a> |
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
</div>
</body>
</html>