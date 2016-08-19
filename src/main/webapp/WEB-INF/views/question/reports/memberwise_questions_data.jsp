<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${empty memberwiseQuestions}">
		<h3><spring:message code="qis.memberwisequestions.noentriesfound" text="No Questions Submitted Yet."/></h3>
	</c:when>
	<c:otherwise>
		<div id="reportDiv" >
			<h2 align="center">${memberwiseQuestions[0][0]}, ${memberwiseQuestions[0][9]}</h2>
			<table class="strippedTable" border="1" style="margin-top: 20px; margin-left: 25px; font-size: 15px;">
				<thead>
					<tr>
						<th style="text-align: center;font-size: 15px;"><spring:message code="general.srnumber" text="S.No."/></th>
						<th style="text-align: center;font-size: 15px;"><spring:message code="qis.memberwisequestions.number" text="Question Number"/></th>
						<th style="text-align: center;font-size: 15px;"><spring:message code="qis.memberwisequestions.groupNumber" text="Group Number"/></th>
						<th style="text-align: center;font-size: 15px;"><spring:message code="qis.memberwisequestions.subject" text="Subject"/></th>
						<th style="text-align: center;font-size: 15px;"><spring:message code="qis.memberwisequestions.workstatus" text="Work Status"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${memberwiseQuestions}" var="question" varStatus="rowNumber">
						<tr>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${formatter.formatNumberNoGrouping(rowNumber.count, locale)}.
							</td>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${question[1]}
							</td>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${formatter.formatNumberNoGrouping(question[10],locale)}
							</td>
							<td style="padding-left: 15px;vertical-align: top;">
								${formatter.formatNumbersInGivenText(question[2], locale)}
							</td>
							<td style="padding-left: 15px; font-weight: bold; text-align: center;vertical-align: top;">
								<c:choose>
									<c:when test="${question[7]=='questions_starred' and question[8]=='question_final_admission' and (empty question[5])}">
										${question[3]}<br/><br/><spring:message code="generic.date" text="Date"/> ${question[4]}
									</c:when>
									<c:when test="${question[7]=='questions_starred' and question[8]=='question_final_admission' and (not empty question[5])}">
										${question[3]}<br/><br/><spring:message code="generic.date" text="Date"/> ${question[6]}
									</c:when>
									<c:otherwise>${question[3]}</c:otherwise>									
								</c:choose>							
							</td>							
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>