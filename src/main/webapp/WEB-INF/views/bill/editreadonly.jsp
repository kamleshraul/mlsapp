<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="bill" text="Bill Information System"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">		
		$(document).ready(function(){
			/**** detail of clubbed, referenced and lapsed bills ****/
			function viewBillDetail(id){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="houseType="+$("#selectedHouseType").val()
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
			
			/**** refer act only in case of amendment bill type ****/
			if($('#readonly_typeOfSelectedBillType').val()!='amending') {
				$('#readonly_referredActDiv').hide();
			}
			//to check/uncheck checkboxes for current checklist selection by assistant
			$('.readonly_checklist_checkbox_fields').each(function() {
				var fieldNumber = this.id.split("_")[4];
				//alert("value for field " + fieldNumber + ": " + $(this).val());
				if($(this).val()=='yes') {
					$('#readonly_checklist_checkbox_'+fieldNumber).attr('checked','checked');
					$('#readonly_checklist_checkbox_'+fieldNumber+'_div').show();
				} else {
					$('#readonly_checklist_checkbox_'+fieldNumber).removeAttr('checked');															
				}
			});			
			/**** Revisions ****/
		    $(".readonly_viewRevisions").click(function(){
		    	var thingToBeRevised = this.id.split("_")[2];
		    	alert("id of this referred bill = " + $("#readonly_id").val());
			    $.get('bill/revisions/'+$("#readonly_id").val()+"?thingToBeRevised="+thingToBeRevised,function(data){
				    $.fancybox.open(data);
			    });
			    return false;
		    });
		    /**** Contact Details ****/
		    $("#readonly_viewContacts").click(function(){
			    var primaryMember=$("#readonly_primaryMember").val();
			    var supportingMembers=$("#readonly_selectedSupportingMembers").val();
			    var members=primaryMember;
			    if(supportingMembers!=null){
				    if(supportingMembers!=''){
					    members=members+","+supportingMembers;
				    }
			    }
			    $.get('question/members/contacts?members='+members,function(data){
				    $.fancybox.open(data);
			    });
			    return false;
		    });	  
		    /**** view detail of referred act (currently showing pdf of act) ****/		
			$('#readonly_viewReferredAct').click(function() {
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
		    /**** On page Load ****/		   
		    if($("#readonly_ministrySelected").val()==''){
				$("#readonly_ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}else{
				$("#readonly_ministry").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			if($("#readonly_subDepartmentSelected").val()==''){
				$("#readonly_subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}else{
				$("#readonly_subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}	
			if($('#readonly_selectedBillType').val()=="" || $('#readonly_selectedBillType').val()==undefined){		
				$("#readonly_billType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill type for now, this option will be useful.
				$("#readonly_billType").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}			
			if($('#readonly_selectedBillKind').val()=="" || $('#readonly_selectedBillKind').val()==undefined){		
				$("#readonly_billKind").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill kind for now, this option will be useful.
				$("#readonly_billKind").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			//----------------------revise drafts script----------------------//		
			$('.readonly_revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedTitlePara_'+currentLanguage).show();
					$('#readonly_reviseTitle_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#readonly_revisedTitlePara_'+currentLanguage).hide();
					if($("#readonly_title_text_"+currentLanguage).val()!=undefined && $("#title_text_"+currentLanguage).val()!='') {
						$(this).val($("#readonly_title_text_"+currentLanguage).val());
					}					
				}
			});			
			$('.readonly_revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedContentDraftPara_'+currentLanguage).show();
					$('#readonly_reviseContentDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#readonly_revisedContentDraftPara_'+currentLanguage).hide();
					if($("#readonly_contentDraft_text_"+currentLanguage).val()!=undefined && $("#contentDraft_text_"+currentLanguage).val()!='') {
						$(this).val($("#readonly_contentDraft_text_"+currentLanguage).val());
					}					
				}
			});
			$('.readonly_revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();
					$('#readonly_reviseStatementOfObjectAndReasonDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#readonly_revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();
					$(this).val($("#readonly_statementOfObjectAndReasonDraft_text_"+currentLanguage).val());
				}
			});
			$('.readonly_revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedFinancialMemorandumDraftPara_'+currentLanguage).show();
					$('#readonly_reviseFinancialMemorandumDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#readonly_revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#readonly_financialMemorandumDraft_text_"+currentLanguage).val());
				}
			});
			$('.readonly_revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();
					$('#readonly_reviseStatutoryMemorandumDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#readonly_revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#readonly_statutoryMemorandumDraft_text_"+currentLanguage).val());
				}
			});			
		});		
		</script>
		 <style type="text/css">
	        @media print {
	            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
	            display:none;
	            }
	        }
	    </style>
	</head> 

	<body>
		<div class="fields clearfix watermark">		
			<div id="readonly_assistantDiv">
				<form:form modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceTypeForBill} ${formattedNumber}</h2>		
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.houseType" text="House Type"/>*</label>
						<input id="readonly_formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="readonly_houseType" name="houseType" value="${houseType}" type="hidden">								
					</p>	
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.year" text="Year"/>*</label>
						<input id="readonly_formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="readonly_sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.sessionType" text="Session Type"/>*</label>		
						<input id="readonly_formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="readonly_sessionType" name="sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="readonly_session" name="session" value="${session}"/>						
					</p>
					
					<p style="display:none;">
							<label class="small"><spring:message code="bill.deviceType" text="Device Type"/>*</label>
							<input id="readonly_formattedDeviceTypeForBill" name="formattedDeviceTypeForBill" value="${formattedDeviceTypeForBill}" class="sText" readonly="readonly">
							<input id="readonly_type" name="type" value="${deviceTypeForBill}" type="hidden">
							<input id="readonly_originalType" name="originalType" value="${originalDeviceType}" type="hidden">											
					</p>	
					
					<c:if test="${!(empty domain.number)}">
					<p>
						<label class="small"><spring:message code="bill.number" text="bill Number"/>*</label>
						<input id="readonly_formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="readonly_number" name="number" value="${domain.number}" type="hidden">						
					</p>
					</c:if>
					
					<c:if test="${!(empty submissionDate)}">
					<p>
					<label class="small"><spring:message code="bill.submissionDate" text="Submitted On"/></label>
					<input id="readonly_formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
					<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
					</p>
					</c:if>
					
					<c:if test="${selectedDeviceTypeForBill == 'bills_government'}">
					<p>
						<label class="small"><spring:message code="bill.introducingHouseType" text="Introducing House Type"/></label>
						<form:select id="readonly_introducingHouseType" class="sSelect" path="introducingHouseType">
						<c:forEach var="i" items="${introducingHouseTypes}">							
							<c:choose>
								<c:when test="${i.id == selectedIntroducingHouseType}">
									<option value="${i.id}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}">${i.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</form:select>														
					</p>
					</c:if>
					
					<p>
						<label class="small"><spring:message code="bill.billType" text="Bill Type"/></label>
						<select id="readonly_billType" class="sSelect" name="billType">
						<c:forEach var="i" items="${billTypes}">
							<c:choose>
								<c:when test="${i.id == selectedBillType}">
									<option value="${i.id}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}">${i.name}</option>
								</c:otherwise>
							</c:choose>							
						</c:forEach>
						</select>
						<label class="small"><spring:message code="bill.billKind" text="Bill Kind"/></label>
						<select id="readonly_billKind" class="sSelect" name="billKind">
						<c:forEach var="i" items="${billKinds}">
							<c:choose>
								<c:when test="${i.id == selectedBillKind}">
									<option value="${i.id}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}">${i.name}</option>
								</c:otherwise>
							</c:choose>							
						</c:forEach>
						</select>			
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.ministry" text="Ministry"/>*</label>
						<select name="ministry" id="readonly_ministry" class="sSelect">
							<c:forEach items="${ministries }" var="i">
								<c:choose>
									<c:when test="${i.id==ministrySelected }">									
										<option value="${i.id }" selected="selected">${i.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${i.id }" >${i.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<label class="small"><spring:message code="bill.subdepartment" text="Sub Department"/></label>
						<select name="subDepartment" id="readonly_subDepartment" class="sSelect">
						<c:forEach items="${subDepartments }" var="i">
							<c:choose>
								<c:when test="${i.id==subDepartmentSelected }">
									<option value="${i.id }" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id }" >${i.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</select>									
					</p>
					
					<p>
						<label class="centerlabel"><spring:message code="bill.members" text="Members"/></label>
						<textarea id="readonly_members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
						<c:if test="${!(empty primaryMember)}">
							<input id="readonly_primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
						</c:if>
						<c:if test="${!(empty supportingMembers)}">
							<select  name="selectedSupportingMembers" id="readonly_selectedSupportingMembers" multiple="multiple" style="display:none;">
							<c:forEach items="${supportingMembers}" var="i">
							<option value="${i.id}" selected="selected"></option>
							</c:forEach>		
							</select>
						</c:if>	
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText">
						<a href="#" id="readonly_viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
					</p>
					
					<div id="readonly_referredActDiv">
						<p>
							<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredAct)}">
									<a href="#" id="readonly_viewReferredAct" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
									<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="readonly_viewReferredAct" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="referredActYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredAct" name="referredAct" value="${referredAct}">
						</p>
					</div>
					
					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">
						<p>
							<label class="small"><spring:message code="bill.parentbill" text="Clubbed To"></spring:message></label>
							<a href="#" id="p${parent}" onclick="viewBillDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
							<input type="hidden" id="parent" name="parent" value="${parent}">
						</p>
					
						<p>
							<label class="small"><spring:message code="bill.clubbedbills" text="Clubbed Bills"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty clubbedBillsToShow) }">
									<c:forEach items="${clubbedBillsToShow }" var="i">
										<a href="#" id="cq${i.number}" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:out value="-"></c:out>
								</c:otherwise>
							</c:choose>
							<select id="readonly_clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
								<c:forEach items="${clubbedBills }" var="i">
									<option value="${i.id}" selected="selected"></option>
								</c:forEach>
							</select>
						</p>
						
						<p>
						<label class="small"><spring:message code="bill.referencedbills" text="Referenced Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty referencedBills) }">
								<c:forEach items="${referencedBills }" var="i" varStatus="index">
									<c:choose>
										<c:when test="${not empty i.name}">
											<a href="#" id="rq${i.number}" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
											&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})	
										</c:when>
										<c:otherwise>											
											<a href="#" id="rq${i.number}" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="readonly_referencedBill" name="referencedBill" value="${referencedBill}" />						
						</p>
					</c:if>
					
					<p>
						<label class="small"><spring:message code="bill.lapsedbill" text="Lapsed Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty lapsedBills) }">
								<c:forEach items="${lapsedBills }" var="i" varStatus="index">
									<c:choose>
										<c:when test="${not empty i.name}">
											<a href="#" id="lq${i.number}" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})	
										</c:when>
										<c:otherwise>											
											<a href="#" id="lq${i.number}" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="lapsedBill" name="lapsedBill" value="${lapsedBill}" />						
					</p>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.titles" text="Titles of Bill" /></label></legend>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_titles_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForTitles" text="View Revisions for Titles"></spring:message></a>
							<div id="readonly_titles_div">
								<c:forEach var="i" items="${titles}" varStatus="draftNumber">
									<p>
										<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
										<textarea rows="2" cols="50" id="readonly_title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p id="readonly_revisedTitlePara_${i.language.type}" style="display:none;">
										<label class="centerlabel">${i.language.name} <spring:message code="bill.revisedTitle" text=" Revised Title"/></label>						
										<c:set var="revisedTitleText" value=""></c:set>
										<c:set var="revisedTitleId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedTitleText" value="${revisedTitle_marathi}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedTitleText" value="${revisedTitle_hindi}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedTitleText" value="${revisedTitle_english}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea rows="2" cols="50" class="readonly_revisedTitle" id="readonly_revised_title_text_${i.language.type}" name="revised_title_text_${i.language.type}">${revisedTitleText}</textarea>
										<input type="hidden" name="revised_title_id_${i.language.type}" value="${revisedTitleId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>	
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.contentDrafts" text="Drafts of Bill" /></label></legend>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_contentDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForContentDrafts" text="View Revisions for Content Drafts"></spring:message></a>
							<div id="readonly_contentDrafts_div">
								<c:forEach var="i" items="${contentDrafts}" varStatus="draftNumber">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
										<textarea class="wysiwyg" id="readonly_contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p id="readonly_revisedContentDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedContentDraft" text=" Revised Draft"/></label>						
										<c:set var="revisedContentDraftText" value=""></c:set>
										<c:set var="revisedContentDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_marathi}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_hindi}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_english}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg readonly_revisedContentDraft" id="readonly_revised_contentDraft_text_${i.language.type}" name="revised_contentDraft_text_${i.language.type}">${revisedContentDraftText}</textarea>
										<input type="hidden" name="revised_contentDraft_id_${i.language.type}" value="${revisedContentDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
	
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statementOfObjectAndReasonDrafts" text="Statement of Object & Reason" /></label></legend>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_statementOfObjectAndReasonDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForStatementOfObjectAndReasonDrafts" text="View Revisions for Statement Of Object And Reason"></spring:message></a>
							<div id="readonly_statementOfObjectAndReasonDrafts_div">
								<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
										<textarea class="wysiwyg" id="readonly_statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p id="readonly_revisedStatementOfObjectAndReasonDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedStatementOfObjectAndReasonDraft" text=" Revised Statement of Object & Reason"/></label>						
										<c:set var="revisedStatementOfObjectAndReasonDraftText" value=""></c:set>
										<c:set var="revisedStatementOfObjectAndReasonDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_marathi}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_hindi}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_english}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg readonly_revisedStatementOfObjectAndReasonDraft" id="readonly_revised_statementOfObjectAndReasonDraft_text_${i.language.type}" name="revised_statementOfObjectAndReasonDraft_text_${i.language.type}">${revisedStatementOfObjectAndReasonDraftText}</textarea>
										<input type="hidden" name="revised_statementOfObjectAndReasonDraft_id_${i.language.type}" value="${revisedStatementOfObjectAndReasonDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.financialMemorandumDrafts" text="Financial Memorandum" /></label></legend>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_financialMemorandumDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForFinancialMemorandumDrafts" text="View Revisions for Financial Memorandum"></spring:message></a>
							<div id="readonly_financialMemorandumDrafts_div">
								<c:forEach var="i" items="${financialMemorandumDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
										<textarea class="wysiwyg" id="readonly_financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p id="readonly_revisedFinancialMemorandumDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedFinancialMemorandumDraft" text=" Revised Financial Memorandum"/></label>						
										<c:set var="revisedFinancialMemorandumDraftText" value=""></c:set>
										<c:set var="revisedFinancialMemorandumDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_marathi}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_hindi}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_english}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg readonly_revisedFinancialMemorandumDraft" id="readonly_revised_financialMemorandumDraft_text_${i.language.type}" name="revised_financialMemorandumDraft_text_${i.language.type}">${revisedFinancialMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_financialMemorandumDraft_id_${i.language.type}" value="${revisedFinancialMemorandumDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statutoryMemorandumDrafts" text="Statutory Memorandum" /></label></legend>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_statutoryMemorandumDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForStatutoryMemorandumDrafts" text="View Revisions for Statutory Memorandum"></spring:message></a>
							<div id="readonly_statutoryMemorandumDrafts_div">
								<c:forEach var="i" items="${statutoryMemorandumDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
										<textarea class="wysiwyg" id="readonly_statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p id="readonly_revisedStatutoryMemorandumDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedStatutoryMemorandumDraft" text=" Revised Statutory Memorandum"/></label>						
										<c:set var="revisedStatutoryMemorandumDraftText" value=""></c:set>
										<c:set var="revisedStatutoryMemorandumDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_marathi}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_hindi}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_english}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg readonly_revisedStatutoryMemorandumDraft" id="readonly_revised_statutoryMemorandumDraft_text_${i.language.type}" name="revised_statutoryMemorandumDraft_text_${i.language.type}">${revisedStatutoryMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_statutoryMemorandumDraft_id_${i.language.type}" value="${revisedStatutoryMemorandumDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
				
					<p id="readonly_internalStatusDiv">
						<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
						<input id="readonly_formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>					
					<c:if test="${not empty formattedTranslationStatus}">
					<p id="readonly_translationStatusDiv">
						<label class="small"><spring:message code="bill.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="readonly_formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${not empty formattedOpinionFromLawAndJDStatus and selectedDeviceTypeForBill=='bills_nonofficial'}">
					<p id="readonly_opinionFromLawAndJDStatusStatusDiv">
						<label class="small"><spring:message code="bill.currentOpinionFromLawAndJDStatus" text="Current Opinion From Law And JD Status"/></label>
						<input id="readonly_formattedOpinionFromLawAndJDStatus" value="${formattedOpinionFromLawAndJDStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
										
					<input type="hidden" id="readonly_internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="readonly_recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					<input type="hidden" id="readonly_translationStatus"  name="translationStatus" value="${translationStatus}">
					<input type="hidden" id="readonly_opinionFromLawAndJDStatus"  name="opinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}">
					
					<c:set var="isOpinionFromLawAndJDReadonly" value="true"/>
					<c:if test="${selectedDeviceTypeForBill=='bills_government' and empty domain.opinionSoughtFromLawAndJD}">
						<c:set var="isOpinionFromLawAndJDReadonly" value="false"/>
					</c:if>	
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
						<form:textarea id="readonly_opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="${isOpinionFromLawAndJDReadonly}"></form:textarea>
						<form:errors path="opinionSoughtFromLawAndJD" />
					</p>
					<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
					<p>
					<label class="small"><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
					<input id="readonly_formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" readonly="readonly">
					<input id="readonly_setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
					</p>
					</c:if>			
					
					<div>
						<fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.checklist" text="Checklist" /></label></legend>
						<table border="1" style="margin-left: 165px; width: 600px; border: 2px solid black;">							
							<tbody>
								<tr id="readonly_checklistQuestion1">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<br/>
											<label><spring:message code="bill.checklistQuestion1" text="Are following things essential for this bill?"/></label>
										</div>
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.1" text="is bill recommended in accordance with constitution article 207 (1)?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_1" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_1" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_1']" />
										</div>
										<br/>																					
										<div style="display: none;" id="readonly_checklist_checkbox_1_div">
											<label><spring:message code="bill.checklistQuestion1.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
										
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.3" text="is bill recommended in accordance with constitution article 207 (3)?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_2" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_2" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_3']" />
										</div>		
										<br />								
										<div style="display: none;" id="readonly_checklist_checkbox_2_div">
											<label><spring:message code="bill.checklistQuestion1.4" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_3']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
										
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.5" text="is bill recommended in accordance with constitution article 304 (b)?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_3" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_3" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_304_b']" />
										</div>	
										<br />									
										<div style="display: none;" id="readonly_checklist_checkbox_3_div">
											<label><spring:message code="bill.checklistQuestion1.6" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_304_b']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>
								<tr id="readonly_checklistQuestion2">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion2.1" text="is bill in scope of state legislature?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_4" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_4" type="hidden" path="checklist['isInScopeOfStateLegislature']" />
										</div>
										<br />
										<div style="display: none;" id="readonly_checklist_checkbox_4_div">
											<label><spring:message code="bill.checklistQuestion2.2" text="if yes, please also mention related schedule issues"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['issuesInRelatedScheduleForScopeOfStateLegislature']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>
								<tr id="readonly_checklistQuestion3">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion3.1" text="is this bill a money bill?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_5" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_5" type="hidden" path="checklist['isMoneyBill']" />
										</div>
										<br />
										<div style="display: none;" id="readonly_checklist_checkbox_5_div">
											<label><spring:message code="bill.checklistQuestion3.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForBeingMoneyBill']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>
								<tr id="readonly_checklistQuestion4">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion4.1" text="is this bill a financial bill as per constitution article 207 (1)?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_6" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_6" type="hidden" path="checklist['isFinancialBillAsPerConstitutionArticle_207_1']" />
										</div>
										<br />
										<div style="display: none;" id="readonly_checklist_checkbox_6_div">
											<label><spring:message code="bill.checklistQuestion4.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForFinancialBillAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>								
								<tr id="readonly_checklistQuestion5">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion5.1" text="are amendments for amending bill as per scope of original act?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_7" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_7" type="hidden" path="checklist['areAmendmentsForAmendingBillAsPerScopeOfOriginalAct']" />
										</div>
										<br/>										
									</td>
								</tr>
								<tr id="readonly_checklistQuestion6">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion6.1" text="is statutory memorandum mandatory?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_8" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_8" type="hidden" path="checklist['isStatutoryMemorandumMandatory']" />
										</div>		
										<br/>								
										<div style="display: none;" id="readonly_checklist_checkbox_8_div">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion6.2" text="is statutory memorandum as per rules?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_9" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_9" type="hidden" path="checklist['isStatutoryMemorandumAsPerRules']" />
										</div>
										<br />
										<div style="display: none;" id="readonly_checklist_checkbox_9_div">
											<label><spring:message code="bill.checklistQuestion6.3" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForStatutoryMemorandum']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>
								<tr id="readonly_checklistQuestion7">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion7.1" text="is financial memorandum mandatory?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_10" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_10" type="hidden" path="checklist['isFinancialMemorandumMandatory']" />
										</div>	
										<br/>									
										<div style="display: none;" id="readonly_checklist_checkbox_10_div">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion7.2" text="is financial memorandum as per rules?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_11" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_11" type="hidden" path="checklist['isFinancialMemorandumAsPerRules']" />
										</div>
										<br/>
										<div style="display: none;" id="readonly_checklist_checkbox_11_div">
											<label><spring:message code="bill.checklistQuestion7.3" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea" path="checklist['sectionsForFinancialMemorandum']" rows="2" cols="50" style="margin: 10px;" readonly="true"/>
										</div>
									</td>
								</tr>
								<tr id="readonly_checklistQuestion8">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion8.1" text="is statement of object and reason complete?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_12" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_12" type="hidden" path="checklist['isStatementOfObjectAndReasonComplete']" />
										</div>										
									</td>
								</tr>
								<tr id="readonly_checklistQuestion9">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">											
											<label style="width:500px;"><spring:message code="bill.checklistQuestion9.1" text="is law & judiciary department in agreement with above opinions on issues 1, 2, 6 & 7?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_13" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_13" type="hidden" path="checklist['isLawAndJudiciaryDepartmentInAgreementWithOpinions']" />
										</div>										
									</td>
								</tr>
								<tr id="readonly_checklistQuestion10">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">										
											<label style="width:500px;"><spring:message code="bill.checklistQuestion10.1" text="are there any recommendations on subject-matter of this bill by sub-legislation committee?"/></label>
											<input type="checkbox" class="sCheck readonly_checklist_checkboxes" id="readonly_checklist_checkbox_14" style="margin: 10px; margin-left: 50px;" disabled="disabled">
											<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_14" type="hidden" path="checklist['isRecommendedOnSubjectMatterBySubLegislationCommittee']" />
										</div>										
									</td>
								</tr>
							</tbody>
						</table>
						</fieldset>
					</div>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
						<textarea id="readonly_remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>						
					</p>				
					
					<form:hidden id="readonly_id" path="id"/>
					<input id="readonly_levelForWorkflow" name="levelForWorkflow" type="hidden">
					<input id="readonly_bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="readonly_status" value="${status }">
					<input type="hidden" name="createdBy" id="readonly_createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="readonly_dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="readonly_setCreationDate" value="${creationDate }">
					<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input type="hidden" name="workflowStartedOnDate" id="readonly_workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" name="workflowForTranslationStartedOnDate" id="readonly_workflowForTranslationStartedOn" value="${workflowForTranslationStartedOnDate }">
					<input type="hidden" name="workflowForOpinionFromLawAndJDStartedOnDate" id="readonly_workflowForOpinionFromLawAndJDStartedOn" value="${workflowForOpinionFromLawAndJDStartedOnDate }">
					<input type="hidden" name="taskReceivedOnDate" id="readonly_taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input type="hidden" name="taskReceivedOnDateForTranslation" id="readonly_taskReceivedOnDateForTranslation" value="${taskReceivedOnDateForTranslation }">
					<input type="hidden" name="taskReceivedOnDateForOpinionFromLawAndJD" id="readonly_taskReceivedOnDateForOpinionFromLawAndJD" value="${taskReceivedOnDateForOpinionFromLawAndJD}">
					<input id="readonly_role" name="role" value="${role}" type="hidden">
					<input id="readonly_taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="readonly_usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="readonly_usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input id="readonly_oldInternalStatus" name="oldInternalStatus" value="${internalStatus}" type="hidden">
					<input id="readonly_oldRecommendationStatus" name="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
					<input id="readonly_oldTranslationStatus" name="oldTranslationStatus" value="${translationStatus}" type="hidden">
					<input id="readonly_oldOpinionFromLawAndJDStatus" name="oldOpinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}" type="hidden">							
				</form:form>
				<input id="readonly_pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
				<input id="readonly_ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="readonly_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">				
				<input id="readonly_selectedBillType" value="${selectedBillType}" type="hidden">
				<input id="readonly_selectedBillKind" value="${selectedBillKind}" type="hidden">
				<input id="readonly_typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />
				<input id="readonly_typeOfSelectedBillType" type="hidden" value="${typeOfSelectedBillType}" />
				<input id="readonly_translationStatusType" type="hidden" value="${translationStatusType}" />			
			</div>		
		</div>		
	</body>
</html>