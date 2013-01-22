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
    /** The Constant STARRED_QUESTION. */
    public static final String STARRED_QUESTION="questions_starred";

    /** The Constant UNSTARRED_QUESTION. */
    public static final String UNSTARRED_QUESTION="questions_unstarred";

    /** The Constant SHORT_NOTICE_QUESTION. */
    public static final String SHORT_NOTICE_QUESTION="questions_shortnotice";

    /** The Constant HALF_HOUR_DISCUSSION_QUESTION_STANDALONE. */
    public static final String HALF_HOUR_DISCUSSION_QUESTION_STANDALONE="questions_halfhourdiscussion_standalone";

    /** The Constant HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION. */
    public static final String HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION="questions_halfhourdiscussion_from_question";
    
    /**** Question Status ****/
    public static final String QUESTION_ASSISTANT_PROCESSED="question_assistantprocessed";
    
    public static final String QUESTION_ADMISSION="question_workflow_decisionstatus_admission";
    
    public static final String QUESTION_APPROVING_ADMISSION="question_workflow_approving_admission";
    
    public static final String QUESTION_REJECTION="question_workflow_decisionstatus_rejection";
    
    public static final String QUESTION_APPROVING_REJECTION="question_workflow_approving_rejection";
    
    public static final String QUESTION_CONVERT_TO_UNSTARRED="question_workflow_decisionstatus_converttounstarred";
    
    public static final String QUESTION_APPROVING_CONVERT_TO_UNSTARRED="question_workflow_approving_converttounstarred";
    
    public static final String QUESTION_CLARIFICATION_NEEDED="question_workflow_decisionstatus_clarificationneeded";
    
    public static final String QUESTION_APPROVING_CLARIFICATION_NEEDED="question_workflow_approving_clarificationneeded";
    
    public static final String QUESTION_DISCUSS="question_workflow_decisionstatus_discuss";
    
    public static final String QUESTION_APPROVING_DISCUSS="question_workflow_approving_discuss";
    
    public static final String QUESTION_SENDBACK="question_workflow_decisionstatus_sendback";
    
    public static final String QUESTION_NAMECLUBBING="question_workflow_decisionstatus_nameclubbing";
    	
    public static final String QUESTION_GROUPCHANGED="question_workflow_decisionstatus_groupchanged";
       
    /**** Approval Workflow(Name of the workflow process that is used for approval cycles) ****/
    public static final String APPROVAL_WORKFLOW="APPROVAL_WORKFLOW";
    
    /**** Question Ministry,Department and Subdepartment parameters key in UserGroup ****/
    public static final String HOUSETYPE_KEY="HOUSETYPE";
    
    public static final String DEVICETYPE_KEY="DEVICETYPE";
    
    public static final String MINISTRY_KEY="MINISTRY";
    
    public static final String DEPARTMENT_KEY="DEPARTMENT";
    
    public static final String SUBDEPARTMENT_KEY="SUBDEPARTMENT";
    
    /**** Approving Authority ****/
    public static final String CHAIRMAN="chairman";
    
    public static final String SPEAKER="speaker";
}
