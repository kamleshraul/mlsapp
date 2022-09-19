<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	</head>
<style>
    table.center {
      margin-left: auto; 
      margin-right: auto; 
     /*  border: 2px solid black;  */
     } 
     .headColor{
       color:black;
     }
      #table3{
          table-layout: fixed;
            width: 100%;
     }
     #table3 th, td {
      padding: 5px;
     }
</style>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<center>
<h3>
    <span class="headColor">${member.lastName}, ${member.title} ${member.firstName} ${member.middleName}</span>
</h3><br/>
</center>
<h4><center style="color:black;"><strong><spring:message code="member.contact" text="Member Contact Details"/></strong></center></h4>
  <br/>
  <table id="table3" class="center tableBoder1" style="width:70%;">
    <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.paName" text="Personal Assistants Name"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.paName}</label></center>
	</td>
	</tr>
	 <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.paContactNo" text="Personal Assistants Contact Nos"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.paContactNo}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.paAddress" text="Personal Assistants Address"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.paAddress}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.permanentAddress" text="Permanent Address 1"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<c:if test="${!(member.permanentAddress=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.permanentAddress}</label></center>
	</c:if>
	<c:if test="${(member.permanentAddress=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.permanentAddress}</label></center>
	</c:if>
	</td>
	</tr>
	<c:if test="${!(member.permanentAddress1=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.permanentAddress1" text="Permanent Address 2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.permanentAddress1}</label></center>
	</td>
	</tr>
	</c:if>
	<c:if test="${!(member.permanentAddress2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.permanentAddress2" text="Permanent Address 3"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.permanentAddress2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.presentAddress" text="Present Address"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<c:if test="${!(member.presentAddress=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.presentAddress}</label></center>
	</c:if>
    <c:if test="${(member.presentAddress=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.presentAddress}</label></center>
	</c:if>
	</td>
	</tr>
	<c:if test="${!(member.presentAddress1=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.presentAddress1" text="Present Address 2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.presentAddress1}</label></center>
	</td>
	</tr>
	</c:if>
	<c:if test="${!(member.presentAddress2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.presentAddress2" text="Present Address 3"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.presentAddress2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.correspondenceAddress" text="Correspondence Address"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<c:if test="${!(member.tempAddress1=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.tempAddress1}</label></center>
	</c:if>
    <c:if test="${(member.tempAddress1=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.tempAddress1}</label></center>
	</c:if>
	</td>
	</tr>
	<c:if test="${!(member.tempAddress2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.correspondenceAddress" text="Correspondence Address"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.tempAddress2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.officeAddress" text="Office Address"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<c:if test="${!(member.officeAddress=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.officeAddress}</label></center>
	</c:if>
    <c:if test="${(member.officeAddress=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.officeAddress}</label></center>
	</c:if>
	</td>
	</tr>
	<c:if test="${!(member.officeAddress1=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.officeAddress1" text="Office Address 1"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.officeAddress1}</label></center>
	</td>
	</tr>
	</c:if>
		<c:if test="${!(member.officeAddress2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.officeAddress2" text="Office Address 2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.officeAddress2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.mobile1" text="Mobile No.1"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.mobile1}</label></center>
	</td>
	</tr>
	<c:if test="${!(member.mobile2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.mobile2" text="Mobile No.2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;"><label class="newsmall">${member.mobile2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.email1" text="Email 1"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.email1}</label></center>
	</td>
	</tr>
	<c:if test="${!(member.email2=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.email2" text="Email 2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;"><label class="newsmall">${member.email2}</label></center>
	</td>
	</tr>
	</c:if>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.website1" text="Website 1"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.website1}</label></center>
	</td>
	</tr>
	<c:if test="${!(member.website1=='-')}">
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.contact.website2" text="Website 2"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;"><label class="newsmall">${member.website2}</label></center>
	</td>
	</tr>
	</c:if>
  </table>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>