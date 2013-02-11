<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="session.list"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
		$('.council').hide();
		if($('#houseType').val()=="upperhouse"){
			$('.council').show();
			$('.assembly').hide();
		}
		else{
			$('.council').hide();
			$('.assembly').show();
		}
	$('#tentativeStartDate').change(function(){
		$('#startDate').val($('#tentativeStartDate').val());
	});
	
	$('#tentativeEndDate').change(function(){
		$('#endDate').val($('#tentativeEndDate').val());
	});
	$('#houseType').change(function(){
		populateHouse($('#houseType').val());
		if($('#houseType').val()=="upperhouse"){
			$('.council').show();
			$('.assembly').hide();
		}
		else{
			$('.council').hide();
			$('.assembly').show();
		}
	});		
	});
	function populateHouse(houseType) {
		$.get('ref/' + houseType + '/houses', function(data) {
			$('#house option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].displayName
						+ "</option>";
			}
			$('#house').html(options);
			
		});
	}
	
	</script>
</head>
<body>
	<div class="commandbar">
	</div>
<div class="fields clearfix vidhanmandalImg">
		<form:form action="session" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
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
			<label class="small"><spring:message
								code="session.houseType" text="House Type" /></label>
			 <select class="sSelect" name="houseType" id="houseType">
					<c:if test="${!(empty houseTypes)}">
							<c:forEach items="${houseTypes}" var="i">
								<c:choose>
								<c:when test="${houseTypeSelected==i.id}">
								<option value="${i.type}" selected="selected">
									<c:out value="${i.name}"></c:out>
								</option>
								</c:when>
								<c:otherwise>
								<option value="${i.type}" >
									<c:out value="${i.name}"></c:out>
								</option>
								</c:otherwise>
								</c:choose>								
							</c:forEach>							
					</c:if>								
			</select>
			</p>
			
			<p>
				<label class="small"><spring:message code="session.house" text="House" /></label>
					<form:select cssClass="sSelect" path="house" items="${houses}"
							itemValue="id" itemLabel="displayName" id="house" size="1">
					</form:select>
				<form:errors path="house" cssClass="validationError" />			
			</p>
			
			<p>
				<label class="small"><spring:message code="session.year" text="Session Year"/>&nbsp;*</label>
				<form:select path="year" items="${years}" cssClass="sSelect"></form:select>
				<form:errors path="year" cssClass="validationError" />			
			</p>
			
			<p>
				<label class="small"><spring:message code="session.type" text="Session Type" />&nbsp;*</label>
				<form:select cssClass="sSelect" path="type"
					items="${sessionType}" itemValue="id" itemLabel="sessionType">
				</form:select>

			</p>
			<p>
				<label class="small"><spring:message code="session.place" text="Session Place" />&nbsp;*</label>
				<form:select cssClass="sSelect" path="place"
					items="${place}" itemValue="id" itemLabel="place">
				</form:select>

			</p>
			<p>
				<label class="small"><spring:message
						code="session.number" text="Session Number" />&nbsp;*</label>
				<form:input path="number" cssClass="integer sText"></form:input>
			</p>
			<p>
				<label class="small"><spring:message
						code="session.tentativeStartDate" text="Tentative Start Date" />&nbsp;*</label>
				<form:input id="tentativeStartDate" cssClass="datemask sText" path="tentativeStartDate" />
				<form:errors path="tentativeStartDate" cssClass="validationError" />

			</p>

			<p>
				<label class="small"><spring:message code="session.tentativeEndDate"
						text="Tentative End Date" /></label>
				<form:input id="tentativeEndDate" cssClass="datemask sText" path="tentativeEndDate" />
				<form:errors path="tentativeEndDate" cssClass="validationError" />

			</p>
			<p>
				<label class="small"><spring:message
						code="session.startDate" text="Start Date" />&nbsp;*</label>
				<form:input id="startDate" cssClass="datemask sText" path="startDate" />
				<form:errors path="startDate" cssClass="validationError" />

			</p>

			<p>
				<label class="small"><spring:message code="session.endDate"
						text="End Date" /></label>
				<form:input id="endDate" cssClass="datemask sText" path="endDate" />
				<form:errors path="endDate" cssClass="validationError" />

			</p>
			
			
		<p>
		<label class="small"><spring:message code="session.durationInDays" text="Session Duration In days"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInDays"/>
				<form:errors path="durationInDays" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="session.durationInHrs" text="Session Duration In Hours"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInHrs"/>
				<form:errors path="durationInHrs" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="session.durationInMins" text="Session Duration In Mins"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInMins"/>
				<form:errors path="durationInMins" cssClass="validationError" />	
			
		</p>
		<p>
				<label class="small"><spring:message code="session.deviceTypesEnabled"
						text="Device Types Enabled" /></label>
				<select class="sSelectMultiple" name="deviceTypesEnabled" id="deviceTypesEnabled" multiple="multiple">
					<c:forEach items="${deviceTypes}" var="i">
						<option value="${i.type}" ><c:out value="${i.name}"></c:out></option>
					</c:forEach>											
				</select>
				<form:errors path="deviceTypesEnabled" cssClass="validationError" />

		</p>	
		<p>
				<label class="labelcentered"><spring:message code="session.remarks"
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
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>"class="butDef"> 
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="version" />
			<form:hidden path="locale" />
		</form:form>
	</div>
</body>
</html>