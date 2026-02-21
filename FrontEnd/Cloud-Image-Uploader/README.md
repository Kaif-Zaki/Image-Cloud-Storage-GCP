# Cloud Image Uploader - Frontend

A modern React-based frontend application for uploading, managing, and downloading images to a cloud storage backend (Google Cloud Storage).

## Features

✨ **Multiple File Upload** - Upload single or multiple image files at once
🖼️ **Image Gallery** - View all uploaded images in a responsive grid
📥 **Batch Download** - Download multiple selected images as a ZIP file
🗑️ **Delete Management** - Delete individual images or multiple selected images at once
✅ **Selection Checkboxes** - Select/deselect images for batch operations
📱 **Responsive Design** - Works seamlessly on desktop, tablet, and mobile devices
🎨 **Modern UI** - Beautiful gradient design with smooth animations

## Tech Stack

- **React 19.2.0** - UI framework
- **Vite 7.3.1** - Build tool and dev server
- **JavaScript (ES6+)** - Modern JavaScript
- **CSS3** - Advanced styling with gradients and animations

## Installation

### Prerequisites

- Node.js (v16 or higher)
- npm (v7 or higher)

### Setup

1. Navigate to the frontend directory:
```bash
cd FrontEnd/Cloud-Image-Uploader
```

2. Install dependencies:
```bash
npm install
```

## Development

### Start the Development Server

```bash
npm run dev
```

The application will be available at `http://localhost:5173` (or the URL shown in your terminal).

### Build for Production

```bash
npm run build
```

This creates an optimized production build in the `dist` folder.

### Preview Production Build

```bash
npm run preview
```

### Lint Code

```bash
npm run lint
```

## API Configuration

The frontend connects to the backend API at:
```
http://localhost:8080/api/v1/images
```

If your backend is running on a different host/port, update the `API_BASE_URL` constant in `src/App.jsx`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1/images'
```

## How to Use

### Upload Images

1. Click on the upload area or drag and drop image files
2. Select one or multiple image files from your device
3. Review the selected files list
4. Click the "Upload" button
5. Wait for the upload to complete
6. Success message will show the number of uploaded files

### View Images

- All uploaded images appear in the gallery below
- Images are displayed in a responsive grid
- Hover over an image to see animations and options

### Download Images

1. Select one or multiple images by checking their checkboxes
2. Click the "Download (n)" button
3. A ZIP file containing all selected images will be downloaded

### Delete Images

- **Single Delete**: Click the "Delete" button on an individual image card
- **Batch Delete**: 
  1. Select multiple images using checkboxes
  2. Click the "Delete (n)" button
  3. Confirm the deletion

### Refresh Gallery

Click the "Refresh" button to reload the list of uploaded images.

## API Endpoints Used

### Upload Multiple Images
- **POST** `/batch`
- **Multipart Form Data**: `images` (file field, can be multiple)
- **Response**: `{ totalFiles, successCount, failedCount, results }`

### Get All Images
- **GET** `/`
- **Response**: Array of filenames

### Download Multiple Images
- **POST** `/batch/retrieve`
- **Body**: `{ filenames: [...] }`
- **Response**: ZIP file

### Delete Image
- **DELETE** `/{filename}`
- **Response**: 204 No Content

### Delete Multiple Images
- **DELETE** `/batch`
- **Body**: `{ filenames: [...] }`
- **Response**: `{ totalFiles, successCount, failedCount, results }`

## Project Structure

```
src/
├── App.jsx          # Main application component
├── App.css          # Application styles
├── main.jsx         # React entry point
├── index.css        # Global styles
└── assets/          # Static assets
```

## Key Components

### App Component

The main component that handles:
- File selection and uploading
- Image gallery display
- Image selection and bulk operations
- Error handling and user feedback

**State Management:**
- `files` - Selected files for upload
- `uploading` - Upload progress state
- `uploadMessage` - User feedback messages
- `uploadStatus` - Message type (success/error/info)
- `images` - List of uploaded image filenames
- `loadingImages` - Gallery loading state
- `selectedImages` - Set of selected image filenames

## Styling

The application uses modern CSS3 features:
- **Gradients** - Linear gradients for backgrounds and text
- **Flexbox & Grid** - Responsive layout
- **Transitions & Animations** - Smooth interactions
- **Media Queries** - Mobile-first responsive design

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Environment Variables

Currently, no environment variables are required. However, to make the API URL configurable:

1. Create a `.env` file in the root directory:
```
VITE_API_BASE_URL=http://localhost:8080/api/v1/images
```

2. Update `src/App.jsx`:
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1/images'
```

## Troubleshooting

### Connection Refused
- Ensure the backend server is running on `http://localhost:8080`
- Check the `API_BASE_URL` in `src/App.jsx`
- Check your firewall settings

### CORS Issues
- Ensure the backend has CORS enabled for your frontend URL
- Add appropriate CORS headers in the backend

### Images Not Loading
- Check the backend server logs
- Verify the image filenames are correct
- Ensure the images are actually stored on the server

### Upload Fails
- Check file size (backend has 100MB limit per file)
- Ensure files are valid image formats
- Check backend server logs for details

## Performance Tips

- Use images with reasonable file sizes (< 10MB each)
- For batch operations with many images, consider breaking into smaller batches
- The application caches nothing; refresh fetches latest data from server

## License

This project is part of the Cloud Storage In Action application.

## Support

For issues or feature requests, contact the development team or check the project repository.

