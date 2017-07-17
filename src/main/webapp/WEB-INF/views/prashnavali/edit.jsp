<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="prashnavali" text="Committee Tour"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	

	

	var questionCount = parseInt($('#questionSize').val());
	var totalQuestionCount = 0;
	totalQuestionCount = totalQuestionCount + questionCount;
	function addPrashnavaliInfoFunction() {
		questionCount = questionCount + 1;
		totalQuestionCount = totalQuestionCount + 1;

		var text = "<div id='prasInfo" + questionCount + "'>" +
	    "<p>" +
		  	"<label class='small'>" + $('#prashnavaliQuestionMessage').val() + "*</label>" +
		  	"<textarea class='wysiwyg' rows='5' cols='100' name='question" + questionCount + "' id='question" + questionCount + "''/>" +
		    "</p>" +
		 	"<p>" +
	  	"<label class='small'>" + $('#prashnavaliAnswerMessage').val() + "*</label>" +
	  	"<textarea class='wysiwyg' rows='5' cols='100' name='answer" + questionCount + "' id='answer" + questionCount + "'/>" +
	  	"</p>" +
		  	"<input type='button' class='button' id='" + questionCount + "' value='" + $('#deletePrashnavaliInfoMessage').val() + "' onclick='deletePrashnavaliInfo(" + questionCount + ");'>" +
	  	"<input type='hidden' id='prasInfoId" + questionCount + "' name='prasInfoId" + questionCount +"'>" +
	  	"<input type='hidden' id='prasInfoLocale" + questionCount + "' name='prasInfoLocale" + questionCount + "' value='" + $('#locale').val() +"'>" +
	  	"<input type='hidden' id='prasInfoVersion" + questionCount + "' name='prasInfoVersion" + questionCount + "'>" +
	  	"</div>"; 
	  	

		var prevCount = questionCount - 1;
		if(totalQuestionCount == 1){
			$('#addPrashnavaliInfo').after(text);
		} else{
			$('#prasInfo'+ prevCount).after(text);
		}
		$('.wysiwyg').wysiwyg();
		$('#questionSize').val(questionCount); 

		// To apply datemask to the date fields
		$('.datemask').focus(function(){
			if($(this).val() == ""){
				$(".datemask").mask("99/99/9999");
			}
		});

		// To apply timemask to the time fields
		$('.timemask').focus(function(){
			if($(this).val() == ""){
				$(".timemask").mask("99:99:99");
			}
		});
	}

	function deletePrashnavaliInfo(id) {
		var prasInfoId = $('#prasInfoId' + id).val();
		if(prasInfoId != ''){		
			$('#prasInfo' + id).remove();
			questionCount = questionCount - 1;
			totalQuestionCount = totalQuestionCount - 1;
			if(id == questionCount) {
				questionCount = questionCount - 1;
			}
	    	/*$.delete_('prashnavali/info/' + prasInfoId + '/delete', 
	    	    null, 
	    	    function(data, textStatus, XMLHttpRequest) {
	    			$('#prasInfo' + id).remove();
	    			questionCount = questionCount - 1;
					if(id == questionCount) {
						questionCount = questionCount - 1;
					}
	    		}
    		).fail(function() {
				if($("#ErrorMsg").val() != '') {
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}
				else {
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	*/
		} 
		else {
			$('#prasInfo'+id).remove();
			totalQuestionCount = totalQuestionCount - 1;
			if(id == questionCount) {
				questionCount = questionCount - 1;
			}
		}	
	}
	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		$('.wysiwyg').wysiwyg();
		$('#state').change(function(){
			var stateId = $('#state').val();
			onStateChange(stateId);
		});

		$('#district').change(function(){
			var districtId = $('#district').val();
			onDistrictChange(districtId);
		});

		$('#addPrashnavaliInfo').click(function(){
			addPrashnavaliInfoFunction();
		});

		$('#addReporter').click(function(){
			addReporter();
		});
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="prashnavali" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	<p>
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>
<%-- 	<select class="sSelect" id="committeeName" name="committeeName">
		<c:forEach items="${committeeNames}" var="i">
			<c:choose>
				<c:when test="${committeeName.id == i.id}">
					<option value="${i.id}" selected="selected"><c:out value="${i.displayName}"></c:out></option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select> --%>
	<input type="text" id="committeeDisplayName" name="committeeDisplayName" value="${committeeDisplayName}" readonly="readonly" class="sText"/>
	<input type="hidden" id="committeeName" name="committeeName" value="${committeName}"/>
	<form:errors path="committee" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="prashnavalinfo.Name" text="Name"/>*</label>
		<form:input path="prashnavaliName" cssClass="sText"/>
	</p>
	
	<c:if test="${domain.createDate != null}">
		<p>
			<input type="hidden" name="createDateHid" id="createDateHid" value="${domain.formatCreateDate('dd/MM/yyy', 'en_US')}"/>
		</p>
	</c:if>
	
	<!-- Dynamic Addition of Itinerary -->
	<div>
		<input type="button" id="addPrashnavaliInfo" class="button" value="<spring:message code='prashvali.addPrasnavaliInfo' text='Add Prashnavali Information'></spring:message>">
		<form:errors path="questionAnswers" cssClass="validationError"></form:errors>
		<c:if test="${not empty questions}">
			<c:set var="questionCount" value="1"></c:set>
			<c:forEach items="${questions}" var="outer">
				<div id="prasInfo${questionCount}">
					<p>
						<label class="small"><spring:message code="prashnavalinfo.question" text="Question"/>*</label>
						<textarea rows="5" cols="100" id="question${questionCount}" name="question${questionCount}" class="wysiwyg">${outer.question}</textarea>
					</p>
					
					<p>
						<label class="small"><spring:message code="prashnavalinfo.answer" text="Answer"/>*</label>
						<textarea rows="5" cols="100" id="answer${questionCount}" name="answer${questionCount}" class="wysiwyg">${outer.answer}</textarea>
					</p>
					
					<input type='button' id='${questionCount}' class='button' value='<spring:message code="prashnavalinfo.delete" text="Delete Info"></spring:message>' onclick='deletePrashnavaliInfo(${questionCount});'/>
					
					<!-- Hidden variables required for each instance of TourItinerary -->
					<input type='hidden' id='prasInfoId${questionCount}' name='prasInfoId${questionCount}' value="${outer.id}">
					<input type='hidden' id='prasInfoVersion${questionCount}' name='prasInfoVersion${questionCount}' value="${outer.version}">
					<input type='hidden' id='prasInfoLocale${questionCount}' name='prasInfoVersion${questionCount}' value="${domain.locale}">
				</div>
				<c:set var="questionCount" value="${questionCount + 1}"></c:set>
			</c:forEach>		
		</c:if>
		
		<!-- Hidden Messages to preserve the localization of the field names -->
		<input type="hidden" id="questionSize" name="questionSize" value="${questionSize}"/>
		
		<input type="hidden" id="deletePrashnavaliInfoMessage" name="deletePrashnavaliInfoMessage" value="<spring:message code='prashnavali.info.delete' text='Delete Prashnavali Info'></spring:message>" disabled="disabled"/>
		
		<input type="hidden" id="prashnavaliQuestionMessage" name="prashnavaliQuestionMessage" value="<spring:message code='prashnavalinfo.question' text='Question'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="prashnavaliAnswerMessage" name="prashnavaliAnswerMessage" value="<spring:message code='prashnavalinfo.answer' text='Answer'></spring:message>" disabled="disabled"/>
	</div>
	<%--
	<c:if test="${internalStatus.type eq 'prashnavali_created'}">
		<p>
		<label class="small"><spring:message code="prashnavali.putUpFor" text="Put Up For" /></label>
		<select id="status" name="status" class="sSelect">
		<c:choose>
			<c:when test="${not empty statuses}">
				<c:forEach items="${statuses}" var="i">
					<c:choose>
						<c:when test="${status.id == i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<option value="${status.id}" selected="selected"><c:out value="${status.name}"></c:out></option>
			</c:otherwise>
		</c:choose>		
		</select>
		</p>
		
	 	<p>
		<label class="small"><spring:message code="prashnavali.nextactor" text="Next Actor"/></label>
		<select id="actor" name="actor" class="sSelect">
		<c:choose>
			<c:when test="${not empty actors}">
				<c:forEach items="${actors}" var="i">
					<c:choose>
						<c:when test="${actor.id == i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<option value="${actor.id}" selected="selected"><c:out value="${actor.name}"></c:out></option>
			</c:otherwise>
		</c:choose>
		</select>
		</p> --%>
		
		<%-- <p>
			<label class="wysiwyglabel"><spring:message code="committee.remarks" text="Remarks"/></label>
			<textarea id="remarks" name="remarks"  class="wysiwyg" rows="2" cols="50">${remarks}</textarea>	
		</p> 	
	</c:if>--%>

	<%--  <c:if test="${internalStatus.type eq 'prashnavali_created'}">	--%>
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</p>
		</div>
	<%--  </c:if>--%>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<input type="hidden" name="houseType" id="houseType" value="${houseTypeId}">
	
	<input type="hidden" id="workflowInit" name="workflowInit" value="${workflowInit}"/>
	<input type="hidden" id="workflowName" name="workflowName" value="${workflowName}"/>
	<input type="hidden" id="assigneeLevel" name="assigneeLevel" value="${assigneeLevel}"/>
 	<input type="hidden" id="houseTypeId" name="houseTypeId" value="${houseType.id}"/>
	<input type="hidden" id="userGroupId" name="userGroupId" value="${userGroup.id}"/>
	<input id="requestForTourMsg" value="<spring:message code='committeetour.requestForTourMsg' text='Do you want to put up request for tour?'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="pleaseSelect" name="pleaseSelect" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
</form:form>
</div>
</body>
</html>