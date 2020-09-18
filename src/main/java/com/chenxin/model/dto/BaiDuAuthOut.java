package com.chenxin.model.dto;

import com.chenxin.model.BaiDuError;
import lombok.Data;

/**
 * @Description 百度授权出参对象
 * @Date 2020/9/18 11:13
 * @Author by 尘心
 */
@Data
public class BaiDuAuthOut extends BaiDuError{

    private String refresh_token;

    private String expires_in;

    private String scope;

    private String session_key;

    private String access_token;

    private String session_secret;
}
