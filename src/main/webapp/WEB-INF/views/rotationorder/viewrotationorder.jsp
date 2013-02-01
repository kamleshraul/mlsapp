<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="group.title" text="Groups"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				$('#key').val('');	
			});
			</script>
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/rotationOrderReport.css?v=46" />
	</head>

	<body>
	<div id="rotationOrderReport" style="overflow: scroll;" >
	<%-- <div id="rotationOrderHeader" style="width: 100%">
		${rotationOrderHeader }
		<br>
		${rotationOrderCover }
		<br>
		<br>
	</div> --%>
	<div id="rotationOrder">
			<table class="uiTable" style="width: 100%">
				<tr>
				<th><spring:message code='group.rotationorder.group' text='Group'></spring:message></th>
				<th><spring:message code='group.rotationorder.ministries' text='Ministries'></spring:message></th>
				<th><spring:message code='group.rotationorder.answeringDate' text='Answering Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.submissionDate' text='Final Submission Date'></spring:message></th>
				</tr>
				
				<c:if test="${!(empty dates) }">
					<c:set var="count" value="1"></c:set>
					<c:forEach items="${dates}" var="i">
						<c:if test="${count==i.rowId or count > i.rowId}">
							<tr><td colspan="12">&nbsp;</td></tr>
						</c:if>
						<tr>
							<td>${i.group}</td>
							<td>
							<c:forEach items= "${i.ministries}" var="j" varStatus="cnt">
							(${i.numberOfMinisteries[cnt.count-1]}) ${j}
								<br>
							</c:forEach> </td>
							<td style="width: 130px">
							<c:forEach items= "${i.answeringDates}" var="k">
								${k}
								<br>
							</c:forEach>
							</td>
							<td style="width: 130px">
							<c:forEach items= "${i.finalSubmissionDates}" var="l">
								${l}
								<br>
							</c:forEach> </td>
						</tr>						
						<c:set var="count" value="${i.rowId}"></c:set>
					</c:forEach>
				</c:if>
			</table>
		</div>
		<%-- <div id="rotationOrderFooter" style="width: 100%">
			${rotationOrderFooter}
		</div>
		<div style="width: 120px; height: 20px; margin-left: 400px; margin-top: 10px;padding: 5px">
			<a id="header" href="#rotationOrderHeader" style="width: 40px; display: inline; float: left;"> 1</a> 
			<a id="rotationOrderLink" href="#rotationOrder" style="width: 40px; display: inline; float: left;"> 2</a> 
			<a id="footer" href="#rotationOrderFooter" style="width: 40px; display: inline; float: left;"> 3</a>
			<p>&nbsp;</p>
		</div> --%>
		</div>
	</body>
</html>