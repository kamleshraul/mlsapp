<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.contact" text="Member Contact Details"/>
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
	function loadDistricts(stateId,type){
	$.get('ref/state/'+stateId+'/districts',function(data){
		$('.'+type+'District').empty();
		if(data.length>0){
		var text="";
		for(var i=0;i<data.length;i++){
		text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
		}
		$('.'+type+'District').html(text);
		loadTehsils(data[0].id,type);
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
	function loadTehsils(districtId,type){
		$.get('ref/district/'+districtId+'/tehsils',function(data){
			$('.'+type+'Tehsil').empty();
			if(data.length>0){
			var text="";
			for(var i=0;i<data.length;i++){
			text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$('.'+type+'Tehsil').html(text);			
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
	$(document).ready(function(){
		$('.state').change(function(){
			loadDistricts($(this).val(),$(this).attr("id").split('.')[0]);
		});	
		$('.district').change(function(){
			loadTehsils($(this).val(),$(this).attr("id").split('.')[0]);
		});	
		//first change label of permanent add,present add and office to 1 if count is >0
		var permanentC=parseInt($("#permanentAddCount").val());
		var presentC=parseInt($("#presentAddCount").val());
		var officeC=parseInt($("#officeAddCount").val());
		//these are used to control display of addresses on clicking of add link
		var newpermanentC=0;
		var newpresentC=0;
		var newofficeC=0;
		if(permanentC>0){
			$("#permanentAddLabel1").show();
		}else{
			$("#permanentAddLabel1").hide();
			$("#permanentAdd1").hide();
			$("#permanentAdd2").hide();
		}
		if(presentC>0){
			$("#presentAddLabel1").show();
		}else{
			$("#presentAddLabel1").hide();
			$("#presentAdd1").hide();
			$("#presentAdd2").hide();
		}
		if(officeC>0){
			$("#officeAddLabel1").show();
		}else{
			$("#officeAddLabel1").hide();
			$("#officeAdd1").hide();
			$("#officeAdd2").hide();
		}
		//display of addresses
		$("#addPermanentAdd").click(function(){
			$("#permanentAddLabel1").show();			
			newpermanentC++;
			if(newpermanentC==1){
				$("#permanentAdd1").show();
			}else if(newpermanentC==2){
				$("#permanentAdd2").show();
			}else{
				alert($("#addPermanentMsg").val());
			}
		});
		$("#addPresentAdd").click(function(){
			$("#presentAddLabel1").show();			
			newpresentC++;
			if(newpresentC==1){
				$("#presentAdd1").show();
			}else if(newpresentC==2){
				$("#presentAdd2").show();
			}else{
				alert($("#addPresentMsg").val());
			}
		});
		$("#addOfficeAdd").click(function(){
			$("#officeAddLabel1").show();			
			newofficeC++;
			if(newofficeC==1){
				$("#officeAdd1").show();
			}else if(newofficeC==2){
				$("#officeAdd2").show();
			}else{
				alert($("#addOfficeMsg").val());
			}
		});
		$("#deletePermanentAdd1").click(function(){
			var id=$("#permanentAddress1.id").val();
			if(id!=""){
				$("#permanentAddress1.details").val("");
				$("#permanentAddress1.city").val("");
				$("#permanentAddress1.pincode").val("");
				$("#contact.telephone6").val("");
				$("#contact.fax6").val("");				
				newpermanentC--;
				$("#permanentAdd1").hide();			    	
			}else{
				$("#permanentAdd1").hide();
			}
			//if all new permanent address have been removed then change label of permanent address
			if(newpermanentC==0){
				$("#permanentAddLabel1").hide();				
			}
		});
		$("#deletePermanentAdd2").click(function(){
			var id=$("#permanentAddress2.id").val();
			if(id!=""){
				$("#permanentAddress2.details").val("");
				$("#permanentAddress2.city").val("");
				$("#permanentAddress2.pincode").val("");
				$("#contact.telephone7").val("");
				$("#contact.fax7").val("");				
				newpermanentC--;
				$("#permanentAdd2").hide();			    
			}else{
				$("#permanentAdd2").hide();
			}
			if(newpermanentC==0){
				$("#permanentAddLabel1").hide();				
			}
		});
		$("#deletePresentAdd1").click(function(){
			var id=$("#presentAddress1.id").val();
			if(id!=""){
				$("#presentAddress1.details").val("");
				$("#presentAddress1.city").val("");
				$("#presentAddress1.pincode").val("");
				$("#contact.telephone8").val("");
				$("#contact.fax8").val("");				
				newpresentC--;
				$("#presentAdd1").hide();			    	
			}else{
				$("#presentAdd1").hide();
			}
			if(newpresentC==0){
				$("#presentAddLabel1").hide();			
			}
		});
		$("#deletePresentAdd2").click(function(){
			var id=$("#presentAddress2.id").val();
			if(id!=""){
				$("#presentAddress2.details").val("");
				$("#presentAddress2.city").val("");
				$("#presentAddress2.pincode").val("");
				$("#contact.telephone9").val("");
				$("#contact.fax9").val("");				
				newpresentC--;
				$("#presentAdd2").hide();			    	
			}else{
				$("#presentAdd2").hide();
			}
			if(newpresentC==0){
				$("#presentAddLabel1").hide();			
			}
		});
		$("#deleteOfficeAdd1").click(function(){
			var id=$("#presentAddress1.id").val();
			if(id!=""){
				$("#officeAddress1.details").val("");
				$("#officeAddress1.city").val("");
				$("#officeAddress1.pincode").val("");
				$("#contact.telephone10").val("");
				$("#contact.fax10").val("");				
				newofficeC--;
				$("#officeAdd1").hide();			    
			}else{
				$("#officeAdd1").hide();
			}
			if(newofficeC==0){
				$("#officeAddLabel1").hide();			
			}
		});
		$("#deleteOfficeAdd2").click(function(){
			var id=$("#officeAddress2.id").val();
			if(id!=""){
				$("#officeAddress2.details").val("");
				$("#officeAddress2.city").val("");
				$("#officeAddress2.pincode").val("");
				$("#contact.telephone11").val("");
				$("#contact.fax11").val("");				
				newofficeC--;
				$("#officeAdd2").hide();			    
			}else{
				$("#officeAdd2").hide();
			}
			if(newofficeC==0){
				$("#officeAddLabel1").hide();			
			}
		});
	});	
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/contact" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${domain.title.name} ${domain.firstName} ${domain.middleName} ${domain.lastName}
	</h2>
	<form:errors path="version" cssClass="validationError"/>
<!-- //////////////////////////////----- Shubham Amande Edit --------------////////////////////////////// -->
	<fieldset>
		<legend> 
		<spring:message code="member.contact.new.link" text="Contact"/>
		</legend>
		<p>
		<label class="small"><spring:message code="member.contact.mobile1" text="Mobile 1"/></label>
		<form:input path="contact.mobile1" cssClass="sText"/>
		<form:errors path="contact.mobile1" cssClass="validationError"/>	
		</p>
		<p>
		<label class="small"><spring:message code="member.contact.mobile2" text="Mobile 2"/></label>
		<form:input path="contact.mobile2" cssClass="sText"/>
		<form:errors path="contact.mobile2" cssClass="validationError"/>	
		</p>
		<p>
		<label class="small"><spring:message code="member.contact.email1" text="Email 1"/></label>
		<form:input path="contact.email1" cssClass="sText"/>
		<form:errors path="contact.email1" cssClass="validationError" />
		</p>
		<p>
		<label class="small"><spring:message code="member.contact.website1" text="Website 1"/></label>
		<form:input path="contact.website1" cssClass="sText"/>
		<form:errors path="contact.website1" cssClass="validationError" />
		</p>
	<br>
	</fieldset>
	<!-- ///////////////////////////////////////////////////////////////////////////////////// -->
	<div id="permanentAdd">
	<fieldset>
		<legend> 
		<spring:message code="member.contact.permanentAddress" text="Permanent Address"/>
		<span id="permanentAddLabel1"><spring:message code="onecount" text="1"></spring:message></span>
		</legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress.details" cssClass="validationError"/>
			<a id="addPermanentAdd" href="#"><spring:message code="add.permanentAdd" text="Add Permanent Address"></spring:message></a>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="permanentAddress.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state permanentAddressState"/>
			<form:errors path="permanentAddress.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="permanentAddress.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district permanentAddressDistrict"/>
			<form:errors path="permanentAddress.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="permanentAddress.tehsil" items="${tehsils1}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil permanentAddressTehsil"/>
			<form:errors path="permanentAddress.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="permanentAddress.city" cssClass="sText"/>
			<form:errors path="permanentAddress.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="permanentAddress.pincode" cssClass="sText"/>
			<form:errors path="permanentAddress.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone1" cssClass="sText"/>
			<form:errors path="contact.telephone1" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax1" cssClass="sText"/>
			<form:errors path="contact.fax1" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	
	<c:choose>
	<c:when test="${!(empty domain.permanentAddress1.details)}">
	


	<div id="permanentAdd1">
	<fieldset>
		<legend> <spring:message code="member.contact.permanentAddress2" text="Permanent Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress1.details" cssClass="validationError"/>	
			<a id="deletePermanentAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="permanentAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state permanentAddressState"/>
			<form:errors path="permanentAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="permanentAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district permanentAddressDistrict"/>
			<form:errors path="permanentAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="permanentAddress1.tehsil" items="${tehsils6}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil permanentAddressTehsil"/>
			<form:errors path="permanentAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="permanentAddress1.city" cssClass="sText"/>
			<form:errors path="permanentAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="permanentAddress1.pincode" cssClass="sText"/>
			<form:errors path="permanentAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone6" cssClass="sText"/>
			<form:errors path="contact.telephone6" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax6" cssClass="sText"/>
			<form:errors path="contact.fax6" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:when>
	<c:otherwise>
	<div id="permanentAdd1">
	<fieldset>
		<legend> <spring:message code="member.contact.permanentAddress2" text="Permanent Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress1.details" cssClass="validationError"/>	
			<a id="deletePermanentAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="permanentAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state permanentAddressState"/>
			<form:errors path="permanentAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="permanentAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district permanentAddressDistrict"/>
			<form:errors path="permanentAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="permanentAddress1.tehsil" items="${tehsils6}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil permanentAddressTehsil"/>
			<form:errors path="permanentAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="permanentAddress1.city" cssClass="sText"/>
			<form:errors path="permanentAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="permanentAddress1.pincode" cssClass="sText"/>
			<form:errors path="permanentAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone6" cssClass="sText"/>
			<form:errors path="contact.telephone6" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax6" cssClass="sText"/>
			<form:errors path="contact.fax6" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test="${!(empty domain.permanentAddress2.details)}">
	<div id="permanentAdd2">
	<fieldset>
		<legend> <spring:message code="member.contact.permanentAddress3" text="Permanent Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress2.details" cssClass="validationError"/>	
			<a id="deletePermanentAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="permanentAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state permanentAddressState"/>
			<form:errors path="permanentAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="permanentAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district permanentAddressDistrict"/>
			<form:errors path="permanentAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="permanentAddress2.tehsil" items="${tehsils7}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil permanentAddressTehsil"/>
			<form:errors path="permanentAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="permanentAddress2.city" cssClass="sText"/>
			<form:errors path="permanentAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="permanentAddress2.pincode" cssClass="sText"/>
			<form:errors path="permanentAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone7" cssClass="sText"/>
			<form:errors path="contact.telephone7" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax7" cssClass="sText"/>
			<form:errors path="contact.fax7" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:when>
	<c:otherwise>
	<div id="permanentAdd2">
	<fieldset>
		<legend> <spring:message code="member.contact.permanentAddress3" text="Permanent Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress2.details" cssClass="validationError"/>	
			<a id="deletePermanentAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="permanentAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state permanentAddressState"/>
			<form:errors path="permanentAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="permanentAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district permanentAddressDistrict"/>
			<form:errors path="permanentAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="permanentAddress2.tehsil" items="${tehsils7}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil permanentAddressTehsil"/>
			<form:errors path="permanentAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="permanentAddress2.city" cssClass="sText"/>
			<form:errors path="permanentAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="permanentAddress2.pincode" cssClass="sText"/>
			<form:errors path="permanentAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone7" cssClass="sText"/>
			<form:errors path="contact.telephone7" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax7" cssClass="sText"/>
			<form:errors path="contact.fax7" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:otherwise>
	</c:choose>
	
	<div id="presentAdd">			
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress" text="Present Address"/>
		 <span id="presentAddLabel1"><spring:message code="onecount" text="1"></spring:message></span></legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress.details" cssClass="validationError"/>	
			<a id="addPresentAdd" href="#"><spring:message code="add.presentAdd" text="Add Present Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="presentAddress.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state presentAddressState"/>
			<form:errors path="presentAddress.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="presentAddress.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district presentAddressDistrict"/>
			<form:errors path="presentAddress.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="presentAddress.tehsil" items="${tehsils2}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil presentAddressTehsil"/>
			<form:errors path="presentAddress.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="presentAddress.city" cssClass="sText"/>
			<form:errors path="presentAddress.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="presentAddress.pincode" cssClass="sText"/>
			<form:errors path="presentAddress.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone2" cssClass="sText"/>
			<form:errors path="contact.telephone2" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax2" cssClass="sText"/>
			<form:errors path="contact.fax2" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>

	<c:choose>
	<c:when test="${!(empty domain.presentAddress1.details)}">
	<div id="presentAdd1">			
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress2" text="Present Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress1.details" cssClass="validationError"/>
			<a id="deletePresentAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
				
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="presentAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state presentAddressState"/>
			<form:errors path="presentAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="presentAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district presentAddressDistrict"/>
			<form:errors path="presentAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="presentAddress1.tehsil" items="${tehsils8}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil presentAddressTehsil"/>
			<form:errors path="presentAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="presentAddress1.city" cssClass="sText"/>
			<form:errors path="presentAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="presentAddress1.pincode" cssClass="sText"/>
			<form:errors path="presentAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone8" cssClass="sText"/>
			<form:errors path="contact.telephone8" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax8" cssClass="sText"/>
			<form:errors path="contact.fax8" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:when>
	<c:otherwise>
		<div id="presentAdd1">			
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress2" text="Present Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress1.details" cssClass="validationError"/>
			<a id="deletePresentAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
				
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="presentAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state presentAddressState"/>
			<form:errors path="presentAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="presentAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district presentAddressDistrict"/>
			<form:errors path="presentAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="presentAddress1.tehsil" items="${tehsils8}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil presentAddressTehsil"/>
			<form:errors path="presentAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="presentAddress1.city" cssClass="sText"/>
			<form:errors path="presentAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="presentAddress1.pincode" cssClass="sText"/>
			<form:errors path="presentAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone8" cssClass="sText"/>
			<form:errors path="contact.telephone8" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax8" cssClass="sText"/>
			<form:errors path="contact.fax8" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test="${!(empty domain.presentAddress2.details)}">
	<div id="presentAdd2">			
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress3" text="Present Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress2.details" cssClass="validationError"/>	
			<a id="deletePresentAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="presentAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state presentAddressState"/>
			<form:errors path="presentAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="presentAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district presentAddressDistrict"/>
			<form:errors path="presentAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="presentAddress2.tehsil" items="${tehsils9}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil presentAddressTehsil"/>
			<form:errors path="presentAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="presentAddress2.city" cssClass="sText"/>
			<form:errors path="presentAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="presentAddress2.pincode" cssClass="sText"/>
			<form:errors path="presentAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone9" cssClass="sText"/>
			<form:errors path="contact.telephone9" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax9" cssClass="sText"/>
			<form:errors path="contact.fax9" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:when>
	<c:otherwise>
	<div id="presentAdd2">			
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress3" text="Present Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress2.details" cssClass="validationError"/>	
			<a id="deletePresentAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="presentAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state presentAddressState"/>
			<form:errors path="presentAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="presentAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district presentAddressDistrict"/>
			<form:errors path="presentAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="presentAddress2.tehsil" items="${tehsils9}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil presentAddressTehsil"/>
			<form:errors path="presentAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="presentAddress2.city" cssClass="sText"/>
			<form:errors path="presentAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="presentAddress2.pincode" cssClass="sText"/>
			<form:errors path="presentAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone9" cssClass="sText"/>
			<form:errors path="contact.telephone9" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax9" cssClass="sText"/>
			<form:errors path="contact.fax9" cssClass="validationError"/>	
		</p>
	</fieldset>
	</div>
	</c:otherwise>
	</c:choose>
	
	<div id="correspondenceAddress">
	<fieldset>
		<legend> <spring:message code="member.contact.correspondenceAddress" text="Correspondence Address"/>
		</legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="correspondenceAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="correspondenceAddress.details" cssClass="validationError"/>	
		</p>		
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="correspondenceAddress.pincode" cssClass="sText"/>
			<form:errors path="correspondenceAddress.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone12" cssClass="sText"/>
			<form:errors path="contact.telephone12" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax12" cssClass="sText"/>
			<form:errors path="contact.fax12" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
	
	<div id="officeAddress">
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress" text="Office Address"/>
		<span id="officeAddLabel1"><spring:message code="onecount" text="1"></spring:message></span> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress.details" cssClass="validationError"/>	
			<a id="addOfficeAdd" href="#"><spring:message code="add.officeAdd" text="Add Office Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="officeAddress.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state officeAddressState"/>
			<form:errors path="officeAddress.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="officeAddress.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district officeAddressDistrict"/>
			<form:errors path="officeAddress.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="officeAddress.tehsil" items="${tehsils3}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil officeAddressTehsil"/>
			<form:errors path="officeAddress.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="officeAddress.city" cssClass="sText"/>
			<form:errors path="officeAddress.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="officeAddress.pincode" cssClass="sText"/>
			<form:errors path="officeAddress.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone3" cssClass="sText"/>
			<form:errors path="contact.telephone3" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax3" cssClass="sText"/>
			<form:errors path="contact.fax3" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
   
   <c:choose>
   <c:when test="${!(empty domain.officeAddress1.details)}">
   <div id="officeAdd1">
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress2" text="Office Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress1.details" cssClass="validationError"/>	
			<a id="deleteOfficeAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="officeAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state officeAddressState"/>
			<form:errors path="officeAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="officeAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district officeAddressDistrict"/>
			<form:errors path="officeAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="officeAddress1.tehsil" items="${tehsils10}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil officeAddressTehsil"/>
			<form:errors path="officeAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="officeAddress1.city" cssClass="sText"/>
			<form:errors path="officeAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="officeAddress1.pincode" cssClass="sText"/>
			<form:errors path="officeAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone10" cssClass="sText"/>
			<form:errors path="contact.telephone10" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax10" cssClass="sText"/>
			<form:errors path="contact.fax10" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
   </c:when>
   <c:otherwise>
   <div id="officeAdd1">
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress2" text="Office Address 2"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress1.details" cssClass="validationError"/>	
			<a id="deleteOfficeAdd1" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
			
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="officeAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state officeAddressState"/>
			<form:errors path="officeAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="officeAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district officeAddressDistrict"/>
			<form:errors path="officeAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="officeAddress1.tehsil" items="${tehsils10}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil officeAddressTehsil"/>
			<form:errors path="officeAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="officeAddress1.city" cssClass="sText"/>
			<form:errors path="officeAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="officeAddress1.pincode" cssClass="sText"/>
			<form:errors path="officeAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone10" cssClass="sText"/>
			<form:errors path="contact.telephone10" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax10" cssClass="sText"/>
			<form:errors path="contact.fax10" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
   </c:otherwise>
   </c:choose>
   
  <c:choose>
   <c:when test="${!(empty domain.officeAddress2.details)}">
   <div id="officeAdd2">
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress3" text="Office Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress2.details" cssClass="validationError"/>
			<a id="deleteOfficeAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
				
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="officeAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state officeAddressState"/>
			<form:errors path="officeAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="officeAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district officeAddressDistrict"/>
			<form:errors path="officeAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="officeAddress2.tehsil" items="${tehsils11}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil officeAddressTehsil"/>
			<form:errors path="officeAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="officeAddress2.city" cssClass="sText"/>
			<form:errors path="officeAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="officeAddress2.pincode" cssClass="sText"/>
			<form:errors path="officeAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone11" cssClass="sText"/>
			<form:errors path="contact.telephone11" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax11" cssClass="sText"/>
			<form:errors path="contact.fax11" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
   </c:when>
   <c:otherwise>
   <div id="officeAdd2">
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress3" text="Office Address 3"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress2.details" cssClass="validationError"/>
			<a id="deleteOfficeAdd2" href="#"><spring:message code="delete.address" text="Delete Address"></spring:message></a>	
				
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="officeAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state officeAddressState"/>
			<form:errors path="officeAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="officeAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district officeAddressDistrict"/>
			<form:errors path="officeAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="officeAddress2.tehsil" items="${tehsils11}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil officeAddressTehsil"/>
			<form:errors path="officeAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="officeAddress2.city" cssClass="sText"/>
			<form:errors path="officeAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="officeAddress2.pincode" cssClass="sText"/>
			<form:errors path="officeAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone11" cssClass="sText"/>
			<form:errors path="contact.telephone11" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax11" cssClass="sText"/>
			<form:errors path="contact.fax11" cssClass="validationError"/>	
		</p>
	</fieldset>
   </div>
   </c:otherwise>
   </c:choose>
   
	<fieldset>
		<legend> <spring:message code="member.contact.mumbaiAddress" text="Mumbai Address"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="tempAddress1.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="tempAddress1.details" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="tempAddress1.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state tempAddress1State"/>
			<form:errors path="tempAddress1.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="tempAddress1.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district tempAddress1District"/>
			<form:errors path="tempAddress1.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="tempAddress1.tehsil" items="${tehsils4}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil tempAddress1Tehsil"/>
			<form:errors path="tempAddress1.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="tempAddress1.city" cssClass="sText"/>
			<form:errors path="tempAddress1.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="tempAddress1.pincode" cssClass="sText"/>
			<form:errors path="tempAddress1.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone4" cssClass="sText"/>
			<form:errors path="contact.telephone4" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax4" cssClass="sText"/>
			<form:errors path="contact.fax4" cssClass="validationError"/>	
		</p>
	</fieldset>
		
	<fieldset>
		<legend> <spring:message code="member.contact.nagpurAddress" text="Nagpur Address"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="tempAddress2.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="tempAddress2.details" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.state" text="State"/></label>
			<form:select path="tempAddress2.state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect state tempAddress2State"/>
			<form:errors path="tempAddress2.state" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.district" text="District"/></label>
			<form:select path="tempAddress2.district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect district tempAddress2District"/>
			<form:errors path="tempAddress2.district" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.tehsil" text="Tehsil"/></label>
			<form:select path="tempAddress2.tehsil" items="${tehsils5}" itemValue="id" itemLabel="name" cssClass="sSelect tehsil tempAddress2Tehsil"/>
			<form:errors path="tempAddress2.tehsil" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="generic.city" text="City"/></label>
			<form:input path="tempAddress2.city" cssClass="sText"/>
			<form:errors path="tempAddress2.city" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.pincode" text="Pincode"/></label>
			<form:input path="tempAddress2.pincode" cssClass="sText"/>
			<form:errors path="tempAddress2.pincode" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="generic.telephone" text="Telephone"/></label>
			<form:input path="contact.telephone5" cssClass="sText"/>
			<form:errors path="contact.telephone5" cssClass="validationError" />
		</p>
		<p>
			<label class="small"><spring:message code="generic.fax" text="Fax"/></label>
			<form:input path="contact.fax5" cssClass="sText"/>
			<form:errors path="contact.fax5" cssClass="validationError"/>	
		</p>
	</fieldset>	
	
	<p>
		<label class="small"><spring:message code="member.contact.email2" text="Email 2"/></label>
		<form:input path="contact.email2" cssClass="sText"/>
		<form:errors path="contact.email2" cssClass="validationError" />
	</p>
	
	<p>
		<label class="small"><spring:message code="member.contact.website2" text="Website 2"/></label>
		<form:input path="contact.website2" cssClass="sText"/>
		<form:errors path="contact.website2" cssClass="validationError" />
	</p>
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>
	
	<form:hidden path="version"/>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<form:hidden path="contact.version"/>
	<form:hidden path="contact.id"/>
	<form:hidden path="contact.locale" />   	
	<form:hidden path="officeAddress.version"/>
	<form:hidden path="officeAddress.id"/>
	<form:hidden path="officeAddress.locale"/>
	<form:hidden path="officeAddress1.version"/>
	<form:hidden path="officeAddress1.id"/>
	<form:hidden path="officeAddress1.locale"/>
	<form:hidden path="officeAddress2.version"/>
	<form:hidden path="officeAddress2.id"/>
	<form:hidden path="officeAddress2.locale"/>
	
	<form:hidden path="permanentAddress.version"/>
	<form:hidden path="permanentAddress.id"/>
	<form:hidden path="permanentAddress.locale"/>
	<form:hidden path="permanentAddress1.version"/>
	<form:hidden path="permanentAddress1.id"/>
	<form:hidden path="permanentAddress1.locale"/>
	<form:hidden path="permanentAddress2.version"/>
	<form:hidden path="permanentAddress2.id"/>
	<form:hidden path="permanentAddress2.locale"/>
	
	<form:hidden path="presentAddress.version"/>
	<form:hidden path="presentAddress.id"/>
	<form:hidden path="presentAddress.locale"/>
	<form:hidden path="presentAddress1.version"/>
	<form:hidden path="presentAddress1.id"/>
	<form:hidden path="presentAddress1.locale"/>
	<form:hidden path="presentAddress2.version"/>
	<form:hidden path="presentAddress2.id"/>
	<form:hidden path="presentAddress2.locale"/>
	
	<form:hidden path="tempAddress1.version"/>
	<form:hidden path="tempAddress1.id"/>
	<form:hidden path="tempAddress1.locale"/>
	<form:hidden path="tempAddress2.version"/>
	<form:hidden path="tempAddress2.id"/>
	<form:hidden path="tempAddress2.locale"/>
	<input type="hidden" name="houseType" id="houseType" value="${houseType}">
	<input type="hidden" name="permanentAddCount" id="permanentAddCount" value="${permanentAddCount}">	
	<input type="hidden" name="presentAddCount" id="presentAddCount" value="${presentAddCount}">	
	<input type="hidden" name="officeAddCount" id="officeAddCount" value="${officeAddCount}">	
	<input type="hidden" id="addPermanentMsg" value="<spring:message code='only3PermanentMsg' text='Only Three Permanent Address Allowed'/>">
	<input type="hidden" id="addPresentMsg" value="<spring:message code='only3PresentMsg' text='Only Three Present Address Allowed'/>">
	<input type="hidden" id="addOfficeMsg" value="<spring:message code='only3OfficeMsg' text='Only Three Office Address Allowed'/>">
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>