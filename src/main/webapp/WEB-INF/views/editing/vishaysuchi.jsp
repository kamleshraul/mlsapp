<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			
		});
	</script>
	<style type="text/css">
		
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="editing.vishaysuchi.notCreated" text="Vishaysuchi is not ready."/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="editing.vishaysuchi.notCreated" text="Vishaysuchi is not ready."/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h2 style="text-decoration: underline; font-family: 'Times New Roman';"><spring:message code="editing.vishaysuchi" text="Vishaysuchi" /></h2>
	</div>
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<c:forEach items="${report}" var="r">
			<tr>
				<td>
					
				</td>
			</tr>
		</c:forEach>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>