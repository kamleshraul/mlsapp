<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>No Clubbing</title>
</head>
<body>
<h4 id="error_p">&nbsp;</h4>
<c:choose>
<c:when test="${flag=='ALREADY_CLUBBED' }">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clubbing.result.already_clubbed" text="This question is already clubbed"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${flag=='CLUBBING_NOT_ALLOWED' }">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clubbing.result.not_allowed" text="Clubbing is not allowed at this stage"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${flag=='REQUEST_PARAMETER_ISEMPTY' }">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clubbing.result.idisempty" text="Check request parameter 'id' for no value"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${flag=='REQUEST_PARAMETER_NULL' }">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clubbing.result.idisnull" text="Check request parameter 'id' for null value"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${flag=='houseformationyearnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>

</body>
</html>