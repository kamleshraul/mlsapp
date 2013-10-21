<script>
	$(document).ready(function() {
		$('#generateLetter').click(function() {
			generateLetter();
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
			<spring:message code="layingletter.layingletter.saving_success" text="Letter Saved Successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'laid'}">
	<div class="tpGreen">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="layingletter.layingletter.laying_success" text="Letter Laid Successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<p>
	<label class="small"><spring:message code="layingletter.layingDate" text="Laying Date"/></label>
	<c:choose>
		<c:when test="${isLetterLaid=='true'}">
			<input id="layingDate" name="setlayingDate" value="${layingDate}" class="datemask sText" readonly="readonly"/>
		</c:when>
		<c:otherwise>
			<input id="layingDate" name="setlayingDate" value="${layingDate}" class="datemask sText"/>
		</c:otherwise>
	</c:choose>			
</p>
<p>
	<label class="small"><spring:message code="layingletter.letter" text="Letter"/></label>
	<c:choose>		
		<c:when test="${empty letter}">
			<c:if test="${isLetterLaid!='true'}">
			<jsp:include page="/common/file_upload.jsp">
				<jsp:param name="fileid" value="letter" />
			</jsp:include>
			<a id="generateLetter" href="#"><spring:message code="layingletter.generateLetter" text="Generate Letter"/></a>
			</c:if>
		</c:when>
		<c:otherwise>		
			<jsp:include page="/common/file_download.jsp">
				<jsp:param name="fileid" value="letter" />
				<jsp:param name="filetag" value="${letter}" />
				<jsp:param name="isRemovable" value="${isLetterRemovable}" />
			</jsp:include>
		</c:otherwise>
	</c:choose>								
</p>
<p>
	<label class="small"><spring:message code="layingletter.status" text="Status"/></label>
	<input id="formattedStatus" name="formattedStatus" value="${status.getName()}" class="sText" readonly="readonly"/>
	<input type="hidden" id="status" name="status" value="${status.getType()}"/>	
</p>
<input type="hidden" name="id" value="${layingLetter.getId()}"/>
<input type="hidden" name="locale" value="${layingLetter.getLocale()}"/>
<input type="hidden" name="version" value="${layingLetter.getVersion()}"/>
<input type="hidden" name="layingFor" value="${layingLetter.getLayingFor()}"/>
<input type="hidden" id="isLetterLaid" name="isLetterLaid" value="${isLetterLaid}"/>
	