<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'MEMBER_OR_SPEAKER_WORKFLOW_IN_PROGRESS_FOR_THE_ROSTER' }">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="editing.errorcode.rosterworkflowon" text="Workflow for the roster is in progress"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'PARAMETER_MISMATCH' }">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="editiing.errorcode.parametermismatch" text="One or more parameters supplied are invalid."/>
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
<c:when test="${errorcode eq 'UNABLE_TO_GENERATE_VISHAYSUCHI'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="editing.error.vishaysuchigenerationerror" text="Can not prepare Vishaysuchi."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'none'}">
	<div class="toolTip tpGreen clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-on.png">
			<spring:message code="generic.done" text="Done"/>
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