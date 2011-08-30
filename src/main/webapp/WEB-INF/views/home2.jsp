<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
	<title>Welcome to e-Legislature</title>
	<link type="text/css" rel="stylesheet" href="./resources/css/wufoo/structure.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/wufoo/theme.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/wufoo/form.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/jquery.toastmessage.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/layout.css" />
	<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="./resources/js/jquery.toastmessage.js"></script>
	<script type="text/javascript" src="./resources/js/wufoo.js"></script>
	<script type="text/javascript">
        function resizeWindow() 
        {
            var windowHeight = getWindowHeight();

            document.getElementById("content").style.height = (windowHeight - 110) + "px";
            document.getElementById("contentPanel").style.height = (windowHeight - 160) + "px";
            document.getElementById("navigation").style.height = (windowHeight - 110) + "px";
        }
  
        function getWindowHeight() 
        {
            var windowHeight=0;
            if (typeof(window.innerHeight)=='number') 
            {
                windowHeight = window.innerHeight;
            }
            else {
                if (document.documentElement && document.documentElement.clientHeight) 
                {
                    windowHeight = document.documentElement.clientHeight;
                }
                else 
                {
                    if (document.body && document.body.clientHeight) 
                    {
                        windowHeight = document.body.clientHeight;
                    }
                }
            }
            return windowHeight;
        }

        function handleXhrError(xhr) {
			if(xhr.status==500 && xhr.responseText.indexOf("SESSION_TIMED_OUT")!=-1){
				alert('Due to inactivity for a long time, your session has expired. You will now be logged out.');
				window.location.href='login.htm';
			}
			else{
				$().toastmessage('showToast', {
				    text     : "<p style='font-size:small;color:red'>An error has occured while carrying out this operation. The details have been reported to the project team.</p> You will be notified of the resolution soon.",
				    type     : 'error',
				    stayTime : 3000,
				    inEffectDuration:  600
				});
			}
		};
		$(document).ready(function () {
			$("#logout").click(function(){
			    window.location.href($(this).href);
			    return false;
		    });
			$.ajaxSetup({
			    error: handleXhrError
			});
		});
    </script>
    <style type="text/css">
    	.field_error{
			color:red !important;
		}
		input[readonly]{
		    background-color: #F2F2F2 !important;
		    color: #C6C6C6;
		    border-color: #ddd;
		}
    </style>
</head>
<body onresize="resizeWindow()" onload="resizeWindow()">
            <%@ include file="/common/header.jsp" %>
			<div class="toolbar">
				<%@ include file="/common/menu.jsp" %>
			</div>
 		   <div class="clear"></div>
 		   <div id="split">
           <div class="navigation" id="navigation">
           	   <div class="subHeader">
            		<div></div>
            	</div>
            	<div id="navContent" style="overflow:auto"></div>
           </div>
            <div class="content" id="content">
            	<div class="subHeader">
            		<div></div>
            	</div>
            	<div class="contentPanel" id="contentPanel">
				</div>
            </div>
            </div>
            <div class="clear"></div>
            <%@ include file="/common/footer.jsp" %>
</body>
</html>

