package com.aw.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 参数校验终极增强工具类
 * 支持：普通对象、List、Set、数组、嵌套对象、集合内嵌套对象
 * 用法：ValidatorUtil.validate(obj);
 * 用法：ValidatorUtil.validateList(list);
 */
public class ValidatorUtil {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /** 校验单个对象（支持嵌套） */
    public static <T> void validate(T object) {
        if (object == null) {
            throw new IllegalArgumentException("校验对象不能为空");
        }
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(formatMessage(violations), violations);
        }
    }

    /** 校验集合（List/Set）里的每一个元素（支持嵌套对象） */
    public static <T> void validateCollection(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return; // 允许空集合
        }
        for (T item : collection) {
            if (item != null) {
                validate(item);
            }
        }
    }

    /** 校验数组里的每一个元素 */
    public static <T> void validateArray(T[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        for (T item : array) {
            if (item != null) {
                validate(item);
            }
        }
    }

    /** 最常用：一键校验任意对象（自动识别是单个还是集合） */
    public static <T> void validateObject(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("校验对象不能为空");
        }
        if (obj instanceof Collection<?> coll) {
            validateCollection((Collection<?>) coll);
        } else if (obj.getClass().isArray()) {
            validateArray((Object[]) obj);
        } else {
            validate(obj);
        }
    }

    /** 把校验错误变成超好看的中文提示 */
    private static <T> String formatMessage(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(v -> {
                    String field = v.getPropertyPath().toString();
                    String msg = v.getMessage();
                    return field + msg;  // 如：username不能为空、age必须大于0
                })
                .collect(Collectors.joining("；"));
    }
}