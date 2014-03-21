<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title><spring:message code="bill.addSection" text="Add Section" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script type="text/javascript">
			$(document).ready(function(){	
				initControls();
				
				if($('#isTextSelected').val()=='true') {
					$('#sectionUpdationButtonDiv').show();
				} else {
					$('#sectionUpdationButtonDiv').hide();
				}
				
				$('#sectionNumber').change(function() {
					$("#ack_header").empty();
					
					/* if($('#isTextSelected').val()=='false') {
						if($('#sectionNumber').val()=="") {
							$('#sectionText').wysiwyg('setContent', "");
							return false;
						}
						//check if section exists & show its text if it exists
						$.get('ref/bill/checkSection?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
								+'&sectionNumber='+$("#sectionNumber").val(), function(data) {
							if(data=='invalidSectionNumber') {
								alert($('#invalidSectionNumberMsg').val());
							} else if(data=='sectionnotfound') {
								alert($('#sectionNotFoundMsg').val());
							} else {								
								$('#sectionText').wysiwyg('setContent', data);								
							}							
						}).fail(function() {
							alert("Error occured.. Please contact support.");
						});
					} */
					if($('#sectionText').val()=="" || $('#sectionText').val()=="<p></p>") {
						if($('#sectionNumber').val()=="") {	
							return false;
						} else {
							//check if section exists & show its text if it exists
							$.get('ref/bill/checkSection?billId='+$("#id").val()+'&language='+$("#sectionLanguage").val()
									+'&sectionNumber='+$("#sectionNumber").val(), function(data) {
								if(data=='invalidSectionNumber') {
									alert($('#invalidSectionNumberMsg').val());
								} else if(data=='sectionnotfound') {
									alert($('#sectionNotFoundMsg').val());
								} else {								
									$('#sectionText').wysiwyg('setContent', data);								
								}							
							}).fail(function() {
								alert("Error occured.. Please contact support.");
							});
						}						
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
							if(data=="error") {
								$("#ack_header").html($("#errorInUpdationMsg").val()).css({'color':'red', 'display':'block'});
							} else {
								$("#ack_header").html($("#successInUpdationMsg").val()).css({'color':'green', 'display':'block'});
								if(data=="added") {
									var updatedVersion = parseInt($("#version").val(), 10);
									$("#version").val(updatedVersion + 1);
								}								
							}									
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
			<h3 id="ack_header"></h3>
			
			<p>
				<label class="small"><spring:message code="bill.section.key" text="Section Key"/></label>
				<input id="sectionNumber" name="sectionNumber" class="sText">
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
			<!-- <div class="fields" id="sectionUpdationButtonDiv"> -->
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
		<input type="hidden" id="sectionNotFoundMsg" value="<spring:message code='bill.section.sectionNotFoundMsg' text='Section with given section number not found'/>">
		<input type="hidden" id="emptySectionNumberMsg" value="<spring:message code='bill.section.emptySectionNumberMsg' text='Please enter section number'/>">
		<input type="hidden" id="successInUpdationMsg" value="<spring:message code='bill.section.successInUpdationMsg' text='Section successfully updated'/>">
		<input type="hidden" id="errorInUpdationMsg" value="<spring:message code='bill.section.errorInUpdationMsg' text='Error in section updation.. Please contact support'/>">
	</body>
</html>