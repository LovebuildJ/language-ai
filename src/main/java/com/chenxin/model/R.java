package com.chenxin.model;

import com.alibaba.fastjson.JSONObject;
import com.chenxin.base.BaseErrorInterface;
import com.chenxin.util.CommonEnum;

/**
 * @Description 统一响应结果实体类
 * @Date 2020/9/18 15:07
 * @Author by 尘心
 */
public class R {
    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object result;

    public R() {
    }

    public R(BaseErrorInterface errorInfo) {
        this.code = errorInfo.code();
        this.message = errorInfo.msg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 成功
     *
     */
    public static R success() {
        return success(null);
    }

    /**
     * 成功
     * @param data 数据
     */
    public static R success(Object data) {
        R rb = new R();
        rb.setCode(CommonEnum.SUCCESS.getCode());
        rb.setMessage(CommonEnum.SUCCESS.getMsg());
        rb.setResult(data);
        return rb;
    }

    /**
     * 失败
     */
    public static R error(BaseErrorInterface errorInfo) {
        R rb = new R();
        rb.setCode(errorInfo.code());
        rb.setMessage(errorInfo.msg());
        rb.setResult(null);
        return rb;
    }

    /**
     * 失败
     */
    public static R error(String code, String message) {
        R rb = new R();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    /**
     * 失败
     */
    public static R error( String message) {
        R rb = new R();
        rb.setCode("-1");
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
