<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<style>
body{
 overflow: hidden;
}
/* make the current radio visually hidden */
input[type=radio]{ 
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  margin: 0;
  box-shadow: none;
  width: 35px; /* remove shadow on invalid submit */
}

/* generated content is now supported on input. supporting older browsers? change button above to {position: absolute; opacity: 0;} and add a label, then style that, and change all selectors to reflect that change */
input[type=radio]::after {
  content: '\2605';
  font-size: 32px;
}

/* by default, if no value is selected, all stars are grey */
input[type=radio]:invalid::after {
  color: #ddd;
}

/* if the rating has focus or is hovered, make all stars darker */
rating:hover input[type=radio]:invalid::after,
rating:focus-within input[type=radio]:invalid::after
{color: #888;}

/* make all the stars after the focused one back to ligh grey, until a value is selected */
rating:hover input[type=radio]:hover ~ input[type=radio]:invalid::after,
rating input[type=radio]:focus ~ input[type=radio]:invalid::after  {color: #ddd;}


/* if a value is selected, make them all selected */
rating input[type=radio]:valid {
  color: orange;
}
/* then make the ones coming after the selected value look inactive */
rating input[type=radio]:checked ~ input[type=radio]:not(:checked)::after{
  color: #ccc;
  content: '\2606'; /* optional. hollow star */
}

#submit {
  border-radius: 25px;
   background-color: #007CC8;
  color: #efefef;
  border: none;
  text-align: center;
  padding: 10px 20px;
}
</style>
<script>
$(document).ready(function(){
	$('[type*="radio"]').change(function () {
	    var me = $(this);
	    $('#ratingSystem').attr('value',me.attr('value'));
	 });
});
 $('#submit').click(function(){
	/*  $.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); */
		$.post($('form').attr('action'), $("form").serialize(), function(data){
			/* 	$('.tabbar').html(data);
				$('html').animate({scrollTop:0}, 'slow');
			 	$('body').animate({scrollTop:0}, 'slow');
			$.unblockUI();
			window.location.reload(); */
			console.log(data);
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
<style>
#submit{
 cursor: pointer;
}
</style>
</head>
<body>
 <img src='./resources/images/feedback.jpg' width= '400' height= '200' >
<br/>
<h1 align="center" style="color:black">Please Rate MLS System</h1>
  <form action="feedback/createfeedback" method="POST">
    <div align="center">
	<fieldset>
     <rating>
       <input type="radio" name="rating" value="1" aria-label="1 star" required/><input type="radio" name="rating" value="2" aria-label="2 stars"/><input type="radio" name="rating" value="3" aria-label="3 stars"/><input type="radio" name="rating" value="4" aria-label="4 stars"/><input type="radio" name="rating" value="5" aria-label="5 stars"/>
     </rating><br/><br/>
     <br/>
     <textarea name="feedback_content" id="feedback_content" rows="10" cols="30" placeholder="Please provide your suggestion here" style="width: 350px;height: 200px;
                 padding: 10px; border:3px dashed black;
                 background-color:aliceblue"></textarea>
     <br/> <br/>
     <input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>">
     </fieldset>
    </div> 
    <input type="hidden" id="ratingSystem" name="ratingSystem"/>
   </form>
</body>
</html>