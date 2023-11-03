<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		  //view supporting members status
	    $("#viewStatus").click(function(){
		    $.get('cutmotion/status/'+$("#motion").val(),function(data){
			    $.fancybox.open(data);
		    }).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });
	});
	</script>	
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="workflow/cutmotion/supportingmember" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>		
	</c:when>
	<c:otherwise>
	<spring:message code="supportingmember.heading" text="Approve request to add you as supporting member"/>		
	</c:otherwise>
	</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p style="display:none;">
		<label class="small"><spring:message code="motion.houseType" text="House Type"/></label>		
		<input type="text" class="sText" id="houseType" name="houseType" value="${houseTypeName}" readonly="readonly">
	
		<label class="small"><spring:message code="motion.year" text="Year"/>*</label>
		<input type="text" class="sText" id="year" name="year" value="${year}" readonly="readonly">
	
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.sessionType" text="Session Type"/>*</label>		
		<input type="text" class="sText" id="sessionType" name="sessionType" value="${sessionType}" readonly="readonly">
		
		<label class="small"><spring:message code="motion.type" text="Type"/>*</label>
		<input type="text" class="sText" id="motionType" name="motionType" value="${motionType}" readonly="readonly">
	</p>	
		
	<p>
		<label class="small"><spring:message code="motion.primaryMember" text="Primary Member"/>*</label>
		<input id="primaryMember" class="sText" type="text"  value="${primaryMemberName}" readonly="readonly" style="height: 28px;">
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="motion.supportingMembers" text="Supporting Members"/></label>
		<textarea id="supportingMembers"  class="sTextarea" readonly="readonly" rows="2" cols="50">${supportingMembersName}</textarea>
		<a href="#" id="viewStatus"><spring:message code="motion.viewstatus" text="View Status"></spring:message></a>		
	</p>
	
	<p>
		<label class="centerlabel"><spring:message code="motion.subject" text="Subject"/>*</label>
		<form:textarea path="approvedSubject" readonly="true" rows="2" cols="50"></form:textarea>
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="motion.details" text="Details"/>*</label>
		<form:textarea path="approvedText" cssClass="wysiwyg" readonly="true"></form:textarea>
	</p>	
	<c:choose>
	<c:when test="${status=='COMPLETED' || status=='TIMEOUT'}">
	<p>
	<label class="small"><spring:message code="motion.decisionstatus" text="Decision?"/>*</label>
	<input id="formattedDecisionStatus" name="formattedDecisionStatus" class="sText" readonly="readonly" value="${formattedDecisionStatus}">
	<input id="decisionStatus" name="decisionStatus" class="sText" readonly="readonly" value="${decisionStatus}" type="hidden">
	</p>	
	<div class="fields">
		<h2></h2>
		<%-- <p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
		</p> --%>
	</div>	
	</c:when>
	<c:otherwise>
	<p>
		<label class="small"><spring:message code="motion.decisionstatus" text="Decision?"/>*</label>
		<form:select path="decisionStatus" cssClass="sSelect" items="${decisionStatus}" itemLabel="name" itemValue="id"/>
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" >
		</p>
	</div>	
	</c:otherwise>
	</c:choose>	
	<p style="display:none;">
		<label class="small"><spring:message code="supportingmember.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError"></form:errors>
	</p>	
		
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<form:hidden path="approvalType"/>	
	<input type="hidden" id="currentSupportingMember" name="currentSupportingMember" value="${currentSupportingMember}">
	<input type="hidden" id="requestReceivedOnDate" name="requestReceivedOnDate" value="${requestReceivedOnDate }">
	<input type="hidden" id="workflowDetailsId" name="workflowDetailsId" value="${workflowDetailsId}">	
	<input type="hidden" id="motion" name="motion" value="${motion}">
</form:form>
</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>