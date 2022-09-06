package dev.andrewjfei.localstackdemo;

import dev.andrewjfei.localstackdemo.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3IT extends BaseIT {

    private final String S3_BUCKET_NAME = "localstack-demo";
    private final String S3_OBJECT_KEY = "localstack-demo-key";
    private final String S3_OBJECT_PATH = "./README.md";
    private final int NUM_OF_S3_BUCKET_OBJECTS = 0;

    private final S3Client s3Client = S3Util.getS3Client(localstackContainer);

    @BeforeEach
    public void setUp() {
        S3Util.createBucket(s3Client, S3_BUCKET_NAME);
    }

    @Test
    public void addNewObjectToS3_success() {
        S3Util.putS3Object(s3Client, S3_BUCKET_NAME, S3_OBJECT_KEY, S3_OBJECT_PATH);

        List<S3Object> s3ObjectList = S3Util.getBucketObjects(s3Client, S3_BUCKET_NAME);

        assertEquals(NUM_OF_S3_BUCKET_OBJECTS + 1, s3ObjectList.size());
        assertTrue(!s3ObjectList.stream().filter((s3Object) -> s3Object.key().equals(S3_OBJECT_KEY)).collect(Collectors.toList()).isEmpty());
    }
}
