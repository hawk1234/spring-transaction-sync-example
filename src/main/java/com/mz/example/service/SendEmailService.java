package com.mz.example.service;

import com.mz.example.db.model.PersonEmail;
import com.mz.example.service.exception.SendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendEmailService {

    public void send(PersonEmail personEmail) throws SendEmailException {
        log.info("Email sent to: "+personEmail.getPersonId());
    }
}
