package com.aw.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class PageUtils {
    public static <T> Page<T> of(PageRequest pageParam) {
        return new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
    }
}