<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeesubject" text="Committee Subject"/>
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
<div class="fields clearfix">
<form:form action="committeesubject" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>
		<input type="text" id="committeeDisplayName" name="committeeDisplayName" value="${committeeDisplayName}" readonly="readonly"/>
		<input type="hidden" id="committeeName" name="committeeName" value="${committeName}"/>
	</p>
	<p style="margin-top: 20px;"> 
		<label class="small labeltop"><spring:message code="committeesubject.name" text="Subject"/>*</label>
		<form:textarea path="name" cssClass="sTextArea" rows="5" cols="50"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>