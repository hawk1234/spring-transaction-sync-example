package com.mz.example.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailDistributionServiceTest extends BaseEmailDistributionServiceTest {

    @Autowired
    private EmailDistributionService service;

    @Test
    public void testSendingEmails() {
        repository.save(create(0));
        Assert.assertEquals(1, service.sendEmails());
    }

    @Test
    public void testSendingEmailsWaitingToSendIsThreadSafe() throws Exception {
        int NUMBER_OF_READ_WRITE_TASKS = 10000;
        int NUMBER_OF_ENTITIES_TO_GENERATE = 10;
        simulateSendEmailsTriggeredOnMultipleInstances(NUMBER_OF_READ_WRITE_TASKS, NUMBER_OF_ENTITIES_TO_GENERATE, service);
    }
}
