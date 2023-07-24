<html>
<head>
<style>
.responsive {
  max-width: 100%;
  height: auto;
}
</style>
<script>

document.addEventListener("DOMContentLoaded", () => {
	var urlText=window.location.href; 
	var lastword = urlText.lastIndexOf("/",urlText.lastIndexOf("/")-1);
	var newUrl = urlText.substring(0,lastword);
	var meta = document.createElement('meta');
	meta.httpEquiv = "refresh";
	meta.content = "5;URL="+newUrl+"/j_spring_security_logout";
	document.getElementsByTagName('head')[0].appendChild(meta);
});

</script>
</head>
<body>
<img class="responsive" src='../resources/images/feedback_success.jpg'/>
</body>
</html>