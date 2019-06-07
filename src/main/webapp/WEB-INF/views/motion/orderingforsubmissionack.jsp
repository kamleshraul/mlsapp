<%@ include file="/common/taglibs.jsp"%>
<div id="orderSubmissionAck">
	<%@ include file="/common/info.jsp" %>
</div>
<c:choose>
	<c:when test="${!(empty motions) }">
		<input type="button" id="orderSubmission" value="<spring:message code='generic.submit' text='Order Submission'/>"  style="width: 100px;margin: 10px;"/>		
		<table class="uiTable" style="width: 850px;">
			<tr>
				<th style="min-width: 10px !important; max-width:25px !important;"><spring:message code="generic.serialNo" text="Serial Number"></spring:message>
				<th><spring:message code="motion.subject" text="Subject"></spring:message></th>
				<th style="min-width: 100px !important; max-width:120px !important;"><spring:message code="motion.ordering_for_submission" text="Submission Order"></spring:message></th>
			</tr>			
			<c:set var="index" value="1"></c:set>	
			<c:forEach items="${motions}" var="i" varStatus="loop">
				<input type="hidden" class="motionId" id="motionId${loop.count}" name="motionId${loop.count}" value="${i.id}">
				<tr id="row${loop.count}">
					<%-- <td class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td> --%>
					<td style="min-width: 10px !important; max-width:25px !important;vertical-align: top;">
						${formater.formatNumberNoGrouping(loop.count, locale)}
					</td>
					<td>${i.subject}</td>
					<td style="min-width: 100px !important; max-width:120px !important;">
						<select class="submissionOrder" id="submissionOrder${loop.count}" name="submissionOrder${loop.count}">
							<option value="${defaultSubmissionPriority}"><spring:message code="motion.default_ordering_for_submission" text="Creation Order"/></option>	
							<c:forEach var="submissionOrder" begin="1" end="${fn:length(motions)}" step="1">
								<c:choose>
									<c:when test="${not empty i.submissionPriority and i.submissionPriority!=defaultSubmissionPriority and submissionOrder==loop.count}">
										<option value="${submissionOrder}" selected="selected">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
									</c:when>
									<c:otherwise>
										<option value="${submissionOrder}">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
									</c:otherwise>
								</c:choose>										
							</c:forEach>
						</select>
					</td>					
				</tr>					
				<%-- <c:set var="index" value="${index+1}"></c:set> --%>
			</c:forEach>
		</table>
	</c:when>
	<c:otherwise>
		<spring:message code="motion.nomotions" text="No Completed Motions Found"></spring:message>
	</c:otherwise>
</c:choose>
<input type="hidden" id="motionsCount" value="${fn:length(motions)}"/>