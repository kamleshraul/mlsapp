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
		
		/* $("#numberP").css("display","none"); */
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
			if(!checkBeforeSubmit()){
				e.preventDefault();
				return false;
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
				return false;
			}
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
	    //view supporting members status
	    $("#viewStatus").click(function(){
		    $.get('motion/status/'+$("#id").val(),function(data){
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
		if($("#currentStatus").val()=='motion_submit'){
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
		
		$("#new_record2").click(function(){
			$("#selectionDiv1").hide();	
			newMotion();
		});
		
		//print pdf
		$('#Generate_PDF').click(function () { 
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
			var parameters = {	motionId:$("#id").val(),
			 					outputFormat:"PDF"
							};
			resourceURL = 'motion/report/motionPrintReport' + parameters;
			form_submit('motion/report/motionPrintReport', parameters, 'GET');
			$.unblockUI();
		});
		
		
		var inputBoxText=document.getElementById('wysiwygBox');
		var wordCountLbl=document.getElementById('wordCountLbl');
		var lblMaxText=document.getElementById('lblMaxText');
		
		function removeTags(str) {
		    if ((str===null) || (str===''))
		        return false;
		    else
		        str = str.toString();
		          
		    return str.replace( /(<([^>]+)>)/ig, '');		   
		}
		
		if(getMaxAllowedTextSize() > 0){
			$(inputBoxText).change(function(event){
				var str=event.target.value;
				checkMaxAllowedTextSize(str);
			});
			
			$('#submit').click(function(event){
				if(!checkBeforeSubmit()){
					event.preventDefault();
					return false;
				}
			});
			
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
		
	});	
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div style="text-align: right">
		<a href="#" id="Generate_PDF">
			<img src="./resources/images/pdf_icon.png" style="width:25px;height:25px;">
		</a>
</div>
<div class="fields clearfix watermark">
	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','MOIS_TYPIST')">			
		<a href="#" id="new_record2" class="butSim">
			<spring:message code="motion.new" text="New"/>
		</a> |
	</security:authorize>	
<form:form action="motion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<h2>${formattedMotionType} </h2><%--${formattedNumber} --%>
	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p id="numberP">
		<label class="small"><spring:message code="question.number" text="Motion Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${!(empty submissionDate)}">
	<p>
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
	</p>
	</c:if>
	
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
		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>	
		
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${!(empty supportingMembers)}">
			<select  name="selectedSupportingMembers" multiple="multiple">
				<c:forEach items="${supportingMembers}" var="i">
					<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
				</c:forEach>		
			</select>
		</c:if>
		<a href="#" id="viewStatus"><spring:message code="question.viewstatus" text="View Status"></spring:message></a>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
		<form:textarea path="subject" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError" />	
	</p>	
		
	<p>
		<p style="padding-left:17%" id="maxTextLengthPara">
			<label id="lblMaxText"> <spring:message code="motion.max.word.label" text="Text Must be max 175 word"/> </label>
			&nbsp;&nbsp;&nbsp;
			<a href="${patrakExternalLink}" target="_blank" style="color:blue"> (<spring:message code="pratak.bhag.external.link" text="patrak"/>) </a> 
			&nbsp;&nbsp;&nbsp;
			<spring:message code="max.words.in.text" text="max words"/>
			<label id="wordCountLbl"> 0 </label>
			<input type="hidden" name="maxAllowedTextSize" id="hddMaxAllowedTextSize" value="${maxAllowedTextSize}"/>
		</p>
		<label class="wysiwyglabel"><spring:message code="motion.details" text="Details"/>*</label>
		<form:textarea id="wysiwygBox" path="details" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>		 
		<form:errors path="details" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>	
	
	<%-- <security:authorize access="!hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
		<p id="internalStatusDiv">
			<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
			<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly" class="sText">
		</p>
	</security:authorize>
	
	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
		<p id="internalStatusDiv" style="display:none;">
			<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
			<input id="formattedInternalStatus" name="formattedInternalStatus" value="${domain.status.name }" type="text" readonly="readonly" class="sText">
		</p>
	</security:authorize> --%>
	
	<c:if test="${not empty formattedMemberStatus}">
	<p id="mainStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedMemberStatus" name="formattedMemberStatus" value="${formattedMemberStatus }" type="text" readonly="readonly" class="sText">
	</p>
	</c:if>
	
	<table>
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
											<option value="${i.id }" selected="selected">${i.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${i.id }" >${i.name}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>		
							<form:errors path="ministry" cssClass="validationError"/>
							<br />
							<label class="small"><spring:message code="motion.department" text="Department"/></label>
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
				<p></p>
			</div>			
			</c:otherwise>
		</c:choose>	
	</table>	
	</div>
	 <div class="fields">
		<h2></h2>
		<c:choose>
			<c:when test="${memberStatusType=='motion_complete' or memberStatusType=='motion_incomplete'}">
				<p class="tright">
					<security:authorize access="hasAnyRole('MOIS_TYPIST')">	
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
			</c:when>	
			<c:otherwise>
				<p class="tright">
					<security:authorize access="hasAnyRole('MOIS_TYPIST')">	
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						<input id="submitmotion" type="button" value="<spring:message code='motion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">				
					</security:authorize>			
					<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
						<input id="sendforapproval" type="button" value="<spring:message code='question.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
						<input id="submitmotion" type="button" value="<spring:message code='motion.submitmotion' text='Submit Motion'/>" class="butDef" disabled="disabled">
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
	<input type="hidden" name="deviceType" id="deviceType" value="${deviceType}"/>
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
<input id="submissionMsg" value="<spring:message code='client.motion.prompt.submit' text='Do you want to submit the motion.'></spring:message>" type="hidden">

</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>