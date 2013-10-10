<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="governmentproject" text="Government Project" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
		<form:form action="governmentproject" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<form:errors path="version" cssClass="validationError" />
			<p>
				<label class="small"><spring:message
						code="governmentproject.name" text="Government Project" />&nbsp;*</label>
				<form:input cssClass="sText " path="name" />
				<form:errors path="name" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="governmentproject.type" text="Type" />&nbsp;*</label>
				<form:input cssClass="sText " path="type" />
				<form:errors path="type" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="fort.district" text="District" />&nbsp;*</label>
				<form:select path="district" cssClass="sSelect" items="${districts}" itemLabel="name" itemValue="id"></form:select>
				<form:errors path="district" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="governmentproject.identificationkey" text="Key" />&nbsp;*</label>
				<form:input cssClass="sText " path="identificationkey" />
				<form:errors path="identificationkey" cssClass="validationError" />
			</p>
			<div class="fields expand">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef"> 
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
						
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>