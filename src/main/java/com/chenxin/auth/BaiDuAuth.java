package com.chenxin.auth;

import com.alibaba.fastjson.JSON;
import com.chenxin.base.BaseAuth;
import com.chenxin.config.BaiduConfigProperty;
import com.chenxin.model.dto.BaiDuAuthOut;
import com.chenxin.util.consts.AiConstant;
import com.chenxin.util.auth.AuthContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate redisTemplate;

    @Autowired
    private BaiduConfigProperty baiduConfigProperty;

    /**
     * 获取百度访问临牌, 使用系统缓存, 避免重复获此调用获取access token
     * <p>
     *     token默认有效时长为 一个月
     * </p>
     */
    public BaiDuAuthOut getAccessToken() {
        //        // 拼接请求地址及参数
        StringBuilder sb = new StringBuilder(AUTH_URL);
        sb.append("?").append("grant_type=client_credentials");
        sb.append("&").append("client_id").append("=").append(baiduConfigProperty.getAppkey());
        sb.append("&").append("client_secret").append("=").append(baiduConfigProperty.getSecret());


        BaiDuAuthOut baiDuAuthOut = null;
        // 发送请求 获取数据
        ResponseEntity<String> forEntity = restTemplate.getForEntity(sb.toString(), String.class);
        if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = forEntity.getBody();
            baiDuAuthOut = JSON.parseObject(body, BaiDuAuthOut.class);
            // 存入认证容器, 避免反复获取
            AuthContainer.add(AiConstant.TOKEN_KEY,baiDuAuthOut,Long.parseLong(baiDuAuthOut.getExpires_in()));
            // 存入一份到redis中
            Boolean bool = redisTemplate.hasKey(AiConstant.TOKEN_KEY);
            if (bool) {
                redisTemplate.delete(AiConstant.TOKEN_KEY);
                // 存储
                redisTemplate.opsForValue().set(AiConstant.TOKEN_KEY,baiDuAuthOut.getAccess_token());
            }

            return baiDuAuthOut;
        }

        return new BaiDuAuthOut();
    }

}
