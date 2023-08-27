<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			
		    if($("#selectedStatus").val() == 955)
			  {
				$("#removeDiscussionDateAndStatus").show()			
			  }else{
				$("#removeDiscussionDateAndStatus").hide()
			  }
		    
		    $('#removeDiscussionDateAndStatus').click(function(){
		    	$.prompt($('#discussionstatusChangeMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	//console.log($("#key").val())
			        	
			        	var id = $("#key").val();
			        	var ugt = $("#currentusergroupType").val();
			        	
			        	if(id != undefined && id != ''){
			        		$.get('motion/removeDiscussionStatusAndDate/'+id+'?usergroupType='+ugt,function(data) {
								$.prompt('Number '+ data +'  Changed Successfully !!' )
								reloadMotionGrid();	
							}).fail(function(){				
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.").
									css({'color':'red', 'display':'block'});
								}
								scrollTop();
							});
			        	}
			        	
			        }}});
			});
			
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
			
			if($('#member_admitted_motions_view_flag').val()=="admitted_visible") {
				$('#member_admitted_motions_view_span').show();
			}
			if($('#member_rejected_motions_view_flag').val()=="rejected_visible") {
				$('#member_rejected_motions_view_span').show();
			}
			
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
			/****updating decision for motions****/
			$("#updateDecisionForMotions").click(function(event, isHighSecurityValidationRequired) {
				//isHighSecurityValidationRequired = false;
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				$("#selectionDiv1").hide();
				updateDecisionForMotions();
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
			
			/****Member's Rejected Motions View ****/
			$("#member_rejected_motions_view").click(function() {
				$("#selectionDiv1").hide();
				memberMotionsViewForStatus("rejected");
			});
			
			/****Member's Admitted Motions View ****/
			$("#member_admitted_motions_view").click(function() {
				$("#selectionDiv1").hide();
				memberMotionsViewForStatus("admitted");
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
				<hr/>				
				<span id="member_admitted_motions_view_span" style="display: none;">
				<a href="#" id="member_admitted_motions_view" class="butSim">
					<spring:message code="motion.member_admitted_motions_view" text="Member's Admitted Motions Detail View"/>
				</a> |
				</span>
				<span id="member_rejected_motions_view_span" style="display: none;">
				<a href="#" id="member_rejected_motions_view" class="butSim">
					<spring:message code="motion.member_rejected_motions_view" text="Member's Rejected Motions Detail View"/>
				</a> |		
				</span>
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
				<a href="#" id="discussionSelection" class="butSim">
					<spring:message code="motion.discussionSelection" text="Discussion Selection"/>
				</a> |			
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_POSTER', 'MOIS_SPEAKER', 'MOIS_CHAIRMAN')">
				<a href="#" id="updateDecisionForMotions" class="butSim">
					<spring:message code="motion.updateDecision" text="Update Decision"/>
				</a> |			
			</security:authorize>
			<security:authorize	access="hasAnyRole('MOIS_SECTION_OFFICER','MOIS_ASSISTANT','MOIS_CLERK')">
			 <a href="#" id ="removeDiscussionDateAndStatus"><spring:message code="motion.removeDiscussionDateAndStatus" text="remove Discussion Date And Status"/></a>
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
	<input type="hidden" id="discussionstatusChangeMsg" value="Do You Want to Proceed With Selected motion ?"/>
</body>
</html>