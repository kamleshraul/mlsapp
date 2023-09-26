<%@ include file="/common/taglibs.jsp"%>

<c:choose>	
	<c:when test="${empty param.filetag and param.isUploadAllowed != false}">
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="${param.fileid}" />
			<jsp:param name="storageType" value="${param.storageType}" />
			<jsp:param name="locationHierarchy" value="${param.locationHierarchy}" />
			<jsp:param name="maxFileSizeMB" value="${param.maxFileSizeMB}" />
		</jsp:include>
	</c:when>
	<c:when test="${empty param.filetag and param.isUploadAllowed == false}">
		<b><c:out value="File not uploaded yet."/></b>
	</c:when>
	<c:otherwise>		
		<jsp:include page="/common/file_download.jsp">
			<jsp:param name="fileid" value="${param.fileid}" />
			<jsp:param name="filetag" value="${param.filetag}" />
			<jsp:param name="isRemovable" value="${param.isRemovable}" />
			<jsp:param name="isDeletable" value="${param.isDeletable}" />
			<jsp:param name="storageType" value="${param.storageType}" />
			<jsp:param name="locationHierarchy" value="${param.locationHierarchy}" />
			<jsp:param name="maxFileSizeMB" value="${param.maxFileSizeMB}" />
		</jsp:include>
	</c:otherwise>
</c:choose>