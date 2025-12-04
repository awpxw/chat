package com.aw.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeptVO {

    private Long id;

    private Long parentId;

    private String name;

    private Integer sort;

    private List<DeptVO> children = new ArrayList<>();

}
