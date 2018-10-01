package cn.jiiiiiin.vplus.security.cypher;

import android.util.Base64;
import android.util.Log;


import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cn.jiiiiiin.vplus.security.exception.SecurityCypherException;

/**
 * @author jiiiiiin
 */
public class CSIICypher implements Cypher {
    private String degree = "S";
    private int rc4keyLength = 16;
    private int rc4FakeDataLength = 512;
    private static Map plainMap;
    private static Provider scProvider = Security.getProvider("SC");

    static {
        if (scProvider == null) {
            Security.addProvider(new BouncyCastleProvider());
            scProvider = Security.getProvider("SC");
            if (scProvider == null) {
                Log.e("", "cannot find SC JCE Providor");
            }
        }

        plainMap = new HashMap();
    }

    private CSIICypher() {
    }

    public static CSIICypher newInstance() {
        return new CSIICypher();
    }

    public String checkLevel(String name) {
        if (name != null && !name.trim().equals("") && plainMap != null) {
            String plain = (String) plainMap.get(name);
            if (plain == null) {
                return "W";
            } else {
                Pattern p = Pattern.compile("^((.)*[a-zA-Z]+(.)*[0-9]+(.)*)|((.)*[0-9]+(.)*[a-zA-Z]+(.)*)$");
                Matcher m = p.matcher(plain);
                return m.find() ? "S" : "W";
            }
        } else {
            return "W";
        }
    }

    public int getPasswordLength(String name) {
        if (name != null && !name.trim().equals("") && plainMap != null) {
            String plain = (String) plainMap.get(name);
            return plain == null ? 0 : plain.length();
        } else {
            return 0;
        }
    }

    public void deleteLastPwdChar(String name) {
        this.putChar(name, "delete");
    }

    public synchronized void putChar(String name, String str) {
        if (name != null && !name.trim().equals("") && str != null && !str.equals("")) {
            String plain = (String) plainMap.get(name);
            if (plain == null) {
                plain = "";
                plainMap.put(name, plain);
            }

            if (!str.equals("ok")) {
                if (str.equals("delete")) {
                    if (plain.length() <= 1) {
                        plain = "";
                    } else {
                        plain = plain.substring(0, plain.length() - 1);
                    }
                } else if (str.equals("clear")) {
                    plain = "";
                } else {
                    plain = plain + str;
                }

                plainMap.put(name, plain);
            }
        }
    }

    public synchronized void clearChar(String name) {
        plainMap.put(name, "");
    }

    public String encrypt(String plainorname, String modulus, String timestamp, String encoding, int flag) throws SecurityCypherException {
        String res = this.encryptWithoutRemove(plainorname, modulus, timestamp, encoding, flag);
        if (flag == 1) {
            plainMap.remove(plainorname);
        }
        return res;
    }

    public String encryptWithoutRemove(String plainorname, String modulus, String timestamp, String encoding, int flag) throws SecurityCypherException {
        String res = null;
        String plain = plainorname;
        if (flag == 1) {
            if (plainorname == null) {
                throw new SecurityCypherException("password_name_is_null");
            }

            plain = (String) plainMap.get(plainorname);
        }

        if (modulus == null) {
            throw new SecurityCypherException("modulus_is_null");
        } else if (plain == null) {
            throw new SecurityCypherException("plain_is_null");
        } else if (timestamp == null) {
            throw new SecurityCypherException("timestamp_is_null");
        } else {
            try {
                String e = this.degree + timestamp + ":" + plain;
                byte[] plainBytes = e.getBytes(encoding);
                byte[] rc4key = new byte[this.rc4keyLength];
                SecureRandom secRan = new SecureRandom();
                secRan.nextBytes(rc4key);
                byte[] rc4cyphered = this.rc4Encrypt(plainBytes, rc4key);
                byte[] rc4keycyphered = this.rsaEncrypt(rc4key, modulus);
                byte[] inversersacyphered = this.inverseBytes(rc4keycyphered);
                byte[] finaldata = this.generateFinal(inversersacyphered, rc4cyphered);
                res = Base64.encodeToString(finaldata, 2);
                return res;
            } catch (Exception var16) {
                throw new SecurityCypherException(var16);
            }
        }
    }

    /**
     * @param plaintext 明文
     * @param modulus   公钥
     * @param timestamp 时间戳
     * @param encoding  编码格式
     * @param flag
     * @return
     * @throws SecurityCypherException
     */
    public String csiiEncryptPlainText(String plaintext, String modulus, String timestamp, String encoding, int flag) throws SecurityCypherException {
        String res = null;
        if (modulus == null) {
            throw new SecurityCypherException("modulus_is_null");
        } else if (plaintext == null) {
            throw new SecurityCypherException("plain_is_null");
        } else if (timestamp == null) {
            throw new SecurityCypherException("timestamp_is_null");
        } else {
            try {
                String e = this.degree + timestamp + ":" + plaintext;
                byte[] plainBytes = e.getBytes(encoding);
                byte[] rc4key = new byte[this.rc4keyLength];
                SecureRandom secRan = new SecureRandom();
                secRan.nextBytes(rc4key);
                byte[] rc4cyphered = this.rc4Encrypt(plainBytes, rc4key);
                byte[] rc4keycyphered = this.rsaEncrypt(rc4key, modulus);
                byte[] inversersacyphered = this.inverseBytes(rc4keycyphered);
                byte[] finaldata = this.generateFinal(inversersacyphered, rc4cyphered);
                res = Base64.encodeToString(finaldata, 2);
                return res;
            } catch (Exception var16) {
                throw new SecurityCypherException(var16);
            }
        }
    }

    private byte[] rc4Encrypt(byte[] plain, byte[] key) throws SecurityCypherException {
        try {
            Cipher exception = Cipher.getInstance("RC4", scProvider);
            SecretKeySpec tmpKey = new SecretKeySpec(key, "RC4");
            exception.init(1, tmpKey);
            byte[] fakeDataIn = new byte[this.rc4FakeDataLength];
            byte[] fakeDataOut = new byte[this.rc4FakeDataLength];
            exception.update(fakeDataIn);
            byte[] dest = exception.doFinal(plain);
            return dest;
        } catch (Exception var8) {
            throw new SecurityCypherException(var8);
        }
    }

    private byte[] rsaEncrypt(byte[] plain, String modulus) throws SecurityCypherException {
        try {
            Cipher exception = Cipher.getInstance("RSA/ECB/PKCS1Padding", scProvider);
            PublicKey key = this.getPublicKey(modulus, "10001");
            exception.init(1, key);
            byte[] dest = exception.doFinal(plain);
            return dest;
        } catch (Exception var6) {
            throw new SecurityCypherException(var6);
        }
    }

    private PublicKey getPublicKey(String modulus, String exp) throws SecurityCypherException {
        try {
            BigInteger exception = new BigInteger(modulus, 16);
            BigInteger e = new BigInteger(exp, 16);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(exception, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception var8) {
            throw new SecurityCypherException(var8);
        }
    }

    private byte[] inverseBytes(byte[] org) {
        byte[] dest = new byte[org.length];

        for (int i = 0; i < org.length; ++i) {
            dest[i] = org[org.length - 1 - i];
        }

        return dest;
    }

    private byte[] generateFinal(byte[] part1, byte[] part2) throws SecurityCypherException {
        try {
            int exception = 20 + part1.length + 8 + part2.length;
            byte[] finalbytes = new byte[exception];
            String part1len = String.format("%1$08d", new Object[]{Integer.valueOf(12 + part1.length)});
            byte[] part1bytes = part1len.getBytes("utf-8");
            String part2len = String.format("%1$08d", new Object[]{Integer.valueOf(part2.length)});
            byte[] part2bytes = part2len.getBytes("utf-8");
            int k = 0;

            int i;
            for (i = 0; i < 8; ++i) {
                finalbytes[k++] = part1bytes[i];
            }

            finalbytes[k++] = 1;
            finalbytes[k++] = 2;
            finalbytes[k++] = 0;
            finalbytes[k++] = 0;
            finalbytes[k++] = 1;
            finalbytes[k++] = 104;
            finalbytes[k++] = 0;
            finalbytes[k++] = 0;
            finalbytes[k++] = 0;
            finalbytes[k++] = -92;
            finalbytes[k++] = 0;
            finalbytes[k++] = 0;

            for (i = 0; i < part1.length; ++i) {
                finalbytes[k++] = part1[i];
            }

            for (i = 0; i < 8; ++i) {
                finalbytes[k++] = part2bytes[i];
            }

            for (i = 0; i < part2.length; ++i) {
                finalbytes[k++] = part2[i];
            }

            return finalbytes;
        } catch (Exception var11) {
            throw new SecurityCypherException(var11);
        }
    }

    public String encryptCommon(String plain, String modulus) throws Exception {
        String plainValue = (String) plainMap.get(plain);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PublicKey key = this.getPublicKey(modulus, "10001");
        cipher.init(1, key);
        byte[] dest = cipher.doFinal(plainValue.getBytes("UTF-8"));
        return Base64.encodeToString(dest, 2);
    }

}
