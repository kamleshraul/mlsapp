<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'nosessionentriesfound'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'houseformationyearnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'qis_allowed_usergroups_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.qis_allowed_usergroups_notset" text="Custom Parameter 'QIS_ALLOWED_USERGROUPTYPES' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'current_user_has_no_usergroups'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.current_user_has_no_usergroups" text="No QIS usergroup set for current user"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'mytask_grid_workflow_types_allowed_by_default_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.mytask_grid_workflow_types_allowed_by_default_notset" text="Custom Parameter 'MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'no_answer_provided_department'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.no_answer_provided_department" text="Department must provide answer."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'no_factual_position_provided_department'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.no_factualposition_provided_department" text="Department must provide factual position."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'no_reply_provided_department'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="device.errorcode.no_reply_provided_department" text="Department must provide reply."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'user_not_allowed'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.user_not_allowed" text="User not allowed."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'answer_sent_post_last_date_of_answer_receiving'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.answer_sent_post_last_date_of_answer_receiving" text="Answer cannot be sent post last date of answer receiving."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:otherwise>
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="generic.errorcode.some_exception_occurred" text="Some Exception Occurred."/>
		</p>
		<p></p>
	</div>
</c:otherwise>
</c:choose>