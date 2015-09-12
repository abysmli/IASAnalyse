package net.icedeer.abysmli.iasanalyse.controller;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Li, Yuan on 12.09.15.
 * All Right reserved!
 */
public class HashGeneratorUtils {
    public static String generateMD5(String message) {
        return hashString(message, "MD5");
    }

    public static String generateSHA1(String message) {
        return hashString(message, "SHA-1");
    }

    public static String generateSHA256(String message) {
        return hashString(message, "SHA-256");
    }

    private static String hashString(String message, String algorithm) {
        MessageDigest digest;
        byte[] hashedBytes = null;

        try {
            digest = MessageDigest.getInstance(algorithm);
            hashedBytes = digest.digest(message.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            Log.e("Hash Algorithm Error", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("Hash Encode Error", e.toString());
        }

        return convertByteArrayToHexString(hashedBytes);
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
