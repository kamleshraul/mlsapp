<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/error.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'PRINT_REQUISITION_NOTSET'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.print_requisition_notset" text="There is no print requisition for this request."/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>