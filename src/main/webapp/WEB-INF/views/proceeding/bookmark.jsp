<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="proceeding.bookmark" text="Bookmark" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
	var contentKey="";
	$(document).ready(function(){	
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
			
			var param="previousText="+text+
					  "&language="+$('#language').val()+
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
				var slotText="";
				var i;
				if(data.length>0){
				for(i=0;i<data.length;i++){
					slotText+="<option value='"+data[i].id+'##'+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
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
		
		$('#addtext').click(function(){
			if(count!=null && count!=""){
				text1=$('#content'+count).wysiwyg("getContent");
				var iframe1= document.getElementById(contentKey+'-wysiwyg-iframe');
				var textToBeEntered=getIframeSelectionText(iframe1);
				text1=text1+textToBeEntered;
				$('#content'+count).wysiwyg("setContent",text1);
			}else{
				text1=$('#proceedingContent').wysiwyg("getContent");
				var iframe1= document.getElementById(contentKey+'-wysiwyg-iframe');
				var textToBeEntered=getIframeSelectionText(iframe1);
				text1=text1+textToBeEntered;
				$('#proceedingContent').wysiwyg("setContent",text1);
			}
			
			
			
			 //.replace(key,textToBeReplaced);
			console.log("replaced text"+text1);
			
			$.fancybox.close();
			
		});
		
	});	
	
	function loadText(){
		var param="";
		$('#slots option').each(function(){
			if($(this).attr('selected')){
				var idval=$(this).val().split('##');
				param=param+idval[0]+",";
			}
		});
		$.get('ref/bookmarktext?slot='+param,function(data){
			console.log(data);
			var elementText="";
			
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					elementText=elementText+"<p>"+
					"<label class='wysiwyglabel'>"+$('#textToBeReplacedMessage').val()+"of Slot "+data[i].value+"</label>"+
					"<textarea id='textToBeReplaced"+i+"' name='textToBeReplaced"+i+"' class='wysiwyg'>"+data[i].name+"</textarea>"+
					"</p>";
				}
				$('#replacingcontent').html(elementText);
				$('#replacingcontent').show();
				$('#addtext').show();
			
			$('.wysiwyg').wysiwyg({
			    	 
		    	  	  controls:{
							fullscreen: {
								/*groupIndex: 12,*/
								visible: true,
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
				$('.wysiwyg').change(function(){
				 contentKey=this.id;
				 console.log(contentKey);
				});
				
				}
			});
			
			
			
			
	}
	
	function getIframeSelectionText(iframe) {
		  var win = iframe.contentWindow;
		 // var doc = iframe.contentDocument || win.document;

		  if (win.getSelection) {
		    return win.getSelection().toString();
		  } /* else if (doc.selection && doc.selection.createRange) {
		    return doc.selection.createRange().text;
		  } */
		}
	</script>
</head>
<body>
	<div class="fields clearfix watermark">	
	<form:form action="proceedings/bookmark" method="POST">
	<%@ include file="/common/info.jsp" %>
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
	<select name="slots" id="slots" multiple="multiple" class="sSelectMultiple" size="5">
	</select>
	</p>
	<div id="replacingcontent" style="display: none;"></div>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input type="button" id="bookmark" value="bookmark" class="butDef"/>
			<input type="button" id="addtext" value="add text" class="butDef" style="display:none;"/>
		</p>
	</div>
	<input type="hidden" name="currentSlot" id="currentSlot" value="${currentSlot}"/>
	<input type="hidden" name="proceeding" id="proceeding" value="${proceedingId}"/>
	<input type="hidden" name="currentPart" id="currentPart" />
	<input type="hidden" name="count" id="count" value="${count}"/>
	<input type="hidden" name="bookmarkSize" id="bookmarkSize" value="${bookmarkSize}"/>
	</form:form>
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="textToBeReplacedMessage" name="textToBeReplacedMessage" value="<spring:message code='part.textToBeReplacedMessage' text='Text to Be replaced'></spring:message>" disabled="disabled"/>
	</div>
</body>
</html>