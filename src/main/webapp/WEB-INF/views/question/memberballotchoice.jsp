<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
	});
</script>
</head>
<body>
<c:choose>
	<c:when test="${!(empty eligibleMembers)}">
		<p><label style="margin: 10px;"><spring:message
			code="memberballotchoice.member" text="Member" />*</label> <select
			id="member" name="member">
			<c:forEach items="${eligibleMembers}" var="i">
				<option value="${i.id }"><c:out value="${i.getFullname()}"></c:out></option>
			</c:forEach>
		</select></p>
		<c:choose>
			<c:when test="${!(empty totalRounds)}">
				<table class="uiTable">
					<tr>
						<th><spring:message code="memberballotchoice.round"
							text="Round"></spring:message></th>
						<th><spring:message code="memberballotchoice.sno" text="S.No"></spring:message></th>
						<th><spring:message code="memberballotchoice.Question"
							text="Question"></spring:message></th>
						<th><spring:message code="memberballotchoice.answeringdate"
							text="Answering Date"></spring:message></th>
					</tr>
					<%
					    for (int i = 1; i <= (Integer) request
					                            .getAttribute("totalRounds"); i++) {
					        for (int j = 1; j <= (Integer) request
                            .getAttribute("round"+i); j++){	
					        

					%>
					<tr>
						<td><%=i %></td>
						<td><%=j %></td>
						<td><select>
							<option value='-'><spring:message code='please.select'
								text='Please Select' /></option>
						</select></td>
						<td><select>
							<option value='-'><spring:message code='please.select'
								text='Please Select' /></option>
						</select></td>
					</tr>
					<%
					        }}
					%>
				</table>
			</c:when>
			<c:otherwise>
				<spring:message code="memberballotchoice.norounds"
					text="No. Of Rounds Not Set"></spring:message>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<spring:message code="memberballotchoice.noeligblemembers"
			text="No Eligible Members Found"></spring:message>
	</c:otherwise>
</c:choose>
<input type="hidden" name="pleaseSelect" id="pleaseSelect"
	value="<spring:message code='please.select' text='Please Select'/>">
</body>
</html>