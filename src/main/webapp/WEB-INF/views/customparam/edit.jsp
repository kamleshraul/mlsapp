<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="customparam" text="Custom Parameters"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		if($('#categorySelected').val()==''){
			$("#category").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#category").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form  action="customparam" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
			<p> 
			<label class="small"><spring:message code="customparam.category" text="Category"/></label>
			<select name="category" id="category" Class="sSelect">
	       	 <c:forEach items="${categories}" var="i">
				<c:choose>
					<c:when test="${category==i.value}">
					<option value="${i.value}" selected="selected"><c:out value="${i.name}"></c:out></option>				
					</c:when>
				<c:otherwise>
					<option value="${i.value}"><c:out value="${i.name}"></c:out></option>			
				</c:otherwise>
				</c:choose>			
			</c:forEach>
			</select>	
	        <form:errors path="category" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="customparam.locale" text="Locale"/></label>
			<form:select path="locale" cssClass="sSelect" >
				<form:option value=''><spring:message code='please.select' text='Please Select'/></form:option>
				<c:forEach var="locale_value" items="${availableLocales}">
					<form:option value="${locale_value}">${locale_value}</form:option>
				</c:forEach>				
			</form:select>	
	        <form:errors path="locale" cssClass="validationError"/>	
		</p>
		
		
		<p> 
			<label class="small"><spring:message code="customparam.name" text="Name"/></label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="customparam.value" text="Value"/></label>
			<form:input cssClass="sSelect" path="value"/>
			<form:errors path="value" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="customparam.updateable" text="Updateable?"/></label>
			<form:checkbox cssClass="sSelect" path="updateable"/>
			<form:errors path="updateable" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="customparam.description" text="Description"/></label>
			<form:textarea cssClass="sSelect" path="description" rows="5" cols="50"/>
			<form:errors path="description" cssClass="validationError"/>	
		</p>			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	
</form:form>
	<input type="hidden" name="categorySelected" id="categorySelected" value="${category}"/>
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>