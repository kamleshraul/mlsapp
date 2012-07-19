<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'actorlistnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.qisactorlist" text="Custom Parameter 'QIS_ACTOR_LIST' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'requiredrolenotfound'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.qisroles" text="You donot have required permission(Required role not found)"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'undefinedhousetype'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.housetype" text="You donot have required permission(Unidentified house type)"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'questiontypeundefined'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.type" text="You donot have required permission(Question type not found)"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'houseformationyearnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.formationyear" text="Custom Parameter 'HOUSE_FORMATION_YEAR' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${errorcode eq 'latestsessionnotset'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="question.undefined.latestsession" text="No Session Info. found"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>