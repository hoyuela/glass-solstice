package com.discover.mobile.card.common.net.json;

import com.discover.mobile.common.net.json.JsonResponseMappingNetworkServiceCall;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates and initializes the common {@link ObjectMapper} for the {@link JsonResponseMappingNetworkServiceCall}s. Since the
 * {@link ObjectMapper} is thread-safe as long as its not being configured, this class will handle its setup and users
 * should not modify its configuration.
 */
public  class JacksonObjectMapperHolder {
	
	static final ObjectMapper mapper = createObjectMapper();
	
	/**
	 * 
	 * @return Returns a jackson mapper used to deserialize an incoming error response with a JSON body 
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}

/** 
 * Create the Jackson Mapper 
 * @return ObjectMapper Jackson Mapper
 */
	private static ObjectMapper createObjectMapper() {
		return new ObjectMapper()
				.disable(MapperFeature.AUTO_DETECT_GETTERS)
				.disable(MapperFeature.AUTO_DETECT_SETTERS)
				.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	/**
	 * Singleton Class so prevent the making of the class.
	 */
	private JacksonObjectMapperHolder() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
