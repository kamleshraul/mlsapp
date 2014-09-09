<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="sectionorderseries" text="Section Order Series"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		
		/* initialize languages from module */
		$('#setLanguage').html($('#selectedLanguage').html());
		if($('#domainLanguageType').val()!=undefined && $('#domainLanguageType').val()!="") {
			$('#setLanguage').val($('#domainLanguageType').val());
		} else {
			$('#setLanguage').val($('#selectedLanguage').val());
		}	
		
		/* initialize 'is autonomous' */
		var isAutonomous = $("#isAutonomous").val();
		if(isAutonomous == "true"){
			$("#isAutonomousCheck").attr("checked","checked");
		}else{
			$("#isAutonomousCheck").removeAttr("checked");
		}	
		
		/* set 'is autonomous' on change */
		$('#isAutonomousCheck').click(function() {
			if($('#isAutonomousCheck').is(':checked'))
	   		{
				$("#isAutonomous").val("true");  	    
			}
			else {
				$("#isAutonomous").val("false"); 
			}					
		});
		
		/* change language event */
		$('#setLanguage').change(function() {
			$('#selectedLanguage').val($(this).val());
		});
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form action="sectionorderseries" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
		<p> 
			<label class="small"><spring:message code="sectionorderseries.name" text="Name"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>	
		<p> 
			<label class="small"><spring:message code="sectionorderseries.language" text="Language"/></label>
			<select id="setLanguage" name="setLanguage"></select>
			<c:if test="${not empty domain.language}">
				<input type="hidden" id="domainLanguageType" value="${domain.language.type}">
				<input type="hidden" id="language" name="language" value="${domain.language.id}">
			</c:if>
			<form:errors path="language" cssClass="validationError"/>	
		</p>	
		<p> 
			<label class="small"><spring:message code="sectionorderseries.isAutonomous" text="is Autonomous?"/></label>
			<input type="checkbox" id="isAutonomousCheck" name="isAutonomousCheck" class="sCheck">
			<form:hidden path="isAutonomous"/>												
			<form:errors path="isAutonomous" cssClass="validationError" />	
		</p>		
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>		
</form:form>
</div>	
<input type="hidden" id="currentPage" value="edit">
</body>
</html>