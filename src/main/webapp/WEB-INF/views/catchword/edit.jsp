<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="catchword" text="Catchwords"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		
		$("#uploadCatchwords").click(function(){
			$.post("catchword/upload/"+$("#catchwordFile").val(),function(data){
				
			});
		});
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
<form:form action="catchword" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="catchword.value" text="Catchword"/>&nbsp;*</label>
			<form:input cssClass="sText" path="value"/>
			<form:errors path="value" cssClass="validationError"/>	
	</p>
	<p>
		<c:choose>
		<c:when test="${empty domain.docID}">
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="fileUpload" />
		</jsp:include>
		</c:when>
		<c:otherwise>
		<jsp:include page="/common/file_download.jsp">
			<jsp:param name="fileid" value="fileUpload" />
			<jsp:param name="filetag" value="${domain.docID}" />
		</jsp:include>
		</c:otherwise>
		</c:choose>
		&nbsp;&nbsp;
		<a href="javascript:void(0);" id="uploadCatchwords"><spring:message code="catchword.upload" text="Upload Catchwords"></spring:message></a>
	</p>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">		
		</p>
	</div>
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>