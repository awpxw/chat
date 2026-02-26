package com.aw.utils.tree;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TreeUtil {

    public static <T extends ITree<T>> List<T> buildTree(List<T> list, Long rootId) {
        return list.stream()
            .filter(node -> Objects.equals(node.getParentId(), rootId))
            .peek(node -> node.setChildren(getChildren(node, list)))
            .sorted(Comparator.comparing(ITree::getSort))
            .collect(Collectors.toList());
    }

    private static <T extends ITree<T>> List<T> getChildren(T parent, List<T> all) {
        return all.stream()
            .filter(node -> Objects.equals(node.getParentId(), parent.getId()))
            .peek(node -> node.setChildren(getChildren(node, all)))
            .sorted(Comparator.comparing(ITree::getSort))
            .collect(Collectors.toList());
    }

}

