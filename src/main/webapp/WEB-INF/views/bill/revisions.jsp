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
			<table class="uiTable">
				<tr>
					<th><spring:message code="bill.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="bill.remark" text="Remarks"></spring:message></th>
					<c:choose>
					<c:when test="${drafts[0][0]!='titles'}">
						<th><spring:message code="bill.title" text="Title"></spring:message></th>		
					</c:when>
					<c:otherwise>
						<th><spring:message code="bill.titleInMarathi" text="Marathi Title"></spring:message></th>
						<th><spring:message code="bill.titleInHindi" text="Hindi Title"></spring:message></th>
						<th><spring:message code="bill.titleInEnglish" text="English Title"></spring:message></th>
					</c:otherwise>
					</c:choose>
					<c:if test="${drafts[0][0]=='contentDrafts'}">
						<th><spring:message code="bill.contentDraftInMarathi" text="Marathi Content Draft"></spring:message></th>
						<th><spring:message code="bill.contentDraftInHindi" text="Hindi Content Draft"></spring:message></th>
						<th><spring:message code="bill.contentDraftInEnglish" text="English Content Draft"></spring:message></th>
					</c:if>
					<c:if test="${drafts[0][0]=='statementOfObjectAndReasonDrafts'}">
						<th><spring:message code="bill.statementOfObjectAndReasonDraftInMarathi" text="Marathi Statement Of Object And Reason Draft"></spring:message></th>
						<th><spring:message code="bill.statementOfObjectAndReasonDraftInHindi" text="Hindi Statement Of Object And Reason Draft"></spring:message></th>
						<th><spring:message code="bill.statementOfObjectAndReasonDraftInEnglish" text="English Statement Of Object And Reason Draft"></spring:message></th>
					</c:if>
					<c:if test="${drafts[0][0]=='financialMemorandumDrafts'}">
						<th><spring:message code="bill.financialMemorandumDraftInMarathi" text="Marathi Financial Memorandum Draft"></spring:message></th>
						<th><spring:message code="bill.financialMemorandumDraftInHindi" text="Hindi Financial Memorandum Draft"></spring:message></th>
						<th><spring:message code="bill.financialMemorandumDraftInEnglish" text="English Financial Memorandum Draft"></spring:message></th>
					</c:if>
					<c:if test="${drafts[0][0]=='statutoryMemorandumDrafts'}">
						<th><spring:message code="bill.statutoryMemorandumDraftInMarathi" text="Marathi Statutory Memorandum Draft"></spring:message></th>
						<th><spring:message code="bill.statutoryMemorandumDraftInHindi" text="Hindi Statutory Memorandum Draft"></spring:message></th>
						<th><spring:message code="bill.statutoryMemorandumDraftInEnglish" text="English Statutory Memorandum Draft"></spring:message></th>
					</c:if>		
					<c:if test="${drafts[0][0]=='annexuresForAmendingBill'}">
						<th><spring:message code="bill.annexureForAmendingBillInMarathi" text="Marathi Annexure For Amending Bill"></spring:message></th>
						<th><spring:message code="bill.annexureForAmendingBillInHindi" text="Hindi Annexure For Amending Bill"></spring:message></th>
						<th><spring:message code="bill.annexureForAmendingBillInEnglish" text="English Annexure For Amending Bill"></spring:message></th>
					</c:if>							
				</tr>				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<td>${i[1]}<br>(${i[2]}-${i[3]})<br>${i[4]}</td>
						<c:choose>
						<c:when test="${i[0]!='titles'}">
							<td>${i[11]}</td>
							<c:set var="isTitleSet" value="no"/>
							<c:if test="${defaultTitleLanguage=='marathi' and not empty i[5]}">
								<td>${i[5]}</td>
								<c:set var="isTitleSet" value="yes"/>
							</c:if>
							<c:if test="${defaultTitleLanguage=='hindi' and not empty i[6]}">
								<td>${i[6]}</td>
								<c:set var="isTitleSet" value="yes"/>
							</c:if>
							<c:if test="${defaultTitleLanguage=='english' and not empty i[7]}">
								<td>${i[7]}</td>
								<c:set var="isTitleSet" value="yes"/>
							</c:if>
							<c:if test="${isTitleSet=='no'}">
								<c:if test="${not empty i[5]}">
									<td>${i[5]}</td>
									<c:set var="isTitleSet" value="yes"/>								
								</c:if>
								<c:if test="${not empty i[6]}">
									<td>${i[6]}</td>	
									<c:set var="isTitleSet" value="yes"/>								
								</c:if>
								<c:if test="${not empty i[7]}">
									<td>${i[7]}</td>		
									<c:set var="isTitleSet" value="yes"/>							
								</c:if>
							</c:if>
							<c:if test="${isTitleSet=='no'}">
								<td></td>
							</c:if>						
							<td>${i[8]}</td>
							<td>${i[9]}</td>
							<td>${i[10]}</td>
						</c:when>	
						<c:otherwise>
							<td>${i[8]}</td>
							<td>${i[5]}</td>						
							<td>${i[6]}</td>
							<td>${i[7]}</td>
						</c:otherwise>
						</c:choose>									
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="bill.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>