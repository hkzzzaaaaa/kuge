package com.example.kuge.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.example.kuge.config.AliyunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class OssUploadUtil {
    @Autowired
    private AliyunConfig ossConfig;
    public String uploadFile(MultipartFile file) throws IOException {
        OSS ossClient = new OSSClientBuilder()
                .build(ossConfig.getEndpoint(),
                        ossConfig.getAccessid(),
                        ossConfig.getAccesskey());
        try {
            String originalFileName = file.getOriginalFilename();
            String fileSuffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileSuffix;
            String key =fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest request = new PutObjectRequest(ossConfig.getBucketName(), key, inputStream, metadata);
                ossClient.putObject(request);
            }
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + key;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        OSS ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(),
                ossConfig.getAccessid(),
                ossConfig.getAccesskey());
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), key);
        } finally {
            ossClient.shutdown();
        }
    }
    private String extractKeyFromUrl(String fileUrl) {
        String ossPrefix = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/";
        if (fileUrl.startsWith(ossPrefix)) {
            return fileUrl.substring(ossPrefix.length());
        } else {
            throw new IllegalArgumentException("无效的URL：" + fileUrl);
        }
    }
}