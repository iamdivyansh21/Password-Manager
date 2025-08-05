import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthService {
    private static final String USERS_FILE = "data/users.json";
    private static final String ALGORITHM = "HmacSHA256";
    private final String jwtSecret;
    
    public AuthService(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
    
    public boolean userExists(String username) throws Exception {
        List<User> users = loadUsers();
        return users.stream().anyMatch(user -> user.username.equals(username));
    }
    
    public void createUser(String username, String password) throws Exception {
        List<User> users = loadUsers();
        
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        
        User newUser = new User();
        newUser.id = String.valueOf(System.currentTimeMillis());
        newUser.username = username;
        newUser.password = hashedPassword;
        newUser.salt = salt;
        newUser.createdAt = Instant.now().toString();
        
        users.add(newUser);
        saveUsers(users);
    }
    
    public String authenticate(String username, String password) throws Exception {
        List<User> users = loadUsers();
        
        User user = users.stream()
                .filter(u -> u.username.equals(username))
                .findFirst()
                .orElse(null);
        
        if (user == null) {
            return null;
        }
        
        String hashedPassword = hashPassword(password, user.salt);
        if (!hashedPassword.equals(user.password)) {
            return null;
        }
        
        return generateJWT(user.id, username);
    }
    
    public String validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            String signature = parts[2];
            
            // Verify signature
            String expectedSignature = createSignature(parts[0] + "." + parts[1]);
            if (!signature.equals(expectedSignature)) {
                return null;
            }
            
            // Check expiration
            String expStr = extractFromJson(payload, "exp");
            long exp = Long.parseLong(expStr);
            if (Instant.now().getEpochSecond() > exp) {
                return null;
            }
            
            return extractFromJson(payload, "userId");
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String generateJWT(String userId, String username) throws Exception {
        long now = Instant.now().getEpochSecond();
        long exp = now + 24 * 60 * 60; // 24 hours
        
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = String.format("{\"userId\":\"%s\",\"username\":\"%s\",\"exp\":%d}", 
                userId, username, exp);
        
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        
        String signature = createSignature(encodedHeader + "." + encodedPayload);
        
        return encodedHeader + "." + encodedPayload + "." + signature;
    }
    
    private String createSignature(String data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSecret.getBytes(), ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
    
    private String extractFromJson(String json, String key) {
        // Simple JSON value extraction - not production ready but works for our case
        String pattern = "\"" + key + "\":\"?([^,}\"]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashedBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
    
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    private List<User> loadUsers() throws Exception {
        List<User> users = new ArrayList<>();
        if (!Files.exists(Paths.get(USERS_FILE))) {
            return users;
        }
        
        String content = Files.readString(Paths.get(USERS_FILE));
        if (content.trim().isEmpty() || content.trim().equals("[]")) {
            return users;
        }
        
        // Simple JSON parsing for users array
        content = content.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
            if (!content.isEmpty()) {
                String[] userObjects = content.split("\\},\\s*\\{");
                for (String userObj : userObjects) {
                    userObj = userObj.trim();
                    if (!userObj.startsWith("{")) userObj = "{" + userObj;
                    if (!userObj.endsWith("}")) userObj = userObj + "}";
                    
                    User user = parseUser(userObj);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        }
        
        return users;
    }
    
    private User parseUser(String json) {
        try {
            User user = new User();
            user.id = extractStringValue(json, "id");
            user.username = extractStringValue(json, "username");
            user.password = extractStringValue(json, "password");
            user.salt = extractStringValue(json, "salt");
            user.createdAt = extractStringValue(json, "createdAt");
            return user;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\":\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }
    
    private void saveUsers(List<User> users) throws Exception {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            json.append("  {\n");
            json.append("    \"id\": \"").append(user.id).append("\",\n");
            json.append("    \"username\": \"").append(user.username).append("\",\n");
            json.append("    \"password\": \"").append(user.password).append("\",\n");
            json.append("    \"salt\": \"").append(user.salt).append("\",\n");
            json.append("    \"createdAt\": \"").append(user.createdAt).append("\"\n");
            json.append("  }");
            if (i < users.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        
        Files.write(Paths.get(USERS_FILE), json.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    public static class User {
        public String id;
        public String username;
        public String password;
        public String salt;
        public String createdAt;
    }
} 