<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><spring:message code="bill" text="Bill Information System"/></title>
		<script type="text/javascript">
		$(document).ready(function(){
			$("#save").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});	
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
				$.post($('form').attr('action')+'?operation=savePressCopy',  
    	            $("form").serialize(),  
    	            function(data){
       					$('.tabContent').html(data);
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');	
    					$.unblockUI();	   				 	   				
   	            });
			});
			$("#submit").click(function(e){
				if($('#pressCopyEnglish').val()=='' && $('#pressCopyMarathi').val()=='' && $('#pressCopyHindi').val()=='') {
					$.prompt("Please upload scanned press copy.");				
					return false;
				}
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});	
				$.prompt($('#sendPressCopyMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$('#endflag').val("end");
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?operation=sendPressCopy',  
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
		});
		</script>
	</head>
	<body>
		<div class="fields clearfix watermark">
			<div id="pressDiv">
				<form:form action="workflow/bill" method="PUT" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceTypeForBill} ${formattedNumber}</h2>
					<p> 
						<label class="small"><spring:message code="printrequisition.requisitionFor" text="Requisition For"/></label>
						<input type="text" class="sText" id="formattedRequisitionFor" name="formattedRequisitionFor" value="<spring:message code="printrequisition.${requisitionFor}"/>"/>								
						<input type="hidden" name="requisitionFor" value="${requisitionFor}"/>
					</p>
					<p> 
						<label class="small"><spring:message code="printrequisition.status" text="Status"/></label>
						<input type="text" class="sText" id="formattedStatus" name="formattedStatus" value="${status.getName()}"/>
						<input type="hidden" name="status" value="${status.getType()}"/>
					</p>
					<c:if test="${not empty houseRound}">
					<p> 
						<label class="small"><spring:message code="printrequisition.houseRound" text="House Round"/></label>
						<input type="text" class="sText" id="formattedHouseRound" name="formattedHouseRound" value="${formattedHouseRound}"/>								
					</p>
					</c:if>		
					<input type="hidden" name="houseRound" value="${houseRound}"/>			
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.printRequisitionForm'/></label></legend>
							<div>
								<c:forEach var="i" items="${printRequisitionParameterVOs}">
									<p> 
										<label class="small"><spring:message code="bill.printRequisitionForm.${i.name}"/></label>
										<textarea style="color: black;" rows="2" cols="50" name="${requisitionFor}#${i.name}" readonly="readonly">${i.value}</textarea>												
									</p>								
								</c:forEach>
								<p>
									<label class="small"><spring:message code='bill.printRequisitionForm.docketReportEnglish' /></label>
									<c:choose>		
										<c:when test="${not empty docketReportEnglish}">
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="docketReportEnglish" />
												<jsp:param name="filetag" value="${docketReportEnglish}" />
												<jsp:param name="isRemovable" value="${isFileRemovable}" />
											</jsp:include>
										</c:when>										
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code='bill.printRequisitionForm.docketReportMarathi' /></label>
									<c:choose>		
										<c:when test="${not empty docketReportMarathi}">
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="docketReportMarathi" />
												<jsp:param name="filetag" value="${docketReportMarathi}" />
												<jsp:param name="isRemovable" value="${isFileRemovable}" />
											</jsp:include>
										</c:when>										
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code='bill.printRequisitionForm.docketReportHindi' /></label>
									<c:choose>		
										<c:when test="${not empty docketReportHindi}">
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="docketReportHindi" />
												<jsp:param name="filetag" value="${docketReportHindi}" />
												<jsp:param name="isRemovable" value="${isFileRemovable}" />
											</jsp:include>
										</c:when>										
									</c:choose>								
								</p>								
							</div>
						</fieldset>
					</div>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.pressCopies' /></label></legend>
							<div>								
								<p>
									<label class="small"><spring:message code="bill.pressCopyEnglish" text="English Press Copy"/></label>
									<c:choose>		
										<c:when test="${empty pressCopyEnglish and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="pressCopyEnglish" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="pressCopyEnglish" />
												<jsp:param name="filetag" value="${pressCopyEnglish}" />
												<jsp:param name="isRemovable" value="${isPressCopyRemovable}" />												
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code="bill.pressCopyMarathi" text="Marathi Press Copy"/></label>
									<c:choose>		
										<c:when test="${empty pressCopyMarathi and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="pressCopyMarathi" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="pressCopyMarathi" />
												<jsp:param name="filetag" value="${pressCopyMarathi}" />
												<jsp:param name="isRemovable" value="${isPressCopyRemovable}" />
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code="bill.pressCopyHindi" text="Hindi Press Copy"/></label>
									<c:choose>		
										<c:when test="${empty pressCopyHindi and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="pressCopyHindi" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="pressCopyHindi" />
												<jsp:param name="filetag" value="${pressCopyHindi}" />
												<jsp:param name="isRemovable" value="${isPressCopyRemovable}" />
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code="bill.endorsementCopyEnglish" text="English Endorsement Copy"/></label>
									<c:choose>		
										<c:when test="${empty endorsementCopyEnglish and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="endorsementCopyEnglish" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="endorsementCopyEnglish" />
												<jsp:param name="filetag" value="${endorsementCopyEnglish}" />
												<jsp:param name="isRemovable" value="${isEndorsementCopyRemovable}" />												
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code="bill.endorsementCopyMarathi" text="Marathi Endorsement Copy"/></label>
									<c:choose>		
										<c:when test="${empty endorsementCopyMarathi and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="endorsementCopyMarathi" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="endorsementCopyMarathi" />
												<jsp:param name="filetag" value="${endorsementCopyMarathi}" />
												<jsp:param name="isRemovable" value="${isEndorsementCopyRemovable}" />
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
								<p>
									<label class="small"><spring:message code="bill.endorsementCopyHindi" text="Hindi Endorsement Copy"/></label>
									<c:choose>		
										<c:when test="${empty endorsementCopyHindi and workflowstatus=='PENDING'}">
											<jsp:include page="/common/file_upload.jsp">
												<jsp:param name="fileid" value="endorsementCopyHindi" />
											</jsp:include>
										</c:when>
										<c:otherwise>		
											<jsp:include page="/common/file_download.jsp">
												<jsp:param name="fileid" value="endorsementCopyHindi" />
												<jsp:param name="filetag" value="${endorsementCopyHindi}" />
												<jsp:param name="isRemovable" value="${isEndorsementCopyRemovable}" />
											</jsp:include>
										</c:otherwise>
									</c:choose>								
								</p>
							</div>
						</fieldset>
					</div>
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<c:choose>
								<c:when test="${workflowstatus!='COMPLETED'}">
									<input id="save" type="button" value="<spring:message code='generic.save' text='Save'/>" class="butDef">	
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">										
								</c:when>
								<c:when test="${workflowstatus=='COMPLETED'}">
									<input id="save" type="button" value="<spring:message code='generic.save' text='Save'/>" class="butDef" disabled="disabled">	
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
								</c:when>								
							</c:choose>
						</p>
					</div>			
					<form:hidden path="id"/>
					<form:hidden path="locale"/>		
					<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
					<input id="endflag" name="endflag" value="${endflag}" type="hidden">					
				</form:form>
				<input id="sendPressCopyMessage" value='<spring:message code="bill.sendPressCopyMessage" text="Do you want to send press copies for the bill?"/>' type="hidden">				
			</div>
		</div>
	</body>
</html>