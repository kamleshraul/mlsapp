<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** Removing Referred Ordinance from replace_ordinance Bill ****/
	function removeReferredOrdinance(){
		if($('#viewReferredOrdinance').text()!="-") {
			$('#viewReferredOrdinance').css('text-decoration','none');
			$.prompt($('#dereferOrdinanceWarningMessage').val(),{
				buttons: {Ok:true}, callback: function(v){
			   		if(v){
			   			$('#referredOrdinance').val("");		
			   			$('#viewReferredOrdinance').text("-");
			   			$('#viewReferredOrdinance').css('text-decoration','none');
						$('#referredOrdinanceYear').text("");
			   		}     						
				}
			});
		}				
	}
	
	function referenceForBill(refType){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
		$.get('bill/referAct/init?action='+refType,function(data){		
			if(refType=='act'){
				$("#referringActResultDiv").html(data);					
				$("#referringActResultDiv").show();
			}else if(refType=='ordinance'){
				$("#referringOrdinanceResultDiv").html(data);					
				$("#referringOrdinanceResultDiv").show();
			}

			$.unblockUI();	
			$("#assistantDiv").hide();
			$("#backToBillDiv").show();
		},'html');
	}
	
	$(document).ready(function(){
		if($('#typeOfSelectedBillType').val() != 'amending') {
			$('#referredActDiv').hide();			
		}
		if($('#typeOfSelectedBillType').val() != 'replace_ordinance') {
			$('#referredOrdinanceDiv').hide();
		}
		$('#billType').change(function() {
			$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
				
				if(data!=undefined || data!='') {
					if(data=='amending') {
						$('#referredActDiv').show();
						$('#annexuresForAmendingBill_div').show();
						$('#referredOrdinanceDiv').hide();
					} else if(data=='replace_ordinance'){
						$('#referredOrdinanceDiv').show();
						$('#referredActDiv').hide();
						$('#annexuresForAmendingBill_div').hide();
					}else{
						$('#referredActDiv').hide();
						$('#annexuresForAmendingBill_div').hide();
						$('#referredOrdinanceDiv').hide();
					}
				} else {
					alert("Some Error Occured!");
				}
			});
		});
		/**** Referring Act for Amendment Bill ****/
		$('#referAct').click(function() {
			referenceForBill('act');
		});			
		
		$('#referOrdinance').click(function() {
			referenceForBill('ordinance');
		});	
		
		  //view supporting members status
	    $("#viewStatus").click(function(){
		    $.get('bill/status/'+$("#bill").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** view detail of referred act (currently showing pdf of act) ****/		
		$('#viewReferredAct').click(function() {
			if(this.text!='-') {					
				var referredActId = $('#referredAct').val();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var resourceURL='act/'+referredActId+'/edit?edit=false';
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:800,height:700});
				},'html');
			}				
		});
	    
		/**** view detail of referred ordinance****/		
		$('#viewReferredOrdinance').click(function() {
			if(this.text!='-') {					
				var referredOrdinanceId = $('#referredOrdinance').val();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var resourceURL='ordinance/'+referredOrdinanceId+'/edit?edit=false';
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:800,height:700});
				},'html');
			}				
		});
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="workflow/bill/supportingmember" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>		
	</c:when>
	<c:otherwise>
	<spring:message code="supportingmember.heading" text="Approve request to add you as supporting member"/>		
	</c:otherwise>
	</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p style="display:none;">
		<label class="small"><spring:message code="bill.houseType" text="House Type"/></label>		
		<input type="text" class="sText" id="houseType" name="houseType" value="${houseTypeName}" readonly="readonly">
	
		<label class="small"><spring:message code="bill.year" text="Year"/>*</label>
		<input type="text" class="sText" id="year" name="year" value="${year}" readonly="readonly">
	
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="bill.sessionType" text="Session Type"/>*</label>		
		<input type="text" class="sText" id="sessionType" name="sessionType" value="${sessionType}" readonly="readonly">
		
		<label class="small"><spring:message code="bill.deviceType" text="Device Type"/>*</label>
		<input type="text" class="sText" id="deviceType" name="deviceType" value="${deviceType}" readonly="readonly">
	</p>	
		
	<p>
		<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
		<input id="primaryMember" class="sText" type="text"  value="${primaryMemberName}" readonly="readonly" style="height: 28px;">
	</p>		
	
	<p>
		<label class="centerlabel"><spring:message code="bill.supportingMembers" text="Supporting Members"/></label>
		<textarea id="supportingMembers"  class="sTextarea" readonly="readonly" rows="2" cols="50">${supportingMembersName}</textarea>
		<a href="#" id="viewStatus"><spring:message code="motion.viewstatus" text="View Status"></spring:message></a>		
	</p>
	
	<p>
		<label class="small"><spring:message code="bill.billType" text="Bill Type"/>*</label>
		<input type="text" class="sText" id="billTypeName" name="billTypeName" value="${billTypeName}" readonly="readonly">
		<form:input type="hidden" id="approvedBillType" path="approvedBillType" value="${billType}" />
	</p>
	
	<p>
		<label class="small"><spring:message code="bill.billKind" text="Bill Kind"/>*</label>
		<input type="text" class="sText" id="billKindName" name="billKindName" value="${billKindName}" readonly="readonly">
		<form:input type="hidden" id="approvedBillKind" path="approvedBillKind" value="${billKind}" />
	</p>
	
	<div id="referredActDiv">
		<p>
			<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty referredAct)}">
					<a href="#" id="viewReferredAct" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
					<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
				</c:when>
				<c:otherwise>
					<a href="#" id="viewReferredAct" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
					<label id="referredActYear"></label>
				</c:otherwise>
			</c:choose>
			<input type="hidden" id="referredAct" name="referredAct" value="${referredAct}">
		</p>
	</div>
	
	<div id="referredOrdinanceDiv">
						<p>
							<a href="#" id="referOrdinance" style="margin: 0px 0px 0px 162px; display: none;"><spring:message code="bill.referOrdinance" text="Refer Ordinance"></spring:message></a>
						</p>		
						<p>
							<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredOrdinance)}">
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px;" class="clubbedRefBills"><c:out value="${referredOrdinanceNumber}"></c:out></a>
									<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px; text-decoration: none;" class="clubbedRefBills"><c:out value="-"></c:out></a>
									<label id="referredOrdinanceYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
						</p>
					</div>	
					
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.titles" text="Titles of Bill" /></label></legend>
			<div id="titles_div">
				<c:forEach var="i" items="${titles}">
					<p>
						<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
						<textarea rows="2" cols="50" id="title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.contentDrafts" text="Drafts of Bill" /></label></legend>
			<div id="contentDrafts_div">
				<c:forEach var="i" items="${contentDrafts}">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
						<textarea class="wysiwyg" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statementOfObjectAndReasonDrafts" text="Statement of Object & Reason" /></label></legend>
			<div id="statementOfObjectAndReasonDrafts_div">
				<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
						<textarea class="wysiwyg" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.financialMemorandumDrafts" text="Financial Memorandum" /></label></legend>
			<div id="financialMemorandumDrafts_div">
				<c:forEach var="i" items="${financialMemorandumDrafts}">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
						<textarea class="wysiwyg" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statutoryMemorandumDrafts" text="Statutory Memorandum" /></label></legend>
			<div id="statutoryMemorandumDrafts_div">
				<c:forEach var="i" items="${statutoryMemorandumDrafts}">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
						<textarea class="wysiwyg" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<p>
	<label class="small"><spring:message code="bill.decisionstatus" text="Decision?"/>*</label>
	<input id="formattedDecisionStatus" name="formattedDecisionStatus" class="sText" readonly="readonly" value="${formattedDecisionStatus}">
	<input id="decisionStatus" name="decisionStatus" class="sText" readonly="readonly" value="${decisionStatus}" type="hidden">
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
		<label class="small"><spring:message code="bill.decisionstatus" text="Decision?"/>*</label>
		<form:select path="decisionStatus" cssClass="sSelect" items="${decisionStatus}" itemLabel="name" itemValue="id"/>
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
		<c:if test="${workflowstatus!='COMPLETED' and bulkedit!='yes'}">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
		<c:if test="${workflowstatus!='COMPLETED' and bulkedit=='yes'}">
			<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
		</c:if>
		</p>
	</div>	
	</c:otherwise>
	</c:choose>	
	<p style="display:none;">
		<label class="small"><spring:message code="supportingmember.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError"></form:errors>
	</p>	
		
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<%-- <form:hidden path="workflowStarted"/> --%>	
	<%-- <form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/> --%>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
	<form:hidden path="approvalDate"/>
	<input type="hidden" id="currentSupportingMember" name="currentSupportingMember" value="${currentSupportingMember}">
	<input type="hidden" id="requestReceivedOnDate" name="requestReceivedOnDate" value="${requestReceivedOnDate }">
	<input type="hidden" id="bill" name="bill" value="${bill}">
	<input type="hidden" id="task" name="task" value="${task}">	
	<input type="hidden" id="workflowDetailsId" name="workflowDetailsId" value="${workflowDetailsId}">
	<input type="hidden" id="workflowdetails" name="workflowdetails" value="${workflowdetails}">	
	<input type="hidden" id="requestReceivedOn" name="requestReceivedOn" value="${requestReceivedOn }">		
</form:form>
</div>
</body>
</html>