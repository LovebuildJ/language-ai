package com.chenxin.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 键值对象
 * @Date 2020/9/23 10:51
 * @Author by 尘心
 */
@Data
@ApiModel("键值对象")
public class MapBo<K,V> {

    @ApiModelProperty("键")
    private K key;

    @ApiModelProperty("值")
    private V val;
}
