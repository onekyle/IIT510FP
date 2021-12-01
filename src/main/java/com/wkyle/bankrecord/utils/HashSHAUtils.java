package com.wkyle.bankrecord.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashSHAUtils {
    public static String toMD5(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plainText.getBytes());

        byte byteData[] = md.digest();

        // convert the byte to hex format method 1
        StringBuffer sbHash = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sbHash.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

//        System.out.println("Hash password created => " + sbHash.toString());
        System.out.println(sbHash.toString().length());
        return sbHash.toString();
    }

    public static void main(String[] args) {
        try {
            System.out.println(toMD5("123456"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}