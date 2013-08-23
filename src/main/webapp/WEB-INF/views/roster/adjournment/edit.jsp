<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster.adjournment" text="Adjournment"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="roster/adjournment" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.adjournment.edit.heading" text="Adjournment:ID"/>(${domain.id})		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.starttime" text="Start Time"/>*</label>
		<input type="text" class="sText datetimemask" name="selectedStartTime" id="selectedStartTime" value="${startTime }">
		<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.endtime" text="End Time"/>*</label>
		<input type="text" class="sText datetimemask" name="selectedEndTime" id="selectedEndTime" value="${endTime }">
		<form:errors path="endTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.adjournmentreason" text="Reason"/>*</label>
		<form:select path="reason" cssClass="sSelect" items="${reasons }" itemLabel="reason" itemValue="id"/>		
		<form:errors path="reason" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.action" text="Action"/>*</label>
		<select id="action" name="action" class="sSelect">
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
		<c:choose>
		<c:when test="${domain.action=='turnoff'}">
		<option value="turnoff" selected="selected"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="turnoff"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${domain.action=='shift'}">
		<option value="shift" selected="selected"><spring:message code="roster.adjournment.shiftslots" text="Shift Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="shift"><spring:message code="roster.adjournment.shiftslots" text="Shift Slots"></spring:message></option>
		</c:otherwise>
		</c:choose>
		</select>
		<form:errors path="action" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="roster.adjournment.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>	
	
		
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>		
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<input type="hidden" id="roster" name="roster" value="${roster}"/>
</form:form>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
</div>
</body>
</html>