package com.edu.fpt.medtest.utils;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
    //phone number 0123456789
    public static boolean isPhoneNumber(String s)
    {
        Pattern p = Pattern.compile("^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    //only alphabet character
    public static boolean isValidUserName(String s)
    {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String k = pattern.matcher(temp).replaceAll("");
        Pattern p = Pattern.compile("^[a-z A-Z]{1,50}$");
        Matcher m = p.matcher(k);
        return (m.find() && m.group().equals(k));
    }

    //only number character
    public static boolean isValidNumber(String s)
    {
        Pattern p = Pattern.compile("^\\d+$");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }


    //isValidEmail
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

}
