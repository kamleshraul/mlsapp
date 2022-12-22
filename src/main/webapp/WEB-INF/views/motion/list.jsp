<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();	
			$("#selectedDisplayContent").hide();
			$("#generateMotion").hide();
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&motionType="+$("#selectedMotionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&subDepartment="+$("#selectedSubDepartment").val()
					+"&clubbingStatus=" + $("#selectedClubbingStatus").val()
					);
			$("#gridURLParams_ForNew").val($("#gridURLParams").val());
			/**** new motion ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newMotion();
			});
			/**** edit motion ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editMotion();
			});
			/**** delete motion ****/
			$("#delete_record").click(function() {
				deleteMotion();
			});		
			/****Searching motion****/
			$("#search").click(function() {
				searchRecord();
			});
			//---ADDED BY VIKAS------------------
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#discussionSelection").click(function(){
				showDiscussionSelection();
			});
			/****Determine Ordering of Motions for Submission ****/
			$("#determine_ordering_for_submission").click(function() {
				$("#selectionDiv1").hide();
				determineOrderingForSubmission();
			});
			/****Member's Motions View ****/
			$("#member_motions_view").click(function() {
				$("#selectedDisplayContent").show();
				$("#generateMotion").show();
			});
			$("#generateMotion").click(function(){
				$("#selectionDiv1").hide();
				memberMotionsView($("#selectedDisplayContent").val());
			});
			
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'motion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="motion.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="motion.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_CLERK')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="motion.delete" text="Delete"/>
			</a> |
			</security:authorize>
				
			<security:authorize access="hasAnyRole('ABC')">		
				<a href="#" id="submitMotion" class="butSim">
					<spring:message code="generic.submitmotion" text="submit"/>
				</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="motion.search" text="Search"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<span id="determine_ordering_for_submission_span">
				<a href="#" id="determine_ordering_for_submission" class="butSim">
					<spring:message code="motion.determine_ordering_for_submission" text="Determine Ordering for Submission"/>
				</a> |
				</span>
				<hr/>
				<a href="#" id="member_motions_view" class="butSim">
					<spring:message code="motion.member_motions_view" text="Member's Motions View"/>
				</a>
				 <select name="selectedDisplayContent" id="selectedDisplayContent" style="width:100px;height: 25px;">			
					<option value="subject"><spring:message code="motion.subject" text="Subject"/></option>
					<option value="details"><spring:message code="motion.details" text="Details"/></option>		
				</select>
				<a href="#" id="generateMotion" class="butSim">
					Go
				</a>|
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
				<a href="#" id="discussionSelection" class="butSim">
					<spring:message code="motion.discussionSelection" text="Discussion Selection"/>
				</a> |			
			</security:authorize>
			<p>&nbsp;</p>
		</div>
	</div>
	
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>