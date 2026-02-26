package com.aw.vo;

import com.aw.utils.tree.ITree;
import lombok.Data;

import java.util.List;

@Data
public class MenuTreeVO implements ITree<MenuTreeVO> {

    private Long id;

    private Long parentId;

    private String name;

    private String path;

    private String component;

    private String perms;

    private Integer type;

    private String icon;

    private Integer sort;

    private Integer visible;

    private List<MenuTreeVO> children;

}
