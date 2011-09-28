<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form:form cssClass="wufoo" action="member_personal_details" method="PUT" modelAttribute="memberPersonalDetails">
	<div class="info">
		 <h2><spring:message code="member_personal_details.new.heading"/></h2>		
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
				
		<li  class="name">
		<label class="desc"><spring:message code="member_personal_details.name"/>&nbsp;*</label>		
		<span>
		<label><spring:message code="member_personal_details.title"/></label>
		<form:select cssClass="field select" path="title" items="${titles}" itemValue="id" itemLabel="name"/><form:errors path="title" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.firstName"/></label>
		<form:input cssClass="field text" path="firstName" size="25"/><form:errors path="firstName" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.middleName"/></label>
		<form:input cssClass="field text" path="middleName" size="25"/><form:errors path="middleName" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.lastName"/></label>
		<form:input cssClass="field text" path="lastName" size="25"/><form:errors path="lastName" cssClass="field_error" />	
		</span>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_personal_details.constituency"/>&nbsp;*</label>
		<div  id="constituencies" url="ref/constituencies" ></div>
		</li>	
			
		<li class="complex">
		<div>
		<span class="left districts">
		<label class="desc"><spring:message code="member_personal_details.district"/></label>
		<input name="district" id="district" class="field text medium" type="text" value="${district}">
		</span>	
		<span class="right states">
		<label class="desc"><spring:message code="member_personal_details.state"/></label>
		<input type="text" name="state" id="state" class="field text medium" value="${state}">
		</span>						
		</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_personal_details.party"/>&nbsp;*</label>
			<div>
				<form:select path="partyName" items="${parties}" itemValue="id" itemLabel="name" cssClass="field select medium">
	            </form:select><form:errors path="partyName" cssClass="field_error" />		
		   </div>
		</li>
		
		<li class="name">
		<label class="desc"><spring:message code="member_personal_details.fatherName"/>&nbsp;*</label>
		<span>
		<label><spring:message code="member_personal_details.father.title"/></label>		
		<form:input cssClass="field text" path="fatherTitle" size="5"/><form:errors path="fatherTitle" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.father.name"/></label>		
		<form:input cssClass="field text" path="fatherName" size="75"/><form:errors path="fatherName" cssClass="field_error" />	
		</span>
		</li>
		
		<li class="name">
		<label class="desc"><spring:message code="member_personal_details.motherName"/>&nbsp;*</label>
		<span>
		<label><spring:message code="member_personal_details.mother.title"/></label>
		<form:input cssClass="field text" path="motherTitle" size="5"/><form:errors path="motherTitle" cssClass="field_error" />	
		</span>
		<span>
		<label><spring:message code="member_personal_details.mother.name"/></label>
		<form:input cssClass="field text" path="motherName" size="75"/><form:errors path="motherName" cssClass="field_error" />	
		</span>
		</li>
		
		<li>	
		<label class="desc"><spring:message code="member_personal_details.birthDate"/>&nbsp;*</label>
			<div>
				<form:input cssClass="date field text medium" path="birthDate"/><form:errors path="birthDate" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_personal_details.maritalStatus"/>&nbsp;*</label>
			<div>
				<form:checkbox cssClass="checkbox" path="maritalStatus" value="true" id="maritalStatus"/><form:errors path="maritalStatus" cssClass="field_error" />	
			</div>
		</li>
		
		<li class="date">
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.marriageDate"/>&nbsp;*</label>
			<div>
				<form:input cssClass="date" path="marriageDate"/><form:errors path="marriageDate" cssClass="field_error" />	
			</div>
		</div>
		</li>
		
		<li>
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.spouseName"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="spouseName" size="200"/><form:errors path="spouseName" cssClass="field_error" />	
			</div>
		</div>
		</li>
		
		<li>
		<div class="marriage">		
		<label class="desc"><spring:message code="member_personal_details.noOfSons"/>&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text medium" path="noOfSons" size="200"/><form:errors path="noOfSons" cssClass="field_error" />	
			</div>
		</div>		
		</li>
		
		<li>
		<div class="marriage">				
		<label class="desc"><spring:message code="member_personal_details.noOfDaughter"/>&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text medium" path="noOfDaughter" size="200"/><form:errors path="noOfDaughter" cssClass="field_error" />	
			</div>	
		</div>						
		</li>	
				
		<li>
		<label class="desc"><spring:message code="member_personal_details.educationalQualification"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea medium" path="educationalQualification" rows="10" cols="50"/><form:errors path="educationalQualification" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_personal_details.profession"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea medium" path="profession" rows="10" cols="50"/><form:errors path="profession" cssClass="field_error" />	
			</div>
		</li>
		
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>
		
		<form:hidden path="version"/>	
		
	</ul>	
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
		if($('#maritalStatus').attr("checked")=="checked"){
		$('#maritalDiv').show();
		}
		else{
		$('#maritalDiv').hide();			
		}
		$('#maritalStatus').change(function(){
		if($('#maritalStatus').attr("checked")=="checked"){
			$('#maritalDiv').show();
		}
		else{
			$('#maritalDiv').hide();			
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
		/***********/
		$('li').sortElements(function(a, b){
			if($(a).attr("id")!=undefined&&$(b).attr("id")!=undefined){
   			 return parseInt($(a).attr("id")) > parseInt($(b).attr("id")) ? 1 : -1;
			}
		});
		$('.HIDDEN').each(function(){
			$(this).hide();
	});
		
</script>
</head>
</html>