<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="eventmotion" text="EventMotion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		.toolbar{
			display: none !important;
		}
		#details{
			width: 400px;
		}
		.wysiwyg{
			margin-left: 160px;
			position: static !important;
			overflow: visible !important; 
			
		}
		
	</style>
	
	<script type="text/javascript">
		//this is for autosuggest
		function split( val ) {
			return val.split( /,\s*/ );
		}	
		function extractLast( term ) {
			return split(term).pop();
		}	
		var controlName=$(".autosuggestmultiple").attr("id");
		/**** Load Sub Departments ****/
		function loadSubDepartments(ministry){
			$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
					function(data){
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#subDepartment").html(subDepartmentText);			
				}else{
					$("#subDepartment").empty();
					var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
					$("#subDepartment").html(subDepartmentText);				
				}
			});
		}		
		$(document).ready(function(){
			/**** Ministry Changes ****/	
			$("#ministry").change(function(){
				if($(this).val()!=''){
					loadSubDepartments($(this).val());
				}else{
					$("#subDepartment").empty();				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
				}
			});	
					
			//initially only minsitry will be visible as either disabled or enabled
			if($("#ministrySelected").val()==''){
				$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}else{
				$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}		
			if($("#subDepartmentSelected").val()==''){
				$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}else{
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}			
			/**** Auto Suggest(clerk login)-Primary Member ****/		
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
					}, response ).fail(function(){
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});
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
			
			/**** send for approval ****/
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
			    	            }).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				});
			        }
				}});			
		        return false;  
		    }); 
			/**** send for submission ****/
			$("#submitCutMotion").click(function(e){			
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
			    	            }).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				});
			        }
				}});			
		        return false;  
		    }); 
		    //view supporting members status
		    $("#viewStatus").click(function(){
			    $.get('eventmotion/status/'+$("#id").val(),function(data){
				    $.fancybox.open(data);
			    }).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			    return false;
		    });  
		    
		    /**** send for submission ****/
			$("#submitEventMotion").click(function(e){			
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
			    	            }).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				});
			        }
				}});			
		        return false;  
		    }); 
		    
			if($("#currentStatus").val()=='eventmotion_submit'){
				$("#ministry").attr("disabled","disabled");
				$("#subDepartment").attr("disabled","disabled");
				$("#eventTitle").attr("readonly","readonly");
				$("#details").attr("readonly","readonly");
				$("#selectedSupportingMembers").attr("readonly","readonly");			
			}	
					
			$("#number").change(function(){
				$.get('ref/eventmotionbynumberandsession?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),function(data){
					if(data){
						$('#numberError').css('display','inline');
					}else{
						$('#numberError').css('display','none');
					}
				});
			});
			
			/**** To prevent copy paste in supporting member field ****/
			$("#selectedSupportingMembers").bind('copy paste', function (e) {
			       e.preventDefault();
			 });
			
			//window.scrollTo($("#formattedNumber").offset().left,$("#formattedNumber").offset().top);
		});	
	</script>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	
	<div class="fields clearfix watermark">
	
		<form:form action="eventmotion" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
				<h2>${formattedMotionType} ${formattedNumber}</h2>
				<form:errors path="version" cssClass="validationError"/>
				<div>
					<c:choose>
						<c:when test="${!(empty domain.number)}">
							<p style="display: inline-block;">
								<label class="small"><spring:message code="eventmotion.number" text="Event Number"/>*</label>
								<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
								<input id="number" name="number" value="${domain.number}" type="hidden">
								<form:errors path="number" cssClass="validationError"/>
							</p>
						</c:when>
						<c:when test="${empty(domain.number)}">
							<security:authorize access="hasAnyRole('EMOIS_CLERK','EMOIS_TYPIST')">	
								<p>
									<label class="small"><spring:message code="eventmotion.number" text="Motion Number"/>*</label>
									<form:input path="number" cssClass="sText integer"/>
									<form:errors path="number" cssClass="validationError"/>
									<span id='numberError' style="display: none; color: red;">
										<spring:message code="eventMotion.domain.NonUnique" text="Duplicate Number"></spring:message>
									</span>
									<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
								</p>					
							</security:authorize>
						</c:when>
					</c:choose>				
		
					<c:if test="${!(empty submissionDate)}">
						<p style="display: inline-block;">
							<label class="small"><spring:message code="cutmotion.submissionDate" text="Submitted On"/></label>
							<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
							<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
						</p>
					</c:if>
				</div>
	
				<p style="display:none;">
					<label class="small"><spring:message code="eventmotion.houseType" text="House Type"/>*</label>
					<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
					<input id="houseType" name="houseType" value="${houseType}" type="hidden">
					<form:errors path="houseType" cssClass="validationError"/>			
				</p>	
				
				<p style="display:none;">
					<label class="small"><spring:message code="eventmotion.year" text="Year"/>*</label>
					<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
					<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
				</p>
				
				<p style="display:none;">
					<label class="small"><spring:message code="eventmotion.sessionType" text="Session Type"/>*</label>		
					<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
					<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
					<input type="hidden" id="session" name="session" value="${session}"/>
					<form:errors path="session" cssClass="validationError"/>	
				</p>
	
				<p style="display:none;">
					<label class="small"><spring:message code="eventmotion.type" text="Type"/>*</label>
					<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
					<input id="deviceType" name="deviceType" value="${motionType}" type="hidden">		
					<form:errors path="deviceType" cssClass="validationError"/>		
				</p>	
		
				<div>
					<c:choose>
						<c:when test="${not (domain.exMemberEnabled)}">
							<p style="display: inline-block;">
								<label class="small"><spring:message code="generic.primaryMember" text="Primary Member"/>*</label>
								<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
								<input id="member" name="member" value="${primaryMember}" type="hidden">		
								<form:errors path="member" cssClass="validationError"/>		
							</p>
							
							<p style="display: inline-block;">
								<label class="small"><spring:message code="generic.primaryMemberConstituency" text="Constituency"/>*</label>
								<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
							</p>
						</c:when>
						<c:when test="${domain.exMemberEnabled}">
							<p style="display: inline-block;">
								<label class="small"><spring:message code="generic.public" text="Public Body"/>*</label>
								<input id="exMember" name="exMember"  value="${domain.exMember}" type="text" class="sText"  readonly="readonly" class="sText">		
							</p>							
						</c:when>
					</c:choose>
				</div>	
	
				<c:if test="${selectedMotionType=='motions_eventmotion_congratulatory'}">
					<p>
						<label class="centerlabel"><spring:message code="generic.supportingMembers" text="Supporting Members"/></label>
						<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="72">${supportingMembersName}</textarea>
						<c:if test="${!(empty supportingMembers)}">
							<select  name="selectedSupportingMembers" multiple="multiple">
								<c:forEach items="${supportingMembers}" var="i">
									<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
								</c:forEach>		
							</select>
						</c:if>
						<a href="#" id="viewStatus"><spring:message code="generic.support.approval.viewstatus" text="View Status"></spring:message></a>
						<form:errors path="supportingMembers" cssClass="validationError"/>	
					</p>	
				</c:if>
				
				<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">	
					<div>
						<p style="display: inline-block;">
							<label class="small"><spring:message code="eventmotion.designationOfPerson" text="Designation"/>*</label>
							<input name="designationOfPerson" id="designationOfPerson" type="text" value="${domain.designationOfPerson}" class="sText">		
							<form:errors path="designationOfPerson" cssClass="validationError"/>
						</p>
						
						<p style="display: inline-block;">
							<label class="small"><spring:message code="eventmotion.constituencyOfPerson" text="Contituency"/>*</label>
							<input name="constituencyOfPerson" id="constituencyOfPerson" type="text" value="${domain.constituencyOfPerson}" class="sText">		
							<form:errors path="constituencyOfPerson" cssClass="validationError"/>		
						</p>
					</div>
				</c:if>
				
				<div>
					<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
						<p style="display: inline-block;">
							<label class="small"><spring:message code="eventmotion.tenureOfPerson" text="Tenure"/>*</label>
							<input name="tenureOfPerson" id="tenureOfPerson" type="text" value="${domain.tenureOfPerson}" class="sText">		
							<form:errors path="tenureOfPerson" cssClass="validationError"/>
						</p>
					</c:if>
					<p style="display: inline-block;">
						<label class="small"><spring:message code="eventmotion.eventDate" text="Event Date"/>*</label>
						<input name="formattedEventDate" id="formattedEventDate" type="text" value="${formattedEventDate}" class="sText datemask">
						<input name="eventDate" id="eventDate" type="hidden" value="${eventDate}">		
						<form:errors path="eventDate" cssClass="validationError"/>
					</p>
				</div>
				
				<p>
					<label class="centerlabel"><spring:message code="eventmotion.eventTitle" text="Event Name"/>*</label>
					<form:textarea path="eventTitle" rows="2" cols="72"></form:textarea>
					<form:errors path="eventTitle" cssClass="validationError" />	
				</p>	
				
				<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
					<p>
						<label class="centerlabel"><spring:message code="eventmotion.eventReason" text="Event Reason"/>*</label>
						<form:textarea path="eventReason" rows="2" cols="72"></form:textarea>
						<form:errors path="eventReason" cssClass="validationError" />
					</p>	
				</c:if>
				
				<p>
					<label class="wysiwyglabel"><spring:message code="eventmotion.description" text="Descrtiption"/>*</label>
					<form:textarea path="description" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
					<form:errors path="description" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
				</p>	
										
				<p>
					<label class="small"><spring:message code="eventmotion.isHouseAdjourned" text="House Adjourned?"/>*</label>
					<c:choose>
						<c:when test="${domain.isHouseAdjourned}">
							<form:checkbox path="isHouseAdjourned" ></form:checkbox>
						</c:when>
						<c:otherwise>
							<form:checkbox path="isHouseAdjourned"></form:checkbox>
						</c:otherwise>
					</c:choose>
					<form:errors path="isHouseAdjourned" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
				</p>
				<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
					<p>
						<label class="small"><spring:message code="eventmotion.collectorReport" text="Collector Report"/></label>
						<span id="image_gallery" style="display: inline;margin: 0px;padding: 0px;">
							<img alt="" src="" id="image_photo" width="70" height="70">
						</span>
						<c:choose>		
						<c:when test="${empty domain.collectorReport}">
						<jsp:include page="/common/file_upload.jsp">
							<jsp:param name="fileid" value="collectorReport" />
						</jsp:include>
						</c:when>
						<c:otherwise>		
						<jsp:include page="/common/file_download.jsp">
							<jsp:param name="fileid" value="collectorReport" />
							<jsp:param name="filetag" value="${domain.collectorReport}" />
						</jsp:include>
						</c:otherwise>
						</c:choose>		
						<form:errors path="collectorReport" cssClass="validationError" />
					</p>
				</c:if>
			 </div>
			
			<div>
				<p>
					<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
					<form:select path="discussionDate" cssClass="datemask sSelect" >
						<option value="">---<spring:message code='please.select' text='Please Select'/>---</option>
						<c:forEach items="${discussionDates}" var="i">
							<c:choose>
								<c:when  test="${i.value==discussionDateSelected}">
									<option value="${i.value}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.value}">${i.name}</option>
								</c:otherwise>					
							</c:choose>
						</c:forEach>					
					</form:select>
					<form:errors path="discussionDate" cssClass="validationError"/>
				</p>
			</div>
			
			 <div class="fields">
				<h2></h2>
				<c:choose>
					<c:when test="${memberStatusType=='eventmotion_complete' or memberStatusType=='eventmotion_incomplete'}">
						<p class="tright">
							<security:authorize access="hasAnyRole('EMOIS_CLERK', 'EMOIS_TYPIST')">	
								<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">			
								<input id="submitEventMotion" type="button" value="<spring:message code='eventmotion.submitmotion' text='Submit Motion'/>" class="butDef">				
							</security:authorize>
							<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
								<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
								<c:if test="${selectedMotionType=='motions_eventmotion_congratulatory'}">
									<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef">
								</c:if>
								<input id="submitEventMotion" type="button" value="<spring:message code='eventmotion.submitmotion' text='Submit Motion'/>" class="butDef">
								<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
							</security:authorize>			
						</p>
					</c:when>	
					<%-- <c:otherwise>
						<p class="tright">
							<c:if test="${memberStatusType!='eventmotion_submit'}">
								<security:authorize access="hasAnyRole('EMOIS_CLERK', 'EMOIS_TYPIST')">	
									<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">			
									<input id="submitEventMotion" type="button" value="<spring:message code='eventmotion.submitmotion' text='Submit Motion'/>" class="butDef">				
								</security:authorize>			
								<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
									<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
									<c:if test="${selectedMotionType=='motions_eventmotion_congratulatory'}">
										<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
									</c:if>
								<input id="submitEventMotion" type="button" value="<spring:message code='eventmotion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">
								<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef" disabled="disabled">
							</security:authorize>
							</c:if>	
						</p>
					</c:otherwise> --%>
				</c:choose>		
			</div>
			<form:hidden path="version" />
			<form:hidden path="id"/>
			<form:hidden path="locale"/>	
			<input type="hidden" name="status" id="status" value="${status }">
			<input id="bulkedit" name="bulkedit" value="no" type="hidden">	
			<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
			<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
			<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
			<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
			<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
		</form:form>
		
		<input id="currentStatus" value="${internalStatusType }" type="hidden">
		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="supportingMembersCountErrorMsg" value='<spring:message code="client.error.motion.limit.supportingmemebers" text="Please provide proper number of supporting members."></spring:message>' type="hidden">
		<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.motion.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
		<input id="subjectEmptyMsg" value='<spring:message code="client.error.motion.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motion.'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>