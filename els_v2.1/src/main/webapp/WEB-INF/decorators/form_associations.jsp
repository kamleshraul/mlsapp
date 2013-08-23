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
				$.post($('form').attr('action'),  
		            $("form").serialize(),  
		            function(data){
	   					$('#grid_container').html(data);
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