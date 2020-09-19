package com.chenxin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 文本DTO对象
 * @Date 2020/9/18 15:32
 * @Author by 尘心
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("文本DTO对象")
public class TextDto {

    @ApiModelProperty("文本信息")
    private String text;
}
