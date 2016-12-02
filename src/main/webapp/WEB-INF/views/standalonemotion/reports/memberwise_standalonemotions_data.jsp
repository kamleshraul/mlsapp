<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${empty memberwiseMotions}">
		<h3><spring:message code="smois.memberwisemotions.noentriesfound" text="No Motions Submitted Yet."/></h3>
	</c:when>
	<c:otherwise>
		<div id="reportDiv" >
			<h2 align="center">${memberwiseMotions[0][0]}, ${memberwiseMotions[0][7]}</h2>
			<table class="strippedTable" border="1" style="margin-top: 20px; margin-left: 25px; font-size: 15px;">
				<thead>
					<tr>
						<th style="text-align: center;font-size: 15px;"><spring:message code="general.srnumber" text="S.No."/></th>
						<th style="text-align: center;font-size: 15px;"><spring:message code="smois.memberwisemotions.number" text="Motion Number"/></th>
						<c:if test="${memberwiseMotions[0][9]=='upperhouse'}">
							<th style="text-align: center;font-size: 15px;"><spring:message code="smois.memberwisemotions.groupNumber" text="Group Number"/></th>
						</c:if>
						<th style="text-align: center;font-size: 15px;"><spring:message code="smois.memberwisemotions.subject" text="Subject"/></th>
						<c:if test="${memberwiseMotions[0][9]=='upperhouse'}">
							<th style="text-align: center;font-size: 15px;"><spring:message code="smois.memberwisemotions.subdepartment" text="Sub-Department"/></th>
						</c:if>
						<th style="text-align: center;font-size: 15px;"><spring:message code="smois.memberwisemotions.workstatus" text="Work Status"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${memberwiseMotions}" var="motion" varStatus="rowNumber">
						<tr>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${formatter.formatNumberNoGrouping(rowNumber.count, locale)}.
							</td>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${motion[1]}
							</td>
							<c:if test="${motion[9]=='upperhouse'}">
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								${formatter.formatNumberNoGrouping(motion[8],locale)}
							</td>
							</c:if>
							<td style="padding-left: 15px;vertical-align: top;">
								${formatter.formatNumbersInGivenText(motion[2], locale)}
							</td>
							<c:if test="${motion[9]=='upperhouse'}">
								<td style="padding-left: 15px;vertical-align: top;">
									${motion[10]}
								</th>
							</c:if>
							<td style="padding-left: 15px; font-weight: bold; text-align: center;vertical-align: top;">
								${motion[3]}						
							</td>							
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>