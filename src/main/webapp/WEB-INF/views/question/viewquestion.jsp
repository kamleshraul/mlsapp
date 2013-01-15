<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="title" text="States"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
	<%@ include file="/common/info.jsp" %>
	
	<h2><label class="small"><spring:message code="question.questiondetails" text="Question Details"></spring:message></label></h2>
	<p>
		<label class="small"><spring:message code="question.createdby" text="Questioned By"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${primaryMemberName}" />
	</p>
	<%-- <p>
		Supported By: <c:foreach items="${referredQuestion.supportingMembers}" var="i">
		</c:foreach>
	</p> --%>
	<p>
		<label class="small"><spring:message code="question.sessiondetails" text="Session Details"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${referredQuestion.session.house.type.name}, ${referredQuestion.session.year}, ${referredQuestion.session.type.sessionType}" />
	</p>
	<p>
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"></spring:message></label>
		<textarea rows="2" cols="50" readonly="readonly">${referredQuestion.subject}</textarea>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"></spring:message></label>
		<textarea class="wysiwyg">${referredQuestion.questionText}</textarea>
	</p>
	<c:if test="${referredQuestion.type.type=='questions_shortnotice'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"></spring:message></label>
		<textarea class="wysiwyg">${referredQuestion.reason}</textarea>
	</p>
	</c:if>
	<p>
		<label class="small"><spring:message code="question.answerindate" text="Answering date"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${referredQuestion.answeringDate.answeringDate}" />	
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"></spring:message></label>
		<textarea class="wysiwyg">${referredQuestion.answer}</textarea>
	</p>
</div>	
</body>
</html>