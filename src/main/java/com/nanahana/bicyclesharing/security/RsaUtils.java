package com.nanahana.bicyclesharing.security;

import lombok.Cleanup;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author nana
 * @Date 2019/5/8 20:48
 * @Description RSA非对称加密
 */
public class RsaUtils {
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
            @Cleanup InputStream inputStream = RsaUtils.class.getResourceAsStream("/enc_pri");
            byte[] data = new byte[inputStream.available()];
            if (inputStream.read(data) != -1) {
                PRIVATE_KEY = new String(data);
            }
            if (PRIVATE_KEY == null) {
                throw new Exception("读取密匙失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 私钥解密
     *
     * @param data 数据
     * @return 解码数据
     * @throws Exception 异常
     */
    public static byte[] decryptByPrivateKey(byte[] data) throws Exception {
        readKey();
        Key privateKey = makePrivateKey(PRIVATE_KEY);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data 数据
     * @param key  16位公钥
     * @return 加密的数据
     * @throws Exception 异常
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Utils.decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 引入第三方密码工具包 处理编码
     *
     * @param stored 需要加密的数据
     * @return 返回密匙
     * @throws Exception 异常
     */
    public static PrivateKey makePrivateKey(String stored) throws Exception {
        byte[] data = Base64Utils.decode(stored);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(0));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
        v2.add(DERNull.INSTANCE);
        aSN1EncodableVector.add(new DERSequence(v2));
        aSN1EncodableVector.add(new DEROctetString(data));
        ASN1Sequence seq = new DERSequence(aSN1EncodableVector);
        byte[] privateKey = seq.getEncoded("DER");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(spec);
    }
}
