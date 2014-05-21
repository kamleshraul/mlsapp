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
	<c:when test="${errorcode eq 'noClubbedEntitiesFound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.report.errorcode.noClubbedEntitiesFound" text="There are no clubbed questions for this question"/>
			</p>
			<p></p>
		</div>
	</c:when>
	<c:when test="${errorcode eq 'noNameClubbedEntitiesFound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="question.errorcode.domain_not_found" text="There are no name-clubbed questions for this question"/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>