package edu.lpnu.auction.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import edu.lpnu.auction.utils.exception.types.CloudException;
import edu.lpnu.auction.utils.exception.types.WrongFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final Storage storage;

    @Value("${GCP_BUCKET_NAME}")
    private String bucketName;

    public List<String> saveImages(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                validateImage(file);
            }
        }

        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String url = saveSingleImage(file);
                    uploadedUrls.add(url);
                }
            }
            return uploadedUrls;

        } catch (Exception e) {
            deleteImages(uploadedUrls);
            throw new CloudException("Збій під час завантаження файлів", e);
        }
    }

    public void deleteImages(List<String> imageUrls) {
        if (imageUrls == null) return;
        for (String url : imageUrls) {
            deleteImage(url);
        }
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new WrongFormatException("Недопустимий формат файлу: " + file.getOriginalFilename());
        }
    }

    private String saveSingleImage(MultipartFile file) {
        try {
            String fileName = "cars/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());
            return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
        } catch (IOException e) {
            throw new CloudException("Помилка при завантаженні фото" ,e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String splitMarker = bucketName + "/";
            int startIndex = imageUrl.indexOf(splitMarker);

            if (startIndex == -1) {
                log.warn("Не вдалося розпарсити URL: {}", imageUrl);
                return;
            }

            String blobName = imageUrl.substring(startIndex + splitMarker.length());
            BlobId blobId = BlobId.of(bucketName, blobName);

            storage.delete(blobId);
            log.info("Файл видалено: {}", blobName);

        } catch (Exception e) {
            log.error("Помилка при видаленні {}: {}", imageUrl, e.getMessage());
        }
    }
}