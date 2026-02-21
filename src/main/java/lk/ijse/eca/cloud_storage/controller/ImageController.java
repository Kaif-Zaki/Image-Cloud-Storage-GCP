package lk.ijse.eca.cloud_storage.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import lk.ijse.eca.cloud_storage.service.StorageService;

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"},
             allowCredentials = "true",
             allowedHeaders = "*",
             methods = {org.springframework.web.bind.annotation.RequestMethod.GET,
                       org.springframework.web.bind.annotation.RequestMethod.POST,
                       org.springframework.web.bind.annotation.RequestMethod.DELETE,
                       org.springframework.web.bind.annotation.RequestMethod.PUT})
public class ImageController {

    private final StorageService storageService;

    public ImageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, List<String>>> upload(MultipartHttpServletRequest request) {
        try {
            List<MultipartFile> files = new ArrayList<>();
            if (request != null) {
                var multiFileMap = request.getMultiFileMap();
                if (multiFileMap != null) {
                    multiFileMap.values().forEach(list -> list.forEach(f -> { if (f != null && !f.isEmpty()) files.add(f); }));
                }
            }

            if (files.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No files provided");
            }

            List<Map<String, String>> results = storageService.uploadMultiple(files);
            List<String> savedFilenames = new ArrayList<>();
            for (Map<String, String> r : results) {
                if ("success".equals(r.get("status"))) {
                    savedFilenames.add(r.get("savedFilename"));
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filenames", savedFilenames));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> listAll() {
        return ResponseEntity.ok(storageService.listAll());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        try {
            Resource resource = storageService.load(filename);
            String contentType = URLConnection.guessContentTypeFromStream(resource.getInputStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(resource);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) {
        try {
            storageService.delete(filename);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadMultiple(MultipartHttpServletRequest request) {
        try {
            List<MultipartFile> files = new ArrayList<>();
            if (request != null) {
                var multiFileMap = request.getMultiFileMap();
                if (multiFileMap != null) {
                    multiFileMap.values().forEach(list -> list.forEach(f -> { if (f != null && !f.isEmpty()) files.add(f); }));
                }
            }

            if (files.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No files provided");
            }

            List<Map<String, String>> results = storageService.uploadMultiple(files);

            long successCount = results.stream().filter(r -> "success".equals(r.get("status"))).count();
            long failedCount = results.size() - successCount;

            Map<String, Object> response = new HashMap<>();
            response.put("totalFiles", results.size());
            response.put("successCount", successCount);
            response.put("failedCount", failedCount);
            response.put("results", results);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload multiple images: " + e.getMessage());
        }
    }

    @PostMapping("/batch/retrieve")
    public ResponseEntity<Resource> getMultipleImages(@RequestBody Map<String, List<String>> request)
            throws IOException {
        try {
            List<String> filenames = request.get("filenames");
            if (filenames == null || filenames.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No filenames provided");
            }

            Map<String, Resource> resources = storageService.loadMultiple(filenames);

            // Create a ZIP file containing all images
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (Map.Entry<String, Resource> entry : resources.entrySet()) {
                    if (entry.getValue() != null) {
                        ZipEntry zipEntry = new ZipEntry(entry.getKey());
                        zos.putNextEntry(zipEntry);
                        entry.getValue().getInputStream().transferTo(zos);
                        zos.closeEntry();
                    }
                }
            }

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=images.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve multiple images: " + e.getMessage());
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteMultiple(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> filenames = request.get("filenames");
            if (filenames == null || filenames.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No filenames provided");
            }

            Map<String, Boolean> results = storageService.deleteMultiple(filenames);

            long successCount = results.values().stream().filter(v -> v).count();
            long failedCount = results.size() - successCount;

            Map<String, Object> response = new HashMap<>();
            response.put("totalFiles", results.size());
            response.put("successCount", successCount);
            response.put("failedCount", failedCount);
            response.put("results", results);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete multiple images: " + e.getMessage());
        }
    }

    // Get information about specific multiple images (metadata)
    @PostMapping("/batch/info")
    public ResponseEntity<Map<String, Object>> getMultipleImagesInfo(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> filenames = request.get("filenames");
            if (filenames == null || filenames.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No filenames provided");
            }

            Map<String, Resource> resources = storageService.loadMultiple(filenames);
            List<Map<String, Object>> fileInfoList = new ArrayList<>();

            for (Map.Entry<String, Resource> entry : resources.entrySet()) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("filename", entry.getKey());
                fileInfo.put("exists", entry.getValue() != null);
                if (entry.getValue() != null) {
                    try {
                        fileInfo.put("size", entry.getValue().contentLength());
                        fileInfo.put("contentType",
                                URLConnection.guessContentTypeFromName(entry.getKey()));
                    } catch (IOException e) {
                        fileInfo.put("error", "Failed to read file info");
                    }
                }
                fileInfoList.add(fileInfo);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("totalFiles", filenames.size());
            response.put("filesInfo", fileInfoList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to get images info: " + e.getMessage());
        }
    }
}
