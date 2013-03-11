<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="group.title" text="Groups"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		$('document').ready(function(){	
			initControls();
			$('#key').val('');
			
			$("#ministries").multiSelect();		
			
			$('#submit').click(function(){
				if($('#number').val() == "") {
					$.prompt($('#pleaseSelectGroupNumber').val());
					return false;
				}				
				if($('#ministries').val() == null) {
					$.prompt($('#pleaseSelectMinistries').val());
					return false;
				}
			});		
		});	
	</script>		
</head>
<body>

<div class="fields clearfix">
<form:form action="group" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
	
	<p>
		<label class="small"><spring:message code="group.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p>
		<label class="small"><spring:message code="group.year" text="Year"/>*</label>
		<input id="formattedYear" name="formattedYear" value="${formattedYear}" class="sText" readonly="readonly">
		<input id="year" name="year" value="${year}" type="hidden">
	</p>
	
	<p>
		<label class="small"><spring:message code="group.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">			
		<form:errors path="sessionType" cssClass="validationError"/>	
	</p>
		 
	<p>
		<label class="small"><spring:message code="group.number" text="Group" /></label>					
		<form:select path="number" id="number" cssClass="sSelect">
			<c:if test="${empty selectedNumber}">
				<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>
			</c:if>
			<c:forEach var="i" items="${groupNumbers}">					
				<c:choose>
					<c:when test="${i.value==selectedNumber }">
						<option value="${i.value}" selected="selected"><c:out value="${i.name}"></c:out></option>				
					</c:when>
					<c:otherwise>
						<option value="${i.value}" ><c:out value="${i.name}"></c:out></option>			
					</c:otherwise>
				</c:choose>
			</c:forEach>			
		</form:select>
		<form:errors path="number" cssClass="validationError" />
	</p>
	<p>
		<label style="vertical-align: top; width: 142px"><spring:message code="group.ministries" text="Ministries" /></label>			
		<form:select path="ministries" id="ministries" items="${ministries}" itemValue="id" itemLabel="name" multiple="multiple" size="10"/>
		<form:errors path="ministries" cssClass="validationError" />
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>

<input id="pleaseSelectGroupNumber" value="<spring:message code='NotNull.groupNumber' text='Please select group number'/>" type="hidden">
<input id="pleaseSelectMinistries" value="<spring:message code='NotNull.ministries' text='Please select ministries in the group'/>" type="hidden">
</div>	
</body>
</html>