<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="proceeding.bookmark" text="Bookmark" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
	var contentKey="";
	var isBookmarkPart=false;
	var isBookmarkCtrl = false;
	var isBookmarkShift = false;
	var divCount='';
	$(document).ready(function(){	
		tinymce.remove();
		 var maxPageCount = 2272;
		 var enterCount = 22;
		 var pageCount =  maxPageCount;
		tinyMCE.init({
	    	  selector: 'div#bkproceedingReportDiv',
	    	  elements : "bkproceedingReportDiv",
	    	  height: 590,
	    	  width:840,
	    	  force_br_newlines : true,
	    	  force_p_newlines : false,
	    	  forced_root_block : "div",
	    	  inline : true,
	    	  nonbreaking_force_tab: true,
	    	  entity_encoding : "raw",
	    	  plugins: [
	    	    'advlist autolink lists link image charmap print preview hr anchor pagebreak',
	    	    'searchreplace wordcount visualblocks visualchars code fullscreen',
	    	    'insertdatetime media nonbreaking  table contextmenu directionality',
	    	    'emoticons template paste textcolor colorpicker textpattern imagetools lineheight'
	    	  ],
	    	  toolbar1: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image |fontselect fontsizeselect| lineheightselect',
	    	  toolbar2: 'print preview media | forecolor backcolor emoticons|fullscreen',
	    	  image_advtab: true,
	    	  templates:"ref/proceedingCitation",
	    	  fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
	    	  lineheight_formats: "100% 120% 150% 180% 200%",
	    	  font_formats: 'Kokila=kokila,Arial=arial,helvetica,sans-serif;Courier New=courier new,courier,monospace;AkrutiKndPadmini=Akpdmi-n',
	    	  pagebreak_split_block: true,
	    	  setup : function(bkproceedingReportDiv)
	    	  {
	    		  bkproceedingReportDiv.on('init',function(){
	    			  style_formats : [
	    			                   {title : 'Line height 20px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '20px'}},
	    			                   {title : 'Line height 30px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '30px'}}
	    			           ]
	    		  });
	    		  
	    		// On key up   
		    		 bkproceedingReportDiv.on('keyup', function(e) 
		    	    {
		    			 top.tinymce.activeEditor.notificationManager.close();
		    			 var enteredText = tinyMCE.activeEditor.getContent({format : 'text'});
		    			 numberOfLineBreaks = (enteredText.match(/\n/g)||[]).length;
		    			 characterCount = enteredText.length + numberOfLineBreaks;
		    			
		    			 if(characterCount >= pageCount){
		    				proceedingReportDiv.execCommand('mcePageBreak',true,this,this);
		    				//tinyMCE.activeEditor.execCommand('mceInsertContent', false, "<div class='pageBreakDiv' style='page-break-before: always; width: 100%; border: 1px dotted; font-size: 20px; height: 20px; text-align: center; background-color: mediumturquoise;'>Page Break</div><br>");
		    				$(".pageBreakDiv").css("display","block");
		    				pageCount = pageCount + maxPageCount;
		    			 }
		    			
						 var keyCode = e.keyCode || e.which; 
						 /* if (keyCode == 9) { 
							e.preventDefault(); 
							tinyMCE.activeEditor.execCommand('mceInsertContent', false, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						} */
					    	
				    	if (e.ctrlKey  || e.metaKey) {
				    	    if(e.ctrlKey && keyCode == 13){
					        	tinyMCE.activeEditor.execCommand('mcePageBreak',true,this,this);
					        	//tinyMCE.activeEditor.execCommand('mceInsertContent', false, "<div class='pageBreakDiv' style='page-break-before: always; width: 100%; border: 1px dotted; font-size: 20px; height: 20px; text-align: center; background-color: mediumturquoise;'>Page Break</div><br>");
					        	//tinyMCE.activeEditor.execCommand('mceInsertContent', false,$(".pageBreakDiv").clone() + "<br>");
					        	$(".pageBreakDiv").css("display","block");
					        	pageCount = pageCount + maxPageCount;
					        }
					    }
				    	
				    	if(e.which == 17) {
							isBookmarkCtrl = false; 
						}
						if(e.which == 16) {
							isBookmarkShift = false; 
						}
					});
		    		   
			    	// action on key down
			    	 bkproceedingReportDiv.on('keydown',function(e) {
			    		if(e.which == 17) {
			    			isBookmarkCtrl = true;
			    		}
			    		if(e.which == 16) {
			    			isBookmarkShift = true;
			    		}
			    		if(e.which == 69 && isBookmarkCtrl && isBookmarkShift) { 
			    			tinyMCE.activeEditor.execCommand('JustifyCenter',true,this,this);
							isBookmarkCtrl = false; 
							isBookmarkShift = false; 
						}else if(e.which == 89 && isBookmarkCtrl && isBookmarkShift){
							tinyMCE.activeEditor.execCommand('JustifyLeft',true,this,this);
							isBookmarkCtrl = false; 
							isBookmarkShift = false; 
						}else if(e.which == 79 && isBookmarkCtrl && isBookmarkShift){
							tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
							isBookmarkCtrl = false; 
							isBookmarkShift = false; 
						}else if(e.which == 88 && isBookmarkCtrl && isBookmarkShift){
							tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
							isBookmarkCtrl = false; 
							isBookmarkShift = false; 
						}else if(e.which == 85 && isBookmarkCtrl && isBookmarkShift){
							updatePart()
						}else if(e.which == 112){
							var textContent = tinyMCE.activeEditor.getContent({format : 'text'});
							var pos = textContent.lastIndexOf(" ");
							var length = textContent.length;
							var res = textContent.substring(pos,length-1);
							var mainContent=$("#autoFill option[value='"+res.trim()+"']").text();
							if(mainContent != '' && mainContent != null){
								var formattedContent  = tinyMCE.activeEditor.getContent();
								formattedContent = formattedContent.replace(res," " + mainContent);
								tinyMCE.activeEditor.setContent(formattedContent);
								tinyMCE.activeEditor.focus();
							}
						}else if(e.which ==80 && isBookmarkCtrl){
							console.log("printing....");
							$(".headerTable").css("display","block");
						}
			    	}); 
		     	 
	     	  }   
 		
 		});
		
		var text="";
		var key="";
		var count=$('#count').val();
		
		if(count!=null && count!=""){
			$('#currentPart').val($('#partId'+count).val());
		}else{
			$('#currentPart').val($('#id').val());
		}
		
		
		$("#bookmark").click(function(){
			var slotArray=$('#slots').val();
			var language=$('#language').val().split("");
			var size=parseInt($('#bookmarkSize').val())+1;
			var currentSlotName=$('#currentSlotName').val();
			key=key+"0XINS-"+language[0].toUpperCase()+"~";
			var l="";
			for(var i=0;i<slotArray.length;i++){
				var j=slotArray[i];
				var k=j.split('##');
				l=l+k[1].toUpperCase();
				if(i+1<slotArray.length){
					l=l+"/";
				}
			}
			if(count!=null && count!=""){
				key=key+l+"_"+currentSlotName+$('#order'+count).val()+size;
				text=$('#content'+count).wysiwyg("getContent");
				$('#content'+count).wysiwyg("setContent",text+"<p>"+key+"</p>");
			}else{
				key=key+l+"_"+currentSlotName+$('#orderNo').val()+size;
				text=$('#proceedingContent').wysiwyg("getContent");
				$('#proceedingContent').wysiwyg("setContent",text+"<p>"+key+"</p>");
			}
			
			var param="&language="+$('#language').val()+
					  "&currentSlot="+$('#currentSlot').val()+
					  "&bookmarkKey="+key+
					  "&masterPart="+$('#currentPart').val();
			$.post('proceeding/part/bookmark?'+param,function(data){
			});
			$.fancybox.close();
			
		});
		
		$('#language').change(function(){
			$.get('ref/slot?language='+$(this).val()+"&currentSlot="+$("#currentSlot").val(),function(data){
				$('#slots').empty();
				var slotText="<option value=''>"+$('#pleaseSelectMessage').val()+"</option>";
				var i;
				if(data.length>0){
				for(i=0;i<data.length;i++){
					slotText+="<option value='"+data[i].id+'##'+data[i].name+"'>"+data[i].name+"("+data[i].value+"-"+data[i].type+")"+"["+ data[i].displayName+"]"+"</option>";
				}
				$("#slots").html(slotText);			
				}else{
					$("#slots").empty();
							
				}
			}).done(function(){
				loadText();
			});
			
			
		});
		
		$('#slots').change(function(){
			loadText();
		});	
		$('#language').change();
		
	});	
	
	function loadText(){
		var size=parseInt($('#bookmarkSize').val());
		var param="";
		$('#slots option').each(function(){
			if($(this).attr('selected')){
				var idval=$(this).val().split('##');
				param=param+idval[0];
				$('#masterSlot').val(param);
			}
		});
		$.get('ref/bookmarktext?slot='+param,function(data){
			$('#previousContent').val(data[0].name);
			$("#masterPart").val(data[0].id);
			$("#bkproceedingReportDiv").html(data[0].name);
		}).done(function(){
			$.get("ref/proceedingHeader?partId="+ $("#masterPart").val(),function(data){
					$("#currentSlotStartDate").html(data.value);
					$("#slotName").html(data.name);
					$("#languageReporter").html(data.displayName);
					$("#previousReporter").html(data.formattedOrder);
					$("#currenSlotStartTime").html(data.type);
				
			});
		});
	}
	
	function updatePart(){
		$("#replacedContent").val(tinyMCE.activeEditor.getContent());
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.post($("form[action='proceeding/part/bookmark']").attr('action'),
				$("form[action='proceeding/part/bookmark']").serialize(),function(data){
			 if(data!=null && data!=''){		
				/* 
				 $("#partId"+$("#partCount").val()).val(data.id); */
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
	
	</script>
	<style type="text/css" >
	
			
		#replacingcontent{
			width: 21cm; 
		}
		 #bkproceedingReportDiv{
		   background: #F2F0F2;
		   font-size: 18pt;
		   font-family: Kokila;
		   line-height: 150%; 
		   text-align: justify;
		   margin-left: 150px; 
		   width: 500px;
		   
		} 
				
		#bkproceedingReportDiv > p{
		   background: #F2F0F2;
		   font-size: 18pt;
		   font-family: Kokila;
		   line-height: 150%; 
	    } 
			
			
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
		    div#replacingcontent{
		    	visibility :hidden;
		    	
		    }
		    
		     div#bookmarkFilterDiv{
		    	visibility :hidden;
		    	display : none;
		    	
		    }
		    
		    
		    div#bkproceedingReportDiv .page-break { page-break-after:auto;}
			div#bkproceedingReportDiv .page-break-before-forced { page-break-before: always;}
			div#bkproceedingReportDiv{
				visibility: visible !important;
				background: #F2F0F2;
				font-size: 16pt;
				font-family: Kokila;
				line-height: 150%; 
				text-align: justify;
				/* top: 1.905cm;  
			 	bottom :1.905cm; */
			 	margin-left:3.302cm;
			 	margin-right:3.302cm;
			 	margin-bottom: 1.905cm;
			}
			
			 div#replacingcontent .headerTable{
				display:block;
				visibility: visible !important;
			}
			
			div#replacingcontent #tableHeader{
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
			
			ul.tabs {
		     display: none !important;
		     visibility:hidden!important;
		   }
		   
		   div.pageBreakDiv{
				visibility: hidden;
				page-break-before: always;
				
			} 
		   #slotName:after {
		    counter-increment: page;
		    content: counter(page);
		 	}
		 	
	}
	</style>
</head>
<body>
	<div class="fields clearfix watermark">	
	<form:form action="proceeding/part/bookmark" method="POST">
	<%@ include file="/common/info.jsp" %>
	<div id="bookmarkFilterDiv">
		<p>
		<label class="small"><spring:message code="proceeding.language" text="Language"/>*</label>
		<select name="language" id="language" class="sSelect" >
			<c:forEach items="${languages}" var="i">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</select>
		</p>
		
		<p>
		<label class="small"><spring:message code="proceeding.slot" text="Slot"/>*</label>
		<select name="slots" id="slots"  class="sSelect" >
		</select>
		</p>
	</div>
	<div id="replacingcontent" style="text-align:justify;">
					<table id="tableHeader" style="font-size: 12px">
				<thead>
					<tr class="print">
						<th colspan="7">&nbsp;</th>
					</tr>
					<tr class="print">
						<th width="40px">&nbsp;</th>
						<th width="30px">&nbsp;</th>
						<th width="30px">&nbsp;</th>
						<th style="font-size: 12pt;text-align:left"><span id="currentSlotStartDate"/></th>
						<th style="font-size: 12pt;text-align:center;"><spring:message code='part.generalNotice' text='Un edited Copy'/></th>
						<th style="font-size: 12pt;text-align:right;"><span id="slotName"></span> </th>
						<th width="120px">&nbsp;</th>
					</tr>
					<tr  class="print">
						<th width="40px">&nbsp;</th>
						<th width="30px">&nbsp;</th>
						<th width="30px">&nbsp;</th>
						<th style="font-size: 12pt; text-align: left"><span id="languageReporter"/></th>
						<th style="font-size: 12pt; text-align: center;"><spring:message code='part.previousReporterMessage' text='Previous Reporter'/><span id="previousReporter"/></th>
						<th style="font-size: 12pt; text-align: right;"><span id="currenSlotStartTime"/></th>
						<th width="120px">&nbsp;</th>
					</tr>
					<br>
				</thead>
				 <tfoot>
				    <tr>
				      <td colspan="7">&nbsp;</td>
				    </tr>
				    <tr>
				      <td colspan="7">&nbsp;</td>
				    </tr>
				    <tr>
				      <td colspan="7">&nbsp;</td>
				    </tr>
				 </tfoot>
				<tbody>
					<tr>
						<td colspan="7">
							<div id='bkproceedingReportDiv' name='bkProcContent'/>
						</td>
					</tr>
				</tbody>
			</table>
		
	</div>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input type="button" id="bookmark" value="bookmark" class="butDef"/>
			<input type="button" id="addtext" value="add text" class="butDef" style="display:none;"/>
		</p>
	</div>
	<input type="hidden" name="previousContent" id="previousContent"/>
	<input type="hidden" name="replacedContent" id="replacedContent"/>
	<input type="hidden" name="currentSlot" id="currentSlot" value="${currentSlot}"/>
	<input type="hidden" name="masterSlot" id="masterSlot"/>
	<input type="hidden" name="proceeding" id="proceeding" value="${proceedingId}"/>
	<input type="hidden" name="currentPart" id="currentPart" value = "${currentPart}" />
	<input type="hidden" name = "masterPart" id="masterPart"/>
	<input type="hidden" name="isBookmarkPart" id="isBookmarkPart" value = "false" />
	<input type="hidden" name="count" id="count" value="${count}"/>
	<input type="hidden" name="bookmarkSize" id="bookmarkSize" value="${bookmarkSize}"/>
	
	</form:form>
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="textToBeReplacedMessage" name="textToBeReplacedMessage" value="<spring:message code='part.textToBeReplacedMessage' text='Text to Be replaced'></spring:message>" disabled="disabled"/>
	</div>
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<div id='bookmarkKey' style='display:none;'></div>
</body>
</html>