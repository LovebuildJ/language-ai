package com.chenxin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文本替换结果
 * Created by 尘心 on 2020/9/26 0026.
 */
@Data
@ApiModel("文本替换结果")
public class ReplaceTextOut {

    @ApiModelProperty("源文本")
    private String source;

    @ApiModelProperty("替换后文本")
    private String replace;

    @ApiModelProperty("替换多少词")
    private Integer replaceCount;
}
