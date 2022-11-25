<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="user.resetPassword" text="Reset Password"/></title>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();	
				
				$('#currentPage').val("resetPassword");
				
				$('#confirmedPassword').change(function() {
					if($('#confirmedPassword').val()!=$('#newPassword').val()) {
						$.prompt("New & Confirmed Passwords do not match");
						//$('#confirmedPassword').val("");						
					}
				});
				
				$('#submit').click(function() {
					if($('#newPassword').val()=='') {
						$.prompt("Please enter new password");
						return false;
					}		
					if($('#confirmedPassword').val()=='') {
						$.prompt("Please confirm new password");
						return false;
					}
					if($('#confirmedPassword').val()!=$('#newPassword').val()) {
						$.prompt("New & Confirmed Passwords do not match");
						return false;
					}
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post($('form').attr('action'), $("form").serialize(), function(data){
       					$('.tabContent').html(data);
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');
    					$.unblockUI();	   				 	   				
	    	        });
				});
			});		
		</script>
	</head>
	<body>				
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields">		
		<form action="user/resetPassword" method="POST">
			<%@ include file="/common/info.jsp" %>
			<h2><spring:message code="user.resetPassword" text="Reset Password"/> (${userFirstLastName} - ${username})</h2>
			<%-- <h2 style="color:black;"> ${userFirstLastName}</h2> --%>
			<p>
				<label class="small"><spring:message code="user.newPassword" text="New Password"/></label>
				<input type="password" id="newPassword" name="newPassword" value="${newPassword}"/>
				<%-- <a href="#" id="newPassword_toggleCharacters" class="toggleCharacters" style="margin-left: 10px;">
					<spring:message code="generic.showCharacters" text="Show Characters"/>
				</a> --%>
			</p>
			<p>
				<label class="small"><spring:message code="user.confirmPassword" text="Confirm Password"/></label>
				<input type="password" id="confirmedPassword" name="confirmedPassword" value="${confirmedPassword}"/>
				<%-- <a href="#" id="confirmedPassword_toggleCharacters" class="toggleCharacters" style="margin-left: 10px;">
					<spring:message code="generic.showCharacters" text="Show Characters"/>
				</a> --%>
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				</p>
			</div>
			<input type="hidden" id="username" name="username" value="${username}">			
		</form>	
		</div>		
	</body>
</html>