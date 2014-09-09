<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="sectionorder" text="Section Order Series"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#listSpacer').hide();
		$('#internalKey').val('');	
		
		$('#submit').click(function() {
			if($('#name').val()=="") {
				$.prompt("Please Enter Name of the Section Order");
				return false;
			} else if($('#sequenceNumber').val()=="") {
				$.prompt("Please Enter Sequence Number of the Section Order");
				return false;
			} else {
				$.prompt("Do you want to add this section order?", {
					buttons: {Ok:true, Cancel:false}, callback: function(v) {
					if(v) {
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post($('form').attr('action'), $('form').serialize(), function(data) {
							$.unblockUI();
							$('#grid_container').html(data);		       					
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');
						}).fail(function(){
	    					$.unblockUI();
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});
					}					
				}});
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
<div class="fields clearfix vidhanmandalImg">
<form:form action="sectionorder" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
		<p>
			<label class="small"><spring:message code="sectionorder.name" text="Name"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="sectionorder.sequenceNumber" text="Sequence Number"/></label>
			<form:input cssClass="sInteger" path="sequenceNumber"/>
			<form:errors path="sequenceNumber" cssClass="validationError"/>	
		</p>				
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" id="sectionOrderSeries" name="sectionOrderSeries" value="${series}">
</form:form>
</div>	
</body>
</html>