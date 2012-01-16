<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>प्रस्ताव सादर करणे</title>
</head>
<body>
<form:form  action="motion_approval" method="POST" modelAttrribute="motionApproval">
	<div class="info">
		 <h2>प्रस्ताव सादर करणे</h2>		
		<%-- <div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="All fields marked * are mandatory"/></div> --%>
	</div>
	<div id="positionContentDiv">
	<ul>
		<li>
		<span>
			<label class="desc">सूचनेचा मजकूर&nbsp;</label>
			<select cssClass="field select medium" cssStyle="width:260px"> 
					<option value="1">Tax</option>
					<option value="2">Budget</option>
					<option value="3">Corruption</option>
			</select>
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
<input id="saveForm" class="btTxt" type="submit" value="Send for Approval" />

</form:form>
<script type="text/javascript">
		$('document').ready(function(){
			$('.mois').click(function(event){
				var id=1;
				if(id!=undefined && id!=""){
					$.get($(this).attr('id')+'/'+id+'/edit', function(data) {
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
				if(count>0){
					
				}
				else{
					$.post($('form').attr('action'),  
				            $("form").serialize(),  
				            function(data){	
			   				$('.contentPanel').html(data);	
			   				$('#refresh').val($('#refreshSe').val());
			   				if($('#info_type').val()=='success'){			   				
				   	   	   		$("#grid").trigger("reloadGrid");		   				
							}						   				  					   						   					
				            }                                         
		            );  
				}				
				        return false;  
			});
			
			
		});
</script>
</body>
</html>
