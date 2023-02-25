<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title><spring:message code="generic.high_security_validation" text="High Security Validation" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script type="text/javascript">
			$(document).ready(function(){
				document.onkeydown = function(e){
					if(e.keyCode===13){ //user enters i.e. presses enter key
						$("#submitHighSecurityPassword").trigger('click');
						return false;
					};
				}
				
				$("#submitHighSecurityPassword").click(function(){
					if($('#highSecurityPassword').val()==undefined || $('#highSecurityPassword').val()=="") {
						setTimeout(function(){
							$.fancybox.close();
							$.prompt("You did not enter the password!! Please try again...");
						},200);
					} else {
						var parameters = "securedItemId="+$('#secured_item_id').val()+"&highSecurityPassword="+encodeURIComponent($('#highSecurityPassword').val());
						$.ajax({
							url: 'high_security_validation_check',
							data: parameters, 
							type: 'POST',
					        async: false,
							success: function(isValidated) {
								if(isValidated==true) {
									$.fancybox.close();
									$("#"+$('#secured_item_id').val()).trigger($('#event_name').val(), [ false ] );																
									
								} else {
									setTimeout(function(){
										$.fancybox.close();
										$.prompt("Incorrect password!! Please try again...");
									},200);
									//$("#error_msg").html("Incorrect password!! Please try again...");								
								}
							}
						}).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}					
						});
					}																		
				});
			});
		</script>
	</head>
	<body>	
		<div>
			<h3><label style="text-align: center;"><spring:message code="generic.high_security_validation.password" text="High security password"/>:&nbsp;&nbsp;</label></h3>
			<input type="password" class="sText" id="highSecurityPassword" name="highSecurityPassword" size="20" style="margin-top: 4px;"/>
			<br><br><br><br>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submitHighSecurityPassword" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<%-- <input id="cancelSubmitHighSecurityPassword" type="button" value="<spring:message code='generic.cancel' text='Canel'/>" class="butDef"> --%>
				</p>
			</div>
		</div>
		<input type="hidden" id="secured_item_id" value="${securedItemId}"/>
		<input type="hidden" id="event_name" value="${eventName}"/>
	</body>
</html>