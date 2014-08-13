<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'workunderprogress'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.workunderprogress" text="Work under progress"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'domain_not_found'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.domain_not_found" text="No Domain Entry Found."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'nosessionentriesfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'houseformationyearnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'permissiondenied'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.permissiondenied" text="You donot have necessary permission"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.houseType_isempty" text="Check request parameter 'houseType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'houseType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionYear_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.sessionYear_isempty" text="Check request parameter 'sessionYear' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionYear_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.sessionYear_isnull" text="Check request parameter 'sessionYear' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when><c:when test="${errorcode eq 'sessionType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'deviceType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.deviceType_isempty" text="Check request parameter 'deviceType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'deviceType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.deviceType_isnull" text="Check request parameter 'deviceType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'member_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.member_isnull" text="Authenticated user is not a member"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'rotationorderpubdate_cannotbeparsed'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.rotationorderpubdate_cannotbeparsed" text="Failed to parse rotation order publish date"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'rotationorderpubdate_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.rotationorderpubdate_notset" text="Parameter 'questions_starred_rotationOrderPublishingDate' not set in session"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'session_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.session_isnull" text="Session doesnot exists"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'requestparams_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.requestparams_isnull" text="Check request parameters 'houseType,sessionYear and sessionType for null values"/>
			</p>
			<p></p>
		</div>
	</c:when>	
	<c:when test="${errorcode eq 'current_user_has_no_usergroups'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.current_user_has_no_usergroups" text="No BIS usergroup set for current user"/>
			</p>
			<p></p>
		</div>
	</c:when>	
	<c:when test="${empty errorcode}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.someerror" text="Some error occured.. Please contact support."/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>