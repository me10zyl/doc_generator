package com.me10zyl.doc_generator.experimental;

import cn.com.infosec.icbc.ReturnValue;
import cn.hutool.core.util.HexUtil;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SSLResearch {
    public static void main(String[] args) {
        simulateServer();
    }
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @SneakyThrows
    private static void readX509Info(PublicKey publicKey){
        CertificateFactory cert = CertificateFactory.getInstance("X.509");
        X509Certificate x509Certificate = (X509Certificate) cert.generateCertificate(new ByteArrayInputStream(publicKey.getEncoded()));
        // 输出证书信息
        System.out.println("Subject: " + x509Certificate.getSubjectDN());
        System.out.println("Issuer: " + x509Certificate.getIssuerDN());
        System.out.println("Valid from: " + x509Certificate.getNotBefore());
        System.out.println("Valid until: " + x509Certificate.getNotAfter());
    }

    @SneakyThrows
    public static void simulateServer(){
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        System.out.println(HexUtil.encodeHex(keyPair.getPublic().getEncoded()));
        System.out.println(HexUtil.encodeHex(keyPair.getPrivate().getEncoded()));
        desc(keyPair);


        PublicKey publicKey = readPublicKey("Bbb_pub.pem");
        readX509Info(keyPair.getPublic());
        PrivateKey privateKey = readPrivateKey("Bbb.pem");
        KeyPair keyPair1 = new KeyPair(publicKey, privateKey);
        desc(keyPair1);

        PublicKey publicKey2 = readPublicKey2("Bbb_pub.pem");
        PrivateKey privateKey2 = readPrivateKey2("Bbb.pem");
        KeyPair keyPair2 = new KeyPair(publicKey2, privateKey2);
        desc(keyPair2);

        PublicKey publicKey3 = readPublicKey3("Ccc.cer");
        PrivateKey privateKey3 = readPrivateKey3("Ccc.key");
        KeyPair keyPair3 = new KeyPair(publicKey3, privateKey3);
        desc(keyPair3);

        PublicKey publicKey4 = readPublicKey4("Aaa.cer");
        PrivateKey privateKey4 = readPrivateKey4("Aaa.key");
        KeyPair keyPair4 = new KeyPair(publicKey4, privateKey4);
        desc(keyPair4);
    }

    @SneakyThrows
    private static PrivateKey readPrivateKey4(String fileName) {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] fileBytes = new byte[fis.available()];
        fis.read(fileBytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(ReturnValue.getPrivateKey(HexUtil.decodeHex(new String(fileBytes)),"11111111".toCharArray()));
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    private static PublicKey readPublicKey4(String fileName) {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(ReturnValue.getPublicKey(HexUtil.decodeHex(new String(bytes))));
        return keyFactory.generatePublic(publicKeySpec);
    }

    @SneakyThrows
    private static PrivateKey readPrivateKey3(String fileName) {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(HexUtil.decodeHex(new String(bytes)));
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    private static PublicKey readPublicKey3(String fileName) {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] hex = new byte[fis.available()];
        fis.read(hex);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(HexUtil.decodeHex(new String(hex)));
        return keyFactory.generatePublic(publicKeySpec);
    }

    private static void desc(KeyPair keyPair) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] bytes = cipher.doFinal("123456".getBytes());

        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] bytes1 = cipher.doFinal(bytes);
        System.out.println(new String(bytes1));
    }

    private static PublicKey readPublicKey(String fileName) throws Exception {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] fileBytes = new byte[fis.available()];
        fis.read(fileBytes);
        fis.close();

        String string = new String(fileBytes);
        String src = string.replaceAll("---.+(\r\n)?", "").replaceAll("(\r)?\n", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(src);


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    private static PrivateKey readPrivateKey(String fileName) throws Exception {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        byte[] fileBytes = new byte[fis.available()];
        fis.read(fileBytes);
        fis.close();

        String string = new String(fileBytes);
        String src = string.replaceAll("---.+(\r\n)?", "").replaceAll("(\r)?\n", "");
        byte[] privateKeyBytes = Base64.getDecoder().decode(src);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    private static PublicKey readPublicKey2(String fileName) throws Exception {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        PemReader pemReader = new PemReader(new InputStreamReader(fis));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pemReader.readPemObject().getContent());
        return keyFactory.generatePublic(publicKeySpec);
    }

    private static PrivateKey readPrivateKey2(String fileName) throws Exception {
        InputStream fis = SSLResearch.class.getClassLoader().getResourceAsStream(fileName);
        PemReader pemReader = new PemReader(new InputStreamReader(fis));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent());
        return keyFactory.generatePrivate(privateKeySpec);
    }
}
