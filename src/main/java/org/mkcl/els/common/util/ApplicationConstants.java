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

    /** The Constant mr_IN_INFONOTFOUND. */
    public static final String mr_IN_INFONOTFOUND="माहिती प्राप्‍त झालेली नाही";

    /** The Constant mr_IN_INFOFOUND. */
    public static final String mr_IN_INFOFOUND="माहिती मिळालेली एकूण";

    /** The Constant en_US_INFOFOUND. */
    public static final String en_US_INFOFOUND="Information Available For";

    /** The Constant SERVER_DATEFORMAT. */
    public static final String SERVER_DATEFORMAT = "dd/MM/yyyy";

    /** The Constant en_US_LOWERHOUSE_DEAFULTROLE. */
    public static final String en_US_LOWERHOUSE_DEAFULTROLE="Member";

    /** The Constant en_US_UPPERHOUSE_DEAFULTROLE. */
    public static final String en_US_UPPERHOUSE_DEAFULTROLE="Member";

    /** The Constant mr_IN_LOWERHOUSE_DEAFULTROLE. */
    public static final String mr_IN_LOWERHOUSE_DEAFULTROLE="सदस्य";

    /** The Constant mr_IN_UPPERHOUSE_DEAFULTROLE. */
    public static final String mr_IN_UPPERHOUSE_DEAFULTROLE="सदस्य";

    /** The Constant DB_DATEFORMAT. */
    public static final String DB_DATEFORMAT="yyyy-MM-dd";

    /** The Constant en_US_DAUGHTER. */
    public static final String en_US_DAUGHTER="Daughter";

    /** The Constant en_US_SON. */
    public static final String en_US_SON="Son";

    /** The Constant mr_IN_DAUGHTER. */
    public static final String mr_IN_DAUGHTER="मुलगी";

    /** The Constant mr_IN_SON. */
    public static final String mr_IN_SON="मुलगा";

    /** The Constant mr_IN_WIFE. */
    public static final String mr_IN_WIFE="पत्नी";

    /** The Constant mr_IN_HUSBAND. */
    public static final String mr_IN_HUSBAND="पती";

    /** The Constant en_US_WIFE. */
    public static final String en_US_WIFE="Wife";

    /** The Constant en_US_HUSBAND. */
    public static final String en_US_HUSBAND="Husband";

    /** The Constant TEHSIL_mr_IN. */
    public static final String TEHSIL_mr_IN="तालुका";

    /** The Constant DISTRICT_mr_IN. */
    public static final String DISTRICT_mr_IN="जिल्हा";

    /** The Constant STATE_mr_IN. */
    public static final String STATE_mr_IN="राज्य";

    /** The Constant PINCODE_mr_IN. */
    public static final String PINCODE_mr_IN="पिनकोड";

    /** The Constant AND_mr_IN. */
    public static final String AND_mr_IN="व";

    /** The Constant DEFAULT_FROM_DATE_LOCALE_en_US. */
    public static final String DEFAULT_FROM_DATE_LOCALE_en_US="01/12/1950";

    /** The Constant DEFAULT_TO_DATE_LOCALE_en_US. */
    public static final String DEFAULT_TO_DATE_LOCALE_en_US="31/12/1950";

    /** The Constant DEFAULT_FROM_DATE_LOCALE_mr_IN. */
    public static final String DEFAULT_FROM_DATE_LOCALE_mr_IN="०१/१२/१९५०";

    /** The Constant DEFAULT_TO_DATE_LOCALE_mr_IN. */
    public static final String DEFAULT_TO_DATE_LOCALE_mr_IN="३१/१२/१९५०";

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

    public static final String HALF_HOUR_DISCUSSION_QUESTION_STANDALONE="questions_halfhourdiscussion_standalone";

    public static final String HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION="questions_halfhourdiscussion_from_question";
    
    /**** Question Status ****/
    /**** Member *****/
    public static final String QUESTION_INCOMPLETE="question_incomplete";
    
    public static final String QUESTION_COMPLETE="question_complete";
    
    public static final String QUESTION_SUBMIT="question_submit";    
    /**** Supporting Member ****/
    public static final String QUESTION_SUPPORTING_MEMBER_APPROVED="supportingmember_approved";
    
    public static final String QUESTION_SUPPORTING_MEMBER_REJECTED="supportingmember_rejected";
    
    public static final String QUESTION_SUPPORTING_MEMBER_PENDING="supportingmember_pending";
    
    public static final String QUESTION_SUPPORTING_MEMBER_NOTSEND="supportingmember_notsend";    
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
    /**** Final ****/ 
    public static final String QUESTION_FINAL_ADMISSION="question_final_admission";

    public static final String QUESTION_FINAL_REJECTION="question_final_rejection";

    public static final String QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_final_convertToUnstarredAndAdmit";

    public static final String QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="question_final_clarificationNotReceivedFromMember";
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
    
    /**** User Group Types    ****/
    public static final String MEMBER="member";
    
    /**** Approving Authority ****/
    public static final String CHAIRMAN="chairman";
    
    public static final String SPEAKER="speaker";   
    
    public static final String ASSISTANT="assistant";
    /**** My Task Status ****/
    public static final String MYTASK_PENDING="PENDING";
    
    public static final String MYTASK_COMPLETED="COMPLETED";    
    /**** Workflow Types ****/
    public static final String APPROVAL_WORKFLOW="APPROVAL_WORKFLOW";
    
    public static final String SUPPORTING_MEMBER_WORKFLOW="Supporting_Members_Approval_Process";    
    /**** URL Pattern of Various Workflows ****/
    public static final String APPROVAL_WORKFLOW_URLPATTERN="workflow/question";

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

    /**** member ballot ****/
    public static final String MEMBERBALLOT_DELETE_EXISTING="DELETE";
    
    public static final String MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT="YES";

    
    
    

    

    
    

    
    
    
    
    
    
    
    


    
}
