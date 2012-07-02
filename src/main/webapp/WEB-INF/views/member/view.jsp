<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.personal" text="Member Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	</head>

<body >
<div  style="background-image: url('/els/resources/images/${houseType}.jpg');background-repeat: no-repeat;background-position: bottom right;position: static;background-attachment: fixed;">
<table style="border-collapse: collapse;width:70%;">

<tr>
<td colspan="13">
<h2><spring:message code="member.new.heading" text="Enter Details"/>:
&nbsp;${member.title} ${member.firstName} ${member.middleName} ${member.lastName}
</h2>
</td>
</tr>

<tr>
<td>
<c:choose>
<c:when test="${!(member.photo=='-')}">
<img alt="" src="file/photo/${member.photo}" width="100" height="100">
</c:when>
<c:otherwise>
<img alt="" src="./resources/images/template/user_icon.png" width="100" height="100">
</c:otherwise>
</c:choose>
</td>
<td>
<c:choose>
<c:when test="${!(empty member.partyFlag)}">
<c:if test="${!(member.partyFlag!='-')}">
<img alt="" src="file/photo/${member.partyFlag}" width="40" height="40">
</c:if>
</c:when>
<c:otherwise>
<img alt="" src="./resources/images/template/user_icon.png" width="40" height="40">
</c:otherwise>
</c:choose>
</td>
<td>
<c:choose>
<c:when test="${!(member.specimenSignature=='-')}">
<img alt="" src="file/photo/${member.specimenSignature}" width="200" height="100">
</c:when>
<c:otherwise>
<img alt="" src="./resources/images/template/user_icon.png" width="200" height="100">
</c:otherwise>
</c:choose>
</td>
</tr>

<tr>
<td>
<spring:message code="member.party" text="Party"/>
</td>
<td>
<label class="newsmall">${member.party}</label>
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
<label class="newsmall">${member.title}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.firstName" text="First Name"/></label>
</td>
<td>
<label class="newsmall">${member.firstName}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.middleName" text="Middle Name"/></label>
</td>
<td>
<label class="newsmall">${member.middleName}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.lastName" text="Last Name"/></label>
</td>
<td>
<label class="newsmall">${member.lastName}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.alias" text="Alias Name"/></label>
</td>
<td>
<label class="newsmall">${member.alias}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.birthDate" text="Birth Date"/></label>
</td>
<td>
<label class="newsmall">${member.birthDate}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.birthPlace" text="Birth Place"/></label>
</td>
<td>
<label class="newsmall">${member.birthPlace}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.nationality" text="Nationality"/></label>
</td>
<td>
<label class="newsmall">${member.nationality}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.gender" text="Gender"/></label>
</td>
<td>
<label class="newsmall">${member.gender}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.qualification" text="Qualification Details"/></label>
</td>
<td>
<label class="newsmall">${member.qualification}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.religion" text="Religion"/></label>
</td>
<td>
<label class="newsmall">${member.religion}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.category" text="Category"/></label>
</td>
<td>
<label class="newsmall">${member.category}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.caste" text="Caste"/></label>
</td>
<td>
<label class="newsmall">${member.caste}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.maritalStatus" text="Marital Status"/></label>
</td>
<td>
<label class="newsmall">${member.maritalStatus}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.spouse" text="Spouse's Name"/></label>
</td>
<td>
<label class="newsmall">${member.spouse}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfDaughter" text="No. of Daughters"/></label>
</td>
<td>
<label class="newsmall">${member.noOfDaughter}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfSons" text="No. of Sons"/></label>
</td>
<td>
<label class="newsmall">${member.noOfSons}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.noOfChildren" text="No. of Children"/></label>
</td>
<td>
<label class="newsmall">${member.noOfChildren}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.languages" text="Language Proficiency"/></label>
</td>
<td>
<label class="newsmall">${member.languages}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.professions" text="Profession"/></label>
</td>
<td>
<label class="newsmall">${member.professions}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.deathDate" text="Death Date"/></label>
</td>
<td>
<label class="newsmall">${member.deathDate}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.condolenceDate" text="Condolence Date"/></label>
</td>
<td>
<label class="newsmall">${member.condolenceDate}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paName" text="Personal Assistants Name"/></label>
</td>
<td>
<label class="newsmall">${member.paName}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paContactNo" text="Personal Assistants Contact Nos"/></label>
</td>
<td>
<label class="newsmall">${member.paContactNo}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.personal.paAddress" text="Personal Assistants Address"/></label>
</td>
<td>
<label class="newsmall">${member.paAddress}</label>
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
<c:choose>
<c:when test="${!(empty member.positionsHeld)}">
<c:forEach items="${member.positionsHeld}" var="i">
<td>
<label class="newsmall">${i.fromDate}</label>
</td>
<td>
<label class="newsmall">${i.toDate}</label>
</td>
<td>
<label class="newsmall">${i.position}</label>
</td>
</c:forEach>
</c:when>
<c:otherwise>
<td>-</td>
<td>-</td>
<td>-</td>
</c:otherwise>
</c:choose>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.other.otherInformation" text="Other Information"/></label>
</td>
<td>
<label class="newsmall">${member.otherInformation}</label>
</td>
</tr>

<tr>
<td>
<label class="labelcentered"><spring:message code="member.other.countriesVisited" text="Countries Visited"/></label>
</td>
<td>
<label class="newsmall">${member.countriesVisited}</label>
</td>
</tr>

<tr>
<td>
<label class="labelcentered"><spring:message code="member.other.publications" text="Publications"/></label>
</td>
<td>
<label class="newsmall">${member.publications}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.details" text="Contact Details"/></label>
</td>
</tr>

<tr>
<td>
<label class="newsmall">
<c:choose>
<c:when test="${!(member.permanentAddress1=='-')}">
<spring:message code="member.contact.permanentAddress1" text="Permanent Address 1"/>
</c:when>
<c:otherwise>
<spring:message code="member.contact.permanentAddress" text="Permanent Address"/>
</c:otherwise>
</c:choose>
</label>
</td>
<td>
<label class="newsmall">${member.permanentAddress}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone1}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax1}
</label>
</td>
</tr>

<c:if test="${!(member.permanentAddress1=='-')}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.permanentAddress2" text="Permanent Address 2"/>
</label>
</td>
<td>
<label class="newsmall">${member.permanentAddress1}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone6}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax6}
</label>
</td>
</tr>
</c:if>

<c:if test="${!(member.permanentAddress2=='-')}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.permanentAddress3" text="Permanent Address 3"/>
</label>
</td>
<td>
<label class="newsmall">${member.permanentAddress2}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone7}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax7}
</label>
</td>
</tr>
</c:if>

<tr>
<td>
<label class="newsmall">
<c:choose>
<c:when test="${member.presentAddress1!='-'}">
<spring:message code="member.contact.presentAddress1" text="Permanent Address 1"/>
</c:when>
<c:otherwise>
<spring:message code="member.contact.presentAddress" text="Permanent Address"/>
</c:otherwise>
</c:choose>
</label>
</td>
<td>
<label class="newsmall">${member.presentAddress}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone2}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax2}
</label>
</td>
</tr>

<c:if test="${member.presentAddress1!='-'}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.presentAddress2" text="Present Address 2"/>
</label>
</td>
<td>
<label class="newsmall">${member.presentAddress1}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone8}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax8}
</label>
</td>
</tr>
</c:if>

<c:if test="${member.presentAddress2!='-'}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.presentAddress3" text="Present Address 3"/>
</label>
</td>
<td>
<label class="newsmall">${member.presentAddress2}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone9}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax9}
</label>
</td>
</tr>
</c:if>

<tr>
<td>
<label class="newsmall">
<c:choose>
<c:when test="${member.officeAddress1!='-'}">
<spring:message code="member.contact.officeAddress1" text="Office Address 1"/>
</c:when>
<c:otherwise>
<spring:message code="member.contact.officeAddress" text="Office Address"/>
</c:otherwise>
</c:choose>
</label>
</td>
<td>
<label class="newsmall">${member.officeAddress}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone3}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax3}
</label>
</td>
</tr>

<c:if test="${member.officeAddress1!='-'}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.officeAddress2" text="Office Address 2"/>
</label>
</td>
<td>
<label class="newsmall">${member.officeAddress1}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone10}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax10}
</label>
</td>
</tr>
</c:if>

<c:if test="${member.officeAddress2!='-'}">
<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.officeAddress3" text="Office Address 3"/>
</label>
</td>
<td>
<label class="newsmall">${member.officeAddress2}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone11}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax11}
</label>
</td>
</tr>
</c:if>

<tr>
<td>
<label class="newsmall">
<spring:message code="member.contact.mumbaiAddress" text="Mumbai Address"/></label>
</td>
<td>
<label class="newsmall">${member.tempAddress1}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone4}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax4}
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
<label class="newsmall">${member.tempAddress2}<br>
<spring:message code="generic.telephone" text="Telephone"/>:&nbsp;${member.telephone5}<br>
<spring:message code="generic.fax" text="Fax"/>:&nbsp;${member.fax5}
</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.mobile1" text="Mobile 1"/></label>
</td>
<td>
<label class="newsmall">${member.mobile1}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.mobile2" text="Mobile 2"/></label>
</td>
<td>
<label class="newsmall">${member.mobile2}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.email1" text="Email 1"/></label>
</td>
<td>
<label class="newsmall">${member.email1}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.email2" text="Email 2"/></label>
</td>
<td>
<label class="newsmall">${member.email2}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.website1" text="Website 1"/></label>
</td>
<td>
<label class="newsmall">${member.website1}</label>
</td>
</tr>

<tr>
<td>
<label class="newsmall"><spring:message code="member.contact.website2" text="Website 2"/></label>
</td>
<td>
<label class="newsmall">${member.website2}</label>
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

<c:choose>
<c:when test="${!(empty member.electionResults) }">
<c:forEach items="${member.electionResults}" var="i">
<tr>
<td>
<label class="newsmall">${i.election}</label>
</td>
<td>
<label class="newsmall">${i.electionType }</label>
</td>
<td>
<label class="newsmall">${i.constituency}</label>
</td>
<td>
<label class="newsmall">${i.votingDate}</label>
</td>
<td>
<label class="newsmall">${i.electionResultDate}</label>
</td>
<td>
<label class="newsmall">${i.noOfVoters}</label>
</td>
<td>
<label class="newsmall">${i.validVotes }</label>
</td>
<td>
<label class="newsmall">${i.votesReceived}</label>
</td>
<td>
<label class="newsmall">${member.title} ${member.firstName} ${member.middleName} ${member.lastName}&nbsp;&nbsp;&nbsp;${i.votesReceived}(${member.party})</label>
</td>
<c:forEach items="${i.rivalMembers}" var="j">
<td>
<label class="newsmall">${j.name}&nbsp;&nbsp;&nbsp;${j.votesReceived}(${j.party})</label>
</td>
</c:forEach>
</tr>
</c:forEach>
</c:when>
<c:otherwise>
<tr>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td>
<label class="newsmall">-</label>
</td>
<td colspan="5">
<label class="newsmall">-</label>
</td>
</tr>
</c:otherwise>
</c:choose>
</table>
	
</div>
</body>
</html>