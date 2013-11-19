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
	
	public static final String DB_DATETIME_FORMAT="yyyy-MM-dd hh:mm:ss";

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
	public static final String QUESTION_FINAL_REANSWER = "question_final_reanswer";
	
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
	
	public static final String TRANSLATOR = "translator";
	
	public static final String OPINION_ABOUT_BILL_DEPARTMENT = "opinionAboutBill_department";
	
	public static final String RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT = "recommendationFromGovernor_department";
	
	public static final String RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT = "recommendationFromPresident_department";

	public static final String PRESS = "press";

	/**** My Task Status ****/
	public static final String MYTASK_PENDING="PENDING";

	public static final String MYTASK_COMPLETED="COMPLETED";    
	/**** Workflow Types ****/
	public static final String APPROVAL_WORKFLOW="APPROVAL_WORKFLOW";
	
	public static final String RESOLUTION_APPROVAL_WORKFLOW = "RESOLUTION_APPROVAL_WORKFLOW";

	public static final String SUPPORTING_MEMBER_WORKFLOW="Supporting_Members_Approval_Process";    
	
	public static final String TRANSLATION_WORKFLOW="TRANSLATION_WORKFLOW";
	
	public static final String OPINION_FROM_LAWANDJD_WORKFLOW="OPINION_FROM_LAWANDJD_WORKFLOW";
	
	public static final String RECOMMENDATION_FROM_GOVERNOR_WORKFLOW="RECOMMENDATION_FROM_GOVERNOR_WORKFLOW";
	
	public static final String RECOMMENDATION_FROM_PRESIDENT_WORKFLOW="RECOMMENDATION_FROM_PRESIDENT_WORKFLOW";
	
	public static final String REQUISITION_TO_PRESS_POST_ADMISSION_WORKFLOW = "REQUISITION_TO_PRESS_POST_ADMISSION_WORKFLOW";
	
	public static final String REQUISITION_TO_PRESS_POST_PASSEDBYFIRSTHOUSE_WORKFLOW = "REQUISITION_TO_PRESS_POST_PASSEDBYFIRSTHOUSE_WORKFLOW";
	
	public static final String REQUISITION_TO_PRESS_FOR_GAZETTE_WORKFLOW = "REQUISITION_TO_PRESS_FOR_GAZETTE_WORKFLOW";
	
	public static final String REQUISITION_TO_PRESS_WORKFLOW = "REQUISITION_TO_PRESS_WORKFLOW";
	
	public static final String NAMECLUBBING_WORKFLOW="NAMECLUBBING_WORKFLOW";
	
	public static final String TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE_WORKFLOW = "TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE_WORKFLOW";
	
	public static final String SEND_FOR_ENDORSEMENT_WORKFLOW = "SEND_FOR_ENDORSEMENT_WORKFLOW";
	
	public static final String TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW = "TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW";
	
	public static final String TRANSMIT_PRESS_COPIES_WORKFLOW = "TRANSMIT_PRESS_COPIES_WORKFLOW";
	
	public static final String LAY_LETTER_WORKFLOW = "LAY_LETTER_WORKFLOW";
	/**** URL Pattern of Various Workflows ****/
	public static final String APPROVAL_WORKFLOW_URLPATTERN="workflow/question";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_MOTION="workflow/motion";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_BILL="workflow/bill";

	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN="workflow/question/supportingmember";

	public static final String REQUEST_TO_SUPPORTING_MEMBER="request_to_supporting_member";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILL="workflow/bill/supportingmember";
	
	public static final String SEND_FOR_ENDORSEMENT_WORKFLOW_URLPATTERN_BILL="workflow/bill/transmitpresscopies";
	
	public static final String TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW_URLPATTERN_BILL="workflow/bill/transmitpresscopies";
	
	public static final String TRANSMIT_PRESS_COPIES_WORKFLOW_URLPATTERN_BILL="workflow/bill/transmitpresscopies";

	public static final String LAY_LETTER_WORKFLOW_URLPATTERN_BILL="workflow/bill/layletter";

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
	
	/** Prefix of bills **/
	public static final String DEVICE_BILLS="bills_";
	
	/** Prefix of acts **/
	public static final String DEVICE_ACTS="act";

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
	
	public static String DEPUTY_SPEAKER="DEPUTY_SPEAKER";

	public static String DEPUTY_CHAIRMAN="DEPUTY_CHAIRMAN";
	
	/****Reporting*******/
	public static String PROCEEDING_SEARCHOPTION="PROCEEDING_SEARCHOPTIONS";

	public static String PROCEEDING_BOOKMARKKEY="PROCEEDING_BOOKMARK_KEY";

	public static String PROCEEDING_CONTENT_MERGE_REPORT="RIS_PROCEEDING_CONTENT_MERGE_REPORT2";

	public static String RIS_SLOT_WISE_REPORT="RIS_SLOT_WISE_REPORT";

	public static String RIS_SESSION_WISE_REPORT="RIS_SESSION_WISE_REPORT";

	public static String RIS_REPORTER_WISE_REPORT="RIS_REPORTER_WISE_REPORT";

	public static String RIS_MEMBER_WISE_REPORT="RIS_MEMBER_WISE_REPORT";
	
	public static String RIS_MEMBER_WISE_REPORT2="RIS_MEMBER_WISE_REPORT2";

	public static String PAGE_HEADING="pageHeading";
	
	public static String MAIN_HEADING="mainHeading";

	public static String REPORTING="reporting";
	
	/****Custom Parameters name ****/
	public static final String RESOLUTION_CHART_WITHDEVICES_VIEW = "RESOLUTION_CHART_WITHDEVICES_VIEW";
	
	public static final String RESOLUTION_CHART_WITHOUTDEVICES_VIEW = "RESOLUTION_CHART_WITHOUTDEVICES_VIEW";
	
	public static final String RESOLUTION_CHART_VIEW = "RESOLUTION_CHART_VIEW";
	
	public static final String MAX_ASWERING_ATTEMPTS_STARRED = "MAX_ASWERING_ATTEMPTS_STARRED";
	
	public static final String DEPARTMENT_SENDBACK_TIME_LIMIT = "DEPARTMENT_SENDBACK_TIME_LIMIT"; 
	
	/**** Rotation order ministry report query key_field ****/
	public static final String ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT="ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT";
	
	/**** COMMITTEE ****/
public static final String RULING_PARTY = "ruling_party";
	
	public static final String OPPOSITION_PARTY = "opposition_party";
	
	public static final String INDEPENDENT_PARTY = "independent";
	
	public static final String COMMITTEE_CREATED = "committee_created";

	public static final String COMMITTEE_RECOMMEND_SENDBACK = "committee_recommend_sendback";
	
	public static final String COMMITTEE_PROCESSED_SENDBACK = "committee_processed_sendback";
	
	public static final String COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER = "committeeMemberAdditionRequestToParliamentaryAffairsMinister";
	
	public static final String COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION = "committeeMemberAdditionRequestToLeaderOfOpposition";
	
	public static final String COMMITTEE_ADDITION_OF_INVITED_MEMBERS = "committeeInvitedMemberAddition";
	
	public static final String COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW = "COMMITTEE_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW";
	
	public static final int WORKFLOW_START_LEVEL = 1;
	
	public static final String COMMITTEE = "COMMITTEE";
	
	public static final String COMMITTEE_MEMBER_ADDITION_URL = "workflow/committee/memberAddition";
	
	public static final String COMMITTEE_INVITED_MEMBER_ADDITION_URL = "workflow/committee/invitedMemberAddition";
	
	public static final String COMMITTEE_CHAIRMAN = "comittee_chairman";
	
	public static final String COMMITTEE_MEMBER = "committee_member";
	
	public static final String COMMITTEE_INVITED_MEMBER = "committee_invited_member";
	
	public static final String COMMITTEE_REQUEST_TO_PARLIAMENTARY_MINISTER_PROCESSED = "committee_final_approved_committeeMemberAdditionRequestToParliamentaryAffairsMinister";
	
	public static final String COMMITTEE_REQUEST_TO_LEADER_OF_OPPOSITION_PROCESSED = "committee_final_approved_committeeMemberAdditionRequestToLeaderOfOpposition";
	
	public static final String COMMITTEE_MEMBERS_ADDED = "committee_membersAdded";
	
	public static final String COMMITTEE_INVITED_MEMBER_ADDITION_PROCESSED = "committee_final_approved_committeeInvitedMemberAddition";
	
	public static final String COMMITTEE_INVITED_MEMBERS_ADDED = "committee_invitedMembersAdded";
	
	public static final String COMMITTEETOUR_INCOMPLETE = "committeetour_incomplete";
	
	public static final String COMMITTEETOUR_CREATED = "committeetour_created";
	
	public static final String COMMITTEETOUR_RECOMMEND_ADMISSION = "committeetour_recommend_tourAdmission";
	
	public static final String COMMITTEETOUR_REQUEST_FOR_TOUR_URL = "workflow/committeetour";
	
	public static final String COMMITTEETOUR_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW = "COMMITTEETOUR_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW";
	
	public static final String SERVER_DATETIMEFORMAT = "SERVER_DATETIMEFORMAT";
	
	/****************Bill Information System********************/
	/**** device types ****/
	public static final String NONOFFICIAL_BILL = "bills_nonofficial";

	public static final String GOVERNMENT_BILL = "bills_government";
	
	/**** bill types ****/
	public static final String AMENDMENT_BILL = "amending";
	
	public static final String ORDINANCE_REPLACEMENT_BILL = "replace_ordinance";
	
	/**** bill kinds ****/
	public static final String MONEY_BILL = "money";	

	/**** member status  ****/	
	public static final String BILL_INCOMPLETE="bill_incomplete";

	public static final String BILL_COMPLETE="bill_complete";

	public static final String BILL_SUBMIT="bill_submit";

	/**** System status****/    
	public static final String BILL_SYSTEM_ASSISTANT_PROCESSED="bill_system_assistantprocessed";

	public static final String BILL_SYSTEM_TO_BE_PUTUP="bill_system_putup";
	
	public static final String BILL_SYSTEM_CLUBBED="bill_system_clubbed";
	
	public static final String BILL_SYSTEM_CLUBBED_WITH_PENDING="bill_system_clubbedwithpending";
	
	/**** Translation & Its Workflow Status ****/
	public static final String BILL_TRANSLATION_NOTSEND = "translation_notsend";

	public static final String BILL_TRANSLATION_COMPLETED = "translation_completed";
	
	public static final String BILL_TRANSLATION_TIMEOUT = "translation_timeout";
	
	public static final String BILL_TRANSLATION_CANCELLED = "translation_cancelled";
	
	public static final String BILL_RECOMMEND_TRANSLATION="bill_recommend_translation";
	
	public static final String BILL_RECOMMEND_REJECT_TRANSLATION="bill_recommend_reject_translation";
	
	public static final String BILL_FINAL_TRANSLATION="bill_final_translation";
	
	public static final String BILL_FINAL_REJECT_TRANSLATION="bill_final_reject_translation";
	
	/**** Opinion Seeking From Law and JD & Its Workflow Status ****/
	public static final String BILL_OPINION_FROM_LAWANDJD_NOTSEND = "opinionFromLawAndJD_notsend";
	
	public static final String BILL_OPINION_FROM_LAWANDJD_RECEIVED = "opinionFromLawAndJD_received";
	
	public static final String BILL_RECOMMEND_OPINION_FROM_LAWANDJD="bill_recommend_opinionFromLawAndJD";
	
	public static final String BILL_FINAL_OPINION_FROM_LAWANDJD="bill_final_opinionFromLawAndJD";
	
	/**** Recommendation From Governor & Its Workflow Status ****/
	public static final String BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND = "recommendationFromGovernor_notsend";
	
	public static final String BILL_RECOMMENDATION_FROM_GOVERNOR_RECEIVED = "recommendationFromGovernor_received";
	
	public static final String BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR="bill_recommend_recommendationFromGovernor";
	
	public static final String BILL_FINAL_RECOMMENDATION_FROM_GOVERNOR="bill_final_recommendationFromGovernor";
	
	/**** Recommendation From President & Its Workflow Status ****/
	public static final String BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND = "recommendationFromPresident_notsend";
	
	public static final String BILL_RECOMMENDATION_FROM_PRESIDENT_RECEIVED = "recommendationFromPresident_received";
	
	public static final String BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT="bill_recommend_recommendationFromPresident";
	
	public static final String BILL_FINAL_RECOMMENDATION_FROM_PRESIDENT="bill_final_recommendationFromPresident";
	
	/**** Print Requisition Workflow Status ****/
	public static final String BILL_FINAL_PRINT_REQUISITION_TO_PRESS="bill_final_printRequisitionToPress";
	
	public static final String BILL_FINAL_PRINT_REQUISITION_TO_PRESS_FOR_GAZETTE="bill_final_printRequisitionToPressForGazette";
	
	public static final String BILL_FINAL_PRINT_REQUISITION_TO_PRESS_POST_PASSEDBYFIRSTHOUSE="bill_final_printRequisitionToPressPostPassedByFirstHouse";
	
	/**** Send for Endorsement Workflow Status ****/
	public static final String BILL_FINAL_SENDFORENDORSEMENT="bill_final_sendForEndorsement";
	
	/**** Transmit Endorsement Copies Workflow Status ****/
	public static final String BILL_FINAL_TRANSMITENDORSEMENTCOPIES="bill_final_transmitEndorsementCopies";
	
	/**** Transmit Press Copies Workflow Status ****/
	public static final String BILL_FINAL_TRANSMITPRESSCOPIES="bill_final_transmitPressCopies";
	
	/**** Lay Letter Workflow Status ****/
	public static final String BILL_FINAL_LAYLETTER="bill_final_layLetter";
	
	/**** Transmit Letter Of Passed By First House Workflow Status ****/
	public static final String BILL_RECOMMEND_TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE = "bill_recommend_transmitLetterOfPassedByFirstHouse";
	
	public static final String BILL_FINAL_TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE = "bill_final_transmitLetterOfPassedByFirstHouse";
	
	public static final String BILL_TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE_NOTSTARTED = "bill_transmitLetterOfPassedByFirstHouse_notStarted";
	
	public static final String BILL_TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE_PENDING = "bill_transmitLetterOfPassedByFirstHouse_pending";
	
	public static final String BILL_TRANSMIT_LETTER_OF_PASSED_BY_FIRST_HOUSE_COMPLETED = "bill_transmitLetterOfPassedByFirstHouse_completed";
	
	

	/**** Recommendation status ****/	
	public static final String BILL_RECOMMEND_ADMISSION="bill_recommend_admission";

	public static final String BILL_RECOMMEND_REJECTION="bill_recommend_rejection";
	
	public static final String BILL_RECOMMEND_NAMECLUBBING="bill_recommend_nameclubbing";
	
//	public static final String BILL_RECOMMEND_REJECT_NAMECLUBBING="bill_recommend_reject_nameclubbing";
	
	public static final String BILL_RECOMMEND_SENDBACK="bill_recommend_sendback";	

	public static final String BILL_RECOMMEND_DISCUSS="bill_recommend_discuss";

	/**** Final status ****/ 
	public static final String BILL_FINAL_ADMISSION="bill_final_admission";    

	public static final String BILL_FINAL_REJECTION="bill_final_rejection";
	
	public static final String BILL_FINAL_REJECTION_DUETOINCOMPLETENESS="bill_final_rejection_dueToIncompleteness";
	
	public static final String BILL_FINAL_REJECTION_DUETOREFERENCING="bill_final_rejection_dueToReferencing";
	
	public static final String BILL_FINAL_REJECTION_DUETOFINALAUTHORITYDECISION="bill_final_rejection_dueToFinalAuthorityDecision";
	
	public static final String BILL_FINAL_NAMECLUBBING = "bill_final_nameclubbing";
	
	public static final String BILL_FINAL_REJECT_NAMECLUBBING = "bill_final_reject_nameclubbing";
	
	public static final String BILL_FINAL_WITHDRAWN="bill_final_withdrawn";
	
	public static final String BILL_FINAL_NEGATIVED="bill_final_negatived";
	
	public static final String BILL_FINAL_LAPSED="bill_final_lapsed";
	
	public static final String BILL_FINAL_PASSED="bill_final_passed";
	
	/**** Processed Status ****/
	public static final String BILL_PROCESSED_REJECTIONWITHREASON = "bill_processed_rejectionWithReason";
	
	public static final String BILL_PROCESSED_SENDTOSECTIONOFFICER = "bill_processed_sendToSectionOfficer";
	
	public static final String BILL_PROCESSED_DEPARTMENTINTIMATED = "bill_processed_departmentIntimated";
	
	public static final String BILL_PROCESSED_TOBEINTRODUCED = "bill_processed_toBeIntroduced";
	
	public static final String BILL_PROCESSED_INTRODUCED = "bill_processed_introduced";
	
	public static final String BILL_PROCESSED_UNDERCONSIDERATION = "bill_processed_underConsideration";
	
	public static final String BILL_PROCESSED_BALLOTED = "bill_processed_balloted";

	public static final String BILL_PROCESSED_TOBEDISCUSSED = "bill_processed_toBeDiscussed";	
	
	public static final String BILL_PROCESSED_PARTIALLYDISCUSSED = "bill_processed_partiallydiscussed";
	
	public static final String BILL_PROCESSED_REFERTOJOINTCOMMITTEE = "bill_processed_referToJointCommittee";
	
	public static final String BILL_PROCESSED_REREFERTOJOINTCOMMITTEE = "bill_processed_reReferToJointCommittee";
	
	public static final String BILL_PROCESSED_DISCUSSEDCLAUSEBYCLAUSE = "bill_processed_discussedClauseByClause";
	
	public static final String BILL_PROCESSED_PASSEDBYFIRSTHOUSE = "bill_processed_passedByFirstHouse";
	
	public static final String BILL_PROCESSED_PASSED = "bill_processed_passed";
	
	public static final String BILL_PROCESSED_NEGATIVED = "bill_processed_negatived";
	
	public static final String BILL_PROCESSED_WITHDRAWN = "bill_processed_withdrawn";
	
	public static final String BILL_PROCESSED_LAPSED = "bill_processed_lapsed";	
	
	public static final String BILL_PROCESSED_PASSEDBYBOTHHOUSES = "bill_processed_passedByBothHouses";
	
	/**** Changed Processed Status After Refactoring ****/
	public static final String BILL_PROCESSED_CONSIDERED = "bill_processed_considered";
	
//	public static final String BILL_PROCESSED_CONSIDERED_UPPERHOUSE_FIRSTHOUSE_1 = "bill_processed_considered_upperhouse_firsthouse_1";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_LOWERHOUSE_FIRSTHOUSE_2 = "bill_processed_considered_lowerhouse_firsthouse_2";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_UPPERHOUSE_FIRSTHOUSE_2 = "bill_processed_considered_upperhouse_firsthouse_2";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_LOWERHOUSE_SECONDHOUSE_1 = "bill_processed_considered_lowerhouse_secondhouse_1";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_UPPERHOUSE_SECONDHOUSE_1 = "bill_processed_considered_upperhouse_secondhouse_1";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_LOWERHOUSE_SECONDHOUSE_2 = "bill_processed_considered_lowerhouse_secondhouse_2";
//	
//	public static final String BILL_PROCESSED_CONSIDERED_UPPERHOUSE_SECONDHOUSE_2 = "bill_processed_considered_upperhouse_secondhouse_2";
	
	/**** Put Up ****/
	public static final String BILL_PUTUP_NAMECLUBBING="bill_putup_nameclubbing";
	
	public static final String BILL_PUTUP_REJECTION="bill_putup_rejection";
	
	/**** Custom Parameters ****/
	public static final String BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT = "BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT";
	
	public static final String BILL_CHECKLIST_COUNT = "BILL_CHECKLIST_COUNT";
	
	/**** Native Query Keys ****/
	public static final String BILL_GET_DRAFT_BY_STATUS = "BILL_GET_DRAFT_BY_STATUS";
	
	/**** Others ****/
	public static final String BILL_RECOMMENDATION_FROM_GOVERNOR_CHECKLIST_PARAMETER = "isRecommendedAsPerConstitutionArticle_207_3";
	
	public static final String BILL_RECOMMENDATION_FROM_PRESIDENT_CHECKLIST_PARAMETER = "isRecommendedAsPerConstitutionArticle_304_b";

	public static final String BILL_PRESS_COPY = "BILL_PRESS_COPY";
	
	public static final String BILL_GAZETTE_COPY = "BILL_GAZETTE_COPY";
	
	public static final String BILL_GAZETTE_RECEIVING_DEPARTMENTS = "BILL_GAZETTE_RECEIVING_DEPARTMENTS";
	
	public static final String BILL_FIRST_HOUSE = "firsthouse";
	
	public static final String BILL_SECOND_HOUSE = "secondhouse";	
	
	/**** Voting For ****/
	public static final String VOTING_FOR_PASSING_OF_BILL = "PASSING_OF_BILL";
	
	/**** Laying For ****/
	public static final String LAYING_IN_SECONDHOUSE_POST_PASSED_BY_FIRST_HOUSE = "LAYING_IN_SECONDHOUSE_POST_PASSED_BY_FIRST_HOUSE";
	
	/**** Transmission/Endorsement of Print Requisition Decisions ****/
	public static final String TRANSMISSION_APPROVED="transmission_approved";

	public static final String TRANSMISSION_REJECTED="transmission_rejected";
	
	/**** Laying Letter Statuses & Decisions ****/
	public static final String LAYINGLETTER_NOTSEND="layingLetter_notsend";
	
	public static final String LAYINGLETTER_PENDING="layingLetter_pending";
	
	public static final String LAYINGLETTER_APPROVED="layingLetter_approved";

	public static final String LAYINGLETTER_REJECTED="layingLetter_rejected";
	
	/***Editing ****/
	public static final String EDITING = "EDITING";
	
	public static final String EDITING_RECOMMEND_SENDBACK = "editing_recommend_sendback";
	
	public static final String EDITING_RECOMMEND_MEMBERAPPROVAL = "editing_recommend_memberapproval";
	
	public static final String EDITING_RECOMMEND_SPEAKERAPPROVAL = "editing_recommend_speakerapproval";
	
	public static final String EDITING_FINAL_MEMBERAPPROVAL = "editing_final_memberapproval";
	
	public static final String EDITING_FINAL_SPEAKERAPPROVAL = "editing_final_speakerapproval";
	
	public static final String EDITOR = "editor";
	
	public static final String CHIEF_EDITOR = "chief_editor";
}
