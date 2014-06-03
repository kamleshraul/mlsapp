<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${empty memberwiseQuestions}">
		<h3><spring:message code="qis.memberwisequestions.noentriesfound" text="No Questions Submitted Yet."/></h3>
	</c:when>
	<c:otherwise>
		<div id="reportDiv" >
			<h2 align="center">${memberwiseQuestions[0][0]}</h2>
			<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
				<thead>
					<tr>
						<th style="text-align: center;"><spring:message code="general.srnumber" text="S.No."/></th>
						<th style="text-align: center;"><spring:message code="qis.memberwisequestions.number" text="Question Number"/></th>
						<th style="text-align: center;"><spring:message code="qis.memberwisequestions.subject" text="Subject"/></th>
						<th style="text-align: center;"><spring:message code="qis.memberwisequestions.workstatus" text="Work Status"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${memberwiseQuestions}" var="question" varStatus="rowNumber">
						<tr>
							<td style="padding-left: 15px;">${formatter.formatNumberNoGrouping(rowNumber.count, locale)}</td>
							<td style="padding-left: 15px;">${question[1]}</td>
							<td style="padding-left: 15px;">${formatter.formatNumbersInGivenText(question[2], locale)}</td>
							<td style="padding-left: 15px;">${question[3]}</td>							
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>