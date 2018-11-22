<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$(document).ready(function(){

		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('proceeding/revisions/'+$("#partId1").val(),function(data){
			    $.fancybox.open(data,{autoSize: false, width: 900, height:700});
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });
		
		/* var footerText = "<div style='font-size:18pt;text-align:right'>"+$("#nextReporterTitle").val()+"</div>";
		$("#proceedingReportDiv").attr("title",footerText); */
		//As tinymce once registered doesnot get reinitialize when the same page is loaded, hence removing the previous tinymce instance
		tinymce.remove();
	 	var isCtrl = false;
		var isShift = false;
		var pageCounter = parseInt("1");
		/****Disable F5 button****/
		document.onkeydown = function(e){
		  //keycode for F5 function
			if(e.keyCode===116){
				return false;
			}
		    
			if (e.ctrlKey || e.metaKey) {
		        switch (String.fromCharCode(e.which).toLowerCase()) {
		        case 's':
		            e.preventDefault();
		            break;
		        case 'r':
		        	e.preventDefault();
		        	break;
		        case 'e':
		        	e.preventDefault();
		        	break;
		        }
		        	
		    }
		};
		
		$('#viewReport').click(function(){
			var params="proceeding="+$('#partProceeding1').val()+
			"&language="+$('#selectedLanguage').val()+
			"&outputFormat=WORD"
			$(this).attr('href','proceeding/proceedingwisereport?'+params);
		});
		
		$('.deviceNo').change(function(){
			var id=this.id;
			var mainId=id.split("deviceNo")[1];
			$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),function(data){
				if(data!=null){
					$('#dContent').html(data);
					tinyMCE.activeEditor.execCommand('mceInsertContent', false, $('#deviceContent').html());
				}
			}).fail(function(){
				
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		});
		
		
		
		  $( ".formattedMember").autocomplete({
				minLength:3,			
				source:'ref/member/getmembers?session='+$("#session").val(),
				select:function(event,ui){	
					id=this.id;
					var elementCount=id.split("formattedPrimaryMember")[1];
					$("#primaryMember"+elementCount).val(ui.item.id);
					/** Setting the party of the Selected member **/
					$.get('ref/findParty?memberId='+ui.item.id,function(data){
						if(data!=null){
							$('#party option').each(function(){
								if($(this).val()==data){
									 $(this).attr('selected',true);
								 } 
							});
						}
					});
				}	
		  });
		  
		  /***Selecting Member By Party***/
		  $('#party').change(function(){
			 	$.get('proceeding/part/getMemberByPartyPage?partyId='+$(this).val()+'&partCount='+partCount+'&housetype='+$("#selectedHouseType").val(),function(data){
		 			$.fancybox.open(data,{autoSize: false,width:800,height:500});
				},'html'); 
		     return false;
		  });
		  
		  /**** Adding Bookmark ****/
		  $('.addBookmark').click(function(){
				var id=this.id;
				var count=id.split("bookmark");
				elementCount=count[count.length-1];
				showTabByIdAndUrl('bookmarks_tab', 'proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count=1&currentPart='+$('#partId1').val());
		});
		  
		  /**** TinyMCE related Events ****/
		 $("#partCount").val(1);
		 var partCount = $("#partCount").val(); 
		 var pCount = 22;
		 if(countLines()>2){
			 pCount = countLines() + 20;
			 
		 }
		 tinyMCE.init({
			    	  selector: 'div#proceedingReportDiv',
			    	  elements : "proceedingReportDiv",
			    	  height: 590,
			    	  width:840,
			    	  force_br_newlines : true,
			    	  force_p_newlines : false,
			    	  forced_root_block : 'div',
			    	  inline : true,
			    	  nonbreaking_force_tab: true,
			    	  entity_encoding : "raw",
			    	  plugins: [
			    	    'print',
			    	    'searchreplace wordcount fullscreen noneditable' ,
			    	    'nonbreaking  table pagebreak',
			    	    'template paste textpattern'
			    	  ],
			    	  noneditable_noneditable_class: "nonEditable",
			    	  toolbar1: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist ',
			    	  toolbar2: 'print|fullscreen',
			    	  image_advtab: true,
			    	  templates:"ref/proceedingCitation",
			    	  fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
			    	  lineheight_formats: "100% 120% 150% 180% 200%",
			    	  font_formats: 'Kokila=kokila,Arial=arial,helvetica,sans-serif;Courier New=courier new,courier,monospace;AkrutiKndPadmini=Akpdmi-n',
			    	  setup : function(proceedingReportDiv)
			    	  {
			    		  proceedingReportDiv.on('init',function(){
			    			  style_formats : [
			    			                   {title : 'Line height 20px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '20px'}},
			    			                   {title : 'Line height 30px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '30px'}}
			    			           ]
			    		  		
			    		  })
			    		 // On key up   
			    		 proceedingReportDiv.on('keyup', function(e) 
			    	    {	
			    			 $("#partContent"+$("#partCount").val()).val(tinyMCE.activeEditor.getContent());
			    			 var element = document.getElementById("proceedingReportDiv");
			    			 var offset = getCaretCharacterOffsetWithin(element);//$(this).offset();
			    			 console.log(offset);
			    			 /* console.log("left" + $(this).offset().left);
			    			 console.log("top" + $(this).offset().top); */
			    			 var lineCounter = countLines();
			    			 var lineCount = 0;
			    			 if(lineCounter > pCount){
			    				 	lineCount = lineCounter -1;
				    				pageCounter = parseInt(pageCounter) + 1;
				    				$("#pageCounter").val(pageCounter);
				    				var headerText = "<table class='headerTable nonEditable'>"
			    						+"<tbody>"
			    						+"<tr>"
			    							+"<td style='font-size: 18pt;text-align:left;'>"+$("#currentSlotStartDateTitle").val()+"</td>"
			    							+"<td style='font-size: 18pt;text-align:center;' width='800px'><spring:message code='part.generalNotice' text='Un edited Copy'/></td>"
			    							+"<td class='slotTD' style='font-size: 18pt;text-align:right;' width='200px'>"+$("#slotNameTitle").val()+" - "+$("#pageCounter").val()+"</td>"
			    						+"</tr>"
			    						+"<tr>"
			    							+"<td style='font-size: 12pt;text-align:left;'>"+$("#languageReporterTitle").val()+"</td>";
			    							if($("#previousReporterTitle").val()!='' && $("#previousReporterTitle").val()!= null){
			    								headerText = headerText + "<td style='font-size: 18pt;text-align:center;'><spring:message code='part.previousReporterMessage' text='Previous Reporter'/> "+$("#previousReporterTitle").val()+"</td>"; 
			    							}else{
			    								headerText = headerText + "<td style='font-size: 18pt;text-align:center;'></td>";
			    							}
			    							
			    							headerText = headerText +"<td style='font-size: 18pt;text-align:right;'>"+$("#currenSlotStartTimeTitle").val()+"</td>"
			    						+"</tr>"
			    					+"</thead>"
			    					+"</table>";
				    				tinyMCE.activeEditor.execCommand('mceInsertContent', false, "<div class='pageBreakDiv nonEditable' style='page-break-before: always; width: 100%; border: 1px dotted; font-size: 20px; height: 20px; text-align: center; background-color: mediumturquoise;'>Page Break</div>"+ headerText+"<span>&nbsp;</span>");
				    				$(".pageBreakDiv").css("display","block");
				    				var slotTDCounter = 1;
						        	$(".slotTD").each(function(){
						        		$(this).html($("#slotNameTitle").val() + "-" +slotTDCounter);
						        		slotTDCounter = parseInt(slotTDCounter) + 1;
						        	}); 
						        	pCount = lineCount + pCount;
			    			 }
			    			 top.tinymce.activeEditor.notificationManager.close();
			    			 var keyCode = e.keyCode || e.which; 
												    	
					    	if (e.ctrlKey  || e.metaKey) {
					    	    if(e.ctrlKey && keyCode == 13){
					    	    	lineCount = lineCounter -1;
					    	    	pageCounter = parseInt(pageCounter) + 1;
			    					$("#pageCounter").val(pageCounter);
			    					var headerText = "<table class='headerTable nonEditable'>"
			    						+"<tbody>"
			    						+"<tr>"
			    							+"<td style='font-size: 18pt;text-align:left;'>"+$("#currentSlotStartDateTitle").val()+"</td>"
			    							+"<td style='font-size: 18pt;text-align:center;' width='800px'><spring:message code='part.generalNotice' text='Un edited Copy'/></td>"
			    							+"<td class='slotTD' style='font-size: 18pt;text-align:right;' width='200px'>"+$("#slotNameTitle").val()+"-"+$("#pageCounter").val()+"</td>"
			    						+"</tr>"
			    						+"<tr>"
			    							+"<td style='font-size: 18pt;text-align:left;'>"+$("#languageReporterTitle").val()+"</td>";
			    							if($("#previousReporterTitle").val()!='' && $("#previousReporterTitle").val()!= null){
			    								headerText = headerText + "<td style='font-size: 18pt;text-align:center;'><spring:message code='part.previousReporterMessage' text='Previous Reporter'/> "+$("#previousReporterTitle").val()+"</td>"; 
			    							}else{
			    								headerText = headerText + "<td style='font-size: 18pt;text-align:center;'></td>";
			    							}
			    							
			    							headerText = headerText +"<td style='font-size: 18pt;text-align:right;'>"+$("#currenSlotStartTimeTitle").val()+"</td>"
			    						+"</tr>"
			    					+"</thead>"
			    					+"</table>";
						        	tinyMCE.activeEditor.execCommand('mceInsertContent', false, "<div id='pageBreakDiv' class='pageBreakDiv nonEditable' style='page-break-before: always; width: 100%; border: 1px dotted; font-size: 20px; height: 20px; text-align: center; background-color: mediumturquoise;'>Page Break</div>"+headerText+"<span>&nbsp;</span>");
						        	var slotTDCounter = 1;
						        	$(".slotTD").each(function(){
						        		$(this).html($("#slotNameTitle").val() + "-" +slotTDCounter);
						        		slotTDCounter = parseInt(slotTDCounter) + 1;
						        	});
						        	pCount = lineCount + pCount;
						        }
						    }
					    	
					    	if(e.which == 17) {
								isCtrl = false; 
							}
							if(e.which == 16) {
								isShift = false; 
							}
						});
			    		   
				    	// action on key down
				    	 proceedingReportDiv.on('keydown',function(e) {
				    		if(e.which == 17) {
				    			isCtrl = true;
				    		}
				    		if(e.which == 16) {
				    			isShift = true;
				    		}
				    		if(e.which == 69 && isCtrl && isShift) { 
				    			tinyMCE.activeEditor.execCommand('JustifyCenter',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 89 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyLeft',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 79 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 88 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 85 && isCtrl && isShift){
								updatePart()
							}else if(e.which == 112){
								var element = document.getElementById("proceedingReportDiv");
								var word = getWordPrecedingCaret(element);
								var mainContent=$("#"+word.trim()).html();
								if(mainContent != '' && mainContent != null){
									tinyMCE.activeEditor.selection.select(tinymce.activeEditor.selection.getNode());
									var formattedContent  = tinymce.activeEditor.selection.getContent();
									formattedContent = formattedContent.replace(word," " + mainContent);
									tinyMCE.activeEditor.selection.setContent(formattedContent);
									tinyMCE.activeEditor.focus();
								} 
							}
				    	}); 
			     	  }   
		 		
		 });

	});
	
	
		
	function updatePart(){
			var toBeReplacedContent =  tinymce.activeEditor.getContent();
			toBeReplacedContent = toBeReplacedContent.replace(/<p/gi,"<div");
			toBeReplacedContent = toBeReplacedContent.replace(/<\/p/gi,"</div");
			tinymce.activeEditor.setContent(toBeReplacedContent);
			$("#partContent"+$("#partCount").val()).val(toBeReplacedContent);
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.post($("form[action='proceeding/part/save']").attr('action'),
					$("form[action='proceeding/part/save']").serialize(),function(data){
				 if(data!=null && data!=''){		
					
					 $("#partId"+$("#partCount").val()).val(data.id);
					 tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(), true);
					 tinyMCE.activeEditor.selection.collapse(false);
					 $.unblockUI();
					 
				} 
			}).fail(function(){
				
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});

	}
	
	function getCaretCharacterOffsetWithin(element) {
	    var caretOffset = 0;
	    if (typeof window.getSelection != "undefined") {
	        var range = window.getSelection().getRangeAt(0);
	        var preCaretRange = range.cloneRange();
	        preCaretRange.selectNodeContents(element);
	        preCaretRange.setEnd(range.endContainer, range.endOffset);
	        caretOffset = preCaretRange.toString().replace(/\r(?!\n)|\n(?!\r)/g, "\r\n").replace(/<[img>]*>/ig, ' ')
	        .replace(/<\/[img>]*>/ig, ' ').length;
	    } else if (typeof document.selection != "undefined" && document.selection.type != "Control") {
	        var textRange = document.selection.createRange();
	        var preCaretTextRange = document.body.createTextRange();
	        preCaretTextRange.moveToElementText(element);
	        preCaretTextRange.setEndPoint("EndToEnd", textRange);
	        caretOffset = preCaretTextRange.text.replace(/\r(?!\n)|\n(?!\r)/g, "\r\n").replace(/<[img>]*>/ig, ' ')
	        .replace(/<\/[img>]*>/ig, ' ').length;
	    }
	    return caretOffset;
	}
	
	function getWordPrecedingCaret(containerEl) {
	    var word = "", sel, range, precedingRange;
	    if (window.getSelection) {
	        sel = window.getSelection();
	        if (sel.rangeCount > 0) {
	            range = sel.getRangeAt(0).cloneRange();
	            range.collapse(true);
	            range.setStart(containerEl, 0);
	            var words = range.toString().split(" ");
	            word =  words[words.length - 1];
	         }
	    } else if ( (sel = document.selection) && sel.type != "Control") {
	        range = sel.createRange();
	        precedingRange = range.duplicate();
	        precedingRange.moveToElementText(containerEl);
	        precedingRange.setEndPoint("EndToStart", range);
	        var words = precedingRange.text.split(" ");
            word =  words[words.length - 1];
	    }
	    return word;
	}
	
	function countLines() {
		  var divHeight = $("#proceedingReportDiv").height();
		  var lineHeight = $("#proceedingReportDiv").css('line-height');
		  lineHeight = parseFloat(lineHeight);
		  var lineCounter = divHeight / lineHeight;
		  return lineCounter;
		}
	</script>
	<style type="text/css" >
	
			
		#outerDiv{
			width: 21cm; 
		}
		 #proceedingReportDiv{
		   background: #F2F0F2;
		   font-size: 18pt;
		   font-family: Kokila;
		   line-height: 150%; 
		   text-align: justify;
		   margin-left: 150px; 
		   width: 500px;
		   max-width:500px;
		   /* position: absolute; */
		   
		} 
				
		#proceedingReportDiv > p{
		   background: #F2F0F2;
		   font-size: 18pt;
		   font-family: Kokila;
		   line-height: 150%; 
	    } 
	    
	    /* .headerTable{
	    	display:none;
	    } */
			
			
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
		
		 div#proceedingReportDiv .pageBreakDiv {
				page-break-before: always !important;}
		
		 .print {
				display:none;
			}
		@media print{
         
		  	div#pannelDash {
		    	visibility: hidden;
		    	display:none;
		    }
		    div#page{
		    	visibility: hidden;
		    }
		    div#container {
		    	visibility: hidden;
		    }
		    div#selectionDiv1{
		    	visibility:hidden;
		    	display:none;
		    }
		    div.menu{
		    	visibility:hidden;
		    	display:none;
		    }
		    div#dummyContent{
		    	visibility:hidden;
		    	display:none;
		    }
		    div#menuOption {
		    	visibility: hidden;
		    	display:none
		    }
		    div#pageHeader{
		    	visibility : hidden;
		    	display:none
		    } 
		    div#outerDiv{
		    	visibility :hidden;
		    	
		    }
		    
		    
		    div#proceedingReportDiv .pageBreakDiv { 
				page-break-before: always;
				background-color: red;}
			div#proceedingReportDiv .page-break-before-forced { page-break-before: always !important;}
			div#proceedingReportDiv{
				visibility: visible !important;
				background: #F2F0F2;
				font-size: 18pt;
				font-family: Kokila;
				line-height: 150%; 
				text-align: justify;
				/* top: 1.905cm;  
			 	bottom :1.905cm; */
			 	margin-left:3.302cm;
			 	margin-right:3.302cm;
			 	margin-bottom: 1.00cm;
			 	width: 500px;
		   		max-width:500px;
			 	float: none !important; 
			 	position: relative !important; 
			 	overflow:hidden !important;
			}
			
			 div#proceedingReportDiv .headerTable{
				display:block;
				visibility: visible !important;
			}
			
			div#outerDiv #tableHeader{
				display:block;
				visibility:visible
			}
			
			div#bk{
				visibility:hidden;
			}
			
			div#clearfix{
			visibility:hidden;
			}
			
			div#tabContent{
				visibility: hidden;
			}
			
			.print {
				display: table-row;
			} 
			
			.pageBreakDiv{
				visibility: hidden;
				page-break-before: always !important;
				
			} 
			ul.tabs {
		     display: none !important;
		     visibility:hidden!important;
		   }
		   
		   .slotName:after {
		    counter-increment: page;
		    content: counter(page);
		 }
		 
		 .headerTable{
	    	display:table;
	    	border:none !important;
	    }
		 
		 div#nextSlotDiv{
		 	display:none;
		 	visibility: hidden;
		 }
		 #proceedingReportDiv:after {
		    display: block;
		    content:attr(title);
		    text-align:right;
		    font-size:18pt;
		    /* margin-bottom: 594mm; */ /* must be larger than largest paper size you support */
		  }
	}
	
	#nextSlotDiv{
	    background: repeat-x scroll 0 0 #FFF;
	    box-shadow: 0 2px 5px #888888;
	    max-height: 260px;
	    right: 0;
	    position: fixed;
	    top: 30px;
	    width: 200px;
	    z-index: 10000;
	    overflow: auto;
	    border-radius: 10px;
	    font-size: 15pt;
	    background-color: #b0e0e6;
	    }
	</style>
</head>
<body>
	<!-- <div class="fields clearfix watermark"> -->
		<div id="pageHeader">
			<h2 style="display:inline;"><spring:message code="proceeding.edit.heading" text="Slot : "/>${slotName}	</h2>| <div style="display:inline;">(<spring:message code="proceeding.edit.heading.timing" text="Slot Time : "/>${slotStartTime} - ${slotEndTime})</div>
			<c:if test="${committeeMeeting != null and committeeMeeting != ''}">
				<spring:message code="proceeding.edit.heading.committeeName" text="Committee : "/>${committeeName}
				<br>
			</c:if>|
			<p style="display:inline;">
			<a id="viewReport" target="_blank" class="reportLinkl" >
				<spring:message code="proceeding.proceedingwiseReport" text="proceeding wise report"/>
			</a>|
			<a href='javascript:void(0)'  id='bookmark${partcount}' class=' addBookmark'><img src='./resources/images/star_full.jpg' title='Bookmark' class='imageLink'/></a>|
			<a id="viewRevision" target="_blank" class="reportLinkl" >
				<spring:message code="proceeding.viewRevision" text="ViewRevision"/>
			</a>|			
			</p>
		</div>
		<div id="menuOption" style="border: 2px solid blue;border-radius: 15px;width: 850px;">
			<p style="padding: 5px;">
				<label class='small'><spring:message code='part.memberName' text='Member'/></label>
				<input type='text' class='autosuggest formattedMember sText' name='formattedPrimaryMember' id='formattedPrimaryMember' style="margin-left: 80px;"/>
				<label class='small' style="margin-left: 50px;width:100px;">OR party</label>
				 <select id='party' class='sSelect' style='width:150px;margin-left: 10px'>
					<c:forEach items="${parties}" var="i">
					 	<option value="${i.id}">${i.name}</option>
					</c:forEach>
				</select>
			</p>
			<p>
	             <label class='small' style="margin-left: 10px;"><spring:message code='part.deviceType' text='Device Type'></spring:message></label>
	             <select name='deviceType' id='deviceType'class='sSelect' style='width:170px;margin-left: 35px;'>
		     		<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${deviceTypes}" var="i">
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:forEach>
				</select>
		     	  <label class='small' style='width:123px;margin-left: 65px;'><spring:message code='part.deviceNo' text='Device No'/></label>
				 <input type='text' name='deviceNo' id='deviceNo' class='deviceNo sInteger sText' style='width:168px;'/>
			</p>
		</div>

		<div id="outerDiv">
			<c:choose>
				<c:when test='${!(empty parts)}'>
					<c:forEach items='${parts}' var='outer'>
						<div id="proceedingReportDiv" name="procContent" title="<spring:message code='part.nextReporterMessage' text='Next'/>${nextReporter}">
							${outer.revisedContent}
						</div>
						
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div id="proceedingReportDiv" name="procContent" title="<spring:message code='part.nextReporterMessage' text='Next'/>${nextReporter}">
						<table class='headerTable nonEditable'>
								<tbody>
								<tr><td style='font-size: 18pt;text-align:left;'>${currentSlotStartDate}</td>
									<td style='font-size: 18pt;text-align:center;' width='800px'><spring:message code='part.generalNotice' text='Un edited Copy'/></td>
									<td class='slotTD' style='font-size: 18pt;text-align:right;' width='200px'><span class='slotName'>${slotName} - </span> </td>
								</tr>
								<tr>
									<td style='font-size: 18pt;text-align:left;'>${languageReporter}</td>
										<c:choose>
											<c:when test="${previousReporter != null && previousReporter!=''}">
												<td style='font-size: 18pt;text-align:center;'><spring:message code='part.previousReporterMessage' text='Previous Reporter'/> ${previousReporter}
											</c:when>
											<c:otherwise>
												<td style='font-size: 18pt;text-align:center;'><spring:message code='part.previousReporterMessage' text='Previous Reporter'/>
											</c:otherwise>
										</c:choose>
									</td>
									<td style='font-size: 18pt;text-align:right;'>${currenSlotStartTime}</td>
								</tr>
							</thead>
							</table> 
					</div>
				</c:otherwise>
			</c:choose>
			
		</div>
		<form action="proceeding/part/save" method="post">
			<c:choose>
				<c:when test='${!(empty parts)}'>
					<c:forEach items='${parts}' var='outer'>
						<textarea id="partContent1" name="partContent1" style="display:none;">${outer.revisedContent}</textarea>
						<input type='hidden' id="partId1" name="partId1" value="${outer.id}">
					</c:forEach>
				</c:when>
				<c:otherwise>
					<input type="hidden" id="partContent1" name="partContent1" />	
					<input type='hidden' id="partId1" name="partId1">
				</c:otherwise>
			</c:choose>
			  <input type="hidden" id="partCount" name="partCount" value="1"/>
			  <input type="hidden" id="partProceeding1" name="partProceeding1" value="${proceeding}"/>
			  <input type="hidden" id="editingUser" name="editingUser" value="${userName}">
			  <input type="hidden" id="partReporter1" name="partReporter1" value="${reporter}">
			  <input type="hidden" id="partOrder1" name="partOrder1" value="1">
		</form>
		<input type="hidden" id="session" name="session" value="${session}"/>
		<input type="hidden" id="slot" value="${slotId}" name="slot"/>
		<div id='dContent' style="display: none"></div>
		<div id="autoFill" style="display:none;">
			<c:forEach items="${proceedingAutofills}" var="i">
				<div id="${i.shortName}">${i.autoFillContent}</div>
			</c:forEach>
		</div>
		<div id="nextSlotDiv">
			<spring:message code="proceeding.nextSlot" text="Next Slot"/>
			<br>
			<table>
				<tbody>
					<c:forEach items="${nextSlots}" var="i">
						<tr>
							<td>${i.name}</td>
							<td>&nbsp;</td>
							<td>${i.type}</td>
							<td>&nbsp;</td>
							<td>${i.value}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<input type="hidden" id="currentSlotStartDateTitle" value="${currentSlotStartDate}"/>
		<input type="hidden" id="slotNameTitle" value="${slotName}"/>
		<input type="hidden" id="languageReporterTitle" value="${languageReporter}"/>
		<input type="hidden" id="currenSlotStartTimeTitle" value="${currentSlotStartTime}"/>
		<input type="hidden" id="previousReporterTitle" value="${previousReporter}"/>
		<input type="hidden" id="pageCounter" />
		<input type="hidden" id="nextReporterTitle" value="${nextReporter}"/>
		<input type="hidden" id="listNavigationConfirmationMessage" value="<spring:message code='part.navigateToListMessage' text='Please Save before going on to other page, If already saved press Ok, if not press CANCEL and Save the Content using CTRL + SHIFT + U'/>"/>
</body>
</html>