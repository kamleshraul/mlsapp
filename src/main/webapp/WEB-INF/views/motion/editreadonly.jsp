<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
		if($("#edit_departmentSelected").val()==null||$("#edit_departmentSelected").val()==''){
		$("#departmentEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
		$("#departmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#edit_subDepartmentSelected").val()==null||$("#edit_subDepartmentSelected").val()==''){
			$("#subDepartmentEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#subDepartmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}		
		if($("#edit_answeringDateSelected").val()==null||$("#edit_answeringDateSelected").val()==''){
			$("#answeringDateEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
			$("#answeringDateEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
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
<%-- <form:form action="motion" method="PUT" modelAttribute="domain"> --%>

	<%-- <%@ include file="/common/info.jsp" %> --%>
	<h2>${formattedQuestionType}: ${formattedNumber}</h2>
		
	<p>
	<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
	<input id="formattedNumberEdit" name="formattedNumberEdit" value="${formattedNumber}" class="sText" readonly="readonly">		
	</p>
		
	<p>		
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDateEdit" name="formattedSubmissionDateEdit" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	</p>
	
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministryEdit" id="ministryEdit" class="sSelect" disabled="disabled">
			<c:forEach items="${ministries }" var="i">
				<c:choose>
					<c:when test="${i.id==ministrySelected }">
						<option value="${i.id }" selected="selected">${i.dropdownDisplayName}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.dropdownDisplayName}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		
		<%-- <label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="formattedGroupEdit" name="formattedGroupEdit"  readonly="readonly" value="${formattedGroup}"> --%>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<%-- <select name="departmentEdit" id="departmentEdit" class="sSelect" disabled="disabled">
			<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${departments }" var="i">
				<c:choose>
					<c:when test="${i.id==departmentSelected }">
						<option value="${i.id }" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		
		<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label> --%>
		<select name="subDepartmentEdit" id="subDepartmentEdit" class="sSelect" disabled="disabled">
			<option value=""><spring:message code='please.select' text='Please Select'/></option>	
			<c:forEach items="${subDepartments }" var="i">
				<c:choose>
					<c:when test="${i.id==subDepartmentSelected }">
						<option value="${i.id }" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		
	</p>	
		
	
	<p>
	<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
	<textarea id="membersEdit" class="sTextareaEdit" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMemberEdit" name="primaryMemberEdit" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembersEdit" id="selectedSupportingMembersEdit" multiple="multiple" style="display:none;">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
	</p>			
	
	<p>
		<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" ><c:out value="${formattedParentNumber}"></c:out></a>
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedEntities) }">
				<c:forEach items="${clubbedEntities }" var="i">
					<a href="#" id="cq${i.id}" class="clubbedQuestions" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>	
	</p>
	<p>
		<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedMotions) }">
				<c:forEach items="${referencedMotions }" var="i">
					<a href="#" id="rm${i.number}" class="referencedMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
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
		<c:choose>
			<c:when test="${!(empty referencedResolutions) }">
				<c:forEach items="${referencedResolutions }" var="i">
					<a href="#" id="rm${i.number}" class="referencedResolutions" onclick="viewResolutionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
	</p>	
	
	<p>	
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<textarea id="subjectEdit" readonly="true" rows="2" cols="50">${domain.subject}</textarea>
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<textarea id="detailsId" readonly="true" class="wysiwyg">${domain.details}</textarea>
	</p>
	
		
	<p style="display:none;" class="revise" id="revisedSubjectEditDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<textarea id="revisedSubjectEdit" rows="2" cols="50" class="sTextarea">${domain.revisedSubject}</textarea>
	</p>
	
	<p style="display:none;" class="revise" id="revisedQuestionTextEditDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<textarea id="revisedQuestionTextEdit" class="wysiwyg">${domain.revisedDetails}</textarea>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
		
	<p>
	<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
	<textarea id="remarksEdit" class="wysiwyg">${domain.remarks}</textarea>
	</p>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="edit_ministrySelected" value="${ministrySelected }" type="hidden">
<input id="edit_departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="edit_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="edit_answeringDateSelected" value="${ answeringDate}" type="hidden">
<%-- </form:form> --%>
</div>
</body>
</html>