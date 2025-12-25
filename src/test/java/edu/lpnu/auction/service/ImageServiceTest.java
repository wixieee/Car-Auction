package edu.lpnu.auction.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import edu.lpnu.auction.utils.exception.types.CloudException;
import edu.lpnu.auction.utils.exception.types.WrongFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock private Storage storage;

    @InjectMocks
    private ImageService imageService;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
    }

    @Test
    void saveImages_WhenInvalidFormat_ShouldThrowExceptionImmediately() {
        MockMultipartFile validImage = new MockMultipartFile("f1", "img.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile invalidFile = new MockMultipartFile("f2", "doc.pdf", "application/pdf", new byte[1]);

        List<MultipartFile> files = List.of(validImage, invalidFile);

        assertThrows(WrongFormatException.class,
                () -> imageService.saveImages(files));

        verify(storage, never()).create(any(BlobInfo.class), any(byte[].class));
    }

    @Test
    void saveImages_WhenValidRequest_ShouldSaveImages() {
        MockMultipartFile file1 = new MockMultipartFile("f1", "img1.jpg", "image/jpeg", "1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("f2", "img2.png", "image/png", "2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(null);

        List<String> urls = imageService.saveImages(files);

        assertThat(urls).hasSize(2);
        assertThat(urls.get(0)).contains("https://storage.googleapis.com/" + BUCKET_NAME + "/cars/");
        assertThat(urls.get(0)).endsWith("_img1.jpg");

        verify(storage, times(2)).create(any(BlobInfo.class), any(byte[].class));
    }

    @Test
    void saveImages_WhenIoError_ShouldRollback() throws IOException {
        MockMultipartFile goodFile = new MockMultipartFile("f1", "good.jpg", "image/jpeg", "ok".getBytes());

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.isEmpty()).thenReturn(false);
        when(badFile.getContentType()).thenReturn("image/png");
        when(badFile.getOriginalFilename()).thenReturn("bad.png");
        when(badFile.getBytes()).thenThrow(new IOException("Disk error"));

        List<MultipartFile> files = List.of(goodFile, badFile);

        assertThrows(CloudException.class,
                () -> imageService.saveImages(files));

        verify(storage).create(any(BlobInfo.class), eq("ok".getBytes()));

        ArgumentCaptor<BlobId> blobCaptor = ArgumentCaptor.forClass(BlobId.class);
        verify(storage).delete(blobCaptor.capture());

        BlobId deletedBlob = blobCaptor.getValue();
        assertThat(deletedBlob.getBucket()).isEqualTo(BUCKET_NAME);
        assertThat(deletedBlob.getName()).contains("cars/");
    }

    @Test
    void deleteImage_WhenValidRequest_ShouldDeleteImage() {
        String fileName = "cars/uuid_photo.jpg";
        String url = "https://storage.googleapis.com/" + BUCKET_NAME + "/" + fileName;

        imageService.deleteImage(url);

        ArgumentCaptor<BlobId> blobCaptor = ArgumentCaptor.forClass(BlobId.class);
        verify(storage).delete(blobCaptor.capture());

        BlobId capturedId = blobCaptor.getValue();
        assertThat(capturedId.getBucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedId.getName()).isEqualTo(fileName);
    }

    @Test
    void deleteImage_WhenWrongBucketInUrl_ShouldDoNothing() {
        String url = "https://storage.googleapis.com/WRONG-BUCKET/cars/photo.jpg";

        imageService.deleteImage(url);

        verify(storage, never()).delete(any(BlobId.class));
    }

    @Test
    void deleteImage_WhenStorageThrowsException_ShouldSwallow() {
        String url = "https://storage.googleapis.com/" + BUCKET_NAME + "/cars/photo.jpg";
        when(storage.delete(any(BlobId.class))).thenThrow(new RuntimeException("GCP Down"));

        imageService.deleteImage(url);

        verify(storage).delete(any(BlobId.class));
    }
}