<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="proceeding.notifyPendingTurn" text="Notify Pending Turn"/></title>
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();	
				
				$('#currentPage').val("notifyPendingTurn");
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
					//if($('#rosterHandledBy').val().contains("admin"))
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
		<form action="proceeding/notifyPendingTurn" method="POST">
			<%@ include file="/common/info.jsp" %>
			<h2><spring:message code="proceeding.notifyPendingTurn" text="Notify Pending Turn"/></h2>
			<p style="margin-top:10px;">
				<label class="centerlabel"><spring:message code="notification.rosterHandledBy" text="Chief Reporter"/></label>
				<textarea id="rosterHandledBy" name="rosterHandledBy" readonly="readonly" rows="2" cols="50">${rosterHandledBy}</textarea>
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
			<%-- <input type="hidden" id="rosterHandledBy" name="rosterHandledBy" value="${rosterHandledBy}"> --%>
		</form>	
		</div>		
	</body>
</html>