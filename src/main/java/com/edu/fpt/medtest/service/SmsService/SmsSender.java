package com.edu.fpt.medtest.service.SmsService;

import com.edu.fpt.medtest.model.SmsRequest;
import org.springframework.stereotype.Service;

@Service
public interface SmsSender {
    void  sendSms(SmsRequest smsRequest);
}
