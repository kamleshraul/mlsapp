package org.mkcl.els.atmosphere;

import java.io.IOException;

import javax.inject.Inject;

import org.mkcl.els.domain.notification.PushMessage;
import org.slf4j.Logger;

import org.atmosphere.config.service.DeliverTo;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Heartbeat;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.LoggerFactory;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

@Singleton
@ManagedService(path = "/chat", atmosphereConfig = MAX_INACTIVE + "=120000")
public class Chat {
	
	private final Logger logger = LoggerFactory.getLogger(Chat.class);
	
	@Inject
	private BroadcasterFactory factory;
	
	@Heartbeat
    public void onHeartbeat(final AtmosphereResourceEvent event) {
        logger.trace("Heartbeat send by {}", event.getResource());
	}

    @Ready
    public void onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected.", r.uuid());
        logger.info("BroadcasterFactory used {}", factory.getClass().getName());
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", event.getResource().uuid());
        }
    }

    @org.atmosphere.config.service.Message(encoders = {JacksonEncoder.class}, decoders = {JacksonDecoder.class})
    public PushMessage onMessage(PushMessage message) throws IOException {
        logger.info("{} just send {}", message.getSender(), message.getMessage());
        return message;
    }

}
