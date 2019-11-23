package com.mz.example.service;

import com.mz.example.AbstractApplicationTest;
import com.mz.example.db.model.PersonEmail;
import com.mz.example.db.repository.PersonEmailRepository;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseEmailDistributionServiceTest extends AbstractApplicationTest {

    @Autowired
    protected PersonEmailRepository repository;

    @Before
    public void setup() throws Exception {
        repository.deleteAll();
        Assert.assertEquals(0, repository.count());
    }

    protected void simulateSendEmailsTriggeredOnMultipleInstances(
            int numberOfReadWriteTasks,
            int numberOfEntitiesToGenerateWithEachTask,
            EmailDistributionService service) throws Exception {

        AtomicInteger expectedSendCount = new AtomicInteger(0);
        AtomicInteger actualSendCount = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        class ReadFromDBTask implements Callable<Boolean> {
            @Override
            public Boolean call() throws Exception {
                actualSendCount.addAndGet(service.sendEmails());
                return true;
            }
        }
        class WriteToDBTask implements Callable<Boolean> {
            @Override
            public Boolean call() throws Exception {
                List<PersonEmail> toSave = IntStream.range(0, numberOfEntitiesToGenerateWithEachTask)
                        .mapToObj(ordinal -> create(expectedSendCount.get()+ordinal))
                        .collect(Collectors.toList());
                repository.saveAll(toSave);
                expectedSendCount.addAndGet(toSave.size());
                return true;
            }
        }

        try {
            int failures = 0;
            for (int i = 0; i < numberOfReadWriteTasks; ++i) {
                int lastRead = actualSendCount.get();
                executorService.submit(new WriteToDBTask()).get();
                List<Future<Boolean>> futures = executorService.invokeAll(Arrays.asList(
                        new ReadFromDBTask(),
                        new ReadFromDBTask()
                ));
                for (Future<Boolean> future : futures) {
                    future.get();
                }
                if (numberOfEntitiesToGenerateWithEachTask != actualSendCount.get() - lastRead) {
                    ++failures;
                }
            }
            Assert.assertEquals("For " + numberOfReadWriteTasks + " tests there where " + failures + " failures." +
                    " Expected number of send emails: " + expectedSendCount.get() + "; Actual number of send emails: " + actualSendCount.get(), 0, failures);
        } finally {
            executorService.shutdownNow();
            if(!executorService.awaitTermination(3, TimeUnit.SECONDS)){
                throw new AssertionError("Timout elapsed while waiting for test executor termination");
            }
        }
    }

    protected PersonEmail create(int id){
        PersonEmail email = new PersonEmail();
        email.setPersonId(id);
        email.setName("IRRELEVANT");
        email.setEmailSent(false);
        return email;
    }
}
