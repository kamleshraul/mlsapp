<%@ include file="/common/taglibs.jsp" %>
<header>
         <div class="container_8 clearfix">
             <h1 class="grid_1"><a href="./home.htm"><spring:message code="app.name"/></a></h1>

             <nav class="grid_5">
                 <ul class="clearfix">
                     <li class="fr">
					 	 <a href="#" style="color:Highlight;">Welcome <security:authentication property="principal.firstName"></security:authentication>&nbsp;<security:authentication property="principal.lastName"></security:authentication></a>
                     	 <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true" />"><spring:message code="menu.logout.title"/></a></li>
                   </ul>

             </nav>
 
         </div>
</header>
      