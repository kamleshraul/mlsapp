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

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationConstants.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class ApplicationConstants {
	
	public static final String SYSTEM_PROPERTIES_FILENAME = "system"; //base-name (or location) of system.properties file on class-path (located in src/main/resources)
	
	public static final ResourceBundle systemPropertiesBundle = PropertyResourceBundle.getBundle(SYSTEM_PROPERTIES_FILENAME);
	
	/**************************************************************************************************************************/
	/************************************************MIS AND MISCELLANEOUS BEGINS********************************************************/
	/** To generate password **/
	public static final String USE_DEFAULT_PASSWORD = "USE_DEFAULT_PASSWORD";
	/** Actor States **/
	public static final String ACTOR_STATES = "ACTOR_STATES";
	
	public static final String ACTOR_ACTIVE = "active";
	
	/** The Constant ASC. */
	public static final String ASC = "asc";

	/** The Constant DESC. */
	public static final String DESC = "desc";
	
	/** The Constant DEFAULT_LOCALE. */
	public static final String DEFAULT_LOCALE = systemPropertiesBundle.getString("locale.default_value");
	
	/** System locale **/
	public static final String STANDARD_LOCALE = "en_US";
	
	public static final String STANDARD_LOCALE_INDIA = "hi_IN";

	/** The Constant LOWER_HOUSE. */
	public static final String LOWER_HOUSE="lowerhouse";

	/** The Constant UPPER_HOUSE. */
	public static final String UPPER_HOUSE="upperhouse";

	/** The Constant BOTH_HOUSE. */
	public static final String BOTH_HOUSE="bothhouse";

	/** The Constant DEFAULT_HOUSE. */
	public static final String DEFAULT_HOUSE="defaulthouse";

	/** The Constant SERVER_DATEFORMAT. */
	public static final String SERVER_DATEFORMAT = "dd/MM/yyyy";
	
	public static final String SERVER_TIMEFORMAT = "hh:mm";
	
	/** The Constant ROTATIONORDER_DATEFORMAT. (This is abstract format defined for rotation order style formatting) */
	public static final String ROTATIONORDER_WITH_DAY_DATEFORMAT = "day, dinank dd month, yyyy";
	
	public static final String ROTATIONORDER_DATEFORMAT = "dinank dd month, yyyy";
	
	public static final String SERVER_DATEFORMAT_DISPLAY_1 = "dd MMM, yyyy";
	
	public static final String SERVER_DATEFORMAT_DISPLAY_2 = "dd month, yyyy";
	
	public static final String SERVER_DATEFORMAT_DISPLAY_3 = "d-M-yyyy";
	
	public static final String SERVER_DATEFORMAT_DDMM = "ddMM";	

	/** The Constant DB_DATEFORMAT. */
	public static final String DB_DATEFORMAT="yyyy-MM-dd";

	public static final String DB_DATETIME_FORMAT="yyyy-MM-dd hh:mm:ss";
	
	public static final String DB_DATETIME__24HOURS_FORMAT="yyyy-MM-dd HH:mm:ss";

	/** The Constant REPORT_DATEFORMAT. */
	public static final String REPORT_DATEFORMAT="dd-MM-yyyy";

	/** The Constant DAUGHTER. */
	public static final String DAUGHTER="Daughter";

	/** The Constant SON. */
	public static final String SON="Son";	

	/** The Constant WIFE. */
	public static final String WIFE="Wife";

	/** The Constant HUSBAND. */
	public static final String HUSBAND="Husband";

	/** The Constant LOWERHOUSEGRID. */
	public static final String LOWERHOUSEGRID="MEMBER_LOWERHOUSEGRID";

	/** The Constant UPPERHOUSEGRID. */
	public static final String UPPERHOUSEGRID="MEMBER_UPPERHOUSEGRID";
	
	/*************** SECURITY PARAMETERS ***************/
	/** The Constant DEFAULT_PASSWORD. */
	public static final String DEFAULT_PASSWORD = "Mls@4321";
	
	public static final String DEFAULT_PASSWORD_LENGTH = "8";
	
	public static final String DEFAULT_HIGH_SECURITY_PASSWORD_INITIAL = "eGov*";
	//====================================================
	
	//related to question module
	/** The Constant QIS_ACTOR_LIST_CUSTOMPARAM_NAME. */
	public static final String QIS_ACTOR_LIST_CUSTOMPARAM_NAME="QIS_ACTOR_LIST";
	
	//parameters for starred question
	//Session Keys
	public static final String QUESTIONS_STARRED_TOTALROUNDS_MEMBERBALLOT="questions_starred_totalRoundsMemberBallot";

	public static final String QUESTIONS_STARRED_TOTALROUNDS_FINALBALLOT="questions_starred_totalRoundsFinalBallot";
	/**************************************************************************************************************************/
	/************************************************MIS AND MISCELLANEOUS BEGINS********************************************************/


	/**************************************************************************************************************************/
	/************************************************DEVICE TYPE BEGINS********************************************************/
	/**** Question Types ****/
	public static final String STARRED_QUESTION="questions_starred";

	public static final String UNSTARRED_QUESTION="questions_unstarred";

	public static final String SHORT_NOTICE_QUESTION="questions_shortnotice";

	public static final String HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION="questions_halfhourdiscussion_from_question";

	/**************************************************************************************************************************/
	/************************************************DEVICE TYPE ENDS*************************************************************/

	/**************************************************************************************************************************/
	/************************************************STATUS BEGINS*************************************************************/
	
	public static final String QUESTION_SESSIONS_TOBE_SEARCHED_COUNT = "QUESTION_SESSIONS_TOBE_SEARCHED_COUNT";
	
	/**** Member(Starred) *****/
	public static final String QUESTION_INCOMPLETE="question_incomplete";

	public static final String QUESTION_COMPLETE="question_complete";

	public static final String QUESTION_SUBMIT="question_submit"; 	

	/**** Recommendation(Starred) ****/
	public static final String QUESTION_RECOMMEND_ADMISSION="question_recommend_admission";

	public static final String QUESTION_RECOMMEND_REJECTION="question_recommend_rejection";

	public static final String QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED="question_recommend_convertToUnstarred";

	public static final String QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT="question_recommend_convertToUnstarredAndAdmit";

	public static final String QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_recommend_convertToUnstarredAndAdmitClubbedWithPreviousSession";

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

	/**** Final(Starred) ****/ 
	public static final String QUESTION_PROCESSED_REANSWER = "question_processed_reanswer";

	public static final String QUESTION_FINAL_ADMISSION="question_final_admission";

	public static final String QUESTION_FINAL_REJECTION="question_final_rejection";

	public static final String QUESTION_FINAL_CONVERT_TO_UNSTARRED="question_final_rejection";
	
	public static final String QUESTION_PROCESSED_ANSWER_RECEIVED = "question_processed_answerReceived";

	public static final String QUESTION_PROCESSED_REJECTIONWITHREASON="question_processed_rejectionWithReason";

	public static final String QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_final_convertToUnstarredAndAdmit";
	
	public static final String QUESTION_PROCESSED_CLARIFICATIONRECEIVED = "question_processed_clarificationReceived";
	
	public static final String QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED = "question_unstarred_processed_clarificationReceived";
	
	public static final String QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED = "question_shortnotice_processed_clarificationReceived";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED = "question_halfHourFromQuestion_processed_clarificationReceived";
	
	/**** System(Starred) ****/    
	public static final String QUESTION_SYSTEM_ASSISTANT_PROCESSED="question_system_assistantprocessed";

	public static final String QUESTION_SYSTEM_TO_BE_PUTUP="question_system_putup";

	public static final String QUESTION_SYSTEM_GROUPCHANGED="question_system_groupchanged";

	public static final String QUESTION_SYSTEM_CLUBBED="question_system_clubbed";

	public static final String QUESTION_SYSTEM_CLUBBED_WITH_PENDING="question_system_clubbedwithpending";
	
	public static final String QUESTION_SYSTEM_LAPSED="question_system_lapsed";
	
	public static final String QUESTION_PROCESSED_LAPSED="question_processed_lapsed";
	
	/**** Put Up ****/
	public static final String QUESTION_PUTUP_NAMECLUBBING="question_putup_nameclubbing";
	
	public static final String QUESTION_PUTUP_ONHOLD="question_putup_onhold";

	public static final String QUESTION_PUTUP_CONVERT_TO_UNSTARRED="question_putup_convertToUnstarred";

	public static final String QUESTION_PUTUP_REJECTION="question_putup_rejection";

	public static final String QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT="question_putup_convertToUnstarredAndAdmit";

	public static final String QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	/**** Processed Status ****/
	public static final String QUESTION_PROCESSED_BALLOTED="question_processed_balloted";

	public static final String QUESTION_PROCESSED_YAADILAID="question_processed_yaadilaid";
	
	public static final String QUESTION_PROCESSED_DISCUSSED="question_processed_discussed";
	
	public static String QUESTION_PROCESSED_SENDTODEPARTMENT="question_processed_sendToDepartment";
	
	public static final String QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED = "question_unstarred_processed_clarificationNotReceived";

	public static final String QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED = "question_shortnotice_processed_clarificationNotReceived";
	
	public static final String QUESTION_HALFHOURFROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "question_halfHourFromQuestion_processed_clarificationNotReceived";

	public static final String QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "question_processed_clarificationNotReceived";
	
	/**** Member(Unstarred) *****/
	public static final String QUESTION_UNSTARRED_INCOMPLETE="question_unstarred_incomplete";

	public static final String QUESTION_UNSTARRED_COMPLETE="question_unstarred_complete";

	public static final String QUESTION_UNSTARRED_SUBMIT="question_unstarred_submit"; 

	/**** Recommendation(Unstarred) ****/
	public static final String QUESTION_UNSTARRED_RECOMMEND_ADMISSION="question_unstarred_recommend_admission";

	public static final String QUESTION_UNSTARRED_RECOMMEND_REJECTION="question_unstarred_recommend_rejection";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CONVERT_TO_UNSTARRED="question_unstarred_recommend_convertToUnstarred";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT="question_unstarred_recommend_convertToUnstarredAndAdmit";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_unstarred_recommend_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER="question_unstarred_recommend_clarificationNeededFromMember";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="question_unstarred_recommend_clarificationNeededFromDepartment";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_GOVT="question_unstarred_recommend_clarificationNeededFromGovt";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="question_unstarred_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_UNSTARRED_RECOMMEND_NAMECLUBBING="question_unstarred_recommend_nameclubbing";

	public static final String QUESTION_UNSTARRED_RECOMMEND_PUTONHOLD="question_unstarred_recommend_putonhold";

	public static final String QUESTION_UNSTARRED_RECOMMEND_SENDBACK="question_unstarred_recommend_sendback";

	public static final String QUESTION_UNSTARRED_RECOMMEND_DISCUSS="question_unstarred_recommend_discuss";

	public static final String QUESTION_UNSTARRED_RECOMMEND_REPEATADMISSION="question_unstarred_recommend_repeatadmission";

	public static final String QUESTION_UNSTARRED_RECOMMEND_REPEATREJECTION="question_unstarred_recommend_repeatrejection";

	/**** Final(Unstarred) ****/ 
	public static final String QUESTION_UNSTARRED_FINAL_REANSWER = "question_unstarred_final_reanswer";

	public static final String QUESTION_UNSTARRED_FINAL_ADMISSION="question_unstarred_final_admission";

	public static final String QUESTION_UNSTARRED_FINAL_REJECTION="question_unstarred_final_rejection";

	public static final String QUESTION_UNSTARRED_PROCESSED_REJECTIONWITHREASON="question_unstarred_processed_rejectionWithReason";

	public static final String QUESTION_UNSTARRED_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_unstarred_final_convertToUnstarredAndAdmit";

	public static final String QUESTION_UNSTARRED_PROCESSED_ANSWER_RECEIVED = "question_unstarred_processed_answerReceived";
	
	public static final String QUESTION_UNSTARRED_FINAL_NAMECLUBBING = "question_unstarred_final_nameclubbing";
	
	public static String QUESTION_UNSTARRED_FINAL_REJECT_NAMECLUBBING = "question_unstarred_final_reject_nameclubbing";
	
	/**** System(Unstarred) ****/    
	public static final String QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED="question_unstarred_system_assistantprocessed";

	public static final String QUESTION_UNSTARRED_SYSTEM_TO_BE_PUTUP="question_unstarred_system_putup";

	public static final String QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED="question_unstarred_system_groupchanged";

	public static final String QUESTION_UNSTARRED_SYSTEM_CLUBBED="question_unstarred_system_clubbed";

	public static final String QUESTION_UNSTARRED_SYSTEM_CLUBBED_WITH_PENDING="question_unstarred_system_clubbedwithpending";
	
	public static final String QUESTION_UNSTARRED_SYSTEM_SYSTEM_LAPSED="question_unstarred_system_lapsed";
	
	/**** Put Up(Unstarred) ****/
	public static final String QUESTION_UNSTARRED_PUTUP_NAMECLUBBING="question_unstarred_putup_nameclubbing";

	public static final String QUESTION_UNSTARRED_PUTUP_ONHOLD="question_unstarred_putup_onhold";

	public static final String QUESTION_UNSTARRED_PUTUP_CONVERT_TO_UNSTARRED="question_unstarred_putup_convertToUnstarred";

	public static final String QUESTION_UNSTARRED_PUTUP_REJECTION="question_unstarred_putup_rejection";

	public static final String QUESTION_UNSTARRED_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT="question_unstarred_putup_convertToUnstarredAndAdmit";

	public static final String QUESTION_UNSTARRED_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_unstarred_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	/**** Processed Status(Unstarred) ****/
	public static final String QUESTION_UNSTARRED_PROCESSED_BALLOTED="question_unstarred_processed_balloted";

	public static final String QUESTION_UNSTARRED_PROCESSED_YAADILAID="question_unstarred_processed_yaadilaid";

	public static String QUESTION_UNSTARRED_PROCESSED_SENDTODEPARTMENT = "question_unstarred_processed_sendToDepartment";
	/**** Member(Short Notice) *****/
	public static final String QUESTION_SHORTNOTICE_INCOMPLETE="question_shortnotice_incomplete";

	public static final String QUESTION_SHORTNOTICE_COMPLETE="question_shortnotice_complete";

	public static final String QUESTION_SHORTNOTICE_SUBMIT="question_shortnotice_submit"; 

	/**** Recommendation(Short Notice) ****/
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_ADMISSION="question_shortnotice_recommend_admission";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REJECTION="question_shortnotice_recommend_rejection";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CONVERT_TO_UNSTARRED="question_shortnotice_recommend_convertToUnstarred";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT="question_shortnotice_recommend_convertToUnstarredAndAdmit";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_shortnotice_recommend_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_MEMBER="question_shortnotice_recommend_clarificationNeededFromMember";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="question_shortnotice_recommend_clarificationNeededFromDepartment";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_GOVT="question_shortnotice_recommend_clarificationNeededFromGovt";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="question_shortnotice_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_NAMECLUBBING="question_shortnotice_recommend_nameclubbing";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_PUTONHOLD="question_shortnotice_recommend_putonhold";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_SENDBACK="question_shortnotice_recommend_sendback";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_DISCUSS="question_shortnotice_recommend_discuss";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REPEATADMISSION="question_shortnotice_recommend_repeatadmission";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REPEATREJECTION="question_shortnotice_recommend_repeatrejection";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "question_unstarred_final_clarificationNeededFromDepartment";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "question_unstarred_final_clarificationNeededFromMember";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT = "question_unstarred_final_clarificationNeededFromMemberAndDepartment";

	/**** Final(Short Notice) ****/ 
	public static final String QUESTION_SHORTNOTICE_FINAL_REANSWER = "question_shortnotice_final_reanswer";

	public static final String QUESTION_SHORTNOTICE_FINAL_ADMISSION="question_shortnotice_final_admission";

	public static final String QUESTION_SHORTNOTICE_FINAL_REJECTION="question_shortnotice_final_rejection";

	public static final String QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED="question_shortnotice_final_rejection";

	public static final String QUESTION_SHORTNOTICE_PROCESSED_REJECTIONWITHREASON="question_shortnotice_processed_rejectionWithReason";

	public static final String QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_shortnotice_final_convertToUnstarredAndAdmit";

	public static final String QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "question_shortnotice_final_clarificationNeededFromDepartment";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "question_shortnotice_final_clarificationNeededFromMember";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT = "question_shortnotice_final_clarificationNeededFromMemberAndDepartment";

	public static String QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING = "question_shortnotice_final_clubbing";
	
	public static String QUESTION_SHORTNOTICE_FINAL_REJECT_NAMECLUBBING = "question_shortnotice_final_reject_nameclubbing";
	/**** System(Short Notice) ****/    
	public static final String QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED="question_shortnotice_system_assistantprocessed";

	public static final String QUESTION_SHORTNOTICE_SYSTEM_TO_BE_PUTUP="question_shortnotice_system_putup";

	public static final String QUESTION_SHORTNOTICE_SYSTEM_GROUPCHANGED="question_shortnotice_system_groupchanged";

	public static final String QUESTION_SHORTNOTICE_SYSTEM_CLUBBED="question_shortnotice_system_clubbed";

	public static final String QUESTION_SHORTNOTICE_SYSTEM_CLUBBED_WITH_PENDING="question_shortnotice_system_clubbedwithpending";
	
	public static final String QUESTION_SHORTNOTICE_SYSTEM_LAPSED="question_shortnotice_system_lapsed";
	
	/**** Put Up(Short Notice) ****/
	public static final String QUESTION_SHORTNOTICE_PUTUP_NAMECLUBBING="question_shortnotice_putup_nameclubbing";

	public static final String QUESTION_SHORTNOTICE_PUTUP_ONHOLD="question_shortnotice_putup_onhold";

	public static final String QUESTION_SHORTNOTICE_PUTUP_CONVERT_TO_UNSTARRED="question_shortnotice_putup_convertToUnstarred";

	public static final String QUESTION_SHORTNOTICE_PUTUP_REJECTION="question_shortnotice_putup_rejection";

	public static final String QUESTION_SHORTNOTICE_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT="question_shortnotice_putup_convertToUnstarredAndAdmit";

	public static final String QUESTION_SHORTNOTICE_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_shortnotice_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	/**** Processed Status(Short Notice) ****/
	public static final String QUESTION_SHORTNOTICE_PROCESSED_BALLOTED="question_shortnotice_processed_balloted";

	public static final String QUESTION_SHORTNOTICE_PROCESSED_YAADILAID="question_shortnotice_processed_yaadilaid";

	public static String QUESTION_SHORTNOTICE_PROCESSED_SENDTODEPARTMENT = "question_shortnotice_processed_sendToDepartment";
	
	public static final String QUESTION_SHORTNOTICE_PROCESSED_DISCUSSED = "question_shortnotice_processed_discussed";
	
	/**** Member(half hour discussion standalone) *****/
	public static final String HALF_HOUR_DISCUSSION_STANDALONE="motions_standalonemotion_halfhourdiscussion";
	
	public static final String HALFHOURDISCUSSIONSTANDALONE_MEMBER_MAX_PUTUP_COUNT_LH = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_LH";

	public static final String HALFHOURDISCUSSIONSTANDALONE_MEMBER_MAX_PUTUP_COUNT_UH = "NO_OF_HALFHOURDISCUSSIONSTANDALONE_MEMBER_PUTUP_COUNT_UH";

	public static final String DEVICE_STANDALONE = "motions_standalonemotion_";
	
	public static final String STANDALONE_GET_LATEST_STANDALONEMOTIONDRAFT_OF_USER = "STANDALONE_GET_LATEST_STANDALONEMOTIONDRAFT_OF_USER";
	
	public static final String STANDALONE_INCOMPLETE="standalonemotion_incomplete";

	public static final String STANDALONE_COMPLETE="standalonemotion_complete";

	public static final String STANDALONE_SUBMIT="standalonemotion_submit"; 	

	public static final String STANDALONE_RECOMMEND_ADMISSION="standalonemotion_recommend_admission";

	public static final String STANDALONE_RECOMMEND_REJECTION="standalonemotion_recommend_rejection";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER="standalonemotion_recommend_clarificationNeededFromMember";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="standalonemotion_recommend_clarificationNeededFromDepartment";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_FROM_GOVT="standalonemotion_recommend_clarificationNeededFromGovt";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="standalonemotion_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String STANDALONE_RECOMMEND_NAMECLUBBING="standalonemotion_recommend_nameclubbing";

	public static final String STANDALONE_RECOMMEND_PUTONHOLD="standalonemotion_recommend_putonhold";

	public static final String STANDALONE_RECOMMEND_SENDBACK="standalonemotion_recommend_sendback";

	public static final String STANDALONE_RECOMMEND_DISCUSS="standalonemotion_recommend_discuss";

	public static final String STANDALONE_RECOMMEND_REPEATADMISSION="standalonemotion_recommend_repeatadmission";

	public static final String STANDALONE_RECOMMEND_REPEATREJECTION="standalonemotion_recommend_repeatrejection";

	public static final String STANDALONE_FINAL_ADMISSION="standalonemotion_final_admission";

	public static final String STANDALONE_FINAL_REJECTION="standalonemotion_final_rejection";

	public static final String STANDALONE_PROCESSED_REJECTIONWITHREASON="standalonemotion_processed_rejectionWithReason";
    
	public static final String STANDALONE_SYSTEM_ASSISTANT_PROCESSED="standalonemotion_system_assistantprocessed";

	public static final String STANDALONE_SYSTEM_TO_BE_PUTUP="standalonemotion_system_putup";

	public static final String STANDALONE_SYSTEM_GROUPCHANGED="standalonemotion_system_groupchanged";

	public static final String STANDALONE_TO_BE_CLUBBED = "standalonemotion_to_be_clubbed";
	
	public static final String STANDALONE_SYSTEM_CLUBBED="standalonemotion_system_clubbed";

	public static final String STANDALONE_SYSTEM_CLUBBED_WITH_PENDING="standalonemotion_system_clubbedwithpending";
	
	public static final String STANDALONE_PUTUP_NAMECLUBBING="standalonemotion_putup_nameclubbing";

	public static final String STANDALONE_PUTUP_ONHOLD="standalonemotion_putup_onhold";

	public static final String STANDALONE_PUTUP_REJECTION="standalonemotion_putup_rejection";

	public static final String STANDALONE_PROCESSED_BALLOTED="standalonemotion_processed_balloted";
	
	public static final String STANDALONEED="standalonemotion_processed_discussed";
	
	public static final String STANDALONE_PROCESSED_YAADILAID = "standalonemotion_processed_yaadilaid";

	public static final String STANDALONE_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="standalonemotion_final_clarificationNotReceivedFromMember";

	public static final String STANDALONE_PROCESSED_CLARIFICATION_RECIEVED="standalonemotion_processed_clarificationReceived";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER="standalonemotion_recommend_clarificationNeededFromMember";
	
	public static final String STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER="standalonemotion_final_clarificationNeededFromMember";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT="standalonemotion_recommend_clarificationNeededFromDepartment";
	
	public static final String STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT="standalonemotion_final_clarificationNeededFromDepartment";

	public static final String STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="standalonemotion_final_clarificationNeededFromMemberAndDepartment";

	public static final String STANDALONE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="standalonemotion_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String STANDALONE_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_DEPARTMENT="standalonemotion_final_clarificationNotReceivedFromDepartment";

	public static final String STANDALONE_FINAL_REPEATADMISSION="standalonemotion_final_repeatadmission";

	public static final String STANDALONE_FINAL_REPEATREJECTION="standalonemotion_final_repeatrejection";

	public static final String STANDALONE_PROCESSED_SENDTOSECTIONOFFICER = "standalonemotion_processed_sendToSectionOfficer";
	
	public static final String STANDALONE_PROCESSED_SENDTODEPARTMENT = "standalonemotion_processed_sendToDepartment";

	public static final String STANDALONE_HALFHOURDISCUSSION_BALLOT_OUTPUT_COUNT_ASSEMBLY="STANDALONE_HALFHOURDISCUSSION_BALLOT_OUTPUT_COUNT_ASSEMBLY";

	public static final String STANDALONE_HALFHOURDISCUSSION_BALLOT_OUTPUT_COUNT_COUNCIL="STANDALONE_HALFHOURDISCUSSION_BALLOT_OUTPUT_COUNT_COUNCIL";

	public static final String STANDALONE_GET_REVISION = "STANDALONE_GET_REVISION";
	
	public static final String STANDALONE_RECOMMEND_CLUBBING = "standalonemotion_recommend_clubbing";
	
	public static final String STANDALONE_FINAL_CLUBBING = "standalonemotion_final_clubbing";
		
	public static final String STANDALONE_FINAL_NAMECLUBBING = "standalonemotion_final_nameclubbing";
	
	public static final String STANDALONE_RECOMMEND_CLUBBING_POST_ADMISSION = "standalonemotion_recommend_clubbingPostAdmission";
	
	public static final String STANDALONE_FINAL_CLUBBING_POST_ADMISSION = "standalonemotion_final_clubbingPostAdmission";
		
	public static final String STANDALONE_PUTUP_CLUBBING_POST_ADMISSION="standalonemotion_putup_clubbingPostAdmission";
	
	public static final String STANDALONE_PUTUP_CLUBBING= "standalonemotion_putup_clubbing";
	
	public static final String STANDALONE_PUTUP_NAME_CLUBBING = "standalonemotion_putup_nameclubbing";
	
	public static final String STANDALONE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING= "standalonemotion_putup_admitDueToReverseClubbing";
	
	public static final String STANDALONE_FINAL_UNCLUBBING = "standalonemotion_final_unclubbing";
	
	public static final String STANDALONE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "standalonemotion_final_admitDueToReverseClubbing";
	
	public static final String STANDALONE_PUTUP_UNCLUBBING = "standalonemotion_putup_unclubbing";
	
	public static final String STANDALONE_PROCESSED_CLARIFICATION_NOT_RECEIVED = "standalonemotion_processed_clarificationNotReceived";
	
	public static final String STANDALONE_PROCESSED_CLARIFICATION_RECEIVED = "standalonemotion_processed_clarificationReceived";
	
	public static final String STANDALONE_FINAL_CLARIFICATION_FROM_GOVT = "standalonemotion_final_clarificationFromGovt";
	
	public static final String STANDALONE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT = "standalonemotion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_STANDALONE = "workflow/standalonemotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_STANDALONE = "workflow/standalonemotion/supportingmember";
	
	public static final String HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_ASSEMBLY="HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_ASSEMBLY";

	public static final String HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL="HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL";
	
	public static final String HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembers";

	public static final String HALFHOURDISCUSSION_STANDALONE_NO_OF_SUPPORTING_MEMBERS_COMPARATOR = "motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembersComparator";

	public static final String HALFHOURDISCUSSION_STANDALONE_SUBMISSIONSTARTDATE = "motions_standalonemotion_halfhourdiscussion_submissionStartDate";

	public static final String HALFHOURDISCUSSION_STANDALONE_SUBMISSIONENDDATE = "motions_standalonemotion_halfhourdiscussion_submissionEndDate";
	
	public static final String STANDALONE_FINAL_REJECT_CLUBBING = "standalonemotion_final_reject_clubbing";
	
	public static final String STANDALONE_FINAL_REJECT_NAMECLUBBING = "standalonemotion_final_reject_nameclubbing";

	public static final String STANDALONE_FINAL_REJECT_UNCLUBBING = "standalonemotion_final_reject_unclubbing";
	
	public static final String STANDALONE_FINAL_REJECT_CLUBBING_POST_ADMISSION = "standalonemotion_final_reject_clubbingPostAdmission";

	public static final String STANDALONE_CLUBBING_MODE = "STANDALONE_CLUBBING_MODE"; 


	/**** STANDALONE CONSTANTS ****/	
	
	/**** Member(Half Hour Discussion from question) *****/
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_INCOMPLETE="question_halfHourFromQuestion_incomplete";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_COMPLETE="question_halfHourFromQuestion_complete";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SUBMIT="question_halfHourFromQuestion_submit"; 

	/**** Recommendation(half hour discussion from question) ****/
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMISSION="question_halfHourFromQuestion_recommend_admission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECTION="question_halfHourFromQuestion_recommend_rejection";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CONVERT_TO_UNSTARRED="question_halfHourFromQuestion_recommend_convertToUnstarred";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT="question_halfHourFromQuestion_recommend_convertToUnstarredAndAdmit";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_halfHourFromQuestion_recommend_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER="question_halfHourFromQuestion_recommend_clarificationNeededFromMember";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT="question_halfHourFromQuestion_recommend_clarificationNeededFromDepartment";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_GOVT="question_halfHourFromQuestion_recommend_clarificationNeededFromGovt";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT="question_halfHourFromQuestion_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_NAMECLUBBING="question_halfHourFromQuestion_recommend_nameclubbing";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_PUTONHOLD="question_halfHourFromQuestion_recommend_putonhold";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_SENDBACK="question_halfHourFromQuestion_recommend_sendback";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_DISCUSS="question_halfHourFromQuestion_recommend_discuss";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REPEATADMISSION="question_halfHourFromQuestion_recommend_repeatadmission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REPEATREJECTION="question_halfHourFromQuestion_recommend_repeatrejection";

	/**** Final(half hour discussion from question) ****/ 
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REANSWER = "question_halfHourFromQuestion_final_reanswer";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION="question_halfHourFromQuestion_final_admission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION="question_halfHourFromQuestion_final_rejection";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CONVERT_TO_UNSTARRED="question_halfHourFromQuestion_final_rejection";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_REJECTIONWITHREASON="question_halfHourFromQuestion_processed_rejectionWithReason";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT="question_halfHourFromQuestion_final_convertToUnstarredAndAdmit";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "question_halfHourFromQuestion_final_clarificationNeededFromDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "question_halfHourFromQuestion_final_clarificationNeededFromMember";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_AND_DEPARTMENT = "question_halfHourFromQuestion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING = "question_halfHourFromQuestion_final_nameclubbing";
	
	public static String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_NAMECLUBBING = "question_halfHourFromQuestion_final_reject_nameclubbing";
	/**** System(half hour discussion from question) ****/    
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED="question_halfHourFromQuestion_system_assistantprocessed";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_TO_BE_PUTUP="question_halfHourFromQuestion_system_putup";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_GROUPCHANGED="question_halfHourFromQuestion_system_groupchanged";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED="question_halfHourFromQuestion_system_clubbed";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_CLUBBED_WITH_PENDING="question_halfHourFromQuestion_system_clubbedwithpending";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_LAPSED="question_halfHourFromQuestion_system_lapsed";
	
	/**** Put Up(half hour discussion from question) ****/
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_NAMECLUBBING="question_halfHourFromQuestion_putup_nameclubbing";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_ONHOLD="question_halfHourFromQuestion_putup_onhold";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CONVERT_TO_UNSTARRED="question_halfHourFromQuestion_putup_convertToUnstarred";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_REJECTION="question_halfHourFromQuestion_putup_rejection";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT="question_halfHourFromQuestion_putup_convertToUnstarredAndAdmit";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_halfHourFromQuestion_putup_convertToUnstarredAndAdmitClubbedWithPreviousSession";

	/**** Processed Status(half hour discussion from question) ****/
	public static String QUESTION_HALFHOURFROMQUESTION_PROCESSED_SENDTODEPARTMENT = "question_halfHourFromQuestion_processed_sendToDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED="question_halfHourFromQuestion_processed_balloted";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_YAADILAID="question_halfHourFromQuestion_processed_yaadilaid";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_DISCUSSED = "question_halfHourFromQuestion_processed_discussed";

	/**************************************************************************************************************************/
	/************************************************STATUS ENDS***************************************************************/

	/**************************************************************************************************************************/
	/************************************************SUPPORTING MEMBER BEGINS *************************************************/
	/**** Supporting Member Status****/
	public static final String SUPPORTING_MEMBER_TIMEOUT="supportingmember_timeout";

	public static final String SUPPORTING_MEMBER_APPROVED="supportingmember_approved";

	public static final String SUPPORTING_MEMBER_REJECTED="supportingmember_rejected";

	public static final String SUPPORTING_MEMBER_PENDING="supportingmember_pending";

	public static final String SUPPORTING_MEMBER_NOTSEND="supportingmember_notsend";

	/**** Supporting Member Approval Type****/
	public static final String SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED="AUTO_APPROVAL"; 

	public static final String SUPPORTING_MEMBER_APPROVALTYPE_ONLINE="ONLINE"; 
	/**************************************************************************************************************************/
	/************************************************SUPPORTING MEMBER ENDS *************************************************/

	public static final String QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="question_final_clarificationNotReceivedFromMember";

	public static String QUESTION_PROCESSED_CLARIFICATION_RECIEVED="question_processed_clarificationReceived";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER="question_recommend_clarificationNeededFromMember";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER="question_unstarred_recommend_clarificationNeededFromMember";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER="question_shortnotice_recommend_clarificationNeededFromMember";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER="question_halfHourFromQuestion_recommend_clarificationNeededFromMember";
	
	public static final String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER="question_final_clarificationNeededFromMember";

	public static final String QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_recommend_clarificationNeededFromDepartment";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_unstarred_recommend_clarificationNeededFromDepartment";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_shortnotice_recommend_clarificationNeededFromDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_halfHourFromQuestion_recommend_clarificationNeededFromDepartment";
	
	public static final String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT="question_final_clarificationNeededFromDepartment";

	public static final String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_final_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_unstarred_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_shortnotice_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_halfHourFromQuestion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_recommend_clarificationNeededFromMemberAndDepartment";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_unstarred_recommend_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_shortnotice_recommend_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT="question_halfHourFromQuestion_recommend_clarificationNeededFromMemberAndDepartment";
	
	public static final String QUESTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_DEPARTMENT="question_final_clarificationNotReceivedFromDepartment";

	public static final String QUESTION_FINAL_REPEATADMISSION="question_final_repeatadmission";

	public static final String QUESTION_FINAL_REPEATREJECTION="question_final_repeatrejection";

	public static final String QUESTION_PROCESSED_SENDTOSECTIONOFFICER = "question_processed_sendToSectionOfficer";
	
	public static final String QUESTION_UNSTARRED_PROCESSED_SENDTOSECTIONOFFICER = "question_unstarred_processed_sendToSectionOfficer";

	public static final String QUESTION_SHORTNOTICE_PROCESSED_SENDTOSECTIONOFFICER = "question_shortnotice_processed_sendToSectionOfficer";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_SENDTOSECTIONOFFICER = "question_halfHourFromQuestion_processed_sendToSectionOfficer";
	
	public static final String QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_final_convertToUnstarredAndAdmitClubbedWithPreviousSession";
	
	public static final String QUESTION_UNSTARRED_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_unstarred_final_convertToUnstarredAndAdmitClubbedWithPreviousSession";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_shortnotice_final_convertToUnstarredAndAdmitClubbedWithPreviousSession";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT_CLUBBED="question_halfHourFromQuestion_final_convertToUnstarredAndAdmitClubbedWithPreviousSession";
	
	/**** Question Ministry,Department and Subdepartment parameters key in UserGroup ****/
	public static final String HOUSETYPE_KEY="HOUSETYPE";

	public static final String DEVICETYPE_KEY="DEVICETYPE";

	public static final String MINISTRY_KEY="MINISTRY";

	public static final String DEPARTMENT_KEY="DEPARTMENT";

	public static final String SUBDEPARTMENT_KEY="SUBDEPARTMENT";    
	
	public static final String GROUPSALLOWED_KEY="GROUPSALLOWED";
	
	public static final String ACTORREMARK_KEY = "ACTORREMARK";
	
	public static final String ACTORSTATE_KEY = "ACTORSTATE";
	
	/**** Approving Authority ****/
	public static final String CHAIRMAN="chairman";

	public static final String SPEAKER="speaker";  

	/**** Roles ****/
	public static final String MEMBER_LOWERHOUSE = "MEMBER_LOWERHOUSE";
	
	public static final String MEMBER_UPPERHOUSE = "MEMBER_UPPERHOUSE";
	
	public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
	
	/**** User Group Types    ****/
	/**** Member			  ****/
	public static final String MEMBER="member"; 
	
	public static final String TYPIST = "typist";

	public static final String ASSISTANT="assistant";

	public static final String DEPARTMENT="department";

	public static final String SECTION_OFFICER="section_officer";

	public static final String UNDER_SECRETARY="under_secretary";

	public static final String UNDER_SECRETARY_COMMITTEE="under_secretary_committee";

	public static final String PRINCIPAL_SECRETARY="principal_secretary";

	public static final String TRANSLATOR = "translator";
	
	public static final String DEPARTMENT_DESKOFFICER = "department_deskofficer";

	public static final String OPINION_ABOUT_BILL_DEPARTMENT = "opinionAboutBill_department";

	public static final String RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT = "recommendationFromGovernor_department";

	public static final String RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT = "recommendationFromPresident_department";

	public static final String PRESS = "press";
	
	public static final String OPINION_ABOUT_BILLAMENDMENTMOTION_DEPARTMENT = "opinionAboutBillAmendmentMotion_department";

	/**** My Task Status ****/
	public static final String MYTASK_PENDING="PENDING";

	public static final String MYTASK_COMPLETED="COMPLETED";    
	
	public static final String MYTASK_TIMEOUT="TIMEOUT"; 
	
	/**** Workflow Types ****/
	public static final String APPROVAL_WORKFLOW="APPROVAL_WORKFLOW";
	
	public static final String ADMISSION_WORKFLOW="admission_workflow";
	
	public static final String REJECTION_WORKFLOW="rejection_workflow";

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
	
	public static final String CLUBBING_WORKFLOW="clubbing_workflow";

	public static final String NAMECLUBBING_WORKFLOW="nameclubbing_workflow";
	
	public static final String UNCLUBBING_WORKFLOW="unclubbing_workflow";
	
	public static final String CLUBBING_POST_ADMISSION_WORKFLOW="clubbingPostAdmission_workflow";
	
	public static final String CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW="clubbingWithUnstarredFromPreviousSession_workflow";
	
	public static final String ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW="admitDueToReverseClubbing_workflow";

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
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION="workflow/billamendmentmotion/supportingmember";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION="workflow/billamendmentmotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION="workflow/adjournmentmotion/supportingmember";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION="workflow/adjournmentmotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_PROPRIETYPOINT="workflow/proprietypoint/supportingmember";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_PROPRIETYPOINT="workflow/proprietypoint";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_SPECIALMENTIONNOTICE="workflow/specialmentionnotice";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_SPECIALMENTIONNOTICE="workflow/specialmentionnotice/supportingmember";
	
	public static final String SPECIALMENTIONNOTICE_APPROVAL_WORKFLOW = "SPECIALMENTIONNOTICE_APPROVAL_WORKFLOW";
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

	public static final String QUESTION_STARRED_SUBMISSION_STARTTIME="questions_starred_submissionStartDate";

	public static final String QUESTION_STARRED_SUBMISSION_ENDTIME="questions_starred_submissionEndDate";

	public static final String QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME="questions_starred_submissionFirstBatchStartDate";

	public static final String QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME="questions_starred_submissionFirstBatchEndDate";

	public static final String QUESTION_STARRED_SECONDBATCH_SUBMISSION_STARTTIME="questions_starred_submissionSecondBatchStartDate";

	public static final String QUESTION_STARRED_SECONDBATCH_SUBMISSION_ENDTIME="questions_starred_submissionSecondBatchEndDate";

	public static final String QUESTION_STARRED_FIRST_BALLOT_DATE="questions_starred_firstBallotDate";

	public static final String QUESTION_STARRED_NO_OF_QUESTIONS_FIRST_BATCH="questions_starred_NumberOfQuestionInFirstBatch";

	public static final String QUESTION_STARRED_NO_OF_QUESTIONS_SECOND_BATCH="questions_starred_NumberOfQuestionInSecondBatch";

	public static final String QUESTION_BALLOTING_REQUIRED="questions_starred_isBallotingRequired";

	public static final String QUESTION_STARRED_ROTATION_ORDER_PUBLISHING_DATE="questions_starred_rotationOrderPublishingDate";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_ATTENDANCE="questions_starred_noOfRoundsMemberBallotAttendance";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT="questions_starred_noOfRoundsMemberBallot";  

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_BALLOT="questions_starred_noOfRoundsBallot";

	public static final String QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL="questions_starred_noOfRoundsMemberBallotFinal";

	public static final String QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS="questions_halfhourdiscussion_from_question_numberOfSupportingMembers";

	public static final String QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_NO_OF_SUPPORTING_MEMBERS_COMPARATOR = "questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator";

	public static String QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT="question_recommend_clarificationNeededFromGovt";
	
	public static String QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT="question_final_clarificationNeededFromGovt";

	public static final String QUESTION_STARRED_PROCESSINGMODE="questions_starred_processingMode";
	
	public static final String PROCESSINGMODE = "processingMode";
	
	public static final String APPLICATION_RUNNING_MODE = "APPLICATION_RUNNING_MODE";
	
	public static final String APPLICATION_RUNNING_MODE_LOCAL = "local";
	
	public static final String APPLICATION_RUNNING_MODE_STAGING = "staging";
	
	public static final String APPLICATION_RUNNING_MODE_PRODUCTION = "production";
	
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
	
	/**** Prefix for calling attention ****/
	public static final String DEVICE_MOTIONS_CALLING = "motions_calling_";
	
	/**** Prefix of cutmotions ****/
	public static final String DEVICE_CUTMOTIONS="motions_cutmotion_";
	
	/**** Prefix of discussionmotions ****/
	public static final String DEVICE_DISCUSSIONMOTIONS="motions_discussion_";
	
	/**** Prefix of eventmotion ****/
	public static final String DEVICE_EVENTMOTIONS="motions_eventmotion_";
	
	/**** Prefix of standalonemotion ****/
	public static final String DEVICE_STANDALONEMOTIONS="motions_standalonemotion_";
	
	/**** Prefix of adjournmentmotion ****/
	public static final String DEVICE_ADJOURNMENTMOTIONS="motions_adjournmentmotion_";

	/** Prefix of resolutions **/
	public static final String DEVICE_RESOLUTIONS="resolutions_";
	
	/**** Prefix of special mention notice ****/
	public static final String DEVICE_SPECIALMENTIONNOTICES="notices_specialmention_";

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

	public static final String RESOLUTION_PROCESSED_UNDERCONSIDERATION = "resolution_processed_underconsideration";

	public static final String VOTING_FOR_PASSING_OF_RESOLUTION = "PASSING_OF_RESOLUTION";

	public static final String RESOLUTION_PROCESSED_SELECTEDANDNOTDISCUSSED = "resolution_processed_selectedAndNotDiscussed";
	
	public static final String RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT = "resolution_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String RESOLUTION_RECOMMEND_CLARIFICATIONNEEDEDFROMMEMBERANDDEPARTMENT = "resolution_recommend_clarificationNeededFromMemberAndDepartment";


	/****************Motion Information System********************/
	/**** device types ****/
	public static final String MOTION_CALLING_ATTENTION = "motions_calling_attention";
	
	public static final String AMENDMENT_FOR_BILL_MOTION = "motions_amendment_for_bill";

	/**** Member View ****/	
	public static final String MOTION_INCOMPLETE="motion_incomplete";

	public static final String MOTION_COMPLETE="motion_complete";

	public static final String MOTION_SUBMIT="motion_submit"; 

	/**** System ****/    
	
	public static final String MOTION_SESSIONS_TOBE_SEARCHED_COUNT="MOTION_SESSIONS_TOBE_SEARCHED_COUNT";
	
	public static final String MOTION_SYSTEM_ASSISTANT_PROCESSED="motion_system_assistantprocessed";

	public static final String MOTION_SYSTEM_TO_BE_PUTUP="motion_system_putup";

	public static final String MOTION_SYSTEM_CLUBBED="motion_system_clubbed";

	public static final String MOTION_SYSTEM_CLUBBED_WITH_PENDING="motion_system_clubbedwithpending";
	
	public static final String MOTION_PROCESSED_YAADILAID = "motion_processed_yaadilaid";
	
	public static final String MOTION_PROCESSED_BALLOTED = "motion_processed_balloted";
	
	public static final String MOTION_PUTUP_CLUBBING_POST_ADMISSION = "motion_putup_clubbingPostAdmission";
	
	public static final String MOTION_RECOMMEND_CLUBBING_POST_ADMISSION = "motion_recommend_clubbingPostAdmission";
	
	public static final String MOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION = "motion_recommend_reject_clubbingPostAdmission";
	
	public static final String MOTION_FINAL_CLUBBING_POST_ADMISSION = "motion_final_clubbingPostAdmission";
	
	public static final String MOTION_OPTIONAL_FIELDS_IN_VALIDATION = "MOTION_OPTIONAL_FIELDS_IN_VALIDATION";
	
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
	
	public static final String MOTION_TO_BE_REJECTED = "motion_to_be_rejected";
	
	public static final String MOTION_TO_BE_CLUBBED = "motion_to_be_clubbed";

	public static final String MOTION_FINAL_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER="motion_final_clarificationNotReceivedFromMember";

	public static final String MOTION_PROCESSED_UNDISCUSSED = "motion_processed_undiscussed";
	
	public static final String MOTION_PROCESSED_DISCUSSED = "motion_processed_discussed";
	
	public static final String MOTION_PUTUP_CLUBBING = "motion_putup_clubbing";
	
	public static final String  MOTION_RECOMMEND_CLUBBING = "motion_recommend_clubbing";
	
	public static final String  MOTION_FINAL_CLUBBING = "motion_final_clubbing";
	
	public static final String MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "motion_final_clarificationNeededFromDepartment";
	
	public static final String MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "motion_processed_clarificationNotReceived";
	
	public static final String MOTION_PROCESSED_CLARIFICATION_RECEIVED = "motion_processed_clarificationReceived";
	
	public static final String MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "motion_final_clarificationNeededFromMember";

	public static final String MOTION_RECOMMEND_NAME_CLUBBING = "motion_recommend_nameclubbing";
	
	public static final String MOTION_FINAL_NAME_CLUBBING = "motion_final_nameclubbing";
	
	public static final String MOTION_PUTUP_UNCLUBBING = "motion_putup_unclubbing";
	
	public static final String MOTION_FINAL_REJECT_CLUBBING = "motion_final_reject_clubbing"; 
	
	public static final String MOTION_RECOMMEND_UNCLUBBING = "motion_recommend_unclubbing";
	
	public static final String MOTION_RECOMMEND_REJECT_UNCLUBBING = "motion_recommend_reject_unclubbing";
	
	public static final String MOTION_FINAL_UNCLUBBING = "motion_final_unclubbing";	
	
	public static final String MOTION_FINAL_REJECT_NAME_CLUBBING = "motion_final_reject_nameclubbing";
	
	public static final String MOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "motion_final_reject_clubbingPostAdmission";

	public static final String MOTION_FINAL_REJECT_UNCLUBBING = "motion_final_reject_unclubbing";
	
	public static final String MOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "motion_putup_admitDueToReverseClubbing";

	
	/**** Put Up ****/
	public static final String MOTION_PUTUP_NAME_CLUBBING="motion_putup_nameclubbing";

	public static final String MOTION_PUTUP_ONHOLD="motion_putup_onhold";

	public static final String MOTION_PUTUP_REJECTION="motion_putup_rejection";
	
	public static final String MOTION_FIRST_BATCH_START_TIME = "motions_calling_attention_firstBatchStartTime";
		
	public static final String MOTION_FIRST_BATCH_END_TIME = "motions_calling_attention_firstBatchEndTime";
	
	public static final String MOTION_SECOND_BATCH_START_TIME = "motions_calling_attention_secondBatchStartTime";
	
	public static final String MOTION_SECOND_BATCH_END_TIME = "motions_calling_attention_secondBatchEndTime";
	
	public static final int MOTION_FIRST_BATCH_START_COUNTER = 0;
	
	public static final String MOTION_PROCESSED_ANSWER_RECEIVED = "motion_processed_answerReceived";
	
	public static final String MOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "motion_recommend_admitDueToReverseClubbing";
	
	public static final String MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "motion_final_admitDueToReverseClubbing";
	
	public static final String MOTION_PROCESSED_SEND_TO_SECTIONOFFICER = "motion_processed_sendToSectionOfficer";
	
	public static final String MOTION_PROCESSED_SEND_TO_DEPARTMENT = "motion_processed_sendToDepartment";
	
	public static final String MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT = "motion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String MOTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT = "motion_recommend_clarificationNeededFromMemberAndDepartment";
	
	public static final String MOTION_CLUBBING_MODE = "MOTION_CLUBBING_MODE";
	
	public static final String MOTION_REFERENCING_SEARCH_IN_CURRENT_SESSION = "MOTION_REFERENCING_SEARCH_IN_CURRENT_SESSION";
	
	public static final String MOTION_REFERENCING_SEARCH_STATUSES = "MOTION_REFERENCING_SEARCH_STATUSES";

	/**** Supporting Member ****/
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_MOTION="workflow/motion/supportingmember";

	public static final String QUESTION_FINAL_NAMECLUBBING = "question_final_nameclubbing";

	public static final String QUESTION_FINAL_REJECT_NAMECLUBBING = "question_final_reject_nameclubbing";

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
	
	public static final String BILLAMENDMENTMOTION_GET_REVISION = "BILLAMENDMENTMOTION_GET_REVISION";

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

	public static String CLERK="clerk";

	public static String STATE_MINISTER="state_minister";

	public static String QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_RECIEVED = "question_unstarred_processed_clarificationReceived";

	public static String QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_RECIEVED = "question_shortnotice_processed_clarificationReceived";

	public static String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "question_halfHourFromQuestion_processed_clarificationNotReceived";
	
	public static String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_RECEIVED = "question_halfHourFromQuestion_processed_clarificationReceived";

	public static String QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_GOVT = "question_unstarred_final_clarificationNeededFromGovt";

	public static String QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_FROM_GOVT = "question_shortnotice_final_clarificationNeededFromGovt";

	public static String QUESTION_SHORTNOTICE_PROCESSED_DATEANDANSWERRECEIVED ="question_shortnotice_processed_dateAndAnswerReceived";

	public static String SECRETARY = "secretary";
	
	public static String COMMITTEENAME_KEY = "COMMITTEENAME";
	
	public static String QUESTION_PROCESSED_SENDTODESKOFFICER = "question_processed_sendToDeskOfficer";
	
	public static String DEPUTY_SECRETARY = "deputy_secretary";
	
	public static String QUESTION_FINAL_REANSWER = "question_final_reanswer";
	
	public static String RESOLUTION_PROCESSED_PASSED = "resolution_processed_passed";
	
	public static String USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS = "USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS";
	
	public static String USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS = "USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS";
	
	public static final String PANEL_CHAIRMAN = "PANEL_CHAIRMAN";
	
	public static final String PANEL_SPEAKER = "PANEL_SPEAKER";

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
	
	public static final String PRASHNAVALI = "PRASHNAVALI"; 

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

	public static final String COMMITTEETOUR_RECOMMEND_ADMISSION = "committeetour_recommend_admission";
	
	public static final String COMMITTEETOUR_FINAL_ADMISSION ="committeetour_final_admission";
	
	public static final String COMMITTEETOUR_REQUEST_FOR_TOUR_URL = "committeetour/requestForTour";

	public static final String COMMITTEETOUR_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW = "COMMITTEETOUR_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW";

	public static final String COMMITTEETOUR_RECOMMEND_SENDBACK = "committeetour_recommend_sendback";

	public static final String SENDBACK = "sendback";

	public static final String ADMISSIONTOUR = "admissionTour";

	public static final String COMMITTEETOUR_RETURN_URL = "workflow/committeetour/requestForTour";

	public static final String SERVER_DATETIMEFORMAT = "dd/MM/yyyy HH:mm:ss";

	/****************Bill Information System********************/
	/**** device types ****/
	public static final String NONOFFICIAL_BILL = "bills_nonofficial";

	public static final String GOVERNMENT_BILL = "bills_government";

	/**** bill types ****/
	public static final String ORIGINAL_BILL = "original";

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

	public static final String REPORTING_BILL_LANGUAGES = "REPORTING_BILL_LANGUAGES";

	public static final String MARATHI = "marathi";

	public static final String QUESTION = "Question";
	
	public static final String RESOLUTION = "Resolution";

	public static final String QUESTION_FINAL = "question_final";

	public static final String FORMAT_MEMBERNAME_FIRSTNAMELASTNAME = "firstnamelastname";
	
	public static final String FORMAT_MEMBERNAME_FULLNAME = "fullname";
	
	public static final String FORMAT_MEMBERNAME_FULLNAMELASTNAMEFIRST = "fullnamelastnamefirst";

	public static final String OUESTION_BALLOT_NO_OF_ROUNDS = "3";
	
	public static final String NON_MEMBER_ROLES = "SPEAKER,DEPUTY_SPEAKER,CHAIRMAN,DEPUTY_CHAIRMAN";
	
	/****CutMotion****/
	public static final String MOTIONS_CUTMOTION_BUDGETARY = "motions_cutmotion_budgetary";
	public static final String MOTIONS_CUTMOTION_SUPPLEMENTARY = "motions_cutmotion_supplementary";
	/**** Statuses ****/
	
	public static final String CUTMOTION_INCOMPLETE = "cutmotion_incomplete";
	public static final String CUTMOTION_COMPLETE = "cutmotion_complete";
	public static final String CUTMOTION_SUBMIT = "cutmotion_submit";
	
	public static final String CUTMOTION_RECOMMEND_ADMISSION = "cutmotion_recommend_admission";
	public static final String CUTMOTION_RECOMMEND_REJECTION = "cutmotion_recommend_rejection";
	public static final String CUTMOTION_RECOMMEND_SENDBACK = "cutmotion_recommend_sendback";
	public static final String CUTMOTION_RECOMMEND_DISCUSS = "cutmotion_recommend_discuss";
	public static final String CUTMOTION_RECOMMEND_PATRAKDATEAPPROVAL = "cutmotion_recommend_patrakdateapproval";
	
	public static final String CUTMOTION_SYSTEM_ASSISTANT_PROCESSED = "cutmotion_system_assistantprocessed";
	public static final String CUTMOTION_SYSTEM_PUTUP = "cutmotion_system_putup";
	public static final String CUTMOTION_SYSTEM_CLUBBEDWITHPENDING = "cutmotion_system_clubbedwithpending";
	public static final String CUTMOTION_SYSTEM_CLUBBED = "cutmotion_system_clubbed";
	
	public static final String CUTMOTION_FINAL_ADMISSION = "cutmotion_final_admission";
	public static final String CUTMOTION_FINAL_REJECTION = "cutmotion_final_rejection";
	public static final String CUTMOTION_FINAL_PATRAKDATEAPPROVAL = "cutmotion_final_patrakdateapproval";
	
	public static final String CUTMOTION_PUTUP_NAMECLUBBING = "cutmotion_putup_nameclubbing";
	public static final String CUTMOTION_PUTUP_ONHOLD = "cutmotion_putup_onhold";
	
	public static final String CUTMOTION_PROCESSED_SENDTOSECTIONOFFICER = "cutmotion_processed_sendToSectionOfficer";
	public static final String CUTMOTION_PROCESSED_SENDTODEPARTMENT = "cutmotion_processed_sendToDepartment";
	public static final String CUTMOTION_PROCESSED_SENDTODESKOFFICER = "cutmotion_processed_sendToDeskOfficer";
	public static final String CUTMOTION_PROCESSED_ANSWERRECEIVED = "cutmotion_processed_answerReceived";
	public static final String CUTMOTION_PROCESSED_REJECTIONWITHREASON ="cutmotion_processed_rejectionWithReason";
	
	public static final String CUTMOTION_GET_REVISION = "CUTMOTION_GET_REVISION";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_CUTMOTION = "workflow/cutmotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_CUTMOTION = "workflow/cutmotion/supportingmember";
	
	public static final String CUTMOTIONDATE_CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE = "CUTMOTIONDATE_CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE";
	
	public static final String CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE = "CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE";
	
	public static final String CUTMOTIONDATE_DATE_INCOMPLETE = "cutmotiondate_dateincomplete";
	public static final String CUTMOTIONDATE_DATE_COMPLETE = "cutmotiondate_datecomplete";
	public static final String CUTMOTIONDATE_DATE_SUBMIT = "cutmotiondate_datesubmit";
	public static final String CUTMOTIONDATE_SYSTEM_ASSISTANT_DATE_PROCESSED = "cutmotiondate_system_assistant_dateprocessed";
	public static final String CUTMOTIONDATE_RECOMMEND_DATE_ADMISSION = "cutmotiondate_recommend_dateadmission";
	public static final String CUTMOTIONDATE_RECOMMEND_DATE_REJECTION = "cutmotiondate_recommend_daterejection";
	public static final String CUTMOTIONDATE_FINAL_DATE_ADMISSION = "cutmotiondate_final_dateadmission";
	public static final String CUTMOTIONDATE_PROCESSED_DATE_ADMISSION = "cutmotiondate_processed_dateadmission";
	public static final String CUTMOTIONDATE_PROCESSED_DATE_REHJECTION = "cutmotiondate_processed_daterejection";
	public static final String CUTMOTIONDATE_FINAL_DATE_REJECTION = "cutmotiondate_final_daterejection";
	public static final String CUTMOTIONDATE_RECOMMEND_DATE_SENDBACK = "cutmotiondate_recommend_datesendback";
	public static final String CUTMOTIONDATE_RECOMMEND_DATE_DISCUSS = "cutmotiondate_recommend_datediscuss";
	
	public static final String CUTMOTION_REASSIGN_ADMISSION_NUMBER = "CUTMOTION_REASSIGN_ADMISSION_NUMBER";
	public static final String CUTMOTION_REASSIGN_REJECTION_NUMBER = "CUTMOTION_REASSIGN_REJECTION_NUMBER";
	
	public static final String CUTMOTION_PROCESSED_YAADILAID = "cutmotion_processed_yaadilaid";
	public static final String CUTMOTION_PUTUP_CLUBBING_POST_ADMISSION = "cutmotion_putup_clubbingPostAdmission";
	public static final String CUTMOTION_PUTUP_CLUBBING = "cutmotion_putup_clubbing";
	
	public static final String CUTMOTION_BUDGETARY_FIRST_BATCH_START_TIME = "motions_cutmotion_budgetary_firstBatchStartTime";
	public static final String CUTMOTION_BUDGETARY_FIRST_BATCH_END_TIME = "motions_cutmotion_budgetary_firstBatchEndTime";
	public static final String CUTMOTION_BUDGETARY_SECOND_BATCH_START_TIME = "motions_cutmotion_budgetary_secondBatchStartTime";
	public static final String CUTMOTION_BUDGETARY_SECOND_BATCH_END_TIME = "motions_cutmotion_budgetary_secondBatchEndTime";
	
	public static final String CUTMOTION_SUPPLEMENTARY_FIRST_BATCH_START_TIME = "motions_cutmotion_supplementary_firstBatchStartTime";
	public static final String CUTMOTION_SUPPLEMENTARY_FIRST_BATCH_END_TIME = "motions_cutmotion_supplementary_firstBatchEndTime";
	public static final String CUTMOTION_SUPPLEMENTARY_SECOND_BATCH_START_TIME = "motions_cutmotion_supplementary_secondBatchStartTime";
	public static final String CUTMOTION_SUPPLEMENTARY_SECOND_BATCH_END_TIME = "motions_cutmotion_supplementary_secondBatchEndTime";
	
	public static final String CUTMOTION_RECOMMEND_CLUBBING = "cutmotion_recommend_clubbing";
	public static final String CUTMOTION_FINAL_CLUBBING = "cutmotion_final_clubbing";
	public static final String CUTMOTION_RECOMMEND_NAME_CLUBBING = "cutmotion_recommend_nameclubbing";
	public static final String CUTMOTION_FINAL_NAME_CLUBBING = "cutmotion_final_nameclubbing";
	public static final String CUTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION = "cutmotion_recommend_clubbingPostAdmission";
	public static final String CUTMOTION_FINAL_CLUBBING_POST_ADMISSION = "cutmotion_final_clubbingPostAdmission";
	public static final String CUTMOTION_PUTUP_UNCLUBBING = "cutmotion_putup_unclubbing";
	public static final String CUTMOTION_PROCESSED_REPLY_RECEIVED = "cutmotion_processed_replyReceived";
	public static final String CUTMOTION_FINAL_UNCLUBBING = "cutmotion_final_unclubbing";
	public static final String CUTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "cutmotion_final_admitDueToReverseClubbing";
	public static final String CUTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "cutmotion_final_clarificationNeededFromDepartment";
	public static final String CUTMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "cutmotion_processed_clarificationNotReceived";
	public static final String CUTMOTION_PROCESSED_CLARIFICATION_RECEIVED = "cutmotion_processed_clarificationReceived";
	public static final String CUTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "cutmotion_final_clarificationNeededFromMember";
	public static final String CUTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT = "cutmotion_final_clarificationNeededFromMemberAndDepartment";
	public static final String CUTMOTION_PROCESSED_SEND_TO_DEPARTMENT = "cutmotion_processed_sendToDepartment";
	public static final String CUTMOTION_FINAL_REJECT_UNCLUBBING = "cutmotion_final_reject_unclubbing";
	public static final String CUTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "cutmotion_final_reject_clubbingPostAdmission";
	public static final String CUTMOTION_FINAL_REJECT_NAME_CLUBBING = "cutmotion_final_reject_nameclubbing";
	public static final String CUTMOTION_PUTUP_NAME_CLUBBING = "cutmotion_putup_nameclubbing";
	public static final String CUTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "cutmotion_putup_admitDueToReverseClubbing";
	public static final String CUTMOTION_FINAL_REJECT_CLUBBING = "cutmotion_final_reject_clubbing";
	public static final String CUTMOTION_PROCESSED_SEND_TO_SECTIONOFFICER = "cutmotion_processed_sendToSectionOfficer";

	public static final String CUTMOTION_CLUBBING_MODE = "CUTMOTION_CLUBBING_MODE";
	/**** CutMotion****/

	/**** DiscussionMotion ****/
	public static final String DISCUSSIONMOTION_SHORTDURATION = "motions_discussionmotion_shortduration";
	public static final String DISCUSSIONMOTION_LASTWEEK = "motions_discussionmotion_lastweek";
	public static final String DISCUSSIONMOTION_PUBLICIMPORTANCE = "motions_discussionmotion_publicimportance";
	
	/****Statuses for Discussion motion ****/
	public static final String DISCUSSIONMOTION_INCOMPLETE = "discussionmotion_incomplete";
	public static final String DISCUSSIONMOTION_COMPLETE = "discussionmotion_complete";
	public static final String DISCUSSIONMOTION_SUBMIT = "discussionmotion_submit";
	
	public static final String DISCUSSIONMOTION_RECOMMEND_ADMISSION = "discussionmotion_recommend_admission";
	public static final String DISCUSSIONMOTION_RECOMMEND_REJECTION = "discussionmotion_recommend_rejection";
	public static final String DISCUSSIONMOTION_RECOMMEND_NAMECLUBBING = "discussionmotion_recommend_nameclubbing";
	public static final String DISCUSSIONMOTION_RECOMMEND_SENDBACK = "discussionmotion_recommend_sendback";
	public static final String DISCUSSIONMOTION_RECOMMEND_DISCUSS = "discussionmotion_recommend_discuss";
	public static final String DISCUSSIONMOTION_RECOMMEND_PATRAKDATEAPPROVAL = "dicussionmotion_recommend_patrakdateapproval";
	
	public static final String DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED = "discussionmotion_system_assistantprocessed";
	public static final String DISCUSSIONMOTION_SYSTEM_PUTUP = "discussionmotion_system_putup";
	public static final String DISCUSSIONMOTION_SYSTEM_CLUBBEDWITHPENDING = "discussionmotion_system_clubbedwithpending";
	public static final String DISCUSSIONMOTION_SYSTEM_CLUBBED = "discussionmotion_system_clubbed";
	
	public static final String DISCUSSIONMOTION_FINAL_ADMISSION = "discussionmotion_final_admission";
	public static final String DISCUSSIONMOTION_FINAL_REJECTION = "discussionmotion_final_rejection";
	public static final String DISCUSSIONMOTION_FINAL_PATRAKDATEAPPROVAL = "discussionmotion_final_patrakdateapproval";
	
	public static final String DISCUSSIONMOTION_PUTUP_NAMECLUBBING = "discussionmotion_putup_nameclubbing";
	public static final String DISCUSSIONMOTION_PUTUP_ONHOLD = "discussionmotion_putup_onhold";
	
	public static final String DISCUSSIONMOTION_PROCESSED_SENDTOSECTIONOFFICER = "discussionmotion_processed_sendToSectionOfficer";
	public static final String DISCUSSIONMOTION_PROCESSED_SENDTODEPARTMENT = "discussionmotion_processed_sendToDepartment";
	public static final String DISCUSSIONMOTION_PROCESSED_SENDTODESKOFFICER = "discussionmotion_processed_sendToDeskOfficer";
	public static final String DISCUSSIONMOTION_PROCESSED_ANSWERRECEIVED = "discussionmotion_processed_answerReceived";
	public static final String DISCUSSIONMOTION_PROCESSED_REJECTIONWITHREASON ="discussionmotion_processed_rejectionWithReason";
	
	public static final String DISCUSSIONMOTION_PROCESSED_YAADILAID = "discussionmotion_processed_yaadilaid";
	
	public static final String DISCUSSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION = "discussionmotion_putup_clubbingPostAdmission";
	
	public static final String DISCUSSIONMOTION_PUTUP_CLUBBING = "discussionmotion_putup_clubbing";
	
	public static final String DISCUSSIONMOTION_RECOMMEND_CLUBBING = "discussionmotion_recommend_clubbing";
	
	public static final String DISCUSSIONMOTION_FINAL_CLUBBING = "discussionmotion_final_clubbing";
	
	public static final String DISCUSSIONMOTION_RECOMMEND_NAME_CLUBBING = "discussionmotion_recommend_nameclubbing";

	public static final String DISCUSSIONMOTION_FINAL_NAME_CLUBBING = "discussionmotion_final_nameclubbing";
	
	public static final String DISCUSSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION = "discussionmotion_recommend_clubbingPostAdmission";
	
	public static final String DISCUSSIONMOTION_FINAL_CLUBBING_POST_ADMISSION = "discussionmotion_final_clubbingPostAdmission";
	
	public static final String DISCUSSIONMOTION_PUTUP_UNCLUBBING = "discussionmotion_putup_unclubbing";

	public static final String DISCUSSIONMOTION_PROCESSED_ANSWER_RECEIVED = "discussionmotion_processed_answerReceived";

	public static final String DISCUSSIONMOTION_FINAL_UNCLUBBING = "discussionmotion_final_unclubbing";
	
	public static final String DISCUSSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "discussionmotion_final_admitDueToReverseClubbing";
	
	public static final String DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "discussionmotion_final_clarificationNeededFromDepartment";
	
	public static final String DISCUSSIONMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "discussionmotion_processed_clarificationNotReceived";
	
	public static final String DISCUSSIONMOTION_PROCESSED_CLARIFICATION_RECEIVED = "discussionmotion_processed_clarificationReceived";
	
	public static final String DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "discussionmotion_final_clarificationNeededFromMember";
	
	public static final String DISCUSSIONMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT = "discussionmotion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String DISCUSSIONMOTION_PROCESSED_SEND_TO_DEPARTMENT = "discussionmotion_processed_sendToDepartment";
	
	public static final String DISCUSSIONMOTION_PROCESSED_SEND_TO_SECTIONOFFICER = "discussionmotion_processed_sendToSectionOfficer";
	
	public static final String DISCUSSIONMOTION_FINAL_REJECT_CLUBBING = "discussionmotion_final_reject_clubbing";
	
	public static final String DISCUSSIONMOTION_FINAL_REJECT_NAME_CLUBBING = "discussionmotion_final_reject_nameclubbing";
	
	public static final String DISCUSSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "discussionmotion_final_reject_clubbingPostAdmission";
	
	public static final String DISCUSSIONMOTION_FINAL_REJECT_UNCLUBBING = "discussionmotionmotion_final_reject_unclubbing";
	
	public static final String DISCUSSIONMOTION_GET_REVISION = "DISCUSSIONMOTION_GET_REVISION";
		
	public static final String APPROVAL_WORKFLOW_URLPATTERN_DISCUSSIONMOTION = "workflow/discussionmotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_DISCUSSIONMOTION = "workflow/discussionmotion/supportingmember";
	
	public static final String DISCUSSIONMOTION_CLUBBING_MODE = "DISCUSSIONMOTION_CLUBBING_MODE";
	
	public static final String DISCUSSIONMOTION_SHORTDURATION_NO_OF_SUPPORTING_MEMBERS="motions_discussionmotion_shortduration_numberOfSupportingMembers";

	public static final String DISCUSSIONMOTION_SHORTDURATION_NO_OF_SUPPORTING_MEMBERS_COMPARATOR = "motions_discussionmotion_shortduration_numberOfSupportingMembersComparator";

	
	/**** EventMotion ****/
	public static final String EVENTMOTION_CONDOLENCE = "motions_eventmotion_condolence";
	public static final String EVENTMOTION_CONGRATULATORY = "motions_eventmotion_congratulatory";
	/**** Statuses ****/
	
	public static final String EVENTMOTION_INCOMPLETE = "eventmotion_incomplete";
	public static final String EVENTMOTION_COMPLETE = "eventmotion_complete";
	public static final String EVENTMOTION_SUBMIT = "eventmotion_submit";
	
	public static final String EVENTMOTION_RECOMMEND_ADMISSION = "eventmotion_recommend_admission";
	public static final String EVENTMOTION_RECOMMEND_REJECTION = "eventmotion_recommend_rejection";
	public static final String EVENTMOTION_RECOMMEND_NAMECLUBBING = "eventmotion_recommend_nameclubbing";
	public static final String EVENTMOTION_RECOMMEND_SENDBACK = "eventmotion_recommend_sendback";
	public static final String EVENTMOTION_RECOMMEND_DISCUSS = "eventmotion_recommend_discuss";
	public static final String EVENTMOTION_RECOMMEND_PATRAKDATEAPPROVAL = "eventmotion_recommend_patrakdateapproval";
	
	public static final String EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED = "eventmotion_system_assistantprocessed";
	public static final String EVENTMOTION_SYSTEM_PUTUP = "eventmotion_system_putup";
	public static final String EVENTMOTION_SYSTEM_CLUBBEDWITHPENDING = "eventmotion_system_clubbedwithpending";
	public static final String EVENTMOTION_SYSTEM_CLUBBED = "eventmotion_system_clubbed";
	
	public static final String EVENTMOTION_FINAL_ADMISSION = "eventmotion_final_admission";
	public static final String EVENTMOTION_FINAL_REJECTION = "eventmotion_final_rejection";
	public static final String EVENTMOTION_FINAL_PATRAKDATEAPPROVAL = "eventmotion_final_patrakdateapproval";
	
	public static final String EVENTMOTION_PUTUP_NAMECLUBBING = "eventmotion_putup_nameclubbing";
	public static final String EVENTMOTION_PUTUP_ONHOLD = "eventmotion_putup_onhold";
	
	public static final String EVENTMOTION_PROCESSED_SENDTOSECTIONOFFICER = "eventmotion_processed_sendToSectionOfficer";
	public static final String EVENTMOTION_PROCESSED_SENDTODEPARTMENT = "eventmotion_processed_sendToDepartment";
	public static final String EVENTMOTION_PROCESSED_ANSWERRECEIVED = "eventmotion_processed_answerReceived";
	public static final String EVENTMOTION_PROCESSED_REJECTIONWITHREASON ="eventmotion_processed_rejectionWithReason";
	
	public static final String EVENTMOTION_PROCESSED_YAADILAID = "eventmotion_processed_yaadilaid";
	
	public static final String EVENTMOTION_PUTUP_CLUBBING_POST_ADMISSION = "eventmotion_putup_clubbingPostAdmission";
	
	public static final String EVENTMOTION_PUTUP_CLUBBING = "eventmotion_putup_clubbing";
	
	public static final String EVENTMOTION_RECOMMEND_CLUBBING = "eventmotion_recommend_clubbing";
	
	public static final String EVENTMOTION_FINAL_CLUBBING = "eventmotion_final_clubbing";
	
	public static final String EVENTMOTION_FINAL_UNCLUBBING = "eventmotion_final_unclubbing";
	
	public static final String EVENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "eventmotion_final_admitDueToReverseClubbing";
	
	public static final String EVENTMOTION_RECOMMEND_NAME_CLUBBING = "eventmotion_recommend_nameclubbing";

	public static final String EVENTMOTION_FINAL_NAME_CLUBBING = "eventmotion_final_nameclubbing";
	
	public static final String EVENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION = "eventmotion_recommend_clubbingPostAdmission";
	
	public static final String EVENTMOTION_FINAL_CLUBBING_POST_ADMISSION = "eventmotion_final_clubbingPostAdmission";
	
	public static final String EVENTMOTION_PUTUP_UNCLUBBING = "eventmotion_putup_unclubbing";

	public static final String EVENTMOTION_PROCESSED_ANSWER_RECEIVED = "eventmotion_processed_answerReceived";
	
	public static final String EVENTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT = "eventmotion_final_clarificationNeededFromDepartment";

	public static final String EVENTMOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED = "eventmotion_processed_clarificationNotReceived";
	
	public static final String EVENTMOTION_PROCESSED_CLARIFICATION_RECEIVED = "eventmotion_processed_clarificationReceived";

	public static final String EVENTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER = "eventmotion_final_clarificationNeededFromMember";
	
	public static final String EVENTMOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT = "eventmotion_final_clarificationNeededFromMemberAndDepartment";
	
	public static final String EVENTMOTION_PROCESSED_SEND_TO_DEPARTMENT = "eventmotion_processed_sendToDepartment";
	
	public static final String EVENTMOTION_PROCESSED_SEND_TO_SECTION_OFFICER = "eventmotion_processed_sendToSectionOfficer";
	
	public static final String EVENTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "eventmotion_putup_admitDueToReverseClubbing";
	
	public static final String EVENTMOTION_FINAL_REJECT_CLUBBING = "eventmotion_final_reject_clubbing";
	
	public static final String EVENTMOTION_FINAL_REJECT_NAME_CLUBBING = "eventmotion_final_reject_nameclubbing";
	
	public static final String EVENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "eventmotion_final_reject_clubbingPostAdmission";
 
	public static final String EVENTMOTION_FINAL_REJECT_UNCLUBBING = "eventmotion_final_reject_unclubbing";


	public static final String EVENTMOTION_GET_REVISION = "EVENTMOTION_GET_REVISION";
		
	public static final String APPROVAL_WORKFLOW_URLPATTERN_EVENTMOTION = "workflow/eventmotion";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_EVENTMOTION = "workflow/eventmotion/supportingmember";
	
	public static final String EVENTMOTION_CLUBBING_MODE = "EVENTMOTION_CLUBBING_MODE";
	/**** EventMotion ****/
	
	/****************Bill Amendment Motion********************/
	public static final String BILLAMENDMENT_MOTION = "motions_billamendment";
	/**** member status  ****/	
	public static final String BILLAMENDMENTMOTION_INCOMPLETE="billamendmentmotion_incomplete";

	public static final String BILLAMENDMENTMOTION_COMPLETE="billamendmentmotion_complete";

	public static final String BILLAMENDMENTMOTION_SUBMIT="billamendmentmotion_submit";

	public static final String BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED="billamendmentmotion_system_assistantprocessed";
	
	public static final String BILLAMENDMENTMOTION_SYSTEM_CLUBBED="billamendmentmotion_system_clubbed";

	/**** System status  ****/	
	public static final String BILLAMENDMENTMOTION_SYSTEM_CLUBBED_WITH_PENDING="billamendmentmotion_system_clubbedwithpending";
	
	/**** Translation & Its Workflow Status ****/
	public static final String BILLAMENDMENTMOTION_TRANSLATION_NOTSEND = "translation_notsend";

	public static final String BILLAMENDMENTMOTION_TRANSLATION_COMPLETED = "translation_completed";

	public static final String BILLAMENDMENTMOTION_TRANSLATION_TIMEOUT = "translation_timeout";

	public static final String BILLAMENDMENTMOTION_TRANSLATION_CANCELLED = "translation_cancelled";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_TRANSLATION="billamendmentmotion_recommend_translation";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECT_TRANSLATION="billamendmentmotion_recommend_reject_translation";

	public static final String BILLAMENDMENTMOTION_FINAL_TRANSLATION="billamendmentmotion_final_translation";

	public static final String BILLAMENDMENTMOTION_FINAL_REJECT_TRANSLATION="billamendmentmotion_final_reject_translation";

	/**** Opinion Seeking From Law and JD & Its Workflow Status ****/
	public static final String BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_NOTSEND = "opinionFromLawAndJD_notsend";

	public static final String BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_RECEIVED = "opinionFromLawAndJD_received";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_OPINION_FROM_LAWANDJD="billamendmentmotion_recommend_opinionFromLawAndJD";

	public static final String BILLAMENDMENTMOTION_FINAL_OPINION_FROM_LAWANDJD="billamendmentmotion_final_opinionFromLawAndJD";
	
	/**** Recommendation status ****/	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_ADMISSION="billamendmentmotion_recommend_admission";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECTION="billamendmentmotion_recommend_rejection";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_NAMECLUBBING="billamendmentmotion_recommend_nameclubbing";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECT_NAMECLUBBING="billamendmentmotion_recommend_reject_nameclubbing";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_SENDBACK="billamendmentmotion_recommend_sendback";	

	public static final String BILLAMENDMENTMOTION_RECOMMEND_DISCUSS="billamendmentmotion_recommend_discuss";

	/**** Final status ****/ 
	public static final String BILLAMENDMENTMOTION_FINAL_ADMISSION="billamendmentmotion_final_admission";    

	public static final String BILLAMENDMENTMOTION_FINAL_REJECTION="billamendmentmotion_final_rejection";
	
	public static final String BILLAMENDMENTMOTION_FINAL_NAMECLUBBING = "billamendmentmotion_final_nameclubbing";

	public static final String BILLAMENDMENTMOTION_FINAL_REJECT_NAMECLUBBING = "billamendmentmotion_final_reject_nameclubbing";
	
	/**** Processed status ****/ 
	public static final String BILLAMENDMENTMOTION_PROCESSED_REJECTIONWITHREASON = "billamendmentmotion_processed_rejectionWithReason";
	
	/**** Put Up ****/
	public static final String BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING="billamendmentmotion_putup_nameclubbing";
	
	public static final String BILLAMENDMENTMOTION_PUTUP_REJECTION="billamendmentmotion_putup_rejection";
	
	/**** Clubbing related statuses ****/
	public static final String BILLAMENDMENTMOTION_PUTUP_CLUBBING="billamendmentmotion_putup_clubbing";
	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_CLUBBING="billamendmentmotion_recommend_clubbing";
	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECT_CLUBBING="billamendmentmotion_recommend_reject_clubbing";
	
	public static final String BILLAMENDMENTMOTION_FINAL_CLUBBING = "billamendmentmotion_final_clubbing";

	public static final String BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING = "billamendmentmotion_final_reject_clubbing";
	
	public static final String BILLAMENDMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION="billamendmentmotion_putup_clubbingPostAdmission";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION="billamendmentmotion_recommend_clubbingPostAdmission";

	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="billamendmentmotion_recommend_reject_clubbingPostAdmission";
	
	public static final String BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION = "billamendmentmotion_final_clubbingPostAdmission";

	public static final String BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "billamendmentmotion_final_reject_clubbingPostAdmission";

	public static final String BILLAMENDMENTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "billamendmentmotion_putup_admitDueToReverseClubbing";
	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "billamendmentmotion_recommend_admitDueToReverseClubbing";
	
	public static final String BILLAMENDMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "billamendmentmotion_final_admitDueToReverseClubbing";
	
	public static final String BILLAMENDMENTMOTION_PUTUP_UNCLUBBING="billamendmentmotion_putup_unclubbing";
	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_UNCLUBBING="billamendmentmotion_recommend_unclubbing";
	
	public static final String BILLAMENDMENTMOTION_RECOMMEND_REJECT_UNCLUBBING = "billamendmentmotion_recommend_reject_unclubbing";
	
	public static final String BILLAMENDMENTMOTION_FINAL_UNCLUBBING="billamendmentmotion_final_unclubbing";
	
	public static final String BILLAMENDMENTMOTION_FINAL_REJECT_UNCLUBBING = "billamendmentmotion_final_reject_unclubbing";
	//==============================================================================================================================

	public static final String QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATEADMITTED = "question_shortnotice_processed_final_dateAdmitted";

	public static final String QUESTION_SHORTNOTICE_PROCESSED_FINAL_DATERESUBMIT = "question_shortnotice_processed_final_dateResumbit";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_FROM_GOVT = "question_halfHourFromQuestion_final_clarificationFromGovt";

	/**** Clubbing related statuses ****/
	/* For Starred Questions */
	public static final String QUESTION_PUTUP_CLUBBING="question_putup_clubbing";
	
	public static final String QUESTION_RECOMMEND_CLUBBING="question_recommend_clubbing";
	
	public static final String QUESTION_RECOMMEND_REJECT_CLUBBING="question_recommend_reject_clubbing";
	
	public static final String QUESTION_FINAL_CLUBBING = "question_final_clubbing";

	public static final String QUESTION_FINAL_REJECT_CLUBBING = "question_final_reject_clubbing";
	
	public static final String QUESTION_PUTUP_CLUBBING_POST_ADMISSION="question_putup_clubbingPostAdmission";

	public static final String QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION="question_recommend_clubbingPostAdmission";

	public static final String QUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="question_recommend_reject_clubbingPostAdmission";
	
	public static final String QUESTION_FINAL_CLUBBING_POST_ADMISSION = "question_final_clubbingPostAdmission";

	public static final String QUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "question_final_reject_clubbingPostAdmission";

	public static final String QUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_putup_admitDueToReverseClubbing";
	
	public static final String QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_recommend_admitDueToReverseClubbing";
	
	public static final String QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_final_admitDueToReverseClubbing";
	
	public static final String QUESTION_PUTUP_UNCLUBBING="question_putup_unclubbing";
	
	public static final String QUESTION_RECOMMEND_UNCLUBBING="question_recommend_unclubbing";
	
	public static final String QUESTION_RECOMMEND_REJECT_UNCLUBBING = "question_recommend_reject_unclubbing";
	
	public static final String QUESTION_FINAL_UNCLUBBING="question_final_unclubbing";
	
	public static final String QUESTION_FINAL_REJECT_UNCLUBBING = "question_final_reject_unclubbing";
	
	public static final String QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_putup_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_recommend_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_recommend_reject_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_final_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_final_reject_clubbingWithUnstarredFromPreviousSession";
	
	/* For Un-Starred Questions */
	public static final String QUESTION_UNSTARRED_PUTUP_CLUBBING="question_unstarred_putup_clubbing";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_CLUBBING="question_unstarred_recommend_clubbing";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING="question_unstarred_recommend_reject_clubbing";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLUBBING = "question_unstarred_final_clubbing";

	public static final String QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING = "question_unstarred_final_reject_clubbing";
	
	public static final String QUESTION_UNSTARRED_PUTUP_CLUBBING_POST_ADMISSION="question_unstarred_putup_clubbingPostAdmission";

	public static final String QUESTION_UNSTARRED_RECOMMEND_CLUBBING_POST_ADMISSION="question_unstarred_recommend_clubbingPostAdmission";

	public static final String QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="question_unstarred_recommend_reject_clubbingPostAdmission";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION = "question_unstarred_final_clubbingPostAdmission";

	public static final String QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_POST_ADMISSION = "question_unstarred_final_reject_clubbingPostAdmission";

	public static final String QUESTION_UNSTARRED_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_unstarred_putup_admitDueToReverseClubbing";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_unstarred_recommend_admitDueToReverseClubbing";
	
	public static final String QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_unstarred_final_admitDueToReverseClubbing";
	
	public static final String QUESTION_UNSTARRED_PUTUP_UNCLUBBING="question_unstarred_putup_unclubbing";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_UNCLUBBING="question_unstarred_recommend_unclubbing";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_REJECT_UNCLUBBING = "question_unstarred_recommend_reject_unclubbing";
	
	public static final String QUESTION_UNSTARRED_FINAL_UNCLUBBING="question_unstarred_final_unclubbing";
	
	public static final String QUESTION_UNSTARRED_FINAL_REJECT_UNCLUBBING = "question_unstarred_final_reject_unclubbing";
	
	public static final String QUESTION_UNSTARRED_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_unstarred_putup_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_unstarred_recommend_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_UNSTARRED_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_unstarred_recommend_reject_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_UNSTARRED_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_unstarred_final_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_UNSTARRED_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_unstarred_final_reject_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_UNSTARRED_PROCESSED_SENDTODESKOFFICER = "question_unstarred_processed_sendToDeskOfficer";
	
	/* For Short Notice Questions */
	public static final String QUESTION_SHORTNOTICE_PUTUP_CLUBBING="question_shortnotice_putup_clubbing";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING="question_shortnotice_recommend_clubbing";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING="question_shortnotice_recommend_reject_clubbing";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLUBBING = "question_shortnotice_final_clubbing";

	public static final String QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING = "question_shortnotice_final_reject_clubbing";
	
	public static final String QUESTION_SHORTNOTICE_PUTUP_CLUBBING_POST_ADMISSION="question_shortnotice_putup_clubbingPostAdmission";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION="question_shortnotice_recommend_clubbingPostAdmission";

	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="question_shortnotice_recommend_reject_clubbingPostAdmission";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION = "question_shortnotice_final_clubbingPostAdmission";

	public static final String QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION = "question_shortnotice_final_reject_clubbingPostAdmission";

	public static final String QUESTION_SHORTNOTICE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_shortnotice_putup_admitDueToReverseClubbing";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_shortnotice_recommend_admitDueToReverseClubbing";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_shortnotice_final_admitDueToReverseClubbing";
	
	public static final String QUESTION_SHORTNOTICE_PUTUP_UNCLUBBING="question_shortnotice_putup_unclubbing";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_UNCLUBBING="question_shortnotice_recommend_unclubbing";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REJECT_UNCLUBBING = "question_shortnotice_recommend_reject_unclubbing";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_UNCLUBBING="question_shortnotice_final_unclubbing";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_REJECT_UNCLUBBING = "question_shortnotice_final_reject_unclubbing";
	
	public static final String QUESTION_SHORTNOTICE_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_shortnotice_putup_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_shortnotice_recommend_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_SHORTNOTICE_RECOMMEND_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_shortnotice_recommend_reject_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_shortnotice_final_clubbingWithUnstarredFromPreviousSession";
	
	public static final String QUESTION_SHORTNOTICE_FINAL_REJECT_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION = "question_shortnotice_final_reject_clubbingWithUnstarredFromPreviousSession";
	
	/* For HDQ Questions */
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING="question_halfHourFromQuestion_putup_clubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING="question_halfHourFromQuestion_recommend_clubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECT_CLUBBING="question_halfHourFromQuestion_recommend_reject_clubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING = "question_halfHourFromQuestion_final_clubbing";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING = "question_halfHourFromQuestion_final_reject_clubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_CLUBBING_POST_ADMISSION="question_halfHourFromQuestion_putup_clubbingPostAdmission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_CLUBBING_POST_ADMISSION="question_halfHourFromQuestion_recommend_clubbingPostAdmission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="question_halfHourFromQuestion_recommend_reject_clubbingPostAdmission";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION = "question_halfHourFromQuestion_final_clubbingPostAdmission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "question_halfHourFromQuestion_final_reject_clubbingPostAdmission";

	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_halfHourFromQuestion_putup_admitDueToReverseClubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_halfHourFromQuestion_recommend_admitDueToReverseClubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "question_halfHourFromQuestion_final_admitDueToReverseClubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PUTUP_UNCLUBBING="question_halfHourFromQuestion_putup_unclubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_UNCLUBBING="question_halfHourFromQuestion_recommend_unclubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_REJECT_UNCLUBBING = "question_halfHourFromQuestion_recommend_reject_unclubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING="question_halfHourFromQuestion_final_unclubbing";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECT_UNCLUBBING = "question_halfHourFromQuestion_final_reject_unclubbing";

	public static final String XSLT_FILE_NOT_FOUND = "xslt_file_not_found";
	
	public static final String HTML_FILE_NOT_FOUND = "html_file_not_found";
	
	public static final String STANDARD_XSLT_FOR_HTML_TO_FO = "xhtml-to-xslfo";
	
	/**************** Adjournment Motion ********************/
	public static final String ADJOURNMENT_MOTION = "motions_adjournment";
	/**** member status  ****/	
	public static final String ADJOURNMENTMOTION_INCOMPLETE="adjournmentmotion_incomplete";

	public static final String ADJOURNMENTMOTION_COMPLETE="adjournmentmotion_complete";

	public static final String ADJOURNMENTMOTION_SUBMIT="adjournmentmotion_submit";

	public static final String ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED="adjournmentmotion_system_assistantprocessed";
	
	public static final String ADJOURNMENTMOTION_SYSTEM_CLUBBED="adjournmentmotion_system_clubbed";

	/**** System status  ****/	
	public static final String ADJOURNMENTMOTION_SYSTEM_CLUBBED_WITH_PENDING="adjournmentmotion_system_clubbedwithpending";
	
	/**** Recommendation status ****/	
	public static final String ADJOURNMENTMOTION_RECOMMEND_ADMISSION="adjournmentmotion_recommend_admission";

	public static final String ADJOURNMENTMOTION_RECOMMEND_REJECTION="adjournmentmotion_recommend_rejection";

	public static final String ADJOURNMENTMOTION_RECOMMEND_NAMECLUBBING="adjournmentmotion_recommend_nameclubbing";

	public static final String ADJOURNMENTMOTION_RECOMMEND_REJECT_NAMECLUBBING="adjournmentmotion_recommend_reject_nameclubbing";

	public static final String ADJOURNMENTMOTION_RECOMMEND_SENDBACK="adjournmentmotion_recommend_sendback";	

	public static final String ADJOURNMENTMOTION_RECOMMEND_DISCUSS="adjournmentmotion_recommend_discuss";

	/**** Final status ****/ 
	public static final String ADJOURNMENTMOTION_FINAL_ADMISSION="adjournmentmotion_final_admission";    

	public static final String ADJOURNMENTMOTION_FINAL_REJECTION="adjournmentmotion_final_rejection";
	
	public static final String ADJOURNMENTMOTION_FINAL_NAMECLUBBING = "adjournmentmotion_final_nameclubbing";

	public static final String ADJOURNMENTMOTION_FINAL_REJECT_NAMECLUBBING = "adjournmentmotion_final_reject_nameclubbing";
	
	/**** Processed status ****/
	public static final String ADJOURNMENTMOTION_PROCESSED_SENDTOSECTIONOFFICER = "adjournmentmotion_processed_sendToSectionOfficer";
	
	public static final String ADJOURNMENTMOTION_PROCESSED_SENDTODEPARTMENT = "adjournmentmotion_processed_sendToDepartment";
	
	public static final String ADJOURNMENTMOTION_PROCESSED_SENDTODESKOFFICER = "adjournmentmotion_processed_sendToDeskOfficer";
	
	public static final String ADJOURNMENTMOTION_PROCESSED_REJECTIONWITHREASON = "adjournmentmotion_processed_rejectionWithReason";
	
	public static final String ADJOURNMENTMOTION_PROCESSED_REPLY_RECEIVED = "adjournmentmotion_processed_replyReceived";
	
	/**** Put Up ****/
	public static final String ADJOURNMENTMOTION_PUTUP_NAMECLUBBING="adjournmentmotion_putup_nameclubbing";
	
	public static final String ADJOURNMENTMOTION_PUTUP_REJECTION="adjournmentmotion_putup_rejection";
	
	/**** Clubbing related statuses ****/
	public static final String ADJOURNMENTMOTION_PUTUP_CLUBBING="adjournmentmotion_putup_clubbing";
	
	public static final String ADJOURNMENTMOTION_RECOMMEND_CLUBBING="adjournmentmotion_recommend_clubbing";
	
	public static final String ADJOURNMENTMOTION_RECOMMEND_REJECT_CLUBBING="adjournmentmotion_recommend_reject_clubbing";
	
	public static final String ADJOURNMENTMOTION_FINAL_CLUBBING = "adjournmentmotion_final_clubbing";

	public static final String ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING = "adjournmentmotion_final_reject_clubbing";
	
	public static final String ADJOURNMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION="adjournmentmotion_putup_clubbingPostAdmission";

	public static final String ADJOURNMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION="adjournmentmotion_recommend_clubbingPostAdmission";

	public static final String ADJOURNMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="adjournmentmotion_recommend_reject_clubbingPostAdmission";
	
	public static final String ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION = "adjournmentmotion_final_clubbingPostAdmission";

	public static final String ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "adjournmentmotion_final_reject_clubbingPostAdmission";

	public static final String ADJOURNMENTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "adjournmentmotion_putup_admitDueToReverseClubbing";
	
	public static final String ADJOURNMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "adjournmentmotion_recommend_admitDueToReverseClubbing";
	
	public static final String ADJOURNMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "adjournmentmotion_final_admitDueToReverseClubbing";
	
	public static final String ADJOURNMENTMOTION_PUTUP_UNCLUBBING="adjournmentmotion_putup_unclubbing";
	
	public static final String ADJOURNMENTMOTION_RECOMMEND_UNCLUBBING="adjournmentmotion_recommend_unclubbing";
	
	public static final String ADJOURNMENTMOTION_RECOMMEND_REJECT_UNCLUBBING = "adjournmentmotion_recommend_reject_unclubbing";
	
	public static final String ADJOURNMENTMOTION_FINAL_UNCLUBBING="adjournmentmotion_final_unclubbing";
	
	public static final String ADJOURNMENTMOTION_FINAL_REJECT_UNCLUBBING = "adjournmentmotion_final_reject_unclubbing";

	/**** Other constants ****/
	public static final String ADJOURNMENTMOTION_GET_REVISION = "ADJOURNMENTMOTION_GET_REVISION";
	/**************** Adjournment Motion Completed ********************/
	
	/**** Status Refactoring of QIS ****/
	public static final String STATUS_INITIAL_STARRED_QUESTION = "question";
	
	public static final String STATUS_INITIAL_UNSTARRED_QUESTION = "question_unstarred";
	
	public static final String STATUS_INITIAL_SHORTNOTICE_QUESTION = "question_shortnotice";
	
	public static final String STATUS_INITIAL_HDQ_QUESTION = "question_halfHourFromQuestion";
	
	/*****************************Prashnavali**************************/
	public static final String PRASHNAVALI_INCOMPLETE = "prashnavali_incomplete";

	public static final String PRASHNAVALI_CREATED = "prashnavali_created";

	public static final String PRASHNAVALI_RECOMMEND_ADMISSION = "prashnavali_recommend_admission";
	
	public static final String PRASHNAVALI_RECOMMEND_REJECTION = "prashnavali_recommend_rejection";
	
	public static final String PRASHNAVALI_FINAL_ADMISSION = "prashnavali_final_admission";
	
	public static final String PRASHNAVALI_FINAL_REJECTION = "prashnavali_final_rejection";
	
	public static final String PRASHNAVALI_RECOMMEND_SENDBACK = "prashnavali_recommend_sendback";
	
	public static final String PRASHNAVALI_WORKFLOW_URL = "workflow/prashnavali/prashnavaliprocess";
	
	public static final String PRASHNAVALI_WORKFLOW_START_URL = "prashnavali/prashnavaliprocess";
	
	public static final String PRASHNAVALI_ADMISSION_WORKFLOW = "admission";
	
	public static final String PRASHNAVALI_REJECTION_WORKFLOW = "rejection";
	
	public static final String ADMISSIONPRASHNAVALI = "admissionPrashnavali";
	
	public static final String PRASHNAVALI_SENDBACK = "prashnavali_recommend_sendbackPrashnavali";
	
	public static final String PRASHNAVALI_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW = "PRASHNAVALI_USERS_EXCLUDED_FROM_CHOOSING_NEXT_ACTOR_IN_WORKFLOW";
	/*****************************Prashnavali**************************/
	
	/**********************QUESTION SEARCH *******************************/
	public static final String QUESTION_SEARCH_DEFAULT = "QUESTION_SEARCH_DEFAULT";
	
	public static final String QUESTION_SEARCH_DEFAULT_DEVICES = "QUESTION_SEARCH_DEFAULT_DEVICES";
	
	public static final String QUESTION_SEARCH_USE_CURRENT_SESSION = "QUESTION_SEARCH_USE_CURRENT_SESSION";
	
	public static final String QUESTION_REFERENCING_SEARCH_STATUSES = "QUESTION_REFERENCING_SEARCH_STATUSES";
	
	public static final String QUESTION_REFERENCING_USE_MINISTRIES_FROM_CURRENT_SESSION = "QUESTION_REFERENCING_USE_MINISTRIES_FROM_CURRENT_SESSION";
	
	public static final String QUESTION_REFERENCING_SEARCH_IN_CURRENT_SESSION = "QUESTION_REFERENCING_SEARCH_IN_CURRENT_SESSION";
	
	/**********************UNSTARRED YAADI STATUSES *******************************/
	public static final String YAADISTATUS_DRAFTED = "yaadi_drafted";
	
	public static final String YAADISTATUS_READY = "yaadi_ready";
	
	public static final String YAADISTATUS_LAID = "yaadi_laid";
	
	public static final String DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION = "DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION";
	
	public static final String DEVICETYPES_HAVING_SUBMISSION_END_DATE_VALIDATION = "DEVICETYPES_HAVING_SUBMISSION_END_DATE_VALIDATION";
	
	public static final String SUBMISSION_START_DATE_SESSION_PARAMETER_KEY = "submissionStartDate";
	
	public static final String SUBMISSION_START_DATE_MOIS_SESSION_PARAMETER_KEY = "submissionStartTime";
	
	public static final String SUBMISSION_END_DATE_SESSION_PARAMETER_KEY = "submissionEndDate";
	
	public static final String SUBMISSION_END_DATE_MOIS_SESSION_PARAMETER_KEY = "submissionEndTime";
	
	public static final String GENERAL_ERROR_PAGE = "generic/error";
	
	public static final String QUERYNAME_STATUS_RECOMMENDATIONS_FOR_INIT_FLOW = "STATUS_RECOMMENDATIONS_FOR_INIT_FLOW";
	
	public static final String ADJOURNMENT_MOTION_CLUBBING_MODE = "ADJOURNMENT_MOTION_CLUBBING_MODE";

	public static final String SPECIALMENTIONNOTICE_CLUBBING_MODE = "SPECIALMENTIONNOTICE_CLUBBING_MODE";

	public static final String CLUBBING_MODE_APPROVAL_WORKFLOW = "approval_workflow";	
	
	public static final String PASSWORD_ENCRYPTION_REQUIRED = "PASSWORD_ENCRYPTION_REQUIRED";
	
	public static final String PASSWORD_ENCRYPTION_REQUIRED_VALUE = "1";
	
	public static final String CLIENTSIDE_PASSWORD_ENCRYPTION_REQUIRED = "CLIENTSIDE_PASSWORD_ENCRYPTION_REQUIRED";
	
	public static final String CLIENTSIDE_PASSWORD_ENCRYPTION_REQUIRED_VALUE = "1";
	
	public static final String INTIMATION_LETTER_FILTER_REMINDERTODEPARTMENTFORANSWER = "reminderToDepartmentForAnswer";
	
	public static final String RIS_CHIEF_REPORTER = "RIS_CHIEF_REPORTER";
	
	public static final String  OFFICER_ON_SPECIAL_DUTY = "officer_on_special_duty";
	
	public static final String QUESTION_UNSTARRED_PROCESSED_REANSWER = "question_unstarred_processed_reanswer";
	
	public static final String MOTION_PROCESSED_SENDTODESKOFFICER = "motion_processed_sendToDeskOfficer";
	
	public static String RIS_REPORTER = "RIS_REPORTER";
	
	public static String CSPT_DEVICES_WITH_SUBDEPARTMENT_FILTER_IN_REFERENCING = "DEVICES_WITH_SUBDEPARTMENT_FILTER_IN_REFERENCING";
	
	public static String DAYS_COUNT_FOR_RECEIVING_CLARIFICATION_FROM_DEPARTMENT = "DAYS_COUNT_FOR_RECEIVING_CLARIFICATION_FROM_DEPARTMENT";
	
	public static String DAYS_COUNT_FOR_RECEIVING_CLARIFICATION_FROM_MEMBER = "DAYS_COUNT_FOR_RECEIVING_CLARIFICATION_FROM_MEMBER";
	
	public static String DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT = "DAYS_COUNT_FOR_RECEIVING_ANSWER_FROM_DEPARTMENT";
	
	public static String QUESTION_PROCESSED_RESENDREVISEDQUESTIONTEXT = "question_processed_resendRevisedQuestionText";
	
	public static String QUESTION_UNSTARRED_PROCESSED_RESENDREVISEDQUESTIONTEXT = "question_unstarred_processed_resendRevisedQuestionText";
	
	public static String QUESTION_UNSTARRED_PROCESSED_RESENDREVISEDQUESTIONTEXTTODEPARTMENT= "question_unstarred_processed_resendRevisedQuestionTextToDepartment";
	
	public static String QUESTION_PROCESSED_RESENDREVISEDQUESTIONTEXTTODEPARTMENT= "question_processed_resendRevisedQuestionTextToDepartment";
	
	public static String LEADER_OF_OPPOSITION = "leader_of_opposition";
	
	public static String QUESTION_PROCESSED_CLARIFICATION_REPUTUP = "question_processed_clarification_reputup";
	
	public static String QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_REPUTUP = "question_unstarred_processed_clarification_reputup";
	
	public static String VALIDATION_FLAG_FOR_LAST_RECEIVING_DATE_FROM_DEPARTMENT = "VALIDATION_FLAG_FOR_LAST_RECEIVING_DATE_FROM_DEPARTMENT";
	
	public static String PART_GET_REVISION = "PART_GET_REVISION";
	
	public static String PARLIAMENTARY_AFFAIRS_MINISTER = "parliamentary_affairs_minister";
	
	public static String DAY_WORKING_SCOPE_COMMON = "common";
	
	public static String DAY_WORKING_SCOPE_HOUSE_PROCEEDING = "house_proceeding";
	
	public static String DAY_WORKING_SCOPE_SECRETARIAT_STAFF = "secretariat_staff";
	
	public static String ANSWER_RECEIVED_MODE_ONLINE = "ONLINE";
	
	public static String REPLY_RECEIVED_MODE_ONLINE = "ONLINE";
	
	public static String ANSWER_RECEIVED_MODE_OFFLINE = "OFFLINE";
	
	public static String REPLY_RECEIVED_MODE_OFFLINE = "OFFLINE";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROM_QUESTION_PROCESSED_SENDDISCUSSIONDATEINTIMATION = "question_halfHourFromQuestion_processed_sendDiscussionDateIntimation";
	
	public static final String STATUS_INCOMPLETE="_incomplete";
	
	public static final String STATUS_COMPLETE="_complete";
	
	public static final String STATUS_SUBMIT="_submit";
	
	public static final String STATUS_SYSTEM_ASSISTANTPROCESSED="_system_assistantprocessed";
	
	public static final String STATUS_SYSTEM_PUTUP="_system_putup";
	
	public static final String STATUS_SYSTEM_CLUBBED="_system_clubbed";
	
	public static final String STATUS_RECOMMEND_ADMISSION="_recommend_admission";
	
	public static final String STATUS_RECOMMEND_REJECTION="_recommend_rejection";
	
	public static final String STATUS_FINAL_ADMISSION="_final_admission";
	
	public static final String STATUS_FINAL_CLARIFICATION_FROM_DEPARTMENT="_final_clarificationNeededFromDepartment";
	
	public static final String STATUS_PROCESSED_SENDTODEPARTMENT="_processed_sendToDepartment";
	
	public static final String STATUS_PROCESSED_SENDTODESKOFFICER = "_processed_sendToDeskOfficer";
	
	public static final String STATUS_FINAL_REJECTION="_final_rejection";
	
	public static final String STATUS_LAPSED="_lapsed";
	
	public static final String QUESTIONSUPPLEMENTARY_WORKFLOW = "questionsupplementary_workflow";
	
	public static final String QUESTION_PROCESSED_SUPPLEMENTARYCLUBBING = "question_processed_supplementaryClubbing";
	
	public static final String QUESTION_PROCESSED_SUPPLEMENTARYCLUBBINGRECEIVED = "question_processed_supplementaryClubbingReceived";
	
	public static final String QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTOSECTIONOFFICER = "question_processed_sendSupplementaryQuestionToSectionOfficer";
	
	public static final String QUESTION_SUPPLEMENTARY_WORKFLOW = "questionsupplementary_workflow";
	
	public static final String QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTODEPARTMENT = "question_processed_sendSupplementaryQuestionToDepartment";
	
	public static final String QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTODESKOFFICER = "question_processed_sendSupplementaryQuestionToDeskOfficer";
	
	public static final String DEPUTY_SECRETARY1 = "deputy_secretary1";
	
	public static final String DEPUTY_SECRETARY2 = "deputy_secretary2";
	
	public static final String QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_SENDTODESKOFFICER = "question_halfHourFromQuestion_processed_sendToDeskOfficer";
	
	public static final String HIGH_SECURITY_PASSWORD_ENABLED_FLAG="HIGH_SECURITY_PASSWORD_ENABLED_FLAG";
	
	public static final String QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED_FROM_MEMBER = "question_processed_clarificationNotReceivedFromMember";
	
	public static final int NOTIFICATIONS_VISIBLE_MAXIMUM_COUNT = 50;
	
	public static final String RESOLUTION_PROCESSED_SENDTODESKOFFICER = "resolution_processed_sendToDeskOfficer";
	
	public static final String STANDALONEMOTION_PROCESSED_SENDTODESKOFFICER = "standalonemotion_processed_sendToDeskOfficer";
	
	/**************** Propriety Point ********************/
	public static final String PROPRIETY_POINT = "proprietypoint";
	/**** member status  ****/	
	public static final String PROPRIETYPOINT_INCOMPLETE="proprietypoint_incomplete";

	public static final String PROPRIETYPOINT_COMPLETE="proprietypoint_complete";

	public static final String PROPRIETYPOINT_SUBMIT="proprietypoint_submit";

	public static final String PROPRIETYPOINT_SYSTEM_ASSISTANT_PROCESSED="proprietypoint_system_assistantprocessed";
	
	/**** Recommendation status ****/	
	public static final String PROPRIETYPOINT_RECOMMEND_ADMISSION="proprietypoint_recommend_admission";

	public static final String PROPRIETYPOINT_RECOMMEND_REJECTION="proprietypoint_recommend_rejection";

	public static final String PROPRIETYPOINT_RECOMMEND_SENDBACK="proprietypoint_recommend_sendback";	

	public static final String PROPRIETYPOINT_RECOMMEND_DISCUSS="proprietypoint_recommend_discuss";

	/**** Final status ****/ 
	public static final String PROPRIETYPOINT_FINAL_ADMISSION="proprietypoint_final_admission";    

	public static final String PROPRIETYPOINT_FINAL_REJECTION="proprietypoint_final_rejection";
	
	/**** Processed status ****/ 
	public static final String PROPRIETYPOINT_PROCESSED_REJECTIONWITHREASON = "proprietypoint_processed_rejectionWithReason";
	
	public static final String PROPRIETYPOINT_PROCESSED_REPLY_RECEIVED = "proprietypoint_processed_replyReceived";
	
	/**** Put Up ****/
	public static final String PROPRIETYPOINT_PUTUP_REJECTION="proprietypoint_putup_rejection";
	
	/**** Other constants ****/
	public static final String PROPRIETYPOINT_GET_REVISION = "PROPRIETYPOINT_GET_REVISION";
	/**************** Propriety Point Completed ********************/
	
	/**** Temporary Count for confirming performance issues if any ****/
	public static long isUserSessionActive_URL_HIT_COUNT = 0; //remove once not needed
	
	public static int DEFAULT_SUBMISSION_PRIORITY = 1000;
	
	public static final String MOTION_SYSTEM_ADVANCECOPYRECEIVED = "motion_system_advanceCopyReceived";
	
	public static final String MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTODEPARTMENT = "motion_processed_resendRevisedMotionTexttoDepartment";
	
	public static final String MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTOSECTIONOFFICER = "motion_processed_resendRevisedMotionTextToSectionOfficer";
	
	public static final String MOTION_PROCESSED_RESENDREVISEDMOTIONTEXT = "motion_processed_resendRevisedMotionText";
	
	public static final String MOTION_PROCESSED_CLARIFICATION_REPUTUP = "motion_processed_clarification_reputup";
	
	/************ Rules Suspension Motion ****************/
	public static final String RULESSUSPENSIONMOTION_SUBMIT = "rulessuspensionmotion_submit";
	
	public static final String RULESSUSPENSIONMOTION_INCOMPLETE = "rulesuspensionmotion_incomplete";
	
	public static final String RULESSUSPENSIONMOTION_COMPLETE = "rulessuspensionmotion_complete";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION = "rulessuspensionmotion_recommend_clubbingPostAdmission";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION = "rulessuspensionmotion_recommend_reject_clubbingPostAdmission";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION = "rulessuspensionmotion_final_clubbingPostAdmission";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION = "rulessuspensionmotion_final_reject_ClubbingPostAdmission";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_UNCLUBBING = "rulessuspensionmotion_recommend_unclubbing";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_REJECT_UNCLUBBING = "rulessuspensionmotion_recommend_reject_unclubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_REJECT_UNCLUBBING = "rulessuspensionmotion_final_reject_unclubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_UNCLUBBING = "rulessuspensionmotion_final_unclubbing";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "rulessuspensionmotion_recommend_admitDueToReverseClubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "rulessuspensionmotion_final_admitDueToReverseClubbing";
	
	public static final String RULESSUSPENSION_MOTION = "motions_rules_suspension";
	
	public static final String RULESSUSPENSIONMOTION_SYSTEM_CLUBBED = "rulessuspensionmotion_system_clubbed";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_ADMISSION = "rulessuspensionmotion_recommend_admission";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_REJECTION = "rulessuspensionmotion_recommend_rejection";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_REJECTION = "rulessuspensionmotion_final_rejection";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_ADMISSION = "rulessuspensionmotion_final_admission";
	
	public static final String SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_RULESSUSPENSIONMOTION = "workflow/rulessuspensionmotion/supportingmember";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_SENDBACK = "rulessuspensionmotion_recommend_sendback";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_DISCUSS = "rulessuspensionmotion_recommend_discuss";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION = "rulessuspension_putup_clubbingPostAdmission";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_REJECTION = "rulessuspensionmotion_putup_rejection";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_UNCLUBBING = "rulessuspensionmotion_putup_unclubbing";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "rulessuspensionmotion_putup_admitDueToReverseClubbing";
	
	public static final String RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED = "rulessuspensionmotion_system_assistantprocessed";
	
	public static final String RULESSUSPENSIONMOTION_GET_REVISION = "rulessuspensionmotion_get_revision";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_CLUBBING = "rulessuspensionmotion_final_clubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING = "rulessuspensionmotion_final_rejectClubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_NAMECLUBBING = "rulessuspensionmotion_final_nameClubbing";
	
	public static final String RULESSUSPENSIONMOTION_FINAL_REJECT_NAMECLUBBING = "rulessuspensionmotion_final_rejectNameClubbing";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING = "rulessuspensionmotion_recommend_clubbing";
	
	public static final String RULESSUSPENSIONMOTION_RECOMMEND_NAMECLUBBING = "rulessuspensionmotion_recommend_nameClubbing";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_CLUBBING = "rulessuspensionmotion_putup_clubbing";
	
	public static final String RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING = "rulessuspensionmotion_putup_nameclubbing";
	
	public static final String APPROVAL_WORKFLOW_URLPATTERN_RULESSUSPENSIONMOTION = "workflow/rulessuspensionmotion";
	
	public static final String JOINT_SECRETARY2 = "joint_secretary2";
	
	public static final String RULES_SUSPENSION_MOTION_CLUBBING_MODE = "RULES_SUSPENSION_MOTION_CLUBBING_MODE";
	
	/**************** Special Mention Notice ********************/
	public static final String SPECIAL_MENTION_NOTICE = "notices_specialmention";
	
	/**** System status  ****/	
	public static final String  SPECIALMENTIONNOTICE_SYSTEM_CLUBBED_WITH_PENDING="specialmentionnotice_system_clubbedwithpending";
	
	/**** Recommendation status ****/	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_ADMISSION="specialmentionnotice_recommend_admission";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_REJECTION="specialmentionnotice_recommend_rejection";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_NAMECLUBBING="specialmentionnotice_recommend_nameclubbing";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_REJECT_NAMECLUBBING="specialmentionnotice_recommend_reject_nameclubbing";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_SENDBACK="specialmentionnotice_recommend_sendback";	

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_DISCUSS="specialmentionnotice_recommend_discuss";

	/**** Final status ****/ 
	public static final String SPECIALMENTIONNOTICE_FINAL_ADMISSION="specialmentionnotice_final_admission";    

	public static final String SPECIALMENTIONNOTICE_FINAL_REJECTION="specialmentionnotice_final_rejection";
	
	public static final String  SPECIALMENTIONNOTICE_FINAL_NAMECLUBBING = "specialmentionnotice_final_nameclubbing";

	public static final String  SPECIALMENTIONNOTICE_FINAL_REJECT_NAMECLUBBING = "specialmentionnotice_final_reject_nameclubbing";
	
	/**** Processed status ****/
	public static final String SPECIALMENTIONNOTICE_PROCESSED_SENDTOSECTIONOFFICER = "specialmentionnotice_processed_sendToSectionOfficer";
	
	public static final String SPECIALMENTIONNOTICE_PROCESSED_SENDTODEPARTMENT = "specialmentionnotice_processed_sendToDepartment";

	public static final String  SPECIALMENTIONNOTICE_PROCESSED_REJECTIONWITHREASON = "specialmentionnotice_processed_rejectionWithReason";
	
	public static final String  SPECIALMENTIONNOTICE_PROCESSED_REPLY_RECEIVED = "specialmentionnotice_processed_replyReceived";
	
	public static final String SPECIALMENTIONNOTICE_PROCESSED_SENDTODESKOFFICER = "specialmentionnotice_processed_sendToDeskOfficer";
	/**** Put Up ****/
	public static final String  SPECIALMENTIONNOTICE_PUTUP_NAMECLUBBING="specialmentionnotice_putup_nameclubbing";
	
	public static final String  SPECIALMENTIONNOTICE_PUTUP_REJECTION="specialmentionnotice_putup_rejection";
	
	
	/**** member status  ****/	
	public static final String SPECIALMENTIONNOTICE_INCOMPLETE="specialmentionnotice_incomplete";

	public static final String SPECIALMENTIONNOTICE_COMPLETE="specialmentionnotice_complete";

	public static final String SPECIALMENTIONNOTICE_SUBMIT="specialmentionnotice_submit";

	public static final String SPECIALMENTIONNOTICE_SYSTEM_ASSISTANT_PROCESSED="specialmentionnotice_system_assistantprocessed";
	
	public static final String SPECIALMENTIONNOTICE_SYSTEM_CLUBBED="specialmentionnotice_system_clubbed";
	
	/**** Clubbing related statuses ****/
	public static final String SPECIALMENTIONNOTICE_PUTUP_CLUBBING="specialmentionnotice_putup_clubbing";
	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_CLUBBING="specialmentionnotice_recommend_clubbing";
	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_REJECT_CLUBBING="specialmentionnotice_recommend_reject_clubbing";
	
	public static final String SPECIALMENTIONNOTICE_FINAL_CLUBBING = "specialmentionnotice_final_clubbing";

	public static final String SPECIALMENTIONNOTICE_FINAL_REJECT_CLUBBING = "specialmentionnotice_final_reject_clubbing";
	
	public static final String SPECIALMENTIONNOTICE_PUTUP_CLUBBING_POST_ADMISSION="specialmentionnotice_putup_clubbingPostAdmission";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_CLUBBING_POST_ADMISSION="specialmentionnotice_recommend_clubbingPostAdmission";

	public static final String SPECIALMENTIONNOTICE_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION="specialmentionnotice_recommend_reject_clubbingPostAdmission";
	
	public static final String SPECIALMENTIONNOTICE_FINAL_CLUBBING_POST_ADMISSION = "specialmentionnotice_final_clubbingPostAdmission";

	public static final String SPECIALMENTIONNOTICE_FINAL_REJECT_CLUBBING_POST_ADMISSION = "specialmentionnotice_final_reject_clubbingPostAdmission";

	public static final String SPECIALMENTIONNOTICE_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING = "specialmentionnotice_putup_admitDueToReverseClubbing";
	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING = "specialmentionnotice_recommend_admitDueToReverseClubbing";
	
	public static final String SPECIALMENTIONNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING = "specialmentionnotice_final_admitDueToReverseClubbing";
	
	public static final String SPECIALMENTIONNOTICE_PUTUP_UNCLUBBING="specialmentionnotice_putup_unclubbing";
	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_UNCLUBBING="specialmentionnotice_recommend_unclubbing";
	
	public static final String SPECIALMENTIONNOTICE_RECOMMEND_REJECT_UNCLUBBING = "specialmentionnotice_recommend_reject_unclubbing";
	
	public static final String SPECIALMENTIONNOTICE_FINAL_UNCLUBBING="specialmentionnotice_final_unclubbing";
	
	public static final String SPECIALMENTIONNOTICE_FINAL_REJECT_UNCLUBBING = "specialmentionnotice_final_reject_unclubbing";	

	public static final String SPECIALMENTIONNOTICE_OPTIONAL_FIELDS_IN_VALIDATION = "SPECIALMENTIONNOTICE_OPTIONAL_FIELDS_IN_VALIDATION";
	/**** Other constants ****/
	public static final String SPECIALMENTIONNOTICEN_GET_REVISION = "SPECIALMENTIONNOTICE_GET_REVISION";
	/**************** Special Mention Notice Completed ********************/
	
	/** The Constant DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE. */
	public static final String DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE="6";
	
	public static final String DEFAULT_MEMBER_USER_START_URL = "question/module?questionType=questions_starred";
	
	/** Default MLS Email Server Host Name **/
	public static final String DEFAULT_EMAIL_HOSTNAME = "mls.org.in";
	
	public static final String STANDALONEMOTION_OPTIONAL_FIELDS_IN_VALIDATION = "STANDALONEMOTION_OPTIONAL_FIELDS_IN_VALIDATION";
	
	public static final String ADJOURNMENTMOTION_OPTIONAL_FIELDS_IN_VALIDATION = "ADJOURNMENTMOTION_OPTIONAL_FIELDS_IN_VALIDATION";
	
	public static final String DEVICETYPES_HAVING_GROUPS = "DEVICETYPES_HAVING_GROUPS";
	
	public static final String END_FLAG_PENDING = "continue";
	
	public static final String END_FLAG_REACHED = "end";
	
}