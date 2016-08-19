<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'access_denied'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="general.errorcode.access_denied" text="You are not authorized to view the requested content."/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>
