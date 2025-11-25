// src/main/java/com/aw/common/result/PageResult.java
package com.aw.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一分页返回体（MyBatis-Plus 专用）
 * 前端最爱这种格式：code + msg + data { records, total, page, size }
 */
@Data
@Schema(description = "分页返回对象")
public class PageResult<T> implements java.io.Serializable {

    @Schema(description = "状态码", example = "200")
    private Integer code = 200;

    @Schema(description = "提示消息", example = "查询成功")
    private String msg = "操作成功";

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "总条数", example = "68")
    private Long total = 0L;

    @Schema(description = "当前页", example = "1")
    private Long page = 1L;

    @Schema(description = "每页条数", example = "10")
    private Long size = 10L;

    // ========== 成功静态方法 ==========
    public static <T> PageResult<T> ok() {
        PageResult<T> r = new PageResult<>();
        r.setMsg("操作成功");
        return r;
    }

    public static <T> PageResult<T> ok(List<T> records) {
        PageResult<T> r = new PageResult<>();
        r.setRecords(records);
        r.setTotal((long) records.size());
        return r;
    }

    // 重点：MyBatis-Plus 的 Page<T> 直接转成我们自己的格式（最爽的一招！）
    public static <T> PageResult<T> ok(Page<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setRecords(page.getRecords());
        r.setTotal(page.getTotal());
        r.setPage(page.getCurrent());
        r.setSize(page.getSize());
        return r;
    }

    // ========== 失败静态方法 ==========
    public static <T> PageResult<T> fail(String msg) {
        PageResult<T> r = new PageResult<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static <T> PageResult<T> fail(Integer code, String msg) {
        PageResult<T> r = new PageResult<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}