<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="notification.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#selectionDiv1").show();
			
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("username="+$("#authusername").val());
			
			$('#view_record').click(function(){
				viewRecord($('#key').val());
			});
		});
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="view_record" class="butSim">
			<spring:message code="notification.view" text="View Notification"/>
			</a><%--  |
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> --%>
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
