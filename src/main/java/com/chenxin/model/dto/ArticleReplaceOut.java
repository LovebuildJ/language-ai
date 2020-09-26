package com.chenxin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文章替换响应对象
 * Created by 尘心 on 2020/9/26 0026.
 */
@Data
@ApiModel("文章替换响应对象")
public class ArticleReplaceOut {

    @ApiModelProperty("源文本")
    private String sourceText;

    @ApiModelProperty("替换后文本")
    private String replaceText;

    @ApiModelProperty("替换总词数")
    private Integer replaceCount;
}
