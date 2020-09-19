package com.chenxin.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DNN单词分值
 * Created by 尘心 on 2020/9/19 0019.
 */
@ApiModel("DNN单词分值")
@Data
public class WordItemBo {

    @ApiModelProperty("句子的切词结果")
    private String word;

    @ApiModelProperty("该词在句子中的概率值,取值范围[0,1]")
    private String prob;
}
