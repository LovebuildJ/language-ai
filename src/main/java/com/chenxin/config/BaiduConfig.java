package com.chenxin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bai du AI 配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "baidu")
public class BaiduConfig {

    /** 百度App Id */
    private String appid;
    /** 百度app key */
    private String appkey;
    /** 百度app secret */
    private String secret;
}
