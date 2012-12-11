<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list" text="Edit Session"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('.council').hide();
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
		
		if('${domain.house.type.type}'=="upperhouse"){
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

		$('#deviceTypesEnabled').change(function(){			
			populateDeviceTypesNeedBallot($('#deviceTypesEnabled').val());
		});
	});
	function populateHouse(houseType) {
			$.get('ref/' + houseType + '/house', function(data) {
				$('#house option').empty();
				var options = "";
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].id+"'>" + data[i].displayName
							+ "</option>";
				}
				$('#house').html(options);
				
			});
		}
	function populateDeviceTypesNeedBallot(deviceTypesEnabled) {
		//get selected deviceTypesEnabled for deviceTypesNeedBallot
		$.get('ref/' + deviceTypesEnabled + '/deviceTypesNeedBallot', function(data) {
			var options = "";
			//if atleast one deviceTypesNeedBallot is selected
			if($('#deviceTypesNeedBallot').val()!=null) {								
				$('#deviceTypesNeedBallot option').empty();						
				for ( var i = 0; i < data.length; i++) {
					//flag for whether element was already selected or not
					var selected = false;
					//check whether element was selected already
					for(var j=0; j<$('#deviceTypesNeedBallot').val().length; j++) {		
						//here data[i].id is not id, but type of DeviceType
						if(data[i].id == $('#deviceTypesNeedBallot').val()[j]) {
							//in case element was already selected, make flag true
							selected = true;							
						}					
					}	
					if(selected==true) {
						//add element as selected option
						options += "<option value='"+data[i].id+"' selected='selected'>" + data[i].name
						+ "</option>";
					}
					else {
						//add element as option but should not be selected
						options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";	
					}
				}
			}
			//if no deviceTypesNeedBallot is selected
			else {				
				$('#deviceTypesNeedBallot option').empty();						
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].id+"'>" + data[i].name
					+ "</option>";	
				}
			}
			$('#deviceTypesNeedBallot').html(options);			
		});
	}
</script>
	</head>
<body> 
<div class="commandbar">
</div>
<div class="fields clearfix vidhanmandalImg">
<form:form action="session" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<p>
			<label class="small"><spring:message
								code="session.houseType" text="House Type" /></label>
			 <select class="sSelect" name="houseType" id="houseType">
					<c:if test="${!(empty houseTypes)}">
							<c:forEach items="${houseTypes}" var="i">
							<c:choose>
							<c:when test="${domain.house.type.id==i.id}">
								<option value="${i.type}" selected="selected">
									${i.name}
								</option>
							</c:when>
							<c:otherwise>
							<option value="${i.type}">
									${i.name}
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
				<select id="year" name="year" class="sSelect">
				<c:forEach items="${years}" var="i">
				<c:choose>
				<c:when test="${sessionYearSelected==i}">
				<option selected="selected" value="${i}"><c:out value="${i}"></c:out></option>
				</c:when>
				<c:otherwise>
				<option value="${i}"><c:out value="${i}"></c:out></option>
				</c:otherwise>
				</c:choose>
				</c:forEach>		
				</select>
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
				<label class="small"><spring:message code="session.rotationOrderPublishingDate"
						text="Rotation Order Publishing Date " /></label>
				<form:input cssClass="datemask sText" path="rotationOrderPublishingDate" />
				<form:errors path="rotationOrderPublishingDate" cssClass="validationError" />

		</p>
		
		<p class="assembly">
				<label class="small"><spring:message code="session.questionSubmissionStartDateLH"
						text="Question Submission Start Date for Lower House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionStartDateLH" value="${questionSubmissionStartDateLH}"/>
				<form:errors path="questionSubmissionStartDateLH" cssClass="validationError" />

		</p>
			
		<p class="assembly">
				<label class="small"><spring:message code="session.questionSubmissionEndDateLH"
						text="Question Submission End Time for Lower House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionEndDateLH" value="${questionSubmissionEndDateLH}" />
				<form:errors path="questionSubmissionEndDateLH" cssClass="validationError" />

		</p>	
		<p class="council">
				<label class="small"><spring:message code="session.questionSubmissionFirstBatchStartDateUH"
						text="Question Submission First Batch Date for Upper House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionFirstBatchStartDateUH" value="${questionSubmissionFirstBatchStartDateUH}" />
				<form:errors path="questionSubmissionFirstBatchStartDateUH" cssClass="validationError" />

		</p>
			
		<p class="council">
				<label class="small"><spring:message code="session.questionSubmissionFirstBatchEndDateUH"
						text="Question Submission First Batch End Time for Upper House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionFirstBatchEndDateUH" value="${questionSubmissionFirstBatchEndDateUH}"/>
				<form:errors path="questionSubmissionFirstBatchEndDateUH" cssClass="validationError" />

		</p>	
		<p class="council">
				<label class="small"><spring:message code="session.questionSubmissionSecondBatchStartDateUH"
						text="Question Submission Second Batch Date for Upper House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionSecondBatchStartDateUH" value="${questionSubmissionSecondBatchStartDateUH}"/>
				<form:errors path="questionSubmissionSecondBatchStartDateUH" cssClass="validationError" />

		</p>
		<p class="council">
				<label class="small"><spring:message code="session.questionSubmissionSecondBatchEndDateUH"
						text="Question Submission Second Batch End Time for Upper House" /></label>
				<input Class="datetimemask sText" name="questionSubmissionSecondBatchEndDateUH" value="${questionSubmissionSecondBatchEndDateUH}" />
				<form:errors path="questionSubmissionSecondBatchEndDateUH" cssClass="validationError" />

		</p>
		<p>
				<label class="small"><spring:message code="session.firstBallotDate"
						text="First Ballot Date" /></label>
				<form:input cssClass="datemask sText" path="firstBallotDate" />
				<form:errors path="firstBallotDate" cssClass="validationError" />

		</p>
		<p class="council">
				<label class="small"><spring:message code="session.numberOfQuestionInFirstBatchUH"
						text="Number Of Questions In First Batch for Upper House" /></label>
				<form:input cssClass="sText" path="numberOfQuestionInFirstBatchUH" />
				<form:errors path="numberOfQuestionInFirstBatchUH" cssClass="validationError" />

		</p>
		<p>
				<label class="small"><spring:message code="session.deviceTypesEnabled"
						text="Device Types Enabled" /></label>
				<select class="sSelectMultiple" name="deviceTypesEnabled" id="deviceTypesEnabled" multiple="multiple">
					<c:forEach items="${deviceTypes}" var="i">
						<c:set var="flag" value="false"></c:set>
						<c:forEach items="${deviceTypesEnabled}" var="j">
							<c:if test="${i.type==j.type}">
								<c:set var="flag" value="true"> </c:set>
							</c:if>
						</c:forEach>
						<c:choose>
							<c:when test="${flag==true}">
								<option value="${i.type}" selected="selected" ><c:out value="${i.name}"></c:out></option>
							</c:when>
							<c:otherwise>
								<option value="${i.type}" ><c:out value="${i.name}"></c:out></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>											
				</select>
				<form:errors path="deviceTypesEnabled" cssClass="validationError" />

		</p>	
		<p>
				<label class="small"><spring:message code="session.deviceTypesNeedBallot"
						text="Device Types Need Ballot" /></label>
				<select class="mts" name="deviceTypesNeedBallot" id="deviceTypesNeedBallot" multiple="multiple">
					<c:forEach items="${deviceTypesEnabled}" var="i">
						<c:set var="flag" value="false"></c:set>
						<c:forEach items="${deviceTypesNeedBallot}" var="j">
							<c:if test="${i.type==j.type}">
								<c:set var="flag" value="true"> </c:set>
							</c:if>
						</c:forEach>
						<c:choose>
							<c:when test="${flag==true}">
								<option value="${i.type}" selected="selected" ><c:out value="${i.name}"></c:out></option>
							</c:when>
							<c:otherwise>
								<option value="${i.type}" ><c:out value="${i.name}"></c:out></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>											
				</select>
			<form:errors path="deviceTypesNeedBallot" cssClass="validationError" />

		</p>
		<p>
				<label class="small"><spring:message code="session.rotationOrderText"
						text="Rotation Order Text " /></label>
				<form:textarea cssClass="wysiwyg" path="rotationOrderText" />
				<form:errors path="rotationOrderText" cssClass="validationError" />

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
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="version" />
			<form:hidden path="locale" />
		</form:form>
	</div>
</body>
</html>
