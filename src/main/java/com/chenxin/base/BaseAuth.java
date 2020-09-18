package com.chenxin.base;

/**
 * @Description 基础权限父类
 * @Date 2020/9/18 9:52
 * @Author by 尘心
 */
public abstract class BaseAuth {

    /**
     * 授权服务器请求地址
     */
    protected static final String AUTH_URL = "https://aip.baidubce.com/oauth/2.0/token";

    /**
     * 通用版 词法分析接口地址
     */
    protected static final String NORMAL_LEXER_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer";
}
