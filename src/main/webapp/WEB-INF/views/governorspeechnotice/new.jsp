<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="specialmentionnotice" text="Special Mention Notice"/>
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
		
		$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
		
		//autosuggest		
		$( "#formattedPrimaryMember").autocomplete({
			minLength:3,			
			source:'ref/member/supportingmembers?session='+$("#session").val(),
			select:function(event,ui){			
			$("#primaryMember").val(ui.item.id);
			}			
		});		
		
		//save the state of special mention notice
		 $("#submit").click(function(e){
			 console.log("I am starting process..");
			 $.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			 console.log("Stuck here");
			 if(!checkBeforeSubmit()){
				 console.log("Inside Submit");
				 e.preventDefault();
				console.log("Submitted..");
				$.unblockUI();
				return false;
			} 
			console.log("Outside..");
			/* $('#specialMentionNoticeDate').removeAttr('disabled'); */
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
		
		//send for submission
		$("#submitmotion").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			if(!checkBeforeSubmit()){
				e.preventDefault();
				return false;
			}
			
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
/* 		        	$('#specialMentionNoticeDate').removeAttr('disabled');
 */					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
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
					$("#subDepartment").empty();			
					$("#subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");
				}			
			}
		});
		
		$('#number').change(function(){
			$('#numberError').css('display','none');
			if($(this).val()!="") {
				$.get('ref/specialmentionnotice/duplicatenumber?'+'number='+$(this).val()
						+'&specialMentionNoticeDate='+$('#specialMentionNoticeDate').val(), function(data){
					if(data){
						$('#numberError').css('display','inline');
					}else{
						$('#numberError').css('display','none');
					}
				});
			}
		});
		
	 	$('#changeSpecialMentionNoticeDate').click(function() {
			var yesLabel = $('#yesLabel').val();
			var noLabel = $('#noLabel').val();
			$.prompt('Do you really want to change the special mention notice date?', {
				buttons: [
					{title: yesLabel, value: true},
					{title: noLabel, value: false}
				],
				callback: function(v) {
					if(v) {
						$('#specialMentionNoticeDate').removeAttr('disabled');
						$('#changeSpecialMentionNoticeDate').hide();
					} else {
						return false;
					}
				}
			});			
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
				 console.log("word count",matches.length,str.length);
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
			console.log("Max text Size : ",maxTextSize);
			if(maxTextSize>0){
				var str=inputBoxText.value;
				console.log("I am going to verify...");
				if(!checkMaxAllowedTextSize(str)){
					$.prompt(lblMaxText.innerHTML);
					return false;
				}
			   console.log("I am out of it");
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
		
	/* 	$('#submit').click(function(e){
			e.preventDefault();
			removeFormattingFromDetails(function(){
				if(getMaxAllowedTextSize() > 0 && !checkBeforeSubmit()){					
					return false;
				}else{
					$("#submit").unbind('click').click();
				}
			});		
			
		});  */
	 	
	});
	</script>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>

	<div class="fields clearfix watermark">

		<form:form action="governorspeechnotice" method="POST" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2><spring:message code="specialmentionnotice.new" text="Enter New SpecialMention Notice Details"/></h2>
			<form:errors path="version" cssClass="validationError"/>
			
			<security:authorize access="hasAnyRole('SMIS_TYPIST')">	
			<p>
				<label class="small"><spring:message code="specialmentionnotice.number" text="Notice Number"/>*</label>
				<form:input path="number" cssClass="sText"/>
				<form:errors path="number" cssClass="validationError"/>
				<span id='numberError' style="display: none; color: red;">
					<spring:message code="MotionNumber.domain.NonUnique" text="Duplicate Number"></spring:message>
				</span>
				<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
			</p>
			</security:authorize>	
				
			<p style="display:none;">
				<label class="small"><spring:message code="specialmentionnotice" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="specialmentionnotice.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="specialmentionnotice.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="specialmentionnotice.type" text="Type"/>*</label>
				<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
				<input id="type" name="type" value="${motionType}" type="hidden">
				<form:errors path="type" cssClass="validationError"/>		
			</p>	
				
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
			<p>
				<label class="small"><spring:message code="specialmentionnotice.primaryMember" text="Primary Member"/>*</label>
				<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" >
				<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
				<form:errors path="primaryMember" cssClass="validationError"/>	
				<label class="small"><spring:message code="specialmentionnotice.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">	
			</p>
			</security:authorize>
			<security:authorize access="hasAnyRole('SMIS_TYPIST')">		
			<p>
				<label class="small"><spring:message code="specialmentionnotice.primaryMember" text="Primary Member"/>*</label>
				<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
				<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
				<form:errors path="primaryMember" cssClass="validationError"/>	
				<%-- <c:if test="${selectedDeviceTypeForBill != 'adjournmentmotions_government'}">
				<label class="small"><spring:message code="adjournmentmotion.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">	
				</c:if> --%>	
			</p>	
			</security:authorize>
			<h2></h2>
			
		<%-- 	<p>
				<label class="small"><spring:message code="specialmentionnotice.selectSpecialMentionNoticedate" text="Special Mention Notice Date"/></label>
				<input name="specialMentionNoticeDate" id="specialMentionNoticeDate" value="${defaultSpecialMentionNoticeDate}" style="width:130px;height: 40px;background-color:white;" readonly="readonly">		
				<select name="specialMentionNoticeDate" id="specialMentionNoticeDate" style="width:130px;height: 25px;" disabled="disabled">
				<c:forEach items="${sessionDates}" var="i">
					<option value="${i[0]}" ${i[0]==defaultSpecialMentionNoticeDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
				</c:forEach>
				</select>
				<a href="#" id="changeSpecialMentionNoticeDate" style="margin-left: 10px;"><spring:message code="specialmentionnotice.changeSpecialMentionNoticeDate" text="Change Special Mention Notice Date"/></a>
			</p> --%>
			
			<p>
				<label class="centerlabel"><spring:message code="specialmentionnotice.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
			    <p style="padding-left:17%" id="maxTextLengthPara">
					<label id="lblMaxText"> <spring:message code="notice.max.word.label" text="Text Must be max 175 word"/> </label>
					&nbsp;&nbsp;&nbsp;
					<a href="${patrakExternalLink}" target="_blank" style="color:blue"> (<spring:message code="pratak.bhag.external.link" text="patrak"/>) </a> 
					&nbsp;&nbsp;&nbsp;
					<span class="wordCountBlk" style="display: inline;font-weight: 600;font-size: 1.13em">
						<spring:message code="max.words.in.text" text="max words"/>
						<label id="wordCountLbl" style="padding:0.6em 1.5em;font-weight: 800;font-size: 1.13em;display:inline-block"> 0 </label>
					</span>
					<input type="hidden" name="maxAllowedTextSize" id="hddMaxAllowedTextSize" value="${maxAllowedTextSize}"/>
				</p>
				<label class="wysiwyglabel"><spring:message code="specialmentionnotice.noticeContent" text="Notice Content"/>*</label>
				<form:textarea id="wysiwygBox" path="noticeContent" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
				<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>	
			
			<%-- <p>
				<label class="small"><spring:message code="specialmentionnotice.ministry" text="Ministry"/></label>
				<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
				<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}"> --%>
				
				<%-- <form:select path="ministry" id="ministry" class="sSelect">
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
				</form:select> --%>	
			<%-- 	<label class="small"><spring:message code="specialmentionnotice.subdepartment" text="Sub Department"/></label>
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
			</p> --%>
			
			<%-- <p>
				<form:errors path="ministry" cssClass="validationError" />	
				
				<form:errors path="subDepartment" cssClass="validationError" style="margin-left:27%"/>	
			</p> --%>
			</div>	
			 <div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					
<%-- 					<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
						<input id="sendforapproval" type="button" value="<spring:message code='specialmentionnotice.sendforapproval' text='Send For Approval'/>" class="butDef">
					</security:authorize> --%>
					
					<input id="submitmotion" type="button" value="<spring:message code='specialmentionnotice.submitmotion' text='Submit Motion'/>" class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
			</div>
			<form:hidden path="version" />
			<form:hidden path="locale"/>
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
			<input type="hidden" name="deviceType" id="deviceType" value="${motionType}"/>
		</form:form>

		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.specialmentionnotice.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
		<input id="subjectEmptyMsg" value='<spring:message code="client.error.specialmentionnotice.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
		<input id="noticeContentEmptyMsg" value='<spring:message code="client.error.specialmentionnotice.noticecontentEmptyMsg" text="Notice Content can not be empty."></spring:message>' type="hidden">
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="subDepartmentEmptyMsg" value='<spring:message code="client.error.subDepartmentEmptyMsg" text="SubDepartment can not be empty."></spring:message>' type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='governorspeechnotice.submissionMsg' text='Do you want to submit the Governor Speech Notice?'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>