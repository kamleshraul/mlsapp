<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="proceedingautofill" text="Proceeding Autofill" />
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
		<form:form action="proceedingautofill" method="PUT"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
				 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
			</h2>
			<form:errors path="version" cssClass="validationError" />
			<p>
				<label class="small"><spring:message
						code="proceedingautofill.shortName" text="ShortName" />&nbsp;*</label>
				<form:textarea cssClass="sTextarea" path="shortName" />
				<form:errors path="shortName" cssClass="validationError" />
			</p>
			<p>
				<label class="wysiwyglabel"><spring:message
						code="proceedingautofill.autoFillContent" text="autoFillContent" />&nbsp;*</label>
				<form:textarea cssClass="wysiwyg" path="autoFillContent" cssStyle="width:560px;" />
				<form:errors path="autoFillContent" cssClass="validationError" />
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
			<form:hidden path="username"/>
		</form:form>
	</div>
</body>
</html>