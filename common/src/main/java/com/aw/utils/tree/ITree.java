package com.aw.utils.tree;


import java.util.List;

public interface ITree<T> {

    Long getId();

    Long getParentId();

    Integer getSort();

    void setChildren(List<T> children);

}