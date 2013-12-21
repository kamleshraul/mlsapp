<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${type eq 'DONE'}">
<div class="toolTip tpGreen clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-on.png">
			<spring:message code="ballot.resolution.choice.filled" text="Choice filled successfully."/>
		</p>
		<p></p>
</div>
</c:when>
</c:choose>