<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
</head>
<body>

<form action="${EXTERNALLINK}" method="post" style="visibility: hidden;">
	<input type="text" name="token" value="${token}"/>
	<input id="btnSubmit" type="submit"></input>
</form>

<script>
	var btnSubmit=document.getElementById("btnSubmit");
	if(btnSubmit!==null && btnSubmit!==undefined){
		btnSubmit.click();
	}
</script>
</body>
</html>