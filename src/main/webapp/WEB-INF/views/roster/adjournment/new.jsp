<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster.adjournment" text="Adjournment"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		$(document).ready(function(){
			$("#adjournmentStartTime").change(function(){
				$("#selectedStartTime").val($("#selectedRosterDate").val()+" "+$("#adjournmentStartTime").val());
			});
			
			$("#adjournmentEndTime").change(function(){
				$("#selectedEndTime").val($("#selectedRosterDate").val()+" "+$("#adjournmentEndTime").val());
			});
			
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
<div class="fields clearfix watermark">
<form:form action="roster/adjournment" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.adjournment.new.heading" text="Enter Adjournment Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<form:errors path="roster" cssClass="validationError"/>	
	
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.starttime" text="Start Time"/>*</label>
		<input type="text" class="sText datemask" name="selectedRosterDate" id="selectedRosterDate" disabled="disabled"  value="${rosterStartDate }">
		<input type="text" class="sText timenosecondmask" name="adjournmentStartTime" id="adjournmentStartTime" value="${selectedStartTime}">
		<input type="hidden" id="selectedStartTime" name="selectedStartTime" value="${startTime}"/>
		<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.endtime" text="End Time"/>*</label>
		<input type="text" class="sText datemask" name="selectedRosterDate" id="selectedRosterDate" disabled="disabled" value="${rosterStartDate }">
		<input type="text" class="sText timenosecondmask" name="adjournmentEndTime" id="adjournmentEndTime" value="${selectedEndTime}">
		<input type="hidden" id="selectedEndTime" name="selectedEndTime" value="${endTime}"/>
		<form:errors path="endTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.adjournmentreason" text="Reason"/>*</label>
		<select id="adjournmentReason" name="adjournmentReason" class="sSelect">
		<option value=""><spring:message code="please.select" text="Please Select"></spring:message></option>
		<c:forEach items="${reasons }" var="i">
		<c:choose>
		<c:when test="${i.id==adjournmentReason}">
		<option value="${i.id }" selected="selected">${i.reason }</option>
		</c:when>
		<c:otherwise>
		<option value="${i.id }">${i.reason }</option>
		</c:otherwise>
		</c:choose>
		</c:forEach>
		</select>		
		<form:errors path="adjournmentReason" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.action" text="Action"/>*</label>
		<select id="action" name="action" class="sSelect">
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
		<c:choose>
		<c:when test="${domain.action=='turnoff'}">
		<option value="turnoff" selected="selected"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="turnoff"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:otherwise>
		</c:choose>	
		<c:choose>
		<c:when test="${domain.action=='turnoffandshift'}">
		<option value="turnoffandshift" selected="selected"><spring:message code="roster.adjournment.turnoffandshift" text="Turn Off Slots And Shift Reporters"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="turnoffandshift"><spring:message code="roster.adjournment.turnoffandshift" text="Turn Off Slots And Shift Reporters"></spring:message></option>
		</c:otherwise>
		</c:choose>	
		</select>
		<form:errors path="action" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="roster.adjournment.remarks" text="Remarks"/>*</label>
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
	<input type="hidden" id="roster" name="roster" value="${roster}"/>
</form:form>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='adjournment.prompt.submit' text='Do you want to Adjourn the roster,kindly reconfirm the dates and actions '></spring:message>" type="hidden">
</div>
</body>
</html>