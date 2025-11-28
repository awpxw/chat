package com.aw.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.util.CollectionUtils;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorUtil {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    // 1. 单个对象支持分组
    public static <T> void validate(T object, Class<?>... groups) {
        if (object == null) {
            throw new IllegalArgumentException("校验对象不能为空");
        }
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(formatMessage(violations), violations);
        }
    }

    // 2. 集合/数组/List 支持分组
    public static <T> void validateCollection(Collection<T> collection, Class<?>... groups) {
        if (CollectionUtils.isEmpty(collection)) return;
        for (T item : collection) {
            if (item != null) {
                validate(item, groups);   // 递归调用支持分组的方法
            }
        }
    }

    public static <T> void validateArray(T[] array, Class<?>... groups) {
        if (array == null || array.length == 0) return;
        for (T item : array) {
            if (item != null) {
                validate(item, groups);
            }
        }
    }

    // 3. 一键校验（自动识别类型）也支持分组
    public static <T> void validateObject(T obj, Class<?>... groups) {
        if (obj == null) {
            throw new IllegalArgumentException("校验对象不能为空");
        }
        if (obj instanceof Collection<?>) {
            validateCollection((Collection<?>) obj, groups);
        } else if (obj.getClass().isArray()) {
            validateArray((Object[]) obj, groups);
        } else {
            validate(obj, groups);
        }
    }

    // 保留原来的无分组方法（兼容老代码）
    public static <T> void validate(T object) {
        validate(object); // 直接调用上面的（默认无分组）
    }

    public static <T> void validateObject(T obj) {
        validateObject(obj); // 默认无分组
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
