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
  		  	"<textarea class='wysiwyg' rows='5' cols='100' name='question" + questionCount + "' id='question" + questionCount + "'/>" +
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
		$('#questionSize').val(questionCount); 
		$('.wysiwyg').wysiwyg();
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
	    	$.delete_('prashnavali/info/' + prasInfoId + '/delete', 
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
			});	
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
<form:form action="prashnavali" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>

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
			<input type="hidden" name="createDate" id="createDate" value="${domain.formatCreateDate('YYYY-MM-DD', 'en_US')}"/>
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
						<input id="question${questionCount}" name="question${questionCount}" class="sText" value="${outer.question}">
						
					</p>
					
					
					<p>
						<label class="small"><spring:message code="prashnavalinfo.answer" text="Answer"/>*</label>
						<input id="answer${questionCount}" name="answer${questionCount}" class="sText" value="${outer.answer}">
					</p>
					
					<input type='button' id='${questionCount}' class='button' value='<spring:message code="prashnavalinfo.delete" text="Delete Prashnavali Info"></spring:message>' onclick='prashnavaliInfo(${questionCount});'/>
					
					
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
	
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<input type="hidden" name="houseType" id="houseType" value="${houseTypeId}">
</form:form>
</div>
</body>
</html>