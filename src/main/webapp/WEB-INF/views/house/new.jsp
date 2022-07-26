<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="house" text="Add House" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
		if($('#HouseType').val()=='lowerhouse'){
			$('.Common').show();
			$('.Assembly').show();
		}
		else if($('#HouseType').val()=='upperhouse')
		{
			$('.Common').show();
			$('.Assembly').hide();			
		}
		else
			{
			$('.Assembly').hide();
			$('.Common').hide();
			}
	});
	$('select').change((function(){
		if($('#HouseType').val()=='lowerhouse'||$('#HouseType').val()=='defaulthouse'){
			$('.Common').show();
			$('.Assembly').show();
		}
		else
		{
			$('.Common').show();
			$('.Assembly').hide();			
		}
	}));
	
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
		<form:form action="house" method="POST" modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<form:errors path="version" cssClass="validationError" />
		<p> 
			<label class="small"><spring:message code="house.type" text="House Type"/></label>
			<select id="HouseType" name="houseType" class="sSelect" >
			<c:forEach items="${assemblycounciltype}" var="htype">
			<option value="${htype.type}">${htype.name}</option>
			</c:forEach>
			</select>
			<form:errors path="type" cssClass="validationError"/>	
		</p>
		<p>
		
		</p>
		<p class="Assembly">
				<label class="small"><spring:message
						code="house.name" text="Assembly Name" />&nbsp;*</label>

				<form:input path="name" cssClass="sText"></form:input>
				<label class="small"><spring:message
						code="house.assemblyNameExample" text="e.g. Eleventh" /></label>
				<form:errors path="name" cssClass="validationError"></form:errors>

			</p>
			<p class="Assembly">
				<label class="small"><spring:message
						code="house.display_name" text="Assembly DisplayName" />&nbsp;*</label>

				<form:input path="displayName" cssClass="sText"></form:input>
				<label class="small"><spring:message
						code="house.assemblyDisplayNameExample" text="e.g. Eleventh Assembly 2004-2009" /></label>
				<form:errors path="displayName" cssClass="validationError"></form:errors>

			</p>
			<p class="Assembly">
				<label class="small"><spring:message
						code="house.number" text="Assembly Number" />&nbsp;*</label>
				<form:input path="number" cssClass="sText"></form:input>
				
				<form:errors path="number" cssClass="validationError"></form:errors>
			</p>

			<p class="Common">
				<label class="small" ><spring:message
						code="house.totalMembers" text="Total Members" />&nbsp;*</label>

				<form:input cssClass="integer sText" path="totalMembers" />
				<form:errors path="totalMembers" cssClass="validationError" />

			</p>
			
			<p class="Common">
				<label class="small"><spring:message
						code="house.formationDate" text="Formation Date" /></label>

				<form:input cssClass="datemask sText" path="formationDate" />
				<form:errors path="formationDate" cssClass="validationError" />

			</p>

			<p class="Assembly">
				<label class="small"><spring:message
						code="house.dissolveDate" text="Dissolved On" /></label>

				<form:input cssClass="datemask sText" path="dissolveDate" />
				<form:errors path="dissolveDate" cssClass="validationError" />

			</p>
				<p class="Assembly">
				<label class="small"><spring:message
						code="house.firstDate" text="Start Date" />&nbsp;*</label>

				<form:input cssClass="datemask sText" path="firstDate" />
				<form:errors path="firstDate" cssClass="validationError" />
				</p>
				<p class="Assembly">
				<label class="small"><spring:message code="house.lastDate"
						text="End Date" /></label>

				<form:input cssClass="datemask sText" path="lastDate" />
				<form:errors path="lastDate" cssClass="validationError" />
				</p>
				<p class="Common">
				<label class="small"><spring:message
						code="house.governorAddressDate" text="Governers Address Date" /></label>

				<form:input cssClass="datemask sText" path="governorAddressDate" />
				<form:errors path="governorAddressDate" cssClass="validationError" />
				</p>
				<p class="Common">
				<label class="labelcentered"><spring:message code="house.remarks"
						text="Remarks" /></label>

				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />

			</p>
		
	
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
						
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="version" />
			<form:hidden path="locale" />
		</form:form>	
		</div>
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>