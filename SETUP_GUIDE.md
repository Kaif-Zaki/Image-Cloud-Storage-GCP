# Cloud Image Uploader - Complete Setup Guide

## 📋 Table of Contents
1. [Backend Setup](#backend-setup)
2. [Frontend Setup](#frontend-setup)
3. [Running Together](#running-together)
4. [Build for Production](#build-for-production)
5. [Troubleshooting](#troubleshooting)

---

## Backend Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Steps

1. **Navigate to project root:**
```bash
cd /Users/kaifzaki/Developer/Projects/AWS/Project
```

2. **Build the backend:**
```bash
./mvnw clean package
```

3. **Run the backend:**
```bash
./mvnw spring-boot:run
```

Or run the JAR file directly after building:
```bash
java -jar target/cloud-storage-in-action-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
```
Started CloudStorageInActionApplication in X.XXX seconds
```

The backend will be running at: `http://localhost:8080`

### API Documentation

Backend endpoints available:

- **GET** `/api/v1/images` - List all uploaded images
- **GET** `/api/v1/images/{filename}` - Download single image
- **POST** `/api/v1/images` - Upload single image
- **POST** `/api/v1/images/batch` - Upload multiple images
- **DELETE** `/api/v1/images/{filename}` - Delete single image
- **DELETE** `/api/v1/images/batch` - Delete multiple images
- **POST** `/api/v1/images/batch/retrieve` - Download multiple images as ZIP
- **POST** `/api/v1/images/batch/info` - Get metadata for multiple images

---

## Frontend Setup

### Prerequisites
- Node.js v16+
- npm v7+

### Steps

1. **Navigate to frontend directory:**
```bash
cd FrontEnd/Cloud-Image-Uploader
```

2. **Install dependencies:**
```bash
npm install
```

3. **Start development server:**
```bash
npm run dev
```

**Expected Output:**
```
  VITE v7.3.1  ready in 123 ms

  ➜  Local:   http://localhost:5173/
  ➜  press h to show help
```

The frontend will be available at: `http://localhost:5173`

### Available npm Scripts

```bash
npm run dev       # Start development server with hot reload
npm run build     # Build for production
npm run preview   # Preview production build locally
npm run lint      # Run ESLint
```

---

## Running Together

### Method 1: Two Terminal Windows (Recommended for Development)

**Terminal 1 - Backend:**
```bash
cd /Users/kaifzaki/Developer/Projects/AWS/Project
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd /Users/kaifzaki/Developer/Projects/AWS/Project/FrontEnd/Cloud-Image-Uploader
npm run dev
```

### Method 2: Build Frontend and Serve with Backend

1. **Build the frontend:**
```bash
cd FrontEnd/Cloud-Image-Uploader
npm run build
```

This creates a `dist` folder with optimized production files.

2. **Copy to backend resources (optional for Spring Boot serving):**
```bash
cp -r dist/* ../../src/main/resources/static/
```

3. **Build and run backend:**
```bash
cd /Users/kaifzaki/Developer/Projects/AWS/Project
./mvnw clean package
java -jar target/cloud-storage-in-action-0.0.1-SNAPSHOT.jar
```

Access at: `http://localhost:8080`

---

## Build for Production

### Frontend Build

1. **Navigate to frontend:**
```bash
cd FrontEnd/Cloud-Image-Uploader
```

2. **Build:**
```bash
npm run build
```

3. **Verify build:**
```bash
npm run preview
```

**Output Structure:**
```
dist/
├── index.html
├── assets/
│   ├── index-*.js
│   └── index-*.css
└── vite.svg
```

### Backend Build

1. **Navigate to project root:**
```bash
cd /Users/kaifzaki/Developer/Projects/AWS/Project
```

2. **Build:**
```bash
./mvnw clean package -DskipTests
```

**Output:**
```
target/cloud-storage-in-action-0.0.1-SNAPSHOT.jar
```

### Deployment

**Option 1: Docker Container**
- Create Dockerfile for backend
- Build Docker image
- Run container with proper volume mounts for file storage

**Option 2: Cloud Platform (AWS, GCP, Azure)**
- Deploy backend as microservice
- Serve frontend from CDN or storage
- Configure API endpoints appropriately

**Option 3: Traditional Server**
- Copy JAR to server
- Copy frontend dist to web server (nginx, Apache)
- Configure reverse proxy
- Update API URL in frontend

---

## Configuration

### Backend Configuration

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: cloud-storage-in-action
  servlet:
    multipart:
      max-file-size: 100MB        # Max size per file
      max-request-size: 100MB     # Max size per request

gcp:
  project-id: your-project-id
  credentials-location: /path/to/credentials.json
  bucket-id: your-bucket-id
```

### Frontend Configuration

Edit `src/App.jsx` line 13:

```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1/images'
```

For production, use your actual backend URL:
```javascript
const API_BASE_URL = 'https://api.yourdomain.com/api/v1/images'
```

---

## Environment Variables

### Backend (Optional - for production)
Create `.env` file in project root:
```
GOOGLE_APPLICATION_CREDENTIALS=/path/to/credentials.json
GCP_PROJECT_ID=your-project-id
GCP_BUCKET_ID=your-bucket-id
```

### Frontend (Optional - for flexibility)

1. Create `.env` file in `FrontEnd/Cloud-Image-Uploader`:
```
VITE_API_BASE_URL=http://localhost:8080/api/v1/images
VITE_APP_NAME=Cloud Image Uploader
```

2. Update `src/App.jsx`:
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1/images'
```

3. Build uses variables automatically:
```bash
VITE_API_BASE_URL=https://api.yourdomain.com/api/v1/images npm run build
```

---

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or use different port in application.yaml:
server:
  port: 8081
```

**Connection to GCP failed:**
- Verify credentials file path
- Check GCP project permissions
- Ensure bucket exists
- Check network connectivity

**Build fails:**
```bash
./mvnw clean install -U  # Update dependencies
```

### Frontend Issues

**Port 5173 already in use:**
Vite automatically uses next available port (5174, 5175, etc.)

**Dependencies not found:**
```bash
npm install            # Reinstall
npm cache clean --force  # Clear cache
rm -rf node_modules   # Remove modules
npm install           # Fresh install
```

**API connection refused:**
1. Ensure backend is running: `curl http://localhost:8080/api/v1/images`
2. Check firewall settings
3. Update API_BASE_URL in App.jsx

**CORS errors:**
Backend needs CORS configuration. Contact backend team or check Spring Boot configuration.

### General Issues

**Clear cache and rebuild:**
```bash
# Backend
./mvnw clean

# Frontend
rm -rf node_modules dist
npm install
npm run build
```

**Check logs:**
```bash
# Backend
tail -f nohup.out

# Frontend
# Check browser console (F12 > Console tab)
```

---

## Performance Tips

1. **Use images with reasonable file sizes** (1-5MB each)
2. **Optimize images** before uploading
3. **For bulk operations**, consider batching (50 files at a time)
4. **Monitor backend logs** for slow operations
5. **Use CDN** for frontend in production

---

## Security Checklist

- [ ] Change GCP credentials file location
- [ ] Update API URLs for production
- [ ] Enable HTTPS for production
- [ ] Set up authentication/authorization
- [ ] Configure CORS properly
- [ ] Use environment variables for secrets
- [ ] Enable file type validation
- [ ] Set up rate limiting
- [ ] Regular security updates

---

## Support and Documentation

- Backend: Spring Boot, Google Cloud Storage
- Frontend: React 19, Vite 7, Vanilla CSS3
- See individual README files for more details

---

## Quick Command Reference

```bash
# Backend
cd /Users/kaifzaki/Developer/Projects/AWS/Project
./mvnw spring-boot:run              # Run backend
./mvnw clean package                # Build backend
./mvnw clean install -U             # Update dependencies

# Frontend
cd FrontEnd/Cloud-Image-Uploader
npm install                         # Install dependencies
npm run dev                         # Start dev server
npm run build                       # Build for production
npm run preview                     # Preview build
npm run lint                        # Check code quality

# Testing
curl http://localhost:8080/api/v1/images  # Test backend
curl http://localhost:5173               # Test frontend
```

---

**Last Updated:** February 2026
**Version:** 1.0

