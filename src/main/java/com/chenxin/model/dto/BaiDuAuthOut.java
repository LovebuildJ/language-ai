package com.chenxin.model.dto;

import com.chenxin.model.BaiDuError;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description 百度授权出参对象
 * @Date 2020/9/18 11:13
 * @Author by 尘心
 */
@Data
@ApiModel("百度授权相应结果对象")
public class BaiDuAuthOut extends BaiDuError{

    private String refresh_token;

    private String expires_in;

    private String scope;

    private String session_key;

    private String access_token;

    private String session_secret;
}
