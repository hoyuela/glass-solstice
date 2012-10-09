package com.discover.mobile.login;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.support.Base64;
import org.springframework.web.client.RestTemplate;

public class LoginService {
	private static String url = "";
	public void login(String username, String password) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpAuthentication auth = authenticationHeader(username, password);
		requestHeaders.setAuthorization(auth);
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		
	}
	private HttpAuthentication authenticationHeader(final String username,
			final String password) {
		return new HttpAuthentication() {
			@Override
			public String getHeaderValue() {
				StringBuilder sb = new StringBuilder();
				sb.append(Base64.encodeBytes(username.getBytes()));
				sb.append("\": :\"");
				sb.append(Base64.encodeBytes(password.getBytes()));
				return sb.toString();
			}
		};
	}

}
