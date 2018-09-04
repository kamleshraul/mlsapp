<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster.slot" text="Slot"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		$(document).ready(function(){
			$("#submit").click(function(){
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
						if(v){
							$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				        	$.post($('form').attr('action'),  
				    	            $("form").serialize(),  
				    	            function(data){
				       					$('.tabContent').html(data);
				       					$('html').animate({scrollTop:0}, 'slow');
				       				 	$('body').animate({scrollTop:0}, 'slow');	
				    					$.unblockUI();	   				 	   				
				    	            }).fail(function (jqxhr, textStatus, err) {
				    	            	$.unblockUI();
				    	            	$("#error_p").html("Server returned an error\n" + err +
			                                    "\n" + textStatus + "\n" +
			                                    "Please try again later.\n"+jqxhr.status+"\n"+jqxhr.statusText).css({'color':'red', 'display':'block'});
				    	            	
				    	            	scrollTop();
		                            });
				        }
					}
				});
				return false;
				});
			
		});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="roster/slot" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.slot.new.heading" text="Enter Slot Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
		<label class="small"><spring:message code="roster.slot.reporter" text="Reporter"/>*</label>
		<select id="user" name="user" class="sSelect">
		<c:forEach items="${users}"  var="i">
		<c:choose>
		<c:when test="${domain.reporter.user.id==i.id }">
		<option selected="selected" value="${i.id}">${i.credential.username }(${i.findFullNameForRis()})</option>
		</c:when>
		<c:otherwise>
		<option  value="${i.id}">${i.credential.username }(${i.findFullNameForRis()})</option>		
		</c:otherwise>		
		</c:choose>
		</c:forEach>
		</select>		
		<form:errors path="reporter" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="small"><spring:message code="roster.slot.starttime" text="Start Time"/>*</label>
		<input type="text" class="sText datetimenosecondmask" name="selectedStartTime" id="selectedStartTime" value="${startTime }">
		<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.slot.endtime" text="End Time"/>*</label>
		<input type="text" class="sText datetimenosecondmask" name="selectedEndTime" id="selectedEndTime" value="${endTime }">
		<form:errors path="endTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.slot.turnedoff" text="Turn Off"/>*</label>
		<form:checkbox cssClass="sCheck" path="turnedoff"/>
		<form:errors path="endTime" cssClass="validationError"/>
	</p>		
	
	<p>
		<label class="wysiwyglabel"><spring:message code="roster.slot.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>	
	
		
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>		
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="name"/>
	<input type="hidden" id="roster" name="roster" value="${roster}"/>
	<form:hidden path="completed"/>
</form:form>
<input id="submissionMsg" value="<spring:message code='slot.prompt.submit' text='Do you want to update? '></spring:message>" type="hidden">
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
</div>
</body>
</html>