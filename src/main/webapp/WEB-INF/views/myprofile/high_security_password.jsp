<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="myProfile.changeHighSecurityPassword" text="Change High Security Password"/></title>
		<script type="text/javascript" src="./resources/js/crypto-js.js"></script>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				
				$('#existingHighSecurityPassword').change(function() {
					var isHighSecurityPasswordValid = true;
					if($('#existingHighSecurityPassword').val()=='') {
						$.prompt("Please enter existing password");
						return false;
					}
					//encryptHighSecurityPassword('existingHighSecurityPassword');
					$.get('ref/user/isAuthenticatedWithEnteredHighSecurityPassword?username='+$("#username").val()
								+'&enteredHighSecurityPassword='+encodeURIComponent($("#existingHighSecurityPassword").val()), function(result) {
						if(result==false) {
							isHighSecurityPasswordValid = false;
						} else {
							isHighSecurityPasswordValid = true;
						}						
						/* if(result==false) {
							$.prompt("Incorrect High Security Password!");
							$('#existingHighSecurityPassword').val("");
							$('#newHighSecurityPassword').val("");
						} */
					}).done(function(){
						//decryptHighSecurityPassword('existingHighSecurityPassword');
						if(isHighSecurityPasswordValid==false) {
							$.prompt("Incorrect High Security Password!");
							$('#existingHighSecurityPassword').val("");
							$('#newHighSecurityPassword').val("");
						}
					});
				});
				
				$('#newHighSecurityPassword').change(function() {
					var newHighSecurityPasswordString = $('#newHighSecurityPassword').val();
					if($('#existingHighSecurityPassword').val()=='') {
						$.prompt("Please enter existing high security password first!");
						$('#newHighSecurityPassword').val("");	
						return false;						
					} else if(newHighSecurityPasswordString == $('#existingHighSecurityPassword').val()) {
						$.prompt("New High Security Password cannot be same as Existing High Security Password!");
						$('#newHighSecurityPassword').val("");
						return false;
					} else if(newHighSecurityPasswordString == $('#existingPassword').val()) {
						$.prompt("High Security Password cannot be same as Login Password!");
						$('#newHighSecurityPassword').val("");
						return false;
					} else {
						if($('#isHighSecurityPasswordValidationRequired').val()=='yes') {
							if (newHighSecurityPasswordString.length < 8) {
								$.prompt("at least 8 characters are required in the password!");
								$('#newHighSecurityPassword').val("");
								return false;
						    } else if (newHighSecurityPasswordString.length > 20) {
						    	$.prompt("that's too long password!");
						    	$('#newHighSecurityPassword').val("");
						    	return false;
						    } else if (newHighSecurityPasswordString.search(/\d/) == -1) {
						    	$.prompt("at least one digit (0-9) is required in the password!");
						    	$('#newHighSecurityPassword').val("");
						    	return false;
						    } else if (newHighSecurityPasswordString.search(/[a-z]/) == -1) {
						    	$.prompt("at least one small letter (a-z) is required in the password!");
						    	$('#newHighSecurityPassword').val("");
						    	return false;
						    } else if (newHighSecurityPasswordString.search(/[A-Z]/) == -1) {
						    	$.prompt("at least one capital letter (A-Z) is required in the password!");
						    	$('#newHighSecurityPassword').val("");
						    	return false;
						    } else if(newHighSecurityPasswordString.search(/[!_@#$%\^&*:~(){}[\]<>?/|\-]/) == -1) {
						    	$.prompt("at least one special character is required in the password!");
						    	$('#newHighSecurityPassword').val("");
						    	return false;
						    }
						}						
					}					
				});
				
				$('#confirmedHighSecurityPassword').change(function() {
					if($('#confirmedHighSecurityPassword').val()!=$('#newHighSecurityPassword').val()) {
						$.prompt("New & Confirmed High Security Passwords do not match");
						//$('#confirmedHighSecurityPassword').val("");						
					}
				});
				
				$('#submit').click(function() {
					if($('#existingHighSecurityPassword').val()=='') {
						$.prompt("Please enter existing password");
						return false;
					}
					if($('#newHighSecurityPassword').val()=='') {
						$.prompt("Please enter new password");
						return false;
					}		
					if($('#confirmedHighSecurityPassword').val()=='') {
						$.prompt("Please confirm new password");
						return false;
					}
					if($('#confirmedHighSecurityPassword').val()!=$('#newHighSecurityPassword').val()) {
						$.prompt("New & Confirmed High Security Passwords do not match");
						return false;
					}		
					//encryptHighSecurityPassword('existingHighSecurityPassword');
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post($('form').attr('action'), $("form").serialize(), function(data){
       					$('.tabbar').html(data);
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');
    					$.unblockUI();	   				 	   				
	    	        });
				});
			});
			
			function encryptHighSecurityPassword(passwordElementId) {
				if($('#'+passwordElementId).val()!=undefined && $('#'+passwordElementId).val()!="") {
					var encryptedPwd = CryptoJS.AES.encrypt($('#'+passwordElementId).val(), '${secret_key}');
					$('#'+passwordElementId).val(encryptedPwd);
				}		
			}
			
			function decryptHighSecurityPassword(passwordElementId) {
				if($('#'+passwordElementId).val()!=undefined && $('#'+passwordElementId).val()!="") {
					var decryptedPwd = CryptoJS.AES.decrypt($('#'+passwordElementId).val(), '${secret_key}');
					$('#'+passwordElementId).val(decryptedPwd.toString(CryptoJS.enc.Utf8));
				}		
			}
		</script>
	</head>
	<body>		
		<div class="clearfix tabbar">
			<ul class="tabs">
				<li>
					<a id="details_tab" href="#" class="tab selected">
					   <spring:message code="generic.details" text="Details"></spring:message>
					</a>
				</li>			
			</ul>
			<div class="tabContent clearfix">
				<p id="error_p" style="display: none;">&nbsp;</p>
				<c:if test="${(error!='') && (error!=null)}">
					<h4 style="color: #FF0000;">${error}</h4>
				</c:if>
				<div class="fields">
				<form action="myprofile/high_security_password" method="POST">
					<%@ include file="/common/info.jsp" %>
					<h2><spring:message code="user.changeHighSecurityPassword" text="Change High Security Password"/></h2>				
					<p>
						<label class="small" style="width: 200px;"><spring:message code="user.existingHighSecurityPassword" text="Existing High Security Password"/></label>
						<input type="password" id="existingHighSecurityPassword" name="existingHighSecurityPassword" value="${existingHighSecurityPassword}"/>
					</p>
					<p>
						<label class="small" style="width: 200px;"><spring:message code="user.newHighSecurityPassword" text="New High Security Password"/></label>
						<input type="password" id="newHighSecurityPassword" name="newHighSecurityPassword" value="${newHighSecurityPassword}"/>
						<label style="margin-left:10px;"><spring:message code="user.password_rules" text="(Note: It should be minimum 8 characters, alphanumeric & with one special character)"/></label>
					</p>
					<p>
						<label class="small" style="width: 200px;"><spring:message code="user.confirmHighSecurityPassword" text="Confirm High Security Password"/></label>
						<input type="password" id="confirmedHighSecurityPassword" name="confirmedHighSecurityPassword" value="${confirmedHighSecurityPassword}"/>
					</p>
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
					</div>
					<input type="hidden" id="username" name="username" value="${username}">			
					<input type="hidden" id="existingPassword" name="existingPassword" value="${existingPassword}">
					<input type="hidden" id="isHighSecurityPasswordValidationRequired" name="isHighSecurityPasswordValidationRequired" value="${isHighSecurityPasswordValidationRequired}">
				</form>	
				</div>
			</div>
			<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">			
		</div>		
	</body>
</html>