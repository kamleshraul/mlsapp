<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotiondate.personal" text="Cut Motion Date Settings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	//for controlling actors Index
	var departmentIndex=$("select option:selected").val();
	var departmentCount=parseInt($('#departmentCount').val());
	var totalDepartmentCount=0;
	totalDepartmentCount=departmentCount+totalDepartmentCount;
	function addDepartment(){
		departmentCount=departmentCount+1;
		totalDepartmentCount=totalDepartmentCount+1;
		var text="<div id='department"+departmentCount+"' style='border: 1px solid #000; margin-top: 10px;'>"+
					 "<p style='display: inline;'>"+
			         "<label class='small'>"+$('#departmentDateMessage').val()+"</label>"+
			         "<select name='departmentDate"+departmentCount+"' id='departmentDate"+departmentCount+"' style='width:100px;'>"+
				      	$('#subdepartmentDateMaster').html()+
				      "</select>"+
				   "</p>"+
					  "<p style='display: inline;'>"+
			              "<label class='small'>"+$('#departmentNameMessage').val()+"</label>"+
			              "<select name='departmentName"+departmentCount+"' id='departmentName"+departmentCount+"' style='width:200px;'>"+
					      	$('#subdepartmentMaster').html()+
					      "</select>"+
					   "</p>"+
				      "<input style='margin-left: 10px;' type='button' class='button' id='"+departmentCount+"' value='"+$('#deleteDepartmentMessage').val()+"' onclick='deleteDepartment("+departmentCount+");'>"+
				      "<input type='hidden' id='departmentId"+departmentCount+"' name='departmentId"+departmentCount+"'>"+
					  "<input type='hidden' id='departmentLocale"+departmentCount+"' name='departmentLocale"+departmentCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='departmentVersion"+departmentCount+"' name='departmentVersion"+departmentCount+"'>"+
					  "<input type='hidden' id='departmentDateId"+departmentCount+"' name='departmentDateId"+departmentCount+"'>"+
					  "<input type='hidden' id='departmentDateLocale"+departmentCount+"' name='departmentDateLocale"+departmentCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='departmentDateVersion"+departmentCount+"' name='departmentDateVersion"+departmentCount+"'>"+
					  "</div>"; 
				      var prevCount=departmentCount-1;
				      if(totalDepartmentCount==1){
				    	  $('#addDepartment').after(text);
					  }else{
						  $('#department'+prevCount).after(text);
				      }
				      $('#departmentCount').val(departmentCount); 
				      $('#departmentLevel'+departmentCount).focus();	
				      return departmentCount;		
	}

	function deleteDepartment(id,continous){	
		var departmentId=$('#departmentId'+id).val();			
		if(departmentId != ''){
	    $.delete_('cutmotiondate/'+$("#id").val()+"/"+departmentId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
		    if(data=='SUCCESS'){
		    	$('#department'+id).remove();
		    	totalDepartmentCount=totalDepartmentCount-1;
				if(id==departmentCount){
					if(continous==null){
						departmentCount=departmentCount-1;
					}				
				}
		    }else{
			    $.prompt($("#deleteFailedMessage").val());
		    }
	    }).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
		}else{
			$('#department'+id).remove();
			totalDepartmentCount=totalDepartmentCount-1;
			if(id==departmentCount){
				if(continous==null){
					departmentCount=departmentCount-1;
				}
			}
		}			
	}
	
	function prependOptionToDeviceType() {
		var isDeviceTypeFieldEmpty = $('#isDeviceTypeEmpty').val();
		var optionValue = $('#allOption').val();
		if(isDeviceTypeFieldEmpty == 'true') {
			var option = "<option value='0' selected>" + optionValue + "</option>";
			$('#deviceType').prepend(option);
		}
		else {
			var option = "<option value='0'>" + optionValue + "</option>";
			$('#deviceType').prepend(option);	
		}
	}
	
	$(document).ready(function(){
		$('#subdepartmentMaster').hide();
		$('#subdepartmentDateMaster').hide();
		$('#addDepartment').click(function(){
			addDepartment();
		});	
		
		$("#submitcutmotiondate").click(function(){
			var param = "?usergroup="+$("#userGroup").val()+
					"&usergroupType="+$("#userGroupType").val()+
					"&role="+$("#role").val()+"&operation=submit";
			
			/* $.post($("form[action='cutmotiondate']").attr('action')+param,
					$("form[action='cutmotiondate']").serialize(),function(data){
			}); */
			
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		        	$.post($('form').attr('action')+param, 
		    	            $("form").serialize(),  
		    	            function(data){
		       					$('.tabContent').html(data);
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	   				 	   				
		    	            }).fail(function(){
		    					$.unblockUI();
		    					if($("#ErrorMsg").val()!=''){
		    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    					}else{
		    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    					}
		    					scrollTop();
		    				});
		        }
			}});			
	        return false;
		});
	});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" >
<form:form action="cutmotiondate" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="cutmotiondate.new.heading" text="Enter Cut Motion Date Settings"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="cutmotiondate.devicetype" text="Device Type"/></label>
		<form:select path="deviceType" items="${deviceTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="deviceType" cssClass="validationError"/>
	</p>
	
	<div>
	<input type="button" class="button" id="addDepartment" value="<spring:message code='cutmotiondate' text='Add Department'></spring:message>">
	<input type="hidden" id="departmentCount" name="departmentCount" value="${departmentCount}"/>	
	<input type="hidden" id="deleteDepartmentMessage" name="deleteDepartmentMessage" value="<spring:message code='cutmotiondate.deleteDepartment' text='Delete Department'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="departmentNameMessage" name="departmentNameMessage" value="<spring:message code='cutmotiondate.departmentName' text='Department'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="departmentDateMessage" name="departmentDateMessage" value="<spring:message code='cutmotiondate.departmentDate' text='Discussion Date'></spring:message>" disabled="disabled"/>

	<select name="subdepartmentMaster" id="subdepartmentMaster" disabled="disabled">
		<c:forEach items="${subdepartments}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	
	<select name="subdepartmentDateMaster" id="subdepartmentDateMaster" disabled="disabled">
		<c:forEach items="${discussionDates}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	<form:errors path="departmentDates" cssClass="validationError"></form:errors>
			
	<c:if test="${!(empty domainsubdepartments)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${domain.departmentDates}" var="outer">
		<div id="department${count}" style="border: 1px solid #000; margin-top: 10px;">
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotiondate.departmentDate" text="Discussion Date"/></label>
				<select name="departmentDate${count}" id="departmentDate${count}" style='width:100px;'>
					<c:forEach items="${discussionDates}" var="i">						
						<c:choose>
							<c:when test="${formater.formatDateToString(outer.discussionDate, 'dd/MM/yyyy', pageLocale)==i.id}">		
								<option value="${i.id}" selected="selected"><c:out value="${i.id}">;${outer.discussionDate}</c:out></option>		
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.id}">;${outer.discussionDate}</c:out></option>		
							</c:otherwise>
						</c:choose>	
					</c:forEach>
				</select>
			</p>
			<p style="display: inline;">
			    <label class="small"><spring:message code="cutmotiondate.departmentName" text="Department"/></label>
				<select name="departmentName${count}" id="departmentName${count}" style='width:200px;'>
					<c:forEach items="${subdepartments}" var="i">						
						<c:choose>
							<c:when test="${outer.subDepartment.id==i.id}">		
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
							</c:otherwise>
						</c:choose>	
					</c:forEach>
				</select>
			</p>
			<input type='button' style="margin-left: 10px;" class='button' id='${count}' value='<spring:message code="cutmotiondate.deleteDepartment" text="Delete Department"></spring:message>' onclick='deleteDepartment(${count});'/>
			<input type='hidden' id='departmentId${count}' name='departmentId${count}' value="${outer.id}">
			<input type='hidden' id='departmentVersion${count}' name='departmentVersion${count}' value="${outer.version}">
			<input type='hidden' id='departmentLocale${count}' name='departmentLocale${count}' value="${domain.locale}">
			<input type='hidden' id='departmentLevel${count}' name='departmentLevel${count}' value="${count}">
			<c:set var="count" value="${count+1}"></c:set>	
		</div>	
	</c:forEach>
	</c:if>
	</div>	
	
	<div class="fields">
		<h2></h2>
		<c:choose>
			<c:when test="${internalstatusType=='cutmotiondate_datecomplete' or internalstatusType=='cutmotiondate_dateincomplete'}" >
				<p class="tright">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<input id="submitcutmotiondate" type="button" value="<spring:message code='cutmotiondate.submitdate' text='Submit Date'/>" class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
			</c:when>
		</c:choose>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input type="hidden" id="departmentCount" name="departmentCount" value="${departmentCount}">
	<input type="hidden" id="createdOn" name="createdOn" value="${createdOn}">
	<input type="hidden" id="houseType" name="houseType" value="${houseType}">
	<input type="hidden" id="session" name="session" value="${domain.session.id}">
	<input type="hidden" id="deleteFailedMessage" name="deleteFailedMessage" value="<spring:message code='cutmotiondate.deletefailedmsg' text='Cannot Be Deleted'/>">	
	<input type="hidden" id="isDeviceTypeEmpty" name="isDeviceTypeEmpty" value="${isDeviceTypeEmpty}">	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}" />
	<input type="hidden" id="usergroupType" name="usergroupType" value="${usergroupType}" />
	<input type="hidden" id="pRole" name="role" value="${role}" />	
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input id="submissionMsg" value="<spring:message code='client.prompt.cutmotiondate.submit' text='Do you want to submit the motion.'></spring:message>" type="hidden">
	<input type="hidden" id="setCretedOn" name="setCreatedOn" value="${createOn}" />
</form:form> 
</div>
</body>
</html>