<%@ include file="/common/taglibs.jsp" %>
<c:choose>
	<c:when test="${not empty departmentDateVOs and fn:length(departmentDateVOs)>0}">
		<c:forEach items="${departmentDateVOs}" var="i" varStatus="dateCounter">
			<tr class="departmentDateRecord" id="departmentDateRecord_${dateCounter.count}">
				<td class="expand" width="25%" valign="top">
					<label style="padding-left: 0px !important;">${i.formattedDiscussionDate}</label>
					<input type="hidden" id="discussionDate${dateCounter.count}" name="discussionDate${dateCounter.count}" value="${i.discussionDate}"/>
				</td>
				<td class="expand" width="50%">
					<c:choose>
						<c:when test="${not empty i.departments and fn:length(i.departments)>0}">
							<table class="innerTable" border="0">
							<c:forEach items="${i.departments}" var="j" varStatus="deptCounter">
								<tr>
									<td width="10%">
										<label>(${j[2]}) <!-- department priority --></label>
										<input type="hidden" id="discussionDate${dateCounter.count}_department${deptCounter.count}_priority" name="discussionDate${dateCounter.count}_department${deptCounter.count}_priority" value="${j[2]}"/>
									</td>
									<td width="90%">
										<label style="padding-left: 0px !important;">${j[1]} <!-- department name --></label>
										<input type="hidden" id="discussionDate${dateCounter.count}_department${deptCounter.count}_name" value="${j[1]}"/>
									</td>
								</tr>
								<input type="hidden" class="discussionDate${dateCounter.count}_department" id="discussionDate${dateCounter.count}_department${deptCounter.count}" name="discussionDate${dateCounter.count}_department${deptCounter.count}" value="${j[0]}"/> <!-- department id -->
							</c:forEach>
							</table>
							<input type="hidden" id="discussionDate${dateCounter.count}_departmentsCount" name="discussionDate${dateCounter.count}_departmentsCount" value="${fn:length(i.departments)}"/>
						</c:when>						
						<c:otherwise>
							<label><spring:message code="please.select" text="Please Select"/></label>
							<input type="hidden" id="discussionDate${dateCounter.count}_departmentsCount" name="discussionDate${dateCounter.count}_departmentsCount" value="0"/>
						</c:otherwise>
					</c:choose>
				</td>
				<td class="expand" width="25%" valign="top">
					<label style="padding-left: 0px !important;">${i.formattedSubmissionEndDate}</label>
					<input type="hidden" id="submissionEndDate${dateCounter.count}" name="submissionEndDate${dateCounter.count}" value="${i.submissionEndDate}"/>
				</td>
			</tr>
		</c:forEach>
		<input type="hidden" id="discussionDatesCount" name="discussionDatesCount" value="${fn:length(departmentDateVOs)}"/>
	</c:when>
	<c:otherwise>
		<tr>
			<td colspan="3">
				<spring:message code="cutmotiondate.no_discussion_date_set" text="Please click on above green icon to add atleast one discussion date."/>
				<input type="hidden" id="discussionDatesCount" name="discussionDatesCount" value="0"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>