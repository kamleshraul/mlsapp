<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${type eq 'REQUEST_PARAMETER_NULL'}">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_null" text="Check Request Parameters For Null Values"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${type eq 'invalid_page_request'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.invalid_page_request" text="Requested Page is not found."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_FOR_VIEWING_PATRAK_BHAG_TWO'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_empty" text="Check Request Parameters For No Values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_FOR_VIEWING_CHOICE_PAGE'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_empty" text="Check Request Parameters For No Values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_TO_SUBMIT_CHOICE'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="resolution.errorcode.INSUFFICIENT_PARAMETERS_TO_SUBMIT_CHOICE" text="All required parameters are not available to fill the choice."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_empty" text="Check Request Parameters For No Values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'NOOFROUNDS_IN_MEMBERBALLOT_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.noofrounds_in_memberballot_notset" text="No. of Rounds In Member Ballot Not Set In Session"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'DB_EXCEPTION'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.db_exception" text="An exception has occurred.Check log for details. "/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBER_BALLOT_NOT_CREATED_FOR_PREVIOUS_ROUND'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_not_created_for_previous_round" text="Please Create Member Ballot For Previous Round To Continue."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'DB_TIMESTAMP_NOT_SET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.db_timestamp_notset" text="Custom Parameter 'DB_TIMESTAMP(yyyy-MM-dd HH:mm:ss)' not set "/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'CREATE_MEMBER_ATTENDANCE_MANUALLY_NOT_SET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.create_member_attendance_manually_not_set" text="Custom parameter 'CREATE_MEMBER_ATTENDANCE_MANUALLY' not set"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${type eq 'FIRST_BATCH_SUBMISSION_START_DATE_NOT_SET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.first_batch_submission_start-date_not_set" text="First Batch Submission Start Date not set in session"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'FIRST_BATCH_SUBMISSION_END_DATE_NOT_SET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.first_batch_submission_end_date_not_set" text="First Batch Submission End Date not set in session"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'FAILED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.failed" text="Member ballot Creation Failed"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOT_DELETE_EXISTING_ROUND_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_delete_existing_round_notset" text="Custom Parameter 'MEMBERBALLOT_DELETE_EXISTING_ROUND' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PREVIOUSROUND_PRESENTLIST_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_previousround_presentlist_not_locked" text="Please create member ballot of previous round to continue"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PRESENT_MEMBERS_FOR_ALL_ROUNDS_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_present_members_for_all_rounds_not_locked" text="Please create member ballot of present members of all round to continue"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'ABSENT_MEMBER_FOR_PREVIOUS_ROUND_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.absent_members_for_previous_round" text="Please create member ballot of previous round to continue"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.absent_members_for_previous_round" text="Please create member ballot of previous round to continue"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_absentballot_after_all_presentballot_notset" text="Custom Parameter 'MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'SESSION_UNDEFINED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.session_undefined" text="Session not defined for selected houseType,sessionType and sessionYear"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'FIRSTBATCH_SUBMISSIONDATE_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.firstbatchsubmission_date_notset" text="First Batch Submission Date Not Set"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${type eq 'MEMBERBALLOTFINAL_FAILED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotfinal_failed" text="Ballot Cannot Be Created At This Time.Please Try Later."/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${type eq 'MEMBERBALLOTUPDATECLUBBING_FAILED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotclubbingupdate_failed" text="Clubbing Cannot Be Updated At This Time.Please Try Later."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'NOOFROUNDS_MEMBERBALLOT_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.totalrounds_memberballot" text="Total No. Of Rounds In Member Ballot Not Set In Session."/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${type eq 'MEMBERBALLOT_DELETE_EXISTING_ROUND_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_delete_existing_round-notset" text="Custom parameter 'MEMBERBALLOT_DELETE_EXISTING_ROUND not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'ATTENDANCE_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.attendance_locked_round" text="Changes cannot be done after Member Ballot is created."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'NO_OF_QUESTIONS_IN_EACH_ROUND_NOT_SET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.noofquestions_in_eachround_notset" text="Custom parameter 'STARRED_MEMBERBALLOTCOUNCIL_ROUND' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_FOR_PRE_BALLOT_CREATION'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.insufficient_parameters_for_pre_ballot_creation" text="Insufficient Parameters for Pre Ballot Creation"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'INSUFFICIENT_PARAMETERS_FOR_VIEWING_BALLOT'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.insufficient_parameters_for_viewing_ballot" text="Insufficient Parameters for Viewing Ballot"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'SUBMIT_BUTTON_NOT_CLICKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.submit_button_not_clicked" text="Please save attendance first."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOT_REORDERING_PATTERN_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotreorderingpatternnotset" text="Custom Parameter 'MEMBERBALLOT_REORDERING_PATTERN' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'SERVER_DATEFORMAT_HYPHEN_NOTSET'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.serverdateformathyphennotset" text="Custom Parameter 'SERVER_DATEFORMAT_HYPHEN' not set"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PRE_BALLOT_NOT_CREATED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.preBallotNotCreated" text="Create the Pre Ballot first"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type=='ballot_not_created'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.ballotNotCreated" text="Create the Ballot first"/>
		</p>
		<p></p>
	</div>
</c:when>
</c:choose>