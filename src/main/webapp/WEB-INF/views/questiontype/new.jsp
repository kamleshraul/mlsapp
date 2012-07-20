<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="questiontype.title" text="Question Types"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		
		var hasLimit = '${domain.hasQuestionLimit}';		
		if(hasLimit == 'true') {
			$('.onQuestionLimit').show();
		} else {
			$('.onQuestionLimit').hide();
		}
		
		$('#hasQuestionLimit').click(function() {
			if($('#hasQuestionLimit').is(':checked'))
	   		{
				$('.onQuestionLimit').show();  	    
			}
			else {
				$('.onQuestionLimit').hide();
			}
		});
		
		$('#submit').click(function() {
			if($('#hasQuestionLimit').is(':checked'))
		   	{
				$('#hasQuestionLimit').val(true);				
			}
			else
		   	{ 				
				$('#hasQuestionLimit').val(false);
				$('#questionLimit').val(null);
				$('#questionLimitingAction').prop('selectedIndex', -1);
				$('#warningMessage').val(null);
		   	};
		});		
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="questiontype" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
	<p> 
		<label class="small"><spring:message code="questiontype.name" text="Name"/></label>
		<form:input cssClass="sText" path="name"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="questiontype.hasQuestionLimit" text="Question Limit?" /></label>
		<form:checkbox cssClass="sCheck" id="hasQuestionLimit" path="hasQuestionLimit"/>
		<form:errors path="hasQuestionLimit" cssClass="validationError" />
	</p>	
	<p class="onQuestionLimit"> 
		<label class="small"><spring:message code="questiontype.questionLimit" text="Question Limit"/></label>
		<form:input cssClass="sText" path="questionLimit" id="questionLimit"/>
		<form:errors path="questionLimit" cssClass="validationError"/>	
	</p>
	<p class="onQuestionLimit">
		<label class="small"><spring:message code="questiontype.questionLimitingAction" text="Question Limit Reached Action" /></label>			
		<form:select path="questionLimitingAction" id="questionLimitingAction" cssClass="sSelect">
			<c:forEach items="${questionLimitingActions}" var="i">
				<c:choose>
					<c:when test="${domain.questionLimitingAction.id == i.id}">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="questionLimitingAction" cssClass="validationError" />
	</p>	
	<p class="onQuestionLimit">
		<label class="labelcentered"><spring:message code="questiontype.warningMessage" text="Warning Message" /></label>
		<form:textarea cssClass="wysiwyg sTextarea" path="warningMessage" rows="3" cols="20" />
		<form:errors path="warningMessage" id="warningMessage" cssClass="validationError" />
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="type"/>
		
</form:form>
</div>	
</body>
</html>