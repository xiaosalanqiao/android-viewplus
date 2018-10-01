package cn.jiiiiiin.vplus.security.utils;

import android.content.Context;
import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import cn.jiiiiiin.vplus.security.cypher.CSIICypher;
import cn.jiiiiiin.vplus.security.cypher.Cypher;
import cn.jiiiiiin.vplus.security.exception.SecurityCypherException;

/**
 * 注意：建议获取实例的时候必须制定为：
 * CSIISecurityUtil.getInstance(ViewPlus.getConfiguration(ConfigKeys.PASSWORD_MODULUS));
 * 全局唯一的securityPubKey
 *
 * @author zhaojin
 */
public class CSIISecurityUtil {

    private Cypher CYPHER;
    private String SECURITY_PUB_KEY;
    private volatile static CSIISecurityUtil INSTANCE;
    private static String ENCODING = "Utf-8";

    private CSIISecurityUtil(String securityPubKey, CSIICypher csiiCypher) {
        this.SECURITY_PUB_KEY = securityPubKey;
        this.CYPHER = csiiCypher;
    }

    private static Provider scProvider = Security.getProvider("BC");

    private static final String TAG = "CSIISecurityUtil";

    static {
        if (scProvider == null) {
            Security.addProvider(new BouncyCastleProvider());
            scProvider = Security.getProvider("BC");
            if (scProvider == null) {
                Log.e(TAG, "cannot find SC JCE Providor");
            }
        }
    }

    public static CSIISecurityUtil getInstance(String securityPubKey) {
        // !不能使用synchronized来做单例，会影响运行效率
        if (INSTANCE == null) {
            INSTANCE = new CSIISecurityUtil(securityPubKey, CSIICypher.newInstance());
        }
        return INSTANCE;
    }


    public static String base64Encode(String plian, String encoding) throws SecurityCypherException {
        String res = plian;
        byte[] pBuffer = new byte[0];
        try {
            pBuffer = plian.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SecurityCypherException(String.format("将明文转换成BASE64字符串出错[%s]", e.getMessage()), "base64encode_error");
        }
        res = Base64.encode(pBuffer);
        return res;
    }

    public static String base64Encode(String plian) throws SecurityCypherException {
        return base64Encode(plian, ENCODING);
    }

    public static String decode(String pBuffer) throws
            UnsupportedEncodingException {
        return new String(Base64.decode(pBuffer), ENCODING);
    }

    public static String decode(String pBuffer, String encoding)
            throws UnsupportedEncodingException {
        try {
            return new String(Base64.decode(pBuffer), encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return pBuffer;
        }
    }

    /**
     * 参数需要加密的时候使用
     *
     * @param plainStr
     * @param timestamp
     * @return
     * @throws SecurityCypherException
     */
    public String encrypt(String plainStr, String timestamp) throws SecurityCypherException {
        return CYPHER.encrypt(plainStr, this.SECURITY_PUB_KEY,
                timestamp, ENCODING, Cypher.EncryptType_Plain);
    }

    public String encryptWithName(String plainStr, String timestamp) throws SecurityCypherException {
        return CYPHER.encrypt(plainStr, this.SECURITY_PUB_KEY,
                timestamp, ENCODING, Cypher.EncryptType_WithName);
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥解密过程
     *
     * @param privateKey
     * @param b64Input
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchProviderException
     */
    public static String deCrypt(Key privateKey, String b64Input)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        byte[] raw = Base64.decode(b64Input);
        int keyLen = Integer.parseInt(new String(raw, 0, 8).trim());
        byte[] keyBytes = new byte[keyLen - 12];
        System.arraycopy(raw, 20, keyBytes, 0, keyLen - 12);
        int dataLen = Integer.parseInt(new String(raw, 8 + keyLen, 8).trim());
        byte[] dataBytes = new byte[dataLen];
        System.arraycopy(raw, 8 + keyLen + 8, dataBytes, 0, dataLen);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", scProvider);
        cipher.init(2, privateKey);
        byte[] rKeyBytes = new byte[128];
        for (int i = 0; i < 128; ++i) {
            rKeyBytes[(127 - i)] = keyBytes[i];
        }
        byte[] key = cipher.doFinal(rKeyBytes);
        SecretKeySpec tmpKey = new SecretKeySpec(key, "RC4");
        Cipher cipherrc = Cipher.getInstance("RC4");
        cipherrc.init(2, tmpKey);
        cipherrc.update(new byte[512]);
        byte[] plainText = cipherrc.doFinal(dataBytes);
        return new String(plainText);
    }

//    public static void main(String[] args) throws
//            UnsupportedEncodingException, SecurityCypherException {
//        String plain = "编译";
//        String res = CSIISecurityUtil.base64Encode(plain);
//        System.out.println(res);
//        System.out.println("-----");
//        System.out.println(CSIISecurityUtil.decode(res));
//    }
}
