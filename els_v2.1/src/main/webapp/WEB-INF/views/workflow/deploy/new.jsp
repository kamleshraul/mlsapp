<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="workflow.deploy" text="Deployments"/>
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
<div class="fields clearfix">
<form:form action="workflow" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="workflow.deploy.new.heading" text="Deploy Process"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<p>
		<label class="small"><spring:message code="workflow.deploy.uploadProcess" text="Upload Process"/></label>
		<span id="image_gallery" style="display: inline;margin: 0px;padding: 0px;">
		<img alt="" src="" id="image_process" width="70" height="70">
		</span>
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="process" />
		</jsp:include>
	</p>	
</form:form>
</div>
</body>
</html>