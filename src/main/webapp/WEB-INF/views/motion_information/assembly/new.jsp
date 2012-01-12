<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>

<html>
<head>
	<title>
		प्रस्ताव सूचना प्रणाली
	</title>
</head>
<body>
<div class="commandbar">
		<div class="commandbarContent">
			<a  href="#" class="mois" id="motion_information">सभा विवरण</a> |
			<a  href="#" class="mois" id="motion_information">प्रस्ताव विवरण</a> 
		</div>
</div>	
<form:form  action="motion_information" method="POST" modelAttribute="motionInformation" >
	<div class="info">
		 <h2>प्रस्ताव सूचना प्रणाली </h2>		
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
		<li>
		<span>
		<label class="desc">वर्ष&nbsp;</label>
		<input type="text" class="field text" name="year" maxlength="4"/>
		</span>
		</li>
		
		<li>
		<span>
		<label class="desc">सभा &nbsp;</label>
			<select>
				<option value="">उन्हाळी</option>
				<option value="">हिवाळी</option>
				<option value="">बजट</option>
	 		</select>
		</span>
		</li>
		
		<li>
		<span>
		<label class="desc">दिनांक&nbsp;</label>
		<form:input cssClass="date field text medium" path="assemblyDate"/>
		</span>
		</li>
				
		</ul>
		</div>
		<input id="saveForm" class="btTxt" type="submit" value="प्रस्तुत" />
	<form:hidden path="id"/>
</form:form>
<script type="text/javascript">
		$('document').ready(function(){
			alert("hello");
			$('.mois').click(function(event){
				var id=1;
				if(id!=undefined && id!=""){
					$.get($(this).attr('id')+'/'+1+'/edit', function(data) {
				  		$('#contentPanel').html(data);
					});
				}			
			return false;			
			});	
			
			initControls();
			
		    $(':input:visible:not([readonly]):first').focus();
		    		 
			$("form").submit(function(e){	
				//e.preventDefault();			
				var count=0;
				$('.MANDATORY').each(function(){
				if($(this).val()==""){
					$(this).after("<span class='field_error'>Required</span>");					
					count++;
					return false;
				}
			});
			});
		});
</script>
</body>
</html>