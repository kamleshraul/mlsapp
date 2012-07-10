<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function loadMinistries(group){
		$.get('ref/'+group,function(data){
			$("#ministry").empty();
			var ministries=data.ministries;
			var ministryText="";
			for(var i=0;i<ministries.length;i++){
				ministryText+="<option value='"+ministries[i].id+"'>"+ministries[i].name;
			}
			$("#ministry").html(ministryText);

			$("#department").empty();
			var departments=data.departments;
			var departmentText="";
			for(var i=0;i<departments.length;i++){
				departmentText+="<option value='"+departments[i].id+"'>"+departments[i].name;
			}
			$("#department").html(ministryText);

			$("#answeringDate").empty();
			var answeringDates=data.answeringDates;
			var answeringDatesText="";
			for(var i=0;i<answeringDates.length;i++){
				departmentText+="<option value='"+answeringDates[i].id+"'>"+answeringDates[i].name;
			}
			$("#answeringDate").html(ministryText);
			
		});
	}
	$(document).ready(function(){
		$("#group").change(function(){
			loadMinistriesDepartmentsAnsweringDates($(this).val());
			
		});
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="question" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="question.new.heading" text="Enter Question Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<p>
		<label class="small"><spring:message code="question.houseType" text="House Type"/></label>
		<form:select path="houseType" items="${houseTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="houseType" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="question.year" text="Year"/></label>
		<input id="sessionYear" name="sessionYear" class="integer sText" value="${sessionYear}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.sessionType" text="Session Type"/></label>
		<select id="sessionType" name="sessionType" class="sSelect">
		<c:forEach items="${sessionTypes}" var="i">
		<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
		</c:forEach>
		</select>
		<input type="hidden" id="session" name="session" value="${session}"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="question.type" text="Type"/></label>
		<form:select path="type" items="${questionTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="type" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/></label>
		<form:select path="primaryMember" cssClass="sSelect" items="${members}" itemLabel="name" itemValue="id"/>
		<form:errors path="primaryMember" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="labelcentered"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<form:select path="supportingMembers" cssStyle="width:188px;" items="${members}" itemLabel="name" itemValue="id" multiple="true" size="7"/>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.group" text="Group"/></label>
		<form:select path="group" cssClass="sSelect" items="${groups}" itemLabel="number" itemValue="id"/>
		<form:errors path="group" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/></label>
		<form:select path="ministry" cssClass="sSelect" items="${ministries}" itemLabel="name" itemValue="id"/>
		<form:errors path="ministry" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<form:select path="department" cssClass="sSelect" items="${departments}" itemLabel="department.name" itemValue="id"/>
		<form:errors path="department" cssClass="validationError"/>	
	</p>
		
	<p>
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<form:select path="answeringDate" cssClass="datemask sSelect" items="${answeringDates}" itemLabel="answeringDate" itemValue="id"/>
		<form:errors path="answeringDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="labelcentered"><spring:message code="question.subject" text="Subject"/></label>
		<form:textarea path="subject"></form:textarea>
		<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="labelcentered"><spring:message code="question.details" text="Details"/></label>
		<form:textarea path="questionText"></form:textarea>
		<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.priority" text="Priority"/></label>
		<select id="priority" name="priority" class="sSelect">
		<c:forEach var="i" begin="1" end="${priority}" step="1">
		<option value="${i}"><c:out value="${i}"></c:out></option>
		</c:forEach>
		</select>
		<form:errors path="priority" cssClass="validationError"/>	
	</p>	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">	
</form:form>
</div>
</body>
</html>