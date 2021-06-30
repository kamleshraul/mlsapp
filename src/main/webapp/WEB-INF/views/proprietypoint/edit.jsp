<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proprietypoint" text="Propriety Point"/>
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
	
	//this is for loading ministries,subdepartments
	/**** Load Ministries ****/
	function loadMinistries(session){
		$.get('ref/session/' + session + '/ministries', function(data) {
			if(data.length>0){
				var ministryText = "<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>";
				for(var i=0 ; i<data.length; i++){
					ministryText += "<option value='" + data[i].id + "'>" + data[i].name;				
				}
				$("#ministry").empty();
				$("#ministry").html(ministryText);				
			}else{
				$("#ministry").empty();				
				$("#subDepartment").empty();				
			}
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	/**** Load Sub Departments ****/
	function loadSubDepartments(ministry){
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data) {
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"
				+ $("#pleaseSelectMsg").val() + "----</option>";
			if(data.length>0) {
			for(var i=0 ;i<data.length; i++){
				subDepartmentText += "<option value='" + data[i].id + "'>" + data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);			
			}else{
				$("#subDepartment").empty();
				var subDepartmentText = 
					"<option value ='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>";				
				$("#subDepartment").html(subDepartmentText);				
			}
		}).fail(function(){
			if($("#ErrorMsg").val() != ''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	$(document).ready(function(){
		initControls();
		
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").
			prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}else{
			$("#subDepartment").
			prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}		
		
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
		
		//save the state of propriety point
		$("#submit").click(function(e){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$('#proprietyPointDate').removeAttr('disabled');
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
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
			$.prompt($('#sendForApprovalMsg').val()+$("#selectedSupportingMembers").val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
					$('#proprietyPointDate').removeAttr('disabled');        
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
		$("#submitdevice").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			/*$.prompt("Submission is offline till further instructions!");
			return false;*/
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$('#proprietyPointDate').removeAttr('disabled');
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
		    $.get('proprietypoint/status/'+$("#id").val(),function(data){
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
		

		$( "#formattedMinistry").autocomplete({
			minLength:3,			
			source:'ref/getministries?session=' + $('#session').val(),
			select:function(event,ui){		
				if(ui.item != undefined) {
					$("#ministry").val(ui.item.id);
				} else {
					$("#ministry").val('');
				}
			},
			change:function(event,ui){
				if(ui.item != undefined) {
					var ministryVal = ui.item.id;						
					if(ministryVal != ''){
						loadSubDepartments(ministryVal);
					}else{
						$("#subDepartment").empty();				
						$("#subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");				
					}
				} else {
					if($( "#formattedMinistry").val() == '') {
						$("#subDepartment").empty();				
						$("#subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");
					}					
				}			
			}
		});
		
		$('#number').change(function(){
			$('#numberError').css('display','none');
			var originalNumber = $('#originalNumber').val();
			var formattedOriginalNumber = $('#formattedOriginalNumber').val();
			if(originalNumber!=undefined && originalNumber!="") {				
				if($(this).val()!="") {
					if($(this).val()!=originalNumber && $(this).val()!=formattedOriginalNumber) {
						$.get('ref/proprietypoint/duplicatenumber?'+'number='+$(this).val(), function(data){
							if(data){
								$('#numberError').css('display','inline');
							}else{
								$('#numberError').css('display','none');
							}
						});
					}
				}
			} else {
				if($(this).val()!="") {
					$.get('ref/proprietypoint/duplicatenumber?'+'number='+$(this).val(), function(data){
						if(data){
							$('#numberError').css('display','inline');
						}else{
							$('#numberError').css('display','none');
						}
					});
				}				
			}						
		});
		
	 	$('#changeProprietyPointDate').click(function() {
			var yesLabel = $('#yesLabel').val();
			var noLabel = $('#noLabel').val();
			$.prompt('Do you really want to change the propriety point date?', {
				buttons: [
					{title: yesLabel, value: true},
					{title: noLabel, value: false}
				],
				callback: function(v) {
					if(v) {
						$('#proprietyPointDate').removeAttr('disabled');
						$('#changeProprietyPointDate').hide();
					} else {
						return false;
					}
				}
			});			
		});
		
		if($("#currentStatus").val()=='proprietypoint_submit'){
			$("#ministry").attr("disabled","disabled");
			$("#subDepartment").attr("disabled","disabled");
			$("#proprietyPointDate").attr("disabled","disabled");
			$("#subject").attr("readonly","readonly");
			$("#pointsOfPropriety").attr("readonly","readonly");
		}
		
		$('#new_record_ForNew').click(function(){	
			newProprietyPoint_ForNew();
		});
	});
	/**** new motion ****/
	function newProprietyPoint_ForNew() {
		showTabByIdAndUrl('details_tab','proprietypoint/new?'+$("#gridURLParams_ForNew").val());
	}
	</script>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>

	<div class="fields clearfix watermark">

		<form:form action="proprietypoint" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<a href="#" id="new_record_ForNew" class="butSim soberFontSize">
				<spring:message code="generic.new" text="New"/>
			</a> 
			<br />
			<br />
			<h2>
				${formattedDeviceType}
				<c:choose>
					<c:when test="${not empty formattedProprietyPointDate and not empty formattedNumber}">
						(${formattedProprietyPointDate} - <spring:message code="generic.number" text="Number"/> ${formattedNumber})
					</c:when>
					<c:when test="${not empty formattedProprietyPointDate and empty formattedNumber}">
						(${formattedProprietyPointDate})
					</c:when>
					<c:when test="${not empty formattedNumber}">
						(<spring:message code="generic.number" text="Number"/> ${formattedNumber})
					</c:when>
				</c:choose>
			</h2>
			<form:errors path="version" cssClass="validationError"/>
			
			<security:authorize access="hasAnyRole('PROIS_TYPIST')">	
			<p>
				<label class="small"><spring:message code="proprietypoint.number" text="Device Number"/>*</label>
				<form:input path="number" cssClass="sText"/>
				<input type="hidden" id="originalNumber" value="${domain.number}"/>
				<input type="hidden" id="formattedOriginalNumber" value="${formattedNumber}"/>
				<form:errors path="number" cssClass="validationError"/>
				<span id='numberError' style="display: none; color: red;">
					<spring:message code="DeviceNumber.domain.NonUnique" text="Duplicate Number"></spring:message>
				</span>
				<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
			</p>
			</security:authorize>	
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE', 'MEMBER_UPPERHOUSE')">
			<c:if test="${!(empty domain.number)}">
			<p>
				<label class="small"><spring:message code="proprietypoint.number" text="Device Number"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>				
			</p>
			</c:if>		
			</security:authorize>		
			
			<c:if test="${!(empty submissionDate)}">
			<p>
				<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
			</p>
			</c:if>	
				
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.deviceType" text="Device Type"/>*</label>
				<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
				<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">
				<form:errors path="deviceType" cssClass="validationError"/>		
			</p>	
				
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
			<p>
				<label class="small"><spring:message code="proprietypoint.primaryMember" text="Primary Member"/>*</label>
				<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
				<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
				<form:errors path="primaryMember" cssClass="validationError"/>	
				<label class="small"><spring:message code="proprietypoint.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">	
			</p>
			</security:authorize>
			<security:authorize access="hasAnyRole('PROIS_TYPIST')">		
			<p>
				<label class="small"><spring:message code="proprietypoint.primaryMember" text="Primary Member"/>*</label>
				<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
				<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
				<form:errors path="primaryMember" cssClass="validationError"/>	
				<%-- <c:if test="${selectedDeviceTypeForBill != 'proprietypoints_government'}">
				<label class="small"><spring:message code="proprietypoint.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">	
				</c:if> --%>	
			</p>	
			</security:authorize>
			
			<p style="display: ${houseTypeType=='lowerhouse'?'none;':'inline-block;'}">
				<label class="small"><spring:message code="proprietypoint.ministry" text="Ministry"/></label>
				<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
				<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}">
				<%-- <form:select path="ministry" id="ministry" class="sSelect">
				<c:forEach items="${ministries}" var="i">
					<c:choose>
						<c:when test="${i.id==ministrySelected }">
							<option value="${i.id}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}" >${i.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</form:select> --%>
				<label class="small"><spring:message code="proprietypoint.subdepartment" text="Sub Department"/></label>
				<select name="subDepartment" id="subDepartment" class="sSelect">
				<c:forEach items="${subDepartments}" var="i">
					<c:choose>
						<c:when test="${i.id==subDepartmentSelected}">
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
				<form:errors path="ministry" cssClass="validationError" />	
				
				<form:errors path="subDepartment" cssClass="validationError" style="margin-left:27%"/>	
			</p>
			
			<p style="display: none;">
				<label class="centerlabel"><spring:message code="proprietypoint.supportingMembers" text="Supporting Members"/></label>
				<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
				<%-- <label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="proprietypoint.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label> --%>										
				<c:if test="${!(empty supportingMembers)}">
				<select name="selectedSupportingMembers" multiple="multiple">
				<c:forEach items="${supportingMembers}" var="i">
				<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
				</c:forEach>		
				</select>
				</c:if>
				<a href="#" id="viewStatus"><spring:message code="proprietypoint.viewstatus" text="View Status"></spring:message></a>
				<form:errors path="supportingMembers" cssClass="validationError"/>
			</p>
			
			<h2></h2>
			
			<c:if test="${houseTypeType=='upperhouse'}">
			<p>
				<label class="small"><spring:message code="proprietypoint.selectProprietyPointdate" text="Propriety Point Date"/></label>
				<%-- <input name="proprietyPointDate" id="proprietyPointDate" value="${selectedProprietyPointDate}"  style="width:130px;height: 40px;" readonly="readonly"> --%>		
				<select name="proprietyPointDate" id="proprietyPointDate" style="width:130px;height: 25px;" disabled="disabled">
				<c:forEach items="${sessionDates}" var="i">
					<option value="${i[0]}" ${i[0]==selectedProprietyPointDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
				</c:forEach>
				</select>
				<c:if test="${empty domain.number}">
					<a href="#" id="changeProprietyPointDate" style="margin-left: 10px;"><spring:message code="proprietypoint.changeProprietyPointDate" text="Change Propriety Point Date"/></a>
				</c:if>
			</p>
			</c:if>
			
			<p>
				<label class="centerlabel"><spring:message code="proprietypoint.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="proprietypoint.pointsOfPropriety" text="Points of Propriety"/>*</label>
				<form:textarea path="pointsOfPropriety" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
				<form:errors path="pointsOfPropriety" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>
			
			<c:if test="${not empty formattedMemberStatus}">
				<p id="mainStatusDiv">
					<label class="small"><spring:message code="proprietypoint.currentStatus" text="Current Status"/></label>
					<input id="formattedMemberStatus" name="formattedMemberStatus" value="${formattedMemberStatus }" type="text" readonly="readonly" class="sText">
				</p>
			</c:if>	
			
			<%-- <c:if test="${not empty sectionofficer_remark and internalStatusType=='proprietypoint_final_rejection'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
				<form:textarea path="remarks" cssClass="wysiwyg invalidFormattingAllowed" readonly="true"></form:textarea>
			</p>
			</c:if>	
			<c:if test="${recommendationStatusType == 'proprietypoint_processed_rejectionWithReason'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="bill.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg invalidFormattingAllowed" readonly="true"></form:textarea>
			</p>
			</c:if> --%>
			</div>	
			 <div class="fields">
				<h2></h2>
				<c:if test="${memberStatusType=='proprietypoint_complete' or memberStatusType=='proprietypoint_incomplete'}">
				<p class="tright">
					<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
						<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						<%-- <input id="sendforapproval" type="button" value="<spring:message code='proprietypoint.sendforapproval' text='Send For Approval'/>" class="butDef"> --%>
					</security:authorize>
					<input id="submitdevice" type="button" value="<spring:message code='proprietypoint.submitdevice' text='Submit Propriety Point'/>" class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
				</c:if>				
			</div>
			<form:hidden path="version" />
			<form:hidden path="id"/>
			<form:hidden path="locale"/>
			<input type="hidden" name="status" id="status" value="${status }">
			<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
			<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
			<input type="hidden" name="createdBy" id="createdBy" value="${domain.createdBy }">
			<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
		</form:form>

		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		
		<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.proprietypoint.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
		<input id="subjectEmptyMsg" value='<spring:message code="client.error.proprietypoint.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
		<input id="noticecontentEmptyMsg" value='<spring:message code="client.error.proprietypoint.noticecontentEmptyMsg" text="Notice Content can not be empty."></spring:message>' type="hidden">
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='client.prompt.proprietypoint.submit' text='Do you want to submit the propriety point?'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>