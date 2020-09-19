package com.chenxin.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 地址成分，非必需，仅对地址型命名实体有效，没有地址成分的
 * @Date 2020/9/18 15:50
 * @Author by 尘心
 */
@Data
@ApiModel("地址成分")
public class IocDetailsBo {

    /** 成分类型，如省、市、区、县 */
    @ApiModelProperty("成分类型")
    private String type;

    /** 在item中的字节级offset */
    @ApiModelProperty("offset")
    private int byte_offset;

    /** 字节级length */
    @ApiModelProperty("length")
    private int byte_length;
}
