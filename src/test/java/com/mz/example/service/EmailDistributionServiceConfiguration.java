package com.mz.example.service;

import com.mz.example.db.model.PersonEmail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration
public class EmailDistributionServiceConfiguration {

    public static final String DELAY_SERVICE = "delay-service";

    @Bean
    @Profile(EmailDistributionServiceConfiguration.DELAY_SERVICE)
    public EmailDistributionService emailDistributionService() {
        return new DelayEmailDistributionService();
    }

    private static class DelayEmailDistributionService extends EmailDistributionService {

        @Override
        boolean trySend(PersonEmail personEmail) {
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException ex) {
                throw new AssertionError("Unable to perform delay.", ex);
            }
            return super.trySend(personEmail);
        }
    }
}
