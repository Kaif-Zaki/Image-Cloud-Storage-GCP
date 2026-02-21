import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [files, setFiles] = useState([])
  const [uploading, setUploading] = useState(false)
  const [uploadMessage, setUploadMessage] = useState('')
  const [uploadStatus, setUploadStatus] = useState(null)
  const [images, setImages] = useState([])
  const [loadingImages, setLoadingImages] = useState(false)
  const [selectedImages, setSelectedImages] = useState(new Set())
  const [failedImages, setFailedImages] = useState(new Set())

  const API_BASE_URL = 'http://localhost:8080/api/v1/images'

  // Handle file selection
  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files)
    setFiles(selectedFiles)
  }

  // Upload single or multiple files
  const handleUpload = async () => {
    if (files.length === 0) {
      setUploadMessage('Please select at least one file')
      setUploadStatus('error')
      return
    }

    setUploading(true)
    setUploadMessage('Uploading files...')
    setUploadStatus('info')

    try {
      const formData = new FormData()
      files.forEach((file) => {
        formData.append('images', file)
      })

      const response = await fetch(`${API_BASE_URL}/batch`, {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        const errorData = await response.text()
        console.error('Upload error response:', response.status, errorData)
        throw new Error(`Upload failed: ${response.status} ${response.statusText}`)
      }

      const data = await response.json()
      setUploadMessage(
        `Successfully uploaded ${data.successCount} out of ${data.totalFiles} files`
      )
      setUploadStatus('success')
      setFiles([])

      // Refresh image list
      fetchImages()

      // Reset file input
      const fileInput = document.querySelector('input[type="file"]')
      if (fileInput) fileInput.value = ''
    } catch (error) {
      console.error('Upload error:', error)
      setUploadMessage(`Upload failed: ${error.message}. Make sure backend is running on ${API_BASE_URL}`)
      setUploadStatus('error')
    } finally {
      setUploading(false)
    }
  }

  // Fetch all uploaded images
  const fetchImages = async () => {
    setLoadingImages(true)
    try {
      const response = await fetch(`${API_BASE_URL}`)
      if (!response.ok) {
        const errorData = await response.text()
        console.error('Error response:', response.status, errorData)
        throw new Error(`Failed to fetch images: ${response.status} ${response.statusText}`)
      }
      const data = await response.json()
      setImages(data || [])
      setSelectedImages(new Set())
      setUploadMessage('Images loaded successfully')
      setUploadStatus('success')
      setTimeout(() => setUploadMessage(''), 3000)
    } catch (error) {
      console.error('Error fetching images:', error)
      setUploadMessage(`Failed to load images: ${error.message}. Make sure backend is running on ${API_BASE_URL}`)
      setUploadStatus('error')
    } finally {
      setLoadingImages(false)
    }
  }

  // Delete single image
  const deleteImage = async (filename) => {
    if (!window.confirm(`Are you sure you want to delete ${filename}?`)) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/${filename}`, {
        method: 'DELETE',
      })

      if (!response.ok) {
        throw new Error('Failed to delete image')
      }

      setUploadMessage(`Successfully deleted ${filename}`)
      setUploadStatus('success')
      fetchImages()
    } catch (error) {
      setUploadMessage(`Delete failed: ${error.message}`)
      setUploadStatus('error')
    }
  }

  // Delete multiple selected images
  const deleteSelectedImages = async () => {
    if (selectedImages.size === 0) {
      setUploadMessage('Please select images to delete')
      setUploadStatus('error')
      return
    }

    if (!window.confirm(`Are you sure you want to delete ${selectedImages.size} image(s)?`)) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/batch`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ filenames: Array.from(selectedImages) }),
      })

      if (!response.ok) {
        throw new Error('Failed to delete images')
      }

      const data = await response.json()
      setUploadMessage(
        `Successfully deleted ${data.successCount} out of ${data.totalFiles} images`
      )
      setUploadStatus('success')
      fetchImages()
    } catch (error) {
      setUploadMessage(`Delete failed: ${error.message}`)
      setUploadStatus('error')
    }
  }

  // Download multiple images as ZIP
  const downloadSelectedImages = async () => {
    if (selectedImages.size === 0) {
      setUploadMessage('Please select images to download')
      setUploadStatus('error')
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/batch/retrieve`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ filenames: Array.from(selectedImages) }),
      })

      if (!response.ok) {
        throw new Error('Failed to download images')
      }

      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'images.zip'
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)

      setUploadMessage('Images downloaded successfully')
      setUploadStatus('success')
    } catch (error) {
      setUploadMessage(`Download failed: ${error.message}`)
      setUploadStatus('error')
    }
  }

  // Toggle image selection
  const toggleImageSelection = (filename) => {
    const newSelected = new Set(selectedImages)
    if (newSelected.has(filename)) {
      newSelected.delete(filename)
    } else {
      newSelected.add(filename)
    }
    setSelectedImages(newSelected)
  }

  // Handle image load error
  const handleImageError = (filename) => {
    setFailedImages((prev) => new Set([...prev, filename]))
    console.error(`Failed to load image: ${filename}`)
  }

  // Initialize - fetch images on component mount
  useEffect(() => {
    fetchImages()
  }, [])

  return (
    <div className="app-container">
      <h1>Cloud Image Uploader</h1>

      {/* Upload Section */}
      <div className="section">
        <h2>Upload Images</h2>
        <div className="upload-area">
          <input
            type="file"
            multiple
            onChange={handleFileChange}
            accept="image/*"
            id="fileInput"
          />
          <label htmlFor="fileInput" className="file-label">
            {files.length === 0
              ? 'Click to select images or drag and drop'
              : `${files.length} file(s) selected`}
          </label>
        </div>

        {files.length > 0 && (
          <div className="selected-files">
            <h3>Selected Files:</h3>
            <ul>
              {files.map((file, index) => (
                <li key={index}>
                  {file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)
                </li>
              ))}
            </ul>
          </div>
        )}

        <button
          onClick={handleUpload}
          disabled={files.length === 0 || uploading}
          className="btn btn-primary"
        >
          {uploading ? 'Uploading...' : 'Upload'}
        </button>
      </div>

      {/* Message Display */}
      {uploadMessage && (
        <div className={`message message-${uploadStatus}`}>
          {uploadMessage}
        </div>
      )}

      {/* Images Gallery Section */}
      <div className="section">
        <div className="gallery-header">
          <h2>Uploaded Images</h2>
          <button
            onClick={fetchImages}
            disabled={loadingImages}
            className="btn btn-secondary"
          >
            {loadingImages ? 'Loading...' : 'Refresh'}
          </button>
        </div>

        {images.length === 0 ? (
          <p className="no-images">No images uploaded yet</p>
        ) : (
          <>
            <div className="action-buttons">
              {selectedImages.size > 0 && (
                <>
                  <button
                    onClick={downloadSelectedImages}
                    className="btn btn-info"
                  >
                    Download ({selectedImages.size})
                  </button>
                  <button
                    onClick={deleteSelectedImages}
                    className="btn btn-danger"
                  >
                    Delete ({selectedImages.size})
                  </button>
                </>
              )}
            </div>

            <div className="gallery-grid">
              {images.map((filename) => (
                <div
                  key={filename}
                  className={`image-card ${selectedImages.has(filename) ? 'selected' : ''} ${failedImages.has(filename) ? 'failed' : ''}`}
                >
                  <input
                    type="checkbox"
                    checked={selectedImages.has(filename)}
                    onChange={() => toggleImageSelection(filename)}
                    className="image-checkbox"
                  />
                  {failedImages.has(filename) ? (
                    <div className="image-thumbnail image-error">
                      <div className="error-message">
                        <span>⚠️</span>
                        <p>Failed to load</p>
                      </div>
                    </div>
                  ) : (
                    <img
                      src={`${API_BASE_URL}/${filename}`}
                      alt={filename}
                      className="image-thumbnail"
                      onError={() => handleImageError(filename)}
                    />
                  )}
                  <div className="image-info">
                    <p className="image-name">{filename}</p>
                    <button
                      onClick={() => deleteImage(filename)}
                      className="btn btn-small btn-danger"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default App
