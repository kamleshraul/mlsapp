<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="session.list"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		//createGroupSameAsPreviousCKB
		 $('#createGroupSameAsPreviousCKB').hide()
		 
		  $('#groupCreationckb').click(function() {
              if($(this).prop("checked") == true) {
            	  $('#createGroupSameAsPreviousCKB').show()
              }
              else if($(this).prop("checked") == false) {
            	  $('#createGroupSameAsPreviousCKB').hide()
              }
            });
		
		
		loadFixedDeviceType($('#houseType').val());
		
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
		loadFixedDeviceType($('#houseType').val());
		if($('#houseType').val()=="upperhouse"){
			$('.council').show();
			$('.assembly').hide();
		}
		else{
			$('.council').hide();
			$('.assembly').show();
		}
	});

	$("#span_roles").attr('style', 'width: 300px !important; height: 140px !important;');
	var multiSelectMaxHeight = $('.multiSelectSpan').css('max-height');	
	
	$('.expansionMultiSelect').click(function() {
		var selectedId = this.id;
		
		if($(this).text()=='Expand') {
			if(selectedId=='expandRole') {
				$("#span_roles").attr('style', 'width: 300px !important; max-height: initial !important;');
			}				
			$(this).text('Collapse');
		} else {
			if(selectedId=='expandRole') {
				$("#span_roles").attr('style', 'width: 300px !important; height: 140px !important; max-height: ' + multiSelectMaxHeight + ' !important;');
			}				
			$(this).text('Expand');
		}
	});
	
	
	});
	function populateHouse(houseType) {
		$.get('ref/' + houseType + '/houses', function(data) {
			$('#house ').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options =options+ "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			$('#house').html(options);
		
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	
	 function loadFixedDeviceType(houseType)
	{
		$("#deviceTypesEnabled").empty();
		$.get('ref/' +houseType+ '/getFixedDevices', function(data) {
			//console.log(data)
			var text="";
			for(var i=0;i<data.length;i++)
					{
						if(data[i].isSelected == true)
							{
							 							
							 $('.checkbox_deviceTypesEnabled').eq(i).prop("checked", true);
							 text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].displayName+"</option>";
							}
						else{
							text+="<option value='"+data[i].name+"' >"+data[i].displayName+"</option>";
							}
					}
				//console.log(text)
				$("#deviceTypesEnabled").html(text);
				$("#deviceTypesEnabled").multiSelect();	
						
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
			
			});
	}
	
	
	
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
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
							itemValue="id" itemLabel="name" id="house" size="1">
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
				<label class="small"><spring:message code="session.financialyear" text="Session Year"/>&nbsp;*</label>
				<form:input id="financialYear"  cssClass="sText" path="financialYear" /> <label class="small">(  eg :- 2022-2023)</label>
				<form:errors path="year" cssClass="validationError" />			
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
																
				</select>
				<a id="expandRole" class="expansionMultiSelect" href="javascript:void(0);" style="float: right;">Expand</a>		
			
				<form:errors path="deviceTypesEnabled" cssClass="validationError" />

		</p>	
		<p>
				<label class="small"><spring:message
						code="session.actualStartDate" text="Actual Start Date" />&nbsp;*</label>
				<form:input id="actualStartDate" cssClass="datemask sText" path="actualStartDate" />
				<form:errors path="actualStartDate" cssClass="validationError" />

			</p>

			<p>
				<label class="small"><spring:message code="session.actualEndDate"
						text="Actual End Date" /></label>
				<form:input id="actualEndDate" cssClass="datemask sText" path="actualEndDate" />
				<form:errors path="actualEndDate" cssClass="validationError" />

			</p> 
		<p>
				<label class="small"><spring:message code="session.remarks"
						text="Remarks" /></label>
				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />

		</p>
		
		<p>
				<label class="small"><spring:message code="session.groupCreation"
						text="groupCreation" /></label>
				
				<input type="checkbox" name="groupCreation" id="groupCreationckb" />
		</p>
		<div id="createGroupSameAsPreviousCKB">
		<p>
				<label class="small"><spring:message code="session.createGroupAsPrevious"
						text="create Group As Previous" /></label>
				
				<input type="checkbox" name="createGroupSameAsPrevious"  />
		</p>
		</div>		
				
		
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
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>