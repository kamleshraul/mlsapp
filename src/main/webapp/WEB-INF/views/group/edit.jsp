<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="group.title" text="Title"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);		
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form  action="group" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
	<p>	 
	<label class="small"><spring:message code="group.number" text="Group" /></label>			
		<form:select path="number" id="number" cssClass="sSelect">
			<c:forEach begin= "1" end = '${groupNo}' var="i">
				<c:choose>
					<c:when test="${domain.number == i}">
						<option value="${i}" selected="selected">${i}</option>
					</c:when>
					<c:otherwise>
						<option value="${i}">${i}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="number" cssClass="validationError" />
	</p>			
	<p>
		<label class="small"><spring:message code="group.houseType" text="House Type" /></label>			
		<form:select path="houseType" id="houseType" cssClass="sSelect">
			<c:forEach items="${houseTypes}" var="i">
				<c:choose>
					<c:when test="${domain.houseType.id == i.id}">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="houseType" cssClass="validationError" />
	</p>	
	<p>
		<label class="small"><spring:message code="group.year" text="Year" /></label>			
		<form:select path="year" id="year" cssClass="sSelect">
			<c:forEach items="${years}" var="i">
				<c:choose>
					<c:when test="${domain.year == i}">
						<option value="${i}" selected="selected">${i}</option>
					</c:when>
					<c:otherwise>
						<option value="${i}">${i}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="year" cssClass="validationError" />
	</p>	
	<p>
		<label class="small"><spring:message code="group.sessionType" text="Session Type" /></label>			
		<form:select path="sessionType" id="sessionType" cssClass="sSelect">
			<c:forEach items="${sessionTypes}" var="i">
				<c:choose>
					<c:when test="${domain.sessionType.id == i.id}">
						<option value="${i.id}" selected="selected">${i.sessionType}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.sessionType}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="sessionType" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="group.ministries" text="Ministries" /></label>			
		<form:select path="ministries" id="ministries" items="${ministries}" itemValue="id" itemLabel="name" multiple="multiple" size="10"/>
		<form:errors path="ministries" cssClass="validationError" />
	</p>				
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<input type="hidden" id="key" name="key">	
	<form:hidden path="version" />
	<form:hidden  path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>	
</body>
</html>