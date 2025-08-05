# 🚀 Deployment Guide

This guide shows you how to host your Java Password Manager online using various platforms.

## 📋 Prerequisites

1. **Git installed** - [Download here](https://git-scm.com/download/windows)
2. **GitHub account** - [Sign up here](https://github.com)
3. **Your code pushed to GitHub** (see instructions below)

## 🔧 Step 1: Push to GitHub

### Install Git and Upload Your Code

```bash
# 1. Navigate to your project folder
cd "C:\Users\2417962\OneDrive - Cognizant\Desktop\Remember Passwords"

# 2. Initialize repository
git init

# 3. Add all files
git add .

# 4. Commit files
git commit -m "Initial commit: Java Password Manager with Encoder/Decoder"

# 5. Create repository on GitHub (go to github.com, click "New repository")
# 6. Add GitHub repository (replace with your actual repo URL)
git remote add origin https://github.com/yourusername/password-manager.git

# 7. Push to GitHub
git push -u origin main
```

---

## 🌐 Hosting Options

### Option 1: GitHub Codespaces (Easiest - Free)

**✅ Best for: Testing and development**

1. **Push code to GitHub** (see above)
2. **Go to your GitHub repository**
3. **Click "Code" → "Codespaces" → "Create codespace"**
4. **Wait for environment to load**
5. **In the terminal, run:**
   ```bash
   java -cp . PasswordManagerServer
   ```
6. **Click on "Ports" tab**
7. **Find port 3000 and click the globe icon**
8. **Your password manager is now live!**

**Pros:** ✅ Free, ✅ Easy setup, ✅ No configuration needed  
**Cons:** ❌ Stops when inactive, ❌ Not permanent hosting

---

### Option 2: Railway (Recommended - Free)

**✅ Best for: Production hosting**

1. **Go to [railway.app](https://railway.app)**
2. **Sign up with GitHub**
3. **Click "New Project" → "Deploy from GitHub repo"**
4. **Select your password-manager repository**
5. **Railway will automatically detect Java and deploy**
6. **Get your public URL and share it!**

**Pros:** ✅ Free tier, ✅ Custom domains, ✅ Always online, ✅ HTTPS included  
**Cons:** ❌ May need credit card for verification

---

### Option 3: Render (Free Tier)

**✅ Best for: Small projects**

1. **Go to [render.com](https://render.com)**
2. **Sign up with GitHub**
3. **Click "New" → "Web Service"**
4. **Connect your GitHub repository**
5. **Settings:**
   - **Build Command:** `javac -cp . *.java`
   - **Start Command:** `java -cp . PasswordManagerServer`
6. **Deploy and get your URL!**

---

### Option 4: Heroku (Popular)

**✅ Best for: Scalable applications**

1. **Install Heroku CLI** - [Download here](https://devcenter.heroku.com/articles/heroku-cli)
2. **Login to Heroku:**
   ```bash
   heroku login
   ```
3. **Create Heroku app:**
   ```bash
   heroku create your-password-manager
   ```
4. **Set environment variables:**
   ```bash
   heroku config:set JWT_SECRET=your-super-secret-key-here
   ```
5. **Deploy:**
   ```bash
   git push heroku main
   ```

---

## 🔒 Production Security Settings

### Environment Variables to Set:

For any hosting platform, set these environment variables:

```bash
JWT_SECRET=your-very-long-secret-key-here-at-least-32-characters
PORT=3000
```

### Example for Railway:
1. Go to your project dashboard
2. Click "Variables"
3. Add: `JWT_SECRET` = `your-secret-key-here-make-it-long-and-random`

---

## 🌍 Accessing Your Live Website

Once deployed, you'll get a URL like:
- **Railway:** `https://your-app-name.up.railway.app`
- **Render:** `https://your-app-name.onrender.com`
- **Heroku:** `https://your-app-name.herokuapp.com`

## 🔧 Custom Domain (Optional)

Most platforms allow custom domains:
1. **Buy a domain** (e.g., mypasswordmanager.com)
2. **Add custom domain** in your hosting platform
3. **Update DNS settings** as instructed

---

## 📱 Mobile Access

Once hosted, your password manager will work on:
- ✅ **Desktop browsers**
- ✅ **Mobile phones** 
- ✅ **Tablets**
- ✅ **Any device with internet**

---

## 🛡️ Security Considerations

### For Production Use:
1. **Change JWT_SECRET** to a long, random string
2. **Use HTTPS** (most platforms provide this automatically)
3. **Regular backups** of your data folder
4. **Strong master password** for your account

### Data Storage:
- Your passwords are stored in the `data/` folder
- They're encrypted with AES-256
- Each user can only access their own data
- Coded patterns can be stored anywhere safely

---

## 🎯 Quick Start Summary

**Fastest way to get online:**

1. **Install Git** if not already installed
2. **Push to GitHub** using commands above
3. **Use GitHub Codespaces** for instant hosting
4. **Or deploy to Railway** for permanent hosting

**Your password manager will be accessible worldwide with enterprise-grade security!** 🔐🌐 