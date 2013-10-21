<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="bill.membercontacts"
	text="Members' Contact Details " /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<c:choose>
<c:when test="${!(empty membersContact) }">
<table class="uiTable">
<tr>
<th><spring:message code="bill.members" text="Members"></spring:message></th>
<th><spring:message code="bill.mobile" text="Mobile No."></spring:message></th>
<th><spring:message code="bill.permanentTelephone" text="Permanent Telephone"></spring:message></th>
<th><spring:message code="bill.presentTelephone" text="Residence Telephone"></spring:message></th>
<th><spring:message code="bill.officeTelephone" text="Office Telephone"></spring:message></th>
<th><spring:message code="bill.mumbaiTelephone" text="Mumbai Telephone"></spring:message></th>
<th><spring:message code="bill.nagpurTelephone" text="Nagpur Telephone"></spring:message></th>
<th><spring:message code="bill.email" text="Email"></spring:message></th>
</tr>
<c:forEach items="${membersContact}" var="i">
<tr>
<td>${i.fullName}</td>
<td>${i.mobile}</td>
<td>${i.permanentTelephone}</td>
<td>${i.presentTelephone}</td>
<td>${i.officeTelephone}</td>
<td>${i.mumbaiTelephone}</td>
<td>${i.nagpurTelephone}</td>
<td>${i.email}</td>
</tr>
</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="bill.nocontact" text="No Contact Details Found"></spring:message>
</c:otherwise>
</c:choose>

</body>
</html>