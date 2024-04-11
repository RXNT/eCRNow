package com.drajer.bsa.auth.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.drajer.bsa.auth.RestApiAuthorizationHeaderIf;
import com.drajer.bsa.model.KarProcessingData;

@Service
public class RestApiAuthorizerImpl implements RestApiAuthorizationHeaderIf {
    private final Logger logger = LoggerFactory.getLogger(RestApiAuthorizerImpl.class);

    @Value("${rxnt.api.key}")
    String apiKey;

    @Override
    public HttpHeaders getAuthorizationHeader(KarProcessingData data) {

        HttpHeaders headers = new HttpHeaders();
        logger.info("Fetching the AccessToken");
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("Invalid API key");
        }

        // Set Authorization Header
        headers.add("X-API-Key", apiKey);

        return headers;
    }
}
