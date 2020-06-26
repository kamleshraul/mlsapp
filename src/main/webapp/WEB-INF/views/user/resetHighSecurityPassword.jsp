<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="user.resetHighSecurityPassword" text="Reset High Security Password"/></title>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();	
				
				$('#currentPage').val("resetHighSecurityPassword");
				
				$('#confirmedHighSecurityPassword').change(function() {
					if($('#confirmedHighSecurityPassword').val()!=$('#newHighSecurityPassword').val()) {
						$.prompt("New & Confirmed High Security Passwords do not match");
						//$('#confirmedHighSecurityPassword').val("");						
					}
				});
				
				$('#submit').click(function() {
					if($('#newHighSecurityPassword').val()=='') {
						$.prompt("Please enter new High Security password");
						return false;
					}		
					if($('#confirmedHighSecurityPassword').val()=='') {
						$.prompt("Please confirm new High Security password");
						return false;
					}
					if($('#confirmedHighSecurityPassword').val()!=$('#newHighSecurityPassword').val()) {
						$.prompt("New & Confirmed High Security Passwords do not match");
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
		<form action="user/resetHighSecurityPassword" method="POST">
			<%@ include file="/common/info.jsp" %>
			<h2><spring:message code="user.resetHighSecurityPassword" text="Reset High Security Password"/></h2>
			<p>
				<label class="small" style="width: 200px"><spring:message code="user.newHighSecurityPassword" text="New High Security Password"/></label>
				<input type="password" id="newHighSecurityPassword" name="newHighSecurityPassword" value="${newHighSecurityPassword}"/>
				<%-- <a href="#" id="newHighSecurityPassword_toggleCharacters" class="toggleCharacters" style="margin-left: 10px;">
					<spring:message code="generic.showCharacters" text="Show Characters"/>
				</a> --%>
			</p>
			<p>
				<label class="small" style="width: 200px"><spring:message code="user.confirmHighSecurityPassword" text="Confirm High Security Password"/></label>
				<input type="password" id="confirmedHighSecurityPassword" name="confirmedHighSecurityPassword" value="${confirmedHighSecurityPassword}"/>
				<%-- <a href="#" id="confirmedHighSecurityPassword_toggleCharacters" class="toggleCharacters" style="margin-left: 10px;">
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