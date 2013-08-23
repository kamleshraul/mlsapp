<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'INSUFFICIENT_PARAMETERS_TO_SUBMIT_CHOICE'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="resolution.errorcode.INSUFFICIENT_PARAMETERS_TO_SUBMIT_CHOICE" text="All required parameters are not available to fill the choice."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'session_expired'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="resolution.errorcode.session_expired" text="Session has been expired."/>
			</p>
			<p></p>
		</div>
	</c:when>

	<c:when test="${errorcode eq 'memberroles_submissionforanyministry_in_government_resolution_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="resolution.errorcode.memberroles_submissionforanyministry_in_government_resolution_notset" text="Custom Parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION' is not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'member_remarks_for_early_discussion_date_in_government_resolution_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="resolution.errorcode.member_remarks_for_early_discussion_date_in_government_resolution_notset" text="Custom Parameter 'MEMBER_REMARKS_FOR_EARLY_DISCUSSION_DATE_IN_GOVERNMENT_RESOLUTION' is not set"/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'resolutions_government_daysForDiscussionDateToBeDecided_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="resolution.errorcode.resolutions_government_daysForDiscussionDateToBeDecided_notset" text="Session Parameter 'resolutions_government_daysForDiscussionDateToBeDecided' is not set"/>
			</p>
			<p></p>
		</div>
	</c:when>	
</c:choose>