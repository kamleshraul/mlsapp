<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="resolution" text="Resolution Information System"/>
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
		if($("#revisedNoticeContentEdit").val()!=''){
			$("#revisedNoticeContentEditDiv").show();
		}
		if($("#revisedresolutionTextEdit").val()!=''){
			$("#revisedresolutionTextEditDiv").show();
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
<%-- <form:form action="resolution" method="PUT" modelAttribute="domain" id="resolutionReadOnly"> --%>

	<%-- <%@ include file="/common/info.jsp" %> --%>
	<h2>${formattedDeviceType}: ${formattedNumber}</h2>
	<%-- <form:errors path="version" cssClass="validationError"/> --%>
		
	<p>
	<label class="small"><spring:message code="resolution.number" text="Resolution Number"/>*</label>
	<input id="formattedNumberEdit" name="formattedNumberEdit" value="${formattedNumber}" class="sText" readonly="readonly">		
	</p>
		
	<p>		
	<label class="small"><spring:message code="resolution.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDateEdit" name="formattedSubmissionDateEdit" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	</p>
	
	<p>
	<label class="small"><spring:message code="resolution.ministry" text="Ministry"/>*</label>
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
	</p>	
	
	<p>
		<%-- <label class="small"><spring:message code="resolution.department" text="Department"/></label>
		<select name="departmentEdit" id="departmentEdit" class="sSelect" disabled="disabled">
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
		</select> --%>
	
		<label class="small"><spring:message code="resolution.subdepartment" text="Sub Department"/></label>
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
		
	<%-- <p>
		<label class="small"><spring:message code="resolution.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
	</p> --%>			
	
	<%-- <p>
	<label class="small"><spring:message code="resolution.parentresolution" text="Clubbed To"></spring:message></label>
	<a href="#" id="p${parent}" ><c:out value="${formattedParentNumber}"></c:out></a>
	</p> --%>	
	
	<%-- <p>
		<label class="small"><spring:message code="resolution.clubbedresolutions" text="Clubbed resolutions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedresolutions) }">
				<c:forEach items="${clubbedresolutions }" var="i">
					<a href="#" id="cq${i.id}" class="clubbedresolutions" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>	
	</p> --%>
	
	<p>	
		<label class="small"><spring:message code="resolution.referencedresolution" text="Referenced Resolution"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedresolutions) }">
				<c:forEach items="${referencedresolutions }" var="i">
					<a href="#" id="rq${i.number}" class="referencedResolution" onclick="viewResolutionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>	
	</p>	
	
	<p>	
	<label class="centerlabel"><spring:message code="resolution.subject" text="Subject"/></label>
	<textarea id="subjectResolutionEdit" readonly="true" rows="2" cols="50">${domain.subject}</textarea>
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="resolution.details" text="Details"/></label>
	<textarea id="noticeContentEdit" readonly="true" class="wysiwyg">${domain.noticeContent}</textarea>
	</p>
	
	<p style="display:none;" class="revise" id="revisedSubjectEditDiv">
	<label class="centerlabel"><spring:message code="resolution.revisedSubject" text="Revised Subject"/></label>
	<textarea id="revisedSubjectEdit" rows="2" cols="50" class="sTextarea">${domain.revisedSubject}</textarea>
	</p>
	
	<p style="display:none;" class="revise" id="revisedNoticeContentEditDiv">
	<label class="wysiwyglabel"><spring:message code="resolution.revisedNoticeContent" text="Revised Notice Content"/></label>
	<textarea id="revisedNoticeContentEdit" class="wysiwyg">${domain.revisedNoticeContent}</textarea>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="resolution.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:if test="${not empty domain.factualPosition}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Postion"/></label>
		<textarea id="factualPositionEdit" class="wysiwyg" readonly="readonly">${domain.factualPosition}</textarea>
	</p>
	</c:if>
		
	<p>
	<label class="wysiwyglabel"><spring:message code="resolution.remarks" text="Remarks"/></label>
	<textarea id="remarksEdit" class="wysiwyg">${domain.remarks}</textarea>
	</p>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="edit_ministrySelected" value="${ministrySelected }" type="hidden">
<input id="edit_departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="edit_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<%-- </form:form> --%>
</div>
</body>
</html>