import java.util.*;

public class SimplePasswordCoder {
    
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
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🔐 Simple Password Encoder/Decoder");
        System.out.println("==================================");
        System.out.println();
        
        while (true) {
            System.out.println("1. 🔒 Encode Password (Simple)");
            System.out.println("2. 🔓 Decode Password (Simple)");
            System.out.println("3. 🔐 Encode Password (Advanced)");
            System.out.println("4. 🔍 Decode Password (Advanced)");
            System.out.println("5. 🧪 Test with Example");
            System.out.println("6. ❌ Exit");
            System.out.println();
            System.out.print("Choose option (1-6): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        System.out.print("\n💭 Enter your original password: ");
                        String original1 = scanner.nextLine();
                        String coded1 = encodePassword(original1);
                        System.out.println("📝 Coded pattern: " + coded1);
                        System.out.println("💡 Write this down and keep it safe!");
                        System.out.println();
                        break;
                        
                    case 2:
                        System.out.print("\n🔍 Enter your coded pattern: ");
                        String pattern1 = scanner.nextLine();
                        String decoded1 = decodePassword(pattern1);
                        System.out.println("🔓 Original password: " + decoded1);
                        System.out.println();
                        break;
                        
                    case 3:
                        System.out.print("\n💭 Enter your original password: ");
                        String original2 = scanner.nextLine();
                        String coded2 = advancedEncode(original2);
                        System.out.println("📝 Advanced coded pattern: " + coded2);
                        System.out.println("💡 This is more secure - write it down!");
                        System.out.println();
                        break;
                        
                    case 4:
                        System.out.print("\n🔍 Enter your advanced coded pattern: ");
                        String pattern2 = scanner.nextLine();
                        String decoded2 = advancedDecode(pattern2);
                        System.out.println("🔓 Original password: " + decoded2);
                        System.out.println();
                        break;
                        
                    case 5:
                        System.out.println("\n🧪 Testing with example password 'abc@123':");
                        String test = "abc@123";
                        String simpleEncoded = encodePassword(test);
                        String advancedEncoded = advancedEncode(test);
                        
                        System.out.println("Original: " + test);
                        System.out.println("Simple coded: " + simpleEncoded);
                        System.out.println("Advanced coded: " + advancedEncoded);
                        System.out.println();
                        System.out.println("Decoding back:");
                        System.out.println("Simple decoded: " + decodePassword(simpleEncoded));
                        System.out.println("Advanced decoded: " + advancedDecode(advancedEncoded));
                        System.out.println();
                        break;
                        
                    case 6:
                        System.out.println("\n👋 Goodbye! Keep your coded passwords safe!");
                        return;
                        
                    default:
                        System.out.println("\n❌ Invalid option! Please choose 1-6.");
                        System.out.println();
                }
            } catch (Exception e) {
                System.out.println("\n❌ Invalid input! Please enter a number.");
                scanner.nextLine(); // clear invalid input
                System.out.println();
            }
        }
    }
} 