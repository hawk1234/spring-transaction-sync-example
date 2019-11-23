package com.mz.example.service;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(EmailDistributionServiceConfiguration.DELAY_SERVICE)
public class DelayEmailDistributionServiceTest extends BaseEmailDistributionServiceTest{

    @Autowired
    private EmailDistributionService service;

    @Test
    @Ignore("Not implemented")
    public void testSendEmailsDoesNotThrowLockAcquisitionExceptionOutsideMethodScope() throws Exception {
        int NUMBER_OF_READ_WRITE_TASKS = 1;
        int NUMBER_OF_ENTITIES_TO_GENERATE = 1;
        simulateSendEmailsTriggeredOnMultipleInstances(NUMBER_OF_READ_WRITE_TASKS, NUMBER_OF_ENTITIES_TO_GENERATE, service);
    }
}
