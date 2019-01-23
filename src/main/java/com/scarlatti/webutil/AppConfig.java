package com.scarlatti.webutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlatti.webutil.model.WuDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    WuDetails wuDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(WuDetails.class.getResourceAsStream("/wuDetails.json"), WuDetails.class);
        } catch (Exception e) {
            log.error("Error initializing tasks from JSON.", e);
            return new WuDetails();
        }
    }
}
