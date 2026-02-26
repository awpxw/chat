package com.aw.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnnouncementVO {

    private final String defaultMsg = "请设置群公告...";

    private String msg;

}
