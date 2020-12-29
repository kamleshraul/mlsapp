<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotion" text="CutMotion Information System"/>
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
		/**** new cutmotion ****/
		function newCutMotion_ForNew() {
			showTabByIdAndUrl('details_tab','cutmotion/new?'+$("#gridURLParams_ForNew").val());
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
			
			/**** SubDepartment Changes (Validate as per Department Priorities) ****/
			$("#subDepartment").change(function(){
				if($(this).val()!=''){
					var subDepartment = $(this).val();
					$.get('ref/cutmotion/validate_department?subDepartment='+subDepartment+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),
							function(result){
						if(result=='unavailable') {
							$.prompt($("#subDepartment option[value="+subDepartment+"]").text() + ": " + $('#departmentUnavailableForDiscussionMsg').val());
							$("#subDepartment").val("");
							return false;
						} else if(result=='expired') {
							$.prompt($("#subDepartment option[value="+subDepartment+"]").text() + ": " + $('#submissionTimeForDepartmentOverMsg').val());
							$("#subDepartment").val("");
							return false;
						}
					});
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
				/*if($('#usergroupType').val()=='member') {
					var restrictedSubDepartmentIds = "8,9,10,56,3100,3101,11,12,13,46,2756,2757,19,2801,3050,16,17,3200,14,15".split(",");
					for(var i=0; i<restrictedSubDepartmentIds.length; i++) {
						if($('#subDepartment').val()==restrictedSubDepartmentIds[i]) {
							$.prompt($('#submissionTimeForDepartmentOverMsg').val());
							return false;
						}
					}
				}*/
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
			    $.get('cutmotion/status/'+$("#id").val(),function(data){
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
		    
			if($("#currentStatus").val()=='motion_submit'){
				$("#ministry").attr("disabled","disabled");
				$("#subDepartment").attr("disabled","disabled");
				$("#mainTitle").attr("readonly","readonly");
				$("#details").attr("readonly","readonly");
				$("#selectedSupportingMembers").attr("readonly","readonly");			
			}	
			
			$('#new_record_ForNew').click(function(){	
				newCutMotion_ForNew();
			});
					
			/**** To prevent copy paste in supporting member field ****/
			$("#selectedSupportingMembers").bind('copy paste', function (e) {
			       e.preventDefault();
			 });
		});	
	</script>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="commandbar">
		<div class="commandbarContent">
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','CMOIS_TYPIST')">
			<a href="#" id="new_record_ForNew" class="butSim">
				<spring:message code="cutmotion.new" text="New"/>
			</a> |
			</security:authorize>
		</div>
	</div>
	<div class="fields clearfix watermark">
	
		<form:form action="cutmotion" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
				<h2>${formattedMotionType} ${formattedNumber}</h2>
				<form:errors path="version" cssClass="validationError"/>
				
				<c:if test="${!(empty domain.number)}">
					<p>
						<label class="small"><spring:message code="cutmotion.number" text="CutMotion Number"/>*</label>
						<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="number" name="number" value="${domain.number}" type="hidden">
						<form:errors path="number" cssClass="validationError"/>
					</p>
				</c:if>
				
				<c:if test="${!(empty domain.internalNumber)}">
					<p style="display: none;">
						<label class="small"><spring:message code="cutmotion.number" text="CutMotion Number"/>*</label>
						<input id="formattedInternalNumber" name="formattedInternalNumber" value="${formattedInternalNumber}" class="sText" readonly="readonly">		
						<input id="internalNumber" name="internalNumber" value="${domain.internalNumber}" type="hidden">
						<form:errors path="internalNumber" cssClass="validationError"/>
					</p>
				</c:if>		
	
				<c:if test="${!(empty submissionDate)}">
					<p>
						<label class="small"><spring:message code="cutmotion.submissionDate" text="Submitted On"/></label>
						<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
						<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
					</p>
				</c:if>
	
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.houseType" text="House Type"/>*</label>
					<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
					<input id="houseType" name="houseType" value="${houseType}" type="hidden">
					<form:errors path="houseType" cssClass="validationError"/>			
				</p>	
				
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.year" text="Year"/>*</label>
					<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
					<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
				</p>
				
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.sessionType" text="Session Type"/>*</label>		
					<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
					<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
					<input type="hidden" id="session" name="session" value="${session}"/>
					<form:errors path="session" cssClass="validationError"/>	
				</p>
	
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.type" text="Type"/>*</label>
					<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
					<input id="deviceType" name="deviceType" value="${motionType}" type="hidden">		
					<form:errors path="deviceType" cssClass="validationError"/>		
				</p>	
		
				<p>
					<label class="small"><spring:message code="generic.primaryMember" text="Primary Member"/>*</label>
					<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
					<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">		
					<form:errors path="primaryMember" cssClass="validationError"/>		
				</p>
				
				<p>
					<label class="small"><spring:message code="generic.primaryMemberConstituency" text="Constituency"/>*</label>
					<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
				</p>	
	
				<p>
					<label class="centerlabel"><spring:message code="generic.supportingMembers" text="Supporting Members"/></label>
					<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
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
	
				<p>
					<label class="centerlabel"><spring:message code="cutmotion.mainTitle" text="Main Title"/>*</label>
					<form:textarea path="mainTitle" rows="2" cols="50"></form:textarea>
					<form:errors path="mainTitle" cssClass="validationError" />	
				</p>	
		
				<c:if test="${selectedMotionType=='motions_cutmotion_budgetary'}">
					<p style="display: none;">
						<label class="centerlabel"><spring:message code="cutmotion.secondaryTitle" text="Secondary Title"/>*</label>
						<form:textarea path="secondaryTitle" rows="2" cols="50"></form:textarea>
						<form:errors path="secondaryTitle" cssClass="validationError" />	
					</p>
				</c:if>
				
				<p>
					<label class="centerlabel"><spring:message code="cutmotion.subTitle" text="Sub Title"/></label>
					<form:textarea path="subTitle" rows="2" cols="50"></form:textarea>
					<form:errors path="subTitle" cssClass="validationError" />	
				</p>
				
				<p>
					<label class="wysiwyglabel"><spring:message code="cutmotion.noticeContent" text="Content"/>*</label>
					<form:textarea path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
					<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
				</p>
				
				<p style="display: inline;">
					<label class="small"><spring:message code="cutmotion.amountToBeDeducted" text="Deductible Amount"/>*</label>
					<input name="setAmountToBeDeducted" value="${formattedAmountToBeDeducted}" type="text" class="sText"/>
					<form:errors path="amountToBeDeducted" cssClass="validationError" />
				</p>
				
				<p style="display: inline;">
					<label class="small"><spring:message code="cutmotion.totalAmoutDemanded" text="Demanded Amount"/>*</label>
					<input name="setTotalAmoutDemanded" type="text" class="sText" value="${formattedTotalAmoutDemanded}"/>
					<form:errors path="totalAmoutDemanded" cssClass="validationError" />
				</p>
				
				<br />
				<p style="display: inline;">
					<label class="small"><spring:message code="cutmotion.pageNumber" text="Page Number"/></label>
					<form:input path="pageNumber" cssClass="sText"/>
					<form:errors path="pageNumber" cssClass="validationError" />
				</p>
				
				<c:if test="${selectedMotionType=='motions_cutmotion_supplementary'}">
					<p style="display: inline;">
						<label class="small"><spring:message code="cutmotion.itemNumber" text="Item Number"/>*</label>
						<form:input path="itemNumber" cssClass="sText"/>
						<form:errors path="itemNumber" cssClass="validationError" />
					</p>
				</c:if>
				
				<p>
					<label class="small"><spring:message code="cutmotion.demandNumber" text="Demand Number"/>*</label>
					<form:input path="demandNumber" cssClass="sText"/>
					<form:errors path="demandNumber" cssClass="validationError" />
				</p>
	
				<p id="internalStatusDiv">
					<label class="small"><spring:message code="generic.status" text="Current Status"/></label>
					<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly" class="sText">
				</p>
				
				<c:choose>
					<c:when test="${! empty ministries}">
						<p style="display: inline;">
							<label class="small"><spring:message code="cutmotion.ministry" text="Ministry"/>*</label>
							<select name="ministry" id="ministry" class="sSelect">
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
							<form:errors path="ministry" cssClass="validationError"/>
							<input type="hidden" name="department" value="${domain.department.id}"/>
						</p>
						<p style="display: inline;">
							<label class="small"><spring:message code="cutmotion.department" text="Department"/></label>
							<select name="subDepartment" id="subDepartment" class="sSelect">
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
							<form:errors path="subDepartment" cssClass="validationError"/>						
						</p>
					</c:when>	
					<c:otherwise>		
						<div class="toolTip tpGreen clearfix">
							<p>
								<img src="./resources/images/template/icons/light-bulb-off.png">
								<spring:message code="rotationordernotpublished" text="Follwoing fields will be activated on {0}(Rotation Order Publishing Date)" arguments="${rotationOrderPublishDate}"/>
							</p>
							<p></p>
						</div>			
					</c:otherwise>
				</c:choose>		
			 </div>
			 <div class="fields">
				<h2></h2>
				<c:choose>
					<c:when test="${memberStatusType=='cutmotion_complete' or memberStatusType=='cutmotion_incomplete'}">
						<p class="tright">
							<security:authorize access="hasAnyRole('CMOIS_CLERK')">	
								<input id="submitCutMotion" type="button" value="<spring:message code='cutmotion.submitmotion' text='Submit Motion'/>" class="butDef">			
							</security:authorize>
							<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
								<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
								<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef">
								<input id="submitCutMotion" type="button" value="<spring:message code='cutmotion.submitmotion' text='Submit Motion'/>" class="butDef">
								<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
							</security:authorize>			
						</p>
					</c:when>	
					<c:otherwise>
						<p class="tright">
							<security:authorize access="hasAnyRole('CMOIS_CLERK')">	
								<input id="submitCutMotion" type="button" value="<spring:message code='motion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">				
							</security:authorize>			
							<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
								<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
								<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
								<input id="submitCutMotion" type="button" value="<spring:message code='cutmotion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">
								<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef" disabled="disabled">
							</security:authorize>
						</p>
					</c:otherwise>
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
		<input id="submissionTimeForDepartmentOverMsg" value="<spring:message code='cutmotion.submissionTimeForDepartmentOverMsg' text='Submission Time for This Department is over now!'/>" type="hidden">
		<input id="departmentUnavailableForDiscussionMsg" value="<spring:message code='cutmotion.departmentUnavailableForDiscussionMsg' text='This Department is unavailable for discussion!'/>" type="hidden">
	</div>
</body>
</html>