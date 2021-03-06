package com.edu.fpt.medtest.service.SmsService;

import com.edu.fpt.medtest.model.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final  SmsSender smsSender;


    @Autowired
    public SmsService(@Qualifier("twilio") TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
    }


    public  void  sendSms(SmsRequest smsRequest){
        smsSender.sendSms(smsRequest);
    }

    public void verifySms(SmsRequest smsRequest){
        smsSender.verifySms(smsRequest);
    }

    public void resetPassword(SmsRequest smsRequest){smsSender.resetPassword(smsRequest);}


}
