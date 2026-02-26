package com.aw.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GlobalSearchVO {

    private List<String> highlights;

}
