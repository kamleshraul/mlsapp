<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
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
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}		
	$(document).ready(function(){	
		
		$('#number').change(function(){
			$.get('ref/motionnumberinsession?'+
					'number=' + $(this).val() +
					'&session=' + $('#session').val() +
					'&deviceType=' + $('#type').val(),function(data){
				if(data){
					$('#numberError').css('display','inline');
				}else{
					$('#numberError').css('display','none');
				}
			});
		});
		
		/**** Ministry Changes ****/	
		$("#ministry").change(function(){
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
			}
		});	
						
		/**** On Load ****/
		if($('#ministrySelected').val()=="" || $('#ministrySelected').val()==undefined){		
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
		}
		$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				

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
		
		
		//Copy supporting members
		$("#copyMembers").click(function(e){
			//no need to send for approval in case of empty supporting members.
			 var supportingMembersName='${supportingMembersName}';
			if(supportingMembersName==""){
				$.prompt($('#memberSupportingMembersCOPYEmptyMsg').val(),{
					buttons: {Ok:true}, callback: function(v){
				   		if(v){
				   			scrollTop();
				   			$('#selectedSupportingMembers').focus();
				   		
				   		}     						
					}
				});	
				return false;
			}
			 $('.multiselect2').empty()
			    var options = $('select.multiselect1 option').sort().clone();
			    $('select.multiselect2').append(options);
			    
			    
			    var supportingMembersName='${supportingMembersName}';
			    $('#selectedSupportingMembers').val(supportingMembersName+',');
			
			    
			});
		
		//save supporting members
		$("#saveMembers").click(function(e){
			
			//no need to send for approval in case of empty supporting members.
			if($("#selectedSupportingMembers").val()==""){
				$.prompt($('#memberSupportingMembersSAVEEmptyMsg').val(),{
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
			//-------------------------------
			$.prompt($('#sendForSaveMsg').val()+$("#selectedSupportingMembers").val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	var postURL = "member/other/saveSupportingMembers";
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
					
						$.post(postURL,
								$('form').serialize(), function(data){
							 if(data=='success'){
								 $.prompt('<spring:message code="update_success" text="Notice"/>',{
										buttons: {Ok:true}, callback: function(v){
									   		if(v){
									   			scrollTop();
									   			$('#selectedSupportingMembers').focus();
									   		
									   		}     						
										}
									});	
		       			
		       				 $('#savedMemberSupportingMembers').empty()

		       				 var options = $('select.multiselect2 option').sort().clone();
		     			    $('select.multiselect1').append(options);
		     			    
		     			   $('#copyMembers').attr('title', $('#selectedSupportingMembers').val());
		    					$.unblockUI();	   				
							 }
		    	            }).fail(function(){
		    	            	$.unblockUI();
		    					if($("#ErrorMsg").val()!=''){
		    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    					}else{
		    						$("#error_p").html("Error occured contact for support.");
		    					}
		    					scrollTop();
		    				});
		        }
			}});			
	    
	    }); 
		
		
		/**** send for approval ****/
		$("#sendforapproval").click(function(e){
			if(!checkBeforeSubmit()){
				e.preventDefault();
				return true;
			}
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
			//-------------------------------
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
			if(!checkBeforeSubmit()){
				e.preventDefault();
				return true;
			}
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			removeFormattingFromDetails(function(){/*blank function*/});
			//------------------------------------------------------------
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
		    	            }).fail(function (jqxhr, textStatus, err) {
		    	            	$.unblockUI();
		    	            	$("#error_p").html("Server returned an error\n" + err +
	                                    "\n" + textStatus + "\n" +
	                                    "Please try again later.\n"+jqxhr.status+"\n"+jqxhr.statusText).css({'color':'red', 'display':'block'});
		    	            	
		    	            	scrollTop();
                            });
		        }
			}});			
	        return false;  
	    }); 	
		
		/**** To prevent copy paste in supporting member field ****/
		$("#selectedSupportingMembers").bind('copy paste', function (e) {
		       e.preventDefault();
		 });
		
		$("#new_record2").click(function(){
			$("#selectionDiv1").hide();	
			newMotion();
		});
		
		function removeTags(str) {
		    if ((str===null) || (str===''))
		        return false;
		    else
		        str = str.toString();
		          
		    return str.replace( /(<([^>]+)>)/ig, '');		   
		}
		
		var inputBoxText=document.getElementById('wysiwygBox');
		var wordCountLbl=document.getElementById('wordCountLbl');
		var lblMaxText=document.getElementById('lblMaxText');
		
		if(getMaxAllowedTextSize() > 0){
			$(inputBoxText).change(function(event){
				var str=event.target.value;
				checkMaxAllowedTextSize(str);
			});
			
			/*$('#submit').click(function(event){
				removeFormattingFromDetails(function() {
					$("#submit").unbind('click').click();
					if(!checkBeforeSubmit()){
						event.preventDefault();
						return false;
					}
				});
				
			});*/
			
		}else{
			var para=document.getElementById('maxTextLengthPara');
			if(para!==null && para!==undefined){
				para.style.visibility='hidden';
			}
		}
		
		function checkMaxAllowedTextSize(str){
			if(str!==null && str.length!==undefined){
				 str=removeTags(str);
				 if(str!==null && str.length!==undefined && str.length>0){
				 var matches = str.match(/\S+/g);
				 //console.log("word count",matches.length,str);
				 wordCountLbl.innerHTML=matches.length;
					 if(getMaxAllowedTextSize()>0 && matches.length > getMaxAllowedTextSize() ){
						wordCountLbl.style.backgroundColor='orange';
						return false;
					 }
					 else{
						wordCountLbl.style.backgroundColor='';
						return true;
					 }
				 }
			}
			return true;
		}
		
		function getMaxAllowedTextSize(){
			var maxTextSizeHdd=document.getElementById('hddMaxAllowedTextSize');
			if(maxTextSizeHdd!==null && maxTextSizeHdd!==undefined 
					&& maxTextSizeHdd.value!==null && maxTextSizeHdd.value!==undefined
					&& maxTextSizeHdd.value!==''){
				return maxTextSizeHdd.value;
			}else{ return -1;}
		}
		
		function checkBeforeSubmit() {
			
			var maxTextSize=getMaxAllowedTextSize();
			if(maxTextSize>0){
				var str=inputBoxText.value;
				if(!checkMaxAllowedTextSize(str)){
					$.prompt(lblMaxText.innerHTML);
					return false;
				}
			}
			
			return true;
		}
		
		if(inputBoxText!==null && $(inputBoxText)!==null && $(inputBoxText)!==undefined 
				&& $(inputBoxText).val()!==null){
			checkMaxAllowedTextSize($(inputBoxText).val());
		}
		
		function removeFormattingFromDetails(callBack){		
			var detailsBox=$('textarea#wysiwygBox');
			if(detailsBox!==undefined && detailsBox!==null){				
				var motionDetailText=$.wysiwyg.getContent(detailsBox);
				if(motionDetailText!==undefined && motionDetailText!==null && motionDetailText!==''){
					cleanText=cleanFormatting(motionDetailText);
					$.wysiwyg.setContent(detailsBox,cleanText);
				}
			}
			
			callBack();
		}
		
		
		$('#submit').click(function(e) {
			e.preventDefault();
			removeFormattingFromDetails(function() {
				if(getMaxAllowedTextSize() > 0 && !checkBeforeSubmit()){					
					return false;
				}else{
					$("#submit").unbind('click').click();
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
	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">			
		<a href="#" id="new_record2" class="butSim">
			<spring:message code="motion.new" text="New"/>
		</a> |
	</security:authorize>
<form:form action="motion" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<h2><spring:message code="motion.new.heading" text="Enter Motion Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	
	<security:authorize access="hasAnyRole('MOIS_CLERK','MOIS_TYPIST')">	
	<p>
		<label class="small"><spring:message code="motion.number" text="Motion Number"/>*</label>
		<form:input path="number" cssClass="sText"/>
		<span id='numberError' style="display: none; color: red;">
			<spring:message code="MotionNumber.domain.NonUnique" text="Duplicate Number"></spring:message>
		</span>
		<form:errors path="number" cssClass="validationError"/>
		<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
	</p>
	</security:authorize>
		
	<p style="display:none;">
		<label class="small"><spring:message code="question.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.type" text="Type"/>*</label>
		<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${motionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>
	</security:authorize>
	
	<security:authorize access="hasAnyRole('MOIS_CLERK','MOIS_TYPIST')">		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
		<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>	
	</security:authorize>			
	
	<p>
	<c:if test="${!(empty savedMemberSupportingMembers)}">		
		<select id="savedMemberSupportingMembers" name="savedMemberSupportingMembers" class="multiselect1" multiple="multiple" style="display: none;">
		<c:forEach items="${savedMemberSupportingMembers}" var="i">
		<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
		</c:forEach>		
		</select>
		</c:if>
	
		<select  name="selectedSupportingMembers" class="multiselect2" multiple="multiple">
		
		</select>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50"></textarea>
	
		<a href="#" title="${supportingMembersName}" id="copyMembers"><img src="./resources/images/back2D.png" title="<spring:message code='question.copyMembers' text='${supportingMembersName}'></spring:message>" style="width: 32px; height: 32px;" /></a>
		<a href="#" id="saveMembers"><img src="./resources/images/save.jpg" title="<spring:message code='question.saveMembers' text='Save Supporting members'></spring:message>" style="width: 32px; height: 32px;" /></a>
	
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
		<form:textarea path="subject" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError" />	
	</p>	
	
	<p>
		
		<p style="padding-left:17%" id="maxTextLengthPara">
			<label id="lblMaxText"> <spring:message code="motion.max.word.label" arguments="${maxAllowedTextSize}" text="Text Must be max 175 word"/> </label>
			&nbsp;&nbsp;&nbsp;
			<a href="${patrakExternalLink}" target="_blank" style="color:blue"> (<spring:message code="pratak.bhag.external.link" text="patrak"/>) </a> 
			&nbsp;&nbsp;&nbsp;
			<span class="wordCountBlk" style="display: inline;font-weight: 600;font-size: 1.13em">
				<spring:message code="max.words.in.text" text="max words"/>
				<label id="wordCountLbl" style="padding:0.6em 1.5em;font-weight: 800;font-size: 1.13em;display:inline-block"> 0 </label>
			</span>
			<input type="hidden" name="maxAllowedTextSize" id="hddMaxAllowedTextSize" value="${maxAllowedTextSize}"/>
		</p>
		<label class="wysiwyglabel"><spring:message code="motion.details" text="Details"/>*</label>
		<form:textarea id="wysiwygBox" path="details" cssClass="wysiwyg invalidFormattingAllowed" onkeypress="alert('hello')"></form:textarea>
		<form:errors path="details" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	
	<table style="width: 100%;">
	<c:choose>
		<c:when test="${! empty ministries}">
			<tr>
				<td style="vertical-align: top;">
				<p>
					<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
					<select name="ministry" id="ministry" class="sSelect">
						<c:forEach items="${ministries }" var="i">
							<c:choose>
								<c:when test="${i.id==ministrySelected }">
									<option value="${i.id }" selected="selected">${i.dropdownDisplayName}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id }" >${i.dropdownDisplayName}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					<form:errors path="ministry" cssClass="validationError"/>
					<br />	
					<label class="small"><spring:message code="question.department" text="Department"/>*</label>
					<select name="subDepartment" id="subDepartment" class="sSelect">
						<c:forEach items="${subDepartments}" var="i">
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
				</td>
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<td style="vertical-align: top;">
				<p>
					<label class="small"><spring:message code="motion.submission_priority" text="Submission Priority"/></label>
					<select id="submissionPriority" name="submissionPriority" class="sSelect">
						<option value="${defaultSubmissionPriority}"><spring:message code="motion.default_ordering_for_submission" text="Creation Order"/></option>	
						<c:forEach var="submissionOrder" begin="1" end="200" step="1">
							<c:choose>
								<c:when test="${not empty domain.submissionPriority and domain.submissionPriority!=defaultSubmissionPriority and submissionOrder==domain.submissionPriority}">
									<option value="${submissionOrder}" selected="selected">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
								</c:when>
								<c:otherwise>
									<option value="${submissionOrder}">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
								</c:otherwise>
							</c:choose>										
						</c:forEach>
					</select>
				</p>
				</td>
				</security:authorize>				
			</tr>			
		</c:when>	
		<c:otherwise>		
		<div class="toolTip tpGreen clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="rotationordernotpublished" text="Follwoing fields will be activated on {0}(Rotation Order Publishing Date)" arguments="${rotationOrderPublishDate}"/>
			</p>
		</div>			
		</c:otherwise>
	</c:choose>
	</table>
</div>	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<security:authorize access="hasAnyRole('MOIS_CLERK','MOIS_TYPIST')">	
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="submitMotion" type="button" value="<spring:message code='motion.submitmotion' text='Submit Motion'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="sendforapproval" type="button" value="<spring:message code='question.sendforapproval' text='Send For Approval'/>" class="butDef">
			<input id="submitMotion" type="button" value="<spring:message code='motion.submitmotion' text='Submit Motion'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>
		</p>
	</div>	
	
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="bulkedit" name="bulkedit" value="no" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input type="hidden" name="deviceType" id="deviceType" value="${deviceType}"/>
	<input type="hidden" name="selectedSupportingMembersIfErrors" value="${selectedSupportingMembersIfErrors}" />
</form:form>

<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.question.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
<input id="subjectEmptyMsg" value='<spring:message code="client.error.question.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
<input id="motionEmptyMsg" value='<spring:message code="client.error.motion.motionEmpty" text="Motion Details can not be empty."></spring:message>' type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
<input id="sendForSaveMsg" value="<spring:message code='client.prompt.save' text='following members will be saved as Supporting Members:'></spring:message>" type="hidden">
<input id="memberSupportingMembersSAVEEmptyMsg" value="<spring:message code='client.error.supportingmembersaveempty' text='No Supporting Member for Save.'/>" type="hidden">
<input id="memberSupportingMembersCOPYEmptyMsg" value="<spring:message code='client.error.supportingmembercopyempty' text='No Supporting Member for Copy.'/>" type="hidden">
<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.motion.prompt.submit' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>
</body>
</html>