<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title><spring:message code="bill.addSection" text="Add Section" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script type="text/javascript">
			$(document).ready(function(){	
				initControls();
				
				$('#sectionNumber').change(function() {
					$("#ack_header").empty();
					if($('#sectionNumber').val()!="") {
						//check if section exists & show its text if it exists
						$.get('ref/bill/checkSectionForNew?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#sectionNumber").val()
								+'&sectionOrder='+$("#sectionOrder").val(), function(data) {
							if(data=='invalidSectionNumber') {
								alert($('#invalidSectionNumberMsg').val());
							} else if(data=='section_exists_already') {								
								alert($('#sectionAlreadyExistsMsg').val());		
								$('#sectionNumber').val("");
								return false;
							} else if(data=="section_with_same_order") {								
								/* $.prompt($('#sectionExistsAtGivenOrderPrompt').val(),{
									buttons: {Ok:true, Cancel:false}, callback: function(v){
							        if(!v){
							        	$('#sectionOrder').val("");
					    	        }
								}});														
						        return false; */	
						        var r = confirm($('#sectionExistsAtGivenOrderPrompt').val());
						        if (r == false)
						        {
						        	$('#sectionOrder').val("");
						        }						        
							} else if(data=="error") {
								alert("Error occured.. Please contact support.");	
								$('#sectionNumber').val("");
								return false;
							}						
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					}									
				});
				
				$('#sectionOrder').change(function() {
					$("#ack_header").empty();
					if($('#sectionNumber').val()!="") {
						//check if section exists & show its text if it exists
						$.get('ref/bill/checkSectionForNew?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#sectionNumber").val()
								+'&sectionOrder='+$("#sectionOrder").val(), function(data) {
							if(data=='invalidSectionNumber') {
								alert($('#invalidSectionNumberMsg').val());
							} else if(data=='section_exists_already') {								
								alert($('#sectionAlreadyExistsMsg').val());		
								$('#sectionNumber').val("");
								return false;
							} else if(data=="section_with_same_order") {
								var r = confirm($('#sectionExistsAtGivenOrderPrompt').val());
						        if (r == false)
						        {
						        	$('#sectionOrder').val("");
						        }
							} else if(data=="error") {
								alert("Error occured.. Please contact support.");	
								$('#sectionNumber').val("");
								return false;
							}						
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					}									
				});
				
				$('#submitSection').click(function() {
					if($('#sectionNumber').val()=="") {
						alert($('#emptySectionNumberMsg').val());
						return false;
					} else if($('#sectionOrder').val()=="") {
						alert($('#emptySectionOrderMsg').val());
						return false;
					} else {
						/* $.prompt($('#sectionSubmissionPrompt').val(),{
							buttons: {Ok:true, Cancel:false}, callback: function(v){
					        if(v){
					        	
			    	        }
						}});														
				        return false; */
						var r = confirm($('#sectionSubmissionPrompt').val());
				        if (r == true)
				        {
				        	//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
							$.post('bill/addSection?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
									+'&sectionNumber='+$("#sectionNumber").val()+'&sectionOrder='+$("#sectionOrder").val()
									+'&sectionText='+$("#sectionText").val()+'&usergroupType='+$("#usergroupType").val(), function(data) {
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
				        return false;												
					}					
				});
				//$.fancybox.close();
			});
		</script>
	</head>
	
	<body>	
		<div class="fields">
			<h3 id="ack_header"></h3>
			
			<p>
				<label class="small"><spring:message code="bill.section.number" text="Section Number"/></label>
				<input id="sectionNumber" name="sectionNumber" class="sText">
			</p>
			
			<p style="margin-top: 15px;">
				<label class="small"><spring:message code="bill.section.order" text="Section Order"/></label>
				<input id="sectionOrder" name="sectionOrder" class="sInteger">
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="bill.section.text" text="Section Text"/></label>
				<%-- <c:choose>
					<c:when test="${not empty selectedText}">
						<textarea class="wysiwyg" id="sectionText" name="sectionText" readonly="readonly">${selectedText}</textarea>
					</c:when>
					<c:otherwise>
						<textarea class="wysiwyg" id="sectionText" name="sectionText">${selectedText}</textarea>
					</c:otherwise>
				</c:choose> --%>		
				<textarea class="wysiwyg" id="sectionText" name="sectionText"></textarea>		
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
		<input type="hidden" id="invalidSectionNumberMsg" value="<spring:message code='bill.section.invalidSectionNumberMsg' text='You have entered invalid section number'/>">
		<input type="hidden" id="sectionAlreadyExistsMsg" value="<spring:message code='bill.section.sectionAlreadyExistsMsg' text='Section already exists.'/>">
		<input type="hidden" id="sectionExistsAtGivenOrderPrompt" value="<spring:message code='bill.section.sectionExistsAtGivenOrderPrompt' text='There is already a section at given order.. Do you want to insert this section?'/>">
		<input type="hidden" id="emptySectionNumberMsg" value="<spring:message code='bill.section.emptySectionNumberMsg' text='Please enter section number'/>">
		<input type="hidden" id="emptySectionOrderMsg" value="<spring:message code='bill.section.emptySectionOrderMsg' text='Please enter section order'/>">
		<input type="hidden" id="sectionSubmissionPrompt" value="<spring:message code='bill.section.sectionSubmissionPrompt' text='Do you want to add this section?'/>">
		<input type="hidden" id="successInUpdationMsg" value="<spring:message code='bill.section.successInUpdationMsg' text='Section successfully updated'/>">
		<input type="hidden" id="errorInUpdationMsg" value="<spring:message code='bill.section.errorInUpdationMsg' text='Error in adding section.. Please contact support'/>">
	</body>
</html>