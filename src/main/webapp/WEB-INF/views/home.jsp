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
		<script type="text/javascript" src="./resources/js/autoNumeric-1.5.4.js"></script>
		<!-- Multiselect -->				
		<script type="text/javascript" src="./resources/js/jquery.multiselect.js"></script>

		<script type="text/javascript" src="./resources/js/common.js"></script> 
		 <!-- WYSIWYG Editor --> 
		<script type="text/javascript" src="./resources/js/template/jquery.wysiwyg.js"></script> 
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/jquery.multiselect.css" />
		
		 <!-- Style switcher --> 
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js"></script>
		
		<script type="text/javascript" src="./resources/js/jquery.fancybox.pack.js"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-thumbs.js"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-buttons.js"></script>
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js"></script>
		
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/reset.css" />
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/blue.css" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.jqgrid.css"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/cupertino/jquery-ui-1.8.16.custom.css"  />
		<!-- comment extra.css for css validation -->
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/extra.css" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery.sexyselect.0.55.css"  />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-buttons.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-thumbs.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox.css" />
		
		
		
		<!-- See Interface Configuration --> 
		<script type="text/javascript" src="./resources/js/template/seeui.js"></script>
		<!--[if IE 6]>
			<script type="text/javascript" src="js/ddbelatedpng.js"></script>
			<script type="text/javascript">	
				DD_belatedPNG.fix('img, .info a');
			</script>
		<![endif]-->

		<!--   <script type="text/javascript"></script>    -->
		
		<!-- This is done to fire the assembly module when a user login to the system. -->
		<script type="text/javascript">
		$(document).ready(function(){
			//triggering mis lowerhouse module on login
			var locale=$('#authlocale').val();
			$('.content').load('member/module?houseType=lowerhouse',function(data){
			    var title = $(data).filter('title').text();
				$('#module_title').html(title);
				$('#authhousetype').val("lowerhouse");				
			});			
		});
		
		</script>
	</head>
		
	<body>
		<div id="bk">
		<%@ include file="/common/header.jsp" %>
			<input type="hidden" id="dateformat" name="dateformat" value="${dateFormat}"/>
			<input type="hidden" id="timeformat" name="timeformat" value="${timeFormat}"/>
            <input type="hidden" id="authusername" name="authusername" value="${authusername}"/>
            <input type="hidden" id="authfullname" name="authfullname" value="${authtitle} ${authfirstname} ${authmiddlename} ${authlastname}"/>    
            <input type="hidden" id="authhousetype" name="authhousetype" value="${authhousetype}"/>  
            <input type="hidden" id="authlocale" name="authlocale" value="${locale}"/> 
            <input type="hidden" name="cancelFn" id="cancelFn"/>	
            <!-- This is done to remove a bug wherein messages Download/Remove in file uploading donot canges to locale specific.
            values as the reuest for jsp is not passing through Resource Bundle Filter -->
            <input type="hidden" name="downloadUploadedFile" id="downloadUploadedFile" value="<spring:message code='file.download' text='Download'></spring:message>"/>	
            <input type="hidden" name="removeUploadedFile" id="removeUploadedFile" value="<spring:message code='file.remove' text='Remove'></spring:message>"/>	
            <input type="hidden" name="cancelFn" id="cancelFn"/>	
            
                             
                        
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
