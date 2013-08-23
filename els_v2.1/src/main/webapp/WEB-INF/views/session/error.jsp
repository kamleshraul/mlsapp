<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'nosessionfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="session.errorcode.nosessionfound" text="No session found."/>
			</p>
			<p></p>
		</div>
	</c:when>
		
	<c:when test="${errorcode eq 'nodevicetypesenabled'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="session.errorcode.nodevicetypesenabled" text="No device type enabled in selected session."/>
			</p>
			<p></p>
		</div>
	</c:when>		
</c:choose>