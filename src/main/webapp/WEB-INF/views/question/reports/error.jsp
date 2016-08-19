<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'invalidQuestion'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.invalidQuestion" text="Please select valid question"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'insufficient_parameters'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.insufficient_parameters" text="Insufficient Parameters."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'invalid_parameters'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.invalid_parameters" text="Invalid Parameters."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'SESSIONS_NOTFOUND'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.SESSIONS_NOTFOUND" text="No Session Found"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'HOUSETYPE_NOTFOUND'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.HOUSETYPE_NOTFOUND" text="No HouseType Found"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING" text="Session Parameter for Unstarred Yaadi Numbering is Missing..."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	<c:when test="${errorcode eq 'UNSTARRED_YAADI_NOT_GENERATED_YET'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.UNSTARRED_YAADI_NOT_GENERATED_YET" text="There is no unstarred yaadi laid in this session"/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>