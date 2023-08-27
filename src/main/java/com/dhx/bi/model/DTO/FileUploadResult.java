package com.dhx.bi.model.DTO;

import lombok.Data;

/**
 * @author adorabled4
 * @className FileUploadResult
 * @date : 2023/08/27/ 13:14
 **/
@Data
public class FileUploadResult {
    // 文件唯一标识
    private String uid;
    // 文件名
    private String name;
    // 状态有：uploading done error removed
    private String status;
    // 服务端响应内容，如：'{"status": "success"}'
    private String response;

    /**
     * 图像url
     */
    private String url ;
}
