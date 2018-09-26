package org.mkcl.els.atmosphere;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.managed.Encoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.mkcl.els.domain.notification.PushMessage;

public class JacksonEncoder implements Encoder<PushMessage, String> {
	
	@Inject
    private ObjectMapper mapper;

    @Override
    public String encode(PushMessage m) {
        try {
            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}