<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="icon" type="image/png" href="./resources/images/mlsicon.png" />
	<title></title>
	
	<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="./resources/js/crypto-js.js"></script>
	<script type="text/javascript" src="./resources/js/jquery/jquery-impromptu.3.2.min.js"></script>
	<!-- <script type="text/javascript" src="./resources/js/crosstab_handling_loginpage.js"></script> -->
	<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css"  />
	
	<script type="text/javascript">
		//console.log("secret key for login page: " + '${secret_key}');
		/* if('${secret_key}'==undefined || '${secret_key}'==null || '${secret_key}'=='') {
			//console.log("secret_key is undefined at: "+location);
			//This case happens when user has already logged in from other tab/window of same browser.. hence redirecting to home page below!
			location.replace("home.htm");
		} */
		$(document).ready(function(){			
		    $("#j_username").focus();
		    $("#j_password").attr('autocomplete','off');
		    if($('#selectedLocale').val()!=""){
			    $('#lang').val($('#selectedLocale').val()); 
			    $('#localeSelectionP').hide(); //hide on production if it has to be one time visible for setting locale.. show for demo of multi locale project
		    } else {
		    	$('#lang').val($('#defaultLocale').val());
		    	$('#localeSelectionP').show();
		    }
		    if($('#selectedLocale').val()!="" && $('#selectedLocale').val()!=$('#defaultLocale').val()) {
		    	location.search = "?lang="+$('#lang').val();
		    }
		    $("#lang").change(function(){
			   location.search = "?lang="+$('#lang').val();
		    });
		    $('#j_username').change(function() {
		    	$('#passwordSection').show();
		    	if($('#login_disabled_notification_flag').val()=='ON') {
		    		$('#login_disabled_notification_message_para').hide();
		    		var isUserToBeNotified = "no";
		    		$('#login_disabled_usernames option').each(function() {
		    			if($(this).val()==$('#j_username').val()) {
		    				isUserToBeNotified = "yes";		    				
		    			}
		    		});
		    		if(isUserToBeNotified=="yes") {
		    			$('#passwordSection').hide();
		    			$('#saveForm').hide();
		    			$.prompt($('#login_disabled_notification_message').val());
		    			$('#login_disabled_notification_message_para').show();
	    			} else {
		    			$('#saveForm').show();
		    			$('#login_disabled_notification_message').hide();
		    		}
		    	}
		    });
		    $('#saveForm').click(function() {
		    	//encryptPassword();
		    });
		    
		    $("#forget_password_link").click(function(){ //later functionality for automatic password reset using OTP
		    	$('#contactDiv').toggle();
		    });
		    
		    $(document).keypress(function(e) {
		        if(e.which == 13) {
		        	if($('#login_disabled_notification_flag').val()=='ON') {
			    		$('#login_disabled_notification_message_para').hide();
			    		var isUserToBeNotified = "no";
			    		$('#login_disabled_usernames option').each(function() {
			    			if($(this).val()==$('#j_username').val()) {
			    				isUserToBeNotified = "yes";		    				
			    			}
			    		});
			    		if(isUserToBeNotified=="yes") {
			    			$('#passwordSection').hide();
			    			$('#saveForm').hide();
			    			$.prompt($('#login_disabled_notification_message').val());
			    			$('#login_disabled_notification_message_para').show();
			    			return false;
		    			} else {
			    			$('#saveForm').show();
			    			$('#login_disabled_notification_message').hide();
			    		}
			    	}
		        }
		    });
		});	
		
		function encryptPassword() {
			if($('#j_password').val()!=undefined && $('#j_password').val()!="") {
				var encryptedPwd = CryptoJS.AES.encrypt($('#j_password').val(), '${secret_key}');		
				$('#j_password').val(encryptedPwd);
			}		
		}
		
		/* setInterval(function(){

	      $('#viewContactsForSupport').each(function() {

	        $(this).toggle();

	      });

	    }, 600); */
	    
	    function changeimage(){
			var img='captcha.jpg?' + Math.random();
			document.getElementById('captcha_id2').style.backgroundImage="url("+img+")";
			return false;		
		}
	</script>
	
	<style type="text/css">
		body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,textarea,p,blockquote,th,td
			{
			margin: 0;
			padding: 0;
		}
		
		body {
			background: none repeat scroll 0 0 #EEEEEE;
			color: #333333;
			font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
			font-size: 14.5px;
			line-height: 16px;
			text-shadow: 0 1px 0 #FFFFFF;
		}
		
		a {
			color: #333333;
			text-decoration: underline;
		}
		
		form {
			background: -moz-linear-gradient(90deg, #CCCCCC, #FFFFFF) repeat scroll
				0 0 transparent;
			border: 1px solid #AAAAAA;
			border-radius: 10px 10px 10px 10px;
			box-shadow: 0 0 15px #AAAAAA;
			margin: 60px auto 0;
			padding: 20px;
			width: 440px;
		}
		
		h1 {
			border-bottom: 1px solid #CCCCCC;
			font-size: 15px;
			font-weight: bold;
			letter-spacing: 2px;
			margin-bottom: 25px;
			padding-bottom: 5px;
		}
		
		form p {
			margin-bottom: 15px;
		}
		
		form p:last-child {
			margin-bottom: 0;
		}
		
		label {
			cursor: pointer;
			display: block;
			float: left;
			font-size: 13px;
			font-weight: bold;
			line-height: 28px;
			margin-bottom: 5px;
			width: 120px;
		}
		
		form p:hover label {
			color: #0459B7;
		}
		
		form p:hover label:after {
			content: " »";
		}
		
		input[type="text"],input[type="password"] {
			background: -moz-linear-gradient(90deg, #FFFFFF, #EEEEEE) repeat scroll
				0 0 transparent;
			border: 1px solid #AAAAAA;
			border-radius: 3px 3px 3px 3px;
			box-shadow: 0 0 3px #AAAAAA;
			padding: 5px;
			width: 200px;
		}
		
		input[type="text"]:focus,input[type="password"]:focus {
			border-color: #093C75;
			box-shadow: 0 0 3px #0459B7;
			outline: medium none;
		}
		
		select {
			box-shadow: 0 0 3px #AAAAAA;
			cursor: pointer;
			padding: 3px;
		}
		
		select:active,select:focus {
			border: 1px solid #093C75;
			box-shadow: 0 0 3px #0459B7;
			outline: medium none;
		}
		
		input[type="submit"],a.submit {
			background: -moz-linear-gradient(90deg, #0459B7, #08ADFF) repeat scroll
				0 0 transparent;
			border: 1px solid #093C75;
			border-radius: 3px 3px 3px 3px;
			box-shadow: 0 1px 0 #FFFFFF;
			color: #FFFFFF;
			cursor: pointer;
			font-family: Arial, sans-serif;
			font-size: 12px;
			font-weight: bold;
			margin-left: 120px;
			padding: 5px 10px;
			text-decoration: none;
			text-shadow: 0 1px 1px #333333;
			text-transform: uppercase;
		}
		
		input[type="submit"]:hover,a.submit:hover {
			background: -moz-linear-gradient(90deg, #067CD3, #0BCDFF) repeat scroll
				0 0 transparent;
			border-color: #093C75;
			text-decoration: none;
		}
		
		input[type="submit"]:active,input[type="submit"]:focus,a.submit:active,a.submit:focus
			{
			background: -moz-linear-gradient(90deg, #0BCDFF, #067CD3) repeat scroll
				0 0 transparent;
			border-color: #093C75;
			outline: medium none;
		}
		
		.error {
			font-weight: bold;
			color: red;
		}
		
		div#contactDiv {
		    background-color: silver;
		    padding: 5px 0 5px 0;
		    text-align: center;
		}
	</style>
</head>
<body>
	<h4 id="error_p">&nbsp;</h4>
	
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	
 	<form id="form" action="<c:url value='/j_spring_security_check'/>" method="post" autocomplete="off">
		<img alt="" src="./resources/images/header.jpg" >
		<p></p>
		
		<c:if test="${not empty param['error']}">
			<p class="error">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</p>
			<c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />
		</c:if>
		
		<if test="${not empty sessionScope.captchaError}">
			<p class="error">${sessionScope.captchaError}</p>
		</if> 
		
		<c:if test="${empty param['error']}"> 
	  		<p class="info"></p>
		</c:if> 
		
		<c:if test="${login_disabled_notification_flag=='ON'}">
			<p id="login_disabled_notification_message_para" style="display: none;font-weight: bold;color: orange;">
				<spring:message code="system.notification_message.login_disabled" text="Your Login is disabled for some reason."/>
			</p>
		</c:if>
		
		<h1><spring:message code="login.vidhanmandal" text=""></spring:message></h1>
		
		<p id="localeSelectionP">
		  	<input type="hidden" value="${lang}" id="language">
			<label for="lang"><spring:message code="lang" text="Change Language" /></label>	
			<select id="lang" name="language" >
				<c:forEach items="${locales}" var="i">
				<option value="${i.localeString}">${i.displayName}</option>
				</c:forEach>					
			</select>
	 	</p>
	 	
		<p>
			<label for="j_username"><spring:message code="user_lbl_username" text="Username" /></label>
			<input type="text" id="j_username"   value="" name="j_username"/>
		</p>
		
		<p id="passwordSection">
			<label for="password"><spring:message code="user_lbl_password" text="Password" /></label>
			<input type="password" id="j_password"  value="" name="j_password" autocomplete="false"/>
		</p>
		
				<c:if test="${requestScope.captchaRequired=='1'}">
		<p id="captchaSection">
			<p>
			<table style="width: 49%;border: 1px solid #d9dbdd; padding: 0;margin-left:27%">
		        	<tr>
                    <td style="width: 75%" >
                        <div id="captcha_id2" style="width:100%;height:40px;background:url('captcha.jpg') no-repeat center;background-size: cover;">
                            <!-- <img id="captcha_id" name="imgCaptcha" src="captcha.jpg" width="100%" height="40px"> -->
                        </div>
                    </td>
 
                    <td align="left"><a href="javascript:;"
                        title="change captcha image"
                        onclick="changeimage()">
                             <img  alt=" Change image" width="80%" height="40px"  src="resources/images/refresh2D.png">
                    </a></td>                    
                    </tr>
 			</table>
 			</p>
 			 <p>
 			 	<label></label>
                <input type="text" name="captcha" class="round full-width-input" placeholder="" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false"/>
             </p>
             
		</p>
		</c:if>
		
		
		<a href="#" id="viewContactsForSupport" style="margin-left:20px;float: right;margin-top: -42px;display: none;">
			<img width="30" height="25" src="./resources/images/contactus.jpg">
		</a>
		
		<p>
			<!-- <span class="fl">
				<a href="#">I Forgot My Password!</a>
			</span> -->
			<input id="saveForm" class="button button-gray fr" type="submit" value="<spring:message code='user_lbl_login' text='Login'/>"/>		
		
			<a href="#" id="forget_password_link" style="margin-left:20px;font-size:  14px;">
				<spring:message code="login_page.forget_password_link" text="Password Queries" />
			</a>
		</p>
	
		<div id="contactDiv" style="display: none;">
			<spring:message code="login_page.lockdown_support_numbers" text="Lockdown Support: 9773955035 / 9096711433 / 9892406094" />			
		</div>		
		
		<input id="selectedLocale" name="selectedLocale" value="${selectedLocale}" type="hidden">
		<input id="defaultLocale" name="defaultLocale" value="${defaultLocale}" type="hidden">
	</form>
	
	<c:if test="${login_disabled_notification_flag=='ON'}">
		<select id="login_disabled_usernames" style="display: none;">
			<c:forEach items="${loginDisabledUsernames}" var="i">
				<option value="${i}">${i}</option>
			</c:forEach>
		</select>
	</c:if>
	
	<input id="encryptionRequired" name="encryptionRequired" value="${passwordEncryptionReq}" type="hidden"/>	
	<input type="hidden" id="login_disabled_notification_flag" value="${login_disabled_notification_flag}">
	<input type="hidden" id="login_disabled_notification_message" value="<spring:message code='system.notification_message.login_disabled' text='Your Login is disabled for some reason.'/>" />
</body>
</html>