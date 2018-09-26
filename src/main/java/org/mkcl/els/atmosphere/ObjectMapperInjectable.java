package org.mkcl.els.atmosphere;

import java.lang.reflect.Type;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.inject.Injectable;
import org.codehaus.jackson.map.ObjectMapper;

public class ObjectMapperInjectable implements Injectable<ObjectMapper> {
	
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public ObjectMapper injectable(AtmosphereConfig config) {
        return mapper;
	}

	@Override
	public boolean supportedType(Type t) {
        return (t instanceof Class) && ObjectMapper.class.equals((Class) t);
	}	

}