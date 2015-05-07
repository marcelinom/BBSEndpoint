package com.brazilboatshare.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Criptografa {
	private Cipher desCipher;
	private SecretKey myDesKey;

	public Criptografa() {
		try {
			myDesKey = KeyGenerator.getInstance("DES").generateKey();
			// Create the cipher
			desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public byte[] encripta(String senha) {
		try {
			// Initialize the cipher for encryption
			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
			return desCipher.doFinal(senha.getBytes());
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String desencripta(byte[] senhaCriptografada) {
		try {
			// Initialize the same cipher for decryption
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
			return new String(desCipher.doFinal(senhaCriptografada));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String geraSHACodigo(int tamanho) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			SecureRandom rnd = new SecureRandom();
			byte bytes[] = new byte[tamanho];
			rnd.nextBytes(bytes);
			md.update(bytes);
			return encodeHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String geraSubString(String chave) {
		if (chave != null) {
			int tam = chave.length();
			if (tam > 10) {
				int ordem = chave.charAt(tam/3)%2;
				int seq = Math.max(2, chave.charAt(tam/2)%4);
				StringBuffer buff = new StringBuffer();
				if (ordem == 0) {
					for (int i=0; i<tam;i=i+seq) {
						buff.append(chave.charAt(i));
					}
				} else {
					for (int i=tam-1; i>=0;i=i-seq) {
						buff.append(chave.charAt(i));
					}
				}
				return buff.toString();
			}
		}
		return null;
	}

	public static String encodeHexString(byte[] bytes) {
		BigInteger bi = new BigInteger(1, bytes);
		return String.format("%0" + (bytes.length << 1) + "X", bi);
	}

	// codigos abaixo extraidos de http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
	public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt) {
		// Encrypt the clear-text password using the same salt that was used to
		// encrypt the original password
		byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
		// Authentication succeeds if encrypted password that the user entered
		// is equal to the stored hash
		return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

	public static byte[] getEncryptedPassword(String password, byte[] salt) {
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for
		// PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = 160;
		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		// iOS 4.x reportedly uses 10,000:
		// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
		int iterations = 20000;

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations,	derivedKeyLength);
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
			return f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateSalt() {
		try {
			// VERY important to use SecureRandom instead of just Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
			byte[] salt = new byte[8];
			random.nextBytes(salt);

			return salt;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
