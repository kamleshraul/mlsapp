<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="bill.citation"	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	$("#selectDate").click(function(){		
		$('#statusDate').val($('#availableStatusDate').val());
	    $.fancybox.close();	    	
	});	
});
</script>
</head>
<body>
<c:choose>
	<c:when test="${!(empty statusDates) }">
		<p> 
			<label class="small"><spring:message code="bill.availableStatusDate" text="Available Status Date"></spring:message></label>
			<select id="availableStatusDate" name="availableStatusDate" multiple="multiple">
				<c:forEach items="${statusDates}" var="i">
					<option value="${i}">${i}</option>
				</c:forEach>
			</select>
		</p>	
		<p class="tright">
			<input id="selectDate" type="button" value="<spring:message code='bill.selectAvailableStatusDate' text='Select Date'/>" class="butDef">
		</p>	
	</c:when>
	<c:otherwise>
		<spring:message code="bill.noStatusDatesAvailable" text="No Status Dates Available"></spring:message>
	</c:otherwise>
</c:choose>
</body>
</html>