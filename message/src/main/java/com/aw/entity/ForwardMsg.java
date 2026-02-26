package com.aw.entity;

import com.aw.fill.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_forward_message")
public class ForwardMsg extends BaseEntity {

    private Long msgId;

    private String msgIdList;

    private Long forwardUserId;

    private LocalDateTime forwardTime;

}
