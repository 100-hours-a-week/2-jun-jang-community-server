package community.Api.Image.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.Api.Image.Dtos.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AmazonS3 amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private String IMG_DIR = "img/";

    @Override
    public ImageResponse.ImageUploadDto UploadImage(MultipartFile file) throws IOException {

        String fileExtension = "jpg";

        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        // 고유한 파일 이름 생성
        String fileName = IMG_DIR + UUID.randomUUID() + "." + fileExtension;
        return putS3(file, fileName);


//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("key", secretKey);
//        try{
//            body.add("image",new NamedByteArrayResource(file.getBytes(), UUID.randomUUID().toString()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        String apiUrl = "https://api.imgbb.com/1/upload";
//        ImageResponse.ImgBBData imgBBData= webClient.post()
//                .uri(apiUrl)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .bodyValue(body)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(ImageResponse.ImgBBInfo.class)
//                .blockOptional()
//                .map(ImageResponse.ImgBBInfo::getData)
//                .orElseThrow(() -> new RuntimeException());
//        return ImageResponse.ImageUploadDto.builder()
//                .fileUrl(imgBBData.getImage().getUrl())
//                .fileName(imgBBData.getImage().getName()).build();

    }

    private ImageResponse.ImageUploadDto putS3(MultipartFile multiFile, String fileName) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);

        multiFile.transferTo(tempFile);

        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(multiFile.getContentType());

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, tempFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)
                .withMetadata(metadata));

        String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

        tempFile.delete(); // 업로드 후 임시 파일 삭제

        return ImageResponse.ImageUploadDto.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .build();
    }
//    private static class NamedByteArrayResource extends ByteArrayResource {
//        private final String filename;
//
//        public NamedByteArrayResource(byte[] byteArray, String filename) {
//            super(byteArray);
//            this.filename = filename;
//        }
//
//        @Override
//        public String getFilename() {
//            return filename;
//        }
//    }
}
