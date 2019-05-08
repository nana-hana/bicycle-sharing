package com.nanahana.bicyclesharing.security;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author nana
 * @Date 2019/5/8 20:48
 * @Description RSA非对称加密
 */
@Slf4j
public class RsaUtil {
    /**
     * 私钥字符串
     */
    private static String PRIVATE_KEY;
    /**
     * 产生密钥的算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * 加解密算法 格式：算法/工作模式/填充模式 注意：ECB不使用IV参数
     */
    private static final String KEY_ALGORITHM_MODE = "RSA/ECB/PKCS1Padding";

    /**
     * 读取密钥字符串
     */
    private static void readKey() {
        try {
            @Cleanup InputStream inputStream = RsaUtil.class.getResourceAsStream("/enc_pri");
            byte[] data = new byte[inputStream.available()];
            if (inputStream.read(data) != -1) {
                PRIVATE_KEY = new String(data);
            }
        } catch (Exception e) {
            log.error("io异常", e);
        }
        if (PRIVATE_KEY == null) {
            log.error("读取密匙失败", new Exception("Fail to retrieve key"));
        }
    }

//    /**
//     * 私钥解密
//     *
//     * @param data 数据
//     * @return 解码数据
//     * @throws Exception 异常
//     */
//    public static byte[] decryptByPrivateKey(byte[] data) throws Exception {
//        readKey();
//        Key privateKey = makePrivateKey(PRIVATE_KEY);
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        return cipher.doFinal(data);
//    }

    /**
     * 公钥加密
     *
     * @param data 数据
     * @param key  16位公钥
     * @return 加密的数据
     * @throws Exception 异常
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Util.decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

//    /**
//     * 引入第三方密码工具包 处理编码
//     *
//     * @param stored
//     * @return
//     * @throws GeneralSecurityException
//     * @throws Exception
//     */
//    public static PrivateKey makePrivateKey(String stored) throws GeneralSecurityException, Exception {
//        byte[] data = Base64Util.decode(stored);
//        ASN1EncodableVector v = new ASN1EncodableVector();
//        v.add(new ASN1Integer(0));
//        ASN1EncodableVector v2 = new ASN1EncodableVector();
//        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
//        v2.add(DERNull.INSTANCE);
//        v.add(new DERSequence(v2));
//        v.add(new DEROctetString(data));
//        ASN1Sequence seq = new DERSequence(v);
//        byte[] privKey = seq.getEncoded("DER");
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privKey);
//        KeyFactory fact = KeyFactory.getInstance("RSA");
//        PrivateKey key = fact.generatePrivate(spec);
//        return key;
//    }
}
