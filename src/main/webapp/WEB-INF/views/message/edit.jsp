<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="message"	text="Edit Message Resource" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
		
		var prevId=$('#hddPrevDomainId')!==null && $('#hddPrevDomainId')!==undefined ? $('#hddPrevDomainId').val():0; 
		var nextId=$('#hddNextDomainId')!==null && $('#hddNextDomainId')!==undefined ? $('#hddNextDomainId').val():0;
		
		function editNextPrev(idToEdit){
			var form=$("form").first().get(0);			
			showTabByIdAndUrl('process_tab', form.action+"/"+idToEdit+"/edit");
		}
		
		if(nextId>0){
			$('#nextBtn').click(function(e){
				e.preventDefault();
				editNextPrev(nextId);
				$(".tabContent").show();
			});
		}
		
		if(prevId>0){
			$('#prevBtn').click(function(){
				editNextPrev(prevId);
				$(".tabContent").show();
			});
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
		<form:form action="message" method="PUT"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.edit.heading" text="Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:${domain.id}]
			</h2>			
			<c:set var="codeErrors">
				<form:errors path="code" />
			</c:set>
			<p <c:if test="${not empty codeErrors}">class="error"</c:if>>
				<label class="small"><spring:message
						code="message.code" text="Code" />(Code)&nbsp;*</label>
				<form:input cssClass="sText large" path="code" />
				<span><form:errors path="code" /></span>
			</p>
			<c:set var="valueErrors">
				<form:errors path="value" />
			</c:set>
			<p <c:if test="${not empty valueErrors}">class="error"</c:if>>
				<label class="small"><spring:message
						code="message.text" text="Text" />(Value/Text)&nbsp;*</label>
				<form:input cssClass="sText large" path="value" />
				<span><form:errors path="value" /></span>
			</p>
			<p>
				<label class="small">
				<spring:message code="message.description" text="Description"/>(Description)
				</label>					
				<form:textarea cssClass="sTextarea" path="description"
					rows="5" cols="30" />
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
					<c:if test="${nextDomain != '' && nextDomain.id>0 }">
						<input id="nextBtn" type="button" value="<spring:message code='generic.next.btn' text='Next'/>" class="butDef"/>
						<input type="hidden" id="hddNextDomainId" value="${nextDomain.id}" />
					</c:if>
					<c:if test="${prevDomain != '' && prevDomain.id>0 }">
						<input id="prevBtn" type="button" value="<spring:message code='generic.previous.btn' text='Previous'/>" 
								class="butDef"
								style="float:left"	/>
						<input type="hidden" id="hddPrevDomainId" value="${prevDomain.id}" />
					</c:if>
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>