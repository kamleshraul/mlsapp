<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="documentlink" text="Document Links"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form  action="documentlink" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>			 
		<p> 
			<label class="small"><spring:message code="documentlink.houseType" text="House Type"/></label>
			<select id="houseType" name="houseType" class="sSelect">
				<c:forEach items="${houseTypes}" var="i">
					<option value="${i.id}">${i.name}</option>
				</c:forEach>
			</select>
			<form:errors path="session" cssClass="validationError"/>
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.sessionType" text="Session Type"/></label>
			<select id="sessionType" name="sessionType" class="sSelect">
				<c:forEach items="${sessionTypes}" var="i">
					<option value="${i.id}">${i.sessionType}</option>
				</c:forEach>
			</select>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.year" text="Session Year"/></label>
			<select id="sessionYear" name="sessionYear" class="sSelect">
				<c:forEach items="${sessionYears}" var="i">
					<option value="${i}">${i}</option>
				</c:forEach>
			</select>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.title" text="Title"/></label>
			<form:input cssClass="sText" path="title"/>
			<form:errors path="title" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.localizedTitle" text="localizedTitle"/></label>
			<form:input cssClass="sText" path="localizedTitle"/>
			<form:errors path="localizedTitle" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.sessionDate" text="Session Date"/></label>
			<form:input cssClass="sText datemask" path="sessionDate"/>
			<form:errors path="sessionDate" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="documentlink.url" text="Link to Document"/></label>
			<form:input cssClass="sText" path="url" style="width:500px;"/>
			<form:errors path="url" cssClass="validationError"/>	
		</p>
		
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>