<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'REPORTER_NAME_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="roster.slot.reminderNotification.reporterNameEmpty" text="Reporter Name not found"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'TITLE_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="roster.slot.reminderNotification.titleEmpty" text="Notification Title Not Set"/>
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