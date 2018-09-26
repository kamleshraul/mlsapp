package org.mkcl.els.atmosphere;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.managed.Decoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.mkcl.els.domain.notification.PushMessage;

public class JacksonDecoder implements Decoder<String, PushMessage> {
	
	@Inject
    private ObjectMapper mapper;

    @Override
    public PushMessage decode(String s) {
        try {
            return mapper.readValue(s, PushMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}