import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.StandardCharsets;

public class PasswordManagerServer {
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "3000"));
    private static final String DATA_DIR = "data";
    private static final String JWT_SECRET = System.getenv().getOrDefault("JWT_SECRET", "your-secret-key-change-this-in-production");
    
    private HttpServer server;
    private AuthService authService;
    private EncryptionService encryptionService;
    private PasswordService passwordService;
    
    public PasswordManagerServer() throws Exception {
        this.encryptionService = new EncryptionService();
        this.authService = new AuthService(JWT_SECRET);
        this.passwordService = new PasswordService(encryptionService);
        
        // Ensure data directory exists
        Files.createDirectories(Paths.get(DATA_DIR));
        
        // Initialize data files if they don't exist
        if (!Files.exists(Paths.get(DATA_DIR + "/users.json"))) {
            Files.write(Paths.get(DATA_DIR + "/users.json"), "[]".getBytes());
        }
        if (!Files.exists(Paths.get(DATA_DIR + "/passwords.json"))) {
            Files.write(Paths.get(DATA_DIR + "/passwords.json"), "[]".getBytes());
        }
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // API endpoints
        server.createContext("/api/register", this::handleRegister);
        server.createContext("/api/login", this::handleLogin);
        server.createContext("/api/passwords", this::handlePasswords);
        server.createContext("/api/encode", this::handleEncode);
        server.createContext("/api/decode", this::handleDecode);
        
        // Static file serving
        server.createContext("/", this::handleStaticFiles);
        
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Password Manager running on http://localhost:" + PORT);
        System.out.println("Encryption key generated for this session");
    }
    
    private void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        
        try {
            String body = readRequestBody(exchange);
            String username = extractJsonValue(body, "username");
            String password = extractJsonValue(body, "password");
            
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                sendResponse(exchange, 400, "{\"error\":\"Username and password required\"}");
                return;
            }
            
            // Check if user already exists
            if (authService.userExists(username)) {
                sendResponse(exchange, 400, "{\"error\":\"User already exists\"}");
                return;
            }
            
            // Create user
            authService.createUser(username, password);
            sendResponse(exchange, 201, "{\"message\":\"User registered successfully\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
        }
    }
    
    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        
        try {
            String body = readRequestBody(exchange);
            String username = extractJsonValue(body, "username");
            String password = extractJsonValue(body, "password");
            
            if (username == null || password == null) {
                sendResponse(exchange, 400, "{\"error\":\"Username and password required\"}");
                return;
            }
            
            String token = authService.authenticate(username, password);
            if (token != null) {
                String response = String.format("{\"token\":\"%s\",\"username\":\"%s\"}", token, username);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 400, "{\"error\":\"Invalid credentials\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
        }
    }
    
    private void handlePasswords(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        // Handle CORS preflight
        if ("OPTIONS".equals(method)) {
            sendCorsResponse(exchange);
            return;
        }
        
        // Authenticate user
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendResponse(exchange, 401, "{\"error\":\"Access token required\"}");
            return;
        }
        
        String token = authHeader.substring(7);
        String userId = authService.validateToken(token);
        if (userId == null) {
            sendResponse(exchange, 403, "{\"error\":\"Invalid token\"}");
            return;
        }
        
        try {
            if ("GET".equals(method) && "/api/passwords".equals(path)) {
                // Get all passwords for user
                String passwords = passwordService.getUserPasswords(userId);
                sendResponse(exchange, 200, passwords);
                
            } else if ("POST".equals(method) && "/api/passwords".equals(path)) {
                // Add new password
                String body = readRequestBody(exchange);
                String site = extractJsonValue(body, "site");
                String username = extractJsonValue(body, "username");
                String password = extractJsonValue(body, "password");
                
                if (site == null || username == null || password == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Site, username, and password required\"}");
                    return;
                }
                
                passwordService.addPassword(userId, site, username, password);
                sendResponse(exchange, 201, "{\"message\":\"Password saved successfully\"}");
                
            } else if ("PUT".equals(method) && path.startsWith("/api/passwords/")) {
                // Update password
                String passwordId = path.substring("/api/passwords/".length());
                String body = readRequestBody(exchange);
                String site = extractJsonValue(body, "site");
                String username = extractJsonValue(body, "username");
                String password = extractJsonValue(body, "password");
                
                boolean updated = passwordService.updatePassword(userId, passwordId, site, username, password);
                if (updated) {
                    sendResponse(exchange, 200, "{\"message\":\"Password updated successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Password not found\"}");
                }
                
            } else if ("DELETE".equals(method) && path.startsWith("/api/passwords/")) {
                // Delete password
                String passwordId = path.substring("/api/passwords/".length());
                boolean deleted = passwordService.deletePassword(userId, passwordId);
                if (deleted) {
                    sendResponse(exchange, 200, "{\"message\":\"Password deleted successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Password not found\"}");
                }
                
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
        }
    }
    
    private void handleStaticFiles(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) {
            path = "/index.html";
        }
        
        String filePath = "public" + path;
        Path file = Paths.get(filePath);
        
        if (Files.exists(file) && !Files.isDirectory(file)) {
            byte[] content = Files.readAllBytes(file);
            String contentType = getContentType(filePath);
            
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, content.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        } else {
            sendResponse(exchange, 404, "File not found");
        }
    }
    
    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".json")) return "application/json";
        return "text/plain";
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\\s*\"([^\"]+)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    private void sendCorsResponse(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().close();
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    private void handleEncode(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        
        try {
            String body = readRequestBody(exchange);
            String password = extractJsonValue(body, "password");
            String method = extractJsonValue(body, "method"); // "simple" or "advanced"
            
            if (password == null) {
                sendResponse(exchange, 400, "{\"error\":\"Password required\"}");
                return;
            }
            
            String encoded;
            if ("advanced".equals(method)) {
                encoded = PasswordEncoderUtil.advancedEncode(password);
            } else {
                encoded = PasswordEncoderUtil.encodePassword(password);
            }
            
            String response = String.format("{\"encoded\":\"%s\"}", encoded);
            sendResponse(exchange, 200, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
        }
    }
    
    private void handleDecode(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        
        try {
            String body = readRequestBody(exchange);
            String encoded = extractJsonValue(body, "encoded");
            String method = extractJsonValue(body, "method"); // "simple" or "advanced"
            
            if (encoded == null) {
                sendResponse(exchange, 400, "{\"error\":\"Encoded password required\"}");
                return;
            }
            
            String decoded;
            if ("advanced".equals(method)) {
                decoded = PasswordEncoderUtil.advancedDecode(encoded);
            } else {
                decoded = PasswordEncoderUtil.decodePassword(encoded);
            }
            
            String response = String.format("{\"decoded\":\"%s\"}", decoded);
            sendResponse(exchange, 200, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
        }
    }
    
    public static void main(String[] args) {
        try {
            PasswordManagerServer server = new PasswordManagerServer();
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 