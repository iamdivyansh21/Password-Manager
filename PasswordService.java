import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PasswordService {
    private static final String PASSWORDS_FILE = "data/passwords.json";
    private final EncryptionService encryptionService;
    
    public PasswordService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }
    
    public String getUserPasswords(String userId) throws Exception {
        List<Password> passwords = loadPasswords();
        List<Password> userPasswords = passwords.stream()
                .filter(p -> p.userId.equals(userId))
                .collect(Collectors.toList());
        
        // Decrypt passwords for display
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < userPasswords.size(); i++) {
            Password p = userPasswords.get(i);
            String decryptedPassword = encryptionService.decrypt(p.password);
            
            json.append("  {\n");
            json.append("    \"id\": \"").append(p.id).append("\",\n");
            json.append("    \"site\": \"").append(p.site).append("\",\n");
            json.append("    \"username\": \"").append(p.username).append("\",\n");
            json.append("    \"password\": \"").append(decryptedPassword).append("\",\n");
            json.append("    \"createdAt\": \"").append(p.createdAt).append("\"\n");
            json.append("  }");
            if (i < userPasswords.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        
        return json.toString();
    }
    
    public void addPassword(String userId, String site, String username, String password) throws Exception {
        List<Password> passwords = loadPasswords();
        
        String encryptedPassword = encryptionService.encrypt(password);
        
        Password newPassword = new Password();
        newPassword.id = String.valueOf(System.currentTimeMillis());
        newPassword.userId = userId;
        newPassword.site = site;
        newPassword.username = username;
        newPassword.password = encryptedPassword;
        newPassword.createdAt = Instant.now().toString();
        
        passwords.add(newPassword);
        savePasswords(passwords);
    }
    
    public boolean updatePassword(String userId, String passwordId, String site, String username, String password) throws Exception {
        List<Password> passwords = loadPasswords();
        
        for (Password p : passwords) {
            if (p.id.equals(passwordId) && p.userId.equals(userId)) {
                p.site = site;
                p.username = username;
                p.password = encryptionService.encrypt(password);
                p.updatedAt = Instant.now().toString();
                savePasswords(passwords);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean deletePassword(String userId, String passwordId) throws Exception {
        List<Password> passwords = loadPasswords();
        int initialSize = passwords.size();
        
        passwords.removeIf(p -> p.id.equals(passwordId) && p.userId.equals(userId));
        
        if (passwords.size() < initialSize) {
            savePasswords(passwords);
            return true;
        }
        
        return false;
    }
    
    private List<Password> loadPasswords() throws Exception {
        List<Password> passwords = new ArrayList<>();
        if (!Files.exists(Paths.get(PASSWORDS_FILE))) {
            return passwords;
        }
        
        String content = Files.readString(Paths.get(PASSWORDS_FILE));
        if (content.trim().isEmpty() || content.trim().equals("[]")) {
            return passwords;
        }
        
        // Simple JSON parsing for passwords array
        content = content.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
            if (!content.isEmpty()) {
                String[] passwordObjects = content.split("\\},\\s*\\{");
                for (String passwordObj : passwordObjects) {
                    passwordObj = passwordObj.trim();
                    if (!passwordObj.startsWith("{")) passwordObj = "{" + passwordObj;
                    if (!passwordObj.endsWith("}")) passwordObj = passwordObj + "}";
                    
                    Password password = parsePassword(passwordObj);
                    if (password != null) {
                        passwords.add(password);
                    }
                }
            }
        }
        
        return passwords;
    }
    
    private Password parsePassword(String json) {
        try {
            Password password = new Password();
            password.id = extractStringValue(json, "id");
            password.userId = extractStringValue(json, "userId");
            password.site = extractStringValue(json, "site");
            password.username = extractStringValue(json, "username");
            password.password = extractStringValue(json, "password");
            password.createdAt = extractStringValue(json, "createdAt");
            password.updatedAt = extractStringValue(json, "updatedAt");
            return password;
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
    
    private void savePasswords(List<Password> passwords) throws Exception {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < passwords.size(); i++) {
            Password password = passwords.get(i);
            json.append("  {\n");
            json.append("    \"id\": \"").append(password.id).append("\",\n");
            json.append("    \"userId\": \"").append(password.userId).append("\",\n");
            json.append("    \"site\": \"").append(password.site).append("\",\n");
            json.append("    \"username\": \"").append(password.username).append("\",\n");
            json.append("    \"password\": \"").append(password.password).append("\",\n");
            json.append("    \"createdAt\": \"").append(password.createdAt).append("\"");
            if (password.updatedAt != null && !password.updatedAt.isEmpty()) {
                json.append(",\n    \"updatedAt\": \"").append(password.updatedAt).append("\"");
            }
            json.append("\n  }");
            if (i < passwords.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        
        Files.write(Paths.get(PASSWORDS_FILE), json.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    public static class Password {
        public String id;
        public String userId;
        public String site;
        public String username;
        public String password;
        public String createdAt;
        public String updatedAt;
    }
} 