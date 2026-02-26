package com.aw.controller;

import com.aw.exception.Result;
import com.aw.service.FileService;
import com.aw.vo.EmojiVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);
        return Result.success();
    }

    @PostMapping("/download/{id}")
    public Result<String> download(@PathVariable("id") Long id, HttpServletResponse response) {
        fileService.download(id,response);
        return Result.success();
    }

    @PostMapping("/emojis")
    public Result<List<EmojiVO>> emojis() {
        List<EmojiVO> emojis = fileService.emojis();
        return Result.success(emojis);
    }

}
