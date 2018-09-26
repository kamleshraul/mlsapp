<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="user.sendNotification" text="Send Notification"/></title>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();	
				
				$('#currentPage').val("sendNotification");
				//alert($('#isVolatile').val());
				if($('#isVolatile').val()=='true') {
					$('#isVolatileCheckbox').attr('checked', 'checked');
					$('#isVolatile').val(true);
				} else {
					$('#isVolatileCheckbox').removeAttr('checked');
					$('#isVolatile').val(false);
				}				
				
				$('#isVolatileCheckbox').change(function() {
					if($(this).is(':checked')) {
						$('#isVolatile').val(true);
					} else {
						$('#isVolatile').val(false);
					}
				});
				
				$('#submitNotification').click(function() {
					$(".wysiwyg").each(function(){
						var wysiwygVal=$(this).val().trim();
						if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
							$(this).val("");
						}
					});
					if($('#notificationTitle').val()=="") {
						$.prompt("Please enter notification title");
						return false;
					}
					if($('#notificationMessage').val()=="") {
						$('#notificationMessage').val($('#notificationTitle').val());
						//$('#notificationMessage').wysiwyg('setContent', '<p>'+$('#notificationTitle').val()+'</p>');
					}
					//if($('#usernames').val().contains("admin"))
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post($('form').attr('action'), $("form").serialize(), function(data){
       					$('.tabContent').html(data);
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');
    					$.unblockUI();	   				 	   				
	    	        });
				});
			});		
		</script>
	</head>
	<body>				
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields">		
		<form action="user/sendNotification" method="POST">
			<%@ include file="/common/info.jsp" %>
			<h2><spring:message code="user.sendNotification" text="Send Notification"/></h2>
			<p style="margin-top:10px;">
				<label class="centerlabel"><spring:message code="notification.usernames" text="Receiver's Names"/></label>
				<textarea id="usernames" name="usernames" rows="2" cols="50">${usernames}</textarea>
			</p>
			<p>
		     	<label class="centerlabel"><spring:message code="notification.title" text="Title"/>:</label>
		     	<textarea id="notificationTitle" name="notificationTitle" rows="2" cols="50">${notificationTitle}</textarea>
		    </p>
		    <p>
		     	<label class="wysiwyglabel"><spring:message code="notification.message" text="Message"/>:</label>
		     	<textarea id="notificationMessage" name="notificationMessage" class="wysiwyg">${notificationMessage}</textarea>
		    </p>
		    <p>
				<label class="small"><spring:message code="notification.isVolatile" text="is notification volatile?"/></label>
				<input type="checkbox" id="isVolatileCheckbox" class="sCheck" value="${isVolatile}">
				<input type="hidden" id="isVolatile" name="isVolatile" value="${isVolatile}">
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submitNotification" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				</p>
			</div>
			<%-- <input type="hidden" id="usernames" name="usernames" value="${usernames}"> --%>
		</form>	
		</div>		
	</body>
</html>