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
			$.fancybox.open(data,{autoSize:false,width:800,height:700});
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
<form:form action="workflow/bill/layletter" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>		
	</c:when>
	<c:otherwise>
	<spring:message code="layingletter.heading" text="Acknowledge the Laying Letter"/>
	</c:otherwise>
	</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p>
		<label class="small"><spring:message code="layingletter.houseType" text="House Type"/></label>
		<input id="formattedSelectedHouseType" name="formattedSelectedHouseType" value="${selectedHouseType.getName()}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedHouseTypeType" name="houseType" value="${selectedHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.year" text="Year"/></label>
		<input id="formattedSelectedYear" name="formattedSelectedYear" value="${formattedSelectedYear}" class="sInteger" readonly="readonly"/>
		<input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/>	
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"  readonly="readonly"/>
		<a href="#" id="viewBillDetails"><spring:message code="layingletter.viewBillDetails" text="View Bill"/></a>
		<input type="hidden" id="deviceId" name="deviceId" value="${selectedBillId}"/>
	</p>	
	<%-- <p>
		<label class="small"><spring:message code="layingletter.status" text="Status"/></label>
		<input id="formattedSelectedStatus" name="formattedSelectedStatus" value="${selectedStatus.getName()}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedStatusType" name="status" value="${selectedStatus.getType()}"/>
	</p> --%>
	<p>
		<label class="small"><spring:message code="layingletter.houseRound" text="House Round"/></label>
		<input id="formattedHouseType" name="formattedSelectedHouseRound" value="${formattedSelectedHouseRound}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedHouseRound" name="houseRound" value="${selectedHouseRound}"/>
	</p>	
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<p>
		<label class="small"><spring:message code="layingletter.layingDate" text="Laying Date"/></label>
		<input id="layingDate" name="setlayingDate" value="${layingDate}" class="datemask sText" readonly="readonly"/>			
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.letter" text="Letter"/></label>
		<c:choose>		
			<c:when test="${not empty letter}">
				<jsp:include page="/common/file_download.jsp">
					<jsp:param name="fileid" value="letter" />
					<jsp:param name="filetag" value="${letter}" />
					<jsp:param name="isRemovable" value="false" />
				</jsp:include>
			</c:when>			
		</c:choose>								
	</p>
	<p>
	<label class="small"><spring:message code="layingletter.acknowledgementDecision" text="Acknowledgement Decision?"/>*</label>
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
		<label class="small"><spring:message code="layingletter.layingDate" text="Laying Date"/></label>
		<input id="layingDate" name="setlayingDate" value="${layingDate}" class="datemask sText"/>			
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.letter" text="Letter"/></label>
		<c:choose>		
			<c:when test="${empty letter}">				
				<jsp:include page="/common/file_upload.jsp">
					<jsp:param name="fileid" value="letter" />
				</jsp:include>
				<a id="generateLetter" href="#"><spring:message code="layingletter.generateLetter" text="Generate Letter"/></a>
			</c:when>
			<c:otherwise>		
				<jsp:include page="/common/file_download.jsp">
					<jsp:param name="fileid" value="letter" />
					<jsp:param name="filetag" value="${letter}" />
					<jsp:param name="isRemovable" value="true" />
				</jsp:include>
			</c:otherwise>
		</c:choose>								
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.acknowledgementDecision" text="Acknowledgement Decision?"/>*</label>
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
	<input type="hidden" name="id" value="${domain.getId()}"/>
	<input type="hidden" name="locale" value="${domain.getLocale()}"/>
	<input type="hidden" name="version" value="${domain.getVersion()}"/>
	<input type="hidden" name="layingFor" value="${domain.getLayingFor()}"/>
	<input type="hidden" id="selectedStatusType" name="status" value="${selectedStatus.getType()}"/>
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