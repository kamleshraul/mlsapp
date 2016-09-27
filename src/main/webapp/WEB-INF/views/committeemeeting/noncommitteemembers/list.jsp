<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="noncommitteemember.list" text="List of Non Committee Members"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* =============== ACTIONS =============== */
function newNonCommiteeMember() {
		$('#cancelFn').val('newNonCommiteeMember');
		$('#key').val('');
		var id = 'details_tab';
		var url = 'committeemeeting/noncommitteemembers/new?committeeMeetingId='+$('#committeeMeetingId').val();
		showTabByIdAndUrl(id, url);
	}
	
 function editNonCommiteeMember(rowId) {
	$('#cancelFn').val('editNonCommiteeMember');
	if(rowId == null || rowId == '') {
		var msg = $('#selectRowFirstMessage').val();
		$.prompt(msg);
		return false;
	}
	var id = 'details_tab';
	
	var url = 'committeemeeting/noncommitteemembers' + '/' + rowId + '/' + 'edit?committeeMeetingId='+$('#committeeMeetingId').val();
	
	showTabByIdAndUrl(id, url);
} 

function deleteNonCommiteeMember(rowId) {
	if(rowId == null || rowId == '') {
		var msg = $('#selectRowFirstMessage').val();
		$.prompt(msg);
		return;
	}
	var promptMsg = $('#confirmDeleteMessage').val() + rowId;
	var options = {buttons: {Ok:true, Cancel:false}, 
				   callback: function(v){
		   				if(v){
							$.delete_('committeemeeting/noncommitteemembers' + '/' + rowId + '/' + 'delete', 
									null, 
									function(data, textStatus, XMLHttpRequest) { 
										reloadGrid(); 
									}
							);
		        		}
				  }};
	$.prompt(promptMsg, options);
}

function searchNonCommiteeMember() {
	searchRecord();
}

function viewNonCommiteeMember(rowId) {
	$('#cancelFn').val('viewNonCommiteeMember');
	if(rowId == null || rowId == '') {
		var msg = $('#selectRowFirstMessage').val();
		$.prompt(msg);
		return false;
	}
	var id = 'details_tab';
	var url = 'committeemeeting/noncommitteemembers' + '/' + rowId + '/' + 'view';
	showTabByIdAndUrl(id, url);
}

	
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		viewNonCommiteeMember(rowId);
	}
	
	function setGridURLParams() {
		var committeeMeetingId = "committeeMeetingId=" + $('#committeeMeetingId').val();
		var gridURLParamsVal = committeeMeetingId;
		
		$('#gridURLParams').val(gridURLParamsVal);
	}	
	
	$('document').ready(function(){
		// New Record link is clicked
		setGridURLParams();
		$('#new_record').click(function(){
			newNonCommiteeMember();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			var rowId = $("#key").val();
			editNonCommiteeMember(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteNonCommiteeMember(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchNonCommiteeMember();
		});

		// View Record link is clicked
		$('#view_record').click(function(){
			var rowId = $("#key").val();
			viewNonCommiteeMember(rowId);
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
					<spring:message code="NonCommiteeMember.new" text="New"/>
				</a> |
				
				<a href="#" id="edit_record" class="butSim">
					<spring:message code="NonCommiteeMember.edit" text="Edit"/>
				</a> |
				
				<a href="#" id="delete_record" class="butSim">
					<spring:message code="NonCommiteeMember.delete" text="Delete"/>
				</a> |
			</security:authorize>
			
			<a href="#" id="search" class="butSim">
				<spring:message code="NonCommiteeMember.search" text="Search"/>
			</a> |
			
			<!-- This link is only for viewing purpose -->
			<a href="#" id="view_record" class="butSim">
				<spring:message code="NonCommiteeMember.view" text="View"/>
			</a> |
			<p>&nbsp;</p>
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="committeeMeetingId" value="${committeeMeetingId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="grid_id" value="${gridId}">
</div>
</body>
</html>