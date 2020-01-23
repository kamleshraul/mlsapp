<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="group.title" text="Title"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		$('document').ready(function(){	
			initControls();
			
			var recordId = ${domain.id};
			$('#key').val(recordId);
			
			$("#ministries").multiSelect();
			$("#subdepartments").multiSelect();
			
			var multiSelectMaxHeight = $('.multiSelectSpan').css('max-height');
			
			$("select[multiple='multiple']").css("width","300px");
			
			$('.expansionMultiSelect').click(function() {
				var selectedId = this.id;
				if($(this).text()=='Expand') {
					if(selectedId=='expandMinistry') {
						$("#span_ministries").attr('style', 'max-height: initial !important;');
					} else if(selectedId=='expandSubDepartment') {
						$("#span_subdepartments").attr('style', 'max-height: initial !important;');
					}				
					$(this).text('Collapse');
				} else {
					if(selectedId=='expandMinistry') {
						$("#span_ministries").attr('style', 'max-height: ' + multiSelectMaxHeight + ' !important;');
					} else if(selectedId=='expandSubDepartment') {
						$("#span_subdepartments").attr('style', 'max-height: ' + multiSelectMaxHeight + ' !important;');
					}
					
					$(this).text('Expand');
				}
			});
			
			$("#ministries").change(function(){
				var ministry = $(this).val();
				if(ministry==null){
					ministry="";
				}
				$.get("ref/getSubDeparmentsByMinistries?ministries=" + ministry
						+ "&session=" + $('#session').val(),function(data){
					
					if(data.length>0){
						var text = "";
						for(var i=0;i<data.length;i++){
							var flag = false;
							$("#subdepartments option").each(function(){
								if($(this).attr("selected")=="selected" && $(this).val()==data[i].id){
									flag=true;
								}
							 });
							if(flag){
								text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
							}else{
								text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
							}
						}
						$("#subdepartments").empty();
						$("#subdepartments").html(text);
						$("#subdepartments").multiSelect();
					}
				});
			});
			
			$('#number').change(function(){				
				if($('#number').val() != "") {
					/* $.get('ref/group/ministries?groupNumber='+$('#number').val()+'&houseType='+$('#houseType').val()+'&year='+$("#year").val()+'&sessionType='+$("#sessionType").val(), 
						function(data) {
							$('#ministries option').empty();
							var options = "";
							for ( var i = 0; i < data.length; i++) {
								options += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
							}
							$('#ministries').html(options);							
							$('#ministries').removeAttr('disabled');
							$("#ministries").multiSelect();							
						}); */					
				}								
			});			
			
			$('#submit').click(function(){
				if($('#number').val() == "") {
					$.prompt($('#pleaseSelectGroupNumber').val());
					return false;
				}				
				
				if($('#ministries').val() == null) {
					$.prompt($('#pleaseSelectMinistries').val());
					return false;
				}
			});	
		});			
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix">
<form:form  action="group" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
	
	<p>
		<label class="small"><spring:message code="group.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p>
		<label class="small"><spring:message code="group.year" text="Year"/>*</label>
		<input id="formattedYear" name="formattedYear" value="${formattedYear}" class="sText" readonly="readonly">
		<input id="year" name="year" value="${year}" type="hidden">
	</p>
	
	<p>
		<label class="small"><spring:message code="group.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">			
		<form:errors path="sessionType" cssClass="validationError"/>	
	</p>
		 
	<p>
		<label class="small"><spring:message code="group.number" text="Group" /></label>		
		<form:input path="number" id="number" cssClass="sText" readonly="true"/>			
		<%-- <form:select path="number" id="number" cssClass="sSelect">
			<c:if test="${empty selectedNumber}">
				<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>
			</c:if>
			<c:forEach var="i" items="${groupNumbers}">					
				<c:choose>
					<c:when test="${i.value==selectedNumber }">
						<option value="${i.value}" selected="selected"><c:out value="${i.name}"></c:out></option>				
					</c:when>
					<c:otherwise>
						<option value="${i.value}" ><c:out value="${i.name}"></c:out></option>			
					</c:otherwise>
				</c:choose>
			</c:forEach>			
		</form:select> --%>
		<form:errors path="number" cssClass="validationError" />
	</p>
	<p>
		<label style="vertical-align: top;"><spring:message code="group.ministries" text="Ministries" /></label>			
		<form:select path="ministries" id="ministries" items="${ministries}" itemValue="id" itemLabel="name" multiple="multiple" size="5" disabled="false"/>
		<form:errors path="ministries" cssClass="validationError" />	
		<a id="expandMinistry" class="expansionMultiSelect" href="javascript:void(0);" style="float: right;">Expand</a>
	</p>	
			
	<p>
		<label style="vertical-align: top; width: 142px"><spring:message code="group.subdepartment" text="SubDepartments" /></label>			
		<form:select path="subdepartments" items="${subdepartments}" itemValue="id" itemLabel="name"  multiple="multiple" size="5"/>
		<form:errors path="subdepartments" cssClass="validationError" />
		<a id="expandSubDepartment" class="expansionMultiSelect" href="javascript:void(0);" style="float: right;">Expand</a>
	</p>		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<input type="hidden" id="key" name="key">
	<input type="hidden" id="noSessionTypeMessage" value='<spring:message code="group.noSessionType" text="Session Does Not exist "></spring:message>'>	
	<form:hidden path="session" value='${session}'/>
	<form:hidden path="version" />
	<form:hidden  path="id"/>
	<form:hidden path="locale"/>	
</form:form>

<input id="pleaseSelectGroupNumber" value="<spring:message code='NotNull.groupNumber' text='Please select group number'/>" type="hidden">
<input id="pleaseSelectMinistries" value="<spring:message code='NotNull.ministries' text='Please select ministries in the group'/>" type="hidden">
<input id="domainGroupNumber" value="${selectedNumber}" type="hidden">
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>