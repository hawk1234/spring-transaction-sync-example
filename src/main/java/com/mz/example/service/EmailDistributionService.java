package com.mz.example.service;

import com.mz.example.db.model.PersonEmail;
import com.mz.example.db.repository.PersonEmailRepository;
import com.mz.example.service.exception.SendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailDistributionService {

    @Autowired
    private PersonEmailRepository repository;
    @Autowired
    private SendEmailService sendEmailService;

    @Transactional
    public int sendEmails() {
        List<PersonEmail> toSend = repository.findByEmailSentFalse();
        toSend = toSend.stream().filter(this::trySend).collect(Collectors.toList());
        repository.saveAll(toSend);
        return toSend.size();
    }

    private boolean trySend(PersonEmail personEmail){
        try {
            sendEmailService.send(personEmail);
            personEmail.setEmailSent(true);
            return true;
        } catch (SendEmailException ex) {
            //It is important to know that this service
            // won't skip any emails and return reliable values for the testing
            throw new RuntimeException("Unable to send email", ex);
        }
    }
}
