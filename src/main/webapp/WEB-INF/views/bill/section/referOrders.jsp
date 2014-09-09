<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="bill.section.referredOrders" text="Referred Orders"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		initControls();
	});		
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<table class="strippedTable" style="width: 100%;">
	<tr>
		<th>Section Number</th>
		<th>Section Order</th>
	</tr>
	<c:forEach var="i" items="${sectionOrders}">
		<tr>
			<td>${i.name}</td>
			<td>${i.value}</td>
		</tr>
	</c:forEach>
</table>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>