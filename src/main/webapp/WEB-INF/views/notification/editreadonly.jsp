<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="notification" text="Notification"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		//populate formatted sent on date
		var formattedSentOnMoment = moment($('#notificationSentOn').text());
		formattedSentOnMoment.locale('mr');
		$('#notificationSentOn').text(formattedSentOnMoment.format('DD/MM/YYYY hh:mm:ss a'));
		
		//alert($('#selectedNotificationType').val());
		if($('#selectedNotificationType').val()=='outbox') {
			$('#senderNameP').hide();
			$('#receiverNameP').show();
		} else {
			$('#senderNameP').show();
			$('#receiverNameP').hide();
		}
	});		
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
	<h2><spring:message code="generic.view.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${notificationVO.id}]
	</h2>		
	<p id="senderNameP">
		<label class="small"><spring:message code="notification.sender" text="Sender Name"/></label>
		<label id="notificationSender" style="margin-left: -10px;">${notificationVO.senderName}</label>
	</p>
	<p id="receiverNameP">
		<label class="small"><spring:message code="notification.receiver" text="Receiver Name"/></label>
		<label id="notificationReceiver" style="margin-left: -10px;">${notificationVO.receiverName}</label>
	</p>
	<p style="margin-top:10px;">
    	<label class="small"><spring:message code="notification.sentOn" text="Sent On"/>:</label>
    	<label id="notificationSentOn" style="margin-left: -10px;">${notificationVO.sentOn}</label>
    </p>
	<c:choose>
		<c:when test="${notificationVO.title!=notificationVO.message}">
		     <p style="margin-top:10px;">
		     	<label class="centerlabel"><spring:message code="notification.title" text="Title"/>:</label>
		     	<textarea id="notificationTitle" rows="2" cols="50" readonly="readonly">${notificationVO.title}</textarea>
		     </p>
		     <p>
		     	<label class="wysiwyglabel"><spring:message code="notification.message" text="Message"/>:</label>
		     	<textarea id="notificationMessage" class="wysiwyg" readonly="readonly">${notificationVO.message}</textarea>
		     </p>
		</c:when>
		<c:otherwise>
			 <p style="margin-top:10px;">
	         	<label class="centerlabel"><spring:message code="notification.message" text="Message"/>:</label>
	         	<textarea id="notificationMessage" rows="3" cols="50" readonly="readonly">${notificationVO.title}</textarea>
	         </p>
		</c:otherwise>
	</c:choose>
	<p style="margin-top:10px;">
		<label class="small"><spring:message code="notification.markedAsReadByReceiver" text="Is Marked As Read?"/></label>
		<c:choose>
			<c:when test="${notificationVO.markedAsReadByReceiver==true}">
				<label style="margin-left: -10px;"><spring:message code="generic.yes" text="Yes"/></label>
			</c:when>
			<c:otherwise>
				<label style="margin-left: -10px;"><spring:message code="generic.no" text="No"/></label>
			</c:otherwise>
		</c:choose>		
	</p>
</div>	
</body>
</html>