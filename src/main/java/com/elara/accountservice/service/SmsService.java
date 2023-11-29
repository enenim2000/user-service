package com.elara.accountservice.service;

import com.elara.accountservice.dto.request.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendMessage(NotificationRequest notification) {
        log.info("SMS messaging not yet supported.");
    }

}