/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.util.ApplicationConstants.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.common.util;


// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationConstants.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class ApplicationConstants {

	/** The Constant ASC. */
	public static final String ASC = "asc";

	/** The Constant DESC. */
	public static final String DESC = "desc";

	/** The Constant ALL_LOCALE. */
	public static final String ALL_LOCALE = "all";

	/** The Constant DEFAULT_LOCALE. */
	public static final String DEFAULT_LOCALE = "mr_IN";

	/** The Constant LOWER_HOUSE. */
	public static final String LOWER_HOUSE="lowerhouse";

	/** The Constant UPPER_HOUSE. */
	public static final String UPPER_HOUSE="upperhouse";

	/** The Constant BOTH_HOUSE. */
	public static final String BOTH_HOUSE="bothhouse";

	/** The Constant DEFAULT_HOUSE. */
	public static final String DEFAULT_HOUSE="defaulthouse";

	/** The Constant en_US_INFONOTFOUND. */
	public static final String en_US_INFONOTFOUND="Information Not Available For";	

	/** The Constant en_US_INFOFOUND. */
	public static final String en_US_INFOFOUND="Information Available For";

	/** The Constant SERVER_DATEFORMAT. */
	public static final String SERVER_DATEFORMAT = "dd/MM/yyyy";

	/** The Constant en_US_LOWERHOUSE_DEAFULTROLE. */
	public static final String en_US_LOWERHOUSE_DEAFULTROLE="Member";

	/** The Constant en_US_UPPERHOUSE_DEAFULTROLE. */
	public static final String en_US_UPPERHOUSE_DEAFULTROLE="Member";	

	/** The Constant DB_DATEFORMAT. */
	public static final String DB_DATEFORMAT="yyyy-MM-dd";

	/** The Constant en_US_DAUGHTER. */
	public static final String DAUGHTER="Daughter";

	/** The Constant en_US_SON. */
	public static final String SON="Son";	

	/** The Constant en_US_WIFE. */
	public static final String WIFE="Wife";

	/** The Constant en_US_HUSBAND. */
	public static final String HUSBAND="Husband";	

	/** The Constant DEFAULT_FROM_DATE_LOCALE_en_US. */
	public static final String DEFAULT_FROM_DATE_LOCALE_en_US="01/12/1950";

	/** The Constant DEFAULT_TO_DATE_LOCALE_en_US. */
	public static final String DEFAULT_TO_DATE_LOCALE_en_US="31/12/1950";

	/** The Constant LOWERHOUSEGRID. */
	public static final String LOWERHOUSEGRID="MEMBER_LOWERHOUSEGRID";

	/** The Constant UPPERHOUSEGRID. */
	public static final String UPPERHOUSEGRID="MEMBER_UPPERHOUSEGRID";
	//related to question module
	/** The Constant QIS_ACTOR_LIST_CUSTOMPARAM_NAME. */
	public static final String QIS_ACTOR_LIST_CUSTOMPARAM_NAME="QIS_ACTOR_LIST";

	//parameters for starred question

	//Session Keys
	public static final String QUESTIONS_STARRED_TOTALROUNDS_MEMBERBALLOT="questions_starred_totalRoundsMemberBallot";

	public static final String QUESTIONS_STARRED_TOTALROUNDS_FINALBALLOT="questions_starred_totalRoundsFinalBallot";

	/**** Question Types ****/
	public static final String STARRED_QUESTION="questions_starred";

	public static final String UNSTARRED_QUESTION="questions_unstarred";

	public static final String SHORT_NOTICE_QUESTION="questions_shortnotice";

	public static final String HALFHOURDISCUSSIONSTANDALONE_MEMBER_MAX_PUTUP_COUNT_LH = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_LH";
	
	public static final String HALFHOURDISCUSSIONSTANDALONE_MEMBER_MAX_PUTUP_COUNT_UH = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_UH";
	
	public static final String HALF_HOUR_DISCUSSION_QUESTION_STANDALONE="questions_halfhourdiscussion_standalone";

	public static final String HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION="questions_halfhourdiscussion_from_question";

	public static final String HALF_HOUR_DISCUSSION_QUESTION_STANDALONE_ROLE="HDS_ASSISTANT";
	
	/**** Question Status ****/
	/**** Member *****/
	public static final String QUESTION_INCOMPLETE="question_incomplete";

	public static final String QUESTION_COMPLETE="question_complete";

	public static final String QUESTION_SUBMIT="question_submit";    
	/**** Supporting Member ****/
	public static final String SUPPORTING_MEMBER_TIMEOUT="supportingmember_timeout";
	
	public static final String SUPPORTING_MEMBER_APPROVED="supportingmember_approved";

	public static final String SUPPORTING_MEMBER_REJECTED="supportingmember_rejected";

	public static final String SUPPORTING_MEMBER_PENDING="supportingmember_pending";

	public static final String SUPPORTING_MEMBER_NOTSEND="supportingmember_notsend";    
	/**** System ****/    
	public static final String QUESTION_SYSTEM_ASSISTANT_PROCESSED="question_system_assistantprocessed";

	public static final String QUESTION_SYSTEM_TO_BE_PUTUP="question_system_putup";

	public static final String QUESTION_SYSTEM_GROUPCHANGED="question_system_groupchanged";

	public static final String QUESTION_SYSTEM_CLUBBED="question_system_clubbed";

	public static final String QUESTION_SYSTEM_CLUBBED_WITH_PENDING="question_system_clubbedwithpending";
	/**** Recommendation ****/
	public static final String QUESTION_RECOMMEND_ADMISSION="question_recommend_admission";

	public static final String QUESTION_RECOMMEND_REJECTION="question_recommend_rejection";

	public static final String QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED="question_recommend_convertToUnstarred";

	public static final String QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT="question_recommend_convertToUnstarredAndAdmit";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER="question_recommend_clarificationNeededFromMember";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="question_recommend_clarificationNeededFromDepartment";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT="question_recommend_clarificationNeededFromGovt";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="question_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_RECOMMEND_NAMECLUBBING="question_recommend_nameclubbing";

	public static final String QUESTION_RECOMMEND_PUTONHOLD="question_recommend_putonhold";

	public static final String QUESTION_RECOMMEND_SENDBACK="question_recommend_sendback";

	public static final String QUESTION_RECOMMEND_DISCUSS="question_recommend_discuss";
	
	public static final String QUESTION_RECOMMEND_REPEATADMISSION="question_recommend_repeatadmission";
	
	public static final String QUESTION_RECOMMEND_REPEATREJECTION="question_recommend_repeatrejection";
	
	/**** Final ****/ 
	public static final String QUESTION_FINAL_ADMISSION="question_final_admission";

	public static final String QUESTION_FINAL_REJECTION="question_final_rejection";
	
	public static final String QUESTION_PROCESSED_REJECTIONWITHREASON="question_processed_rejectionWithReason";

	public static final String QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_final_convertToUnstarredAndAdmit";

	/** added for HDS ***/
	public static final String QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="question_final_clarificationNotReceivedFromMember";
	
	public static String QUESTION_PROCESSED_CLARIFICATION_RECIEVED="question_processed_clarificationReceived";
	
	public static final String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER="question_final_clarificationNeededFromMember";

	public static final String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_final_clarificationNeededFromDepartment";

	public static final String QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_DEPARTMENT="question_final_clarificationNotReceivedFromDepartment";
	
	public static final String QUESTION_FINAL_REPEATADMISSION="question_final_repeatadmission";
	
	public static final String QUESTION_FINAL_REPEATREJECTION="question_final_repeatrejection";
	
	public static final String QUESTION_PROCESSED_SENDTOSECTIONOFFICER = "question_processed_sendToSectionOfficer";
	
	public static final String QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_ASSEMBLY="QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_ASSEMBLY";

	public static final String QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL="QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL";
	
	//------------
	
	/**** Put Up ****/
	public static final String QUESTION_PUTUP_NAMECLUBBING="question_putup_nameclubbing";

	public static final String QUESTION_PUTUP_ONHOLD="question_putup_onhold";

	public static final String QUESTION_PUTUP_CONVERT_TO_UNSTARRED="question_putup_convertToUnstarred";

	public static final String QUESTION_PUTUP_REJECTION="question_putup_rejection";

	public static final String QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT="question_putup_convertToUnstarredAndAdmit";

	/**** Processed Status ****/
	public static final String QUESTION_PROCESSED_BALLOTED="question_processed_balloted";

	public static final String QUESTION_PROCESSED_YAADILAID="question_processed_yaadilaid";

	/**** Question Ministry,Department and Subdepartment parameters key in UserGroup ****/
	public static final String HOUSETYPE_KEY="HOUSETYPE";

	public static final String DEVICETYPE_KEY="DEVICETYPE";

	public static final String MINISTRY_KEY="MINISTRY";

	public static final String DEPARTMENT_KEY="DEPARTMENT";

	public static final String SUBDEPARTMENT_KEY="SUBDEPARTMENT";    

	/**** Approving Authority ****/
	public static final String CHAIRMAN="chairman";

	/**** User Group Types    ****/
	/**** Member			  ****/
	public static final String MEMBER="member";

	public static final String SPEAKER="speaker";   

	public static final String ASSISTANT="assistant";

	public static final String DEPARTMENT="department";

	public static final String SECTION_OFFICER="section_officer";

	public static final String UNDER_SECRETARY="under_secretary";

	/**** My Task Status ****/
	public static final String MYTASK_PENDING="PENDING";

	public static final String MYTASK_COMPLETED="COMPLETED";    
	/**** Workflow Types ****/
	public static final String APPROVAL_WORKFLOW="APPROVAL_WORKFLOW";
	
	public static final String RESOLUTION_APPROVAL_WORKFLOW = "RESOLUTION_APPROVAL_WORKFLOW";

	public static final String SUPPORTING_MEMBER_WORKFLOW="Supporting_Members_Approval_Process";    
	/**** URL Pattern of Various Workflows ****/
	public static final String APPROVAL_WORKFLOW_URLPATTERN="workflow/question";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_MOTION="workflow/motion";

	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN="workflow/question/supportingmember";

	public static final String REQUEST_TO_SUPPORTING_MEMBER="request_to_supporting_member";

	/**** Advanced Search Status Filter ****/
	public static final String UNPROCESSED_FILTER="UNPROCESSED";

	public static final String PENDING_FILTER="PENDING";

	public static final String APPROVED_FILTER="APPROVED";
	/**** Member Ballot Stored procedure ****/
	public static final String CLUBBING_UPDATE_PROCEDURE="memberballot_updateclubbing_procedure";

	public static final String DELETE_TEMP_PROCEDURE="memberballot_delete_tempentries_procedure";

	public static final String FINAL_BALLOT_PROCEDURE="memberballot_finalballot_procedure";

	public static final String FINAL_BALLOT_UPDATE_SEQUENCE_PROCEDURE="memberballot_finalballot_updatesequenceno_procedure";
	/**** Session parameters ****/
	public static final String QUESTION_STARRED_SUBMISSION_STARTTIME_LH="questions_starred_submissionStartDate";

	public static final String QUESTION_STARRED_SUBMISSION_ENDTIME_LH="questions_starred_submissionEndDate";

	public static final String QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH="questions_starred_submissionFirstBatchStartDate";

	public static final String QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH="questions_starred_submissionFirstBatchEndDate";

	public static final String QUESTION_STARRED_SECONDBATCH_SUBMISSION_STARTTIME_UH="questions_starred_submissionSecondBatchStartDate";

	public static final String QUESTION_STARRED_SECONDBATCH_SUBMISSION_ENDTIME_UH="questions_starred_submissionSecondBatchEndDate";

	public static final String QUESTION_STARRED_FIRST_BALLOT_DATE_UH="questions_starred_firstBallotDate";

	public static final String QUESTION_STARRED_NO_OF_QUESTIONS_FIRST_BATCH_UH="questions_starred_NumberOfQuestionInFirstBatch";

	public static final String QUESTION_STARRED_NO_OF_QUESTIONS_SECOND_BATCH_UH="questions_starred_NumberOfQuestionInSecondBatch";

	public static final String QUESTION_BALLOTING_REQUIRED="questions_starred_isBallotingRequired";

	public static final String QUESTION_STARRED_ROTATION_ORDER_PUBLISHING_DATE="questions_starred_rotationOrderPublishingDate";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_ATTENDANCE_UH="questions_starred_noOfRoundsMemberBallotAttendance";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH="questions_starred_noOfRoundsMemberBallot";  

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_BALLOT_LH="questions_starred_noOfRoundsBallot";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_BALLOT_UH="questions_starred_noOfRoundsMemberBallotFinal";

	public static final String QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS="questions_halfhourdiscussion_from_question_numberOfSupportingMembers";

	public static final String QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS_COMPARATOR = "questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator";

	public static final String QUESTION_HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS="questions_halfhourdiscussion_standalone_numberOfSupportingMembers";

	public static final String QUESTION_HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS_COMPARATOR = "questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator";
	
	public static final String QUESTIONS_HALFHOURDISCUSSION_STANDALONE_SUBMISSIONSTARTDATE = "questions_halfhourdiscussion_standalone_submissionStartDate";
	
	public static final String QUESTIONS_HALFHOURDISCUSSION_STANDALONE_SUBMISSIONENDDATE = "questions_halfhourdiscussion_standalone_submissionEndDate"; 

	/**** member ballot ****/
	public static final String MEMBERBALLOT_DELETE_EXISTING="DELETE";

	public static final String MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT="YES";


	/**** Member Ballot Final Ballot Procedures Names ****/
	public static final String MEMBERBALLOT_FINALBALLOT_HORIZONTAL_PROCEDURE="memberballot_finalballot_horizontalscanning_procedure";

	public static final String MEMBERBALLOT_FINALBALLOT_VERTICAL_PROCEDURE="memberballot_finalballot_verticalscanning_procedure";


	/****************Resolution Information System********************/
	public static final String RESOLUTION_INCOMPLETE="resolution_incomplete";

	public static final String RESOLUTION_COMPLETE="resolution_complete";

	public static final String RESOLUTION_SUBMIT="resolution_submit";

	public static final String NONOFFICIAL_RESOLUTION = "resolutions_nonofficial";

	public static final String GOVERNMENT_RESOLUTION = "resolutions_government";

	/**** System ****/    
	public static final String RESOLUTION_SYSTEM_ASSISTANT_PROCESSED="resolution_system_assistantprocessed";

	public static final String RESOLUTION_SYSTEM_TO_BE_PUTUP="resolution_system_putup";

	/**** Recommendation ****/
	public static final String RESOLUTION_RECOMMEND_ADMISSION="resolution_recommend_admission";

	public static final String RESOLUTION_RECOMMEND_REJECTION="resolution_recommend_rejection";


	public static final String RESOLUTION_RECOMMEND_SENDBACK="resolution_recommend_sendback";

	public static final String RESOLUTION_RECOMMEND_DISCUSS="resolution_recommend_discuss";

	public static final String RESOLUTION_RECOMMEND_REPEAT="resolution_recommend_repeat";
	
	public static final String RESOLUTION_PROCESSED_SENDTOSECTIONOFFICER = "resolution_processed_sendToSectionOfficer";
	
	/**** Final ****/ 
	public static final String RESOLUTION_FINAL_ADMISSION="resolution_final_admission";    

	public static final String RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER="resolution_final_clarificationNeededFromMember";

	public static final String RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT="resolution_final_clarificationNeededFromDepartment";

	public static final String RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT="resolution_final_clarificationNotReceivedFromDepartment";
	
	public static final String RESOLUTION_FINAL_REJECTION="resolution_final_rejection";

	public static final String RESOLUTION_PUTUP_REJECTION="resolution_putup_rejection";

	public static final String RESOLUTION_FINAL_REPEAT="resolution_final_repeat";

	public static final String RIS_APPROVAL_WORKFLOW_URLPATTERN="workflow/resolution";

	/** Prefix of questions **/
	public static final String DEVICE_QUESTIONS="questions_";
	
	/**** Prefix of motions ****/
	public static final String DEVICE_MOTIONS="motions_";

	/** Prefix of resolutions **/
	public static final String DEVICE_RESOLUTIONS="resolutions_";

	public static final String RESOLUTION_LOWERHOUSEGRID="resolution_lowerhouse";

	/** The Constant UPPERHOUSEGRID. */
	public static final String RESOLUTION_UPPERHOUSEGRID="resolution_upperhouse";

	public static final String GOVERNMENT_RESOLUTION_LOWERHOUSEGRID="resolution_government_lowerhouse";

	public static final String GOVERNMENT_RESOLUTION_UPPERHOUSEGRID="resolution_government_upperhouse";

	public static String RESOLUTION_NONOFFICIAL_SUBMISSION_STARTDATE="resolutions_nonofficial_submissionStartDate";

	public static String RESOLUTION_NONOFFICIAL_SUBMISSION_ENDDATE="resolutions_nonofficial_submissionEndDate";

	public static String RESOLUTION_PROCESSED_CLARIFICATIONRECIEVED="resolution_processed_clarificationReceived";

	public static String RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="resolution_recommend_clarificationNeededFromDepartment";

	public static String RESOLUTION_RECOMMEND_CLARIFICATION_FROM_GOVT="resolution_recommend_clarificationNeededFromGovt";

	public static String RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER="resolution_recommend_clarificationNeededFromMember";

	public static String RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="resolution_recommend_clarificationNeededFromMemberAndDepartment";
	
	public static String RESOLUTION_PROCESSED_CLARIFICATIONNOTRECIEVED="resolution_processed_clarificationNotReceived";

	public static String CATEGORY_MASTER="CATEGORY_MASTER";

	public static final String RESOLUTION_RECOMMEND_REPEATADMISSION="resolution_recommend_repeatadmission";
	
	public static final String RESOLUTION_RECOMMEND_REPEATREJECTION="resolution_recommend_repeatrejection";
	
	public static final String RESOLUTION_FINAL_REPEATADMISSION="resolution_final_repeatadmission";
	
	public static final String RESOLUTION_FINAL_REPEATREJECTION="resolution_final_repeatrejection";

	public static final String RESOLUTION_PROCESSED_BALLOTED="resolution_processed_balloted";

	public static final String RESOLUTION_PROCESSED_TOBEDISCUSSED="resolution_processed_tobediscussed";

	public static final String RESOLUTION_FINAL_TOBEDISCUSSED="resolution_final_tobediscussed";

	public static final String RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_ASSEMBLY="RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_ASSEMBLY";

	public static final String RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_COUNCIL="RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_COUNCIL";

	public static final String RESOLUTION_NONOFFICIAL_SESSIONS_TOBE_SEARCHED_COUNT="RESOLUTION_NONOFFICIAL_SESSIONS_TOBE_SEARCHED_COUNT";

	/**** Motion ****/	
	
	/**** Member View ****/	
	public static final String MOTION_INCOMPLETE="motion_incomplete";

	public static final String MOTION_COMPLETE="motion_complete";

	public static final String MOTION_SUBMIT="motion_submit"; 
	
	/**** System ****/    
	public static final String MOTION_SYSTEM_ASSISTANT_PROCESSED="motion_system_assistantprocessed";

	public static final String MOTION_SYSTEM_TO_BE_PUTUP="motion_system_putup";

	public static final String MOTION_SYSTEM_CLUBBED="motion_system_clubbed";

	public static final String MOTION_SYSTEM_CLUBBED_WITH_PENDING="motion_system_clubbedwithpending";
	
	/**** Recommendation ****/
	public static final String MOTION_RECOMMEND_ADMISSION="motion_recommend_admission";

	public static final String MOTION_RECOMMEND_REJECTION="motion_recommend_rejection";

	public static final String MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER="motion_recommend_clarificationNeededFromMember";

	public static final String MOTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="motion_recommend_clarificationNeededFromDepartment";

	public static final String MOTION_RECOMMEND_CLARIFICATION_FROM_GOVT="motion_recommend_clarificationNeededFromGovt";

	public static final String MOTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="motion_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String MOTION_RECOMMEND_NAMECLUBBING="motion_recommend_nameclubbing";

	public static final String MOTION_RECOMMEND_PUTONHOLD="motion_recommend_putonhold";

	public static final String MOTION_RECOMMEND_SENDBACK="motion_recommend_sendback";

	public static final String MOTION_RECOMMEND_DISCUSS="motion_recommend_discuss";
	
	/**** Final ****/ 
	public static final String MOTION_FINAL_ADMISSION="motion_final_admission";

	public static final String MOTION_FINAL_REJECTION="motion_final_rejection";

	public static final String MOTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="motion_final_clarificationNotReceivedFromMember";
	
	/**** Put Up ****/
	public static final String MOTION_PUTUP_NAMECLUBBING="motion_putup_nameclubbing";

	public static final String MOTION_PUTUP_ONHOLD="motion_putup_onhold";

	public static final String MOTION_PUTUP_REJECTION="motion_putup_rejection";
	
	/**** Supporting Member ****/
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_MOTION="workflow/motion/supportingmember";

	public static final String QUESTION_FINAL_NAMECLUBBING = "question_final_nameclubbing";

	public static final String BALLOT_REPORT_DATE_FORMAT = "dd-MM-yyyy";
	
	public static final String WORKFLOWCONFIG_REMOVEACTOR_WFCONFIG_WFACTORS_QUERY = "WORKFLOWCONFIG_REMOVEACTOR_WFCONFIG_WFACTORS_QUERY";

	public static final String WORKFLOWCONFIG_REMOVEACTOR_WORKFLOWACTORS_QUERY = "WORKFLOWCONFIG_REMOVEACTOR_WORKFLOWACTORS_QUERY";

	public static final String MINISTRY_FIND_MINISTRIES_ASSIGNED_TO_GROUPS_QUERY = "MINISTRY_FIND_MINISTRIES_ASSIGNED_TO_GROUPS_QUERY";

	public static final String SESSION_GET_PARAMETER_SET_FOR_DEVICETYPE_QUERY = "SESSION_GET_PARAMETER_SET_FOR_DEVICETYPE_QUERY";

	public static final String RESOLUTION_GET_REVISION = "RESOLUTION_GET_REVISION";

	public static final String RESOLUTION_GET_REVISION_WITH_WORKFLOWHOUSETYPE = "RESOLUTION_GET_REVISION_WITH_WORKFLOWHOUSETYPE";

	public static final String RESOLUTION_GET_LATEST_RESOLUTIONDRAFT_OF_USER = "RESOLUTION_GET_LATEST_RESOLUTIONDRAFT_OF_USER";

	public static final String QUESTION_GET_REVISION = "QUESTION_GET_REVISION";

	public static final String QUESTION_GET_LATEST_QUESTIONDRAFT_OF_USER = "QUESTION_GET_LATEST_QUESTIONDRAFT_OF_USER";

	public static final String QUESTION_FIND_MEMBERWISE_REPORTVO_COUNTQUERY = "QUESTION_FIND_MEMBERWISE_REPORTVO_COUNTQUERY";

	public static final String QUESTION_FIND_MEMBERWISE_REPORTVO_QUESTIONQUERY = "QUESTION_FIND_MEMBERWISE_REPORTVO_QUESTIONQUERY";

	public static final String MOTION_GET_REVISION = "MOTION_GET_REVISION";

	public static final String MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO = "MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO";

	public static final String MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO_WITH_GROUP = "MEMBERMINISTER_FIND_ASSIGNED_SUBDEPARTMENTSVO_WITH_GROUP";

	public static final String MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO = "MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO";

	public static final String MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO_WITH_GROUP = "MEMBERMINISTER_FIND_ASSIGNED_DEPARTMENTSVO_WITH_GROUP";

	public static final String MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION = "MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION";

	public static final String MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION_WITH_PARAM = "MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION_WITH_PARAM";
	
	/**** role types for comparison ****/
	
	public static final String MINISTER = "MINISTER";
	
	/**** member role types for comparison ****/
	public static final String CHIEF_MINISTER = "CHIEF_MINISTER";
	
	public static final String DEPUTY_CHIEF_MINISTER = "DEPUTY_CHIEF_MINISTER";
	
	
	/**** Chart related Constants ****/
	/**** Resolution Chart Constants ****/
	public static final String RESOLUTION_CHART_WITHDEVICES_VIEW = "RESOLUTION_CHART_WITHDEVICES_VIEW";
	
	public static final String RESOLUTION_CHART_WITHOUTDEVICES_VIEW = "RESOLUTION_CHART_WITHOUTDEVICES_VIEW";
	
	public static final String RESOLUTION_CHART_VIEW = "RESOLUTION_CHART_VIEW";
	
	/**** COMMITTEE ****/
	public static final String RULING_PARTY = "ruling_party";
	
	public static final String OPPOSITION_PARTY = "opposition_party";
	
	public static final String INDEPENDENT_PARTY = "independent";
	
	public static final String COMMITTEE_CREATED = "committee_created";

	public static final String COMMITTEE_RECOMMEND_SENDBACK = "committee_recommend_sendback";
	
	public static final String COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER = "committeeMemberAdditionRequestToParliamentaryAffairsMinister";
	
	public static final String COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION = "committeeMemberAdditionRequestToLeaderOfOpposition";
	
	public static final String COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW = "COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW";
	
	public static final int WORKFLOW_START_LEVEL = 1;
	
	public static final String COMMITTEE = "COMMITTEE";
	
	public static final String COMMITTEE_MEMBER_ADDITION_URL = "workflow/committee/memberAddition";
	
	public static final String COMMITTEE_CHAIRMAN = "comittee_chairman";
	
	public static final String COMMITTEE_MEMBER = "committee_member";
	
	public static final String COMMITTEE_INVITED_MEMBER = "committee_invited_member";
}
