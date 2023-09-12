<%@ include file="/common/taglibs.jsp" %><!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<script>
   $("#submit").click(function(){
	   showTabByIdAndUrl('details_tab', "question/report/subdepartmentChangeReport?deviceType=" + $('#currentDeviceType').val() +"&sessionId="+$('#currentSessionId').val()
			   + "&isEqualToGroup="+$('#selectedGroupSubdepartmentChangeStatus').val()
			   +"&report=QUESTION_SUBDEPARTMENT_CHANGE&reportout=group_changed_subdepartment_change"
			   +"&locale=mr_IN"); 
	   $.fancybox.close();
   });
</script>
</head>
<body>
	<h1 style="text-align:center;"><spring:message code="question.changeSubdepartmentDetailReport" text="Subdepartment Change Detail Report"/></h1>
	<br/>
	<form>
	    <div style="display: flex;justify-content: center;align-items: center;"> 
		<spring:message code="question.selectBoolGroupChange" text="Select whether subdepartment change with group change or without group change ?"/> : &nbsp;&nbsp;&nbsp;
		<select name="selectedGroupSubdepartmentChangeStatus" id="selectedGroupSubdepartmentChangeStatus" style="height: 25px;">
		    <option value="all"><spring:message code="question.allGroupChanges" text="All group changes"/></option> 
			<option value="yes"><spring:message code="question.groupChangeSubdepartment" text="Within Same Group Subdepartment Change"/></option>
			<option value="no"><spring:message code="question.changeGroupChangeSubdepartment" text="Transferred to different Group Subdepartment Change"/></option>
		</select>
		</div><br/>
		<div style="display: flex;justify-content: center;align-items: center;">
     		<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
        </div> 
    </form>
    <input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceType}"/>
    <input type="hidden" name="currentSessionId" id="currentSessionId" value="${sessionId}"/>
</body>
</html>