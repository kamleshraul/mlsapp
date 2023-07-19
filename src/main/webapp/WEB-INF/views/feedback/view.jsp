<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<style>
		.rating input {
			display: none;
		}
		.rating {
			display: flex;
	    }
		.rating > label {
			display: block;
			width: 50px;
			height: 50px;
			font-size: 50px;
			color: #F39F00; /* color browser dependent */
			font-family: sans-serif;
			opacity: 0.33;
			transition: opacity .2s; /* OPTIONAL */
		}
		
	/* .star:checked ~.r1,
	.star:focus ~.r1,
	.star:hover .star{
	  opacity:1;
	}
	.r1:hover ~.r1{
	  opacity:0.33;
	} */
		
/* 	.rating:has(input[type='radio']:checked) label {
    	opacity:1;
	}	
		
     .rating input[type='radio']:checked ~ label {
	   opacity: 0.33;
	} 

	 .rating input[type='radio']:checked + label {
	    opacity:1;
	}    */

/*      .rating input[type='radio']:not(checked) ~ label {
	    opacity: 1;
	} */
	 
/* 		
	     div.rating:hover label
	     {
		  opacity: 1;
		} 
		
		 
		div.rating label:hover  ~ label{
		  opacity: 0.33;
		}  
		
		  div.rating input[type="radio"]:not(checked) label.r1 {
		  opacity: 1;
		}  */ 
		 /* For loop of Css for checked and hover of each star */
		 #star-1:checked ~ label[for="star-1"],
		#star-1:hover ~ label[for="star-1"],

		#star-2:checked ~ label[for="star-1"],
		#star-2:checked ~ label[for="star-2"],
		#star-2:hover ~ label[for="star-1"],
		#star-2:hover ~ label[for="star-2"],

		#star-3:checked ~ label[for="star-1"],
		#star-3:checked ~ label[for="star-2"],
		#star-3:checked ~ label[for="star-3"],
		#star-3:hover ~ label[for="star-1"],
		#star-3:hover ~ label[for="star-2"],
		#star-3:hover ~ label[for="star-3"],

		#star-4:checked ~ label[for="star-1"],
		#star-4:checked ~ label[for="star-2"],
		#star-4:checked ~ label[for="star-3"],
		#star-4:checked ~ label[for="star-4"],
		#star-4:hover ~ label[for="star-1"],
		#star-4:hover ~ label[for="star-2"],
		#star-4:hover ~ label[for="star-3"],
		#star-4:hover ~ label[for="star-4"],

		#star-5:checked ~ label[for="star-1"],
		#star-5:checked ~ label[for="star-2"],
		#star-5:checked ~ label[for="star-3"],
		#star-5:checked ~ label[for="star-4"],
		#star-5:checked ~ label[for="star-5"],
		#star-5:hover ~ label[for="star-1"],
		#star-5:hover ~ label[for="star-2"],
		#star-5:hover ~ label[for="star-3"],
		#star-5:hover ~ label[for="star-4"],
		#star-5:hover ~ label[for="star-5"] {
			opacity: 1;
		}  

		.center {
			/* min-height: 100vh; */
			display: flex;
			justify-content: center;
			align-items: center;
		}
		
		#submit {
		  border-radius: 25px;
		   background-color: #007CC8;
		  color: #efefef;
		  border: none;
		  text-align: center;
		  padding: 10px 20px;
		  cursor: pointer;
		}
</style>

<script>
$(document).ready(function(){
	
 $('#feedback_content').keyup(function() {
	    
		  var characterCount = $(this).val().length,
		      current = $('#current'),
		      maximum = $('#maximum'),
		      theCount = $('#the-count');
		    
		  current.text(characterCount);
		 
		  
		  /*This isn't entirely necessary, just playin around*/
		  if (characterCount < 70) {
		    current.css('color', '#666');
		  }
		  if (characterCount > 70 && characterCount < 90) {
		    current.css('color', '#6d5555');
		  }
		  if (characterCount > 90 && characterCount < 100) {
		    current.css('color', '#793535');
		  }
		  if (characterCount > 100 && characterCount < 120) {
		    current.css('color', '#841c1c');
		  }
		  if (characterCount > 120 && characterCount < 139) {
		    current.css('color', '#8f0001');
		  }
		  
		  if (characterCount >= 140) {
		    maximum.css('color', '#8f0001');
		    current.css('color', '#8f0001');
		    theCount.css('font-weight','bold');
		  } else {
		    maximum.css('color','#666');
		    theCount.css('font-weight','normal');
		  }
		  
		      
		});
});
 $('#submit').click(function(){
	 $('#ratingSystem').val($("input[name='star']:checked").val());
	/*  $.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); */
		$.post($('form').attr('action'), $("form").serialize(), function(data){
			/* 	$('.tabbar').html(data);
				$('html').animate({scrollTop:0}, 'slow');
			 	$('body').animate({scrollTop:0}, 'slow');
			$.unblockUI();
			window.location.reload(); */
		
			if(data===true){
			  $.post('feedback/success',function(data){
				 $.fancybox(data);
			  });
			}else{
				$.post('feedback/failure',function(data){
					 $.fancybox(data);
				 });
			}
        });	 
 });
</script>
</head>
<body>
 <img src='./resources/images/feedback.jpg' width= '400' height= '200' >
<br/>
<h2 align="center" style="color:black"><spring:message code='feedback.header' text='Please Rate MLS System'/></h2>
  <form action="feedback/createfeedback" method="POST">
    <div align="center">
	<fieldset>
	<div class="center">
     <div class="rating">
     			<input type="radio" name="star" class="star" id="star-1" value="1">
				<input type="radio" name="star" class="star" id="star-2" value="2">
				<input type="radio" name="star" class="star" id="star-3" value="3">
				<input type="radio" name="star" class="star" id="star-4" value="4">
				<input type="radio" name="star" class="star" id="star-5" value="5">
				<label class="r1" for="star-1">&#11088;</label>
				<label class="r1" for="star-2">&#11088;</label>
				<label class="r1" for="star-3">&#11088;</label>
				<label class="r1" for="star-4">&#11088;</label>
				<label class="r1" for="star-5">&#11088;</label>
	</div></div><br/><br/>
    <div class="wrapper">
     <textarea name="feedback_content" id="feedback_content" rows="10" cols="30" maxlength="255" placeholder="<spring:message code='feedback.content' text='Please provide your suggestion here'/>" style="width: 350px;height: 200px;
                 padding: 10px; border:3px dashed black;
                 background-color:aliceblue"></textarea><br/><br/>
                   <div id="the-count">
    				<span id="current">0</span>
    				<span id="maximum">/ 255</span>
  				</div>
      </div>           
     <br/> <br/>
     <input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>">
     </fieldset>
    </div> 
    <input type="hidden" id="ratingSystem" name="ratingSystem"/>
   </form>
</body>
</html>