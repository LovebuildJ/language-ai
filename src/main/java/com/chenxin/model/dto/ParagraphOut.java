package com.chenxin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章段落替换结果返回
 * Created by 尘心 on 2020/9/26 0026.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("文章段落替换结果返回对象")
public class ParagraphOut {

    @ApiModelProperty("替换结果")
    private String result;

    @ApiModelProperty("总计替换多少词")
    private int total;
}
