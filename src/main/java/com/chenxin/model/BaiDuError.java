package com.chenxin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 百度API 错误信息返回VO
 * @Date 2020/9/18 11:16
 * @Author by 尘心
 */
@Data
@ApiModel("百度API 错误信息返回VO")
public class BaiDuError {

    @ApiModelProperty("错误类型")
    private String error;

    @ApiModelProperty("错误描述")
    private String error_description;
}
