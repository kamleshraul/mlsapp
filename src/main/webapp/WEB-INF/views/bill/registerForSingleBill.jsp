<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.register" text="Register" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:500px;min-height:30px;}
		th{min-width:150px; max-width:500px;min-height:30px;}
	</style>
</head>
<body>	
	<h2><spring:message code="bill.register" text="Bill Register"/></h2>
	
	<c:set var="consideredInFirstHouseFirstRoundDate" value="bill_processed_considered_${firsthouse}_firsthouse_1_statusDate"></c:set>
			
	<c:set var="withdrawnInFirstHouseFirstRoundDate" value="bill_processed_withdrawn_${firsthouse}_firsthouse_1_statusDate"></c:set>

	<c:set var="negativedByFirstHouseFirstRoundDate" value="bill_processed_negatived_${firsthouse}_firsthouse_1_statusDate"></c:set>			

	<c:set var="referToSelectCommitteeFirstHouseFirstRoundDate" value="bill_processed_referToSelectCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteeFirstHouseFirstRoundExpectedDate" value="bill_processed_referToSelectCommittee_${firsthouse}_firsthouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteePresentedFirstHouseFirstRoundDate" value="bill_processed_reportOfReferSelectCommitteePresented_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferSelectCommitteeFirstHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReferSelectCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="referToJointCommitteeFirstHouseFirstRoundDate" value="bill_processed_referToJointCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="reportOfReferJointCommitteeFirstHouseFirstRoundExpectedDate" value="bill_processed_referToJointCommittee_${firsthouse}_firsthouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReferJointCommitteePresentedFirstHouseFirstRoundDate" value="bill_processed_reportOfReferJointCommitteePresented_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferJointCommitteeFirstHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReferJointCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>

	<c:set var="reReferToSelectCommitteeFirstHouseFirstRoundDate" value="bill_processed_reReferToSelectCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteeFirstHouseFirstRoundExpectedDate" value="bill_processed_reReferToSelectCommittee_${firsthouse}_firsthouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteePresentedFirstHouseFirstRoundDate" value="bill_processed_reportOfReReferSelectCommitteePresented_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferSelectCommitteeFirstHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReReferSelectCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="reReferToJointCommitteeFirstHouseFirstRoundDate" value="bill_processed_reReferToJointCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteeFirstHouseFirstRoundExpectedDate" value="bill_processed_reReferToJointCommittee_${firsthouse}_firsthouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteePresentedFirstHouseFirstRoundDate" value="bill_processed_reportOfReReferJointCommitteePresented_${firsthouse}_firsthouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferJointCommitteeFirstHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReReferJointCommittee_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="circulationForElicitingOpinionFirstHouseFirstRoundDate" value="bill_processed_circulationForElicitingOpinion_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="discussedClauseByClauseFirstHouseFirstRoundDate" value="bill_processed_discussedClauseByClause_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="passedInFirstHouseFirstRoundDate" value="bill_processed_passed_${firsthouse}_firsthouse_1_statusDate"></c:set>
	
	<c:set var="consideredInSecondHouseFirstRoundDate" value="bill_processed_considered_${secondhouse}_secondhouse_1_statusDate"></c:set>

	<c:set var="withdrawnInSecondHouseFirstRoundDate" value="bill_processed_withdrawn_${secondhouse}_secondhouse_1_statusDate"></c:set>

	<c:set var="negativedBySecondHouseFirstRoundDate" value="bill_processed_negatived_${secondhouse}_secondhouse_1_statusDate"></c:set>			

	<c:set var="referToSelectCommitteeSecondHouseFirstRoundDate" value="bill_processed_referToSelectCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteeSecondHouseFirstRoundExpectedDate" value="bill_processed_referToSelectCommittee_${secondhouse}_secondhouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteePresentedSecondHouseFirstRoundDate" value="bill_processed_reportOfReferSelectCommitteePresented_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferSelectCommitteeSecondHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReferSelectCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	
	<c:set var="referToJointCommitteeSecondHouseFirstRoundDate" value="bill_processed_referToJointCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="reportOfReferJointCommitteeSecondHouseFirstRoundExpectedDate" value="bill_processed_referToJointCommittee_${secondhouse}_secondhouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReferJointCommitteePresentedSecondHouseFirstRoundDate" value="bill_processed_reportOfReferJointCommitteePresented_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferJointCommitteeSecondHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReferJointCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>

	<c:set var="reReferToSelectCommitteeSecondHouseFirstRoundDate" value="bill_processed_reReferToSelectCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteeSecondHouseFirstRoundExpectedDate" value="bill_processed_reReferToSelectCommittee_${secondhouse}_secondhouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteePresentedSecondHouseFirstRoundDate" value="bill_processed_reportOfReReferSelectCommitteePresented_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferSelectCommitteeSecondHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReReferSelectCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	
	<c:set var="reReferToJointCommitteeSecondHouseFirstRoundDate" value="bill_processed_reReferToJointCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteeSecondHouseFirstRoundExpectedDate" value="bill_processed_reReferToJointCommittee_${secondhouse}_secondhouse_1_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteePresentedSecondHouseFirstRoundDate" value="bill_processed_reportOfReReferJointCommitteePresented_${secondhouse}_secondhouse_1_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferJointCommitteeSecondHouseFirstRoundDate" value="bill_processed_consideredAsPerReportOfReReferJointCommittee_${secondhouse}_secondhouse_1_statusDate"></c:set>
	
	<c:set var="circulationForElicitingOpinionSecondHouseFirstRoundDate" value="bill_processed_circulationForElicitingOpinion_${secondhouse}_secondhouse_1_statusDate"></c:set>
	
	<c:set var="discussedClauseByClauseSecondHouseFirstRoundDate" value="bill_processed_discussedClauseByClause_${secondhouse}_secondhouse_1_statusDate"></c:set>
	
	<c:set var="passedInSecondHouseFirstRoundDate" value="bill_processed_passed_${secondhouse}_secondhouse_1_statusDate"></c:set>

	<c:set var="consideredInFirstHouseSecondRoundDate" value="bill_processed_considered_${firsthouse}_firsthouse_2_statusDate"></c:set>

	<c:set var="withdrawnInFirstHouseSecondRoundDate" value="bill_processed_withdrawn_${firsthouse}_firsthouse_2_statusDate"></c:set>

	<c:set var="negativedByFirstHouseSecondRoundDate" value="bill_processed_negatived_${firsthouse}_firsthouse_2_statusDate"></c:set>			

	<c:set var="referToSelectCommitteeFirstHouseSecondRoundDate" value="bill_processed_referToSelectCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteeFirstHouseSecondRoundExpectedDate" value="bill_processed_referToSelectCommittee_${firsthouse}_firsthouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteePresentedFirstHouseSecondRoundDate" value="bill_processed_reportOfReferSelectCommitteePresented_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferSelectCommitteeFirstHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReferSelectCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="referToJointCommitteeFirstHouseSecondRoundDate" value="bill_processed_referToJointCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="reportOfReferJointCommitteeFirstHouseSecondRoundExpectedDate" value="bill_processed_referToJointCommittee_${firsthouse}_firsthouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReferJointCommitteePresentedFirstHouseSecondRoundDate" value="bill_processed_reportOfReferJointCommitteePresented_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferJointCommitteeFirstHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReferJointCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>

	<c:set var="reReferToSelectCommitteeFirstHouseSecondRoundDate" value="bill_processed_reReferToSelectCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteeFirstHouseSecondRoundExpectedDate" value="bill_processed_reReferToSelectCommittee_${firsthouse}_firsthouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteePresentedFirstHouseSecondRoundDate" value="bill_processed_reportOfReReferSelectCommitteePresented_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferSelectCommitteeFirstHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReReferSelectCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="reReferToJointCommitteeFirstHouseSecondRoundDate" value="bill_processed_reReferToJointCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteeFirstHouseSecondRoundExpectedDate" value="bill_processed_reReferToJointCommittee_${firsthouse}_firsthouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteePresentedFirstHouseSecondRoundDate" value="bill_processed_reportOfReReferJointCommitteePresented_${firsthouse}_firsthouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferJointCommitteeFirstHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReReferJointCommittee_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="circulationForElicitingOpinionFirstHouseSecondRoundDate" value="bill_processed_circulationForElicitingOpinion_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="discussedClauseByClauseFirstHouseSecondRoundDate" value="bill_processed_discussedClauseByClause_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="passedInFirstHouseSecondRoundDate" value="bill_processed_passed_${firsthouse}_firsthouse_2_statusDate"></c:set>
	
	<c:set var="consideredInSecondHouseSecondRoundDate" value="bill_processed_considered_${secondhouse}_secondhouse_2_statusDate"></c:set>

	<c:set var="withdrawnInSecondHouseSecondRoundDate" value="bill_processed_withdrawn_${secondhouse}_secondhouse_2_statusDate"></c:set>

	<c:set var="negativedBySecondHouseSecondRoundDate" value="bill_processed_negatived_${secondhouse}_secondhouse_2_statusDate"></c:set>			

	<c:set var="referToSelectCommitteeSecondHouseSecondRoundDate" value="bill_processed_referToSelectCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteeSecondHouseSecondRoundExpectedDate" value="bill_processed_referToSelectCommittee_${secondhouse}_secondhouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReferSelectCommitteePresentedSecondHouseSecondRoundDate" value="bill_processed_reportOfReferSelectCommitteePresented_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferSelectCommitteeSecondHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReferSelectCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	<c:set var="referToJointCommitteeSecondHouseSecondRoundDate" value="bill_processed_referToJointCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="reportOfReferJointCommitteeSecondHouseSecondRoundExpectedDate" value="bill_processed_referToJointCommittee_${secondhouse}_secondhouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReferJointCommitteePresentedSecondHouseSecondRoundDate" value="bill_processed_reportOfReferJointCommitteePresented_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReferJointCommitteeSecondHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReferJointCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>

	<c:set var="reReferToSelectCommitteeSecondHouseSecondRoundDate" value="bill_processed_reReferToSelectCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteeSecondHouseSecondRoundExpectedDate" value="bill_processed_reReferToSelectCommittee_${secondhouse}_secondhouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferSelectCommitteePresentedSecondHouseSecondRoundDate" value="bill_processed_reportOfReReferSelectCommitteePresented_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferSelectCommitteeSecondHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReReferSelectCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	<c:set var="reReferToJointCommitteeSecondHouseSecondRoundDate" value="bill_processed_reReferToJointCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteeSecondHouseSecondRoundExpectedDate" value="bill_processed_reReferToJointCommittee_${secondhouse}_secondhouse_2_expectedStatusDate"></c:set>
	<c:set var="reportOfReReferJointCommitteePresentedSecondHouseSecondRoundDate" value="bill_processed_reportOfReReferJointCommitteePresented_${secondhouse}_secondhouse_2_statusDate"></c:set>
	<c:set var="consideredAsPerReportOfReReferJointCommitteeSecondHouseSecondRoundDate" value="bill_processed_consideredAsPerReportOfReReferJointCommittee_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	<c:set var="circulationForElicitingOpinionSecondHouseSecondRoundDate" value="bill_processed_circulationForElicitingOpinion_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	<c:set var="discussedClauseByClauseSecondHouseSecondRoundDate" value="bill_processed_discussedClauseByClause_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	<c:set var="passedInSecondHouseSecondRoundDate" value="bill_processed_passed_${secondhouse}_secondhouse_2_statusDate"></c:set>
	
	
	
	<table class="uiTable" style="width: 100%; border-right: 0px;">
		<tr>
			<td><spring:message code="bill.number" text="Bill Number"></spring:message></td>
			<td>${billNumber}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.title" text="Bill Title"></spring:message></td>
			<td>${billTitle}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.type" text="Bill Type"></spring:message></td>
			<td>${billType}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.submissionDate" text="Bill Submission Date"></spring:message></td>
			<td>${billSubmissionDate}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.members" text="Bill Members"></spring:message></td>
			<td>${billMemberNames}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.introductionDate" text="Bill Introduction Date"></spring:message></td>
			<td>${bill_processed_introduced_1_statusDate}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.gazettePublishingDate" text="Bill Gazette Publishing Date"></spring:message></td>
			<td>${gazettePublishDateMarathi}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.gazettePublishingDateForEnglishTranslation" text="Bill Gazette Publishing Date For English Translation"></spring:message></td>
			<td>${gazettePublishDateEnglish}</td>										
		</tr>
		<tr>
			<td><spring:message code="bill.gazettePublishingDateForHindiTranslation" text="Bill Gazette Publishing Date For Hindi Translation"></spring:message></td>
			<td>${gazettePublishDateHindi}</td>										
		</tr>
		<c:if test="${not empty requestScope[consideredInFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.considered.${firsthouse}FirstRoundDate" text="Date of Bill Considered in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[consideredInFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[withdrawnInFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.withdrawn.${firsthouse}FirstRoundDate" text="Date of Bill Withdrawn in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[withdrawnInFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[negativedByFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.negatived.${firsthouse}FirstRoundDate" text="Date of Bill Negatived in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[negativedByFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToSelectCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToSelectCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Referred To Select Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[referToSelectCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteeFirstHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommittee.${firsthouse}FirstRoundExpectedDate" text="Expected Date of Report Of Refer Select Committee To Be Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteeFirstHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteePresentedFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommitteePresented.${firsthouse}FirstRoundDate" text="Date of Report Of Refer Select Committee Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteePresentedFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferSelectCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferSelectCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Refer Select Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferSelectCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToJointCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToJointCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Referred To Joint Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[referToJointCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteeFirstHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommittee.${firsthouse}FirstRoundExpectedDate" text="Expected Date of Report Of Refer Joint Committee To Be Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteeFirstHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteePresentedFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommitteePresented.${firsthouse}FirstRoundDate" text="Date of Report Of Refer Joint Committee Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteePresentedFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferJointCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferJointCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Refer Joint Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferJointCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reReferToSelectCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToSelectCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Re-Referred To Select Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reReferToSelectCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteeFirstHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommittee.${firsthouse}FirstRoundExpectedDate" text="Expected Date of Report Of Re-Refer Select Committee To Be Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteeFirstHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteePresentedFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommitteePresented.${firsthouse}FirstRoundDate" text="Date of Report Of Re-Refer Select Committee Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteePresentedFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferSelectCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferSelectCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Select Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferSelectCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>		
		<c:if test="${not empty requestScope[reReferToJointCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToJointCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Re-Referred To Joint Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reReferToJointCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteeFirstHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommittee.${firsthouse}FirstRoundExpectedDate" text="Expected Date of Report Of Re-Refer Joint Committee To Be Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteeFirstHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteePresentedFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommitteePresented.${firsthouse}FirstRoundDate" text="Date of Report Of Re-Refer Joint Committee Presented in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteePresentedFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferJointCommitteeFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferJointCommittee.${firsthouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Joint Committee in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferJointCommitteeFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[discussedClauseByClauseFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.discussedClauseByClause.${firsthouse}FirstRoundDate" text="Date of Bill Discussed Clause By Clause in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[discussedClauseByClauseFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[passedInFirstHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.passed.${firsthouse}FirstRoundDate" text="Date of Bill Passed in ${firsthouse} first time"></spring:message></td>
			<td>${requestScope[passedInFirstHouseFirstRoundDate]}</td>
		</tr>
		</c:if>		
		
		<c:if test="${not empty transmissionFromFirstHouseFirstRoundDate}">
		<tr>
			<td><spring:message code="bill.transmissionFromFirstHouseFirstRoundDate.${firsthouse}" text="Date of transmission from ${firsthouse} first time"></spring:message></td>
			<td>${transmissionFromFirstHouseFirstRoundDate}</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty requestScope[consideredInSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.considered.${secondhouse}FirstRoundDate" text="Date of Bill Considered in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[consideredInSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[withdrawnInSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.withdrawn.${secondhouse}FirstRoundDate" text="Date of Bill Withdrawn in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[withdrawnInSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[negativedBySecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.negatived.${secondhouse}FirstRoundDate" text="Date of Bill Negatived in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[negativedBySecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToSelectCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToSelectCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Referred To Select Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[referToSelectCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteeSecondHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommittee.${secondhouse}FirstRoundExpectedDate" text="Expected Date of Report Of Refer Select Committee To Be Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteeSecondHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteePresentedSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommitteePresented.${secondhouse}FirstRoundDate" text="Date of Report Of Refer Select Committee Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteePresentedSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferSelectCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferSelectCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Refer Select Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferSelectCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToJointCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToJointCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Referred To Joint Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[referToJointCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteeSecondHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommittee.${secondhouse}FirstRoundExpectedDate" text="Expected Date of Report Of Refer Joint Committee To Be Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteeSecondHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteePresentedSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommitteePresented.${secondhouse}FirstRoundDate" text="Date of Report Of Refer Joint Committee Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteePresentedSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferJointCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferJointCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Refer Joint Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferJointCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reReferToSelectCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToSelectCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Re-Referred To Select Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reReferToSelectCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteeSecondHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommittee.${secondhouse}FirstRoundExpectedDate" text="Expected Date of Report Of Re-Refer Select Committee To Be Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteeSecondHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteePresentedSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommitteePresented.${secondhouse}FirstRoundDate" text="Date of Report Of Re-Refer Select Committee Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteePresentedSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferSelectCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferSelectCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Select Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferSelectCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>		
		<c:if test="${not empty requestScope[reReferToJointCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToJointCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Re-Referred To Joint Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reReferToJointCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteeSecondHouseFirstRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommittee.${secondhouse}FirstRoundExpectedDate" text="Expected Date of Report Of Re-Refer Joint Committee To Be Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteeSecondHouseFirstRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteePresentedSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommitteePresented.${secondhouse}FirstRoundDate" text="Date of Report Of Re-Refer Joint Committee Presented in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteePresentedSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferJointCommitteeSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferJointCommittee.${secondhouse}FirstRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Joint Committee in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferJointCommitteeSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[discussedClauseByClauseSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.discussedClauseByClause.${secondhouse}FirstRoundDate" text="Date of Bill Discussed Clause By Clause in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[discussedClauseByClauseSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[passedInSecondHouseFirstRoundDate]}">
		<tr>
			<td><spring:message code="bill.passed.${secondhouse}FirstRoundDate" text="Date of Bill Passed in ${secondhouse} first time"></spring:message></td>
			<td>${requestScope[passedInSecondHouseFirstRoundDate]}</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty transmissionFromSecondHouseFirstRoundDate}">
		<tr>
			<td><spring:message code="bill.transmissionFromSecondHouseFirstRoundDate.${secondhouse}" text="Date of transmission from ${secondhouse} first time"></spring:message></td>
			<td>${transmissionFromSecondHouseFirstRoundDate}</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty requestScope[consideredInFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.considered.${firsthouse}SecondRoundDate" text="Date of Bill Considered in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[consideredInFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[withdrawnInFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.withdrawn.${firsthouse}SecondRoundDate" text="Date of Bill Withdrawn in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[withdrawnInFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[negativedByFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.negatived.${firsthouse}SecondRoundDate" text="Date of Bill Negatived in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[negativedByFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToSelectCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToSelectCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Referred To Select Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[referToSelectCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteeFirstHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommittee.${firsthouse}SecondRoundExpectedDate" text="Expected Date of Report Of Refer Select Committee To Be Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteeFirstHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteePresentedFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommitteePresented.${firsthouse}SecondRoundDate" text="Date of Report Of Refer Select Committee Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteePresentedFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferSelectCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferSelectCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Refer Select Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferSelectCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToJointCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToJointCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Referred To Joint Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[referToJointCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteeFirstHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommittee.${firsthouse}SecondRoundExpectedDate" text="Expected Date of Report Of Refer Joint Committee To Be Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteeFirstHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteePresentedFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommitteePresented.${firsthouse}SecondRoundDate" text="Date of Report Of Refer Joint Committee Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteePresentedFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferJointCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferJointCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Refer Joint Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferJointCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reReferToSelectCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToSelectCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Re-Referred To Select Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reReferToSelectCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteeFirstHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommittee.${firsthouse}SecondRoundExpectedDate" text="Expected Date of Report Of Re-Refer Select Committee To Be Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteeFirstHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteePresentedFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommitteePresented.${firsthouse}SecondRoundDate" text="Date of Report Of Re-Refer Select Committee Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteePresentedFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferSelectCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferSelectCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Select Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferSelectCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>		
		<c:if test="${not empty requestScope[reReferToJointCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToJointCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Re-Referred To Joint Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reReferToJointCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteeFirstHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommittee.${firsthouse}SecondRoundExpectedDate" text="Expected Date of Report Of Re-Refer Joint Committee To Be Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteeFirstHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteePresentedFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommitteePresented.${firsthouse}SecondRoundDate" text="Date of Report Of Re-Refer Joint Committee Presented in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteePresentedFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferJointCommitteeFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferJointCommittee.${firsthouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Joint Committee in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferJointCommitteeFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[discussedClauseByClauseFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.discussedClauseByClause.${firsthouse}SecondRoundDate" text="Date of Bill Discussed Clause By Clause in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[discussedClauseByClauseFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[passedInFirstHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.passed.${firsthouse}SecondRoundDate" text="Date of Bill Passed in ${firsthouse} second time"></spring:message></td>
			<td>${requestScope[passedInFirstHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty transmissionFromFirstHouseSecondRoundDate}">
		<tr>
			<td><spring:message code="bill.transmissionFromFirstHouseSecondRoundDate.${firsthouse}" text="Date of transmission from ${firsthouse} second time"></spring:message></td>
			<td>${transmissionFromFirstHouseSecondRoundDate}</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty requestScope[consideredInSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.considered.${secondhouse}SecondRoundDate" text="Date of Bill Considered in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[consideredInSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[withdrawnInSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.withdrawn.${secondhouse}SecondRoundDate" text="Date of Bill Withdrawn in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[withdrawnInSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[negativedBySecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.negatived.${secondhouse}SecondRoundDate" text="Date of Bill Negatived in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[negativedBySecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToSelectCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToSelectCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Referred To Select Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[referToSelectCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteeSecondHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommittee.${secondhouse}SecondRoundExpectedDate" text="Expected Date of Report Of Refer Select Committee To Be Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteeSecondHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferSelectCommitteePresentedSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferSelectCommitteePresented.${secondhouse}SecondRoundDate" text="Date of Report Of Refer Select Committee Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferSelectCommitteePresentedSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferSelectCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferSelectCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Refer Select Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferSelectCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[referToJointCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.referToJointCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Referred To Joint Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[referToJointCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteeSecondHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommittee.${secondhouse}SecondRoundExpectedDate" text="Expected Date of Report Of Refer Joint Committee To Be Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteeSecondHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReferJointCommitteePresentedSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReferJointCommitteePresented.${secondhouse}SecondRoundDate" text="Date of Report Of Refer Joint Committee Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReferJointCommitteePresentedSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReferJointCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReferJointCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Refer Joint Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReferJointCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reReferToSelectCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToSelectCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Re-Referred To Select Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reReferToSelectCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteeSecondHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommittee.${secondhouse}SecondRoundExpectedDate" text="Expected Date of Report Of Re-Refer Select Committee To Be Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteeSecondHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferSelectCommitteePresentedSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferSelectCommitteePresented.${secondhouse}SecondRoundDate" text="Date of Report Of Re-Refer Select Committee Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferSelectCommitteePresentedSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferSelectCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferSelectCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Select Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferSelectCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>		
		<c:if test="${not empty requestScope[reReferToJointCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reReferToJointCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Re-Referred To Joint Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reReferToJointCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteeSecondHouseSecondRoundExpectedDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommittee.${secondhouse}SecondRoundExpectedDate" text="Expected Date of Report Of Re-Refer Joint Committee To Be Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteeSecondHouseSecondRoundExpectedDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[reportOfReReferJointCommitteePresentedSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.reportOfReReferJointCommitteePresented.${secondhouse}SecondRoundDate" text="Date of Report Of Re-Refer Joint Committee Presented in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[reportOfReReferJointCommitteePresentedSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[consideredAsPerReportOfReReferJointCommitteeSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.consideredAsPerReportOfReReferJointCommittee.${secondhouse}SecondRoundDate" text="Date of Bill Considered As Per Report Of Re-Refer Joint Committee in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[consideredAsPerReportOfReReferJointCommitteeSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[discussedClauseByClauseSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.discussedClauseByClause.${secondhouse}SecondRoundDate" text="Date of Bill Discussed Clause By Clause in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[discussedClauseByClauseSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>
		<c:if test="${not empty requestScope[passedInSecondHouseSecondRoundDate]}">
		<tr>
			<td><spring:message code="bill.passed.${secondhouse}SecondRoundDate" text="Date of Bill Passed in ${secondhouse} second time"></spring:message></td>
			<td>${requestScope[passedInSecondHouseSecondRoundDate]}</td>
		</tr>
		</c:if>	
		<c:if test="${not empty transmissionFromSecondHouseSecondRoundDate}">
		<tr>
			<td><spring:message code="bill.transmissionFromSecondHouseSecondRoundDate.${secondhouse}" text="Date of transmission from ${secondhouse} second time"></spring:message></td>
			<td>${transmissionFromSecondHouseSecondRoundDate}</td>
		</tr>
		</c:if>
		<c:if test="${not empty bill_processed_passedByBothHouses_statusDate}">
		<tr>
			<td><spring:message code="bill.bill_processed_passedByBothHouses" text="Date of Bill Passed By Both Houses"></spring:message></td>
			<td>${bill_processed_passedByBothHouses_statusDate}</td>
		</tr>
		</c:if>		
		<c:if test="${not empty bill_processed_sentToLawAndJudiciaryForAscenting_statusDate}">
		<tr>
			<td><spring:message code="bill.bill_processed_sentToLawAndJudiciaryForAscenting" text="Date of Bill Sent To Law And Judiciary For Ascenting"></spring:message></td>
			<td>${bill_processed_sentToLawAndJudiciaryForAscenting_statusDate}</td>
		</tr>
		</c:if>
		<c:if test="${not empty bill_processed_acceptedByGovernorOrPresident_statusDate}">
		<tr>
			<td><spring:message code="bill.bill_processed_acceptedByGovernorOrPresident" text="Date of Bill Accepted By Governor/President"></spring:message></td>
			<td>${bill_processed_acceptedByGovernorOrPresident_statusDate}</td>
		</tr>
		</c:if>
		<c:if test="${not empty bill_processed_convertedToAct_statusDate}">
		<tr>
			<td><spring:message code="bill.bill_processed_convertedToAct" text="Date of Bill Converted To Act"></spring:message></td>
			<td>${bill_processed_convertedToAct_statusDate}</td>
		</tr>
		</c:if>
	</table>		
</body>
</html>