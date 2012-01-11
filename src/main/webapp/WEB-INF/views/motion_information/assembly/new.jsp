<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	Motion Information System
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
</head>
<body>
<div class="commandbar">
		<div class="commandbarContent">
			<a  href="#" class="mis" id="member_personal_details">Assembly Details</a> |
			<a  href="#" class="mis" id="member_contact_details">Motion Details</a> 
		</div>
</div>	
<form:form  action="motion_information" method="POST" >
	<div class="info">
		 <h2>Motion Information System </h2>		
	</div>
	<div id="positionContentDiv">
	<ul>	
					
		<li>
		<label class="desc">सूचना प्रकार&nbsp;*</label>
			<div>
			<select>
				<option value="Adjournment Motion">स्थगन प्रस्ताव</option>
				<option value="Calling Attention">लक्षवेधी सूचना</option>
				<option value="Half an hour discussion">अर्धा-तास चर्चा</option>
				<option value="Short Duration Discussion">अल्पकालीन चर्चा</option>
			</select>
			</div>
		</li>
			
		
		</ul>
		</div>
	<input id="saveForm" class="btTxt" type="submit" value="Submit" />
	
</form:form>

</body>
</html>