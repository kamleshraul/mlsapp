<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.devicetypeconfig.edit" text="Edit Session Config"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title></title>
<script type="text/javascript">
//hide all the divs
	function hideDivs(){
	   $('div.formDiv').hide();   
	}

	//as all controls get loaded all divs wil be hiddden
	$('document').ready(function(){
		hideDivs();
	});
	
	 $("#deviceType").change(function(){
		
		var isBallotingRequired = "#"+$('#deviceType').val()+"_isBallotingRequired";
		var isBallotingRequiredValue = $(isBallotingRequired).val();
		//alert($.type($(isBallotingRequired).val()));
		if(isBallotingRequiredValue.length==4){
			//alert(isBallotingRequired+":"+isBallotingRequiredValue);
			$(isBallotingRequired).attr('checked','checked');
		}else if(isBallotingRequiredValue.length==5){
			//alert("nononnoo");
			$(isBallotingRequired).removeAttr('checked');
		}
	});

	//show the particular div as option is selected
	$('document').ready(function(){
		
		$('.datemask').focus(function(){		
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});
		$('.datetimemask').focus(function(){		
			if($(this).val()==""){
				$(".datetimemask").mask("99/99/9999 99:99:99");
			}
		});
		$('#deviceType').change(function(){
	      var selVal=$('#deviceType').val();
	      var selectedDiv='#'+selVal;
		  hideDivs();
		  //alert(selectedDiv);
		  
		  $(selectedDiv).show();
	    });
		
		$('#submit').click(function(){
			var deviceType = $('#deviceType').val();
			if(deviceType.length>0) {	
				
				var id="id";
				var submissionStartDate=deviceType+"_submissionStartDate";
				var submissionEndDate=deviceType+"_submissionEndDate";
				var submissionFirstBatchStartDate=deviceType+"_submissionFirstBatchStartDate";
				var submissionFirstBatchEndDate=deviceType+"_submissionFirstBatchEndDate";
				var submissionSecondBatchStartDate=deviceType+"_submissionSecondBatchStartDate";
				var submissionSecondBatchEndDate=deviceType+"_submissionSecondBatchEndDate";
				
				var firstBallotDate=deviceType+"_firstBallotDate";
				var NumberOfQuestionInFirstBatch=deviceType+"_NumberOfQuestionInFirstBatch";
				var NumberOfQuestionInSecondBatch=deviceType+"_NumberOfQuestionInSecondBatch";
				var isBallotingRequired=deviceType+"_isBallotingRequired";
				var rotationOrderHeader = deviceType+"_rotationOrderHeader";
				var rotationOrderFooter = deviceType+"_rotationOrderFooter";
				
				var params={};
				params[id]=$("#"+id).val();
				params[submissionStartDate]=$("#"+submissionStartDate).val();
				params[submissionEndDate]=$("#"+submissionEndDate).val();
				params[submissionFirstBatchStartDate]=$("#"+submissionFirstBatchStartDate).val();
				params[submissionFirstBatchEndDate]=$("#"+submissionFirstBatchEndDate).val();
				params[submissionSecondBatchStartDate]=$("#"+submissionSecondBatchStartDate).val();
				params[submissionSecondBatchEndDate]=$("#"+submissionSecondBatchEndDate).val();
				
				params[firstBallotDate]=$("#"+firstBallotDate).val();
				params[NumberOfQuestionInFirstBatch]=$("#"+NumberOfQuestionInFirstBatch).val();
				params[NumberOfQuestionInSecondBatch]=$("#"+NumberOfQuestionInSecondBatch).val();
				params[rotationOrderHeader] =$("#"+rotationOrderHeader).val();
				params[rotationOrderFooter] = $("#"+rotationOrderFooter).val();
				
				if($("#"+isBallotingRequired).is(":checked")) {
					$("#"+isBallotingRequired).val(true);
				}
				else {
					$("#"+isBallotingRequired).val(false);
				}
				params[isBallotingRequired]=($("#"+isBallotingRequired).val());
	 
				$('#deviceTypeSelected').val(deviceType);
				 $.post("session/devicetypeconfig" , params);
			}
			else {
				return false;
			}
		});
	});
	

</script>
</head>
<body>
<div class="fields clearfix">

	<div id="headerDiv">
		<p> 
		<label class="small"><spring:message code="devicetype.name" text="Name"/></label>
		<select id="deviceType" name="deviceType">
			<option value=""><spring:message code="please.select.deviceType" text="Please Select Device Type"/></option>
			<c:forEach items="${deviceTypesEnabled}" var="i">
			<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</select>
		</p>
	</div>
	<%@ include file="/common/info.jsp" %>
				<form:form action="session/devicetypeconfig" method="POST" modelAttribute="domain">
	<c:forEach  items="${deviceTypesEnabled}" var="i">
			<div id="${i.type}" class="formDiv">
			
					
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionStartDate" text="Submission Start Date"/></label>
						<c:set var="key" value="${i.type}_submissionStartDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionStartDate" id="${i.type}_submissionStartDate" value="${domain.parameters[key]}"/>
					</p>
					
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date"/></label>
						<c:set var="key" value="${i.type}_submissionEndDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionEndDate" id="${i.type}_submissionEndDate" value="${domain.parameters[key]}"/>
					</p>
					
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionFirstBatchStartDate" text="Submission First Batch Start Date"/></label>
						
						<c:set var="key" value="${i.type}_submissionFirstBatchStartDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionFirstBatchStartDate" id="${i.type}_submissionFirstBatchStartDate" value="${domain.parameters[key]}"/>
					</p>
						
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionFirstBatchEndDate" text="Submission First Batch End Date"/></label>
						<c:set var="key" value="${i.type}_submissionFirstBatchEndDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionFirstBatchEndDate" id="${i.type}_submissionFirstBatchEndDate" value="${domain.parameters[key]}"/>
					</p>
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionSecondBatchStartDate" text="Submission Second Batch Start Date"/></label>
						<c:set var="key" value="${i.type}_submissionSecondBatchStartDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionSecondBatchStartDate" id="${i.type}_submissionSecondBatchStartDate" value="${domain.parameters[key]}"/>
					</p>
						
					<p>
						<label class="small"><spring:message code="session.deviceType.submissionSecondBatchEndDate" text="Submission Second Batch End Date"/></label>
						<c:set var="key" value="${i.type}_submissionSecondBatchEndDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_submissionSecondBatchEndDate" id="${i.type}_submissionSecondBatchEndDate" value="${domain.parameters[key]}"/>
					</p>
					
					<p>
						<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date"/></label>
						<c:set var="key" value="${i.type}_firstBallotDate"></c:set>
						<input type="text" class="datetimemask sText" name="${i.type}_firstBallotDate" id="${i.type}_firstBallotDate" value="${domain.parameters[key]}"/>
					</p>
						
					<p>
						<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInFirstBatch" text="Number of Question In First Batch"/></label>
						<c:set var="key" value="${i.type}_NumberOfQuestionInFirstBatch"></c:set>
						<input type="text" class="sInteger" name="${i.type}_NumberOfQuestionInFirstBatch" id="${i.type}_NumberOfQuestionInFirstBatch" value="${domain.parameters[key]}" />
					</p>
					
					<p>
						<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInSecondBatch" text="Number of Question In Second Batch"/></label>
						<c:set var="key" value="${i.type}_NumberOfQuestionInSecondBatch"></c:set>
						<input type="text" class="sInteger" name="${i.type}_NumberOfQuestionInSecondBatch" id="${i.type}_NumberOfQuestionInSecondBatch" value="${domain.parameters[key]}" />
					</p>
						
					<p>
						<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
						<c:set var="key" value="${i.type}_isBallotingRequired"></c:set>
						<input type="checkbox" class="sCheck" name="${i.type}_isBallotingRequired" id="${i.type}_isBallotingRequired" value="${domain.parameters[key]}" />					
					</p>

					<p>
						<label class="small"><spring:message code="session.deviceType.rotationOrderHeader" text="Rotation Order Header" /></label>
						<c:set var="key" value="${i.type}_rotationOrderHeader"></c:set>
						<textarea class="wysiwyg" cols="50" rows="5" id="${i.type}_rotationOrderHeader">${domain.parameters[key]}</textarea>
					</p>
					
					<p>
						<label class="small"><spring:message code="session.deviceType.rotationOrderFooter" text="Rotation Order Footer" /></label>
						<c:set var="key" value="${i.type}_rotationOrderFooter"></c:set>
						<textarea class="wysiwyg" cols="50" rows="5" id="${i.type}_rotationOrderFooter">${domain.parameters[key]}</textarea>
					</p>
					
					

				</div>
		</c:forEach>
		<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				</p>
		</div>	
		<form:hidden path="version"  />
		<form:hidden id="id" path="id" />
		<form:hidden path="locale"/>
		<input type="hidden" name="deviceTypeSelected" id="deviceTypeSelected" />
		</form:form>
		</div>
</body>
</html>