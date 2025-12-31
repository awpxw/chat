package com.aw.service.impl;

import com.aw.entity.FileRecord;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.FileRecordMapper;
import com.aw.properties.MinioProps;
import com.aw.service.FileService;
import com.aw.utils.FileUploadUtil;
import com.aw.vo.EmojiVO;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.URLEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final List<String> EMOJI_FILES = List.of(
            "smile.png", "laugh.png", "cry.png",
            "sad.png", "angry.png", "love.png", "heart.png",
            "cool.png", "wink.png", "kiss.png", "surprise.png",
            "thinking.png", "sleep.png", "happy.png", "wow.png"
    );

    private final static String EMOJI_BUCKET = "emojis";

    @Resource
    private FileUploadUtil fileUploadUtil;

    @Resource
    private FileRecordMapper fileRecordMapper;

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProps minioProps;

    @Override
    public void uploadFile(MultipartFile file) {

        String url = fileUploadUtil.upload(file);

        savaFileRecord(url, file);

    }

    @Override
    public void download(Long id, HttpServletResponse response) {

        FileRecord fileRecord = checkFileExist(id);

        GetObjectArgs args = downloadFromMinio(fileRecord);

        transfer(response, args, fileRecord);

    }

    @Override
    public List<EmojiVO> emojis() {

        String baseUrl = createEmojiBase();

        return getEmojiFromMinio(baseUrl);

    }

    private void savaFileRecord(String url, MultipartFile file) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUploaderId(UserContext.get().getUserId());
        fileRecord.setUploadTime(LocalDateTime.now());
        fileRecord.setUrl(url);
        fileRecord.setSize(file.getSize());
        fileRecord.setOriginName(file.getOriginalFilename());
        fileRecord.setObjectName(fileUploadUtil.createObjectName(file.getOriginalFilename()));
        int success = fileRecordMapper.insert(fileRecord);
        if (success <= 0) {
            log.error(">>>上传文件失败，请稍后重试");
            throw new BizException(">>>上传文件失败，请稍后重试");
        }
    }

    private static List<EmojiVO> getEmojiFromMinio(String baseUrl) {
        List<EmojiVO> emojiVOS = new ArrayList<>();
        for (String fileName : EMOJI_FILES) {
            String url = baseUrl + fileName;
            emojiVOS.add(new EmojiVO(fileName, url));
        }
        return emojiVOS;
    }

    private String createEmojiBase() {
        return minioProps.getEndpoint() + "/" + EMOJI_BUCKET + "/";
    }

    private void transfer(HttpServletResponse response, GetObjectArgs args, FileRecord fileRecord) {
        try (InputStream inputStream = minioClient.getObject(args)) {
            // 设置响应头
            String fileName = URLEncoder.DEFAULT
                    .encode(fileRecord.getOriginName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", ("attachment") + "; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileName);
            if (fileRecord.getSize() != null) {
                response.setContentLengthLong(fileRecord.getSize());
            }
            inputStream.transferTo(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error(">>>文件下载失败，文件名：{}", fileRecord.getOriginName());
            throw new BizException(">>>文件下载失败");
        }
    }

    private GetObjectArgs downloadFromMinio(FileRecord fileRecord) {
        return GetObjectArgs.builder()
                .bucket(minioProps.getBucketName())
                .object(fileRecord.getObjectName())
                .build();
    }

    private FileRecord checkFileExist(Long id) throws BizException {
        FileRecord fileRecord = fileRecordMapper.selectById(id);
        if (fileRecord == null) {
            log.error("文件不存在或已删除，id：{}", id);
            throw new BizException(">>>文件不存在或已删除");
        }
        return fileRecord;
    }

}
