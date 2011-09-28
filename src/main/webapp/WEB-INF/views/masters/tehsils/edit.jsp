<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form:form cssClass="wufoo" action="tehsils" method="PUT" modelAttribute="tehsil">
	<div class="info">
		<h2><spring:message code="tehsil.edit.heading"/>[Id:${tehsil.id}]</h2>
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul>	
		<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error"><spring:message code="generic.error.label"/></p>
			</c:if>
			<c:if test="${isvalid eq true}">
				<p class="field_error"><spring:message code="generic.update_success.label"/></p>
			</c:if>
		</li>	
		<li>
		<label class="desc"><spring:message code="generic.id"/></label>
		<div>
			<form:input cssClass="field text small" path="id" readonly="true" /> 
		</div>
	    </li>
	    <li>
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en"><spring:message code="generic.lang.english"/></form:option>
					<form:option value="hi_IN"><spring:message code="generic.lang.hindi"/></form:option>
					<form:option value="mr_IN"><spring:message code="generic.lang.marathi"/></form:option>
				</form:select>
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="tehsil.state"/>&nbsp;*</label>
			<div>
				<select name="state" id="states">
				<c:forEach items="${states}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:forEach>
				</select>	
	        </div>
		</li>
		<li>
		<label class="desc"><spring:message code="tehsil.district"/>&nbsp;*</label>
			<div>
				<form:select path="district" items="${districts}" itemValue="id" itemLabel="name" id="districts">
	            </form:select>			
	        </div>
		</li>		
		<li>
		<label class="desc"><spring:message code="tehsil.name"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="name" size="50"/><form:errors path="name" cssClass="field_error" />	
			</div>
		</li>		
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
<head>
	<title><spring:message code="tehsil.edit.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	if($('#states').val()!=undefined){
		$('#states').change(function(){
			$.ajax({
				url:'ref/'+$('#states').val()+'/districts',
				datatype:'json',
				success:function(data){
				$('#districts option').remove();
				for(var i=0;i<data.length;i++){
					$('#districts').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
				}
			}							
			});
	});	
	}    
	</script>
</head>
</html>