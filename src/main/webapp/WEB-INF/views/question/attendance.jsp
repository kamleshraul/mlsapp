<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$(".chk").click(function(){
				var id=$(this).attr("id");
				var state=$(this).is(':checked');
				
			});
		});
	</script>
</head>

<body>
<c:choose>
<c:when test="${!(empty attendance) }">
<table class="uiTable">
<tr>
<th><spring:message code="attendance.eligiblemembers" text="Eligible Members"></spring:message></th>
<th><spring:message code="attendance.mark" text="Present/Absent"></spring:message></th>
<th><spring:message code="attendance.time" text="Time"></spring:message></th>
</tr>
<c:forEach items="${ attendance}" var="i">
<tr>
<td><input id="${i.id }" name="${i.id }" type="checkbox" class="chk"></td>
<td>${i.member.getFullname()}</td>
<td><span id="time${i.id}"></span></td>
</tr>
</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="attendance.noeligible" text="No Eligible Members Found"></spring:message>
</c:otherwise>
</c:choose>
</body>
</html>