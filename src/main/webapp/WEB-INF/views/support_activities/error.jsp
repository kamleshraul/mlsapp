<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${errorcode eq 'SOME_ERROR_OCCURED_CONTACT_ADMIN'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="generic.errorcode.someerroroccured_contactadmin" text="Some error occured.. Please contact administrator"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:otherwise>
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="generic.errorcode.someerroroccured_contactadmin" text="Some error occured.. Please contact administrator"/>
		</p>
		<p></p>
	</div>
</c:otherwise>
</c:choose>