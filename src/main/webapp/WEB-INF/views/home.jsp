<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" > 
	<head>
		<title><spring:message code="home.title" text="ELS - Home"></spring:message></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		 <!-- Jquery directly from google servers--> 
		<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js" ></script>
		<script type="text/javascript" src="./resources/js/jquery-ui-1.8.15.custom.min.js"></script> 
		<script type="text/javascript" src="./resources/js/i18n/grid.locale-${locale}.js"></script>
		<script type="text/javascript" src="./resources/js/jquery.jqGrid.min.js"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery-impromptu.3.2.min.js"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.iframe-transport.js"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.fileupload.js"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.maskedinput.js"></script>	
		<script type="text/javascript" src="./resources/js/jquery/jquery.sexyselect.min.0.55.js"></script>
		<script type="text/javascript" src="./resources/js/jquery/blockUI.js"></script>				
		<script type="text/javascript" src="./resources/js/common.js"></script> 
		 <!-- WYSIWYG Editor --> 
		<script type="text/javascript" src="./resources/js/template/jquery.wysiwyg.js"></script> 
		
		 <!-- Style switcher --> 
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js"></script>
		
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/reset.css" />
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/blue.css" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.jqgrid.css"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/cupertino/jquery-ui-1.8.16.custom.css"  />
		<!-- comment extra.css for css validation -->
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/extra.css" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery.sexyselect.0.55.css"  />
		
		<!-- See Interface Configuration --> 
		<script type="text/javascript" src="./resources/js/template/seeui.js"></script>
		<!--[if IE 6]>
			<script type="text/javascript" src="js/ddbelatedpng.js"></script>
			<script type="text/javascript">	
				DD_belatedPNG.fix('img, .info a');
			</script>
		<![endif]-->

		<!--   <script type="text/javascript"></script>    -->
	</head>
		
	<body>
		<div id="bk">
		<%@ include file="/common/header.jsp" %>
			<input type="hidden" id="dateformat" name="dateformat" value="${dateFormat}"/>
			<input type="hidden" id="timeformat" name="timeformat" value="${timeFormat}"/>
            <input type="hidden" id="authusername" name="authusername" value="${authusername}"/>
            <input type="hidden" id="authfullname" name="authfullname" value="${authtitle} ${authfirstname} ${authmiddlename} ${authlastname}"/>    
            <input type="hidden" id="authhousetype" name="authhousetype" value="${authhousetype}"/>             
                        
		<div id="container" class="clearfix">
			<div id="page">
				<div class="menu clearfix">
					<%@ include file="/common/menu.jsp" %>
					 <!-- Page title --> 
					<div id="module_title"></div>
				</div>
				
				<div class="clearfix content">
					 <!-- Page content --> 
									
					 <!-- Page content -->
				</div>
			</div>
		</div>
		</div>
		
	</body>

</html>
