import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_FILE = "data/encryption.key";
    private final SecretKey secretKey;
    
    public EncryptionService() throws Exception {
        this.secretKey = loadOrGenerateKey();
    }
    
    private SecretKey loadOrGenerateKey() throws Exception {
        if (Files.exists(Paths.get(KEY_FILE))) {
            // Load existing key
            String keyString = Files.readString(Paths.get(KEY_FILE));
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } else {
            // Generate new key and save it
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey newKey = keyGenerator.generateKey();
            
            // Save the key for future use
            String keyString = Base64.getEncoder().encodeToString(newKey.getEncoded());
            Files.createDirectories(Paths.get(KEY_FILE).getParent());
            Files.write(Paths.get(KEY_FILE), keyString.getBytes(StandardCharsets.UTF_8));
            
            return newKey;
        }
    }
    
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        
        // Generate a random IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        
        // Combine IV and cipher text
        byte[] encryptedWithIv = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedWithIv, iv.length, cipherText.length);
        
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }
    
    public String decrypt(String encryptedText) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
        
        // Extract IV and cipher text
        byte[] iv = new byte[16];
        byte[] cipherText = new byte[encryptedWithIv.length - 16];
        System.arraycopy(encryptedWithIv, 0, iv, 0, 16);
        System.arraycopy(encryptedWithIv, 16, cipherText, 0, cipherText.length);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
    }
} 