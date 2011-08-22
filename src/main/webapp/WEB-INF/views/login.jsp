<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<link rel="stylesheet" media="screen" href="./resources/css/insync.css" />
<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js"></script>

<!--[if lt IE 9]>
<script src="http://themes.vivantdesigns.com/streamlined/js/html5.js"></script>
<script src="http://themes.vivantdesigns.com/streamlined/js/PIE.js"></script>
<![endif]-->
<!-- jquerytools -->

<!--[if lte IE 9]>
<link rel="stylesheet" media="screen" href="http://themes.vivantdesigns.com/streamlined/css/ie.css" />
<script type="text/javascript" src="http://themes.vivantdesigns.com/streamlined/js/ie.js"></script>
<![endif]-->


<script type="text/javascript"> 
	$(document).ready(function(){
	    $("#j_username").focus();
	    $("#lang").change(function(){
		    var url = location.href + "?lang="+$(this).val();
		    location.href = url;
	    });
	});
</script> 
<meta charset="UTF-8"></head><p>
<label for="lang"><spring:message code="lang" text="Change Language" /></label>
				<select id="lang" name="language" required="required">
					<option value="en" selected="selected">English</option>
					<option value="mr_IN">Marathi</option>
					<option value="hi_IN">Hindi</option>
</select>
</p>
<body class="login">
    <div class="login-box main-content">
      <header><h2></h2></header>
    	<section>
    		<c:if test="${not empty param['error']}"> 
    			<div class="message error">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
			</c:if> 
			<c:if test="${empty param['error']}"> 
    			<div class="message info"></div>
			</c:if> 
    		<form id="form" action="<c:url value='/j_spring_security_check'/>" method="post" class="clearfix">
			<p>
				<label for="j_username"><spring:message code="user_lbl_fname" text="Welcome" /></label>
				<input type="text" id="j_username"  class="full" value="" name="j_username" required="required" placeholder="Username" autofocus/>
			</p>
			<p>
				<label for="password"></label>
				<input type="password" id="j_password" class="full" value="" name="j_password" required="required" placeholder="Password" />
			</p>
			<p>
			</p>
			<p class="clearfix">
				<span class="fl">
					<input type="checkbox" id="remember" class="" value="1" name="remember"/>
					<label class="choice" for="remember"></label>
				</span>
				<button class="button button-gray fr" type="submit"></button>
			</p>
		</form>
    	</section>
    </div>
</body>
</html>