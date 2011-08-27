<%@ include file="/common/taglibs.jsp"%>
<footer>
      <div id="footer" style="padding:5px;font-weight:bold;float:right;margin-right: 10px;color:navy; ">
      	  <jsp:useBean id="now" class="java.util.Date" />
          <div class="grid_8">
              <spring:message code="app.name"/>&nbsp;v<spring:message code="app.version"/> | &copy; <a href="http://www.mkcl.org" target="_blank">MKCL</a>, <fmt:formatDate pattern="yyyy" value="${now}" />-12. All rights reserved. 
          </div>

      </div>
</footer>