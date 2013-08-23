<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title><spring:message code="holiday" text="Holidays"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				$('#key').val('');	
				$('.datemask').focus(function(){					
					$(".datemask").mask("99/99/9999");				
				});
			});		
		</script>
		
		<style type="text/css">
			.customTextArea{
				border: 1px solid #011B80;
				border-radius: 5px;
				min-width: 500px;
				max-width: 700px;
				min-height: 250px;
			}
			.customText{
				border-radius: 5px;
				min-width: 500px;
				max-width: 700px;
				border: 1px solid #011B80;
			}
			.customText:focus{
				/* background: #77BFBD;
				filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#77BFBD', endColorstr='#FFFFFF');
				background: -webkit-gradient(linear, left top, left bottom, from(#77BFBD), to(#FFFFFF));
				background: -moz-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -ms-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -o-linear-gradient(top,  #77BFBD,  #FFFFFF); */
				background: #DDE0ED;
			}
			.customTextArea:focus{
				/* background: #77BFBD;
				filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#77BFBD', endColorstr='#FFFFFF');
				background: -webkit-gradient(linear, left top, left bottom, from(#77BFBD), to(#FFFFFF));
				background: -moz-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -ms-linear-gradient(top,  #77BFBD,  #FFFFFF);
				background: -o-linear-gradient(top,  #77BFBD,  #FFFFFF); */
				background: #DDE0ED;
			}
		</style>
	</head>
	
	<body>	
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields clearfix vidhanmandalImg">
		<form:form action="query" method="POST"  modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<h2><spring:message code="generic.new.heading" text="Enter Details"/>
				[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
			</h2>	
			<form:errors path="version" cssClass="validationError"/>	
			<p> 
				<label class="small"><spring:message code="generic.locale" text="Locale"/></label>
				<form:input cssClass="sText customText" path="locale"/>
				<form:errors path="locale" cssClass="validationError"/>	
			</p>	 
			<p> 
				<label class="small"><spring:message code="query.field" text="Key Field"/></label>
				<form:input cssClass="sText customText" path="keyField"/>
				<form:errors path="keyField" cssClass="validationError"/>	
			</p>
			<p> 
				<label class="labeltop"><spring:message code="query.querytext" text="Query"/></label>
				<form:textarea cssClass="customTextArea" path="query" rows="10" cols="100" />
				<form:errors path="query" cssClass="validationError"/>	
			</p>				
			<div class="fields expand">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				</p>
			</div>	
			<form:hidden path="version" />
			<form:hidden path="id"/>
		</form:form>
		</div>	
	</body>
</html>