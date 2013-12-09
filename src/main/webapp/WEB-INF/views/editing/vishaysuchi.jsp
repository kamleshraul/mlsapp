<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
		function produceVishaysuchiReport(element, repType){
			var params='houseType=' + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val()
			+ '&userGroup=' + $("#userGroup").val()
			+ '&userGroupType=' + $("#userGroupType").val()
			+ '&outputFormat=' + repType;
			
			var reportURL = 'editing/vishaysuchireport?' + params;
			$(element).attr('href', reportURL);
		}
		
		$(document).ready(function() {	
			$("#vishaysuchi_pdf").click(function(){
				produceVishaysuchiReport(this, "PDF");
			});
			$("#vishaysuchi_word").click(function(){
				produceVishaysuchiReport(this, "WORD");
			});
		});
	</script>
	<style type="text/css">
		
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<div>
	<a id="vishaysuchi_pdf" class="exportLink" href="javascript:void(0);" style="text-decoration: none; margin-left: 40px;" target="_blank">
		<img class="imgN" src="./resources/images/pdf_icon.png" alt="Export to PDF" width="24px" height="32px" title="<spring:message code='editing.vishaysuchi.pdf' text='Vishaysuchi In PDF' />">
	</a>
	&nbsp;&nbsp;
	<a id="vishaysuchi_word" class="exportLink" href="javascript:void(0);" style="text-decoration: none;" target="_blank">
		<img class="imgN" src="./resources/images/word_icon.png" alt="Export to WORD" width="33px" height="32px" title="<spring:message code='editing.vishaysuchi.doc' text='Vishaysuchi In Word' />">
	</a>
</div>

<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h5 style="color: #FF0000;">${error}</h5>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="editing.vishaysuchi.notCreated" text="Vishaysuchi is not ready."/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="editing.vishaysuchi.notCreated" text="Vishaysuchi is not ready."/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h2 style="text-decoration: underline; font-family: 'Times New Roman';"><spring:message code="editing.vishaysuchi" text="Vishaysuchi" /></h2>
	</div>
	
		<c:forEach items="${report}" var="r">
			<c:choose>
				<c:when test="${r.type=='member'}">
					<c:if test="${r.vishaysuchiDevices!=null and not(empty r.vishaysuchiDevices)}">
						<table style="margin-left: 25px; font-size: 14px; width: 750px;">
							<c:if test="${(r.catchWordIndex!=null and not(empty r.catchWordIndex)) and
							((r.headings!=null and not(empty r.headings)) or (r.vishaysuchiDevices!=null and not(empty r.vishaysuchiDevices)))}">
								<thead>
									<tr>
										<td style="text-align: center;">
											<br />
											<b>"${r.catchWordIndex}"</b>
											<span style="float: right;">
												<spring:message code="editing.vishaysuchi.poageno" text="Page No."></spring:message>
											</span>
										</td>
									</tr>
								</thead>
							</c:if>
							<tr>
								<td>
									<br />
									<b>${r.value}</b><br />
									<c:if test="${r.vishaysuchiDevices!=null and not(empty r.vishaysuchiDevices)}">
										<c:set var="catchWordOfDevice" value="-" />
										<c:set var="deviceOfDevice" value="-" /> 
										<c:forEach items="${r.vishaysuchiDevices}" var="vd">
											<c:choose>
												<c:when test="${deviceOfDevice != vd.deviceType}">
													<span style="margin-left: 14px;">${vd.deviceName}-----</span><br />
													<c:choose>
														<c:when test="${catchWordOfDevice != vd.catchwordHeading.catchWord}">
															<span style="margin-left: 28px;">${vd.catchwordHeading.catchWord}-----</span><br />
															<span style="margin-left: 42px;">${vd.catchwordHeading.heading}</span><br />
														</c:when>
														<c:otherwise>
															<span style="margin-left: 42px;">${vd.catchwordHeading.heading}</span><br />
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${catchWordOfDevice != vd.catchwordHeading.catchWord}">
															<span style="margin-left: 28px;">${vd.catchwordHeading.catchWord}-----</span><br />
															<span style="margin-left: 42px;">${vd.catchwordHeading.heading}</span><br />
														</c:when>
														<c:otherwise>
															<span style="margin-left: 42px;">${vd.catchwordHeading.heading}</span><br />
														</c:otherwise>
													</c:choose>
												</c:otherwise>										
											</c:choose>
											<c:set var="catchWordOfDevice" value="${vd.catchwordHeading.catchWord}" />
											<c:set var="deviceOfDevice" value="${vd.deviceType}" /> 
										</c:forEach>								
									</c:if>
								</td>
							</tr>
						</table>
					</c:if>
				</c:when>
				<c:when test="${r.type=='catchWord'}">
					<c:if test="${r.headings != null and not(empty r.headings)}">
						<table style="margin-left: 25px; font-size: 14px; width: 750px;">
							<c:if test="${(r.catchWordIndex!=null and not(empty r.catchWordIndex)) and
							((r.headings!=null and not(empty r.headings)) or (r.vishaysuchiDevices!=null and not(empty r.vishaysuchiDevices)))}">
								<thead>
									<tr>
										<td style="text-align: center;">
											<br />
											<b>"${r.catchWordIndex}"</b>
											<span style="float: right;">
												<spring:message code="editing.vishaysuchi.poageno" text="Page No."></spring:message>
											</span>
										</td>
									</tr>
								</thead>
							</c:if>
							<tr>
								<td>
									<br />
									<b>${r.value}</b>-----<br />							
									<c:forEach items="${r.headings}" var="h">
										<span style="margin-left: 14px;">${h.heading}</span><br />
									</c:forEach>
																	
								</td>
							</tr>
						</table>
					</c:if>
				</c:when>
			</c:choose>			
		</c:forEach>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>