<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${errorcode eq 'houseformationyearnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseformationyearnotset" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) not set"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseformationyearsetincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseformationyearsetincorrect" text="Custom Parameter 'HOUSE_FORMATION_YEAR'(Year in which assembly/council was formed) was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'nosessionentriesfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.nosessionentriesfound" text="No session found in selected house type of authenticated user"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'userhousetypenotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.userhousetypenotset" text="There is no house type set for the current user."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'houseType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseType_isempty" text="Check request parameter 'houseType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseType_isnull" text="Check request parameter 'houseType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'houseType_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseType_isincorrect" text="request parameter 'houseType' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionType_isempty" text="Check request parameter 'sessionType' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionType_isnull" text="Check request parameter 'sessionType' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'sessionType_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseType_isincorrect" text="request parameter 'sessionType' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'year_isempty'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.year_isempty" text="Check request parameter 'year' for no value"/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'year_isnull'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.year_isnull" text="Check request parameter 'year' for null value"/>
			</p>
			<p></p>
		</div>
	</c:when>

	<c:when test="${errorcode eq 'year_isincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.year_isincorrect" text="request parameter 'year' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'sessionparametersnotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionparametersnotset" text="one or more session device config parameters ending with _difference not set for the session."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'allgroupssetforsession'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.allgroupssetforsession" text="Not allowed. All the groups are already set for this session."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'nodefaultgroupnumberfound'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.nodefaultgroupnumberfound" text="Custom Parameter 'DEFAULT_GROUP_NUMBER' not set."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'defaultgroupnumbersetincorrect'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.defaultgroupnumbersetincorrect" text="Custom Parameter 'DEFAULT_GROUP_NUMBER' was set with incorrect value."/>
			</p>
			<p></p>
		</div>
	</c:when>	
		
	
	<c:when test="${errorcode eq 'incorrectdefaultgroupnumberset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.incorrectdefaultgroupnumberset" text="All the groups are already set for this session. Please check custom parameter 'DEFAULT_GROUP_NUMBER'."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'sessionnotfoundforgroup'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionnotfoundforgroup" text="There is no session associated with this group. Please delete this group."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'sessionstartdatenotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionstartdatenotset" text="There is no start date set for the session associated with this group."/>
			</p>
			<p></p>
		</div>
	</c:when>	
	
	<c:when test="${errorcode eq 'sessionenddatenotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.sessionenddatenotset" text="There is no end date set for the session associated with this group."/>
			</p>
			<p></p>
		</div>
	</c:when>
	
	<c:when test="${errorcode eq 'server_dateformat_notset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.houseformationyearnotset" text="Custom Parameter 'SERVER_DATEFORMAT' not set"/>
			</p>
			<p></p>
		</div>
	</c:when>		
	
	<c:when test="${errorcode eq 'groupnumbernotset'}">
		<div class="toolTip tpRed clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="group.errorcode.groupnumbernotset" text="Group Number is not set for this group."/>
			</p>
			<p></p>
		</div>
	</c:when>	
</c:choose>