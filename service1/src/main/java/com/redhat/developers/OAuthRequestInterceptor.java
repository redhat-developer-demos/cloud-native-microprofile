package com.redhat.developers;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class OAuthRequestInterceptor implements RequestInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String BEARER_TOKEN_TYPE = "Bearer";

	private String accessToken;

	public OAuthRequestInterceptor(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public void apply(RequestTemplate template) {
		if (accessToken != null) {
			template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, accessToken));
		}

	}

}
