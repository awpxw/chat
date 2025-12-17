package com.aw.warm;

import com.aw.redis.RedisUtils;
import com.aw.service.DeptService;
import com.aw.service.UserService;
import com.aw.vo.DeptVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmupService {

    private final RedisUtils redisUtils;

    private final DeptService deptService;

    @Value("warm.open")
    private String open;

    /**
     * 启动后延迟 15 秒开始预热（等数据库/Redis 完全就绪）
     */
    @PostConstruct
    public void initCache() {
        if (Objects.equals(open, "true")) {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(15_000);
                    log.info("【缓存预热】开始执行...");
                    long start = System.currentTimeMillis();
                    warmDeptTree();
                    log.info("【缓存预热】全部完成，耗时 {} ms", System.currentTimeMillis() - start);
                } catch (Exception e) {
                    log.error("【缓存预热】失败", e);
                }
            });
        }
    }

    /**
     * 预热部门树
     */
    private void warmDeptTree() {
        String key = redisUtils.key("dept_tree");
        DeptVO tree = deptService.loadDeptTreeFromDB();
        redisUtils.set(key, tree, 30, TimeUnit.MINUTES);
        log.info("【缓存预热】部门树完成，节点数：{}", countTreeNodes(tree));
    }

    /**
     * 统计树节点数（日志用）
     */
    private int countTreeNodes(DeptVO node) {
        if (node == null || node.getChildren() == null) return 1;
        return 1 + node.getChildren().stream().mapToInt(this::countTreeNodes).sum();
    }

}