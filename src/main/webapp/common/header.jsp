<%@ include file="/common/taglibs.jsp" %>
<div class="header">
        	<div>
         		Welcome <security:authentication property="principal.firstName"></security:authentication>&nbsp;<security:authentication property="principal.lastName"></security:authentication> | <a href="#">Your account</a> | <a id="logout" href="<c:url value="/j_spring_security_logout" />">Logout</a> | <a href="#">Help</a>
        	</div>
        	<span class="title">e-Legislature <a href="#" class="version">v 1.0.0</a></span><br />
</div>