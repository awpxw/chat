package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("t_file_record")
@EqualsAndHashCode(callSuper = true)
public class FileRecord extends BaseEntity {

    /**
     * 原始文件名
     */
    private String originName;

    /**
     * 文件名
     */
    private String objectName;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 上传人ID
     */
    private Long uploaderId;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 过期时间（为空表示永久有效）
     */
    private LocalDateTime expireTime;

}