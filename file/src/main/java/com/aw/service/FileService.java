package com.aw.service;

import com.aw.vo.EmojiVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    /**
     * 上传文件
     */
    void uploadFile(MultipartFile file);

    /**
     * 下载文件
     */
    void download(Long id, HttpServletResponse response);

    /**
     * 表情包
     */
    List<EmojiVO> emojis();

}
