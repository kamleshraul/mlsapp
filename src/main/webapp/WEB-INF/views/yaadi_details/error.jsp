<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorCode eq 'GROUP_NOT_ALLOWED_FOR_USER'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.group_not_allowed_for_user" text="Selected Group is not allowed to access for Yaadi Generation!"/>
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