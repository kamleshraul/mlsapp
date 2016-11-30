<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'invalidQuestion'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="motion.report.errorcode.invalidQuestion" text="Please select valid motion"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'insufficient_parameters'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="motion.report.errorcode.insufficient_parameters" text="Insufficient Parameters."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'invalid_parameters'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="motion.report.errorcode.invalid_parameters" text="Invalid Parameters."/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'SESSIONS_NOTFOUND'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="motion.report.errorcode.SESSIONS_NOTFOUND" text="No Session Found"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'HOUSETYPE_NOTFOUND'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="motion.report.errorcode.HOUSETYPE_NOTFOUND" text="No HouseType Found"/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>