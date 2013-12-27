<script>
	$('document').ready(function(){	
		/**** Date Time Mask ****/
		$('.datemask').focus(function(){		
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});
		
		$('.generateDocketReport').click(function() {			
			generateDocketReport(this.id);		
		});
	});
</script>

<%@ include file="/common/taglibs.jsp" %>
<c:if test="${type eq 'error'}">
	<div class="tpRed">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="update_failed" text="Please correct following errors."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'saved'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="printrequisition.update_success" text="Requisition saved successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'sent'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="printrequisition.send_success" text="Requisition sent successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<fieldset>
	<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.printRequisitionForm" text="Print Requisition Form" /></label></legend>
	<div>
		<c:forEach var="i" items="${printRequisitionParameterVOs}">
			<p>
				<label class="small"><spring:message code="bill.printRequisitionForm.${i.name}"/></label>
				<c:choose>
					<c:when test="${isPrintRequisitionSent=='true'}">
						<textarea rows="2" cols="50"  name="fields[${i.name}]" readonly="readonly">${i.value}</textarea>
					</c:when>
					<c:otherwise>
						<textarea rows="2" cols="50"  name="fields[${i.name}]">${i.value}</textarea>
					</c:otherwise>
				</c:choose>																								
			</p>		
		</c:forEach>
		<p>
			<label class="small"><spring:message code="bill.printRequisitionForm.optionalFieldsForDocketReport" text="Optional Fields for Report"/></label>
			<select class="sSelectMultiple" id="optionalFieldsForDocket" name="optionalFieldsForDocket" multiple="multiple">
				<c:forEach var="i" items="${optionalFieldsForDocket}">
					<c:choose>
						<c:when test="${i.isSelected==true}">
							<option value="${i.name}" selected="selected"><spring:message code="bill.${i.name}" text="${i.name}"/></option>
						</c:when>	
						<c:otherwise>
							<option value="${i.name}"><spring:message code="bill.${i.name}" text="${i.name}"/></option>
						</c:otherwise>
					</c:choose>							
				</c:forEach>
			</select>									
		</p>
		<p>
			<label class="small"><spring:message code="bill.printRequisitionForm.docketReportEnglish" text="English Report"/></label>
			<c:choose>		
				<c:when test="${empty docketReportEnglish}">
					<c:if test="${isPrintRequisitionSent!='true'}">
					<jsp:include page="/common/file_upload.jsp">
						<jsp:param name="fileid" value="docketReportEnglish" />
					</jsp:include>
					<a class="generateDocketReport" id="generateDocketReport_english" href="#"><spring:message code="bill.printRequisitionForm.generateDocketReportEnglish" text="Generate English Report"/></a>
					</c:if>
				</c:when>
				<c:otherwise>		
					<jsp:include page="/common/file_download.jsp">
						<jsp:param name="fileid" value="docketReportEnglish" />
						<jsp:param name="filetag" value="${docketReportEnglish}" />
						<jsp:param name="isRemovable" value="${isDocketReportRemovable}" />
					</jsp:include>
				</c:otherwise>
			</c:choose>								
		</p>
		<p>
			<label class="small"><spring:message code="bill.printRequisitionForm.docketReportMarathi" text="Marathi Report"/></label>
			<c:choose>		
				<c:when test="${empty docketReportMarathi}">
					<c:if test="${isPrintRequisitionSent!='true'}">
					<jsp:include page="/common/file_upload.jsp">
						<jsp:param name="fileid" value="docketReportMarathi" />
					</jsp:include>
					<a class="generateDocketReport" id="generateDocketReport_marathi" href="#"><spring:message code="bill.printRequisitionForm.generateDocketReportMarathi" text="Generate Marathi Report"/></a>
					</c:if>
				</c:when>
				<c:otherwise>		
					<jsp:include page="/common/file_download.jsp">
						<jsp:param name="fileid" value="docketReportMarathi" />
						<jsp:param name="filetag" value="${docketReportMarathi}" />
						<jsp:param name="isRemovable" value="${isDocketReportRemovable}" />
					</jsp:include>
				</c:otherwise>
			</c:choose>								
		</p>
		<p>
			<label class="small"><spring:message code="bill.printRequisitionForm.docketReportHindi" text="Hindi Report"/></label>
			<c:choose>		
				<c:when test="${empty docketReportHindi}">
					<c:if test="${isPrintRequisitionSent!='true'}">
					<jsp:include page="/common/file_upload.jsp">
						<jsp:param name="fileid" value="docketReportHindi" />
					</jsp:include>
					<a class="generateDocketReport" id="generateDocketReport_hindi" href="#"><spring:message code="bill.printRequisitionForm.generateDocketReportHindi" text="Generate Hindi Report"/></a>
					</c:if>
				</c:when>
				<c:otherwise>		
					<jsp:include page="/common/file_download.jsp">
						<jsp:param name="fileid" value="docketReportHindi" />
						<jsp:param name="filetag" value="${docketReportHindi}" />
						<jsp:param name="isRemovable" value="${isDocketReportRemovable}" />
					</jsp:include>
				</c:otherwise>
			</c:choose>								
		</p>
		<c:choose>
			<c:when test="${pressCopiesReceived=='yes'}">
				<h3><spring:message code='bill.pressCopies'/></h3>
				<div>
					<%-- <fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.${pressCopyHeader}' /></label></legend> --%>
						<p>
							<label class="small"><spring:message code='bill.pressCopyEnglish' text="English Press Copy"/></label>
							<c:choose>		
								<c:when test="${not empty pressCopyEnglish}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="pressCopyEnglish" />
										<jsp:param name="filetag" value="${pressCopyEnglish}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.pressCopyMarathi' text="Marathi Press Copy"/></label>
							<c:choose>		
								<c:when test="${not empty pressCopyMarathi}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="pressCopyMarathi" />
										<jsp:param name="filetag" value="${pressCopyMarathi}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.pressCopyHindi' text="Hindi Press Copy"/></label>
							<c:choose>		
								<c:when test="${not empty pressCopyHindi}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="pressCopyHindi" />
										<jsp:param name="filetag" value="${pressCopyHindi}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.endorsementCopyEnglish' text="English Endorsement Copy"/></label>
							<c:choose>		
								<c:when test="${not empty endorsementCopyEnglish}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="endorsementCopyEnglish" />
										<jsp:param name="filetag" value="${endorsementCopyEnglish}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.endorsementCopyMarathi' text="Marathi Endorsement Copy"/></label>
							<c:choose>		
								<c:when test="${not empty endorsementCopyMarathi}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="endorsementCopyMarathi" />
										<jsp:param name="filetag" value="${endorsementCopyMarathi}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.endorsementCopyHindi' text="Hindi Endorsement Copy"/></label>
							<c:choose>		
								<c:when test="${not empty endorsementCopyHindi}">
									<jsp:include page="/common/file_open.jsp">
										<jsp:param name="fileid" value="endorsementCopyHindi" />
										<jsp:param name="filetag" value="${endorsementCopyHindi}" />
										<jsp:param name="isRemovable" value="false" />
									</jsp:include>
								</c:when>										
							</c:choose>								
						</p>
						<p>
							<label class="small"><spring:message code='bill.publishDateMarathi' text="Publish Date for Marathi Copy"/></label>
							<input type="text" class="datemask sText" name="setPublishDateMarathi" value="${publishDateMarathi}"/>
						</p>
						<p>
							<label class="small"><spring:message code='bill.publishDateEnglish' text="Publish Date for English Copy"/></label>
							<input type="text" class="datemask sText" name="setPublishDateEnglish" value="${publishDateEnglish}"/>
						</p>
						<p>
							<label class="small"><spring:message code='bill.publishDateHindi' text="Publish Date for Hindi Copy"/></label>
							<input type="text" class="datemask sText" name="setPublishDateHindi" value="${publishDateHindi}"/>
						</p>
					<!-- </fieldset> -->
				</div>
			</c:when>
			<c:when test="${pressCopiesReceived!='yes' and isPrintRequisitionSent=='true'}">
				<h3><spring:message code='bill.pressCopies.notReceived'/></h3>
			</c:when>
		</c:choose>
		<input type="hidden" name="id" value="${printRequisition.getId()}"/>
		<input type="hidden" name="locale" value="${printRequisition.getLocale()}"/>
		<input type="hidden" name="version" value="${printRequisition.getVersion()}"/>
		<input type="hidden" id="isPrintRequisitionSent" name="isPrintRequisitionSent" value="${isPrintRequisitionSent}"/>
	</div>
</fieldset>