<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
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
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	
	$(document).ready(function(){		
		
$('#ministries').change(function(){
			
			if($(this).val()!=''){
				if($(this).val() != null){
					loadSubDepartment($(this).val());
				}else{
					$("#subDepartments").empty();					
				}
			}
		});
			
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
		$("#submitMotion").click(function(e){			
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
		    $.get('discussionmotion/status/'+$("#id").val(),function(data){
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
		/***** added by sandeep singh *****/
		if($("#currentStatus").val()=='discussionmotion_submit'){
			$("#ministry").attr("disabled","disabled");
			$("#subDepartment").attr("disabled","disabled");
			$("#subject").attr("readonly","readonly");
			$("#details").attr("readonly","readonly");
			$("#selectedSupportingMembers").attr("readonly","readonly");			
		}	
				
		/**** To prevent copy paste in supporting member field ****/
		$("#selectedSupportingMembers").bind('copy paste', function (e) {
		       e.preventDefault();
		 });
	});	
	
	
	function loadSubDepartment(ministry){
		$.get('ref/getSubDeparmentsByMinistries?ministries='+ ministry +'&session=' + $('#session').val(),
				function(data){
			if(data.length>0){
				var selectedSubDepartments = $('#subDepartments').val();
				var subDepartmentText='';
				for(var i=0;i<data.length;i++){
					var flag=false;
					if(selectedSubDepartments!=null && selectedSubDepartments!=''){
						for(var j=0;j<selectedSubDepartments.length;j++){
							if(selectedSubDepartments[j]==data[i].id){
								flag=true;
								break;
							}
						}
					}
					if(flag){
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}else{
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"'selected='selected'>"+data[i].name+"</option>";
					}
				}
				$('#subDepartments').empty();
				$('#subDepartments').html(subDepartmentText);
				//$('#subDepartments').multiSelect();
			}
		});
	}
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="discussionmotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<h2>${formattedDiscussionMotionType} ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p>
		<label class="small"><spring:message code="discussionmotion.number" text="Motion Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${!(empty submissionDate)}">
	<p>
	<label class="small"><spring:message code="discussionmotion.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
	</p>
	</c:if>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.type" text="Type"/>*</label>
		<input id="formattedDiscussionMotionType" name="formattedDiscussionMotionType" value="${formattedDiscussionMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${discussionMotionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
		
	<p>
		<label class="small"><spring:message code="discussionmotion.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
		
		<label class="small" style="margin-left:100px;"><spring:message code="discussionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>
	
	<p>
		<label class="centerlabel"><spring:message code="discussionmotion.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${(selectedDiscussionMotionType=='motions_discussionmotion_shortduration')
			 and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
			<label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="question.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label>										
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
		</c:forEach>		
		</select>
		</c:if>
		<a href="#" id="viewStatus"><spring:message code="discussionmotion.viewstatus" text="View Status"></spring:message></a>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="discussionmotion.subject" text="Subject"/>*</label>
		<form:textarea path="subject" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError" />	
	</p>	
		
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.noticeContent" text="Notice Content"/>*</label>
		<form:textarea path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>		 
		<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>	
	<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.briefExplanation" text="Brief Explanation"/>*</label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>
	

	<p>
	<table>
		<c:choose>
			<c:when test="${! empty ministries}">
				<tr>
				<td>		
				<label class="centerlabel"><spring:message code="discussionmotion.ministry" text="Ministry"/>*</label>
				
				</td>
					<td style="vertical-align: top;">
					
								<select name="ministries" id="ministries" multiple="multiple" size="5" style="width:200px;">
								<c:forEach items="${ministries}" var="i">
									<c:set var="selectedMinistry" value="no"></c:set>
									<c:forEach items="${selectedministries}" var="j">
										<c:if test="${j.id==i.id}">
											<c:set var="selectedMinistry" value="yes"></c:set>
										</c:if>
									</c:forEach>
									<c:choose>
										<c:when test="${selectedMinistry=='yes'}">
											<option selected="selected" value="${i.id}">${i.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${i.id}">${i.name}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select> 	 
							<form:errors path="ministries" cssClass="validationError"/>
						
					</td>
					<td>
						<label class="centerlabel" style="margin-left: 10px;"><spring:message code="discussionmotion.department" text="Department"/></label>
					
					</td>
						
					<td>
					
							<select name="subDepartments" id="subDepartments" multiple="multiple" size="5" class="sSelectMultiple" style="max-width: 188px !important;">
								<c:forEach items="${subDepartments}" var="i">
									<c:set var="selectedSubDepartment" value="no"></c:set>
									<c:forEach items="${selectedSubDepartments}" var="j">
										<c:if test="${j.id==i.id}">
											<c:set var="selectedSubDepartment" value="yes"></c:set>
										</c:if>
									</c:forEach>
									<c:choose>
										<c:when test="${selectedSubDepartment=='yes'}">
											<option selected="selected" value="${i.id}">${i.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${i.id}">${i.name}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>	
							<form:errors path="subDepartments" cssClass="validationError"/>						
							
					</td>				
				</tr>	
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
	</table>
	</p>
		<p id="internalStatusDiv">
	<label class="small"><spring:message code="discussionmotion.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly" class="sText">
	</p>		
	</div>
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${memberStatusType=='discussionmotion_complete' or memberStatusType=='discussionmotion_incomplete'}">
			<p class="tright">
			<security:authorize access="hasAnyRole('DMOIS_TYPIST')">	
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="submitMotion" type="button" value="<spring:message code='discussionmotion.submitmotion' text='Submit Motion'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="sendforapproval" type="button" value="<spring:message code='discussionmotion.sendforapproval' text='Send For Approval'/>" class="butDef">
			<input id="submitMotion" type="button" value="<spring:message code='discussionmotion.submitmotion' text='Submit Motion'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>			
		</p>
		</c:when>	
		<c:otherwise>
			<p class="tright">
				<security:authorize access="hasAnyRole('DMOIS_TYPIST')">
					<input id="submitmotion" type="button" value="<spring:message code='discussionmotion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">				
				</security:authorize>			
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
					<input id="sendforapproval" type="button" value="<spring:message code='discussionmotion.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
					<input id="submitmotion" type="button" value="<spring:message code='discussionmotion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">
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
<input id="supportingMembersCountErrorMsg" value='<spring:message code="client.error.motion.limit.supportingmemebers" text="Please provide proper number of supporting members."></spring:message>' type="hidden">
<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.motion.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
<input id="subjectEmptyMsg" value='<spring:message code="client.error.motion.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.motion.prompt.submit' text='Do you want to submit the motion.'></spring:message>" type="hidden">

</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>