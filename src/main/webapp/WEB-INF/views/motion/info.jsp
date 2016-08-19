<%@ include file="/common/taglibs.jsp"%>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${info eq 'general_info'}">
	<div class="toolTip tpGreen clearfix">
		<p>
			<img src="./resources/images/template/icons/light-on-off.png">
			<spring:message code="motion.infocode.general_info" text="Action completed successfully."/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>