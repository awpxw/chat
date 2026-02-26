// common/src/main/java/com/aw/common/util/FileUploadUtil.java
package com.aw.utils;


import com.aw.properties.MinioProps;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadUtil {

    @Resource
    private MinioClient minioClient;

    @Resource
    private final MinioProps properties;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 上传文件（最常用）
     */
    @SneakyThrows
    public String upload(MultipartFile file) {
        return upload(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
    }

    /**
     * 核心上传方法
     */
    @SneakyThrows
    public String upload(InputStream inputStream, String originalFilename, String contentType) {
        // 1. 确保 bucket 存在
        ensureBucket();

        // 2. 生成新文件名：年月日/UUID.后缀
        String fileName = createObjectName(originalFilename);

        // 3. 上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(properties.getBucketName())
                        .object(fileName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType != null ? contentType : "application/octet-stream")
                        .build()
        );

        // 4. 返回完整 URL
        return getFileUrl(fileName);
    }

    public String createObjectName(String originalFilename) {
        String datePath = LocalDateTime.now().format(DTF);
        String suffix = getFileSuffix(originalFilename);
        return datePath + "/" + UUID.randomUUID() + suffix;
    }

    /**
     * 获取文件访问 URL
     */
    public String getFileUrl(String fileName) {
        String domain = properties.getDomain();
        if (StringUtils.isNotBlank(domain)) {
            return domain.replaceAll("/+$", "") + "/" + fileName;
        }
        return properties.getEndpoint().replaceAll("/+$", "") + "/" + properties.getBucketName() + "/" + fileName;
    }

    /**
     * 删除文件
     */
    @SneakyThrows
    public void remove(String fileName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(properties.getBucketName())
                        .object(fileName.startsWith("/") ? fileName.substring(1) : fileName)
                        .build()
        );
    }

    // 私有工具方法
    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(properties.getBucketName()).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(properties.getBucketName()).build()
            );
            // 设置公共读权限（可根据需要调整）
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(properties.getBucketName())
                            .config(createPublicPolicy())
                            .build()
            );
        }
    }

    private String createPublicPolicy() {
        String bucketName = properties.getBucketName();
        return "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                "      \"Action\": [\"s3:GetObject\"],\n" +
                "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private String getFileSuffix(String originalFilename) {
        if (originalFilename == null || Objects.equals(originalFilename, "")) {
            return ".tmp";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        return dotIndex == -1 ? "" : originalFilename.substring(dotIndex);
    }
}