<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	 <!-- <link rel="stylesheet" href="./resources/css/memberprint.css" type="text/css" media="print" /> -->
<!-- 	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" /> -->
	<style>
	
		table.center {
      margin-left: auto; 
      margin-right: auto; 
     /*  border: 2px solid black;  */
     }
     .headColor{
       color:black;
     }
     .containerBorder{
      /*    margin-left: auto; 
        margin-right: auto;  */
        border-style: solid;
 	    border-width: thin;
 	   /*  padding : 30px; */
 	    width:100%;      
     }
    /*  .tableBoder1{
      border : 2px solid black;
       } */
     .imageBorder{
        border: 1px solid black;
     }
   /*      #table8{
     table-layout: fixed;
            width: 100%;
     }
     #table8 th, td {
      padding: 5px;
     } */
         #table9{
     table-layout: fixed;
            width: 100%;
     }
     #table9 th, td {
      padding: 5px;
     }
     #table2{
     table-layout: fixed;
            width: 100%;
     }
     #table2 th, td {
      padding: 5px;
     }
     #table3{
          table-layout: fixed;
            width: 100%;
     }
     #table3 th, td {
      padding: 5px;
     }
     #table4{
          table-layout: fixed;
            width: 100%;
     }
     #table4 th, td {
      padding: 5px;
     }
      #table5{
          table-layout: fixed;
            width: 100%;
     }
     #table5 th, td {
      padding: 5px;
     }
     #table6{
           table-layout: fixed; 
            width: 100%;
     }
     #table6 th, td {
      padding: 5px;
     }
     #table7{
           table-layout: fixed; 
            width: 100%;
     }
     #table7 th, td {
      padding: 5px;
     }
     
     #tdOtherContent a {
       cursor: pointer;
     }
     
     #tdOtherContent {
       text-align:center;
     }
     
     #tdCountryContent a {
       cursor: pointer;
     }
     
     #tdCountryContent {
       text-align: center;
     }
     
     #tdPublishContent a {
       cursor: pointer;
     }
     
     #tdPublishContent {
       text-align: center;
     }
     
     @media print{
           @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 0px 0px 0px 40px !important;
	        }  
	        #table10{
             	  border:none;
           	}
	   /*      #table5 {
  				  border:solid #000 !important;
    			  border-width:1px 0 0 1px !important;
    		}
			#table5	th, td {
         			   border:solid #000 !important;
  					  border-width:0 1px 1px 0 !important;
				}	 */
			 .containerBorder{
			   border: none;
			 }    
		
     }
     #printerIcon{
     float:right;
     }
  /*    .reportHead{
      text-align:center;
     } */
	</style>

	</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="printerIcon">
<a id="memberPrint_pdf" class="exportLink" onclick="window.print()" href="#" style="text-decoration: none;">
  	<img src="./resources/images/printer.png" alt="Export to PDF" width="32" height="32">
</a>
</div><br/><br/><br/><br/>
 <div id="reportDiv" class="containerBorder"> 
 <br/>
<center><h2 style="color:black;"><spring:message code="member.profileView.heading" text="Member Profile View"/></h2></center>

  <br/>
  <table id="table10" class="center" style=" border-collapse: collapse;width:70%;">
    <tr>
    <td>
    <center>
    <c:if test="${!(member.photo=='-')}">
    <img alt="" class="imageBorder" src="file/photo/${member.photo}" height="150">
    </c:if>
    <c:if test="${(member.photo=='-')}">
      <img alt=""  src="./resources/images/template/user_icon1.png" width="100" height="100">
    </c:if>
     </center>
    </td>
    </tr>
    <tr>
    <td>
   <center>
   <h4>
    <span class="headColor">${member.lastName}, ${member.title} ${member.firstName} ${member.middleName}</span>
   </h4>
   </center>
    </td>
    </tr>
    <c:if test="${not empty member.constituency}">
     <tr>
    <td>
   <center>
   <h4>
     <span class="headColor">${member.constituency}</span>
    </h4>
</center>
    </td>
    </tr>
    </c:if>
    <c:if test="${not empty member.party}">
     <tr>
    <td>
   <center>
    <h4> <span class="headColor">${member.party}</span></h4>
</center>
    </td>
    </tr>
    </c:if>
  </table>
   <br/>
   <center><h4 style="color:black;"><spring:message code="member.profileView.personalDetail" text="Personal Details"/></h4></center>
    <br/>
    <table id="table2" class="center tableBoder1" style="width:70%;">
    <tr>
    <td>
	<center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.birthDate" text="Birth Date"/></label></strong></center>
	</td>
	<td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.birthDate}</label></center>
    </td>
    </tr>
    <tr>
    <td>
	<center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.birthPlace" text="Birth Place"/></label></strong></center>
	</td>
	<td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.state}, ${member.district }, ${member.tehsil }</label></center>
    </td>
    </tr>
    <tr>
   <td>
  <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.nationality" text="Nationality"/></label></strong></center>
  </td>
  <td><center style="color:black;"><strong>:</strong></center></td>
  <td>
  <center style="color:black;text-align:left;"><label class="newsmall">${member.nationality}</label></center>
  </td>
  </tr>
    <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.gender" text="Gender"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.gender}</label></label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.religion" text="Religion"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.religion}</label></label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.category" text="Category"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.category}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.caste" text="Caste"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.caste}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.maritalStatus" text="Marital Status"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.maritalStatus}</label></label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.spouse" text="Spouse's Name"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.spouse}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.noOfChildren" text="No. of Children"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.noOfChildren}</label></center>
	</td>
	</tr>
		<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.noOfSons" text="No. of Sons"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.noOfSons}</label></center>
	</td>
	</tr>
    <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.noOfDaughter" text="No. of Daughters"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.noOfDaughter}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.personal.professions" text="Profession"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.professions}</label></center>
	</td>
	</tr>
	 <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><spring:message code="member.profileView.education" text="Education Qualification"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	  <c:if test="${(member.qualification=='-')}">
       <center style="text-align:left;">${member.qualification}</center>
    </c:if>
      <c:if test="${!(member.qualification=='-')}">
	<p style="color:black; text-align:justify;"><label class="newsmall">${member.qualification}</label></label></p>
    </c:if>
	</td>
	</tr>
	 <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.languages" text="Language Proficiency"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.languages}</label></center>
	</td>
	</tr>
	 <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.deathDate" text="Death Date"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.deathDate}</label></center>
	</td>
	</tr>
    <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.personal.condolenceDate" text="Condolence Date"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td>
	<center style="color:black;text-align:left;"><label class="newsmall">${member.condolenceDate}</label></center>
	</td>
	</tr>
  </table>
 	<br/>
  <h4><center style="color:black;"><strong><spring:message code="member.profileView.contact" text="Personal Details"/></strong></center></h4>
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
  <br/>
  <h4><center style="color:black;"><strong><spring:message code="member.other.otherInformation" text="Other Information"/></strong></center></h4>
  <br/> <br/>
  <table id="table4" class="center tableBoder1" style="width:70%;">
  <tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.other.otherInformation" text="Other Information"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td id="tdOtherContent">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.otherInformation}</label></center>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.other.countriesVisited" text="Countries Visited"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td id="tdCountryContent">
	  <c:if test="${!(member.countriesVisited=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.countriesVisited}</label></center>
	</c:if>
	 <c:if test="${(member.countriesVisited=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.countriesVisited}</label></center>
	</c:if>
	</td>
	</tr>
	<tr>
	<td>
	 <center style="color:black;text-align:right;"><strong><label class="newsmall"><label class="newsmall"><spring:message code="member.other.publications" text="Publications"/></label></strong></center>
	</td>
	 <td><center style="color:black;"><strong>:</strong></center></td>
	<td id="tdPublishContent">
	 <c:if test="${!(member.publications=='-')}">
	<center style="color:black; text-align:justify;"><label class="newsmall">${member.publications}</label></center>
	</c:if>
	 <c:if test="${(member.publications=='-')}">
	<center style="color:black;text-align:left;"><label class="newsmall">${member.publications}</label></center>
	 </c:if>
	
	</td>
	</tr>
  </table>
    <br/>
  <h4><center style="color:black;"><strong><spring:message code="member.personal.positionHeld" text="Positions Held"/></strong></center></h4>
  <br/>
  <table id="table5" border="1" class="center tableBoder1" style="width:70%;">
   <tr>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.other.fromYear" text="From Year"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.other.toYear" text="To Year"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.other.positionPosition" text="Position"/></label></strong></center></th>
   </tr>
  <tr>
<c:choose>
<c:when test="${!(empty member.positionsHeld)}">
<c:forEach items="${member.positionsHeld}" var="i">
<tr>
<td>
<center style="color:black;"><label class="newsmall">${i.fromDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.toDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.position}</label></center>
</td>
</tr>
</c:forEach>
</c:when>
<c:otherwise>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
</c:otherwise>
</c:choose>
</tr>
</table>  
  <br/>
  <h4><center style="color:black;"><strong><spring:message code="member.minister.minister" text="Minsitries"/></strong></center></h4>
  <br/>
  <table  id="table7" border="1" class="center tableBoder1" style="width:70%;" >
   <tr>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.minister.designation" text="Designation"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.minister.minsitry" text="Ministry"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.minister.oathDate" text="Oath Date"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.minister.departmentFromDate" text="From Date"/></label></strong></center></th>
   <th><center style="color:black;"><strong><label class="newsmall"><spring:message code="member.minister.departmentToDate" text="To Date"/></label></strong></center></th>
   </tr>
     <tr>
<c:choose>
<c:when test="${!(empty member.memberMinisters)}">
<c:forEach items="${member.memberMinisters}" var="j">
<tr>
<td>
<center style="color:black;"><label class="newsmall">${j.designation}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${j.ministry}</label></center>
</td> 
<td>
<center style="color:black;"><label class="newsmall">${j.oathDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${j.ministryFromDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${j.ministryToDate}</label></center>
</td>
</tr>
</c:forEach>
</c:when>
<c:otherwise>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
<td><center style="color:black;">-</center></td>
</c:otherwise>
</c:choose>
</tr>
  </table>
     <br/>
  <h4><center style="color:black;"><strong><spring:message code="election.result" text="Election Result"/></strong></center></h4>
  <br/>
  <table id="table6"  border="1" class="center tableBoder1" style="width:70%;">
  <tr>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.election" text="Election"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.electionType" text="Election Type"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.house.constituencies" text="Constituency"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.votingDate" text="Voting Date"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.electionResultDate" text="Election Result Date"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.noOfVoters" text="No of Voters"/></label></center></th>
    <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.totalValidVotes" text="Total Valid Votes"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.votesReceived" text="Votes Received"/></label></center></th>
   <th><center style="color:black;"><label class="newsmall"><spring:message code="member.election.rivals" text="Rival members"/></label></center></th>
   </tr>
  
  <c:choose>
<c:when test="${!(empty member.electionResults) }">
<c:forEach items="${member.electionResults}" var="i">
<tr>
<td>
<center style="color:black;"><label class="newsmall">${i.election}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.electionType }</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.constituency}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.votingDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.electionResultDate}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.noOfVoters}</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.validVotes }</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">${i.votesReceived}</label></center>
</td>
<td>
<c:forEach items="${i.rivalMembers}" var="j">
<label class="newsmall" style="max-width: 140px; word-wrap: break-word;">${j.name}&nbsp;${j.votesReceived}(${j.party})</label><br/><br/>
</c:forEach>
</td>
</tr>
</c:forEach>
</c:when>
<c:otherwise>
<tr>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
<td>
<center style="color:black;"><label class="newsmall">-</label></center>
</td>
</tr>
</c:otherwise>
</c:choose>
 </table>
 <br/>
 </div>
  
<%-- ${member.HouseType} --%>
	<script type="text/javascript">

        /*   Other Information */
	 let text = document.getElementById("tdOtherContent").innerText.length;
	 let text1 = document.getElementById("tdOtherContent").innerText;
     
	if(text > 100){
	 let result = text1.substring(0,100);
	 document.getElementById("tdOtherContent").innerHTML = result +"......"+ "<a id='anchor1' style='color:blue;' onclick='readMore()'>Read More</a>";
	}

	function readMore(){
	 let result1 = text1.substring(0,text);
	  document.getElementById("tdOtherContent").innerHTML = result1 +" "+ "<a id='anchor2' style='color:blue;' onclick='readLess()'>Read Less</a>";
	}

	function readLess(){
	 let result2 = text1.substring(0,100);
	  document.getElementById("tdOtherContent").innerHTML = result2 +"....... "+ "<a id='anchor3' style='color:blue;' onclick='readMore()'>Read More</a>";
	}
	

	
	  /* Countries Visited */
	   let countryText = document.getElementById("tdCountryContent").innerText.length;
	 let countryText1 = document.getElementById("tdCountryContent").innerText;
	 
	 if(countryText > 100){
		 let countryResult = countryText1.substring(0,100);
		 document.getElementById("tdCountryContent").innerHTML = countryResult +"......"+ "<a style='color:blue;' onclick='readCountryMore()'>Read More</a>";
		}

		function readCountryMore(){
		 let countryResult1 = countryText1.substring(0,countryText);
		  document.getElementById("tdCountryContent").innerHTML = countryResult1 +" "+ "<a style='color:blue;' onclick='readCountryLess()'>Read Less</a>";
		}

		function readCountryLess(){
		 let countryResult2 = countryText1.substring(0,100);
		  document.getElementById("tdCountryContent").innerHTML = countryResult2 +"....... "+ "<a style='color:blue;' onclick='readCountryMore()'>Read More</a>";
		}
		
		/* Publications */
		
		  let publishText = document.getElementById("tdPublishContent").innerText.length;
	 	  let publishText1 = document.getElementById("tdPublishContent").innerText;
	 
	 if(publishText > 100){
		 let publishResult = publishText1.substring(0,100);
		 document.getElementById("tdPublishContent").innerHTML = publishResult +"......"+ "<a style='color:blue;' onclick='readPublishMore()'>Read More</a>";
		}

		function readPublishMore(){
		 let publishResult1 = publishText1.substring(0,publishText);
		  document.getElementById("tdPublishContent").innerHTML = publishResult1 +" "+ "<a style='color:blue;' onclick='readPublishLess()'>Read Less</a>";
		}

		function readPublishLess(){
		 let publishResult2 = publishText1.substring(0,100);
		  document.getElementById("tdPublishContent").innerHTML = publishResult2 +"....... "+ "<a style='color:blue;' onclick='readPublishMore()'>Read More</a>";
		}
		
		/* Print Action Event */
		
		if (window.matchMedia) {
			var mediaQueryList = window.matchMedia('print');
			mediaQueryList.addListener(function(mql) {
				if (mql.matches) {
					beforePrint();
				}
				 else {
					afterPrint();
				} 
			});
		}
		
		function beforePrint() {
			 let result3 = text1.substring(0,text);
			  document.getElementById("tdOtherContent").innerHTML = result3;
			  
			  let countryResult3 = countryText1.substring(0,countryText);
			  document.getElementById("tdCountryContent").innerHTML = countryResult3;
			  
			  let publishResult3 = publishText1.substring(0,publishText);
			  document.getElementById("tdPublishContent").innerHTML = publishResult3;			  
		}
		
		function afterPrint(){
			 let text = document.getElementById("tdOtherContent").innerText.length;
			 let text1 = document.getElementById("tdOtherContent").innerText;
		     
			if(text > 100){
			 let result = text1.substring(0,100);
			 document.getElementById("tdOtherContent").innerHTML = result +"......"+ "<a id='anchor1' style='color:blue;' onclick='readMore()'>Read More</a>";
			}
			
			let countryText = document.getElementById("tdCountryContent").innerText.length;
			let countryText1 = document.getElementById("tdCountryContent").innerText;
				 
			if(countryText > 100){
			   let countryResult = countryText1.substring(0,100);
				document.getElementById("tdCountryContent").innerHTML = countryResult +"......"+ "<a style='color:blue;' onclick='readCountryMore()'>Read More</a>";
			}
			
			  let publishText = document.getElementById("tdPublishContent").innerText.length;
		 	  let publishText1 = document.getElementById("tdPublishContent").innerText;
		 
		 if(publishText > 100){
			 let publishResult = publishText1.substring(0,100);
			 document.getElementById("tdPublishContent").innerHTML = publishResult +"......"+ "<a style='color:blue;' onclick='readPublishMore()'>Read More</a>";
			}

		}
		
		window.onbeforeprint = beforePrint;
		window.onafterprint = afterPrint;
		
		
	
	</script>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>