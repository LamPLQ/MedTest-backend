package com.edu.fpt.medtest.configuration;

import com.twilio.Twilio;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TwilioInitiazer {
    private  final static Logger LOGGER = LoggerFactory.getLogger(TwilioInitiazer.class);

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioInitiazer(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
        Twilio.init(
                twilioConfiguration.getAccountSid(),
                twilioConfiguration.getAuthToken()
        );
        LOGGER.info("Twillio inittialize...with account sid {}" + twilioConfiguration.getAccountSid());
    }
}
