<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="bill.section.new" text="New Section Details"/>
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
			$('#currentPage').val("new");
			$('#listSpacer').hide();
			$('#internalKey').val('');
			
			$('#number').change(function() {
				$("#error_p").empty();
				$('#order').val("");
				if($('#number').val()!="") {
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
					$.get('ref/bill/checkSectionDetails?billId='+$("#key").val()+'&language='+$("#selectedLanguage").val()
							+'&sectionNumber='+$("#number").val()
							+'&sectionOrder='+$("#order").val(), function(data) {
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
					$.prompt($('#sectionSubmissionPrompt').val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
							$.post($('form').attr('action'), $("form").serialize()+'&billId='+$("#key").val()
									+'&language='+$("#selectedLanguage").val()+'&usergroupType='+$("#currentusergroupType").val(),
									function(data){
										$.unblockUI();
										$("#cancelFn").val("editSectionRecord");
										$('#grid_container').html(data);											       					
				       					$('html').animate({scrollTop:0}, 'slow');
				       				 	$('body').animate({scrollTop:0}, 'slow');				    									 	   				
				    	            }
							).fail(function(){
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
<form:form action="bill/section" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div class="fields">
		<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		</h2>
		<p>
			<label class="small"><spring:message code="bill.section.number" text="Number"/></label>
			<form:input id="number" path="number" cssClass="sText"/>
			<form:errors path="number" cssClass="validationError"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="bill.section.orderingSeries" text="Ordering Series"/></label>
			<select class="sSelect" id="orderingSeries" name="orderingSeries" disabled="disabled">
				<option value=""><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${sectionOrderSeries}" var="i">
					<option value="${i.id}">${i.name}</option>
				</c:forEach>
			</select>
			<form:errors path="orderingSeries" cssClass="validationError"/>
		</p>
		
		<p style="margin-top: 15px;">
			<label class="small"><spring:message code="bill.section.order" text="Order"/></label>
			<input id="order" name="order" class="sInteger" readonly="readonly">
			<%-- <a id="customOrderLink" class="customOrder" href="#" style="margin-left: 10px;">
				<spring:message code="bill.section.customOrderLinkMsg" text="custom"/>
			</a> --%>
			<a id="referOrderLink" class="referOrder" href="#" style="margin-left: 10px;display: none;">
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
			<form:textarea cssClass="wysiwyg" id="sectionText" path="text"></form:textarea>		
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
	<input type="hidden" id="invalidSectionNumberMsg" value="<spring:message code='bill.section.invalidSectionNumberMsg' text='You have entered invalid section number'/>">
	<input type="hidden" id="sectionAlreadyExistsMsg" value="<spring:message code='bill.section.sectionAlreadyExistsMsg' text='Section already exists.'/>">
	<input type="hidden" id="sectionExistsAtGivenOrderPrompt" value="<spring:message code='bill.section.sectionExistsAtGivenOrderPrompt' text='There is already a section at given order.. Do you want to insert this section?'/>">
	<input type="hidden" id="emptySectionNumberMsg" value="<spring:message code='bill.section.emptySectionNumberMsg' text='Please enter section number'/>">
	<input type="hidden" id="emptySectionOrderMsg" value="<spring:message code='bill.section.emptySectionOrderMsg' text='Please enter section order'/>">
	<input type="hidden" id="sectionSubmissionPrompt" value="<spring:message code='bill.section.sectionSubmissionPrompt' text='Do you want to add this section?'/>">
</form:form>
</div>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>"/>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="customOrderLinkMsg" value="<spring:message code='bill.section.customOrderLinkMsg' text='custom'/>"/>
<input type="hidden" id="autoOrderLinkMsg" value="<spring:message code='bill.section.autoOrderLinkMsg' text='auto'/>"/>
<input type="hidden" id="customOrderPromptMsg" value="<spring:message code='bill.section.customOrderPromptMsg' text='Do you really want to enter the order manually?'/>"/>
<input type="hidden" id="autoOrderPromptMsg" value="<spring:message code='bill.section.autoOrderPromptMsg' text='Do you really want the order to be entered automatically?'/>"/>
<input type="hidden" id="referTextImageTitle" value="<spring:message code='bill.section.referTextImageTitle' text='Refer Text'/>"/>
<input type="hidden" id="deReferTextImageTitle" value="<spring:message code='bill.section.deReferTextImageTitle' text='De-Refer Text'/>"/>

<input type="hidden" id="isFirstForHierarchyLevel" value="no"/>
<input type="hidden" id="isHierarchyLevelWithCustomOrder" value="no"/>
</body>
</html>