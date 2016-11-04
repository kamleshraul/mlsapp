<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeemeetings.list" text="List of Committee Meetings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* On selecting a record in the grid */
	function rowSelectHandler(rowId, status){			
		$('#key').val(rowId);
		$("#selectedCommitteeMeeting").val(rowId);
	}

	/* On double clicking a record in the grid */
	function rowDblClickHandler(rowId, iRow, iCol, e) {
		$("#selectionDiv1").hide();
		viewCommitteeMeeting(rowId);
		$("#selectedCommitteeMeeting").val(rowId);
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
			newCommitteeMeeting();
		});

		// Edit Record link is clicked
		$('#edit_record').click(function(){
			$('#selectionDiv1').hide();
			var rowId = $("#key").val();
			editCommitteeMeeting(rowId);
		});

		// Delete Record link is clicked
		$('#delete_record').click(function(){
			var rowId = $("#key").val();
			deleteCommitteeMeeting(rowId);
		});

		// Search link is clicked
		$('#search').click(function(){
			searchCommitteeMeetings();
		});

		// View Record link is clicked
		$('#view_record').click(function(){
			var rowId = $("#key").val();
			viewCommitteeMeeting(rowId);
		});

		// Report for Internal Meeting
		$('#internalMeeting').click(function(){
		$(this).attr('href','#');
			var rowId = $("#key").val();
			
			internalMeeting(rowId);
		});
		
		// Report for first Meeting
		$('#firstMeeting').click(function(){
		$(this).attr('href','#');
			var rowId = $("#key").val();
			
			firstMeeting(rowId);
		});
		
		// Report for Internal Meeting
		$('#meetingAttendance').click(function(){
		$(this).attr('href','#');
			var rowId = $("#key").val();
			
			meetingAttendance(rowId);
		});
		$('#proceeding').click(function(){
			var id = 'details_tab';
			var url = 'proceeding/rosterwisereport?day='+$("#selectedDay").val() 
					+'&committeeMeeting='+$("#key").val()+'&language='+$("#selectedLanguage").val();
			$.get("ref/rosterFromCommitteeMeeting?committeeMeeting="+$("#key").val()
					+'&language='+$("#selectedLanguage").val()
					+'&day='+$("#selectedDay").val(),function(data){
				if(data){
					showTabByIdAndUrl(id, url);
				}else{
					$.prompt("Roster is not created or Roster is not Published for viewing");
					return false;
				}
			});
			
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
		
		
		<security:authorize access="hasAnyRole('CIS_ASSISTANT','CIS_MAIN_ASSISTANT')">
				<hr>
				
				<a href="#" id="internalMeeting" class="butSim">
					<spring:message code="committee.internalMeeting" text="Internal Meeting"/>
				</a> |
				<a href="#" id="firstMeeting" class="butSim">
					<spring:message code="committee.firstMeeting" text="First Meeting"/>
				</a>| 
				 <a href="#" id="meetingAttendance" class="butSim">
					<spring:message code="committee.meetingAttendance" text="Meeting Attendance"/>
				</a>| 
				 
			</security:authorize>
			<a href="#" id="proceeding" class="butSim">
				<spring:message code="committee.proceeding" text="Proceeding"/>
			</a>
			
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="committeeMeetingId" name="committeeMeetingId" value="${committeeMeetingId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="allOption" name="allOption" value="<spring:message code='generic.allOption' text='---- All ----'></spring:message>">
</div>
</body>
</html>