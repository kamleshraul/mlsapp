<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div  style="background-image: url('/els/resources/images/${houseType}.jpg');background-repeat: no-repeat;background-position: bottom right;position: static;background-attachment: fixed;">
<table style="border-collapse: collapse;width:70%;">

<tr>
<td colspan="13">
<p style="font-size:190px;"><spring:message code="member.new.heading" text="Enter Details"/>:</p>

</td>
</tr>

<tr>
<td>
<img alt="" src="./resources/images/template/user_icon.png" width="100" height="100">
</td>
<td>
<img alt="" src="./resources/images/template/user_icon.png" width="40" height="40">
</td>
<td>
<img alt="" src="./resources/images/template/user_icon.png" width="40" height="40">
</td>
</tr>

<tr>
<td>
<spring:message code="member.party" text="Party"/>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.details" text="Personal"/></label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.title" text="Title"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.firstName" text="First Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.middleName" text="Middle Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.lastName" text="Last Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.alias" text="Alias Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.birthDate" text="Birth Date"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.birthPlace" text="Birth Place"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.nationality" text="Nationality"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.gender" text="Gender"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.qualification" text="Qualification Details"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.religion" text="Religion"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.category" text="Category"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.caste" text="Caste"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.maritalStatus" text="Marital Status"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.spouse" text="Spouse's Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfDaughter" text="No. of Daughters"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfSons" text="No. of Sons"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfChildren" text="No. of Children"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.languages" text="Language Proficiency"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.professions" text="Profession"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.deathDate" text="Death Date"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.condolenceDate" text="Condolence Date"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paName" text="Personal Assistants Name"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paContactNo" text="Personal Assistants Contact Nos"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paAddress" text="Personal Assistants Address"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.positionHeld" text="Positions Held"/></label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.other.fromYear" text="From Year"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.other.toYear" text="To Year"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.other.positionPosition" text="Position"/></label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.other.otherInformation" text="Other Information"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="labelcentered"><spring:message code="member.other.countriesVisited" text="Countries Visited"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="labelcentered"><spring:message code="member.other.publications" text="Publications"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.details" text="Contact Details"/></label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.permanentAddress" text="Permanent Address"/></label>
</td>
<td>
<label class="newsmall"><br>
<spring:message code="generic.telephone" text="Telephone"/>:<br>
<spring:message code="generic.fax" text="Fax"/>:
</label>
</td>
</tr>


<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.presentAddress" text="Present Address"/></label>
</td>
<td>
<label class="newsmall"><br>
<spring:message code="generic.telephone" text="Telephone"/>:<br>
<spring:message code="generic.fax" text="Fax"/>:
</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.officeAddress" text="Office Address"/></label>
</td>
<td>
<label class="newsmall"><br>
<spring:message code="generic.telephone" text="Telephone"/>:<br>
<spring:message code="generic.fax" text="Fax"/>:
</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.mumbaiAddress" text="Mumbai Address"/></label>
</td>
<td>
<label class="newsmall"><br>
<spring:message code="generic.telephone" text="Telephone"/>:<br>
<spring:message code="generic.fax" text="Fax"/>:
</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.nagpurAddress" text="Nagpur Address"/>
</label>
</td>
<td>
<label class="newsmall"><br>
<spring:message code="generic.telephone" text="Telephone"/>:<br>
<spring:message code="generic.fax" text="Fax"/>:
</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.mobile1" text="Mobile 1"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.mobile2" text="Mobile 2"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.email1" text="Email 1"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.email2" text="Email 2"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.website1" text="Website 1"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.website2" text="Website 2"/></label>
</td>
<td>
</td>
</tr>

<tr>
<td>
<spring:message code="election.result" text="Election Result"/>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.election.election" text="Election"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.electionType" text="Election Type"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.house.constituencies" text="Constituency"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.votingDate" text="Voting Date"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.electionResultDate" text="Election Result Date"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.noOfVoters" text="No of Voters"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.totalValidVotes" text="Total Valid Votes"/></label>
</td>
<td>
<label class="newsmall"><spring:message code="member.election.votesReceived" text="Votes Received"/></label>
</td>
<td colspan="5">
<label class="newsmall"><spring:message code="member.election.rivals" text="Rival members"/></label>
</td>
</tr>
</table>
	
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>