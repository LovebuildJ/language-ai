package com.chenxin.model.dto;

import com.chenxin.model.bo.LexerItemBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 分词返回结果
 * @Date 2020/9/18 15:44
 * @Author by 尘心
 */
@Data
@ApiModel("分词结果")
public class LexerOut {

    /** 日志ID */
    @ApiModelProperty("日志ID")
    private String log_id;

    /** 原始单条请求文本 */
    @ApiModelProperty("原始单条请求文本")
    private String text;

    /** 词汇数组，每个元素对应结果中的一个词 */
    @ApiModelProperty("词汇数组，每个元素对应结果中的一个词")
    private List<LexerItemBo> items;
}
