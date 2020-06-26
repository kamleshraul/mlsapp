<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="constituency"
		text="Constituencies" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- <link rel="stylesheet" media="screen" href="./resources/css/ui.sexyselect.0.55.css" />	
	
	<script type="text/javascript" src="./resources/js/ui.sexyselect.min.0.55.js"></script>	 -->

<script type="text/javascript">
	function translateNumerals(input, locale) { //instead use ajax request "'ref/format_numeric_text?numericText='+input+'&locale='+locale" to get formatted numeric text of input parameter
	  var offset=null;
	  //zero digit value must be defined for each locale
	  if(locale=="mr_IN"){
		  offset=2358; //this offset actually corresponds to alphabet 'sha' in marathi.. very difficult to find corresponding alphabet for other locale
	  }
	  var formattedDigit=new Array();
	  for (var i=0;i<input.length; i++) {
	    var c = input.charCodeAt(i);
	    var codepoint=parseInt(c)+offset;
	    var unicode=String.fromCharCode(codepoint);	    
	    formattedDigit.push(unicode);	    
	  }	 
	  return formattedDigit.join("");  
	}
	function populateDivisions(state) {
		$.get('ref/' + state + '/divisions', function(data) {
			$('#division option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			$('#division').html(options);
			if ($('#division').val() != undefined) {
				populateDistricts($('#division').val());				
			} else {
				var options = "";
				$('#districts').html(options);				
			}
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function populateDistricts(division) {
		$.get('ref/' + division + '/districts', function(data) {
			$('#districts option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			$('#districts').html(options);
			/* $('#districts').sexyselect('destroy');
			$('#districts').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true}); */
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function populateRailwayStations(selectedDistricts) {
		$.get('ref/districts' + selectedDistricts + '/railwayStations',
				function(data) {
					$('#nearestRailwayStation option').empty();
					var options = "";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].id+"'>"
								+ data[i].name + "</option>";
					}
					$('#nearestRailwayStation').html(options);
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
	}

	function populateAirports(selectedDistricts) {
		$.get('ref/districts' + selectedDistricts + '/airports',
				function(data) {
					$('#nearestAirport option').empty();
					var options = "";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].id+"'>"
								+ data[i].name + "</option>";
					}
					$('#nearestAirport').html(options);
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
	}	

	function clearRA() {
		$('#nearestRailwayStation option').empty();
		//$('#nearestRailwayStation').html("<option value=""></option>");
		//$('#nearestRailwayStation :selected').prop('value',0);
		$('#nearestRailwayStation').prop('selectedIndex', -1);
		$('#nearestAirport option').empty();
		//$('#nearestAirport :selected').prop('value',0);
		$('#nearestAirport').prop('selectedIndex', -1);
	}

	function setDisplayNameLH(){
		if($("#houseTypeType").val()=="lowerhouse"){
		var constituencyName=$("#constituencyNameLH").val();		
		if(constituencyName!=""){
			$("#name").val(constituencyName);
			constituencyNumber=$("#number").val();
			constituencyIsReserved=$("#isReserved").val();
			constituencyReservedFor=$("#reservedFor option:selected").text();				
			var selectedDistricts = [];			
			$('#districts :selected').each(function(i, selected) {
			selectedDistricts[i] = $(selected).text();
			});	
			var constituencyDistrict="";
			for(var i=0;i<selectedDistricts.length;i++){
				if(i<selectedDistricts.length-1){
				constituencyDistrict=constituencyDistrict+selectedDistricts[i].trim()+",";
				}else{
				constituencyDistrict=constituencyDistrict+selectedDistricts[i].trim();						
				}//inside if else
			}//for			
			//**************algorithm*********for display name 
			if(constituencyNumber!=""){
				$.get('ref/format_numeric_text?numericText='+constituencyNumber+'&locale='+$("#locale").val(), function(data) {
					cNo = data;
				}).done(function() {					
					if(constituencyDistrict.length>1){	
						if(constituencyIsReserved=="true"){
							if(constituencyReservedFor!=""){
								$("#displayName").val(cNo+"-"+constituencyName+"("+constituencyReservedFor+"), "+$("#districtMsg").val()+" "+constituencyDistrict);
							}//reserved for
							else{
								alert($("#selectReservedForMsg").val());
							}//reserved for
						}//is reserved 
						else{
							$("#displayName").val(cNo+"-"+constituencyName+", "+$("#districtMsg").val()+" "+constituencyDistrict);			
						}//is reserved 
					}//district
					else{
						alert($("#selectDistrictMsg").val());							
					}//district	
				}).fail(function() {
					alert("Some error occurred in formatting constituency number for display name!");
					return false;
				});				
			}//number
			else{
				if(constituencyDistrict.length>1){	
					if(constituencyIsReserved=="true"){
						if(constituencyReservedFor!=""){
							$("#displayName").val(constituencyName+"("+constituencyReservedFor+"), "+$("#districtMsg").val()+" "+constituencyDistrict);
						}//reserved for
						else{
							alert($("#selectReservedForMsg").val());
						}//reserved for
					}//is reserved 
					else{
						$("#displayName").val(constituencyName+", "+$("#districtMsg").val()+" "+constituencyDistrict);			
					}//is reserved 
				}//district
				else{
					if(constituencyIsReserved=="true"){
						if(constituencyReservedFor!=""){
							$("#displayName").val(constituencyName+"("+constituencyReservedFor+")");
						}//reserved for
						else{
							alert($("#selectReservedForMsg").val());
						}//reserved for
					}//is reserved 
					else{
						$("#displayName").val(constituencyName);			
					}//is reserved 			
				}//district	
			}//number				
		}//name
		else{
			$("#name").val("");	
			$("#displayName").val("");
		}//name
		}
	}

	function setDisplayNameUH(){
		if($("#houseTypeType").val()=="upperhouse"){
		$("#name").val($("#upperHouseConstituencyType option:selected").text().trim());			
		var selectedName=$("#upperHouseConstituencyType option:selected").text().trim();
		if(selectedName==$("#nominated").val().trim()||selectedName==$("#nominatedByAssemblyMembers").val().trim()){
			$("#stateP").hide();
			$("#divisionP").hide();
			$("#districtP").hide();
			$("#displayName").val(selectedName);
		}else if(selectedName==$("#degreeHolder").val().trim()||selectedName==$("#teacher").val().trim()){
			$("#stateP").show();
			$("#divisionP").show();
			$("#districtP").hide();				
			var divisionName=$("#division option:selected").text();	
			$("#displayName").val(divisionName.trim()+" "+selectedName.trim());
		}else{			
			$("#stateP").show();
			$("#divisionP").show();
			$("#districtP").show();
			var districtPart="";
			var selectedDistricts = [];			
			$('#districts :selected').each(function(i, selected) {
				selectedDistricts[i] = $(selected).text();
			});	
			for(var i=0;i<selectedDistricts.length;i++){
				if(i<selectedDistricts.length-1){
				districtPart=districtPart+selectedDistricts[i].trim()+"-";
				}else{
					districtPart=districtPart+selectedDistricts[i].trim();						
				}
			}				
			$("#displayName").val(districtPart.trim()+" "+selectedName);				
		}
		}
	}

	$(document).ready(function() {
		$("#houseTypeMasterDiv").hide();
		//Depending on the values of houseTypeType suitable controls will be made visible. 
		//if houseTypeType is upperhouse then state,district and division field will be hidden.
		//also we will start with nominated values. and so constituencyNameUH,name and displayname will
		//be set to nominated values.Here two things can happen when we are arriving on new by cliking new or because of validation error
		//in first case nominated is the default value and in second case whatever wa set in name will be name on
		//which decisions will be made.Now if name is set then we will also set the display name.
		if($('#houseTypeType').val()=="upperhouse") {						
			var selectedName=$("#upperHouseConstituencyType option:selected").text().trim();
			//1.when its a new record.
			if(selectedName==""){
				$("#stateP").hide();
				$("#divisionP").hide();
				$("#districtP").hide();
				//$("#constituencyNameUH").val($("#nominated").val().trim());
				$("#name").val($("#nominated").val());
				$("#displayName").val(selectedName);
			}//2.validation error
			else{
				if(selectedName==$("#nominated").val().trim()||selectedName==$("#nominatedByAssemblyMembers").val().trim()){
					$("#stateP").hide();
					$("#divisionP").hide();
					$("#districtP").hide();				
				}else if(selectedName==$("#degreeHolder").val().trim()||selectedName==$("#teacher").val().trim()){
					$("#stateP").show();
					$("#divisionP").show();
					$("#districtP").hide();						
				}else{
					$("#stateP").show();
					$("#divisionP").show();
					$("#districtP").show();			
				}
				//$("#constituencyNameUH").val(selectedName);
				setDisplayNameUH();
			}			
			$('.hiddenCommonFields').show();
			$('.hiddenAssemblyFields').hide();
			$('.hiddenCouncilFields').show();			
			$('.tright').show();
			$('#rLabel').hide();
			$('#reservedFor').hide();
		} else if($('#houseTypeType').val()=="lowerhouse") {
			$('.hiddenAssemblyFields').show();
			$('.hiddenCommonFields').show();
			$('.hiddenCouncilFields').hide();			
			$('.tright').show();
			var constituencyName=$("#name").val().trim();			
			if(constituencyName!=""){
				$("#constituencyNameLH").val(constituencyName);
				setDisplayNameLH();				
			}else{
				$("#constituencyNameLH").val("");
				$("#displayName").val("");											
			}
			if($("#isReserved").val()=="true"){
				$(this).attr("checked","checked");
				$('#rLabel').show();
				$('#reservedFor').show();
			}else{
				$('#rLabel').hide();
				$('#reservedFor').hide();
			}			
		}	
		//set initial state and division
		//housetype change	
		$('#houseType').change(function() {	
			//set the value of houseTypeType
			var selectedHTId=$(this).val();
			$("#houseTypeMaster").val(selectedHTId);
			var houseTypeType=$("#houseTypeMaster option:selected").text().trim();
			$("#houseTypeType").val(houseTypeType);				
			if($('#houseTypeType').val()=="lowerhouse") {
					$('.hiddenAssemblyFields').show();
					$('.hiddenCommonFields').show();
					$('.hiddenCouncilFields').hide();			
					$('.tright').show();
					$("#name").val("");
					$("#displayName").val("");
					$("#constituencyNameLH").val("");
			}
			else if($('#houseTypeType').val()=="upperhouse") {
					$('.hiddenCommonFields').show();
					$('.hiddenAssemblyFields').hide();
					$('.hiddenCouncilFields').show();				
					$('.tright').show();					
					$("#stateP").hide();
					$("#divisionP").hide();
					$("#districtP").hide();
					//$("#constituencyNameUH").val($("#nominated").val().trim());
					$("#name").val($("#nominated").val());
					var selectedName=$("#name").val();
					$("#displayName").val(selectedName);
			}								
		}); 
		//LH name change field		
		$("#constituencyNameLH").change(function(){
				setDisplayNameLH();			
		});		
		//UH name change field
		$("#upperHouseConstituencyType").change(function(){
		    	setDisplayNameUH();					
		});	
		//states changed field
		$('#state').change(function() {
			populateDivisions($('#state').val());
			clearRA();
		}); 
		//division change field
		$('#division').change(function() {
				populateDistricts($('#division').val());				
				clearRA();
				//if division changes in cas of housetype=upperhouse and constituencyNameUH=teacher or degree holder
				//then display name must change too.	
				if($("#houseTypeType").val()=="upperhouse"){
					var selectedName=$("#upperHouseConstituencyType option:selected").text().trim();
			        if(selectedName==$("#degreeHolder").val().trim()||selectedName==$("#teacher").val().trim()){
			        	var divisionName=$("#division option:selected").text().trim();				
						$("#displayName").val(divisionName+" "+selectedName);  
						$("#divisionName").val($("#division option:selected").text());
						$("#divisionId").val($("#division").val());						
			        }else if(selectedName==$("#localInstitutionNominated").val().trim()){
						var divisionName=$("#division option:selected").text().trim();				
						$("#displayName").val(divisionName+" "+selectedName);  
						$("#divisionName").val($("#division option:selected").text());
						$("#divisionId").val($("#division").val());
					}				        										
				}//only in case of upperhouse has some effect			
		});		
		//districts change
		$("#districts").change(function(){
			if($("#houseTypeType").val()=="upperhouse"){
				var selectedName=$("#upperHouseConstituencyType option:selected").text().trim();
				if(selectedName==$("#localInstitutionNominated").val().trim()){
					var districtPart="";
					var selectedDistricts = [];			
					$('#districts :selected').each(function(i, selected) {
						selectedDistricts[i] = $(selected).text().trim();
					});	
					for(var i=0;i<selectedDistricts.length;i++){
						if(i<selectedDistricts.length-1){
						districtPart=districtPart+selectedDistricts[i].trim()+"-";
						}else{
							districtPart=districtPart+selectedDistricts[i].trim();						
						}
					}				
					$("#displayName").val(districtPart+" "+selectedName);	
				}	
			}//upperhouse
			else if($("#houseTypeType").val()=="lowerhouse"){
				setDisplayNameLH();
			}//lowerhouse					
		});		
		//number changed function
		$("#number").change(function(){
			if($("#houseTypeType").val()=="lowerhouse"){
				setDisplayNameLH();
			}
		});
		//is retired change		
		$("#isSittingSelect").val($("#isRetired").val());
		$("#isSittingSelect").change(function(){
			$("#isRetired").val($(this).val());
		});	
		//railway station change  
	    $('#railwayStationsBySelectedDistricts').click(function() {
			var selectedDistricts = [];
			$('#districts :selected').each(function(i, selected) {
				selectedDistricts[i] = $(selected).val();
			});
			if (selectedDistricts == "") {
				alert("Please select at least one district first!");
			} else {
				populateRailwayStations(selectedDistricts);
			}
		});
		//airport change
		$('#airportsBySelectedDistricts').click(function() {
			var selectedDistricts = [];
			$('#districts :selected').each(function(i, selected) {
				selectedDistricts[i] = $(selected).val();
			});
			if (selectedDistricts == "") {
				alert("Please select at least one district first!");
			} else {
				populateAirports(selectedDistricts);
			}
		});
		//is reserved change
		var isReserved=$("#isReserved").val();
		if(isReserved=="true"){
			$('#rLabel').show();
			$('#reservedFor').show();  
			$("#isReservedCheck").attr("checked","checked");
		}else{
			$('#rLabel').hide();
			$('#reservedFor').hide();  
			$("#isReservedCheck").removeAttr("checked");
		}
		$('#isReservedCheck').click(function() {
			if($('#isReservedCheck').is(':checked'))
	   		{
				$('#rLabel').show();
				$('#reservedFor').show(); 
				$("#isReserved").val("true");  	    
			}
			else {
				$('#rLabel').hide();
				$('#reservedFor').hide();
				$("#isReserved").val("false"); 
			}
			setDisplayNameLH();			
		});	
		$("#reservedFor").change(function(){
			setDisplayNameLH();
		});	
	});
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
		<form:form action="constituency" method="POST" id="form"
			modelAttribute="domain">
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
						<label class="small"><spring:message
								code="constituency.houseType"
								text="House Type" /></label>
						<form:select path="houseType" items="${houseTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
					</p>
					<input type="hidden" id="houseTypeType" name="houseTypeType" value="${houseTypeType}" >
										
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.name" text="Name" /></label>
						<input id="constituencyNameLH" type="text">						
					</p>
										
					<p class="hiddenCouncilFields">
						<label class="small"><spring:message
								code="constituency.name" text="Name" /></label>
								<form:select path="upperHouseConstituencyType" itemLabel="name" itemValue="id" items="${upperHouseConstituencyTypes}" cssClass="sSelect"></form:select>
					</p>
					<form:hidden cssClass="sText" path="name"/>
					<form:errors path="name" cssClass="validationError" />
					<input type="hidden" id="nominated" value="<spring:message code='upperhouse.constituencytype.1' text='Constituency1'/>">	
					<input type="hidden" id="degreeHolder" value="<spring:message code='upperhouse.constituencytype.2' text='Constituency2'/>">						
					<input type="hidden" id="nominatedByAssemblyMembers" value="<spring:message code='upperhouse.constituencytype.3' text='Constituency3'/>">						
					<input type="hidden" id="teacher" value="<spring:message code='upperhouse.constituencytype.4' text='Constituency4'/>">						
					<input type="hidden" id="localInstitutionNominated" value="<spring:message code='upperhouse.constituencytype.5' text='Constituency5'/>">					
					
					<p class="hiddenCommonFields" id="stateP">
						<label class="small"><spring:message
								code="constituency.state" text="State" /></label>
						    <select class="sSelect" name="state"
							id="state">
							<c:if test="${!(empty states)}">
							<c:forEach items="${states}" var="i">
							<c:choose>
							<c:when test="${defaultState==i.id}">
							<option value="${i.id}" selected="selected">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:when>
							<c:otherwise>
							<option value="${i.id}" selected="selected">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:otherwise>
							</c:choose>
							</c:forEach>							
							</c:if>								
						</select>
					</p>
					
					<p class="hiddenCommonFields" id="divisionP">
						<label class="small"><spring:message
								code="constituency.division" text="Division" /></label>
							<select class="sSelect" name="division"
							id="division">
							<c:if test="${!(empty divisions)}">
							<c:forEach items="${divisions}" var="i">
							<c:choose>
							<c:when test="${selectedDivision==i.id}">
							<option value="${i.id}" selected="selected">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:when>
							<c:otherwise>
							<option value="${i.id}" selected="selected">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:otherwise>
							</c:choose>
							</c:forEach>							
							</c:if>								
						</select>	
						<form:hidden path="divisionName"/>
						<input id="divisionId" name="divisionId" type="hidden">					 
					</p>
					
					<p class="hiddenCommonFields" id="districtP">
						<label class="labeltop" style="top: -40px;"><spring:message
								code="constituency.district" text="Districts" /></label>
						<form:select cssClass="sSelectMultiple" path="districts" items="${districts}" itemValue="id"
							itemLabel="name" multiple="multiple" onclick="clearRA()"></form:select>
						<form:errors path="districts" cssClass="validationError" />
					</p>					
					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.number" text="Number" /></label>
						<form:input cssClass="sText" path="number" id="number"/>
						<form:errors path="number" cssClass="validationError" />
					</p>
					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.voters" text="Voters" /></label>
						<form:input cssClass="sText" path="voters" id="voters"/>
						<form:errors path="voters" cssClass="validationError" />
					</p>
					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.reserved" text="Reserved?" /></label>
						<input type="checkbox" id="isReservedCheck" name="isReservedCheck" class="sCheck">
						<form:hidden path="isReserved"/>												
						<form:errors path="isReserved" cssClass="validationError" />
					</p>
					
					<p class="hiddenAssemblyFields">
						<label class="small" id="rLabel"><spring:message
								code="constituency.reservedFor" text="Reserved for" /></label>
						<form:select cssClass="sSelect" path="reservedFor" items="${reservations}" itemValue="id" itemLabel="name" id="reservedFor"></form:select>
						<form:errors path="reservedFor" cssClass="validationError" />
					</p>	
					
					<p class="hiddenCommonFields">
						<label class="labeltop" style="top: -40px;"><spring:message
								code="constituency.displayname" text="Full Name" /></label>
						<form:textarea cssClass="sTextarea" path="displayName" id="displayName"></form:textarea>
						<form:errors path="displayName" cssClass="validationError" />
					</p>
									
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.railwayStation"
								text="Nearest Railway Station" /></label>
						<form:select cssClass="sSelect" path="nearestRailwayStation"
							items="${railwayStations}" itemValue="id" itemLabel="name"
							id="nearestRailwayStation" size="1"></form:select>
						<form:errors path="nearestRailwayStation"
							cssClass="validationError" />
						<input type="button" class="small"
							id="railwayStationsBySelectedDistricts"
							value="list for selected districts" />
					</p>
					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.airport" text="Nearest Airport" /></label>
						<form:select cssClass="sSelect" path="nearestAirport" items="${airports}"
							itemValue="id" itemLabel="name" id="nearestAirport" size="1"></form:select>
						<form:errors path="nearestAirport" cssClass="validationError" />
						<input type="button" class="small"
							id="airportsBySelectedDistricts"
							value="list for selected districts" />
					</p>			
					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.retired" text="Retired?" /></label>
						<select id="isSittingSelect" name="isSittingSelect" class="sSelect">
						<option value="false"><spring:message code="member.house.sitting" text="Sitting"></spring:message></option>
						<option value="true"><spring:message code="member.house.ex" text="Ex"></spring:message></option>
						</select>
						<form:hidden path="isRetired" />		
						<form:errors path="isRetired" cssClass="validationError" />
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
			<form:hidden path="version" />
			<form:hidden path="id" />
			<form:hidden path="locale" />			
		</form:form>
		<p id="houseTypeMasterDiv">
						<label class="small"><spring:message
								code="constituency.houseType"
								text="House Type" /></label>
						<select class="sSelect" id="houseTypeMaster">
						<c:forEach items="${houseTypes}" var="i">
						<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
						</c:forEach>
						</select>										
		</p>	
		<input type="hidden" id="divisionMsg" name="divisionMsg" value="<spring:message code='constituency.division' text='Division'></spring:message>">
		<input type="hidden" id="districtMsg" name="districtMsg" value="<spring:message code='constituency.districtMsg' text='District'></spring:message>">
		<input type="hidden" id="selectDistrictMsg" name="selectDistrictMsg" value="<spring:message code='constituency.selectDistrictMsg' text='Please select atleat 1 district'></spring:message>">
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>