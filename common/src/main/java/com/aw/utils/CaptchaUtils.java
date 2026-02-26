package com.aw.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码工具类
 */
@Slf4j
@Component
public class CaptchaUtils {

    private final StringRedisTemplate redisTemplate;
    private static final int WIDTH = 160;      // 验证码宽度
    private static final int HEIGHT = 48;      // 验证码高度
    private static final int CODE_LENGTH = 4;  // 验证码位数
    private static final int EXPIRE_SECOND = 180; // 过期时间 3 分钟
    private static final String CHAR_POOL = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

    public CaptchaUtils(@Autowired StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 生成验证码图片并返回 uuid（前端存 cookie 或 localStorage）
     */
    public Map<String, String> generateCaptcha(int expireMinutes) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = generateCode();
        String redisKey = "captcha:" + uuid;
        redisTemplate.opsForValue().set(redisKey, code.toLowerCase(), expireMinutes == -1 ? EXPIRE_SECOND : expireMinutes, TimeUnit.SECONDS);
        BufferedImage image = createImage(code);
        String imageToBase64 = convertImageToBase64(image);
        map.put("captchaId", redisKey);
        map.put("image", imageToBase64);
        return map;
    }

    /**
     * 校验验证码
     */
    public boolean validate(String uuid, String code) {
        if (uuid == null || code == null) return false;
        String savedCode = redisTemplate.opsForValue().get(uuid);
        if (savedCode == null) {
            return false; // 已过期或不存在
        }
        boolean valid = savedCode.equals(code.toLowerCase());
        // 校验完立即删除，防止重复使用
        redisTemplate.delete(uuid);
        return valid;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    private BufferedImage createImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random random = new Random();

        // 背景色
        g.setColor(new Color(240, 248, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        g.setColor(getRandColor(100, 160));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(30);
            int yl = random.nextInt(30);
            g.drawLine(x, y, x + xl, y + yl);
        }

        // 绘制验证码
        g.setFont(new Font("Arial", Font.BOLD, 38));
        for (int i = 0; i < code.length(); i++) {
            String ch = String.valueOf(code.charAt(i));
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 随机倾斜和位置
            g.translate(random.nextInt(6), random.nextInt(6));
            g.drawString(ch, 38 * i + 12, 36);
        }

        // 边框
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        g.dispose();
        return image;
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 图片转base64
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }
}