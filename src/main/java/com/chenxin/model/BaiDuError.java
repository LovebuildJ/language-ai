package com.chenxin.model;

import lombok.Data;

/**
 * @Description 百度API 错误信息返回VO
 * @Date 2020/9/18 11:16
 * @Author by 尘心
 */
@Data
public class BaiDuError {
    private String error;
    private String error_description;
}
