package com.aw.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本高亮工具类
 */
public class HighlightUtil {

    private static final String HIGHLIGHT_TEMPLATE = "<span style=\"color:red; font-weight:bold;\">%s</span>";

    /**
     * 对传入的多个字符串进行关键字高亮（大小写敏感）
     *
     * @param texts     要处理的文本列表（不会修改原列表）
     * @param keyword   要高亮的关键字（为空或null则不处理）
     * @return 包含高亮标签的新字符串列表
     */
    public static List<String> highlightKeywords(List<String> texts, String keyword) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            // 关键字为空，直接返回原列表（或视需求返回深拷贝）
            return new ArrayList<>(texts);
        }

        List<String> result = new ArrayList<>();
        // 预编译正则，提高性能（尤其当 texts 很多时）
        Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.LITERAL);

        for (String text : texts) {
            if (text == null) {
                result.add(null);
                continue;
            }

            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String replacement = String.format(HIGHLIGHT_TEMPLATE, matcher.group());
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);

            result.add(sb.toString());
        }

        return result;
    }

    // ---------------------- 重载版本：单个字符串更方便使用 ----------------------

    /**
     * 对单个字符串进行关键字高亮
     */
    public static String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null || keyword.trim().isEmpty()) {
            return text;
        }

        Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.LITERAL);
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = String.format(HIGHLIGHT_TEMPLATE, matcher.group());
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    // ---------------------- 快速测试 ----------------------
    public static void main(String[] args) {
        List<String> messages = List.of(
            "你好，这是关于Java的学习笔记",
            "java和javascript是不同的语言",
            "大家一起来学java吧！Java很好玩"
        );

        List<String> highlighted = highlightKeywords(messages, "java");

        for (String s : highlighted) {
            System.out.println(s);
        }
        // 输出示例：
        // 你好，这是关于<span style="color:red; font-weight:bold;">java</span>的学习笔记
        // <span style="color:red; font-weight:bold;">java</span>和javascript是不同的语言
        // 大家一起来学<span style="color:red; font-weight:bold;">java</span>吧！<span style="color:red; font-weight:bold;">Java</span>很好玩
    }
}