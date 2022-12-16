<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
		if($("#revisedSubjectEdit").val()!=''){
			$("#revisedSubjectEditDiv").show();
		}
		if($("#revisedQuestionTextEdit").val()!=''){
			$("#revisedQuestionTextEditDiv").show();
		}
					
	});
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="motion" method="PUT" modelAttribute="domain">

	<%@ include file="/common/info.jsp" %>
	<h2>${formattedDiscussionMotionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
		
	<p>
	<label class="small"><spring:message code="discussionmotion.number" text="Motion Number"/>*</label>
	<input id="formattedNumberEdit" name="formattedNumberEdit" value="${formattedNumber}" class="sText" readonly="readonly">		
	</p>
		
	<p>		
	<label class="small"><spring:message code="discussionmotion.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDateEdit" name="formattedSubmissionDateEdit" value="${formattedSubmissionDate}" class="sText" readonly="readonly">
	
	<c:if test="${(internalStatusType=='discussionmotion_final_admission')}">
		<label class="small"><spring:message code="discussionmotion.discussionDate" text="Discussion Date"/></label>
		
		<input id="formattedDiscussionDate" name="formattedDiscussionDate" value="${formattedDiscussionDate}" class="datemask sText" />
		<input id="discussionDate" name="discussionDate" value="${discussionDate}" class="sText" type="hidden" />
		<form:errors path="discussionDate" cssClass="validationError"/>
	</c:if> 
	</p>
	
	<p>
	<label class="labeltop"><spring:message code="discussionmotion.ministry" text="Ministry"/>*</label>
	<select name="ministryEdit" id="ministryEdit" class="sSelectMultiple" size="5" multiple="multiple" disabled="disabled">
		<c:forEach items="${ministries}" var="i">
			<c:set var="selectedMinistry" value="no"></c:set>
			<c:forEach items="${selectedministries}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedMinistry" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedMinistry=='yes'}">
					<option selected="selected" value="${i.id}">${i.dropdownDisplayName}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.dropdownDisplayName}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>		
	
	<label class="labeltop"><spring:message code="discussionmotion.subdepartment" text="Sub Department"/></label>
	<select name="subDepartmentEdit" id="subDepartmentEdit" multiple="multiple" size="5" class="sSelectMultiple" style="max-width: 188px !important;" disabled="disabled">
		<c:forEach items="${subDepartments}" var="i">
			<c:set var="selectedSubDepartment" value="no"></c:set>
			<c:forEach items="${selectedSubDepartments}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedSubDepartment" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedSubDepartment=='yes'}">
					<option selected="selected" value="${i.id}">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>		
	<form:errors path="subDepartments" cssClass="validationError"/>	
	</p>	
		
	
	<p>
	<label class="centerlabel"><spring:message code="discussionmotion.members" text="Members"/></label>
	<textarea id="membersEdit" class="sTextareaEdit" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMemberEdit" name="primaryMemberEdit" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembersEdit" id="selectedSupportingMembersEdit" multiple="multiple" style="display:none;">
		<c:if test="${(selectedDiscussionMotionType=='motions_discussionmotion_shortduration')
			 and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
			<label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="question.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label>										
		</c:if>
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	</p>
	
	<p>
		<label class="small"><spring:message code="discussionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
	</p>			
	
	<p>
	<label class="small"><spring:message code="discussionmotion.parentquestion" text="Clubbed To"></spring:message></label>
	<a href="#" id="p${parent}" ><c:out value="${formattedParentNumber}"></c:out></a>
	</p>	
	<p>
	<label class="small"><spring:message code="discussionmotion.clubbedquestions" text="Clubbed Motions"></spring:message></label>
	<c:choose>
		<c:when test="${!(empty clubbedQuestions) }">
			<c:forEach items="${clubbedQuestions }" var="i">
				<a href="#" id="cq${i.id}" class="clubbedQuestions" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:out value="-"></c:out>
		</c:otherwise>
	</c:choose>	
	</p>
	<p>
	<label class="small"><spring:message code="discussionmotion.referencedquestions" text="Referenced Motions"></spring:message></label>
	<c:choose>
		<c:when test="${!(empty referencedQuestions) }">
			<c:forEach items="${referencedQuestions }" var="i">
				<a href="#" id="rq${i.number}" class="referencedQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:out value="-"></c:out>
		</c:otherwise>
	</c:choose>	
	</p>	
	
	<p>	
	<label class="centerlabel"><spring:message code="discussionmotion.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="discussionmotion.noticeContent" text="Notice Content"/></label>
	<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
	</p>
	
		
	<p style="display:none;" class="revise" id="revisedSubjectEditDiv">
	<label class="centerlabel"><spring:message code="discussionmotion.revisedSubject" text="Revised Subject"/></label>
	<textarea id="revisedSubjectEdit" rows="2" cols="50" class="sTextarea">${domain.revisedSubject}</textarea>
	</p>
	
	<p style="display:none;" class="revise" id="revisedQuestionTextEditDiv">
	<label class="wysiwyglabel"><spring:message code="discussionmotion.revisedNoticeContent" text="Revised Notice Content"/></label>
	<textarea id="revisedNoticeContentEdit" class="wysiwyg">${domain.revisedNoticeContent}</textarea>
	</p>
	
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.briefExplanation" text="Brief Explanation"/>*</label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="discussionmotion.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
		
	<p>
	<label class="wysiwyglabel"><spring:message code="discussionmotion.remarks" text="Remarks"/></label>
	<textarea id="remarksEdit" class="wysiwyg">${domain.remarks}</textarea>
	</p>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
</form:form>
</div>
</body>
</html>