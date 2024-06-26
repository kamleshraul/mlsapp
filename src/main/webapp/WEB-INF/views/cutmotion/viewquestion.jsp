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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>

<div class="fields clearfix vidhanmandalImg">
	<%@ include file="/common/info.jsp" %>
	
	<h2><label class="small"><spring:message code="question.questiondetails" text="Question Details"></spring:message></label></h2>
	<p>
		<label class="small"><spring:message code="question.members" text="Questioned By"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${primaryMemberName}" />
	</p>
	<%-- <p>
		Supported By: <c:foreach items="${referredQuestion.supportingMembers}" var="i">
		</c:foreach>
	</p> --%>
	<p>
		<label class="small"><spring:message code="question.sessiondetails" text="Session Details"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${sessionName}, ${sessionYear}, ${sessionType}" />
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.subject" text="Subject"></spring:message></label>
		<textarea rows="2" cols="50" readonly="readonly" class="wysiwyg">${subject}</textarea>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${qText}</textarea>
	</p>
	<c:if test="${referredQuestion.type.type=='questions_shortnotice'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${qReason}</textarea>
	</p>
	</c:if>
	<p>
		<label class="small"><spring:message code="question.answeringDate" text="Answering date"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${answerDate}" />	
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${qAnswer}</textarea>
	</p>
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>