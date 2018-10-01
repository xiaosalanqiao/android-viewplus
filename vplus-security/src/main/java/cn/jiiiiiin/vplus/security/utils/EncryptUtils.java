package cn.jiiiiiin.vplus.security.utils;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jiiiiiin on 2017/9/12.
 */

public class EncryptUtils  {
    private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";
    private static final String Algorithm = "DESede";

    public EncryptUtils() {
    }

    public static String Decrypt3DES(String value, String key) throws Exception {
        byte[] b = decryptMode(GetKeyBytes(key), Base64.decode(value));
        return new String(b);
    }

    public static String Encrypt3DES(String value, String key) throws Exception {
        String str = byte2Base64(encryptMode(GetKeyBytes(key), value.getBytes()));
        return str;
    }

    public static byte[] GetKeyBytes(String strKey) throws Exception {
        if(strKey != null && strKey.length() >= 1) {
            MessageDigest alg = MessageDigest.getInstance("MD5");
            alg.update(strKey.getBytes());
            byte[] bkey = alg.digest();
            int start = bkey.length;
            byte[] bkey24 = new byte[24];

            int i;
            for(i = 0; i < start; ++i) {
                bkey24[i] = bkey[i];
            }

            for(i = start; i < 24; ++i) {
                bkey24[i] = bkey[i - start];
            }

            return bkey24;
        } else {
            throw new Exception("key is null or empty!");
        }
    }

    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            SecretKeySpec e3 = new SecretKeySpec(keybyte, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, e3);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        } catch (NoSuchPaddingException var5) {
            var5.printStackTrace();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public static String Encrypt3DES(String value, String key, byte[] ivByte) throws Exception {
        String str = byte2Base64(encryptMode(GetKeyBytes(key), value.getBytes(), ivByte));
        return str;
    }

    public static byte[] encryptMode(byte[] keybyte, byte[] src, byte[] ivByte) {
        try {
            SecureRandom e3 = new SecureRandom();
            DESedeKeySpec spec = new DESedeKeySpec(keybyte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey sec = keyFactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            IvParameterSpec IvParameters = new IvParameterSpec(ivByte);
            cipher.init(1, sec, IvParameters, e3);
            return cipher.doFinal(src);
        } catch (NoSuchAlgorithmException var9) {
            var9.printStackTrace();
        } catch (NoSuchPaddingException var10) {
            var10.printStackTrace();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return null;
    }

    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            SecretKeySpec e3 = new SecretKeySpec(keybyte, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(2, e3);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        } catch (NoSuchPaddingException var5) {
            var5.printStackTrace();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public static String Decrypt3DES(String value, String key, byte[] ivByte) throws Exception {
        byte[] b = decryptMode(GetKeyBytes(key), Base64.decode(value), ivByte);
        return new String(b);
    }

    public static byte[] decryptMode(byte[] keybyte, byte[] src, byte[] ivByte) {
        try {
            SecureRandom e3 = new SecureRandom();
            DESedeKeySpec spec = new DESedeKeySpec(keybyte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey sec = keyFactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            IvParameterSpec IvParameters = new IvParameterSpec(ivByte);
            cipher.init(2, sec, IvParameters, e3);
            return cipher.doFinal(src);
        } catch (NoSuchAlgorithmException var9) {
            var9.printStackTrace();
        } catch (NoSuchPaddingException var10) {
            var10.printStackTrace();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return null;
    }

    public static String byte2Base64(byte[] b) {
        return Base64.encode(b);
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if(stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if(n < b.length - 1) {
                hs = hs + ":";
            }
        }

        return hs.toUpperCase();
    }

    public static int byte2Integer(byte[] b) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        int j = dis.readInt();
        return j;
    }

    public static Map<String, Object> Encrypt3DES4Map(String value, String key) throws Exception {
        HashMap ret = new HashMap();
        byte[] encryptByte = encryptMode(GetKeyBytes(key), value.getBytes());
        int intValue = byte2Integer(encryptByte);
        String strValue = byte2Base64(encryptByte);
        ret.put("intValue", Integer.valueOf(intValue));
        ret.put("strValue", strValue);
        return ret;
    }

    public static Map<String, Object> Encrypt3DES4Map(String value, String key, byte[] ivByte) throws Exception {
        HashMap ret = new HashMap();
        byte[] encryptByte = encryptMode(GetKeyBytes(key), value.getBytes(), ivByte);
        int intValue = byte2Integer(encryptByte);
        String strValue = byte2Base64(encryptByte);
        ret.put("intValue", Integer.valueOf(intValue));
        ret.put("strValue", strValue);
        return ret;
    }

    public static byte[] randomIVBytes() {
        Random ran = new Random();
        byte[] bytes = new byte[8];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)ran.nextInt(128);
        }

        return bytes;
    }
}
