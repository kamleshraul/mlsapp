<html>
<head>
<style>
.responsive {
  max-width: 100%;
  height: auto;
}
</style>
</head>
<body>
<img class="responsive" src='../resources/images/feedback_success.jpg'/>
</body>
<script type="text/javascript">


function redirectLogin() {
	var urlText=window.location.href; 
	var lastword = urlText.lastIndexOf("/",urlText.lastIndexOf("/")-1);
	var newUrl = urlText.substring(0,lastword);
	var meta = document.createElement('meta');
/* 	meta.httpEquiv = "refresh";
	meta.content = "5;URL="+newUrl+"/j_spring_security_logout";
	document.getElementsByTagName('head')[0].appendChild(meta); */
	window.location.href = newUrl+"/j_spring_security_logout";
};

 setTimeout(function() {
	redirectLogin();
}, 1500);
 /*
alert('Hello');
 */
</script>

</html>