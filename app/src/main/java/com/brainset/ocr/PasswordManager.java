package com.brainset.ocr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
    public static String hashPass(String passToHash){
        String genPass = "";
        try
        {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(passToHash.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            genPass = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return genPass;
    }

    public boolean isValidPass(String password){
        if (password.length() <= 8){
            return false;
        }
        char[] chars = password.toCharArray();
        int digitCheck = 0, charCheck = 0;

        for (int i = 0; i < chars.length; i++){
            if (Character.isDigit(chars[i])){
                digitCheck = 1;
            }
            if (Character.isAlphabetic(chars[i])){
                charCheck = 1;
            }
        }
        if (digitCheck == 1 && charCheck == 1){
            return true;
        }

        return false;
    }
}
