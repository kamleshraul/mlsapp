<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'nosessionentriesfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'houseformationyearnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'userdoesnotexist'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="errorcode.permissiondenied" text="You donot have necessary permission"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'languagesnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="errorcode.languagesnotset" text="Languages not set for the reporter"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'CHIEF_REPORTER_NAME_EMPTY'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="proceeding.notifyPendingTurn.reporterNameEmpty" text="Chief Reporter Name not found"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'TITLE_EMPTY'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="proceeding.notifyPendingTurn.titleEmpty" text="Notification Title Not Set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'EXCEPTION_OCCURRED'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="proceeding.notifyPendingTurn.exceptionOccurred" text="Exception Occurred"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:otherwise>
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="generic.error" text="Error Occured"/>
			</p>
			<p></p>
		</div>
	</c:otherwise>
</c:choose>