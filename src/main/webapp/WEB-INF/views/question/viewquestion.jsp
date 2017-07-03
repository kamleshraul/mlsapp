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
		<label class="small"><spring:message code="question.number" text="Question Number"/></label>
		<input class="sText" readonly="readonly" value="${report[0][13]}">
		<label class="small"><spring:message code="question.group" text="Group Number"/></label>
		<input class="sText" readonly="readonly" value="${report[0][11]}">
	</p>
	
	<p>
		<label class="small"><spring:message code="question.members" text="Questioned By"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][7]}" />
	</p>
	<%-- <p>
		Supported By: <c:foreach items="${referredQuestion.supportingMembers}" var="i">
		</c:foreach>
	</p> --%>
	<p>
		<label class="small"><spring:message code="question.sessiondetails" text="Session Details"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][3]}" />
	</p>
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][9]}" />
		<label class="small"><spring:message code="question.subdepartment" text="Sub Department"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][10]}" />
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.subject" text="Subject"></spring:message></label>
		<textarea rows="2" cols="50" readonly="readonly" class="wysiwyg">${report[0][1]}</textarea>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][2]}</textarea>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.revisedSubject" text="Revised Subject"></spring:message></label>
		<textarea rows="2" cols="50" readonly="readonly" class="wysiwyg">${report[0][15]}</textarea>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][16]}</textarea>
	</p>
	<c:if test="${referredQuestion.type.type=='questions_shortnotice'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][5]}</textarea>
	</p>
	</c:if>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reference" text="Reference Text"/></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][14]}</textarea>
	</p>
	<p>
		<label class="small"><spring:message code="question.answeringDate" text="Answering date"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][4]}" />	
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][6]}</textarea>
	</p>
	<c:if test="${report[0][15]=='question_final_rejection' }">
		<p>
		<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection Reason"></spring:message></label>
		<textarea readonly="readonly" class="wysiwyg">${report[0][8]}</textarea>
		</p>
	</c:if>
	<p>
		<label class="small"><spring:message code="question.status" text="Status"></spring:message></label>
		<input class="sText" type="text" readonly="readonly" value="${report[0][12]}" />
	</p>
</div>	
</body>
</html>