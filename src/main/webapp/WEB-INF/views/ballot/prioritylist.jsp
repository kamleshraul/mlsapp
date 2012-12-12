<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="ballot/prioritylist" method="POST">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="prioritylist.heading" text="Maharashtra Vidhanparishad"/></h2>
	<c:set value="1" var="count"></c:set>
	<table class="uiTable">
		<tr>
			<th><spring:message code="prioritylist.sno" text="S.No"></spring:message></th>
			<th>${deviceTypeName}<spring:message code="prioritylist.questionno" text=" Number"></spring:message></th>			
			<th><spring:message code="prioritylist.answeringdate" text="Answering Date"></spring:message></th>
		</tr>	
		
		<c:if test="${count<=noOfAdmittedQuestions }">
		<tr>
		<td colspan="3" style="font-size: 14px;"><spring:message code="prioritylist" text="Round"></spring:message> 1</td>
		</tr>
		</c:if>
		
		<c:forEach var="i" begin="1" end="1" step="1">
		<c:if test="${count<=noOfAdmittedQuestions }">
		<tr>
			<td>${i}</td>
			<td>
			<select id="r1question${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questions}" var="i">
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>
			</c:forEach>
			</select>			
			</td>			
			<td>
			<select id="r1answeringDate${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select>
			</td>
		</tr>
		</c:if>
		<c:set value="${count+1}" var="count"></c:set>		
		</c:forEach>
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<tr>
		<td colspan="3" style="font-size: 14px;"><spring:message code="prioritylist" text="Round"></spring:message> 2</td>
		</tr>
		</c:if>
		
		<c:forEach var="i" begin="1" end="2" step="1">
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<tr>
			<td>${i}</td>
			<td>
			<select id="r2question${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questions}" var="i">
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>
			</c:forEach>
			</select>
			</td>			
			<td>
			<select id="r2answeringDate${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select>
			</td>
		</tr>
		</c:if>
		<c:set value="${count+1}" var="count"></c:set>		
		</c:forEach>
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<tr>
		<td colspan="3" style="font-size: 14px;"><spring:message code="prioritylist" text="Round"></spring:message> 3</td>
		</tr>
		</c:if>
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<c:forEach var="i" begin="1" end="3" step="1">
		<tr>
			<td>${i}</td>
			<td>
			<select id="r3question${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questions}" var="i">
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>
			</c:forEach>
			</select>
			</td>			
			<td>
			<select id="r3answeringDate${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select>
			</td>
		</tr>
		</c:forEach>
		</c:if>
		
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<tr>
		<td colspan="3" style="font-size: 14px;"><spring:message code="prioritylist" text="Round"></spring:message> 4</td>
		</tr>
		</c:if>
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<c:forEach var="i" begin="1" end="5" step="1">
		<tr>
			<td>${i}</td>
			<td>
			<select id="r4question${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questions}" var="i">
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>
			</c:forEach>
			</select>
			</td>			
			<td>
			<select id="r4answeringDate${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select>
			</td>
		</tr>
		</c:forEach>
		</c:if>
		
		<c:if test="${count<=noOfAdmittedQuestions }">	
		<tr>
		<td colspan="3" style="font-size: 14px;"><spring:message code="prioritylist" text="Round"></spring:message> 5</td>
		</tr>
		</c:if>
		
		<c:if test="${count<=noOfAdmittedQuestions }">		
		<c:forEach var="i" begin="1" end="20" step="1">
		<tr>
			<td>${i}</td>
			<td>
			<select id="r5question${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questions}" var="i">
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>
			</c:forEach>
			</select>
			</td>			
			<td>
			<select id="r5answeringDate${i}">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select>
			</td>
		</tr>
		</c:forEach>
		</c:if>
		
	</table>
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">			
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	
	<input type="hidden" id="session" name="session" value="${session}">
	<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}">
	<input type="hidden" id="member" name="member" value="${member}">
	<input type="hidden" id="noOfAdmittedQuestions" name="noOfAdmittedQuestions" value="${noOfAdmittedQuestions}">
</form:form>

</div>
</body>
</html>