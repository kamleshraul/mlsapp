<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		if(($('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
				&& $('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
				&& $('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
			||
			($('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
			&& $('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
			&& $('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
		) {
			$('#financialMemorandumDrafts_button').hide();
			$('#financialMemorandumDrafts_div').show();
		} else {
			$('#financialMemorandumDrafts_button').show();
		}
		
		if(($('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
				&& $('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
				&& $('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
			||
			($('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
			&& $('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
			&& $('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
		) {
			$('#statutoryMemorandumDrafts_button').hide();
			$('#statutoryMemorandumDrafts_div').show();
		} else {
			$('#statutoryMemorandumDrafts_button').show();
		}
		
		/**** show title for only default bill language ****/
		$('.title').each(function() {
			var currentLanguage = this.id.split("_")[2];				
			if(currentLanguage==$('#defaultBillLanguage').val()) {		
				$('#title_para_'+currentLanguage).show();					
			} else {
				$('#title_para_'+currentLanguage).hide();										
			}
		});
		/**** toggle title for given language icon ****/
		$('.toggleTitle').click(function() {
			var currentLanguage = this.id.split("_")[1];				
			if($('#title_para_'+currentLanguage).css('display')=='none') {
				$('#title_para_'+currentLanguage).show();					
			} else {
				$('#title_para_'+currentLanguage).hide();					
			}
			return false;
		});
		
		/**** show content draft for only default bill language ****/
		$('.contentDraft').each(function() {
			var currentLanguage = this.id.split("_")[2];				
			if(currentLanguage==$('#defaultBillLanguage').val()) {		
				$('#contentDraft_para_'+currentLanguage).show();
				$('#contentDraft_FileDiv_'+currentLanguage).show();
			} else {
				$('#contentDraft_para_'+currentLanguage).hide();
				$('#contentDraft_FileDiv_'+currentLanguage).hide();
			}
		});
		/**** toggle contentDraft for given language icon ****/
		$('.toggleContentDraft').click(function() {
			var currentLanguage = this.id.split("_")[1];				
			if($('#contentDraft_para_'+currentLanguage).css('display')=='none') {
				$('#contentDraft_para_'+currentLanguage).show();
				$('#contentDraft_FileDiv_'+currentLanguage).show();
			} else {
				$('#contentDraft_para_'+currentLanguage).hide();
				$('#contentDraft_FileDiv_'+currentLanguage).hide();
			}
			return false;
		});
		
		/**** show statement of object and reason draft for only default bill language ****/
		$('.statementOfObjectAndReasonDraft').each(function() {
			var currentLanguage = this.id.split("_")[2];				
			if(currentLanguage==$('#defaultBillLanguage').val()) {		
				$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
			} else {
				$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();										
			}
		});
		/**** toggle statement of object and reason draft for given language icon ****/
		$('.toggleStatementOfObjectAndReasonDraft').click(function() {
			var currentLanguage = this.id.split("_")[1];				
			if($('#statementOfObjectAndReasonDraft_para_'+currentLanguage).css('display')=='none') {
				$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
			} else {
				$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();					
			}
			return false;
		});
		
		/**** show financial memorandum draft for only default bill language ****/
		$('.financialMemorandumDraft').each(function() {
			var currentLanguage = this.id.split("_")[2];				
			if(currentLanguage==$('#defaultBillLanguage').val()) {		
				$('#financialMemorandumDraft_para_'+currentLanguage).show();					
			} else {
				$('#financialMemorandumDraft_para_'+currentLanguage).hide();										
			}
		});
		/**** toggle financial memorandum draft on user demand ****/
		$('#financialMemorandumDrafts_button').click(function() {
			if($('#financialMemorandumDrafts_div').css('display')=='none') {
				$('#financialMemorandumDrafts_div').show();
			} else {
				$('#financialMemorandumDrafts_div').hide();
			}
			return false;
		});
		/**** toggle financial memorandum draft for given language icon ****/
		$('.toggleFinancialMemorandumDraft').click(function() {
			var currentLanguage = this.id.split("_")[1];				
			if($('#financialMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
				$('#financialMemorandumDraft_para_'+currentLanguage).show();					
			} else {
				$('#financialMemorandumDraft_para_'+currentLanguage).hide();					
			}
			return false;
		});
		
		
		/**** show statutory memorandum draft for only default bill language ****/
		$('.statutoryMemorandumDraft').each(function() {
			var currentLanguage = this.id.split("_")[2];				
			if(currentLanguage==$('#defaultBillLanguage').val()) {		
				$('#statutoryMemorandumDraft_para_'+currentLanguage).show();					
			} else {
				$('#statutoryMemorandumDraft_para_'+currentLanguage).hide();										
			}
		});
		/**** toggle statutory memorandum draft on user demand ****/
		$('#statutoryMemorandumDrafts_button').click(function() {
			if($('#statutoryMemorandumDrafts_div').css('display')=='none') {
				$('#statutoryMemorandumDrafts_div').show();
			} else {
				$('#statutoryMemorandumDrafts_div').hide();
			}
			return false;
		});
		/**** toggle statutory memorandum draft for given language icon ****/
		$('.toggleStatutoryMemorandumDraft').click(function() {
			var currentLanguage = this.id.split("_")[1];				
			if($('#statutoryMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
				$('#statutoryMemorandumDraft_para_'+currentLanguage).show();					
			} else {
				$('#statutoryMemorandumDraft_para_'+currentLanguage).hide();					
			}
			return false;
		});
		
		/**** allow refer act & ordinance as per bill type ****/
		if($('#typeOfSelectedBillType').val()=='') {
			$('#referredActDiv').hide();
			$('#referredOrdinanceDiv').hide();
		} else if($('#typeOfSelectedBillType').val()=='original') {
			$('#referredActDiv').hide();
			$('#referredOrdinanceDiv').hide();
		} else if($('#typeOfSelectedBillType').val()=='replace_ordinance'){
			$('#referredOrdinanceDiv').show();
			$('#referredActDiv').hide();
		} else if($('#typeOfSelectedBillType').val()=='amending' && $('#typeOfSelectedDeviceType').val()=='bills_nonofficial') {
			$('#referredActDiv').show();
			$('#referredOrdinanceDiv').hide();
		} else{
			$('#referredActDiv').show();
			$('#referredOrdinanceDiv').show();
		}

		$('#billType').change(function() {
			$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
				
				if(data!=undefined || data!='') {
					if(data=='original') {
						$('#referredActDiv').hide();
						$('#referredOrdinanceDiv').hide();
					} else if(data=='replace_ordinance'){
						$('#referredOrdinanceDiv').show();
						$('#referredActDiv').hide();
					} else if(data=='amending' && $('#typeOfSelectedDeviceType').val()=='bills_nonofficial') {
						$('#referredActDiv').show();
						$('#referredOrdinanceDiv').hide();
					} else{
						$('#referredActDiv').show();
						$('#referredOrdinanceDiv').show();
					}
				} else {
					alert("Some Error Occured!");
				}
			});
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
	
	<style type="text/css">
		.imageLink{
			width: 18px;
			height: 18px;				
			/* box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; */ 
		}
		
		/* .imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		} */
		
		.impIcons{
			box-shadow: 2px 2px 2px black;
		}	
		
		.textdraft_file {
			float: right; 
			margin: -210px 20px;
			position: relative;
		}					
	</style>
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
	
	<div id="referredOrdinanceDiv" style="margin-top:10px;">
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
					
	<div style="margin-top: 20px;">
		<fieldset>
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${titles}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>																			
			<div id="titles_div">
				<c:forEach var="i" items="${titles}">
					<div id="title_para_${i.language.type}" style="display:none;">
					<p>
						<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
						<textarea rows="2" cols="50" class="title" id="title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
					</p>							
					</div>								
				</c:forEach>
			</div>
		</fieldset>
	</div>	

	<div style="margin-top: 20px;">
		<fieldset>
			<p style="margin-bottom: -10px;">
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${contentDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			</p>						
			<div id="contentDrafts_div">
				<c:forEach var="i" items="${contentDrafts}" varStatus="draftNumber">
					<div id="contentDraft_para_${i.language.type}" style="display:none;">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
						<textarea class="wysiwyg contentDraft" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}">${i.text}</textarea>
						<div class="textdraft_file" id="contentDraft_FileDiv_${i.language.type}">
							<jsp:include page="/common/file_load.jsp">
								<jsp:param name="fileid" value="contentDraft-file-${i.language.type}" />
								<jsp:param name="filetag" value="${i.file}" />
								<jsp:param name="isUploadAllowed" value="false" />
								<jsp:param name="isRemovable" value="false" />
							</jsp:include>			
						</div>
						<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>						
					</div>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div style="margin-top: 20px;">
		<fieldset>
			<p style="margin-bottom: -20px;">
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			</p>
			<div id="statementOfObjectAndReasonDrafts_div">
				<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
					<div id="statementOfObjectAndReasonDraft_para_${i.language.type}" style="display:none;">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
						<textarea class="wysiwyg statementOfObjectAndReasonDraft" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
					</div>
				</c:forEach>
			</div>
		</fieldset>
	</div>					
	
	<p style="margin-left: 162px;margin-top: 20px;">
	<a href="#" id="financialMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
		<img src="./resources/images/Fmemo.jpg" title="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'></spring:message>" class="imageLink impIcons" />
	</a>	
	<a href="#" id="statutoryMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
		<img src="./resources/images/Smemo.jpg" title="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'></spring:message>" class="imageLink impIcons" />
	</a>
	</p>
	
	<div id="financialMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
		<fieldset>
			<p style="margin-bottom: -10px;">
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${financialMemorandumDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			</p>
			<div>
				<c:forEach var="i" items="${financialMemorandumDrafts}">
					<div id="financialMemorandumDraft_para_${i.language.type}" style="display:none;">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
						<textarea class="wysiwyg financialMemorandumDraft" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
					</div>
				</c:forEach>
			</div>
		</fieldset>
	</div>					
	
	<div id="statutoryMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
		<fieldset>
			<p style="margin-bottom: -20px;">
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${statutoryMemorandumDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			</p>
			<div>
				<c:forEach var="i" items="${statutoryMemorandumDrafts}">
					<div id="statutoryMemorandumDraft_para_${i.language.type}" style="display:none;">
					<p>
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
						<textarea class="wysiwyg statutoryMemorandumDraft" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
						<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
					</div>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<c:if test="${selectedDeviceTypeForBill=='bills_government'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
			<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg"></form:textarea>
			<div class="textdraft_file" id="opinionSoughtFromLawAndJD_FileDiv">
				<jsp:include page="/common/file_load.jsp">
					<jsp:param name="fileid" value="opinionSoughtFromLawAndJDFile" />
					<jsp:param name="filetag" value="${domain.opinionSoughtFromLawAndJDFile}" />
					<jsp:param name="isRemovable" value="false" />
					<jsp:param name="isUploadAllowed" value="false" />
				</jsp:include>							
			</div>
			<form:errors path="opinionSoughtFromLawAndJD" />
		</p>
		<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
			<p>
			<label class="small"><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
			<input id="formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" readonly="readonly">
			<input id="setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
			</p>
		</c:if>
		
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.recommendationFromGovernor" text="Recommendation From Governor"/></label>
			<form:textarea id="recommendationFromGovernor" path="recommendationFromGovernor" cssClass="wysiwyg"></form:textarea>
			<form:errors path="recommendationFromGovernor" />
		</p>
		<c:if test="${not empty dateOfRecommendationFromGovernor}">
			<p>
			<label class="small"><spring:message code="bill.dateOfRecommendationFromGovernor" text="Date Of Recommendation From Governor"/></label>
			<input id="formattedDateOfRecommendationFromGovernor" name="formattedDateOfRecommendationFromGovernor" value="${formattedDateOfRecommendationFromGovernor}" class="sText" readonly="readonly">
			<input id="setDateOfRecommendationFromGovernor" name="setDateOfRecommendationFromGovernor" type="hidden"  value="${dateOfRecommendationFromGovernor}">	
			</p>
		</c:if>
		
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.recommendationFromPresident" text="Recommendation From President"/></label>
			<form:textarea id="recommendationFromPresident" path="recommendationFromPresident" cssClass="wysiwyg"></form:textarea>
			<form:errors path="recommendationFromPresident" />
		</p>
		<c:if test="${not empty dateOfRecommendationFromPresident}">
			<p>
			<label class="small"><spring:message code="bill.dateOfRecommendationFromPresident" text="Date Of Recommendation From President"/></label>
			<input id="formattedDateOfRecommendationFromPresident" name="formattedDateOfRecommendationFromPresident" value="${formattedDateOfRecommendationFromPresident}" class="sText" readonly="readonly">
			<input id="setDateOfRecommendationFromPresident" name="setDateOfRecommendationFromPresident" type="hidden"  value="${dateOfRecommendationFromPresident}">	
			</p>
		</c:if>
	</c:if>
	
	<c:choose>
	<c:when test="${status=='COMPLETED' || status=='TIMEOUT'}">
	<p>
	<label class="small"><spring:message code="bill.decisionstatus" text="Decision?"/>*</label>
	<input id="formattedDecisionStatus" name="formattedDecisionStatus" class="sText" readonly="readonly" value="${formattedDecisionStatus}">
	<input id="decisionStatus" name="decisionStatus" class="sText" readonly="readonly" value="${decisionStatus}" type="hidden">
	</p>	
	<div class="fields">
		<h2></h2>
		<%-- <p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
		</p> --%>
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
	<input type="hidden" id="defaultBillLanguage" name="defaultBillLanguage" value="${defaultBillLanguage}">	
</form:form>
</div>
</body>
</html>