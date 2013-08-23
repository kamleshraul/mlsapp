<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="election" text="Election"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">

	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form  action="election" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="election.electionType" text="Election Type"/></label>
		<form:select path="electionType" items="${electionTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="electionType" cssClass="validationError"/>		
	</p>
	<p> 
		<label class="small"><spring:message code="election.house" text="Assembly"/></label>
		<form:select path="house" items="${houses}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="house" cssClass="validationError"/>	
	</p>
	<p> 
		<label class="small"><spring:message code="election.name" text="Election"/></label>
		<form:input cssClass="sText" path="name"/>
		<form:errors path="name" cssClass="validationError" />	
	</p>	
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="datemask sText"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="datemask sText"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>	
	<form:hidden path="id" />
	<form:hidden path="locale" />
	<form:hidden path="version" />
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>