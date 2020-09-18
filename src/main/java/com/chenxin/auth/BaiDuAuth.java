package com.chenxin.auth;

import com.alibaba.fastjson.JSON;
import com.chenxin.base.BaseAuth;
import com.chenxin.config.BaiduConfigProperty;
import com.chenxin.model.dto.BaiDuAuthOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Description 百度AI 权限模块
 * @Date 2020/9/18 9:50
 * @Author by 尘心
 */
@Service
public class BaiDuAuth extends BaseAuth{


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BaiduConfigProperty baiduConfigProperty;

    /**
     * 获取百度访问临牌
     */
    public BaiDuAuthOut getAccessToken() {
        // 拼接请求地址及参数
        StringBuilder sb = new StringBuilder(AUTH_URL);
        sb.append("?").append("grant_type=client_credentials");
        sb.append("&").append("client_id").append("=").append(baiduConfigProperty.getAppkey());
        sb.append("&").append("client_secret").append("=").append(baiduConfigProperty.getSecret());

        // 发送请求 获取数据
        ResponseEntity<String> forEntity = restTemplate.getForEntity(sb.toString(), String.class);
        if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = forEntity.getBody();
            return JSON.parseObject(body, BaiDuAuthOut.class);
        }

        return new BaiDuAuthOut();
    }

}
