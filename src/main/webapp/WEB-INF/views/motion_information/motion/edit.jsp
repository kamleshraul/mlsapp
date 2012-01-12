<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
	<title>
	सूचना सादर करणे
	</title>
</head>
<body>
<div class="commandbar">
		<div class="commandbarContent">
			<a  href="#" class="mois" id="motion_assembly">सभा विवरण</a> |
			<a  href="#" class="mois" id="motion_information">सूचना विवरण</a> |
		</div>
</div>	
<form:form  action="motion_information" method="POST" modelAttribute="motionInformation">
	<div class="info">
		 <h2>प्रस्ताव सूचना प्रणाली</h2>		
		<%-- <div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="All fields marked * are mandatory"/></div> --%>
	</div>
	<div id="positionContentDiv">
	<ul>	
					
		<li>
		<label class="desc">मंत्री/विभाग &nbsp;</label>
			<div>
				<form:select cssClass="field select medium" path="department"> 
					<form:option value="Home Affairs">Home Affairs</form:option>
					<form:option value="Forests">Forests</form:option>
					<form:option value="Agriculture">Agriculture</form:option>
				</form:select>
			</div>
		</li>
			
		 <li>
		 <span>
		 	<label class="desc">सूचना प्रस्तुती दिनांक &nbsp;</label>
	  	 	<form:input cssClass="date field text medium" path="submissionDate"/>
		 </span>	
		 </li>
		 	
		<li>
			<label class="desc">सूचना प्रस्तुती वेळ &nbsp;</label>		
			<form:input cssClass="field text" path="submissionTime"/>
		</li>
		 <li>
		<span>
			<label class="desc">सूचनेचा विषय&nbsp;</label>
			<form:input cssClass="field text" path="motionSubject"/>	
		</span>
		</li>
		<li>
		<span>
			<label class="desc">सूचनेचा मजकूर&nbsp;</label>
			<form:input cssClass="field text" path="motionText"/>
		</span>
		</li>
		<li>
		<label class="desc">सूचना देणारे सदस्य &nbsp;</label>
			<div>
				<form:select cssClass="field select medium" path="supportingMembers"> 
					<form:option value="Home Affairs">Member one</form:option>
					<form:option value="Forests">Member Two</form:option>
					<form:option value="Agriculture">Three</form:option>
				</form:select>
			</div>
		</li>
		<li>
		<span>
			<label class="desc">स्विकृत सूचना? &nbsp;</label>
			<form:checkbox cssClass="field text" path="isAdmitted" value="true" id="isAdmitted"/>	
		</span>
		</li>
		
		<li>
		<span>
			<label class="desc">चर्चित सूचना? &nbsp;</label>	
			<form:checkbox cssClass="field text" path="isDiscussed" value="true" id="isDiscussed"/>
		</span>
		</li>
		<li>
		<span>
			<label class="desc">सूचनेवरील चर्चेची तारीख</label>
			<form:input cssClass="date field text medium" path="dateOfDiscussion" id="dateOfDiscussion"/>
		</span>
		</li>
	</ul>
	</div>	
	<input id="saveForm" class="btTxt" type="submit" value="प्रस्तुत" />
	<form:hidden path="version"/>
	<form:hidden path="id"/>	
</form:form>
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
				
<script type="text/javascript">
	function repositionElements(){	
	var positionList=$('#positionList').val();
	var positionArrays=positionList.split("#");
	for(var i=0;i<positionArrays.length-1;i++){
		var id=positionArrays[i];
    	$('#positionContentDiv').prepend($('#'+id));
	}
	}

	$('document').ready(function(){	
			/*
			*Reposition Elements
			*/
			repositionElements();
			
		
			
			$('.HIDDEN').each(function(){
				$(this).hide();
		});
		
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
			})
			     
			if($('#info_msg').val().length!=0){
				$().toastmessage('showToast',{
					text:$('#info_msg').val(),
					type:$('#info_type').val(),
					stayTime:3000,
					inEffectDuration:600
				});
			}
	});		
</script>
</body>
</html>