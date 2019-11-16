package com.mz.example.service;

import com.mz.example.AbstractApplicationTest;
import com.mz.example.db.model.PersonEmail;
import com.mz.example.db.repository.PersonEmailRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EmailDistributionServiceTest extends AbstractApplicationTest {

    @Autowired
    private EmailDistributionService service;
    @Autowired
    private PersonEmailRepository repository;

    @Before
    public void setup() throws Exception {
        repository.deleteAll();
        Assert.assertEquals(0, repository.count());
    }

    @Test
    public void testSendingEmails() {
        repository.save(create(0));
        Assert.assertEquals(1, service.sendEmails());
    }

    @Test
    public void testSendingEmailsWaitingToSendIsThreadSafe() throws Exception {
        int NUMBER_OF_ENTITIES_TO_GENERATE = 10;
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
                List<PersonEmail> toSave = IntStream.range(0, NUMBER_OF_ENTITIES_TO_GENERATE)
                        .mapToObj(ordinal -> create(expectedSendCount.get()+ordinal))
                        .collect(Collectors.toList());
                repository.saveAll(toSave);
                expectedSendCount.addAndGet(toSave.size());
                return true;
            }
        }

        try {
            int failures = 0;
            int NUMBER_OF_READ_WRITE_TASKS = 10000;
            for (int i = 0; i < NUMBER_OF_READ_WRITE_TASKS; ++i) {
                int lastRead = actualSendCount.get();
                executorService.submit(new WriteToDBTask()).get();
                List<Future<Boolean>> futures = executorService.invokeAll(Arrays.asList(
                        new ReadFromDBTask(),
                        new ReadFromDBTask()
                ));
                for (Future<Boolean> future : futures) {
                    future.get();
                }
                if (NUMBER_OF_ENTITIES_TO_GENERATE != actualSendCount.get() - lastRead) {
                    ++failures;
                }
            }
            Assert.assertEquals("For " + NUMBER_OF_READ_WRITE_TASKS + " tests there where " + failures + " failures." +
                    " Expected number of send emails: " + expectedSendCount.get() + "; Actual number of send emails: " + actualSendCount.get(), 0, failures);
        } finally {
            executorService.shutdownNow();
            if(!executorService.awaitTermination(3, TimeUnit.SECONDS)){
                throw new AssertionError("Timout elapsed while waiting for test executor termination");
            }
        }
    }

    private PersonEmail create(int id){
        PersonEmail email = new PersonEmail();
        email.setPersonId(id);
        email.setName("IRRELEVANT");
        email.setEmailSent(false);
        return email;
    }
}
