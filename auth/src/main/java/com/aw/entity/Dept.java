package com.aw.entity;// Dept.java
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_dept")                     // 表名
public class Dept {

    @TableId(type = IdType.AUTO)         // 自增主键
    private Long id;

    private Long parentId = 0L;          // 默认 0

    private String name;

    private Integer sort = 0;

    private Integer status = 1;          // 1启用 0禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}