<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.contact" text="Member Contact Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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
		});		
	}
	$(document).ready(function(){
		$('.state').change(function(){
			loadDistricts($(this).val(),$(this).attr("id").split('.')[0]);
		});	
		$('.district').change(function(){
			loadTehsils($(this).val(),$(this).attr("id").split('.')[0]);
		});	
	});	
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/contact" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.member" text="Member"></spring:message>:&nbsp;
		${domain.title.name} ${domain.firstName} ${domain.middleName } ${domain.lastName }]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="member.contact.email1" text="Email 1"/></label>
		<form:input path="contact.email1" cssClass="sText"/>
		<form:errors path="contact.email1" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.email2" text="Email 2"/></label>
		<form:input path="contact.email2" cssClass="sText"/>
		<form:errors path="contact.email2" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.website1" text="Website 1"/></label>
		<form:input path="contact.website1" cssClass="sText"/>
		<form:errors path="contact.website1" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.website2" text="Website 2"/></label>
		<form:input path="contact.website2" cssClass="sText"/>
		<form:errors path="contact.website2" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.telephone1" text="Telephone 1"/></label>
		<form:input path="contact.telephone1" cssClass="sText"/>
		<form:errors path="contact.telephone1" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.telephone2" text="Telephone 2"/></label>
		<form:input path="contact.telephone2" cssClass="sText"/>
		<form:errors path="contact.telephone2" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.fax1" text="Fax 1"/></label>
		<form:input path="contact.fax1" cssClass="sText"/>
		<form:errors path="contact.fax1" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.contact.fax2" text="Fax 2"/></label>
		<form:input path="contact.fax2" cssClass="sText"/>
		<form:errors path="contact.fax2" cssClass="validationError"/>	
	</p>
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
	<fieldset>
		<legend> <spring:message code="member.contact.presentAddress" text="Present Address"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="presentAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="presentAddress.details" cssClass="validationError"/>	
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
	</fieldset>
	
	<fieldset>
		<legend> <spring:message code="member.contact.permanentAddress" text="Permanent Address"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="permanentAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="permanentAddress.details" cssClass="validationError"/>	
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
	</fieldset>
	<fieldset>
		<legend> <spring:message code="member.contact.officeAddress" text="Office Address"/> </legend>
		<p>
			<label class="small"><spring:message code="generic.addressDetails" text="Address"/></label>
			<form:textarea path="officeAddress.details" cssClass="sTextarea"></form:textarea>
			<form:errors path="officeAddress.details" cssClass="validationError"/>	
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
	</fieldset>
	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
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
	<form:hidden path="permanentAddress.version"/>
	<form:hidden path="permanentAddress.id"/>
	<form:hidden path="permanentAddress.locale"/>
	<form:hidden path="presentAddress.version"/>
	<form:hidden path="presentAddress.id"/>
	<form:hidden path="presentAddress.locale"/>
	
</form:form>
</div>
</body>
</html>