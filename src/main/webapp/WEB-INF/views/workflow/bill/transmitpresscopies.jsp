<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function viewBillDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseTypeType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&deviceType="+$("#typeOfSelectedDeviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='bill/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:1000,height:750});
		},'html');	
	}
	$(document).ready(function(){
		$('#viewBillDetails').click(function() {			
			viewBillDetail($('#deviceId').val());
		});
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="workflow/bill/transmitpresscopies" method="PUT">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>		
	</c:when>
	<c:otherwise>
	<spring:message code="transmitpresscopies.heading" text="Acknowledge the Endorsement Copies"/>
	</c:otherwise>
	</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p>
		<label class="small"><spring:message code="transmitpresscopies.houseType" text="House Type"/></label>
		<input id="formattedSelectedHouseType" name="formattedSelectedHouseType" value="${selectedHouseType.getName()}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedHouseTypeType" name="selectedHouseTypeType" value="${selectedHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.year" text="Year"/></label>
		<input id="formattedSelectedYear" name="formattedSelectedYear" value="${formattedSelectedYear}" class="sInteger" readonly="readonly"/>
		<input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/>	
	</p>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"  readonly="readonly"/>
		<a href="#" id="viewBillDetails"><spring:message code="transmitpresscopies.viewBillDetails" text="View Bill"/></a>
		<input type="hidden" id="deviceId" name="deviceId" value="${selectedBillId}"/>
	</p>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.status" text="Status"/></label>
		<input id="formattedSelectedStatus" name="formattedSelectedStatus" value="${selectedStatus.getName()}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedStatusType" name="selectedStatusType" value="${selectedStatus.getType()}"/>
	</p>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.houseRound" text="House Round"/></label>
		<input id="formattedHouseType" name="formattedSelectedHouseRound" value="${formattedSelectedHouseRound}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedHouseRound" name="selectedHouseRound" value="${selectedHouseRound}"/>
	</p>
	<c:choose>
		<c:when test="${workflowType=='SEND_FOR_ENDORSEMENT_WORKFLOW' or workflowType=='TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW'}">
			<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.endorsementCopies" text="Endorsement Copies" /></label></legend>
			<div>		
				<%-- <fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.${pressCopyHeader}' /></label></legend> --%>
					<p>
						<label class="small"><spring:message code='bill.endorsementCopyEnglish' text="English Endorsement Copy"/></label>
						<c:choose>		
							<c:when test="${not empty endorsementCopyEnglish}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="endorsementCopyEnglish" />
									<jsp:param name="filetag" value="${endorsementCopyEnglish}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
					<p>
						<label class="small"><spring:message code='bill.endorsementCopyMarathi' text="Marathi Endorsement Copy"/></label>
						<c:choose>		
							<c:when test="${not empty endorsementCopyMarathi}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="endorsementCopyMarathi" />
									<jsp:param name="filetag" value="${endorsementCopyMarathi}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
					<p>
						<label class="small"><spring:message code='bill.endorsementCopyHindi' text="Hindi Endorsement Copy"/></label>
						<c:choose>		
							<c:when test="${not empty endorsementCopyHindi}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="endorsementCopyHindi" />
									<jsp:param name="filetag" value="${endorsementCopyHindi}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
				<!-- </fieldset> -->			
			</div>
			</fieldset>	
		</c:when>
		<c:when test="${workflowType=='TRANSMIT_PRESS_COPIES_WORKFLOW'}">
			<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.pressCopies" text="Press Copies" /></label></legend>
			<div>		
				<%-- <fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.${pressCopyHeader}' /></label></legend> --%>
					<p>
						<label class="small"><spring:message code='bill.pressCopyEnglish' text="English Press Copy"/></label>
						<c:choose>		
							<c:when test="${not empty pressCopyEnglish}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="pressCopyEnglish" />
									<jsp:param name="filetag" value="${pressCopyEnglish}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
					<p>
						<label class="small"><spring:message code='bill.pressCopyMarathi' text="Marathi Press Copy"/></label>
						<c:choose>		
							<c:when test="${not empty pressCopyMarathi}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="pressCopyMarathi" />
									<jsp:param name="filetag" value="${pressCopyMarathi}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
					<p>
						<label class="small"><spring:message code='bill.pressCopyHindi' text="Hindi Press Copy"/></label>
						<c:choose>		
							<c:when test="${not empty pressCopyHindi}">
								<jsp:include page="/common/file_open.jsp">
									<jsp:param name="fileid" value="pressCopyHindi" />
									<jsp:param name="filetag" value="${pressCopyHindi}" />
									<jsp:param name="isRemovable" value="false" />
								</jsp:include>
							</c:when>										
						</c:choose>								
					</p>
				<!-- </fieldset> -->			
			</div>
			</fieldset>	
		</c:when>
	</c:choose>	
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<p>
	<label class="small"><spring:message code="transmitpresscopies.isHardCopyReceived" text="Is Hard Copy Received?"/>*</label>
	<input id="isHardCopyReceived" name="isHardCopyReceived" class="sText" readonly="readonly" value="<spring:message code='generic.${isHardCopyReceived}'/>">
	</p>	
	<p>
		<label class="small"><spring:message code="transmitpresscopies.dateOfHardCopyReceived" text="Date Of Hard Copy Received"/>*</label>
		<input type="text" id="dateOfHardCopyReceived" name="dateOfHardCopyReceived" class="datemask sText" value="${dateOfHardCopyReceived}"/>			
	</p>	
	<p>
	<label class="small"><spring:message code="transmitpresscopies.acknowledgementDecision" text="Acknowledgement Decision?"/>*</label>
	<input id="formattedAcknowledgementDecision" name="formattedAcknowledgementDecision" class="sText" readonly="readonly" value="${formattedAcknowledgementDecision}">
	</p>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
		</p>
	</div>	
	</c:when>
	<c:otherwise>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.isHardCopyReceived" text="Is Hard Copy Received?"/>*</label>
		<select id="isHardCopyReceived" name="isHardCopyReceived" class="sSelect">
			<option value="yes"><spring:message code='generic.yes' text='Yes'/></option>
			<option value="no"><spring:message code='generic.no' text='No'/></option>
		</select>		
	</p>	
	<p>
		<label class="small"><spring:message code="transmitpresscopies.dateOfHardCopyReceived" text="Date Of Hard Copy Received"/>*</label>
		<input type="text" id="dateOfHardCopyReceived" name="dateOfHardCopyReceived" class="datemask sText" value="${dateOfHardCopyReceived}"/>			
	</p>
	<p>
		<label class="small"><spring:message code="transmitpresscopies.acknowledgementDecision" text="Acknowledgement Decision?"/>*</label>
		<select id="acknowledgementDecision" name="acknowledgementDecision" class="sSelect">
			<c:forEach var="i" items="${acknowledgementDecisionStatuses}">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</select>		
	</p>
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	</c:otherwise>
	</c:choose>	
	<input type="hidden" id="requestReceivedOnDate" name="requestReceivedOnDate" value="${requestReceivedOnDate }">
	<input type="hidden" id="printRequisitionId" name="printRequisitionId" value="${printRequisitionId}">
	<input type="hidden" id="task" name="task" value="${task}">	
	<input type="hidden" id="workflowDetailsId" name="workflowDetailsId" value="${workflowDetailsId}">
	<input type="hidden" id="workflowdetails" name="workflowdetails" value="${workflowdetails}">	
	<input type="hidden" id="requestReceivedOn" name="requestReceivedOn" value="${requestReceivedOn }">		
	<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
	<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
</form:form>
</div>
</body>
</html>