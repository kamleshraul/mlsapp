<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="proprietypoint.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){
		
	});
	</script>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<table class="uiTable">
		<tr>
			<th><spring:message code="proprietypoint.number" text="Number"></spring:message></th>
			<th><spring:message code="proprietypoint.subject" text="Subject"></spring:message></th>
		</tr>			
		<c:forEach items="${proprietyPoints}" var="i" varStatus="cnt">
			<tr>
				<td>${i.formatNumber()}</td>
				<td class="subject" id="subject${cnt.count}">${i.subject}</td>
			</tr>
		</c:forEach>
	</table>	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>