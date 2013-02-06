<%@ include file="/common/taglibs.jsp" %>
<c:choose>
<c:when test="${flag=='SUCCESS'}">
<spring:message code="generic.deletesucces" text="Successfully Deleted"></spring:message>
</c:when>
<c:when test="${flag=='FAILED'}">
<spring:message code="generic.deletefailed" text="Cannot Be Deleted."></spring:message>
</c:when>
</c:choose>