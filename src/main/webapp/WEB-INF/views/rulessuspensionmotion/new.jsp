<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="rulessuspensionmotion_${houseTypeType}" text="Rules Suspension Motion"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
		}
		 .ui-combobox-toggle {
		    position: absolute;
		    top: 0;
		    bottom: 0;
		    margin-left: -1px;
		    padding: 0;
		    /* support: IE7 */
		    *height: 1.7em;
		    *top: 0.1em;
 		 }
		  .ui-combobox-input {
		    margin: 0;
		    padding: 0.3em;
		  }
	</style>
	
	<script type="text/javascript"><!--
	//this is for autosuggest
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	var primaryMemberControlName=$(".autosuggest").attr("id");
	
	/**** Load Sub Departments ****/
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
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
				}
				$('#subDepartments').empty();
				$('#subDepartments').html(subDepartmentText);
				//$('#subDepartments').multiSelect();
			}
		});
	}
	
	
	function removeFormattingFromDetails(callBack){		
		var detailsBox=$('textarea#noticeContent');
		if(detailsBox!==undefined && detailsBox!==null){				
			var noticeContentText=$.wysiwyg.getContent(detailsBox);
			if(noticeContentText!==undefined && noticeContentText!==null && noticeContentText!==''){
				var cleanText=cleanFormatting(noticeContentText);
				$.wysiwyg.setContent(detailsBox,cleanText);
			}
		}
		
		callBack();
	}
	
	$(document).ready(function(){
		
		initControls();
		
		$('#ministries').change(function(){
			if($(this).val() != null && $(this).val()!=''){
				loadSubDepartment($(this).val());
			}else{
				$("#subDepartments").empty();					
			}
		});
		
						
		
		//autosuggest		
		$( "#formattedPrimaryMember").autocomplete({
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
			$("select[name='" + controlName + "'] option:selected").each(function(){
				var optionClass = $(this).attr("class");
				if(value.indexOf(optionClass) == -1){
					$("select[name='" + controlName + "'] option[class='" + optionClass + "']").remove();
				}		
			});	
			$("select[name='"+controlName+"']").hide();				
		});
		
		//http://api.jqueryui.com/autocomplete/#event-select
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:3,
			source: function( request, response ) {
				$.getJSON( 'ref/member/supportingmembers?'+
						'session=' + $("#session").val() +
						'&primaryMemberId='+ $('#primaryMember').val(), {
					term: extractLast( request.term )
				}, response ).fail(function(){
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
				if($("select[name ='" + controlName + "']").length>0){
					if($("select[name ='" + controlName + "'] option[value='" + ui.item.id + "']").length > 0){
					//if option being selected is already present then do nothing
					this.value = $(this).val();					
					$("select[name ='" + controlName + "']").hide();						
					}else{
					//if option is not present then add it in select box and autocompletebox
					if(ui.item.id != undefined && ui.item.value != undefined){
					var text ="<option value ='"+ ui.item.id +"' selected='selected' class = '"
								+ ui.item.value + "'></option>";
					$("select[name='" + controlName + "']").append(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}							
					$("select[name='" + controlName + "']").hide();								
					}
				}else{
					if(ui.item.id != undefined && ui.item.value != undefined){
					text = "<select name='"+ $(this).attr("id") + "'  multiple='multiple'>";
					textoption = "<option value='" + ui.item.id + "' selected='selected'"+
						" class='" + ui.item.value + "'></option>";				
					text = text + textoption + "</select>";
					$(this).after(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}	
					$("select[name='" + controlName + "']").hide();									
				}		
				return false;
			}
		});	
		
		//save the state of adjournment motion
		$("#submit").click(function(e){
			$('#ruleSuspensionDate').removeAttr('disabled');
			
			e.preventDefault();
			removeFormattingFromDetails(function() {
				$("#submit").unbind('click').click();
			});
			
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
			$.post($('form').attr('action'), $("form").serialize(), function(data){
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
		});
		
		//send for approval
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
			
			removeFormattingFromDetails(function(){/*blank function*/});
			
			$.prompt($('#sendForApprovalMsg').val()+$("#selectedSupportingMembers").val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$('#ruleSuspensionDate').removeAttr('disabled');
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
		
		//send for submission
		$("#submitmotion").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			
			removeFormattingFromDetails(function(){/*blank function*/});
			
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$('#ruleSuspensionDate').removeAttr('disabled');
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
		    $.get('rulessuspensionmotion/status/'+$("#id").val(),function(data){
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
		
		/**** To prevent the copy paste in supporting members field ****/
		$("#selectedSupportingMembers").bind('copy paste', function (e) {
		       e.preventDefault();
		 });
		

		$('#number').change(function(){
			$('#numberError').css('display','none');
			if($(this).val()!="") {
				$.get('ref/rulessuspensionmotion/duplicatenumber?'+'number='+$(this).val()
						+'&ruleSuspensionDate='+$('#ruleSuspensionDate').val(), function(data){
					if(data){
						$('#numberError').css('display','inline');
					}else{
						$('#numberError').css('display','none');
					}
				});
			}
		});
		
		$('#changeRulesSuspensionDate').click(function() {
			var yesLabel = $('#yesLabel').val();
			var noLabel = $('#noLabel').val();
			$.prompt('Do you really want to change the adjourning date?', {
				buttons: [
					{title: yesLabel, value: true},
					{title: noLabel, value: false}
				],
				callback: function(v) {
					if(v) {
						$('#ruleSuspensionDate').removeAttr('disabled');
						$('#changeRulesSuspensionDate').hide();
					} else {
						return false;
					}
				}
			});			
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

		<form:form action="rulessuspensionmotion" method="POST" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2><spring:message code="rulessuspensionmotion_${houseTypeType}.new.heading" text="Enter New Rules Suspension Motion Details"/></h2>
			<form:errors path="version" cssClass="validationError"/>
			
			<security:authorize access="hasAnyRole('RSMOIS_TYPIST')">	
			<p>
				<label class="small"><spring:message code="rulessuspensionmotion.number" text="Motion Number"/>*</label>
				<form:input path="number" cssClass="sText"/>
				<form:errors path="number" cssClass="validationError"/>
				<span id='numberError' style="display: none; color: red;">
					<spring:message code="MotionNumber.domain.NonUnique" text="Duplicate Number"></spring:message>
				</span>
				<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
			</p>
			</security:authorize>	
				
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.type" text="Type"/>*</label>
				<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
				<input id="type" name="type" value="${motionType}" type="hidden">
				<form:errors path="type" cssClass="validationError"/>		
			</p>	
				
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<p>
					<label class="small"><spring:message code="rulessuspensionmotion.primaryMember" text="Primary Member"/>*</label>
					<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
					<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
					<form:errors path="primaryMember" cssClass="validationError"/>	
				</p>
				<p>
					<label class="small"><spring:message code="rulessuspensionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
					<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
				</p>
			</security:authorize>
			<security:authorize access="hasAnyRole('RSMOIS_TYPIST')">		
				<p>
					<label class="small"><spring:message code="rulessuspensionmotion.primaryMember" text="Primary Member"/>*</label>
					<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
					<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
					<form:errors path="primaryMember" cssClass="validationError"/>	
				</p>	
			</security:authorize>
			
			<p>
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.supportingMembers" text="Supporting Members"/></label>
				<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
				<%-- <label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="rulessuspensionmotion.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label> --%>										
				<c:if test="${!(empty supporingMembers)}">		
					<select  name="selectedSupportingMembers" multiple="multiple">
						<c:forEach items="${supportingMembers}" var="i">
							<option value="${i.id}" class="${i.getFullname()}"></option>
						</c:forEach>		
					</select>
				</c:if>
				<form:errors path="supportingMembers" cssClass="validationError"/>
			</p>
			
			<h2></h2>
			
			<p>
				<label class="small"><spring:message code="rulessuspensionmotion.selectrulessuspensiondate" text="Rules Suspension Date"/></label>
				<select name="ruleSuspensionDate" id="ruleSuspensionDate" style="width:130px;height: 25px;" disabled="disabled">
					<c:forEach items="${sessionDates}" var="i">
						<option value="${i[0]}" ${i[0]==defaultRuleSuspensionDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
					</c:forEach>
				</select>
				<a href="#" id="changeRulesSuspensionDate" style="margin-left: 10px;"><spring:message code="rulessuspensionmotion.changeRulesSuspensionDate" text="Change Rules Suspension Date"/></a>
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.shortSubjectMessage" text="Please Write in summary form"/>*</label>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.noticeContent" text="Notice Content"/>*</label>
				<form:textarea path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
				<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>	
			
			<table>
				<c:choose>
					<c:when test="${! empty ministries}">
						<tr>
							<td style="vertical-align: top;">
								<p>
									<label class="centerlabel"><spring:message code="rulessuspensionmotion.ministry" text="Ministry"/></label>
									<select class="sSelectMultiple" name="ministries" id="ministries" multiple="multiple" size="5" style="width:250px;">
										<c:forEach items="${ministries}" var="i">
											<c:set var="selectedMinistry" value="no"></c:set>
											<c:forEach items="${selectedministries}" var="j">
												<c:if test="${j.id==i.id}">
													<c:set var="selectedMinistry" value="yes"></c:set>
												</c:if>
											</c:forEach>
											<c:choose>
												<c:when test="${selectedMinistry=='yes'}">
													<option selected="selected" value="${i.id}">${i.dropdownDisplayName}</option>
												</c:when>
												<c:otherwise>
													<option value="${i.id}">${i.dropdownDisplayName}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select> 	 
									<form:errors path="ministries" cssClass="validationError"/>
								</p>
							</td>
							<td>
								<p>
									<label class="centerlabel" style="margin-left: 10px;"><spring:message code="rulessuspensionmotion.department" text="Department"/></label>
									<select name="subDepartments" id="subDepartments" multiple="multiple" size="5" class="sSelectMultiple" style="max-width: 200px !important;">
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
								</p>	
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
			</div>	
			 <div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
						<input id="sendforapproval" type="button" value="<spring:message code='rulessuspensionmotion.sendforapproval' text='Send For Approval'/>" class="butDef">
					</security:authorize>
					<input id="submitmotion" type="button" value="<spring:message code='rulessuspensionmotion.submitmotion' text='Submit Motion'/>" class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
			</div>
			<form:hidden path="version" />
			<form:hidden path="locale"/>
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
		</form:form>

		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		
		<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.rulessuspensionmotion.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
		<input id="subjectEmptyMsg" value='<spring:message code="client.error.rulessuspensionmotion.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
		<input id="noticecontentEmptyMsg" value='<spring:message code="client.error.rulessuspensionmotion.noticecontentEmptyMsg" text="Notice Content can not be empty."></spring:message>' type="hidden">
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='rulessuspensionmotion.submissionMsg' text='Do you want to submit the Rule Suspension motion?'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>