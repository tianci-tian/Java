package com.link;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.*;

import java.io.*;
import java.lang.reflect.Field;
import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class KeyBasedFileProcessor {
	static final int BUFFER_SIZE = 16384;
	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
			Security.setProperty("crypto.policy", "limited");
			RemoveCryptographyRestrictions();
			System.out.println(" static execute");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void RemoveCryptographyRestrictions() throws Exception {
		Class<?> jceSecurity = getClazz("javax.crypto.JceSecurity");
		Class<?> cryptoPermissions = getClazz("javax.crypto.CryptoPermissions");
		Class<?> cryptoAllPermission = getClazz("javax.crypto.CryptoAllPermission");
		if (jceSecurity != null) {
			setFinalStaticValue(jceSecurity, "isRestricted", false);
			PermissionCollection defaultPolicy = (PermissionCollection)getFieldValue(jceSecurity, "defaultPolicy", (Object)null, PermissionCollection.class);
			if (cryptoPermissions != null) {
				Map<?, ?> map = (Map)getFieldValue(cryptoPermissions, "perms", defaultPolicy, Map.class);
				map.clear();
			}

			if (cryptoAllPermission != null) {
				Permission permission = (Permission)getFieldValue(cryptoAllPermission, "INSTANCE", (Object)null, Permission.class);
				defaultPolicy.add(permission);
			}
		}

	}
	private static Class<?> getClazz(String className) {
		Class clazz = null;

		try {
			clazz = Class.forName(className);
		} catch (Exception var3) {
		}

		return clazz;
	}
	private static void setFinalStaticValue(Class<?> srcClazz, String fieldName, Object newValue) throws Exception {
		Field field = srcClazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & -17);
		field.set((Object)null, newValue);
	}
	private static <T> T getFieldValue(Class<?> srcClazz, String fieldName, Object owner, Class<T> dstClazz) throws Exception {
		Field field = srcClazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return dstClazz.cast(field.get(owner));
	}
	


	public static void encryptFile(String outputFileName, String inputFileName, String encKeyFileName, String priKeyIn,
								   boolean armor, boolean withIntegrityCheck, char[] pass) throws IOException, NoSuchProviderException, PGPException {

		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName));
		PGPPublicKey encKey = readPublicKey(encKeyFileName);
		PGPSecretKey pgpSec = readSecretKey(priKeyIn);
		encryptFile(out, inputFileName, encKey, pgpSec, armor, withIntegrityCheck, pass);

		out.close();
	}

	private static void encryptFile(OutputStream out, String fileName, PGPPublicKey encKey, PGPSecretKey pgpSec,
									boolean armor, boolean withIntegrityCheck, char[] pass) throws IOException, NoSuchProviderException {

		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		try {
			byte[] bytes = compressFile(fileName, CompressionAlgorithmTags.ZIP);
			PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
					new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck)
							.setSecureRandom(new SecureRandom()).setProvider("BC"));

			// 使用Base64编码输出字节数据
			String encodedBytes = Base64.getEncoder().encodeToString(bytes);
			System.out.println("Compressed file bytes (Base64): " + encodedBytes);

			encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"));
			OutputStream encryptedOut = encGen.open(out, new byte[BUFFER_SIZE]);

			PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
			OutputStream compressedData = comData.open(encryptedOut);

			PGPPrivateKey pgpPrivKey =
					pgpSec.extractPrivateKey(
							new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
			PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
			sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
			Iterator it = pgpSec.getPublicKey().getUserIDs();
			if (it.hasNext()) {
				PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
				spGen.setSignerUserID(false, (String) it.next());
				sGen.setHashedSubpackets(spGen.generate());
			}
			sGen.generateOnePassVersion(false).encode(compressedData);

			File file = new File(fileName);
			PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
			OutputStream lOut = lGen.open(compressedData, PGPLiteralData.BINARY, file.getName(), new Date(),
					new byte[BUFFER_SIZE]);
			FileInputStream fIn = new FileInputStream(file);


			int ch;

			while ((ch = fIn.read()) >= 0) {
				lOut.write(ch);
				sGen.update((byte) ch);
			}

			fIn.close();
			lOut.close();
			lGen.close();

			sGen.generate().encode(compressedData);

			// bOut.close();
			comData.close();
			compressedData.close();

			encryptedOut.close();
			encGen.close();

			if (armor) {
				out.close();
			}
		} catch (PGPException e) {
			System.err.println(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}


	}

	static PGPPublicKey readPublicKey(String fileName) throws IOException, PGPException {
		InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
		PGPPublicKey pubKey = readPublicKey(keyIn);
		keyIn.close();
		return pubKey;
	}
	static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {
		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input),
				new JcaKeyFingerprintCalculator());

		//
		// we just loop through the collection till we find a key suitable for
		// encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//
		Iterator keyRingIter = pgpPub.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();
			Iterator keyIter = keyRing.getPublicKeys();
			while (keyIter.hasNext()) {
				PGPPublicKey key = (PGPPublicKey) keyIter.next();
				if (key.isEncryptionKey()) {
					return key;
				}
			}
		}
		throw new IllegalArgumentException("Can't find encryption key in key ring.");
	}

	static byte[] compressFile(String fileName, int algorithm) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
		PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY,
				new File(fileName));
		comData.close();
		return bOut.toByteArray();

	}

	static PGPSecretKey readSecretKey(String fileName) throws IOException, PGPException {
		InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
		PGPSecretKey pgpSecretKey = readSecretKey(keyIn);
		keyIn.close();
		return pgpSecretKey;
	}

	static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException {

		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input),
				new JcaKeyFingerprintCalculator());

		//
		// we just loop through the collection till we find a key suitable for
		// encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//
		Iterator keyRingIter = pgpSec.getKeyRings();

		while (keyRingIter.hasNext()) {
			PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
			Iterator keyIter = keyRing.getSecretKeys();
			while (keyIter.hasNext()) {
				PGPSecretKey key = (PGPSecretKey) keyIter.next();
				if (key.isSigningKey()) {
					return key;
				}
			}
		}
		throw new IllegalArgumentException("Can't find signing key in key ring.");

	}

}

