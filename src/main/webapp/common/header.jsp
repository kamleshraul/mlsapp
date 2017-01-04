<%@ include file="/common/taglibs.jsp" %>
<!-- Header  --> 
<div id="pannelDash" class="clearfix">
	 <!-- Tabs--> 
	<div class="menu">
		 <ul>
		 <li class="selected">
				<a href="#" onclick="showOnly('tabDashboard','dashWidget')" id="welcome"><img src="./resources/images/template/icons/home.png" alt="" width="37" height="37"/><spring:message code="home.welcome" text="Welcome to ELS"></spring:message></a>
		</li>				
		</ul>
		<div class="info">
			<div><a id="logout" class="icOff" href="<c:url value="/j_spring_security_logout" />"><spring:message code="logout" text="Logout"/></a></div>
			<div class="user">
				<img width="27" height="27" src="./resources/images/template/user_icon.png" alt=" " />
				<span><a id="support" href="${supportURL}" target="_blank"><spring:message code="support.text" text="Support"/></a></span>
				<span>${authtitle}&nbsp;${authfirstname}&nbsp;${authmiddlename}&nbsp;${authlastname}</span>
				<span style="width: 200px">${logintime}</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
</script>