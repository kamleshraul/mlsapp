<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title><spring:message code="pushmessage" text="Push Message"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				$('#key').val('');	
				$('.datemask').focus(function(){					
					$(".datemask").mask("99/99/9999");				
				});
			});		
		</script>
		
		<style type="text/css">
			.customTextArea{
				border: 1px solid #011B80;
				border-radius: 5px;
				min-width: 500px;
				max-width: 700px;
				min-height: 250px;
			}
			.customText{
				border-radius: 5px;
				min-width: 500px;
				max-width: 700px;
				border: 1px solid #011B80;
			}
			.customText:focus{
				/* background: #77BFBD;
				filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#77BFBD', endColorstr='#FFFFFF');
				background: -webkit-gradient(linear, left top, left bottom, from(#77BFBD), to(#FFFFFF));
				background: -moz-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -ms-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -o-linear-gradient(top,  #77BFBD,  #FFFFFF); */
				background: #DDE0ED;
			}
			.customTextArea:focus{
				/* background: #77BFBD;
				filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#77BFBD', endColorstr='#FFFFFF');
				background: -webkit-gradient(linear, left top, left bottom, from(#77BFBD), to(#FFFFFF));
				background: -moz-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -ms-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -o-linear-gradient(top,  #77BFBD,  #FFFFFF); */
				background: #DDE0ED;
			}
		</style>
	</head>
	
	<body>	
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields clearfix">
		<form:form action="pushmessage" method="PUT"  modelAttribute="domain">			
			<%@ include file="/common/info.jsp" %>
			<form:errors path="version" cssClass="validationError"/>	
			<p> 
				<form:hidden path="locale"/>
				<form:errors path="locale" cssClass="validationError"/>	
			</p>	 
			<p> 
				<label class="small"><spring:message code="pushmessage.devicenumber" text="Device Number"/></label>
				<form:input cssClass="sText customText" path="deviceNumber" readonly="true"/>
				<form:errors path="deviceNumber" cssClass="validationError"/>	
			</p>
			<p> 
				<label class="small"><spring:message code="pushmessage.recepient" text="Recepient"/></label>
				<form:input cssClass="sText customText" path="recepientName" readonly="true"/>
				<form:errors path="recepientName" cssClass="validationError"/>	
			</p>
			<p> 
				<label class="labeltop"><spring:message code="pushmessage.message" text="Message"/></label>
				<form:textarea cssClass="customTextArea" path="message" rows="10" cols="100" readonly="true" />
				<form:errors path="message" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="message.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseTypeType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="message.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="message.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">
				<form:errors path="sessionType" cssClass="validationError"/>	
			</p>				
			
			<p style="display:none;">
				<label class="small"><spring:message code="message.deviceType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
				<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">
				<form:errors path="deviceType" cssClass="validationError"/>	
			</p>
			
			<c:if test="${usergroupType=='developer'}">
				<div class="fields">
					<h2></h2>
					<p class="tright">
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
					</p>
				</div>	
			</c:if>
			<form:hidden path="version" />
			<form:hidden path="id"/>
			
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
			
			<input type="hidden" id="device" name="device" value="${device}" />
			
			<input type="hidden" id="senderUserGroup" name="senderUserGroup" value="${senderUserGroup}" />
			<input type="hidden" id="senderUserGroupType" name="senderUserGroupType" value="${senderUserGroupType}" />
			<input type="hidden" id="senderUserName" name="senderUserName" value="${senderUserName}" />
			<input type="hidden" id="senderName" name="senderName" value="${senderName}" />
			
			<input type="hidden" id="recepientUserName" name="recepientUserName" value="${recepientUserName}" />
			<input type="hidden" id="recepientUserGroup" name="recepientUserGroup" value="${recepientUserGroup}" />	
			<input type="hidden" id="recepientUserGroupType" name="recepientUserGroupType" value="${recepientUserGroupType}" />
			<input type="hidden" id="mRole" name="mRole" value="${role}" />
			<form:hidden path="sendDate"/>
			
		</form:form>
		</div>	
	</body>
</html>