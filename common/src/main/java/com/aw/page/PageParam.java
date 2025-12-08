package com.aw.page;

import lombok.Data;
import org.aspectj.weaver.ast.Not;

@Data
public class PageParam {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}