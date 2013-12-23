<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="title" text="States"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		//$('#version').val($('#deviceVersion').val());
		/**** Submit Details ****/
		$("#submitDetails").click(function(e){			
			$('.isInDecorum').each(function() {				
				if($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}				
			});
			$.post($("#editVotingDetailsForDeviceForm").attr('action'),
	            $("#editVotingDetailsForDeviceForm").serialize(),  
	            function(data){
					if(data=="success") {
						$('#resultDiv').html("<h3>" + $('#successMessage').val() + "</h3>");						
					} else {						
						$('#resultDiv').html("<h3>" + $('#errorMessage').val() + "</h3>");
					}   					
	            });
	        return false;  
	    });
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
<form id="editVotingDetailsForDeviceForm" action="votingdetail/editVotingDetailsForDevice" method="POST">
	<div id="resultDiv"></div>			 
		<p>
			<label class="small"><spring:message code="votingdetail.houseType" text="House Type"/></label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input type="hidden" id="houseType" name="houseType" value="${houseType}"/>
			<form:errors path="houseType" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="votingdetail.deviceType" text="Device Type"/></label>
			<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
			<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}"/>
			<form:errors path="deviceType" cssClass="validationError"/>
		</p>
		<input type="hidden" id="deviceId" name="deviceId" value="${deviceId}"/>
		<p> 
			<label class="small"><spring:message code="votingdetail.votingFor" text="Voting For"/></label>
			<input id="votingForMessage" name="votingForMessage" value="<spring:message code="votingdetail.${votingFor}"/>" class="sText" readonly="readonly">
			<input type="hidden" id="votingFor" name="votingFor" value="${votingFor}"/>
			<form:errors path="votingFor" cssClass="validationError"/>	
		</p>
		<c:choose>
		<c:when test="${not empty votingDetailsForDevice}">
			<input type="hidden" name="numberOfVotingDetailsForDevice" value="${votingDetailsForDevice.size()}"/>			
			<h3>
				<spring:message code="votingdetail.votingDetailsForDevice" text="Voting Details"></spring:message>
			</h3>
			<div style="overflow: auto;">
			<table class="uiTable">				
				<tr>
					<c:if test="${deviceType!='resolutions_nonofficial' }">
					<th><spring:message code="votingdetail.houseRound" text="House Round"/></th>
					</c:if>
					<th><spring:message code="votingdetail.totalNumberOfVoters" text="Total Number Of Voters"/></th>
					<th><spring:message code="votingdetail.actualNumberOfVoters" text="Actual Number Of Voters"/></th>
					<th><spring:message code="votingdetail.votesInFavor" text="Votes In Favor"/></th>
					<th><spring:message code="votingdetail.votesAgainst" text="Votes Against"/></th>
					<th><spring:message code="votingdetail.decision" text="Decision"/></th>
					<th><spring:message code="votingdetail.isInDecorum" text="Is Voting In Decorum?"/></th>					
				</tr>
				<c:forEach items="${votingDetailsForDevice}" var="i" varStatus="entry">							
					<input type="hidden" name="id_${entry.count}" value="${i.id}"/>
					
					<tr>
						<c:if test="${deviceType!='resolutions_nonofficial' }">
						<td>
							<select class="sSelect" name="houseRound_${entry.count}">
								<c:forEach var="j" items="${houseRoundVOs}">
									<c:choose>
										<c:when test="${j.value==i.houseRound}">
											<option value="${j.value}" selected="selected">${j.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${j.value}">${j.name}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</td>
						</c:if>
						<td><input class="sInteger" name="totalNumberOfVoters_${entry.count}" value="${i.totalNumberOfVoters}"/></td>
						<td><input class="sInteger" name="actualNumberOfVoters_${entry.count}" value="${i.actualNumberOfVoters}"/></td>
						<td><input class="sInteger" name="votesInFavor_${entry.count}" value="${i.votesInFavor}"/></td>
						<td><input class="sInteger" name="votesAgainst_${entry.count}" value="${i.votesAgainst}"/></td>
						<td>
							<select class="sSelect" name="decision_${entry.count}">
								<c:forEach var="k" items="${votingDecisionStatuses}">
									<c:choose>
										<c:when test="${k.type==i.decision}">
											<option value="${k.type}" selected="selected">${k.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${k.type}">${k.name}</option>
										</c:otherwise>
									</c:choose>					
								</c:forEach>
							</select>
						</td>
						<td>
							<c:choose>
								<c:when test="${i.isInDecorum=='true'}">
									<input type="checkbox" class="sCheck isInDecorum" name="isInDecorum_${entry.count}" value="${i.isInDecorum}" checked="checked"/>
								</c:when>
								<c:otherwise>
									<input type="checkbox" class="sCheck isInDecorum" name="isInDecorum_${entry.count}" value="${i.isInDecorum}"/>
								</c:otherwise>
							</c:choose>							
						</td>
					</tr>
				</c:forEach>
			</table>
			</div>
			<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<input id="submitDetails" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>
		</c:when>
		<c:otherwise>
			<h3>
				<spring:message code="votingdetail.noentriesfound" text="No Entries Found"></spring:message>
			</h3>
		</c:otherwise>
		</c:choose>				
</form>
<input type="hidden" id="errorMessage" value="<spring:message code="votingdetail.errorInUpdationForDevice" text="Error In Updation"/>">
<input type="hidden" id="successMessage" value="<spring:message code="votingdetail.errorInUpdationForDevice" text="Success In Updation"/>">
<input type="hidden" id="deviceVersion" value="${deviceVersion}"/>
</div>	
</body>
</html>