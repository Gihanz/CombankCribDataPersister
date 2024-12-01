package com.gs.encrypt;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

public class EncryptionUtil {
	
	public static Logger log = Logger.getLogger(EncryptionUtil.class);
	
    public String encryptAES(String word, String password, byte[] salt, int iterations, int keySize, int blockSize) {
        try {
            byte[] pswd = sha256String(password, "UTF-8");
            PKCS5S2ParametersGenerator key = keyGeneration(pswd, salt, iterations);
            ParametersWithIV iv = generateIV(key, keySize, blockSize);
            BufferedBlockCipher cipher = getCipher(true, iv);
            byte[] inputText = word.getBytes("UTF-8");
            byte[] newData = new byte[cipher.getOutputSize(inputText.length)];
            int l = cipher.processBytes(inputText, 0, inputText.length, newData, 0);
            cipher.doFinal(newData, l);
            return new String(Base64.encode(newData), "UTF-8");
        } catch (UnsupportedEncodingException | IllegalStateException | DataLengthException | InvalidCipherTextException e) {
        	e.printStackTrace();
        	log.info("Error encryptAES : " +e.fillInStackTrace());
            return null;
        }
    }
    
    public String decryptAES(String word, String password, byte[] salt, int iterations, int keySize, int blockSize) {
        try {
            byte[] pswd = sha256String(password, "UTF-8");
            PKCS5S2ParametersGenerator key = keyGeneration(pswd, salt, iterations);
            ParametersWithIV iv = generateIV(key, keySize, blockSize);
            BufferedBlockCipher cipher = getCipher(false, iv);
            byte[] inputText = Base64.decode(word.getBytes());
            byte[] newData = new byte[cipher.getOutputSize(inputText.length)];
            int l = cipher.processBytes(inputText, 0, inputText.length, newData, 0);
            l += cipher.doFinal(newData, l);
            byte[] bytesDec = new byte[l];
            System.arraycopy(newData, 0, bytesDec, 0, l);            
            return new String(bytesDec);  
        } catch (IllegalStateException | DataLengthException | InvalidCipherTextException e) {
        	e.printStackTrace();
        	log.info("Error decryptAES : " +e.fillInStackTrace());
            return null;
        }
    }

    public BufferedBlockCipher getCipher(boolean encrypt, ParametersWithIV iv) {
        RijndaelEngine rijndael = new RijndaelEngine();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(rijndael));
        cipher.init(encrypt, iv);
        return cipher;
    }
    
    public ParametersWithIV generateIV(PKCS5S2ParametersGenerator key, int keySize, int blockSize) {
        try {
            ParametersWithIV iv = null;
            iv = ((ParametersWithIV) key.generateDerivedParameters(keySize, blockSize));
            return iv;
        } catch (Exception e) {
        	e.printStackTrace();
        	log.info("Error generateIV : " +e.fillInStackTrace());
            return null;
        }
    }

    public PKCS5S2ParametersGenerator keyGeneration(byte[] password, byte[] salt, int iterations) {
        try {
            PKCS5S2ParametersGenerator key = new PKCS5S2ParametersGenerator();
            key.init(password, salt, iterations);
            return key;
        } catch (Exception e) {
        	e.printStackTrace();
        	log.info("Error keyGeneration : " +e.fillInStackTrace());
            return null;
        }
    }

    public byte[] sha256String(String password, Charset charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(charset));
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
        	ex.printStackTrace();
        	log.info("Error sha256String : " +ex.fillInStackTrace());
            return null;
        }
    }

    public byte[] sha256String(String password, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(charset));
            return md.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
        	ex.printStackTrace();
        	log.info("Error sha256String : " +ex.fillInStackTrace());
            return null;
        }
    }
    
}