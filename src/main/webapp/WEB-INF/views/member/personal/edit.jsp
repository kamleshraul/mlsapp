<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style>
	#submit{
	cursor: pointer;
	}
	#cancel{
	cursor: pointer;
	}
	</style>
	<script type="text/javascript">
	//Edit---------------
	function loadDistricts(stateId){
	$.get('ref/state/'+stateId+'/districts',function(data){
		$('#birthPlaceDistrict').empty();
		if(data.length>0){
		var text="";
		for(var i=0;i<data.length;i++){
		text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
		}
		$('#birthPlaceDistrict').html(text);
		loadTehsils(data[0].id);
		}
	}).fail(function(){
		$.unblockUI();
		if($("#ErrorMsg").val()!=''){
			$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		}else{
			$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		}
		scrollTop();
	});	
	}
	function loadTehsils(districtId){
		$.get('ref/district/'+districtId+'/tehsils',function(data){
			$('#birthPlaceTehsil').empty();
			if(data.length>0){
			var text="";
			for(var i=0;i<data.length;i++){
			text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$('#birthPlaceTehsil').html(text);			
			}
		}).fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});		
	}
	
	///-------------------
	
	
	
	var spouseIndex=$("select option:selected").val();	
	var familyCount=parseInt($('#familyCount').val());
	var totalFamilyCount=0;
	totalFamilyCount=familyCount+totalFamilyCount;
	function addFamily(){
		familyCount=familyCount+1;
		totalFamilyCount=totalFamilyCount+1;
		var text="<div id='family"+familyCount+"'>"+
				  "<p>"+
	    		  "<label class='small'>"+$('#familyNameMessage').val()+"</label>"+
	    		  "<input name='familyMemberName"+familyCount+"' id='familyMemberName"+familyCount+"' class='sText'>"+
	    		  "</p>"+
				  "<p>"+
	              	  "<label class='small'>"+$('#familyRelationMessage').val()+"</label>"+
		              "<select name='familyMemberRelation"+familyCount+"' id='familyMemberRelation"+familyCount+"' class='sSelect'>"+
				      $('#relationMaster').html()+
				      "</select>"+
				      "</p>"+
				      "<input type='button' class='button' id='"+familyCount+"' value='"+$('#deleteFamilyMessage').val()+"' onclick='deleteFamily("+familyCount+");'>"+
					  "<input type='hidden' id='familyMemberId"+familyCount+"' name='familyMemberId"+familyCount+"'>"+
					  "<input type='hidden' id='familyMemberLocale"+familyCount+"' name='familyMemberLocale"+familyCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='familyMemberVersion"+familyCount+"' name='familyMemberVersion"+familyCount+"'>"+
				      "</div>"; 
				      var prevCount=familyCount-1;
				      //here is the code to add the divs
				      if(totalFamilyCount==1){
					   $('#addFamily').after(text);
					    }else{
				      $('#family'+prevCount).after(text);
				      }
				      $('#familyCount').val(familyCount); 	
				      return familyCount;				      			
	}
	function deleteFamily(id,continous){	
		var familyMemberId=$('#familyMemberId'+id).val();			
		if(familyMemberId != ''){
	    $.delete_('member/personal/family/'+familyMemberId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
	    	$('#family'+id).remove();
			totalFamilyCount=totalFamilyCount-1;
			if(id==familyCount){
				if(continous==null){
				familyCount=familyCount-1;
				}				
			}
	    }).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
		}else{
			$('#family'+id).remove();
			totalFamilyCount=totalFamilyCount-1;
			if(id==familyCount){
				if(continous==null){
				familyCount=familyCount-1;
				}
			}
		}			
	}

	var qualificationCount=parseInt($('#qualificationCount').val());
	var totalQualificationCount=0;
	totalQualificationCount=totalQualificationCount+qualificationCount;
	var totalQualificationCount=0;
	function addQualification(){
		qualificationCount=qualificationCount+1;
		totalQualificationCount=totalQualificationCount+1;
		var text="<div id='qualification"+qualificationCount+"'>"+
				 "<p>"+
        		 "<label class='small'>"+$('#qualificationDegreeMessage').val()+"</label>"+
        		 "<select name='qualificationDegree"+qualificationCount+"' id='qualificationDegree"+qualificationCount+"' class='sSelect'>"+
	      		 $('#degreeMaster').html()+
	      		 "</select>"+
	     		 "</p>"+
				  "<p>"+
	    		  "<label class='labelcentered'>"+$('#qualificationDetailsMessage').val()+"</label>"+
	    		  "<textarea name='qualificationDetail"+qualificationCount+"' id='qualificationDetail"+qualificationCount+"' class='sTextarea'></textarea>"+
	    		  "</p>"+
				  "<input type='button' class='button' id='"+qualificationCount+"' value='"+$('#deleteQualificationMessage').val()+"' onclick='deleteQualification("+qualificationCount+");'>"+
				  "<input type='hidden' id='qualificationId"+qualificationCount+"' name='qualificationId"+qualificationCount+"'>"+
				  "<input type='hidden' id='qualificationLocale"+qualificationCount+"' name='qualificationLocale"+qualificationCount+"' value='"+$('#locale').val()+"'>"+
				  "<input type='hidden' id='qualificationVersion"+qualificationCount+"' name='qualificationVersion"+qualificationCount+"'>"+
				  "</div>"; 
				      var prevCount=qualificationCount-1;
				      if(totalQualificationCount==1){
					   $('#addQualification').after(text);
					    }else{
				      $('#qualification'+prevCount).after(text);
				      }
				      $('#qualificationCount').val(qualificationCount); 				
	}
	function deleteQualification(id){
		var qualificationId=$('#qualificationId'+id).val();
		if(qualificationId != ''){			
	    $.delete_('member/personal/qualification/'+qualificationId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
	    	$('#qualification'+id).remove();
			totalQualificationCount=totalQualificationCount-1;
			if(id==qualificationCount){
				qualificationCount=qualificationCount-1;
			}
	    }).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
		}else{	
		$('#qualification'+id).remove();
		totalQualificationCount=totalQualificationCount-1;
		if(id==qualificationCount){
			qualificationCount=qualificationCount-1;
		}
		}		
	}

	function autoAddFamily(){
		var children=parseInt($("#noOfChildren").val());
		var childrenAlreadyPresent=0;
		var dcount=0;
		var scount=0;
		var daughters=parseInt($("#noOfDaughters").val());
		var sons=parseInt($("#noOfSons").val());
		var initialFamilyCount=familyCount;
		for(var i=1;i<initialFamilyCount+1;i++){
			if($('#family'+i).length>0){
				var relation=$("#familyMemberRelation"+i).val();
				var name=$("#familyMemberName"+i).val();				
				if(daughters==0 && relation==6){
					//if daughters=0 delete all daughters						
					deleteFamily(i,"continous");
					familyCount--;						
				}else if(sons==0 && relation==5){
					//if sons=0 delete all sons					
					deleteFamily(i,"continous");
					familyCount--;
				}else if(children==0 && (relation==5 || relation==6)){
					//if children=0 delete all children			
					deleteFamily(i,"continous");
					familyCount--;	
				}else if(name.length==0 && (relation==5 ||relation==6)){
					//if name is empty and relation is either son or daughter
					deleteFamily(i,"continous");
					familyCount--;
				}else if(name.length!=0 && (relation==5 ||relation==6)){
					//if name is not empty and relation is either son or daughter
					childrenAlreadyPresent++;					
				}
			}				
		}					
		for(var j=0;j<children-childrenAlreadyPresent;j++){
			var index=addFamily();
			if(daughters!=0 && dcount!=daughters){
				$("#familyMemberRelation"+index).val(6);				
				dcount++;
			}else{
				if(sons!=0 && scount!=sons){
					$("#familyMemberRelation"+index).val(5);					
					scount++;
				}	
			}				
		}	
}	
	
	//----------------------------------------------------------//
	
	 function isDate(txtDate) {
           var currVal = txtDate;
           if (currVal == '')
               return false;
           var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/; 
           var dtArray = currVal.match(rxDatePattern);

           if (dtArray == null)
               return false;
           
           dtMonth = dtArray[1];
           dtDay = dtArray[3];
           dtYear = dtArray[5];

           if (dtYear >2100 || dtYear < 1900)
        	   return false;
           if (dtMonth < 1 || dtMonth > 12)
               return false;
           else if (dtDay < 1 || dtDay > 31)
               return false;
           else if ((dtMonth == 4 || dtMonth == 6 || dtMonth == 9 || dtMonth == 11) && dtDay == 31)
               return false;
           else if (dtMonth == 2) {
               var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
               if (dtDay > 29 || (dtDay == 29 && !isleap))
                   return false;
           }
           return true;
       }
	
	 function vali()
     {
         var s = document.getElementById("birthDate").value;
        
             if (isDate(s) == false) {
            	
            	 document.getElementById("birthDate").value = '';
               
              
             } else {
            	
                 document.getElementById("birthDate").value = s;
                 
             }     
     }

	
	//----------------------------------------------------------//
	
	$(document).ready(function(){
		
		
		
		
		
		
		$('#birthPlaceState').change(function(){
			console.log($('#birthPlaceState').val())
			loadDistricts($('#birthPlaceState').val());
		});	
		
		$('#birthPlaceDistrict').change(function(){
			loadTehsils($('#birthPlaceDistrict').val());
		});	
		
		
		
		$('#relationMaster').hide();
		$('#degreeMaster').hide();
		$('#addFamily').click(function(){
			addFamily();
		});
		$('#addQualification').click(function(){
			addQualification();
		});	
		$('#key').val($('#id').val());
		//on entering sons and daughters nos. children no. should be automatically calculated
		$("#noOfDaughters").change(function(){
			var daughters=0;
			if($(this).val()!=""){
				daughters=parseInt($(this).val());
			}else{
				$(this).val("0");				
			}
			var sons=0;
			if($("#noOfSons").val()!=""){			
			sons=parseInt($("#noOfSons").val());			
			}else{
				$("#noOfSons").val("0");
			}
			var children=sons+daughters;
			$("#noOfChildren").val(children);
			//change daughter label
			if(daughters>1){
				$("#daughterLabel").html($("#daughtersMsg").val());				
			}else{
				$("#daughterLabel").html($("#daughterMsg").val());				
			}
			autoAddFamily();			
		});
		$("#noOfSons").change(function(){
			var sons=0;
			if($(this).val()!=""){
				sons=parseInt($(this).val());		
			}else{
				$(this).val("0");				
			}
			var daughters=0;
			if($("#noOfDaughters").val()!=""){			
			daughters=parseInt($("#noOfDaughters").val());
			}else{
				$("#noOfDaughters").val("0");				
			}
			var children=sons+daughters;
			$("#noOfChildren").val(children);
			//change son label
			if(sons>1){
				$("#sonLabel").html($("#sonsMsg").val());				
			}else{
				$("#sonLabel").html($("#sonMsg").val());				
			}
			autoAddFamily();
		});	
			var spouseC=0;
		$("#Spouse2").hide();
		$("#Spouse3").hide();
		$("#addSpouse").click(function(){
			$("#spouseLabel1").show();			
			spouseC++;
			if(spouseC==1){
				$("#Spouse2").show();
			}else if(spouseC==2){
				$("#Spouse3").show();
			}else{
				alert("MAX Field Limit");
			}
		});
 		$("#deleteSpouse2").click(function(){
	     	if($("#Spouse3").is(":hidden")){
	     		  spouseC--;
		          $("#Spouse2").hide();
	     	}else{
	     		 alert("Please delete the third field");
	     	}
	     
		});
 		$("#deleteSpouse3").click(function(){
			spouseC--;
			$("#Spouse3").hide();
		});
		$("#spouseName").change(function(){
			//if spousename is not empty then a family is added 
			if($(this).val().length>0){
				spouseIndex=addFamily();
				$("#familyMemberName"+spouseIndex).val($(this).val());
				if($("#gender").val()==2){
				$("#familyMemberRelation"+spouseIndex).val(3);
				}else if($("#gender").val()==3){
				$("#familyMemberRelation"+spouseIndex).val(4);
				}								
			}else{
				if($("#family"+spouseIndex).length>0){
				//console.log(spouseIndex);
				deleteFamily(spouseIndex,"continous");
				familyCount--;
				}
			}
		});	
		//setting initial value of spouseindex
		$("select[id^='familyMemberRelation']").each(function(){
			if($(this).val()==3||$(this).val()==4){
			spouseIndex=$(this).attr("id").split("familyMemberRelation")[1];
			}
		});
		//just for time being
		$("#aliasEnabled").attr("checked","checked");		
		//on document load for appropriate sons,daughters in case of validation exceptions
		//change son label
		var initialNoOfSons=parseInt($("#noOfSons").val());
		var initialNoOfDaughters=parseInt($("#noOfDaughters").val());
		if(initialNoOfSons>1){
			$("#sonLabel").empty();
			$("#sonLabel").html($("#sonsMsg").val());				
		}else{
			$("#sonLabel").empty();			
			$("#sonLabel").html($("#sonMsg").val());				
		}
		//change daughter label
		if(initialNoOfDaughters>1){
			$("#daughterLabel").empty();			
			$("#daughterLabel").text($("#daughtersMsg").val());				
		}else{
			$("#daughterLabel").empty();		
			$("#daughterLabel").text($("#daughterMsg").val());				
		}
		//set isEnabled if credential of member is enabled
		if($('#credential_enabled').val()=="true") {
			$("#isEnabled").attr("checked","checked");
			$("#isEnabled").attr("disabled","disabled");
		}	
		//disable option to set isEnabled if english names are not entered
		if($('#credential_enabled').val()!="true"
				&& ($('#firstNameEnglish').val()=='' || $('#lastNameEnglish').val()=='')) {
			$("#isEnabled").attr("disabled","disabled");
		}
		$("#isEnabled").change(function(){
			if($(this).is(":checked")) {
				if($('#firstNameEnglish').val()=='') {
					$(this).val(false);
					$(this).removeAttr("checked");
					$.prompt("Please enter First Name in English!");
					return false;
				} else if($('#lastNameEnglish').val()=='') {
					$(this).val(false);
					$(this).removeAttr("checked");
					$.prompt("Please enter Last Name in English!");
					return false;
				} else {
					$.prompt("Do you really want to activate the member?", {
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	$("#isEnabled").val(true);
				        } else {
				        	$("#isEnabled").val(false);
				        	$("#isEnabled").removeAttr("checked");
				        }
					}});
					return false;
				}
			} else {
				$(this).val(false);
			}
		});
	});//document.ready	
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<%-- <div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');"> --%>
<div class="fields clearfix watermark">
<form:form action="member/personal" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${domain.title.name} ${domain.firstName} ${domain.middleName} ${domain.lastName}
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<p>
		<label class="small"><spring:message code="member.personal.photo" text="Upload Photo"/></label>
		<span id="image_gallery" style="display: inline;margin: 0px;padding: 0px;">
		<img alt="" src="" id="image_photo" width="70" height="70">
		</span>
		<c:choose>
		<c:when test="${empty domain.photo}">		
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="photo" />
		</jsp:include>
		</c:when>
		<c:otherwise>		
		<jsp:include page="/common/file_download.jsp">
			<jsp:param name="fileid" value="photo" />
			<jsp:param name="filetag" value="${domain.photo}" />
		</jsp:include>
		</c:otherwise>
		</c:choose>		
		<form:errors path="photo" cssClass="validationError" />
	</p>
	<p style="display:none;">
		<label class="small"><spring:message code="member.personal.specimenSignature" text="Specimen Signature"/></label>
		<span id="image_gallery" style="display: inline;margin: 0px;padding: 0px;">
		<img alt="" src="" id="image_specimenSignature" width="70" height="70">
		</span>
		<c:choose>
		<c:when test="${empty domain.specimenSignature}">
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="specimenSignature" />
		</jsp:include>
		</c:when>
		<c:otherwise>
		<jsp:include page="/common/file_download.jsp">
			<jsp:param name="fileid" value="specimenSignature" />
			<jsp:param name="filetag" value="${domain.specimenSignature}" />
		</jsp:include>
		</c:otherwise>
		</c:choose>	
		<form:errors path="specimenSignature" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.title" text="Title"/></label>
		<form:select path="title" items="${titles}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="title" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.firstName" text="First Name"/></label>
		<form:input path="firstName" cssClass="sText"/>
		<label class="small" style="width: 80px;padding-left: 10px;">
			<spring:message code="member.personal.firstNameEnglish" text="English Value"/>
		</label>
		<form:input path="firstNameEnglish" cssClass="sText"/>
		<form:errors path="firstName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.middleName" text="Middle Name"/></label>
		<form:input path="middleName" cssClass="sText"/>
		<label class="small" style="width: 80px;padding-left: 10px;">
			<spring:message code="member.personal.middleNameEnglish" text="English Value"/>
		</label>
		<form:input path="middleNameEnglish" cssClass="sText"/>
		<form:errors path="middleName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.lastName" text="Last Name"/></label>
		<form:input path="lastName" cssClass="sText"/>
		<label class="small" style="width: 80px;padding-left: 10px;">
			<spring:message code="member.personal.lastNameEnglish" text="English Value"/>
		</label>
		<form:input path="lastNameEnglish" cssClass="sText"/>
		<form:errors path="lastName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.alias" text="Alias Name"/></label>
		<form:input path="alias" cssClass="sText"/>
		<label class="small" style="width: 80px;padding-left: 10px;">
			<spring:message code="member.personal.alias_english" text="English Value"/>
		</label>
		<form:input path="aliasEnglish" cssClass="sText"/>
		<form:errors path="alias" cssClass="validationError"/>
	</p>
	<p style="display: none;">
		<label class="small"><spring:message code="member.personal.aliasEnabled" text="Alias Enabled"/></label>
		<form:checkbox path="aliasEnabled" cssClass="sCheck" value="true"/>
		<form:errors path="aliasEnabled" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.birthDate" text="Birth Date"/></label>
		<form:input id="birthDate" name="birthDate" path="birthDate" onchange = "vali()" cssClass="datemask sText"/>
		<input type="checkbox" id="isEnabled" name="isEnabled" value="false" class="sCheck" style="margin-left: 94px;"/>
		<label class="small" style="padding-left: 5px;"><spring:message code="member.personal.isEnabled" text="Is Enabled?"/></label>
		<form:errors path="birthDate" cssClass="validationError"/>
	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.personal.birthPlace" text="Birth Place"/></label>
		<form:input path="birthPlace" cssClass="sText"/>
		<form:errors path="birthPlace" cssClass="validationError"/>	
	</p>	
	<!-- Edit ---------------------------- -->
<hr>
	<fieldset>
		<legend> 
		<spring:message code="member.personal.birthPlaceAddress" text="Birth Place Address"/>
	
		</legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="birthPlaceAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="birthPlaceAddress.details" cssClass="validationError"/>
		
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select id="birthPlaceState" path="birthPlaceAddress.state" items="${states}" itemValue="id" itemLabel="name"   cssClass="sSelect" />
			<form:errors path="birthPlaceAddress.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select id="birthPlaceDistrict" path="birthPlaceAddress.district" items="${districts}" itemValue="id" itemLabel="name"  cssClass="sSelect" />
			<form:errors path="birthPlaceAddress.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select id="birthPlaceTehsil" path="birthPlaceAddress.tehsil" items="${tehsils1}" itemValue="id" itemLabel="name"  cssClass="sSelect" />
<%-- 			<form:errors path="birthPlaceAddress.tehsil" cssClass="validationError"/>
 --%>		</p>
		
	
	</fieldset>
	
	
<hr>
	<!--  ---------------------------- -->	
	<p>
		<label class="small"><spring:message code="member.personal.nationality" text="Nationality"/></label>
		<form:select path="nationality" items="${nationalities}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="nationality" cssClass="validationError"/>
	</p>

	<p>
		<label class="small"><spring:message code="member.personal.gender" text="Gender"/></label>
		<form:select path="gender" items="${genders}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="gender" cssClass="validationError"/>
	</p>
	<div>
	<input type="button" class="button" id="addQualification" value="<spring:message code='member.personal.addQualification' text='Add Qualification'></spring:message>">
	<input type="hidden" id="qualificationCount" name="qualificationCount" value="${qualificationCount}"/>
	
	<input type="hidden" id="deleteQualificationMessage" name="deleteQualificationMessage" value="<spring:message code='member.personal.deleteQualification' text='Delete Qualification'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="qualificationDegreeMessage" name="qualificationDegreeMessage" value="<spring:message code='member.personal.qualificationDegree' text='Degree'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="qualificationDetailsMessage" name="qualificationDetailsMessage" value="<spring:message code='member.personal.qualificationDetail' text='Details'></spring:message>" disabled="disabled"/>
		
	<select name="degreeMaster" id="degreeMaster" disabled="disabled">
	<c:forEach items="${degrees}" var="i">
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="qualifications" cssClass="validationError"></form:errors>
	<c:if test="${!(empty qualifications)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${qualifications}" var="outer">
	<div id="qualification${count}">
	<p>
	    <label class="small"><spring:message code="member.personal.qualificationDegree" text="Degree"/></label>
		<select name="qualificationDegree${count}" id="qualificationDegree${count}" class="sSelect">
		<c:forEach items="${degrees}" var="i">
		<c:choose>
		<c:when test="${outer.degree.id==i.id}">		
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
		</c:when>
		<c:otherwise>
		<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
		</c:otherwise>
		</c:choose>		
		</c:forEach>
		</select>
	</p>
	<p>
	    <label class="labelcentered"><spring:message code="member.personal.qualificationDetail" text="Details"/></label>
		<textarea name="qualificationDetail${count}" id="qualificationDetail${count}" class="sTextarea">${outer.details}</textarea>
	</p>	
	<input type='button' class='button' id='${count}' value='<spring:message code="member.personal.deleteQualification" text="Delete Qualification"></spring:message>' onclick='deleteQualification(${count});'/>
	<input type='hidden' id='qualificationLocale${count}' name='qualificationLocale${count}' value='${domain.locale}'>
	<input type='hidden' id='qualificationId${count}' name='qualificationId${count}' value="${outer.id}">
	<input type='hidden' id='qualificationVersion${count}' name='qualificationVersion${count}' value="${outer.version}">
	<c:set var="count" value="${count+1}"></c:set>
	</div>	
	</c:forEach>
	</c:if>
	</div>		
	<p>
		<label class="small"><spring:message code="member.personal.religion" text="Religion"/></label>
		<form:select path="religion" items="${religions}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="religion" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.category" text="Category"/></label>
		<form:select path="reservation" items="${reservations}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="reservation" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.caste" text="Caste"/></label>
		<form:input path="caste" cssClass="sText"/>
		<form:errors path="caste" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.maritalStatus" text="Marital Status"/></label>
		<form:select path="maritalStatus" items="${maritalStatuses}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="maritalStatus" cssClass="validationError"/>
	</p>
	<p>
		
		<%-- <input type="text" class="sText" id="spouseName" name="spouseName" value="${spouseName}"/>
	 --%>
	<c:if test="${!(empty familyMembers)}">
	<c:set var="count" value="1"></c:set>		
	<c:forEach items="${familyMembers}" var="outer">
	<div id="family${count}">
	<c:if test="${outer.relation.id==4 || outer.relation.id==3}">	
	<p>
	    <label class="small"><spring:message code="member.personal.spouse${count}" text="Spouse's Name${count}"/></label>
	   	<input name="familyMemberName${count}" id="familyMemberName${count}" class="sText" value="${outer.name}" readonly>
	</p>
	</c:if>		
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>
	
	
		<%-- <a id="addSpouse" href="#!"><spring:message code="add.Spouse" text="Add"></spring:message></a>
		
		<div id="Spouse2">
	     <label class="small"><spring:message code="member.personal.spouse2" text="Spouse's Name 2"/></label>
		<input type="text" class="sText" id="spouseName1" name="spouseName" value="${spouseName}"/>
		<a id="deleteSpouse2" href="#!"><spring:message code="remove.Spouse" text="Remove"></spring:message></a>
		</div>

		<div id="Spouse3">
		 <label class="small"><spring:message code="member.personal.spouse3" text="Spouse's Name 3"/></label>
		<input type="text" class="sText" styles="padding-left:400px;" id="spouseName2" name="spouseName" value="${spouseName}"/>
		<a id="deleteSpouse3" href="#!"><spring:message code="remove.Spouse" text="Remove"></spring:message></a>
		</div> --%>
		
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.children" text="Children"/></label>
		<label class="small" style="width: 80px;padding-left: 0px;" id="daughterLabel">					
		<spring:message code="member.personal.noOfDaughters" text="Daughters"/>		
		</label>
		<input type="text" class="integer" id="noOfDaughters" name="noOfDaughters" value="${daughters}" style="width: 100px;"/>
		<label class="small" style="width: 80px;padding-left: 10px;" id="sonLabel">
		<spring:message code="member.personal.noOfSons" text="Sons"/>
		</label>
		<input type="text" class="integer" id="noOfSons" name="noOfSons" value="${sons}" style="width: 100px;"/>
		<label class="small" style="width: 80px;padding-left: 10px;"><spring:message code="member.personal.noOfChildren" text="No. of Children"/></label>
		<input type="text" class="integer" id="noOfChildren" name="noOfChildren" readonly="readonly" value="${children}" style="width: 100px;"/>				
	</p>
	<div>
	<input type="button" class="button" id="addFamily" value="<spring:message code='member.personal.addFamily' text='Add Family Members'></spring:message>">
	<input type="hidden" id="familyCount" name="familyCount" value="${familyCount}"/>
	<input type="hidden" id="deleteFamilyMessage" name="deleteFamilyMessage" value="<spring:message code='member.personal.deleteFamily' text='Delete Family Member'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="familyNameMessage" name="familyNameMessage" value="<spring:message code='member.personal.familyMemberName' text='Name'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="familyRelationMessage" name="familyRelationMessage" value="<spring:message code='member.personal.familyMemberRelation' text='Relation'></spring:message>" disabled="disabled"/>
	
	<select name="relationMaster" id="relationMaster" disabled="disabled">
	<c:forEach items="${relations}" var="i">
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="familyMembers" cssClass="validationError"></form:errors>
	<c:if test="${!(empty familyMembers)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${familyMembers}" var="outer">
	<div id="family${count}">
	<p>
	    <label class="small"><spring:message code="member.personal.familyMemberName" text="Name"/></label>
		<input name="familyMemberName${count}" id="familyMemberName${count}" class="sText" value="${outer.name}">
	</p>	
	<p>
	    <label class="small"><spring:message code="member.personal.familyMemberRelation" text="Relation"/></label>
		<select name="familyMemberRelation${count}" id="familyMemberRelation${count}" class="sSelect">
		<c:forEach items="${relations}" var="i">		
		<c:choose>
		<c:when test="${outer.relation.id==i.id}">		
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
		</c:when>
		<c:otherwise>
		<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
		</c:otherwise>
		</c:choose>			
		</c:forEach>
		</select>
	</p>
	<input type='button' class='button' id='${count}' value='<spring:message code="member.personal.deleteFamily" text="Delete Family Member"></spring:message>' onclick='deleteFamily(${count});'/>
	<input type='hidden' id='familyMemberId${count}' name='familyMemberId${count}' value="${outer.id}">
	<input type='hidden' id='familyMemberVersion${count}' name='familyMemberVersion${count}' value="${outer.version}">
	<input type='hidden' id='familyMemberLocale${count}' name='familyMemberLocale${count}' value="${domain.locale}">
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>
	</div>		
	<p >
		<label class="small"><spring:message code="member.personal.languages" text="Language Proficiency"/></label>
		<form:select path="languages" items="${languages}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="languages" cssClass="validationError"/>
	</p>	
	<p>
		<label class="small"><spring:message code="member.personal.professions" text="Profession"/></label>
		<form:select path="professions" items="${professions}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="professions" cssClass="validationError"/>
	</p>	
	
	
	<p>
	    <label class="small"><spring:message code="member.personal.deathDate" text="Death Date"/></label>
		<form:input path="deathDate" cssClass="datemask sText"/>
		<form:errors path="deathDate" cssClass="validationError"/>	
	</p>	
	<p>
		<label class="small"><spring:message code="member.personal.condolenceDate" text="Condolence Date"/></label>
		<form:input path="condolenceDate" cssClass="datemask sText"/>
		<form:errors path="condolenceDate" cssClass="validationError"/>	
	</p>	
	<p>
		<label class="small"><spring:message code="member.personal.deathHouseDismissed" text="House Dismissed"/></label>
		<form:textarea path="deathHouseDismissed" cssClass="wysiwyg"></form:textarea>
		<form:errors path="deathHouseDismissed" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.deathRemarks" text="Remarks"/></label>
		<form:textarea path="deathRemarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="deathRemarks" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.personal.paName" text="Personal Assistants Name"/></label>
		<form:input path="paName" cssClass="sText"/>
		<form:errors path="paName" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.personal.paContactNo" text="Personal Assistants Contact Nos"/></label>
		<form:input path="paContactNo" cssClass="sText"/>
		<form:errors path="paContactNo" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.personal.paAddress" text="Personal Assistants Address"/></label>
		<form:textarea cssClass="wysiwyg" path="paAddress"/>
		<form:errors path="paAddress" cssClass="validationError"/>	
	</p>	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input type="hidden" name="oldBirthDate" id="oldBirthDate" value="${oldBirthDate}">
	<input id="house" name="house" value="${house}" type="hidden">
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">
	<input id="sonsMsg" name="sonsMsg" value="<spring:message code='member.personal.noOfSons' text='Sons'/>" type="hidden">
    <input id="sonMsg" name="sonMsg" value="<spring:message code='member.personal.noOfSon' text='Son'/>" type="hidden">
    <input id="daughtersMsg" name="daughtersMsg" value="<spring:message code='member.personal.noOfDaughters' text='Daughters'/>" type="hidden">
	<input id="daughterMsg" name="daughterMsg" value="<spring:message code='member.personal.noOfDaughter' text='Daughter'/>" type="hidden">
	<input type="hidden" id="credential_enabled" name="credential_enabled" value="${credentialEnabled}">
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>