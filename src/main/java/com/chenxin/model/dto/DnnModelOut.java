package com.chenxin.model.dto;

import com.chenxin.model.bo.WordItemBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * DNN语言模型分析结果
 * Created by 尘心 on 2020/9/19 0019.
 */
@ApiModel("DNN语言模型分析结果")
@Data
public class DnnModelOut {

    @ApiModelProperty("请求唯一标识码")
    private String log_id;

    @ApiModelProperty("文本内容，最大256字节，不需要切词")
    private String text;

    @ApiModelProperty("切词概率值集合")
    private List<WordItemBo> items;

    @ApiModelProperty("描述句子通顺的值：数值越低，句子越通顺")
    private String ppl;
}
