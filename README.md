# 🔐 Java Password Manager

A secure password manager built with **Java 21** featuring strong AES encryption and JWT authentication. Store, manage, and access your passwords securely through a beautiful web interface.

## ✨ Features

### 🔒 Security Features
- **User Authentication**: Secure login/registration with JWT tokens (24-hour expiry)
- **AES-256 Encryption**: Military-grade encryption for stored passwords
- **SHA-256 Password Hashing**: User passwords are hashed with salt
- **Secure Decryption**: Passwords are decrypted only when viewing (for authenticated users)

### 🎨 User Interface
- **Modern Design**: Beautiful, responsive interface with gradient backgrounds
- **Mobile Responsive**: Works perfectly on desktop, tablet, and mobile devices
- **Intuitive Icons**: FontAwesome icons for better user experience
- **Real-time Updates**: Instant UI updates when managing passwords

### 🛠 Password Management
- **Add Passwords**: Store passwords for different websites/services
- **Edit Passwords**: Modify existing password entries
- **Delete Passwords**: Remove passwords you no longer need
- **Search Function**: Quickly find passwords by site name or username
- **Password Generator**: Generate strong, random passwords
- **Copy to Clipboard**: One-click copying of usernames and passwords
- **Password Visibility**: Toggle between hidden and visible passwords

## 🏗 Technology Stack

- **Backend**: Java 21 with built-in HTTP server
- **Authentication**: JWT (JSON Web Tokens) + SHA-256 password hashing
- **Encryption**: AES-256-CBC for password encryption
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Storage**: JSON files (no external database required)
- **Icons**: FontAwesome 6

## 📦 Quick Start

### Prerequisites
- **Java 21** (or newer) - [Download here](https://www.oracle.com/java/technologies/downloads/)

### Installation

1. **Download/Clone** the project to your computer
2. **Run the application**:
   
   **On Windows:**
   ```cmd
   run.bat
   ```
   
   **On Linux/Mac:**
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

3. **Open your browser** and go to: `http://localhost:3000`

That's it! No additional dependencies or installations required.

## 🎯 How to Use

### Getting Started
1. **Register**: Create a new account with a username and password
2. **Login**: Sign in with your credentials
3. **Start Managing**: Add, edit, and organize your passwords

### Adding Passwords
1. Fill in the "Add New Password" form:
   - **Website/Service**: Name of the site (e.g., "Gmail", "Facebook")
   - **Username/Email**: Your login username or email
   - **Password**: Your password (or generate a strong one)
2. Click **"Generate"** for a strong random password
3. Click **"Save Password"** to store it securely

### Managing Passwords
- **View**: Click on the dots (••••••••••••) to reveal a password
- **Copy**: Use the copy buttons to copy username or password to clipboard
- **Edit**: Click the edit icon to modify password details
- **Delete**: Click the trash icon to remove a password (with confirmation)
- **Search**: Use the search box to find specific passwords

## 🔧 Technical Details

### Security Implementation

#### Password Encryption Process
When you save a password:
1. **Input**: You enter "mySecretPassword123"
2. **AES-256 Encryption**: Server encrypts using AES-256-CBC with random IV
3. **Storage**: Encrypted text like "dH7cF3x8R9..." is stored in JSON file
4. **Decryption**: When viewing, server decrypts back to readable text

#### User Authentication
- User passwords are hashed with SHA-256 + random salt
- JWT tokens contain user ID and expire after 24 hours
- Each user can only access their own encrypted passwords

### Project Structure
```
Password Manager/
├── PasswordManagerServer.java  # Main HTTP server
├── AuthService.java           # Authentication & JWT handling
├── EncryptionService.java     # AES encryption/decryption
├── PasswordService.java       # Password CRUD operations
├── run.bat                    # Windows run script
├── run.sh                     # Linux/Mac run script
├── data/                      # Auto-created data folder
│   ├── users.json            # User accounts (hashed passwords)
│   └── passwords.json        # Encrypted password entries
└── public/                   # Frontend files
    ├── index.html            # Main HTML page
    ├── styles.css            # CSS styling
    └── script.js             # JavaScript functionality
```

## 🛡 Security Features

### What Makes It Secure?
- **No plain text storage**: Passwords are encrypted before saving
- **Individual user isolation**: Users can only see their own data
- **Session management**: JWT tokens prevent unauthorized access
- **Random encryption**: Each password encrypted with unique IV
- **Memory security**: Encryption key generated fresh each session

### Encryption Details
- **Algorithm**: AES-256-CBC (Advanced Encryption Standard)
- **Key Size**: 256-bit encryption key
- **IV**: Random 16-byte initialization vector per password
- **Encoding**: Base64 encoding for storage

## 🚀 Advanced Usage

### Manual Compilation (if needed)
```bash
javac -cp . PasswordManagerServer.java AuthService.java EncryptionService.java PasswordService.java
java -cp . PasswordManagerServer
```

### Customization
- **Change port**: Modify `PORT` constant in `PasswordManagerServer.java`
- **JWT secret**: Update `JWT_SECRET` for production use
- **Token expiry**: Modify expiration time in `AuthService.java`

## 🆘 Troubleshooting

### Common Issues
- **"java command not found"**: Install Java 21 and restart terminal
- **"Compilation failed"**: Ensure you're using Java 11 or newer
- **"Port already in use"**: Change PORT in PasswordManagerServer.java
- **"Permission denied"** (Linux/Mac): Run `chmod +x run.sh`

### Verify Java Installation
```bash
java --version
```
Should show Java 21 or newer.

### Reset Everything
To start fresh:
1. Stop the server (Ctrl+C)
2. Delete the `data` folder
3. Restart the application

## 🔄 Why Java Over Node.js?

### Advantages of This Java Version:
- **No external dependencies**: Uses only built-in Java libraries
- **Better security**: Strong typing and memory management
- **Cross-platform**: Runs on Windows, Mac, Linux
- **Simple deployment**: Single executable with no package managers
- **Production ready**: Enterprise-grade security and performance

## 📋 Requirements

- **Java 21 or newer** (LTS recommended)
- **Modern Web Browser** (Chrome, Firefox, Safari, Edge)
- **4MB disk space** (for the application)

## 🎉 Features Demo

### Encryption in Action
```
Original Password: "MyGmail@2024!"
↓ (AES-256 Encryption)
Stored in File: "xK8mP9LqR3vN2zC7..."
↓ (AES-256 Decryption when viewing)
Displayed: "MyGmail@2024!"
```

### Authentication Flow
```
1. Register → SHA-256 hash stored
2. Login → Verify hash, generate JWT
3. Access → Validate JWT, allow operations
4. Logout → Token expires automatically
```

---


**Key Benefits:**
- ✅ **Zero external dependencies** 
- ✅ **Military-grade encryption**
- ✅ **Cross-platform compatibility**
- ✅ **Professional security practices**
- ✅ **Beautiful, responsive interface**

**Get Started:**
1. Run `run.bat` (Windows) or `./run.sh` (Linux/Mac)
2. Open `http://localhost:3000`
3. Create your account and start securing your passwords!


**Happy password managing! 🔐** 
