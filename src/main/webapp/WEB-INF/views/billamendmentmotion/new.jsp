<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><spring:message code="billamendmentmotion" text="Bill Amendment Motion"/></title>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
		}		
	</style>	
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
	</style>
	
	<script type="text/javascript">
		//this is for autosuggest
		function split( val ) {
			return val.split( /,\s*/ );
		}	
		function extractLast( term ) {
			return split( term ).pop();
		}
		/**** detail of clubbed, referenced and lapsed bills ****/		
		function viewAmendedBillDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="ugparam="+$("#ugparam").val()
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
		var controlName=$(".autosuggestmultiple").attr("id");
		var primaryMemberControlName=$(".autosuggest").attr("id");
	
		$(document).ready(function(){
			initControls();			
			$.get('ref/billamendmentmotion/amendedBillInfo?amendedBillInfo='+$('#amendedBillInfo').val(), function(data) {
				$('#showAmendedBillText').empty();
				$('#showAmendedBillText').html(data);				
				$('#showAmendedBill').text($('#showAmendedBillText').html());
			});
			$('#showAmendedBill').click(function() {
				viewAmendedBillDetail($('#amendedBill').val());
			});
			/**** Auto Suggest(member login)- Member ****/		
			$( ".autosuggest").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers?session='+$("#session").val(),
				select:function(event,ui){			
				$("#primaryMember").val(ui.item.id);
			}	
			});
			$("select[name='"+controlName+"']").hide();			
			$( ".autosuggestmultiple" ).change(function(){
				//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
				//for this we iterate through the slect box selected value and check if that value is present in the 
				//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
				//then that value will be removed from the select box.
				var value=$(this).val();
				$("select[name='"+controlName+"'] option:selected").each(function(){
					var optionClass=$(this).attr("class");
					if(value.indexOf(optionClass)==-1){
						$("select[name='"+controlName+"'] option[class='"+optionClass+"']").remove();
					}		
				});	
				$("select[name='"+controlName+"']").hide();				
			});
			//http://api.jqueryui.com/autocomplete/#event-select
			$( ".autosuggestmultiple" ).autocomplete({
				minLength:3,
				source: function( request, response ) {
					$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()+'&primaryMemberId='+$('#primaryMember').val(), {
						term: extractLast( request.term )
					}, response );
				},			
				search: function() {
					var term = extractLast( this.value );
					if ( term.length < 2 ) {
						return false;
					}
				},
				focus: function() {
					return false;
				},
				select: function( event, ui ) {
					//what happens when we are selecting a value from drop down
					var terms = $(this).val().split(",");
					//if select box is already present i.e atleast one option is already added
					if($("select[name='"+controlName+"']").length>0){
						if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
						//if option being selected is already present then do nothing
						this.value = $(this).val();					
						$("select[name='"+controlName+"']").hide();						
						}else{
						//if option is not present then add it in select box and autocompletebox
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
						$("select[name='"+controlName+"']").append(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
						}							
						$("select[name='"+controlName+"']").hide();								
						}
					}else{
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
						textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
						text=text+textoption+"</select>";
						$(this).after(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
						}	
						$("select[name='"+controlName+"']").hide();									
					}		
					return false;
				}
			});
			/**** show section amendment for only default bill language ****/
			$('.sectionAmendment').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#sectionAmendment_para_'+currentLanguage).show();					
				} else {
					$('#sectionAmendment_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle section amendment for given language icon ****/
			$('.toggleSectionAmendment').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#sectionAmendment_para_'+currentLanguage).css('display')=='none') {
					$('#sectionAmendment_para_'+currentLanguage).show();			
					$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+currentLanguage).offset().top)}, 'slow');
				} else {
					$('#sectionAmendment_para_'+currentLanguage).hide();	
					$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+$('#defaultBillLanguage').val()).offset().top)}, 'slow');
				}				
			});
			/**** section number change event ****/
			$('.sectionAmendment_sectionNumber').change(function() {
				var currentLanguage = this.id.split("_")[2];
				$('#referSectionText_'+currentLanguage).removeClass("referred");
				$('#referSectionTextImage_'+currentLanguage).attr('title',$('#referSectionTextImageTitle').val());
				$('#referredSectionTextPara_'+currentLanguage).hide();
				if($(this).val()!="") {
					//$('#referSectionText_'+currentLanguage).show();
					$.get('ref/billamendmentmotion/getReferredSectionText?billId='+$('#amendedBill').val()
								+'&sectionNumber='+$('#sectionAmendment_sectionNumber_'+currentLanguage).val()
								+'&language='+currentLanguage, function(data) {
						$('#referredSectionText_'+currentLanguage).wysiwyg("setContent", data.name);
						$('#referredSectionText_'+currentLanguage).val(data.name);
						if(data.id!="0") {
							$('#referSectionText_'+currentLanguage).show();
						} else {
							$('#referSectionText_'+currentLanguage).hide();
						}
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						resetControls();
						scrollTop();
					});
				} else {					
					$('#referSectionText_'+currentLanguage).hide();
					$('#referredSectionText_'+currentLanguage).wysiwyg("setContent", "");
					$('#referredSectionText_'+currentLanguage).val("");										
				}				
			});
			/**** refer section content ****/
			$('.referSectionText').click(function() {
				var currentLanguage = this.id.split("_")[1];
				if($('#referSectionText_'+currentLanguage).hasClass("referred")) {
					$('#referSectionText_'+currentLanguage).removeClass("referred");
					$('#referSectionTextImage_'+currentLanguage).attr('title',$('#referSectionTextImageTitle').val());
					$('#referredSectionTextPara_'+currentLanguage).hide();					
				} else {
					$('#referSectionText_'+currentLanguage).addClass("referred");
					$('#referSectionTextImage_'+currentLanguage).attr('title',$('#deReferSectionTextImageTitle').val());					
					$('#referredSectionTextPara_'+currentLanguage).show();
				}	
				$('html,body').animate({scrollTop:($('#scrollToReferredSectionText_'+currentLanguage).offset().top)}, 'slow');
			});
			/**** refer Bill Draft ****/
			$('.referBillDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];
				if($('#referBillDraft_'+currentLanguage).hasClass("referred")) {
					$('#referBillDraft_'+currentLanguage).removeClass("referred");
					$('#referBillDraftImage_'+currentLanguage).attr('title',$('#referBillDraftImageTitle').val());
					$('#referredBillDraftPara_'+currentLanguage).hide();				
				} else {
					if($('#referredBillDraft_'+currentLanguage).val()==""
							|| $('#referredBillDraft_'+currentLanguage).val()=="<p></p>"
							|| $('#referredBillDraft_'+currentLanguage).val()=="<br><p></p>") {
						$.get('ref/billamendmentmotion/getReferredBillDraft?billId='+$('#amendedBill').val()
								+'&language='+currentLanguage, function(data) {
							$('#referredBillDraft_'+currentLanguage).wysiwyg("setContent", data.name);
							$('#referredBillDraft_'+currentLanguage).val(data.name);
						});
					}
					$('#referBillDraft_'+currentLanguage).addClass("referred");
					$('#referBillDraftImage_'+currentLanguage).attr('title',$('#deReferBillDraftImageTitle').val());					
					$('#referredBillDraftPara_'+currentLanguage).show();
				}
				$('html,body').animate({scrollTop:($('#scrollToReferredBillDraft_'+currentLanguage).offset().top)}, 'slow');
			});
			//send for supporting member approval
			$("#sendforapproval").click(function(e){
				//no need to send for approval in case of empty supporting members.
				if($("#selectedSupportingMembers").val()==""){
					$.prompt($('#supportingMembersEmptyMsg').val(),{
						buttons: {Ok:true}, callback: function(v){
					   		if(v){
					   			scrollTop();
					   			$('#selectedSupportingMembers').focus();
					   		}     						
						}
					});	
					return false;
				}				
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				$.prompt($('#sendForApprovalMsg').val()+$("#selectedSupportingMembers").val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?operation=approval',  
			    	            $("form").serialize(),
			    	            function(data){
			       					$('.tabContent').html(data);
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	   				 	   				
			    	            });
			        }
				}});			
		        return false; 
			});
			//save the amendment motion
			$('#submit').click(function() {
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
	        	$.post($('form').attr('action'),  
	    	            $("form").serialize(),  
	    	            function(data){
	       					$('.tabContent').html(data);
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');	
	    					$.unblockUI();	   				 	   				
	    	            });
			});
			$("#submitmotion").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});				
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			        	$.post($('form').attr('action')+'?operation=submit',  
			    	            $("form").serialize(),  
			    	            function(data){
			       					$('.tabContent').html(data);
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	   				 	   				
			    	            });
			        }
				}});						
				return false;
		    });
		});		
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
	<div id="billDiv">
		<form:form action="billamendmentmotion" method="POST" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<div>
		<h2><spring:message code="billamendmentmotion.new.heading" text="Enter Bill Amendment Details"/>	
		</h2>
		<form:errors path="version" cssClass="validationError"/>
		
		<security:authorize access="hasAnyRole('BAMOIS_TYPIST')">	
		<p>
			<label class="small"><spring:message code="billamendmentmotion.number" text="Motion Number"/>*</label>
			<input type="text" class="sInteger" id="number" name="number" value="${formattedNumber}">
			<form:errors path="number" cssClass="validationError"/>
			<span id='numberError' style="display: none; color: red;">
				<spring:message code="Number.domain.NonUnique" text="Duplicate Number"></spring:message>
			</span>
			<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
		</p>
		</security:authorize>	
		
		<p style="display:none;">
			<label class="small"><spring:message code="billamendmentmotion.houseType" text="House Type"/>*</label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input id="houseType" name="houseType" value="${houseType}" type="hidden">
			<form:errors path="houseType" cssClass="validationError"/>			
		</p>	
	
		<p style="display:none;">
			<label class="small"><spring:message code="billamendmentmotion.year" text="Year"/>*</label>
			<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
			<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
		</p>
	
		<p style="display:none;">
			<label class="small"><spring:message code="billamendmentmotion.sessionType" text="Session Type"/>*</label>		
			<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
			<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
			<input type="hidden" id="session" name="session" value="${session}"/>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
	
		<p style="display:none;">
			<label class="small"><spring:message code="billamendmentmotion.deviceType" text="Device Type"/>*</label>
			<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
			<input id="type" name="type" value="${deviceType}" type="hidden">
			<input id="originalType" name="originalType" value="${deviceType}" type="hidden">			
			<form:errors path="type" cssClass="validationError"/>		
		</p>	
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
		<p>
			<label class="small"><spring:message code="billamendmentmotion.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
			<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
			<form:errors path="primaryMember" cssClass="validationError"/>	
			<label class="small"><spring:message code="billamendmentmotion.primaryMemberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
		</p>
		</security:authorize>
		<security:authorize access="hasAnyRole('BAMOIS_TYPIST')">		
		<p>
			<label class="small"><spring:message code="billamendmentmotion.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
			<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
			<form:errors path="primaryMember" cssClass="validationError"/>		
		</p>	
		</security:authorize>
		
		<p>
			<label class="centerlabel"><spring:message code="billamendmentmotion.supportingMembers" text="Supporting Members"/></label>
			<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
			<%-- <label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="billamendmentmotion.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label> --%>										
			<c:if test="${!(empty supporingMembers)}">		
			<select  name="selectedSupportingMembers" multiple="multiple">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" class="${i.member.getFullname()}"></option>
			</c:forEach>		
			</select>
			</c:if>
			<form:errors path="supportingMembers" cssClass="validationError"/>	
		</p>	
		
		<p>	
			<label class="small"><spring:message code="billamendmentmotion.amendedBill" text="Amended Bill"/></label>
			<a id="showAmendedBill" href="#"></a>
			<span id="showAmendedBillText" style="display:none;"></span>
		</p>
		
		<div style="margin-top: 20px;">
			<fieldset>
				<p style="margin-bottom: 10px;">
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${sectionAmendments}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleSectionAmendment" id="toggleSectionAmendment_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleSectionAmendment" id="toggleSectionAmendment_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
					</c:when>
				</c:choose>					
				</c:forEach>
				</p>						
				<div id="sectionAmendments_div">
					<c:forEach var="i" items="${sectionAmendments}">
						<a href="#" id="scrollToSectionAmendment_${i.language.type}"></a>
						<div id="sectionAmendment_para_${i.language.type}" style="display:none;">
							<p>
								<label class="small">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.sectionNumber" text="Section Number"/></label>
								<input class="sText sectionAmendment_sectionNumber" id="sectionAmendment_sectionNumber_${i.language.type}" name="sectionAmendment_sectionNumber_${i.language.type}" value="${i.sectionNumber}"/>
								<a href="#" id="referSectionText_${i.language.type}" class="referSectionText" style="margin-left: 10px;text-decoration: none;display:none;">		
									<img id="referSectionTextImage_${i.language.type}" src="./resources/images/Ico_Refer2.jpg" title="<spring:message code='billamendmentmotion.sectionAmendment.referSectionText' text='Refer Section Text'/>" class="imageLink" />
								</a>
							</p>
							<p>
								<a href="#" id="referBillDraft_${i.language.type}" class="referBillDraft" style="margin-left: 162px;text-decoration: none;">
									<img id="referBillDraftImage_${i.language.type}" src="./resources/images/Ico_Refer1.jpg" title="<spring:message code='billamendmentmotion.sectionAmendment.referBillDraft' text='Refer Bill Draft'/>" class="imageLink" />
								</a>
							</p>
							<a href="#" id="scrollToReferredBillDraft_${i.language.type}"></a>
							<p id="referredBillDraftPara_${i.language.type}" style="display: none;">
								<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.referredBillDraft" text="Referred Bill Draft"/></label>
								<textarea class="wysiwyg invalidFormattingAllowed" id="referredBillDraft_${i.language.type}"></textarea>
							</p>
							<a href="#" id="scrollToReferredSectionText_${i.language.type}"></a>
							<p id="referredSectionTextPara_${i.language.type}" style="display: none;">
								<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.referredSectionText" text="Referred Section Text"/></label>
								<textarea class="wysiwyg invalidFormattingAllowed" id="referredSectionText_${i.language.type}">${requestScope[referredSectionText]}</textarea>
							</p>
							<p>
								<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.amendingContent" text="Amendment Content"/></label>
								<textarea class="wysiwyg sectionAmendment" id="sectionAmendment_amendingContent_${i.language.type}" name="sectionAmendment_amendingContent_${i.language.type}">${i.amendingContent}</textarea>
							</p>
							<input type="hidden" name="sectionAmendment_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="sectionAmendment_language_id_${i.language.type}" value="${i.language.id}">
							<input type="hidden" name="sectionAmendment_amendedSection_id_${i.language.type}" value="${i.amendedSection.id}">				
						</div>
					</c:forEach>
				</div>
			</fieldset>
		</div>		
		</div>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
					<input id="sendforapproval" type="button" value="<spring:message code='billamendmentmotion.sendforapproval' text='Send For Approval'/>" class="butDef">
				</security:authorize>	
				<input id="submitmotion" type="button" value="<spring:message code='billamendmentmotion.submitMotion' text='Submit Motion'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
			</p>
		</div>	
		<form:hidden path="version" />
		<form:hidden path="locale"/>
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">		
		<input id="amendedBill" name="amendedBill" value="${amendedBill}" type="hidden">
		<input id="amendedBillLanguages" name="amendedBillLanguages" value="${amendedBillLanguages}" type="hidden">
		</form:form>
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='billamendmentmotion.client.prompt.submit' text='Do you want to submit the billamendmentmotion'></spring:message>" type="hidden">	
		<input id="defaultBillLanguage" value="${defaultBillLanguage}" type="hidden">
		<input id="referSectionTextImageTitle" value="<spring:message code='billamendmentmotion.referSectionTextImageTitle' text='Refer Section Text'/>" type="hidden">
		<input id="deReferSectionTextImageTitle" value="<spring:message code='billamendmentmotion.deReferSectionTextImageTitle' text='De-Refer Section Text'/>" type="hidden">
		<input id="amendedBillInfo" value="${amendedBillInfo}" type="hidden">
	</div>
	</div>	
</body>
</html>