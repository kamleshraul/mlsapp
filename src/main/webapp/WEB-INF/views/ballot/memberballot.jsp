<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="ballot.memberballot"
	text="Member Ballot" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
</script>
</head>
<body>
<h3 style="text-align: center;margin-bottom: 15px;font-weight: bold;font-size:16px;"><spring:message code="memberballot.heading" text="Vidhanparishad Question Ballot"></spring:message></h3>
<h4 style="text-align: center;margin-bottom: 20px;font-size:12px;"><spring:message code="memberballot.subheading" text="Ballot Round - "></spring:message>${round}</h4>
<h5 style="text-align: right;margin-bottom: 5px;font-size:12px;"><spring:message code="memberballot.ballotvenue" text="Maharashtra Vidhanmandal Secretariat"></spring:message></h5>
<h5 style="text-align: right;margin-bottom: 5px;font-size:12px;"><spring:message code="memberballot.ballotplace" text="Vidhan Bhawan - "></spring:message>${place}</h5>
<h5 style="text-align: right;margin-bottom: 5px;font-size:12px;"><spring:message code="memberballot.ballotdate" text="Date - ">${date}</spring:message></h5>
<c:set value="1" var="count"></c:set>
<c:choose>
<c:when test="${!(empty memberBallots) }">
<table class="uiTable">
<tr>
<th style="font-size: 14px;"><spring:message code="ballot.sno" text="S.No"></spring:message></th>
<th style="font-size: 14px;"><spring:message code="ballot.member" text="Member"></spring:message></th>
</tr>

<c:forEach items="${memberBallots}" var="i">
<tr>
<td style="font-size: 14px;">${count}</td>
<td style="font-size: 14px;">${i.getFullname()}</td>
</tr>
<c:set value="${count+1}" var="count"></c:set>
</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="ballot.nomemberballot" text="No Member Ballot Found"></spring:message>
</c:otherwise>
</c:choose>

</body>
</html>