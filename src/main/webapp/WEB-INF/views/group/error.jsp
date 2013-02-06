<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'houseformationyearnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseformationyearsetincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.houseformationyearsetincorrect" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) was set with incorrect value."/>
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
	
	<c:when test="${errorcode eq 'userhousetypenotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.userhousetypenotset" text="There is no house type set for the current user."/>
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
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.houseType_isincorrect" text="request parameter 'houseType' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.houseType_isincorrect" text="request parameter 'sessionType' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'year_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.year_isempty" text="Check request parameter 'year' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'year_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.year_isnull" text="Check request parameter 'year' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>

	<c:when test="${errorcode eq 'year_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.year_isincorrect" text="request parameter 'year' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'sessionparametersnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.sessionparametersnotset" text="one or more session device config parameters ending with _difference not set for the session."/>
			</p>
			<p></p>
		</div>
	</c:when>	
</c:choose>