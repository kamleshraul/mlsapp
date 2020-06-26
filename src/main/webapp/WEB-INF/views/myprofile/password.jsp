<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="myProfile.changePassword" text="Change Password"/></title>
		<script type="text/javascript" src="./resources/js/crypto-js.js"></script>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();	
				  $( function() {
					    $( "#birthDate" ).datepicker({
					    	dateFormat: 'dd/mm/yy',
					    	changeMonth: true, 
					    	changeYear: true,  
					    	yearRange: "-100:", 
					    	maxDate: "-17Y"
					    });
					  } );
				  
				$('#existingPassword').change(function() {
					var isPasswordValid = true;
					if($('#existingPassword').val()=='') {
						$.prompt("Please enter existing password");
						return false;
					}
					//encryptPassword('existingPassword');
					$.get('ref/user/isAuthenticatedWithEnteredPassword?username='+$("#username").val()
								+'&enteredPassword='+encodeURIComponent($("#existingPassword").val()), function(result) {
						if(result==false) {
							isPasswordValid = false;
						} else {
							isPasswordValid = true;
						}						
						/* if(result==false) {
							$.prompt("Incorrect Password!");
							$('#existingPassword').val("");
							$('#newPassword').val("");
						} */
					}).done(function(){
						//decryptPassword('existingPassword');
						if(isPasswordValid==false) {
							$.prompt("Incorrect Password!");
							$('#existingPassword').val("");
							$('#newPassword').val("");
						}
					});
				});
				
				$('#newPassword').change(function() {
					var newPasswordString = $('#newPassword').val();
					if($('#existingPassword').val()=='') {
						$.prompt("Please enter existing password first");
						$('#newPassword').val("");						
					} else {
						if($('#isPasswordValidationRequired').val()=='yes') {
							if (newPasswordString.length < 8) {
								$.prompt("at least 8 characters are required in the password!");
								$('#newPassword').val("");
								return false;
						    } else if (newPasswordString.length > 20) {
						    	$.prompt("that's too long password!");
						    	$('#newPassword').val("");
						    	return false;
						    } else if (newPasswordString.search(/\d/) == -1) {
						    	$.prompt("at least one digit (0-9) is required in the password!");
						    	$('#newPassword').val("");
						    	return false;
						    } else if (newPasswordString.search(/[a-z]/) == -1) {
						    	$.prompt("at least one small letter (a-z) is required in the password!");
						    	$('#newPassword').val("");
						    	return false;
						    } else if (newPasswordString.search(/[A-Z]/) == -1) {
						    	$.prompt("at least one capital letter (A-Z) is required in the password!");
						    	$('#newPassword').val("");
						    	return false;
						    } else if(newPasswordString.search(/[!_@#$%\^&*:~(){}[\]<>?/|\-]/) == -1) {
						    	$.prompt("at least one special character is required in the password!");
						    	$('#newPassword').val("");
						    	return false;
						    }
						}						
					}					
				});
				
				$('#confirmedPassword').change(function() {
					if($('#confirmedPassword').val()!=$('#newPassword').val()) {
						$.prompt("New & Confirmed Passwords do not match");
						//$('#confirmedPassword').val("");						
					}
				});
				
				$('#submit').click(function() {
					if($('#existingPassword').val()=='') {
						$.prompt("Please enter existing password");
						return false;
					}
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
					/* if($('#birthDate').val()=='') {
						$.prompt("Please enter birth Date");
						return false;
					} */	
					//encryptPassword('existingPassword');
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post($('form').attr('action'), $("form").serialize(), function(data){
       					$('.tabbar').html(data);
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');
    					$.unblockUI();	   				 	   				
	    	        });
				});
			});
			
			function encryptPassword(passwordElementId) {
				if($('#'+passwordElementId).val()!=undefined && $('#'+passwordElementId).val()!="") {
					var encryptedPwd = CryptoJS.AES.encrypt($('#'+passwordElementId).val(), '${secret_key}');
					$('#'+passwordElementId).val(encryptedPwd);
				}		
			}
			
			function decryptPassword(passwordElementId) {
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
				<form action="myprofile/password" method="POST">
					<%@ include file="/common/info.jsp" %>
					<h2><spring:message code="user.changePassword" text="Change Password"/></h2>				
					<p>
						<label class="small"><spring:message code="user.existingPassword" text="Existing Password"/></label>
						<input type="password" id="existingPassword" name="existingPassword" value="${existingPassword}"/>
					</p>
					<p>
						<label class="small"><spring:message code="user.newPassword" text="New Password"/></label>
						<input type="password" id="newPassword" name="newPassword" value="${newPassword}"/>
						<label style="margin-left:10px;"><spring:message code="user.password_rules" text="(Note: It should be minimum 8 characters, alphanumeric & with one special character)"/></label>
					</p>
					<p>
						<label class="small"><spring:message code="user.confirmPassword" text="Confirm Password"/></label>
						<input type="password" id="confirmedPassword" name="confirmedPassword" value="${confirmedPassword}"/>
					</p>
					
					<p>
						<label class="small"><spring:message code="user.birthdate"  text="Birth Date"/></label>
						<input type="text" id="birthDate" name="birthDate" value="${birthDate}">
					</p>
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
					</div>
					<input type="hidden" id="username" name="username" value="${username}">			
					<input type="hidden" id="isPasswordValidationRequired" name="isPasswordValidationRequired" value="${isPasswordValidationRequired}">
				</form>	
				</div>
			</div>
			<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">			
		</div>		
	</body>
</html>