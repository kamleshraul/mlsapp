<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
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
	
	
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
			var params="cutmotiondate="+$("#id").val()+"&status="+value+
			"&userGroup="+$("#usergroup").val()+"&level="+$("#level").val()+
			"&usergroupType="+$("#usergroupType").val()+"&deviceType="+$("#deviceType").val()+
			"&houseType="+$("#selectedHouseType").val()+"&isWF=NO";
			var resourceURL='ref/cutmotiondate/actors?'+params;
		    var sendback=$("#internalStatusMaster option[value='cutmotiondate_recommend_datesendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='cutmotiondate_recommend_datediscuss']").text();		
			$.get(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						text+="<option value='"+data[i].value+"'>"+data[i].name+"</option>";
					}
					$("#actor").html(text);
					$("#actorDiv").show();				
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
					$("#internalStatus").val(value);
					}
					$("#recommendationStatus").val(value);			
					/**** setting level,localizedActorName ****/
					/*  var actor1=data[0].id;
					 var temp=actor1.split("#");
					 $("#level").val(temp[2]);		    
					 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
					 $('#actorName').val(temp[4]);
					 $('#actorName').css('display','inline'); */
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
						$("#internalStatus").val(value);
					}
				    $("#recommendationStatus").val(value);
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
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	
	$(document).ready(function(){
				
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
		
	       
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
			    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();			    
			    loadActors(value);	
			    $("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");		    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#startworkflow").attr("disabled","disabled");
			    $("#submit").removeAttr("disabled");
			}		    
	    });
	    /**** On page Load ****/
	    $("#startworkflow").attr("disabled","disabled");
		$("#submit").removeAttr("disabled");
	    /**** Put Up ****/
		$("#startworkflow").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});			
			$.prompt($('#startWorkflowMessage').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post($('form').attr('action')+'?username='+ $('#authusername').val() +'&operation=startworkflow',  
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
	    
		$('#subdepartmentMaster').hide();
		$('#subdepartmentDateMaster').hide();
		$('#addDepartment').click(function(){
			addDepartment();
		});	
		
		$("#submitcutmotiondate").click(function(){
			var param = "?usergroup="+$("#userGroup").val()+
					"&usergroupType="+$("#userGroupType").val()+
					"&role="+$("#role").val()+"&operation=submit";
			$.post($("form[action='cutmotiondate']").attr('action')+param,
					$("form[action='cutmotiondate']").serialize(),function(data){
			});
		});
	});
		
	
  	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}
</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
        #clubbedQuestionTextsDiv{
        	background: none repeat-x scroll 0 0 #FFF;
		    box-shadow: 0 2px 5px #888888;
		    max-height: 260px;
		    right: 0;
		    position: fixed;
		    top: 10px;
		    width: 300px;
		    z-index: 10000;
		    overflow: auto;
		    border-radius: 10px;
	    }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${error!=''}">
	<h3 style="color: #FF0000;">${error}</h3>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
	<form:form action="cutmotiondate" method="PUT" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<h2>${formattedDeviceType}</h2>
		<form:errors path="version" cssClass="validationError"/>
		
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.houseType" text="House Type"/>*</label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input id="houseType" name="houseType" value="${houseType}" type="hidden">
			<form:errors path="houseType" cssClass="validationError"/>			
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.year" text="Year"/>*</label>
			<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
			<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
			<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
			<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
			<input type="hidden" id="session" name="session" value="${session}"/>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="device.type" text="Type"/>*</label>
			<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
			<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">		
			<form:errors path="deviceType" cssClass="validationError"/>		
		</p>
			
		<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalstatus}">
		<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationstatus}">
		
		<p id="internalStatusDiv">
			<label class="small"><spring:message code="cutmotiondate.currentStatus" text="Current Status"/></label>
			<input class="sText" id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
		</p>

		<c:if test="${(internalstatusType=='cutmotiondate_system_assistant_dateprocessed')}">
			<security:authorize access="hasAnyRole('CMOIS_ASSISTANT')">		
				<p>
					<label class="small"><spring:message code="cutmotiondate.putupfor" text="Put up for"/></label>
					<select id="changeInternalStatus" class="sSelect">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${internalStatuses}" var="i">
						
						<c:if test="${(i.value!='cutmotiondate_recommend_datesendback'&&i.value!='cutmotiondate_recommend_datediscuss') }">
					
							<c:choose>
								<c:when test="${i.id==internalStatus}">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
								</c:when>
								<c:otherwise>
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
					</select>
					
					<select id="internalStatusMaster" style="display:none;">
						<c:forEach items="${internalStatuses}" var="i">
							<option value="${i.value}"><c:out value="${i.id}"></c:out></option>
						</c:forEach>
					</select>	
					<form:errors path="internalStatus" cssClass="validationError"/>	
				</p>
			</security:authorize>
	
			<p id="actorDiv" style="display: none;">
				<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
				<select class="sSelect" name="actor" id="actor">
				</select>
				<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
			</p>		
	
		</c:if>	
		<%-- <p>
			<label class="wysiwyglabel"><spring:message code="cutmotion.remarks" text="Remarks"/></label>
			<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		</p>
			 --%>
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
		<h1>
			${internalStatusType}
		</h1>
		<div class="fields">
			<h2></h2>
			<p class="tright">
	
				<c:if test="${internalstatusType=='cutmotiondate_datesubmit'
							||internalstatusType=='cutmotiondate_system_assistant_dateprocessed'
							}">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<security:authorize access="hasAnyRole('CMOIS_ASSISTANT')">
						<input id="startworkflow" type="button" value="<spring:message code='cutmotion.putupquestion' text='Put Up Cut Motion Date'/>" class="butDef">
					</security:authorize>					
				</c:if>
			</p>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="locale"/>
		<form:hidden path="version"/>
		
		<input type="hidden" name="level" id="level" value="${level}" />
		<input type="hidden" name="status" id="status" value="${status }">
		<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
		<input type="hidden" name="setCreatedOn" id="setCreatedOn" value="${creationDate }">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
		<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
		<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
		<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
	
	</form:form>
	</div>
</div>

<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmDateSubmission" value="<spring:message code='confirm.cutmotiondatesubmission.message' text='Do you want to submit the Cut Motion Date'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='cutmotiondate.startworkflowmessage' text='Do You Want To Put Up Cutmotion Date'></spring:message>" type="hidden">
<input id="oldStatus" value="${status}" type="hidden">
<input type="hidden" id="oldInternalStatus" value="${internalstatus}" />
<input type="hidden" id="oldRecommendationStatus" value="${recommendationstatus}" />
<input id="deviceType" type="hidden" value="${deviceTypeType}" />

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>