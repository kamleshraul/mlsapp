<%@ include file="/common/taglibs.jsp"%>

<c:choose>	
	<c:when test="${empty param.filetag and param.isUploadAllowed != false}">
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="${param.fileid}" />
		</jsp:include>
	</c:when>
	<c:when test="${empty param.filetag and param.isUploadAllowed == false}">
		
	</c:when>
	<c:otherwise>		
		<jsp:include page="/common/file_download.jsp">
			<jsp:param name="fileid" value="${param.fileid}" />
			<jsp:param name="filetag" value="${param.filetag}" />
			<jsp:param name="isRemovable" value="${param.isRemovable}" />
			<jsp:param name="isDeletable" value="${param.isDeletable}" />
		</jsp:include>
	</c:otherwise>
</c:choose>