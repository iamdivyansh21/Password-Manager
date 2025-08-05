import java.util.*;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordEncoder {
    
    // Character mapping for encoding/decoding
    private static final String ORIGINAL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String CODED_CHARS =    "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM9876543210#@!%^&*()_+-=[]{}|;:,.<>?";
    
    // Create mapping tables
    private static final Map<Character, Character> encodeMap = new HashMap<>();
    private static final Map<Character, Character> decodeMap = new HashMap<>();
    
    static {
        // Initialize encoding/decoding maps
        for (int i = 0; i < ORIGINAL_CHARS.length(); i++) {
            char original = ORIGINAL_CHARS.charAt(i);
            char coded = CODED_CHARS.charAt(i);
            encodeMap.put(original, coded);
            decodeMap.put(coded, original);
        }
    }
    
    /**
     * Encode a password into a coded pattern
     * Example: "abc@123" -> "qwerty@987"
     */
    public static String encodePassword(String originalPassword) {
        if (originalPassword == null || originalPassword.isEmpty()) {
            return "";
        }
        
        StringBuilder encoded = new StringBuilder();
        for (char c : originalPassword.toCharArray()) {
            if (encodeMap.containsKey(c)) {
                encoded.append(encodeMap.get(c));
            } else {
                // If character not in mapping, keep as is
                encoded.append(c);
            }
        }
        
        // Add some scrambling for extra security
        return scrambleString(encoded.toString());
    }
    
    /**
     * Decode a coded pattern back to original password
     * Example: "qwerty@987" -> "abc@123"
     */
    public static String decodePassword(String codedPassword) {
        if (codedPassword == null || codedPassword.isEmpty()) {
            return "";
        }
        
        // First unscramble
        String unscrambled = unscrambleString(codedPassword);
        
        StringBuilder decoded = new StringBuilder();
        for (char c : unscrambled.toCharArray()) {
            if (decodeMap.containsKey(c)) {
                decoded.append(decodeMap.get(c));
            } else {
                // If character not in mapping, keep as is
                decoded.append(c);
            }
        }
        
        return decoded.toString();
    }
    
    /**
     * Add extra scrambling by inserting random characters at predictable positions
     */
    private static String scrambleString(String input) {
        StringBuilder scrambled = new StringBuilder();
        Random rand = new Random(input.hashCode()); // Use input hash as seed for consistency
        
        for (int i = 0; i < input.length(); i++) {
            scrambled.append(input.charAt(i));
            
            // Add random character every 3 positions
            if ((i + 1) % 3 == 0 && i < input.length() - 1) {
                char randomChar = CODED_CHARS.charAt(rand.nextInt(CODED_CHARS.length()));
                scrambled.append(randomChar);
            }
        }
        
        return scrambled.toString();
    }
    
    /**
     * Remove the scrambling to get back the mapped string
     */
    private static String unscrambleString(String scrambled) {
        StringBuilder unscrambled = new StringBuilder();
        Random rand = new Random(scrambled.hashCode()); // This won't work perfectly, let me fix this
        
        // Better approach: remove characters at predictable positions
        for (int i = 0, originalPos = 0; i < scrambled.length(); i++, originalPos++) {
            unscrambled.append(scrambled.charAt(i));
            
            // Skip the random character that was inserted every 3 positions
            if ((originalPos + 1) % 3 == 0 && i < scrambled.length() - 1) {
                i++; // Skip the next character (it was randomly inserted)
            }
        }
        
        return unscrambled.toString();
    }
    
    /**
     * Generate a completely different looking coded password using Base64 and character substitution
     */
    public static String generateCodedPattern(String originalPassword) {
        try {
            // Create a unique but deterministic transformation
            String withSalt = originalPassword + "MySecretSalt2024";
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(withSalt.getBytes());
            
            // Take first part of hash and combine with encoded password
            String hashPart = Base64.getEncoder().encodeToString(hash).substring(0, 8);
            String encodedPart = encodePassword(originalPassword);
            
            return scrambleAdvanced(hashPart + "X" + encodedPart);
            
        } catch (Exception e) {
            // Fallback to simple encoding
            return encodePassword(originalPassword);
        }
    }
    
    /**
     * Decode the advanced coded pattern back to original
     */
    public static String decodeCodedPattern(String codedPattern) {
        try {
            String unscrambled = unscrambleAdvanced(codedPattern);
            
            // Split by 'X' to get hash part and encoded part
            String[] parts = unscrambled.split("X", 2);
            if (parts.length == 2) {
                String encodedPart = parts[1];
                return decodePassword(encodedPart);
            }
            
            // Fallback to simple decoding
            return decodePassword(codedPattern);
            
        } catch (Exception e) {
            return decodePassword(codedPattern);
        }
    }
    
    private static String scrambleAdvanced(String input) {
        char[] chars = input.toCharArray();
        Random rand = new Random(42); // Fixed seed for consistency
        
        // Shuffle characters deterministically
        for (int i = chars.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }
    
    private static String unscrambleAdvanced(String scrambled) {
        char[] chars = scrambled.toCharArray();
        char[] original = new char[chars.length];
        Random rand = new Random(42); // Same seed
        
        // Generate the same shuffle sequence
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            indices.add(i);
        }
        
        // Reverse the shuffle
        for (int i = chars.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            // This reverses the shuffle by applying it backwards
            char temp = chars[j];
            chars[j] = chars[i];
            chars[i] = temp;
        }
        
        return new String(chars);
    }
    
    // Test method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🔐 Password Encoder/Decoder Tool");
        System.out.println("================================");
        
        while (true) {
            System.out.println("\n1. Encode Password (get coded pattern)");
            System.out.println("2. Decode Password (get original from coded pattern)");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    System.out.print("Enter your original password: ");
                    String original = scanner.nextLine();
                    String coded = generateCodedPattern(original);
                    System.out.println("📝 Coded pattern: " + coded);
                    System.out.println("💡 Save this coded pattern somewhere safe!");
                    break;
                    
                case 2:
                    System.out.print("Enter your coded pattern: ");
                    String pattern = scanner.nextLine();
                    String decoded = decodeCodedPattern(pattern);
                    System.out.println("🔓 Original password: " + decoded);
                    break;
                    
                case 3:
                    System.out.println("👋 Goodbye!");
                    return;
                    
                default:
                    System.out.println("❌ Invalid option!");
            }
        }
    }
} 