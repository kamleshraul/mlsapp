<%-- <%@ include file="/common/taglibs.jsp"%>
<div class="toolTip tpGreen clearfix">
	<p>
		<img src="./resources/images/template/icons/light-bulb-off.png">
		<spring:message code="bill.section.added" text="Section Added Successfully"/>
	</p>
	<p></p>
</div> --%>

<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title><spring:message code="bill.addSection" text="Add Section" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script type="text/javascript">
			$(document).ready(function(){	
				initControls();
				
				if($('#sectionOrder').val()=="") {
					$('#sectionOrderPara').hide();
				}				
				
				$('#changeSectionNumber').hide();
				$('#changedSectionNumberDiv').hide();
				
				$('#changeSectionOrder').hide();
				$('#changedSectionOrderDiv').hide();
				
				$('#sectionNumber').change(function() {
					$("#ack_header").empty();
					
					if($('#sectionNumber').val()=="") {	
						$('#sectionOrder').val("");						
						return false;
					} else {						
						$.get('ref/bill/checkSectionForEdit?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#sectionNumber").val(), function(data) {
							if(data.isSelected==false) {
								alert($('#sectionNotFoundMsg').val());
								$("#sectionNumber").val("");
								$("#sectionOrder").val("");
								$('#sectionOrderPara').hide();
								$('#changeSectionNumber').hide();
								$('#changedSectionNumber').val("");
								$('#changedSectionNumberDiv').hide();								
								$('#changeSectionOrder').hide();
								$('#changedSectionOrder').val("");
								$('#changedSectionOrderDiv').hide();
							} else if(data.isSelected==true) {
								$('#changeSectionNumber').show();
								$("#sectionOrder").val(data.order);
								$("#sectionOrderPara").show();
								$('#changeSectionOrder').show();
								$('#changedSectionNumber').val("");
								$('#changedSectionNumberDiv').hide();
								$('#changedSectionOrder').val("");
								$('#changedSectionOrderDiv').hide();
								if($("#sectionText").val()=="" || $("#sectionText").val()=="<p></p>") {
									$("#sectionText").wysiwyg("setContent", data.value);
								} else {
									var r1 = confirm($('#updateTextForSectionPrompt').val());
							        if (r1 == true)
							        {
							        	$("#sectionText").wysiwyg("setContent", data.value);
							        }
								}
							} else {								
								if(data.name=="invalidSectionNumber") {
									alert($('#invalidSectionNumberMsg').val());
									$("#sectionNumber").val("");
									$("#sectionOrder").val("");
									$('#sectionOrderPara').hide();
									$('#changeSectionNumber').hide();
									$('#changedSectionNumber').val("");
									$('#changedSectionNumberDiv').hide();								
									$('#changeSectionOrder').hide();
									$('#changedSectionOrder').val("");
									$('#changedSectionOrderDiv').hide();
								} else if(data.name=="some_error") {
									alert("Error occured.. Please contact support.");
									$("#sectionNumber").val("");
									$("#sectionOrder").val("");
									$('#sectionOrderPara').hide();
									$('#changeSectionNumber').hide();
									$('#changedSectionNumber').val("");
									$('#changedSectionNumberDiv').hide();								
									$('#changeSectionOrder').hide();
									$('#changedSectionOrder').val("");
									$('#changedSectionOrderDiv').hide();
								}		
							}							
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					}
				});
				
				$('#changeSectionNumber').click(function() {
					$('#changedSectionNumberDiv').show();
				});
				
				$('#changedSectionNumber').change(function() {
					if($(this).val()=="") {						
						return false;
					} else {
						$.get('ref/bill/checkSectionForEdit?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#changedSectionNumber").val(), function(data) {
							if(data.isSelected==true) {
								alert($('#sectionAlreadyExistsMsg').val());		
								$('#changedSectionNumber').val("");
								$('#changedSectionOrder').val("");
							} else if(data.isSelected==false) {
								$('#changedSectionOrder').val($('#sectionOrder').val());
								$('#changedSectionOrderDiv').show();
								$('#changedSectionOrder').change();
							} else {								
								if(data.name=="invalidSectionNumber") {
									alert($('#invalidSectionNumberMsg').val());
									$("#changedSectionNumber").val("");									
								} else if(data.name=="some_error") {
									alert("Error occured.. Please contact support.");
									$("#changedSectionNumber").val("");
								}		
							}							
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					}
				});
				
				$('#changeSectionOrder').click(function() {
					$('#changedSectionOrderDiv').show();
				});
				
				$('#changedSectionOrder').change(function() {
					if($(this).val()=="") {
						alert("empty order");
						return false;
					} else {
						var sectionNumberForOrder = $("#sectionNumber").val();
						if($("#changedSectionNumber").val()!="") {
							sectionNumberForOrder = $("#changedSectionNumber").val();
						}
						$.get('ref/bill/checkSectionForEdit?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+sectionNumberForOrder+'&sectionOrder='+$("#changedSectionOrder").val(), function(data) {
							if(data.isSelected==true) {
								alert($('#sectionExistsAtGivenOrderMsg').val());
								$('#changedSectionOrder').val("");								
							} else {								
								if(data.name=="some_error") {
									alert("Error occured.. Please contact support.");
									$('#changedSectionOrder').val("");	
								}		
							}							
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					}
				});
				
				$('#submitSection').click(function() {
					$("#ack_header").empty();
					
					if($('#sectionNumber').val()=="") {
						alert($('#emptySectionNumberMsg').val());
						return false;
					} else {
						//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post('bill/addSection?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#sectionNumber").val()+'&sectionText='+$("#sectionText").val(), function(data) {
							//$.unblockUI();
							$('.fancybox-inner').html(data);							
	       				 	var updatedVersion = parseInt($("#version").val(), 10);
							$("#version").val(updatedVersion + 1);									
							//scrollTop();
						}).fail(function() {
							//$.unblockUI();
							$("#ack_header").html($("#errorInUpdationMsg").val()).css({'color':'red', 'display':'block'});
							//scrollTop();
						});
					}					
				});
				//$.fancybox.close();
			});
		</script>
	</head>
	
	<body>	
		<div class="fields">
			<div id="ack_header">
				<c:choose>
					<c:when test="${ack_message=='success_addition'}">
						<h3 style="color: green;"><spring:message code='bill.section.successInAdditionMsg' text='Section successfully added'/></h3>
					</c:when>
					<c:when test="${ack_message=='success_updation'}">
						<h3 style="color: green;"><spring:message code='bill.section.successInUpdationMsg' text='Section successfully updated'/></h3>
					</c:when>
					<c:when test="${ack_message=='failure_addition'}">
						<h3 style="color: red;"><spring:message code='bill.section.failureInAdditionMsg' text='Section could not be added'/></h3>
					</c:when>
					<c:when test="${ack_message=='success_updation'}">
						<h3 style="color: red;"><spring:message code='bill.section.failureInUpdationMsg' text='Section could not be updated'/></h3>
					</c:when>					
				</c:choose>
			</div>			
			
			<%-- <h3 style="text-align: center; margin-bottom: 20px;"><spring:message code='bill.section.edit' text='Edit Section'/></h3> --%>
			
			<p>
				<label class="small"><spring:message code="bill.section.number" text="Section Number"/></label>
				<input id="sectionNumber" name="sectionNumber" class="sText" value="${selectedSectionNumber}">
				<a id="changeSectionNumber" href="javascript:void(0)"><spring:message code="bill.section.changeSectionNumber" text="Change"/></a>
				<div id="changedSectionNumberDiv" style="float: right;margin-top: -30px">
					<label class="small"><spring:message code="bill.section.newNumber" text="New Section Number"/></label>
					<input id="changedSectionNumber" name="changedSectionNumber" class="sText">
				</div>
			</p>
			
			<p id="sectionOrderPara" style="margin-top: 15px;">
				<label class="small"><spring:message code="bill.section.order" text="Section Order"/></label>
				<input id="sectionOrder" name="sectionOrder" class="sInteger" readonly="readonly" value="${selectedSectionOrder}">
				<a id="changeSectionOrder" href="javascript:void(0)"><spring:message code="bill.section.changeSectionOrder" text="Change"/></a>
				<div id="changedSectionOrderDiv" style="float: right;margin-top: -30px">
					<label class="small"><spring:message code="bill.section.newOrder" text="New Section Order"/></label>
					<input id="changedSectionOrder" name="changedSectionOrder" class="sText">
				</div>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="bill.section.text" text="Section Text"/></label>
				<textarea class="wysiwyg" id="sectionText" name="sectionText">${selectedSectionText}</textarea>
			</p>
			
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submitSection" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				</p>
			</div>
		</div>
		<input type="hidden" id="sectionLanguage" name="sectionLanguage" value="${language}">
		<input type="hidden" id="isTextSelected" value="${not empty selectedText}">		
		<input type="hidden" id="updateTextForSectionPrompt" value="<spring:message code='bill.section.updateTextForSectionPrompt' text='Do you want to get text of this section?'/>">
		<input type="hidden" id="invalidSectionNumberMsg" value="<spring:message code='bill.section.invalidSectionNumberMsg' text='You have entered invalid section number'/>">
		<input type="hidden" id="sectionNotFoundMsg" value="<spring:message code='bill.section.sectionNotFoundMsg' text='Section with given section number not found'/>">
		<input type="hidden" id="sectionExistsAtGivenOrderMsg" value="<spring:message code='bill.section.sectionExistsAtGivenOrderMsg' text='There is already a section at given order'/>">
		<input type="hidden" id="emptySectionNumberMsg" value="<spring:message code='bill.section.emptySectionNumberMsg' text='Please enter section number'/>">
		<input type="hidden" id="successInUpdationMsg" value="<spring:message code='bill.section.successInUpdationMsg' text='Section successfully updated'/>">
		<input type="hidden" id="errorInUpdationMsg" value="<spring:message code='bill.section.errorInUpdationMsg' text='Error in section updation.. Please contact support'/>">
	</body>
</html>