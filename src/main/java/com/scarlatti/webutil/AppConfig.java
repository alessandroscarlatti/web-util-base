package com.scarlatti.webutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlatti.webutil.model.WuActivityDetails;
import com.scarlatti.webutil.model.WuDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    WuActivityDetails wuActivityDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(WuActivityDetails.class.getResourceAsStream("/wuDetails.json"), WuActivityDetails.class);
        } catch (Exception e) {
            log.error("Error initializing tasks from JSON.", e);
            return new WuActivityDetails();
        }
    }

    @Bean
    @ConfigurationProperties("wu")
    WuDetails wuDetails() {
        return new WuDetails();
    }
}
