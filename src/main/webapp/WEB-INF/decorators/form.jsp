<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<title>
	<decorator:title/>
</title>
	<decorator:head/>			
	<script type="text/javascript">
		function scrollTop(){
		$('html').animate({scrollTop:0}, 'slow');
		$('body').animate({scrollTop:0}, 'slow');				 	   	
		}
		$(document).ready(function() {
			//for cancel button we need to do following:
			//1.In case of method handlers for various operations like new and edit
			//we will set the name of the function to be called when cancel is clicked.
			//this will mostly require setting the value of cancelFn from inside
			//these method handlers(list.jsp and module.jsp)
			//Also cancelFn is a hidden field in home.jsp
			$("#cancel").click(function(){								
				var cancelFunction=$("#cancelFn").val();
				var fnName=cancelFunction;
				window[fnName]();									
				return false;				
			});
			initControls();
		    $(':input:visible:not([readonly]):first').focus();
			$("form").submit(function(e){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});								
				$.post($('form').attr('action'),  
		            $("form").serialize(),  
		            function(data){
	   					$('.tabContent').html(data);
	   					$('html').animate({scrollTop:0}, 'slow');
	   				 	$('body').animate({scrollTop:0}, 'slow');	
						$.unblockUI();	   				 	   				
		            });
		        return false;  
		    }); 
		
		});

		
	</script>	
</head>
<body>	
	<decorator:body/>	
</body>
</html>