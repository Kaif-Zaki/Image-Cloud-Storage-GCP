package lk.ijse.eca.cloud_storage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CloudStorageInActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageInActionApplication.class, args);
    }

    @Bean
    CommandLineRunner initStorageDirectory() {
        return args -> {
            Path storagePath = Path.of(System.getProperty("user.home"), ".ijse", "eca", "storage");
            if (Files.exists(storagePath)) {
                log.info("Storage directory already exists: {}", storagePath);
            } else {
                Files.createDirectories(storagePath);
                log.info("Storage directory created: {}", storagePath);
            }
        };
    }
}
