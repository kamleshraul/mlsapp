<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="title" text="Voting Detail"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		if( ($('#submitThroughDevice').length > 0) 
				&& ($('#deviceVersion').val()!=undefined || $('#deviceVersion').val()!="")) {
			//update version of parent device
			$('#version').val($('#deviceVersion').val());
		}
		$('#votingDetail').val($('#votingDetailId').val());
		$('#votesInFavor').change(function() {
			if($('#votesInFavor').val()!='' && $('#votesAgainst').val()!='') {
				if(parseInt($('#votesInFavor').val())>parseInt($('#votesAgainst').val())) {
					$('#decision').val("voting_passed");					
				} else if(parseInt($('#votesInFavor').val())<parseInt($('#votesAgainst').val())) {
					$('#decision').val("voting_failed");
				}
			}
		});
		
		$('#votesAgainst').change(function() {
			if($('#votesInFavor').val()!='' && $('#votesAgainst').val()!='') {
				if(parseInt($('#votesInFavor').val())>parseInt($('#votesAgainst').val())) {
					$('#decision').val("voting_passed");					
				} else if(parseInt($('#votesInFavor').val())<parseInt($('#votesAgainst').val())) {
					$('#decision').val("voting_failed");
				}
			}
		});
		
		/**** On Submit Through Device ****/
		$("#submitThroughDevice").click(function(e){
			$.post($("#formThroughDevice").attr('action'),
	            $("#formThroughDevice").serialize(),  
	            function(data){
   					$('.fancybox-inner').html(data);
   					$('html').animate({scrollTop:0}, 'slow');
   				 	$('body').animate({scrollTop:0}, 'slow');	
	            });
	        return false;  
	    });
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
<c:choose>
	<c:when test="${openThroughOverlay=='yes'}">
	<c:set var="formId" value="formThroughDevice"/>
	</c:when>
	<c:otherwise>
	<c:set var="formId" value="domain"/>
	</c:otherwise>
</c:choose>
<form:form id="${formId}" action="votingdetail" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="votingdetail.edit.heading" text="Edit Voting Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>
		<c:if test="${not empty houseType}">	
		<p>
			<label class="small"><spring:message code="votingdetail.houseType" text="House Type"/></label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<form:hidden id="houseType" path="houseType"/>
			<form:errors path="houseType" cssClass="validationError"/>
		</p>	 
		<p>
			<label class="small"><spring:message code="votingdetail.houseRound" text="House Round"/></label>
			<form:select id="houseRound" class="sSelect" path="houseRound">
				<c:forEach var="i" items="${houseRoundVOs}">
					<c:choose>
						<c:when test="${i.isSelected==true}">
							<option value="${i.value}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.value}">${i.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select>							
		</p>
		</c:if>
		<c:if test="${not empty deviceType}">
		<p>
			<label class="small"><spring:message code="votingdetail.deviceType" text="Device Type"/></label>
			<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
			<form:hidden id="deviceType" path="deviceType"/>
			<form:errors path="deviceType" cssClass="validationError"/>
		</p>	
		<c:if test="${not empty deviceId}">
		<form:hidden id="deviceId" path="deviceId"/>
		</c:if> 
		</c:if>	
		
		<p> 
			<label class="small"><spring:message code="votingdetail.votingFor" text="Voting For"/></label>
			<input id="votingForMessage" name="votingForMessage" value="<spring:message code="votingdetail.${votingFor}"/>" class="sText" readonly="readonly">
			<form:hidden cssClass="sText" path="votingFor"/>
			<form:errors path="votingFor" cssClass="validationError"/>	
		</p>
		
		<p> 
			<label class="small"><spring:message code="votingdetail.totalNumberOfVoters" text="Total Number Of Voters"/></label>
			<form:input cssClass="sInteger" path="totalNumberOfVoters"/>
			<form:errors path="totalNumberOfVoters" cssClass="validationError"/>	
		</p>
		
		<p> 
			<label class="small"><spring:message code="votingdetail.actualNumberOfVoters" text="Actual Number Of Voters"/></label>
			<form:input cssClass="sInteger" path="actualNumberOfVoters"/>
			<form:errors path="actualNumberOfVoters" cssClass="validationError"/>	
		</p>	
		
		<p> 
			<label class="small"><spring:message code="votingdetail.votesInFavor" text="Votes In Favor"/></label>
			<form:input id="votesInFavor" cssClass="sInteger" path="votesInFavor"/>
			<form:errors path="votesInFavor" cssClass="validationError"/>	
		</p>
		
		<p> 
			<label class="small"><spring:message code="votingdetail.votesAgainst" text="Votes Against"/></label>
			<form:input id="votesAgainst" cssClass="sInteger" path="votesAgainst"/>
			<form:errors path="votesAgainst" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="votingdetail.decision" text="Decision"/></label>
			<form:select id="decision" class="sSelect" path="decision">
				<c:forEach var="i" items="${votingDecisionStatuses}">
					<c:choose>
						<c:when test="${i.type==selectedDecision}">
							<option value="${i.type}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.type}">${i.name}</option>
						</c:otherwise>
					</c:choose>					
				</c:forEach>
			</form:select>							
		</p>
		
		<p> 
			<label class="small"><spring:message code="votingdetail.isInDecorum" text="Is Voting In Decorum?"/></label>
			<form:checkbox cssClass="sCheck" path="isInDecorum"/>
			<form:errors path="isInDecorum" cssClass="validationError"/>	
		</p>
						
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<c:choose>
					<c:when test="${openThroughOverlay=='yes'}">
					<input id="submitThroughDevice" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					</c:when>
					<c:otherwise>
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					</c:otherwise>
				</c:choose>				
			</p>
		</div>	
	<form:hidden id="votingDetailVersion" path="version" />
	<form:hidden id="votingDetailId" path="id"/>
	<form:hidden id="votingDetailPath" path="locale"/>	
	</form:form>
</div>
<input type="hidden" id="deviceVersion" value="${deviceVersion}"/>	
</body>
</html>