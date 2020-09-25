package com.chenxin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 词语相似度DTO
 * Created by 尘心 on 2020/9/25 0025.
 */
@Data
@ApiModel("词语相似度DTO")
public class SimilarWordDto {

    @ApiModelProperty("原词语")
    private String source;

    @ApiModelProperty("目标词语")
    private String target;
}
