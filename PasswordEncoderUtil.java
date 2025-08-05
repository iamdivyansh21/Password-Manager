import java.util.*;

public class PasswordEncoderUtil {
    
    /**
     * Simple character substitution mapping
     * Each character gets replaced with a different one
     */
    private static final Map<Character, Character> ENCODE_MAP = new HashMap<>();
    private static final Map<Character, Character> DECODE_MAP = new HashMap<>();
    
    static {
        // Simple alphabet shift + symbol mapping
        String original = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        String coded    = "zyxwvutsrqponmlkjihgfedcbaZYXWVUTSRQPONMLKJIHGFEDCBA9876543210)(*&^%$#@!";
        
        for (int i = 0; i < original.length(); i++) {
            ENCODE_MAP.put(original.charAt(i), coded.charAt(i));
            DECODE_MAP.put(coded.charAt(i), original.charAt(i));
        }
    }
    
    /**
     * Encode password to coded pattern
     * Example: "abc@123" becomes "zyx)987"
     */
    public static String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : password.toCharArray()) {
            // If character has a mapping, use it; otherwise keep original
            result.append(ENCODE_MAP.getOrDefault(c, c));
        }
        
        return result.toString();
    }
    
    /**
     * Decode coded pattern back to original password
     * Example: "zyx)987" becomes "abc@123"
     */
    public static String decodePassword(String codedPassword) {
        if (codedPassword == null || codedPassword.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : codedPassword.toCharArray()) {
            // If character has a mapping, use it; otherwise keep original
            result.append(DECODE_MAP.getOrDefault(c, c));
        }
        
        return result.toString();
    }
    
    /**
     * Advanced encoding with position-based shifts
     */
    public static String advancedEncode(String password) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            
            // Apply character mapping first
            char mapped = ENCODE_MAP.getOrDefault(c, c);
            
            // Then apply position-based shift
            int shift = (i % 3) + 1; // Shift by 1, 2, or 3 based on position
            
            if (Character.isLetter(mapped)) {
                if (Character.isLowerCase(mapped)) {
                    mapped = (char) ((mapped - 'a' + shift) % 26 + 'a');
                } else {
                    mapped = (char) ((mapped - 'A' + shift) % 26 + 'A');
                }
            } else if (Character.isDigit(mapped)) {
                mapped = (char) ((mapped - '0' + shift) % 10 + '0');
            }
            
            result.append(mapped);
        }
        
        return result.toString();
    }
    
    /**
     * Decode advanced encoded password
     */
    public static String advancedDecode(String codedPassword) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < codedPassword.length(); i++) {
            char c = codedPassword.charAt(i);
            
            // Reverse position-based shift first
            int shift = (i % 3) + 1;
            
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    c = (char) ((c - 'a' - shift + 26) % 26 + 'a');
                } else {
                    c = (char) ((c - 'A' - shift + 26) % 26 + 'A');
                }
            } else if (Character.isDigit(c)) {
                c = (char) ((c - '0' - shift + 10) % 10 + '0');
            }
            
            // Then reverse character mapping
            char original = DECODE_MAP.getOrDefault(c, c);
            result.append(original);
        }
        
        return result.toString();
    }
} 