package com.chenxin.util.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @Description http 请求头设置
 * @Date 2020/9/18 16:18
 * @Author by 尘心
 */
public class HttpHeader {

    /**
     * 获取JSON类型的请求头
     */
    public static HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        // 根据百度ai 的接口要求, 默认使用GBK编码
        MediaType type = MediaType.parseMediaType("application/json;");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }
}
