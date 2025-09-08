import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

/**
 * *作者：jiaohan
 * *日期：2025/9/8 14:34
 * *文件描述：
 */
public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void testUpload() throws Exception {
        minioClient.uploadObject(
                UploadObjectArgs
                        .builder()
                        .bucket("testbucket")
                        .object("test.jpg")
                        .filename("D:\\图片\\1.jpg")
                        .build()
        );
    }
}
