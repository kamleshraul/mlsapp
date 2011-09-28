<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form:form cssClass="wufoo" action="constituencies" method="POST" modelAttribute="constituency">
	<div class="info">
		<h2><spring:message code="constituency.new.heading"/></h2>
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul>	
		<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error"><spring:message code="generic.error.label"/></p>
			</c:if>
		</li>	
		<li>
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="constituency.state"/>&nbsp;*</label>
			<div>
				<select name="state" id="states">
				<c:forEach items="${states}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:forEach>
				</select>			
	        </div>
		</li>
		<li>
		<div>
		<label class="desc"><spring:message code="constituency.district"/>&nbsp;*</label>
				<form:select path="districts" items="${districts}" itemValue="id" itemLabel="name" size="5" multiple="multiple" id="districts">
	            </form:select><form:errors path="districts" cssClass="field_error" />			
	        </div>
		</li>		
		<li>
		<label class="desc"><spring:message code="constituency.name"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="name" size="50"/><form:errors path="name" cssClass="field_error" />
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="constituency.number"/>&nbsp;</label>
			<div>
				<form:input cssClass="field text medium" path="number"/><form:errors path="number" cssClass="field_error" />
			</div>
		</li>	
		<li>	
		<label class="desc"><spring:message code="constituency.reserved"/>&nbsp;</label>
		<div>
				<form:checkbox cssClass="field text medium" path="reserved" value="true" /><form:errors path="reserved" cssClass="field_error" />
		</div>	
		</li>		
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>
		<form:hidden path="id"/>
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
<head>
	<title><spring:message code="constituency.new.title"/></title>
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