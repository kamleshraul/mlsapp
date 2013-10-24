<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><spring:message code="bill" text="Bill Information System"/></title>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
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
		var primaryMemberControlName=$(".autosuggest").attr("id");
		
		/**** Load Sub Departments ****/
		function loadSubDepartments(ministry){
			$.get('ref/ministry/subdepartments?ministry='+ministry,function(data){
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
			});
		}
		
		$(document).ready(function(){		
			if($('#ministrySelected').val()=="" || $('#ministrySelected').val()==undefined){		
				$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select ministry for now, this option will be useful.
				$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
				
			if($('#subDepartmentSelected').val()=="" || $('#subDepartmentSelected').val()==undefined){
				$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select subdepartment for now, this option will be useful.
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}
			
			if($('#selectedBillType').val()=="" || $('#selectedBillType').val()==undefined){		
				$("#billType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill type for now, this option will be useful.
				$("#billType").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			
			if($('#selectedBillKind').val()=="" || $('#selectedBillKind').val()==undefined){		
				$("#billKind").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill kind for now, this option will be useful.
				$("#billKind").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			
			if($('#selectedIntroducingHouseType').val()=="" || $('#selectedIntroducingHouseType').val()==undefined){		
				$("#introducingHouseType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select introducing housetype for now, this option will be useful.
				$("#introducingHouseType").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}			
			
			/**** Auto Suggest(member login)- Member ****/		
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
					}, response );
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
			
			/**** refer act only in case of amendment bill type ****/
			if($('#typeOfSelectedBillType').val()!='amending') {
				$('#referredActDiv').hide();
			}
			
			if($('#typeOfSelectedBillType').val()!='replace_ordinance') {
				$('#referredOrdinanceDiv').hide();
			}
			
			$('#billType').change(function() {
				$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
					if(data!=undefined || data!='') {
						if(data=='amending') {
							$('#referredActDiv').show();
							$('#referredOrdinanceDiv').hide();
						} else if(data=='replace_ordinance'){
							$('#referredOrdinanceDiv').show();
							$('#referredActDiv').hide();
						}else{
							$('#referredActDiv').hide();
							$('#referredOrdinanceDiv').hide();
						}
					} else {
						alert("Some Error Occured!");
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
			
			/**** Referring Act for Amendment Bill ****/
			$('#referAct').click(function() {
				referenceForBill('act');
			});
			
			/**** Referring Ordinance for Amendment Bill ****/
			$('#referOrdinance').click(function() {
				referenceForBill('ordinance');
			});	
			
			/**** Removing Referred Act from Amendment Bill ****/
			$('#dereferAct').click(function() {
				if($('#viewReferredAct').text()!="-") {
					$('#viewReferredAct').css('text-decoration','none');
					$.prompt($('#dereferActWarningMessage').val(),{
						buttons: {Ok:true}, callback: function(v){
					   		if(v){
					   			$('#referredAct').val("");		
					   			$('#viewReferredAct').text("-");
					   			$('#viewReferredAct').css('text-decoration','none');
								$('#referredActYear').text("");
					   			//code left to do..
					   		}     						
						}
					});
				}				
				return false;
			});
			
			/**** view detail of referred act (currently showing pdf of act) ****/		
			$('#viewReferredAct').click(function() {
				if(this.text!='-') {					
					var referredActId = $('#referredAct').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='act/'+referredActId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			
			/**** view detail of referred ordinance****/		
			$('#viewReferredOrdinance').click(function() {
				if(this.text!='-') {					
					var referredOrdinanceId = $('#referredOrdinance').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='ordinance/'+referredOrdinanceId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			
			//send for supporting member approval
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
				
				if($('#referredActDiv').is(':hidden')) {
					$('#referredAct').val("");
				}
				
				if($('#referredOrdinanceDiv').is(':hidden')) {
					$('#referredOrdinance').val("");
				}
				
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
			    	            });
			        }
				}});			
		        return false; 
			});
			
			$("#submitbill").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				
				if($('#referredActDiv').is(':hidden')) {
					$('#referredAct').val("");
				}
				
				if($('#referredOrdinanceDiv').is(':hidden')) {
					$('#referredOrdinance').val("");
				}
					
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
			    	            });
			        }
				}});		
				return false;  
		    });
			
				/**** Right Click Menu ****/
				$(".refer").contextMenu({menu: 'contextMenuItems'},
			        function(action, el, pos) {
						if(action=='dereferOrdinance'){
							removeReferredOrdinance();	
						}
		    	});
			});
		
		/**** Removing Referred Ordinance from replace_ordinance Bill ****/
		function removeReferredOrdinance(){
			if($('#viewReferredOrdinance').text()!="-") {
				$('#viewReferredOrdinance').css('text-decoration','none');
				$.prompt($('#dereferOrdinanceWarningMessage').val(),{
					buttons: {Ok:true}, callback: function(v){
				   		if(v){
				   			$('#referredOrdinance').val("");		
				   			$('#viewReferredOrdinance').text("-");
				   			$('#viewReferredOrdinance').css('text-decoration','none');
							$('#referredOrdinanceYear').text("");
				   		}     						
					}
				});
			}				
		}
		
		function referenceForBill(refType){
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
			$.get('bill/referAct/init?action='+refType,function(data){
				$.unblockUI();			
				if(refType=='act'){
					$("#referringActResultDiv").html(data);					
					$("#referringActResultDiv").show();
				}else if(refType=='ordinance'){
					$("#referringOrdinanceResultDiv").html(data);					
					$("#referringOrdinanceResultDiv").show();
				}
				$("#billDiv").hide();
				$("#backToBillDiv").show();
				
			},'html');
		}
	</script>
	<!-- <style type="text/css">
		.position_after_element {display: inline;}
	</style> -->
</head>
<body>
	<div class="fields clearfix watermark">
	<div id="billDiv">
		<form:form action="bill" method="POST" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<div id="reportDiv">
		<h2><spring:message code="bill.new.heading" text="Enter Bill Details"/>		
		</h2>
		<form:errors path="version" cssClass="validationError"/>	
		<%-- <security:authorize access="hasAnyRole('ROIS_CLERK')">	
		<p>
			<label class="small"><spring:message code="bill.number" text="Bill Number"/>*</label>
			<form:input path="number" cssClass="sText"/>
			<form:errors path="number" cssClass="validationError"/>
		</p>
		</security:authorize> --%>
	
		<p style="display:none;">
			<label class="small"><spring:message code="bill.houseType" text="House Type"/>*</label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input id="houseType" name="houseType" value="${houseType}" type="hidden">
			<form:errors path="houseType" cssClass="validationError"/>			
		</p>	
	
		<p style="display:none;">
			<label class="small"><spring:message code="bill.year" text="Year"/>*</label>
			<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
			<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
		</p>
	
		<p style="display:none;">
			<label class="small"><spring:message code="bill.sessionType" text="Session Type"/>*</label>		
			<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
			<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
			<input type="hidden" id="session" name="session" value="${session}"/>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
	
		<p style="display:none;">
			<label class="small"><spring:message code="bill.deviceType" text="Device Type"/>*</label>
			<input id="formattedDeviceTypeForBill" name="formattedDeviceTypeForBill" value="${formattedDeviceTypeForBill}" class="sText" readonly="readonly">
			<input id="type" name="type" value="${deviceTypeForBill}" type="hidden">
			<input id="originalType" name="originalType" value="${deviceTypeForBill}" type="hidden">			
			<form:errors path="type" cssClass="validationError"/>		
		</p>	
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
		<p>
			<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
			<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
			<form:errors path="primaryMember" cssClass="validationError"/>		
		</p>
		<p>
			<label class="small"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
		</p>
		</security:authorize>
		<security:authorize access="hasAnyRole('BIS_CLERK')">		
		<p>
			<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
			<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
			<form:errors path="primaryMember" cssClass="validationError"/>		
		</p>	
		</security:authorize>
		
		<c:if test="${selectedDeviceTypeForBill == 'bills_government'}">
		<p>
			<label class="small"><spring:message code="bill.introducingHouseType" text="Introducing House Type"/></label>
			<form:select id="introducingHouseType" class="sSelect" path="introducingHouseType">
			<c:forEach var="i" items="${introducingHouseTypes}">	
				<c:choose>
					<c:when test="${i.id == selectedIntroducingHouseType}">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>						
			</c:forEach>
			</form:select>		
			<form:errors path="introducingHouseType" cssClass="validationError"/>				
		</p>
		</c:if>
		
		<p>
			<label class="small"><spring:message code="bill.billType" text="Bill Type"/></label>
			<form:select id="billType" class="sSelect" path="billType">
			<c:forEach var="i" items="${billTypes}">
				<c:choose>
					<c:when test="${i.id == selectedBillType}">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>							
			</c:forEach>
			</form:select>	
			<label class="small"><spring:message code="bill.billKind" text="Bill Kind"/></label>
			<form:select id="billKind" class="sSelect" path="billKind">
			<c:forEach var="i" items="${billKinds}">
				<c:choose>
					<c:when test="${i.id == selectedBillKind}">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>							
			</c:forEach>
			</form:select>					
		</p>
		
		<p>
			<label class="small"><spring:message code="bill.ministry" text="Ministry"/></label>
			<form:select path="ministry" id="ministry" class="sSelect">
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
			</form:select>
			<form:errors path="ministry" cssClass="validationError"/>			
			<label class="small"><spring:message code="bill.subdepartment" text="Sub Department"/></label>
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
		
		<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
		<p>
			<label class="centerlabel"><spring:message code="bill.supportingMembers" text="Supporting Members"/></label>
			<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
			<%-- <label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="bill.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label> --%>										
			<c:if test="${!(empty supporingMembers)}">		
			<select  name="selectedSupportingMembers" multiple="multiple">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" class="${i.member.getFullname()}"></option>
			</c:forEach>		
			</select>
			</c:if>
			<form:errors path="supportingMembers" cssClass="validationError"/>	
		</p>
		</c:if>
		
		<div id="referredActDiv">
			<p>
				<a href="#" id="referAct" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referAct" text="Refer Act"></spring:message></a>
				<a href="#" id="dereferAct" style="margin: 20px;"><spring:message code="bill.dereferAct" text="Derefer Act"></spring:message></a>
			</p>		
			<p>
				<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty referredAct)}">
						<a href="#" id="viewReferredAct" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
						<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
					</c:when>
					<c:otherwise>
						<a href="#" id="viewReferredAct" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
						<label id="referredActYear"></label>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="referredAct" name="referredAct" value="${referredAct}">
			</p>
		</div>	
		
		<div id="referredOrdinanceDiv" style="display: none;">
			<p>
				<a href="#" id="referOrdinance" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referOrdinance" text="Refer Ordinance"></spring:message></a>
			</p>		
			<p>
				<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty referredOrdinance)}">
						<a href="#" id="viewReferredOrdinance" style="font-size: 18px;" class="refer"><c:out value="${referredOrdinanceNumber}"></c:out></a>
						<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
					</c:when>
					<c:otherwise>
						<a href="#" id="viewReferredOrdinance" style="font-size: 18px; text-decoration: none;" class="refer"><c:out value="-"></c:out></a>
						<label id="referredOrdinanceYear"></label>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
			</p>
		</div>		
		
		<div>
			<fieldset>
				<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.titles" text="Titles of Bill" /></label></legend>
				<div id="titles_div">
					<c:forEach var="i" items="${titles}">
						<p>
							<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
							<textarea rows="2" cols="50" id="title_text_${i.language.type}" name="title_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
					</c:forEach>
				</div>
			</fieldset>
		</div>	
	
		<div>
			<fieldset>
				<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.contentDrafts" text="Drafts of Bill" /></label></legend>
				<div id="contentDrafts_div">
					<c:forEach var="i" items="${contentDrafts}">
						<p>
							<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
							<textarea class="wysiwyg" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<div>
			<fieldset>
				<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statementOfObjectAndReasonDrafts" text="Statement of Object & Reason" /></label></legend>
				<div id="statementOfObjectAndReasonDrafts_div">
					<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
						<p>
							<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
							<textarea class="wysiwyg" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<div>
			<fieldset>
				<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.financialMemorandumDrafts" text="Financial Memorandum" /></label></legend>
				<div id="financialMemorandumDrafts_div">
					<c:forEach var="i" items="${financialMemorandumDrafts}">
						<p>
							<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
							<textarea class="wysiwyg" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<div>
			<fieldset>
				<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statutoryMemorandumDrafts" text="Statutory Memorandum" /></label></legend>
				<div id="statutoryMemorandumDrafts_div">
					<c:forEach var="i" items="${statutoryMemorandumDrafts}">
						<p>
							<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
							<textarea class="wysiwyg" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<c:if test="${selectedDeviceTypeForBill=='bills_government'}">
		<p>
		<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
		<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg"></form:textarea>
		<form:errors path="opinionSoughtFromLawAndJD" />
		</p>
		</c:if>		
		</div>
		 <div class="fields">
			<h2></h2>
			<p class="tright">
			<security:authorize access="hasAnyRole('BIS_CLERK')">	
				<input id="submitbill" type="button" value="<spring:message code='bill.submitBill' text='Submit Bill'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="sendforapproval" type="button" value="<spring:message code='bill.sendforapproval' text='Send For Approval'/>" class="butDef">
				<input id="submitbill" type="button" value="<spring:message code='bill.submitBill' text='Submit Bill'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>				
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
	<input id="selectedBillType" value="${selectedBillType}" type="hidden">
	<input id="selectedBillKind" value="${selectedBillKind}" type="hidden">
	<input id="selectedIntroducingHouseType" value="${selectedIntroducingHouseType}" type="hidden">
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
	<input id="submissionMsg" value="<spring:message code='bill.client.prompt.submit' text='Do you want to submit the bill'></spring:message>" type="hidden">	
	<input type="hidden" id="typeOfSelectedDeviceType" value="${selectedDeviceTypeForBill}">	
	<input type="hidden" id="typeOfSelectedBillType" value="${typeOfSelectedBillType}" />
	<input type="hidden" id="referredActYearLabel" value="<spring:message code="bill.referredActYear" text="Year"/>">
	<input type="hidden" id="referredOrdinanceYearLabel" value="<spring:message code="bill.referredOrdinanceYear" text="Year"/>">
	<input type="hidden" id="dereferActWarningMessage" value="<spring:message code="dereferActWarningMessage" text="Do you really want to de-refer this act?"/>">
	<input type="hidden" id="dereferOrdinanceWarningMessage" value="<spring:message code="dereferOrdinanceWarningMessage" text="Do you really want to de-refer this ordinance?"/>">
	</div>
	</div>
	<ul id="contextMenuItems" style="width: 150px; list-style-type: none; list-style-position: inside;">
		<li><a href="#dereferOrdinance" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
	</ul>
	<div id="referringActResultDiv" style="display:none;">
	</div>
	<div id="referringOrdinanceResultDiv" style="display:none;">
	</div>
</body>
</html>