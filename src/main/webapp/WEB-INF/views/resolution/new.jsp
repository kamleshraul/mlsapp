<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><spring:message code="resolution" text="Resolution Information System"/>
	</title>
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
		}
	</style>
	<script type="text/javascript">
	
	var memberControlName=$(".autosuggest").attr("id");
//	function loadDepartments(ministry){
//		$.get('ref/departments/'+ministry,function(data){
//			$("#department").empty();
//			var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
//			if(data.length>0){
//			for(var i=0;i<data.length;i++){
//				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
//			}
//			$("#department").html(departmentText);			
//			loadSubDepartments(ministry,data[0].id);
//			}else{
//				$("#department").empty();
//				var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
//				$("#department").html(departmentText);			
//				$("#subDepartment").empty();			
//			}
//		}).fail(function(){
//			if($("#ErrorMsg").val()!=''){
//				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
//			}else{
//				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
//			}
//		scrollTop();
//		});
//	}
	
	
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
	
	function loadMinistries(session){
		$.get('ref/session/'+session+'/ministries',function(data){
			if(data.length>0){
				var minsitryText="<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					minsitryText+="<option value='"+data[i].id+"'>"+data[i].name;				
				}
				$("#ministry").empty();
				$("#ministry").html(minsitryText);
				loadGroup(data[i].id);
			}else{
				$("#ministry").empty();
				$("#department").empty();				
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
	
	function removeFormattingFromDetails(callBack){		
		var detailsBox=$('textarea#noticeContent');
		if(detailsBox!==undefined && detailsBox!==null){				
			var noticeContentText=$.wysiwyg.getContent(detailsBox);
			if(noticeContentText!==undefined && noticeContentText!==null && noticeContentText!==''){
				cleanText=cleanFormatting(noticeContentText);
				$.wysiwyg.setContent(detailsBox,cleanText);
			}
		}			
		callBack();
	}
	
	$(document).ready(function(){
		
		/**** Auto Suggest(typist login)- Member ****/		
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
		
			
		/**** Ministry Changes ****/	
		$("#ministry").change(function(){
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
			}
		});

			
		if($('#ministrySelected').val()=="" || $('#ministrySelected').val()==undefined){		
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
		}
		$("#department").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
		$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
		
		$("#submit").click(function(e){
			e.preventDefault();
			removeFormattingFromDetails(function() {
				$("#submit").unbind('click').click();
			});
		});
		
		$("#submitresolution").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			
			removeFormattingFromDetails(function(){/*blank function*/});
			
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
			    	            }).fail(function (jqxhr, textStatus, err) {
			    	            	$.unblockUI();
			    	            	$("#error_p").html("Server returned an error\n" + err +
		                                    "\n" + textStatus + "\n" +
		                                    "Please try again later.\n"+jqxhr.status+"\n"+jqxhr.statusText).css({'color':'red', 'display':'block'});
			    	            	
			    	            	scrollTop();
	                            });
			        }
				}});		
			}
			
				
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
		<form:form action="resolution" method="POST" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<div id="reportDiv">
		<h2><spring:message code="resolution.new.heading" text="Enter Resolution Details"/>		
		</h2>
		<form:errors path="version" cssClass="validationError"/>	
		<security:authorize access="hasAnyRole('ROIS_TYPIST')">	
		<p>
			<label class="small"><spring:message code="resolution.number" text="Resolution Number"/>*</label>
			<form:input path="number" cssClass="sText"/>
			<form:errors path="number" cssClass="validationError"/>
		</p>
		</security:authorize>
	
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
			<input id="formattedResolutionType" name="formattedResolutionType" value="${formattedResolutionType}" class="sText" readonly="readonly">
			<input id="type" name="type" value="${resolutionType}" type="hidden">		
			<form:errors path="type" cssClass="validationError"/>		
		</p>	
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
		<p>
			<label class="small"><spring:message code="resolution.member" text="Member"/>*</label>
			<input id="formattedMember" name="formattedMember"  value="${formattedMember}" type="text" class="sText"  readonly="readonly" class="sText">
			<input name="member" id="member" value="${member}" type="hidden">		
			<form:errors path="member" cssClass="validationError"/>		
		</p>
		<p>
			<label class="small"><spring:message code="resolution.memberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
		</p>
		</security:authorize>
		<security:authorize access="hasAnyRole('ROIS_TYPIST')">		
		<p>
			<label class="small"><spring:message code="resolution.member" text="Member"/>*</label>
			<input id="formattedMember" name="formattedMember" type="text" class="sText autosuggest" value="${formattedMember}">
			<input name="member" id="member" type="hidden" value="${member}">		
			<form:errors path="member" cssClass="validationError"/>		
		</p>	
		</security:authorize>
		
		<p>
			<label class="centerlabel"><spring:message code="resolution.subject" text="Subject"/>*</label>
			<form:textarea path="subject" rows="2" cols="50"></form:textarea>
			<form:errors path="subject" cssClass="validationError" />	
		</p>	
	
		<p>
			<label class="wysiwyglabel"><spring:message code="resolution.noticeContent" text="Notice Content"/>*</label>
			<form:textarea path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
		
		<table style="width: 100%;">
			<tr>
				<td>
					<p>
						<label class="small"><spring:message code="resolution.ministry" text="Ministry"/></label>
						<select name="ministry" id="ministry" class="sSelect">
						<c:forEach items="${ministries}" var="i">
							<c:choose>
								<c:when test="${i.id==ministrySelected }">
									<option value="${i.id}" selected="selected">${i.dropdownDisplayName}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}" >${i.dropdownDisplayName}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</select>
						<form:errors path="ministry" cssClass="validationError"/>
					<p> 
						<label class="small"><spring:message code="resolution.subdepartment" text="Sub Department"/></label>
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
					</p>
				</td>				
				<c:if test="${selectedResolutionType == 'resolutions_government'}">
				<td style="vertical-align: top;">
					<p>
						<label class="small"><spring:message code="resolution.discussionDate" text="Discussion Date"/></label>
						<c:choose>
							<c:when test="${empty domain.discussionDate}">
								<form:input id="discussionDate" path="discussionDate" cssClass="datemask sText" value="${expectedDiscussionDate}"/>
							</c:when>
							<c:otherwise>
								<form:input id="discussionDate" path="discussionDate" cssClass="datemask sText"/>
							</c:otherwise>
						</c:choose>
						
						<form:errors path="discussionDate" cssClass="validationError"/>
					</p>
					<p id="ruleForDiscussionDateParagraph" hidden="true">
						<label class="small"><spring:message code="resolution.ruleForDiscussionDate" text="Rule For Discussion Date"/></label>
						<form:select id="ruleForDiscussionDate" class="sSelect" path="ruleForDiscussionDate">
							<option value="" selected="selected"><spring:message code='client.prompt.select' text='Please Select'/></option>
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
		<c:if test="${selectedResolutionType == 'resolutions_government'}">
			<p id="remarksParagraph" hidden="true">
				<label class="wysiwyglabel"><spring:message code="resolution.remarks" text="Remarks"/></label>
				<form:textarea id="remarks" path="remarks" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			</p>
		</c:if>		
		</div>
		 <div class="fields">
			<h2></h2>
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
		</div>	
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">
	<input type="hidden" id="expectedDiscussionDate" name="expectedDiscussionDate" value="${expectedDiscussionDate}"/>	
	</form:form>
	<input id="ministrySelected" value="${ministrySelected }" type="hidden">
	<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
	<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input id="submissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='Do you want to submit the resolution'></spring:message>" type="hidden">	
	<input type="hidden" id="typeOfSelectedDeviceType" value="${selectedResolutionType}">
	<input id="extrasubmissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='The limit of 5 Resolution is Exceeded ,Do you still want to submit the resolution'></spring:message>" type="hidden">
	<input id="resolutionCount" value="${resolutionCount}" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>