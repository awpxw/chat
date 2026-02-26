package com.aw.vo;

import lombok.Data;

@Data
public class EmojiVO {

    private String filename;

    private String url;

    public EmojiVO(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

}