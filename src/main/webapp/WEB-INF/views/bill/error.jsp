<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'workunderprogress'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.workunderprogress" text="Work under progress"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'domain_not_found'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.domain_not_found" text="No Bill Found."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'billdraft_not_found_for_remark'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.billdraft_not_found_for_remark" text="No BillDraft found to retrieve remark."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'nosessionentriesfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'houseformationyearnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'permissiondenied'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.permissiondenied" text="You donot have necessary permission"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.houseType_isempty" text="Check request parameter 'houseType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'houseType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionYear_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.sessionYear_isempty" text="Check request parameter 'sessionYear' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionYear_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.sessionYear_isnull" text="Check request parameter 'sessionYear' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'deviceType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.deviceType_isempty" text="Check request parameter 'deviceType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'deviceType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.deviceType_isnull" text="Check request parameter 'deviceType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'member_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.member_isnull" text="Authenticated user is not a member"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'rotationorderpubdate_cannotbeparsed'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.rotationorderpubdate_cannotbeparsed" text="Failed to parse rotation order publish date"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'rotationorderpubdate_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.rotationorderpubdate_notset" text="Parameter 'questions_starred_rotationOrderPublishingDate' not set in session"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'session_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.session_isnull" text="Session doesnot exists"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'requestparams_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.requestparams_isnull" text="Check request parameters 'houseType,sessionYear and sessionType for null values"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bills_nonofficial_billTypesAllowed_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bills_nonofficial_billTypesAllowed_notset" text="Session Parameter 'bills_nonofficial_billTypesAllowed' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bills_government_billTypesAllowed_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bills_government_billTypesAllowed_notset" text="Session Parameter 'bills_government_billTypesAllowed' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bills_nonofficial_billKindsAllowed_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bills_nonofficial_billKindsAllowed_notset" text="Session Parameter 'bills_nonofficial_billKindsAllowed' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bills_government_billKindsAllowed_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bills_government_billKindsAllowed_notset" text="Session Parameter 'bills_government_billKindsAllowed' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'billKind_notfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.billKind_notfound" text="Bill kinds are not defined"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'member_null'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.member_null" text="member is not set for this bill"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'highestbillprioritynotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.highestbillprioritynotset" text="Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'qis_allowed_usergroups_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.qis_allowed_usergroups_notset" text="Custom Parameter 'QIS_ALLOWED_USERGROUPTYPES' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'current_user_has_no_usergroups'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.current_user_has_no_usergroups" text="No BIS usergroup set for current user"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bill_status_allowed_by_default_not_set'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bill_status_allowed_by_default_not_set" text="Custom Parameter 'QUESTION_STATUS_ALLOWED_BY_DEFAULT' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bis_allowed_usergroups_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bis_allowed_usergroups_notset" text="Custom Parameter 'BIS_ALLOWED_USERGROUPTYPES' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bill_putup_options_recommend_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bill_putup_options_recommend_notset" text="Custom Parameter 'QUESTION_PUT_UP_OPTIONS_RECOMMEND' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'bill_putup_options_final_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="bill.errorcode.bill_putup_options_final_notset" text="Custom Parameter 'QUESTION_PUT_UP_OPTIONS_FINAL' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'memberroles_submissionforanyministry_in_government_bill_notset'}">
			<div class="toolTip tpRed clearfix">
				<p>
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="bill.errorcode.memberroles_submissionforanyministry_in_government_bill_notset" text="Custom Parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_BILL' is not set"/>
				</p>
				<p></p>
			</div>
	</c:when>	
	<c:when test="${errorcode eq 'billType_notfound'}">
			<div class="toolTip tpRed clearfix">
				<p>
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="bill.errorcode.billType_notfound" text="Bill types are not defined."/>
				</p>
				<p></p>
			</div>
	</c:when>
	<c:when test="${errorcode eq 'default_bill_type_notset'}">
			<div class="toolTip tpRed clearfix">
				<p>
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="bill.errorcode.default_bill_type_notset" text="custom parameter 'DEFAULT_BILL_TYPE' is either not set or set incorrectly."/>
				</p>
				<p></p>
			</div>
	</c:when>
</c:choose>