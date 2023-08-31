package com.dhx.bi.manager;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.dhx.bi.config.OSSConfig;
import com.dhx.bi.model.DTO.FileUploadResult;
import com.dhx.bi.utils.UserHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author adorabled4
 * @className OssManager
 * @date : 2023/08/27/ 15:01
 **/
@Component
public class OssManager {

    private static final String[] IMAGE_TYPES = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};

    @Resource
    OSSClient ossClient;

    @Resource
    OSSConfig ossConfig;


    /**
     * 文件上传
     *
     * @param uploadFile
     * @return
     */
    public FileUploadResult uploadImage(MultipartFile uploadFile) {
        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPES) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        // 封装Result对象，并且将文件的byte数组放置到result对象中
        FileUploadResult fileUploadResult = new FileUploadResult();
        if (!isLegal) {
            fileUploadResult.setStatus("error");
            return fileUploadResult;
        }
        // 文件新路径
        String fileName = uploadFile.getOriginalFilename();
        String newFileName = getFilePath(fileName);
        // 上传到阿里云
        try {
            byte[] imageData = uploadFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(imageData);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageData.length);
            PutObjectResult putObjectResult = ossClient.putObject(ossConfig.getBucket(), newFileName, inputStream, metadata);
            System.out.println(putObjectResult);
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            fileUploadResult.setStatus("error");
            return fileUploadResult;
        }
        fileUploadResult.setStatus("done");
        fileUploadResult.setResponse("success");
        // 文件路径需要保存到数据库
        fileUploadResult.setName(ossConfig.getUrlPrefix() + newFileName);
        fileUploadResult.setUid(String.valueOf(System.currentTimeMillis()));
        return fileUploadResult;
    }

    /**
     * 生成路径以及文件名
     *
     * @param sourceFileName
     * @return
     */
    private String getFilePath(String sourceFileName) {
        Long userId = UserHolder.getUser().getUserId();
        return "hxBI/" + "user-" + userId + RandomUtils.nextInt(100, 9999) + "."
                + StringUtils.substringAfterLast(sourceFileName, ".");
    }
}
