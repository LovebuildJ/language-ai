package com.chenxin.util;

import com.chenxin.base.BaseErrorInterface;

/**
 * @Description 操作信息枚举类
 * @Date 2020/9/18 14:55
 * @Author by 尘心
 */
public enum CommonEnum implements BaseErrorInterface{

    // 数据操作错误定义
    SUCCESS("200", "成功!"),
    REQ_URL_ERROR("300","请求地址错误!"),
    BODY_NOT_MATCH("400","请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401","请求的数字签名不匹配!"),
    TOKEN_ERROR("402","获取access token 失败!"),
    NOT_FOUND("404", "未找到该资源!"),
    TOKEN_NOT_FOUND("406","找不到access token!"),
    PARAM_ERROR("407","参数错误!"),
    ANALYSE_WORDS_FAIL("408","分词解析失败!"),
    USERNAME_PASS_ERRPR("430","用户名或密码错误!"),
    IMPORT_WORDS("431","导入词库失败!"),
    TEXT_NULL("432","输入的文本不能为空!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503","服务器正忙，请稍后再试!")
    ;

    CommonEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
