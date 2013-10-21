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
		
		/**** Submit Details ****/
		$("#submitDetails").click(function(e){			
			$.post($("#editStatusUpdationForm").attr('action'),
	            $("#editStatusUpdationForm").serialize(),  
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
<form id="editStatusUpdationForm" action="bill/editStatusUpdation" method="POST">
	<div id="resultDiv"></div>			 
		<p>
			<label class="small"><spring:message code="bill.currentHouseType" text="Current House Type"/></label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input type="hidden" id="houseType" name="houseType" value="${houseType}"/>
			<form:errors path="houseType" cssClass="validationError"/>
		</p>
		<input type="hidden" id="billId" name="billId" value="${billId}"/>
		<c:choose>
		<c:when test="${not empty draftsOfStatusUpdationForGivenHouse}">
			<input type="hidden" name="numberOfDraftsOfStatusUpdationForGivenHouse" value="${draftsOfStatusUpdationForGivenHouse.size()}"/>			
			<h3>
				<spring:message code="bill.draftsOfStatusUpdationForGivenHouse" text="Drafts Of Status Updation For Given House"></spring:message>
			</h3>
			<div style="overflow: auto;">
			<table class="uiTable">				
				<tr>
					<th><spring:message code="bill.draftStatusForUpdation" text="Status For Updation"/></th>
					<th><spring:message code="bill.houseRound" text="House Round"/></th>					
					<th><spring:message code="bill.draftStatusDate" text="Status Date"/></th>
					<th><spring:message code="bill.draftExpectedStatusDate" text="Expected Status Date"/></th>									
				</tr>
				<c:forEach items="${draftsOfStatusUpdationForGivenHouse}" var="i" varStatus="entry">							
					<input type="hidden" name="id_${entry.count}" value="${i.id}"/>					
					<tr>
						<td><input class="sText" name="status_${entry.count}" value="${statuses[entry.count-1]}" readonly="readonly"/></td>
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
						<td><input class="datemask sText" name="statusDate_${entry.count}" value="${statusDates[entry.count-1]}"/></td>
						<td><input class="datemask sText" name="expectedStatusDate_${entry.count}" value="${expectedStatusDates[entry.count-1]}"/></td>									
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
				<spring:message code="bill.noentriesfound" text="No Entries Found"></spring:message>
			</h3>
		</c:otherwise>
		</c:choose>				
</form>
<input type="hidden" id="errorMessage" value="<spring:message code="bill.errorInUpdationForDevice" text="Error In Updation"/>">
<input type="hidden" id="successMessage" value="<spring:message code="bill.errorInUpdationForDevice" text="Success In Updation"/>">
</div>	
</body>
</html>