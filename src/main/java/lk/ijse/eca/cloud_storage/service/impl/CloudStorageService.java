package lk.ijse.eca.cloud_storage.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lk.ijse.eca.cloud_storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

@Primary
@Service
public class CloudStorageService implements StorageService {


    @Autowired
    private  Storage storage;
    @Value("${gcp.bucket-id}")
    private String bucketId;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String savedFilename = UUID.randomUUID() + extension;

        try {
            BlobId blobId = BlobId.of(bucketId, savedFilename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return savedFilename;
    }

    @Override
    public List<String> listAll() {
        try (Stream<Blob> blobs = storage.list(bucketId).streamAll()) {
            return blobs
                    .map(BlobInfo::getName)
                    .toList();
        }
    }

    @Override
    public Resource load(String filename) {
            byte[] fileContent = storage.readAllBytes(bucketId, filename);
            if (fileContent == null) {
                throw new IllegalArgumentException("File not found: " + filename);
            }
            return new ByteArrayResource(fileContent);

    }

    @Override
    public void delete(String filename) {
            Blob blob = storage.get(bucketId, filename);
            if (blob == null) {
                System.out.println("The object " + bucketId + " wasn't found in " + filename);
            }
            storage.delete(blob.getBlobId());


    }

    @Override
    public List<Map<String, String>> uploadMultiple(List<MultipartFile> files) {
        List<Map<String, String>> results = new ArrayList<>();

        for (MultipartFile file : files) {
            Map<String, String> result = new HashMap<>();
            try {
                if (file.isEmpty()) {
                    result.put("originalFilename", file.getOriginalFilename());
                    result.put("status", "failed");
                    result.put("error", "File is empty");
                    results.add(result);
                    continue;
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    result.put("originalFilename", file.getOriginalFilename());
                    result.put("status", "failed");
                    result.put("error", "Only image files are allowed");
                    results.add(result);
                    continue;
                }

                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String savedFilename = UUID.randomUUID() + extension;

                BlobId blobId = BlobId.of(bucketId, savedFilename);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.createFrom(blobInfo, file.getInputStream());

                result.put("originalFilename", originalFilename);
                result.put("savedFilename", savedFilename);
                result.put("status", "success");
                results.add(result);

            } catch (IOException e) {
                result.put("originalFilename", file.getOriginalFilename());
                result.put("status", "failed");
                result.put("error", "Failed to store file: " + e.getMessage());
                results.add(result);
            }
        }

        return results;
    }

    @Override
    public Map<String, Resource> loadMultiple(List<String> filenames) {
        Map<String, Resource> results = new HashMap<>();

        for (String filename : filenames) {
            try {
                byte[] fileContent = storage.readAllBytes(bucketId, filename);
                if (fileContent != null) {
                    results.put(filename, new ByteArrayResource(fileContent));
                } else {
                    results.put(filename, null);
                }
            } catch (Exception e) {
                results.put(filename, null);
            }
        }

        return results;
    }

    @Override
    public Map<String, Boolean> deleteMultiple(List<String> filenames) {
        Map<String, Boolean> results = new HashMap<>();

        for (String filename : filenames) {
            try {
                Blob blob = storage.get(bucketId, filename);
                if (blob != null) {
                    storage.delete(bucketId, filename);
                    results.put(filename, true);
                } else {
                    results.put(filename, false);
                }
            } catch (Exception e) {
                results.put(filename, false);
            }
        }

        return results;
    }
}
