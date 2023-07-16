<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" > 
	<head>
		<title><spring:message code="home.title" text="ELS - Home"></spring:message></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="icon" type="image/png" href="./resources/images/mlsicon.png" />
		
		<script type="text/javascript" src="./resources/js/jquery-1.6.2.min.js?v=5" ></script>
		<script type="text/javascript" src="./resources/js/jquery-ui-1.8.15.custom.min.js?v=5"></script> 
		<script type="text/javascript" src="./resources/js/i18n/grid.locale-${locale}.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery.jqGrid.min.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/grid.subgrid.js?v=5"></script>		
		<script type="text/javascript" src="./resources/js/jquery/jquery-impromptu.3.2.min.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.iframe-transport.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.fileupload.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery/jquery.maskedinput.js?v=5"></script>	
		<script type="text/javascript" src="./resources/js/jquery/blockUI.js?v=5"></script>				
		<script type="text/javascript" src="./resources/js/autoNumeric-1.5.4.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/tinymce.min.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/themes/modern/theme.min.js?v=6"></script>
		<script type="text/javascript" src="./resources/js/moment-with-locales.js?v=6"></script>
		<!-- <script type="text/javascript" src="./resources/js/vue-2.4.0.dev.js?v=7"></script> -->
		<script type="text/javascript" src="./resources/js/vue-2.4.0.prod.min.js?v=7"></script>
		<script type="text/javascript" src="./resources/js/jquery.toastmessage.js?v=1"></script>
			
		<!-- Multiselect -->				

		<script type="text/javascript" src="./resources/js/common.js?v=3060"></script>
		
		<script type="text/javascript" src="./resources/js/ui.sexyselect.0.6.js?v=6"></script>
		
		
		
		<!-- WYSIWYG Editor --> 
		<script type="text/javascript" src="./resources/js/jquery.wysiwyg.js?v=7"></script>
		<script type="text/javascript" src="./resources/js/wysiwyg.rmFormat.js?v=6"></script>
		<script type="text/javascript" src="./resources/js/wysiwyg.table.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/wysiwyg.i18n.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/wysiwyg.fullscreen.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/i18n/lang.mr.js?v=5"></script>	
		<!-- Context Menu -->
		<script type="text/javascript" src="./resources/js/jquery.contextMenu.js?v=5"></script>
		<!-- Tooltip -->
		<script type="text/javascript" src="./resources/js/jquery.qtip-1.0.0-rc3.min.js?v=5"></script>
		 <!-- Style switcher --> 
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js?v=5"></script>
		
		<script type="text/javascript" src="./resources/js/jquery.fancybox.pack.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-thumbs.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/jquery.fancybox-buttons.js?v=5"></script>
		<script type="text/javascript" src="./resources/js/template/stylesheetToggle.js?v=5"></script>
	    <script type="text/javascript" src="./resources/js/jquery.fcbkcomplete.js?q=123?v=5"></script>
	    
	    <!-- 2 way multiselect -->
	    <script type="text/javascript" src="./resources/js/ui.multiselect.js?q=123?v=5"></script>
	    <script type="text/javascript" src="./resources/js/jquery.tmpl.1.1.1.js?q=123?v=5"></script>	    
	    
	    <!-- <script type="text/javascript" src="./resources/js/crosstab_handling_homepage.js?v=5"></script> -->
	    
		<!-- <link rel="stylesheet" rel="stylesheet" href="./resources/js/skins/lightgray/content.min.css?v=6"  /> -->
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/reset.css?v=3" />
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/blue.css?v=5" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.jqgrid.css?v=3"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/jquery-impromptu.css?v=3"  />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/cupertino/jquery-ui-1.8.16.custom.css?v=3"  />
		<!-- comment extra.css for css validation -->
		<link rel="stylesheet" type="text/css" media="all" href="./resources/css/template/extra.css?v=3" />
		<link rel="stylesheet" rel="stylesheet" href="./resources/css/ui.sexyselect.0.55.css?v=3"  />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-buttons.css?v=3" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox-thumbs.css?v=3" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.fancybox.css?v=3" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.wysiwyg.css?v=3" />
		
		<!-- 2 way multiselect -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/ui.multiselect.css?v=3" />
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/ui.multiselect.common.css?v=3" />
		
		<!-- Context Menu -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.contextMenu.css?v=3" />
		
		<!-- Toast Messages CSS -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/jquery.toastmessage.css?v=1" />
		
		<!-- Notification CSS -->
		<link rel="stylesheet" type="text/css" media="screen" href="./resources/css/vue_notifications.css?v=1" />
		
		<!-- Printer Friendly CSS -->
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=5" />
		
		<!-- See Interface Configuration --> 
		<script type="text/javascript" src="./resources/js/template/seeui.js"></script>
		<!--[if IE 6]>
			<script type="text/javascript" src="js/ddbelatedpng.js"></script>
			<script type="text/javascript">	
				DD_belatedPNG.fix('img, .info a');
			</script>
		<![endif]-->
		
		<style>
		
	/* 	#userLoginDetails{
		float: right;
		margin-top:2%;
		} */
		#module_title{
		margin-left:50px;
		}
		.arrowBox{
  			position: relative;
 			 width:220px;
  			/* background-color: #0085D1; */
  			background: #1C5987 !important;
  			height:40px;
  			line-height: 40px;
  			margin-bottom:30px; 
 			 text-align:center;
 			 color:#fff;
 			 /* border: 2px solid red; */
			}

		.arrowBox a{
 			 color:#000000;
		}
		
		.arrow-left:before{
   			 content: "";
    		position: absolute;
   			 left: -20px;
    		top: 0;
  		  border-top: 20px solid transparent;
 		   border-bottom: 20px solid transparent;
  		  border-right: 20px solid #0085D1; 
		}
		
		/* #module_title{
		background-color: lightblue !important;
		} */
		
			@media print {
			    html, body {
			      height:100vh; 
			      margin: 0 !important; 
			      padding: 0 !important;
			      overflow: hidden;
			    }
			}
			div#browserIncompatible {
			    background-color: yellow;
			    padding: 5px 0 5px 0;
			    text-align: center;
			}
			div#contactDiv {
			    background-color: yellow;
			    padding: 5px 0 5px 0;
			    text-align: center;
			}

		</style>
		
		<script type="text/javascript">
			var server_time = null;

			var updateServerTime = function () {				
				server_time.add(1, 'seconds').calendar();				
				$('#server_time_display').html(server_time.format('DD/MM/YYYY hh:mm:ss a'));
			};
			
			$(document).ready(function(){
				//This is done to fire the assembly module when a user login to the system.
				//triggering mis lowerhouse module on login
				var locale=$('#authlocale').val();
				var startURL=$("#startURL").val();
				var resourceURL='member/module?houseType=lowerhouse';
				if(startURL!=''){
					resourceURL=startURL;
				}
				$('.content').load(resourceURL,function(data){
				    var title = $(data).filter('title').text();
					$('#module_title').html(title);
					//$('#authhousetype').val("lowerhouse");				
				});
				
				$.ajax({
					url: 'ref/servertime',
					type: 'GET',
			        async: false,
			        success: function(data) {
			        	$('#logintime_server').val(data);
			        	server_time = moment($('#logintime_server').val());
			 			server_time.locale(locale.substring(0, 2));
						$('#server_time_display').html(server_time.format('DD/MM/YYYY hh:mm:ss a'));
			        }
				});
				
				setInterval(updateServerTime, 1000);
				
				if($('#isFeedbackEnabled').val()=="YES"){
					viewFeedback();
				}
				
				if($('#pushNotificationsEnabled').val()=="YES") {
					$.getScript("./resources/js/atmosphere.js?v=1", function() {
						$.getScript("./resources/js/atmosphere_notifications.js?v=7", function() {
							//console.log("atmosphere push notifications loaded..");
						});
					});
				} else {
					$.getScript("./resources/js/vue_notifications.js?v=7", function() {
						//console.log("notifications widget loaded..");
					});
				}
				
				//comment out below code if we need to close notifications box UI after clicking on the page outside the UI box
				/* $('body').click(function(evt){    
				       if(evt.target.id == "notifications" || evt.target.id == "notificationViewerPopUpDiv" || evt.target.class == "fancybox-close")
				          return;
				       //For descendants of notifications being clicked, remove this check if you do not want to put constraint on descendants.
				       if($(evt.target).closest('#notifications').length)
				          return; 
				       //For descendants of notificationViewerPopUp being clicked, remove this check if you do not want to put constraint on descendants.
				       if($(evt.target).closest('#notificationViewerPopUpDiv').length)
				          return;

				      //Do processing of click event here for every element except with id menu_content
				      $('#notifications').fadeOut('slow');
				}); */				
				
				$('#contactDivMarquee').mouseover(function() {
			        $(this).attr('scrollamount',0);
			    }).mouseout(function() {
			         $(this).attr('scrollamount',5);
			    });
	    	});	
			
			function viewFeedback(){
				$.get('view/feedback',function(data){
					$.fancybox(data);						
					},'html').fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});
			}
			

		</script>
	</head>
		
	<body id="vue_root">
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div id="bk">
		<%@ include file="/common/header.jsp" %>
			<input type="hidden" id="dateformat" name="dateformat" value="${dateFormat}"/>
			<input type="hidden" id="timeformat" name="timeformat" value="${timeFormat}"/>
            <input type="hidden" id="authusername" name="authusername" value="${authusername}"/>
            <input type="hidden" id="authfullname" name="authfullname" value="${authtitle} ${authfirstname} ${authmiddlename} ${authlastname}"/>    
            <input type="hidden" id="authlocale" name="authlocale" value="${locale}"/>
            <input type="hidden" id="authhousetype" name="authhousetype" value="${authhousetype}"/>
            <input type="hidden" id="isMemberLogin" name="isMemberLogin" value="${isMemberLogin}"/>
            <input type="hidden" id="isMinisterLogin" name="isMinisterLogin" value="${isMinisterLogin}"/>
            <input type="hidden" id="isSpeakerOrChairmanLogin" name="isSpeakerOrChairmanLogin" value="${isSpeakerOrChairmanLogin}"/>
            <input type="hidden" id="isDepartmentLogin" name="isDepartmentLogin" value="${isDepartmentLogin}"/>
           
            <input type="hidden" id="isFeedbackEnabled" name="isFeedbackEnabled" value="${isFeedbackEnabled}"/>
            
            <!-- uncomment in HomeController to use following parameter if needed --> 
            <input type="hidden" id="zeroDigitForLocale" name="zeroDigitForLocale" value="${zeroDigitForLocale}"/>
            
            <input type="hidden" name="cancelFn" id="cancelFn"/>
            <input type="hidden" id="startURL" name="startURL" value="${startURL}"/>	
            <input type="hidden" id="latestAssemblyHouseFormationDate" name="latestAssemblyHouseFormationDate" value="${latestAssemblyHouseFormationDate}"/>
            <input type="hidden" id="onlineDepartmentReplyBeginningDate" name="onlineDepartmentReplyBeginningDate" value="${onlineDepartmentReplyBeginningDate}"/>
            <!-- This is done to remove a bug wherein messages Download/Remove in file uploading donot canges to locale specific.
            values as the reuest for jsp is not passing through Resource Bundle Filter -->
            <input type="hidden" name="downloadUploadedFile" id="downloadUploadedFile" value="<spring:message code='file.download' text='Download'></spring:message>"/>	
            <input type="hidden" name="removeUploadedFile" id="removeUploadedFile" value="<spring:message code='file.remove' text='Remove'></spring:message>"/>	
            <input type="hidden" name="cancelFn" id="cancelFn"/>
            <input type="hidden" id="selectAll" value="<spring:message code='multiSelect.selectAll' text='Select All'/>"/>                 
            <input type="hidden" id="selectNone" value="<spring:message code='multiSelect.selectNone' text='Un-Select All'/>"/>
            <input type="hidden" id="outputFormatNotSetPrompt" value="<spring:message code='generic.report.outputFormatNotSetPrompt' text='Please select output format first.'/>"/>         	
            <input type="hidden" id="yesLabel" value="<spring:message code='generic.yes' text='Yes.'/>"/>
            <input type="hidden" id="noLabel" value="<spring:message code='generic.no' text='No.'/>"/>  
            <input type="hidden" id="noWordContentPrompt" value="<spring:message code='generic.noWordContentPrompt' text='MS Word Content is not allowed. Please paste from Notepad'/>"/>      
           	<input type="hidden" id="noMsOfficeContentPrompt" value="<spring:message code='generic.noMsOfficeContentPrompt' text='Microsoft Office Content is not allowed. Please paste from Notepad'/>"/>
        	<input type="hidden" id="noInvalidFormattingPrompt" value="<spring:message code='generic.noInvalidFormattingPrompt' text='Content is having invalid formatting tags which are not allowed. Please paste from Notepad'/>"/>
        	<input type="hidden" id="noInvalidFormattingInDeviceTextPrompt" value="<spring:message code='generic.noInvalidFormattingInDeviceTextPrompt' text='Device Text submitted is having invalid formatting tags which are not allowed. Please paste from Notepad'/>"/>
        	<input type="hidden" id="noInvalidFormattingInRevisedDeviceTextPrompt" value="<spring:message code='generic.noInvalidFormattingInRevisedDeviceTextPrompt' text='Revised Device Text submitted is having invalid formatting tags which are not allowed. Please paste from Notepad'/>"/>
        	<input type="hidden" id="loginMessageFromSystem" value="${loginMessageFromSystem}"/>
        	<input type="hidden" id="logintime_server" value="${logintime_server}"/>
        	<input type="hidden" id="pushNotificationsEnabled" value="${pushNotificationsEnabled}"/>
        	<input type="hidden" id="notification_alert" value="${notification_alert}"/>
        	<input type="hidden" id="notifications_visibleMaxCount" value="${notifications_visibleMaxCount}"/>
        	<input type="hidden" id="specialDashCharacter" value="${specialDashCharacter}"/>
        	<input type="hidden" id="topNotificationForAllUsers" value="<spring:message code='generic.topNotificationForAllUsers' text=''/>"/>
        	<input type="hidden" id="topNotificationForMembers" value="<spring:message code='generic.topNotificationForMembers' text=''/>"/>
        	<input type="hidden" id="topNotificationForMinisters" value="<spring:message code='generic.topNotificationForMinisters' text=''/>"/>
        	<input type="hidden" id="topNotificationForDepartmentUsers" value="<spring:message code='generic.topNotificationForDepartmentUsers' text=''/>"/>
        	<input type="hidden" id="topNotificationForLowerhouseUsers" value="<spring:message code='generic.topNotificationForLowerhouseUsers' text=''/>"/>
        	<input type="hidden" id="topNotificationForUpperhouseUsers" value="<spring:message code='generic.topNotificationForUpperhouseUsers' text=''/>"/>
        	<input type="hidden" id="topNotificationForLowerhouseMembers" value="<spring:message code='generic.topNotificationForLowerhouseMembers' text=''/>"/>
        	<input type="hidden" id="topNotificationForUpperhouseMembers" value="<spring:message code='generic.topNotificationForUpperhouseMembers' text=''/>"/>
       		<input type="hidden" id="markAllNotificationsAsReadPrompt" value="<spring:message code='notification.markAllAsReadPrompt' text='Do you really want to mark all pending notifications as read?'/>"/>
       		<input type="hidden" id="system_notifier_name" value="<spring:message code='notification.system_username' text='System Notifier'/>"/>
			<input type="hidden" id="submittedParliamentaryDevicesCannotBeDeletedPrompt" value="<spring:message code='generic.submittedParliamentaryDeviceCannotBeDeletedPrompt' text='parliamentary devices cannot be deleted post submission'></spring:message>" />
        	
        	<%-- <div id="userLoginDetails">
				<span style="text-align:center;">${authtitle}&nbsp;${authfirstname}&nbsp;${authmiddlename}&nbsp;${authlastname}</span><br/>
				<span id="server_time_display" style="width: 200px"></span>
				</div><br/> --%>
        <div id="container" class="clearfix">
			<div id="page">
				<div id="browserIncompatible"> 
					<spring:message code="browser.detection.msg"  
									text="Please Use Mozilla Firefox Browser For Better Performance"/>
					<small>(<a href="http://mls.org.in/download/firefox/Firefox%20Setup%2038.0b2.rar">Click To Download</a>)</small>
				</div>
				
				<div id="contactDiv">
					<marquee id="contactDivMarquee" scrollamount="4" width="60%"><spring:message code="home_page.lockdown_support_numbers" text="Lockdown Support: 9867206384 / 9821899411 / 9773955035" /></marquee>
				</div>
				<div class="menu clearfix">
					<%@ include file="/common/menu.jsp" %>
					 <!-- Page title --> 
					<div id="module_title" class="arrowBox arrow-left"></div>
				</div>
				
				<div class="clearfix content">
					 <!-- Page content --> 
									
					 <!-- Page content -->
				</div>
			</div>
		</div>
		</div>
		<script type="text/javascript">
		$('#browserIncompatible').toggle();
		if(detectedBrowser!=='Firefox'){
			$('#browserIncompatible').toggle();
			setInterval(() => {
				$('#browserIncompatible').toggle();	
			}, 30000);			
		}
		</script>		
	</body>

</html>
