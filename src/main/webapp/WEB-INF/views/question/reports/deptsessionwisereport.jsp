<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$(".session").click(function(){
				//var session = $(this).attr('class').split(' ')[1];
				//console.log("Session: " + session + "\n Device: "+$("#selectedQuestionType").val());
			});
		});
	</script>
	<style type="text/css">
		th{
			text-align: center;
		}
		
		td{
			text-align: center;
		}
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
	<spring:message code="question.report.notCreated" text="Report not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.report.empty" text="No entry found."/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h3 style="color: black; font-family: 'Times New Roman';">
			${report[0][8]}<br><br>
			${report[0][9]}
		</h3>
	</div>
	<br />
	<c:set var="columns" value="3" />
	<c:set var="yearColSpan" value="${report[0][1]}" />
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr>				
				<th rowspan="2">${topHeader[0]}</th>
				<th rowspan="2">${topHeader[1]}</th>
				<c:set var="foundYear" value="no" />
				<c:forEach items="${report}" var="rr">
					<c:if test="${foundYear=='no'}">
						<c:if test="${rr[4]!=0}">
							<th colspan="${yearColSpan}">${formater.formatNumberNoGrouping(rr[4], locale)}</th>
							<c:set var="foundYear" value="yes" />		
						</c:if>						
					</c:if>
				</c:forEach>				
			</tr>
			<tr>
				<c:set var="sessionUniqueName" value="${fn:split(report[0][10],',')}" />
				<c:forEach items="${sessionUniqueName}" var="s">
					<c:set var="curSession" value="${fn:split(s,';')}" />
					<th><a id="session${curSession[1]}" href="javascript:void(0);" class="session ${curSession[1]}" style="color: black; text-decoration: none; text-shadow: 1px 1px grey;">${curSession[0]}</a></th>
				</c:forEach>
			</tr>			
		</thead>
		<tbody>
			<c:set var="deptName" value="-"></c:set>
			<c:set var="nextRow" value="1" />
			<c:set var="reachedTotalSessions" value="0" />
			<c:forEach items="${report}" var="r" varStatus="counter">
				<c:choose>
					<c:when test="${deptName!=r[2]}">
						<c:if test="${deptName!='-'}">
							<c:if test="${reachedTotalSessions<yearColSpan}">
								<c:forEach begin="${reachedTotalSessions}" end="${yearColSpan-1}">
									<td>-</td>
								</c:forEach>
							</c:if>
							</tr>
							<c:set var="reachedTotalSessions" value="0" />
							<c:set var="nextRow" value="${nextRow + 1}" />
						</c:if>
						<tr>
							<td>${nextRow}</td>
							<td>${r[2]}</td>
							<c:choose>
								<c:when test="${(r[5]!=1) and (r[5] > 0)}">
									<%-- <c:if test="${r[5]>1}">
										<c:forEach begin="1" end="${r[5]-1}" var="v">
											<td>-</td>
											<c:set var="reachedTotalSessions" value="${reachedTotalSessions + 1}" />
										</c:forEach>
									</c:if> --%>
									<td>${r[6]}</td>
									<c:set var="reachedTotalSessions" value="${reachedTotalSessions + 1}" />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${r[5]==0}">
											<c:forEach begin="1" end="${yearColSpan}" var="t">
												<td>-</td>
											</c:forEach>
											<c:set var="reachedTotalSessions" value="${yearColSpan}" />
										</c:when>
										<c:otherwise>
											<td>${r[6]}</td>
											<c:set var="reachedTotalSessions" value="${reachedTotalSessions + 1}" />
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
					</c:when>
					<c:otherwise>
							<c:choose>
								<c:when test="${r[5]==0}">
									<c:forEach begin="1" end="${yearColSpan}" var="t">
										<td>-</td>
									</c:forEach>
									<c:set var="reachedTotalSessions" value="${yearColSpan}" />
								</c:when>
								<c:otherwise>
									<td>${r[6]}</td>
									<c:set var="reachedTotalSessions" value="${reachedTotalSessions + 1}" />
								</c:otherwise>
							</c:choose>
					</c:otherwise>					
				</c:choose>
				<c:set var="deptName" value="${r[2]}"></c:set>
			</c:forEach>
			
			<c:if test="${reachedTotalSessions<yearColSpan}">
				<c:forEach begin="${reachedTotalSessions}" end="${yearColSpan-1}">
					<td>-</td>
				</c:forEach>
			</c:if>
			</tr>
		</tbody>
	</table>	
	<%-- <table class="strippedTable" border="1">
		<c:forEach items="${report}" var="r">
			<tr>
				<c:forEach items="${r}" var="rr">
					<td>${rr}</td>
				</c:forEach>		
			</tr>
		</c:forEach>
	</table> --%>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>