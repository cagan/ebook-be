package com.cagan.library.integration.s3.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.cagan.library.integration.s3.ObjectLocator;
import com.cagan.library.web.errors.BookBucketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class S3ClientService {
    private static final Logger log = LoggerFactory.getLogger(S3ClientService.class);
    @Value("${s3.access.key}")
    private String accessKey;
    @Value("${s3.secret.key}")
    private String secretKey;
    @Value("${s3.connection.timeout.in.millis}")
    private Integer connectionTimeout;
    @Value("${s3.socket.timeout.in.millis}")
    private Integer socketTimeout;
    private AmazonS3 connection;

    @PostConstruct
    private void createConnection() {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout(connectionTimeout);
        config.setSocketTimeout(socketTimeout);

        connection = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .withClientConfiguration(config)
                .build();
    }

    public AmazonS3 getConnection() {
        if (connection == null) {
            createConnection();
        }
        return connection;
    }

    public void putObject(ObjectLocator objectLocator, InputStream inputStream, ObjectMetadata metadata) {
        if (!getConnection().doesBucketExistV2(objectLocator.getBucketName())) {
            log.info("Can not find bucket. Creating {}...", objectLocator.getBucketName());
            createBucket(objectLocator.getBucketName());
        }
        connection.putObject(objectLocator.getBucketName(), objectLocator.getObjectName(), inputStream, metadata);
    }

    public void putObject(ObjectLocator objectLocator, InputStream inputStream, long actualSize) {
        if (!getConnection().doesBucketExistV2(objectLocator.getBucketName())) {
            log.info("Can not find bucket. Creating {}...", objectLocator.getBucketName());
            createBucket(objectLocator.getBucketName());
        }
        ObjectMetadata metadata = getObjectMetadata(actualSize);
        putObject(objectLocator, inputStream, metadata);
        log.info("[OBJECT: {}] has putted in the [BUCKET: {}].", objectLocator.getObjectName(), objectLocator.getBucketName());
        validatePutObject(objectLocator);
    }

    public void validatePutObject(ObjectLocator objectLocator) {
        String bucketName = objectLocator.getBucketName();
        String name = objectLocator.getObjectName();

        if (!name.equals(connection.getObject(bucketName, name).getKey())) {
            BookBucketException exception = new BookBucketException("Validate put object");
            log.error("[OBJECT_STORAGE_S3_CLIENT] [VALIDATE_PUT_OBJECT] [BUCKET_NAME: {}], [OBJECT_NAME: {}] [CAUSE: {}]", bucketName, name, exception);
        }

        log.info("[OBJECT: {}] has been validated in the [BUCKET: {}].", objectLocator.getObjectName(), objectLocator.getBucketName());
    }

    private ObjectMetadata getObjectMetadata(long actualSize) {
        ObjectMetadata metadata = new ObjectMetadata();
        if (actualSize > 0) {
            metadata.setContentLength(actualSize);
        }
        return metadata;
    }

    public void createBucket(String bucketName) {
        try {
            getConnection().createBucket(bucketName);
            log.info("BUCKET CREATED: {}", bucketName);
        } catch (Exception exception) {
            log.error("[S3_CLIENT_CREATE_BUCKET] [BUCKET_NAME: {}] ERROR CREATING BUCKET: {}", bucketName, exception.getMessage());
            throw new BookBucketException(exception.getMessage());
        }
    }

    public S3Object getObject(ObjectLocator objectLocator) {
        return getConnection().getObject(objectLocator.getBucketName(), objectLocator.getObjectName());
    }

    public void deleteObject(ObjectLocator locator) {
        log.info("Deleting the object: [BUCKET_NAME: {}] [OBJECT_NAME: {}]", locator.getBucketName(), locator.getObjectName());
        getConnection().deleteObject(locator.getBucketName(), locator.getBucketName());
    }
}
