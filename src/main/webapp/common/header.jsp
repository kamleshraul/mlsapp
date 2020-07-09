<%@ include file="/common/taglibs.jsp" %>
<!-- Header  --> 
<div id="pannelDash" class="clearfix">
	 <!-- Tabs--> 
	<div class="menu" id="vueApp" v-cloak>
		 <ul>
		 <li class="selected">
				<a href="#" onclick="showOnly('tabDashboard','dashWidget')" id="welcome"><img src="./resources/images/template/icons/home.png" alt="" width="37" height="37"/><spring:message code="home.welcome" text="Welcome to ELS"></spring:message></a>
		</li>				
		</ul>
		<div class="info">
			<div><a id="logout" class="icOff" href="<c:url value="/j_spring_security_logout" />"><spring:message code="logout" text="Logout"/></a></div>
			<div class="user">
				<img width="27" height="27" src="./resources/images/template/user_icon.png" alt=" " />
				
				<span v-if="notificationsCount>0" id="notification_counter" style="opacity: 1;margin-left: 72px;">{{notificationsCount}}</span>
				<a id="notifyIcon" href="#" style="margin-left: 50px;border-radius: 50%;cursor:pointer;">
					<!-- <img src="./resources/images/template/icons/fb_notification.png" alt="Notifications" width="18" height="18"/> -->
					<img src="./resources/images/template/icons/bell_notification_icon.png" alt="Notifications" width="22" height="22" title="Notifications"/>
				</a>				
				<a id="supportIcon" href="${supportURL}" target="_blank" style="margin-top: -20px;margin-left: 95px;border-radius: 50%;cursor:pointer;">
					<img src="./resources/images/template/icons/support_icon3.jpg" alt="Support" width="22" height="22" title="Support"/>
				</a>
				<!-- <support-icon></support-icon> -->
				<!-- <notification-icon></notification-icon> -->
				
				<span>${authtitle}&nbsp;${authfirstname}&nbsp;${authmiddlename}&nbsp;${authlastname}</span>
				<span id="server_time_display" style="width: 200px"></span>
			</div>
		</div>
		<div id="notifications" v-if="notificationsList.length > 0">
            <div class="notifications_header">
            	<label><spring:message code="generic.notification" text="Notifications"/></label>       
            	<a href="#" v-on:click="markAllNotificationsAsRead" style="font-size: 12px !important;"><spring:message code="notification.markAllAsRead" text="Mark all as read"/></a>
            	<a href="#" v-on:click="clearAllReadNotifications" style="font-size: 12px !important;"><spring:message code="notification.clearAllRead" text="Clear all read"/></a>
            </div>
            <div class="notifications_content">
				<!-- <ol>
					<li v-for="notification in notificationsList">
					  {{ notification.title }}
					</li>
				</ol> -->
				<table class="notifications_table" style="width:380px !important;">
					<tr v-for="(notification, nIndex) in notificationsList" v-show="!notification.clearedByReceiver" v-bind:class="{ notifications_marked_as_read : notification.markedAsReadByReceiver, notifications_unread : !notification.markedAsReadByReceiver }">
				    	<td width="90%">
				    		<div><a href="#notificationViewerPopUp" class="fbox" v-on:click="readNotification(nIndex)" style="text-decoration: none;color: black;">{{ notification.title }}</a></div>
				    		<div class="momentDuration" style="color: #90949c;margin-top: 5px;">{{ notification.formattedSentOn }}</div>
				    	</td>
				    	<td width="10%" align="right">
				    		<a href="#" v-on:click="clearNotification(nIndex)" style="text-decoration: none;"><img src="./resources/images/delete_dustbin.png" alt="Support" width="16" height="18"/></a>	
				    	</td> 
				  </tr>
				</table>
            </div>
            <div class="notifications_footer">
            	<a href="#" v-on:click="viewAllNotifications" style="font-size: 11.5px !important;">
            		<spring:message code="notification.seeAll" text="See all"/>
            	</a>
            </div>
        </div>
        <!-- notification viewer div -->
        <notification-viewer v-bind:selected-notification="selectedNotification" inline-template>
		<div id="notificationViewerPopUpDiv" style="display:none">
		    <span id="notificationViewerPopUp" class="fields clearfix">
		    	 <p>
		    	 	<label class="small"><spring:message code="notification.sender" text="Sender"/>:</label>
					<input type="text" class="sText" v-bind:value="selectedNotification.senderName" readonly="readonly">
	    		 </p> 
	    		 <template v-if="selectedNotification.title != selectedNotification.message">
	    		 	 <p style="margin-top:20px;">
			         	<label class="centerlabel"><spring:message code="notification.title" text="Title"/>:</label>
			         	<textarea rows="2" cols="50" readonly="readonly" style="text-align: left;">{{ selectedNotification.title }}</textarea>
			         </p>
			         <p style="margin-top:20px;">
			         	<label class="wysiwyglabel"><spring:message code="notification.message" text="Message"/>:</label>
			         	<textarea id="selectedNotificationMessage" readonly="readonly">{{ selectedNotification.message }}</textarea>
			         </p>
	    		 </template>
	    		 <template v-else>
	    		 	 <p style="margin-top:20px;">
			         	<label class="centerlabel"><spring:message code="notification.message" text="Message"/>:</label>
			         	<textarea rows="3" cols="50" readonly="readonly" style="text-align: left;">{{ selectedNotification.title }}</textarea>
			         </p>
	    		 </template>		         
		    </span>
		</div>
		</notification-viewer>
	</div>
</div>