package com.quickpos.quickposbackend.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ImageService {
    private static final String UPLOAD_DIR = "uploads/images";
    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IOException("File is empty");

        Path uploadPath = Paths.get("uploads/images");
        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String safeFilename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(safeFilename).normalize();
        Files.write(filePath, file.getBytes());

        return "/uploads/images/" + safeFilename;
    }

    public void deleteImage(String imageUrl) throws IOException{
        if(imageUrl != null && !imageUrl.isBlank()){
            String filename = Paths.get(imageUrl).getFileName().toString();
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.deleteIfExists(filePath);
        }
    }
}
