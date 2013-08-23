<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_FOR_VIEWING_CHART'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="chart.error.insufficient_parameters_for_viewing_chart" text="Insufficient Parameters for Viewing Chart"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>