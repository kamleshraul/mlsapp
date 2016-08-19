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
</c:choose>