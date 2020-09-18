package com.chenxin.base;

/**
 * @Description 基础错误接口
 * @Date 2020/9/18 14:49
 * @Author by 尘心
 */
public interface BaseErrorInterface {

    /** 状态码 */
    String code();
    /** 状态描述 */
    String msg();
}
