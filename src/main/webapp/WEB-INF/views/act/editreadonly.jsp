<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="act" text="Acts"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				if($('#readonly_selectedYear').val()=="" || $('#selectedYear').val()==undefined){		
					$("#readonly_year").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
				}
			});		
		</script>
	</head>
	<body>	
		<div class="fields clearfix vidhanmandalImg">
			<form:form action="act" method="POST"  modelAttribute="domain">
				<%@ include file="/common/info.jsp" %>
				<h2><spring:message code="generic.edit.heading" text="Details"/>
					[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
				</h2>	
				<form:errors path="version" cssClass="validationError"/>				
				<p> 
					<label class="small"><spring:message code="act.year" text="Year"/></label>
					<form:select id="readonly_year" path="year">
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
					<form:errors path="year" cssClass="validationError"/>	
				</p>	
				 
				<p> 
					<label class="small"><spring:message code="act.number" text="Number"/></label>
					<form:input id="readonly_number" cssClass="sInteger" path="number"/>
					<form:errors path="number" cssClass="validationError"/>	
				</p>
				
				<div>
					<fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code="act.titles" text="Titles of Act" /></label></legend>
						<div id="titles_div">
							<c:forEach var="i" items="${titles}">
								<p>
									<label class="centerlabel">${i.language.name} <spring:message code="act.title" text="Title"/></label>
									<textarea rows="2" cols="50" id="readonly_title_text_${i.language.type}" name="title_text_${i.language.type}">${i.text}</textarea>
									<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
									<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
								</p>
							</c:forEach>
						</div>
					</fieldset>
				</div>
				
				<p>
					<label class="small"><spring:message code="act.fileMarathi" text="Act File In Marathi"/></label>
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
					<label class="small"><spring:message code="act.fileEnglish" text="Act File In English"/></label>
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
				
				<p>
					<label class="small"><spring:message code="act.fileHindi" text="Act File In Hindi"/></label>
					<c:choose>		
						<c:when test="${empty domain.fileHindi}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="fileHindi" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_open.jsp">
								<jsp:param name="fileid" value="fileHindi" />
								<jsp:param name="filetag" value="${domain.fileHindi}" />
								<jsp:param name="isRemovable" value="${isFileRemovable}" />
							</jsp:include>
						</c:otherwise>
					</c:choose>		
					<form:errors path="fileHindi" cssClass="validationError" />
				</p>	
			</form:form>
			<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>">
			<input type="hidden" id="selectedYear" value="${selectedYear}">			
		</div>	
	</body>
</html>