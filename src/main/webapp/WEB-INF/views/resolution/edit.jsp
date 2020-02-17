<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="resolution" text="Resolution Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		.toolbar{
			display: none !important;
		}
		#noticeContent{
			width: 400px;
		}
		.wysiwyg{
			margin-left: 160px;
			position: static !important;
			overflow: visible !important; 
			
		}
		
	</style>
	
	<script type="text/javascript">
	
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
		$("#numberP").css("display","none");
		
		
		/**** Auto Suggest(typist login)-Primary Member ****/		
		$( ".autosuggest").autocomplete({
			minLength:3,			
			source:'ref/member/supportingmembers?session='+$("#session").val(),
			select:function(event,ui){			
			$("#member").val(ui.item.id);
		}	
		});
		
		//in case of Government Resolution, to populate rule & remark for early discussion date
		var deviceTypeSelected = $('#typeOfSelectedDeviceType').val();
		if(deviceTypeSelected == 'resolutions_government') {
			var expectedDiscussionDate= $('#expectedDiscussionDate').val();					
			var requestedDiscussionDate = $('#discussionDate').val();			
			$.get("ref/resolutions_government/isDiscussionDateEarly?expectedDiscussionDate="+expectedDiscussionDate+"&requestedDiscussionDate="+requestedDiscussionDate, function(data){
				if(data == "true") {
					$('#ruleForDiscussionDateParagraph').show();				
					if($('#ruleForDiscussionDate').val()!="") {
						$('#remarksParagraph').show();
					} else {
						$('#remarksParagraph').hide();
					}
				} else if(data == "false") {
					$('#ruleForDiscussionDateParagraph').hide();
					$('#remarksParagraph').hide();								
				} else if(data == "" || data == undefined) {
					alert("error in discussion date...");
				}
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
		
		$('#discussionDate').change(function() {
			var deviceTypeSelected = $('#typeOfSelectedDeviceType').val();
			if(deviceTypeSelected == 'resolutions_government') {
				var expectedDiscussionDate= $('#expectedDiscussionDate').val();					
				var requestedDiscussionDate = $('#discussionDate').val();			
				$.get("ref/resolutions_government/isDiscussionDateEarly?expectedDiscussionDate="+expectedDiscussionDate+"&requestedDiscussionDate="+requestedDiscussionDate, function(data){
					if(data == "true") {
						$('#ruleForDiscussionDateParagraph').show();
						if($('#ruleForDiscussionDate').val()!="") {
							$('#remarksParagraph').show();
						}
					} else if(data == "false") {
						$('#ruleForDiscussionDateParagraph').hide();
						$('#remarksParagraph').hide();
						$('#ruleForDiscussionDate').val("");
						$('#remarks').wysiwyg("setContent","");				
					} else if(data == "" || data == undefined) {
						alert("error in discussion date...");
					}
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
		});
		$('#ruleForDiscussionDate').change(function() {
			var deviceTypeSelected = $('#typeOfSelectedDeviceType').val();
			if(deviceTypeSelected == 'resolutions_government') {
				if($('#ruleForDiscussionDate').val() != "") {
					var ruleNumber = $('#ruleForDiscussionDate option:selected').text();			
					var remarksCitation = '${remarksCitation}';
					remarksCitation = remarksCitation.replace(/ruleNumber/g, ruleNumber);			
					$('#remarks').wysiwyg("setContent",remarksCitation);
					$('#remarksParagraph').show();		
				} else {
					$('#remarks').wysiwyg("setContent","");
				}
			}						
		});
				
		/* $("#ministry").change(function(){
			if($(this).val()!=''){
			loadDepartments($(this).val());
			}else{
				$("#department").empty();				
				$("#subDepartment").empty();				
				$("#department").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
							
			}
		});

		$("#department").change(function(){
			if($(this).val()!=''){
			loadSubDepartments($("#ministry").val(),$(this).val());
			}
		}); */
		
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
		/* if($("#departmentSelected").val()==''){
			$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
		}else{
			$("#department").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
		} */
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");			
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
		}
			
		
		//send for submission
		$("#submitresolution").click(function(e){
			
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			
			if($('#resolutionCount').val()==5){
				$.prompt($('#extrasubmissionMsg').val(),{
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
			}else{
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
			}			
	        return false;  
	    }); 
	    
	    
	  
		/***** added by sandeep singh *****/
		if($("#currentStatus").val()=='resolutions_submit'){
			$("#ministry").attr("disabled","disabled");
			$("#department").attr("disabled","disabled");
			$("#subDepartment").attr("disabled","disabled");
			$("#subject").attr("readonly","readonly");
			$("#noticeContent").attr("readonly","readonly");
		}
		
		$('#new_record_ForNew').click(function(){	
			newResolution_ForNew();
		});
		
		//print pdf
		$('#Generate_PDF').click(function () { 
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
			var parameters = {	resolutionId:$("#id").val(),
			 					outputFormat:"PDF"
							};
			resourceURL = 'resolution/report/resolutionPrintReport' + parameters;
			form_submit('resolution/report/resolutionPrintReport', parameters, 'GET');
			$.unblockUI();
		});
	});
	
	/**** new question ****/
	function newResolution_ForNew() {
		showTabByIdAndUrl('details_tab','resolution/new?'+$("#gridURLParams_ForNew").val());
	}		
	</script>
	
	<style type="text/css">
		.soberFontSize{
			font-size: 14px;
			color: blue;
		}
	</style>
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
<form:form action="resolution" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<a href="#" id="new_record_ForNew" class="butSim soberFontSize">
		<spring:message code="generic.new" text="New"/>
	</a> 
	<br />
	<br />
	<h2>${formattedDeviceType} ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p id="numberP">
		<label class="small"><spring:message code="resolution.number" text="Resolution Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly" type="hidden">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${!(empty submissionDate)}">
	<p>
	<label class="small"><spring:message code="resolution.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
	</p>
	</c:if>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.type" text="Type"/>*</label>
		<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${deviceType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
		
	<p>
		<label class="small"><spring:message code="resolution.member" text="Member"/>*</label>
		<input id="formattedMember" name="formattedMember"  value="${formattedMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input id="member" name="member" value="${member}" type="hidden">		
		<form:errors path="member" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="resolution.memberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>
	<p>
		<label class="centerlabel"><spring:message code="resolution.subject" text="Subject"/>*</label>
		<form:textarea path="subject" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError" />	
	</p>	
	
	<p id="NoticeContent">
		<label class="wysiwyglabel"><spring:message code="resolution.noticeContent" text="Notice Content"/>*</label>
		<form:textarea path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>		 
		<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	<c:if test="${internalStatusType != null }">
		<c:if test="${!empty internalStatusType}">
			<c:if test="${sectionofficer_remark != null}">
				<c:if test="${! empty sectionofficer_remark}">
					<c:if test="${internalStatusType=='resolution_final_rejection'}">
						<p>
							<label class="wysiwyglabel"><spring:message code="resolution.remarks" text="Remarks"/></label>
							<form:textarea path="remarks" cssClass="wysiwyg invalidFormattingAllowed" readonly="true"></form:textarea>
						</p>
					</c:if>
				</c:if>
			</c:if>
		</c:if>
	</c:if>
		
	<table style="width: 100%;">
		<tr>
			<td>
				<p id="internalStatusDiv" style="display:none;">
					<label class="small"><spring:message code="resolution.currentStatus" text="Current Status"/></label>
					<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly" class="sText">
				</p>
				 <p>
					<label class="small"><spring:message code="resolution.ministry" text="Ministry"/>*</label>
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
					<%-- </p>
					<p>
					<label class="small"><spring:message code="resolution.department" text="Department"/></label>
					<select name="department" id="department" class="sSelect">
					<c:forEach items="${departments }" var="i">
						<c:choose>
							<c:when test="${i.id==departmentSelected }">
								<option value="${i.id }" selected="selected">${i.name}</option>
							</c:when>
							<c:otherwise>
									<option value="${i.id }" >${i.name}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
					<form:errors path="department" cssClass="validationError"/>
					</p> --%>
					<p>
					<label class="small"><spring:message code="resolution.subdepartment" text="Sub Department"/></label>
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
				<c:if test="${selectedDeviceType == 'resolutions_government'}">
				<td style="vertical-align: top;">
					<p>
						<label class="small"><spring:message code="resolution.discussionDate" text="Discussion Date"/></label>
						<form:input path="discussionDate" cssClass="datemask sText" readonly="${isDiscussionDateReadOnly}"/>
						<form:errors path="discussionDate" cssClass="validationError"/>
					</p>					
					<p id="ruleForDiscussionDateParagraph">
						<label class="small"><spring:message code="resolution.ruleForDiscussionDate" text="Rule For Discussion Date"/></label>
						<form:select id="ruleForDiscussionDate" class="sSelect" path="ruleForDiscussionDate">
							<option value=""><spring:message code='client.prompt.select' text='Please Select'/></option>
							<c:forEach items="${rulesForDiscussionDate}" var="i">
								<c:choose>
								<c:when test="${i.id==ruleForDiscussionDateSelected}">
									<option value="${i.id }" selected="selected">${i.value}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id }" >${i.value}</option>
								</c:otherwise>
							</c:choose>
							</c:forEach>							
						</form:select>
						<form:errors path="ruleForDiscussionDate" cssClass="validationError"/>
					</p>								
				</td>
				</c:if>
			</tr>
		</table>
		<c:if test="${selectedDeviceType == 'resolutions_government'}">
			<p id="remarksParagraph">
				<label class="wysiwyglabel"><spring:message code="resolution.remarks" text="Remarks"/></label>
				<form:textarea id="remarks" path="remarks" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			</p>
		</c:if>		
	</div>
	<c:if test="${recommendationStatusType == 'resolution_processed_rejectionWithReason'}">
	<p>
	<label class="wysiwyglabel"><spring:message code="resolution.rejectionReason" text="Rejection reason"/></label>
	<form:textarea path="rejectionReason" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
	</p>
	</c:if>
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${memberStatusType=='resolution_complete' or memberStatusType=='resolution_incomplete'}">
			<p class="tright">
			<security:authorize access="hasAnyRole('ROIS_TYPIST')">	
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="submitresolution" type="button" value="<spring:message code='resolution.submitResolution' text='Submit resolution'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="submitresolution" type="button" value="<spring:message code='resolution.submitResolution' text='Submit resolution'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>
			
			</p>
		</c:when>	
		<c:otherwise>
			<p class="tright">
				<security:authorize access="hasAnyRole('ROIS_TYPISR')">	
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<input id="submitresolution" type="button" value="<spring:message code='resolution.submitResolution' text='Submit resolution'/>" class="butDef" disabled="disabled">				
				</security:authorize>			
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
					<input id="submitresolution" type="button" value="<spring:message code='resolution.submitResolution' text='Submit resolution'/>" class="butDef" disabled="disabled">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef" disabled="disabled">
				</security:authorize>
				
			</p>
		</c:otherwise>
		</c:choose>
		
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<c:choose>
		<c:when test="${selectedDeviceType == 'resolutions_government'}">
			<c:if test="${houseTypeForStatus=='lowerhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusLowerHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusLowerHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusLowerHouse" id="status" value="${status}">
				
				<input type="hidden" name="internalStatusUpperHouse" value="${internalStatusUpperHouse }">
				<input type="hidden" name="recommendationStatusUpperHouse" value="${recommendationStatusUpperHouse}">
				<input type="hidden" name="statusUpperHouse" value="${statusUpperHouse}">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusUpperHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusUpperHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusUpperHouse" id="status" value="${status}">
				
				<input type="hidden" name="internalStatusLowerHouse" value="${internalStatusLowerHouse }">
				<input type="hidden" name="recommendationStatusLowerHouse" value="${recommendationStatusLowerHouse}">
				<input type="hidden" name="statusLowerHouse" value="${statusLowerHouse}">				
			</c:if>			
		</c:when>
		<c:otherwise>
			<c:if test="${houseTypeType=='lowerhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusLowerHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusLowerHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusLowerHouse" id="status" value="${status}">
			</c:if>	
			<c:if test="${houseTypeType=='upperhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusUpperHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusUpperHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusUpperHouse" id="status" value="${status}">
			</c:if>
		</c:otherwise>
	</c:choose>
	<form:hidden path="file"/>
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">
	<input type="hidden" id="expectedDiscussionDate" name="expectedDiscussionDate" value="${expectedDiscussionDate}"/>
</form:form>
<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
<input id="currentStatus" value="${internalStatusType }" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<%-- <input id="departmentSelected" value="${ departmentSelected}" type="hidden"> --%>
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceType}" />
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='Do you want to submit the resolution.'></spring:message>" type="hidden">
<input id="resolutionCount" value="${resolutionCount}" type="hidden">
<input id="extrasubmissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='The limit of 5 Resolution is Exceeded ,Do you still want to submit the resolution'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>
</body>
</html>