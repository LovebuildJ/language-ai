package com.chenxin.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 词义分析业务对象
 * @Date 2020/9/18 15:49
 * @Author by 尘心
 */
@Data
@ApiModel("词义分析业务对象")
public class LexerItemBo {

    /**	词汇的字符串  */
    @ApiModelProperty("词汇的字符串")
    private String item;

    /** 命名实体类型，命名实体识别算法使用。词性标注算法中，此项为空串 */
    @ApiModelProperty("命名实体类型")
    private String ne;

    /** 词性，词性标注算法使用。命名实体识别算法中，此项为空串 */
    @ApiModelProperty("词性")
    private String pos;

    /** 在text中的字节级offset */
    @ApiModelProperty("offset")
    private int byte_offset;

    /** 字节级length */
    @ApiModelProperty("length")
    private int byte_length;

    /** 链指到知识库的URI，只对命名实体有效。对于非命名实体和链接不到知识库的命名实体，此项为空串 */
    @ApiModelProperty("链指到知识库的URI")
    private String uri;

    /** 词汇的标准化表达，主要针对时间、数字单位，没有归一化表达的，此项为空串 */
    @ApiModelProperty("词汇的标准化表达")
    private String formal;

    /** 基本词成分 */
    @ApiModelProperty("基本词成分")
    private String[] basic_words;

    /** 地址成分，非必需，仅对地址型命名实体有效，没有地址成分的，此项为空数组。 */
    @ApiModelProperty("地址成分")
    private IocDetailsBo[] loc_details;
}
