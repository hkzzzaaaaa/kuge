package com.example.kuge.util;

import com.example.kuge.pojo.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileTypesUtil {
    public boolean inspect(MultipartFile file,String allowedFileTypes){
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (!isValidFile(originalFilename,allowedFileTypes)) {
            throw new RuntimeException("不支持文件类型");
        }
        return true;
    }
    private boolean isValidFile(String filename,String allowedFileTypes) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String fileExtension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        List<String> allowedTypes = Arrays.stream(allowedFileTypes.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return allowedTypes.contains(fileExtension);
    }
}
