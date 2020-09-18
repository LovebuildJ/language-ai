package com.chenxin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 百度授权入参对象
 * @Date 2020/9/18 10:53
 * @Author by 尘心
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaiDuAuthDto {

    /** 必须参数，固定为client_credentials */
    private String grant_type;

    /** 必须参数，应用的API Key */
    private String client_id;

    /**  必须参数，应用的Secret Key */
    private String client_secret;
}
