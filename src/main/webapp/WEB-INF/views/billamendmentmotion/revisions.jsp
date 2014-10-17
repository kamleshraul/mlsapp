<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="billamendmentmotion.revisions" text="Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>
	<c:choose>
		<c:when test="${!(empty drafts) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="billamendmentmotion.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="billamendmentmotion.remark" text="Remarks"></spring:message></th>
					<c:forEach items="${languages}" var="lang">
						<th><spring:message code="billamendmentmotion.sectionNumber_${lang.type}" text="${lang.name} Section Number"></spring:message></th>
					</c:forEach>
					<c:forEach items="${languages}" var="lang">
						<th><spring:message code="billamendmentmotion.amendingContent_${lang.type}" text="${lang.name} Amending Content"></spring:message></th>
					</c:forEach>
				</tr>				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<td>${i.editedAs}<br>(${i.editedBY}-${i.editedOn})<br>${i.status}</td>
 						<td>${i.remarks}</td>
						<c:forEach items="${languages}" var="lang">
							<c:set var="sectionNumberInThisLanguage" value=""/>
							<c:forEach items="${i.sectionAmendments}" var="sa">
								<c:if test="${sa.language==lang.type}">
									<c:set var="sectionNumberInThisLanguage" value="${sa.amendedSectionNumber}"/>
								</c:if>
							</c:forEach>
							<td>${sectionNumberInThisLanguage}</td>							
						</c:forEach>
						<c:forEach items="${languages}" var="lang">
							<c:set var="amendingContentInThisLanguage" value=""/>
							<c:forEach items="${i.sectionAmendments}" var="sa">
								<c:if test="${sa.language==lang.type}">
									<c:set var="amendingContentInThisLanguage" value="${sa.amendingContent}"/>
								</c:if>
							</c:forEach>
							<td>${amendingContentInThisLanguage}</td>							
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="billamendmentmotion.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>