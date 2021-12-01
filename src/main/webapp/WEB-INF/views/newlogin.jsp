<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="icon" type="image/png" href="./resources/images/mlsicon.png" />

  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  	<title><spring:message code="login.vidhanmandal1" text="Vidhan Mandal"></spring:message> | <spring:message code='user_lbl_login' text='Login'/></title>
	
	<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="./resources/js/crypto-js.js"></script>
	<script type="text/javascript" src="./resources/js/jquery/jquery-impromptu.3.2.min.js"></script>
	<!-- <script type="text/javascript" src="./resources/js/crosstab_handling_loginpage.js"></script> -->
	<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css"  />
	
	<link rel="stylesheet" href="./resources/plugins/materialdesignicons/css/materialdesignicons.min.css" />
	<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
	<link rel="stylesheet" href="./resources/css/custom.css" />
	
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
		    			//$.prompt($('#login_disabled_notification_message').val());
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
			    			//$.prompt($('#login_disabled_notification_message').val());
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
	

</head>
<body class="login">
  <div class="lp-wrapper">
    <div class="lp-holder">
      <div class="row no-gutters">
        <div class="col-md-6 col-lg-5 col-xl-4">
          <div class="form-wrapper">
            <div class="logo-wrapper">
              <img
                src="./resources/images/Seal_of_Maharashtra.png"
                alt=""
                class="logo-m"
              />
              <h3 class="logo-title"><spring:message code="login.vidhanmandal" text=""></spring:message></h3>
            </div>
            <h4 id="error_p">&nbsp;</h4>
	
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
            <form class="form-holder" id="form" action="<c:url value='/j_spring_security_check'/>" method="post" autocomplete="off">
           
            
		
		<c:if test="${not empty param['error']}">
    <div id="error" style="font-weight: bold;color: red;font-size:20px;">
        <spring:message code="login_page.invalid_login">   
        </spring:message>
        <c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />
    </div>
</c:if>

<%--   <c:if test="${not empty param['error']}">
			<div class="help-block" >${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
			<c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />
		</c:if> --%>
		
		<if test="${not empty sessionScope.captchaError}">
			<div class="help-block" style="color: #FF0000;">${sessionScope.captchaError}</div>
		</if> 
		
		<c:if test="${empty param['error']}"> 
	  		<div class="info"></div>
		</c:if> 
		
		<c:if test="${login_disabled_notification_flag=='ON'}">
			<div id="login_disabled_notification_message_para" style="display: none;font-weight: bold;color: orange;">
				<spring:message code="system.notification_message.login_disabled" text="Your Login is disabled for some reason."/>
			</div>
		</c:if>
		<div id="localeSelectionP">
		  	<input type="hidden" value="${lang}" id="language">
			<label for="lang"><spring:message code="lang" text="Change Language" /></label>	
			<select id="lang" name="language" >
				<c:forEach items="${locales}" var="i">
				<option value="${i.localeString}">${i.displayName}</option>
				</c:forEach>					
			</select>
	 	</div>
              <div class="form-group">
                <label for="exampleInputEmail1"><spring:message code="user_lbl_username" text="Username" /></label>
                <div class="input-group">
                  <input
                    type="text"
                    class="form-control"
                    id="j_username"   value="" name="j_username"
                    aria-label="Username"
                  />
                  <i class="mdi mdi-email-outline"></i>
                </div>
              </div>
              <div class="form-group" id="passwordSection">
                <label for="exampleInputPassword1"><spring:message code="user_lbl_password" text="Password" /></label>
                <div class="input-group">
                  <input
                    type="password"
                    class="form-control"
                    id="j_password"  value="" name="j_password" autocomplete="false"
                    aria-label="Password"
                  />
                  <i class="mdi mdi-lock-outline"></i>
                </div>
              </div>
              <c:if test="${requestScope.captchaRequired=='1'}">
		<div id="captchaSection">
			<div>
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
 			</div>
 			 <div>
 			 	<label></label>
                <input type="text" name="captcha" class="round full-width-input" placeholder="" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false"/>
             </div>
             
		</div>
		</c:if>
                               
              <div class="form-group form-action">
                <button class="btn btn-login" id="saveForm"><spring:message code='user_lbl_login' text='Login'/></button>
              </div>
              <hr>
              <div class="link-holder">
                <button type="button" class="btn btn-link" data-toggle="modal" data-target="#rules">
                <spring:message code='login.rules' text='Rules and Regualtions'/>
                </button>
                 <button type="button" class="btn btn-link" data-toggle="modal" data-target="#manual">
                <spring:message code='login.manual' text='User Manuals'/>
                </button>
                
              </div>
              <div class="link-holder">
               <button type="button" class="btn btn-link" data-toggle="modal" data-target="#help">
                <spring:message code='login.help' text='Help'/>
                </button>
                
                <button type="button" class="btn btn-link" data-toggle="modal" data-target="#download">
                <spring:message code='login.impdownload' text='Help'/>
                </button>
              
              </div>
            </form>
			
				<c:if test="${login_disabled_notification_flag=='ON'}">
		<select id="login_disabled_usernames" style="display: none;">
			<c:forEach items="${loginDisabledUsernames}" var="i">
				<option value="${i}">${i}</option>
			</c:forEach>
		</select>
	</c:if>
	
            <div class="login-footer">
            <!--   <p class="version">version 1.0.0</p> -->
              <div class="copyright">
                Copyright &copy; 2021 Maharashtra Knowledge Corporation Ltd.<br>
                All Rights Reserved.
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-7 col-xl-8 d-none d-md-block">
          <div class="img-wrapper" style="background-image: url(./resources/images/vidhanmandal.png);"></div>
        </div>
      </div>
    </div>
  </div>
  <div class="modal fade" id="rules" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
      <div class="modal-content">
        <div class="modal-body">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="mdi mdi-close"></i></span>
          </button>
          <h5 class="modal-title" id="exampleModalLabel"><spring:message code='login.rules' text='Rules and Regualtions'/></h5>
          <ul>
            <li><spring:message code='login.rules2' text='Rules and Regualtions'/></li>
            <li><spring:message code='login.rules3' text='Rules and Regualtions'/></li>
             <li><spring:message code='login.rules4' text='Rules and Regualtions'/></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  
    <div class="modal fade" id="help" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
      <div class="modal-content">
        <div class="modal-body">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="mdi mdi-close"></i></span>
          </button>
          <h5 class="modal-title" id="exampleModalLabel"><spring:message code='login.help' text='Help'/></h5>
          <ul>
            <li><spring:message code='login.help1' text='Rules and Regualtions'/></li>

          </ul>
        </div>
      </div>
    </div>
  </div>
  
     <div class="modal fade" id="manual" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
      <div class="modal-content">
        <div class="modal-body">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="mdi mdi-close"></i></span>
          </button>
          <h5 class="modal-title" id="exampleModalLabel"><spring:message code='login.manual' text='Help'/></h5>
          <ul>
            <li><a href="http://mls.org.in/newpdf/Member%20manual%20ver%204.pdf" target="_blank"><spring:message code='login.manual1' text='User Manuals'/></a></li>
			<li><a href="http://mls.org.in/newpdf/Mantralaya_Department%20Manual.pdf" target="_blank"><spring:message code='login.manual2' text='User Manuals'/></a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  
     <div class="modal fade" id="download" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
      <div class="modal-content">
        <div class="modal-body">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true"><i class="mdi mdi-close"></i></span>
          </button>
          <h5 class="modal-title" id="exampleModalLabel"><spring:message code='login.impdownload' text='Help'/></h5>
          <ul>
            <li><a href="http://mls.org.in/download/ISM/ISMV6.3.rar" target="_blank"><spring:message code='login.impdownload1' text='Download'/></a></li>
             <li><a href="http://mls.org.in/download/firefox/Firefox%20Setup%2038.0b2.rar" target="_blank"><spring:message code='login.impdownload2' text='Download'/></a></li>
              <li><a href="https://download.anydesk.com/AnyDesk.exe" target="_blank"><spring:message code='login.impdownload3' text='Download'/></a></li>
              
          	
           </ul>
        </div>
      </div>
    </div>
  </div>

	
	 <input id="selectedLocale" name="selectedLocale" value="${selectedLocale}" type="hidden">
	<input id="defaultLocale" name="defaultLocale" value="${defaultLocale}" type="hidden">
	<input id="encryptionRequired" name="encryptionRequired" value="${passwordEncryptionReq}" type="hidden"/>	
	<input type="hidden" id="login_disabled_notification_flag" value="${login_disabled_notification_flag}">
	<input type="hidden" id="login_disabled_notification_message" value="<spring:message code='system.notification_message.login_disabled' text='Your Login is disabled for some reason.'/>" />
	
  
  <script src="./resources/js/jquery.min.js"></script>
  <script src="./resources/js/popper.min.js"></script>
  <script src="./resources/js/bootstrap.min.js"></script>
  
  	
</body>
</html>