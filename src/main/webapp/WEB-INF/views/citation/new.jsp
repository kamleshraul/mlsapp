<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="citation" text="Citation" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		$("#deviceType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		$("#status").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
		<form:form action="citation" method="POST"
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
			<label class="small"><spring:message code="citation.deviceType" text="Device Type"/></label>
			<form:select path="deviceType" items="${deviceTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="deviceType" cssClass="validationError"/>	
			</p>
			<p> 
			<label class="small"><spring:message code="citation.status" text="Status"/></label>
			<form:select id="status" name="status" path="status" items="${statuses}" itemValue="type" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="status" cssClass="validationError"/>	
			</p>
			<p>
				<label class="small"><spring:message code="citation.type" text="Citation Type" />&nbsp;*</label>
				<form:textarea cssClass="sText" path="type" />
				<form:errors path="type" cssClass="validationError" />
			</p>
			
			<p>
				<label class="labelcentered"><spring:message code="citation.text" text="Citation" />&nbsp;</label>
				<form:textarea cssClass="sTextarea" path="text" />
				<form:errors path="text" cssClass="validationError" />
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
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
</body>
</html>