package dev.andrewjfei.localstackdemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3Util {

    private static final Logger logger = LoggerFactory.getLogger(S3Util.class);

    public static S3Client getS3Client(LocalStackContainer localstackContainer) {
        return S3Client
                .builder()
                .endpointOverride(localstackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstackContainer.getAccessKey(), localstackContainer.getSecretKey())
                        )
                )
                .region(Region.of(localstackContainer.getRegion()))
                .build();
    }

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
        } catch (S3Exception e) {
            logger.error("Error creating bucket. {}", e.awsErrorDetails().errorMessage());
        }
    }

    public static String putS3Object(S3Client s3Client, String bucketName, String objectKey, String objectPath) {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            RequestBody requestBody = RequestBody.fromBytes(getObjectFile(objectPath));

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);
            return putObjectResponse.eTag();
        } catch (S3Exception e) {
            logger.error("Error putting object into bucket. {}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static List<S3Object> getBucketObjects(S3Client s3Client, String bucketName ) {
        try {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
            return listObjectsResponse.contents();
        } catch (S3Exception e) {
            logger.error("Error getting bucket objects. {}", e.awsErrorDetails().errorMessage());
            return Arrays.asList();
        }
    }

    // Convert file on file path into a byte array
    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;
    }


}
