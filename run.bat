@echo off
echo Compiling Java Password Manager...

javac -cp . PasswordManagerServer.java AuthService.java EncryptionService.java PasswordService.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Starting Password Manager Server...
echo.
echo Open your browser and go to: http://localhost:3000
echo Press Ctrl+C to stop the server
echo.

java -cp . PasswordManagerServer

pause 