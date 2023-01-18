<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="group.rotationorder.edit" text="Question Dates"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var count=$('#dateCount').val();
	var linkId;	
	$(document).ready(function(){
		initControls();
		
		var recordId = $('#domainId').val();
		$('#key').val(recordId);	
		
		$(".datemask").mask("99/99/9999");
		
		for(var i=0;i<count;i++){			
			if(!$('#date'+i).is(':checked')){				
				$('#submissionDate'+i).val("");
				$('#submissionDate'+i).attr('disabled', true);
				$('#submissionDate'+i).css('backgroundColor', '#00FFFF)');
				
				$('#lastSendingDateToDepartment'+i).val("");
				$('#lastSendingDateToDepartment'+i).attr('disabled', true);
				
				$('#lastReceivingDateFromDepartment'+i).val("");
				$('#lastReceivingDateFromDepartment'+i).attr('disabled', true);
				
				$('#lastDateForChangingDepartment'+i).val("");
				$('#lastDateForChangingDepartment'+i).attr('disabled', true);
				
				$('#yaadiPrintingDate'+i).val("");
				$('#yaadiPrintingDate'+i).attr('disabled', true);
				
				$('#yaadiReceivingDate'+i).val("");
				$('#yaadiReceivingDate'+i).attr('disabled', true);
				
				$('#suchhiPrintingDate'+i).val("");
				$('#suchhiPrintingDate'+i).attr('disabled', true);
				
				$('#suchhiReceivingDate'+i).val("");
				$('#suchhiReceivingDate'+i).attr('disabled', true);
				
				$('#suchhiDistributionDate'+i).val("");
				$('#suchhiDistributionDate'+i).attr('disabled', true);
				
				$('#speakerSendingDate'+i).val("");
				$('#speakerSendingDate'+i).attr('disabled', true);
			};			
		};
		
		var y = '${domain.year}';
		$('#year').val(y.toString());
		
		$(".sOption").click(function(){
			var j = this.id.substr(this.id.length - 1);				
			if($('#date'+j).is(':checked')){
				$('#submissionDate'+j).attr('disabled', false);
				$('#submissionDate'+j).val($('#submissionDateHidden'+j).val());
				
				$('#lastSendingDateToDepartment'+j).attr('disabled', false);
				$('#lastSendingDateToDepartment'+j).val($('#lastSendingDateToDepartmentHidden'+j).val());
				
				$('#lastReceivingDateFromDepartment'+j).attr('disabled', false);
				$('#lastReceivingDateFromDepartment'+j).val($('#lastReceivingDateFromDepartmentHidden'+j).val());
				
				$('#lastDateForChangingDepartment'+j).attr('disabled', false);
				$('#lastDateForChangingDepartment'+j).val($('#lastDateForChangingDepartmentHidden'+j).val());
				
				$('#yaadiPrintingDate'+j).attr('disabled', false);
				$('#yaadiPrintingDate'+j).val($('#yaadiPrintingDateHidden'+j).val());
				
				$('#yaadiReceivingDate'+j).attr('disabled', false);
				$('#yaadiReceivingDate'+j).val($('#yaadiReceivingDateHidden'+j).val());
				
				$('#suchhiPrintingDate'+j).attr('disabled', false);
				$('#suchhiPrintingDate'+j).val($('#suchhiPrintingDateHidden'+j).val());
				
				$('#suchhiReceivingDate'+j).attr('disabled', false);
				$('#suchhiReceivingDate'+j).val($('#suchhiReceivingDateHidden'+j).val());
				
				$('#suchhiDistributionDate'+j).attr('disabled', false);
				$('#suchhiDistributionDate'+j).val($('#suchhiDistributionDateHidden'+j).val());
				
				$('#speakerSendingDate'+j).attr('disabled', false);
				$('#speakerSendingDate'+j).val($('#speakerSendingDateHidden'+j).val());
			}
			else{
				$('#submissionDate'+j).attr('disabled', true);
				$('#submissionDate'+j).val("");	
					
				$('#lastSendingDateToDepartmentHidden'+j).val($('#lastSendingDateToDepartment'+j).val());
				$('#lastSendingDateToDepartment'+j).attr('disabled', true);
				$('#lastSendingDateToDepartment'+j).val("");
					
				$('#lastReceivingDateFromDepartmentHidden'+j).val($('#lastReceivingDateFromDepartment'+j).val());
				$('#lastReceivingDateFromDepartment'+j).attr('disabled', true);
				$('#lastReceivingDateFromDepartment'+j).val("");
				
				$('#lastDateForChangingDepartmentHidden'+j).val($('#lastDateForChangingDepartment'+j).val());
				$('#lastDateForChangingDepartment'+j).attr('disabled', true);
				$('#lastDateForChangingDepartment'+j).val("");
					
				$('#yaadiPrintingDateHidden'+j).val($('#yaadiPrintingDate'+j).val());
				$('#yaadiPrintingDate'+j).attr('disabled', true);
				$('#yaadiPrintingDate'+j).val("");
					
				$('#yaadiReceivingDateHidden'+j).val($('#yaadiReceivingDate'+j).val());
				$('#yaadiReceivingDate'+j).attr('disabled', true);
				$('#yaadiReceivingDate'+j).val("");
					
				$('#suchhiPrintingDateHidden'+j).val($('#suchhiPrintingDate'+j).val());
				$('#suchhiPrintingDate'+j).attr('disabled', true);
				$('#suchhiPrintingDate'+j).val("");
					
				$('#suchhiReceivingDateHidden'+j).val($('#suchhiReceivingDate'+j).val());
				$('#suchhiReceivingDate'+j).attr('disabled', true);
				$('#suchhiReceivingDate'+j).val("");
					
				$('#suchhiDistributionDateHidden'+j).val($('#suchhiDistributionDate'+j).val());
				$('#suchhiDistributionDate'+j).attr('disabled', true);
				$('#suchhiDistributionDate'+j).val("");
				
				$('#sspeakerSendingDateHidden'+j).val($('#speakerSendingDate'+j).val());
				$('#speakerSendingDate'+j).attr('disabled', true);
				$('#speakerSendingDate'+j).val("");
			};			
		});
		
		$('#submit').click(function(){
			var flag=0;
			for(var k=0;k<count;k++){
				if($('#date'+k).is(':checked')){
					flag=1;
				};
			}
			if(flag==0){
				$.prompt($('#errorMsg').val());
				return false;
			};				
		});	    
	});	
	</script>
	 
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix">
<form:form action="group/rotationorder" method="POST" modelAttribute="domain">
	<div style="overflow: auto;">
		<%@ include file="/common/info.jsp" %>
		<h2><spring:message code="generic.edit.heading" text="Enter Details"/></h2>
		<form:errors path="version" cssClass="validationError"/>
		
		<p>
			<label class="small"><spring:message code="group.number" text="Group" /></label>
			<form:input cssClass="sText" path="number" readonly="true"/>
		</p>	
		<p>
			<label class="small"><spring:message code="group.houseType" text="House Type" /></label>
			<form:input cssClass="sText" path="houseType.name" readonly="true"/>
		</p>
		<p>
			<label class="small"><spring:message code="group.year" text="Year" /></label>
			<form:input cssClass="sText" id="year" path="year" readonly="true"/>
		</p>
		<p>
			<label class="small"><spring:message code="group.sessionType" text="Session Type" /></label>
			<form:input cssClass="sText" path="sessionType.sessionType" readonly="true"/>
		</p>	
		<p>
			<table>		
				<tr>
					<td valign="middle">				
						<label class="small"><spring:message code="group.rotationorder.selectDates" text="Rotation Order"/></label>
					</td>
					<td>
						<table class="uiTable">
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.select" text="Select"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<th>		
									<c:choose>
										<c:when test="${empty selects}">
											<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true">
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${selects[i.count-1]=='true'}">
													<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true" checked="checked">
												</c:when>
												<c:otherwise>
													<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true">
												</c:otherwise>
											</c:choose>				
										</c:otherwise>
									</c:choose>		
									<spring:message code="group.rotationorder.selectCriteria" text="Date"/> ${i.count}
								</th>
								</c:forEach>
							</tr>						
							<tr></tr>
	
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.anweringDate" text="Answering Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>		
									<input style="width: 70px;" class="datemask sText" type="text"  name="answeringDate${i.count-1}" value="${answeringDates[i.count-1]}">		
									<input type="hidden" name="originalAnsweringDate${i.count-1}" value="${originalAnsweringDates[i.count-1]}" readonly="readonly">
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.submissionDate" text="Last Submission Date"/></label></th>							
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>		
									<input style="width: 70px;" class="datemask sText" type="text" id="submissionDate${i.count-1}" name="submissionDate${i.count-1}" value="${submissionDates[i.count-1]}">	
									<input type="hidden" id="submissionDateHidden${i.count-1}" value="${submissionDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<c:if test="${houseType=='lowerhouse'}">
								<tr>
									<th><label style="width: 250px;"><spring:message code="group.rotationorder.speakerSendingDate" text="Last Sending Date to Speaker"/></label></th>
									<c:forEach begin="1" end="${dateCount}" varStatus="i">
									<td>
										<input style="width: 70px;" class="datemask sText" type="text" id="speakerSendingDate${i.count-1}" name="speakerSendingDate${i.count-1}" value="${speakerSendingDates[i.count-1]}">	
										<input type="hidden" id="speakerSendingDateHidden${i.count-1}" value="${speakerSendingDates[i.count-1]}">	
									</td>
									</c:forEach>
								</tr>			
							</c:if>			
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.lastSendingDateToDepartment" text="Last Sending Date To Department"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="lastSendingDateToDepartment${i.count-1}" name="lastSendingDateToDepartment${i.count-1}" value="${lastSendingDatesToDepartment[i.count-1]}">	
									<input type="hidden" id="lastSendingDateToDepartmentHidden${i.count-1}" value="${lastSendingDatesToDepartment[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.lastReceivingDateFromDepartment" text="Last Recieving Date From Department"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="lastReceivingDateFromDepartment${i.count-1}" name="lastReceivingDateFromDepartment${i.count-1}" value="${lastReceivingDatesFromDepartment[i.count-1]}">	
									<input type="hidden" id="lastReceivingDateFromDepartmentHidden${i.count-1}" value="${lastReceivingDatesFromDepartment[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.lastDateForChangingDepartment" text="Last Date For Changing Department"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="lastDateForChangingDepartment${i.count-1}" name="lastDateForChangingDepartment${i.count-1}" value="${lastDatesForChangingDepartment[i.count-1]}">	
									<input type="hidden" id="lastDateForChangingDepartmentHidden${i.count-1}" value="${lastDatesForChangingDepartment[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.yaadiPrintingDate" text="Yaadi Printing Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="yaadiPrintingDate${i.count-1}" name="yaadiPrintingDate${i.count-1}" value="${yaadiPrintingDates[i.count-1]}">	
									<input type="hidden" id="yaadiPrintingDateHidden${i.count-1}" value="${yaadiPrintingDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.yaadiReceivingDate" text="Yaadi Receiving Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="yaadiReceivingDate${i.count-1}" name="yaadiReceivingDate${i.count-1}" value="${yaadiReceivingDates[i.count-1]}">	
									<input type="hidden" id="yaadiReceivingDateHidden${i.count-1}" value="${yaadiReceivingDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.suchhiPrintingDate" text="Suchi Printing Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="suchhiPrintingDate${i.count-1}" name="suchhiPrintingDate${i.count-1}" value="${suchhiPrintingDates[i.count-1]}">	
									<input type="hidden" id="suchhiPrintingDateHidden${i.count-1}" value="${suchhiPrintingDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.suchhiReceivingDate" text="Suchi Receiving Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="suchhiReceivingDate${i.count-1}" name="suchhiReceivingDate${i.count-1}" value="${suchhiReceivingDates[i.count-1]}">	
									<input type="hidden" id="suchhiReceivingDateHidden${i.count-1}" value="${suchhiReceivingDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.suchhiDistributionDate" text="Suchhi Distribution Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>							
									<input style="width: 70px;" class="datemask sText" type="text" id="suchhiDistributionDate${i.count-1}" name="suchhiDistributionDate${i.count-1}" value="${suchhiDistributionDates[i.count-1]}">	
									<input type="hidden" id="suchhiDistributionDateHidden${i.count-1}" value="${suchhiDistributionDates[i.count-1]}">	
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							<tr>
								<th><label style="width: 250px;"><spring:message code="group.rotationorder.displayAnsweringDate" text="Display Answering Date"/></label></th>
								<c:forEach begin="1" end="${dateCount}" varStatus="i">
								<td>		
									<input style="width: 70px;" class="datemask sText" type="text"  name="displayAnsweringDates${i.count-1}" id="displayAnsweringDates${i.count-1}" value="${displayAnsweringDates[i.count-1]}">		
									<input type="hidden" id="displayAnsweringDates${i.count-1}" value="${displayAnsweringDates[i.count-1]}">
								</td>
								</c:forEach>
							</tr>						
							<tr></tr>
							
							
						
						</table>
					</td>
				</tr>
			</table>
			<!-- <a class= "link" id="ajaxLink" href='#'> fancybox ajax</a> -->	
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>
		
		<input type="hidden" name="dateCount" value="${dateCount}">
		<input type="hidden" id="errorMsg" value='<spring:message code="group.rotationorder.errormsg"/>'>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="dateCount" value="${dateCount}" />
		<input type="hidden" id="domainId" value="${domain.id}" />
		<form:hidden path="version"/>
		<form:hidden path="id"/>
		<form:hidden path="locale"/>
	</div>	
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>