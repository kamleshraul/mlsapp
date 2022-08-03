<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			/**** View Question Details ****/
			function viewQuestion(id){
				var questionid=id.split("questionview")[1];
				var href='question/'+questionid+'/edit';
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
				var parameters="houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&questionType="+$("#selectedQuestionType").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						+"&edit=false";
				$.get(href+"?"+parameters,function(data){
					$.unblockUI();	
				    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
			    },'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			    return false;
			}
			$(document).ready(function() {				
				if($('#yaadiLayingDate').val()!='-') {
					$("#yaadiLayingDate option[value='-']").hide();		
					if($('#isYaadiLayingDateSet').val()=='yes') {
						$('#yaadiLayingDate').css('display', 'none');
						$('#existingYaadiLayingDate').css('display', 'inline');
						$('#existingYaadiLayingDate').val($('#yaadiLayingDate').val());
						$('#changeYaadiNumber').css('display', 'inline');
						$('#changeYaadiLayingDate').css('display', 'inline');
					}
				}
				
				var yaadiLaidStatus = $("#yaadiLayingStatusMaster option[value='yaadi_laid']").text();
				var yaadiReadyStatus = $("#yaadiLayingStatusMaster option[value='yaadi_ready']").text();
				if($('#yaadiLayingStatus').val()!=undefined 
						&& ($('#yaadiLayingStatus').val()==yaadiLaidStatus || $('#yaadiLayingStatus').val()==yaadiReadyStatus)) {
					$(".ckb").hide();
				}
				
				$('#linkForReport').css('font-size','20px');
				
				if($('#selectedHouseType').val()=='upperhouse') {
					$('#houseDurationCategoryPara').show();
				} else {
					$('#houseDurationCategoryPara').hide();
				}
				//console.log("houseDurationCategory: " + $('#houseDurationCategory').val());
				$('#yaadiNumber').change(function() {	
					if($('#yaadiNumber').val()==undefined || $('#yaadiNumber').val()=="") {
						$.prompt("Please select yaadi number");
						return false;
					}
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.get('ref/yaadidetails?houseType='+$('#houseTypeId').val()
		        			+'&sessionId='+$('#sessionId').val()
		        			+'&deviceType='+$('#deviceTypeId').val()
		        			+'&yaadiNumber='+$('#yaadiNumber').val(),
		    	            function(data){
				        		$("#resultDiv").empty();
								$("#resultDiv").html(data);
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
				
				$('#changeYaadiNumber').click(function() {		
					var yaadiNumber = $('#yaadiNumber').val();
					if($('#changedYaadiNumberPara').css('display')=='none') {						
						if(yaadiNumber=="") {
							$.prompt("Please select existing yaadi number for the yaadi first");
							return false;
						}
						$('#imageLink_yaadiNumber').attr('title', $('#iconLabelOnClick').val());
						$('#yaadiNumber').attr('readonly', 'readonly');
						$('#changedYaadiNumberPara').show();				
					} else {
						$('#imageLink_yaadiNumber').attr('title', $('#iconLabelOnUndo').val());
						if($('#changedYaadiLayingDatePara').css('display')=='none') {
							$('#yaadiNumber').removeAttr('readonly');
						}						
						$("#changedYaadiNumber").attr('value','');
						$('#changedYaadiNumberPara').hide();
					}
				});
				
				$('#changeYaadiLayingDate').click(function() {		
					var yaadiLayingDate = $('#yaadiLayingDate').val();
					if($('#changedYaadiLayingDatePara').css('display')=='none') {						
						if(yaadiLayingDate=="-") {
							$.prompt("Please select existing yaadi laying date for the yaadi first");
							return false;
						}
						$('#yaadiNumber').attr('readonly', 'readonly');
						$('#imageLink_yaadiLayingDate').attr('title', $('#iconLabelOnClick').val());
						$("#changedYaadiLayingDate option[value='"+yaadiLayingDate+"']").hide();
						$('#yaadiLayingDate').css('display', 'none');
						$('#existingYaadiLayingDate').css('display', 'inline');
						$('#existingYaadiLayingDate').val(yaadiLayingDate);
						$('#changedYaadiLayingDatePara').show();						
					} else {
						if($('#changedYaadiNumberPara').css('display')=='none') {
							$('#yaadiNumber').removeAttr('readonly');
						}						
						$('#imageLink_yaadiLayingDate').attr('title', $('#iconLabelOnUndo').val());
						$("#changedYaadiLayingDate option[value='"+yaadiLayingDate+"']").show();
						$("#changedYaadiLayingDate option[value='-']").attr('selected', 'selected');
						$('#changedYaadiLayingDatePara').hide();
					}
				});
				
				$('#changedYaadiNumber').change(function() {
					if($('#changedYaadiNumber').val()==$('#yaadiNumber').val()) {
						$.prompt("Please select different yaadi number");
						$('#changedYaadiNumber').val("");
						return false;
					}
					$.get('ref/checkduplicateyaadidetails?houseType='+$('#houseTypeId').val()
		        			+'&sessionId='+$('#sessionId').val()
		        			+'&deviceType='+$('#deviceTypeId').val()
		        			+'&yaadiNumber='+$('#changedYaadiNumber').val(),
		    	            function(data){
								if(data==true) {
		       						$.prompt("Yaadi with number " + $('#changedYaadiNumber').val() + " already exists");
		       						$('#changedYaadiNumber').val("");
		       					}	    							 	   				
		    	            }).fail(function(){		    					
		    					if($("#ErrorMsg").val()!=''){
		    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    					}else{
		    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    					}
		    					scrollTop();
		    				});
				});
				
				$('#groupNumber').change(function() {
					var groupNumber = $(this).val();
					if(groupNumber!=undefined && groupNumber!="" && groupNumber!="-") {
						$(".groupRows").hide();
						$(".groupRows[id=group"+groupNumber+"]").show();
					} else {
						$(".groupRows").show();
					}
				});
				
				/**** View Question Details ****/
				$(".questionview").click(function(){
					viewQuestion($(this).attr("id"));
				});
				/**** Check/Uncheck Submit All ****/		
				$("#chkall").change(function(){
					if($(this).is(":checked")){
						$(".action").attr("checked","checked");	
					}else{
						$(".action").removeAttr("checked");
					}
				});
				
				$('#yaadiLayingStatus').change(function() {
					var yaadiDraftedStatus = $("#yaadiLayingStatusMaster option[value='yaadi_drafted']").text();
					var yaadiReadyStatus = $("#yaadiLayingStatusMaster option[value='yaadi_ready']").text();
					var yaadiLaidStatus = $("#yaadiLayingStatusMaster option[value='yaadi_laid']").text();				
					if($(this).val()==yaadiDraftedStatus) {
						$('.ckb').show();
					} else if($(this).val()==yaadiLaidStatus) {
						if(window.confirm("Are you sure you want to lay this yaadi?")) {
							$('.ckb').hide();
						} else {
							$('.ckb').show();
						}						
					} else if($(this).val()==yaadiReadyStatus) {
						$('.ckb').hide();
					}
				});
				
				$('.manualNumber').change(function() {	
					var manualNumber = $(this).val();
					var referredNumberId=$(this).attr('id').split("manual_number")[1];			
					var parentTR = $(this).closest('tr');		
					
					var isDuplicateNumber = false;
					$('.manualNumberEnglish').each(function() {
						if($(this).val()==manualNumber) {
							isDuplicateNumber = true;
							return false;
						}
					});
					if(isDuplicateNumber == false) {
						var regexRefId = referredNumberId + "$";
						$('.manualNumber').each(function() {			
							if($(this).val()==manualNumber) {
								if(!$(this).attr('id').match(new RegExp(regexRefId))) { //perform check except for this number input element
									isDuplicateNumber = true;
									return false;
								}								
							}							
						});
					}					
					if(isDuplicateNumber == true) {
						$.prompt("This question number is already included in the current yaadi!");
						$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
						isDuplicateNumber = false;
						return false;
					}
					
					var parameters = "questionNumber="+manualNumber
								+'&sessionId='+$('#sessionId').val()
								+'&deviceTypeId='+$('#deviceTypeId').val()
								+"&yaadiDetailsId="+$('#yaadiDetailsId').val()
								+"&houseDurationCategory="+$('#houseDurationCategory').val();
					
					$.ajax({url: 'ref/yaadidetails/validateAndLoadQuestionDetailsForUnstarredYaadi', data: parameters, 
						type: 'GET',
				        async: false,
				        beforeSend: function() {
				        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				        },
				        success: function(data) {
							$.unblockUI();
							if(data==undefined || data[0]=="" || data[0]=="0") {
								$.prompt("Some error occurred! Please contact support...");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-1") {
								$.prompt("No question found with given number " + $('#manual_number'+referredNumberId).val() + "!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-2") {
								$.prompt("Question with given number " + $('#manual_number'+referredNumberId).val() + " is clubbed or pending for clubbing approval!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-3") {							
								$.prompt("Question with given number " + $('#manual_number'+referredNumberId).val() + " is not final admitted yet!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-4") {
								$.prompt("Question with given number " + $('#manual_number'+referredNumberId).val() + " is not answered yet!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-5") {
								$.prompt("Question with given number " + $('#manual_number'+referredNumberId).val() + " is already in yaadi with number " + data[7] + "!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-6") {
								$.prompt("Member of question with given number " + $('#manual_number'+referredNumberId).val() + " is already expired!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-7") {
								$.prompt("Member of question with given number " + $('#manual_number'+referredNumberId).val() + " is currently under suspension!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-8") {
								$.prompt("Member of question with given number " + $('#manual_number'+referredNumberId).val() + " is not active in house currently!");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-9") {
								$.prompt("Question with given number " + $('#manual_number'+referredNumberId).val() + " has been removed from existing yaadi! Please unlock the question...");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else if(data[0]=="-10") {
								$.prompt("Answer of question with given number " + $('#manual_number'+referredNumberId).val() + " is not confirmed yet! Please complete the answer confirmation flow...");
								$('#manual_number'+referredNumberId).val($('#manual_number_backup'+referredNumberId).val());
								return false;
								
							} else {
								$('#chk'+referredNumberId).attr('name', 'chk'+data[0]);
								$('#chk'+referredNumberId).attr('id', 'chk'+data[0]);
								$('#manual_number'+referredNumberId).attr('value', data[1]);
								$('#manual_number_backup'+referredNumberId).val(data[1]);
								$('#manual_number_english'+referredNumberId).val(data[8]);
								$('#manual_number'+referredNumberId).attr('id', 'manual_number'+data[0]);	
								$('#manual_number_backup'+referredNumberId).attr('id', 'manual_number_backup'+data[0]);
								$('#manual_number_english'+referredNumberId).attr('id', 'manual_number_english'+data[0]);
								var subjectHtml = data[2] + "<br/><br/>" + data[3];
								$('#manual_subject'+referredNumberId).html(subjectHtml);
								$('#manual_subject'+referredNumberId).attr('id', 'manual_subject'+data[0]);
								var contentHtml = data[4];
								$('#manual_content'+referredNumberId).html(contentHtml);
								$('#manual_content'+referredNumberId).attr('id', 'manual_content'+data[0]);
								var answerHtml = data[5] + "<br/><br/><div align='right'><a href='#yaadiDevicesCountPara'>goto top</a></div>";
								$('#manual_answer'+referredNumberId).html(answerHtml);
								$('#manual_answer'+referredNumberId).attr('id', 'manual_answer'+data[0]);
								parentTR.attr('id', 'group'+data[6]);		
								$.unblockUI();
							}
						}
					});					
				});
				
				$('#linkForReport').click(function() {
					var selectedDeviceIds = "";
					var deSelectedDeviceIds = "";
					$('.action').each(function() {
						if($(this).is(':checked')) {
							var selectedDeviceId = $(this).attr('id').split("chk")[1];
							selectedDeviceIds = selectedDeviceIds + selectedDeviceId + ",";
						} else {
							var deSelectedDeviceId = $(this).attr('id').split("chk")[1];
							deSelectedDeviceIds = deSelectedDeviceIds + deSelectedDeviceId + ",";
						}			
					});				
					if($('#yaadiNumber').val()==undefined || $('#yaadiNumber').val()=="") {
						$.prompt("Please select yaadi number");
						return false;
					} else if($('#yaadiLayingDate').val()==undefined 
							|| $('#yaadiLayingDate').val()=="" || $('#yaadiLayingDate').val()=="-") {
						$.prompt("Please select yaadi laying date");
						return false;
					}
					if(selectedDeviceIds=="") {
						$.prompt("Please select atleast one question in the yaadi!");
						return false;
					} else {
						var isInvalidFormattingFoundInQuestions = false;
						$.ajax({url: 'ref/yaadidetails/validateQuestionsFormattingForUnstarredYaadi', data: "selectedDeviceIds="+selectedDeviceIds, 
							type: 'GET',
					        async: false,
					        success: function(data) {
					        	if(data==undefined || data=="" || data=="error_occurred") {
					        		$.prompt("Some error occurred! Please contact support...");
					        		isInvalidFormattingFoundInQuestions = true;
					        	} else if(data!="formatting_is_valid") {
					        		$.prompt(data);
					        		isInvalidFormattingFoundInQuestions = true;
					        	}
					        }
						});
						if(isInvalidFormattingFoundInQuestions==true) {
							return false;
						}
					}					
					var yaadiLayingStatus = "";
					if($('#yaadiLayingStatus').val()!=undefined && $('#yaadiLayingStatus').val()!="") {
						yaadiLayingStatus = $('#yaadiLayingStatus').val();
					}
					/* $('#linkForReport').attr('href', 'question/report/generateUnstarredYaadiReport?houseType='+$('#houseTypeId').val()
		        			+'&sessionId='+$('#sessionId').val()
		        			+'&deviceType='+$('#deviceTypeId').val()
		        			+'&yaadiDetailsId='+$('#yaadiDetailsId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val()
							+'&yaadiLayingDate='+$('#yaadiLayingDate').val()
							+'&changedYaadiLayingDate='+$('#changedYaadiLayingDate').val()
							+'&changedYaadiNumber='+$('#changedYaadiNumber').val()
							+'&selectedDeviceIds='+selectedDeviceIds
							+'&deSelectedDeviceIds='+deSelectedDeviceIds
							+'&yaadiLayingStatus='+yaadiLayingStatus
							+'&outputFormat=WORD'); */
					console.log("proper page!")
					var parameters = {
							houseType				: $("#houseTypeId").val(),
							sessionId				: $('#sessionId').val(), 
							deviceType				: $("#deviceTypeId").val(), 
							yaadiDetailsId			: $("#yaadiDetailsId").val(),
							yaadiNumber				: $("#yaadiNumber").val(),
							yaadiLayingDate			: $('#yaadiLayingDate').val(), 
							changedYaadiLayingDate	: $("#changedYaadiLayingDate").val(), 
							changedYaadiNumber		: $("#changedYaadiNumber").val(),
							selectedDeviceIds		: selectedDeviceIds,
							deSelectedDeviceIds		: deSelectedDeviceIds, 
							yaadiLayingStatus		: yaadiLayingStatus, 
							outputFormat			: 'WORD'
					}
					form_submit('yaadi_details/generate_yaadi', parameters, 'POST');
					
					var ackMsg = "";
					ackMsg += "<div class='toolTip tpGreen clearfix'>";
					ackMsg += "<p>";
					//ackMsg += "<img src='./resources/images/template/icons/light-bulb-off.png'>";
					ackMsg += "<spring:message code='question.unstarred_yaadi_report.in_process' text='Unstarred Yaadi Report is being generated..'/>";
					ackMsg += "</p>";
					$("#resultDiv").empty();
					$("#resultDiv").html(ackMsg);
				});
			});
		</script>		
		<style type="text/css">
			/***** For custom stripped table UI ****/
			.strippedTable_custom{
				font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
				font-size: 12px;	
				width: 900px;
				text-align: left;
				border-collapse: collapse;
				border-left: 2px solid #000000;
				border-right: 2px solid #000000;
				border-bottom: 2px solid #000000;
			}
			.strippedTable_custom tbody{
			}
			.strippedTable_custom tr:nth-child(even) {background: #C6D3DD/*#CCC*/}
			.strippedTable_custom tr:nth-child(odd) {background: #DAE4EC/*#FFF*/}
			.strippedTable_custom th
			{
				font-size: 13px;
				font-weight: bold;
				padding: 8px;
				/* background: #FEFFD1; */	
				border-top: 2px solid #000000;
				border-bottom: 1px solid #000000;
				/* color: #039; */
				background-color: #A2C6E4;	
			}
			.strippedTable_custom td
			{
				padding: 8px;
				/* background: #e8edff; */
				border-bottom: 1px solid #000000;
				/* color: #669; */
				border-top: 1px solid transparent;
				max-width: 250px;
			}
		</style> 
	</head>	
	<body>		
		<p id="overlay_error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<p style="color: #FF0000;"><spring:message code="${error}" text="Error Occured Contact For Support."/></p>
		</c:if>
		<div class="fields clearfix watermark">
		<h3><spring:message code='question.generateUnstarredYaadiReport.header' text='Unstarred Questions Yaadi'/>:</h3>
		<p style="margin-top: 20px;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiNumber' text='Yaadi Number'/></label>
			<input id="yaadiNumber" class="sInteger" name="yaadiNumber" value="${yaadiNumber}"/>
			<c:if test="${empty yaadiLayingStatus or yaadiLayingStatus.type=='yaadi_drafted'}">
				<c:set var="iconLabel">
					<spring:message code='question.unstarred_yaadi_report.changeField' text='Change'/>
				</c:set>
				<a href="#" id="changeYaadiNumber" style="margin-left: 10px;text-decoration: none;display: none;">				
					<img src="./resources/images/Revise.jpg" title="${iconLabel}" id="imageLink_yaadiNumber" width="20px" height="20px"/>
				</a>
			</c:if> 
		</p>
		<p id="changedYaadiNumberPara" style="margin-top: 10px;display: none;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.newYaadiNumber' text='New Yaadi Number'/></label>
			<input id="changedYaadiNumber" class="sInteger" name="changedYaadiNumber"/>			
		</p>
		<p style="margin-top: 10px;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiLayingDate' text='Yaadi Laying Date'/></label>
			<select id="yaadiLayingDate" name="yaadiLayingDate" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingDates}" var="i">					
					<c:choose>
						<c:when test="${i==yaadiLayingDate}">
							<option value="${i}" selected="selected">${i}</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">${i}</option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</select>	
			<input id="existingYaadiLayingDate" class="sText datemask" style="display:none;" readonly="readonly"/>
			<c:if test="${empty yaadiLayingStatus or yaadiLayingStatus.type=='yaadi_drafted' or yaadiLayingStatus.type=='yaadi_ready'}">
				<c:set var="iconLabel">
					<spring:message code='question.unstarred_yaadi_report.changeYaadiLayingDate' text='Change'/>
				</c:set>
				<a href="#" id="changeYaadiLayingDate" style="margin-left: 10px;text-decoration: none;display: none;">				
					<img src="./resources/images/Revise.jpg" title="${iconLabel}" id="imageLink_yaadiLayingDate" width="20px" height="20px"/>
				</a>	
			</c:if>				
		</p>
		<p id="changedYaadiLayingDatePara" style="margin-top: 10px;display: none;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.newYaadiLayingDate' text='New Yaadi Laying Date'/></label>
			<select id="changedYaadiLayingDate" name="changedYaadiLayingDate" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingDates}" var="i">					
					<c:choose>
						<c:when test="${i==currentDate}">
							<option value="${i}" selected="selected">${i}</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">${i}</option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</select>					
		</p>
		<c:if test="${not empty yaadiLayingStatus}">
		<p id="yaadiFinalizationPara" style="margin-top: 10px;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiLayingStatus' text='Yaadi Laying Status'/></label>
			<c:choose>
				<c:when test="${yaadiLayingStatus.type=='yaadi_laid'}">
					<input type="hidden" id="yaadiLayingStatus" name="yaadiLayingStatus" value="${yaadiLayingStatus.id}">
					<input type="text" value="${yaadiLayingStatus.name}" readonly="readonly" style="font-weight: bold;">
				</c:when>
				<c:otherwise>
					<select id="yaadiLayingStatus" name="yaadiLayingStatus" class="sSelect">
						<c:forEach items="${yaadiLayingStatuses}" var="i">					
							<c:choose>
								<c:when test="${i.id==yaadiLayingStatus.id}">
									<option value="${i.id}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}">${i.name}</option>
								</c:otherwise>
							</c:choose>	
						</c:forEach>
					</select>
				</c:otherwise>
			</c:choose>						
		</p>		
		</c:if>		
		<select id="yaadiLayingStatusMaster" style="display:none;">
			<c:forEach items="${yaadiLayingStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
		</select>
		<p>
			<label class="small"><spring:message code='question.unstarred_yaadi_report.groupNumber' text='Group Number'/></label>
			<select id="groupNumber"  class="sSelect" name="groupNumber">
				<option value="-"><spring:message code='please.select' text='Please Select'/></option>
				<c:forEach items="${groupNumbers}" var="gn">
					<option value="${gn.number}">${gn.name}</option>
				</c:forEach>
			</select>			
		</p>
		<p id="houseDurationCategoryPara" style="display: none;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.houseDurationCategory' text='House Duration'/></label>
			<select id="houseDurationCategory"  class="sSelect" name="houseDurationCategory">
				<option value="currentHouse" selected="selected"><spring:message code='house.category.current' text='Current House'/></option>
				<option value="previousHouse"><spring:message code='house.category.previous' text='Previous House'/></option>
			</select>			
		</p>		
		
		<div id="yaadiDevicesDiv">
			<p id="yaadiDevicesCountPara" style="margin-top: 10px;margin-bottom: 10px;">
				<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiDevicesCount' text='Total Number of Questions'/></label>
				<input id="yaadiDevicesCount" class="sInteger" value="${empty yaadiDevicesCount?'-':yaadiDevicesCount}" readonly="readonly"/>	
			</p>
			<c:choose>
				<c:when test="${!(empty totalDevicesInYaadiVOs) }">
					<table class="strippedTable_custom">
						<thead>
						<tr>
							<th width="100px">
								<input type="checkbox" id="chkall" name="chkall" class="sCheck ckb" value="true">
								<spring:message code="question.number" text="Question Number"></spring:message>								
							</th>
							<th width="250px"><spring:message code="question.subjectDetails" text="Subject"></spring:message></th>						
							<th width="250px"><spring:message code="question.content" text="Content"></spring:message></th>
							<th width="250px"><spring:message code="question.answer" text="Answer"></spring:message></th>
						</tr>		
						<thead>
						<tbody>
						<c:forEach items="${totalDevicesInYaadiVOs}" var="i">
							<tr id="group${i.groupNumber}" class="groupRows">
								<td width="100px">
									<input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action ckb" checked="checked"  style="margin-right: 10px;">						
									<c:choose>
										<c:when test="${not empty manuallyEnteringAllowed and manuallyEnteringAllowed=='true'}">
											<c:choose>
												<c:when test="${i.isPresentInYaadi=='true'}">
													<input type="text" class="existingNumber" value="${i.formattedNumber}" readonly="readonly" style="width: 50px;padding-left: 5px;font-weight: bold;background-color: lightblue;"/>
												</c:when>
												<c:otherwise>
													<input type="text" id="manual_number${i.id}" class="manualNumber" value="${i.formattedNumber}" style="width: 50px;padding-left: 5px;font-weight: bold;"/>
													<input type="hidden" id="manual_number_backup${i.id}" value="${i.formattedNumber}"/>
													<input type="hidden" id="manual_number_english${i.id}"  class="manualNumberEnglish" value="${i.number}"/>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<a href="#" class="questionview" id="questionview${i.id}">
												<c:choose>
													<c:when test="${i.isPresentInYaadi=='true'}">
														${i.formattedNumber}
													</c:when>
													<c:otherwise>
														${i.formattedNumber}<sup><b>*</b></sup>
													</c:otherwise>
												</c:choose>
											</a>
										</c:otherwise>
									</c:choose>
								</td>
								<td width="250px" id="manual_subject${i.id}">
									${i.subject}	
									<br/><br/>		
									${i.shortDetails}												
								</td>
								<td width="250px" id="manual_content${i.id}">${i.content}</td>
								<td width="250px" id="manual_answer${i.id}">
									${i.answer}
									<br/><br/>
									<div align="right">
									  <a href="#yaadiDevicesCountPara">goto top</a>
									</div>
								</td>
							</tr>
						</c:forEach>
						</tbody>	
					</table>
				</c:when>
				<c:otherwise>
					<h3 align="center"><spring:message code="question.unstarred_yaadi_report.noquestions" text="No Questions Found"></spring:message></h3>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${not empty yaadiDevicesCount}">
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<a href="#" id="linkForReport"><spring:message code='question.unstarred_yaadi_report.generateReport' text='Generate Report'/></a>
				</p>
			</div>
		</c:if>				
		</div>
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="sessionId" name="sessionId" value="${sessionId}"/>
		<input type="hidden" id="houseTypeId" name="houseTypeId" value="${houseTypeId}"/>	
		<input type="hidden" id="deviceTypeId" name="deviceTypeId" value="${deviceTypeId}"/>	
		<input type="hidden" id="yaadiDetailsId" name="yaadiDetailsId" value="${yaadiDetailsId}"/>
		<input type="hidden" id="iconLabelOnClick" value="<spring:message code='question.unstarred_yaadi_report.undoChangeField' text='Undo'/>">
		<input type="hidden" id="iconLabelOnUndo" value="<spring:message code='question.unstarred_yaadi_report.changeField' text='Change'/>">		
		<input type="hidden" id="isYaadiLayingDateSet" value="${isYaadiLayingDateSet}"/>	
	</body>
</html>