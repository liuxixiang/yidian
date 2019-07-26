package com.linken.newssdk.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class EncryptUtil {
    private static final String TAG = EncryptUtil.class.getSimpleName();

    public EncryptUtil() {
    }

    /**
     * This method encrypt the user name and password. //the password should be
     * encrypt as this // credit = username + md5(password); // credit =
     * sha1(credit) 1000 times
     *
     * @param original
     * @return
     */
    public static String generateMD5(String original) {
        return generateMD5(original.getBytes());
    }

    public static String generateMD5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            return generateMD5(bos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateMD5(byte[] bytes) {
        byte[] tmpDigest = null;
        try {
            tmpDigest = MessageDigest.getInstance("MD5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "MD5 aglorithm not support");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Generate digest failed.");
            return null;
        }

        return bytes2HexString(tmpDigest);
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex;
        }
        return ret;
    }

    public static String generateCRC32(String original) {
        if (original == null || original.length() < 1) {
            return null;
        }
        CRC32 crc32 = new CRC32();
        crc32.update(original.getBytes());
        long v = crc32.getValue();
        return Long.toHexString(v);
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("byte array must not be null");
        }
        StringBuffer hex = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }

    public static String getMD5_32(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            if (messageDigest == null)
                return "";
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
