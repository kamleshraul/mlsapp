<%@ include file="/common/taglibs.jsp" %>
<!-- Header  --> 
<div id="pannelDash" class="clearfix">
	 <!-- Tabs--> 
	<div class="menu">
		 <ul>
		 <li class="selected">
				<a href="#" onclick="showOnly('tabDashboard','dashWidget')" id="welcome"><img src="./resources/images/template/icons/home.png" alt="" width="27" height="27"/><spring:message code="home.welcome" text="Welcome to ELS"></spring:message></a>
		</li>
		<security:authorize access="hasAnyRole('DATA ENTRY OPERATOR','ADMIN')">
		<li>
				<a href="#"  id="lowerhouse" class="changeHouse"><img src="./resources/images/template/icons/home.png" alt="" width="27" height="27"/><spring:message code="home.lowerhouse" text="Assembly"></spring:message></a>
		</li>		
		<li>
				<a href="#"  id="upperhouse" class="changeHouse"><img src="./resources/images/template/icons/home.png" alt="" width="27" height="27"/><spring:message code="home.upperhouse" text="Council"></spring:message></a>
		</li>
		</security:authorize>				
		</ul>
		<div class="info">
			<div><a id="logout" class="icOff" href="<c:url value="/j_spring_security_logout" />"><spring:message code="logout" text="Logout"/></a></div>
			<div class="user">
			<c:choose>
		 	<c:when test="${(authphoto=='')}">
			<img width="27" height="27" src="./resources/images/template/user_icon.png" alt="User name" />
		 	</c:when>
		 	<c:otherwise>
		 	<img width="27" height="27" src="file/photo/${authphoto}" alt="User name" />
		 	</c:otherwise>
		 	</c:choose>
				<span>${authtitle}&nbsp;${authlastname}&nbsp;${authfirstname}&nbsp;${authmiddlename}</span>
				<span class="detail" style="color:black">${logintime}</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
//added by amitd and sandeeps
$('.changeHouse').click(function(){
	$.get('change/'+$(this).attr("id"),function(data){
		$('#authhousetype').val(data.authhousetype);
	});
	});
</script>