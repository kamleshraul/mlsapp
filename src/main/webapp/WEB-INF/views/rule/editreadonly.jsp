<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="rule" text="Rules"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				if($('#readonly_selectedYear').val()=="" || $('#readonly_selectedYear').val()==undefined){		
					$("#readonly_editionYear").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
				}
			});		
		</script>
	</head>
	<body>	
		<div class="fields clearfix vidhanmandalImg">
			<form:form ruleion="rule" method="POST"  modelAttribute="domain">
				<%@ include file="/common/info.jsp" %>
				<h2><spring:message code="generic.edit.heading" text="Details"/>
					[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
				</h2>	
				<form:errors path="version" cssClass="validationError"/>
				
				<p>
					<label class="small"><spring:message code="rule.houseType" text="House Type"/></label>
					<form:select id="readonly_houseType" class="sSelect" path="houseType">
					<c:forEach var="i" items="${houseTypes}">	
						<c:choose>
							<c:when test="${i.id == selectedHouseType}">
								<option value="${i.id}" selected="selected">${i.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${i.id}">${i.name}</option>
							</c:otherwise>
						</c:choose>						
					</c:forEach>
					</form:select>		
					<form:errors path="houseType" cssClass="validationError"/>				
				</p>
				
				<p> 
					<label class="small"><spring:message code="rule.editionNumber" text="Edition Number"/></label>
					<form:input id="readonly_editionNumber" cssClass="sInteger" path="editionNumber"/>
					<form:errors path="editionNumber" cssClass="validationError"/>	
				</p>
					
				<p> 
					<label class="small"><spring:message code="rule.editionYear" text="Edition Year"/></label>
					<form:select id="readonly_editionYear" path="editionYear">
						<c:forEach var="i" items="${years}">							
							<c:choose>
								<c:when test="${i.number==selectedYear }">
									<option value="${i.number}" selected="selected"><c:out value="${i.name}"></c:out></option>				
								</c:when>
								<c:otherwise>
									<option value="${i.number}" ><c:out value="${i.name}"></c:out></option>			
								</c:otherwise>
							</c:choose>
						</c:forEach> 
					</form:select>
					<form:errors path="editionYear" cssClass="validationError"/>	
				</p>	
				 
				<p> 
					<label class="small"><spring:message code="rule.number" text="Number"/></label>
					<form:input id="readonly_number" cssClass="sText" path="number"/>
					<form:errors path="number" cssClass="validationError"/>	
				</p>
				
				<div>
					<fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code="rule.titles" text="Titles of Rule" /></label></legend>
						<div id="titles_div">
							<c:forEach var="i" items="${titles}">
								<p>
									<label class="centerlabel">${i.language.name} <spring:message code="rule.title" text="Title"/></label>
									<textarea rows="2" cols="50" id="readonly_title_text_${i.language.type}" name="title_text_${i.language.type}">${i.text}</textarea>
									<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
									<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
								</p>
							</c:forEach>
						</div>
					</fieldset>
				</div>
				
				<div>
					<fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code="rule.contentDrafts" text="Content Drafts of Rule" /></label></legend>
						<div id="contentDrafts_div">
							<c:forEach var="i" items="${contentDrafts}">
								<p>
									<label class="wysiwyglabel">${i.language.name} <spring:message code="rule.contentDraft" text="Content Draft"/></label>
									<textarea class="wysiwyg" id="readonly_contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}">${i.text}</textarea>
									<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
									<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
								</p>
							</c:forEach>
						</div>
					</fieldset>
				</div>
				
				<p>
					<label class="small"><spring:message code="rule.fileMarathi" text="Rule File In Marathi"/></label>
					<c:choose>		
						<c:when test="${empty domain.fileMarathi}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="fileMarathi" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_open.jsp">
								<jsp:param name="fileid" value="fileMarathi" />
								<jsp:param name="filetag" value="${domain.fileMarathi}" />
								<jsp:param name="isRemovable" value="${isFileRemovable}" />
							</jsp:include>
						</c:otherwise>
					</c:choose>		
					<form:errors path="fileMarathi" cssClass="validationError" />
				</p>
				
				<p>
					<label class="small"><spring:message code="rule.fileEnglish" text="Rule File In English"/></label>
					<c:choose>		
						<c:when test="${empty domain.fileEnglish}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="fileEnglish" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_open.jsp">
								<jsp:param name="fileid" value="fileEnglish" />
								<jsp:param name="filetag" value="${domain.fileEnglish}" />
								<jsp:param name="isRemovable" value="${isFileRemovable}" />
							</jsp:include>
						</c:otherwise>
					</c:choose>		
					<form:errors path="fileEnglish" cssClass="validationError" />
				</p>				
			</form:form>
			<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>">
			<input type="hidden" id="readonly_selectedYear" value="${selectedYear}">			
		</div>	
	</body>
</html>