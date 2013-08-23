<%@ include file="/common/taglibs.jsp"%>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:if test="${type eq 'success'}">
	<div class="toolTip tpGreen clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clubbingupdate_success" text="Clubbing Updated Successfully"/>
		</p>
		<p></p>
	</div>
</c:if>

