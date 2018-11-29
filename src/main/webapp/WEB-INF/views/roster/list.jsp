<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.list" text="List Of Rosters"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			/**** grid params which is sent to load grid data being sent ****/	
			if($('#selectedModule').val()=="COMMITTEE"){
				$("#gridURLParams").val("houseType=''"
						+"&sessionYear=0"
						+"&sessionType=0"
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting="+$("#selectedCommitteeMeeting").val()
						);
			}else{
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting=0"
						);
			}
			
			/**** new roster ****/
			$('#new_record').click(function(){
				newRoster();
			});
			/**** edit roster ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editRoster();
			});
			/**** delete roster ****/
			$("#delete_record").click(function() {
				deleteRoster();
			});		
			/****Searching roster****/
			$("#search").click(function() {
				searchRecord();
			});		
			
			/****To see the roster of the day****/
			$("#view_roster").click(function() {
				viewRoster();
			});
			
			/****To see the roster of the day****/
			$("#view_totalWorkRep").click(function() {
				viewTotalWorkRep();
			});
			
			/****Publish the roster proceeding for Committees & Editing****/
			$("#publish").click(function(){
				currentSelectedRow=$('#key').val();
				publishRoster(currentSelectedRow);
			});
			
			/****Publish the roster proceeding for Committees & Editing****/
			$("#adhawa").click(function(){
				
				viewAdhawa();
			});
		});	
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();			
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'roster/'+rowid+'/edit?'+$("#gridURLParams").val());
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
				<spring:message code="roster.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="roster.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="roster.delete" text="Delete"/>
			</a> |			
			<a href="#" id="search" class="butSim">
				<spring:message code="roster.search" text="Search"/>
			</a> |
			<a href="#" id="view_roster" class="butSim">
				<spring:message code="roster.roster" text="Roster"/>
			</a> |
			<a href="#" id="view_totalWorkRep" class="butSim">
				<spring:message code="roster.totalWorkRep" text="Total Work Report"/>
			</a> |
			<a href="#" id="publish" class="butSim">
				<spring:message code="roster.publish" text="Publish"/>
			</a> |		
			<a href="#" id="adhawa" class="butSim">
				<spring:message code="roster.Adhawa" text="Adhawa"/>
			</a> |				
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
</body>
</html>