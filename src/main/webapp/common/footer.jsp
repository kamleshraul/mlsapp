<%@ include file="/common/taglibs.jsp"%>
<footer>
      <div class="container_8 clearfix" id="footer-inner">
      	  <jsp:useBean id="now" class="java.util.Date" />
          <div class="grid_8">
              <spring:message code="app.name"/>&nbsp;v<spring:message code="app.version"/> | &copy; MKCL, <fmt:formatDate pattern="yyyy" value="${now}" />-12. All rights reserved. 
          </div>

      </div>
</footer>