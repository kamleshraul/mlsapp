<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="party.new.title"
		text="Add Party" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {		
		initControls();
		$('#key').val('');	
		$('#addSymbol').click(function(){			
			addSymbol();
		});		
					
		$(".datemask").mask("99/99/9999");			
		
	});
</script>

<script type="text/javascript">
	var symbolCount=parseInt($('#symbolCount').val());
	var totalSymbolCount=symbolCount;	
	var tag=null;
	function populateDistrictsR(state) {
		$.get('ref/state' + state + '/districts', function(data) {
			$('#districtR option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>"
						+ data[i].name + "</option>";
			}
			$('#districtR').html(options);
			if ($('#districtR').val() != undefined) {
				alert("selected districtR = " + $('#districtR').val());
				populateTehsilsR($('#districtR').val());
			}
		});	
	}
	
	function populateDistrictsS(state) {
		$.get('ref/state' + state + '/districts', function(data) {
			$('#districtS option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>"
						+ data[i].name + "</option>";
			}
			$('#districtS').html(options);
			if ($('#districtS').val() != undefined) {
				alert("selected districtS = " + $('#districtS').val());
				populateTehsilsS($('#districtS').val());
			}
		});	
	}
	
	function populateTehsilsR(district) {
		$.get('ref/' + district + '/tehsils', function(data) {
			$('#tehsilR option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>"
						+ data[i].name + "</option>";
			}
			$('#tehsilR').html(options);
		});
	}
	
	function populateTehsilsS(district) {
		$.get('ref/' + district + '/tehsils', function(data) {
			$('#tehsilS option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>"
						+ data[i].name + "</option>";
			}
			$('#tehsilS').html(options);
		});
	}	
	
	if ($('#stateR').val() != undefined) {
		$('#stateR').change(function(){
				var state = $('#stateR').val();
				populateDistrictsR(state);				
		});
	}
	
	if ($('#stateS').val() != undefined) {
		$('#stateS').change(function(){
				var state = $('#stateS').val();
				populateDistrictsS(state);				
		});
	}
	
	if ($('#districtR').val() != undefined) {
		$('#districtR').change(function(){
				var district = $('#districtR').val();
				populateTehsilsR(district);				
		});
	}
	
	if ($('#districtS').val() != undefined) {
		$('#districtS').change(function(){
				var district = $('#districtS').val();
				populateTehsilsS(district);				
		});
	}
	
	function uploadFile(fileID) {		
		$('#file_symbol'+fileID).fileupload({url: 'fileupload?authusername='+$('#authusername').val(), formData:null,success:function(data){	
			tag = data;			
			var text="<div id='symbol"+fileID+"'>"+
	  		 		 "<p>"+
	  		 		 "<label class='small' ><spring:message text='Symbol "+fileID+"'/></label>"+
	  		 		 "<span id='file_symbol" + fileID + "_download' style='display: inline; margin: 0px; padding: 0px;'>"+
	  		 			"<a id='file_symbol" + fileID + "_link' href='file/" + data + "'>"+data+"</a>"+
	  		 			"<input type=hidden id='symbol" + fileID + "' name='symbol" + fileID + "' value='" + data + "'/>"+
	  		 			"<button id='file_symbol" + fileID + "_remove' class='butDef' type='button'>"+
	  		 				"<spring:message code='generic.remove' text='Remove' />"+
	  		 			"</button>"+
	  		 		 "</span>"+
			  		 "</p>"+
			  		 "<p>"+
					 	"<label class='small'><spring:message code='party.symbolChangeDate' text='Date of Change' />&nbsp;*</label>"+
					 	"<input type='text' id='changeDate"+fileID+"' class='datemask sText' name='changeDate" + fileID +"' />"+									
				 	 "</p>"+
			  		 "<input type='button' class='button' id='symbol"+fileID+"_delete' value='"+$('#deleteSymbolMessage').val()+"' onclick='deleteSymbol("+fileID+");'>"+
		 			 "</div>";			
			$('#symbol' + fileID).html(text);
			
			$('#file_symbol'+fileID+'_remove').click(function(){
				removeFile(tag,fileID);
			});
			
			$('.datemask').focus(function(){
				if($(this).val()==""){
					$(".datemask").mask("99/99/9999");
				}
			});
			//alert("uploaded successfully " + $(data).contents());
		}
		});
	};
	
	function removeFile(tag, fileID) {			
		$.delete_('file/remove/'+tag,function(data){
			$('#image_symbol'+fileID).attr("src","");					
			if(data){
				$.get('./common/file_upload.jsp?fileid=symbol'+fileID,function(data){
					var text="<div id='symbol"+fileID+"'>"+
			  		 "<p>"+
			  		 "<label class='small' ><spring:message text='Upload Symbol'/></label>" +
					 "<span id='file_symbol" + fileID + "_upload' style='display: inline; margin: 0px; padding: 0px;'>" +
					 "<input id='file_symbol" + fileID + "' type='file' class='sText' onclick='uploadFile("+fileID+");'/>" +
					 "<input type='hidden' id='symbol" + fileID + "' name='symbol" + fileID + "' value=''/>" +
					 "<span id='file_symbol_progress' style='display: none;'>File uploading. Please wait...</span>" +
					 "</span>" +				 
			  		 "</p>"+		  		 
			  		 "<input type='button' class='button' id='symbol"+fileID+"_delete' value='"+$('#deleteSymbolMessage').val()+"' onclick='deleteSymbol("+fileID+");'>"+
		 			 "</div>";						
					$('#symbol'+fileID).replaceWith(text);
				});
			};
		});		
	}
	
	function addSymbol(){			
		symbolCount=symbolCount+1;
		totalSymbolCount=totalSymbolCount+1;		
		// var fileID = "file_symbol"+symbolCount;		
		var text="<div id='symbol"+symbolCount+"'>"+
		  		 "<p>"+
		  		 "<label class='small' ><spring:message text='Upload Symbol'/></label>" +
				 "<span id='file_symbol" + symbolCount + "_upload' style='display: inline; margin: 0px; padding: 0px;'>" +
				 "<input id='file_symbol" + symbolCount + "' type='file' class='sText' onclick='uploadFile("+symbolCount+");'/>" +
				 "<input type='hidden' id='symbol" + symbolCount + "' name='symbol" + symbolCount + "' value=''/>" +
				 "<span id='file_symbol_progress' style='display: none;'>File uploading. Please wait...</span>" +
				 "</span>" +				 
		  		 "</p>"+		  		 
		  		 "<input type='button' class='button' id='symbol"+symbolCount+"_delete' value='"+$('#deleteSymbolMessage').val()+"' onclick='deleteSymbol("+symbolCount+");'>"+
		  		 "<input type='hidden' id='partySymbolId"+symbolCount+"' name='partySymbolId"+symbolCount+"'>"+
				 "<input type='hidden' id='partySymbolLocale"+symbolCount+"' name='partySymbolLocale"+symbolCount+"' value='"+$('#locale').val()+"'>"+
				 "<input type='hidden' id='partySymbolVersion"+symbolCount+"' name='partySymbolVersion"+symbolCount+"'>"+
	 			 "</div>"; 	
	 	
		var prevCount=symbolCount-1;
		if(totalSymbolCount==1){
			$('#addSymbol').after(text);			
			//uploadFile(fileId);
		}
		else{
			$('#symbol'+prevCount).after(text);
			//uploadFile(fileId);
		}		
		$('#symbolCount').val(symbolCount); 
		$('#symbol'+symbolCount).focus();		
	}	
	
	function deleteSymbol(id){
		$('#symbol'+id).remove();
		totalSymbolCount=totalSymbolCount-1;		
		if(id==symbolCount){
			symbolCount=symbolCount-1;
		}
	}
	
	$('#submit').click(function() {
		if($('#isDissolved').is(':checked'))
	   	{
			$('#isDissolved').val(true);		   	    
		}
		else
	   	{ 				
			$('#isDissolved').val(false);				
	   	};	   	
	});	
</script>
</head>
<body>
	<div class="fields clearfix">
		<form:form action="${urlPattern}" method="PUT"
			modelAttribute="party">
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
			<div>
			<p>
				<label class="small"><spring:message
						code="party.name" text="Name" />&nbsp;*</label>
				<form:input cssClass="sText" path="name" />
				<form:errors path="name" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="party.shortName" text="Short Name" />&nbsp;*</label>
				<form:input cssClass="sText" path="shortName" />
				<form:errors path="shortName" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="party.establishmentDate" text="Establishment Date" />&nbsp;*</label>
				<form:input cssClass="datemask sText" path="establishmentDate" />
				<form:errors path="establishmentDate" cssClass="validationError" />
			</p>
			<div>
					<input type="button" class="button" id="addSymbol"
						value="<spring:message code='party.addSymbol' text='Add Symbols'></spring:message>">
					<input type="hidden" id="symbolCount" name="symbolCount"
						value="${symbolCount}" /> 
					<input type="hidden"
						id="deleteSymbolMessage" name="deleteSymbolMessage"
						value="<spring:message code='party.deleteSymbol' text='Delete Symbol'></spring:message>"
						disabled="disabled" />					
					<%-- <c:if test="${!(empty partySymbols[0].symbol)}"> --%>
						<c:set var="count" value="0"></c:set>
						<c:forEach items="${party.partySymbols}" var="partySymbol">
							<c:set var="count" value="${count+1}"></c:set>
							<div id="symbol${count}">
								<p>
									<label class="small"><spring:message
											text="Symbol ${count}" /></label>
									<jsp:include page="/common/file_download.jsp">
										<jsp:param name="fileid" value="symbol${count}" />
										<jsp:param name="filetag" value="${partySymbol.symbol}" />
									</jsp:include>
								</p>
								<p>
									<label class="small"><spring:message code='party.symbolChangeDate' text="Change Date" />&nbsp;*</label>
									<input class="datemask sText" name="changeDate${count}" value="${partySymbol.changeDate}"/>
								</p>
								<input type='button' class='button' id='${count}'
									value='<spring:message code="party.deleteSymbol" text="Delete Symbol"></spring:message>'
									onclick='deleteSymbol(${count});' />		
								<input type='hidden' id='partySymbolId${count}' name='partySymbolId${count}' value="${partySymbol.id}">
								<input type='hidden' id='partySymbolVersion${count}' name='partySymbolVersion${count}' value="${partySymbol.version}">
								<input type='hidden' id='partySymbolLocale${count}' name='partySymbolLocale${count}' value="${party.locale}">						
							</div>
						</c:forEach>
						<form:errors path="partySymbols" cssClass="validationError"></form:errors>
						<%-- <input type="hidden" id="startCount" name="symbolCount" value="${count}" /> --%>
					<%-- </c:if> --%>
				</div>								
			</div>			
			<div class="fields clearfix">
				<h3><spring:message	code="party.registeredOfficeAddress" text="Registered Office Address:" /></h3>
				<p>
					<label class="small"><spring:message
							code="party.state" text="State" /></label>
					<form:select path="registeredOfficeAddress.state" items="${statesR}" itemValue="id" itemLabel="name" id="stateR" cssClass="sSelect state registeredOfficeAddressState"></form:select>
					<form:errors path="registeredOfficeAddress.state" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.district" text="District" /></label>
					<form:select path="registeredOfficeAddress.district"
						items="${districtsR}" itemValue="id" itemLabel="name"
						id="districtR" cssClass="sSelect"></form:select>
					<form:errors path="registeredOfficeAddress.district"
						cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.tehsil" text="Tehsil" /></label>
					<form:select path="registeredOfficeAddress.tehsil"
						items="${tehsilsR}" itemValue="id" itemLabel="name" id="tehsilR"
						cssClass="sSelect"></form:select>
					<form:errors path="registeredOfficeAddress.tehsil"
						cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.city" text="City" /></label>
					<form:input cssClass="sText" path="registeredOfficeAddress.city" />
					<form:errors path="registeredOfficeAddress.city" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.details" text="Details" /></label>
					<form:textarea cssClass="wysiwyg sTextarea" path="registeredOfficeAddress.details"
					rows="3" cols="20" />
					<form:errors path="registeredOfficeAddress.details" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.pincode" text="Pincode" /></label>
					<form:input cssClass="sText" path="registeredOfficeAddress.pincode" />
					<form:errors path="registeredOfficeAddress.pincode" cssClass="validationError" />
				</p>
			</div>			
			
			<div class="fields clearfix">
				<h3><spring:message	code="party.stateOfficeAddress" text="State Office Address:" /></h3>
				<p>
						<label class="small"><spring:message
								code="party.state" text="State" /></label>
						<form:select path="stateOfficeAddress.state" items="${statesS}" itemValue="id"
							itemLabel="name" id="stateS" cssClass="sSelect"></form:select>
						<form:errors path="stateOfficeAddress.state" cssClass="validationError" />
				</p>
				<p>
						<label class="small"><spring:message
								code="party.district" text="District" /></label>
						<form:select path="stateOfficeAddress.district" items="${districtsS}" itemValue="id"
							itemLabel="name" id="districtS" cssClass="sSelect"></form:select>
						<form:errors path="stateOfficeAddress.district" cssClass="validationError" />
				</p>
				<p>
						<label class="small"><spring:message
								code="party.tehsil" text="Tehsil" /></label>
						<form:select path="stateOfficeAddress.tehsil" items="${tehsilsS}" itemValue="id"
							itemLabel="name" id="tehsilS" cssClass="sSelect"></form:select>
						<form:errors path="stateOfficeAddress.tehsil" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.city" text="City" /></label>
					<form:input cssClass="sText" path="stateOfficeAddress.city" />
					<form:errors path="stateOfficeAddress.city" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.details" text="Details" /></label>
					<form:textarea cssClass="wysiwyg sTextarea" path="stateOfficeAddress.details"
					rows="3" cols="20" />					
					<form:errors path="stateOfficeAddress.details" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.pincode" text="Pincode" /></label>
					<form:input cssClass="sText" path="stateOfficeAddress.pincode" />
					<form:errors path="stateOfficeAddress.pincode" cssClass="validationError" />
				</p>
			</div>
			
			<div class="fields clearfix">
				<h3><spring:message	code="party.contactDetails" text="Contact Details:" /></h3>
				<p>
					<label class="small"><spring:message
							code="party.email1" text="Email 1" /></label>
					<form:input cssClass="sText" path="contact.email1" />
					<form:errors path="contact.email1" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.email2" text="Email 2" /></label>
					<form:input cssClass="sText" path="contact.email2" />
					<form:errors path="contact.email2" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.website1" text="Website 1" /></label>
					<form:input cssClass="sText" path="contact.website1" />
					<form:errors path="contact.website1" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.website2" text="Website 2" /></label>
					<form:input cssClass="sText" path="contact.website2" />
					<form:errors path="contact.website2" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.telephone1" text="Telephone 1" /></label>
					<form:input cssClass="sText" path="contact.telephone1" />
					<form:errors path="contact.telephone1" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.telephone2" text="Telephone 2" /></label>
					<form:input cssClass="sText" path="contact.telephone2" />
					<form:errors path="contact.telephone2" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.fax1" text="Fax 1" /></label>
					<form:input cssClass="sText" path="contact.fax1" />
					<form:errors path="contact.fax1" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.fax2" text="Fax 2" /></label>
					<form:input cssClass="sText" path="contact.fax2" />
					<form:errors path="contact.fax2" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.mobile1" text="Mobile 1" /></label>
					<form:input cssClass="sText" path="contact.mobile1" />
					<form:errors path="contact.mobile1" cssClass="validationError" />
				</p>
				<p>
					<label class="small"><spring:message
							code="party.mobile2" text="Mobile 2" /></label>
					<form:input cssClass="sText" path="contact.mobile2" />
					<form:errors path="contact.mobile2" cssClass="validationError" />
				</p>
			</div>

			<div class="fields clearfix">
				<p>
					<label class="small"><spring:message
							code="party.dissolved" text="Dissolved?" /></label>
					<form:checkbox cssClass="sCheck" id="isDissolved"
						path="isDissolved" />
					<form:errors path="isDissolved" cssClass="validationError" />
				</p>
			</div>						
			<!-- <input type="hidden" id="fileid" value="fileid">
			<input type="hidden" id="symbol" value="symbol"> -->
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
			<form:hidden path="contact.version"/>
			<form:hidden path="contact.id"/>
			<form:hidden path="contact.locale" />   	
			<form:hidden path="registeredOfficeAddress.version"/>
			<form:hidden path="registeredOfficeAddress.id"/>
			<form:hidden path="registeredOfficeAddress.locale"/>
			<form:hidden path="stateOfficeAddress.version"/>
			<form:hidden path="stateOfficeAddress.id"/>
			<form:hidden path="stateOfficeAddress.locale"/>		
			<%-- <c:forEach items="${party.partySymbols }" varStatus="counter">
				<form:hidden path="partySymbols[${counter.count-1}].id"/>
			</c:forEach> --%>
		</form:form>	
	</div>	
</body>

</html>