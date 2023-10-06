package com.drajer.bsa.auth.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import com.drajer.bsa.auth.RestApiAuthorizationHeaderIf;
import com.drajer.bsa.model.KarProcessingData;

public class RestApiAuthorizerImpl implements RestApiAuthorizationHeaderIf {
    private final Logger logger = LoggerFactory.getLogger(SampleRestApiAuthorizer.class);

    @Autowired private Environment environment;

    @Override
    public HttpHeaders getAuthorizationHeader(KarProcessingData data) {

        HttpHeaders headers = new HttpHeaders();
        logger.info("Fetching the AccessToken");

        String securityToken = environment.getRequiredProperty("rxnt.api.key");

        // Set Authorization Header
        headers.add("X-Api-Key", securityToken);
        return headers;
    }
}
