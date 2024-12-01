package com.gs.encrypt;

public class CribEncryption {

	static byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
	static int iterations = 1000;
	static int keySize = 256;
	static int blockSize = 128;
	
	public String encryptorSHA(String strInputText, String password) {
		
		if(strInputText == null){
            return null;
        }
		EncryptionUtil encryptionUtil = new EncryptionUtil();
		return encryptionUtil.encryptAES(strInputText, password, salt, iterations, keySize, blockSize);
	}
	
	public String decryptorSHA(String encryptedText, String password) {
		
		if(encryptedText == null){
            return null;
        }
		EncryptionUtil encryptionUtil = new EncryptionUtil();
		return encryptionUtil.decryptAES(encryptedText, password, salt, iterations, keySize, blockSize);
	}
	
	  public static void main(String[] args) { 
		  CribEncryption cribEncryption = new CribEncryption();
		  String encryptStr = cribEncryption.encryptorSHA("db2inst1", "asdf-9kjh-qwe56");
		  System.out.println("Encryptation: " + encryptStr);
		  String decryptStr = cribEncryption.decryptorSHA("CfBqYty6pYEFB4Zilihg2w==", "asdf-9kjh-qwe56");
		  System.out.println("Decryptation: " + decryptStr);
	  }
	  
}
