<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/error.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'BILL_LAYINGERROR_HOUSETYPENOTSECOND'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="common.errorcode.bill_layingerror_housetypenotsecond" text="Selected housetype is not second house of selected bill. So it is not eligible for laying letter."/>
			</p>
			<p></p>
		</div>
	</c:when>
</c:choose>