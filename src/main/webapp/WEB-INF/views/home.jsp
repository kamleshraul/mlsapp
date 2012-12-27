<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" > 
	<head>
		<title><spring:message code="home.title" text="ELS - Home"></spring:message></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js?v=2" ></script>
		<script type="text/javascript" src="./resources/js/jquery-ui-1.8.15.custom.min.js?v=2"></script> 
		<script type="text/javascript" src="./resources/js/i18n/grid.locale-${locale}.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery.jqGrid.min.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/grid.subgrid.js?v=2"></script>		
		<script type="text/javascript" src="./resources/js/jquery/jquery-impromptu.3.2.min.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.iframe-transport.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.fileupload.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.maskedinput.js?v=2"></script>	
		<script type="text/javascript" src="./resources/js/jquery/blockUI.js?v=2"></script>				
		<script type="text/javascript" src="./resources/js/autoNumeric-1.5.4.js?v=2"></script>
		
		<!-- Multiselect -->				
		<script type="text/javascript" src="./resources/js/ui.sexyselect.0.6.js?v=2"></script>

		<script type="text/javascript" src="./resources/js/common.js?v=2032"></script> 
		
		<!-- Dual Listbox -->
	    <script type="text/javascript" src="./resources/js/jquery.dualListBox-1.3.min.js?v=2032"></script> 
		
		
		<!-- WYSIWYG Editor --> 
		<script type="text/javascript" src="./resources/js/jquery.wysiwyg.js?v=2"></script>		
		<!-- Context Menu -->
		<script type="text/javascript" src="./resources/js/jquery.contextMenu.js?v=2"></script>
		<!-- Tooltip -->
		<script type="text/javascript" src="./resources/js/jquery.qtip-1.0.0-rc3.min.js?v=2"></script>
		 <!-- Style switcher --> 
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js?v=2"></script>
		
		<script type="text/javascript" src="./resources/js/jquery.fancybox.pack.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-thumbs.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-buttons.js?v=2"></script>
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js?v=2"></script>
	    <script type="text/javascript" src="./resources/js/jquery.fcbkcomplete.js?q=123?v=2"></script>
	    
	    <!-- 2 way multiselect -->
	    <script type="text/javascript" src="./resources/js/ui.multiselect.js?q=123?v=1"></script>
	    <script type="text/javascript" src="./resources/js/jquery.tmpl.1.1.1.js?q=123?v=1"></script>	    
	    <script type="text/javascript" src="./resources/js/jquery.localisation.js?q=123?v=1"></script>     
		
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/reset.css?v=2" />
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/blue.css?v=2" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.jqgrid.css?v=2"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css?v=2"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/cupertino/jquery-ui-1.8.16.custom.css?v=2"  />
		<!-- comment extra.css for css validation -->
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/extra.css?v=2" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.sexyselect.0.55.css?v=2"  />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-buttons.css?v=2" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-thumbs.css?v=2" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox.css?v=2" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.wysiwyg.css?v=2" />
		
		<!-- 2 way multiselect -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/ui.multiselect.css?v=1" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/ui.multiselect.common.css?v=1" />
		
		<!-- Context Menu -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.contextMenu.css?v=2" />
		
		
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
