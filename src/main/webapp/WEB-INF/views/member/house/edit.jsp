<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.house" text="Member Role Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
		});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/house" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details"/>
		[<spring:message code="generic.member" text="Member"></spring:message>:&nbsp;
		${domain.member.title.name} ${domain.member.firstName} ${domain.member.middleName} ${domain.member.lastName}]
	</h2>
	<form:errors path="recordIndex" cssClass="validationError" cssStyle="color:red;"/>	
	<p>
	<c:choose>
	<c:when test="${houseType=='lowerhouse'}">
	<label class="small"><spring:message code="generic.lowerhouse" text="Assembly"/></label>
	</c:when>
	<c:when test="${houseType=='upperhouse'}">
	<label class="small"><spring:message code="generic.upperhouse" text="Council"/></label>
	</c:when>
	<c:when test="${houseType=='both'}">
	<label class="small"><spring:message code="generic.bothhouse" text="Council"/></label>
	</c:when>
	<c:otherwise>
	<label class="small"><spring:message code="generic.defaulthouse" text="House"/></label>
	</c:otherwise>
	</c:choose>
		<form:select path="house" items="${houses}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="house" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.house.role" text="Role"/></label>
		<form:select path="role" items="${roles}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="role" cssClass="validationError"/>		
	</p>
	<p>
		<label class="small"><spring:message code="member.house.constituencies" text="Constituency"/></label>
		<form:select path="constituency" items="${constituencies}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="constituency" cssClass="validationError"/>		
	</p>
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="sText datemask"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.house.oathdate" text="Oath Date"/></label>
		<form:input path="oathDate" cssClass="sText datemask"/>
		<form:errors path="oathDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.house.internalVotingDate" text="Internal Voting Date"/></label>
		<form:input path="internalPollDate" cssClass="sText datemask"/>
		<form:errors path="internalPollDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.house.sitting" text="Is Sitting"/></label>
		<form:checkbox path="isSitting" cssClass="sOption" value="true" />
		<form:errors path="isSitting" cssClass="validationError"/>	
	</p>
	<p>
		<label class="labelcentered"><spring:message code="member.house.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="sTextarea" rows="5" cols="50"/>
		<form:errors path="remarks" cssClass="validationError"/>	
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="recordIndex"/>
	<form:hidden path="version"/>
	<form:hidden path="locale"/>			
	<input id="member" name="member" value="${member}" type="hidden">
</form:form>
</div>
</body>
</html>