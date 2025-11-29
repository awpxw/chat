package com.aw.dto;

import com.aw.dto.groups.CaptchaGroup;
import com.aw.dto.groups.CaptchaVerifyGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaptchaDTO {

    @Max(value = 300, message = "过期时间最大为5分钟", groups = {CaptchaGroup.class})
    Integer expireIns = -1;

    @NotBlank(message = "uuid不为空", groups = {CaptchaVerifyGroup.class})
    String uuid;

    @NotBlank(message = "code不为空", groups = {CaptchaVerifyGroup.class})
    String code;

}
