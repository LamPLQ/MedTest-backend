package com.edu.fpt.medtest.service.SmsService;

import com.edu.fpt.medtest.configuration.TwilioConfiguration;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.entity.ValidPhoneToken;
import com.edu.fpt.medtest.model.SmsRequest;
import com.edu.fpt.medtest.repository.TokenRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.GetRandomString;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service("twilio")
public class TwilioSmsSender implements SmsSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioSmsSender.class);
    private final TwilioConfiguration twilioConfiguration;

    public TwilioSmsSender(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void sendSms(SmsRequest smsRequest) {
       // if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {

            boolean resetPassword = true;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

            Date inputDate;

            //phoneNumber format 0xxxxxxxxx (input type)
            String phoneNumberInput = smsRequest.getPhoneNumber();

            //phoneNumber format +84xxxxxxxxxx (valid type)
            String mainNumberInput = phoneNumberInput.substring(1);
            String validPhoneNumber = "+84".concat(mainNumberInput);

            //Generate token
            String token = GetRandomString.getNumberString(6);

            //save token to database
            ValidPhoneToken validPhoneToken = new ValidPhoneToken();
            validPhoneToken.setPhoneNumber(phoneNumberInput);
            validPhoneToken.setToken(token);
            validPhoneToken.setCreatedTime(System.currentTimeMillis());
            validPhoneToken.setExpiredTime(System.currentTimeMillis() + 180000);
            //tokenRepository.save(validPhoneToken);

            //System.out.printf("valid Phone Token expire time after save:"+ validPhoneToken.getCreatedTime()+ "\n");
            //System.out.println("createdtime" + validPhoneToken.getCreatedTime());
            //System.out.println("expireTime" + validPhoneToken.getExpiredTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date createdTime = new Date(validPhoneToken.getCreatedTime());
            Date expiredTime = new Date(validPhoneToken.getExpiredTime());
            //System.out.println("createdtime" + createdTime);
            //System.out.println("expireTime" + expiredTime);

            //String CreatedTime
            String createdTimeRaw = createdTime.toString();
            String cutTime = createdTimeRaw.substring(createdTimeRaw.length() - 17);
            String messageTime = cutTime.substring(0, cutTime.length() - 9);
            //System.out.println("MessageTime '" + messageTime + "'");

            PhoneNumber phoneNumberTo = new PhoneNumber(validPhoneNumber);
            PhoneNumber phoneNumberfrom = new PhoneNumber(twilioConfiguration.getTrialNumber());
            String message = "[MEDTEST] Mã OTP xác nhận: " + token + ". Hãy nhập mã trong vòng 3 phút kể từ " + messageTime + ".";

            try {
                MessageCreator creator = Message.creator(phoneNumberTo, phoneNumberfrom, message);
                creator.create();
                resetPassword = true;
            }catch (Exception e){
                resetPassword = false;
            }
            if(resetPassword == true){
                tokenRepository.save(validPhoneToken);
            }else {
                System.out.println("Số điện thoại không tồn tại!");
            }

            //System.out.println(message);
            //LOGGER.info("Send sms {}" + smsRequest);
        /*} else {
            throw new IllegalArgumentException("Số điện thoại [" + smsRequest.getPhoneNumber() + "] không tồn tại!");
        }*/
    }

    @Override
    public void verifySms(SmsRequest smsRequest) {
        if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {
            //phoneNumber format 0xxxxxxxxx (input type)
            String phoneNumberInput = smsRequest.getPhoneNumber();

            //phoneNumber format +84xxxxxxxxxx (valid type)
            String mainNumberInput = phoneNumberInput.substring(1);
            String validPhoneNumber = "+84".concat(mainNumberInput);

            PhoneNumber phoneNumberTo = new PhoneNumber(validPhoneNumber);
            PhoneNumber phoneNumberfrom = new PhoneNumber(twilioConfiguration.getTrialNumber());
            String message = "Bạn vừa được đăng kí thành công với hệ thống MedTest. Vui lòng liên hệ với phòng khám để lấy thông tin tài khoản. ";
            MessageCreator creator = Message.creator(phoneNumberTo, phoneNumberfrom, message);
            creator.create();
            //System.out.println(message);
            LOGGER.info("Send sms {}" + smsRequest);

        } else {
            throw new IllegalArgumentException("Số điện thoại [" + smsRequest.getPhoneNumber() + "] không tồn tại");
        }
    }

    @Override
    public void resetPassword(SmsRequest smsRequest) {
        //if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {
        //phoneNumber format 0xxxxxxxxx (input type)
        //try {
        boolean resetPassword = true;
        String phoneNumberInput = smsRequest.getPhoneNumber();

        //phoneNumber format +84xxxxxxxxxx (valid type)
        String mainNumberInput = phoneNumberInput.substring(1);
        String validPhoneNumber = "+84".concat(mainNumberInput);

        //Gen new password and save new password to db
        String newPassword = GetRandomString.getAlphaNumericString(6);
        String password = bCryptPasswordEncoder.encode(newPassword);
        //

        PhoneNumber phoneNumberTo = new PhoneNumber(validPhoneNumber);
        PhoneNumber phoneNumberfrom = new PhoneNumber(twilioConfiguration.getTrialNumber());
        String message = "Mật khẩu mới: " + newPassword + ". Vui lòng đổi sang mật khẩu mới sau khi đăng nhập! ";
        try {
            MessageCreator creator = Message.creator(phoneNumberTo, phoneNumberfrom, message);
            creator.create();
            //System.out.println(message);
            //LOGGER.info("Send sms {}" + smsRequest);
            resetPassword = true;
            //System.out.println(isPasswordChange);
        } catch (Exception e) {
            System.out.println("Loi");
            resetPassword = false;
        }
        if(resetPassword==true){
            User changePassword = userRepository.getUserByPhoneNumberAndRole(smsRequest.getPhoneNumber(), smsRequest.getRole());
            changePassword.setPassword(password);
            userService.resetPassword(changePassword);
            //System.out.println(isPasswordChange);
        }else {
            System.out.println("No update");
            ApiResponse apiResponse = new ApiResponse(false,"Số điện thoại không đúng!");
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }

        /*} else {
            throw new IllegalArgumentException("Số điện thoại [" + smsRequest.getPhoneNumber() + "] không tồn tại!");
        }*/

    }


    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: Implement phone number validator
        return true;
    }
}