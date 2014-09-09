<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="bill.section.edit" text="Edit Section Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function resetControls() {
		$('#number').val("");
		$('#isFirstForHierarchyLevel').val("no");
		$('#isHierarchyLevelWithCustomOrder').val("no");
		$('#orderingSeries').val("");
		$('#orderingSeries').attr('disabled', 'disabled');
		$('#order').val("");
		$('#order').attr('readonly', 'readonly');
		$('#referOrderLink').css('display', 'none');
		$('#referenceText').wysiwyg("setContent", "");
		$('#referenceText').val("");
		$('#referenceTextPara').hide();				
		$('#referTextImage').attr('title',$('#referTextImageTitle').val());
		$('#referText').attr('class', 'referText');
	}
	
	$(document).ready(function(){
		initControls();
		
		$('#currentPage').val("edit");
		$('#listSpacer').hide();
		if($('#updation_message').val()=="success") {
			$('#newLink').show();
		} else {
			$('#newLink').hide();
		}		
		
		$('#number').change(function() {
			$("#error_p").empty();
			$('#order').val("");
			if($('#number').val()!="") {
				if($('#number').val()==$('#currentNumber').val()) {
					$('#isFirstForHierarchyLevel').val($('#isCurrentSectionFirstForHierarchyLevel').val());
					$('#isHierarchyLevelWithCustomOrder').val($('#isHierarchyLevelForCurrentSectionWithCustomOrder').val());
					$('#orderingSeries').val($('#currentOrderingSeries').val());
					$('#orderingSeries').removeAttr('disabled');
					$('#order').val($('#currentOrder').val());					
					$('#order').removeAttr('readonly');
					if($('#isFirstForHierarchyLevel').val()=='no') {
						$('#referOrderLink').css('display', 'inline');
					}							
					$('#referenceText').wysiwyg("setContent", "");
					$('#referenceText').val("");
					$('#referenceTextPara').hide();				
					$('#referTextImage').attr('title',$('#referTextImageTitle').val());
					$('#referText').attr('class', 'referText');
				} else {
					$.get('ref/bill/checkSectionDetails?billId='+$("#key").val()+'&language='+$("#selectedLanguage").val()
							+'&sectionNumber='+$("#number").val()
							+'&sectionOrder='+$("#order").val(), function(data) {
						if(data.info=='invalidSectionNumber') {
							$.prompt($('#invalidSectionNumberMsg').val());
							resetControls();
						} else if(data.info=='section_exists_already') {					
							$.prompt($('#sectionAlreadyExistsMsg').val());
							resetControls();
						} else if(data=="error") {
							if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}	    					
	    					resetControls();
	    					scrollTop();
						} else {
							$('#isFirstForHierarchyLevel').val(data.isFirstForHierarchyLevel);
							$('#isHierarchyLevelWithCustomOrder').val(data.isHierarchyLevelWithCustomOrder);
							$('#orderingSeries').val(data.orderingSeries);	
							$('#orderingSeries').removeAttr('disabled');
							$('#order').removeAttr('readonly');
							if($('#isFirstForHierarchyLevel').val()=='no') {
								$('#referOrderLink').css('display', 'inline');
							}							
							$('#referenceText').wysiwyg("setContent", "");
							$('#referenceText').val("");
							$('#referenceTextPara').hide();				
							$('#referTextImage').attr('title',$('#referTextImageTitle').val());
							$('#referText').attr('class', 'referText');
							if($('#orderingSeries').val()!="") {
								$('#orderingSeries').change();
							}
						}
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						resetControls();
						scrollTop();
					});
				}				
			} else {
				resetControls();
				scrollTop();
			}
		});
		
		$('#orderingSeries').change(function() {
			if($('#number').val()=="") {
				resetControls();
				scrollTop();
				return false;
			}
			if($('#orderingSeries').val()!="") {	
				if($('#isFirstForHierarchyLevel').val()=='yes' || $('#isHierarchyLevelWithCustomOrder').val()=='no') {
					$.get('ref/section/findOrderSequenceByNumberAndSeries?number='+$("#number").val()
							+'&seriesId='+$("#orderingSeries").val(), function(data) {
						$('#order').val(data.name);
						$('#order').change();
					}).fail(function(){
						$('#order').val("");
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});
				}								
			}
		});
		
		$('#order').change(function() {
			$("#error_p").empty();
			if($('#number').val()!="") {
				if($('#number').val()==$('#currentNumber').val()
						&& $('#order').val()==$('#currentOrder').val()) {
					console.log("success in stopping");
					return false;					
				} else {
					var parameters = "billId="+$('#key').val()+"&language="+$('#selectedLanguage').val()								
					+"&sectionNumber="+$('#number').val()
					+"&sectionOrder="+$('#order').val();
					if($('#number').val()==$('#currentNumber').val()
							&& $('#order').val()!=$('#currentOrder').val()) {					
						parameters += "&isCurrent=yes";
					}
					$.get('ref/bill/checkSectionDetails?'+parameters, function(data) {
						if(data.info=="section_with_same_order") {
							$.prompt($('#sectionExistsAtGivenOrderPrompt').val(), {
								buttons: {Ok:true, Cancel:false}, callback: function(v){
									if(!v) {
										$('#order').val("");
										//$('#orderingSeries').val("");
									}
								}
							});						
						} else if(data.info=="error") {
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();								
							$('#order').val("");
							//$('#orderingSeries').val("");
							return false;
						}						
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
						$('#order').val("");	
						//$('#orderingSeries').val("");
					});
				}				
			}									
		});
		
		$('#referOrderLink').click(function() {
			if($('#number').val()!="") {
				$.get('ref/bill/section/referOrders?billId='+$("#key").val()+'&language='+$("#selectedLanguage").val()
						+'&sectionNumber='+$("#number").val(), function(data) {
					$.fancybox.open(data);
				}).fail(function() {
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					resetControls();
					scrollTop();
				});
			} else {
				$.prompt($('#emptySectionNumberMsg').val());
			}
		});
		
		$('#referText').click(function() {			
			if($(this).attr('class')=='referText') {
				if($('#number').val()!="") {
					$.get('ref/bill/section/getReferenceText?number='+$('#number').val()
							+'&language='+$('#selectedLanguage').val()
							+'&billId='+$('#key').val(), function(data) {
						$('#referenceText').wysiwyg("setContent", data.name);
						$('#referenceText').val(data.name);							
						$('#referenceTextPara').show();
						$('html,body').animate({scrollTop:($('#scrollToSectionTextAnchor').offset().top)}, 'slow');
						$('#referTextImage').attr('title',$('#deReferTextImageTitle').val());
						$('#referText').attr('class', 'deReferText');
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
    					}else{
    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    					}
						resetControls();
						scrollTop();
					});					
				}				
			} else if($(this).attr('class')=='deReferText') {
				$('#referenceText').wysiwyg("setContent", "");
				$('#referenceText').val("");
				$('#referenceTextPara').hide();				
				$('#referTextImage').attr('title',$('#referTextImageTitle').val());
				$('#referText').attr('class', 'referText');
			}
		});
		
		$('#submit').click(function() {
			if($('#number').val()=="") {
				alert($('#emptySectionNumberMsg').val());
				return false;
			} else if($('#order').val()=="") {
				alert($('#emptySectionOrderMsg').val());
				return false;
			} else {
				$.prompt($('#sectionUpdationPrompt').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post($('form').attr('action'), $("form").serialize()+'&billId='+$("#key").val()
								+'&language='+$("#selectedLanguage").val()+'&usergroupType='+$("#currentusergroupType").val(),
								function(data){
									alert(data);
									$.unblockUI();
									console.log("success in updating data");
									$('#grid_container').html(data);		       					
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');				    						   				 	   				
			    	            }
						).fail(function(){
	    					$.unblockUI();
	    					console.log("failure in updating data");
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
			}					
		});
	});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="bill/section" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<p id="error_p" style="display: none;">&nbsp;</p>	
	<div class="fields">
		<h2><spring:message code="generic.edit.heading" text="Enter Details"/>
		</h2>
		<p>
			<label class="small"><spring:message code="bill.section.number" text="Section Number"/></label>
			<form:input id="number" path="number" cssClass="sText"/>
			<form:errors path="number" cssClass="validationError"/>
			<input type="hidden" id="currentNumber" name="currentNumber" value="${currentNumber}">
		</p>
		
		<p>
			<label class="small"><spring:message code="bill.section.orderingSeries" text="Ordering Series"/></label>
			<select class="sSelect" id="orderingSeries" name="orderingSeries">
				<option value=""><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${sectionOrderSeries}" var="i">
					<c:choose>
						<c:when test="${i.id==selectedOrderingSeries}">
							<option value="${i.id}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">${i.name}</option>
						</c:otherwise>
					</c:choose>					
				</c:forEach>
			</select>
			<form:errors path="orderingSeries" cssClass="validationError"/>
		</p>
		
		<p style="margin-top: 15px;">
			<label class="small"><spring:message code="bill.section.order" text="Section Order"/></label>
			<input id="order" name="order" class="sInteger" value="${currentOrder}">
			<input type="hidden" id="currentOrder" name="currentOrder" value="${currentOrder}">
			<a id="referOrderLink" class="referOrder" href="#" style="margin-left: 10px;">
				<spring:message code="bill.section.referOrderLinkMsg" text="Refer Orders"/>
			</a>
		</p>
		
		<p style="margin-top: 15px;margin-bottom: -10px;">
			<a href="#" id="referText" class="referText" style="margin-left: 165px;text-decoration: none;">
				<img id="referTextImage" src="./resources/images/Ico_Refer2.jpg" title="<spring:message code='bill.section.referText' text='Refer Text'/>" class="imageLink" />
			</a>						
		</p>
		<p id="referenceTextPara" style="display: none;">
			<label class="wysiwyglabel"><spring:message code="bill.section.referenceText" text="Reference Text"/></label>
			<textarea class="wysiwyg" id="referenceText" name="referenceText"></textarea>
		</p>
		<a href="#" id="scrollToSectionTextAnchor"></a>
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.section.text" text="Section Text"/></label>
			<form:textarea cssClass="wysiwyg" id="text" path="text"></form:textarea>	
			<form:errors path="text" cssClass="validationError"/>	
		</p>
		
		<p class="tright">
			<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
			
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<form:hidden path="hierarchyOrder" />
	<input type="hidden" id="currentLanguage" name="currentLanguage" value="${currentLanguage}"/>
	<input type="hidden" id="invalidSectionNumberMsg" value="<spring:message code='bill.section.invalidSectionNumberMsg' text='You have entered invalid section number'/>">
	<input type="hidden" id="sectionAlreadyExistsMsg" value="<spring:message code='bill.section.sectionAlreadyExistsMsg' text='Section already exists.'/>">
	<input type="hidden" id="sectionExistsAtGivenOrderPrompt" value="<spring:message code='bill.section.sectionExistsAtGivenOrderPrompt' text='There is already a section at given order.. Do you want to insert this section?'/>">
	<input type="hidden" id="emptySectionNumberMsg" value="<spring:message code='bill.section.emptySectionNumberMsg' text='Please enter section number'/>">
	<input type="hidden" id="emptySectionOrderMsg" value="<spring:message code='bill.section.emptySectionOrderMsg' text='Please enter section order'/>">
	<input type="hidden" id="sectionUpdationPrompt" value="<spring:message code='bill.section.sectionUpdationPrompt' text='Do you want to update this section?'/>">
	<input type="hidden" id="updation_message" value="${type}"/>
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="isFirstForHierarchyLevel" value="${isFirstForHierarchyLevel}"/>
<input type="hidden" id="isHierarchyLevelWithCustomOrder" value="${isHierarchyLevelWithCustomOrder}"/>
<input type="hidden" id="isCurrentSectionFirstForHierarchyLevel" value="${isFirstForHierarchyLevel}"/>
<input type="hidden" id="isHierarchyLevelForCurrentSectionWithCustomOrder" value="${isHierarchyLevelWithCustomOrder}"/>
<input type="hidden" id="currentOrderingSeries" value="${selectedOrderingSeries}"/>
</body>
</html>