<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="constituency"
		text="Constituencies" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- <link rel="stylesheet" media="screen" href="./resources/css/ui.sexyselect.0.55.css" />	
	
	<script type="text/javascript" src="./resources/js/ui.sexyselect.min.0.55.js"></script>	 -->

<script type="text/javascript">
	function populateDivisions(state) {
		$.get('ref/' + state + '/divisions', function(data) {
			$('#divisions option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			$('#divisions').html(options);
			if ($('#divisions').val() != undefined) {
				populateDistricts($('#divisions').val());
				//populateName($('#divisions').val());
			} else {
				var options = "";
				$('#districts').html(options);
				//document.getElementById("name").value = "";
				alert("There is NO division in selected state");
			}
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

	$(document).ready(function() {
		$('.hiddenAssemblyFields').hide();
		$('.hiddenCommonFields').hide();
		$('.tright').hide();
		
		if($('#houseTypes :selected').val()=="lowerhouse") {
			$('.hiddenAssemblyFields').show();
			$('.hiddenCommonFields').show();
			$('.tright').show();
		}
		else if($('#houseTypes :selected').val()=="upperhouse") {
			$('.hiddenCommonFields').show();
			$('.hiddenAssemblyFields').hide();
			$('.tright').show();
		}
		else {
			$('.hiddenAssemblyFields').hide();
			$('.hiddenCommonFields').hide();
			$('.tright').hide();
		}
		
		
		if ($('#houseTypes').val() != undefined) {
			$('#houseTypes').change(function() {
				if($('#houseTypes :selected').val()=="lowerhouse") {
					$('.hiddenAssemblyFields').show();
					$('.hiddenCommonFields').show();
					$('.tright').show();
				}
				else if($('#houseTypes :selected').val()=="upperhouse") {
					$('.hiddenCommonFields').show();
					$('.hiddenAssemblyFields').hide();
					$('.tright').show();
				}
				else {
					$('.hiddenAssemblyFields').hide();
					$('.hiddenCommonFields').hide();
					$('.tright').hide();
				}
			}); //change			
		} //if
		
		//$('select[multiple="multiple"]').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true});		
		if ($('#states').val() != undefined) {
			$('#states').change(function() {
				populateDivisions($('#states').val());
				clearRA();
			}); //change
		} //if
		if ($('#divisions').val() != undefined) {			
			$('#divisions').change(function() {
				populateDistricts($('#divisions').val());				
				clearRA();				
			}); //change
		} //if	
		if ($('#districts').val() != undefined) {
			$('#districts :selected').change(function() {
				clearRA();
			}); //change			
		} //if		  
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
		$('#submit').click(function() {		
			var htype = $('#houseTypes :selected').val();
			if( htype=="lowerhouse" ) {					
				$('#divisionName').val($('#divisions option:selected').text().trim());
				alert($('#divisionName').val());
				
				if($('#isReserved').is(':checked'))
			   	{
					$('#isReserved').val(true);		   	    
				}
				else
			   	{ 				
					$('#isReserved').val(false);				
					$('#reservedFor').prop('selectedIndex', 0);
			   	};
			   	
			   	
			   	if($('#isRetired').is(':checked'))
			   	{
					$('#isRetired').val(true);		   	    
				}
				else
			   	{ 				
					$('#isRetired').val(false);				
			   	};
			}
			else if( htype=="upperhouse" ) {
				$('#divisionName').val($('#constituencyName').val());
				alert($('#divisionName').val());				
				$('#districts').prop('selectedIndex', -1);
				$('#number').val(null);
				$('#voters').val(null);	
				$('#isReserved').val(null);
				$('#reservedFor').prop('selectedIndex', -1);
				$('#nearestRailwayStation').prop('selectedIndex', -1);
				$('#nearestAirport').prop('selectedIndex', -1);				
				$('#isRetired').val(null);
			}			
		});
	});//document .ready
</script>
</head>
<body>
	<div class="fields clearfix">
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
						<select id="houseTypes" name="housetype">
						<c:forEach items="${houseTypes }" var="htype">
						<c:choose>
						<c:when test="${houseType==htype.type }">
						<option value="${htype.type}" selected="selected">${htype.name}</option>
						</c:when>
						<c:otherwise>
						<option value="${htype.type}">${htype.name}</option>
						</c:otherwise>
						</c:choose>
						</c:forEach>
						</select>											
					</p>
			
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.state" text="State" /></label> <select name="state"
							id="states">
							<c:forEach items="${states}" var="i">
								<option value="${i.id}">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:forEach>
						</select>
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.division" text="Division" /></label> <select
							name="division" id="divisions">
							<c:forEach items="${divisions}" var="i">
								<option value="${i.id}">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:forEach>
						</select>
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.district" text="Districts" /></label>
						<form:select path="districts" items="${districts}" itemValue="id"
							itemLabel="name" multiple="multiple" id="districts"
							onclick="clearRA()"></form:select>
						<form:errors path="districts" cssClass="validationError" />
					</p>
					<p class="hiddenCommonFields">
						<label class="small"><spring:message
								code="constituency.name" text="Name" /></label>
						<form:input cssClass="sSelect" path="name" id="constituencyName" />
						<form:errors path="name" cssClass="validationError" />
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.number" text="Number" /></label>
						<form:input cssClass="sSelect" path="number" id="number"/>
						<form:errors path="number" cssClass="validationError" />
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.voters" text="Voters" /></label>
						<form:input cssClass="sSelect" path="voters" id="voters"/>
						<form:errors path="voters" cssClass="validationError" />
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.reserved" text="Reserved?" /></label>
						<form:checkbox  cssClass="sCheck" id="isReserved" path="isReserved"/>												
						<form:errors path="isReserved" cssClass="validationError" />
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.reservedFor" text="Reserved for" /></label>
						<form:select cssClass="sOption" path="reservedFor" items="${reservations}" itemValue="id" itemLabel="name" id="reservedFor"></form:select>
						<form:errors path="reservedFor" cssClass="validationError" />
					</p>					
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.railwayStation"
								text="Nearest Railway Station" /></label>
						<form:select path="nearestRailwayStation"
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
						<form:select path="nearestAirport" items="${airports}"
							itemValue="id" itemLabel="name" id="nearestAirport" size="1"></form:select>
						<form:errors path="nearestAirport" cssClass="validationError" />
						<input type="button" class="small"
							id="airportsBySelectedDistricts"
							value="list for selected districts" />
					</p>
					<p class="hiddenAssemblyFields">
						<label class="small"><spring:message
								code="constituency.retired" text="Retired?" /></label>
						<form:checkbox cssClass="sSelect" path="isRetired" id="isRetired"/>
						<form:errors path="isRetired" cssClass="validationError" />
					</p>					
				<%-- </c:when>
				<c:otherwise> --%>										
					<%-- <p>
						<label class="small"><spring:message
								code="constituency.electionType" text="Election Type" /></label> <select
							name="electionType" id="electionTypes">
							<c:forEach items="${electionTypes}" var="i">
								<option value="${i.id}">
									<c:out value="${i.name}"></c:out>
								</option>
							</c:forEach>
						</select>
					</p> --%>
				<%-- </c:otherwise>
			</c:choose> --%>
			<%-- <p>
				<input type="hidden" name="houseType" value="${houseType}" id="houseType"/>
				<form:errors path="houseType" cssClass="validationError" />
			</p> --%>
			<p>
				<input type="hidden" name="divisionName" id="divisionName" />
				<form:errors path="divisionName" cssClass="validationError" />
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<form:hidden path="version" />
			<form:hidden path="id" />
			<form:hidden path="locale" />						
		</form:form>
	</div>
</body>
</html>