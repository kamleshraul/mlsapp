<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="part.revisions" text="Revisions" /></title>
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
					<th><spring:message code="part.pageheading" text="Page Heading"></spring:message></th>
					<th><spring:message code="part.mainheading" text="Main Heading"></spring:message></th>
					<th><spring:message code="part.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="part.revisedcontent" text="Revised Content"></spring:message></th>
					<th><spring:message code="part.revisedon" text="Revised On"></spring:message></th>
					<th><spring:message code="part.revisedby" text="Revised By"></spring:message></th>
				</tr>
				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<td>${i.pageHeading}</td>
						<td>${i.mainHeading}</td>
						<td>${i.editedAs}</td>
						<td>${i.revisedContent}</td>
						<td>${i.editedOn}</td>
						<td>${i.editedBy}</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="part.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>