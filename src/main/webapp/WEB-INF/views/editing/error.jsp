<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'PARAMETER_MISMATCH' }">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="editiong.errorcode.parametermismatch" text="One or more parameters supplied are invalid."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'workunderprogress'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.workunderprogress" text="Work under progress"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:otherwise>
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="draft.errorcode.norevision" text="Not Revised Yet"/>
		</p>
		<p></p>
	</div>
</c:otherwise>
</c:choose>