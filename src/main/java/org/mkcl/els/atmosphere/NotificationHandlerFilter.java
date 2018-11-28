package org.mkcl.els.atmosphere;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.BroadcastFilter.BroadcastAction.ACTION;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.mkcl.els.domain.notification.PushMessage;

public class NotificationHandlerFilter implements PerRequestBroadcastFilter   {
	
	@Override
	public BroadcastAction filter(String broadcasterId, AtmosphereResource r,
			Object originalMessage, Object message) {
		
		if(message instanceof PushMessage) {
			
			PushMessage pm = (PushMessage) message;
			
			if(pm.getReceivers()!=null && !pm.getReceivers().isEmpty() && !pm.getReceivers().equals("all")) {	
				
				boolean isReceiverFound = false;
				String receiver = r.getRequest().getParameter("client_username");
				
				for(String rValue: pm.getReceivers().split(",")) {
					
					if(receiver!=null && receiver.equals(rValue)) {
						isReceiverFound = true;
						return new BroadcastAction(ACTION.CONTINUE, message);
						
					} else {
						continue;
					}
					
				}
				
				if(!isReceiverFound) {
					return new BroadcastAction(ACTION.ABORT, message);
				}
				
				return new BroadcastAction(message);
				
			} else {
				return new BroadcastAction(message);
			}
			
		} else {
			return new BroadcastAction(message);
		}		
	}

	@Override
	public BroadcastAction filter(String broadcasterId, Object originalMessage, Object message) {
		return new BroadcastAction(message);
	}	

}