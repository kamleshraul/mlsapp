<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="adjournmentmotion.membercontacts"
	text="Members' Contact Details " /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${!(empty membersContact) }">
<table class="uiTable">
<tr>
<th><spring:message code="adjournmentmotion.members" text="Members"></spring:message></th>
<th><spring:message code="adjournmentmotion.mobile" text="Mobile No."></spring:message></th>
<th><spring:message code="adjournmentmotion.permanentTelephone" text="Permanent Telephone"></spring:message></th>
<th><spring:message code="adjournmentmotion.presentTelephone" text="Residence Telephone"></spring:message></th>
<th><spring:message code="adjournmentmotion.officeTelephone" text="Office Telephone"></spring:message></th>
<th><spring:message code="adjournmentmotion.mumbaiTelephone" text="Mumbai Telephone"></spring:message></th>
<th><spring:message code="adjournmentmotion.nagpurTelephone" text="Nagpur Telephone"></spring:message></th>
<th><spring:message code="adjournmentmotion.email" text="Email"></spring:message></th>
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
<spring:message code="adjournmentmotion.nocontact" text="No Contact Details Found"></spring:message>
</c:otherwise>
</c:choose>

</body>
</html>