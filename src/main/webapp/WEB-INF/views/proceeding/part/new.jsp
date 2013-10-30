<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="part.new" text="Parts"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<style type="text/css">
		.imageLink{
			width: 14px;
			height: 14px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		}
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
		
		.bookmarkKey{
			margin-left: 162px;
			margin-top: 30px;
		}
		
		.fields ul{
			background: none !important;
			background: white !important;
		}
		
		.searchBy{
			width:60px;
			font-size: 12px;
		}
	</style>
	<script type="text/javascript">
	
	var keycode=new Array();
	var mainContent="";
	var finalContent="";
	var j=0;
	var currentContent="";
	var contentToBeReplaced="";
	var lineCount=1;
	var maxHeight = '';
		$(document).ready(function(){
			$(document).click(function(){
				 $('#interruptProceedingDiv').css('display','none');
			});
			$('.halfHourDiscussionFromQuestion').hide();
			$('.starredQuestion').hide();
			currentRowId=$('#lastPart').val();
			console.log(currentRowId);
			$('#pageHeadingP').hide();
			$('#mainHeadingP').hide();
			$('.minister').hide();
			$('.public').hide();
			$('.substitute').hide();
			$('#privateLink').hide();
			$('.deviceType').hide();
			
			$('#formattedSubstituteMember').autocomplete({
				minLength:3,			
				source:'ref/member/getmembers?session='+$("#session").val(),
				select:function(event,ui){		
					$("#substituteMember").val(ui.item.id);
				}	
			});
			$( "#formattedMember").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers?session='+$("#session").val(),
				select:function(event,ui){		
					$("#primaryMember").val(ui.item.id);
				}	
			});
			
			$('#mainHeadingLink').click(function(){
				$('#mainHeadingP').toggle();
			});
			
			$('#pageHeadingLink').click(function(){
				$('#pageHeadingP').toggle();
			});
			
			$('#previousLink').click(function(){
				var prevId=$('#lastPart').val();
				showTabByIdAndUrl('part_tab','proceeding/part/'+prevId+'/edit');
				//previousPart();
			});
			
			$('#nextLink').click(function(){
				nextPart();
			});
					
			//alert($('#mainHeadingContent').val());
			
			if($('#mainHeadingContent').val()!=''){
				$('#mainHeading').val($('#mainHeadingContent').val());
				$('#mainHeadingP').show();
			}
			if($('#pageHeadingContent').val()!=''){
				$('#pageHeading').val($('#pageHeadingContent').val());
				$('#pageHeadingP').show();
			}
			
			$('#addBookmark').click(function(){
				//var id=this.id;
				$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#cSlot').val()+'&currentPart='+$('#id').val(),function(data){
					    $.fancybox.open(data, {autoSize: false, width:800, height:500});
				 },'html');
				    return false;
			});
			
			$(".viewProceedingCitation").click(function(){
				$.get('proceeding/part/citations',function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:400});
			    },'html');
			    return false;
			});
			
			
			$('.wysiwyg').wysiwyg({
				events:{
					keydown:function(e){
						if(j==0 && e.which==17){
							keycode[j]=e.which;
							j++;
						}else if(j==1 && e.which==16){
							keycode[j]=e.which;
							j++;
						}else if(j==2 && e.which!=0){
							
							mainContent=$('#proceedingContent').val();
							finalContent=mainContent;					
							mainContent=mainContent.replace(/<br><p><\/p>/g,"");
							mainContent=mainContent.replace(/<br>/g,"");
							mainContent=mainContent.replace(/<p>/g,"");
							mainContent=mainContent.replace(/<\/p>/g,"");
							prevMainContentLength=mainContent.length;
							console.log("MainContent="+mainContent);
							keycode[j]=e.which;
							j++;
						
						}else{
							if(e.which==13){
								
								lineCount=lineCount+1;
								console.log(lineCount);
							}
							currentContent=$('#proceedingContent').wysiwyg('getContent');
							currentContent=currentContent.replace(/<br><p><\/p>/g,"");
							currentContent=currentContent.replace(/<br>/g,"");
							currentContent=currentContent.replace(/<p>/g,"");
							currentContent=currentContent.replace(/<\/p>/g,"");
							console.log("current Content:"+currentContent);
							
							
						}
						
						if(keycode[0]==17 && keycode[1]==16){
							
							var key="";
							if(keycode.length>2){
								e.preventDefault();
								key=keycode;
							}
													
							if(key!=""){
													
							if(currentContent.length>mainContent.length){
								
								currentContent=currentContent.replace(/<br>/g,"");
								var lookupContent=currentContent.substring(mainContent.length);
								if(lookupContent!=""){
									$.get("ref/search?key="+key+"&term="+lookupContent,function(data){
										 $("#autosuggest_menu").empty();
										var menuText = "";
										if(data.length>0){
											for(var i=0;i<data.length;i++){
												menuText+="<option value='"+data[i].value+"'>"+data[i].name;
											}
											$("#autosuggest_menu").html(menuText);
											keycode.length=0;
											j=0;
											var offset = $("#proceedingContent-wysiwyg-iframe").offset();
											$("#shiftDiv").css("display","block");
											$("#autosuggest_menu").css("display","block");
											$("#autosuggest_menu").focus();
											$("#autosuggest_menu").get(0).selectedIndex = 0;;
											$("#shiftDiv").css("left",/* $('#'+contentNo).val().length  + */ offset.left);
											 $("#shiftDiv").css("top",offset.top/* +(lineCount*30) */);
											
										}else{
											$("#shiftDiv").css("display","none");
											j=0;
											keycode.length=0;
										}
									}); 
								}
							}
							}
						}
					}
				},
					
				    controls:{
						fullscreen: {
							visible: true,
							hotkey:{
								"ctrl":1,
								"key":122
							},
							exec: function () {
								if ($.wysiwyg.fullscreen) {
									$.wysiwyg.fullscreen.init(this);
								}
							},
							tooltip: "Fullscreen"
						},
						bookmark:{
							visible: true, 
							 icon: './resources/images/bookmark.png',
				             tags: ['bookmark'], 
				             exec: function(){
				            	 	 $.get('proceeding/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val(),function(data){
				 				   		 $.fancybox.open(data, {autoSize: false, width:800, height:500});
				 			   		 },'html');
				 			    	return false;
				             	}
				            }
						}	  
	      });
			
			$("#autosuggest_menu").click(function(e){
					var content=$(this).val();
					content==content.replace(/<br><p><\/p>/g,"");
					content==content.replace(/<br>/g,"");
					$("#proceedingContent").wysiwyg("setContent",finalContent+content);
					mainContent=$("#proceedingContent").val();
					mainContent=mainContent.replace(/<br><p><\/p>/g,"");
					mainContent=mainContent.replace(/<br>/g,"");
					mainContent=mainContent.replace(/<p>/g,"");
					mainContent=mainContent.replace(/<\/p>/g,"");
					$(this).css("display","none");
					$("proceedingContent-wysiwyg-iframe").focus();
				});
			
			 $('#orderNoInput').change(function(){
				var orderNoText=$(this).val();
				goToPart(orderNoText);
			});  
			
			/* $('#go').click(function(){
				var orderNoText=$('#orderNoInput').val();
				goToPart(orderNoText);
			}); */
			
			$('#ministerLink').click(function(){
				$('.minister').show();
				$('.member').show();
				$('.substitute').hide();
				$('.private').hide();
				$('.public').hide();
				$('#privateLink').show();
				$('#ministerLink').hide();
			});
			
			$('#substituteLink').click(function(){
				$('.substitute').toggle();
			});
			
			$('#privateLink').click(function(){
				$('.member').show();
				$('.minister').hide();
				$('.public').hide();
				$('.substitute').hide();
				$('#privateLink').hide();
				$('#ministerLink').show();
			});
			
			$('#publicLink').click(function(){
				$('.public').show();
				$('.member').hide();
				$('.substitute').hide();
				$('.minister').hide();
				
			});
			
			$('#addDevice').click(function(){
				$('.deviceType').toggle();
			});
			
			$('#deviceNo').change(function(){
				$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),function(data){
					if(data!=null){
						$('#deviceId').val(data.id);
						$('#proceedingContent').wysiwyg('setContent',data.name);
					}
				});
			});
			
			$('#deviceType').change(function(){
				var deviceType="";
				$.ajax({url: 'ref/getTypeOfSelectedDeviceType?deviceTypeId='+ $(this).val(), async: false, success : function(data){	
					deviceType = data;
				}}).done(function(){
					if(deviceType=="questions_halfhourdiscussion_from_question"){
						$('.starredQuestion').show();
					}else{
						$('.halfHourDiscussionFromQuestion').hide();
						$('.starredQuestion').hide();
					}
				});
				
			});
			
			$('#starredQuestionNo').change(function(){
				$.get('ref/gethalfhourdiscussionfromquestion?starredQuestionNo='+$(this).val()+'&session='+$('#session').val(),function(data){
					var text="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
					$('#halfHourDiscussionFromQuestionNo').empty();
					if(data.length>0){
						for(var i=0;i<data.length;i++){
							text=text+"<option value='"+data[i].name+"'>"+data[i].name+"</option>";
						}
						$('#halfHourDiscussionFromQuestionNo').html(text);
						$('.halfHourDiscussionFromQuestion').show();
					}
				});
			});
			
			$('#halfHourDiscussionFromQuestionNo').change(function(){
				$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),function(data){
					if(data!=null){
						$('#deviceId').val(data.id);
						$('#proceedingContent').wysiwyg('setContent',data.name);
					}
				});
			});
			
			$('#formattedMember').change(function(){
				$('#memberImage').attr("src","ref/getphoto?memberId="+$('#primaryMember').val());
				$('#memberPhoto').css("display","inline");
				
			});
			
			$('#resetMainHeading').click(function(){
				$('#mainHeading').wysiwyg('setContent',"");
			});
			$('#resetPageHeading').click(function(){
				$('#pageHeading').wysiwyg('setContent',"");
			});
			
			
		

			 /**** Right Click Menu ****/
			$(".bookmarkKey").contextMenu({
		        menu: 'contextMenuItems'
		    	},
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='viewBookmarkDetails'){
					viewBookmarkDetail(id);	
				}
			});	
				
			$('#interruptedProceeding').click(function(){
				$('#searchBy').toggle();
			});
			
			$('#searchBy').change(function(){
				$.get('ref/getInterruptedProceedings?currentSlot='+$('#currentSlot').val()+"&searchBy="+$(this).val(),function(data){
					var text="";
					if(data.length>0){
					 for(var i=0;i<data.length;i++){
						 text=text+"<option value='"+data[i].value +"'>"+data[i].name+"</option>"; 
						//text=text+"<li><a href='#"+data[i].value+"'>"+data[i].name+"</a></li>";
					 }
					 $('#iProceeding').html(text);
					 $('#iProceeding').attr("size",data.length);
					 $('#interruptProceedingDiv').css('left','469px');
					 $('#interruptProceedingDiv').css('top','245px');
					 $('#interruptProceedingDiv').css('display','block');
					}
					
				});
			});
			
			$('#iProceeding').change(function(){
				var strAction=$(this).val().split("#");
				$('#mainHeading').wysiwyg('setContent',strAction[1]);
				$('#pageHeading').wysiwyg('setContent',strAction[0]);
				$('#mainHeadingP').css('display','block');
				$('#pageHeadingP').css('display','block');
			});
			/* $('#interruptedProceeding').contextMenu({
			        menu: 'iProceeding'
		    		},
		       	 function(action, el, pos) {
					//var id=$(el).attr("id");
					var strAction=action.split('#');
				$('#mainHeading').wysiwyg('setContent',strAction[1]);
				$('#pageHeading').wysiwyg('setContent',strAction[0]);
				$('.mainHeadingP').show();
				$('.pageHeadingP').show();
			});	 */
			 
			$("#deviceType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			$("#substituteMemberMinistry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			$("#primaryMemberMinistry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			$("#primaryMemberDesignation").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			$("#substituteMemberDesignation").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		});
		
		function viewBookmarkDetail(id){
			var params="id="+id;
			$.get('proceeding/part/viewbookmark?'+params,function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');					
			
		}
		
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="proceeding/part" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2>
			<spring:message code="generic.new.heading" text="Enter Details" />
			[
			<spring:message code="generic.id" text="Id"></spring:message>
			:&nbsp;
			<spring:message code="generic.new" text="New"></spring:message>
			]:&nbsp;
			<spring:message code="proceeding.slot" text="Slot"></spring:message>
			: ${slotName}
			
			
			<span style="margin-left: 20px;">
			<a href='#' id="interruptedProceeding"><img src="./resources/images/IcoInterruptProceeding.png" title="Interrupted Proceeding" class="imageLink" /></a>
			<select id="searchBy" class="sSelect searchBy" style="display: none;">
				<option class="searchBy" selected="selected" value='pageHeading'><spring:message code="part.pageHeading"/></option>
				<option class="searchBy" value='mainHeading'><spring:message code="part.mainHeading"/></option>
			</select>
			
			<a href="#" id="mainHeadingLink"><img src="./resources/images/IcoMainHeading.jpg" title="Main Heading" class="imageLink" /></a>
			<a href="#"  id="pageHeadingLink"><img src="./resources/images/IcoPageHeading.jpg" title="Page Heading" class="imageLink" /></a>
			<a href="#" id="addBookmark" class="addBookmark" ><img src="./resources/images/IcoBookMark.jpg" class="imageLink" title="Bookmark" /></a>
			<a href="#" id="viewProceedingCitation" class="viewProceedingCitation"><img src="./resources/images/IcoCitation.jpg" class="imageLink" title="Citation" /></a>
			<a href="#" id="addDevice" class="addDevice"><img src="./resources/images/IcoDeviceType.jpg" title="Device" class="imageLink"/></a>
			<a href='#' id="privateLink"><img src="./resources/images/IcoPrivateMember.jpg" title="Private" class="imageLink" /></a>
			<a href='#' id="ministerLink"><img src="./resources/images/IcoMinister.jpg" title="Minister"  class="imageLink"/></a>
			<a href='#' id="publicLink"><img src="./resources/images/IcoPublicRepresentative.jpg" title="Public" class="imageLink" /></a>
			<a href='#' id="substituteLink"><img src="./resources/images/IcoSubstitute.jpg" title="In place of" class="imageLink"/></a>
			<span style="min-width:80px; margin-right: 100px;">&nbsp;</span>
			<span style="text-align: right;">
			<a href="#" id="previousLink"><img src="./resources/images/IcoBack.jpg" title="Previous Part" class="imageLink"  /></a>
			<input type="text" id="orderNoInput" name="orderNoInput" style="width: 60px"/>
			<!-- <input type="button" id="go" name="go" value="Go" style="width: 21px"/> --> 	
			<a href="#" id="nextLink"><img src="./resources/images/IcoNext.jpg" class="imageLink" title="Next Part"/></a>
			</span>
		</span>
	</h2>
	
	
	<p style="height: 20px">
		<c:forEach items="${bookmarks}" var="i">
			<c:choose>
				<c:when test="${i.textToBeReplaced!=null and i.textToBeReplaced!=''}">
					<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="color:green;">${i.bookmarkKey}</a>
				</c:when>
				<c:otherwise>
					<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="color: red;">${i.bookmarkKey}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</p>
	<p>
		<label class="small"><spring:message code="part.chairPersonRole" text="Chair Person Role"/></label>
		<form:select path="chairPersonRole" items="${roles}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>
	</p>
	
	
	<p class="member">
		<label  class="small"><spring:message code="part.memberName" text=" Member"/></label>
		<input type="text" name="formattedMember" id="formattedMember" class="autosuggest sText"/>
		<form:hidden path="primaryMember"/>
		<div id="memberPhoto" style=" display: none;">
			<img src="" id="memberImage" style="width:60px; margin-right: 445px; float:right; margin-top: -72px; border-radius: 5px; box-shadow: 1px 1px 5px black;"/> 
		</div>
	</p>
	
	<p class="minister">
		<label class="small"><spring:message code="part.primaryMemberDesignation" text="Primary Member Designation"/></label>
		<select id="primaryMemberDesignation" name="primaryMemberDesignation" class="sSelect">
			<c:forEach items="${designations}" var="i" >
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		<form:errors path="primaryMemberDesignation"/>
	</p>
	<p class="minister">
		<label class="small"><spring:message code="part.primaryMemberMinistry" text="Primary Member Ministry"/></label>
		<select id="primaryMemberMinistry" name="primaryMemberMinistry" class="sSelect">
			<c:forEach items="${ministries}" var="i">
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		<form:errors path="primaryMemberMinistry"/>
	</p>
	
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberName" text="In Place of"/></label>
		<input type="text" name="formattedSubstituteMember" id="formattedSubstituteMember" class="autosuggest sText"/>
		<form:hidden path="substituteMember"/>
	</p>
	
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberDesignation" text="Designation"/></label>
		<select id="substituteMemberDesignation" name="substituteMemberDesignation" class="sSelect">
			<c:forEach items="${designations}" var="i">
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		
		<form:errors path="substituteMemberDesignation"/>
	</p>
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberMinistry" text="Ministry"/></label>
		<select id="substituteMemberMinistry" name="substituteMemberMinistry" class="sSelect">
			<c:forEach items="${ministries}" var="i">
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		<form:errors path="substituteMemberMinistry"/>
	</p>
	<p class="public">
		<label class="small"><spring:message code="part.publicRepresentative" text="public Representative"/></label>
		<form:input path="publicRepresentative" cssClass="sText"/>
		<form:errors path="publicRepresentative"></form:errors>
	</p>
	<p class="public">
		<label class="wysiwyglabel"><spring:message code="part.publicRepresentativeDetail" text="Details"/></label>
		<form:textarea cssClass="wysiwyg" path="publicRepresentativeDetail"/>
		<form:errors path="publicRepresentativeDetail"></form:errors>
	</p>
	<p class="deviceType">
		<label class="small"><spring:message code="part.deviceType" text="Device Type"/></label>
		<select id="deviceType" name="deviceType" class="sSelect">
			<c:forEach items="${deviceTypes}" var="i">
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		<form:errors path="deviceType"></form:errors>
	</p>
	<p class="deviceNo deviceType">
		<label class="small"><spring:message code="part.deviceNo" text="Device No"/></label>
		<input type="text" name="deviceNo" id="deviceNo" class="sInteger"/>
	</p>
	<p class="starredQuestion">
		<label class="small"><spring:message code="part.starredQuestionNo" text=" OR Starred Question No"/></label>
		<input type="text" name="starredQuestionNo" id="starredQuestionNo" class="sInteger"/>
	</p>
	<p class="halfHourDiscussionFromQuestion">
		<label class="small"><spring:message code="part.DeviceNo" text="Device No"/></label>
		<select id="halfHourDiscussionFromQuestionNo" name="halfHourDiscussionFromQuestionNo" class="sSelect"></select>
	</p>
	<p>
		<label class="small"><spring:message code="part.isInterrupted" text="Is Interrupted"/></label>
		<form:checkbox path="isInterrupted" cssClass="sCheck"/>
	</p>
	<p id="mainHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.mainHeading" text="Main Heading"/></label>
		<form:textarea path="mainHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetMainHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
	</p>
	<p id="pageHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.pageHeading" text="Page Heading"/></label>
		<form:textarea path="pageHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetPageHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="part.proceedingContent" text="Content"/></label>
		<form:textarea path="proceedingContent" cssClass="wysiwyg"/>
	</p>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="reporter" value="${reporter}"/>
	<form:hidden path="EntryDate"/>
	<form:hidden path="chairPerson"/>
	<form:hidden path="RevisedContent"/>
	<form:hidden path="orderNo" value="${orderNo}"/>
	<form:hidden path="proceeding" value="${proceeding}"/>
	<%-- <input type="hidden" id="proceeding" name="proceeding" value="${proceeding}"/> --%>
	<input type="hidden" id="deviceId" name="deviceId"/>
	<input type="hidden" id="currentSlot" name="currentSlot" value="${currentSlot}"/>
	<input type="hidden" id="currentSlotName" name="currentSlotName" value="${slotName}"/>
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>	
	<div style="border: 2px solid black; position:absolute; z-index: 2000; display:none;" id="shiftDiv">
		<select size="5" id="autosuggest_menu">
		</select> 
	</div>
	<div id="interruptProceedingDiv" style="height:auto;width:400px; display:none; border:1px solid black; padding:5px; border-radius:5px; box-shadow:2px 2px 5px; position:absolute;"> 
		<select id="iProceeding" style="width:400px;">
		</select>
	</div>	
		
	
</form:form>
<input type="hidden" id="session" value="${session}"/>
<ul id="contextMenuItems" >
	<li><a href="#viewBookmarkDetails" class="edit"><spring:message code="proceeding.viewBookmarkDetails" text="viewDetails"></spring:message></a></li>
</ul>
<!-- <ul id="iProceeding">
	<li></li>
</ul> -->
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input type="hidden" id="lastPart" name="lastPart" value="${lastPartId}"/>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id="mainHeadingContent" name="mainHeadingContent" value="${mainHeading}">
<input type="hidden" id="pageHeadingContent" name="pageHeadingContent" value="${pageHeading}">
</div>

</body>
</html>