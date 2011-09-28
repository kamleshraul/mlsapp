<%@ include file="/common/taglibs.jsp" %>
<html>
<body>	
<form:form cssClass="wufoo" action="member_contact_details" method="PUT" modelAttribute="memberContactDetails">
	<div class="info">
		 <h2><spring:message code="member_contact_details.edit.heading"/></h2>		
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	
	<ul>
	
		<li>
		<label class="desc"><spring:message code="generic.id"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="id" readonly="true" /> 
			</div>
		</li>	
						
		<li>
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select medium" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_contact_details.email"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="email"/><form:errors path="email" cssClass="field_error" />	
			</div>
		</li>
				
		<li class="complex">
		<label class="desc"><spring:message code="member_contact_details.present.address"/>&nbsp;*</label>
		<div>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.address"/></label>
		<form:input cssClass="field text addr" path="presentAddress"/><form:errors path="presentAddress" cssClass="field_error" />	
		</span>
		<span class="left city">
		<label><spring:message code="member_contact_details.city"/></label>
		<form:input cssClass="field text addr" path="presentCity"/><form:errors path="presentAddress" cssClass="field_error" />	
		</span>
		</div>
		</li>
		
		<li class="leftThird">
		<label><spring:message code="member_personal_details.state"/></label>
		<div>
		<form:select path="presentState" items="${presentStates}" itemValue="id" itemLabel="name" id="presentStates" cssClass="field select medium">
	    </form:select><form:errors path="presentState" cssClass="field_error" />				
		</div>
		</li>
		
		<li class="middleThird">
		<div id="presentDistrict">
		<label><spring:message code="member_personal_details.district"/></label>
		<form:select path="presentDistrict" items="${presentDistricts}" itemValue="id" itemLabel="name" id="presentDistricts" cssClass="field select medium">
	    </form:select><form:errors path="presentDistrict" cssClass="field_error" />				
		</div>
		</li>
		
		<li class="rightThird">
		<div id="presentTehsil">
		<label><spring:message code="member_personal_details.tehsil"/></label>
		<form:select path="presentTehsil" items="${presentTehsils}" itemValue="id" itemLabel="name" id="presentTehsils" cssClass="field select medium">
	    </form:select><form:errors path="presentTehsil" cssClass="field_error" />							
		</div>		
		</li>
		
		<li class="complex">
		<div>
		<span class="left pin">
		<label ><spring:message code="member_personal_details.pincode"/></label>
		<form:input cssClass="field text addr" path="presentPinCode" size="6"/><form:errors path="presentPinCode" cssClass="field_error" />	
		</span>	
		<span class="full addr1">
		<label><spring:message code="member_contact_details.telephone"/></label>
		<form:input cssClass="field text addr" path="presentTelephone"/><form:errors path="presentTelephone" cssClass="field_error" />	
		</span>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.fax"/></label>
		<form:input cssClass="field text addr" path="presentFax"/><form:errors path="presentFax" cssClass="field_error" />	
		</span>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.mobile"/></label>
		<form:input cssClass="field text addr" path="presentMobile"/><form:errors path="presentMobile" cssClass="field_error" />	
		</span>	
		</div>
		</li>	
			
		<li class="complex">
		<label class="desc"><spring:message code="member_contact_details.permanent.address"/>&nbsp;*</label>
		<div>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.address"/></label>
		<form:input cssClass="field text addr" path="permanentAddress"/><form:errors path="permanentAddress" cssClass="field_error" />	
		</span>
		<span class="left city">
		<label><spring:message code="member_contact_details.city"/></label>
		<form:input cssClass="field text addr" path="permanentCity"/><form:errors path="permanentAddress" cssClass="field_error" />	
		</span>
		</div>
		</li>
		
		<li class="leftThird">
		<label><spring:message code="member_personal_details.state"/></label>
		<div>
		<form:select path="permanentState" items="${permanentStates}" itemValue="id" itemLabel="name" id="permanentStates" cssClass="field select medium">
	    </form:select><form:errors path="permanentState" cssClass="field_error" />				
		</div>
		</li>
		
		<li class="middleThird">
		<div id="permanentDistrict">
		<label><spring:message code="member_personal_details.district"/></label>
		<form:select path="permanentDistrict" items="${permanentDistricts}" itemValue="id" itemLabel="name" id="permanentDistricts" cssClass="field select medium">
	    </form:select><form:errors path="permanentDistrict" cssClass="field_error" />				
		</div>
		</li>
		
		<li class="rightThird">
		<div id="permanentTehsil">
		<label><spring:message code="member_personal_details.tehsil"/></label>
		<form:select path="permanentTehsil" items="${permanentTehsils}" itemValue="id" itemLabel="name" id="permanentTehsils" cssClass="field select medium">
	    </form:select><form:errors path="permanentTehsil" cssClass="field_error" />							
		</div>
		</li>
		
		<li class="complex">
		<div>
		<span class="left pin">
		<label ><spring:message code="member_personal_details.pincode"/></label>
		<form:input cssClass="field text addr" path="permanentPinCode" size="6"/><form:errors path="permanentPinCode" cssClass="field_error" />	
		</span>	
		<span class="full addr1">
		<label><spring:message code="member_contact_details.telephone"/></label>
		<form:input cssClass="field text addr" path="permanentTelephone"/><form:errors path="permanentTelephone" cssClass="field_error" />	
		</span>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.fax"/></label>
		<form:input cssClass="field text addr" path="permanentFax"/><form:errors path="permanentFax" cssClass="field_error" />	
		</span>
		<span class="full addr1">
		<label><spring:message code="member_contact_details.mobile"/></label>
		<form:input cssClass="field text addr" path="permanentMobile"/><form:errors path="permanentMobile" cssClass="field_error" />	
		</span>	
		</div>
		</li>	
		
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>	
		
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
<head>
	<title>
		<spring:message code="member_information_system"/>	
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">		
		//Related to state ,ditricts and tehsils	
		if($('#presentStates').val()!=undefined){
			loadDistrictsByStateId($('#presentStates').val(),"present");			
		}
		if($('#permanentStates').val()!=undefined){
			loadDistrictsByStateId($('#permanentStates').val(),"permanent");			
		}
		$('#presentStates').change(function(){
			loadDistrictsByStateId($('#presentStates').val(),"present");				
		});	
		$('#presentDistricts').change(function(){
			loadTehsilsByDistrictId($('#presentDistricts').val(),"present");				
		});	
		$('#permanentStates').change(function(){
			loadDistrictsByStateId($('#permanentStates').val(),"permanent");				
		});	
		$('#permanentDistricts').change(function(){
			loadTehsilsByDistrictId($('#permanentDistricts').val(),"permanent");				
		});
		function loadDistrictsByStateId(id,type){
			$.ajax({
				url:'ref/'+id+'/districts',
				datatype:'json',
				success:function(data){
					$('#'+type+'Districts option').remove();
					if(data.length>=1){
						for(var i=0;i<data.length;i++){
							$('#'+type+'Districts').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
						}
						loadTehsilsByDistrictId(data[0].id,type);							
					}else{
						$('#'+type+'tehsils option').remove();
					}					
			}							
			});
		}

		function loadTehsilsByDistrictId(id,type){
			$.ajax({
				url:'ref/'+id+'/tehsils',
				datatype:'json',
				success:function(data){
				$('#'+type+'Tehsils option').remove();
					for(var i=0;i<data.length;i++){
					$('#'+type+'Tehsils').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
				}
			}							
			});
		}

		//function for sorting fields according to their positions
		//important thing is all fields to be sorted must be placed inside a li 
		
		$('li').sortElements(function(a, b){
			if($(a).attr("id")!=undefined&&$(b).attr("id")!=undefined){
   			 return parseInt($(a).attr("id")) > parseInt($(b).attr("id")) ? 1 : -1;
			}
		});
	   
	</script>	
</head>
</html>