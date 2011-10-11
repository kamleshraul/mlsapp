<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form:form cssClass="wufoo" action="member_personal_details" method="POST" modelAttribute="memberPersonalDetails">
	<div class="info">
		 <h2><spring:message code="member_personal_details.new.heading"/></h2>		
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul id="myList">				
		<li>
		<label class="desc" style="display: "><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select medium" path="locale"> 
					<form:option value="en"><spring:message code="generic.lang.english"/></form:option>
					<form:option value="hi_IN"><spring:message code="generic.lang.hindi"/></form:option>
					<form:option value="mr_IN"><spring:message code="generic.lang.marathi"/></form:option>
				</form:select>
			</div>
		</li>
			
		 <li id="${fields.photoField.position}" class="${fields.photoField.visible}">	
		 	 <label><spring:message code="member_personal_details.photo.label"/>&nbsp;<c:if test="${fields.photoField.mandatory=='MANDATORY'}">*</c:if></label>	
		 	 <div class="hideDiv" id="photoDiv">
		     <img width="80" height="90" id="photoDisplay"/>
		     </div>
		     <div>
			 <input id="photo" readonly="readonly" type="text">
			 <button id="photoRemove" class="btTxt" type="button"><spring:message code="generic.remove"/></button>
			 </div>
			 <form:hidden path="photo" id="photoField" cssClass="${fields.photoField.mandatory}"></form:hidden>
			 <form:errors path="photo" cssClass="field_error" />
		 </li>
		 	
		<li  class="name ${fields.title.visible}" id="${fields.title.position}">
		<label class="desc"><spring:message code="member_personal_details.name"/>&nbsp;<c:if test="${fields.title.mandatory=='MANDATORY'}">*</c:if></label>		
		<span>
		<label><spring:message code="member_personal_details.title"/></label>
		<form:select cssClass="field select ${fields.title.mandatory}" path="title" items="${titles}" itemValue="id" itemLabel="name"/><form:errors path="title" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.firstName"/></label>
		<form:input cssClass="field text ${fields.firstName.mandatory}" path="firstName" size="25" /><form:errors path="firstName" cssClass="field_error" id="firstNameError" /><span id="firstNameError"></span>
		</span>
		<span>
		<label><spring:message code="member_personal_details.middleName"/></label>
		<form:input cssClass="field text ${fields.middleName.mandatory}" path="middleName" size="25" /><form:errors path="middleName" cssClass="field_error" /><span id="middleNameError"></span>	
		</span>
		<span>
		<label><spring:message code="member_personal_details.lastName"/></label>
		<form:input cssClass="field text ${fields.lastName.mandatory}" path="lastName" size="25"/><form:errors path="lastName" cssClass="field_error" /><span id="lastNameError"></span>	
		</span>
		</li>
		
		<li id="${fields.gender.position}" class="${fields.gender.visible}">
		<label class="desc"><spring:message code="member_personal_details.gender"/>&nbsp;<c:if test="${fields.gender.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:select path="gender" cssClass="field select medium ${fields.partyName.mandatory}">
				<form:option value="male">Male</form:option>
				<form:option value="male">Female</form:option>				
	            </form:select><form:errors path="gender" cssClass="field_error"/>		
		   </div>
		</li>
		
		<li id="${fields.constituencies.position}" class="${fields.constituencies.visible}">
		<label class="desc"><spring:message code="member_personal_details.constituency"/>&nbsp;<c:if test="${fields.constituencies.mandatory=='MANDATORY'}">*</c:if></label>
		<div  id="constituencies" url="ref/constituencies" ></div>
		</li>	
			
		<li id="${fields.district.position}" class="${fields.district.visible}">
		<div>
		<span class="left districts">
		<label class="desc"><spring:message code="member_personal_details.district"/>&nbsp;<c:if test="${fields.district.mandatory=='MANDATORY'}">*</c:if></label>
		<input name="district" id="district" class="field text medium ${fields.district.mandatory}" type="text" value="${district}">
		</span>	
		<span class="right states">
		<label class="desc"><spring:message code="member_personal_details.state"/>&nbsp;<c:if test="${fields.state.mandatory=='MANDATORY'}">*</c:if></label>
		<input type="text" name="state" id="state" class="field text medium ${fields.district.mandatory}" value="${state}">
		</span>						
		</div>
		</li>
		
		<li id="${fields.partyName.position}" class="${fields.partyName.visible}">
		<label class="desc"><spring:message code="member_personal_details.party"/>&nbsp;<c:if test="${fields.partyName.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:select path="partyName" items="${parties}" itemValue="id" itemLabel="name" cssClass="field select medium ${fields.partyName.mandatory}">
	            </form:select><form:errors path="partyName" cssClass="field_error" />		
		   </div>
		</li>
		
		<li class="complex ${fields.fatherName.visible}" id="${fields.fatherName.position}">
		<div>
		<span class="left">
		<label class="desc"><spring:message code="member_personal_details.fatherName"/>&nbsp;<c:if test="${fields.fatherName.mandatory=='MANDATORY'}">*</c:if></label>
		<form:input cssClass="field text ${fields.fatherName.mandatory}" path="fatherName"/><form:errors path="fatherName" cssClass="field_error" />	
		</span>
		<span class="right">
		<label class="desc"><spring:message code="member_personal_details.motherName"/>&nbsp;<c:if test="${fields.motherName.mandatory=='MANDATORY'}">*</c:if></label>
		<form:input cssClass="field text ${fields.motherName.mandatory}" path="motherName"/><form:errors path="motherName" cssClass="field_error" />	
		</span>
		</div>
		</li>
		
		<li id="${fields.birthDate.position}" class="${fields.birthDate.visible}">	
		<label class="desc"><spring:message code="member_personal_details.birthDate"/>&nbsp;<c:if test="${fields.birthDate.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:input cssClass="date ${fields.birthDate.mandatory}" path="birthDate"/><form:errors path="birthDate" cssClass="field_error" />	
			</div>
		</li>
		
		<li id="${fields.maritalStatus.position}" class="${fields.maritalStatus.visible}">
		<label class="desc"><spring:message code="member_personal_details.maritalStatus"/>&nbsp;<c:if test="${fields.maritalStatus.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:checkbox cssClass="checkbox ${fields.maritalStatus.mandatory}" path="maritalStatus" value="true" id="maritalStatus"/><form:errors path="maritalStatus" cssClass="field_error" />	
			</div>
		</li>
		
		
		<li class="date ${fields.marriageDate.visible}" id="${fields.marriageDate.position}">
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.marriageDate"/>&nbsp;<c:if test="${fields.marriageDate.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:input cssClass="date ${fields.marriageDate.mandatory}" path="marriageDate"/><form:errors path="marriageDate" cssClass="field_error" />	
			</div>
		</div>
		</li>
		
		<li id="${fields.spouseName.position}" class="${fields.spouseName.visible}">
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.spouseName"/>&nbsp;<c:if test="${fields.spouseName.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:input cssClass="field text medium ${fields.spouseName.mandatory}" path="spouseName" size="200"/><form:errors path="spouseName" cssClass="field_error" />	
			</div>
		</div>
		</li>
		
		<li id="${fields.noOfSons.position}" class="${fields.noOfSons.visible}">
		<div class="marriage">		
		<label class="desc"><spring:message code="member_personal_details.noOfSons"/>&nbsp;<c:if test="${fields.noOfSons.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:input cssClass="integer field text medium ${fields.noOfSons.mandatory}" path="noOfSons" size="200"/><form:errors path="noOfSons" cssClass="field_error" />	
			</div>
		</div>		
		</li>
		
		<li id="${fields.noOfDaughter.position}" class="${fields.noOfDaughter.visible}">
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.noOfDaughter"/>&nbsp;<c:if test="${fields.noOfDaughter.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:input cssClass="integer field text medium ${fields.noOfDaughter.mandatory}" path="noOfDaughter" size="200"/><form:errors path="noOfDaughter" cssClass="field_error" />	
			</div>	
		</div>						
		</li>			
			
		<li id="${fields.educationalQualification.position}" class="${fields.educationalQualification.visible}">
		<label class="desc"><spring:message code="member_personal_details.educationalQualification"/>&nbsp;<c:if test="${fields.educationalQualification.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:textarea cssClass="field textarea medium ${fields.educationalQualification.mandatory}" path="educationalQualification" rows="5" cols="50"/><form:errors path="educationalQualification" cssClass="field_error" />	
			</div>
		</li>
		
		<li id="${fields.profession.position}" class="${fields.profession.visible}">
		<label class="desc"><spring:message code="member_personal_details.profession"/>&nbsp;<c:if test="${fields.profession.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
				<form:textarea cssClass="field textarea medium ${fields.profession.mandatory}" path="profession" rows="5" cols="50"/><form:errors path="profession" cssClass="field_error" />	
			</div>
		</li>
		
		<!-- <li class="buttons"> -->
		 <!-- </li> -->		
	</ul>
	<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>		
	<input type="hidden" id="const_name" value="${constituency.name}">
	<input type="hidden" id="const_id" value="${constituency.id}">	
	<input type="hidden" id="photo_size" value="${photoSize}">	
	<input type="hidden" id="photo_ext" value="${photoExt}">			
			
</form:form>
</body>
<head>
	<title>
	<spring:message code="member_information_system"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$('.marriage').hide();	
		$('#maritalStatus').change(function(){
		if($('#maritalStatus').attr("checked")=="checked"){
			$('.marriage').show();
		}
		else{
			$('.marriage').hide();			
		}
		});			
		var constFlexBox=$('#constituencies').flexbox($('#constituencies').attr('url'), {  
			paging: false,  
			maxVisibleRows: 20,
			onSelect:autosuggest_onchange
		});
		if($('#const_name').val()!=undefined&&$('#const_id').val()!=undefined){
			constFlexBox.setValue($('#const_name').val());	
			$('.states').show();
			$('.districts').show();		
		}
		if($('#const_name').val()!=""&&$('#const_id').val()!=""){
			constFlexBox.setValue($('#const_name').val());	
			$('.states').show();
			$('.districts').show();		
		}
		else{
			$('.states').hide();
			$('.districts').hide();
		}			
		function autosuggest_onchange(){
			var constituencyName=$('input[name=constituencies]').val();
			$.ajax({
				url:'ref/data/'+constituencyName+'/districts',
				datatype:'json',
				success:function(data){
					if(data.length>=1){
						var districts=data[0].name;
						for(var i=1;i<data.length;i++){
							if(data[i].name!=undefined){
								districts=districts+","+data[i].name;
							}
						}
						$('#district').val(districts);
						$('.districts').show();	
						$('#state').val(data[0].state.name);
						$('.states').show();
					}				
			}							
			});
		}
			/*code for uploading photo*/		
			if($('#photo').val()==''){
			uploadify('#photo',$('#photo_ext').val(),$('#photo_size').val());
	   		}

	   		if(($('#photo').val()!='') && ($('#photo').val()!=undefined))
	   		{
	   		$('#photoDisplay').attr('src','/els/file/photo/'+$('#photoField').val());
		   	   $('#photoDiv').removeClass('hideDiv').addClass('showDiv');
	   		}
			$('#photoRemove').click(function(){
				$.ajax({
				    type: "DELETE",
				    url: "file/remove/"+$('#photoField').val(),
				    contentType: "application/json; charset=utf-8",
				    dataType: "json",
				    success: function(json) {
				        if(json==true){
				        	$('#photo').val('');
				        	uploadify('#photo',$('#photo_ext').val(),$('#photo_size').val());
	   				        alert('File successfully deleted');
				        }
				    },
				    error: function (xhr, textStatus, errorThrown) {
				    	alert(xhr.responseText);
				    }
				});
			   $('#photoDisplay').attr('src','');
		   	   $('#photoDiv').removeClass('showDiv').addClass('hideDiv');
			});
			/************/
		
		
		$('li').sortElements(function(a, b){
			if($(a).attr("id")!=undefined&&$(b).attr("id")!=undefined){
   			 return parseInt($(a).attr("id")) > parseInt($(b).attr("id")) ? 1 : -1;
			}
		});

		//function for making fields visible/invisible
		$('.HIDDEN').each(function(){
				$(this).hide();
		})
		
				 		
		
</script>
</head>
</html>