<%@ include file="/common/taglibs.jsp"%>
<c:if test="${type eq 'error'}">
	<div class="toolTip tpRed clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="update_failed" text="Please correct following errors."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'success'}">
	<div class="toolTip tpGreen clearfix">
		<p style="font-size: 14px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="update_success" text="Data saved successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'taskcompleted'}">
	<div class="toolTip tpGreen clearfix">
		<p style="font-size: 14px;">
			<spring:message code="task_completed" text="Task Completed successfully."/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'taskalreadycompleted'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="task_already_completed" text="Task Completed Already!"/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'clarification_task_alreadytimeout'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="clarification_task_alreadytimeout" text="This task of sending clarification for given device has been timed out!"/>
		</p>
		<p></p>
	</div>
</c:if>
<c:if test="${type eq 'task_already_timeout'}">
	<div class="toolTip tpRed clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="task_already_timeout" text="This task has been timed out!"/>
		</p>
		<p></p>
	</div>
</c:if>