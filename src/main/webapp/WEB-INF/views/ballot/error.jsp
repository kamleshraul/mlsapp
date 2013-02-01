<%@ include file="/common/taglibs.jsp"%>
<c:choose>
<c:when test="${type eq 'REQUEST_PARAMETER_NULL'}">
<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_null" text="Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values"/>
		</p>
		<p></p>
</div>
</c:when>
<c:when test="${type eq 'REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.request_parameter_empty" text="Check request parameter 'houseType,sessionType,sessionYear,questionType' for no values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PREVIOUS_ROUND_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.previous_round_not_locked" text="Please lock present and absent members list of previous round before continuing."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PREVIOUS_ROUND_PRESENTEES_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.previous_round_not_locked" text="Please lock present members list of previous round before continuing."/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'PREVIOUS_ROUND_ABSENTEES_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.previous_round_not_locked" text="Please lock absent members list of previous round before continuing."/>
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
<c:when test="${type eq 'DB_EXCEPTION'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.db_exception" text="An exception has occurred.Check log for details. "/>
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
<c:when test="${type eq 'SESSION_UNDEFINED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.session_undefined" text="Session not defined for selected houseType,sessionType and sessionYear"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'ATTENDANCE_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.attendance_request_parameter_null" text="Check request parameter 'session,questionType,round and attendance' for null values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'ATTENDANCE_REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.attendance_request_parameter_empty" text="Check request parameter 'session,questionType,round and attendance' for no values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOT_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_request_parameter_empty" text="Check request parameter 'session,questionTypev,attendance,round and noofrounds' for no values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOT_REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_request_parameter_empty" text="Check request parameter 'session,questionType,attendance,round and noofrounds' for no values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOTCHOICE_REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotchoice_request_parameter_empty" text="Check request parameter 'session,questionType' for empty values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOTCHOICE_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotchoice_request_parameter_empty" text="Check request parameter 'session,questionType' for null values"/>
		</p>
		<p></p>
	</div>
</c:when>

<c:when test="${type eq 'MEMBERBALLOTCHOICE_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotchoice_request_parameter_empty" text="Check request parameter 'session,questionType' for null values"/>
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
<c:when test="${type eq 'MEMBERBALLOTFINAL_REQUEST_PARAMETER_EMPTY'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotfinal_request_parameter_empty" text="Check Request Parameter 'session,deviceType,group,answering date' For No Values"/>
		</p>
		<p></p>
	</div>
</c:when>
<c:when test="${type eq 'MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotfinal_request_parameter_empty" text="Check Request Parameter 'session,deviceType,group,answering date' For No Values"/>
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
<c:when test="${type eq 'MEMBERBALLOTUPDATECLUBBING_REQUEST_PARAMETER_NULL'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballotupdateclubbing_request_parameter_null" text="Check Request Parameters 'session and deviceType' For Null Values"/>
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
<c:when test="${type eq 'MEMBER_BALLOT_NOT_CREATED_FOR_PREVIOUS_ROUND'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.memberballot_not_created_for_previous_round" text="Please Create Member Ballot For Previous Round To Continue."/>
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
<c:when test="${type eq 'PRESENT_MEMBERS_FOR_ALL_ROUNDS_NOT_LOCKED'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="ballot.error.present_members_for_all_round_not_locked" text="Member Ballot For Absent Members Can Be Created Only After Present Members Ballot"/>
		</p>
		<p></p>
	</div>
</c:when>

</c:choose>







