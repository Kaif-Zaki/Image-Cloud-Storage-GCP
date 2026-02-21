# Quick Start Guide - Cloud Image Uploader Frontend

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Node.js installed (v16+)
- npm installed
- Backend server running on `http://localhost:8080`

### Step 1: Install Dependencies
```bash
cd FrontEnd/Cloud-Image-Uploader
npm install
```

### Step 2: Start Development Server
```bash
npm run dev
```

You'll see output like:
```
  VITE v7.3.1  ready in 123 ms

  ➜  Local:   http://localhost:5173/
  ➜  press h to show help
```

### Step 3: Open in Browser
Click the link or go to `http://localhost:5173`

### Step 4: Start Using
- Select images to upload
- Click Upload
- View gallery below
- Select images and download or delete

## 📋 Available Commands

| Command | What it does |
|---------|------------|
| `npm run dev` | Start development server (recommended for development) |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build |
| `npm run lint` | Check code quality |

## 🔧 Configuration

If your backend is NOT running on `http://localhost:8080`, edit `src/App.jsx` line 13:

```javascript
const API_BASE_URL = 'http://YOUR_BACKEND_URL:PORT/api/v1/images'
```

For example:
```javascript
const API_BASE_URL = 'http://192.168.1.100:8080/api/v1/images'
```

## 📱 What You Can Do

✅ Upload 1 or more images at once  
✅ View all uploaded images in a gallery  
✅ Download selected images as ZIP  
✅ Delete single images  
✅ Delete multiple images at once  
✅ Responsive design for all devices  

## ✨ Features

- **Drag & Drop** - Drag images onto the upload area
- **Batch Operations** - Upload, download, or delete multiple files at once
- **Real-time Feedback** - Get instant success/error messages
- **Beautiful UI** - Modern gradient design with animations
- **Mobile Friendly** - Works great on phones and tablets

## 🐛 Troubleshooting

### "Cannot connect to backend"
1. Check if backend server is running: `http://localhost:8080`
2. Update `API_BASE_URL` in `src/App.jsx` if needed
3. Check firewall settings

### "Dependencies not found"
Run: `npm install` again

### "Port 5173 already in use"
Vite will automatically use the next available port. Check your terminal output.

### "Images not showing in gallery"
1. Refresh the page (click Refresh button)
2. Check browser console (F12) for errors
3. Verify images exist on backend

## 📚 Learn More

- [React Documentation](https://react.dev)
- [Vite Documentation](https://vite.dev)
- [MDN Web Docs](https://developer.mozilla.org)

## 💡 Tips

1. **Large Files**: The backend supports up to 100MB per file. For faster uploads, use smaller images.

2. **Multiple Uploads**: You can upload multiple files at once. Just select them all and click Upload.

3. **Batch Download**: Select multiple images, click Download, and they'll be zipped automatically.

4. **Performance**: Images load from the backend API. The gallery caches nothing, so refresh to see new uploads immediately.

## 🎯 Next Steps

1. ✅ Install dependencies
2. ✅ Start development server
3. ✅ Open in browser
4. ✅ Upload test images
5. ✅ Test all features
6. ✅ Customize if needed
7. ✅ Build for production when ready

---

Happy uploading! 🎉

