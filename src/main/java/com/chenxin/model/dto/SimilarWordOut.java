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
public class SimilarWordOut extends SimilarWordDto{

    @ApiModelProperty("相似度得分")
    private Long score;
}
