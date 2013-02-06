<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
th,td{
text-align: center;
}
</style>
</head>

<body>
<c:set value="0" var="admitted"></c:set>
<c:set value="0" var="unstarred"></c:set>
<c:set value="0" var="rejected"></c:set>
<c:set value="0" var="clarification"></c:set>
<c:set value="0" var="total"></c:set>

<c:set value="0" var="admittedpercent"></c:set>
<c:set value="0" var="unstarredpercent"></c:set>
<c:set value="0" var="rejectedpercent"></c:set>
<c:set value="0" var="clarificationpercent"></c:set>

<c:set value="0" var="totalMembers"></c:set>
<c:set value="0" var="size"></c:set>



<c:choose>
<c:when test="${!(empty questionDistributions) }">
<c:set value="${fn:length(questionDistributions) }" var="size"></c:set>

<table class="strippedTable" border="1">
<tr>
<th><spring:message code="memberdistribution.sno" text="S.No"></spring:message></th>
<th><spring:message code="memberdistribution.member" text="Member"></spring:message></th>
<th><spring:message code="memberdistribution.admitted" text="Admitted"></spring:message></th>
<th><spring:message code="memberdistribution.unstarredadmitted" text="Unstarred Admitted"></spring:message></th>
<th><spring:message code="memberdistribution.rejection" text="Rejection"></spring:message></th>
<th><spring:message code="memberdistribution.clarificationneeded" text="Clarification From Member/Fact Finding"></spring:message></th>
<th><spring:message code="memberdistribution.totalcount" text="Total Count"></spring:message></th>
</tr>
<c:forEach items="${questionDistributions}"  var="i">
<c:set value="${totalMembers+1 }" var="totalMembers"></c:set>
<tr>
<td>${i.sNo}</td>
<td>${i.member}</td>

<c:if test="${!(empty i.distributions ) }">

<c:set value="0" var="count"></c:set>
<c:forEach items="${i.distributions}" var="j">
<c:choose>
<c:when test="${j.statusTypeType=='question_final_admission'}">
<td>${j.count}</td>
<c:set value="${count+1}" var="count"></c:set>
<c:set value="${admitted+j.count}" var="admitted"></c:set>
</c:when>
<c:otherwise>
</c:otherwise>
</c:choose>
</c:forEach>

<c:if test="${count==0 }">
<td>-</td>
</c:if>

<c:set value="0" var="count"></c:set>
<c:forEach items="${i.distributions}" var="j">
<c:choose>
<c:when test="${j.statusTypeType=='question_final_convertToUnstarredAndAdmit'}">
<td>${j.count}</td>
<c:set value="${count+1}" var="count"></c:set>
<c:set value="${unstarred+j.count}" var="unstarred"></c:set>
</c:when>
<c:otherwise>
</c:otherwise>
</c:choose>
</c:forEach>

<c:if test="${count==0 }">
<td>-</td>
</c:if>

<c:set value="0" var="count"></c:set>
<c:forEach items="${i.distributions}" var="j">
<c:choose>
<c:when test="${j.statusTypeType=='question_final_rejection'}">
<td>${j.count}</td>
<c:set value="${count+1}" var="count"></c:set>
<c:set value="${rejected+j.count}" var="rejected"></c:set>
</c:when>
<c:otherwise>
</c:otherwise>
</c:choose>
</c:forEach>

<c:if test="${count==0 }">
<td>-</td>
</c:if>

<c:set value="0" var="count"></c:set>
<c:forEach items="${i.distributions}" var="j">
<c:choose>
<c:when test="${j.statusTypeType=='clarification'}">
<td>${j.count}</td>
<c:set value="${count+1}" var="count"></c:set>
<c:set value="${clarification+j.count}" var="clarification"></c:set>
</c:when>
<c:otherwise>
</c:otherwise>
</c:choose>
</c:forEach>

<c:if test="${count==0 }">
<td>-</td>
</c:if>


</c:if>
<td>${i.totalCount }</td>
<c:set value="${total+i.totalCount}" var="total"></c:set>
</tr>
<c:if test="${size==totalMembers }">
<c:set value="${(admitted/total)*100}" var="admittedpercent"></c:set>
<c:set value="${(unstarred/total)*100}" var="unstarredpercent"></c:set>
<c:set value="${(rejected/total)*100}" var="rejectedpercent"></c:set>
<c:set value="${(clarification/total)*100}" var="clarificationpercent"></c:set>
<c:set value="${i.formatNumber(admitted,locale)}" var="admitted"></c:set>
<c:set value="${i.formatNumber(unstarred,locale)}" var="unstarred"></c:set>
<c:set value="${i.formatNumber(rejected,locale)}" var="rejected"></c:set>
<c:set value="${i.formatNumber(clarification,locale)}" var="clarification"></c:set>
<c:set value="${i.formatNumber(total,locale)}" var="total"></c:set>
<c:set value="${i.formatDecimalNumber(admittedpercent,locale)}" var="admittedpercent"></c:set>
<c:set value="${i.formatDecimalNumber(unstarredpercent,locale)}" var="unstarredpercent"></c:set>
<c:set value="${i.formatDecimalNumber(rejectedpercent,locale)}" var="rejectedpercent"></c:set>
<c:set value="${i.formatDecimalNumber(clarificationpercent,locale)}" var="clarificationpercent"></c:set>
</c:if>
</c:forEach>
<tr>
<td></td>
<td><spring:message code="memberdistribution.total" text="Total"></spring:message></td>
<td>${admitted }</td>
<td>${unstarred }</td>
<td>${rejected }</td>
<td>${clarification }</td>
<td>${total }</td>
</tr>
<tr>
<td></td>
<td><spring:message code="memberdistribution.percentage" text="Percentage"></spring:message></td>
<td>${admittedpercent }%</td>
<td>${unstarredpercent }%</td>
<td>${rejectedpercent }%</td>
<td>${clarificationpercent }%</td>
<td></td>
</tr>
</table>
</c:when>
<c:otherwise>
<h2><spring:message code="memberdistribution.noresultsfound" text="No Results Found"></spring:message></h2>
</c:otherwise>
</c:choose>
</body>
</html>