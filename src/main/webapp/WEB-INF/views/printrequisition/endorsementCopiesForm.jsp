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
<c:if test="${type eq 'sent'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="printrequisition.sendForEndorsement.send_success" text="Request for Endorsement sent successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'transmitted'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="printrequisition.transmitEndorsementCopies.transmit_success" text="Endorsement Copies transmitted successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<fieldset>
	<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.endorsementCopies" text="Endorsement Copies" /></label></legend>
	<div>
		<c:choose>
			<c:when test="${endorsementCopiesReceived=='yes'}">				
				<div>
					<%-- <fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code='bill.${pressCopyHeader}' /></label></legend> --%>
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
					<!-- </fieldset> -->
				</div>
			</c:when>
			<c:when test="${endorsementCopiesReceived!='yes' and isPrintRequisitionSent=='true'}">
				<h3><spring:message code='bill.endorsementCopies.notReceived' text="Endorsement Copies Not Received Yet From Press"/></h3>
			</c:when>
			<c:when test="${endorsementCopiesReceived!='yes' and isPrintRequisitionSent!='true'}">
				<h3><spring:message code='bill.endorsementCopies.printRequisitionNotSent' text="Print Requisition Not Sent to Press"/></h3>
			</c:when>
		</c:choose>
		<input type="hidden" name="id" value="${printRequisition.getId()}"/>
		<input type="hidden" name="locale" value="${printRequisition.getLocale()}"/>
		<input type="hidden" name="version" value="${printRequisition.getVersion()}"/>
		<input type="hidden" id="isPrintRequisitionSent" name="isPrintRequisitionSent" value="${isPrintRequisitionSent}"/>
		<input type="hidden" id="isAlreadySentForEndorsement" name="isAlreadySentForEndorsement" value="${isAlreadySentForEndorsement}"/>
		<input type="hidden" id="isAlreadyTransmitted" name="isAlreadyTransmitted" value="${isAlreadyTransmitted}"/>
	</div>
</fieldset>