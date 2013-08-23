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
		var hasLimit = $('#hasQuestionLimit').val();
		var locale=$("#locale").val();
		if(hasLimit == 'true') {
			$('#param_STARRED_QUESTION_HAS_LIMIT_'+locale).attr("checked","checked");
			$(".p").show();
			$("#param_STARRED_QUESTION_LIMIT_"+locale).removeAttr("disabled");
			$("#param_STARRED_QUESTION_LIMIT_ACTION_"+locale).removeAttr("disabled");
			$("#param_STARRED_QUESTION_WARNING_MESSAGE_"+locale).removeAttr("disabled");	 
		} else {
			$('#param_STARRED_QUESTION_HAS_LIMIT_'+locale).removeAttr("checked");			
			$("#param_STARRED_QUESTION_LIMIT_"+locale).attr("disabled","disabled");
			$("#param_STARRED_QUESTION_LIMIT_ACTION_"+locale).attr("disabled","disabled");
			$("#param_STARRED_QUESTION_WARNING_MESSAGE_"+locale).attr("disabled","disabled");	
			$(".p").hide();		
		}			
		$('#param_STARRED_QUESTION_HAS_LIMIT_'+locale).click(function() {
			if($('#param_STARRED_QUESTION_HAS_LIMIT_'+locale).is(':checked')){
				$(".p").show();  
				$("#param_STARRED_QUESTION_LIMIT_"+locale).removeAttr("disabled");
				$("#param_STARRED_QUESTION_LIMIT_ACTION_"+locale).removeAttr("disabled");
				$("#param_STARRED_QUESTION_WARNING_MESSAGE_"+locale).removeAttr("disabled");	 
			}else {
				$("#param_STARRED_QUESTION_LIMIT_"+locale).attr("disabled","disabled");
				$("#param_STARRED_QUESTION_LIMIT_ACTION_"+locale).attr("disabled","disabled");
				$("#param_STARRED_QUESTION_WARNING_MESSAGE_"+locale).attr("disabled","disabled");	
				$(".p").hide();											
			}
		});			
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix">
<form:form action="devicetype" method="POST"  modelAttribute="domain">
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
		<label class="small"><spring:message code="questiontype.type" text="Type"/></label>
		<form:input cssClass="sText" path="type"/>
		<form:errors path="type" cssClass="validationError"/>	
	</p>
	
	<fieldset>
		<legend> <spring:message code="starredquestion.parameters" text="Only For Starred Questions"/> </legend>
	<p>
		<label class="small"><spring:message code="starredquestion.hasQuestionLimit" text="Starred Question Limit?" /></label>
		<input type="checkbox" class="sCheck" id="param_STARRED_QUESTION_HAS_LIMIT_${locale}" name="param_STARRED_QUESTION_HAS_LIMIT_${locale}" value="true">
	</p>	
	<p class="p"> 
		<label class="small"><spring:message code="starredquestion.questionLimit" text="Starred Question Limit"/></label>
		<input type="text" class="sText" id="param_STARRED_QUESTION_LIMIT_${locale}" name="param_STARRED_QUESTION_LIMIT_${locale}" value="${questionlimit}">
	</p>
	<p class="p">
		<label class="small"><spring:message code="starredquestiontype.questionLimitingAction" text="Starred Question Limit Reached Action" /></label>			
		<select class="sSelect" id="param_STARRED_QUESTION_LIMIT_ACTION_${locale}" name="param_STARRED_QUESTION_LIMIT_ACTION_${locale}">
			<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${questionLimitingActions}" var="i">	
			<c:choose>
			<c:when test="${limitaction ==i.name}">
			<option value="${i.name}" selected="selected">${i.name}</option>
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>			
			</c:otherwise>
			</c:choose>			
			</c:forEach>
		</select>
	</p>	
	<p class="p">
		<label class="small"><spring:message code="questiontype.warningMessage" text="Warning Message" /></label>
		<textarea class="wysiwyg sTextarea" id="param_STARRED_QUESTION_WARNING_MESSAGE_${locale}" name="param_STARRED_QUESTION_WARNING_MESSAGE_${locale}" rows="3" cols="20">${warningmessage}</textarea>
	</p>	
	</fieldset>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input id="hasQuestionLimit" name="hasQuestionLimit" type="hidden" value="${hasQuestionLimit}">
		
</form:form>
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>