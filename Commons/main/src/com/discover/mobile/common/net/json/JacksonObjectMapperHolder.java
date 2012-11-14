package com.discover.mobile.common.net.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates and initializes the common {@link ObjectMapper} for the {@link JsonResponseMappingNetworkServiceCall}s. Since the
 * {@link ObjectMapper} is thread-safe as long as its not being configured, this class will handle its setup and users
 * should not modify its configuration.
 */
final class JacksonObjectMapperHolder {
	
	static final ObjectMapper mapper = createObjectMapper();
	
	private static ObjectMapper createObjectMapper() {
		return new ObjectMapper()
				.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_SETTERS, false)
				.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
				.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	private JacksonObjectMapperHolder() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
