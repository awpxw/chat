package com.aw.ws;

import com.aw.entity.Message;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Content {

    private String event;

    private Message message;

    private ReactContent reactContent;

    private ForwardContent forwardcontent;

    private ReplyContent replycontent;

}
