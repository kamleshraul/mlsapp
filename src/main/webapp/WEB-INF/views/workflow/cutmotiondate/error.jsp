<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'CAN_NOT_INITIATE'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.workunderprogress" text="Work under progress"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cannot_populateafterupdate'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.populateAfterUpdate" text="Can not populate after update."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'budgetlaydat_setting_error'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.budgetlaydate_setting" text="Budget Lay Date is not set for this Device."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'discussiondate_setting_error'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.date_setting" text="Discussion Date can not be set."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'date_exists_for_cutmotion'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.date_exists" text="date for this device is already created."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'workunderprogress'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.workunderprogress" text="Work under progress"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'no_budget_session'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.no_budget_session" text="This is not a budget session."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'domain_not_found'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.domain_not_found" text="No Question Found."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotiondraft_not_found_for_remark'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.questiondraft_not_found_for_remark" text="No MotionDraft found to retrieve remark."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'nosessionentriesfound'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'houseformationyearnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${errorcode eq 'permissiondenied'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.permissiondenied" text="You donot have necessary permission"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${errorcode eq 'houseType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.houseType_isempty" text="Check request parameter 'houseType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'houseType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionYear_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.sessionYear_isempty" text="Check request parameter 'sessionYear' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionyear_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.sessionyear_isnull" text="Check request parameter 'sessionYear' for null value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotionType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.questionType_isempty" text="Check request parameter 'questionType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotionType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.questionType_isnull" text="Check request parameter 'questionType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'member_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.member_isnull" text="Authenticated user is not a member"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'rotationorderpubdate_cannotbeparsed'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.rotationorderpubdate_cannotbeparsed" text="Failed to parse rotation order publish date"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'rotationorderpubdate_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.rotationorderpubdate_notset" text="Parameter 'questions_starred_rotationOrderPublishingDate' not set in session"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'session_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.session_isnull" text="Session doesnot exists"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'requestparams_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.requestparams_isnull" text="Check request parameters 'houseType,sessionYear and sessionType for null values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'highestquestionprioritynotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.highestquestionprioritynotset" text="Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cmois_not_authorized'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.cmois_not_authorized" text="User is not authorized to activate this work."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cmois_allowed_usergroups_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.cmois_allowed_usergroups_notset" text="Custom Parameter 'CMOIS_ALLOWED_USERGROUPTYPES' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'current_user_has_no_usergroups'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.current_user_has_no_usergroups" text="No CMOIS usergroup set for current user"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotion_grid_status_allowed_by_default_not_set'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.cutmotion_status_allowed_by_default_not_set" text="Custom Parameter 'CUTMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotion_putup_options_recommend_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.cutmotion_putup_options_recommend_notset" text="Custom Parameter 'CUTMOTION_PUT_UP_OPTIONS_RECOMMEND' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'cutmotion_putup_options_final_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="cutmotion.errorcode.cutmotion_putup_options_final_notset" text="Custom Parameter 'CUTMOTION_PUT_UP_OPTIONS_FINAL' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>