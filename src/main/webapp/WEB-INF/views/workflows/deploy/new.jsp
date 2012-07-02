<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="workflow.deploy" text="Workflow Deployments"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<div class="fields clearfix">
<form action="wf/deploy" method="POST">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.deploy.heading" text="Deploy Bar"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<p>
		<label class="small"><spring:message code="workflow.deploy.upload" text="Upload BAR"/></label>		
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="photo" />
			<jsp:param name="filetypes" value="bar" />			
		</jsp:include>		
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
</form>
</div>
</body>
</html>