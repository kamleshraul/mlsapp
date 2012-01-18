<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>प्रस्ताव सादर करणे</title>
</head>
<body>
<form:form  action="motion_approval" method="POST">
	<div class="info">
		 <h2>प्रस्ताव सादर करणे</h2>		
		<%-- <div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="All fields marked * are mandatory"/></div> --%>
	</div>
	<div id="positionContentDiv">
	<ul>
		<li>
		<span>
			<label class="desc">सूचनेचा मजकूर&nbsp;</label>
			<input type="textarea" readonly="true" cssClass="field textarea medium" rows="7" cols="70" cssStyle="width:500px" value="Tax"/>
		</span>
		</li>
		<li>
		<span>
			<label class="desc">संशोधित मजकूर&nbsp;</label>
			<input type="textarea" readonly="true" cssClass="field textarea medium" rows="7" cols="70" cssStyle="width:500px" value="Revised Tax"/>
		</span>
		</li>
		<li>
		<span>
			<label class="desc">संशोधित मजकूर&nbsp;</label>
			<input type="textarea" cssClass="field textarea medium" rows="7" cols="70" cssStyle="width:500px"/>
		</span>
		</li>
		<li>
		<span>
			<label class="desc">मापदंड&nbsp;</label>
			<input type="radio" value="A" >सूचनेचा विषय सार्वजनिक महत्वाचा आहे. म्हणून सूचना सुधारल्या प्रमाणे मान्य करण्यात यावी <br>
			<input type="radio" value="B" >विषयाचा उल्लेख राज्यपालांच्या अभिभाषणात आलेला आहे. म्हणून सूचना सुधारल्या प्रमाणे मान्य करण्यात यावी
		</span>
		</li>
		<li>
		<span>
			<label class="desc">टिप्पणी&nbsp;</label>
			<select cssClass="field select medium" cssStyle="width:260px"> 
					<option value="1">लक्ष घालावे </option>
					<option value="2">निवेदन करावे </option>
					<option value="3">चर्चा</option>
			</select>
		</span>
		</li>
		</ul>
</div>
<input id="saveForm" class="btTxt" type="button" value="सेव करणे" onclick="sentforApp()"/>
<div id="info" style="visibility: hidden;">
	<c:choose>
	<c:when test="${(!empty type) && (!empty msg)}">
	<input id="info_type" type="text"  value="${type}">
	<input id="info_msg" type="hidden" value="<spring:message code='${msg}'/>">
	</c:when>
	<c:when test="${(!empty param.type) && (!empty param.msg)}">
	<input id="info_type" type="hidden"  value="${param.type}">
	<input id="info_msg" type="hidden" value="<spring:message code='${param.msg}'/>">
	</c:when>
	<c:otherwise>
	<input id="info_type"  type="text" value="">
	<input id="info_msg" type="hidden" value="">
	</c:otherwise>
	</c:choose>	
	</div>	
	<input type="hidden" id="refreshSe" value="<%=session.getAttribute("refresh")%>">		
	<input type="hidden" id="const_name" value="${constituency.name}">
	<input type="hidden" id="const_id" value="${constituency.id}">	
	<input type="hidden" id="photo_size" value="${photoSize}">	
	<input type="hidden" id="photo_ext" value="${photoExt}">
	<input type="hidden" id="positionList" value="${positionList}">	

</form:form>
<script type="text/javascript">
function sentforApp(){
	alert("Sent to next level");
}
	</script>

</body>
</html>
