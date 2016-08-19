<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&discussionMotionType="+$("#selectedDiscussionMotionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
				);
			/**** show/hide unstarred yaadi link as per selected devicetype ****/
			var currentDeviceType = $("#deviceTypeMaster option[value='"+ $("#selectedDiscussionMotionType").val() + "']").text();
			
			/**** new motion ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newDiscussionMotion();
			});
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editDiscussionMotion();
			});
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteDiscussionMotion();
			});		
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
						
			//---ADDED BY ANAND------------------
			
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedQuestionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&discussionMotionType="+$(this).val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());				
			});

		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'discussionmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','DMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','DMOIS_TYPIST','DMOIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a>|
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 			
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>