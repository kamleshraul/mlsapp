<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
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
				      if(totalFamilyCount==1){
					   $('#addFamily').after(text);
					    }else{
				      $('#family'+prevCount).after(text);
				      }
				      $('#familyCount').val(familyCount); 
				      $('#familyMemberName'+familyCount).focus();				
	}
	function deleteFamily(id){			
		$('#family'+id).remove();
		totalFamilyCount=totalFamilyCount-1;
		if(id==familyCount){
			familyCount=familyCount-1;
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
	    		  "<label class='small'>"+$('#qualificationDetailsMessage').val()+"</label>"+
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
				      $('#qualificationDegree'+qualificationCount).focus(); 				
	}
	function deleteQualification(id){
		$('#qualification'+id).remove();
		totalQualificationCount=totalQualificationCount-1;
		if(id==qualificationCount){
			qualificationCount=qualificationCount-1;
		}
	}
	
	$(document).ready(function(){	
		$('#relationMaster').hide();
		$('#degreeMaster').hide();
		$('#addFamily').click(function(){
			addFamily();
		});
		$('#addQualification').click(function(){
			addQualification();
		});	
	});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/personal" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<span id="image_gallery" style="float: right;right: 10px;">
	<img alt="" src="" id="image_photo" width="70" height="70">
	<img alt="" src="" id="image_specimenSignature" width="70" height="70">	
	</span>
	<p>
		<label class="small"><spring:message code="member.personal.photo" text="Upload Photo"/></label>
		<c:choose>
		<c:when test="${empty domain.photo}">
		<jsp:include page="/common/file_upload.jsp">
			<jsp:param name="fileid" value="photo" />
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
	<p>
		<label class="small"><spring:message code="member.personal.specimenSignature" text="Specimen Signature"/></label>
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
		<form:errors path="firstName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.middleName" text="Middle Name"/></label>
		<form:input path="middleName" cssClass="sText"/>
		<form:errors path="middleName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.lastName" text="Last Name"/></label>
		<form:input path="lastName" cssClass="sText"/>
		<form:errors path="lastName" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.alias" text="Alias Name"/></label>
		<form:input path="alias" cssClass="sText"/>
		<form:errors path="alias" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.aliasEnabled" text="Alias Enabled"/></label>
		<form:checkbox path="aliasEnabled" cssClass="sCheck" value="true"/>
		<form:errors path="aliasEnabled" cssClass="validationError"/>	
	</p>
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
	<p>
		<label class="small"><spring:message code="member.personal.birthDate" text="Birth Date"/></label>
		<form:input path="birthDate" cssClass="datemask sText" />
		<form:errors path="birthDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.personal.birthPlace" text="Birth Place"/></label>
		<form:input path="birthPlace" cssClass="sText"/>
		<form:errors path="birthPlace" cssClass="validationError"/>	
	</p>
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
		<label class="small"><spring:message code="member.personal.marriageDate" text="Marriage Date"/></label>
		<form:input path="marriageDate" cssClass="datemask sText" />
		<form:errors path="marriageDate" cssClass="validationError"/>	
	</p>

	<p>
		<label class="small"><spring:message code="member.personal.languages" text="Language Proficiency"/></label>
		<form:select path="languages" items="${languages}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="languages" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.personal.professions" text="Profession"/></label>
		<form:select path="professions" items="${professions}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="professions" cssClass="validationError"/>
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
	    <label class="small"><spring:message code="member.personal.qualificationDetail" text="Details"/></label>
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
	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
</form:form>
</div>
</body>
</html>