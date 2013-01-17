<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'workunderprogress'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.workunderprogress" text="Work under progress"/>
		</p>
		<p></p>
	</div>
</c:when>
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

<c:when test="${errorcode eq 'permissiondenied'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.permissiondenied" text="You donot have necessary permission"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${errorcode eq 'houseType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.houseType_isempty" text="Check request parameter 'houseType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'houseType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionYear_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.sessionYear_isempty" text="Check request parameter 'sessionYear' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionyear_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.sessionyear_isnull" text="Check request parameter 'sessionYear' for null value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when><c:when test="${errorcode eq 'sessionType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'questionType_isempty'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.questionType_isempty" text="Check request parameter 'questionType' for no value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'questionType_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.questionType_isnull" text="Check request parameter 'questionType' for null value"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'member_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.member_isnull" text="Authenticated user is not a member"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'rotationorderpubdate_cannotbeparsed'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.rotationorderpubdate_cannotbeparsed" text="Failed to parse rotation order publish date"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'rotationorderpubdate_notset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.rotationorderpubdate_notset" text="Parameter 'questions_starred_rotationOrderPublishingDate' not set in session"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'session_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.session_isnull" text="Session doesnot exists"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'requestparams_isnull'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.requestparams_isnull" text="Check request parameters 'houseType,sessionYear and sessionType for null values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'highestquestionprioritynotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.highestquestionprioritynotset" text="Custom Parameter 'HIGHEST_QUESTION_PRIORITY' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>