<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="house" text="Edit House"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
	});		
	
</script>
</head>
<body>
	<div class="fields clearfix">
		<form:form action="house" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>

			<form:errors path="version" cssClass="validationError" />
					
		<c:choose>
		<c:when test="${houseType=='lowerhouse'||houseType=='defaulthouse'}">
			<p>
				<label class="small"><spring:message
						code="house.name" text="Assembly Name" />&nbsp;*</label>

				<form:input path="name" cssClass="sText"></form:input>
				<label class="small"><spring:message
						code="house.assemblyNameExample" text="e.g. Eleventh" /></label>
				<form:errors path="name" cssClass="validationError"></form:errors>

			</p>
			<p>
				<label class="small"><spring:message
						code="house.number" text="Assembly Number" />&nbsp;*</label>
				<form:input path="number" cssClass="sText"></form:input>
				
				<form:errors path="number" cssClass="validationError"></form:errors>
			</p>

			<p>
				<label class="small" ><spring:message
						code="house.totalMembers" text="Total Members" />&nbsp;*</label>

				<form:input cssClass="integer sText" path="totalMembers" />
				<form:errors path="totalMembers" cssClass="validationError" />

			</p>
			
			<p>
				<label class="small"><spring:message
						code="house.formationDate" text="Formation Date" /></label>

				<form:input cssClass="datemask sText" path="formationDate" />
				<form:errors path="formationDate" cssClass="validationError" />

			</p>

			<p>
				<label class="small"><spring:message
						code="house.dissolveDate" text="Dissolved On" /></label>

				<form:input cssClass="datemask sText" path="dissolveDate" />
				<form:errors path="dissolveDate" cssClass="validationError" />

			</p>
				<p>
				<label class="small"><spring:message
						code="house.firstDate" text="Start Date" />&nbsp;*</label>

				<form:input cssClass="datemask sText" path="firstDate" />
				<form:errors path="firstDate" cssClass="validationError" />
				</p>
				<p>
				<label class="small"><spring:message code="house.lastDate"
						text="End Date" /></label>
				<form:input cssClass="datemask sText" path="lastDate" />
				<form:errors path="lastDate" cssClass="validationError" />
				</p>
				<p>
				<label class="small"><spring:message
						code="house.governorAddressDate" text="Governers Address Date" /></label>

				<form:input cssClass="datemask sText" path="governorAddressDate" />
				<form:errors path="governorAddressDate" cssClass="validationError" />
				</p>
				<p>
				<label class="labelcentered"><spring:message code="house.remarks"
						text="Remarks" /></label>

				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />

			</p>
		</c:when>
		<c:otherwise>
			
			<p>
				<label class="small"><spring:message
						code="house.formationDate" text="Formation Date" /></label>

				<form:input cssClass="datemask sText" path="formationDate" />
				<form:errors path="formationDate" cssClass="validationError" />
			</p>
							
			<p>
				<label class="small" ><spring:message
						code="house.totalMembers" text="Total Members" />&nbsp;*</label>
				<form:input cssClass="integer sText" path="totalMembers" />
				<form:errors path="totalMembers" cssClass="validationError" />
			</p>			
			<p>
				<label class="small"><spring:message
						code="house.governorAddressDate" text="Chairmans Address Date" /></label>
				<form:input cssClass="datemask sText" path="governorAddressDate" />
				<form:errors path="governorAddressDate" cssClass="validationError" />
				</p>								
			<p>
				<label class="labelcentered"><spring:message code="house.remarks"
						text="Remarks" /></label>
				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />

			</p>
		</c:otherwise>
		</c:choose>		
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="version" />
			<form:hidden path="locale" />
			<form:hidden path="type" value="${housetype}"/>
		</form:form>
	</div>
</body>
</html>
