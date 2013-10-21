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
<c:if test="${type eq 'transmitted'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="printrequisition.transmitPressCopies.transmit_success" text="Press Copies transmitted successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<fieldset>
	<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.pressCopies" text="Press Copies" /></label></legend>
	<div>
		<c:choose>
			<c:when test="${pressCopiesReceived=='yes'}">				
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
					<!-- </fieldset> -->
				</div>
			</c:when>
			<c:when test="${pressCopiesReceived!='yes' and isPrintRequisitionSent=='true'}">
				<h3><spring:message code='bill.pressCopies.notReceived' text="Press Copies Not Received Yet From Press"/></h3>
			</c:when>
			<c:when test="${pressCopiesReceived!='yes' and isPrintRequisitionSent!='true'}">
				<h3><spring:message code='bill.pressCopies.printRequisitionNotSent' text="Print Requisition Not Sent to Press"/></h3>
			</c:when>
		</c:choose>
		<input type="hidden" name="id" value="${printRequisition.getId()}"/>
		<input type="hidden" name="locale" value="${printRequisition.getLocale()}"/>
		<input type="hidden" name="version" value="${printRequisition.getVersion()}"/>
		<input type="hidden" id="isPrintRequisitionSent" name="isPrintRequisitionSent" value="${isPrintRequisitionSent}"/>
		<input type="hidden" id="isAlreadyTransmitted" name="isAlreadyTransmitted" value="${isAlreadyTransmitted}"/>
	</div>
</fieldset>