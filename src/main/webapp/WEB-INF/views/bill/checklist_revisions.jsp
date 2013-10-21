<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.revisions" text="Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>
	<c:choose>
		<c:when test="${!(empty drafts) }">
			<table class="uiTable" style="width: 900px !important;">
				<tr>
					<th style="max-width: 150px !important;"><spring:message code="bill.checklistParameter" text="Checklist Parameter"></spring:message></th>
					<c:forEach var="i" items="${draftRevisedByHeaders}">
						<th style="max-width: 150px !important;">${i}</th>
					</c:forEach>							
				</tr>		
				<c:set var="checklistKey" value="${drafts[0][5]}"/>
				<c:set var="draftNumber" value="1" />		
				<c:forEach items="${drafts}" var="i">
					<c:if test="${i[5]!=checklistKey}">
						<c:set var="draftNumber" value="1" />
						</tr>
						<c:set var="checklistKey" value="${i[5]}"/>
					</c:if>					
					<c:choose>
						<c:when test="${draftNumber==1}">
							<tr>
								<td style="max-width: 150px !important;">
									<div style="word-wrap: break-word;"><spring:message code="bill.${i[5]}" text="${i[5]}"/></div>									
									<%-- <div style="word-wrap: break-word;">${i[5]}</div> --%>
								</td>
								<td style="max-width: 150px !important;">${i[6]}</td>
						</c:when>
						<c:otherwise>
							<td style="max-width: 150px !important;">${i[6]}</td>
						</c:otherwise>
					</c:choose>	
					<c:set var="draftNumber" value="${draftNumber+1}" />			
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="bill.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>